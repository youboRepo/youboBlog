package com.youbo.exception;

import lombok.Getter;

import java.util.List;
import java.util.Map;

/**
 * 基础异常
 *
 * @author liunancun
 * @date 2020/4/8
 */
@Getter
public class BaseException extends RuntimeException
{
    /**
     * 序列化标识
     */
    private static final long serialVersionUID = 1L;

    /**
     * 错误代码
     */
    private int code = 50000;

    /**
     * 错误信息
     */
    private String message;

    /**
     * 错误列表
     */
    private List<String> errorList;

    /**
     * 错误映射
     */
    private Map<String, String> errorMap;

    public BaseException(String message)
    {
        super(message);
        this.message = message;
    }

    public BaseException(String message, List<String> errorList)
    {
        super(message);
        this.message = message;
        this.errorList = errorList;
    }

    public BaseException(String message, Map<String, String> errorMap)
    {
        super(message);
        this.message = message;
        this.errorMap = errorMap;
    }

    public BaseException(String message, Throwable e)
    {
        super(message, e);
        this.message = message;
    }

    public BaseException(String message, Throwable e, List<String> errorList)
    {
        super(message, e);
        this.message = message;
        this.errorList = errorList;
    }

    public BaseException(String message, Throwable e, Map<String, String> errorMap)
    {
        super(message, e);
        this.message = message;
        this.errorMap = errorMap;
    }

    public BaseException(int code, String message)
    {
        super(message);
        this.code = code;
        this.message = message;
    }

    public BaseException(int code, String message, List<String> errorList)
    {
        super(message);
        this.code = code;
        this.message = message;
        this.errorList = errorList;
    }

    public BaseException(int code, String message, Map<String, String> errorMap)
    {
        super(message);
        this.code = code;
        this.message = message;
        this.errorMap = errorMap;
    }

    public BaseException(int code, String message, Throwable e)
    {
        super(message, e);
        this.code = code;
        this.message = message;
    }

    public BaseException(int code, String message, Throwable e, List<String> errorList)
    {
        super(message, e);
        this.code = code;
        this.message = message;
        this.errorList = errorList;
    }

    public BaseException(int code, String message, Throwable e, Map<String, String> errorMap)
    {
        super(message, e);
        this.code = code;
        this.message = message;
        this.errorMap = errorMap;
    }
}
