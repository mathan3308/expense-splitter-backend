package com.mathan.expensesplitter.service;

import com.mathan.expensesplitter.dto.auth.group.CreateGroupRequest;
import com.mathan.expensesplitter.dto.auth.group.GroupResponse;
import com.mathan.expensesplitter.dto.auth.group.MemberResponse;

import java.util.List;

public interface GroupService {

    GroupResponse createGroup(CreateGroupRequest request);

    List<GroupResponse> getMyGroups();

    GroupResponse getGroup(Long id);

    List<MemberResponse> getMembers(Long groupId);

    void addMember(Long groupId, String email);

    void removeMember(Long groupId, Long memberUserId);

}