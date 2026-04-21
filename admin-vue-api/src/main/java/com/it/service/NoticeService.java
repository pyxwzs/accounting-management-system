package com.it.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.entity.Notice;

import java.util.List;

public interface NoticeService {


    /**
     * 分页查询
     *
     * @param entity
     * @param page
     * @param limit
     * @return
     */
    IPage<Notice> selectPage(Notice entity, int page, int limit);

    /**
     * 根据主键id查询
     *
     * @param id
     * @return
     */
    Notice selectByPrimaryKey(String id);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean insert(Notice entity);

    /**
     * 修改
     *
     * @param
     * @return
     */
    boolean updateByPrimaryKey(Notice entity);

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
    List<Notice> selectList(Notice entity);
}
