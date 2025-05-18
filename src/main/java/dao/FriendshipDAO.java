package dao;

import model.Friendship;
import model.User;
import utils.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class FriendshipDAO {
    
    // 发送好友请求
    public boolean sendFriendRequest(int userId, int friendId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBUtils.getConnection();
            
            // 检查是否已存在好友关系
            String checkSql = "SELECT COUNT(*) FROM friendships WHERE (user_id = ? AND friend_id = ?) OR (user_id = ? AND friend_id = ?)";
            stmt = conn.prepareStatement(checkSql);
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            stmt.setInt(3, friendId);
            stmt.setInt(4, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                // 已存在好友关系或请求
                return false;
            }
            
            // 创建好友请求
            String sql = "INSERT INTO friendships (user_id, friend_id, status, create_time) VALUES (?, ?, 0, NOW())";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            
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
    
    // 接受好友请求
    public boolean acceptFriendRequest(int requestId, int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBUtils.getConnection();
            
            // 检查请求是否存在且属于当前用户
            String checkSql = "SELECT * FROM friendships WHERE id = ? AND friend_id = ? AND status = 0";
            stmt = conn.prepareStatement(checkSql);
            stmt.setInt(1, requestId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (!rs.next()) {
                // 请求不存在或不属于当前用户
                return false;
            }
            
            // 更新请求状态为已接受
            String sql = "UPDATE friendships SET status = 1 WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, requestId);
            
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                // 创建反向好友关系（因为好友关系是双向的）
                int friendId = rs.getInt("user_id");
                String insertSql = "INSERT INTO friendships (user_id, friend_id, status, create_time) VALUES (?, ?, 1, NOW())";
                stmt = conn.prepareStatement(insertSql);
                stmt.setInt(1, userId);
                stmt.setInt(2, friendId);
                stmt.executeUpdate();
                
                success = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, null);
        }
        
        return success;
    }
    
    // 拒绝好友请求
    public boolean rejectFriendRequest(int requestId, int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBUtils.getConnection();
            
            // 检查请求是否存在且属于当前用户
            String checkSql = "SELECT * FROM friendships WHERE id = ? AND friend_id = ? AND status = 0";
            stmt = conn.prepareStatement(checkSql);
            stmt.setInt(1, requestId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (!rs.next()) {
                // 请求不存在或不属于当前用户
                return false;
            }
            
            // 删除请求
            String sql = "DELETE FROM friendships WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, requestId);
            
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
    
    // 获取用户的好友列表
    public List<User> getFriendsByUserId(int userId) {
        List<User> friends = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT u.* FROM friendships f JOIN users u ON f.friend_id = u.id " +
                         "WHERE f.user_id = ? AND f.status = 1";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                User friend = new User();
                friend.setId(rs.getInt("id"));
                friend.setUsername(rs.getString("username"));
                friend.setEmail(rs.getString("email"));
                friend.setBio(rs.getString("bio"));
                friends.add(friend);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return friends;
    }
    
    // 获取用户收到的好友请求
    public List<Friendship> getFriendRequestsByUserId(int userId) {
        List<Friendship> requests = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT f.*, u.username AS friend_username FROM friendships f " +
                         "JOIN users u ON f.user_id = u.id " +
                         "WHERE f.friend_id = ? AND f.status = 0";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Friendship request = new Friendship();
                request.setId(rs.getInt("id"));
                request.setUserId(rs.getInt("user_id"));
                request.setFriendId(rs.getInt("friend_id"));
                request.setFriendUsername(rs.getString("friend_username"));
                request.setStatus(rs.getInt("status"));
                request.setCreateTime(rs.getTimestamp("create_time"));
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return requests;
    }
    
 // 删除好友关系
    public boolean deleteFriendship(int userId, int friendId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBUtils.getConnection();
            
            // 开始事务
            conn.setAutoCommit(false);
            
            // 删除第一个方向的好友关系
            String sql1 = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ? AND status = 1";
            stmt = conn.prepareStatement(sql1);
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            int rowsAffected1 = stmt.executeUpdate();
            
            // 删除反向的好友关系
            String sql2 = "DELETE FROM friendships WHERE user_id = ? AND friend_id = ? AND status = 1";
            stmt = conn.prepareStatement(sql2);
            stmt.setInt(1, friendId);
            stmt.setInt(2, userId);
            int rowsAffected2 = stmt.executeUpdate();
            
            // 两个方向都删除成功，提交事务
            if (rowsAffected1 > 0 && rowsAffected2 > 0) {
                conn.commit();
                success = true;
            } else {
                // 如果有任何一个方向删除失败，回滚事务
                conn.rollback();
            }
            
            // 恢复自动提交
            conn.setAutoCommit(true);
            
        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                    conn.setAutoCommit(true);
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, null);
        }
        
        return success;
    }
    
 // 获取用户的好友总数
    public int getTotalFriendCount(int userId) {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT COUNT(*) FROM friendships WHERE user_id = ? AND status = 1";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
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

    // 获取用户的好友列表（带分页）
    public List<User> getFriendsByUserIdWithPagination(int userId, int offset, int limit) {
        List<User> friends = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT u.* FROM friendships f JOIN users u ON f.friend_id = u.id " +
                         "WHERE f.user_id = ? AND f.status = 1 " +
                         "ORDER BY u.username " +
                         "LIMIT ?, ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, offset);
            stmt.setInt(3, limit);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                User friend = new User();
                friend.setId(rs.getInt("id"));
                friend.setUsername(rs.getString("username"));
                friend.setEmail(rs.getString("email"));
                friend.setBio(rs.getString("bio"));
                friend.setRegisterTime(rs.getString("register_time"));
                friends.add(friend);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return friends;
    }

    // 获取用户收到的好友请求总数
    public int getTotalFriendRequestCount(int userId) {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT COUNT(*) FROM friendships WHERE friend_id = ? AND status = 0";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
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

    // 获取用户收到的好友请求（带分页）
    public List<Friendship> getFriendRequestsByUserIdWithPagination(int userId, int offset, int limit) {
        List<Friendship> requests = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT f.*, u.username AS friend_username FROM friendships f " +
                         "JOIN users u ON f.user_id = u.id " +
                         "WHERE f.friend_id = ? AND f.status = 0 " +
                         "ORDER BY f.create_time DESC " +
                         "LIMIT ?, ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, offset);
            stmt.setInt(3, limit);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Friendship request = new Friendship();
                request.setId(rs.getInt("id"));
                request.setUserId(rs.getInt("user_id"));
                request.setFriendId(rs.getInt("friend_id"));
                request.setFriendUsername(rs.getString("friend_username"));
                request.setStatus(rs.getInt("status"));
                request.setCreateTime(rs.getTimestamp("create_time"));
                requests.add(request);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return requests;
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