package com.it.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.entity.MsgCode;

import java.util.List;

public interface MsgCodeService {
    /**
     * 分页查询
     *
     * @param entity
     * @param page
     * @param limit
     * @return
     */
    IPage<MsgCode> selectPage(MsgCode entity, int page, int limit);

    /**
     * 根据主键id查询
     *
     * @param id
     * @return
     */
    MsgCode selectByPrimaryKey(String id);

    /**
     * 新增
     *
     * @return
     */
    String insert();

    /**
     * 验证验证码
     * @param code
     * @return
     */
    boolean verifyCode(String code);
    /**
     * 删除
     *
     * @param
     * @return
     */
    boolean deleteByPrimaryKey(List<String> ids);


}
