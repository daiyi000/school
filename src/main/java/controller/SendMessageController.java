package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.PrivateMessage;
import model.User;
import service.NotificationService;
import service.PrivateMessageService;

import java.io.IOException;
import java.util.List;

@WebServlet("/sendMessage")
public class SendMessageController extends HttpServlet {
    private PrivateMessageService privateMessageService;
    private NotificationService notificationService;
    
    @Override
    public void init() throws ServletException {
        privateMessageService = new PrivateMessageService();
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
        
        // 获取表单数据
        String receiverIdStr = request.getParameter("receiverId");
        String content = request.getParameter("content");
        
        // 简单的输入验证
        if (receiverIdStr == null || receiverIdStr.trim().isEmpty() || 
            content == null || content.trim().isEmpty()) {
            response.sendRedirect("messages?friendId=" + receiverIdStr + "&error=emptyContent");
            return;
        }
        
        try {
            int receiverId = Integer.parseInt(receiverIdStr);
            
            // 创建私信对象
            PrivateMessage message = new PrivateMessage(user.getId(), receiverId, content);
            
            // 发送私信
            boolean success = privateMessageService.sendMessage(message);
            if (success) {
                // 获取私信ID
                List<PrivateMessage> sentMessages = privateMessageService.getSentMessages(user.getId());
                int messageId = 0;
                if (!sentMessages.isEmpty()) {
                    messageId = sentMessages.get(0).getId();
                }
                
                // 添加私信通知
                notificationService.addMessageNotification(receiverId, messageId, user.getUsername());
                
                // 发送成功，重定向回对话页面
                response.sendRedirect("messages?friendId=" + receiverId + "&sent=success");
            } else {
                // 发送失败
                response.sendRedirect("messages?friendId=" + receiverId + "&error=sendFailed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("messages?error=invalidReceiverId");
        }
    }
}