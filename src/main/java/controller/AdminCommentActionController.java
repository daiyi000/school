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
        // 设置响应编码
        response.setCharacterEncoding("UTF-8");
        
        // 检查管理员是否登录
        HttpSession session = request.getSession();
        Administrator admin = (Administrator) session.getAttribute("admin");
        
        if (admin == null) {
            if ("true".equals(request.getParameter("isAjax"))) {
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.print("{\"success\":false,\"message\":\"未授权访问\"}");
                out.flush();
            } else {
                response.sendRedirect("adminLogin");
            }
            return;
        }
        
        // 打印请求参数，调试用
        System.out.println("请求参数: ");
        System.out.println("action=" + request.getParameter("action"));
        System.out.println("commentId=" + request.getParameter("commentId"));
        System.out.println("isAjax=" + request.getParameter("isAjax"));
        
        // 获取操作类型和评论ID
        String action = request.getParameter("action");
        String commentIdStr = request.getParameter("commentId");
        String isAjaxStr = request.getParameter("isAjax");
        boolean isAjax = "true".equals(isAjaxStr);
        
        if (action == null || commentIdStr == null) {
            if (isAjax) {
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.print("{\"success\":false,\"message\":\"参数缺失\"}");
                out.flush();
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
                System.out.println("删除评论结果: " + success); // 调试输出
            } else {
                if (isAjax) {
                    response.setContentType("application/json;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.print("{\"success\":false,\"message\":\"无效的操作类型\"}");
                    out.flush();
                } else {
                    response.sendRedirect("adminDashboard?section=posts&error=invalid_action");
                }
                return;
            }
            
            // 根据操作结果返回
            if (success) {
                if (isAjax) {
                    response.setContentType("application/json;charset=UTF-8");
                    PrintWriter out = response.getWriter();
                    out.print("{\"success\":true,\"message\":\"评论已成功删除\"}");
                    out.flush();
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
                    PrintWriter out = response.getWriter();
                    out.print("{\"success\":false,\"message\":\"删除失败，请稍后再试\"}");
                    out.flush();
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
            System.out.println("解析评论ID时出错: " + e.getMessage()); // 调试输出
            if (isAjax) {
                response.setContentType("application/json;charset=UTF-8");
                PrintWriter out = response.getWriter();
                out.print("{\"success\":false,\"message\":\"无效的评论ID\"}");
                out.flush();
            } else {
                response.sendRedirect("adminDashboard?section=posts&error=invalid_comment_id");
            }
        }
    }
}