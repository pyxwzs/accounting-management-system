package com.it.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.config.Operation;
import com.it.entity.Budget;
import com.it.pojo.PageResult;
import com.it.service.BudgetAnalysisHelper;
import com.it.service.BudgetService;
import com.it.util.AuthUserUtils;
import com.it.util.BusinessException;
import com.it.util.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/budget")
@Api(tags = "预算模块")
public class BudgetController {
    @Resource
    private BudgetService BudgetService;
    @Resource
    private AuthUserUtils authUserUtils;
    @Resource
    private BudgetAnalysisHelper budgetAnalysisHelper;

    /**
     * @param entity
     * @return
     */
    @PostMapping("budgets")
    @ApiOperation(value = "预算分页查询接口", notes = "传入的参数为:\n" +
            "    1.查询条件:Budget对象(属性非必传)\n" +
            "    2.page:页数\n" +
            "    3.limit:条数\n")
    public PageResult<Budget> pageBudgets(@RequestBody Budget entity) {
        IPage<Budget> selectPage = BudgetService.selectPage(entity, entity.getCurrent(), entity.getSize());
        List<Budget> BudgetList = selectPage.getRecords();
        for (Budget Budget : BudgetList) {
            budgetAnalysisHelper.enrich(Budget);
            if (Budget.getCreate_time() != null && Budget.getCreate_time().length() >= 19) {
                Budget.setCreate_time(Budget.getCreate_time().substring(0, 19));
            }
        }
        return PageResult.getPage(selectPage.getRecords(), selectPage.getTotal());
    }

    /**
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    @Operation("删除预算")
    @ApiOperation(value = "预算删除接口", notes = "传入的参数为:\n" +
            "   Budget主键id\n")
    public void deleteLog(@RequestBody List<String> ids) {
        boolean delete = BudgetService.deleteByPrimaryKey(ids);
        if (!delete) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PostMapping("/budget")
    @Operation("新增预算")
    @ApiOperation(value = "预算新增接口", notes = "传入的参数为:\n" +
            "   Budget对象\n")
    public void insertRole(@RequestBody Budget entity) {
        boolean insert = BudgetService.insert(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PutMapping("/budget")
    @Operation("编辑预算")
    @ApiOperation(value = "预算编辑接口", notes = "传入的参数为:\n" +
            "   Budget对象\n")
    public void editRole(@RequestBody Budget entity) {
        boolean insert = BudgetService.updateByPrimaryKey(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/budget/{id}")
    @ApiOperation(value = "预算单个对象查询接口", notes = "传入的参数为:\n" +
            "  Budget主键id\n")
    public Budget selectOne(@PathVariable("id") String id) {
        Budget entity = BudgetService.selectByPrimaryKey(id);
        if (entity == null) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
        return entity;
    }

    @PostMapping("list")
    @ApiOperation(value = "预算列表查询接口")
    public List<Budget> list(@RequestBody Budget entity) {
        List<Budget> budgetList = BudgetService.selectList(entity);
        return budgetList;
    }
}
