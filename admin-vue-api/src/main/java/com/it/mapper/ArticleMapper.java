package com.it.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.it.entity.Article;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户信息数据访问层
 */
@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

}
