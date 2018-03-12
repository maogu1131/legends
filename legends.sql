
-- ----------------------------
--  Table structure for `job_info`
-- ----------------------------
DROP TABLE IF EXISTS `job_info`;
CREATE TABLE `job_info` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `group` varchar(100) DEFAULT NULL COMMENT '分组,一般是模块名',
  `type` varchar(100) NOT NULL COMMENT '一次性任务或者cron任务',
  `time` varchar(100) DEFAULT NULL COMMENT '一次性任务执行时间',
  `cron` varchar(100) DEFAULT NULL COMMENT 'cron 任务的 cron 表达式',
  `urls` varchar(1000) NOT NULL COMMENT '目标服务器URL列表,如果多条使用逗号分割.',
  `class_path` varchar(100) NOT NULL COMMENT '任务类的 ClassPath',
  `invoke_policy` varchar(100) DEFAULT NULL COMMENT '调用策略, 优先,随机 等.',
  `is_activity` int(11) DEFAULT NULL COMMENT '是否启用',
  `desc` varchar(200) DEFAULT NULL COMMENT '描述信息',
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `param` varchar(255) DEFAULT NULL,
  `latest_trigger_time` datetime DEFAULT NULL COMMENT '上一次触发时间',
  `latest_server_address` varchar(100) DEFAULT NULL COMMENT '上一次触发任务的服务器地址',
  `owner_phone` varchar(255) DEFAULT NULL COMMENT '负责人手机号',
  `is_check_finish` tinyint(4) DEFAULT '0' COMMENT '是否每日检查完成情况,0:false,1:true',
  `check_finish_time` varchar(5) NOT NULL DEFAULT '' COMMENT '检查完成时间，HH:mm 格式',
  PRIMARY KEY (`id`),
  UNIQUE KEY `JOB_INFO_UK_NAME_GROUP` (`name`,`group`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
--  Table structure for `job_info_history`
-- ----------------------------
DROP TABLE IF EXISTS `job_info_history`;
CREATE TABLE `job_info_history` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `job_info_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `group` varchar(100) DEFAULT NULL COMMENT '分组,一般是模块名',
  `type` varchar(100) NOT NULL COMMENT '一次性任务或者cron任务',
  `time` varchar(100) DEFAULT NULL COMMENT '一次性任务执行时间',
  `cron` varchar(100) DEFAULT NULL COMMENT 'cron 任务的 cron 表达式',
  `urls` varchar(1000) NOT NULL COMMENT '目标服务器URL列表,如果多条使用逗号分割.',
  `class_path` varchar(100) NOT NULL COMMENT '任务类的 ClassPath',
  `invoke_policy` varchar(100) DEFAULT NULL COMMENT '调用策略, 优先,随机 等.',
  `is_activity` int(11) DEFAULT NULL COMMENT '是否启用',
  `desc` varchar(200) DEFAULT NULL COMMENT '描述信息',
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `param` varchar(255) DEFAULT NULL,
  `latest_trigger_time` datetime DEFAULT NULL COMMENT '上一次触发时间',
  `latest_server_address` varchar(100) DEFAULT NULL COMMENT '上一次触发任务的服务器地址',
  `owner_phone` varchar(255) DEFAULT NULL COMMENT '负责人手机号',
  `is_check_finish` tinyint(4) DEFAULT '0' COMMENT '是否每日检查完成情况,0:false,1:true',
  `check_finish_time` varchar(5) DEFAULT NULL COMMENT '检查完成时间，HH:mm 格式',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


-- ----------------------------
--  Table structure for `job_snapshot`
-- ----------------------------
DROP TABLE IF EXISTS `job_snapshot`;
CREATE TABLE `job_snapshot` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `job_info_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `group` varchar(100) DEFAULT NULL COMMENT '分组,一般是模块名',
  `status` varchar(100) DEFAULT NULL COMMENT 'init, invoking, completed , error 等',
  `ip` varchar(100) DEFAULT NULL COMMENT '目标服务器 IP 地址',
  `url` varchar(100) DEFAULT NULL COMMENT '目标服务器 URL',
  `result` varchar(4000) DEFAULT NULL COMMENT '任务结果',
  `time_consume` bigint(20) DEFAULT NULL COMMENT '任务耗时',
  `detail` text COMMENT '执行信息',
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `server_address` varchar(100) DEFAULT NULL COMMENT '触发任务的服务器地址',
  `actual_start_time` datetime DEFAULT NULL COMMENT '任务实际开始时间',
  `actual_finish_time` datetime DEFAULT NULL COMMENT '任务实际开始时间',
  PRIMARY KEY (`id`),
  KEY `IDX_NAME_GROUP_STATUS` (`name`,`group`,`status`),
  KEY `IDX_GROUP_STATUS` (`group`,`status`),
  KEY `IDX_STATUS` (`status`),
  KEY `IDX_JOB_INFO_ID` (`job_info_id`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8;



-- ----------------------------
--  Table structure for `job_snapshot_history`
-- ----------------------------
DROP TABLE IF EXISTS `job_snapshot_history`;
CREATE TABLE `job_snapshot_history` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `job_snapshot_id` int(11) NOT NULL,
  `job_info_id` int(11) NOT NULL,
  `name` varchar(100) NOT NULL COMMENT '名称',
  `group` varchar(100) DEFAULT NULL COMMENT '分组,一般是模块名',
  `status` varchar(100) DEFAULT NULL COMMENT 'init, invoking, completed , error 等',
  `ip` varchar(100) DEFAULT NULL COMMENT '目标服务器 IP 地址',
  `url` varchar(100) DEFAULT NULL COMMENT '目标服务器 URL',
  `result` varchar(4000) DEFAULT NULL COMMENT '任务结果',
  `time_consume` bigint(20) DEFAULT NULL COMMENT '任务耗时',
  `detail` text COMMENT '执行信息',
  `create_time` datetime DEFAULT NULL,
  `modify_time` datetime DEFAULT NULL,
  `server_address` varchar(100) DEFAULT NULL COMMENT '触发任务的服务器地址',
  PRIMARY KEY (`id`),
  KEY `job_info_id` (`job_info_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

