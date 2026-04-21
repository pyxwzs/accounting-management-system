package com.it.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.config.Operation;
import com.it.entity.Plan;
import com.it.pojo.PageResult;
import com.it.service.PlanForecastHelper;
import com.it.service.PlanService;
import com.it.util.AuthUserUtils;
import com.it.util.CommonUtils;
import com.it.util.BusinessException;
import com.it.util.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/plan")
@Api(tags = "存钱记录模块")
public class PlanController {
    @Resource
    private PlanService PlanService;
    @Resource
    private AuthUserUtils authUserUtils;
    @Resource
    private PlanForecastHelper planForecastHelper;


    /**
     * @param entity
     * @return
     */
    @PostMapping("plans")
    @ApiOperation(value = "存钱记录信息分页查询接口", notes = "传入的参数为:\n" +
            "    1.查询条件:Plan对象(属性非必传)\n" +
            "    2.page:页数\n" +
            "    3.limit:条数\n")
    public PageResult<Plan> pagePlans(@RequestBody Plan entity) {
        IPage<Plan> selectPage = PlanService.selectPage(entity, entity.getCurrent(), entity.getSize());
        List<Plan> records = selectPage.getRecords();
        if (CommonUtils.stringIsNotBlack(entity.getUser_id())) {
            planForecastHelper.enrichPlans(records, entity.getUser_id());
        }
        return PageResult.getPage(records, selectPage.getTotal());
    }

    /**
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    @Operation("删除存钱计划")
    @ApiOperation(value = "存钱记录信息删除接口", notes = "传入的参数为:\n" +
            "   Plan主键id\n")
    public void deleteLog(@RequestBody List<String> ids) {
        boolean delete = PlanService.deleteByPrimaryKey(ids);
        if (!delete) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PostMapping("/plan")
    @Operation("新增存钱计划")
    @ApiOperation(value = "存钱记录信息新增接口", notes = "传入的参数为:\n" +
            "   Plan对象\n")
    public void insertRole(@RequestBody Plan entity) {
        boolean insert = PlanService.insert(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PutMapping("/plan")
    @Operation("编辑存钱计划")
    @ApiOperation(value = "存钱记录信息编辑接口", notes = "传入的参数为:\n" +
            "   Plan对象\n")
    public void editRole(@RequestBody Plan entity) {
        boolean insert = PlanService.updateByPrimaryKey(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/plan/{id}")
    @ApiOperation(value = "存钱记录信息单个对象查询接口", notes = "传入的参数为:\n" +
            "  Plan主键id\n")
    public Plan selectOne(@PathVariable("id") String id) {
        Plan entity = PlanService.selectByPrimaryKey(id);
        if (entity == null) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
        return entity;
    }
}
