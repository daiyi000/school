package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Administrator;
import service.PostService;

import java.io.IOException;

@WebServlet("/adminPostAction")
public class AdminPostActionController extends HttpServlet {
    private PostService postService;
    
    @Override
    public void init() throws ServletException {
        postService = new PostService();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 检查管理员是否登录
        HttpSession session = request.getSession();
        Administrator admin = (Administrator) session.getAttribute("admin");
        
        if (admin == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权访问");
            return;
        }
        
        // 获取操作类型和帖子ID
        String action = request.getParameter("action");
        String postIdStr = request.getParameter("postId");
        
        if (action == null || postIdStr == null) {
            response.sendRedirect("adminDashboard?section=posts&error=missing_params");
            return;
        }
        
        try {
            int postId = Integer.parseInt(postIdStr);
            boolean success = false;
            
            // 执行相应的操作
            if ("delete".equals(action)) {
                success = postService.adminDeletePost(postId);
            } else {
                response.sendRedirect("adminDashboard?section=posts&error=invalid_action");
                return;
            }
            
            // 根据操作结果重定向
            if (success) {
                response.sendRedirect("adminDashboard?section=posts&message=action_success");
            } else {
                response.sendRedirect("adminDashboard?section=posts&error=action_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect("adminDashboard?section=posts&error=invalid_post_id");
        }
    }
}