package com.youbo.mapper;

import com.youbo.jdbc.mapper.MyBaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import com.youbo.entity.About;

/**
 * @Description: 关于我持久层接口
 * @Author: youbo
 * @Date: 2020-08-31
 */
@Mapper
@Repository
public interface AboutMapper extends MyBaseMapper<About> {
}
