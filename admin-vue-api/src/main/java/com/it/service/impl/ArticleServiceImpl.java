package com.it.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.it.entity.Article;
import com.it.mapper.ArticleMapper;
import com.it.service.ArticleService;
import com.it.util.CommonUtils;
import com.it.util.DateUtil;
import com.it.util.EmojiFilter;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户service实现类
 *
 * @author
 */
@Service
public class ArticleServiceImpl implements ArticleService {

    @Resource
    private ArticleMapper ArticleMapper;


    @Override
    public IPage<Article> selectPage(Article entity, int page, int limit) {
        QueryWrapper<Article> searchInfo = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getName())) {
            searchInfo.like("name", entity.getName());
        }
        if (CommonUtils.stringIsNotBlack(entity.getType())) {
            searchInfo.like("type", entity.getType());
        }
        if (CommonUtils.stringIsNotBlack(entity.getCreate_time())) {
            searchInfo.between("create_time", entity.getCreate_time().split(" - ")[0], entity.getCreate_time().split(" - ")[1]);
        }
        searchInfo.orderBy(true, false, "create_time");
        IPage<Article> pageInfo = new Page<>(page, limit);
        IPage<Article> selectPage = ArticleMapper.selectPage(pageInfo, searchInfo);
        if (!selectPage.getRecords().isEmpty()) {
            for (Article record : selectPage.getRecords()) {
                record.setContent(EmojiFilter.emojiRecovery2(record.getContent()));
            }
        }
        return selectPage;
    }

    @Override
    public Article selectByPrimaryKey(String id) {
        Article Article = ArticleMapper.selectById(id);
        if (Article == null) {
            return null;
        }
        if (Article.getContent() != null) {
            Article.setContent(EmojiFilter.emojiRecovery2(Article.getContent()));
        }
        return Article;
    }

    @Override
    public boolean insert(Article entity) {
        entity.setCreate_time(DateUtil.getNowDateSS());
        entity.setContent(EmojiFilter.emojiConvert1(entity.getContent()));
        Integer result = ArticleMapper.insert(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean updateByPrimaryKey(Article entity) {
        entity.setContent(EmojiFilter.emojiConvert1(entity.getContent()));
        Integer result = ArticleMapper.updateById(entity);
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByPrimaryKey(List<String> ids) {
        Integer result = 0;
        for (String id : ids) {
            result = ArticleMapper.deleteById(id);
        }
        if (result > 0) {
            return true;
        }
        return false;
    }

    @Override
    public List<Article> selectList(Article entity) {
        QueryWrapper<Article> wrapper = new QueryWrapper<>();
        if (CommonUtils.stringIsNotBlack(entity.getName())) {
            wrapper.like("name", entity.getName());
        }
        wrapper.orderBy(true, false, "create_time");
        List<Article> ArticleList = ArticleMapper.selectList(wrapper);
        return ArticleList;
    }

    @Override
    public int countBySourceUrl(String url) {
        if (!CommonUtils.stringIsNotBlack(url)) {
            return 0;
        }
        return ArticleMapper.selectCount(new QueryWrapper<Article>().eq("source_url", url)).intValue();
    }

    @Override
    public boolean insertFeedArticle(Article entity) {
        if (!CommonUtils.stringIsNotBlack(entity.getCreate_time())) {
            entity.setCreate_time(DateUtil.getNowDateSS());
        }
        Integer result = ArticleMapper.insert(entity);
        return result != null && result > 0;
    }
}