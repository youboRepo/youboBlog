package com.youbo.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import com.youbo.entity.Visitor;
import com.youbo.model.dto.VisitLogUuidTime;

import java.util.List;

/**
 * @Description: 访客统计持久层接口
 * @Author: youbo
 * @Date: 2021-01-31
 */
@Mapper
@Repository
public interface VisitorMapper {
	List<Visitor> getVisitorListByDate(String startDate, String endDate);

	List<String> getNewVisitorIpSourceByYesterday();

	int hasUUID(String uuid);

	int saveVisitor(Visitor visitor);

	int updatePVAndLastTimeByUUID(VisitLogUuidTime dto);

	int deleteVisitorById(Long id);
}
