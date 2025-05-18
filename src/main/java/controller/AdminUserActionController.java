package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Administrator;
import service.UserService;

import java.io.IOException;

@WebServlet("/adminUserAction")
public class AdminUserActionController extends HttpServlet {
    private UserService userService;
    
    @Override
    public void init() throws ServletException {
        userService = new UserService();
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 检查管理员是否登录
        HttpSession session = request.getSession();
        Administrator admin = (Administrator) session.getAttribute("admin");
        
        if (admin == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权访问");
            return;
        }
        
        // 获取操作类型和用户ID
        String action = request.getParameter("action");
        String userIdStr = request.getParameter("userId");
        
        if (action == null || userIdStr == null) {
            response.sendRedirect("adminDashboard?section=users&error=missing_params");
            return;
        }
        
        try {
            int userId = Integer.parseInt(userIdStr);
            boolean success = false;
            
            // 执行相应的操作
            switch (action) {
                case "delete":
                    success = userService.deleteUser(userId);
                    break;
                case "disable":
                    success = userService.disableUser(userId);
                    break;
                case "enable":
                    success = userService.enableUser(userId);
                    break;
                default:
                    response.sendRedirect("adminDashboard?section=users&error=invalid_action");
                    return;
            }
            
            // 根据操作结果重定向
            if (success) {
                response.sendRedirect("adminDashboard?section=users&message=action_success");
            } else {
                response.sendRedirect("adminDashboard?section=users&error=action_failed");
            }
            
        } catch (NumberFormatException e) {
            response.sendRedirect("adminDashboard?section=users&error=invalid_user_id");
        }
    }
}