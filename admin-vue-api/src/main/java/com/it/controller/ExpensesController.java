package com.it.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.config.Operation;
import com.it.entity.Expenses;
import com.it.pojo.PageResult;
import com.it.service.ExpensesService;
import com.it.util.AuthUserUtils;
import com.it.util.BusinessException;
import com.it.util.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/expenses")
@Api(tags = "报销模块")
public class ExpensesController {
    @Resource
    private ExpensesService ExpensesService;
    @Resource
    private AuthUserUtils authUserUtils;


    /**
     * @param entity
     * @return
     */
    @PostMapping("expensess")
    @ApiOperation(value = "报销信息分页查询接口", notes = "传入的参数为:\n" +
            "    1.查询条件:Expenses对象(属性非必传)\n" +
            "    2.page:页数\n" +
            "    3.limit:条数\n")
    public PageResult<Expenses> pageExpensess(@RequestBody Expenses entity) {
        IPage<Expenses> selectPage = ExpensesService.selectPage(entity, entity.getCurrent(), entity.getSize());
        return PageResult.getPage(selectPage.getRecords(), selectPage.getTotal());
    }

    /**
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    @Operation("删除报销记录")
    @ApiOperation(value = "报销信息删除接口", notes = "传入的参数为:\n" +
            "   Expenses主键id\n")
    public void deleteLog(@RequestBody List<String> ids) {
        boolean delete = ExpensesService.deleteByPrimaryKey(ids);
        if (!delete) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PostMapping("/expenses")
    @Operation("新增报销记录")
    @ApiOperation(value = "报销信息新增接口", notes = "传入的参数为:\n" +
            "   Expenses对象\n")
    public void insertRole(@RequestBody Expenses entity) {
        boolean insert = ExpensesService.insert(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PutMapping("/expenses")
    @Operation("编辑报销记录")
    @ApiOperation(value = "报销信息编辑接口", notes = "传入的参数为:\n" +
            "   Expenses对象\n")
    public void editRole(@RequestBody Expenses entity) {
        boolean insert = ExpensesService.updateByPrimaryKey(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/expenses/{id}")
    @ApiOperation(value = "报销信息单个对象查询接口", notes = "传入的参数为:\n" +
            "  Expenses主键id\n")
    public Expenses selectOne(@PathVariable("id") String id) {
        Expenses entity = ExpensesService.selectByPrimaryKey(id);
        if (entity == null) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
        return entity;
    }
}
