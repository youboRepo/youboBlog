package com.youbo.entity;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.FieldStrategy;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 博客实体类
 *
 * @author youxiaobo
 * @date 2022/09/14
 */
@Data
@TableName("blog")
public class Blog implements Serializable
{
	/**
	 * 序列化标识
	 */
	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 文章标题
	 */
	private String title;

	/**
	 * 文章首图，用于随机文章展示
	 */
	private String firstPicture;

	/**
	 * 文章正文
	 */
	private String content;

	/**
	 * 描述
	 */
	private String description;

	/**
	 * 公开或私密
	 */
	private Boolean isPublished;

	/**
	 * 推荐开关
	 */
	private Boolean isRecommend;

	/**
	 * 赞赏开关
	 */
	private Boolean isAppreciation;

	/**
	 * 评论开关
	 */
	private Boolean isCommentEnabled;

	/**
	 * 浏览次数
	 */
	private Integer views;

	/**
	 * 文章字数
	 */
	private Integer words;

	/**
	 * 阅读时长(分钟)
	 */
	private Integer readTime;

	/**
	 * 文章分类
	 */
	private Long categoryId;

	/**
	 * 是否置顶
	 */
	private Boolean isTop;

	/**
	 * 密码保护
	 */
	private String password;

	/**
	 * 文章作者
	 */
	private Long userId;

	/**
	 * 创建时间
	 */
	@TableField(fill = FieldFill.INSERT, updateStrategy = FieldStrategy.NEVER)
	private LocalDateTime createTime;

	/**
	 * 更新时间
	 */
	@TableField(fill = FieldFill.INSERT_UPDATE)
	private LocalDateTime updateTime;
}
