package com.it.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.entity.Plan;

import java.util.List;

public interface PlanService {


    /**
     * 分页查询
     *
     * @param entity
     * @param page
     * @param limit
     * @return
     */
    IPage<Plan> selectPage(Plan entity, int page, int limit);

    /**
     * 根据主键id查询
     *
     * @param id
     * @return
     */
    Plan selectByPrimaryKey(String id);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean insert(Plan entity);

    /**
     * 修改
     *
     * @param
     * @return
     */
    boolean updateByPrimaryKey(Plan entity);

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
    List<Plan> selectList(Plan entity);
}
