package utils;

import java.util.ArrayList;
import java.util.List;

public class PaginationUtils<T> {
    private List<T> items; // 当前页的数据项
    private int currentPage; // 当前页码
    private int pageSize; // 每页显示的数量
    private int totalItems; // 总数据项数量
    private int totalPages; // 总页数
    
    // 从完整列表创建分页
    public PaginationUtils(List<T> allItems, int pageSize, int currentPage) {
        this.pageSize = pageSize;
        this.totalItems = allItems.size();
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
        
        // 确保当前页在有效范围内
        this.currentPage = Math.max(1, Math.min(currentPage, totalPages > 0 ? totalPages : 1));
        
        // 计算当前页的起始和结束索引
        int startIndex = (this.currentPage - 1) * pageSize;
        int endIndex = Math.min(startIndex + pageSize, totalItems);
        
        // 获取当前页的数据
        if (startIndex < totalItems) {
            this.items = allItems.subList(startIndex, endIndex);
        } else {
            // 如果当前页没有数据，返回空列表
            this.items = new ArrayList<>();
        }
    }
    
    // 从分页查询结果创建分页
    public PaginationUtils(List<T> pageItems, int pageSize, int currentPage, int totalItems) {
        this.items = pageItems;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
        this.totalPages = (int) Math.ceil((double) totalItems / pageSize);
        this.currentPage = Math.max(1, Math.min(currentPage, totalPages > 0 ? totalPages : 1));
    }
    
    // 获取当前页的数据项
    public List<T> getItems() {
        return items;
    }
    
    // 获取当前页码
    public int getCurrentPage() {
        return currentPage;
    }
    
    // 获取每页显示的数量
    public int getPageSize() {
        return pageSize;
    }
    
    // 获取总数据项数量
    public int getTotalItems() {
        return totalItems;
    }
    
    // 获取总页数
    public int getTotalPages() {
        return totalPages;
    }
    
    // 检查是否有上一页
    public boolean hasPreviousPage() {
        return currentPage > 1;
    }
    
    // 检查是否有下一页
    public boolean hasNextPage() {
        return currentPage < totalPages;
    }
    
    // 获取上一页的页码
    public int getPreviousPage() {
        return Math.max(1, currentPage - 1);
    }
    
    // 获取下一页的页码
    public int getNextPage() {
        return Math.min(totalPages, currentPage + 1);
    }
    
    // 生成分页导航的HTML代码
    public String generatePaginationHTML(String baseUrl) {
        StringBuilder html = new StringBuilder();
        html.append("<div class=\"pagination\" style=\"margin: 20px 0; text-align: center;\">");
        
        // 上一页按钮
        if (hasPreviousPage()) {
            html.append("<a href=\"").append(baseUrl)
                .append(baseUrl.contains("?") ? "&" : "?")
                .append("page=").append(getPreviousPage())
                .append("\" style=\"margin: 0 5px;\">上一页</a>");
        } else {
            html.append("<span style=\"margin: 0 5px; color: #ccc;\">上一页</span>");
        }
        
        // 页码链接
        int startPage = Math.max(1, currentPage - 2);
        int endPage = Math.min(totalPages, currentPage + 2);
        
        for (int i = startPage; i <= endPage; i++) {
            if (i == currentPage) {
                html.append("<span style=\"margin: 0 5px; font-weight: bold;\">").append(i).append("</span>");
            } else {
                html.append("<a href=\"").append(baseUrl)
                    .append(baseUrl.contains("?") ? "&" : "?")
                    .append("page=").append(i)
                    .append("\" style=\"margin: 0 5px;\">").append(i).append("</a>");
            }
        }
        
        // 下一页按钮
        if (hasNextPage()) {
            html.append("<a href=\"").append(baseUrl)
                .append(baseUrl.contains("?") ? "&" : "?")
                .append("page=").append(getNextPage())
                .append("\" style=\"margin: 0 5px;\">下一页</a>");
        } else {
            html.append("<span style=\"margin: 0 5px; color: #ccc;\">下一页</span>");
        }
        
        html.append("</div>");
        return html.toString();
    }
}