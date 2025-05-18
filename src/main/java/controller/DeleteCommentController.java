package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import service.CommentService;

import java.io.IOException;

@WebServlet("/deleteComment")
public class DeleteCommentController extends HttpServlet {
    private CommentService commentService;
    
    @Override
    public void init() throws ServletException {
        commentService = new CommentService();
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
        
        // 获取评论ID和帖子ID
        String commentIdStr = request.getParameter("commentId");
        String postIdStr = request.getParameter("postId");
        
        if (commentIdStr == null || commentIdStr.trim().isEmpty() || 
            postIdStr == null || postIdStr.trim().isEmpty()) {
            response.sendRedirect("hall");
            return;
        }
        
        try {
            int commentId = Integer.parseInt(commentIdStr);
            int postId = Integer.parseInt(postIdStr);
            
            // 删除评论
            boolean success = commentService.deleteComment(commentId, user.getId());
            
            // 无论成功与否，都重定向回帖子详情页面
            if (success) {
                response.sendRedirect("comment?postId=" + postId + "&message=deleteSuccess");
            } else {
                response.sendRedirect("comment?postId=" + postId + "&error=deleteFailed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("hall");
        }
    }
}