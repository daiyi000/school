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

@WebServlet("/adminChangePassword")
public class AdminPasswordController extends HttpServlet {
    private AdministratorService adminService;
    
    @Override
    public void init() throws ServletException {
        adminService = new AdministratorService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 检查管理员是否登录
        HttpSession session = request.getSession();
        Administrator admin = (Administrator) session.getAttribute("admin");
        
        if (admin == null) {
            response.sendRedirect("adminLogin");
            return;
        }
        
        request.getRequestDispatcher("/adminChangePassword.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 检查管理员是否登录
        HttpSession session = request.getSession();
        Administrator admin = (Administrator) session.getAttribute("admin");
        
        if (admin == null) {
            response.sendRedirect("adminLogin");
            return;
        }
        
        // 获取表单数据
        String oldPassword = request.getParameter("oldPassword");
        String newPassword = request.getParameter("newPassword");
        String confirmPassword = request.getParameter("confirmPassword");
        
        // 简单的验证
        if (oldPassword == null || newPassword == null || confirmPassword == null ||
            oldPassword.trim().isEmpty() || newPassword.trim().isEmpty() || confirmPassword.trim().isEmpty()) {
            request.setAttribute("error", "所有字段都不能为空");
            request.getRequestDispatcher("/adminChangePassword.jsp").forward(request, response);
            return;
        }
        
        if (!newPassword.equals(confirmPassword)) {
            request.setAttribute("error", "两次输入的新密码不一致");
            request.getRequestDispatcher("/adminChangePassword.jsp").forward(request, response);
            return;
        }
        
        // 修改密码
        boolean success = adminService.updatePassword(admin.getId(), oldPassword, newPassword);
        
        if (success) {
            // 密码修改成功，更新session中的管理员信息
            admin.setPassword(newPassword);
            session.setAttribute("admin", admin);
            
            request.setAttribute("message", "密码修改成功");
        } else {
            request.setAttribute("error", "原密码不正确");
        }
        
        request.getRequestDispatcher("/adminChangePassword.jsp").forward(request, response);
    }
}