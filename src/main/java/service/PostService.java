package service;

import dao.PostDAO;
import model.Post;

import java.util.List;

public class PostService {
    private PostDAO postDAO;
    
    public PostService() {
        this.postDAO = new PostDAO();
    }
    
    // 发帖
    public boolean addPost(Post post) {
        return postDAO.addPost(post);
    }
    
    // 获取所有帖子
    public List<Post> getAllPosts() {
        return postDAO.getAllPosts();
    }
    
    // 通过ID获取帖子
    public Post getPostById(int postId) {
        return postDAO.getPostById(postId);
    }
    
 // 根据内容搜索帖子
    public List<Post> searchPostsByContent(String keyword) {
        return postDAO.searchPostsByContent(keyword);
    }
    
 // 获取帖子总数
    public int getTotalPosts() {
        return postDAO.getTotalPosts();
    }

    // 获取特定页的帖子
    public List<Post> getPostsByPage(int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return postDAO.getPostsByPage(offset, pageSize);
    }
    
 // 删除帖子
    public boolean deletePost(int postId, int userId) {
        return postDAO.deletePost(postId, userId);
    }
    
 // 管理员删除帖子
    public boolean adminDeletePost(int postId) {
        return postDAO.adminDeletePost(postId);
    }
}