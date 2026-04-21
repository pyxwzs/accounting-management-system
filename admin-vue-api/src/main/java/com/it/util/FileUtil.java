package com.it.util;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;


@Slf4j
public class FileUtil {

    /**
     * @description 创建文件夹
     * @author 明思梨: https://space.bilibili.com/486686697
     * @date 2023/6/5 20:19
     */
    public static void createFilePath(Path path) {
        if (!Files.isWritable(path)) {
            try {
                Files.createDirectories(path);
            } catch (IOException e) {
                log.error(e.getMessage(), e);
                throw new BusinessException(ResponseCode.CREATE_PATH_ERROR.getCode(), ResponseCode.CREATE_PATH_ERROR.getMessage());
            }
        }
    }

}
