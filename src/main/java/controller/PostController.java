package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.Post;
import model.User;
import service.PostService;
import utils.FileUploadUtils;

import java.io.IOException;

@WebServlet("/post")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 5 * 1024 * 1024,    // 5 MB
    maxRequestSize = 10 * 1024 * 1024 // 10 MB
)
public class PostController extends HttpServlet {
    private PostService postService;
    
    @Override
    public void init() throws ServletException {
        postService = new PostService();
        
        // Create upload directories
        String basePath = getServletContext().getRealPath("/");
        FileUploadUtils.createUploadDirectories(basePath);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            // Not logged in, redirect to login page
            response.sendRedirect("login");
            return;
        }
        
        // Get form data
        String content = request.getParameter("content");
        
        // Basic input validation
        if (content == null || content.trim().isEmpty()) {
            request.setAttribute("error", "帖子内容不能为空");
            request.getRequestDispatcher("/hall.jsp").forward(request, response);
            return;
        }
        
        // Create post object
        Post post = new Post(user.getId(), content);
        
        // Handle image upload if present
        Part filePart = request.getPart("image");
        if (filePart != null && filePart.getSize() > 0) {
            // Validate image
            if (!FileUploadUtils.isImageFile(filePart)) {
                request.setAttribute("error", "只能上传图片文件");
                request.getRequestDispatcher("/hall.jsp").forward(request, response);
                return;
            }
            
            // Check file size
            if (FileUploadUtils.getFileSizeInMB(filePart) > 5) {
                request.setAttribute("error", "图片大小不能超过5MB");
                request.getRequestDispatcher("/hall.jsp").forward(request, response);
                return;
            }
            
            // Save the image and get its path
            String basePath = getServletContext().getRealPath("/");
            String imagePath = FileUploadUtils.saveUploadedFile(filePart, basePath, "posts");
            post.setImagePath(imagePath);
        }
        
        // Save post
        boolean success = postService.addPost(post);
        if (success) {
            // Posting successful, redirect to hall page
            response.sendRedirect("hall");
        } else {
            // Posting failed
            request.setAttribute("error", "发帖失败，请稍后再试");
            request.getRequestDispatcher("/hall.jsp").forward(request, response);
        }
    }
}