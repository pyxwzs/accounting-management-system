package com.it.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.config.Operation;
import com.it.entity.Leaveword;
import com.it.entity.Reply;
import com.it.pojo.PageResult;
import com.it.service.LeavewordService;
import com.it.service.ReplyService;
import com.it.service.UserService;
import com.it.util.AuthUserUtils;
import com.it.util.BusinessException;
import com.it.util.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/leaveword")
@Api(tags = "留言模块")
public class LeavewordController {
    @Resource
    private LeavewordService LeavewordService;
    @Resource
    private AuthUserUtils authUserUtils;
    @Resource
    private ReplyService replyService;


    @Resource
    private UserService userService;

    /**
     * @param entity
     * @return
     */
    @PostMapping("leavewords")
    @ApiOperation(value = "留言分页查询接口", notes = "传入的参数为:\n" +
            "    1.查询条件:Leaveword对象(属性非必传)\n" +
            "    2.page:页数\n" +
            "    3.limit:条数\n")
    public PageResult<Leaveword> pageLeavewords(@RequestBody Leaveword entity) {
        IPage<Leaveword> selectPage = LeavewordService.selectPage(entity, entity.getCurrent(), entity.getSize());
        List<Leaveword> LeavewordList = selectPage.getRecords();
        for (Leaveword Leaveword : LeavewordList) {
            Leaveword.setCreate_time(Leaveword.getCreate_time().substring(0, 19));
            List<Reply> replyList = replyService.selectList(Leaveword.getId());
            Leaveword.setReplyList(replyList);
        }
        return PageResult.getPage(selectPage.getRecords(), selectPage.getTotal());
    }

    /**
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    @Operation("删除留言")
    @ApiOperation(value = "留言删除接口", notes = "传入的参数为:\n" +
            "   Leaveword主键id\n")
    public void delete(@RequestBody List<String> ids) {
        boolean delete = LeavewordService.deleteByPrimaryKey(ids);
        if (!delete) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PostMapping("/leaveword")
    @Operation("新增留言")
    @ApiOperation(value = "留言新增接口", notes = "传入的参数为:\n" +
            "   Leaveword对象\n")
    public void insert(@RequestBody Leaveword entity) {
        boolean insert = LeavewordService.insert(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PutMapping("/leaveword")
    @Operation("编辑留言")
    @ApiOperation(value = "留言编辑接口", notes = "传入的参数为:\n" +
            "   Leaveword对象\n")
    public void edit(@RequestBody Leaveword entity) {
        boolean insert = LeavewordService.updateByPrimaryKey(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/leaveword/{id}")
    @ApiOperation(value = "留言单个对象查询接口", notes = "传入的参数为:\n" +
            "  Leaveword主键id\n")
    public Leaveword selectOne(@PathVariable("id") String id) {
        Leaveword entity = LeavewordService.selectByPrimaryKey(id);
        if (entity == null) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
        return entity;
    }
}
