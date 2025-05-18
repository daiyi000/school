package model;

import java.util.Date;

public class Friendship {
    private int id;
    private int userId;
    private int friendId;
    private String friendUsername; // 存储好友的用户名，方便显示
    private int status; // 0: 请求中, 1: 已接受
    private Date createTime;

    // 构造函数
    public Friendship() {}
    
    public Friendship(int userId, int friendId, int status) {
        this.userId = userId;
        this.friendId = friendId;
        this.status = status;
    }
    
    // getter和setter方法
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getUserId() {
        return userId;
    }
    
    public void setUserId(int userId) {
        this.userId = userId;
    }
    
    public int getFriendId() {
        return friendId;
    }
    
    public void setFriendId(int friendId) {
        this.friendId = friendId;
    }
    
    public String getFriendUsername() {
        return friendUsername;
    }
    
    public void setFriendUsername(String friendUsername) {
        this.friendUsername = friendUsername;
    }
    
    public int getStatus() {
        return status;
    }
    
    public void setStatus(int status) {
        this.status = status;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}