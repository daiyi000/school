package service;

import dao.AdministratorDAO;
import model.Administrator;

public class AdministratorService {
    private AdministratorDAO administratorDAO;
    
    public AdministratorService() {
        this.administratorDAO = new AdministratorDAO();
    }
    
    // 管理员登录
    public Administrator login(String username, String password) {
        return administratorDAO.login(username, password);
    }
    
 // 修改管理员密码
    public boolean updatePassword(int adminId, String oldPassword, String newPassword) {
        // 先验证旧密码是否正确
        if (!administratorDAO.verifyPassword(adminId, oldPassword)) {
            return false;
        }
        
        // 旧密码正确，更新为新密码
        return administratorDAO.updatePassword(adminId, newPassword);
    }
}