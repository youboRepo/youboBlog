package com.youbo.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.youbo.constant.RedisKeyConstants;
import com.youbo.entity.Blog;
import com.youbo.entity.Category;
import com.youbo.exception.NotFoundException;
import com.youbo.exception.PersistenceException;
import com.youbo.jdbc.impl.MyServiceImpl;
import com.youbo.model.dto.BlogCustom;
import com.youbo.query.BlogQuery;
import com.youbo.query.PageDTO;
import com.youbo.util.markdown.MarkdownImportUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.youbo.mapper.BlogMapper;
import com.youbo.model.dto.BlogVisibility;
import com.youbo.model.vo.ArchiveBlog;
import com.youbo.model.vo.BlogDetail;
import com.youbo.model.vo.BlogInfo;
import com.youbo.model.vo.PageResult;
import com.youbo.model.vo.RandomBlog;
import com.youbo.service.BlogService;
import com.youbo.service.RedisService;
import com.youbo.service.TagService;
import com.youbo.util.JacksonUtils;
import com.youbo.util.markdown.MarkdownUtils;
import org.springframework.util.Assert;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @Description: 博客文章业务层实现
 * @Author: youbo
 * @Date: 2020-07-29
 */
@Service
public class BlogServiceImpl extends MyServiceImpl<BlogMapper, Blog> implements BlogService {
	@Autowired
	BlogMapper blogMapper;
	@Autowired
	TagService tagService;
	@Autowired
	RedisService redisService;
	//随机博客显示5条
	private static final int randomBlogLimitNum = 5;
	//最新推荐博客显示3条
	private static final int newBlogPageSize = 3;
	//每页显示5条博客简介
	private static final int pageSize = 5;
	//博客简介列表排序方式
	private static final String orderBy = "is_top desc, create_time desc";
	//私密博客提示
	private static final String PRIVATE_BLOG_DESCRIPTION = "此文章受密码保护！";

	/**
	 * 项目启动时，保存所有博客的浏览量到Redis
	 */
	@PostConstruct
	private void saveBlogViewsToRedis() {
		String redisKey = RedisKeyConstants.BLOG_VIEWS_MAP;
		//Redis中没有存储博客浏览量的Hash
		if (!redisService.hasKey(redisKey)) {
			//从数据库中读取并存入Redis
			Map<Long, Integer> blogViewsMap = this.getBlogViewsMap();
			redisService.saveMapToHash(redisKey, blogViewsMap);
		}
	}

	@Override
	public List<BlogCustom> getSearchBlogListByQueryAndIsPublished(BlogQuery query) {
		
		List<BlogCustom> blogs = this.list(query, this::setQueryWrapper, BlogCustom.class);
		
		for (BlogCustom searchBlog : blogs) {
			String content = searchBlog.getContent();
			int contentLength = content.length();
			int index = content.indexOf(query.getContent()) - 10;
			index = index < 0 ? 0 : index;
			int end = index + 21;//以关键字字符串为中心返回21个字
			end = end > contentLength - 1 ? contentLength - 1 : end;
			searchBlog.setContent(content.substring(index, end));
		}
		return blogs;
	}

	@Override
	public List<BlogCustom> getIdAndTitleList() {
		BlogQuery query = new BlogQuery();
		query.setIsQueryContent(false);
		return this.list(query, this::setQueryWrapper, BlogCustom.class);
	}

	@Override
	public List<BlogCustom> getNewBlogListByIsPublished() {
		String redisKey = RedisKeyConstants.NEW_BLOG_LIST;
		List<BlogCustom> newBlogListFromRedis = redisService.getListByValue(redisKey);
		if (newBlogListFromRedis != null) {
			return newBlogListFromRedis;
		}
		
		BlogQuery query = new BlogQuery();
		query.setIsQueryContent(false);
		query.setIsPublished(true);
		List<BlogCustom> newBlogs = this.list(query, this::setQueryWrapper, BlogCustom.class);
		
		for (BlogCustom newBlog : newBlogs) {
			if (!"".equals(newBlog.getPassword())) {
				newBlog.setPrivacy(true);
				newBlog.setPassword("");
			} else {
				newBlog.setPrivacy(false);
			}
		}
		redisService.saveListToValue(redisKey, newBlogs);
		return newBlogs;
	}

	@Override
	public PageResult<BlogInfo> getBlogInfoListByIsPublished(Integer pageNum) {
		String redisKey = RedisKeyConstants.HOME_BLOG_INFO_LIST;
		//redis已有当前页缓存
		PageResult<BlogInfo> pageResultFromRedis = redisService.getBlogInfoPageResultByHash(redisKey, pageNum);
		if (pageResultFromRedis != null) {
			setBlogViewsFromRedisToPageResult(pageResultFromRedis);
			return pageResultFromRedis;
		}
		//redis没有缓存，从数据库查询，并添加缓存
		PageHelper.startPage(pageNum, pageSize, orderBy);
		List<BlogInfo> blogInfos = processBlogInfosPassword(blogMapper.getBlogInfoListByIsPublished());
		PageInfo<BlogInfo> pageInfo = new PageInfo<>(blogInfos);
		PageResult<BlogInfo> pageResult = new PageResult<>(pageInfo.getPages(), pageInfo.getList());
		setBlogViewsFromRedisToPageResult(pageResult);
		//添加首页缓存
		redisService.saveKVToHash(redisKey, pageNum, pageResult);
		return pageResult;
	}

	/**
	 * 将pageResult中博客对象的浏览量设置为Redis中的最新值
	 *
	 * @param pageResult
	 */
	private void setBlogViewsFromRedisToPageResult(PageResult<BlogInfo> pageResult) {
		String redisKey = RedisKeyConstants.BLOG_VIEWS_MAP;
		List<BlogInfo> blogInfos = pageResult.getList();
		for (int i = 0; i < blogInfos.size(); i++) {
			BlogInfo blogInfo = JacksonUtils.convertValue(blogInfos.get(i), BlogInfo.class);
			Long blogId = blogInfo.getId();
			/**
			 * 这里如果出现异常，通常是手动修改过 MySQL 而没有通过后台管理，导致 Redis 和 MySQL 不同步
			 * 从 Redis 中查出了 null，强转 int 时出现 NullPointerException
			 * 直接抛出异常比带着 bug 继续跑要好得多
			 *
			 * 解决步骤：
			 * 1.结束程序
			 * 2.删除 Redis DB 中 blogViewsMap 这个 key（或者直接清空对应的整个 DB）
			 * 3.重新启动程序
			 *
			 */
			int view = (int) redisService.getValueByHashKey(redisKey, blogId);
			blogInfo.setViews(view);
			blogInfos.set(i, blogInfo);
		}
	}

	@Override
	public PageResult<BlogInfo> getBlogInfoListByCategoryNameAndIsPublished(String categoryName, Integer pageNum) {
		PageHelper.startPage(pageNum, pageSize, orderBy);
		List<BlogInfo> blogInfos = processBlogInfosPassword(blogMapper.getBlogInfoListByCategoryNameAndIsPublished(categoryName));
		PageInfo<BlogInfo> pageInfo = new PageInfo<>(blogInfos);
		PageResult<BlogInfo> pageResult = new PageResult<>(pageInfo.getPages(), pageInfo.getList());
		setBlogViewsFromRedisToPageResult(pageResult);
		return pageResult;
	}

	@Override
	public PageResult<BlogInfo> getBlogInfoListByTagNameAndIsPublished(String tagName, Integer pageNum) {
		PageHelper.startPage(pageNum, pageSize, orderBy);
		List<BlogInfo> blogInfos = processBlogInfosPassword(blogMapper.getBlogInfoListByTagNameAndIsPublished(tagName));
		PageInfo<BlogInfo> pageInfo = new PageInfo<>(blogInfos);
		PageResult<BlogInfo> pageResult = new PageResult<>(pageInfo.getPages(), pageInfo.getList());
		setBlogViewsFromRedisToPageResult(pageResult);
		return pageResult;
	}

	private List<BlogInfo> processBlogInfosPassword(List<BlogInfo> blogInfos) {
		for (BlogInfo blogInfo : blogInfos) {
			if (!"".equals(blogInfo.getPassword())) {
				blogInfo.setPrivacy(true);
				blogInfo.setPassword("");
				blogInfo.setDescription(PRIVATE_BLOG_DESCRIPTION);
			} else {
				blogInfo.setPrivacy(false);
				blogInfo.setDescription(MarkdownUtils.markdownToHtmlExtensions(blogInfo.getDescription()));
			}
			blogInfo.setTags(tagService.getTagListByBlogId(blogInfo.getId()));
		}
		return blogInfos;
	}

	@Override
	public Map<String, Object> getArchiveBlogAndCountByIsPublished() {
		String redisKey = RedisKeyConstants.ARCHIVE_BLOG_MAP;
		Map<String, Object> mapFromRedis = redisService.getMapByValue(redisKey);
		if (mapFromRedis != null) {
			return mapFromRedis;
		}
		List<String> groupYearMonth = blogMapper.getGroupYearMonthByIsPublished();
		Map<String, List<ArchiveBlog>> archiveBlogMap = new LinkedHashMap<>();
		for (String s : groupYearMonth) {
			List<ArchiveBlog> archiveBlogs = blogMapper.getArchiveBlogListByYearMonthAndIsPublished(s);
			for (ArchiveBlog archiveBlog : archiveBlogs) {
				if (!"".equals(archiveBlog.getPassword())) {
					archiveBlog.setPrivacy(true);
					archiveBlog.setPassword("");
				} else {
					archiveBlog.setPrivacy(false);
				}
			}
			archiveBlogMap.put(s, archiveBlogs);
		}
		Integer count = countBlogByIsPublished();
		Map<String, Object> map = new HashMap<>(4);
		map.put("blogMap", archiveBlogMap);
		map.put("count", count);
		redisService.saveMapToValue(redisKey, map);
		return map;
	}

	@Override
	public List<RandomBlog> getRandomBlogListByLimitNumAndIsPublishedAndIsRecommend() {
		BlogQuery query = new BlogQuery();
		query.setIsPublished(true);
		query.setIsRecommend(true);
		query.setIsQueryContent(false);
		
		List<RandomBlog> randomBlogs = this.list(query, this::setQueryWrapper, RandomBlog.class);
		for (RandomBlog randomBlog : randomBlogs) {
			if (!"".equals(randomBlog.getPassword())) {
				randomBlog.setPrivacy(true);
				randomBlog.setPassword("");
			} else {
				randomBlog.setPrivacy(false);
			}
		}
		return randomBlogs;
	}

	private Map<Long, Integer> getBlogViewsMap() {
		return this.list(new BlogQuery(), this::setQueryWrapper, Blog::getId, Blog::getViews).stream().collect(Collectors.toMap(Blog::getId, Blog::getViews));
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteBlogById(Long id) {
		boolean isRemove = this.remove(id);
		if (!isRemove) {
			throw new NotFoundException("该博客不存在");
		}
		deleteBlogRedisCache();
		redisService.deleteByHashKey(RedisKeyConstants.BLOG_VIEWS_MAP, id);
	}

	// TODO 移BlogTagService
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteBlogTagByBlogId(Long blogId) {
		if (blogMapper.deleteBlogTagByBlogId(blogId) == 0) {
			throw new PersistenceException("维护博客标签关联表失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveBlog(BlogCustom blog) {
		boolean isSave = this.save(blog);
		if (!isSave) {
			throw new PersistenceException("添加博客失败");
		}
		redisService.saveKVToHash(RedisKeyConstants.BLOG_VIEWS_MAP, blog.getId(), 0);
		deleteBlogRedisCache();
	}

	// TODO 移BlogTagService
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveBlogTag(Long blogId, Long tagId) {
		if (blogMapper.saveBlogTag(blogId, tagId) != 1) {
			throw new PersistenceException("维护博客标签关联表失败");
		}
	}

	// TODO 删除BlogVisibility对象
	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateBlogVisibilityById(Long blogId, BlogVisibility blogVisibility) {
		if (blogMapper.updateBlogVisibilityById(blogId, blogVisibility) != 1) {
			throw new PersistenceException("操作失败");
		}
		redisService.deleteCacheByKey(RedisKeyConstants.HOME_BLOG_INFO_LIST);
		redisService.deleteCacheByKey(RedisKeyConstants.NEW_BLOG_LIST);
		redisService.deleteCacheByKey(RedisKeyConstants.ARCHIVE_BLOG_MAP);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateBlogById(Blog blog) {
		this.update(blog);
		redisService.deleteCacheByKey(RedisKeyConstants.HOME_BLOG_INFO_LIST);
	}

	@Override
	public void updateViewsToRedis(Long blogId) {
		redisService.incrementByHashKey(RedisKeyConstants.BLOG_VIEWS_MAP, blogId, 1);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateBlogsById(List<Blog> blogs) {
		this.update(blogs);
	}

	@Override
	public BlogCustom getBlogById(Long id) {
		BlogCustom blog = this.get(id, BlogCustom.class);
		if (blog == null) {
			throw new NotFoundException("博客不存在");
		}
		/**
		 * 将浏览量设置为Redis中的最新值
		 * 这里如果出现异常，查看第 152 行注释说明
		 * @see BlogServiceImpl#setBlogViewsFromRedisToPageResult
		 */
		int view = (int) redisService.getValueByHashKey(RedisKeyConstants.BLOG_VIEWS_MAP, blog.getId());
		blog.setViews(view);
		return blog;
	}

	@Override
	public String getTitleByBlogId(Long id) {
		return blogMapper.getTitleByBlogId(id);
	}

	@Override
	public BlogDetail getBlogByIdAndIsPublished(Long id) {
		BlogDetail blog = blogMapper.getBlogByIdAndIsPublished(id);
		if (blog == null) {
			throw new NotFoundException("该博客不存在");
		}
		blog.setContent(MarkdownUtils.markdownToHtmlExtensions(blog.getContent()));
		/**
		 * 将浏览量设置为Redis中的最新值
		 * 这里如果出现异常，查看第 152 行注释说明
		 * @see BlogServiceImpl#setBlogViewsFromRedisToPageResult
		 */
		int view = (int) redisService.getValueByHashKey(RedisKeyConstants.BLOG_VIEWS_MAP, blog.getId());
		blog.setViews(view);
		return blog;
	}

	@Override
	public String getBlogPassword(Long blogId) {
		return this.get(blogId, Blog::getPassword);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updateBlog(BlogCustom blog) {
		
		this.update(blog);
		deleteBlogRedisCache();
		redisService.saveKVToHash(RedisKeyConstants.BLOG_VIEWS_MAP, blog.getId(), blog.getViews());
	}

	@Override
	public int countBlogByIsPublished() {
		return this.count(Blog::getIsPublished, true);
	}

	@Override
	public int countBlogByCategoryId(Long categoryId) {
		return this.count(Blog::getCategoryId, categoryId);
	}

	// TODO blog_tag
	@Override
	public int countBlogByTagId(Long tagId) {
		return blogMapper.countBlogByTagId(tagId);
	}

	@Override
	public Boolean getCommentEnabledByBlogId(Long blogId) {
		return this.get(blogId, Blog::getIsCommentEnabled);
	}

	@Override
	public Boolean getPublishedByBlogId(Long blogId) {
		return this.get(blogId, Blog::getIsPublished);
	}

	@Override
	public PageDTO<BlogCustom> getBlogList(BlogQuery query)
	{
		return this.page(query, this::setQueryWrapper, BlogCustom.class);
	}

	@Override
	public Long importMarkdown(MultipartFile file) throws IOException {
		String markdown = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8)).lines()
				.collect(Collectors.joining("\n"));

		Assert.notNull(markdown, "Markdown document must not be null");

		// Gets frontMatter
		Map<String, List<String>> frontMatter = MarkdownImportUtils.getFrontMatter(markdown);
		// remove frontMatter
		markdown = MarkdownImportUtils.removeFrontMatter(markdown);

		BlogCustom blog = new BlogCustom();

		List<String> elementValue;

		Set<Integer> tagIds = new HashSet<>();

		Set<Integer> categoryIds = new HashSet<>();

		/*if (frontMatter.size() > 0) {
			for (String key : frontMatter.keySet()) {
				elementValue = frontMatter.get(key);
				for (String ele : elementValue) {
					ele = com.youbo.util.StringUtils.strip(ele, "[", "]");
					ele = StringUtils.strip(ele, "\"");
					ele = StringUtils.strip(ele, "\'");
					if ("".equals(ele)) {
						continue;
					}
					switch (key) {
						case "title":
							blog.setTitle(ele);
							break;
						case "date":
							blog.setCreateTime(LocalDateTime.parse(ele));
							break;
						case "categories":
							Integer lastCategoryId = null;
							for (String categoryName : ele.split(",")) {
								categoryName = categoryName.trim();
								categoryName = StringUtils.strip(categoryName, "\"");
								categoryName = StringUtils.strip(categoryName, "\'");
								Category category = categoryService.getByName(categoryName);
								if (null == category) {
									category = new Category();
									category.setName(categoryName);
									category.setSlug(SlugUtils.slug(categoryName));
									category.setDescription(categoryName);
									if (lastCategoryId != null) {
										category.setParentId(lastCategoryId);
									}
									category = categoryService.create(category);
								}
								lastCategoryId = category.getId();
								categoryIds.add(lastCategoryId);
							}
							break;
						default:
							break;
					}
				}
			}
		}

		if (null == post.getStatus()) {
			post.setStatus(PostStatus.PUBLISHED);
		}

		if (StringUtils.isEmpty(post.getTitle())) {
			post.setTitle(filename);
		}

		if (StringUtils.isEmpty(post.getSlug())) {
			post.setSlug(SlugUtils.slug(post.getTitle()));
		}

		post.setOriginalContent(markdown);

		return createBy(post.convertTo(), tagIds, categoryIds, false);
		*/
		return null;
	}

	/**
	 * 删除首页缓存、最新推荐缓存、归档页面缓存、博客浏览量缓存
	 */
	private void deleteBlogRedisCache() {
		redisService.deleteCacheByKey(RedisKeyConstants.HOME_BLOG_INFO_LIST);
		redisService.deleteCacheByKey(RedisKeyConstants.NEW_BLOG_LIST);
		redisService.deleteCacheByKey(RedisKeyConstants.ARCHIVE_BLOG_MAP);
	}

	private LambdaQueryWrapper<Blog> setQueryWrapper(BlogQuery query) {
		LambdaQueryWrapper<Blog> queryWrapper = Wrappers.lambdaQuery();
		
		// 默认根据id降序
		queryWrapper.orderByDesc(Blog::getId);
		
		if (query == null)
		{
			return queryWrapper;
		}

		// 是否查询博客内容
		if (BooleanUtils.isFalse(query.getIsQueryContent()))
		{
			queryWrapper.select(Blog.class, i -> !"content".equals(i.getColumn()));
		}
		
		this.eq(queryWrapper, Blog::getCategoryId, query.getCategoryId());
		this.eq(queryWrapper, Blog::getIsPublished, query.getIsPublished());
		this.eq(queryWrapper, Blog::getPassword, query.getPassword());
		
		this.like(queryWrapper, Blog::getTitle, query.getTitle());
		this.like(queryWrapper, Blog::getContent, query.getContent());
		
		return queryWrapper;
	}
}
