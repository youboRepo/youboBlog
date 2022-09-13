package com.youbo.mapper;

import com.youbo.model.dto.Friend;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @Description: 友链持久层接口
 * @Author: Naccl
 * @Date: 2020-09-08
 */
@Mapper
@Repository
public interface FriendMapper {
	List<com.youbo.entity.Friend> getFriendList();

	List<com.youbo.model.vo.Friend> getFriendVOList();

	int updateFriendPublishedById(Long id, Boolean published);

	int saveFriend(com.youbo.entity.Friend friend);

	int updateFriend(Friend friend);

	int deleteFriend(Long id);

	int updateViewsByNickname(String nickname);
}
