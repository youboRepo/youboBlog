package com.youbo.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 关于表查询对象
 *
 * @author youbo
 * @date 2022/9/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AboutQuery extends PageQuery {
    /**
     * 序列化标识
     */
    private static final long serialVersionUID = 1L;

    /**
     * 英文名称列表查询
     */
    List<String> nameEns;
}
