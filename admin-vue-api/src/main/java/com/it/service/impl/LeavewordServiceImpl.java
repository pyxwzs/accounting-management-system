package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.entity.Leaveword;
import com.it.entity.User;
import com.it.mapper.LeavewordMapper;
import com.it.mapper.UserMapper;
import com.it.service.LeavewordService;
import com.it.util.AuthUserUtils;
import com.it.util.CommonUtils;
import com.it.util.DateUtil;
import com.it.util.EmojiFilter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户service实现类
 *
 * @author
 */
@Service
public class LeavewordServiceImpl implements LeavewordService {

    @Resource
    private LeavewordMapper LeavewordMapper;
    @Resource
    private AuthUserUtils authUserUtils;
    @Resource
    private UserMapper userMapper;

    @Override
    public IPage<Leaveword> selectPage(Leaveword entity, int page, int limit) {
        QueryWrapper<Leaveword> searchInfo = new QueryWrapper<>();

        if (CommonUtils.stringIsNotBlack(entity.getUser_id())) {
            searchInfo.eq("user_id", entity.getUser_id());
        }
        if (CommonUtils.stringIsNotBlack(entity.getType())) {
            searchInfo.like("type", entity.getType());
        }
        if (CommonUtils.stringIsNotBlack(entity.getCreate_time())) {
            searchInfo.between("create_time", entity.getCreate_time().split(" - ")[0], entity.getCreate_time().split(" - ")[1]);
        }
        searchInfo.orderBy(true, false, "create_time");
        IPage<Leaveword> pageInfo = new Page<>(page, limit);
        IPage<Leaveword> selectPage = LeavewordMapper.selectPage(pageInfo, searchInfo);
        if (!selectPage.getRecords().isEmpty()) {
            for (Leaveword record : selectPage.getRecords()) {
                User user = userMapper.selectById(record.getUser_id());
                record.setUser(user == null ? new User() : user);
                record.setContent(EmojiFilter.emojiRecovery2(record.getContent()));
            }
        }
        return selectPage;
    }

    @Override
    public Leaveword selectByPrimaryKey(String id) {
        Leaveword Leaveword = LeavewordMapper.selectById(id);
        return Leaveword;
    }

    @Override
    public boolean insert(Leaveword entity) {
        entity.setCreate_time(DateUtil.getNowDateSS());
        entity.setContent(EmojiFilter.emojiConvert1(entity.getContent()));
        entity.setUser_id(authUserUtils.getLoginUserId());
        Integer result = LeavewordMapper.insert(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateByPrimaryKey(Leaveword entity) {
        entity.setContent(EmojiFilter.emojiConvert1(entity.getContent()));
        Integer result = LeavewordMapper.updateById(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByPrimaryKey(List<String> ids) {
        Integer result = 0;
        for (String id : ids) {
            result = LeavewordMapper.deleteById(id);
        }
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Leaveword> selectList(Leaveword entity) {
        QueryWrapper<Leaveword> wrapper = new QueryWrapper<>();

        wrapper.orderBy(true, false, "create_time");
        List<Leaveword> LeavewordList = LeavewordMapper.selectList(wrapper);
        return LeavewordList;
    }

    @Override
    public List<Leaveword> selectList(String item_id) {
        QueryWrapper<Leaveword> wrapper = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(item_id)) {
            wrapper.like("item_id", item_id);
        }
        wrapper.orderBy(true, false, "create_time");
        List<Leaveword> LeavewordList = LeavewordMapper.selectList(wrapper);
        return LeavewordList;
    }
}