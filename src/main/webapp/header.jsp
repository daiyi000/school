<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<%@ page import="service.NotificationService" %>
<%@ page import="java.util.*" %>

<link rel="stylesheet" type="text/css" href="css/head.css">

<%
    User currentUser = (User) session.getAttribute("user");
    if (currentUser != null) {
        // 获取未读通知数量
        NotificationService notificationService = new NotificationService();
        int unreadNotifications = notificationService.getUnreadNotificationCount(currentUser.getId());
%>
    <div class="header-container">
        <div class="logo">
            <h2>社交系统</h2>
        </div>
        <div class="search-notification-bar">
            <form action="search" method="get" class="search-form">
                <input type="text" name="keyword" placeholder="搜索...">
            </form>
            <div class="nav-links">
                <a href="hall">首页</a>
                <a href="profile">个人中心</a>
                <a href="friends">好友管理</a>
                <a href="messages">私信</a>
                <a href="notifications">通知
                    <% if (unreadNotifications > 0) { %>
                        <span class="notification-badge"><%= unreadNotifications %></span>
                    <% } %>
                </a>
                <a href="logout">退出登录</a>
            </div>
        </div>
    </div>
<% } else { %>
    <div class="header-container">
        <div class="logo">
            <h2>社交系统</h2>
        </div>
        <div class="search-notification-bar">
            <div class="nav-links">
                <a href="hall">首页</a>
                <a href="login">登录</a>
                <a href="register">注册</a>
            </div>
        </div>
    </div>
<% } %>