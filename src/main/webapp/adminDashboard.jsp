<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="model.Administrator" %>
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
    <title>管理员控制台</title>
    <style>
        body {
            font-family: 'Helvetica Neue', Arial, sans-serif;
            background-color: #f5f5f5;
            margin: 0;
            padding: 0;
            color: #333;
        }
        
        .container {
            max-width: 1200px;
            margin: 0 auto;
            padding: 20px;
        }
        
        .header {
            background-color: #2c3e50;
            color: white;
            padding: 20px;
            margin-bottom: 20px;
            display: flex;
            justify-content: space-between;
            align-items: center;
        }
        
        .header h1 {
            margin: 0;
        }
        
        .logout {
            color: white;
            text-decoration: none;
            padding: 8px 16px;
            background-color: #e74c3c;
            border-radius: 4px;
        }
        
        .logout:hover {
            background-color: #c0392b;
        }
        
        .password-link {
            margin-right: 15px;
            color: white;
            text-decoration: none;
            background-color: #3498db;
            padding: 5px 10px;
            border-radius: 4px;
        }
        
        .password-link:hover {
            background-color: #2980b9;
        }
        
        .navigation {
            display: flex;
            background-color: #fff;
            border-radius: 8px;
            margin-bottom: 20px;
            overflow: hidden;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        
        .nav-link {
            padding: 15px 20px;
            text-decoration: none;
            color: #2c3e50;
            flex: 1;
            text-align: center;
            border-right: 1px solid #eee;
        }
        
        .nav-link:last-child {
            border-right: none;
        }
        
        .nav-link.active {
            background-color: #3498db;
            color: white;
        }
        
        .nav-link:hover:not(.active) {
            background-color: #f8f9fa;
        }
        
        .content {
            background-color: #fff;
            border-radius: 8px;
            padding: 20px;
            box-shadow: 0 0 10px rgba(0, 0, 0, 0.1);
        }
        
        h2 {
            margin-top: 0;
            margin-bottom: 20px;
            padding-bottom: 10px;
            border-bottom: 1px solid #eee;
        }
        
        table {
            width: 100%;
            border-collapse: collapse;
        }
        
        th, td {
            padding: 12px 15px;
            text-align: left;
            border-bottom: 1px solid #eee;
        }
        
        th {
            background-color: #f8f9fa;
            font-weight: bold;
        }
        
        tr:hover {
            background-color: #f8f9fa;
        }
        
        .pagination {
            display: flex;
            justify-content: center;
            margin-top: 20px;
        }
        
        .pagination a {
            padding: 8px 12px;
            margin: 0 5px;
            border-radius: 4px;
            text-decoration: none;
            color: #333;
            background-color: #f2f2f2;
            transition: background-color 0.3s;
        }
        
        .pagination a:hover {
            background-color: #e0e0e0;
        }
        
        .pagination .active {
            background-color: #3498db;
            color: white;
        }
        
        .action-link {
            display: inline-block;
            margin-right: 8px;
            padding: 4px 8px;
            border-radius: 4px;
            text-decoration: none;
            color: #3498db;
            background-color: #e8f4fe;
            font-size: 0.9em;
        }

        .action-link:hover {
            background-color: #d6e9f9;
        }

        .action-link.danger {
            color: #e74c3c;
            background-color: #fee8e7;
        }

        .action-link.danger:hover {
            background-color: #fdd6d4;
        }

        .action-link.success {
            color: #27ae60;
            background-color: #e6f9ee;
        }

        .action-link.success:hover {
            background-color: #d4f5e2;
        }
        
        .message {
            padding: 10px;
            border-radius: 4px;
            margin-bottom: 15px;
        }

        .success {
            background-color: #d4edda;
            color: #155724;
            border: 1px solid #c3e6cb;
        }

        .error {
            background-color: #f8d7da;
            color: #721c24;
            border: 1px solid #f5c6cb;
        }
        
        .notification {
    position: fixed;
    top: 20px;
    right: 20px;
    padding: 10px 20px;
    border-radius: 4px;
    z-index: 9999;
    box-shadow: 0 4px 8px rgba(0,0,0,0.1);
    font-size: 14px;
    font-weight: bold;
    transition: opacity 0.5s, transform 0.5s;
}

.notification.success {
    background-color: #d4edda;
    color: #155724;
    border: 1px solid #c3e6cb;
}

.notification.error {
    background-color: #f8d7da;
    color: #721c24;
    border: 1px solid #f5c6cb;
}
    </style>
</head>
<body>
    <% 
        Administrator admin = (Administrator) session.getAttribute("admin");
        if (admin == null) {
            response.sendRedirect("adminLogin");
            return;
        }
        
        String section = (String) request.getAttribute("section");
        if (section == null) {
            section = "users";
        }
    %>
    
    <div class="header">
        <h1>管理员控制台</h1>
        <div>
            <span>欢迎, <%= admin.getUsername() %></span>
            <a href="adminChangePassword" class="password-link">修改密码</a>
            <a href="adminLogout" class="logout">退出登录</a>
        </div>
    </div>
    
    <div class="container">
        <div class="navigation">
            <a href="adminDashboard?section=users" class="nav-link <%= "users".equals(section) ? "active" : "" %>">用户管理</a>
            <a href="adminDashboard?section=posts" class="nav-link <%= "posts".equals(section) ? "active" : "" %>">帖子管理</a>
        </div>
        
        <div class="content">
            <% if(request.getParameter("message") != null) { %>
                <div class="message success">
                    <% 
                        String message = request.getParameter("message");
                        if ("action_success".equals(message)) {
                            out.print("操作成功完成！");
                        } else if ("comment_deleted".equals(message)) {
                            out.print("评论已成功删除！");
                        } else {
                            out.print(message);
                        }
                    %>
                </div>
            <% } %>
            
            <% if(request.getParameter("error") != null) { %>
                <div class="message error">
                    <% 
                        String error = request.getParameter("error");
                        if ("action_failed".equals(error)) {
                            out.print("操作失败，请稍后再试！");
                        } else if ("missing_params".equals(error)) {
                            out.print("参数缺失，请重试！");
                        } else if ("invalid_action".equals(error)) {
                            out.print("无效的操作类型！");
                        } else if ("invalid_user_id".equals(error)) {
                            out.print("无效的用户ID！");
                        } else if ("invalid_post_id".equals(error)) {
                            out.print("无效的帖子ID！");
                        } else if ("delete_failed".equals(error)) {
                            out.print("删除失败，请稍后再试！");
                        } else {
                            out.print(error);
                        }
                    %>
                </div>
            <% } %>
            
            <% if ("users".equals(section)) { %>
                <h2>用户列表</h2>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>用户名</th>
                            <th>邮箱</th>
                            <th>注册时间</th>
                            <th>状态</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                            PaginationUtils<User> usersPagination = (PaginationUtils<User>) request.getAttribute("pagination");
                            if (usersPagination != null) {
                                List<User> users = usersPagination.getItems();
                                for (User user : users) {
                        %>
                        <tr>
                            <td><%= user.getId() %></td>
                            <td><%= user.getUsername() %></td>
                            <td><%= user.getEmail() != null ? user.getEmail() : "" %></td>
                            <td><%= user.getRegisterTime() %></td>
                            <td>
                                <% if (user.getStatus() == 1) { %>
                                    <span style="color: #27ae60;">正常</span>
                                <% } else { %>
                                    <span style="color: #e74c3c;">已禁用</span>
                                <% } %>
                            </td>
                            <td>
                                <a href="javascript:void(0)" onclick="showUserDetails(<%= user.getId() %>)" class="action-link">查看详情</a>
                                <% if (user.getStatus() == 1) { %>
                                    <a href="javascript:void(0)" onclick="confirmUserAction(<%= user.getId() %>, 'disable')" class="action-link danger">禁用</a>
                                <% } else { %>
                                    <a href="javascript:void(0)" onclick="confirmUserAction(<%= user.getId() %>, 'enable')" class="action-link success">启用</a>
                                <% } %>
                                <a href="javascript:void(0)" onclick="confirmUserAction(<%= user.getId() %>, 'delete')" class="action-link danger">删除</a>
                            </td>
                        </tr>
                        <% 
                                }
                                out.println("<div class=\"pagination\">" + usersPagination.generatePaginationHTML("adminDashboard?section=users") + "</div>");
                            }
                        %>
                    </tbody>
                </table>
            <% } else if ("posts".equals(section)) { %>
                <h2>帖子列表</h2>
                <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>用户</th>
                            <th>内容</th>
                            <th>发布时间</th>
                            <th>操作</th>
                        </tr>
                    </thead>
                    <tbody>
                        <% 
                            PaginationUtils<Post> postsPagination = (PaginationUtils<Post>) request.getAttribute("pagination");
                            if (postsPagination != null) {
                                List<Post> posts = postsPagination.getItems();
                                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                for (Post post : posts) {
                        %>
                        <tr>
                            <td><%= post.getId() %></td>
                            <td><%= post.getUsername() %></td>
                            <td>
                                <% 
                                    String content = post.getContent();
                                    if (content.length() > 50) {
                                        out.print(content.substring(0, 50) + "...");
                                    } else {
                                        out.print(content);
                                    }
                                %>
                            </td>
                            <td><%= sdf.format(post.getCreateTime()) %></td>
                            <td>
                                <a href="javascript:void(0)" onclick="showPostDetails(<%= post.getId() %>)" class="action-link">查看详情</a>
                                <a href="javascript:void(0)" onclick="confirmPostAction(<%= post.getId() %>, 'delete')" class="action-link danger">删除</a>
                            </td>
                        </tr>
                        <% 
                                }
                                out.println("<div class=\"pagination\">" + postsPagination.generatePaginationHTML("adminDashboard?section=posts") + "</div>");
                            }
                        %>
                    </tbody>
                </table>
            <% } %>
        </div>
    </div>
    
    <!-- 用户详情模态框 -->
    <div id="userDetailsModal" style="display: none; position: fixed; z-index: 1; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0,0,0,0.4);">
        <div style="background-color: #fefefe; margin: 15% auto; padding: 20px; border: 1px solid #888; width: 80%; max-width: 600px; border-radius: 8px;">
            <span style="color: #aaa; float: right; font-size: 28px; font-weight: bold; cursor: pointer;" onclick="closeUserDetailsModal()">&times;</span>
            <h2>用户详情</h2>
            <div id="userDetailsContent">
                <!-- 这里将通过 AJAX 加载用户详情 -->
                <p>加载中...</p>
            </div>
        </div>
    </div>
    
    <!-- 帖子详情模态框 -->
    <div id="postDetailsModal" style="display: none; position: fixed; z-index: 1; left: 0; top: 0; width: 100%; height: 100%; overflow: auto; background-color: rgba(0,0,0,0.4);">
        <div style="background-color: #fefefe; margin: 15% auto; padding: 20px; border: 1px solid #888; width: 80%; max-width: 600px; border-radius: 8px;">
            <span style="color: #aaa; float: right; font-size: 28px; font-weight: bold; cursor: pointer;" onclick="closePostDetailsModal()">&times;</span>
            <h2>帖子详情</h2>
            <div id="postDetailsContent">
                <!-- 这里将通过 AJAX 加载帖子详情 -->
                <p>加载中...</p>
            </div>
        </div>
    </div>
    
    <!-- 隐藏的表单，用于提交用户操作 -->
    <form id="userActionForm" action="adminUserAction" method="post" style="display: none;">
        <input type="hidden" id="userActionUserId" name="userId" value="">
        <input type="hidden" id="userActionType" name="action" value="">
    </form>

    <!-- 隐藏的表单，用于提交帖子操作 -->
    <form id="postActionForm" action="adminPostAction" method="post" style="display: none;">
        <input type="hidden" id="postActionPostId" name="postId" value="">
        <input type="hidden" id="postActionType" name="action" value="">
    </form>

    <!-- 隐藏的表单，用于提交评论操作 (非AJAX方式使用) -->
<form id="commentActionForm" action="adminCommentAction" method="post" style="display: none;">
    <input type="hidden" id="commentActionCommentId" name="commentId" value="">
    <input type="hidden" id="commentActionPostId" name="postId" value="">
    <input type="hidden" id="commentActionType" name="action" value="">
    <input type="hidden" name="isAjax" value="false">
</form>
    
    <script>
        // 显示用户详情模态框
        function showUserDetails(userId) {
            document.getElementById('userDetailsModal').style.display = 'block';
            document.getElementById('userDetailsContent').innerHTML = '<p>加载中...</p>';
            
            // 使用 AJAX 获取用户详情
            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function() {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        document.getElementById('userDetailsContent').innerHTML = xhr.responseText;
                    } else {
                        document.getElementById('userDetailsContent').innerHTML = '<p>加载用户详情失败</p>';
                    }
                }
            };
            xhr.open('GET', 'adminUserDetail?userId=' + userId, true);
            xhr.send();
        }
        
        // 关闭用户详情模态框
        function closeUserDetailsModal() {
            document.getElementById('userDetailsModal').style.display = 'none';
        }
        
        // 显示帖子详情模态框
        function showPostDetails(postId) {
            document.getElementById('postDetailsModal').style.display = 'block';
            document.getElementById('postDetailsContent').innerHTML = '<p>加载中...</p>';
            
            // 使用 AJAX 获取帖子详情
            var xhr = new XMLHttpRequest();
            xhr.onreadystatechange = function() {
                if (xhr.readyState == 4) {
                    if (xhr.status == 200) {
                        document.getElementById('postDetailsContent').innerHTML = xhr.responseText;
                    } else {
                        document.getElementById('postDetailsContent').innerHTML = '<p>加载帖子详情失败</p>';
                    }
                }
            };
            xhr.open('GET', 'adminPostDetail?postId=' + postId, true);
            xhr.send();
        }
        
        // 关闭帖子详情模态框
        function closePostDetailsModal() {
            document.getElementById('postDetailsModal').style.display = 'none';
        }
        
        // 用户操作确认
        function confirmUserAction(userId, action) {
            let message = "";
            if (action === 'delete') {
                message = "确定要删除该用户吗？这将同时删除用户的所有帖子、评论和私信，此操作不可恢复！";
            } else if (action === 'disable') {
                message = "确定要禁用该用户吗？禁用后用户将无法登录系统。";
            } else if (action === 'enable') {
                message = "确定要启用该用户吗？";
            }
            
            if (confirm(message)) {
                document.getElementById('userActionUserId').value = userId;
                document.getElementById('userActionType').value = action;
                document.getElementById('userActionForm').submit();
            }
        }
        
        // 帖子操作确认
        function confirmPostAction(postId, action) {
            if (action === 'delete') {
                if (confirm("确定要删除该帖子吗？这将同时删除帖子下的所有评论，此操作不可恢复！")) {
                    document.getElementById('postActionPostId').value = postId;
                    document.getElementById('postActionType').value = action;
                    document.getElementById('postActionForm').submit();
                }
            }
        }
        
        // 评论操作确认
        function confirmCommentAction(commentId, postId, action) {
            if (action === 'delete') {
                if (confirm("确定要删除该评论吗？此操作不可恢复！")) {
                    document.getElementById('commentActionCommentId').value = commentId;
                    document.getElementById('commentActionPostId').value = postId;
                    document.getElementById('commentActionType').value = action;
                    document.getElementById('commentActionForm').submit();
                }
            }
        }
        
        // 关闭模态框当点击模态框外部时
        window.onclick = function(event) {
            if (event.target == document.getElementById('userDetailsModal')) {
                document.getElementById('userDetailsModal').style.display = 'none';
            }
            if (event.target == document.getElementById('postDetailsModal')) {
                document.getElementById('postDetailsModal').style.display = 'none';
            }
        }
    </script>
</body>
</html>