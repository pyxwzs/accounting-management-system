package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.entity.MsgCode;
import com.it.mapper.MsgCodeMapper;
import com.it.service.MsgCodeService;
import com.it.util.BusinessException;
import com.it.util.CommonUtils;
import com.it.util.DateUtil;
import com.it.util.ResponseCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Random;

/**
 * 〈日志service实现类〉<br>
 *
 * @author
 */
@Service
public class MsgCodeServiceImpl implements MsgCodeService {
    @Resource
    private MsgCodeMapper msgCodeMapper;


    @Override
    public IPage<MsgCode> selectPage(MsgCode entity, int page, int limit) {
        QueryWrapper<MsgCode> searchInfo = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getCreate_time())) {
            searchInfo.between("create_time", entity.getCreate_time().split(" - ")[0], entity.getCreate_time().split(" - ")[1]);
        }
        searchInfo.orderBy(true, false, "create_time");
        IPage<MsgCode> pageInfo = new Page<>(page, limit);
        IPage<MsgCode> selectPage = msgCodeMapper.selectPage(pageInfo, searchInfo);
        return selectPage;
    }

    @Override
    public MsgCode selectByPrimaryKey(String id) {
        return msgCodeMapper.selectById(id);
    }

    @Override
    public String insert() {
        MsgCode entity = new MsgCode();
        Random random = new Random();
        int randomNumber = random.nextInt(900000) + 100000; // 生成[100000, 999999]范围内的随机数
        String sixDigitRandomNumber = String.format("%06d", randomNumber); // 格式化为六位数字字符串
        entity.setCode(sixDigitRandomNumber);
        entity.setCreate_time(DateUtil.getNowDateSS());
        entity.setExpire_time(DateUtil.addMinute(5, "yyyy-MM-dd HH:mm:ss", entity.getCreate_time()));
        Integer result = msgCodeMapper.insert(entity);
        if (result > 0) {
            return sixDigitRandomNumber;
        }
        return "";
    }

    @Override
    public boolean verifyCode(String code) {
        QueryWrapper<MsgCode> searchInfo = new QueryWrapper<>();
        searchInfo.eq("code", code);
        List<MsgCode> msgCodes = msgCodeMapper.selectList(searchInfo);
        if (msgCodes.isEmpty()) {
            throw new BusinessException(ResponseCode.CODE_ERROR.getCode(), ResponseCode.CODE_ERROR.getMessage());
        }
        MsgCode msgCode = msgCodes.get(0);
        if (DateUtil.compare(msgCode.getExpire_time(), DateUtil.getNowDateSS())) {
            throw new BusinessException(ResponseCode.CODE_EXPIRE_ERROR.getCode(), ResponseCode.CODE_EXPIRE_ERROR.getMessage());
        }
        return true;
    }


    @Override
    public boolean deleteByPrimaryKey(List<String> ids) {
        Integer result = 0;
        for (String id : ids) {
            result = msgCodeMapper.deleteById(id);
        }
        if (result > 0) {
            return true;
        }
        return false;
    }
}