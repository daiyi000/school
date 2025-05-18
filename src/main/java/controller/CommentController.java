package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Comment;
import model.Post;
import model.User;
import service.CommentService;
import service.NotificationService;
import service.PostService;
import utils.PaginationUtils;

import java.io.IOException;
import java.util.List;

@WebServlet("/comment")
public class CommentController extends HttpServlet {
    private CommentService commentService;
    private PostService postService;
    private NotificationService notificationService;
    
    @Override
    public void init() throws ServletException {
        commentService = new CommentService();
        postService = new PostService();
        notificationService = new NotificationService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 获取帖子ID
        String postIdStr = request.getParameter("postId");
        if (postIdStr == null || postIdStr.trim().isEmpty()) {
            response.sendRedirect("hall");
            return;
        }
        
        try {
            int postId = Integer.parseInt(postIdStr);
            
            // 获取帖子信息
            Post post = postService.getPostById(postId);
            if (post == null) {
                response.sendRedirect("hall");
                return;
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
            
            // 设置每页显示的评论数量
            int pageSize = 10;
            
            // 获取评论总数和分页数据
            int totalComments = commentService.getTotalCommentsByPostId(postId);
            List<Comment> comments = commentService.getCommentsByPostIdWithPagination(postId, page, pageSize);
            
            // 创建分页工具
            PaginationUtils<Comment> pagination = new PaginationUtils<>(comments, pageSize, page, totalComments);
            
            // 设置属性
            request.setAttribute("post", post);
            request.setAttribute("pagination", pagination);
            
            // 转发到帖子详情页面
            request.getRequestDispatcher("/postComment.jsp").forward(request, response);
        } catch (NumberFormatException e) {
            response.sendRedirect("hall");
        }
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
        String postIdStr = request.getParameter("postId");
        String content = request.getParameter("content");
        
        // 简单的输入验证
        if (postIdStr == null || postIdStr.trim().isEmpty() || content == null || content.trim().isEmpty()) {
            request.setAttribute("error", "评论内容不能为空");
            doGet(request, response);
            return;
        }
        
        try {
            int postId = Integer.parseInt(postIdStr);
            
            // 创建评论对象
            Comment comment = new Comment(postId, user.getId(), content);
            
            // 保存评论
            boolean success = commentService.addComment(comment);
            if (success) {
                // 获取帖子作者信息
                Post post = postService.getPostById(postId);
                if (post != null && post.getUserId() != user.getId()) {
                    // 获取评论ID
                    List<Comment> comments = commentService.getCommentsByPostId(postId);
                    int commentId = 0;
                    for (Comment c : comments) {
                        if (c.getUserId() == user.getId() && c.getContent().equals(content)) {
                            commentId = c.getId();
                            break;
                        }
                    }
                    
                    // 添加评论通知
                    notificationService.addCommentNotification(post.getUserId(), commentId, user.getUsername());
                }
                
                // 评论成功，重定向到帖子详情页面
                response.sendRedirect("comment?postId=" + postId);
            } else {
                // 评论失败
                request.setAttribute("error", "评论失败，请稍后再试");
                doGet(request, response);
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("hall");
        }
    }
}