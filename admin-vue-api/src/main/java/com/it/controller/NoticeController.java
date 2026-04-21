package com.it.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.config.Operation;
import com.it.entity.Notice;
import com.it.pojo.PageResult;
import com.it.service.NoticeService;
import com.it.util.AuthUserUtils;
import com.it.util.BusinessException;
import com.it.util.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/notice")
@Api(tags = "系统公告模块")
public class NoticeController {
    @Resource
    private NoticeService NoticeService;
    @Resource
    private AuthUserUtils authUserUtils;


    /**
     * @param entity
     * @return
     */
    @PostMapping("notices")
    @ApiOperation(value = "系统公告信息分页查询接口", notes = "传入的参数为:\n" +
            "    1.查询条件:Notice对象(属性非必传)\n" +
            "    2.page:页数\n" +
            "    3.limit:条数\n")
    public PageResult<Notice> pageNotices(@RequestBody Notice entity) {
        IPage<Notice> selectPage = NoticeService.selectPage(entity, entity.getCurrent(), entity.getSize());
        List<Notice> NoticeList = selectPage.getRecords();
        for (Notice Notice : NoticeList) {
            Notice.setCreate_time(Notice.getCreate_time().substring(0, 19));
        }
        return PageResult.getPage(selectPage.getRecords(), selectPage.getTotal());
    }

    /**
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    @Operation("删除系统公告")
    @ApiOperation(value = "系统公告信息删除接口", notes = "传入的参数为:\n" +
            "   Notice主键id\n")
    public void deleteLog(@RequestBody List<String> ids) {
        boolean delete = NoticeService.deleteByPrimaryKey(ids);
        if (!delete) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PostMapping("/notice")
    @Operation("新增系统公告")
    @ApiOperation(value = "系统公告信息新增接口", notes = "传入的参数为:\n" +
            "   Notice对象\n")
    public void insertRole(@RequestBody Notice entity) {
        boolean insert = NoticeService.insert(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PutMapping("/notice")
    @Operation("编辑系统公告")
    @ApiOperation(value = "系统公告信息编辑接口", notes = "传入的参数为:\n" +
            "   Notice对象\n")
    public void editRole(@RequestBody Notice entity) {
        boolean insert = NoticeService.updateByPrimaryKey(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/notice/{id}")
    @ApiOperation(value = "系统公告信息单个对象查询接口", notes = "传入的参数为:\n" +
            "  Notice主键id\n")
    public Notice selectOne(@PathVariable("id") String id) {
        Notice entity = NoticeService.selectByPrimaryKey(id);
        if (entity == null) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
        return entity;
    }
}
