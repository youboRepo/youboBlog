package com.youbo.query;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

/**
 * 列表分页对象
 *
 * @author liunancun
 * @date 2020/7/31
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ListPage<T> implements Serializable
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

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<T> records = new ArrayList<>();

    /**
     * 存在更多
     */
    private boolean more;

    /**
     * 下页地址
     */
    private String nextUrl;

    /**
     * 构造函数
     *
     * @param current
     * @param size
     * @param total
     * @param records
     */
    public ListPage(long current, long size, long total, List<T> records)
    {
        this.current = current;
        this.size = size;
        this.total = total;
        this.records = records;
    }

    /**
     * 克隆方法
     *
     * @param action
     * @param clazz
     * @param <R>
     * @return
     */
    public <R> ListPage<R> clone(BiFunction<List<T>, Class<R>, List<R>> action, Class<R> clazz)
    {
        return new ListPage<>(this.current, this.size, this.total, action.apply(this.records, clazz));
    }
}
