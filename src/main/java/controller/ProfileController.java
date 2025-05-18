package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import service.UserService;

import java.io.IOException;

@WebServlet("/profile")
public class ProfileController extends HttpServlet {
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 检查用户是否登录
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            // 未登录，重定向到登录页面
            response.sendRedirect("login");
            return;
        }
        
        // 显示个人中心页面
        request.getRequestDispatcher("/profile.jsp").forward(request, response);
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
        String email = request.getParameter("email");
        String bio = request.getParameter("bio");
        
        // 简单的输入验证
        if (email == null || email.trim().isEmpty()) {
            request.setAttribute("error", "邮箱不能为空");
            request.getRequestDispatcher("/profile.jsp").forward(request, response);
            return;
        }
        
        // 更新用户信息
        user.setEmail(email);
        user.setBio(bio);
        
        boolean success = userService.updateProfile(user);
        if (success) {
            // 更新成功，更新session中的用户信息
            session.setAttribute("user", user);
            request.setAttribute("message", "个人信息更新成功");
        } else {
            // 更新失败
            request.setAttribute("error", "个人信息更新失败");
        }
        
        request.getRequestDispatcher("/profile.jsp").forward(request, response);
    }
}