package com.youbo.jdbc.aspect;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 动态表名注解
 *
 * @author majiawen
 * @date 2022/07/06
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DynamicTableName
{
    /**
     * 表名
     *
     * @return
     */
    String[] value();

    /**
     * 后缀
     *
     * @return
     */
    String suffix();
}
