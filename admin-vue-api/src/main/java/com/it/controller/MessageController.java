package com.it.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.it.config.Operation;
import com.it.entity.Message;
import com.it.pojo.PageResult;
import com.it.service.MessageService;
import com.it.util.AuthUserUtils;
import com.it.util.BusinessException;
import com.it.util.EmojiFilter;
import com.it.util.ResponseCode;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@RestController
@RequestMapping("/message")
@Api(tags = "消息模块")
public class MessageController {
    @Resource
    private MessageService MessageService;
    @Resource
    private AuthUserUtils authUserUtils;


    /**
     * @param entity
     * @return
     */
    @PostMapping("messages")
    @ApiOperation(value = "消息分页查询接口", notes = "传入的参数为:\n" +
            "    1.查询条件:Message对象(属性非必传)\n" +
            "    2.page:页数\n" +
            "    3.limit:条数\n")
    public PageResult<Message> pageMessages(@RequestBody Message entity) {
        IPage<Message> selectPage = MessageService.selectPage(entity, entity.getCurrent(), entity.getSize());
        List<Message> MessageList = selectPage.getRecords();
        for (Message Message : MessageList) {
            //修改状态
            Message.setCreate_time(Message.getCreate_time().substring(0, 19));
            Message.setState("1");
            MessageService.updateByPrimaryKey(Message);
            Message.setContent(EmojiFilter.emojiRecovery2(Message.getContent()));
        }
        return PageResult.getPage(selectPage.getRecords(), selectPage.getTotal());

    }

    /**
     * @param ids
     * @return
     */
    @PostMapping("/delete")
    @Operation("删除消息")
    @ApiOperation(value = "消息删除接口", notes = "传入的参数为:\n" +
            "   Message主键id\n")
    public void delete(@RequestBody List<String> ids) {
        boolean delete = MessageService.deleteByPrimaryKey(ids);
        if (!delete) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PostMapping("/message")
    @Operation("新增消息")
    @ApiOperation(value = "消息新增接口", notes = "传入的参数为:\n" +
            "   Message对象\n")
    public void insert(@RequestBody Message entity) {
        entity.setSend_id(authUserUtils.getLoginUserName());
        boolean insert = MessageService.insert(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }


    /**
     * @param entity
     * @return
     */
    @PutMapping("/message")
    @Operation("编辑消息")
    @ApiOperation(value = "消息编辑接口", notes = "传入的参数为:\n" +
            "   Message对象\n")
    public void edit(@RequestBody Message entity) {
        boolean insert = MessageService.updateByPrimaryKey(entity);
        if (!insert) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
    }

    /**
     * @param id
     * @return
     */
    @GetMapping("/message/{id}")
    @ApiOperation(value = "消息单个对象查询接口", notes = "传入的参数为:\n" +
            "  Message主键id\n")
    public Message selectOne(@PathVariable("id") String id) {
        Message entity = MessageService.selectByPrimaryKey(id);
        if (entity == null) {
            throw new BusinessException(ResponseCode.OPERATION_ERROR.getCode(), ResponseCode.OPERATION_ERROR.getMessage());
        }
        return entity;
    }

    /**
     * @param
     * @return
     */
    @GetMapping("/haveMessage")
    @ApiOperation(value = "获取当前用户是否有新消息(用户间发送的消息)")
    public boolean haveMessage() {
        Message message = new Message();
        message.setType("1");
        message.setState("0");
        message.setUser_id(authUserUtils.getLoginUserName());
        List<Message> messages = MessageService.selectList(message);
        if (messages.isEmpty()) {
            return false;
        }
        return true;
    }

    /**
     * @param
     * @return
     */
    @GetMapping("/haveSystemMessage")
    @ApiOperation(value = "获取当前用户是否有新消息(系统消息)")
    public boolean haveSystemMessage() {
        Message message = new Message();
        message.setType("0");
        message.setState("0");
        message.setUser_id(authUserUtils.getLoginUserId());
        List<Message> messages = MessageService.selectList(message);
        if (messages.isEmpty()) {
            return false;
        }
        return true;
    }
}
