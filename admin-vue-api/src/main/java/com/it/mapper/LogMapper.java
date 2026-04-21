package com.it.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.it.entity.Log;
import org.apache.ibatis.annotations.Mapper;

/**
 * 操作日志信息数据访问层
 */
@Mapper
public interface LogMapper extends BaseMapper<Log> {

}
