package com.youbo.mapper;

import com.youbo.model.dto.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;
import com.youbo.model.vo.PageComment;

import java.util.List;

/**
 * @Description: 博客评论持久层接口
 * @Author: youbo
 * @Date: 2020-08-03
 */
@Mapper
@Repository
public interface CommentMapper {
	List<com.youbo.entity.Comment> getListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId);

	List<com.youbo.entity.Comment> getListByParentCommentId(Long parentCommentId);

	List<PageComment> getPageCommentListByPageAndParentCommentId(Integer page, Long blogId, Long parentCommentId);

	com.youbo.entity.Comment getCommentById(Long id);

	int updateCommentPublishedById(Long commentId, Boolean published);

	int updateCommentNoticeById(Long commentId, Boolean notice);

	int deleteCommentById(Long commentId);

	int deleteCommentsByBlogId(Long blogId);

	int updateComment(com.youbo.entity.Comment comment);

	int countByPageAndIsPublished(Integer page, Long blogId, Boolean isPublished);

	int countComment();

	int saveComment(Comment comment);
}
