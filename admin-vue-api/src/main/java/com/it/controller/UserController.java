package com.it.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.config.Operation;
import com.it.entity.User;
import com.it.pojo.PageResult;
import com.it.service.UserService;
import com.it.util.AuthUserUtils;
import com.it.util.BusinessException;
import com.it.util.ResponseCode;
import com.it.vo.req.LoginFrontReqVO;
import com.it.vo.req.ResetPwdPwdReqVO;
import com.it.vo.req.UpdatePwdReqVO;
import com.it.vo.resp.HomeRespVO;
import com.it.vo.resp.LoginRespVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/user")
@Api(tags = "用户模块")
public class UserController {
    @Resource
    private UserService userService;
    @Resource
    private AuthUserUtils authUserUtils;


    @PostMapping("/register")
    @Operation("用户注册")
    @ApiOperation(value = "用户注册接口", notes = "注册接口\n" +
            "     * 1.定义一个实体接收用户提交过来的表单数据\n" +
            "     * 2.就要判断是否已经注册过\n" +
            "     * 3.构造一系列的用户数据\n" +
            "     * 4.就是保存到db\n" +
            "     * 5.判断是否操作成功")

    public void register(@RequestBody User entity) {
        //判断用户是否存在
        User user = userService.getUserByUsername(entity.getUsername());
        if (user != null) {
            throw new BusinessException(ResponseCode.ACCOUNT_ALREADY_EXISTS.getCode(), ResponseCode.ACCOUNT_ALREADY_EXISTS.getMessage());
        }
        boolean insert = userService.insert(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param username 账号
     * @param password 密码(如果需要加密则进行加密处理,不需要则跳过)
     * @return 返回响应对象, 包含 用户主键id,token,和账号名
     */
    @ApiOperation(value = "后台登录接口", notes = "* @param username 账号\n" +
            "     * @param password 密码(如果需要加密则进行加密处理,不需要则跳过)\n" +
            "     * @return 返回响应对象,包含 用户主键id,token,和账号名)\n" +
            "系统采用jwt+shiro进行登录认证，调用登录接口后验证用户信息之后颁发token," +
            "后续所有请求会携带token信息，然后有shrio去判断token信息")
    @GetMapping("/login")
    public LoginRespVO login(String username, String password) {
        return userService.login(username, password);
    }

    /**
     * 前台登录：POST + JSON 请求体，避免账号密码出现在 URL 查询参数中。
     */
    @PostMapping("/loginFront")
    @ApiOperation(value = "前台登录接口", notes = "请求体 JSON：username、password。图片验证码仅前端校验。")
    public LoginRespVO loginFront(@RequestBody LoginFrontReqVO req) {
        if (req == null || req.getUsername() == null || req.getPassword() == null) {
            throw new BusinessException(ResponseCode.DATA_PARAM_ERROR.getCode(), ResponseCode.DATA_PARAM_ERROR.getMessage());
        }
        return userService.loginFront(req.getUsername().trim(), req.getPassword());
    }

    /**
     * @param entity
     * @return
     */
    @PostMapping("users")
    @ApiOperation(value = "用户分页查询接口", notes = "传入的参数为:\n" +
            "    1.查询条件:user对象(属性非必传)\n" +
            "    2.page:页数\n" +
            "    3.limit:条数\n")
    public PageResult<User> pageUsers(@RequestBody User entity) {
        IPage<User> selectPage = userService.selectPage(entity, entity.getCurrent(), entity.getSize());
        List<User> userList = selectPage.getRecords();
        for (User user : userList) {
            user.setCreate_time(user.getCreate_time().substring(0, 19));
        }
        return PageResult.getPage(selectPage.getRecords(), selectPage.getTotal());
    }

    /**
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    @Operation("删除用户")
    @ApiOperation(value = "用户删除接口", notes = "传入的参数为:\n" +
            "   User主键id\n")
    public void deleteLog(@RequestBody List<String> ids) {
        boolean delete = userService.deleteByPrimaryKey(ids);
        if (!delete) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    @GetMapping("/verifyToken")
    public void verifyToken() {
    }


    /**
     * @param entity
     * @return
     */
    @PostMapping("/user")
    @Operation("新增用户")
    @ApiOperation(value = "用户新增接口", notes = "传入的参数为:\n" +
            "   User对象\n")
    public void insertRole(@RequestBody User entity) {
        boolean insert = userService.insert(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PutMapping("/user")
    @Operation("编辑用户")
    @ApiOperation(value = "用户编辑接口", notes = "传入的参数为:\n" +
            "   User对象\n")
    public void editRole(@RequestBody User entity) {
        boolean insert = userService.updateByPrimaryKey(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/user/{id}")
    @ApiOperation(value = "用户单个对象查询接口", notes = "传入的参数为:\n" +
            "  User主键id\n")
    public User selectOne(@PathVariable("id") String id) {
        User user = userService.selectByPrimaryKey(id);
        if (user == null) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
        return user;
    }

    @GetMapping("/user")
    @ApiOperation(value = "获取当前登录用户信息")
    public User selectOneLogInUser() {
        User user = userService.selectByPrimaryKey(authUserUtils.getLoginUserId());
        if (user == null) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
        return user;
    }

    /**
     * @param entity
     * @return
     */
    @PutMapping("/resetPwa")
    @Operation("管理员重置用户密码")
    @ApiOperation(value = "管理员重置用户密码", notes = "传入的参数为:\n" +
            "   User对象\n")
    public void resetPwa(@RequestBody User entity) {
        boolean insert = userService.resetPwa(entity.getId());
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    @PutMapping("/resetPwd")
    @Operation("忘记密码重置")
    @ApiOperation(value = "忘记密码重置用户密码", notes = "传入的参数为:\n" +
            "   User对象\n")
    public void resetPwd(@RequestBody ResetPwdPwdReqVO resetPwdPwdReqVO) {
        boolean insert = userService.resetPwa(resetPwdPwdReqVO.getUsername(), resetPwdPwdReqVO.getNewPwd());
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param updatePwdReqVO
     * @return
     */
    @PutMapping("/updatePwd")
    @Operation("修改登录密码")
    @ApiOperation(value = "用户修改密码", notes = "传入的参数为:\n" +
            "   UpdatePwdReqVO对象\n")
    public void updatePwd(@RequestBody UpdatePwdReqVO updatePwdReqVO) {
        boolean insert = userService.updatePwa(updatePwdReqVO.getOldPwd(), updatePwdReqVO.getNewPwd(), authUserUtils.getLoginUserId());
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PostMapping("/userList")
    @ApiOperation(value = "用户分页查询接口", notes = "传入的参数为:\n" +
            "    1.查询条件:user对象(属性非必传)")
    public List<User> list(@RequestBody User entity) {
        List<User> users = userService.selectList(entity);
        return users;
    }

    /**
     * @param
     * @return
     */
    @PostMapping("/home")
    @ApiOperation(value = "首页数据查询接口")
    public HomeRespVO home() {
        HomeRespVO homeRespVO = new HomeRespVO();
     ;
        return homeRespVO;
    }

}
