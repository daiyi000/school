package dao;

import model.Comment;
import utils.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CommentDAO {
    
    // 添加评论
    public boolean addComment(Comment comment) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "INSERT INTO comments (post_id, user_id, content, create_time) VALUES (?, ?, ?, NOW())";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, comment.getPostId());
            stmt.setInt(2, comment.getUserId());
            stmt.setString(3, comment.getContent());
            
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
    
    // 获取帖子的所有评论
    public List<Comment> getCommentsByPostId(int postId) {
        List<Comment> comments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT c.*, u.username FROM comments c JOIN users u ON c.user_id = u.id WHERE c.post_id = ? ORDER BY c.create_time ASC";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, postId);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Comment comment = new Comment();
                comment.setId(rs.getInt("id"));
                comment.setPostId(rs.getInt("post_id"));
                comment.setUserId(rs.getInt("user_id"));
                comment.setUsername(rs.getString("username"));
                comment.setContent(rs.getString("content"));
                comment.setCreateTime(rs.getTimestamp("create_time"));
                comments.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return comments;
    }
    
 // 获取帖子评论的总数
    public int getTotalCommentsByPostId(int postId) {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT COUNT(*) FROM comments WHERE post_id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, postId);
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

    // 获取帖子的评论（带分页）
    public List<Comment> getCommentsByPostIdWithPagination(int postId, int offset, int limit) {
        List<Comment> comments = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT c.*, u.username FROM comments c " +
                         "JOIN users u ON c.user_id = u.id " +
                         "WHERE c.post_id = ? " +
                         "ORDER BY c.create_time ASC " +
                         "LIMIT ?, ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, postId);
            stmt.setInt(2, offset);
            stmt.setInt(3, limit);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Comment comment = new Comment();
                comment.setId(rs.getInt("id"));
                comment.setPostId(rs.getInt("post_id"));
                comment.setUserId(rs.getInt("user_id"));
                comment.setUsername(rs.getString("username"));
                comment.setContent(rs.getString("content"));
                comment.setCreateTime(rs.getTimestamp("create_time"));
                comments.add(comment);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return comments;
    }
    
 // 删除评论
    public boolean deleteComment(int commentId, int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBUtils.getConnection();
            
            // 首先检查评论是否存在且属于该用户
            String checkSql = "SELECT * FROM comments WHERE id = ? AND user_id = ?";
            stmt = conn.prepareStatement(checkSql);
            stmt.setInt(1, commentId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (!rs.next()) {
                // 评论不存在或不属于该用户
                return false;
            }
            
            // 删除评论
            String deleteSql = "DELETE FROM comments WHERE id = ? AND user_id = ?";
            stmt = conn.prepareStatement(deleteSql);
            stmt.setInt(1, commentId);
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
    
 // 管理员删除评论（不检查用户ID）
    public boolean adminDeleteComment(int commentId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBUtils.getConnection();
            
            // 首先检查评论是否存在
            String checkSql = "SELECT * FROM comments WHERE id = ?";
            stmt = conn.prepareStatement(checkSql);
            stmt.setInt(1, commentId);
            ResultSet rs = stmt.executeQuery();
            
            if (!rs.next()) {
                // 评论不存在
                return false;
            }
            
            // 删除评论
            String deleteSql = "DELETE FROM comments WHERE id = ?";
            stmt = conn.prepareStatement(deleteSql);
            stmt.setInt(1, commentId);
            
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