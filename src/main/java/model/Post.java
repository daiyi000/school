package model;

import java.util.Date;

public class Post {
    private int id;
    private int userId;
    private String username; // 存储发帖用户的用户名，方便显示
    private String content;
    private Date createTime;
    private String imagePath;
    
    // 构造函数
    public Post() {}
    
    public Post(int userId, String content) {
        this.userId = userId;
        this.content = content;
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
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public Date getCreateTime() {
        return createTime;
    }
    
    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

	public String getImagePath() {
		return imagePath;
	}

	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}
    
    
}