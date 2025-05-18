<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.User" %>
<%@ page import="model.Post" %>
<%@ page import="utils.PaginationUtils" %>
<%@ page import="java.util.List" %>
<%@ page import="java.text.SimpleDateFormat" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>大厅</title>
<link rel="stylesheet" href="css/hall.css">

</head>
<body>
<%@ include file="header.jsp" %>

<div class="main-container">
    <h1>社交系统大厅</h1>

    <%
    User user = (User) session.getAttribute("user");
    if (user != null) {
    %>

    <!-- 发帖表单 -->
    <div class="post-form-container">
        <h2>发布新帖子</h2>
        <% if(request.getAttribute("error") != null) { %>
        <div class="error-message">
            <%= request.getAttribute("error") %>
        </div>
        <% } %>
        <form action="post" method="post" enctype="multipart/form-data">
            <textarea name="content" rows="4" cols="50" placeholder="写点什么..." required></textarea>
            <div class="image-upload-container">
                <label for="image" class="image-upload-label">
                    <i class="image-icon">📷</i> 添加图片
                </label>
                <input type="file" id="image" name="image" accept="image/*" style="display: none;" onchange="previewImage(this)">
                <div id="image-preview-container" style="display: none; margin-top: 10px;">
                    <img id="image-preview" src="#" alt="预览" style="max-width: 200px; max-height: 200px;">
                    <button type="button" onclick="removeImage()" style="margin-left: 10px;">删除</button>
                </div>
            </div>
            <button type="submit">发布</button>
        </form>
    </div>
    <% } else { %>
    <p class="auth-links"><a href="login">登录</a> | <a href="register">注册</a></p>
    <% } %>

    <!-- 帖子列表 -->
    <div class="posts-container">
        <h2>所有帖子</h2>
        <%
        PaginationUtils<Post> pagination = (PaginationUtils<Post>) request.getAttribute("pagination");
        if (pagination != null) {
        List<Post> posts = pagination.getItems();
        if (posts != null && !posts.isEmpty()) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        for (Post post : posts) {
        %>
        <div class="post">
            <p class="post-header"><strong class="post-username"><%= post.getUsername() %></strong> 发表于 <%= sdf.format(post.getCreateTime()) %></p>
            <p class="post-content"><%= post.getContent() %></p>
            <% if (post.getImagePath() != null && !post.getImagePath().isEmpty()) { %>
            <div class="post-image">
                <img src="<%= post.getImagePath() %>" alt="帖子图片" style="max-width: 100%; max-height: 400px;">
            </div>
            <% } %>
            <p class="post-actions">
                <a href="comment?postId=<%= post.getId() %>">查看评论</a>
                <% if (user != null && post.getUserId() == user.getId()) { %>
                | <a href="javascript:void(0)" onclick="confirmDeletePost(<%= post.getId() %>)" class="delete-link">删除</a>
                <% } %>
            </p>
        </div>
        <%
        }

        // 输出分页导航
        out.println("<div class=\"pagination\">" + pagination.generatePaginationHTML("hall") + "</div>");

        } else {
        %>
        <p class="no-posts">暂无帖子</p>
        <%
        }
        } else {
        %>
        <p class="no-posts">暂无帖子</p>
        <%
        }
        %>
    </div>

    <!-- 删除帖子的表单（隐藏） -->
    <form id="deletePostForm" action="deletePost" method="post" style="display: none;">
        <input type="hidden" id="deletePostId" name="postId" value="">
    </form>

    <!-- JavaScript 代码 -->
    <script>
    function confirmDeletePost(postId) {
      if (confirm("确定要删除这个帖子吗？\n删除后将无法恢复，帖子下的所有评论也会一并删除。")) {
        document.getElementById("deletePostId").value = postId;
        document.getElementById("deletePostForm").submit();
      }
    }

    // 图片预览功能
    function previewImage(input) {
        if (input.files && input.files[0]) {
            var reader = new FileReader();
            
            reader.onload = function(e) {
                document.getElementById('image-preview').src = e.target.result;
                document.getElementById('image-preview-container').style.display = 'block';
            }
            
            reader.readAsDataURL(input.files[0]);
        }
    }

    function removeImage() {
        document.getElementById('image').value = '';
        document.getElementById('image-preview-container').style.display = 'none';
    }

    // 显示操作结果消息
    window.onload = function() {
    <% if (request.getParameter("message") != null && request.getParameter("message").equals("deleteSuccess")) { %>
      alert("删除帖子成功！");
    <% } %>
    <% if (request.getParameter("error") != null && request.getParameter("error").equals("deleteFailed")) { %>
      alert("删除帖子失败！");
    <% } %>
    };
    </script>
</div>

<%@ include file="footer.jsp" %>
</body>
</html>