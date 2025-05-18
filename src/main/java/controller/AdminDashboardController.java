package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Administrator;
import model.Post;
import model.User;
import service.PostService;
import service.UserService;
import utils.PaginationUtils;

import java.io.IOException;
import java.util.List;

@WebServlet("/adminDashboard")
public class AdminDashboardController extends HttpServlet {
    private UserService userService;
    private PostService postService;
    
    @Override
    public void init() throws ServletException {
        userService = new UserService();
        postService = new PostService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 检查管理员是否登录
        HttpSession session = request.getSession();
        Administrator admin = (Administrator) session.getAttribute("admin");
        
        if (admin == null) {
            // 未登录，重定向到管理员登录页面
            response.sendRedirect("adminLogin");
            return;
        }
        
        // 获取当前页面
        String section = request.getParameter("section");
        if (section == null) {
            section = "users"; // 默认显示用户管理
        }
        
        // 从请求参数中获取当前页码，默认为第1页
        int page = 1;
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                // 忽略错误，使用默认页码
            }
        }
        
        // 设置每页显示的条目数量
        int pageSize = 10;
        
        // 根据选择的部分加载相应的数据
        if ("users".equals(section)) {
            // 获取用户列表
            List<User> users = userService.getAllUsersWithPagination(page, pageSize);
            int totalUsers = userService.getTotalUsers();
            PaginationUtils<User> pagination = new PaginationUtils<>(users, pageSize, page, totalUsers);
            request.setAttribute("pagination", pagination);
        } else if ("posts".equals(section)) {
            // 获取帖子列表
            List<Post> posts = postService.getPostsByPage(page, pageSize);
            int totalPosts = postService.getTotalPosts();
            PaginationUtils<Post> pagination = new PaginationUtils<>(posts, pageSize, page, totalPosts);
            request.setAttribute("pagination", pagination);
        }
        
        request.setAttribute("section", section);
        request.getRequestDispatcher("/adminDashboard.jsp").forward(request, response);
    }
}