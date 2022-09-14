package com.youbo.jdbc.aspect;

import cn.hutool.core.util.StrUtil;
import com.youbo.util.ContextUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;
import org.springframework.expression.EvaluationContext;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Arrays;
import java.util.Optional;

/**
 * 动态表名切面类
 *
 * @author majiawen
 * @date 2022/07/06
 */
@Aspect
@Configuration
public class DynamicTableNameAspect
{
    /**
     * 表达式解析器
     */
    private static final ExpressionParser EXPRESSION_PARSER = new SpelExpressionParser();

    @Before("@annotation(dynamicTableName)")
    public void begin(JoinPoint joinPoint, DynamicTableName dynamicTableName)
    {
        // 获取方法参数
        Object[] args = joinPoint.getArgs();

        // 获取参数名字
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] paramNames = signature.getParameterNames();

        EvaluationContext evaluationContext = new StandardEvaluationContext();
        for (int i = 0; i < paramNames.length; i++)
        {
            evaluationContext.setVariable(paramNames[i], args[i]);
        }

        // 设置表名后缀
        Optional.of(dynamicTableName.suffix()).map(EXPRESSION_PARSER::parseExpression)
                .map(expression -> expression.getValue(evaluationContext, String.class)).filter(StrUtil::isNotBlank)
                .ifPresent(suffix -> Arrays.asList(dynamicTableName.value())
                        .forEach(tableName -> ContextUtils.setContext(tableName, suffix)));
    }
}
