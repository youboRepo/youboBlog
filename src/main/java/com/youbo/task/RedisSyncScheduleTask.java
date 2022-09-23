package com.youbo.task;

import cn.hutool.core.map.MapUtil;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.youbo.constant.RedisKeyConstants;
import com.youbo.entity.Blog;
import com.youbo.service.BlogService;
import com.youbo.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Description: Redis相关定时任务
 * @Author: youbo
 * @Date: 2020-11-02
 */
@Component
public class RedisSyncScheduleTask {
	@Autowired
	RedisService redisService;
	@Autowired
	BlogService blogService;

	/**
	 * 从Redis同步博客文章浏览量到数据库
	 */
	public void syncBlogViewsToDatabase() {
		String redisKey = RedisKeyConstants.BLOG_VIEWS_MAP;
		Map<Long, Integer> blogViewsMap = redisService.getMapByHash(redisKey);
		
		if (MapUtil.isEmpty(blogViewsMap)) {
			return;
		}
		
		List<Blog> updateBlogs = new ArrayList<>();
		blogViewsMap.forEach((blogId, views) -> {
			if (blogId != null) {
				Blog blog = new Blog();
				blog.setId(blogId);
				blog.setViews(views);
				updateBlogs.add(blog);	
			}
		});
		
		if (CollectionUtils.isNotEmpty(updateBlogs)) {
			blogService.updateBlogsById(updateBlogs);
		}
	}
}
