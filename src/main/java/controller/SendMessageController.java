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
import service.NotificationService;
import service.PrivateMessageService;
import utils.FileUploadUtils;

import java.io.IOException;
import java.util.List;

@WebServlet("/sendMessage")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024, // 1 MB
    maxFileSize = 5 * 1024 * 1024,    // 5 MB
    maxRequestSize = 10 * 1024 * 1024 // 10 MB
)
public class SendMessageController extends HttpServlet {
    private PrivateMessageService privateMessageService;
    private NotificationService notificationService;
    
    @Override
    public void init() throws ServletException {
        privateMessageService = new PrivateMessageService();
        notificationService = new NotificationService();
        
        // Create upload directories
        String basePath = getServletContext().getRealPath("/");
        FileUploadUtils.createUploadDirectories(basePath);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Check if user is logged in
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");
        
        if (user == null) {
            // Not logged in, redirect to login page
            response.sendRedirect("login");
            return;
        }
        
        // Get form data
        String receiverIdStr = request.getParameter("receiverId");
        String content = request.getParameter("content");
        
        // Basic input validation
        boolean hasContent = content != null && !content.trim().isEmpty();
        Part filePart = request.getPart("image");
        boolean hasImage = filePart != null && filePart.getSize() > 0;
        
        if (receiverIdStr == null || receiverIdStr.trim().isEmpty()) {
            response.sendRedirect("messages?error=invalidReceiverId");
            return;
        }
        
        // Either content or image must be provided
        if (!hasContent && !hasImage) {
            response.sendRedirect("messages?friendId=" + receiverIdStr + "&error=emptyContentAndNoImage");
            return;
        }
        
        try {
            int receiverId = Integer.parseInt(receiverIdStr);
            
            // Create private message object
            PrivateMessage message = new PrivateMessage(user.getId(), receiverId, content != null ? content : "");
            
            // Handle image upload if present
            if (hasImage) {
                // Validate image
                if (!FileUploadUtils.isImageFile(filePart)) {
                    response.sendRedirect("messages?friendId=" + receiverId + "&error=invalidImageFormat");
                    return;
                }
                
                // Check file size
                if (FileUploadUtils.getFileSizeInMB(filePart) > 5) {
                    response.sendRedirect("messages?friendId=" + receiverId + "&error=imageTooLarge");
                    return;
                }
                
                // Save the image and get its path
                String basePath = getServletContext().getRealPath("/");
                String imagePath = FileUploadUtils.saveUploadedFile(filePart, basePath, "messages");
                message.setImagePath(imagePath);
            }
            
            // Send the message
            boolean success = privateMessageService.sendMessage(message);
            if (success) {
                // Get message ID
                List<PrivateMessage> sentMessages = privateMessageService.getSentMessages(user.getId());
                int messageId = 0;
                if (!sentMessages.isEmpty()) {
                    messageId = sentMessages.get(0).getId();
                }
                
                // Add notification
                notificationService.addMessageNotification(receiverId, messageId, user.getUsername());
                
                // Send successful, redirect back to conversation page
                response.sendRedirect("messages?friendId=" + receiverId + "&sent=success");
            } else {
                // Send failed
                response.sendRedirect("messages?friendId=" + receiverId + "&error=sendFailed");
            }
        } catch (NumberFormatException e) {
            response.sendRedirect("messages?error=invalidReceiverId");
        }
    }
}