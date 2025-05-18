package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Administrator;
import model.Comment;
import model.Post;
import service.CommentService;
import service.PostService;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.List;

@WebServlet("/adminPostDetail")
public class AdminPostDetailController extends HttpServlet {
    private PostService postService;
    private CommentService commentService;
    
    @Override
    public void init() throws ServletException {
        postService = new PostService();
        commentService = new CommentService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 检查管理员是否登录
        HttpSession session = request.getSession();
        Administrator admin = (Administrator) session.getAttribute("admin");
        
        if (admin == null) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "未授权访问");
            return;
        }
        
        // 获取帖子ID
        String postIdStr = request.getParameter("postId");
        
        if (postIdStr == null || postIdStr.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "帖子ID不能为空");
            return;
        }
        
        try {
            int postId = Integer.parseInt(postIdStr);
            Post post = postService.getPostById(postId);
            
            if (post == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "帖子不存在");
                return;
            }
            
            // 获取该帖子的所有评论
            List<Comment> comments = commentService.getCommentsByPostId(postId);
            
            // 设置响应类型
            response.setContentType("text/html;charset=UTF-8");
            PrintWriter out = response.getWriter();
            
            // 格式化日期
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            // 输出帖子详情HTML
            out.println("<div class='post-details'>");
            
            // 显示成功或错误消息
            String message = request.getParameter("message");
            String error = request.getParameter("error");
            
            if (message != null) {
                out.println("<div class='message success' style='padding: 10px; background-color: #d4edda; color: #155724; border-radius: 4px; margin-bottom: 15px;'>");
                if ("comment_deleted".equals(message)) {
                    out.println("评论已成功删除");
                } else {
                    out.println(message);
                }
                out.println("</div>");
            }
            
            if (error != null) {
                out.println("<div class='message error' style='padding: 10px; background-color: #f8d7da; color: #721c24; border-radius: 4px; margin-bottom: 15px;'>");
                if ("delete_failed".equals(error)) {
                    out.println("删除评论失败");
                } else {
                    out.println(error);
                }
                out.println("</div>");
            }
            
            out.println("<p><strong>ID:</strong> " + post.getId() + "</p>");
            out.println("<p><strong>作者:</strong> " + post.getUsername() + " (ID: " + post.getUserId() + ")</p>");
            out.println("<p><strong>发布时间:</strong> " + sdf.format(post.getCreateTime()) + "</p>");
            out.println("<p><strong>内容:</strong></p>");
            out.println("<div class='post-content' style='margin: 10px 0; padding: 10px; background-color: #f8f9fa; border-radius: 4px;'>");
            out.println(post.getContent());
            out.println("</div>");
            
            // 显示图片（如果有）
            if (post.getImagePath() != null && !post.getImagePath().isEmpty()) {
                out.println("<p><strong>图片:</strong></p>");
                out.println("<div class='post-image' style='margin: 10px 0;'>");
                out.println("<img src='" + post.getImagePath() + "' alt='帖子图片' style='max-width: 100%; max-height: 300px;'>");
                out.println("</div>");
            }
            
            // 帖子删除按钮
            out.println("<div style='margin: 20px 0;'>");
            out.println("<button onclick='confirmPostAction(" + post.getId() + ", \"delete\")' style='padding: 8px 16px; background-color: #e74c3c; color: white; border: none; border-radius: 4px; cursor: pointer;'>删除帖子</button>");
            out.println("</div>");
            
         // 显示评论区域
            out.println("<h3 style='margin-top: 30px; padding-bottom: 10px; border-bottom: 1px solid #eee;'>评论列表</h3>");
            out.println("<div id='comments-container'>"); // 添加一个包裹评论的容器，便于AJAX刷新

            if (comments != null && !comments.isEmpty()) {
                for (Comment comment : comments) {
                    out.println("<div class='comment' id='comment-" + comment.getId() + "' style='margin: 15px 0; padding: 10px; background-color: #f8f9fa; border-radius: 4px; border-left: 3px solid #3498db;'>");
                    out.println("<div style='display: flex; justify-content: space-between; margin-bottom: 5px;'>");
                    out.println("<span><strong>" + comment.getUsername() + "</strong> (ID: " + comment.getUserId() + ") " + sdf.format(comment.getCreateTime()) + "</span>");
                    out.println("<a href='javascript:void(0)' onclick='ajaxDeleteComment(" + comment.getId() + ", " + post.getId() + ")' style='color: #e74c3c;'>删除</a>");
                    out.println("</div>");
                    out.println("<div style='margin-top: 5px;'>" + comment.getContent() + "</div>");
                    out.println("</div>");
                }
            } else {
                out.println("<p style='text-align: center; color: #6c757d; font-style: italic; margin: 20px 0;'>暂无评论</p>");
            }

            out.println("</div>"); // 关闭评论容器
            
         // 添加必要的JavaScript代码
            out.println("<script>");

         // AJAX删除评论
            out.println("function ajaxDeleteComment(commentId, postId) {");
            out.println("  console.log('删除评论:', commentId, postId);"); // 调试日志
            out.println("  if (confirm('确定要删除该评论吗？此操作不可恢复！')) {");
            out.println("    var xhr = new XMLHttpRequest();");
            out.println("    xhr.onreadystatechange = function() {");
            out.println("      if (xhr.readyState == 4) {");
            out.println("        console.log('响应状态:', xhr.status);"); // 调试日志
            out.println("        console.log('响应内容:', xhr.responseText);"); // 调试日志
            out.println("        if (xhr.status == 200) {");
            out.println("          try {"); // 添加 try-catch 来捕获 JSON 解析错误
            out.println("            var response = JSON.parse(xhr.responseText);");
            out.println("            if (response.success) {");
            out.println("              // 评论删除成功，移除评论元素");
            out.println("              var commentElement = document.getElementById('comment-' + commentId);");
            out.println("              if (commentElement) {");
            out.println("                commentElement.parentNode.removeChild(commentElement);");
            out.println("              }");
            out.println("              // 显示成功消息");
            out.println("              showNotification(response.message, 'success');");
            out.println("              // 检查是否没有评论了");
            out.println("              if (document.getElementById('comments-container').children.length === 0) {");
            out.println("                document.getElementById('comments-container').innerHTML = \"<p style='text-align: center; color: #6c757d; font-style: italic; margin: 20px 0;'>暂无评论</p>\";");
            out.println("              }");
            out.println("            } else {");
            out.println("              showNotification(response.message, 'error');");
            out.println("            }");
            out.println("          } catch (e) {");
            out.println("            console.error('JSON解析错误:', e);"); // 调试日志
            out.println("            showNotification('处理响应时出错', 'error');");
            out.println("          }");
            out.println("        } else {");
            out.println("          showNotification('删除评论时发生错误 (状态码: ' + xhr.status + ')', 'error');");
            out.println("        }");
            out.println("      }");
            out.println("    };");
            out.println("    xhr.open('POST', 'adminCommentAction', true);");
            out.println("    xhr.setRequestHeader('Content-Type', 'application/x-www-form-urlencoded');");
            out.println("    var data = 'action=delete&commentId=' + commentId + '&postId=' + postId + '&isAjax=true';");
            out.println("    console.log('请求数据:', data);"); // 调试日志
            out.println("    xhr.send(data);");
            out.println("  }");
            out.println("}");

            // 显示通知消息
            out.println("function showNotification(message, type) {");
            out.println("  var notification = document.createElement('div');");
            out.println("  notification.className = 'notification ' + type;");
            out.println("  notification.style.position = 'fixed';");
            out.println("  notification.style.top = '20px';");
            out.println("  notification.style.right = '20px';");
            out.println("  notification.style.padding = '10px 20px';");
            out.println("  notification.style.borderRadius = '4px';");
            out.println("  notification.style.zIndex = '9999';");
            out.println("  notification.style.boxShadow = '0 4px 8px rgba(0,0,0,0.1)';");
            out.println("  notification.style.fontSize = '14px';");
            out.println("  notification.style.fontWeight = 'bold';");
            out.println("  notification.style.transition = 'opacity 0.5s, transform 0.5s';");
            out.println("  notification.style.opacity = '0';");
            out.println("  notification.style.transform = 'translateY(-20px)';");
            out.println("  if (type === 'success') {");
            out.println("    notification.style.backgroundColor = '#d4edda';");
            out.println("    notification.style.color = '#155724';");
            out.println("    notification.style.border = '1px solid #c3e6cb';");
            out.println("  } else {");
            out.println("    notification.style.backgroundColor = '#f8d7da';");
            out.println("    notification.style.color = '#721c24';");
            out.println("    notification.style.border = '1px solid #f5c6cb';");
            out.println("  }");
            out.println("  notification.textContent = message;");
            out.println("  document.body.appendChild(notification);");
            out.println("  setTimeout(function() {");
            out.println("    notification.style.opacity = '1';");
            out.println("    notification.style.transform = 'translateY(0)';");
            out.println("  }, 10);");
            out.println("  setTimeout(function() {");
            out.println("    notification.style.opacity = '0';");
            out.println("    notification.style.transform = 'translateY(-20px)';");
            out.println("    setTimeout(function() {");
            out.println("      document.body.removeChild(notification);");
            out.println("    }, 500);");
            out.println("  }, 3000);");
            out.println("}");

            // 其他JavaScript函数
            out.println("function confirmPostAction(postId, action) {");
            out.println("  if (action === 'delete') {");
            out.println("    if (confirm('确定要删除该帖子吗？这将同时删除帖子下的所有评论，此操作不可恢复！')) {");
            out.println("      document.getElementById('postActionPostId').value = postId;");
            out.println("      document.getElementById('postActionType').value = action;");
            out.println("      document.getElementById('postActionForm').submit();");
            out.println("    }");
            out.println("  }");
            out.println("}");

            out.println("</script>");
            
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的帖子ID");
        }
    }
}