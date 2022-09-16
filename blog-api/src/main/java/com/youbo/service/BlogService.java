package com.youbo.service;

import com.youbo.entity.Blog;
import com.youbo.model.dto.BlogCustom;
import com.youbo.model.dto.BlogVisibility;
import com.youbo.model.vo.BlogDetail;
import com.youbo.model.vo.BlogInfo;
import com.youbo.model.vo.NewBlog;
import com.youbo.model.vo.PageResult;
import com.youbo.model.vo.RandomBlog;
import com.youbo.model.vo.SearchBlog;
import com.youbo.query.BlogQuery;
import com.youbo.query.PageDTO;

import java.util.List;
import java.util.Map;

public interface BlogService {
	List<BlogCustom> getSearchBlogListByQueryAndIsPublished(BlogQuery query);

	List<BlogCustom> getIdAndTitleList();

	List<BlogCustom> getNewBlogListByIsPublished();

	PageResult<BlogInfo> getBlogInfoListByIsPublished(Integer pageNum);

	PageResult<BlogInfo> getBlogInfoListByCategoryNameAndIsPublished(String categoryName, Integer pageNum);

	PageResult<BlogInfo> getBlogInfoListByTagNameAndIsPublished(String tagName, Integer pageNum);

	Map<String, Object> getArchiveBlogAndCountByIsPublished();

	List<RandomBlog> getRandomBlogListByLimitNumAndIsPublishedAndIsRecommend();

	void deleteBlogById(Long id);

	void deleteBlogTagByBlogId(Long blogId);

	void saveBlog(BlogCustom blog);

	void saveBlogTag(Long blogId, Long tagId);
	
	void updateBlogVisibilityById(Long blogId, BlogVisibility blogVisibility);
	
	void updateBlogById(Blog blog);

	void updateViewsToRedis(Long blogId);

	void updateBlogsById(List<Blog> blogs);

	BlogCustom getBlogById(Long id);

	String getTitleByBlogId(Long id);

	BlogDetail getBlogByIdAndIsPublished(Long id);

	String getBlogPassword(Long blogId);

	void updateBlog(BlogCustom blog);

	int countBlogByIsPublished();

	int countBlogByCategoryId(Long categoryId);

	int countBlogByTagId(Long tagId);

	Boolean getCommentEnabledByBlogId(Long blogId);

	Boolean getPublishedByBlogId(Long blogId);

    PageDTO<BlogCustom> getBlogList(BlogQuery query);
}
