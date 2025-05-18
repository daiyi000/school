package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Post;
import service.PostService;
import utils.PaginationUtils;

import java.io.IOException;
import java.util.List;

@WebServlet("/hall")
public class HallController extends HttpServlet {
    private PostService postService;
    
    @Override
    public void init() throws ServletException {
        postService = new PostService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        
        // 设置每页显示的帖子数量
        int pageSize = 5;
        
        // 获取帖子总数和分页数据
        int totalPosts = postService.getTotalPosts();
        List<Post> posts = postService.getPostsByPage(page, pageSize);
        
        // 创建分页工具
        PaginationUtils<Post> pagination = new PaginationUtils<>(posts, pageSize, page, totalPosts);
        
        // 设置请求属性
        request.setAttribute("pagination", pagination);
        
        // 显示大厅页面
        request.getRequestDispatcher("/hall.jsp").forward(request, response);
    }
}