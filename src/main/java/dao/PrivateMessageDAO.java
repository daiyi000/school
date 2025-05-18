package dao;

import model.PrivateMessage;
import utils.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrivateMessageDAO {
    
    // 发送私信
    public boolean sendMessage(PrivateMessage message) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "INSERT INTO private_messages (sender_id, receiver_id, content, image_path, is_read, create_time) " +
                         "VALUES (?, ?, ?, ?, 0, NOW())";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, message.getSenderId());
            stmt.setInt(2, message.getReceiverId());
            stmt.setString(3, message.getContent());
            stmt.setString(4, message.getImagePath());
            
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
    
    // 获取用户收到的私信
    public List<PrivateMessage> getReceivedMessages(int userId) {
        List<PrivateMessage> messages = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT m.*, u.username AS sender_username, u2.username AS receiver_username " +
                         "FROM private_messages m " +
                         "JOIN users u ON m.sender_id = u.id " +
                         "JOIN users u2 ON m.receiver_id = u2.id " +
                         "WHERE m.receiver_id = ? " +
                         "ORDER BY m.create_time DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                PrivateMessage message = new PrivateMessage();
                message.setId(rs.getInt("id"));
                message.setSenderId(rs.getInt("sender_id"));
                message.setSenderUsername(rs.getString("sender_username"));
                message.setReceiverId(rs.getInt("receiver_id"));
                message.setReceiverUsername(rs.getString("receiver_username"));
                message.setContent(rs.getString("content"));
                message.setImagePath(rs.getString("image_path"));
                message.setIsRead(rs.getInt("is_read"));
                message.setCreateTime(rs.getTimestamp("create_time"));
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return messages;
    }
    
    // 获取用户发送的私信
    public List<PrivateMessage> getSentMessages(int userId) {
        List<PrivateMessage> messages = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT m.*, u.username AS sender_username, u2.username AS receiver_username " +
                         "FROM private_messages m " +
                         "JOIN users u ON m.sender_id = u.id " +
                         "JOIN users u2 ON m.receiver_id = u2.id " +
                         "WHERE m.sender_id = ? " +
                         "ORDER BY m.create_time DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                PrivateMessage message = new PrivateMessage();
                message.setId(rs.getInt("id"));
                message.setSenderId(rs.getInt("sender_id"));
                message.setSenderUsername(rs.getString("sender_username"));
                message.setReceiverId(rs.getInt("receiver_id"));
                message.setReceiverUsername(rs.getString("receiver_username"));
                message.setContent(rs.getString("content"));
                message.setImagePath(rs.getString("image_path"));
                message.setIsRead(rs.getInt("is_read"));
                message.setCreateTime(rs.getTimestamp("create_time"));
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return messages;
    }
    
    // 获取用户与特定好友的对话
    public List<PrivateMessage> getConversation(int userId, int friendId) {
        List<PrivateMessage> messages = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT m.*, u.username AS sender_username, u2.username AS receiver_username " +
                         "FROM private_messages m " +
                         "JOIN users u ON m.sender_id = u.id " +
                         "JOIN users u2 ON m.receiver_id = u2.id " +
                         "WHERE (m.sender_id = ? AND m.receiver_id = ?) OR (m.sender_id = ? AND m.receiver_id = ?) " +
                         "ORDER BY m.create_time ASC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            stmt.setInt(3, friendId);
            stmt.setInt(4, userId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                PrivateMessage message = new PrivateMessage();
                message.setId(rs.getInt("id"));
                message.setSenderId(rs.getInt("sender_id"));
                message.setSenderUsername(rs.getString("sender_username"));
                message.setReceiverId(rs.getInt("receiver_id"));
                message.setReceiverUsername(rs.getString("receiver_username"));
                message.setContent(rs.getString("content"));
                message.setImagePath(rs.getString("image_path"));
                message.setIsRead(rs.getInt("is_read"));
                message.setCreateTime(rs.getTimestamp("create_time"));
                messages.add(message);
            }
            
            // 把未读消息标记为已读
            String updateSql = "UPDATE private_messages SET is_read = 1 " +
                              "WHERE sender_id = ? AND receiver_id = ? AND is_read = 0";
            stmt = conn.prepareStatement(updateSql);
            stmt.setInt(1, friendId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return messages;
    }
    
    // 获取未读消息数量
    public int getUnreadMessageCount(int userId) {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT COUNT(*) FROM private_messages WHERE receiver_id = ? AND is_read = 0";
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
    
    // 获取用户收到的私信总数
    public int getTotalReceivedMessages(int userId) {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT COUNT(*) FROM private_messages WHERE receiver_id = ?";
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

    // 获取用户发送的私信总数
    public int getTotalSentMessages(int userId) {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT COUNT(*) FROM private_messages WHERE sender_id = ?";
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

    // 获取用户收到的私信（带分页）
    public List<PrivateMessage> getReceivedMessagesWithPagination(int userId, int offset, int limit) {
        List<PrivateMessage> messages = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT m.*, u.username AS sender_username, u2.username AS receiver_username " +
                         "FROM private_messages m " +
                         "JOIN users u ON m.sender_id = u.id " +
                         "JOIN users u2 ON m.receiver_id = u2.id " +
                         "WHERE m.receiver_id = ? " +
                         "ORDER BY m.create_time DESC " +
                         "LIMIT ?, ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, offset);
            stmt.setInt(3, limit);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                PrivateMessage message = new PrivateMessage();
                message.setId(rs.getInt("id"));
                message.setSenderId(rs.getInt("sender_id"));
                message.setSenderUsername(rs.getString("sender_username"));
                message.setReceiverId(rs.getInt("receiver_id"));
                message.setReceiverUsername(rs.getString("receiver_username"));
                message.setContent(rs.getString("content"));
                message.setImagePath(rs.getString("image_path"));
                message.setIsRead(rs.getInt("is_read"));
                message.setCreateTime(rs.getTimestamp("create_time"));
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return messages;
    }

    // 获取用户发送的私信（带分页）
    public List<PrivateMessage> getSentMessagesWithPagination(int userId, int offset, int limit) {
        List<PrivateMessage> messages = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT m.*, u.username AS sender_username, u2.username AS receiver_username " +
                         "FROM private_messages m " +
                         "JOIN users u ON m.sender_id = u.id " +
                         "JOIN users u2 ON m.receiver_id = u2.id " +
                         "WHERE m.sender_id = ? " +
                         "ORDER BY m.create_time DESC " +
                         "LIMIT ?, ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, offset);
            stmt.setInt(3, limit);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                PrivateMessage message = new PrivateMessage();
                message.setId(rs.getInt("id"));
                message.setSenderId(rs.getInt("sender_id"));
                message.setSenderUsername(rs.getString("sender_username"));
                message.setReceiverId(rs.getInt("receiver_id"));
                message.setReceiverUsername(rs.getString("receiver_username"));
                message.setContent(rs.getString("content"));
                message.setImagePath(rs.getString("image_path"));
                message.setIsRead(rs.getInt("is_read"));
                message.setCreateTime(rs.getTimestamp("create_time"));
                messages.add(message);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return messages;
    }

    // 获取用户与特定好友的对话总数
    public int getTotalConversationMessages(int userId, int friendId) {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT COUNT(*) FROM private_messages " +
                         "WHERE (sender_id = ? AND receiver_id = ?) OR (sender_id = ? AND receiver_id = ?)";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            stmt.setInt(3, friendId);
            stmt.setInt(4, userId);
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

    // 获取用户与特定好友的对话（带分页）
    public List<PrivateMessage> getConversationWithPagination(int userId, int friendId, int offset, int limit) {
        List<PrivateMessage> messages = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT m.*, u.username AS sender_username, u2.username AS receiver_username " +
                         "FROM private_messages m " +
                         "JOIN users u ON m.sender_id = u.id " +
                         "JOIN users u2 ON m.receiver_id = u2.id " +
                         "WHERE (m.sender_id = ? AND m.receiver_id = ?) OR (m.sender_id = ? AND m.receiver_id = ?) " +
                         "ORDER BY m.create_time ASC " +
                         "LIMIT ?, ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, friendId);
            stmt.setInt(3, friendId);
            stmt.setInt(4, userId);
            stmt.setInt(5, offset);
            stmt.setInt(6, limit);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                PrivateMessage message = new PrivateMessage();
                message.setId(rs.getInt("id"));
                message.setSenderId(rs.getInt("sender_id"));
                message.setSenderUsername(rs.getString("sender_username"));
                message.setReceiverId(rs.getInt("receiver_id"));
                message.setReceiverUsername(rs.getString("receiver_username"));
                message.setContent(rs.getString("content"));
                message.setImagePath(rs.getString("image_path"));
                message.setIsRead(rs.getInt("is_read"));
                message.setCreateTime(rs.getTimestamp("create_time"));
                messages.add(message);
            }
            
            // 把未读消息标记为已读
            String updateSql = "UPDATE private_messages SET is_read = 1 " +
                              "WHERE sender_id = ? AND receiver_id = ? AND is_read = 0";
            stmt = conn.prepareStatement(updateSql);
            stmt.setInt(1, friendId);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
            
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return messages;
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