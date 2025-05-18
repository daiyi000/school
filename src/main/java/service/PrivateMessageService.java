package service;

import dao.PrivateMessageDAO;
import model.PrivateMessage;
import utils.PaginationUtils;

import java.util.List;

public class PrivateMessageService {
    private PrivateMessageDAO privateMessageDAO;
    
    public PrivateMessageService() {
        this.privateMessageDAO = new PrivateMessageDAO();
    }
    
    // 发送私信
    public boolean sendMessage(PrivateMessage message) {
        return privateMessageDAO.sendMessage(message);
    }
    
    // 获取用户收到的私信
    public List<PrivateMessage> getReceivedMessages(int userId) {
        return privateMessageDAO.getReceivedMessages(userId);
    }
    
    // 获取用户发送的私信
    public List<PrivateMessage> getSentMessages(int userId) {
        return privateMessageDAO.getSentMessages(userId);
    }
    
    // 获取用户与特定好友的对话
    public List<PrivateMessage> getConversation(int userId, int friendId) {
        return privateMessageDAO.getConversation(userId, friendId);
    }
    
    // 获取未读消息数量
    public int getUnreadMessageCount(int userId) {
        return privateMessageDAO.getUnreadMessageCount(userId);
    }
    
 // 获取用户收到的私信总数
    public int getTotalReceivedMessages(int userId) {
        return privateMessageDAO.getTotalReceivedMessages(userId);
    }

    // 获取用户发送的私信总数
    public int getTotalSentMessages(int userId) {
        return privateMessageDAO.getTotalSentMessages(userId);
    }

    // 获取用户收到的私信（带分页）
    public List<PrivateMessage> getReceivedMessagesWithPagination(int userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return privateMessageDAO.getReceivedMessagesWithPagination(userId, offset, pageSize);
    }

    // 获取用户发送的私信（带分页）
    public List<PrivateMessage> getSentMessagesWithPagination(int userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return privateMessageDAO.getSentMessagesWithPagination(userId, offset, pageSize);
    }

    // 获取用户与特定好友的对话总数
    public int getTotalConversationMessages(int userId, int friendId) {
        return privateMessageDAO.getTotalConversationMessages(userId, friendId);
    }

    // 获取用户与特定好友的对话（带分页）
    public List<PrivateMessage> getConversationWithPagination(int userId, int friendId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return privateMessageDAO.getConversationWithPagination(userId, friendId, offset, pageSize);
    }

    // 获取用户收到的私信（带分页工具）
    public PaginationUtils<PrivateMessage> getReceivedMessagesWithPaginationUtils(int userId, int page, int pageSize) {
        int totalMessages = getTotalReceivedMessages(userId);
        List<PrivateMessage> messages = getReceivedMessagesWithPagination(userId, page, pageSize);
        return new PaginationUtils<>(messages, pageSize, page, totalMessages);
    }

    // 获取用户发送的私信（带分页工具）
    public PaginationUtils<PrivateMessage> getSentMessagesWithPaginationUtils(int userId, int page, int pageSize) {
        int totalMessages = getTotalSentMessages(userId);
        List<PrivateMessage> messages = getSentMessagesWithPagination(userId, page, pageSize);
        return new PaginationUtils<>(messages, pageSize, page, totalMessages);
    }

    // 获取用户与特定好友的对话（带分页工具）
    public PaginationUtils<PrivateMessage> getConversationWithPaginationUtils(int userId, int friendId, int page, int pageSize) {
        int totalMessages = getTotalConversationMessages(userId, friendId);
        List<PrivateMessage> messages = getConversationWithPagination(userId, friendId, page, pageSize);
        return new PaginationUtils<>(messages, pageSize, page, totalMessages);
    }
}