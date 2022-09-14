package com.youbo.query;

import lombok.Data;

import java.io.Serializable;

/**
 * 分页查询对象
 *
 * @author liunancun
 * @date 2021/7/23
 */
@Data
public class PageQuery implements Serializable
{
    /**
     * 序列化标识
     */
    private static final long serialVersionUID = 1L;

    /**
     * 当前页码
     */
    private long current = 1L;

    /**
     * 每页数量
     */
    private long size = 10L;
}
