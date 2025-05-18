<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<%@ page import="model.Notification" %>
<%@ page import="utils.PaginationUtils" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>通知</title>
    <link rel="stylesheet" href="css/notification.css">
</head>
<body>
<%@ include file="header.jsp" %>

<div class="main-container">
    <h1>通知中心</h1>
    
    <% 
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }
    %>
    
    <% if(request.getAttribute("error") != null) { %>
        <div class="error-message">
            <%= request.getAttribute("error") %>
        </div>
    <% } %>
    
    <% if(request.getAttribute("message") != null) { %>
        <div class="success-message">
            <%= request.getAttribute("message") %>
        </div>
    <% } %>
    
    <!-- 标记所有已读按钮 -->
    <div class="mark-all-read">
        <form action="notifications" method="post">
            <input type="hidden" name="action" value="markAllAsRead">
            <button type="submit" class="mark-all-button">全部标记为已读</button>
        </form>
    </div>
    
    <!-- 通知列表 -->
    <div class="notifications-container">
        <% 
            PaginationUtils<Notification> pagination = (PaginationUtils<Notification>) request.getAttribute("pagination");
            if (pagination != null) {
                List<Notification> notifications = pagination.getItems();
                if (notifications != null && !notifications.isEmpty()) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    for (Notification notification : notifications) {
                        String typeIcon = "";
                        String linkUrl = "";
                        
                        // 根据通知类型设置图标
                        switch (notification.getType()) {
                            case "system":
                                typeIcon = "📢";
                                break;
                            case "friend_request":
                                typeIcon = "👤";
                                linkUrl = "friends";
                                break;
                            case "message":
                                typeIcon = "✉️";
                                linkUrl = "messages?friendId=" + notification.getRelatedId();
                                break;
                            case "comment":
                                typeIcon = "💬";
                                linkUrl = "comment?postId=" + notification.getRelatedId();
                                break;
                            default:
                                typeIcon = "📌";
                                break;
                        }
        %>
                    <div class="notification-item <%= notification.getIsRead() == 0 ? "unread" : "" %>">
                        <div class="notification-header">
                            <div class="notification-type">
                                <span class="notification-type-icon"><%= typeIcon %></span>
                                <strong>
                                    <%= notification.getType().equals("system") ? "系统通知" : 
                                       notification.getType().equals("friend_request") ? "好友请求" :
                                       notification.getType().equals("message") ? "新私信" :
                                       notification.getType().equals("comment") ? "新评论" : "通知" %>
                                </strong>
                            </div>
                            <div class="notification-timestamp">
                                <%= sdf.format(notification.getCreateTime()) %>
                            </div>
                        </div>
                        <div class="notification-content">
                            <%= notification.getContent() %>
                        </div>
                        <div class="notification-actions">
                            <% if (!linkUrl.isEmpty()) { %>
                                <a href="<%= linkUrl %>" class="notification-action-link">查看详情</a>
                            <% } %>
                            <% if (notification.getIsRead() == 0) { %>
                                <form action="notifications" method="post" style="display: inline;">
                                    <input type="hidden" name="action" value="markAsRead">
                                    <input type="hidden" name="notificationId" value="<%= notification.getId() %>">
                                    <button type="submit" class="notification-action-button">标记为已读</button>
                                </form>
                            <% } %>
                        </div>
                    </div>
        <%
                    }
                    
                    // 输出分页导航
                    out.println("<div class=\"pagination\">" + pagination.generatePaginationHTML("notifications") + "</div>");
                    
                } else {
        %>
                    <div class="empty-message">
                        <p>暂无通知</p>
                    </div>
        <%
                }
            } else {
        %>
                <div class="empty-message">
                    <p>暂无通知</p>
                </div>
        <%
            }
        %>
    </div>
</div>

<%@ include file="footer.jsp" %>
</body>
</html>