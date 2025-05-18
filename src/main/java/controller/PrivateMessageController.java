package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;
import model.PrivateMessage;
import model.User;
import service.FriendshipService;
import service.NotificationService;
import service.PrivateMessageService;
import utils.FileUploadUtils;
import utils.PaginationUtils;

import java.io.IOException;
import java.util.List;

@WebServlet("/messages")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 5 * 1024 * 1024,    // 5 MB
    maxRequestSize = 10 * 1024 * 1024 // 10 MB
)
public class PrivateMessageController extends HttpServlet {
    private PrivateMessageService privateMessageService;
    private FriendshipService friendshipService;
    private NotificationService notificationService;
    
    @Override
    public void init() throws ServletException {
        privateMessageService = new PrivateMessageService();
        friendshipService = new FriendshipService();
        notificationService = new NotificationService();
        
        // 创建上传目录
        String basePath = getServletContext().getRealPath("/");
        FileUploadUtils.createUploadDirectories(basePath);
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
        
        // 从请求参数中获取当前页码，默认为第1页
        int page = 1;
        String pageStr = request.getParameter("page");
        if (pageStr != null && !pageStr.trim().isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
                if (page < 1) {
                    page = 1;
                }
            } catch (NumberFormatException e) {
                // 忽略错误，使用默认页码
            }
        }
        
        // 设置每页显示的条目数量
        int pageSize = 10;
        
        // 获取好友ID参数
        String friendIdStr = request.getParameter("friendId");
        
        if (friendIdStr != null && !friendIdStr.trim().isEmpty()) {
            try {
                int friendId = Integer.parseInt(friendIdStr);
                
                // 获取与该好友的所有对话，不使用分页
                List<PrivateMessage> conversation = privateMessageService.getConversation(user.getId(), friendId);
                
                request.setAttribute("conversation", conversation);
                request.setAttribute("friendId", friendId);
                
                // 获取好友列表（用于侧边栏显示）
                List<User> friends = friendshipService.getFriendsByUserId(user.getId());
                request.setAttribute("friends", friends);
                
                // 转发到对话详情页面
                request.getRequestDispatcher("/messageDetail.jsp").forward(request, response);
                return;
            } catch (NumberFormatException e) {
                // 忽略错误，显示消息列表
            }
        }
        
        // 获取标签（收件箱/发件箱）
        String tab = request.getParameter("tab");
        if (tab == null) {
            tab = "received"; // 默认显示收件箱
        }
        
        if ("received".equals(tab)) {
            // 获取用户收到的私信（带分页）
            PaginationUtils<PrivateMessage> receivedPagination = 
                privateMessageService.getReceivedMessagesWithPaginationUtils(user.getId(), page, pageSize);
            request.setAttribute("receivedPagination", receivedPagination);
        } else {
            // 获取用户发送的私信（带分页）
            PaginationUtils<PrivateMessage> sentPagination = 
                privateMessageService.getSentMessagesWithPaginationUtils(user.getId(), page, pageSize);
            request.setAttribute("sentPagination", sentPagination);
        }
        
        // 设置当前标签
        request.setAttribute("currentTab", tab);
     // 获取好友列表（用于发送新私信）
        List<User> friends = friendshipService.getFriendsByUserId(user.getId());
        request.setAttribute("friends", friends);
        
        // 显示私信列表页面
        request.getRequestDispatcher("/privateMessage.jsp").forward(request, response);
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
        
        // 获取表单数据
        String receiverIdStr = request.getParameter("receiverId");
        String content = request.getParameter("content");
        
        // 获取图片上传部分
        Part filePart = request.getPart("image");
        boolean hasImage = filePart != null && filePart.getSize() > 0;
        boolean hasContent = content != null && !content.trim().isEmpty();
        
        // 简单的输入验证
        if (receiverIdStr == null || receiverIdStr.trim().isEmpty()) {
            request.setAttribute("error", "收件人不能为空");
            doGet(request, response);
            return;
        }
        
        // 必须至少有内容或图片
        if (!hasContent && !hasImage) {
            request.setAttribute("error", "消息内容和图片不能同时为空");
            doGet(request, response);
            return;
        }
        
        try {
            int receiverId = Integer.parseInt(receiverIdStr);
            
            // 创建私信对象
            PrivateMessage message = new PrivateMessage(user.getId(), receiverId, content != null ? content : "");
            
            // 处理图片上传
            if (hasImage) {
                // 验证图片类型
                if (!FileUploadUtils.isImageFile(filePart)) {
                    request.setAttribute("error", "只能上传图片文件");
                    doGet(request, response);
                    return;
                }
                
                // 检查文件大小
                if (FileUploadUtils.getFileSizeInMB(filePart) > 5) {
                    request.setAttribute("error", "图片大小不能超过5MB");
                    doGet(request, response);
                    return;
                }
                
                // 保存图片并获取路径
                String basePath = getServletContext().getRealPath("/");
                String imagePath = FileUploadUtils.saveUploadedFile(filePart, basePath, "messages");
                message.setImagePath(imagePath);
            }
            
            // 发送私信
            boolean success = privateMessageService.sendMessage(message);
            if (success) {
                // 获取私信ID（如果需要）
                List<PrivateMessage> sentMessages = privateMessageService.getSentMessages(user.getId());
                int messageId = 0;
                if (!sentMessages.isEmpty()) {
                    messageId = sentMessages.get(0).getId(); // 最新发送的消息应该在列表的第一个
                }
                
                // 添加私信通知
                notificationService.addMessageNotification(receiverId, messageId, user.getUsername());
                
                // 重新获取对话以便在发送后立即显示
                List<PrivateMessage> conversation = privateMessageService.getConversation(user.getId(), receiverId);
                request.setAttribute("conversation", conversation);
                request.setAttribute("friendId", receiverId);
                
                // 获取好友列表（用于侧边栏显示）
                List<User> friends = friendshipService.getFriendsByUserId(user.getId());
                request.setAttribute("friends", friends);
                
                // 使用请求转发而不是重定向
                request.getRequestDispatcher("/messageDetail.jsp").forward(request, response);
            } else {
                // 发送失败
                request.setAttribute("error", "发送失败，请稍后再试");
                doGet(request, response);
            }
        } catch (NumberFormatException e) {
            request.setAttribute("error", "收件人ID格式错误");
            doGet(request, response);
        }
    }
}