package com.youbo.model.dto;

import com.youbo.entity.Blog;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import com.youbo.entity.Category;
import com.youbo.entity.Tag;
import com.youbo.entity.User;

import java.util.ArrayList;
import java.util.List;

/**
 * @Description: 博客DTO
 * @Author: youbo
 * @Date: 2020-08-27
 */
@NoArgsConstructor
@Getter
@Setter
@ToString
public class BlogCustom extends Blog {

	/**
	 * 文章作者
	 */
	private User user;

	/**
	 * 文章分类
	 */
	private Category category;

	/**
	 * 文章标签
	 */
	private List<Tag> tags = new ArrayList<>();

	/**
	 * 分类对象
	 */
	private Object cate;

	/**
	 * 页面展示层传输的标签对象：正常情况下为 List<Integer>标签id 或 List<String>标签名
	 */
	private List<Object> tagList;

	private Boolean privacy;
}
