package com.it.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.entity.Reply;

import java.util.List;

public interface ReplyService {


    /**
     * 分页查询
     *
     * @param entity
     * @param page
     * @param limit
     * @return
     */
    IPage<Reply> selectPage(Reply entity, int page, int limit);

    /**
     * 根据主键id查询
     *
     * @param id
     * @return
     */
    Reply selectByPrimaryKey(String id);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean insert(Reply entity);

    /**
     * 修改
     *
     * @param
     * @return
     */
    boolean updateByPrimaryKey(Reply entity);

    /**
     * 删除,单个删除批量删除通用
     *
     * @param ids
     * @return
     */
    boolean deleteByPrimaryKey(List<String> ids);

    /**
     * 列表查询
     *
     * @param entity
     * @return
     */
    List<Reply> selectList(Reply entity);
    List<Reply> selectList(String item_id);
}
