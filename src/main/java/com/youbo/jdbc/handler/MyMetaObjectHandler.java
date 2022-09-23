package com.youbo.jdbc.handler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.core.metadata.TableFieldInfo;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import org.apache.ibatis.reflection.MetaObject;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * 自动填充处理类
 *
 * @author liunancun
 * @date 2021/3/31
 */
public class MyMetaObjectHandler implements MetaObjectHandler
{
    @Override
    public void insertFill(MetaObject metaObject)
    {
        Optional.ofNullable(metaObject).map(this::findTableInfo).map(TableInfo::getFieldList).ifPresent(fields -> {
            Integer userId = 1;
            LocalDate nowDate = LocalDate.now();
            LocalDateTime nowTime = LocalDateTime.now();
            fields.stream().filter(TableFieldInfo::isWithInsertFill).forEach(field -> {
                // 填充当前用户
                if (field.getPropertyType() == Integer.class)
                {
                    this.strictInsertFill(metaObject, field.getProperty(), Integer.class, userId);
                }
                // 填充当前日期
                else if (field.getPropertyType() == LocalDate.class)
                {
                    this.strictInsertFill(metaObject, field.getProperty(), LocalDate.class, nowDate);
                }
                // 填充当前时间
                else if (field.getPropertyType() == LocalDateTime.class)
                {
                    this.strictInsertFill(metaObject, field.getProperty(), LocalDateTime.class, nowTime);
                }
            });
        });
    }

    @Override
    public void updateFill(MetaObject metaObject)
    {
        Optional.ofNullable(metaObject).map(this::findTableInfo).map(TableInfo::getFieldList).ifPresent(fields -> {
            Integer userId = 1;
            LocalDate nowDate = LocalDate.now();
            LocalDateTime nowTime = LocalDateTime.now();
            fields.stream().filter(TableFieldInfo::isWithUpdateFill).forEach(field -> {
                // 填充当前用户
                if (field.getPropertyType() == Integer.class)
                {
                    this.strictUpdateFill(metaObject, field.getProperty(), Integer.class, userId);
                }
                // 填充当前日期
                else if (field.getPropertyType() == LocalDate.class)
                {
                    this.strictUpdateFill(metaObject, field.getProperty(), LocalDate.class, nowDate);
                }
                // 填充当前时间
                else if (field.getPropertyType() == LocalDateTime.class)
                {
                    this.strictUpdateFill(metaObject, field.getProperty(), LocalDateTime.class, nowTime);
                }
            });
        });
    }
}
