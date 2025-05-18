package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Post;
import model.User;
import service.PostService;

import java.io.IOException;

@WebServlet("/post")
public class PostController extends HttpServlet {
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
        
        // 获取表单数据
        String content = request.getParameter("content");
        
        // 简单的输入验证
        if (content == null || content.trim().isEmpty()) {
            request.setAttribute("error", "帖子内容不能为空");
            request.getRequestDispatcher("/hall.jsp").forward(request, response);
            return;
        }
        
        // 创建帖子对象
        Post post = new Post(user.getId(), content);
        
        // 保存帖子
        boolean success = postService.addPost(post);
        if (success) {
            // 发帖成功，重定向到大厅页面
            response.sendRedirect("hall");
        } else {
            // 发帖失败
            request.setAttribute("error", "发帖失败，请稍后再试");
            request.getRequestDispatcher("/hall.jsp").forward(request, response);
        }
    }
}