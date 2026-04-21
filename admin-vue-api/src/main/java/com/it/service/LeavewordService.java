package com.it.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.entity.Leaveword;

import java.util.List;

public interface LeavewordService {


    /**
     * 分页查询
     *
     * @param entity
     * @param page
     * @param limit
     * @return
     */
    IPage<Leaveword> selectPage(Leaveword entity, int page, int limit);

    /**
     * 根据主键id查询
     *
     * @param id
     * @return
     */
    Leaveword selectByPrimaryKey(String id);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean insert(Leaveword entity);

    /**
     * 修改
     *
     * @param
     * @return
     */
    boolean updateByPrimaryKey(Leaveword entity);

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
    List<Leaveword> selectList(Leaveword entity);

    List<Leaveword> selectList(String item_id);
}
