package com.youbo.jdbc.injector.method;

import com.baomidou.mybatisplus.core.injector.AbstractMethod;
import com.baomidou.mybatisplus.core.metadata.TableInfo;
import com.baomidou.mybatisplus.core.toolkit.sql.SqlScriptUtils;
import org.apache.ibatis.executor.keygen.NoKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.mapping.SqlSource;

/**
 * 插入一条数据（更新已经存在）
 *
 * @author liunancun
 * @date 2021/3/31
 */
public class Duplicate extends AbstractMethod
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
        String updateScript = SqlScriptUtils.convertTrim(this.getKeySqlSet(tableInfo) + tableInfo
                .getAllSqlSet(tableInfo.isWithLogicDelete(), EMPTY), null, null, null, COMMA);
        String sql = String
                .format("<script>\nINSERT INTO %s %s VALUES %s ON DUPLICATE KEY UPDATE %s\n</script>", tableInfo
                        .getTableName(), columnScript, valuesScript, updateScript);
        SqlSource sqlSource = languageDriver.createSqlSource(configuration, sql, modelClass);
        return this
                .addInsertMappedStatement(mapperClass, modelClass, "duplicate", sqlSource, new NoKeyGenerator(), null, null);
    }

    /**
     * 获取主键语句设值片段
     *
     * @param tableInfo
     * @return
     */
    private String getKeySqlSet(TableInfo tableInfo)
    {
        if (tableInfo.havePK())
        {
            return tableInfo.getKeyColumn() + EQUALS + tableInfo.getKeyColumn() + COMMA + NEWLINE;
        }
        return EMPTY;
    }
}
