package dao;

import model.Post;
import utils.DBUtils;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PostDAO {
    
    // 添加帖子
    public boolean addPost(Post post) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "INSERT INTO posts (user_id, content, image_path, create_time) VALUES (?, ?, ?, NOW())";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, post.getUserId());
            stmt.setString(2, post.getContent());
            stmt.setString(3, post.getImagePath());
            
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
    
    // 获取所有帖子
    public List<Post> getAllPosts() {
        List<Post> posts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT p.*, u.username FROM posts p JOIN users u ON p.user_id = u.id ORDER BY p.create_time DESC";
            stmt = conn.prepareStatement(sql);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setUserId(rs.getInt("user_id"));
                post.setUsername(rs.getString("username"));
                post.setContent(rs.getString("content"));
                post.setImagePath(rs.getString("image_path"));
                post.setCreateTime(rs.getTimestamp("create_time"));
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return posts;
    }
    
    // 通过ID获取帖子
    public Post getPostById(int postId) {
        Post post = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT p.*, u.username FROM posts p JOIN users u ON p.user_id = u.id WHERE p.id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, postId);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                post = new Post();
                post.setId(rs.getInt("id"));
                post.setUserId(rs.getInt("user_id"));
                post.setUsername(rs.getString("username"));
                post.setContent(rs.getString("content"));
                post.setImagePath(rs.getString("image_path"));
                post.setCreateTime(rs.getTimestamp("create_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return post;
    }
    
    // 根据内容搜索帖子
    public List<Post> searchPostsByContent(String keyword) {
        List<Post> posts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT p.*, u.username FROM posts p " +
                         "JOIN users u ON p.user_id = u.id " +
                         "WHERE p.content LIKE ? " +
                         "ORDER BY p.create_time DESC";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setUserId(rs.getInt("user_id"));
                post.setUsername(rs.getString("username"));
                post.setContent(rs.getString("content"));
                post.setImagePath(rs.getString("image_path"));
                post.setCreateTime(rs.getTimestamp("create_time"));
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return posts;
    }
    
    // 获取帖子总数
    public int getTotalPosts() {
        int count = 0;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT COUNT(*) FROM posts";
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

    // 获取特定页的帖子
    public List<Post> getPostsByPage(int offset, int limit) {
        List<Post> posts = new ArrayList<>();
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT p.*, u.username FROM posts p JOIN users u ON p.user_id = u.id ORDER BY p.create_time DESC LIMIT ?, ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, offset);
            stmt.setInt(2, limit);
            rs = stmt.executeQuery();
            
            while (rs.next()) {
                Post post = new Post();
                post.setId(rs.getInt("id"));
                post.setUserId(rs.getInt("user_id"));
                post.setUsername(rs.getString("username"));
                post.setContent(rs.getString("content"));
                post.setImagePath(rs.getString("image_path"));
                post.setCreateTime(rs.getTimestamp("create_time"));
                posts.add(post);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return posts;
    }
    
    // 删除帖子
    public boolean deletePost(int postId, int userId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBUtils.getConnection();
            
            // 首先检查帖子是否存在且属于该用户
            String checkSql = "SELECT * FROM posts WHERE id = ? AND user_id = ?";
            stmt = conn.prepareStatement(checkSql);
            stmt.setInt(1, postId);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (!rs.next()) {
                // 帖子不存在或不属于该用户
                return false;
            }
            
            // 先删除帖子的所有评论
            String deleteCommentsSql = "DELETE FROM comments WHERE post_id = ?";
            stmt = conn.prepareStatement(deleteCommentsSql);
            stmt.setInt(1, postId);
            stmt.executeUpdate();
            
            // 再删除帖子
            String deletePostSql = "DELETE FROM posts WHERE id = ? AND user_id = ?";
            stmt = conn.prepareStatement(deletePostSql);
            stmt.setInt(1, postId);
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
    
 // 管理员删除帖子（不检查用户ID）
    public boolean adminDeletePost(int postId) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBUtils.getConnection();
            
            // 首先检查帖子是否存在
            String checkSql = "SELECT * FROM posts WHERE id = ?";
            stmt = conn.prepareStatement(checkSql);
            stmt.setInt(1, postId);
            ResultSet rs = stmt.executeQuery();
            
            if (!rs.next()) {
                // 帖子不存在
                return false;
            }
            
            // 先删除帖子的所有评论
            String deleteCommentsSql = "DELETE FROM comments WHERE post_id = ?";
            stmt = conn.prepareStatement(deleteCommentsSql);
            stmt.setInt(1, postId);
            stmt.executeUpdate();
            
            // 再删除帖子
            String deletePostSql = "DELETE FROM posts WHERE id = ?";
            stmt = conn.prepareStatement(deletePostSql);
            stmt.setInt(1, postId);
            
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