package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Administrator;
import service.CommentService;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/adminCommentAction")
public class AdminCommentActionController extends HttpServlet {
    private CommentService commentService;
    
    @Override
    public void init() throws ServletException {
        commentService = new CommentService();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 检查管理员是否登录
        HttpSession session = request.getSession();
        Administrator admin = (Administrator) session.getAttribute("admin");
        
        if (admin == null) {
            if ("true".equals(request.getParameter("isAjax"))) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"未授权访问\"}");
            } else {
                response.sendRedirect("adminLogin");
            }
            return;
        }
        
        // 获取操作类型和评论ID
        String action = request.getParameter("action");
        String commentIdStr = request.getParameter("commentId");
        String isAjaxStr = request.getParameter("isAjax");
        boolean isAjax = "true".equals(isAjaxStr);
        
        if (action == null || commentIdStr == null) {
            if (isAjax) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"参数缺失\"}");
            } else {
                response.sendRedirect("adminDashboard?section=posts&error=missing_params");
            }
            return;
        }
        
        try {
            int commentId = Integer.parseInt(commentIdStr);
            boolean success = false;
            
            // 执行相应的操作
            if ("delete".equals(action)) {
                success = commentService.adminDeleteComment(commentId);
            } else {
                if (isAjax) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"success\":false,\"message\":\"无效的操作类型\"}");
                } else {
                    response.sendRedirect("adminDashboard?section=posts&error=invalid_action");
                }
                return;
            }
            
            // 根据操作结果返回
            if (success) {
                if (isAjax) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"success\":true,\"message\":\"评论已成功删除\"}");
                } else {
                    String postIdStr = request.getParameter("postId");
                    if (postIdStr != null && !postIdStr.isEmpty()) {
                        response.sendRedirect("adminPostDetail?postId=" + postIdStr + "&message=comment_deleted");
                    } else {
                        response.sendRedirect("adminDashboard?section=posts&message=comment_deleted");
                    }
                }
            } else {
                if (isAjax) {
                    response.setContentType("application/json;charset=UTF-8");
                    response.getWriter().write("{\"success\":false,\"message\":\"删除失败，请稍后再试\"}");
                } else {
                    String postIdStr = request.getParameter("postId");
                    if (postIdStr != null && !postIdStr.isEmpty()) {
                        response.sendRedirect("adminPostDetail?postId=" + postIdStr + "&error=delete_failed");
                    } else {
                        response.sendRedirect("adminDashboard?section=posts&error=delete_failed");
                    }
                }
            }
            
        } catch (NumberFormatException e) {
            if (isAjax) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"success\":false,\"message\":\"无效的评论ID\"}");
            } else {
                response.sendRedirect("adminDashboard?section=posts&error=invalid_comment_id");
            }
        }
    }
}