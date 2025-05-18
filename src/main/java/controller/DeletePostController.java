package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import service.PostService;

import java.io.IOException;

@WebServlet("/deletePost")
public class DeletePostController extends HttpServlet {
    private PostService postService;
    
    @Override
    public void init() throws ServletException {
        postService = new PostService();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 检查用户是否登录
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            // 未登录，重定向到登录页面
            response.sendRedirect("login");
            return;
        }
        
        // 获取帖子ID
        String postIdStr = request.getParameter("postId");
        if (postIdStr == null || postIdStr.trim().isEmpty()) {
            response.sendRedirect("hall");
            return;
        }
        
        try {
            int postId = Integer.parseInt(postIdStr);
            
            // 删除帖子
            boolean success = postService.deletePost(postId, user.getId());
            
            // 无论成功与否，都重定向回大厅页面
            // 可以通过URL参数传递结果消息
            if (success) {
                response.sendRedirect("hall?message=deleteSuccess");
            } else {
                response.sendRedirect("hall?error=deleteFailed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("hall");
        }
    }
}