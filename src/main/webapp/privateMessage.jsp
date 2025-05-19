<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<%@ page import="model.PrivateMessage" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="utils.PaginationUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>私信</title>
    <link rel="stylesheet" href="css/privateMessage.css">
</head>
<body>
<%@ include file="header.jsp" %>

<div class="container">
    <h1>私信管理</h1>
    
    <% 
        User user = (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect("login");
            return;
        }
        
        // 获取当前选中的选项卡
        String currentTab = (String) request.getAttribute("currentTab");
        if (currentTab == null) {
            currentTab = "received";
        }
    %>
    
    <% if(request.getAttribute("error") != null) { %>
        <div class="error-message">
            <%= request.getAttribute("error") %>
        </div>
    <% } %>
    
    <% if(request.getParameter("sent") != null && request.getParameter("sent").equals("success")) { %>
        <div class="success-message">
            私信发送成功！
        </div>
    <% } %>
    
  
    
    <!-- 选项卡导航 -->
    <div class="tab-container">
        <div class="tab <%= "received".equals(currentTab) ? "active" : "" %>" onclick="switchTab('received')">收到的私信</div>
        <div class="tab <%= "sent".equals(currentTab) ? "active" : "" %>" onclick="switchTab('sent')">发送的私信</div>
    </div>
    
    <!-- 收到的私信列表 -->
    <div id="received-tab" class="tab-content <%= "received".equals(currentTab) ? "active" : "" %>">
        <h2>收到的私信</h2>
        <div class="message-list">
            <% 
                PaginationUtils<PrivateMessage> receivedPagination = (PaginationUtils<PrivateMessage>) request.getAttribute("receivedPagination");
                if (receivedPagination != null) {
                    List<PrivateMessage> receivedMessages = receivedPagination.getItems();
                    if (receivedMessages != null && !receivedMessages.isEmpty()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        for (PrivateMessage message : receivedMessages) {
            %>
                        <div class="message-item <%= message.getIsRead() == 0 ? "message-unread" : "" %>">
                            <div class="message-header">
                                <div class="message-sender">来自: <%= message.getSenderUsername() %></div>
                                <div class="message-time"><%= sdf.format(message.getCreateTime()) %></div>
                            </div>
                            <div class="message-content"><%= message.getContent() %></div>
                            <div class="message-action">
                                <a href="messages?friendId=<%= message.getSenderId() %>">查看对话</a>
                            </div>
                        </div>
            <%
                        }
                        
                        // 输出分页导航
                        out.println("<div class=\"pagination\">" + receivedPagination.generatePaginationHTML("messages?tab=received") + "</div>");
                        
                    } else {
            %>
                        <div class="empty-message">暂无收到的私信</div>
            <%
                    }
                } else {
            %>
                    <div class="empty-message">暂无收到的私信</div>
            <%
                }
            %>
        </div>
    </div>
    
    <!-- 发送的私信列表 -->
    <div id="sent-tab" class="tab-content <%= "sent".equals(currentTab) ? "active" : "" %>">
        <h2>发送的私信</h2>
        <div class="message-list">
            <% 
                PaginationUtils<PrivateMessage> sentPagination = (PaginationUtils<PrivateMessage>) request.getAttribute("sentPagination");
                if (sentPagination != null) {
                    List<PrivateMessage> sentMessages = sentPagination.getItems();
                    if (sentMessages != null && !sentMessages.isEmpty()) {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        for (PrivateMessage message : sentMessages) {
            %>
                        <div class="message-item">
                            <div class="message-header">
                                <div class="message-sender">发送给: <%= message.getReceiverUsername() %></div>
                                <div class="message-time"><%= sdf.format(message.getCreateTime()) %></div>
                            </div>
                            <div class="message-content"><%= message.getContent() %></div>
                            <div class="message-action">
                                <a href="messages?friendId=<%= message.getReceiverId() %>">查看对话</a>
                            </div>
                        </div>
            <%
                        }
                        
                        // 输出分页导航
                        out.println("<div class=\"pagination\">" + sentPagination.generatePaginationHTML("messages?tab=sent") + "</div>");
                        
                    } else {
            %>
                        <div class="empty-message">暂无发送的私信</div>
            <%
                    }
                } else {
            %>
                    <div class="empty-message">暂无发送的私信</div>
            <%
                }
            %>
        </div>
    </div>
</div>

<!-- JavaScript 代码 -->
<script>
    // 切换选项卡
    function switchTab(tabName) {
        // 修改URL，保留页码参数
        var urlParams = new URLSearchParams(window.location.search);
        urlParams.set('tab', tabName);
        
        // 保留页码参数
        if (urlParams.has(tabName + 'Page')) {
            urlParams.set('page', urlParams.get(tabName + 'Page'));
        } else {
            urlParams.delete('page');
        }
        
        // 重定向到新URL
        window.location.href = 'messages?' + urlParams.toString();
    }
    
    // 页面加载时自动滚动到错误消息或成功消息位置
    window.onload = function() {
        <% if (request.getAttribute("error") != null || (request.getParameter("sent") != null && request.getParameter("sent").equals("success"))) { %>
            window.scrollTo(0, 0);
        <% } %>
    };
</script>

<%@ include file="footer.jsp" %>
</body>
</html>