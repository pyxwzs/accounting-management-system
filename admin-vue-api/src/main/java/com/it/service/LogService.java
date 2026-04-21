package com.it.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.entity.Log;

import java.util.List;

public interface LogService {
    /**
     * 分页查询
     *
     * @param entity
     * @param page
     * @param limit
     * @return
     */
    IPage<Log> selectPage(Log entity, int page, int limit);

    /**
     * 根据主键id查询
     *
     * @param id
     * @return
     */
    Log selectByPrimaryKey(String id);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean insert(Log entity);

    /**
     * 删除
     *
     * @param id
     * @return
     */
    boolean deleteByPrimaryKey(List<String> ids);


}
