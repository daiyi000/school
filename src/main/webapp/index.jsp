<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>社交系统首页</title>
</head>
<body>
<%@ include file="header.jsp" %>
    <h1>社交系统首页</h1>
    
    <% 
        User user = (User) session.getAttribute("user");
        if (user != null) {
    %>
        <p>欢迎，<%= user.getUsername() %>！</p>
        <p><a href="profile">个人中心</a> | <a href="hall">进入大厅</a> | <a href="logout">退出登录</a></p>
    <% } else { %>
        <p><a href="login">登录</a> | <a href="register">注册</a></p>
    <% } %>
    
    <div>
        <p>点击"进入大厅"查看所有帖子</p>
    </div>
    
    <script>
        // 自动跳转到大厅页面
        window.location.href = "hall";
    </script>
    <%@ include file="footer.jsp" %>
</body>
</html>