package com.youbo.query;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 分页传输对象
 *
 * @author liunancun
 * @date 2021/7/23
 */
@Getter
@AllArgsConstructor
public class PageDTO<T> implements Serializable
{
    /**
     * 序列化标识
     */
    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private long total;

    /**
     * 列表数据
     */
    private List<T> records;

    /**
     * 克隆方法
     *
     * @param action
     * @param clazz
     * @param <R>
     * @return
     */
    public <R> PageDTO<R> clone(BiFunction<List<T>, Class<R>, List<R>> action, Class<R> clazz)
    {
        return new PageDTO<>(this.total, action.apply(this.records, clazz));
    }

    /**
     * 克隆方法
     *
     * @param mapper
     * @param <R>
     * @return
     */
    public <R> PageDTO<R> clone(Function<Collection<T>, List<R>> mapper)
    {
        return new PageDTO<>(this.total, mapper.apply(this.records));
    }

    /**
     * 循环方法
     *
     * @param action
     */
    public void forEach(Consumer<? super T> action)
    {
        Optional.ofNullable(this.records).orElse(Collections.emptyList()).forEach(action);
    }
}
