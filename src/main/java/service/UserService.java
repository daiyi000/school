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
}