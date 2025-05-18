package model;

import java.util.Date;

public class PrivateMessage {
    private int id;
    private int senderId;
    private String senderUsername; // 存储发送者的用户名，方便显示
    private int receiverId;
    private String receiverUsername; // 存储接收者的用户名，方便显示
    private String content;
    private int isRead; // 0: 未读, 1: 已读
    private Date createTime;
    
    // 构造函数
    public PrivateMessage() {}
    
    public PrivateMessage(int senderId, int receiverId, String content) {
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.content = content;
        this.isRead = 0; // 默认为未读
    }
    
    // getter和setter方法
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getSenderId() {
        return senderId;
    }
    
    public void setSenderId(int senderId) {
        this.senderId = senderId;
    }
    
    public String getSenderUsername() {
        return senderUsername;
    }
    
    public void setSenderUsername(String senderUsername) {
        this.senderUsername = senderUsername;
    }
    
    public int getReceiverId() {
        return receiverId;
    }
    
    public void setReceiverId(int receiverId) {
        this.receiverId = receiverId;
    }
    
    public String getReceiverUsername() {
        return receiverUsername;
    }
    
    public void setReceiverUsername(String receiverUsername) {
        this.receiverUsername = receiverUsername;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
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