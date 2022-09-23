package com.youbo.jdbc.impl;

import cn.hutool.core.collection.ListUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.enums.SqlMethod;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.core.toolkit.StringPool;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.core.toolkit.support.SFunction;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.SqlHelper;
import com.youbo.exception.BaseException;
import com.youbo.jdbc.mapper.MyBaseMapper;
import com.youbo.query.ListPage;
import com.youbo.query.PageDTO;
import com.youbo.query.PageQuery;
import com.youbo.util.BeanUtils;
import com.youbo.util.StringUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.ibatis.binding.MapperMethod;
import org.springframework.util.Assert;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * 我的服务实现类
 *
 * @author liunancun
 * @date 2020/9/1
 */
public class MyServiceImpl<M extends MyBaseMapper<E>, E> extends ServiceImpl<M, E>
{
    /**
     * 插入实体对象
     *
     * @param entity 实体对象
     * @return 是否成功
     */
    protected boolean insert(E entity)
    {
        return Optional.ofNullable(entity).map(this::save).orElse(false);
    }

    /**
     * 插入传输对象
     *
     * @param dto    传输对象
     * @param mapper 映射方法
     * @param getId  获取主键
     * @param <D>    传输类型
     * @param <I>    主键类型
     * @return 返回主键
     */
    protected <D, I extends Serializable> I insert(D dto, Function<D, E> mapper, Function<E, I> getId)
    {
        return Optional.ofNullable(dto).map(mapper).filter(this::save).map(getId).orElse(null);
    }

    /**
     * 插入传输对象
     *
     * @param dto    传输对象
     * @param action 操作方法
     * @param mapper 映射方法
     * @param getId  获取主键
     * @param <D>    传输类型
     * @param <I>    主键类型
     * @return 返回主键
     */
    protected <D, I extends Serializable> I insert(D dto, Consumer<D> action, Function<D, E> mapper, Function<E, I> getId)
    {
        Optional.ofNullable(dto).ifPresent(action);
        return this.insert(dto, mapper, getId);
    }

    /**
     * 插入实体列表
     *
     * @param entityList 实体列表
     * @return 是否成功
     */
    protected boolean insert(Collection<? extends E> entityList)
    {
        if (CollectionUtils.isEmpty(entityList))
        {
            return false;
        }

        String sqlStatement = getSqlStatement(SqlMethod.INSERT_ONE);
        return executeBatch(entityList, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
    }

    /**
     * 更新实体对象
     *
     * @param entity 实体对象
     * @return 是否成功
     */
    protected boolean update(E entity)
    {
        return Optional.ofNullable(entity).map(this::updateById).orElse(false);
    }

    /**
     * 更新传输对象
     *
     * @param dto    传输对象
     * @param mapper 映射方法
     * @param <D>    传输类型
     * @return 是否成功
     */
    protected <D> boolean update(D dto, Function<D, E> mapper)
    {
        return Optional.ofNullable(dto).map(mapper).map(this::updateById).orElse(false);
    }

    /**
     * 更新传输对象
     *
     * @param dto    传输对象
     * @param action 操作方法
     * @param mapper 映射方法
     * @param <D>    传输类型
     * @return 是否成功
     */
    protected <D> boolean update(D dto, Consumer<D> action, Function<D, E> mapper)
    {
        Optional.ofNullable(dto).ifPresent(action);
        return this.update(dto, mapper);
    }

    /**
     * 更新实体列表
     *
     * @param entityList 实体列表
     * @return 是否成功
     */
    protected boolean update(Collection<? extends E> entityList)
    {
        if (CollectionUtils.isEmpty(entityList))
        {
            return false;
        }

        String sqlStatement = getSqlStatement(SqlMethod.UPDATE_BY_ID);
        return executeBatch(entityList, (sqlSession, entity) -> {
            MapperMethod.ParamMap<E> param = new MapperMethod.ParamMap<>();
            param.put(Constants.ENTITY, entity);
            sqlSession.update(sqlStatement, param);
        });
    }

    /**
     * 更新传输列表
     *
     * @param dtoList 传输列表
     * @param mapper  映射方法
     * @param <D>     传输类型
     * @return 是否成功
     */
    protected <D> boolean update(Collection<D> dtoList, Function<Collection<D>, Collection<E>> mapper)
    {
        return Optional.ofNullable(dtoList).map(mapper).map(this::updateBatchById).orElse(false);
    }

    /**
     * 更新传输列表
     *
     * @param dtoList 传输列表
     * @param action  操作方法
     * @param mapper  映射方法
     * @param <D>     传输类型
     * @return 是否成功
     */
    protected <D> boolean update(Collection<D> dtoList, Consumer<D> action, Function<Collection<D>, Collection<E>> mapper)
    {
        if (CollectionUtils.isNotEmpty(dtoList))
        {
            dtoList.forEach(action);
        }
        return this.update(dtoList, mapper);
    }

    /**
     * 根据主键删除
     *
     * @param id 主键的值
     * @return 是否成功
     */
    protected boolean remove(Serializable id)
    {
        return Optional.ofNullable(id).map(this::removeById).orElse(false);
    }

    /**
     * 根据主键删除
     *
     * @param ids 主键列表
     * @return 是否成功
     */
    protected boolean remove(Collection<? extends Serializable> ids)
    {
        return this.removeByIds(ids);
    }

    /**
     * 根据条件删除
     *
     * @param column 条件字段
     * @param value  条件的值
     * @return 是否成功
     */
    protected boolean remove(SFunction<E, ?> column, Object value)
    {
        if (ObjectUtil.hasNull(column, value) || "".equals(value))
        {
            return false;
        }

        LambdaQueryWrapper<E> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(column, value);
        return this.remove(queryWrapper);
    }

    /**
     * 根据条件删除
     *
     * @param column 条件字段
     * @param values 条件列表
     * @return 是否成功
     */
    protected boolean remove(SFunction<E, ?> column, Collection<?> values)
    {
        if (column == null || CollectionUtils.isEmpty(values))
        {
            return false;
        }

        LambdaQueryWrapper<E> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(column, values);
        return this.remove(queryWrapper);
    }

    /**
     * 插入实体对象存在则忽略
     *
     * @param entity 实体对象
     * @return 是否成功
     */
    protected boolean ignore(E entity)
    {
        return Optional.ofNullable(entity).map(baseMapper::ignore).map(SqlHelper::retBool).orElse(false);
    }

    /**
     * 插入实体列表存在则忽略
     *
     * @param entityList 实体列表
     * @return 是否成功
     */
    protected boolean ignore(Collection<? extends E> entityList)
    {
        if (CollectionUtils.isEmpty(entityList))
        {
            return false;
        }

        return this.executeBatch(entityList, "ignore");
    }

    /**
     * 插入实体对象存在则替换
     *
     * @param entity 实体对象
     * @return 是否成功
     */
    protected boolean replace(E entity)
    {
        return Optional.ofNullable(entity).map(baseMapper::replace).map(SqlHelper::retBool).orElse(false);
    }

    /**
     * 插入实体列表存在则替换
     *
     * @param entityList 实体列表
     * @return 是否成功
     */
    protected boolean replace(Collection<? extends E> entityList)
    {
        if (CollectionUtils.isEmpty(entityList))
        {
            return false;
        }

        return this.executeBatch(entityList, "replace");
    }

    /**
     * 插入实体对象存在则更新
     *
     * @param entity 实体对象
     * @return 是否成功
     */
    protected boolean duplicate(E entity)
    {
        return Optional.ofNullable(entity).map(baseMapper::duplicate).map(SqlHelper::retBool).orElse(false);
    }

    /**
     * 插入实体列表存在则更新
     *
     * @param entityList 实体列表
     * @return 是否成功
     */
    protected boolean duplicate(Collection<? extends E> entityList)
    {
        if (CollectionUtils.isEmpty(entityList))
        {
            return false;
        }

        return this.executeBatch(entityList, "duplicate");
    }

    /**
     * 根据主键获取实体对象
     *
     * @param id 主键的值
     * @return 实体对象
     */
    protected E get(Serializable id)
    {
        Assert.notNull(id, "主键不能为空");
        return this.getById(id);
    }

    /**
     * 根据主键获取传输对象
     *
     * @param id     主键的值
     * @param mapper 映射方法
     * @param <D>    传输类型
     * @return 传输对象
     */
    protected <D> D get(Serializable id, Function<E, D> mapper)
    {
        return Optional.ofNullable(id).map(this::getById).map(mapper).orElse(null);
    }

    /**
     * 根据主键获取传输对象
     *
     * @param id     主键的值
     * @param mapper 映射方法
     * @param action 操作方法
     * @param <D>    传输类型
     * @return 传输对象
     */
    protected <D> D get(Serializable id, Function<E, D> mapper, Consumer<D> action)
    {
        D dto = this.get(id, mapper);
        action.accept(dto);
        return dto;
    }

    /**
     * 根据主键获取传输对象
     *
     * @param id    主键的值
     * @param clazz 传输的类
     * @param <D>   传输类型
     * @return 传输对象
     */
    protected <D> D get(Serializable id, Class<D> clazz)
    {
        return BeanUtils.clone(this.get(id), clazz);
    }

    /**
     * 根据条件获取实体对象
     *
     * @param queryWrapper 查询条件
     * @return 实体对象
     */
    protected E get(LambdaQueryWrapper<E> queryWrapper)
    {
        Assert.notNull(queryWrapper, "条件不能为空");
        queryWrapper.last("LIMIT 1");
        return this.getOne(queryWrapper);
    }

    /**
     * 根据条件获取实体对象
     *
     * @param consumer 设置条件
     * @return 实体对象
     */
    protected E get(Consumer<LambdaQueryWrapper<E>> consumer)
    {
        LambdaQueryWrapper<E> queryWrapper = Wrappers.lambdaQuery();
        consumer.accept(queryWrapper);
        queryWrapper.last("LIMIT 1");
        return this.get(queryWrapper);
    }

    /**
     * 根据条件获取传输对象
     *
     * @param consumer 设置条件
     * @param mapper   映射方法
     * @param <D>      传输类型
     * @return 传输对象
     */
    protected <D> D get(Consumer<LambdaQueryWrapper<E>> consumer, Function<E, D> mapper)
    {
        return Optional.ofNullable(consumer).map(this::get).map(mapper).orElse(null);
    }

    /**
     * 根据条件获取实体对象
     *
     * @param column 条件字段
     * @param value  条件的值
     * @return 实体对象
     */
    protected E get(SFunction<E, ?> column, Object value)
    {
        if (ObjectUtil.hasNull(column, value) || "".equals(value))
        {
            throw new BaseException("条件不能为空");
        }

        LambdaQueryWrapper<E> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(column, value);
        queryWrapper.last("LIMIT 1");
        return this.getOne(queryWrapper);
    }

    /**
     * 根据条件获取传输对象
     *
     * @param column 条件字段
     * @param value  条件的值
     * @param clazz  传输的类
     * @param <D>    传输类型
     * @return 传输对象
     */
    protected <D> D get(SFunction<E, ?> column, Object value, Class<D> clazz)
    {
        E entity = this.get(column, value);
        return BeanUtils.clone(entity, clazz);
    }

    /**
     * 根据条件获取实体对象
     *
     * @param query   查询对象
     * @param builder 构建方法
     * @param <Q>     查询类型
     * @return 实体对象
     */
    protected <Q> E get(Q query, Function<Q, LambdaQueryWrapper<E>> builder)
    {
        if (ObjectUtil.hasNull(query, builder))
        {
            throw new BaseException("条件不能为空");
        }

        LambdaQueryWrapper<E> queryWrapper = builder.apply(query);
        queryWrapper.last("LIMIT 1");
        return this.getOne(queryWrapper);
    }

    /**
     * 根据条件获取传输对象
     *
     * @param query   查询对象
     * @param builder 构建方法
     * @param clazz   传输的类
     * @param <Q>     查询类型
     * @param <D>     传输类型
     * @return 传输对象
     */
    protected <Q, D> D get(Q query, Function<Q, LambdaQueryWrapper<E>> builder, Class<D> clazz)
    {
        E entity = this.get(query, builder);
        return BeanUtils.clone(entity, clazz);
    }

    /**
     * 根据主键获取实体列表
     *
     * @param ids 主键列表
     * @return 实体列表
     */
    protected List<E> list(Collection<? extends Serializable> ids)
    {
        return Optional.ofNullable(ids).filter(CollectionUtils::isNotEmpty).map(this::listByIds)
                .orElseGet(ListUtil::empty);
    }

    /**
     * 根据主键获取传输列表
     *
     * @param ids    主键列表
     * @param mapper 映射方法
     * @param <D>    传输类型
     * @return 传输列表
     */
    protected <D> List<D> list(Collection<? extends Serializable> ids, Function<Collection<E>, List<D>> mapper)
    {
        return Optional.ofNullable(ids).filter(CollectionUtils::isNotEmpty).map(this::listByIds).map(mapper)
                .orElseGet(ListUtil::empty);
    }

    /**
     * 根据主键获取传输列表
     *
     * @param ids    主键列表
     * @param mapper 映射方法
     * @param action 操作方法
     * @param <D>    传输类型
     * @return 传输列表
     */
    protected <D> List<D> list(Collection<? extends Serializable> ids, Function<Collection<E>, List<D>> mapper, Consumer<D> action)
    {
        List<D> dtoList = this.list(ids, mapper);
        dtoList.forEach(action);
        return dtoList;
    }

    /**
     * 根据主键获取传输列表
     *
     * @param ids   主键列表
     * @param clazz 传输的类
     * @param <D>   传输类型
     * @return 传输对象
     */
    protected <D> List<D> list(Collection<? extends Serializable> ids, Class<D> clazz)
    {
        List<E> entityList = this.list(ids);
        return BeanUtils.clone(entityList, clazz);
    }

    /**
     * 根据条件获取传输列表
     *
     * @param queryWrapper 查询条件
     * @param clazz        传输的类
     * @param <D>          传输类型
     * @return 传输列表
     */
    protected <D> List<D> list(Wrapper<E> queryWrapper, Class<D> clazz)
    {
        List<E> entityList = this.list(queryWrapper);
        return BeanUtils.clone(entityList, clazz);
    }

    /**
     * 根据条件获取实体列表
     *
     * @param consumer 设置条件
     * @return 实体列表
     */
    protected List<E> list(Consumer<LambdaQueryWrapper<E>> consumer)
    {
        LambdaQueryWrapper<E> queryWrapper = Wrappers.lambdaQuery();
        consumer.accept(queryWrapper);
        return this.list(queryWrapper);
    }

    /**
     * 根据条件获取传输列表
     *
     * @param consumer 设置条件
     * @param clazz    传输的类
     * @param <D>      传输类型
     * @return 传输列表
     */
    protected <D> List<D> list(Consumer<LambdaQueryWrapper<E>> consumer, Class<D> clazz)
    {
        List<E> entityList = this.list(consumer);
        return BeanUtils.clone(entityList, clazz);
    }

    /**
     * 根据条件获取实体列表
     *
     * @param query   查询对象
     * @param builder 构建方法
     * @param <Q>     查询类型
     * @return 实体列表
     */
    protected <Q> List<E> list(Q query, Function<Q, Wrapper<E>> builder)
    {
        return Optional.ofNullable(query).map(builder).map(this::list).orElseGet(ListUtil::empty);
    }

    /**
     * 根据条件获取传输列表
     *
     * @param query   查询对象
     * @param builder 构建方法
     * @param mapper  映射方法
     * @param <Q>     查询类型
     * @param <D>     传输类型
     * @return 传输列表
     */
    protected <Q, D> List<D> list(Q query, Function<Q, Wrapper<E>> builder, Function<Collection<E>, List<D>> mapper)
    {
        return Optional.ofNullable(query).map(builder).map(this::list).map(mapper).orElseGet(ListUtil::empty);
    }

    /**
     * 根据条件获取传输列表
     *
     * @param query   查询对象
     * @param builder 构建方法
     * @param mapper  映射方法
     * @param action  操作方法
     * @param <Q>     查询类型
     * @param <D>     传输类型
     * @return 传输列表
     */
    protected <Q, D> List<D> list(Q query, Function<Q, Wrapper<E>> builder, Function<Collection<E>, List<D>> mapper, Consumer<D> action)
    {
        List<D> dtoList = this.list(query, builder, mapper);
        dtoList.forEach(action);
        return dtoList;
    }

    /**
     * 根据条件获取传输列表
     *
     * @param query   查询对象
     * @param builder 构建方法
     * @param clazz   传输的类
     * @param <Q>     查询类型
     * @param <D>     传输类型
     * @return 传输列表
     */
    protected <Q, D> List<D> list(Q query, Function<Q, Wrapper<E>> builder, Class<D> clazz)
    {
        List<E> entityList = this.list(query, builder);
        return BeanUtils.clone(entityList, clazz);
    }

    /**
     * 根据条件获取指定字段的传输列表
     *
     * @param query   查询对象
     * @param builder 构建方法
     * @param clazz   传输的类
     * @param <Q>     查询类型
     * @param <D>     传输类型
     * @param columns  要查询的字段
     * @return 传输列表
     */
    protected <Q, D> List<D> list(Q query, Function<Q, LambdaQueryWrapper<E>> builder, Class<D> clazz, SFunction<E, ?>... columns)
    {
        LambdaQueryWrapper<E> queryWrapper = builder.apply(query);
        queryWrapper.select(columns);
        if (ArrayUtils.isNotEmpty(columns)) {
            for (SFunction<E, ?> column : columns)
            {
                queryWrapper.select(column);
            }
        }
        
        List<E> entityList = this.list(queryWrapper);
        return BeanUtils.clone(entityList, clazz);
    }

    /**
     * 根据条件获取指定字段的传输列表
     *
     * @param query   查询对象
     * @param builder 构建方法
     * @param <Q>     查询类型
     * @param <D>     传输类型
     * @param columns  要查询的字段
     * @return 传输列表
     */
    protected <Q, D> List<E> list(Q query, Function<Q, LambdaQueryWrapper<E>> builder, SFunction<E, ?>... columns)
    {
        LambdaQueryWrapper<E> queryWrapper = builder.apply(query);
        queryWrapper.select(columns);
        if (ArrayUtils.isNotEmpty(columns)) {
            for (SFunction<E, ?> column : columns)
            {
                queryWrapper.select(column);
            }
        }

        return this.list(queryWrapper);
    }

    /**
     * 根据条件获取实体列表
     *
     * @param column 条件字段
     * @param value  条件的值
     * @return 实体列表
     */
    protected List<E> list(SFunction<E, ?> column, Object value)
    {
        if (ObjectUtil.hasNull(column, value) || "".equals(value))
        {
            return ListUtil.empty();
        }

        LambdaQueryWrapper<E> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(column, value);
        return this.list(queryWrapper);
    }


    /**
     * 根据条件统计数量
     *
     * @param column 条件字段
     * @param value  条件的值
     * @return 数量
     */
    protected Integer count(SFunction<E, ?> column, Object value) {
        if (ObjectUtil.hasNull(column, value) || "".equals(value))
        {
            return 0;
        }

        LambdaQueryWrapper<E> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(column, value);
        return this.count(queryWrapper);
    }

    /**
     * 根据条件获取传输列表
     *
     * @param column 条件字段
     * @param value  条件的值
     * @param clazz  传输的类
     * @param <D>    传输类型
     * @return 传输列表
     */
    protected <D> List<D> list(SFunction<E, ?> column, Object value, Class<D> clazz)
    {
        List<E> entityList = this.list(column, value);
        return BeanUtils.clone(entityList, clazz);
    }

    /**
     * 根据条件获取传输列表
     *
     * @param column1 条件字段1
     * @param value1  条件的值1
     * @param column2 条件字段2
     * @param value2  条件的值2
     * @return 实体列表
     */
    protected List<E> list(SFunction<E, ?> column1, Object value1, SFunction<E, ?> column2, Object value2)
    {
        if (ObjectUtil.hasNull(column1, value1, column2, value2) || "".equals(value1) || "".equals(value2))
        {
            return ListUtil.empty();
        }

        LambdaQueryWrapper<E> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(column1, value1);
        queryWrapper.eq(column2, value2);
        return this.list(queryWrapper);
    }

    /**
     * 根据条件获取实体列表
     *
     * @param column 条件字段
     * @param values 条件列表
     * @return 实体列表
     */
    protected List<E> list(SFunction<E, ?> column, Collection<?> values)
    {
        if (column == null || CollectionUtils.isEmpty(values))
        {
            return ListUtil.empty();
        }

        LambdaQueryWrapper<E> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.in(column, values);
        return this.list(queryWrapper);
    }

    /**
     * 根据条件获取传输列表
     *
     * @param column 条件字段
     * @param values 条件列表
     * @param clazz  传输的类
     * @param <D>    传输类型
     * @return 传输列表
     */
    protected <D> List<D> list(SFunction<E, ?> column, Collection<?> values, Class<D> clazz)
    {
        List<E> entityList = this.list(column, values);
        return BeanUtils.clone(entityList, clazz);
    }

    /**
     * 根据条件获取实体分页
     *
     * @param query   查询对象
     * @param builder 构建方法
     * @param <Q>     查询类型
     * @return 实体分页
     */
    protected <Q extends PageQuery> PageDTO<E> page(Q query, Function<Q, Wrapper<E>> builder)
    {
        IPage<E> page = new Page<>(query.getCurrent(), query.getSize());
        Wrapper<E> queryWrapper = builder.apply(query);
        page = this.page(page, queryWrapper);
        return new PageDTO<>(page.getTotal(), page.getRecords());
    }

    /**
     * 根据条件获取传输分页
     *
     * @param query   查询对象
     * @param builder 构建方法
     * @param mapper  映射方法
     * @param <Q>     查询类型
     * @param <D>     传输类型
     * @return 传输分页
     */
    protected <Q extends PageQuery, D> PageDTO<D> page(Q query, Function<Q, Wrapper<E>> builder, Function<Collection<E>, List<D>> mapper)
    {
        return this.page(query, builder).clone(mapper);
    }

    /**
     * 根据条件获取传输分页
     *
     * @param query   查询对象
     * @param builder 构建方法
     * @param clazz   传输的类
     * @param <Q>     查询类型
     * @param <D>     传输类型
     * @return 传输分页
     */
    protected <Q extends PageQuery, D> PageDTO<D> page(Q query, Function<Q, Wrapper<E>> builder, Class<D> clazz)
    {
        return this.page(query, builder).clone(BeanUtils::clone, clazz);
    }

    /**
     * 根据条件获取实体分页
     *
     * @param query   查询对象
     * @param builder 构建方法
     * @param current 当前页码
     * @param size    每页数量
     * @param <Q>     查询类型
     * @return 实体分页
     */
    protected <Q> PageDTO<E> page(Q query, Function<Q, Wrapper<E>> builder, long current, long size)
    {
        IPage<E> page = new Page<>(current, size);
        Wrapper<E> queryWrapper = builder.apply(query);
        page = this.page(page, queryWrapper);
        return new PageDTO<>(page.getTotal(), page.getRecords());
    }

    /**
     * 根据条件获取实体分页
     * <p>
     * 后面改用返回PageDTO对象方法
     *
     * @param queryWrapper 查询条件
     * @param current      当前页码
     * @param size         每页数量
     * @return 实体分页
     */
    @Deprecated
    protected ListPage<E> page(Wrapper<E> queryWrapper, long current, long size)
    {
        IPage<E> page = new Page<>(current, size);
        page = this.page(page, queryWrapper);
        return new ListPage<>(current, size, page.getTotal(), page.getRecords());
    }

    /**
     * 拼接等于查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void eq(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, Object value)
    {
        if (ObjectUtil.hasNull(queryWrapper, column, value) || "".equals(value))
        {
            return;
        }

        queryWrapper.eq(column, value);
    }

    /**
     * 拼接不等查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void ne(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, Object value)
    {
        if (ObjectUtil.hasNull(queryWrapper, column, value) || "".equals(value))
        {
            return;
        }

        queryWrapper.ne(column, value);
    }

    /**
     * 拼接模糊查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void like(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, String value)
    {
        if (ObjectUtil.hasNull(queryWrapper, column) || org.apache.commons.lang3.StringUtils.isBlank(value))
        {
            return;
        }

        queryWrapper.like(column, value);
    }

    /**
     * 拼接模糊查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param values       条件列表
     */
    protected void like(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, Collection<?> values)
    {
        if (ObjectUtil.hasNull(queryWrapper, column) || CollectionUtils.isEmpty(values))
        {
            return;
        }

        queryWrapper.and(item -> values.forEach(value -> item.like(column, value).or()));
    }

    /**
     * 拼接模糊查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void likeList(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, String value)
    {
        List<String> values = StringUtils.splitStringList(value);
        this.like(queryWrapper, column, values);
    }

    /**
     * 拼接模糊查询条件
     *
     * @param queryWrapper 查询条件
     * @param column1      条件字段1
     * @param column2      条件字段2
     * @param values       条件列表
     */
    protected void like(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column1, SFunction<E, ?> column2, Collection<?> values)
    {
        if (ObjectUtil.hasNull(queryWrapper, column1, column2) || CollectionUtils.isEmpty(values))
        {
            return;
        }

        queryWrapper.and(item -> values.forEach(value -> {
            item.like(column1, value).or();
            item.like(column2, value).or();
        }));
    }

    /**
     * 拼接模糊查询条件
     *
     * @param queryWrapper 查询条件
     * @param column1      条件字段1
     * @param column2      条件字段2
     * @param value        条件的值
     */
    protected void likeList(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column1, SFunction<E, ?> column2, String value)
    {
        List<String> values = StringUtils.splitStringList(value);
        this.like(queryWrapper, column1, column2, values);
    }

    /**
     * 拼接左边模糊查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void likeLeft(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, String value)
    {
        if (ObjectUtil.hasNull(queryWrapper, column) || org.apache.commons.lang3.StringUtils.isBlank(value))
        {
            return;
        }

        queryWrapper.likeLeft(column, value);
    }

    /**
     * 拼接左边模糊查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param values       条件列表
     */
    protected void likeLeft(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, Collection<?> values)
    {
        if (ObjectUtil.hasNull(queryWrapper, column) || CollectionUtils.isEmpty(values))
        {
            return;
        }

        queryWrapper.and(item -> values.forEach(value -> item.likeLeft(column, value).or()));
    }

    /**
     * 拼接左边模糊查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void likeLeftList(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, String value)
    {
        List<String> values = StringUtils.splitStringList(value);
        this.likeLeft(queryWrapper, column, values);
    }

    /**
     * 拼接左边模糊查询条件
     *
     * @param queryWrapper 查询条件
     * @param column1      条件字段1
     * @param column2      条件字段2
     * @param values       条件列表
     */
    protected void likeLeft(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column1, SFunction<E, ?> column2, Collection<?> values)
    {
        if (ObjectUtil.hasNull(queryWrapper, column1, column2) || CollectionUtils.isEmpty(values))
        {
            return;
        }

        queryWrapper.and(item -> values.forEach(value -> {
            item.likeLeft(column1, value).or();
            item.likeLeft(column2, value).or();
        }));
    }

    /**
     * 拼接左边模糊查询条件
     *
     * @param queryWrapper 查询条件
     * @param column1      条件字段1
     * @param column2      条件字段2
     * @param value        条件的值
     */
    protected void likeLeftList(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column1, SFunction<E, ?> column2, String value)
    {
        List<String> values = StringUtils.splitStringList(value);
        this.likeLeft(queryWrapper, column1, column2, values);
    }

    /**
     * 拼接右边模糊查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void likeRight(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, String value)
    {
        if (ObjectUtil.hasNull(queryWrapper, column) || org.apache.commons.lang3.StringUtils.isBlank(value))
        {
            return;
        }

        queryWrapper.likeRight(column, value);
    }

    /**
     * 拼接右边模糊查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param values       条件列表
     */
    protected void likeRight(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, Collection<?> values)
    {
        if (ObjectUtil.hasNull(queryWrapper, column) || CollectionUtils.isEmpty(values))
        {
            return;
        }

        queryWrapper.and(item -> values.forEach(value -> item.likeRight(column, value).or()));
    }

    /**
     * 拼接右边模糊查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void likeRightList(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, String value)
    {
        List<String> values = StringUtils.splitStringList(value);
        this.likeRight(queryWrapper, column, values);
    }

    /**
     * 拼接右边模糊查询条件
     *
     * @param queryWrapper 查询条件
     * @param column1      条件字段1
     * @param column2      条件字段2
     * @param values       条件列表
     */
    protected void likeRight(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column1, SFunction<E, ?> column2, Collection<?> values)
    {
        if (ObjectUtil.hasNull(queryWrapper, column1, column2) || CollectionUtils.isEmpty(values))
        {
            return;
        }

        queryWrapper.and(item -> values.forEach(value -> {
            item.likeRight(column1, value).or();
            item.likeRight(column2, value).or();
        }));
    }

    /**
     * 拼接右边模糊查询条件
     *
     * @param queryWrapper 查询条件
     * @param column1      条件字段1
     * @param column2      条件字段2
     * @param value        条件的值
     */
    protected void likeRightList(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column1, SFunction<E, ?> column2, String value)
    {
        List<String> values = StringUtils.splitStringList(value);
        this.likeRight(queryWrapper, column1, column2, values);
    }

    /**
     * 拼接模糊取反查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void notLike(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, String value)
    {
        if (ObjectUtil.hasNull(queryWrapper, column) || org.apache.commons.lang3.StringUtils.isBlank(value))
        {
            return;
        }

        queryWrapper.notLike(column, value);
    }

    /**
     * 拼接模糊取反查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param values       条件列表
     */
    protected void notLike(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, Collection<?> values)
    {
        if (ObjectUtil.hasNull(queryWrapper, column) || CollectionUtils.isEmpty(values))
        {
            return;
        }

        values.forEach(value -> queryWrapper.notLike(column, value));
    }

    /**
     * 拼接列表查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param values       条件列表
     */
    protected void in(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, Collection<?> values)
    {
        if (ObjectUtil.hasNull(queryWrapper, column) || CollectionUtils.isEmpty(values))
        {
            return;
        }

        queryWrapper.in(column, values);
    }

    /**
     * 拼接字符列表查询
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void inString(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, String value)
    {
        List<String> values = StringUtils.splitStringList(value);
        this.in(queryWrapper, column, values);
    }

    /**
     * 拼接整型列表查询
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void inInteger(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, String value)
    {
        List<Integer> values = StringUtils.splitIntegerList(value);
        this.in(queryWrapper, column, values);
    }

    /**
     * 拼接长整列表查询
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void inLong(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, String value)
    {
        List<Long> values = StringUtils.splitLongList(value);
        this.in(queryWrapper, column, values);
    }

    /**
     * 拼接集合取反查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param values       条件列表
     */
    protected void notIn(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, Collection<?> values)
    {
        if (ObjectUtil.hasNull(queryWrapper, column) || CollectionUtils.isEmpty(values))
        {
            return;
        }

        queryWrapper.notIn(column, values);
    }

    /**
     * 拼接大于查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void gt(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, Object value)
    {
        if (ObjectUtil.hasNull(queryWrapper, column, value))
        {
            return;
        }

        queryWrapper.gt(column, value);
    }

    /**
     * 拼接大于等于查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void ge(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, Object value)
    {
        if (ObjectUtil.hasNull(queryWrapper, column, value))
        {
            return;
        }

        queryWrapper.ge(column, value);
    }

    /**
     * 拼接小于查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void lt(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, Object value)
    {
        if (ObjectUtil.hasNull(queryWrapper, column, value))
        {
            return;
        }

        queryWrapper.lt(column, value);
    }

    /**
     * 拼接小于等于查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void le(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, Object value)
    {
        if (ObjectUtil.hasNull(queryWrapper, column, value))
        {
            return;
        }

        queryWrapper.le(column, value);
    }

    /**
     * 拼接范围查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param from         开始条件
     * @param to           结束条件
     */
    protected void range(LambdaQueryWrapper<E> queryWrapper, SFunction<E, ?> column, Object from, Object to)
    {
        if (ObjectUtil.hasNull(queryWrapper, column))
        {
            return;
        }

        if (from != null)
        {
            queryWrapper.ge(column, from);
        }

        if (to != null)
        {
            queryWrapper.le(column, to);
        }
    }

    /**
     * 在集合中查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void findInSet(LambdaQueryWrapper<E> queryWrapper, String column, Object value)
    {
        if (ObjectUtil.hasNull(queryWrapper, value) || org.apache.commons.lang3.StringUtils.isBlank(column) || "".equals(value))
        {
            return;
        }

        queryWrapper.apply("FIND_IN_SET({0}, " + column + ")", value);
    }

    /**
     * 在集合中查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param values       条件列表
     */
    protected void findInSet(LambdaQueryWrapper<E> queryWrapper, String column, Collection<?> values)
    {
        if (queryWrapper == null || org.apache.commons.lang3.StringUtils.isBlank(column) || CollectionUtils.isEmpty(values))
        {
            return;
        }

        queryWrapper.and(item -> values.forEach(value -> item.apply("FIND_IN_SET({0}, " + column + ")", value).or()));
    }

    /**
     * 在集合中取反查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param value        条件的值
     */
    protected void notFindInSet(LambdaQueryWrapper<E> queryWrapper, String column, Object value)
    {
        if (ObjectUtil.hasNull(queryWrapper, value) || org.apache.commons.lang3.StringUtils.isBlank(column) || "".equals(value))
        {
            return;
        }

        queryWrapper.apply("NOT FIND_IN_SET({0}, " + column + ")", value);
    }

    /**
     * 在集合中取反查询条件
     *
     * @param queryWrapper 查询条件
     * @param column       条件字段
     * @param values       条件的值
     */
    protected void notFindInSet(LambdaQueryWrapper<E> queryWrapper, String column, Collection<?> values)
    {
        if (queryWrapper == null || org.apache.commons.lang3.StringUtils.isBlank(column) || CollectionUtils.isEmpty(values))
        {
            return;
        }

        values.forEach(value -> queryWrapper.apply("NOT FIND_IN_SET({0}, " + column + ")", value));
    }

    /**
     * 聚合查询
     *
     * @param queryWrapper 查询条件
     * @param sql          条件语句
     * @param params       条件参数
     */
    protected void having(LambdaQueryWrapper<E> queryWrapper, String sql, Object... params)
    {
        if (queryWrapper == null || org.apache.commons.lang3.StringUtils.isBlank(sql))
        {
            return;
        }

        queryWrapper.having(sql, params);
    }

    /**
     * 限制数量
     *
     * @param queryWrapper 查询条件
     * @param value        限制数量
     */
    protected void limit(LambdaQueryWrapper<E> queryWrapper, Number value)
    {
        if (ObjectUtil.hasNull(queryWrapper, value))
        {
            return;
        }

        queryWrapper.last("LIMIT " + value);
    }

    @Override
    protected Class<E> currentMapperClass()
    {
        return (Class<E>) this.getResolvableType().as(ServiceImpl.class).getGeneric(0).resolve();
    }

    @Override
    protected Class<E> currentModelClass()
    {
        return (Class<E>) this.getResolvableType().as(ServiceImpl.class).getGeneric(1).resolve();
    }

    /**
     * 执行批量操作
     *
     * @param entityList 实体列表
     * @param method     执行方法
     * @return 是否成功
     */
    private boolean executeBatch(Collection<? extends E> entityList, String method)
    {
        String sqlStatement = mapperClass.getName() + StringPool.DOT + method;
        return executeBatch(entityList, (sqlSession, entity) -> sqlSession.insert(sqlStatement, entity));
    }
}
