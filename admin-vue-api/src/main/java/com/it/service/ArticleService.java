package com.it.service;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.entity.Article;

import java.util.List;

public interface ArticleService {


    /**
     * 分页查询
     *
     * @param entity
     * @param page
     * @param limit
     * @return
     */
    IPage<Article> selectPage(Article entity, int page, int limit);

    /**
     * 根据主键id查询
     *
     * @param id
     * @return
     */
    Article selectByPrimaryKey(String id);

    /**
     * 新增
     *
     * @param entity
     * @return
     */
    boolean insert(Article entity);

    /**
     * 修改
     *
     * @param
     * @return
     */
    boolean updateByPrimaryKey(Article entity);

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
    List<Article> selectList(Article entity);

    /**
     * 按原文链接查重（RSS 入库用）
     */
    int countBySourceUrl(String url);

    /**
     * 插入 RSS 聚合文章（不做 Emoji 富文本转换）
     */
    boolean insertFeedArticle(Article entity);
}
