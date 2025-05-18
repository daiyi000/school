package service;

import dao.FriendshipDAO;
import model.Friendship;
import model.User;
import utils.PaginationUtils;

import java.util.List;

public class FriendshipService {
    private FriendshipDAO friendshipDAO;
    
    public FriendshipService() {
        this.friendshipDAO = new FriendshipDAO();
    }
    
    // 发送好友请求
    public boolean sendFriendRequest(int userId, int friendId) {
        return friendshipDAO.sendFriendRequest(userId, friendId);
    }
    
    // 接受好友请求
    public boolean acceptFriendRequest(int requestId, int userId) {
        return friendshipDAO.acceptFriendRequest(requestId, userId);
    }
    
    // 拒绝好友请求
    public boolean rejectFriendRequest(int requestId, int userId) {
        return friendshipDAO.rejectFriendRequest(requestId, userId);
    }
    
    // 获取用户的好友列表
    public List<User> getFriendsByUserId(int userId) {
        return friendshipDAO.getFriendsByUserId(userId);
    }
    
    // 获取用户收到的好友请求
    public List<Friendship> getFriendRequestsByUserId(int userId) {
        return friendshipDAO.getFriendRequestsByUserId(userId);
    }
    
 // 删除好友关系
    public boolean deleteFriendship(int userId, int friendId) {
        return friendshipDAO.deleteFriendship(userId, friendId);
    }
    
 // 获取用户的好友总数
    public int getTotalFriendCount(int userId) {
        return friendshipDAO.getTotalFriendCount(userId);
    }

    // 获取用户的好友列表（带分页）
    public List<User> getFriendsByUserIdWithPagination(int userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return friendshipDAO.getFriendsByUserIdWithPagination(userId, offset, pageSize);
    }

    // 获取用户的好友列表（带分页工具）
    public PaginationUtils<User> getFriendsByUserIdWithPaginationUtils(int userId, int page, int pageSize) {
        int totalFriends = getTotalFriendCount(userId);
        List<User> friends = getFriendsByUserIdWithPagination(userId, page, pageSize);
        return new PaginationUtils<>(friends, pageSize, page, totalFriends);
    }

    // 获取用户收到的好友请求总数
    public int getTotalFriendRequestCount(int userId) {
        return friendshipDAO.getTotalFriendRequestCount(userId);
    }

    // 获取用户收到的好友请求（带分页）
    public List<Friendship> getFriendRequestsByUserIdWithPagination(int userId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return friendshipDAO.getFriendRequestsByUserIdWithPagination(userId, offset, pageSize);
    }

    // 获取用户收到的好友请求（带分页工具）
    public PaginationUtils<Friendship> getFriendRequestsByUserIdWithPaginationUtils(int userId, int page, int pageSize) {
        int totalRequests = getTotalFriendRequestCount(userId);
        List<Friendship> requests = getFriendRequestsByUserIdWithPagination(userId, page, pageSize);
        return new PaginationUtils<>(requests, pageSize, page, totalRequests);
    }
}