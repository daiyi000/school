<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<%@ page import="model.Post" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>搜索</title>
    <link rel="stylesheet" href="css/search.css">
</head>
<body>
<%@ include file="header.jsp" %>

<div class="main-container">
    <h1>搜索</h1>
    
    <% 
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }
    %>
    
    <!-- 搜索表单 -->
    <div class="search-form-container">
        <form action="search" method="post" class="search-form">
            <input type="text" name="keyword" placeholder="输入关键词..." value="<%= request.getAttribute("keyword") != null ? request.getAttribute("keyword") : "" %>" class="search-input" required>
            <select name="type" class="search-select">
                <option value="all" <%= "all".equals(request.getAttribute("searchType")) ? "selected" : "" %>>全部</option>
                <option value="user" <%= "user".equals(request.getAttribute("searchType")) ? "selected" : "" %>>用户</option>
                <option value="post" <%= "post".equals(request.getAttribute("searchType")) ? "selected" : "" %>>帖子</option>
            </select>
            <button type="submit" class="search-button">搜索</button>
        </form>
    </div>
    
    <!-- 搜索结果 -->
    <% 
        String keyword = (String) request.getAttribute("keyword");
        String searchType = (String) request.getAttribute("searchType");
        
        // 确保默认值，防止空白页面
        if (searchType == null) {
            searchType = "all";
        }
        
        if (keyword != null && !keyword.isEmpty()) {
    %>
        <div class="search-results-container">
            <h2>搜索结果: "<span class="search-keyword"><%= keyword %></span>"</h2>
            
            <% if ("all".equals(searchType) || "user".equals(searchType)) { %>
                <!-- 用户搜索结果 -->
                <div class="users-results">
                    <h3>用户</h3>
                    <% 
                        List<User> users = (List<User>) request.getAttribute("users");
                        if (users != null && !users.isEmpty()) {
                            boolean foundOtherUsers = false;
                            for (User foundUser : users) {
                                // 不显示当前用户
                                if (foundUser.getId() == user.getId()) continue;
                                foundOtherUsers = true;
                    %>
                            <div class="user-card">
                                <div class="user-name"><strong><%= foundUser.getUsername() %></strong></div>
                                <div class="user-email">邮箱: <%= foundUser.getEmail() != null ? foundUser.getEmail() : "未设置" %></div>
                                <div class="user-bio">简介: <%= foundUser.getBio() != null ? foundUser.getBio() : "这个人很懒，什么都没留下" %></div>
                                <div class="user-actions">
                                    <form action="friends" method="post" class="add-friend-form">
                                        <input type="hidden" name="action" value="add">
                                        <input type="hidden" name="username" value="<%= foundUser.getUsername() %>">
                                        <button type="submit" class="add-friend-button">添加好友</button>
                                    </form>
                                    <a href="messages?friendId=<%= foundUser.getId() %>" class="message-link">发送私信</a>
                                </div>
                            </div>
                    <%
                            }
                            if (!foundOtherUsers) {
                    %>
                                <p class="no-results">没有找到其他用户</p>
                    <%
                            }
                        } else {
                    %>
                            <p class="no-results">没有找到匹配的用户</p>
                    <%
                        }
                    %>
                </div>
            <% } %>
            
            <% if ("all".equals(searchType) || "post".equals(searchType)) { %>
                <!-- 帖子搜索结果 -->
                <div class="posts-results">
                    <h3>帖子</h3>
                    <% 
                        List<Post> posts = (List<Post>) request.getAttribute("posts");
                        if (posts != null && !posts.isEmpty()) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            for (Post post : posts) {
                    %>
                            <div class="post-card">
                                <div class="post-header">
                                    <span class="post-author"><%= post.getUsername() %></span>
                                    <span class="post-date"><%= sdf.format(post.getCreateTime()) %></span>
                                </div>
                                <div class="post-content"><%= post.getContent() %></div>
                                <% if (post.getImagePath() != null && !post.getImagePath().isEmpty()) { %>
                                <div class="post-image">
                                    <img src="<%= post.getImagePath() %>" alt="帖子图片" style="max-width: 200px; max-height: 200px;">
                                </div>
                                <% } %>
                                <div class="post-actions">
                                    <a href="comment?postId=<%= post.getId() %>">查看评论</a>
                                </div>
                            </div>
                    <%
                            }
                        } else {
                    %>
                            <p class="no-results">没有找到匹配的帖子</p>
                    <%
                        }
                    %>
                </div>
            <% } %>
            
            <%
            // 如果用户和帖子都为空，则显示总体的"未找到"消息
            List<User> users = (List<User>) request.getAttribute("users");
            List<Post> posts = (List<Post>) request.getAttribute("posts");
            boolean noUsers = users == null || users.isEmpty();
            boolean noPosts = posts == null || posts.isEmpty();
            
            if ("all".equals(searchType) && noUsers && noPosts) {
            %>
                <div class="no-results-container" style="text-align: center; margin-top: 30px;">
                    <p class="no-results" style="font-size: 1.2em;">没有找到与 "<%= keyword %>" 相关的任何结果</p>
                    <p>请尝试使用其他关键词搜索</p>
                </div>
            <% } %>
        </div>
    <% } else { %>
        <div class="search-instructions" style="text-align: center; margin-top: 50px;">
            <p>请在上方输入搜索关键词</p>
            <p>您可以搜索用户和帖子内容</p>
        </div>
    <% } %>
</div>

<%@ include file="footer.jsp" %>
</body>
</html>