package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Friendship;
import model.User;
import service.FriendshipService;
import service.NotificationService;
import service.UserService;
import utils.PaginationUtils;

import java.io.IOException;
import java.util.List;

@WebServlet("/friends")
public class FriendController extends HttpServlet {
    private FriendshipService friendshipService;
    private UserService userService;
    private NotificationService notificationService;
    
    @Override
    public void init() throws ServletException {
        friendshipService = new FriendshipService();
        userService = new UserService();
        notificationService = new NotificationService();
    }
    
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 检查用户是否登录
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            // 未登录，重定向到登录页面
            response.sendRedirect("login");
            return;
        }
        
        // 从请求参数中获取当前页码和标签
        int friendsPage = 1;
        int requestsPage = 1;
        String tab = request.getParameter("tab");
        if (tab == null) {
            tab = "friends"; // 默认显示好友列表
        }
        
        String friendsPageStr = request.getParameter("friendsPage");
        if (friendsPageStr != null && !friendsPageStr.trim().isEmpty()) {
            try {
                friendsPage = Integer.parseInt(friendsPageStr);
                if (friendsPage < 1) {
                    friendsPage = 1;
                }
            } catch (NumberFormatException e) {
                // 忽略错误，使用默认页码
            }
        }
        
        String requestsPageStr = request.getParameter("requestsPage");
        if (requestsPageStr != null && !requestsPageStr.trim().isEmpty()) {
            try {
                requestsPage = Integer.parseInt(requestsPageStr);
                if (requestsPage < 1) {
                    requestsPage = 1;
                }
            } catch (NumberFormatException e) {
                // 忽略错误，使用默认页码
            }
        }
        
        // 设置每页显示的条目数量
        int pageSize = 10;
        
        // 获取用户的好友列表（带分页）
        PaginationUtils<User> friendsPagination = 
            friendshipService.getFriendsByUserIdWithPaginationUtils(user.getId(), friendsPage, pageSize);
        request.setAttribute("friendsPagination", friendsPagination);
        
        // 获取用户收到的好友请求（带分页）
        PaginationUtils<Friendship> requestsPagination = 
            friendshipService.getFriendRequestsByUserIdWithPaginationUtils(user.getId(), requestsPage, pageSize);
        request.setAttribute("requestsPagination", requestsPagination);
        
        // 设置当前标签
        request.setAttribute("currentTab", tab);
        
        // 显示好友页面
        request.getRequestDispatcher("/friends.jsp").forward(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 检查用户是否登录
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            // 未登录，重定向到登录页面
            response.sendRedirect("login");
            return;
        }
        
        // 获取操作类型
        String action = request.getParameter("action");
        if (action == null) {
            response.sendRedirect("friends");
            return;
        }
        
        boolean success = false;
        
        switch (action) {
            case "add": // 发送好友请求
                String username = request.getParameter("username");
                if (username == null || username.trim().isEmpty()) {
                    request.setAttribute("error", "用户名不能为空");
                    doGet(request, response);
                    return;
                }
                
                // 查找用户
                User foundUser = null;
                List<User> users = userService.searchUsersByUsername(username);
                if (!users.isEmpty()) {
                    foundUser = users.get(0);
                }
                
                if (foundUser == null) {
                    request.setAttribute("error", "用户不存在");
                    doGet(request, response);
                    return;
                }
                
                if (foundUser.getId() == user.getId()) {
                    request.setAttribute("error", "不能添加自己为好友");
                    doGet(request, response);
                    return;
                }
                
                // 发送好友请求
                success = friendshipService.sendFriendRequest(user.getId(), foundUser.getId());
                if (success) {
                    // 添加好友请求通知
                    notificationService.addFriendRequestNotification(foundUser.getId(), user.getId(), user.getUsername());
                    request.setAttribute("message", "好友请求已发送");
                } else {
                    request.setAttribute("error", "好友请求发送失败，可能已存在好友关系");
                }
                break;
                
            case "accept": // 接受好友请求
                String requestIdStr = request.getParameter("requestId");
                if (requestIdStr == null || requestIdStr.trim().isEmpty()) {
                    response.sendRedirect("friends");
                    return;
                }
                
                try {
                    int requestId = Integer.parseInt(requestIdStr);
                    // 获取请求发送者的ID
                    List<Friendship> requests = friendshipService.getFriendRequestsByUserId(user.getId());
                    int senderId = 0;
                    String senderUsername = "";
                    
                    for (Friendship req : requests) {
                        if (req.getId() == requestId) {
                            senderId = req.getUserId();
                            senderUsername = req.getFriendUsername();
                            break;
                        }
                    }
                    
                    success = friendshipService.acceptFriendRequest(requestId, user.getId());
                    if (success && senderId > 0) {
                        // 添加系统通知
                        notificationService.addSystemNotification(
                            senderId, 
                            user.getUsername() + " 接受了您的好友请求，现在你们已经是好友了！"
                        );
                        request.setAttribute("message", "已接受好友请求");
                    } else {
                        request.setAttribute("error", "接受好友请求失败");
                    }
                } catch (NumberFormatException e) {
                    response.sendRedirect("friends");
                    return;
                }
                break;
                
            case "reject": // 拒绝好友请求
                requestIdStr = request.getParameter("requestId");
                if (requestIdStr == null || requestIdStr.trim().isEmpty()) {
                    response.sendRedirect("friends");
                    return;
                }
                
                try {
                    int requestId = Integer.parseInt(requestIdStr);
                    // 获取请求发送者的ID
                    List<Friendship> requests = friendshipService.getFriendRequestsByUserId(user.getId());
                    int senderId = 0;
                    
                    for (Friendship req : requests) {
                        if (req.getId() == requestId) {
                            senderId = req.getUserId();
                            break;
                        }
                    }
                    
                    success = friendshipService.rejectFriendRequest(requestId, user.getId());
                    if (success && senderId > 0) {
                        // 添加系统通知
                        notificationService.addSystemNotification(
                            senderId, 
                            user.getUsername() + " 拒绝了您的好友请求。"
                        );
                        request.setAttribute("message", "已拒绝好友请求");
                    } else {
                        request.setAttribute("error", "拒绝好友请求失败");
                    }
                } catch (NumberFormatException e) {
                    response.sendRedirect("friends");
                    return;
                }
                break;
                
            default:
                response.sendRedirect("friends");
                return;
        }
        
        // 重新加载好友页面
        doGet(request, response);
    }
}