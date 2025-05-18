package model;

import java.util.Date;

public class Notification {
    private int id;
    private int userId;
    private String type; // 通知类型: "system", "friend_request", "message", "comment"
    private String content;
    private int relatedId; // 关联的内容ID（如好友请求ID、私信ID、评论ID等）
    private int isRead; // 0: 未读, 1: 已读
    private Date createTime;
    
    // 构造函数
    public Notification() {}
    
    public Notification(int userId, String type, String content, int relatedId) {
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.relatedId = relatedId;
        this.isRead = 0; // 默认为未读
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
    
    public String getType() {
        return type;
    }
    
    public void setType(String type) {
        this.type = type;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public int getRelatedId() {
        return relatedId;
    }
    
    public void setRelatedId(int relatedId) {
        this.relatedId = relatedId;
    }
    
    public int getIsRead() {
        return isRead;
    }
    
    public void setIsRead(int isRead) {
        this.isRead = isRead;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }
}