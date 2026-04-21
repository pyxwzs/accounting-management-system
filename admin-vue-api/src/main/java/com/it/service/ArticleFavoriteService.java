package com.it.service;

import java.util.Set;

public interface ArticleFavoriteService {

    /**
     * 切换收藏，返回当前是否已收藏
     */
    boolean toggle(String userId, String articleId);

    boolean isFavorite(String userId, String articleId);

    Set<String> listArticleIds(String userId);
}
