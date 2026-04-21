package com.it.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.it.entity.ArticleFavorite;
import com.it.mapper.ArticleFavoriteMapper;
import com.it.service.ArticleFavoriteService;
import com.it.util.CommonUtils;
import com.it.util.DateUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ArticleFavoriteServiceImpl implements ArticleFavoriteService {

    @Resource
    private ArticleFavoriteMapper articleFavoriteMapper;

    @Override
    public boolean toggle(String userId, String articleId) {
        if (!CommonUtils.stringIsNotBlack(userId) || !CommonUtils.stringIsNotBlack(articleId)) {
            return false;
        }
        QueryWrapper<ArticleFavorite> w = new QueryWrapper<>();
        w.eq("user_id", userId).eq("article_id", articleId);
        ArticleFavorite existing = articleFavoriteMapper.selectOne(w);
        if (existing != null) {
            articleFavoriteMapper.deleteById(existing.getId());
            return false;
        }
        ArticleFavorite f = new ArticleFavorite();
        f.setUser_id(userId);
        f.setArticle_id(articleId);
        f.setCreate_time(DateUtil.getNowDateSS());
        articleFavoriteMapper.insert(f);
        return true;
    }

    @Override
    public boolean isFavorite(String userId, String articleId) {
        if (!CommonUtils.stringIsNotBlack(userId) || !CommonUtils.stringIsNotBlack(articleId)) {
            return false;
        }
        QueryWrapper<ArticleFavorite> w = new QueryWrapper<>();
        w.eq("user_id", userId).eq("article_id", articleId);
        return articleFavoriteMapper.selectCount(w) > 0;
    }

    @Override
    public Set<String> listArticleIds(String userId) {
        if (!CommonUtils.stringIsNotBlack(userId)) {
            return new HashSet<>();
        }
        QueryWrapper<ArticleFavorite> w = new QueryWrapper<>();
        w.eq("user_id", userId);
        List<ArticleFavorite> list = articleFavoriteMapper.selectList(w);
        return list.stream().map(ArticleFavorite::getArticle_id).collect(Collectors.toSet());
    }
}
