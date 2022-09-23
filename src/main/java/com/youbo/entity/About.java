package com.youbo.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 关于我实体类
 *
 * @author youxiaobo
 * @date 2022/09/14
 */
@Data
@TableName("about")
public class About implements Serializable
{
	/**
	 * 序列化标识
	 */
	private static final long serialVersionUID = 1L;

	@TableId(type = IdType.AUTO)
	private Long id;

	/**
	 * 英文名称
	 */
	private String nameEn;

	/**
	 * 中文名称
	 */
	private String nameZh;

	/**
	 * 值
	 */
	private String value;
}
