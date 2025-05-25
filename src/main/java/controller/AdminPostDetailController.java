package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Administrator;
import model.Comment;
import model.Post;
import service.CommentService;
import service.PostService;
import utils.HtmlUtils;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/adminPostDetail")
public class AdminPostDetailController extends HttpServlet {
    private PostService postService;
    private CommentService commentService;
    
    @Override
    public void init() throws ServletException {
        postService = new PostService();
        commentService = new CommentService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 检查管理员是否登录
        HttpSession session = request.getSession();
        Administrator admin = (Administrator) session.getAttribute("admin");
        
        if (admin == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权访问");
            return;
        }
        
        // 获取帖子ID
        String postIdStr = request.getParameter("postId");
        
        if (postIdStr == null || postIdStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "帖子ID不能为空");
            return;
        }
        
        try {
            int postId = Integer.parseInt(postIdStr);
            Post post = postService.getPostById(postId);
            
            if (post == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "帖子不存在");
                return;
            }
            
            // 获取该帖子的所有评论
            List<Comment> comments = commentService.getCommentsByPostId(postId);
            
            // 设置响应类型
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            // 格式化日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            // 输出帖子详情HTML
            out.println("<div class='post-details'>");
            
            // 显示成功或错误消息
            String message = request.getParameter("message");
            String error = request.getParameter("error");
            
            if (message != null) {
                out.println("<div id='success-message' class='message success' style='padding: 10px; background-color: #d4edda; color: #155724; border-radius: 4px; margin-bottom: 15px;'>");
                if ("comment_deleted".equals(message)) {
                    out.println("评论已成功删除");
                } else {
                    out.println(message);
                }
                out.println("</div>");
            }
            
            if (error != null) {
                out.println("<div id='error-message' class='message error' style='padding: 10px; background-color: #f8d7da; color: #721c24; border-radius: 4px; margin-bottom: 15px;'>");
                if ("delete_failed".equals(error)) {
                    out.println("删除评论失败");
                } else {
                    out.println(error);
                }
                out.println("</div>");
            }
            
            out.println("<p><strong>ID:</strong> " + post.getId() + "</p>");
            out.println("<p><strong>作者:</strong> " + post.getUsername() + " (ID: " + post.getUserId() + ")</p>");
            out.println("<p><strong>发布时间:</strong> " + sdf.format(post.getCreateTime()) + "</p>");
            out.println("<p><strong>内容:</strong></p>");
            out.println("<div class='post-content' style='margin: 10px 0; padding: 10px; background-color: #f8f9fa; border-radius: 4px;'>");
            out.println(HtmlUtils.nl2br(post.getContent()));
            out.println("</div>");
            
            // 显示图片（如果有）
            if (post.getImagePath() != null && !post.getImagePath().isEmpty()) {
                out.println("<p><strong>图片:</strong></p>");
                out.println("<div class='post-image' style='margin: 10px 0;'>");
                out.println("<img src='" + post.getImagePath() + "' alt='帖子图片' style='max-width: 100%; max-height: 300px;'>");
                out.println("</div>");
            }
            
            // 帖子删除按钮
            out.println("<div style='margin: 20px 0;'>");
            out.println("<button onclick='confirmPostAction(" + post.getId() + ", \"delete\")' style='padding: 8px 16px; background-color: #e74c3c; color: white; border: none; border-radius: 4px; cursor: pointer;'>删除帖子</button>");
            out.println("</div>");
            
            // 显示评论区域
            out.println("<h3 style='margin-top: 30px; padding-bottom: 10px; border-bottom: 1px solid #eee;'>评论列表</h3>");
            
            // 显示通知消息区域
            out.println("<div id='notification-area' style='display: none; margin: 10px 0; padding: 10px; border-radius: 4px;'></div>");
            
            out.println("<div id='comments-container'>"); // 添加一个包裹评论的容器

            if (comments != null && !comments.isEmpty()) {
                for (Comment comment : comments) {
                    out.println("<div class='comment' id='comment-" + comment.getId() + "' style='margin: 15px 0; padding: 10px; background-color: #f8f9fa; border-radius: 4px; border-left: 3px solid #3498db;'>");
                    out.println("<div style='display: flex; justify-content: space-between; margin-bottom: 5px;'>");
                    out.println("<span><strong>" + comment.getUsername() + "</strong> (ID: " + comment.getUserId() + ") " + sdf.format(comment.getCreateTime()) + "</span>");
                    
                    // 使用全局删除函数删除评论
                    out.println("<a href='javascript:void(0)' onclick='deleteCommentWithoutRedirect(" + comment.getId() + ", " + post.getId() + ")' style='color: #e74c3c; cursor: pointer;'>删除</a>");
                    
                    out.println("</div>");
                    out.println("<div style='margin-top: 5px;'>" + HtmlUtils.nl2br(comment.getContent()) + "</div>");
                    out.println("</div>");
                }
            } else {
                out.println("<p id='no-comments-message' style='text-align: center; color: #6c757d; font-style: italic; margin: 20px 0;'>暂无评论</p>");
            }

            out.println("</div>"); // 关闭评论容器
            
            out.println("</div>"); // 关闭post-details容器
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的帖子ID");
        }
    }
}