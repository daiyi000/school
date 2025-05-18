package dao;

import model.Administrator;
import utils.DBUtils;

import java.sql.*;

public class AdministratorDAO {
    
    // 管理员登录验证
    public Administrator login(String username, String password) {
        Administrator admin = null;
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT * FROM administrators WHERE username = ? AND password = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, username);
            stmt.setString(2, password);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                admin = new Administrator();
                admin.setId(rs.getInt("id"));
                admin.setUsername(rs.getString("username"));
                admin.setPassword(rs.getString("password"));
                admin.setCreateTime(rs.getTimestamp("create_time"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return admin;
    }
    
    // 修改管理员密码
    public boolean updatePassword(int adminId, String newPassword) {
        Connection conn = null;
        PreparedStatement stmt = null;
        boolean success = false;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "UPDATE administrators SET password = ? WHERE id = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, newPassword);
            stmt.setInt(2, adminId);
            
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
    
    // 验证旧密码
    public boolean verifyPassword(int adminId, String oldPassword) {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        boolean isValid = false;
        
        try {
            conn = DBUtils.getConnection();
            String sql = "SELECT * FROM administrators WHERE id = ? AND password = ?";
            stmt = conn.prepareStatement(sql);
            stmt.setInt(1, adminId);
            stmt.setString(2, oldPassword);
            rs = stmt.executeQuery();
            
            if (rs.next()) {
                isValid = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            closeResources(conn, stmt, rs);
        }
        
        return isValid;
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