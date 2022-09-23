package com.youbo.model.vo;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description: 封装响应结果
 * @Author: youbo
 * @Date: 2020-07-19
 */

@NoArgsConstructor
@Getter
@Setter
@ToString
public class Result<T> {
	private Integer code;
	private String msg;
	private T data;
	
	private Map<String, Object> extra;

	private Result(Integer code, String msg) {
		this.code = code;
		this.msg = msg;
		this.data = null;
	}

	private Result(Integer code, String msg, T data) {
		this.code = code;
		this.msg = msg;
		this.data = data;
	}

	public static <T> Result<T> ok(String msg, T data) {
		return new Result(200, msg, data);
	}

	public static <T> Result<T> ok(String msg) {
		return new Result(200, msg);
	}

	public static <T> Result<T> error(String msg) {
		return new Result(500, msg);
	}

	public static <T> Result<T> error() {
		return new Result(500, "异常错误");
	}

	public static <T> Result<T> create(Integer code, String msg, T data) {
		return new Result(code, msg, data);
	}

	public static <T> Result<T> create(Integer code, String msg) {
		return new Result(code, msg);
	}

	public Result<T> put(String key, Object value) {
		if (StringUtils.isNotBlank(key) || value != null) {
			if (this.extra == null) {
				this.extra = new HashMap(16);
			}

			this.extra.put(key, value);
		}

		return this;
	}

}
