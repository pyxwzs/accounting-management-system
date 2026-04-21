package com.it.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.config.Operation;
import com.it.entity.Article;
import com.it.pojo.PageResult;
import com.it.service.ArticleFavoriteService;
import com.it.service.ArticleService;
import com.it.service.FinanceRssSyncService;
import com.it.util.BusinessException;
import com.it.util.CommonUtils;
import com.it.util.JwtUtil;
import com.it.util.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/article")
@Api(tags = "文章模块")
public class ArticleController {
    @Resource
    private ArticleService ArticleService;
    @Resource
    private ArticleFavoriteService articleFavoriteService;
    @Resource
    private FinanceRssSyncService financeRssSyncService;
    @Resource
    private JwtUtil jwtUtil;

    /**
     * 列表页为 anon，若请求携带有效 Token，则解析用户并填充是否收藏。
     */
    private String optionalUserId() {
        try {
            ServletRequestAttributes ra = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (ra == null) {
                return null;
            }
            HttpServletRequest req = ra.getRequest();
            String token = req.getHeader("Authorization");
            if (!CommonUtils.stringIsNotBlack(token)) {
                return null;
            }
            if (!jwtUtil.verify(token) || jwtUtil.isExpired(token)) {
                return null;
            }
            return jwtUtil.getAccount(token);
        } catch (Exception e) {
            return null;
        }
    }

    private String requireUserId() {
        String uid = optionalUserId();
        if (uid == null) {
            throw new BusinessException(ResponseCode.TOKEN_ERROR.getCode(), ResponseCode.TOKEN_ERROR.getMessage());
        }
        return uid;
    }

    private void enrichFavorites(List<Article> list, String userId) {
        if (list == null || list.isEmpty() || !CommonUtils.stringIsNotBlack(userId)) {
            return;
        }
        Set<String> fav = articleFavoriteService.listArticleIds(userId);
        for (Article a : list) {
            a.setFavorited(fav.contains(a.getId()));
        }
    }

    /**
     * @param entity
     * @return
     */
    @PostMapping("articles")
    @ApiOperation(value = "文章信息分页查询接口", notes = "传入的参数为:\n" +
            "    1.查询条件:Article对象(属性非必传)\n" +
            "    2.page:页数\n" +
            "    3.limit:条数\n")
    public PageResult<Article> pageArticles(@RequestBody Article entity) {
        IPage<Article> selectPage = ArticleService.selectPage(entity, entity.getCurrent(), entity.getSize());
        List<Article> ArticleList = selectPage.getRecords();
        String uid = optionalUserId();
        enrichFavorites(ArticleList, uid);
        for (Article Article : ArticleList) {
            if (Article.getCreate_time() != null && Article.getCreate_time().length() > 19) {
                Article.setCreate_time(Article.getCreate_time().substring(0, 19));
            }
        }
        return PageResult.getPage(selectPage.getRecords(), selectPage.getTotal());
    }

    /**
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    @Operation("删除文章")
    @ApiOperation(value = "文章信息删除接口", notes = "传入的参数为:\n" +
            "   Article主键id\n")
    public void deleteLog(@RequestBody List<String> ids) {
        boolean delete = ArticleService.deleteByPrimaryKey(ids);
        if (!delete) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PostMapping("/article")
    @Operation("新增文章")
    @ApiOperation(value = "文章信息新增接口", notes = "传入的参数为:\n" +
            "   Article对象\n")
    public void insertRole(@RequestBody Article entity) {
        boolean insert = ArticleService.insert(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PutMapping("/article")
    @Operation("编辑文章")
    @ApiOperation(value = "文章信息编辑接口", notes = "传入的参数为:\n" +
            "   Article对象\n")
    public void editRole(@RequestBody Article entity) {
        boolean insert = ArticleService.updateByPrimaryKey(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/article/{id}")
    @ApiOperation(value = "文章信息单个对象查询接口", notes = "传入的参数为:\n" +
            "  Article主键id\n")
    public Article selectOne(@PathVariable("id") String id) {
        Article entity = ArticleService.selectByPrimaryKey(id);
        if (entity == null) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
        String uid = optionalUserId();
        if (CommonUtils.stringIsNotBlack(uid)) {
            entity.setFavorited(articleFavoriteService.isFavorite(uid, id));
        }
        return entity;
    }

    @PostMapping("/sync-rss")
    @Operation("同步RSS财经资讯")
    @ApiOperation("拉取 RSS 财经快讯并入库（去重）")
    public Map<String, Object> syncRss() {
        requireUserId();
        int n = financeRssSyncService.sync();
        Map<String, Object> m = new HashMap<>(2);
        m.put("imported", n);
        return m;
    }

    @PostMapping("/favorite/toggle")
    @Operation("切换资讯收藏")
    @ApiOperation("切换资讯收藏")
    public Map<String, Object> toggleFavorite(@RequestBody Map<String, String> body) {
        String uid = requireUserId();
        String articleId = body != null ? body.get("article_id") : null;
        if (!CommonUtils.stringIsNotBlack(articleId)) {
            throw new BusinessException(ResponseCode.DATA_PARAM_ERROR.getCode(), ResponseCode.DATA_PARAM_ERROR.getMessage());
        }
        boolean favorited = articleFavoriteService.toggle(uid, articleId);
        Map<String, Object> m = new HashMap<>(2);
        m.put("favorited", favorited);
        return m;
    }

    @GetMapping("/favorite/ids")
    @ApiOperation("当前用户已收藏的资讯 id 列表")
    public Map<String, Object> favoriteIds() {
        String uid = requireUserId();
        Set<String> ids = articleFavoriteService.listArticleIds(uid);
        Map<String, Object> m = new HashMap<>(2);
        m.put("ids", ids.stream().collect(Collectors.toList()));
        return m;
    }
}
