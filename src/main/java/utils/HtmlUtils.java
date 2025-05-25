package utils;

public class HtmlUtils {
    
    /**
     * 将文本中的换行符转换为HTML的<br>标签
     * @param text 原始文本
     * @return 转换后的HTML文本
     */
    public static String nl2br(String text) {
        if (text == null) {
            return "";
        }
        // 替换所有的换行符为<br>标签
        return text.replaceAll("\\n", "<br>");
    }
    
    /**
     * 转义HTML特殊字符并将换行符转换为<br>标签
     * @param text 原始文本
     * @return 转换后的HTML文本
     */
    public static String escapeHtmlAndNl2br(String text) {
        if (text == null) {
            return "";
        }
        // 先转义HTML特殊字符
        String escaped = escapeHtml(text);
        // 再将换行符转换为<br>标签
        return escaped.replaceAll("\\n", "<br>");
    }
    
    /**
     * 转义HTML特殊字符
     * @param text 原始文本
     * @return 转义后的文本
     */
    public static String escapeHtml(String text) {
        if (text == null) {
            return "";
        }
        // 替换HTML特殊字符
        return text.replaceAll("&", "&amp;")
                  .replaceAll("<", "&lt;")
                  .replaceAll(">", "&gt;")
                  .replaceAll("\"", "&quot;")
                  .replaceAll("'", "&#39;");
    }
}