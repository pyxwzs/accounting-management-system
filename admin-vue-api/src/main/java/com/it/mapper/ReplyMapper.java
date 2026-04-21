package com.it.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.it.entity.Reply;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户信息数据访问层
 */
@Mapper
public interface ReplyMapper extends BaseMapper<Reply> {

}
