package com.youbo.service;

import com.youbo.entity.CityVisitor;

import java.util.List;
import java.util.Map;

public interface DashboardService {
	int countVisitLogByToday();

	int getBlogCount();

	int getCommentCount();

	Map<String, List> getCategoryBlogCountMap();

	Map<String, List> getTagBlogCountMap();

	Map<String, List> getVisitRecordMap();

	List<CityVisitor> getCityVisitorList();
}
