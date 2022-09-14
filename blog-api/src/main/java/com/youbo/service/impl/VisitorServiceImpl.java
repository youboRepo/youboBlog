package com.youbo.service.impl;

import com.youbo.constant.RedisKeyConstants;
import com.youbo.exception.PersistenceException;
import com.youbo.mapper.VisitorMapper;
import com.youbo.service.RedisService;
import com.youbo.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.youbo.entity.Visitor;
import com.youbo.model.dto.VisitLogUuidTime;
import com.youbo.util.IpAddressUtils;
import com.youbo.util.UserAgentUtils;

import java.util.List;
import java.util.Map;

/**
 * @Description: 访客统计业务层实现
 * @Author: youbo
 * @Date: 2021-01-31
 */
@Service
public class VisitorServiceImpl implements VisitorService
{
	@Autowired
    VisitorMapper visitorMapper;
	@Autowired
    RedisService redisService;
	@Autowired
	UserAgentUtils userAgentUtils;

	@Override
	public List<Visitor> getVisitorListByDate(String startDate, String endDate) {
		return visitorMapper.getVisitorListByDate(startDate, endDate);
	}

	@Override
	public List<String> getNewVisitorIpSourceByYesterday() {
		return visitorMapper.getNewVisitorIpSourceByYesterday();
	}

	@Override
	public boolean hasUUID(String uuid) {
		return visitorMapper.hasUUID(uuid) != 0;
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveVisitor(Visitor visitor) {
		String ipSource = IpAddressUtils.getCityInfo(visitor.getIp());
		Map<String, String> userAgentMap = userAgentUtils.parseOsAndBrowser(visitor.getUserAgent());
		String os = userAgentMap.get("os");
		String browser = userAgentMap.get("browser");
		visitor.setIpSource(ipSource);
		visitor.setOs(os);
		visitor.setBrowser(browser);
		if (visitorMapper.saveVisitor(visitor) != 1) {
			throw new PersistenceException("访客添加失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void updatePVAndLastTimeByUUID(VisitLogUuidTime dto) {
		visitorMapper.updatePVAndLastTimeByUUID(dto);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteVisitor(Long id, String uuid) {
		//删除Redis中该访客的uuid
		redisService.deleteValueBySet(RedisKeyConstants.IDENTIFICATION_SET, uuid);
		if (visitorMapper.deleteVisitorById(id) != 1) {
			throw new PersistenceException("删除访客失败");
		}
	}
}
