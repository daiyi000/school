package utils;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

import jakarta.servlet.http.Part;

public class FileUploadUtils {
    
    private static final String UPLOAD_DIRECTORY = "uploads";
    
    // 获取上传基础路径
    public static String getUploadBasePath(String contextPath) {
        return contextPath + File.separator + UPLOAD_DIRECTORY;
    }
    
    // 创建上传目录
    public static void createUploadDirectories(String basePath) {
        File baseDir = new File(basePath + File.separator + UPLOAD_DIRECTORY);
        if (!baseDir.exists()) {
            baseDir.mkdirs();
        }
        
        // 创建帖子和私信的子目录
        File postsDir = new File(basePath + File.separator + UPLOAD_DIRECTORY + File.separator + "posts");
        if (!postsDir.exists()) {
            postsDir.mkdirs();
        }
        
        File messagesDir = new File(basePath + File.separator + UPLOAD_DIRECTORY + File.separator + "messages");
        if (!messagesDir.exists()) {
            messagesDir.mkdirs();
        }
    }
    
    // 保存上传的文件并返回相对路径
    public static String saveUploadedFile(Part filePart, String basePath, String subDirectory) throws IOException {
        // 创建唯一的文件名
        String fileName = getUniqueFileName(filePart.getSubmittedFileName());
        
        // 创建完整的目录路径
        String uploadDir = basePath + File.separator + UPLOAD_DIRECTORY + File.separator + subDirectory;
        
        // 确保目录存在
        File uploadDirFile = new File(uploadDir);
        if (!uploadDirFile.exists()) {
            uploadDirFile.mkdirs();
        }
        
        // 创建完整的文件路径
        String filePath = uploadDir + File.separator + fileName;
        
        // 保存文件
        filePart.write(filePath);
        
        // 返回相对路径用于存储在数据库中
        return UPLOAD_DIRECTORY + "/" + subDirectory + "/" + fileName;
    }
    
    // 生成唯一文件名以避免冲突
    private static String getUniqueFileName(String originalFileName) {
        // 获取文件扩展名
        String extension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i > 0) {
            extension = originalFileName.substring(i);
        }
        
        // 创建日期格式化对象以便更好地组织文件
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String datePrefix = dateFormat.format(new Date());
        
        // 返回唯一文件名: 日期_uuid_扩展名
        return datePrefix + "_" + UUID.randomUUID().toString() + extension;
    }
    
    // 验证上传的文件是否为图片
    public static boolean isImageFile(Part filePart) {
        String contentType = filePart.getContentType();
        return contentType != null && contentType.startsWith("image/");
    }
    
    // 获取文件大小（MB）
    public static double getFileSizeInMB(Part filePart) {
        return filePart.getSize() / (1024.0 * 1024.0);
    }
}