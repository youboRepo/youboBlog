package com.youbo.service.impl;

import com.youbo.constant.RedisKeyConstants;
import com.youbo.jdbc.impl.MyServiceImpl;
import com.youbo.mapper.AboutMapper;
import com.youbo.query.AboutQuery;
import com.youbo.service.AboutService;
import com.youbo.service.RedisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.youbo.entity.About;
import com.youbo.util.markdown.MarkdownUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * @Description: 关于我页面业务层实现
 * @Author: youbo
 * @Date: 2020-08-31
 */
@Service
public class AboutServiceImpl extends MyServiceImpl<AboutMapper, About> implements AboutService {
	@Autowired
    RedisService redisService;

	@Override
	public Map<String, String> getAboutInfo() {
		String redisKey = RedisKeyConstants.ABOUT_INFO_MAP;
		Map<String, String> aboutInfoMapFromRedis = redisService.getMapByValue(redisKey);
		if (aboutInfoMapFromRedis != null) {
			return aboutInfoMapFromRedis;
		}
		List<About> abouts = this.list();
		Map<String, String> aboutInfoMap = new HashMap<>(16);
		for (About about : abouts) {
			if ("content".equals(about.getNameEn())) {
				about.setValue(MarkdownUtils.markdownToHtmlExtensions(about.getValue()));
			}
			aboutInfoMap.put(about.getNameEn(), about.getValue());
		}
		redisService.saveMapToValue(redisKey, aboutInfoMap);
		return aboutInfoMap;
	}

	@Override
	public Map<String, String> getAboutSetting() {
		List<About> abouts = this.list();
		Map<String, String> map = new HashMap<>(16);
		for (About about : abouts) {
			map.put(about.getNameEn(), about.getValue());
		}
		return map;
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void updateAbout(List<About> abouts) {
		this.update(abouts);
		deleteAboutRedisCache();
	}

	@Override
	public boolean getAboutCommentEnabled() {
		return  Optional.ofNullable(this.get(About::getNameEn, "commentEnabled")).map(About::getValue).map(Boolean::parseBoolean).orElse(false);
	}

	@Override
	public List<About> getAboutInfos(AboutQuery query)
	{
		return this.list(About::getNameEn, query.getNameEns());
	}

	/**
	 * 删除关于我页面缓存
	 */
	private void deleteAboutRedisCache() {
		redisService.deleteCacheByKey(RedisKeyConstants.ABOUT_INFO_MAP);
	}
}
