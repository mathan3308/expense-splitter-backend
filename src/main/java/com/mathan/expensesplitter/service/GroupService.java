package com.mathan.expensesplitter.service;

import com.mathan.expensesplitter.dto.auth.group.CreateGroupRequest;
import com.mathan.expensesplitter.dto.auth.group.GroupResponse;

import java.util.List;

public interface GroupService {

    GroupResponse createGroup(CreateGroupRequest request);

    List<GroupResponse> getMyGroups();

    GroupResponse getGroup(Long id);

    void addMember(Long groupId, String email);

    void removeMember(Long groupId, Long memberUserId);

}