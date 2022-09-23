package com.youbo.controller;

import com.youbo.annotation.VisitLogger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.youbo.enums.VisitBehavior;
import com.youbo.model.vo.BlogInfo;
import com.youbo.model.vo.PageResult;
import com.youbo.model.vo.Result;
import com.youbo.service.BlogService;

/**
 * @Description: 标签
 * @Author: youbo
 * @Date: 2020-08-17
 */
@RestController
public class TagController {
	@Autowired
	BlogService blogService;

	/**
	 * 根据标签name分页查询公开博客列表
	 *
	 * @param tagName 标签name
	 * @param pageNum 页码
	 * @return
	 */
	@VisitLogger(VisitBehavior.TAG)
	@GetMapping("/tag")
	public Result tag(@RequestParam String tagName,
	                  @RequestParam(defaultValue = "1") Integer pageNum) {
		PageResult<BlogInfo> pageResult = blogService.getBlogInfoListByTagNameAndIsPublished(tagName, pageNum);
		return Result.ok("请求成功", pageResult);
	}
}
