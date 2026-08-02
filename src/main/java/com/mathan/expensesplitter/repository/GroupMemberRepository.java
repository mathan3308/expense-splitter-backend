package com.mathan.expensesplitter.repository;

import com.mathan.expensesplitter.entity.GroupMember;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GroupMemberRepository extends JpaRepository<GroupMember, Long> {

    List<GroupMember> findByUserId(Long userId);

    List<GroupMember> findByExpenseGroupId(Long expenseGroupId);

    Optional<GroupMember> findByExpenseGroupIdAndUserId(Long expenseGroupId, Long userId);

    boolean existsByExpenseGroupIdAndUserId(Long expenseGroupId, Long userId);

    void deleteByExpenseGroupIdAndUserId(Long expenseGroupId, Long userId);
}