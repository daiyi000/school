<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<%@ page import="model.Friendship" %>
<%@ page import="java.util.List" %>
<%@ page import="utils.PaginationUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>好友管理</title>
    <link rel="stylesheet" href="css/friends.css">
</head>
<body>
<%@ include file="header.jsp" %>

<div class="main-container">
    <h1>好友管理</h1>

    <% 
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }
        String currentTab = (String) request.getAttribute("currentTab");
        if (currentTab == null) {
            currentTab = "friends";
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

    <!-- 添加好友表单 -->
    <div class="add-friend-container">
        <h2>添加好友</h2>
        <form action="friends" method="post" class="add-friend-form">
            <input type="hidden" name="action" value="add">
            <input type="text" id="username" name="username" placeholder="输入用户名添加好友" required class="search-input">
            <button type="submit" class="search-button">发送请求</button>
        </form>
    </div>

    <!-- 选项卡按钮 -->
    <div class="tab-container">
        <button class="tab-button <%= "friends".equals(currentTab) ? "active" : "" %>" onclick="location.href='friends?tab=friends'">我的好友</button>
        <button class="tab-button <%= "requests".equals(currentTab) ? "active" : "" %>" onclick="location.href='friends?tab=requests'">好友请求</button>
    </div>

    <!-- 选项卡内容 -->
    <div class="tab-content">
        <% if ("friends".equals(currentTab)) { %>
            <h2>我的好友</h2>
            <% 
                PaginationUtils<User> friendsPagination = (PaginationUtils<User>) request.getAttribute("friendsPagination");
                if (friendsPagination != null) {
                    List<User> friends = friendsPagination.getItems();
                    if (friends != null && !friends.isEmpty()) {
                        for (User friend : friends) {
            %>
                <div class="friend-card">
                    <div class="friend-info">
                        <h3><%= friend.getUsername() %></h3>
                        <p>邮箱: <%= friend.getEmail() != null ? friend.getEmail() : "未设置" %></p>
                        <p>简介: <%= friend.getBio() != null ? friend.getBio() : "这个人很懒，什么都没留下" %></p>
                    </div>
                    <div class="friend-actions">
                        <a href="messages?friendId=<%= friend.getId() %>" class="friend-action-button message-button">发送私信</a>
                        <a href="javascript:void(0)" onclick="confirmDeleteFriend(<%= friend.getId() %>, '<%= friend.getUsername() %>')" class="friend-action-button delete-button">删除好友</a>
                    </div>
                </div>
            <%
                        }
                        out.println("<div class=\"pagination\">" + friendsPagination.generatePaginationHTML("friends?tab=friends&requestsPage=" + request.getParameter("requestsPage") + "&friendsPage=") + "</div>");
                    } else {
            %>
                <p class="empty-message">暂无好友</p>
            <%
                    }
                } else {
            %>
                <p class="empty-message">暂无好友</p>
            <%
                }
            %>
        <% } else if ("requests".equals(currentTab)) { %>
            <h2>好友请求</h2>
            <% 
                PaginationUtils<Friendship> requestsPagination = (PaginationUtils<Friendship>) request.getAttribute("requestsPagination");
                if (requestsPagination != null) {
                    List<Friendship> friendRequests = requestsPagination.getItems();
                    if (friendRequests != null && !friendRequests.isEmpty()) {
                        for (Friendship friendRequest : friendRequests) {
            %>
                <div class="friend-card">
                    <div class="friend-info">
                        <p>用户 <strong><%= friendRequest.getFriendUsername() %></strong> 请求添加您为好友</p>
                    </div>
                    <div class="friend-actions">
                        <form action="friends" method="post" style="display: inline;">
                            <input type="hidden" name="action" value="accept">
                            <input type="hidden" name="requestId" value="<%= friendRequest.getId() %>">
                            <button type="submit" class="friend-action-button accept-button">接受</button>
                        </form>
                        <form action="friends" method="post" style="display: inline;">
                            <input type="hidden" name="action" value="reject">
                            <input type="hidden" name="requestId" value="<%= friendRequest.getId() %>">
                            <button type="submit" class="friend-action-button reject-button">拒绝</button>
                        </form>
                    </div>
                </div>
            <%
                        }
                        out.println("<div class=\"pagination\">" + requestsPagination.generatePaginationHTML("friends?tab=requests&friendsPage=" + request.getParameter("friendsPage") + "&requestsPage=") + "</div>");
                    } else {
            %>
                <p class="empty-message">暂无好友请求</p>
            <%
                    }
                } else {
            %>
                <p class="empty-message">暂无好友请求</p>
            <%
                }
            %>
        <% } %>
    </div>

    <form id="deleteFriendForm" action="deleteFriend" method="post" style="display: none;">
        <input type="hidden" id="deleteFriendId" name="friendId" value="">
        <input type="hidden" id="deleteFriendUsername" name="friendUsername" value="">
        <input type="hidden" name="tab" value="<%= currentTab %>">
        <input type="hidden" name="friendsPage" value="<%= request.getParameter("friendsPage") %>">
        <input type="hidden" name="requestsPage" value="<%= request.getParameter("requestsPage") %>">
    </form>

    <script>
    function confirmDeleteFriend(friendId, friendUsername) {
        if (confirm("确定要删除好友 '" + friendUsername + "' 吗？")) {
            document.getElementById("deleteFriendId").value = friendId;
            document.getElementById("deleteFriendUsername").value = friendUsername;
            document.getElementById("deleteFriendForm").submit();
        }
    }

    window.onload = function() {
        <% if ("deleteSuccess".equals(request.getParameter("message"))) { %>
            showCustomAlert("删除好友成功！");
        <% } else if ("deleteFailed".equals(request.getParameter("error"))) { %>
            showCustomAlert("删除好友失败！", true);
        <% } %>
    };

    // 自定义提示框函数
    function showCustomAlert(message, isError = false) {
        const alertDiv = document.createElement('div');
        alertDiv.className = isError ? 'error-message' : 'success-message';
        alertDiv.style.position = 'fixed';
        alertDiv.style.top = '20px';
        alertDiv.style.right = '20px';
        alertDiv.style.zIndex = '1000';
        alertDiv.style.padding = '15px 20px';
        alertDiv.style.borderRadius = '4px';
        alertDiv.style.boxShadow = '0 4px 12px rgba(0, 0, 0, 0.1)';
        alertDiv.style.opacity = '0';
        alertDiv.style.transform = 'translateY(-20px)';
        alertDiv.style.transition = 'opacity 0.3s, transform 0.3s';
        alertDiv.textContent = message;
        
        document.body.appendChild(alertDiv);
        
        setTimeout(() => {
            alertDiv.style.opacity = '1';
            alertDiv.style.transform = 'translateY(0)';
        }, 10);
        
        // 3秒后自动关闭
        setTimeout(() => {
            alertDiv.style.opacity = '0';
            alertDiv.style.transform = 'translateY(-20px)';
            setTimeout(() => {
                alertDiv.remove();
            }, 300);
        }, 3000);
    }
    </script>
</div>

<%@ include file="footer.jsp" %>
</body>
</html>