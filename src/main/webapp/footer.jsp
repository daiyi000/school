<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<link rel="stylesheet" href="${pageContext.request.contextPath}/css/footer.css">

<footer class="footer">
    <div class="footer-container">
        <div class="footer-content">
            <div class="footer-section">
                <h3>关于我们</h3>
                <p>社交系统是一个简单的在线社交平台，让用户能够分享生活、交流想法、结交朋友。</p>
                <p>本系统提供发帖、评论、私信、好友管理等功能，为用户提供便捷的社交体验。</p>
            </div>
            
            <div class="footer-section">
                <h3>快速链接</h3>
                <ul>
                    <li><a href="hall">首页</a></li>
                    <li><a href="profile">个人中心</a></li>
                    <li><a href="friends">好友管理</a></li>
                    <li><a href="messages">私信</a></li>
                    <li><a href="search">搜索</a></li>
                </ul>
            </div>
            
            <div class="footer-section">
                <h3>联系我们</h3>
                <p>邮箱: support@socialsystem.com</p>
                <p>电话: 123-456-7890</p>
                <p>地址: 中国北京市海淀区</p>
            </div>
        </div>
        
        <div class="footer-bottom">
            <p>&copy; <%= new java.text.SimpleDateFormat("yyyy").format(new java.util.Date()) %> 社交系统 - 版权所有</p>
            <div class="social-links">
                <a href="#" title="微信">微信</a>
                <a href="#" title="微博">微博</a>
                <a href="#" title="QQ">QQ</a>
            </div>
        </div>
    </div>
</footer>