package com.it.controller;

import com.it.config.Operation;
import com.it.entity.User;
import com.it.service.UserService;
import com.it.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.MessageFormat;

@RestController
@Api(tags = "上传文件接口")
public class UpLoadController {

    @Value("${file.upload.dir:upload}")
    private String uploadDir;

    @Resource
    UserService userService;
    @Resource
    AuthUserUtils authUserUtils;

    @Operation("上传用户头像")
    @ApiOperation(value = "用户头像上传接口")
    @PostMapping("/upload/avatar")
    public String uploadAvatar(@RequestParam("file") MultipartFile file) {
        //获取文件后缀
        String extensionName = StringUtils.hasLength(file.getOriginalFilename()) ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase() : null;
        //获取当前日期
        String dateFolder = DateUtil.getCurrentTimeMillis();
        //生成新的文件名称
        String fileName = MessageFormat.format("{0}.{1}", dateFolder, extensionName);
        try {
            Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path path = root.resolve(fileName);
            FileUtil.createFilePath(path.getParent());
            Files.write(path, file.getBytes());
        } catch (IOException e) {
            throw new BusinessException(ResponseCode.UPLOAD_FILE_ERROR.getCode(), ResponseCode.UPLOAD_FILE_ERROR.getMessage());
        }
        //生成头像url
        String url = MessageFormat.format("/image/{0}", fileName);
        User user = new User();
        user.setImg_url(url);
        user.setId(authUserUtils.getLoginUserId());
        userService.updateByPrimaryKey(user);
        return url;
    }

    @Operation("上传附件")
    @ApiOperation(value = "文件上传接口")
    @PostMapping("/upload/other")
    public String uploadOther(@RequestParam("file") MultipartFile file) {

        //获取文件后缀
        String extensionName = StringUtils.hasLength(file.getOriginalFilename()) ? file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf(".") + 1).toLowerCase() : null;
        //获取当前日期
        String dateFolder = DateUtil.getCurrentTimeMillis();
        //生成新的文件名称
        String fileName = MessageFormat.format("{0}.{1}", dateFolder, extensionName);

        try {
            Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
            Path path = root.resolve(fileName);
            FileUtil.createFilePath(path.getParent());
            Files.write(path, file.getBytes());
        } catch (IOException e) {
            throw new BusinessException(ResponseCode.UPLOAD_FILE_ERROR.getCode(), ResponseCode.UPLOAD_FILE_ERROR.getMessage());
        }

        // 存相对路径，由前端 dev 代理 /image 或部署时同源访问
        String url = MessageFormat.format("/image/{0}", fileName);
        return url;
    }
}
