package com.example.controller;

import com.example.pojo.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@Slf4j
@RestController
public class UploadController {

    @Value("${file.upload.path:/tmp/uploads/}")
    private String uploadDir;

    @PostMapping("/upload")
    public Result upload(MultipartFile file) throws IOException {
        log.info("上传文件: {}", file.getOriginalFilename());

        if (file.isEmpty()) {
            return Result.error("文件为空");
        }

        String originalFilename = file.getOriginalFilename();
        String extName = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extName = originalFilename.substring(originalFilename.lastIndexOf("."));
        }

        String uniqueFileName = UUID.randomUUID().toString().replace("-", "") + extName;

        File dir = new File(uploadDir);
        if (!dir.exists()) {
            boolean created = dir.mkdirs();
            if (!created) {
                log.error("目录创建失败: {}", uploadDir);
                return Result.error("上传目录创建失败");
            }
        }

        File targetFile = new File(uploadDir + uniqueFileName);
        file.transferTo(targetFile);

        log.info("文件保存成功: {}", targetFile.getAbsolutePath());
        return Result.success("上传成功，文件名：" + uniqueFileName);
    }
}
