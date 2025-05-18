/*
 Navicat Premium Data Transfer

 Source Server         : 1
 Source Server Type    : MySQL
 Source Server Version : 80031 (8.0.31)
 Source Host           : localhost:3306
 Source Schema         : social_system

 Target Server Type    : MySQL
 Target Server Version : 80031 (8.0.31)
 File Encoding         : 65001

 Date: 18/05/2025 18:20:49
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for administrators
-- ----------------------------
DROP TABLE IF EXISTS `administrators`;
CREATE TABLE `administrators`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of administrators
-- ----------------------------
INSERT INTO `administrators` VALUES (1, 'admin', 'admin', '2025-05-18 15:46:31');

-- ----------------------------
-- Table structure for comments
-- ----------------------------
DROP TABLE IF EXISTS `comments`;
CREATE TABLE `comments`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `user_id` int NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `post_id`(`post_id` ASC) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `comments_ibfk_1` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `comments_ibfk_2` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 33 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of comments
-- ----------------------------

-- ----------------------------
-- Table structure for friendships
-- ----------------------------
DROP TABLE IF EXISTS `friendships`;
CREATE TABLE `friendships`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `friend_id` int NOT NULL,
  `status` int NOT NULL DEFAULT 0,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  INDEX `friend_id`(`friend_id` ASC) USING BTREE,
  CONSTRAINT `friendships_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `friendships_ibfk_2` FOREIGN KEY (`friend_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 26 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of friendships
-- ----------------------------
INSERT INTO `friendships` VALUES (4, 1, 2, 1, '2025-05-17 23:44:09');
INSERT INTO `friendships` VALUES (5, 2, 1, 1, '2025-05-17 23:44:26');
INSERT INTO `friendships` VALUES (6, 2, 3, 1, '2025-05-17 23:45:34');
INSERT INTO `friendships` VALUES (7, 1, 3, 1, '2025-05-17 23:46:03');
INSERT INTO `friendships` VALUES (8, 1, 4, 1, '2025-05-17 23:46:06');
INSERT INTO `friendships` VALUES (9, 1, 5, 1, '2025-05-17 23:46:08');
INSERT INTO `friendships` VALUES (10, 3, 2, 1, '2025-05-17 23:46:23');
INSERT INTO `friendships` VALUES (11, 3, 1, 1, '2025-05-17 23:46:24');
INSERT INTO `friendships` VALUES (12, 4, 1, 1, '2025-05-17 23:46:30');
INSERT INTO `friendships` VALUES (13, 5, 1, 1, '2025-05-17 23:46:41');
INSERT INTO `friendships` VALUES (14, 1, 6, 1, '2025-05-18 11:08:04');
INSERT INTO `friendships` VALUES (15, 1, 7, 1, '2025-05-18 11:08:06');
INSERT INTO `friendships` VALUES (16, 1, 8, 1, '2025-05-18 11:08:07');
INSERT INTO `friendships` VALUES (17, 1, 9, 1, '2025-05-18 11:08:09');
INSERT INTO `friendships` VALUES (18, 1, 10, 1, '2025-05-18 11:08:11');
INSERT INTO `friendships` VALUES (19, 6, 1, 1, '2025-05-18 11:08:22');
INSERT INTO `friendships` VALUES (20, 7, 1, 1, '2025-05-18 11:08:31');
INSERT INTO `friendships` VALUES (21, 8, 1, 1, '2025-05-18 11:08:40');
INSERT INTO `friendships` VALUES (22, 9, 1, 1, '2025-05-18 11:08:49');
INSERT INTO `friendships` VALUES (23, 10, 1, 1, '2025-05-18 11:08:59');
INSERT INTO `friendships` VALUES (24, 2, 10, 1, '2025-05-18 14:00:16');
INSERT INTO `friendships` VALUES (25, 10, 2, 1, '2025-05-18 14:00:41');

-- ----------------------------
-- Table structure for notifications
-- ----------------------------
DROP TABLE IF EXISTS `notifications`;
CREATE TABLE `notifications`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `type` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `related_id` int NOT NULL DEFAULT 0,
  `is_read` tinyint NOT NULL DEFAULT 0,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `notifications_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 68 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of notifications
-- ----------------------------
INSERT INTO `notifications` VALUES (1, 1, 'friend_request', '3 发送了好友请求', 3, 1, '2025-05-17 22:58:30');
INSERT INTO `notifications` VALUES (2, 2, 'comment', '3 评论了你的帖子', 3, 1, '2025-05-17 22:58:38');
INSERT INTO `notifications` VALUES (3, 3, 'system', '1 拒绝了您的好友请求。', 0, 1, '2025-05-17 22:58:51');
INSERT INTO `notifications` VALUES (4, 3, 'comment', '1 评论了你的帖子', 6, 0, '2025-05-17 23:14:02');
INSERT INTO `notifications` VALUES (5, 3, 'comment', '1 评论了你的帖子', 18, 0, '2025-05-17 23:37:05');
INSERT INTO `notifications` VALUES (6, 2, 'system', '1 已将您从好友列表中移除。', 0, 1, '2025-05-17 23:43:36');
INSERT INTO `notifications` VALUES (7, 2, 'friend_request', '1 发送了好友请求', 1, 1, '2025-05-17 23:44:09');
INSERT INTO `notifications` VALUES (8, 1, 'system', '2 接受了您的好友请求，现在你们已经是好友了！', 0, 1, '2025-05-17 23:44:26');
INSERT INTO `notifications` VALUES (9, 3, 'friend_request', '2 发送了好友请求', 2, 0, '2025-05-17 23:45:34');
INSERT INTO `notifications` VALUES (10, 3, 'friend_request', '1 发送了好友请求', 1, 0, '2025-05-17 23:46:03');
INSERT INTO `notifications` VALUES (11, 4, 'friend_request', '1 发送了好友请求', 1, 0, '2025-05-17 23:46:06');
INSERT INTO `notifications` VALUES (12, 5, 'friend_request', '1 发送了好友请求', 1, 0, '2025-05-17 23:46:08');
INSERT INTO `notifications` VALUES (13, 2, 'system', '3 接受了您的好友请求，现在你们已经是好友了！', 0, 1, '2025-05-17 23:46:23');
INSERT INTO `notifications` VALUES (14, 1, 'system', '3 接受了您的好友请求，现在你们已经是好友了！', 0, 1, '2025-05-17 23:46:24');
INSERT INTO `notifications` VALUES (15, 1, 'system', '4 接受了您的好友请求，现在你们已经是好友了！', 0, 1, '2025-05-17 23:46:30');
INSERT INTO `notifications` VALUES (16, 1, 'system', '5 接受了您的好友请求，现在你们已经是好友了！', 0, 1, '2025-05-17 23:46:41');
INSERT INTO `notifications` VALUES (17, 2, 'message', '收到来自 1 的新私信', 3, 1, '2025-05-18 10:48:25');
INSERT INTO `notifications` VALUES (18, 1, 'message', '收到来自 2 的新私信', 4, 1, '2025-05-18 11:03:25');
INSERT INTO `notifications` VALUES (19, 1, 'message', '收到来自 2 的新私信', 5, 1, '2025-05-18 11:03:29');
INSERT INTO `notifications` VALUES (20, 1, 'message', '收到来自 2 的新私信', 6, 1, '2025-05-18 11:03:32');
INSERT INTO `notifications` VALUES (21, 1, 'message', '收到来自 2 的新私信', 7, 1, '2025-05-18 11:03:36');
INSERT INTO `notifications` VALUES (22, 1, 'message', '收到来自 2 的新私信', 8, 1, '2025-05-18 11:03:39');
INSERT INTO `notifications` VALUES (23, 1, 'message', '收到来自 2 的新私信', 9, 1, '2025-05-18 11:03:42');
INSERT INTO `notifications` VALUES (24, 1, 'message', '收到来自 2 的新私信', 10, 1, '2025-05-18 11:03:45');
INSERT INTO `notifications` VALUES (25, 1, 'message', '收到来自 2 的新私信', 11, 1, '2025-05-18 11:03:50');
INSERT INTO `notifications` VALUES (26, 6, 'friend_request', '1 发送了好友请求', 1, 0, '2025-05-18 11:08:04');
INSERT INTO `notifications` VALUES (27, 7, 'friend_request', '1 发送了好友请求', 1, 0, '2025-05-18 11:08:06');
INSERT INTO `notifications` VALUES (28, 8, 'friend_request', '1 发送了好友请求', 1, 0, '2025-05-18 11:08:07');
INSERT INTO `notifications` VALUES (29, 9, 'friend_request', '1 发送了好友请求', 1, 0, '2025-05-18 11:08:09');
INSERT INTO `notifications` VALUES (30, 10, 'friend_request', '1 发送了好友请求', 1, 1, '2025-05-18 11:08:11');
INSERT INTO `notifications` VALUES (31, 1, 'system', '6 接受了您的好友请求，现在你们已经是好友了！', 0, 1, '2025-05-18 11:08:22');
INSERT INTO `notifications` VALUES (32, 1, 'system', '7 接受了您的好友请求，现在你们已经是好友了！', 0, 1, '2025-05-18 11:08:31');
INSERT INTO `notifications` VALUES (33, 1, 'system', '8 接受了您的好友请求，现在你们已经是好友了！', 0, 1, '2025-05-18 11:08:40');
INSERT INTO `notifications` VALUES (34, 1, 'system', '9 接受了您的好友请求，现在你们已经是好友了！', 0, 1, '2025-05-18 11:08:49');
INSERT INTO `notifications` VALUES (35, 1, 'system', '10 接受了您的好友请求，现在你们已经是好友了！', 0, 1, '2025-05-18 11:08:59');
INSERT INTO `notifications` VALUES (36, 2, 'message', '收到来自 1 的新私信', 12, 1, '2025-05-18 11:16:52');
INSERT INTO `notifications` VALUES (37, 2, 'message', '收到来自 1 的新私信', 13, 1, '2025-05-18 11:16:58');
INSERT INTO `notifications` VALUES (38, 2, 'message', '收到来自 1 的新私信', 14, 1, '2025-05-18 11:17:02');
INSERT INTO `notifications` VALUES (39, 2, 'message', '收到来自 1 的新私信', 15, 1, '2025-05-18 11:18:49');
INSERT INTO `notifications` VALUES (40, 2, 'message', '收到来自 1 的新私信', 16, 1, '2025-05-18 11:19:17');
INSERT INTO `notifications` VALUES (41, 2, 'message', '收到来自 1 的新私信', 17, 1, '2025-05-18 11:22:13');
INSERT INTO `notifications` VALUES (42, 2, 'message', '收到来自 1 的新私信', 18, 1, '2025-05-18 11:22:57');
INSERT INTO `notifications` VALUES (43, 3, 'comment', '1 评论了你的帖子', 19, 0, '2025-05-18 11:30:16');
INSERT INTO `notifications` VALUES (44, 2, 'message', '收到来自 1 的新私信', 19, 1, '2025-05-18 11:30:27');
INSERT INTO `notifications` VALUES (45, 2, 'message', '收到来自 1 的新私信', 20, 1, '2025-05-18 11:30:31');
INSERT INTO `notifications` VALUES (46, 2, 'message', '收到来自 1 的新私信', 21, 1, '2025-05-18 11:30:57');
INSERT INTO `notifications` VALUES (47, 3, 'message', '收到来自 1 的新私信', 22, 0, '2025-05-18 11:31:17');
INSERT INTO `notifications` VALUES (48, 10, 'message', '收到来自 1 的新私信', 23, 1, '2025-05-18 11:31:35');
INSERT INTO `notifications` VALUES (49, 1, 'message', '收到来自 10 的新私信', 24, 1, '2025-05-18 11:32:06');
INSERT INTO `notifications` VALUES (50, 2, 'comment', '1 评论了你的帖子', 20, 1, '2025-05-18 12:25:58');
INSERT INTO `notifications` VALUES (51, 3, 'comment', '1 评论了你的帖子', 21, 0, '2025-05-18 12:42:45');
INSERT INTO `notifications` VALUES (52, 2, 'message', '收到来自 1 的新私信', 25, 1, '2025-05-18 12:48:53');
INSERT INTO `notifications` VALUES (53, 2, 'message', '收到来自 1 的新私信', 26, 1, '2025-05-18 13:56:35');
INSERT INTO `notifications` VALUES (54, 10, 'friend_request', '2 发送了好友请求', 2, 1, '2025-05-18 14:00:16');
INSERT INTO `notifications` VALUES (55, 2, 'system', '10 接受了您的好友请求，现在你们已经是好友了！', 0, 1, '2025-05-18 14:00:41');
INSERT INTO `notifications` VALUES (56, 2, 'message', '收到来自 10 的新私信', 27, 1, '2025-05-18 14:00:59');
INSERT INTO `notifications` VALUES (57, 10, 'message', '收到来自 1 的新私信', 28, 1, '2025-05-18 15:11:18');
INSERT INTO `notifications` VALUES (58, 1, 'message', '收到来自 10 的新私信', 29, 0, '2025-05-18 15:13:31');
INSERT INTO `notifications` VALUES (59, 11, 'comment', '1 评论了你的帖子', 24, 0, '2025-05-18 16:23:09');
INSERT INTO `notifications` VALUES (60, 11, 'comment', '2 评论了你的帖子', 25, 0, '2025-05-18 16:25:37');
INSERT INTO `notifications` VALUES (61, 11, 'comment', '1 评论了你的帖子', 26, 0, '2025-05-18 16:30:04');
INSERT INTO `notifications` VALUES (62, 11, 'comment', '1 评论了你的帖子', 27, 0, '2025-05-18 16:30:07');
INSERT INTO `notifications` VALUES (63, 11, 'comment', '1 评论了你的帖子', 28, 0, '2025-05-18 16:30:08');
INSERT INTO `notifications` VALUES (64, 11, 'comment', '1 评论了你的帖子', 29, 0, '2025-05-18 16:41:38');
INSERT INTO `notifications` VALUES (65, 11, 'comment', '1 评论了你的帖子', 30, 0, '2025-05-18 16:41:41');
INSERT INTO `notifications` VALUES (66, 11, 'comment', '1 评论了你的帖子', 31, 0, '2025-05-18 17:59:20');
INSERT INTO `notifications` VALUES (67, 11, 'comment', '1 评论了你的帖子', 32, 0, '2025-05-18 17:59:23');

-- ----------------------------
-- Table structure for posts
-- ----------------------------
DROP TABLE IF EXISTS `posts`;
CREATE TABLE `posts`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `image_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `user_id`(`user_id` ASC) USING BTREE,
  CONSTRAINT `posts_ibfk_1` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 15 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of posts
-- ----------------------------
INSERT INTO `posts` VALUES (12, 1, '1', '2025-05-18 15:09:40', 'uploads/posts/20250518_6aa06e40-b225-42a4-bf43-f420ba1f5abd.png');
INSERT INTO `posts` VALUES (13, 2, '阿', '2025-05-18 15:23:56', NULL);
INSERT INTO `posts` VALUES (14, 11, '1', '2025-05-18 15:37:29', NULL);

-- ----------------------------
-- Table structure for private_messages
-- ----------------------------
DROP TABLE IF EXISTS `private_messages`;
CREATE TABLE `private_messages`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `sender_id` int NOT NULL,
  `receiver_id` int NOT NULL,
  `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `is_read` int NOT NULL DEFAULT 0,
  `create_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `image_path` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `sender_id`(`sender_id` ASC) USING BTREE,
  INDEX `receiver_id`(`receiver_id` ASC) USING BTREE,
  CONSTRAINT `private_messages_ibfk_1` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT,
  CONSTRAINT `private_messages_ibfk_2` FOREIGN KEY (`receiver_id`) REFERENCES `users` (`id`) ON DELETE RESTRICT ON UPDATE RESTRICT
) ENGINE = InnoDB AUTO_INCREMENT = 30 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of private_messages
-- ----------------------------
INSERT INTO `private_messages` VALUES (1, 2, 1, '111', 1, '2025-05-17 21:49:28', NULL);
INSERT INTO `private_messages` VALUES (2, 1, 2, '你妈妈死了', 1, '2025-05-17 21:52:27', NULL);
INSERT INTO `private_messages` VALUES (3, 1, 2, '吃饱了没事干', 1, '2025-05-18 10:48:25', NULL);
INSERT INTO `private_messages` VALUES (4, 2, 1, '111', 1, '2025-05-18 11:03:25', NULL);
INSERT INTO `private_messages` VALUES (5, 2, 1, '111', 1, '2025-05-18 11:03:29', NULL);
INSERT INTO `private_messages` VALUES (6, 2, 1, '111', 1, '2025-05-18 11:03:32', NULL);
INSERT INTO `private_messages` VALUES (7, 2, 1, '666', 1, '2025-05-18 11:03:36', NULL);
INSERT INTO `private_messages` VALUES (8, 2, 1, '111', 1, '2025-05-18 11:03:39', NULL);
INSERT INTO `private_messages` VALUES (9, 2, 1, '111111', 1, '2025-05-18 11:03:42', NULL);
INSERT INTO `private_messages` VALUES (10, 2, 1, '111111111', 1, '2025-05-18 11:03:45', NULL);
INSERT INTO `private_messages` VALUES (11, 2, 1, '8888888888', 1, '2025-05-18 11:03:50', NULL);
INSERT INTO `private_messages` VALUES (12, 1, 2, '1', 1, '2025-05-18 11:16:52', NULL);
INSERT INTO `private_messages` VALUES (13, 1, 2, '1', 1, '2025-05-18 11:16:58', NULL);
INSERT INTO `private_messages` VALUES (14, 1, 2, '1', 1, '2025-05-18 11:17:02', NULL);
INSERT INTO `private_messages` VALUES (15, 1, 2, '1', 1, '2025-05-18 11:18:49', NULL);
INSERT INTO `private_messages` VALUES (16, 1, 2, '和', 1, '2025-05-18 11:19:17', NULL);
INSERT INTO `private_messages` VALUES (17, 1, 2, '123', 1, '2025-05-18 11:22:13', NULL);
INSERT INTO `private_messages` VALUES (18, 1, 2, '666', 1, '2025-05-18 11:22:57', NULL);
INSERT INTO `private_messages` VALUES (19, 1, 2, '应用', 1, '2025-05-18 11:30:27', NULL);
INSERT INTO `private_messages` VALUES (20, 1, 2, '123', 1, '2025-05-18 11:30:31', NULL);
INSERT INTO `private_messages` VALUES (21, 1, 2, '1', 1, '2025-05-18 11:30:57', NULL);
INSERT INTO `private_messages` VALUES (22, 1, 3, '111', 0, '2025-05-18 11:31:17', NULL);
INSERT INTO `private_messages` VALUES (23, 1, 10, '123', 1, '2025-05-18 11:31:35', NULL);
INSERT INTO `private_messages` VALUES (24, 10, 1, '111', 1, '2025-05-18 11:32:06', NULL);
INSERT INTO `private_messages` VALUES (25, 1, 2, '你好', 1, '2025-05-18 12:48:53', NULL);
INSERT INTO `private_messages` VALUES (26, 1, 2, '阿松大', 1, '2025-05-18 13:56:35', NULL);
INSERT INTO `private_messages` VALUES (27, 10, 2, '阿松大', 1, '2025-05-18 14:00:59', NULL);
INSERT INTO `private_messages` VALUES (28, 1, 10, '', 1, '2025-05-18 15:11:18', 'uploads/messages/20250518_cded889b-304f-4074-ac24-40651c4531eb.png');
INSERT INTO `private_messages` VALUES (29, 10, 1, '', 1, '2025-05-18 15:13:31', 'uploads/messages/20250518_ec36d77a-0a52-4898-b90d-4edbb5459504.png');

-- ----------------------------
-- Table structure for users
-- ----------------------------
DROP TABLE IF EXISTS `users`;
CREATE TABLE `users`  (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `password` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
  `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
  `bio` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
  `register_time` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
  `status` int NULL DEFAULT 1 COMMENT '1:正常, 0:禁用',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `username`(`username` ASC) USING BTREE
) ENGINE = InnoDB AUTO_INCREMENT = 12 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of users
-- ----------------------------
INSERT INTO `users` VALUES (1, '1', '1', '1@qq.com', '1', '2025-05-17 21:11:06', 0);
INSERT INTO `users` VALUES (2, '2', '2', '2@qq.com', NULL, '2025-05-17 21:35:15', 1);
INSERT INTO `users` VALUES (3, '3', '3', '3@qq.com', NULL, '2025-05-17 22:58:23', 1);
INSERT INTO `users` VALUES (4, '4', '4', '4@qq.com', NULL, '2025-05-17 23:45:47', 1);
INSERT INTO `users` VALUES (5, '5', '5', '5@qq.com', NULL, '2025-05-17 23:45:55', 1);
INSERT INTO `users` VALUES (6, '6', '6', '6@qq.com', NULL, '2025-05-18 11:06:08', 1);
INSERT INTO `users` VALUES (7, '7', '7', '7@qq.com', NULL, '2025-05-18 11:07:28', 1);
INSERT INTO `users` VALUES (8, '8', '8', '8@qq.com', NULL, '2025-05-18 11:07:38', 1);
INSERT INTO `users` VALUES (9, '9', '9', '9@qq.com', NULL, '2025-05-18 11:07:44', 1);
INSERT INTO `users` VALUES (10, '10', '10', '10@qq.com', NULL, '2025-05-18 11:07:52', 1);
INSERT INTO `users` VALUES (11, '戴懿', '1', '1@qq.com', NULL, '2025-05-18 15:37:11', 1);

SET FOREIGN_KEY_CHECKS = 1;
