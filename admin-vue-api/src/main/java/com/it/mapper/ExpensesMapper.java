package com.it.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.it.entity.Expenses;
import org.apache.ibatis.annotations.Mapper;

/**
 * 动物求助信息数据访问层
 */
@Mapper
public interface ExpensesMapper extends BaseMapper<Expenses> {

}
