package com.youbo.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 博客表查询对象
 *
 * @author Administrator
 * @date 2022/9/14
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class BlogQuery extends PageQuery {
    /**
     * 序列化标识
     */
    private static final long serialVersionUID = 1L;

    /**
     * 博客标题模糊查询
     */
    private String title;

    /**
     * 分类标识等于查询
     */
    private Integer categoryId;

    /**
     * 博客内容模糊查询
     */
    private String content;

    /**
     * 是否公开等于查询
     */
    private Boolean isPublished;

    /**
     * 是否推荐等于查询
     */
    private Boolean isRecommend;

    /**
     * 博客内容模糊查询
     */
    private String password;
    
    /**
     * 是否查询博客内容
     */
    private Boolean isQueryContent;
}
