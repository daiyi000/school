package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.User;
import service.FriendshipService;
import service.NotificationService;

import java.io.IOException;

@WebServlet("/deleteFriend")
public class DeleteFriendController extends HttpServlet {
    private FriendshipService friendshipService;
    private NotificationService notificationService;
    
    @Override
    public void init() throws ServletException {
        friendshipService = new FriendshipService();
        notificationService = new NotificationService();
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
        
        // 获取好友ID和其他参数
        String friendIdStr = request.getParameter("friendId");
        String friendUsername = request.getParameter("friendUsername");
        String tab = request.getParameter("tab");
        String friendsPage = request.getParameter("friendsPage");
        String requestsPage = request.getParameter("requestsPage");
        
        // 构建重定向URL
        StringBuilder redirectUrl = new StringBuilder("friends?");
        if (tab != null && !tab.trim().isEmpty()) {
            redirectUrl.append("tab=").append(tab).append("&");
        }
        if (friendsPage != null && !friendsPage.trim().isEmpty()) {
            redirectUrl.append("friendsPage=").append(friendsPage).append("&");
        }
        if (requestsPage != null && !requestsPage.trim().isEmpty()) {
            redirectUrl.append("requestsPage=").append(requestsPage).append("&");
        }
        
        if (friendIdStr == null || friendIdStr.trim().isEmpty()) {
            response.sendRedirect(redirectUrl.toString() + "error=invalidId");
            return;
        }
        
        try {
            int friendId = Integer.parseInt(friendIdStr);
            
            // 删除好友关系
            boolean success = friendshipService.deleteFriendship(user.getId(), friendId);
            
            if (success) {
                // 添加系统通知
                notificationService.addSystemNotification(
                    friendId, 
                    user.getUsername() + " 已将您从好友列表中移除。"
                );
                
                response.sendRedirect(redirectUrl.toString() + "message=deleteSuccess");
            } else {
                response.sendRedirect(redirectUrl.toString() + "error=deleteFailed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect(redirectUrl.toString() + "error=invalidId");
        }
    }
}