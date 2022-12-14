package com.youbo.service.impl;

import com.youbo.entity.CityVisitor;
import com.youbo.mapper.CityVisitorMapper;
import com.youbo.service.CityVisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: 城市访客数量统计业务层实现
 * @Author: youbo
 * @Date: 2021-02-26
 */
@Service
public class CityVisitorServiceImpl implements CityVisitorService
{
	@Autowired
    CityVisitorMapper cityVisitorMapper;

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveCityVisitor(CityVisitor cityVisitor) {
		cityVisitorMapper.saveCityVisitor(cityVisitor);
	}
}
