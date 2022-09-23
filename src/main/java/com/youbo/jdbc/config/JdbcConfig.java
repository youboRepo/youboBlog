package com.youbo.jdbc.config;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.handler.TableNameHandler;
import com.baomidou.mybatisplus.extension.plugins.inner.DynamicTableNameInnerInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import com.youbo.jdbc.handler.MyMetaObjectHandler;
import com.youbo.jdbc.injector.MySqlInjector;
import com.youbo.util.ContextUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Map;
import java.util.Optional;

/**
 * 数据库配置类
 *
 * @author liunancun
 * @date 2019/11/25
 */
@Configuration
public class JdbcConfig
{
    private String[] dynamicTableNames = {"about", "blog", "category", "city_visitor", "comment", "exception_log", "friend", "login_log", "moment", "operation_log", "schedule_job", "schedule_job_log", "site_setting", "tag", "user", "visit_log", "visitor", "visit_record"};

    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        // 动态表名拦截器
        DynamicTableNameInnerInterceptor dynamicTableNameInnerInterceptor = new DynamicTableNameInnerInterceptor();
        Map<String, TableNameHandler> tableNameHandlerMap = MapUtil.newHashMap();
        for (String dynamicTableName : dynamicTableNames)
        {
            tableNameHandlerMap.put(dynamicTableName, (sql, tableName) -> Optional.of(tableName)
                    .map(ContextUtils::getContext).filter(StrUtil::isNotBlank).map(suffix -> tableName + "_" + suffix)
                    .orElse(tableName));
        }
        dynamicTableNameInnerInterceptor.setTableNameHandlerMap(tableNameHandlerMap);

        // 物理分页拦截器
        PaginationInnerInterceptor paginationInnerInterceptor = new PaginationInnerInterceptor(DbType.MYSQL);
        paginationInnerInterceptor.setMaxLimit(1000L);

        // 创建拦截器对象
        MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
        mybatisPlusInterceptor.addInnerInterceptor(dynamicTableNameInnerInterceptor);
        mybatisPlusInterceptor.addInnerInterceptor(paginationInnerInterceptor);
        return mybatisPlusInterceptor;
    }

    @Bean
    public MySqlInjector mySqlInjector() {
        return new MySqlInjector();
    }

    @Bean
    public MyMetaObjectHandler myMetaObjectHandler() {
        return new MyMetaObjectHandler();
    }
}
