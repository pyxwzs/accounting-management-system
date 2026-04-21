package com.it.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.entity.Comment;

import java.util.List;

public interface CommentService {


    /**
     * 分页查询
     *
     * @param entity
     * @param page
     * @param limit
     * @return
     */
    IPage<Comment> selectPage(Comment entity, int page, int limit);

    /**
     * 根据主键id查询
     *
     * @param id
     * @return
     */
    Comment selectByPrimaryKey(String id);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean insert(Comment entity);

    /**
     * 修改
     *
     * @param
     * @return
     */
    boolean updateByPrimaryKey(Comment entity);

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
    List<Comment> selectList(Comment entity);
    List<Comment> selectList(String item_id);
}
