<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>个人中心</title>
    <link rel="stylesheet" href="css/profile.css">
</head>
<body>
<%@ include file="header.jsp" %>

<div class="main-container">
    <% 
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }
    %>
    
    <h1>个人中心</h1>
    
    <div class="profile-form-container">
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
        
        <form action="profile" method="post">
            <div class="form-group">
                <label for="username">用户名:</label>
                <input type="text" id="username" value="<%= user.getUsername() %>" disabled>
            </div>
            <div class="form-group">
                <label for="email">邮箱:</label>
                <input type="email" id="email" name="email" value="<%= user.getEmail() != null ? user.getEmail() : "" %>" required>
            </div>
            <div class="form-group">
                <label for="bio">个人简介:</label>
                <textarea id="bio" name="bio" rows="4" cols="50"><%= user.getBio() != null ? user.getBio() : "" %></textarea>
            </div>
            <div class="form-group">
                <label for="registerTime">注册时间:</label>
                <input type="text" id="registerTime" value="<%= user.getRegisterTime() != null ? user.getRegisterTime() : "" %>" disabled>
            </div>
            <div class="form-group">
                <button type="submit">更新信息</button>
            </div>
        </form>
    </div>
    
    <p class="back-link"><a href="hall">返回首页</a></p>
</div>

<%@ include file="footer.jsp" %>
</body>
</html>