package com.mathan.expensesplitter.service.impl;

import com.mathan.expensesplitter.dto.expense.CreateExpenseRequest;
import com.mathan.expensesplitter.dto.expense.ExpenseResponse;
import com.mathan.expensesplitter.dto.expense.ExpenseSplitRequest;
import com.mathan.expensesplitter.dto.expense.ExpenseSplitResponse;
import com.mathan.expensesplitter.dto.expense.ExpenseSummaryResponse;
import com.mathan.expensesplitter.dto.expense.GroupBalanceResponse;
import com.mathan.expensesplitter.dto.expense.SettlementResponse;
import com.mathan.expensesplitter.entity.Expense;
import com.mathan.expensesplitter.entity.ExpenseGroup;
import com.mathan.expensesplitter.entity.ExpenseSplit;
import com.mathan.expensesplitter.entity.User;
import com.mathan.expensesplitter.enums.SplitType;
import com.mathan.expensesplitter.exception.AccessDeniedException;
import com.mathan.expensesplitter.exception.ExpenseNotFoundException;
import com.mathan.expensesplitter.exception.GroupNotFoundException;
import com.mathan.expensesplitter.exception.InvalidExpenseException;
import com.mathan.expensesplitter.repository.ExpenseGroupRepository;
import com.mathan.expensesplitter.repository.ExpenseRepository;
import com.mathan.expensesplitter.repository.ExpenseSplitRepository;
import com.mathan.expensesplitter.repository.GroupMemberRepository;
import com.mathan.expensesplitter.repository.UserRepository;
import com.mathan.expensesplitter.security.SecurityUtils;
import com.mathan.expensesplitter.service.ExpenseService;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ExpenseServiceImpl implements ExpenseService {

    private static final Logger log = LoggerFactory.getLogger(ExpenseServiceImpl.class);

    private final ExpenseRepository expenseRepository;
    private final ExpenseGroupRepository groupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    private final ExpenseSplitRepository expenseSplitRepository;

    public ExpenseServiceImpl(ExpenseRepository expenseRepository,
                              ExpenseGroupRepository groupRepository,
                              GroupMemberRepository groupMemberRepository,
                              UserRepository userRepository,
                              ExpenseSplitRepository expenseSplitRepository) {
        this.expenseRepository = expenseRepository;
        this.groupRepository = groupRepository;
        this.groupMemberRepository = groupMemberRepository;
        this.userRepository = userRepository;
        this.expenseSplitRepository = expenseSplitRepository;
    }

    @Override
    @Transactional
    public ExpenseResponse createExpense(CreateExpenseRequest request) {
        if (request.getTotalAmount() == null || request.getTotalAmount().compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidExpenseException("Total amount must be greater than zero");
        }

        ExpenseGroup group = groupRepository.findById(request.getGroupId())
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));

        Long currentUserId = SecurityUtils.getCurrentUser().getId();
        if (!groupMemberRepository.existsByExpenseGroupIdAndUserId(request.getGroupId(), currentUserId)) {
            throw new AccessDeniedException("User is not a member of this group");
        }

        User paidByUser = userRepository.findById(request.getPaidByUserId())
                .orElseThrow(() -> new InvalidExpenseException("PaidBy user not found"));

        if (!groupMemberRepository.existsByExpenseGroupIdAndUserId(request.getGroupId(), request.getPaidByUserId())) {
            throw new InvalidExpenseException("PaidBy user is not a member of the group");
        }

        Expense expense = Expense.builder()
                .expenseGroup(group)
                .description(request.getDescription())
                .totalAmount(request.getTotalAmount())
                .splitType(request.getSplitType())
                .paidBy(paidByUser)
                .expenseDate(LocalDateTime.now())
                .build();

        Expense savedExpense = expenseRepository.save(expense);
        List<ExpenseSplit> splits = new ArrayList<>();

        if (request.getSplitType() == SplitType.EQUAL) {
            splits = processEqualSplits(savedExpense, request);
        } else if (request.getSplitType() == SplitType.CUSTOM) {
            splits = processCustomSplits(savedExpense, request);
        } else {
            throw new InvalidExpenseException("Unsupported split type");
        }

        expenseSplitRepository.saveAll(splits);
        log.info("Created expense ID: {} ('{}') of amount {} with splitType {} in group ID: {}",
                savedExpense.getId(), savedExpense.getDescription(), savedExpense.getTotalAmount(), savedExpense.getSplitType(), group.getId());

        List<ExpenseSplitResponse> splitResponses = splits.stream()
                .map(s -> ExpenseSplitResponse.builder()
                        .userId(s.getUser().getId())
                        .userName(s.getUser().getName())
                        .amount(s.getAmount())
                        .build())
                .toList();

        return ExpenseResponse.builder()
                .expenseId(savedExpense.getId())
                .description(savedExpense.getDescription())
                .totalAmount(savedExpense.getTotalAmount())
                .splitType(savedExpense.getSplitType())
                .paidByUserId(paidByUser.getId())
                .paidByName(paidByUser.getName())
                .expenseDate(savedExpense.getExpenseDate())
                .groupId(group.getId())
                .splits(splitResponses)
                .build();
    }

    private List<ExpenseSplit> processEqualSplits(Expense savedExpense, CreateExpenseRequest request) {
        List<Long> participantIds = request.getParticipantIds();
        if (participantIds == null || participantIds.isEmpty()) {
            throw new InvalidExpenseException("At least one participant is required");
        }

        for (Long participantId : participantIds) {
            if (!groupMemberRepository.existsByExpenseGroupIdAndUserId(request.getGroupId(), participantId)) {
                throw new InvalidExpenseException("Participant user " + participantId + " is not a member of the group");
            }
        }

        int count = participantIds.size();
        BigDecimal countBD = BigDecimal.valueOf(count);
        BigDecimal perPersonShare = request.getTotalAmount().divide(countBD, 2, RoundingMode.DOWN);
        BigDecimal totalAllocated = perPersonShare.multiply(countBD);
        BigDecimal remainder = request.getTotalAmount().subtract(totalAllocated);

        List<ExpenseSplit> splits = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Long pId = participantIds.get(i);
            User pUser = userRepository.findById(pId)
                    .orElseThrow(() -> new InvalidExpenseException("Participant user not found"));

            BigDecimal splitAmount = perPersonShare;
            if (i == 0 && remainder.compareTo(BigDecimal.ZERO) > 0) {
                splitAmount = splitAmount.add(remainder);
            }

            ExpenseSplit split = ExpenseSplit.builder()
                    .expense(savedExpense)
                    .user(pUser)
                    .amount(splitAmount)
                    .build();

            splits.add(split);
        }

        return splits;
    }

    private List<ExpenseSplit> processCustomSplits(Expense savedExpense, CreateExpenseRequest request) {
        List<ExpenseSplitRequest> customSplits = request.getSplits();
        if (customSplits == null || customSplits.isEmpty()) {
            throw new InvalidExpenseException("At least one split is required");
        }

        Set<Long> seenUserIds = new HashSet<>();
        BigDecimal sum = BigDecimal.ZERO;
        List<ExpenseSplit> splits = new ArrayList<>();

        for (ExpenseSplitRequest splitReq : customSplits) {
            if (splitReq.getUserId() == null) {
                throw new InvalidExpenseException("Participant user ID is required");
            }

            if (!seenUserIds.add(splitReq.getUserId())) {
                throw new InvalidExpenseException("Duplicate participant found.");
            }

            if (!groupMemberRepository.existsByExpenseGroupIdAndUserId(request.getGroupId(), splitReq.getUserId())) {
                throw new InvalidExpenseException("Participant is not a member of the group.");
            }

            if (splitReq.getAmount() == null || splitReq.getAmount().compareTo(BigDecimal.ZERO) <= 0) {
                throw new InvalidExpenseException("Split amount must be greater than zero.");
            }

            sum = sum.add(splitReq.getAmount());

            User user = userRepository.findById(splitReq.getUserId())
                    .orElseThrow(() -> new InvalidExpenseException("Participant user not found"));

            ExpenseSplit split = ExpenseSplit.builder()
                    .expense(savedExpense)
                    .user(user)
                    .amount(splitReq.getAmount())
                    .build();

            splits.add(split);
        }

        if (sum.compareTo(request.getTotalAmount()) != 0) {
            throw new InvalidExpenseException("Split total must equal expense total.");
        }

        return splits;
    }

    @Override
    public List<ExpenseSummaryResponse> getExpensesByGroup(Long groupId) {
        ExpenseGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));

        Long currentUserId = SecurityUtils.getCurrentUser().getId();
        if (!groupMemberRepository.existsByExpenseGroupIdAndUserId(groupId, currentUserId)) {
            throw new AccessDeniedException("User is not a member of this group");
        }

        List<Expense> expenses = expenseRepository.findByExpenseGroupIdOrderByExpenseDateDesc(groupId);

        return expenses.stream()
                .map(expense -> ExpenseSummaryResponse.builder()
                        .expenseId(expense.getId())
                        .description(expense.getDescription())
                        .totalAmount(expense.getTotalAmount())
                        .splitType(expense.getSplitType())
                        .paidByUserId(expense.getPaidBy().getId())
                        .paidByName(expense.getPaidBy().getName())
                        .expenseDate(expense.getExpenseDate())
                        .build())
                .toList();
    }

    @Override
    public ExpenseResponse getExpenseById(Long expenseId) {
        Expense expense = expenseRepository.findById(expenseId)
                .orElseThrow(() -> new ExpenseNotFoundException("Expense not found"));

        Long currentUserId = SecurityUtils.getCurrentUser().getId();
        Long groupId = expense.getExpenseGroup().getId();
        if (!groupMemberRepository.existsByExpenseGroupIdAndUserId(groupId, currentUserId)) {
            throw new AccessDeniedException("User is not a member of this group");
        }

        List<ExpenseSplitResponse> splitResponses = expense.getSplits().stream()
                .map(split -> ExpenseSplitResponse.builder()
                        .userId(split.getUser().getId())
                        .userName(split.getUser().getName())
                        .amount(split.getAmount())
                        .build())
                .toList();

        return ExpenseResponse.builder()
                .expenseId(expense.getId())
                .description(expense.getDescription())
                .totalAmount(expense.getTotalAmount())
                .splitType(expense.getSplitType())
                .paidByUserId(expense.getPaidBy().getId())
                .paidByName(expense.getPaidBy().getName())
                .expenseDate(expense.getExpenseDate())
                .groupId(groupId)
                .splits(splitResponses)
                .build();
    }

    @Override
    public List<GroupBalanceResponse> getGroupBalances(Long groupId) {
        ExpenseGroup group = groupRepository.findById(groupId)
                .orElseThrow(() -> new GroupNotFoundException("Group not found"));

        Long currentUserId = SecurityUtils.getCurrentUser().getId();
        if (!groupMemberRepository.existsByExpenseGroupIdAndUserId(groupId, currentUserId)) {
            throw new AccessDeniedException("User is not a member of this group");
        }

        List<com.mathan.expensesplitter.entity.GroupMember> groupMembers = groupMemberRepository.findByExpenseGroupId(groupId);

        java.util.Map<Long, MemberBalanceAccumulator> balanceMap = new java.util.LinkedHashMap<>();
        for (com.mathan.expensesplitter.entity.GroupMember member : groupMembers) {
            User user = member.getUser();
            balanceMap.put(user.getId(), new MemberBalanceAccumulator(user.getId(), user.getName()));
        }

        List<Expense> expenses = expenseRepository.findByExpenseGroupId(groupId);

        for (Expense expense : expenses) {
            Long paidByUserId = expense.getPaidBy().getId();
            MemberBalanceAccumulator payerAccumulator = balanceMap.get(paidByUserId);
            if (payerAccumulator != null) {
                payerAccumulator.totalPaid = payerAccumulator.totalPaid.add(expense.getTotalAmount());
            }

            for (ExpenseSplit split : expense.getSplits()) {
                Long participantUserId = split.getUser().getId();
                MemberBalanceAccumulator participantAccumulator = balanceMap.get(participantUserId);
                if (participantAccumulator != null) {
                    participantAccumulator.totalOwed = participantAccumulator.totalOwed.add(split.getAmount());
                }
            }
        }

        return balanceMap.values().stream()
                .map(acc -> GroupBalanceResponse.builder()
                        .userId(acc.userId)
                        .userName(acc.userName)
                        .totalPaid(acc.totalPaid)
                        .totalOwed(acc.totalOwed)
                        .netBalance(acc.totalPaid.subtract(acc.totalOwed))
                        .build())
                .toList();
    }

    private static class MemberBalanceAccumulator {
        final Long userId;
        final String userName;
        BigDecimal totalPaid = BigDecimal.ZERO;
        BigDecimal totalOwed = BigDecimal.ZERO;

        MemberBalanceAccumulator(Long userId, String userName) {
            this.userId = userId;
            this.userName = userName;
        }
    }

    /**
     * Calculates the minimum number of debt settlement transactions using a Greedy Two-Pointer algorithm.
     *
     * Time Complexity: O(N) where N is the number of group members.
     *   - Partitioning members into creditors and debtors takes O(N).
     *   - The two-pointer while loop terminates in at most 2 * N steps because each iteration advances
     *     at least one pointer (creditorIndex or debtorIndex).
     *   - Total Time Complexity: O(N).
     *
     * Space Complexity: O(N) for storing the creditor and debtor lists and resulting settlement list.
     */
    @Override
    public List<SettlementResponse> calculateSettlements(Long groupId) {
        List<GroupBalanceResponse> balances = getGroupBalances(groupId);

        List<PartyBalance> creditors = new ArrayList<>();
        List<PartyBalance> debtors = new ArrayList<>();

        for (GroupBalanceResponse b : balances) {
            BigDecimal net = b.getNetBalance();
            if (net != null) {
                if (net.compareTo(BigDecimal.ZERO) > 0) {
                    creditors.add(new PartyBalance(b.getUserId(), b.getUserName(), net));
                } else if (net.compareTo(BigDecimal.ZERO) < 0) {
                    debtors.add(new PartyBalance(b.getUserId(), b.getUserName(), net.abs()));
                }
            }
        }

        List<SettlementResponse> settlements = new ArrayList<>();

        int creditorIndex = 0;
        int debtorIndex = 0;

        while (creditorIndex < creditors.size() && debtorIndex < debtors.size()) {
            PartyBalance creditor = creditors.get(creditorIndex);
            PartyBalance debtor = debtors.get(debtorIndex);

            BigDecimal payment = creditor.amount.min(debtor.amount);

            if (payment.compareTo(BigDecimal.ZERO) > 0) {
                settlements.add(SettlementResponse.builder()
                        .payerId(debtor.userId)
                        .payerName(debtor.userName)
                        .receiverId(creditor.userId)
                        .receiverName(creditor.userName)
                        .amount(payment)
                        .build());

                creditor.amount = creditor.amount.subtract(payment);
                debtor.amount = debtor.amount.subtract(payment);
            }

            if (creditor.amount.compareTo(BigDecimal.ZERO) == 0) {
                creditorIndex++;
            }

            if (debtor.amount.compareTo(BigDecimal.ZERO) == 0) {
                debtorIndex++;
            }
        }

        return settlements;
    }

    private static class PartyBalance {
        final Long userId;
        final String userName;
        BigDecimal amount;

        PartyBalance(Long userId, String userName, BigDecimal amount) {
            this.userId = userId;
            this.userName = userName;
            this.amount = amount;
        }
    }
}
