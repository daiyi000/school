package dao;

import model.User;
import utils.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UserDAO {
    
    // 用户登录验证
    public User login(String username, String password) {
        User user = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setBio(rs.getString("bio"));
                user.setRegisterTime(rs.getString("register_time"));
                user.setStatus(rs.getInt("status")); // 获取用户状态
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return user;
    }
    
    // 用户注册
    public boolean register(User user) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "INSERT INTO users (username, password, email, status, register_time) VALUES (?, ?, ?, 1, NOW())";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getUsername());
            stmt.setString(2, user.getPassword());
            stmt.setString(3, user.getEmail());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, null);
        }
        
        return success;
    }
    
    // 检查用户名是否已存在
    public boolean isUsernameExists(String username) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean exists = false;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT COUNT(*) FROM users WHERE username = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            rs = stmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                exists = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return exists;
    }
    
    // 获取用户信息
    public User getUserById(int userId) {
        User user = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT * FROM users WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setBio(rs.getString("bio"));
                user.setRegisterTime(rs.getString("register_time"));
                user.setStatus(rs.getInt("status")); // 获取用户状态
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return user;
    }
    
    // 更新用户信息
    public boolean updateProfile(User user) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "UPDATE users SET email = ?, bio = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getEmail());
            stmt.setString(2, user.getBio());
            stmt.setInt(3, user.getId());
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, null);
        }
        
        return success;
    }
    
    // 根据用户名搜索用户
    public List<User> searchUsersByUsername(String username) {
        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT * FROM users WHERE username LIKE ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + username + "%");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setEmail(rs.getString("email"));
                user.setBio(rs.getString("bio"));
                user.setRegisterTime(rs.getString("register_time"));
                user.setStatus(rs.getInt("status")); // 获取用户状态
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return users;
    }
    
    // 获取所有用户
    public List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT * FROM users ORDER BY id";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setBio(rs.getString("bio"));
                user.setRegisterTime(rs.getString("register_time"));
                user.setStatus(rs.getInt("status")); // 获取用户状态
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return users;
    }
    
    // 获取用户总数
    public int getTotalUsers() {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT COUNT(*) FROM users";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return count;
    }
    
    // 分页获取用户
    public List<User> getAllUsersWithPagination(int offset, int limit) {
        List<User> users = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT * FROM users ORDER BY id LIMIT ?, ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, offset);
            stmt.setInt(2, limit);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                User user = new User();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setPassword(rs.getString("password"));
                user.setEmail(rs.getString("email"));
                user.setBio(rs.getString("bio"));
                user.setRegisterTime(rs.getString("register_time"));
                user.setStatus(rs.getInt("status")); // 获取用户状态
                users.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return users;
    }
    
    // 删除用户账户
    public boolean deleteUser(int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBUtils.getConnection();
            conn.setAutoCommit(false); // 开始事务
            
            // 先删除该用户的所有评论
            String deleteCommentsSql = "DELETE FROM comments WHERE user_id = ?";
            stmt = conn.prepareStatement(deleteCommentsSql);
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            
            // 删除该用户的所有帖子
            String deletePostsSql = "DELETE FROM posts WHERE user_id = ?";
            stmt = conn.prepareStatement(deletePostsSql);
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            
            // 删除该用户的所有好友关系
            String deleteFriendshipsSql = "DELETE FROM friendships WHERE user_id = ? OR friend_id = ?";
            stmt = conn.prepareStatement(deleteFriendshipsSql);
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            
            // 删除该用户的所有私信
            String deleteMessagesSql = "DELETE FROM private_messages WHERE sender_id = ? OR receiver_id = ?";
            stmt = conn.prepareStatement(deleteMessagesSql);
            stmt.setInt(1, userId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            
            // 删除该用户的所有通知
            String deleteNotificationsSql = "DELETE FROM notifications WHERE user_id = ?";
            stmt = conn.prepareStatement(deleteNotificationsSql);
            stmt.setInt(1, userId);
            stmt.executeUpdate();
            
            // 最后删除用户本身
            String deleteUserSql = "DELETE FROM users WHERE id = ?";
            stmt = conn.prepareStatement(deleteUserSql);
            stmt.setInt(1, userId);
            int rowsAffected = stmt.executeUpdate();
            
            if (rowsAffected > 0) {
                conn.commit(); // 提交事务
                success = true;
            } else {
                conn.rollback(); // 回滚事务
            }
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback(); // 发生异常时回滚事务
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true); // 恢复自动提交
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            closeResources(conn, stmt, null);
        }
        
        return success;
    }
    
    // 禁用/启用用户账户
    public boolean updateUserStatus(int userId, int status) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "UPDATE users SET status = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, status);
            stmt.setInt(2, userId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, null);
        }
        
        return success;
    }
    
    // 关闭资源
    private void closeResources(Connection conn, Statement stmt, ResultSet rs) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}