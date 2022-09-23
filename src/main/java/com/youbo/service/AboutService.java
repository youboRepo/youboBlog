package com.youbo.service;

import com.youbo.entity.About;
import com.youbo.query.AboutQuery;

import java.util.List;
import java.util.Map;

public interface AboutService {
	Map<String, String> getAboutInfo();

	Map<String, String> getAboutSetting();

	void updateAbout(List<About> abouts);

	boolean getAboutCommentEnabled();

	List<About> getAboutInfos(AboutQuery query);
}
