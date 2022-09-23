package com.youbo.service;

import com.youbo.model.dto.Friend;
import com.youbo.model.vo.FriendInfo;

import java.util.List;

public interface FriendService {
	List<com.youbo.entity.Friend> getFriendList();

	List<com.youbo.model.vo.Friend> getFriendVOList();

	void updateFriendPublishedById(Long friendId, Boolean published);

	void saveFriend(com.youbo.entity.Friend friend);

	void updateFriend(Friend friend);

	void deleteFriend(Long id);

	void updateViewsByNickname(String nickname);

	FriendInfo getFriendInfo(boolean cache, boolean md);

	void updateFriendInfoContent(String content);

	void updateFriendInfoCommentEnabled(Boolean commentEnabled);
}
