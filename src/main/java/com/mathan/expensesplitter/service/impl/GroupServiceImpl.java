package com.mathan.expensesplitter.service.impl;

import com.mathan.expensesplitter.dto.auth.group.CreateGroupRequest;
import com.mathan.expensesplitter.dto.auth.group.GroupResponse;
import com.mathan.expensesplitter.dto.auth.group.MemberResponse;
import com.mathan.expensesplitter.entity.ExpenseGroup;
import com.mathan.expensesplitter.entity.GroupMember;
import com.mathan.expensesplitter.entity.User;
import com.mathan.expensesplitter.exception.AccessDeniedException;
import com.mathan.expensesplitter.exception.GroupNotFoundException;
import com.mathan.expensesplitter.exception.InvalidExpenseException;
import com.mathan.expensesplitter.exception.UserNotFoundException;
import com.mathan.expensesplitter.repository.ExpenseGroupRepository;
import com.mathan.expensesplitter.repository.GroupMemberRepository;
import com.mathan.expensesplitter.repository.UserRepository;
import com.mathan.expensesplitter.security.SecurityUtils;
import com.mathan.expensesplitter.service.GroupService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GroupServiceImpl implements GroupService {

    private static final Logger log = LoggerFactory.getLogger(GroupServiceImpl.class);

    private final ExpenseGroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    public GroupServiceImpl(ExpenseGroupRepository groupRepository,
                            GroupMemberRepository groupMemberRepository,
                            UserRepository userRepository) {
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
    }

    private void validateMember(Long groupId) {
        if (!groupRepository.existsById(groupId)) {
            throw new GroupNotFoundException("Group not found");
        }
        Long currentUserId = SecurityUtils.getCurrentUser().getId();
        if (!groupMemberRepository.existsByExpenseGroupIdAndUserId(groupId, currentUserId)) {
            throw new AccessDeniedException("You are not a member of this group");
        }
    }

    @Override
    @Transactional
    public GroupResponse createGroup(CreateGroupRequest request) {

        Long userId = SecurityUtils.getCurrentUser().getId();

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        ExpenseGroup group = ExpenseGroup.builder()
                .name(request.getName())
                .description(request.getDescription())
                .build();

        ExpenseGroup savedGroup = groupRepository.save(group);

        GroupMember member = GroupMember.builder()
                .expenseGroup(savedGroup)
                .user(user)
                .build();

        groupMemberRepository.save(member);
        log.info("Expense group created successfully with ID: '{}' by user ID: {}", savedGroup.getId(), userId);

        return GroupResponse.builder()
                .id(savedGroup.getId())
                .name(savedGroup.getName())
                .description(savedGroup.getDescription())
                .build();
    }

    @Override
    public List<GroupResponse> getMyGroups() {
        Long userId = SecurityUtils.getCurrentUser().getId();
        List<GroupMember> members = groupMemberRepository.findByUserId(userId);

        return members.stream()
                .map(member -> {
                    ExpenseGroup group = member.getExpenseGroup();
                    return GroupResponse.builder()
                            .id(group.getId())
                            .name(group.getName())
                            .description(group.getDescription())
                            .build();
                })
                .toList();
    }

    @Override
    public GroupResponse getGroup(Long id) {
        validateMember(id);

        ExpenseGroup group = groupRepository.findById(id)
                .orElseThrow(() ->
                        new GroupNotFoundException("Group not found"));

        return GroupResponse.builder()
                .id(group.getId())
                .name(group.getName())
                .description(group.getDescription())
                .build();
    }

    @Override
    public List<MemberResponse> getMembers(Long groupId) {
        validateMember(groupId);

        List<GroupMember> members = groupMemberRepository.findByExpenseGroupId(groupId);

        return members.stream()
                .map(member -> MemberResponse.builder()
                        .id(member.getUser().getId())
                        .name(member.getUser().getName())
                        .email(member.getUser().getEmail())
                        .build())
                .toList();
    }

    @Override
    @Transactional
    public void addMember(Long groupId, String email) {
        validateMember(groupId);

        ExpenseGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));

        if (groupMemberRepository.existsByExpenseGroupIdAndUserId(groupId, user.getId())) {
            throw new InvalidExpenseException("User is already a member of this group");
        }

        GroupMember newMember = GroupMember.builder()
                .expenseGroup(group)
                .user(user)
                .build();

        groupMemberRepository.save(newMember);
        log.info("Added user email '{}' (ID: {}) to group ID: {}", email, user.getId(), groupId);
    }

    @Override
    @Transactional
    public void removeMember(Long groupId, Long memberUserId) {
        validateMember(groupId);

        GroupMember targetMember = groupMemberRepository.findByExpenseGroupIdAndUserId(groupId, memberUserId)
                .orElseThrow(() -> new UserNotFoundException("Member not found in this group"));

        groupMemberRepository.delete(targetMember);
        log.info("Removed member user ID: {} from group ID: {}", memberUserId, groupId);
    }
}