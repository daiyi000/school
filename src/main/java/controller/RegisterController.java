package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.User;
import service.UserService;

import java.io.IOException;

@WebServlet("/register")
public class RegisterController extends HttpServlet {
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 显示注册页面
        request.getRequestDispatcher("/register.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String confirmPassword = request.getParameter("confirmPassword");
        String email = request.getParameter("email");
        
        // 简单的输入验证
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty() ||
            confirmPassword == null || confirmPassword.trim().isEmpty() ||
            email == null || email.trim().isEmpty()) {
            
            request.setAttribute("error", "所有字段都必须填写");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        // 检查密码是否匹配
        if (!password.equals(confirmPassword)) {
            request.setAttribute("error", "两次输入的密码不匹配");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
            return;
        }
        
        // 创建用户对象
        User user = new User(username, password, email);
        
        // 注册用户
        boolean success = userService.register(user);
        if (success) {
            // 注册成功，重定向到登录页面
            request.setAttribute("message", "注册成功，请登录");
            request.getRequestDispatcher("/login.jsp").forward(request, response);
        } else {
            // 注册失败
            request.setAttribute("error", "用户名已存在，请选择其他用户名");
            request.getRequestDispatcher("/register.jsp").forward(request, response);
        }
    }
}