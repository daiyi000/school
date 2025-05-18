package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Administrator;
import model.User;
import service.UserService;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/adminUserDetail")
public class AdminUserDetailController extends HttpServlet {
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        userService = new UserService();
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
        
        // 获取用户ID
        String userIdStr = request.getParameter("userId");
        
        if (userIdStr == null || userIdStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "用户ID不能为空");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            User user = userService.getUserById(userId);
            
            if (user == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "用户不存在");
                return;
            }
            
            // 设置响应类型
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            // 输出用户详情HTML
            out.println("<div class='user-details'>");
            out.println("<p><strong>ID:</strong> " + user.getId() + "</p>");
            out.println("<p><strong>用户名:</strong> " + user.getUsername() + "</p>");
            out.println("<p><strong>邮箱:</strong> " + (user.getEmail() != null ? user.getEmail() : "未设置") + "</p>");
            out.println("<p><strong>个人简介:</strong> " + (user.getBio() != null ? user.getBio() : "未设置") + "</p>");
            out.println("<p><strong>注册时间:</strong> " + user.getRegisterTime() + "</p>");
            out.println("</div>");
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的用户ID");
        }
    }
}