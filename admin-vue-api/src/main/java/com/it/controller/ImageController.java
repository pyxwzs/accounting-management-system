package com.it.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * 访问上传目录中的图片；缺失时回退到 default.jpg，避免示例数据指向不存在的文件导致 404。
 */
@RestController
public class ImageController {

    private static final String DEFAULT_IMAGE = "default.jpg";

    @Value("${file.upload.dir:upload}")
    private String uploadDir;

    @GetMapping("/image/{filename:.+}")
    public ResponseEntity<Resource> image(@PathVariable String filename) {
        Path root = Paths.get(uploadDir).toAbsolutePath().normalize();
        Path file = root.resolve(filename).normalize();
        if (!file.startsWith(root)) {
            return ResponseEntity.notFound().build();
        }
        if (!Files.isRegularFile(file)) {
            file = root.resolve(DEFAULT_IMAGE).normalize();
            if (!file.startsWith(root) || !Files.isRegularFile(file)) {
                return ResponseEntity.notFound().build();
            }
        }
        FileSystemResource body = new FileSystemResource(file);
        String name = file.getFileName().toString().toLowerCase();
        MediaType mediaType = MediaType.APPLICATION_OCTET_STREAM;
        if (name.endsWith(".jpg") || name.endsWith(".jpeg")) {
            mediaType = MediaType.IMAGE_JPEG;
        } else if (name.endsWith(".png")) {
            mediaType = MediaType.IMAGE_PNG;
        } else if (name.endsWith(".gif")) {
            mediaType = MediaType.IMAGE_GIF;
        } else if (name.endsWith(".webp")) {
            mediaType = MediaType.parseMediaType("image/webp");
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=3600")
                .contentType(mediaType)
                .body(body);
    }
}
