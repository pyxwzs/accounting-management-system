package com.it.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.config.Operation;
import com.it.entity.Article;
import com.it.entity.Slideshow;
import com.it.pojo.PageResult;
import com.it.service.ArticleService;
import com.it.service.SlideshowService;
import com.it.util.AuthUserUtils;
import com.it.util.BusinessException;
import com.it.util.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/slideshow")
@Api(tags = "轮播图模块")
public class SlideshowController {
    @Resource
    private SlideshowService SlideshowService;
    @Resource
    private AuthUserUtils authUserUtils;
    @Resource
    private ArticleService articleService;

    /**
     * @param entity
     * @return
     */
    @PostMapping("slideshows")
    @ApiOperation(value = "轮播图分页查询接口", notes = "传入的参数为:\n" +
            "    1.查询条件:Slideshow对象(属性非必传)\n" +
            "    2.page:页数\n" +
            "    3.limit:条数\n")
    public PageResult<Slideshow> pageSlideshows(@RequestBody Slideshow entity) {
        IPage<Slideshow> selectPage = SlideshowService.selectPage(entity, entity.getCurrent(), entity.getSize());
        List<Slideshow> SlideshowList = selectPage.getRecords();
        for (Slideshow Slideshow : SlideshowList) {

            Slideshow.setCreate_time(Slideshow.getCreate_time().substring(0, 19));
        }
        return PageResult.getPage(selectPage.getRecords(), selectPage.getTotal());
    }

    /**
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    @Operation("删除轮播图")
    @ApiOperation(value = "轮播图删除接口", notes = "传入的参数为:\n" +
            "   Slideshow主键id\n")
    public void delete(@RequestBody List<String> ids) {
        boolean delete = SlideshowService.deleteByPrimaryKey(ids);
        if (!delete) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PostMapping("/slideshow")
    @Operation("新增轮播图")
    @ApiOperation(value = "轮播图新增接口", notes = "传入的参数为:\n" +
            "   Slideshow对象\n")
    public void insert(@RequestBody Slideshow entity) {
        boolean insert = SlideshowService.insert(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PutMapping("/slideshow")
    @Operation("编辑轮播图")
    @ApiOperation(value = "轮播图编辑接口", notes = "传入的参数为:\n" +
            "   Slideshow对象\n")
    public void edit(@RequestBody Slideshow entity) {
        boolean insert = SlideshowService.updateByPrimaryKey(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/slideshow/{id}")
    @ApiOperation(value = "轮播图单个对象查询接口", notes = "传入的参数为:\n" +
            "  Slideshow主键id\n")
    public Slideshow selectOne(@PathVariable("id") String id) {
        Slideshow entity = SlideshowService.selectByPrimaryKey(id);
        if (entity == null) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
        return entity;
    }

    @GetMapping("/slideshows")
    @ApiOperation(value = "轮播图列表查询接口")
    public List<Slideshow> pageSlideshows() {
        List<Slideshow> selectList = SlideshowService.selectList(new Slideshow());
        for (Slideshow slideshow : selectList) {
            Article article = articleService.selectByPrimaryKey(slideshow.getItem_id());
            if (article != null) {
                slideshow.setArticle(article);
            } else {
                slideshow.setArticle(new Article());
            }
        }
        return selectList;
    }
}
