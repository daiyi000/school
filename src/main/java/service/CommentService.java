package service;

import dao.CommentDAO;
import model.Comment;
import utils.PaginationUtils;

import java.util.List;

public class CommentService {
    private CommentDAO commentDAO;
    
    public CommentService() {
        this.commentDAO = new CommentDAO();
    }
    
    // 添加评论
    public boolean addComment(Comment comment) {
        return commentDAO.addComment(comment);
    }
    
    // 获取帖子的所有评论
    public List<Comment> getCommentsByPostId(int postId) {
        return commentDAO.getCommentsByPostId(postId);
    }
    
 // 获取帖子评论的总数
    public int getTotalCommentsByPostId(int postId) {
        return commentDAO.getTotalCommentsByPostId(postId);
    }

 // 删除评论
    public boolean deleteComment(int commentId, int userId) {
        return commentDAO.deleteComment(commentId, userId);
    }
    
    // 获取帖子的评论（带分页）
    public List<Comment> getCommentsByPostIdWithPagination(int postId, int page, int pageSize) {
        int offset = (page - 1) * pageSize;
        return commentDAO.getCommentsByPostIdWithPagination(postId, offset, pageSize);
    }

    // 获取帖子的评论（带分页工具）
    public PaginationUtils<Comment> getCommentsByPostIdWithPaginationUtils(int postId, int page, int pageSize) {
        int totalComments = getTotalCommentsByPostId(postId);
        List<Comment> comments = getCommentsByPostIdWithPagination(postId, page, pageSize);
        return new PaginationUtils<>(comments, pageSize, page, totalComments);
    }
    
 // 管理员删除评论
    public boolean adminDeleteComment(int commentId) {
        return commentDAO.adminDeleteComment(commentId);
    }
}