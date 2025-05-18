<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<%@ page import="model.PrivateMessage" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>私信对话</title>
    <link rel="stylesheet" href="css/messageDetail.css">
    
</head>
<body>
<%@ include file="header.jsp" %>
    <div class="container">
        <h1>私信对话</h1>
        
        <% 
            User user = (User) session.getAttribute("user");
            if (user == null) {
                response.sendRedirect("login");
                return;
            }
            
            int friendId = 0;
            String friendIdStr = request.getParameter("friendId");
            if (friendIdStr != null && !friendIdStr.trim().isEmpty()) {
                try {
                    friendId = Integer.parseInt(friendIdStr);
                } catch (NumberFormatException e) {
                    response.sendRedirect("messages");
                    return;
                }
            } else {
                response.sendRedirect("messages");
                return;
            }
            
            // 查找好友名称用于显示
            String friendName = "用户";
            List<User> allFriends = (List<User>) request.getAttribute("friends");
            if (allFriends != null) {
                for (User friend : allFriends) {
                    if (friend.getId() == friendId) {
                        friendName = friend.getUsername();
                        break;
                    }
                }
            }
        %>
        
        <div class="navigation-links">
            <a href="hall" class="btn">首页</a>
            <a href="messages" class="btn">返回私信列表</a>
        </div>
        
        <div class="chat-layout">
            <!-- 侧边栏：好友列表 -->
            <div class="sidebar">
                <div class="friend-list">
                    <h3>好友列表</h3>
                    <% 
                        List<User> friends = (List<User>) request.getAttribute("friends");
                        if (friends != null && !friends.isEmpty()) {
                            for (User friend : friends) {
                                boolean isActive = friend.getId() == friendId;
                    %>
                                <div class="friend-item <%= isActive ? "active" : "" %>">
                                    <a href="messages?friendId=<%= friend.getId() %>" class="friend-link">
                                        <%= friend.getUsername() %>
                                    </a>
                                </div>
                    <%
                            }
                        } else {
                    %>
                            <p style="color: #777;">暂无好友</p>
                    <%
                        }
                    %>
                </div>
            </div>
            
            <!-- 主内容区：对话 -->
            <div class="chat-main">
                <h3 class="conversation-title">
                    与 <%= friendName %> 的对话
                </h3>
                
                <!-- 对话历史 -->
                <div id="messageContainer" class="message-container">
                    <% 
                        List<PrivateMessage> conversation = (List<PrivateMessage>) request.getAttribute("conversation");
                        if (conversation != null && !conversation.isEmpty()) {
                            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                            for (PrivateMessage message : conversation) {
                                boolean isSentByMe = message.getSenderId() == user.getId();
                    %>
                            <div class="message-item">
                                <div class="message-bubble <%= isSentByMe ? "sent" : "received" %>">
                                    <% if (message.getContent() != null && !message.getContent().trim().isEmpty()) { %>
                                    <p class="message-content"><%= message.getContent() %></p>
                                    <% } %>
                                    <% if (message.getImagePath() != null && !message.getImagePath().isEmpty()) { %>
                                    <div class="message-image">
                                        <img src="<%= message.getImagePath() %>" alt="消息图片" style="max-width: 200px; max-height: 200px; margin-top: 5px;">
                                    </div>
                                    <% } %>
                                    <span class="message-time"><%= sdf.format(message.getCreateTime()) %></span>
                                </div>
                                <div style="clear: both;"></div>
                            </div>
                    <%
                            }
                        } else {
                    %>
                            <p class="empty-message">暂无对话历史，发送第一条消息开始对话吧！</p>
                    <%
                        }
                    %>
                </div>
                
                <!-- 发送消息表单 -->
                <div class="message-form">
                    <form action="sendMessage" method="post" enctype="multipart/form-data">
                        <input type="hidden" name="receiverId" value="<%= friendId %>">
                        <textarea name="content" class="message-input" rows="3" placeholder="输入消息..."></textarea>
                        <div class="message-actions">
                            <div class="attachment-btn">
                                <label for="image" class="paperclip-icon">
                                    <i>📎</i>
                                </label>
                                <input type="file" id="image" name="image" accept="image/*" style="display: none;" onchange="previewImage(this)">
                            </div>
                            <div id="image-preview-container" style="display: none; margin-top: 5px;">
                                <img id="image-preview" src="#" alt="预览" style="max-width: 100px; max-height: 100px;">
                                <button type="button" onclick="removeImage()" class="remove-image-btn">×</button>
                            </div>
                            <div class="send-button-container">
                                <button type="submit" class="send-button">发送</button>
                            </div>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
    
    <!-- JavaScript - 自动滚动到底部 -->
    <script>
        // 页面加载完成后自动滚动到对话底部
        window.onload = function() {
            var messageContainer = document.getElementById("messageContainer");
            if (messageContainer) {
                messageContainer.scrollTop = messageContainer.scrollHeight;
            }
        };
        
        // 图片预览功能
        function previewImage(input) {
            if (input.files && input.files[0]) {
                var reader = new FileReader();
                
                reader.onload = function(e) {
                    document.getElementById('image-preview').src = e.target.result;
                    document.getElementById('image-preview-container').style.display = 'flex';
                }
                
                reader.readAsDataURL(input.files[0]);
            }
        }

        function removeImage() {
            document.getElementById('image').value = '';
            document.getElementById('image-preview-container').style.display = 'none';
        }
    </script>
    
    <%@ include file="footer.jsp" %>
</body>
</html>