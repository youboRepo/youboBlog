package com.youbo.util;

import com.youbo.exception.BaseException;
import org.springframework.cglib.beans.BeanCopier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对象工具类
 *
 * @author liunancun
 * @date 2019/11/13
 */
public class BeanUtils
{
    /**
     * 缓存对象克隆实例对象
     */
    private static final Map<String, BeanCopier> BEAN_COPIER_MAP = new ConcurrentHashMap<>();

    /**
     * 对象克隆
     *
     * @param source
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> T clone(Object source, Class<T> targetClass)
    {
        if (source == null)
        {
            return null;
        }

        try
        {
            String key = source.getClass().getName() + targetClass.getName();
            BeanCopier beanCopier = BEAN_COPIER_MAP.get(key);
            if (beanCopier == null)
            {
                beanCopier = BeanCopier.create(source.getClass(), targetClass, false);
                BEAN_COPIER_MAP.put(key, beanCopier);
            }

            T target = targetClass.newInstance();
            beanCopier.copy(source, target, null);

            return target;
        }
        catch (Exception e)
        {
            throw new BaseException(e.getMessage(), e);
        }
    }

    /**
     * 列表克隆
     *
     * @param sourceList
     * @param targetClass
     * @param <T>
     * @return
     */
    public static <T> List<T> clone(Collection<?> sourceList, Class<T> targetClass)
    {
        if (sourceList == null)
        {
            return null;
        }

        List<T> targetList = new ArrayList<>(sourceList.size());

        for (Object source : sourceList)
        {
            T target = clone(source, targetClass);
            targetList.add(target);
        }

        return targetList;
    }
}
