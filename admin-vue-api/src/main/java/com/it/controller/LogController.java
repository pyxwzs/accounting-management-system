package com.it.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.config.Operation;
import com.it.entity.Log;
import com.it.pojo.PageResult;
import com.it.service.LogService;
import com.it.util.BusinessException;
import com.it.util.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/log")
@Api(tags = "日志模块")
public class LogController {
    @Resource
    private LogService logService;

    /**
     * @param entity
     * @return
     */
    @PostMapping("/logs")
    @ApiOperation(value = "日志分页查询接口", notes = "传入的参数为:\n" +
            "    1.查询条件:log对象(属性非必传)\n" +
            "    2.page:页数\n" +
            "    3.limit:条数\n")
    public PageResult<Log> pageLogs(@RequestBody Log entity) {
        IPage<Log> selectPage = logService.selectPage(entity, entity.getCurrent(), entity.getSize());
        List<Log> logList = selectPage.getRecords();
        for (Log log : logList) {
            log.setCreate_time(log.getCreate_time().substring(0, 19));
        }
        return PageResult.getPage(selectPage.getRecords(), selectPage.getTotal());
    }


    /**
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    @Operation("删除操作日志")
    @ApiOperation(value = "日志删除接口", notes = "传入的参数为:\n" +
            "   Log主键id\n")
    public void deleteLog(@RequestBody List<String> ids) {
        boolean delete = logService.deleteByPrimaryKey(ids);
        if (!delete) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

}
