package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.entity.Role;
import com.it.entity.User;
import com.it.mapper.RoleMapper;
import com.it.mapper.UserMapper;
import com.it.service.UserService;
import com.it.util.*;
import com.it.vo.resp.LoginRespVO;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户service实现类
 *
 * @author
 */
@Service
public class UserServiceImpl implements UserService {
    private static final String USER_IMAGE = "/image/default.jpg"; // 初始用户头像（相对路径，前端代理 /image）
    private static final String USER_STATUS = "1";//初始用户状态
    private static final Boolean IS_ENCRYPTED = false;//密码是否需要加密
    private static final String DEFAULT_ROLE = "1879449283212673025";//注册默认用户
    @Resource
    private UserMapper userMapper;
    @Resource
    private RoleMapper roleMapper;
    @Resource
    private JwtUtil jwtUtil;


    @Override
    public User getUserByUsername(String username) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        List<User> selectList = userMapper.selectList(wrapper);
        if (selectList.isEmpty()) {
            return null;
        }
        User user = selectList.get(0);
        Role role = roleMapper.selectById(user.getRoleId());
        user.setRole_name(role == null ? "" : role.getName());
        return user;
    }

    @Override
    public IPage<User> selectPage(User entity, int page, int limit) {
        QueryWrapper<User> searchInfo = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getUsername())) {
            searchInfo.like("username", entity.getUsername());
        }
        if (CommonUtils.stringIsNotBlack(entity.getReal_name())) {
            searchInfo.like("real_name", entity.getReal_name());
        }
        if (CommonUtils.stringIsNotBlack(entity.getPhone())) {
            searchInfo.like("phone", entity.getPhone());
        }
        if (CommonUtils.stringIsNotBlack(entity.getRoleId())) {
            searchInfo.like("roleId", entity.getRoleId());
        }

        if (CommonUtils.stringIsNotBlack(entity.getStatus())) {
            searchInfo.eq("status", entity.getStatus());
        }
        if (CommonUtils.stringIsNotBlack(entity.getCreate_time())) {
            searchInfo.between("create_time", entity.getCreate_time().split(" - ")[0], entity.getCreate_time().split(" - ")[1]);
        }
        if (entity.getRoleId() != null) {
            searchInfo.eq("roleId", entity.getRoleId());
        }
        searchInfo.orderBy(true, false, "create_time");
        IPage<User> pageInfo = new Page<>(page, limit);
        IPage<User> selectPage = userMapper.selectPage(pageInfo, searchInfo);
        if (!selectPage.getRecords().isEmpty()) {
            for (User user : selectPage.getRecords()) {
                Role role = roleMapper.selectById(user.getRoleId());
                user.setRole_name(role == null ? "" : role.getName());
            }
        }
        return selectPage;
    }

    @Override
    public User selectByPrimaryKey(String id) {
        User user = userMapper.selectById(id);
        if (user != null) {
            Role role = roleMapper.selectById(user.getRoleId());
            user.setRole_name(role == null ? "" : role.getName());
        }
        return user;
    }

    @Override
    public boolean insert(User user) {
        if (!CommonUtils.stringIsNotBlack(user.getStatus())) {
            user.setStatus(USER_STATUS);
        }
        if (!CommonUtils.stringIsNotBlack(user.getImg_url())) {
            user.setImg_url(USER_IMAGE);
        }
        if (!CommonUtils.stringIsNotBlack(user.getRoleId())) {
            user.setRoleId(DEFAULT_ROLE);
        }
        user.setNick_name("用户" + System.currentTimeMillis());

        if (!CommonUtils.stringIsNotBlack(user.getPassword())) {
            user.setPassword("123456");
            if (IS_ENCRYPTED) {
                user.setSalt(PasswordUtils.getSalt());
                String encode = PasswordUtils.encode(user.getPassword(), user.getSalt());
                user.setPassword(encode);
            }
        }

        user.setCreate_time(DateUtil.getNowDateSS());
        Integer result = userMapper.insert(user);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateByPrimaryKey(User user) {
        if (IS_ENCRYPTED) {
            user.setSalt(PasswordUtils.getSalt());
            String encode = PasswordUtils.encode(user.getPassword(), user.getSalt());
            user.setPassword(encode);
        }

        Integer result = userMapper.updateById(user);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean resetPwa(String id) {
        User user = userMapper.selectById(id);
        user.setPassword("123456");
        if (IS_ENCRYPTED) {
            user.setSalt(PasswordUtils.getSalt());
            String encode = PasswordUtils.encode(user.getPassword(), user.getSalt());
            user.setPassword(encode);
        }
        Integer result = userMapper.updateById(user);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean resetPwa(String username, String newPwd) {
        QueryWrapper<User> searchInfo = new QueryWrapper<>();
        searchInfo.eq("username", username);
        List<User> users = userMapper.selectList(searchInfo);
        if (users.isEmpty()) {
            throw new BusinessException(ResponseCode.ACCOUNT_NOT_FOUND.getCode(), ResponseCode.ACCOUNT_NOT_FOUND.getMessage());
        }
        User user = users.get(0);
        user.setPassword(newPwd);
        if (IS_ENCRYPTED) {
            user.setSalt(PasswordUtils.getSalt());
            String encode = PasswordUtils.encode(user.getPassword(), user.getSalt());
            user.setPassword(encode);
        }
        Integer result = userMapper.updateById(user);
        if (result > 0) {
            return true;
        }
        return false;

    }

    @Override
    public boolean updatePwa(String oldPwd, String newPwd, String userId) {
        User user = userMapper.selectById(userId);
        //判断旧密码
        if (IS_ENCRYPTED) {
            if (!PasswordUtils.matches(user.getSalt(), oldPwd, user.getPassword())) {
                throw new BusinessException(ResponseCode.OLD_PASSWORD_ERROR.getCode(), ResponseCode.OLD_PASSWORD_ERROR.getMessage());
            }
            user.setSalt(PasswordUtils.getSalt());
            String encode = PasswordUtils.encode(user.getPassword(), user.getSalt());
            user.setPassword(encode);
        } else {
            //直接比对
            if (!oldPwd.equals(user.getPassword())) {
                throw new BusinessException(ResponseCode.OLD_PASSWORD_ERROR.getCode(), ResponseCode.OLD_PASSWORD_ERROR.getMessage());
            }
            user.setPassword(newPwd);
        }
        Integer result = userMapper.updateById(user);
        if (result > 0) {
            return true;
        }
        return false;
    }


    @Override
    public boolean checkPassword(String password, String userId) {
        User user = userMapper.selectById(userId);
        //判断旧密码
        if (IS_ENCRYPTED) {
            if (!PasswordUtils.matches(user.getSalt(), password, user.getPassword())) {
                throw new BusinessException(ResponseCode.PASSWORD_ERROR.getCode(), ResponseCode.PASSWORD_ERROR.getMessage());
            }
        } else {
            //直接比对
            if (!password.equals(user.getPassword())) {
                throw new BusinessException(ResponseCode.PASSWORD_ERROR.getCode(), ResponseCode.PASSWORD_ERROR.getMessage());
            }
        }
        return true;
    }

    @Override
    public boolean deleteByPrimaryKey(List<String> ids) {
        if (ids.contains("1882308062962794498")) {
            throw new BusinessException(ResponseCode.DELETE_USER_ERROR.getCode(), ResponseCode.DELETE_USER_ERROR.getMessage());
        }
        Integer result = 0;
        for (String id : ids) {
            result = userMapper.deleteById(id);
        }
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<User> selectList() {
        List<User> userList = userMapper.selectList(null);
        if (!userList.isEmpty()) {
            for (User user : userList) {
                Role role = roleMapper.selectById(user.getRoleId());
                user.setRole_name(role == null ? "" : role.getName());
            }
        }
        return userList;
    }

    @Override
    public List<User> selectList(String roleId) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("roleId", roleId);
        List<User> userList = userMapper.selectList(wrapper);
        if (!userList.isEmpty()) {
            for (User user : userList) {
                Role role = roleMapper.selectById(user.getRoleId());
                user.setRole_name(role == null ? "" : role.getName());
            }
        }
        return userList;
    }

    @Override
    public List<User> selectList(User entity) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getUsername())) {
            wrapper.like("username", entity.getUsername());
        }
        if (CommonUtils.stringIsNotBlack(entity.getReal_name())) {
            wrapper.like("real_name", entity.getReal_name());
        }
        if (CommonUtils.stringIsNotBlack(entity.getPhone())) {
            wrapper.like("iphone", entity.getPhone());
        }
        if (entity.getRoleId() != null) {
            wrapper.eq("roleId", entity.getRoleId());
        }
        wrapper.orderBy(true, false, "create_time");
        List<User> userList = userMapper.selectList(wrapper);
        if (!userList.isEmpty()) {
            for (User user : userList) {
                Role role = roleMapper.selectById(user.getRoleId());
                user.setRole_name(role == null ? "" : role.getName());
            }
        }
        return userList;
    }

    @Override
    public LoginRespVO login(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        List<User> users = userMapper.selectList(wrapper);
        if (users.isEmpty()) {
            throw new BusinessException(ResponseCode.ACCOUNT_NOT_FOUND.getCode(), ResponseCode.ACCOUNT_NOT_FOUND.getMessage());
        }
        User user = users.get(0);
        if ("0".equals(user.getStatus())) {
            throw new BusinessException(ResponseCode.ACCOUNT_LOCK.getCode(), ResponseCode.ACCOUNT_LOCK.getMessage());
        }
        if (DEFAULT_ROLE.equals(user.getRoleId())) {
            throw new BusinessException(ResponseCode.LOGIN_ERROR.getCode(), ResponseCode.LOGIN_ERROR.getMessage());
        }
        //如果密码加密需要特殊处理,否则直接对比
        if (IS_ENCRYPTED) {
            if (!PasswordUtils.matches(user.getSalt(), password, user.getPassword())) {
                throw new BusinessException(ResponseCode.ACCOUNT_ERROR.getCode(), ResponseCode.ACCOUNT_ERROR.getMessage());
            }
        } else {
            //直接对比
            if (!password.equals(user.getPassword())) {
                throw new BusinessException(ResponseCode.ACCOUNT_ERROR.getCode(), ResponseCode.ACCOUNT_ERROR.getMessage());
            }
        }
        Role role = roleMapper.selectById(user.getRoleId());
        user.setRole_name(role == null ? "" : role.getName());
        LoginRespVO result = new LoginRespVO();
        result.setUser(user);
        //封装token
        result.setToken(jwtUtil.getToken(user.getId(), System.currentTimeMillis()));
        return result;
    }

    @Override
    public LoginRespVO loginFront(String username, String password) {
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.eq("username", username);
        List<User> users = userMapper.selectList(wrapper);
        if (users.isEmpty()) {
            throw new BusinessException(ResponseCode.ACCOUNT_NOT_FOUND.getCode(), ResponseCode.ACCOUNT_NOT_FOUND.getMessage());
        }
        User user = users.get(0);
        if ("0".equals(user.getStatus())) {
            throw new BusinessException(ResponseCode.ACCOUNT_LOCK.getCode(), ResponseCode.ACCOUNT_LOCK.getMessage());
        }
        //如果密码加密需要特殊处理,否则直接对比
        if (IS_ENCRYPTED) {
            if (!PasswordUtils.matches(user.getSalt(), password, user.getPassword())) {
                throw new BusinessException(ResponseCode.ACCOUNT_ERROR.getCode(), ResponseCode.ACCOUNT_ERROR.getMessage());
            }
        } else {
            //直接对比
            if (!password.equals(user.getPassword())) {
                throw new BusinessException(ResponseCode.ACCOUNT_ERROR.getCode(), ResponseCode.ACCOUNT_ERROR.getMessage());
            }
        }
        Role role = roleMapper.selectById(user.getRoleId());
        user.setRole_name(role == null ? "" : role.getName());
        LoginRespVO result = new LoginRespVO();
        result.setUser(user);
        //封装token
        result.setToken(jwtUtil.getToken(user.getId(), System.currentTimeMillis()));
        return result;

    }
}