package service;

import dao.NotificationDAO;
import model.Notification;
import utils.PaginationUtils;

import java.util.List;

public class NotificationService {
    private NotificationDAO notificationDAO;
    
    public NotificationService() {
        this.notificationDAO = new NotificationDAO();
    }
    
    // 添加系统通知
    public boolean addSystemNotification(int userId, String content) {
        Notification notification = new Notification(userId, "system", content, 0);
        return notificationDAO.addNotification(notification);
    }
    
    // 添加好友请求通知
    public boolean addFriendRequestNotification(int userId, int requestId, String username) {
        String content = username + " 发送了好友请求";
        Notification notification = new Notification(userId, "friend_request", content, requestId);
        return notificationDAO.addNotification(notification);
    }
    
    // 添加私信通知
    public boolean addMessageNotification(int userId, int messageId, String senderUsername) {
        String content = "收到来自 " + senderUsername + " 的新私信";
        Notification notification = new Notification(userId, "message", content, messageId);
        return notificationDAO.addNotification(notification);
    }
    
    // 添加评论通知
    public boolean addCommentNotification(int userId, int commentId, String commenterUsername) {
        String content = commenterUsername + " 评论了你的帖子";
        Notification notification = new Notification(userId, "comment", content, commentId);
        return notificationDAO.addNotification(notification);
    }
    
    // 获取用户的所有通知
    public List<Notification> getNotificationsByUserId(int userId) {
        return notificationDAO.getNotificationsByUserId(userId);
    }
    
 // 获取用户的通知（带分页）
    public PaginationUtils<Notification> getNotificationsByUserIdWithPagination(int userId, int page, int pageSize) {
        int totalItems = notificationDAO.getNotificationCountByUserId(userId);
        int offset = (page - 1) * pageSize;
        List<Notification> notifications = notificationDAO.getNotificationsByUserId(userId, offset, pageSize);
        
        // 创建分页工具实例，使用新的构造函数
        return new PaginationUtils<>(notifications, pageSize, page, totalItems);
    }
    
    // 将通知标记为已读
    public boolean markNotificationAsRead(int notificationId, int userId) {
        return notificationDAO.markNotificationAsRead(notificationId, userId);
    }
    
    // 将所有通知标记为已读
    public boolean markAllNotificationsAsRead(int userId) {
        return notificationDAO.markAllNotificationsAsRead(userId);
    }
    
    // 获取未读通知数量
    public int getUnreadNotificationCount(int userId) {
        return notificationDAO.getUnreadNotificationCount(userId);
    }
}