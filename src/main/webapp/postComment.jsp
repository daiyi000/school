<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<%@ page import="model.Post" %>
<%@ page import="model.Comment" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="utils.PaginationUtils" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>帖子详情</title>
    <link rel="stylesheet" href="css/postComment.css">
</head>
<body>
<%@ include file="header.jsp" %>

<div class="main-container">
    <h1>帖子详情</h1>
    
    <div class="welcome-message">
        <% 
            User user = (User) session.getAttribute("user");
            if (user != null) {
        %>
            <span>欢迎，<span class="welcome-username"><%= user.getUsername() %></span>！</span>
        <% } %>
        <a href="hall" class="back-link">返回大厅</a>
    </div>
    
    <!-- 帖子详情 -->
    <% 
        Post post = (Post) request.getAttribute("post");
        if (post != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    %>
        <div class="post-card">
            <div class="post-header">
                <span class="post-author"><%= post.getUsername() %></span>
                <span class="post-date">发表于 <%= sdf.format(post.getCreateTime()) %></span>
            </div>
            <div class="post-content"><%= post.getContent() %></div>
        </div>
    <% } else { %>
        <div class="no-comments">帖子不存在</div>
    <% } %>
    
    <!-- 评论列表 -->
    <div class="comments-container">
        <h2>评论列表</h2>
        <%
        PaginationUtils<Comment> pagination = (PaginationUtils<Comment>) request.getAttribute("pagination");
        if (pagination != null) {
            List<Comment> comments = pagination.getItems();
            if (comments != null && !comments.isEmpty()) {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                for (Comment comment : comments) {
        %>
                <div class="comment-card">
                    <div class="comment-header">
                        <span class="comment-author"><%= comment.getUsername() %></span>
                        <span class="comment-date">评论于 <%= sdf.format(comment.getCreateTime()) %></span>
                        <% if (user != null && comment.getUserId() == user.getId()) { %>
                            <a href="javascript:void(0)" onclick="confirmDeleteComment(<%= comment.getId() %>, <%= post.getId() %>)" class="delete-comment">删除</a>
                        <% } %>
                    </div>
                    <div class="comment-content"><%= comment.getContent() %></div>
                </div>
        <%
                }
                
                // 输出分页导航
                out.println("<div class=\"pagination\">" + pagination.generatePaginationHTML("comment?postId=" + post.getId()) + "</div>");
                
            } else {
        %>
                <p class="no-comments">暂无评论</p>
        <%
            }
        } else {
        %>
            <p class="no-comments">暂无评论</p>
        <%
        }
        %>
    </div>

    <!-- 删除评论的表单（隐藏） -->
    <form id="deleteCommentForm" action="deleteComment" method="post" style="display: none;">
        <input type="hidden" id="deleteCommentId" name="commentId" value="">
        <input type="hidden" id="deleteCommentPostId" name="postId" value="">
    </form>

    <!-- JavaScript 代码 -->
    <script>
    function confirmDeleteComment(commentId, postId) {
        if (confirm("确定要删除这条评论吗？\n删除后将无法恢复。")) {
            document.getElementById("deleteCommentId").value = commentId;
            document.getElementById("deleteCommentPostId").value = postId;
            document.getElementById("deleteCommentForm").submit();
        }
    }

    // 显示操作结果消息
    window.onload = function() {
        <% if (request.getParameter("message") != null && request.getParameter("message").equals("deleteSuccess")) { %>
            showCustomAlert("删除评论成功！");
        <% } %>
        <% if (request.getParameter("error") != null && request.getParameter("error").equals("deleteFailed")) { %>
            showCustomAlert("删除评论失败！", true);
        <% } %>
    };

    // 自定义提示框函数，取代原生的alert
    function showCustomAlert(message, isError = false) {
        const alertDiv = document.createElement('div');
        alertDiv.className = isError ? 'alert alert-error' : 'alert';
        alertDiv.textContent = message;
        
        document.body.appendChild(alertDiv);
        
        // 3秒后自动关闭
        setTimeout(() => {
            alertDiv.style.opacity = '0';
            setTimeout(() => {
                alertDiv.remove();
            }, 300);
        }, 3000);
    }
    </script>
    
    <!-- 发表评论表单 -->
    <% if (user != null && post != null) { %>
        <div class="comment-form-container">
            <h2>发表评论</h2>
            <% if(request.getAttribute("error") != null) { %>
                <div class="error-message">
                    <%= request.getAttribute("error") %>
                </div>
            <% } %>
            <form action="comment" method="post">
                <input type="hidden" name="postId" value="<%= post.getId() %>">
                <textarea name="content" class="comment-textarea" rows="3" cols="50" placeholder="写评论..." required></textarea>
                <br>
                <button type="submit" class="submit-button">提交评论</button>
            </form>
        </div>
    <% } else if (post != null) { %>
        <p class="login-prompt"><a href="login">登录</a>后才能发表评论</p>
    <% } %>
</div>

<%@ include file="footer.jsp" %>
</body>
</html>