package com.youbo.service.impl;

import com.youbo.entity.ExceptionLog;
import com.youbo.exception.PersistenceException;
import com.youbo.mapper.ExceptionLogMapper;
import com.youbo.service.ExceptionLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.youbo.util.IpAddressUtils;
import com.youbo.util.UserAgentUtils;

import java.util.List;
import java.util.Map;

/**
 * @Description: 异常日志业务层实现
 * @Author: youbo
 * @Date: 2020-12-03
 */
@Service
public class ExceptionLogServiceImpl implements ExceptionLogService
{
	@Autowired
    ExceptionLogMapper exceptionLogMapper;
	@Autowired
	UserAgentUtils userAgentUtils;

	@Override
	public List<ExceptionLog> getExceptionLogListByDate(String startDate, String endDate) {
		return exceptionLogMapper.getExceptionLogListByDate(startDate, endDate);
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void saveExceptionLog(ExceptionLog log) {
		String ipSource = IpAddressUtils.getCityInfo(log.getIp());
		Map<String, String> userAgentMap = userAgentUtils.parseOsAndBrowser(log.getUserAgent());
		String os = userAgentMap.get("os");
		String browser = userAgentMap.get("browser");
		log.setIpSource(ipSource);
		log.setOs(os);
		log.setBrowser(browser);
		if (exceptionLogMapper.saveExceptionLog(log) != 1) {
			throw new PersistenceException("日志添加失败");
		}
	}

	@Transactional(rollbackFor = Exception.class)
	@Override
	public void deleteExceptionLogById(Long id) {
		if (exceptionLogMapper.deleteExceptionLogById(id) != 1) {
			throw new PersistenceException("删除日志失败");
		}
	}
}
