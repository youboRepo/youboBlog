package com.youbo.service;

import org.springframework.scheduling.annotation.Async;
import com.youbo.entity.OperationLog;

import java.util.List;

public interface OperationLogService {
	List<OperationLog> getOperationLogListByDate(String startDate, String endDate);

	@Async
	void saveOperationLog(OperationLog log);

	void deleteOperationLogById(Long id);
}
