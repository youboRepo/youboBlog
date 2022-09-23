package com.youbo.jdbc.injector.method;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 插入一条数据（忽略已经存在）
 *
 * @author liunancun
 * @date 2020/8/21
 */
public class Ignore extends AbstractMethod
{
    /**
     * 序列化标识
     */
    private static final long serialVersionUID = 1L;

    @Override
    public MappedStatement injectMappedStatement(Class<?> mapperClass, Class<?> modelClass, TableInfo tableInfo)
    {
        String columnScript = SqlScriptUtils
                .convertTrim(tableInfo.getAllInsertSqlColumnMaybeIf(null), LEFT_BRACKET, RIGHT_BRACKET, null, COMMA);
        String valuesScript = SqlScriptUtils
                .convertTrim(tableInfo.getAllInsertSqlPropertyMaybeIf(null), LEFT_BRACKET, RIGHT_BRACKET, null, COMMA);
        String sql = String.format("<script>\nINSERT IGNORE INTO %s %s VALUES %s\n</script>", tableInfo
                .getTableName(), columnScript, valuesScript);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this
                .addInsertMappedStatement(mapperClass, modelClass, "ignore", sqlSource, new NoKeyGenerator(), null, null);
    }
}
