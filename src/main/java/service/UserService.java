package service;

import java.util.List;

import dao.UserDAO;
import model.User;

public class UserService {
    private UserDAO userDAO;
    
    public UserService() {
        this.userDAO = new UserDAO();
    }
    
    // 用户登录
    public User login(String username, String password) {
        return userDAO.login(username, password);
    }
    
    // 用户注册
    public boolean register(User user) {
        // 检查用户名是否已存在
        if (userDAO.isUsernameExists(user.getUsername())) {
            return false;
        }
        
        return userDAO.register(user);
    }
    
    // 获取用户信息
    public User getUserById(int userId) {
        return userDAO.getUserById(userId);
    }
    
    // 更新用户信息
    public boolean updateProfile(User user) {
        return userDAO.updateProfile(user);
    }
    
 // 根据用户名搜索用户
    public List<User> searchUsersByUsername(String username) {
        return userDAO.searchUsersByUsername(username);
    }
    
 // 获取所有用户
    public List<User> getAllUsers() {
        return userDAO.getAllUsers();
    }

    // 获取用户总数
    public int getTotalUsers() {
        return userDAO.getTotalUsers();
    }

    // 分页获取用户
    public List<User> getAllUsersWithPagination(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return userDAO.getAllUsersWithPagination(offset, pageSize);
    }
    
 // 删除用户账户
    public boolean deleteUser(int userId) {
        return userDAO.deleteUser(userId);
    }
    
    // 禁用用户账户
    public boolean disableUser(int userId) {
        return userDAO.updateUserStatus(userId, 0);
    }
    
    // 启用用户账户
    public boolean enableUser(int userId) {
        return userDAO.updateUserStatus(userId, 1);
    }
    
    // 检查用户状态
    public boolean isUserActive(int userId) {
        User user = userDAO.getUserById(userId);
        return user != null && user.isActive();
    }
}