package com.youbo.service;

import com.youbo.model.dto.Comment;
import com.youbo.model.vo.PageComment;

import java.util.List;

public interface CommentService {
	List<com.youbo.entity.Comment> getListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId);

	List<PageComment> getPageCommentList(Integer page, Long blogId, Long parentCommentId);

	com.youbo.entity.Comment getCommentById(Long id);

	void updateCommentPublishedById(Long commentId, Boolean published);

	void updateCommentNoticeById(Long commentId, Boolean notice);

	void deleteCommentById(Long commentId);

	void deleteCommentsByBlogId(Long blogId);

	void updateComment(com.youbo.entity.Comment comment);

	int countByPageAndIsPublished(Integer page, Long blogId, Boolean isPublished);

	void saveComment(Comment comment);
}
