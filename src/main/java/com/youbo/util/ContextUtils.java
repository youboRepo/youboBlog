package com.youbo.util;

import cn.hutool.core.map.MapUtil;
import org.apache.commons.lang3.math.NumberUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 上下文工具类
 *
 * @author youxiaobo
 * @date 2022/9/14
 */
public class ContextUtils
{
    /**
     * 本地线程变量
     */
    private static final ThreadLocal<Map<String, String>> THREAD_LOCAL = new InheritableThreadLocal<Map<String, String>>()
    {
        @Override
        protected synchronized Map<String, String> initialValue()
        {
            return MapUtil.newHashMap();
        }

        @Override
        protected Map<String, String> childValue(Map<String, String> parentValue)
        {
            return Optional.ofNullable(parentValue).map(HashMap::new).orElse(null);
        }
    };

    /**
     * 获取本地上下文参数
     *
     * @param key
     * @return
     */
    public static String getContext(String key)
    {
        return THREAD_LOCAL.get().get(key);
    }

    /**
     * 获取整数类型的本地上下文参数
     *
     * @param key
     * @return
     */
    public static Integer getIntegerContext(String key)
    {
        return Optional.ofNullable(key).map(ContextUtils::getContext).filter(NumberUtils::isDigits)
                .map(Integer::valueOf).orElse(null);
    }

    /**
     * 设置本地上下文参数
     *
     * @param key
     * @param value
     */
    public static void setContext(String key, String value)
    {
        THREAD_LOCAL.get().put(key, value);
    }

    /**
     * 设置整数类型的本地上下文参数
     *
     * @param key
     * @param value
     */
    public static void setIntegerContext(String key, Integer value)
    {
        if (value != null)
        {
            setContext(key, value.toString());
        }
        else
        {
            removeContext(key);
        }
    }

    /**
     * 删除本地上下文参数
     *
     * @param key
     */
    public static void removeContext(String key)
    {
        THREAD_LOCAL.get().remove(key);
    }

    /**
     * 清除本地上下文参数
     */
    public static void clearContext()
    {
        THREAD_LOCAL.get().clear();
    }

}
