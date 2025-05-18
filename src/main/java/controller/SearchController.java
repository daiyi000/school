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
import service.UserService;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/search")
public class SearchController extends HttpServlet {
    private UserService userService;
    private PostService postService;
    
    @Override
    public void init() throws ServletException {
        userService = new UserService();
        postService = new PostService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 检查用户是否登录
        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("user");
        
        if (currentUser == null) {
            // 未登录，重定向到登录页面
            response.sendRedirect("login");
            return;
        }
        
        String keyword = request.getParameter("keyword");
        String type = request.getParameter("type"); // "user" 或 "post"
        
        if (keyword != null && !keyword.trim().isEmpty()) {
            // 设置搜索关键词
            request.setAttribute("keyword", keyword);
            
            // 根据搜索类型执行相应的搜索
            if ("user".equals(type)) {
                List<User> users = userService.searchUsersByUsername(keyword);
                request.setAttribute("users", users != null ? users : new ArrayList<>());
                request.setAttribute("searchType", "user");
            } else if ("post".equals(type)) {
                List<Post> posts = postService.searchPostsByContent(keyword);
                request.setAttribute("posts", posts != null ? posts : new ArrayList<>());
                request.setAttribute("searchType", "post");
            } else {
                // 默认同时搜索用户和帖子
                List<User> users = userService.searchUsersByUsername(keyword);
                List<Post> posts = postService.searchPostsByContent(keyword);
                request.setAttribute("users", users != null ? users : new ArrayList<>());
                request.setAttribute("posts", posts != null ? posts : new ArrayList<>());
                request.setAttribute("searchType", "all");
            }
        }
        
        // 显示搜索页面
        request.getRequestDispatcher("/search.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 处理搜索表单提交
        String keyword = request.getParameter("keyword");
        String type = request.getParameter("type");
        
        // 重定向到GET方法处理
        // 关键修改：URL编码参数值，避免中文字符编码问题
        String encodedKeyword = "";
        if (keyword != null) {
            encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        }
        
        String typeParam = (type != null) ? "&type=" + type : "";
        response.sendRedirect("search?keyword=" + encodedKeyword + typeParam);
    }
}