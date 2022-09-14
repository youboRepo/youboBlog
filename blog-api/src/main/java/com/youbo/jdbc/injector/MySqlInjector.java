package com.youbo.jdbc.injector;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.injector.DefaultSqlInjector;
import com.youbo.jdbc.injector.method.Duplicate;
import com.youbo.jdbc.injector.method.Ignore;
import com.youbo.jdbc.injector.method.Replace;

import java.util.List;

/**
 * 自定义 SQL 注入器
 *
 * @author liunancun
 * @date 2020/7/29
 */
public class MySqlInjector extends DefaultSqlInjector
{
    @Override
    public List<AbstractMethod> getMethodList(Class<?> mapperClass)
    {
        List<AbstractMethod> methods = super.getMethodList(mapperClass);
        methods.add(new Ignore());
        methods.add(new Replace());
        methods.add(new Duplicate());
        return methods;
    }
}
