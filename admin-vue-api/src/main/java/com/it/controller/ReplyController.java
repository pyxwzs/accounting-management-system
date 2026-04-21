package com.it.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.config.Operation;
import com.it.entity.*;
import com.it.pojo.PageResult;
import com.it.service.*;
import com.it.util.AuthUserUtils;
import com.it.util.BusinessException;
import com.it.util.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/reply")
@Api(tags = "回复模块")
public class ReplyController {
    @Resource
    private ReplyService ReplyService;
    @Resource
    private AuthUserUtils authUserUtils;
    @Resource
    private UserService userService;
    @Resource
    private CommentService commentService;
    @Resource
    private MessageService messageService;
    @Resource
    private LeavewordService leavewordService;

    /**
     * @param entity
     * @return
     */
    @PostMapping("replys")
    @ApiOperation(value = "回复分页查询接口", notes = "传入的参数为:\n" +
            "    1.查询条件:Reply对象(属性非必传)\n" +
            "    2.page:页数\n" +
            "    3.limit:条数\n")
    public PageResult<Reply> pageReplys(@RequestBody Reply entity) {
        IPage<Reply> selectPage = ReplyService.selectPage(entity, entity.getCurrent(), entity.getSize());
        List<Reply> ReplyList = selectPage.getRecords();
        for (Reply Reply : ReplyList) {
            Reply.setCreate_time(Reply.getCreate_time().substring(0, 19));
        }
        return PageResult.getPage(selectPage.getRecords(), selectPage.getTotal());
    }

    /**
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    @Operation("删除回复")
    @ApiOperation(value = "回复删除接口", notes = "传入的参数为:\n" +
            "   Reply主键id\n")
    public void delete(@RequestBody List<String> ids) {
        boolean delete = ReplyService.deleteByPrimaryKey(ids);
        if (!delete) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PostMapping("/reply")
    @Operation("新增回复")
    @ApiOperation(value = "回复新增接口", notes = "传入的参数为:\n" +
            "   Reply对象\n")
    public void insert(@RequestBody Reply entity) {
        User send = userService.selectByPrimaryKey(authUserUtils.getLoginUserId());//当前评论的用户
        User user = null;
        Comment comment = commentService.selectByPrimaryKey(entity.getItem_id());//被回复的评论
        Leaveword leaveword = leavewordService.selectByPrimaryKey(entity.getItem_id());//被回复的评论
        if (comment != null) {
            user = userService.selectByPrimaryKey(comment.getUser_id());//这条评论的所属用户
        }
        if (leaveword != null) {
            user = userService.selectByPrimaryKey(leaveword.getUser_id());//这条评论的所属用户
        }

        if (user != null) {
            //新增消息
            Message message = new Message();
            message.setType("0");
            message.setSend_id("system");
            message.setUser_id(user.getId());
            String content = send.getNick_name() + "回复了您:" + entity.getContent();
            message.setContent(content);
            messageService.insert(message);
            entity.setSend_id(user.getId());
        }
        boolean insert = ReplyService.insert(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     */
    @PostMapping("/replyItem")
    @Operation("回复条目回复")
    @ApiOperation(value = "回复条目回复接口", notes = "传入的参数为:\n" +
            "   Reply对象\n")
    public void replyItem(@RequestBody Reply entity) {
        User user = userService.selectByPrimaryKey(entity.getSend_id());//这条回复的所属用户
        User send = userService.selectByPrimaryKey(authUserUtils.getLoginUserId());//当前评论的用户
        if (user != null) {
            //新增消息
            Message message = new Message();
            message.setUser_id(user.getId());
            message.setType("0");
            message.setSend_id("system");
            String content = send.getNick_name() + "回复了您:" + entity.getContent();
            message.setContent(content);
            messageService.insert(message);
        }
        boolean insert = ReplyService.insert(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param entity
     * @return
     */
    @PutMapping("/reply")
    @Operation("编辑回复")
    @ApiOperation(value = "回复编辑接口", notes = "传入的参数为:\n" +
            "   Reply对象\n")
    public void edit(@RequestBody Reply entity) {

        boolean insert = ReplyService.updateByPrimaryKey(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/reply/{id}")
    @ApiOperation(value = "回复单个对象查询接口", notes = "传入的参数为:\n" +
            "  Reply主键id\n")
    public Reply selectOne(@PathVariable("id") String id) {
        Reply entity = ReplyService.selectByPrimaryKey(id);
        if (entity == null) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
        return entity;
    }
}
