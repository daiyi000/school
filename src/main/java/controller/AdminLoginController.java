package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Administrator;
import service.AdministratorService;

import java.io.IOException;

@WebServlet("/adminLogin")
public class AdminLoginController extends HttpServlet {
    private AdministratorService adminService;
    
    @Override
    public void init() throws ServletException {
        adminService = new AdministratorService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 如果已经登录，则重定向到管理员控制台
        HttpSession session = request.getSession();
        if (session.getAttribute("admin") != null) {
            response.sendRedirect("adminDashboard");
            return;
        }
        
        // 显示管理员登录页面
        request.getRequestDispatcher("/adminLogin.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        // 简单的输入验证
        if (username == null || username.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "用户名和密码不能为空");
            request.getRequestDispatcher("/adminLogin.jsp").forward(request, response);
            return;
        }
        
        // 验证管理员
        Administrator admin = adminService.login(username, password);
        if (admin != null) {
            // 登录成功，将管理员信息存入session
            HttpSession session = request.getSession();
            session.setAttribute("admin", admin);
            
            // 重定向到管理员控制台
            response.sendRedirect("adminDashboard");
        } else {
            // 登录失败
            request.setAttribute("error", "用户名或密码错误");
            request.getRequestDispatcher("/adminLogin.jsp").forward(request, response);
        }
    }
}