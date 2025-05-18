package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Notification;
import model.User;
import service.NotificationService;
import utils.PaginationUtils;

import java.io.IOException;

@WebServlet("/notifications")
public class NotificationController extends HttpServlet {
    private NotificationService notificationService;
    
    @Override
    public void init() throws ServletException {
        notificationService = new NotificationService();
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
        
        // 从请求参数中获取当前页码，默认为第1页
        int page = 1;
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                // 忽略错误，使用默认页码
            }
        }
        
        // 设置每页显示的通知数量
        int pageSize = 10;
        
        // 获取带分页的通知列表
        PaginationUtils<Notification> pagination = notificationService.getNotificationsByUserIdWithPagination(user.getId(), page, pageSize);
        
        // 设置请求属性
        request.setAttribute("pagination", pagination);
        
        // 显示通知页面
        request.getRequestDispatcher("/notification.jsp").forward(request, response);
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
        
        // 获取操作类型
        String action = request.getParameter("action");
        if (action == null) {
            doGet(request, response);
            return;
        }
        
        boolean success = false;
        
        switch (action) {
            case "markAsRead":
                String notificationIdStr = request.getParameter("notificationId");
                if (notificationIdStr != null && !notificationIdStr.trim().isEmpty()) {
                    try {
                        int notificationId = Integer.parseInt(notificationIdStr);
                        success = notificationService.markNotificationAsRead(notificationId, user.getId());
                    } catch (NumberFormatException e) {
                        // 忽略错误
                    }
                }
                break;
                
            case "markAllAsRead":
                success = notificationService.markAllNotificationsAsRead(user.getId());
                break;
                
            default:
                break;
        }
        
        // 根据操作结果设置消息
        if (success) {
            request.setAttribute("message", "操作成功");
        } else {
            request.setAttribute("error", "操作失败");
        }
        
        // 重新加载通知页面
        doGet(request, response);
    }
}