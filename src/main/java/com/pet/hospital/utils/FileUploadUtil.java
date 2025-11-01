package com.pet.hospital.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

public class FileUploadUtil {
    
    private static final String UPLOAD_DIR = "uploads/pets/";
    
    /**
     * 上传文件并返回文件路径
     * @param file 上传的文件
     * @return 文件存储路径
     * @throws IOException
     */
    public static String uploadPetImage(MultipartFile file) throws IOException {
        // 检查文件是否为空
        if (file == null || file.isEmpty()) {
            throw new IOException("文件不能为空");
        }
        
        // 创建上传目录
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // 生成唯一的文件名
        String originalFilename = file.getOriginalFilename();
        String fileExtension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String uniqueFilename = UUID.randomUUID().toString() + fileExtension;
        
        // 保存文件
        Path filePath = uploadPath.resolve(uniqueFilename);
        Files.write(filePath, file.getBytes());
        
        // 返回相对路径
        return UPLOAD_DIR + uniqueFilename;
    }
}