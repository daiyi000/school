
package controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Post;
import model.User;
import model.PrivateMessage;
import model.Comment;
import service.PostService;
import service.UserService;
import service.PrivateMessageService;
import service.CommentService;
import service.FriendshipService;

import com.google.gson.Gson;

@WebServlet("/api/*")
public class ApiController extends HttpServlet {
    private UserService userService;
    private PostService postService;
    private PrivateMessageService messageService;
    private CommentService commentService;
    private Gson gson;
    
    @Override
    public void init() throws ServletException {
        userService = new UserService();
        postService = new PostService();
        messageService = new PrivateMessageService();
        commentService = new CommentService();
        gson = new Gson();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应类型
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // 获取请求路径
        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();
        
        try {
            if (pathInfo == null || pathInfo.equals("/")) {
                // API根路径
                out.print(gson.toJson(new ApiResponse(true, "Social System API")));
            } else if (pathInfo.startsWith("/posts")) {
                handleGetPosts(request, response);
            } else if (pathInfo.startsWith("/messages")) {
                handleGetMessages(request, response);
            } else if (pathInfo.startsWith("/comments")) {
                handleGetComments(request, response);
            } else if (pathInfo.startsWith("/user")) {
                handleGetUser(request, response);
            } else if (pathInfo.startsWith("/friends")) {
                handleGetFriends(request, response);
            }else if (pathInfo.startsWith("/post")) {
                handleGetPostById(request, response);
            }
            else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(new ApiResponse(false, "API endpoint not found")));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ApiResponse(false, "Error: " + e.getMessage())));
            e.printStackTrace();
        }
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 设置响应类型
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        
        // 获取请求路径
        String pathInfo = request.getPathInfo();
        PrintWriter out = response.getWriter();
        
        try {
            if (pathInfo.startsWith("/login")) {
                handleLogin(request, response);
            } else if (pathInfo.startsWith("/posts")) {
                handleAddPost(request, response);
            } else if (pathInfo.startsWith("/messages")) {
                handleSendMessage(request, response);
            } else if (pathInfo.startsWith("/comments")) {
                handleAddComment(request, response);
            } else if (pathInfo.startsWith("/register")) {
                handleRegister(request, response);
            } else if (pathInfo.startsWith("/profile")) {
                handleUpdateProfile(request, response);
            }
            else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(new ApiResponse(false, "API endpoint not found")));
            }
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.print(gson.toJson(new ApiResponse(false, "Error: " + e.getMessage())));
            e.printStackTrace();
        }
    }
    
    // 处理获取帖子的请求
    private void handleGetPosts(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        int page = 1;
        int pageSize = 10;
        
        // 获取分页参数
        String pageParam = request.getParameter("page");
        if (pageParam != null && !pageParam.isEmpty()) {
            page = Integer.parseInt(pageParam);
        }
        
        // 获取帖子列表
        List<Post> posts = postService.getPostsByPage(page, pageSize);
        out.print(gson.toJson(posts));
    }
    
    // 处理获取私信的请求
    private void handleGetMessages(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String userIdParam = request.getParameter("userId");
        String friendIdParam = request.getParameter("friendId");
        
        if (userIdParam != null && friendIdParam != null) {
            int userId = Integer.parseInt(userIdParam);
            int friendId = Integer.parseInt(friendIdParam);
            List<PrivateMessage> messages = messageService.getConversation(userId, friendId);
            out.print(gson.toJson(messages));
        } else if (userIdParam != null) {
            int userId = Integer.parseInt(userIdParam);
            List<PrivateMessage> messages = messageService.getReceivedMessages(userId);
            out.print(gson.toJson(messages));
        } else {
            out.print(gson.toJson(new ApiResponse(false, "Missing parameters")));
        }
    }
    
    // 处理获取评论的请求
    private void handleGetComments(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String postIdParam = request.getParameter("postId");
        
        if (postIdParam != null) {
            int postId = Integer.parseInt(postIdParam);
            List<Comment> comments = commentService.getCommentsByPostId(postId);
            out.print(gson.toJson(comments));
        } else {
            out.print(gson.toJson(new ApiResponse(false, "Missing post ID")));
        }
    }
    
    // 处理获取用户信息的请求
    private void handleGetUser(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String userIdParam = request.getParameter("id");
        
        if (userIdParam != null) {
            int userId = Integer.parseInt(userIdParam);
            User user = userService.getUserById(userId);
            if (user != null) {
                // 不返回密码
                user.setPassword(null);
                out.print(gson.toJson(user));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(new ApiResponse(false, "User not found")));
            }
        } else {
            out.print(gson.toJson(new ApiResponse(false, "Missing user ID")));
        }
    }
    
    // 处理登录请求
    private void handleLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        
        if (username != null && password != null) {
            User user = userService.login(username, password);
            if (user != null) {
                if (user.getStatus() == 0) {
                    out.print(gson.toJson(new ApiResponse(false, "Your account is disabled")));
                } else {
                    // 不返回密码
                    user.setPassword(null);
                    out.print(gson.toJson(new LoginResponse(true, "Login successful", user)));
                }
            } else {
                out.print(gson.toJson(new ApiResponse(false, "Invalid username or password")));
            }
        } else {
            out.print(gson.toJson(new ApiResponse(false, "Username and password are required")));
        }
    }
    
    // 处理添加帖子的请求
    private void handleAddPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String userIdParam = request.getParameter("userId");
        String content = request.getParameter("content");
        
        if (userIdParam != null && content != null) {
            int userId = Integer.parseInt(userIdParam);
            Post post = new Post(userId, content);
            boolean success = postService.addPost(post);
            if (success) {
                out.print(gson.toJson(new ApiResponse(true, "Post added successfully")));
            } else {
                out.print(gson.toJson(new ApiResponse(false, "Failed to add post")));
            }
        } else {
            out.print(gson.toJson(new ApiResponse(false, "User ID and content are required")));
        }
    }
    
    // 处理发送私信的请求
    private void handleSendMessage(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String senderIdParam = request.getParameter("senderId");
        String receiverIdParam = request.getParameter("receiverId");
        String content = request.getParameter("content");
        
        if (senderIdParam != null && receiverIdParam != null && content != null) {
            int senderId = Integer.parseInt(senderIdParam);
            int receiverId = Integer.parseInt(receiverIdParam);
            PrivateMessage message = new PrivateMessage(senderId, receiverId, content);
            boolean success = messageService.sendMessage(message);
            if (success) {
                out.print(gson.toJson(new ApiResponse(true, "Message sent successfully")));
            } else {
                out.print(gson.toJson(new ApiResponse(false, "Failed to send message")));
            }
        } else {
            out.print(gson.toJson(new ApiResponse(false, "Sender ID, receiver ID, and content are required")));
        }
    }
    
    // 处理添加评论的请求
    private void handleAddComment(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String postIdParam = request.getParameter("postId");
        String userIdParam = request.getParameter("userId");
        String content = request.getParameter("content");
        
        if (postIdParam != null && userIdParam != null && content != null) {
            int postId = Integer.parseInt(postIdParam);
            int userId = Integer.parseInt(userIdParam);
            Comment comment = new Comment(postId, userId, content);
            boolean success = commentService.addComment(comment);
            if (success) {
                out.print(gson.toJson(new ApiResponse(true, "Comment added successfully")));
            } else {
                out.print(gson.toJson(new ApiResponse(false, "Failed to add comment")));
            }
        } else {
            out.print(gson.toJson(new ApiResponse(false, "Post ID, user ID, and content are required")));
        }
    }
    
    // 处理注册请求
    private void handleRegister(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        
        if (username != null && password != null && email != null) {
            User user = new User(username, password, email);
            boolean success = userService.register(user);
            if (success) {
                out.print(gson.toJson(new ApiResponse(true, "Registration successful")));
            } else {
                out.print(gson.toJson(new ApiResponse(false, "Registration failed. Username may already exist.")));
            }
        } else {
            out.print(gson.toJson(new ApiResponse(false, "Username, password, and email are required")));
        }
    }
    
 // 处理获取好友列表的请求
    private void handleGetFriends(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String userIdParam = request.getParameter("userId");
        
        if (userIdParam != null) {
            int userId = Integer.parseInt(userIdParam);
            FriendshipService friendshipService = new FriendshipService();
            List<User> friends = friendshipService.getFriendsByUserId(userId);
            out.print(gson.toJson(friends));
        } else {
            out.print(gson.toJson(new ApiResponse(false, "Missing user ID")));
        }
    }
    
 // 处理获取帖子详情的请求
    private void handleGetPostById(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String postIdParam = request.getParameter("postId");
        
        if (postIdParam != null) {
            int postId = Integer.parseInt(postIdParam);
            Post post = postService.getPostById(postId);
            if (post != null) {
                out.print(gson.toJson(post));
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(new ApiResponse(false, "Post not found")));
            }
        } else {
            out.print(gson.toJson(new ApiResponse(false, "Missing post ID")));
        }
    }

    // 处理更新用户资料的请求
    private void handleUpdateProfile(HttpServletRequest request, HttpServletResponse response) throws IOException {
        PrintWriter out = response.getWriter();
        String userIdParam = request.getParameter("userId");
        String email = request.getParameter("email");
        String bio = request.getParameter("bio");
        
        if (userIdParam != null && email != null) {
            int userId = Integer.parseInt(userIdParam);
            User user = userService.getUserById(userId);
            
            if (user != null) {
                user.setEmail(email);
                user.setBio(bio);
                boolean success = userService.updateProfile(user);
                
                if (success) {
                    out.print(gson.toJson(new ApiResponse(true, "Profile updated successfully")));
                } else {
                    out.print(gson.toJson(new ApiResponse(false, "Failed to update profile")));
                }
            } else {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
                out.print(gson.toJson(new ApiResponse(false, "User not found")));
            }
        } else {
            out.print(gson.toJson(new ApiResponse(false, "Missing user ID or email")));
        }
    }
    // API响应类
    class ApiResponse {
        boolean success;
        String message;
        
        public ApiResponse(boolean success, String message) {
            this.success = success;
            this.message = message;
        }
    }
    
    // 登录响应类
    class LoginResponse extends ApiResponse {
        User user;
        
        public LoginResponse(boolean success, String message, User user) {
            super(success, message);
            this.user = user;
        }
    }
    
    
}