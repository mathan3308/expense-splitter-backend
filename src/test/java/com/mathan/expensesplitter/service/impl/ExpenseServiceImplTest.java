package com.mathan.expensesplitter.service.impl;

import com.mathan.expensesplitter.dto.expense.*;
import com.mathan.expensesplitter.entity.Expense;
import com.mathan.expensesplitter.entity.ExpenseGroup;
import com.mathan.expensesplitter.entity.ExpenseSplit;
import com.mathan.expensesplitter.entity.GroupMember;
import com.mathan.expensesplitter.entity.User;
import com.mathan.expensesplitter.enums.SplitType;
import com.mathan.expensesplitter.exception.AccessDeniedException;
import com.mathan.expensesplitter.exception.GroupNotFoundException;
import com.mathan.expensesplitter.exception.InvalidExpenseException;
import com.mathan.expensesplitter.repository.*;
import com.mathan.expensesplitter.security.UserPrincipal;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.quality.Strictness;
import org.mockito.junit.jupiter.MockitoSettings;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class ExpenseServiceImplTest {

    @Mock
    private ExpenseRepository expenseRepository;

    @Mock
    private ExpenseSplitRepository expenseSplitRepository;

    @Mock
    private ExpenseGroupRepository groupRepository;

    @Mock
    private GroupMemberRepository groupMemberRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ExpenseServiceImpl expenseService;

    private User user1;
    private User user2;
    private User user3;
    private ExpenseGroup group1;

    @BeforeEach
    void setUp() {
        user1 = User.builder().id(1L).name("Alice").email("alice@example.com").password("pass").build();
        user2 = User.builder().id(2L).name("Bob").email("bob@example.com").password("pass").build();
        user3 = User.builder().id(3L).name("Charlie").email("charlie@example.com").password("pass").build();

        group1 = ExpenseGroup.builder().id(1L).name("Trip Group").description("Vacation").build();

        mockSecurityUser(1L, "alice@example.com");
    }

    private void mockSecurityUser(Long userId, String email) {
        User user = User.builder().id(userId).name("User " + userId).email(email).password("pass").build();
        UserPrincipal principal = new UserPrincipal(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities());
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    // ==========================================
    // 1. EQUAL SPLIT TESTS
    // ==========================================

    @Test
    @DisplayName("createExpense - Equal Split - Success")
    void createExpense_shouldCreateEqualSplitSuccessfully() {
        // Arrange
        CreateExpenseRequest request = CreateExpenseRequest.builder()
                .groupId(1L)
                .description("Dinner")
                .totalAmount(new BigDecimal("1200.00"))
                .splitType(SplitType.EQUAL)
                .paidByUserId(1L)
                .participantIds(List.of(1L, 2L, 3L))
                .build();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group1));
        when(groupMemberRepository.existsByExpenseGroupIdAndUserId(1L, 1L)).thenReturn(true);
        when(groupMemberRepository.existsByExpenseGroupIdAndUserId(1L, 2L)).thenReturn(true);
        when(groupMemberRepository.existsByExpenseGroupIdAndUserId(1L, 3L)).thenReturn(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user3));

        Expense savedExpense = Expense.builder()
                .id(100L)
                .expenseGroup(group1)
                .description("Dinner")
                .totalAmount(new BigDecimal("1200.00"))
                .splitType(SplitType.EQUAL)
                .paidBy(user1)
                .expenseDate(LocalDateTime.now())
                .build();

        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        // Act
        ExpenseResponse response = expenseService.createExpense(request);

        // Assert
        assertNotNull(response);
        assertEquals(100L, response.getExpenseId());
        assertEquals("Dinner", response.getDescription());
        assertEquals(new BigDecimal("1200.00"), response.getTotalAmount());
        assertEquals(SplitType.EQUAL, response.getSplitType());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ExpenseSplit>> splitsCaptor = ArgumentCaptor.forClass(List.class);
        verify(expenseSplitRepository).saveAll(splitsCaptor.capture());

        List<ExpenseSplit> savedSplits = splitsCaptor.getValue();
        assertEquals(3, savedSplits.size());
        assertEquals(new BigDecimal("400.00"), savedSplits.get(0).getAmount());
        assertEquals(new BigDecimal("400.00"), savedSplits.get(1).getAmount());
        assertEquals(new BigDecimal("400.00"), savedSplits.get(2).getAmount());
    }

    @Test
    @DisplayName("createExpense - Equal Split - Throws when no participants")
    void createExpense_shouldThrowException_whenEqualSplitHasNoParticipants() {
        CreateExpenseRequest request = CreateExpenseRequest.builder()
                .groupId(1L)
                .description("Coffee")
                .totalAmount(new BigDecimal("150.00"))
                .splitType(SplitType.EQUAL)
                .paidByUserId(1L)
                .participantIds(List.of())
                .build();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group1));
        when(groupMemberRepository.existsByExpenseGroupIdAndUserId(1L, 1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        assertThrows(InvalidExpenseException.class, () -> expenseService.createExpense(request));
    }

    // ==========================================
    // 2. CUSTOM SPLIT TESTS
    // ==========================================

    @Test
    @DisplayName("createExpense - Custom Split - Success")
    void createExpense_shouldCreateCustomSplitSuccessfully() {
        CreateExpenseRequest request = CreateExpenseRequest.builder()
                .groupId(1L)
                .description("Hotel")
                .totalAmount(new BigDecimal("1000.00"))
                .splitType(SplitType.CUSTOM)
                .paidByUserId(1L)
                .splits(List.of(
                        ExpenseSplitRequest.builder().userId(1L).amount(new BigDecimal("500.00")).build(),
                        ExpenseSplitRequest.builder().userId(2L).amount(new BigDecimal("300.00")).build(),
                        ExpenseSplitRequest.builder().userId(3L).amount(new BigDecimal("200.00")).build()
                ))
                .build();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group1));
        when(groupMemberRepository.existsByExpenseGroupIdAndUserId(1L, 1L)).thenReturn(true);
        when(groupMemberRepository.existsByExpenseGroupIdAndUserId(1L, 2L)).thenReturn(true);
        when(groupMemberRepository.existsByExpenseGroupIdAndUserId(1L, 3L)).thenReturn(true);

        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));
        when(userRepository.findById(3L)).thenReturn(Optional.of(user3));

        Expense savedExpense = Expense.builder()
                .id(101L)
                .expenseGroup(group1)
                .description("Hotel")
                .totalAmount(new BigDecimal("1000.00"))
                .splitType(SplitType.CUSTOM)
                .paidBy(user1)
                .expenseDate(LocalDateTime.now())
                .build();

        when(expenseRepository.save(any(Expense.class))).thenReturn(savedExpense);

        ExpenseResponse response = expenseService.createExpense(request);

        assertNotNull(response);
        assertEquals(101L, response.getExpenseId());
        assertEquals(SplitType.CUSTOM, response.getSplitType());

        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<ExpenseSplit>> splitsCaptor = ArgumentCaptor.forClass(List.class);
        verify(expenseSplitRepository).saveAll(splitsCaptor.capture());

        List<ExpenseSplit> savedSplits = splitsCaptor.getValue();
        assertEquals(3, savedSplits.size());
        assertEquals(new BigDecimal("500.00"), savedSplits.get(0).getAmount());
        assertEquals(new BigDecimal("300.00"), savedSplits.get(1).getAmount());
        assertEquals(new BigDecimal("200.00"), savedSplits.get(2).getAmount());
    }

    @Test
    @DisplayName("createExpense - Custom Split - Mismatched total throws exception")
    void createExpense_shouldThrowException_whenCustomSplitTotalMismatch() {
        CreateExpenseRequest request = CreateExpenseRequest.builder()
                .groupId(1L)
                .description("Hotel")
                .totalAmount(new BigDecimal("1000.00"))
                .splitType(SplitType.CUSTOM)
                .paidByUserId(1L)
                .splits(List.of(
                        ExpenseSplitRequest.builder().userId(1L).amount(new BigDecimal("500.00")).build(),
                        ExpenseSplitRequest.builder().userId(2L).amount(new BigDecimal("300.00")).build()
                ))
                .build();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group1));
        when(groupMemberRepository.existsByExpenseGroupIdAndUserId(1L, 1L)).thenReturn(true);
        when(groupMemberRepository.existsByExpenseGroupIdAndUserId(1L, 2L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));
        when(userRepository.findById(2L)).thenReturn(Optional.of(user2));

        InvalidExpenseException ex = assertThrows(InvalidExpenseException.class, () -> expenseService.createExpense(request));
        assertTrue(ex.getMessage().contains("Split total must equal expense total"));
    }

    @Test
    @DisplayName("createExpense - Custom Split - Duplicate participants throw exception")
    void createExpense_shouldThrowException_whenDuplicateParticipantsInCustomSplit() {
        CreateExpenseRequest request = CreateExpenseRequest.builder()
                .groupId(1L)
                .description("Hotel")
                .totalAmount(new BigDecimal("1000.00"))
                .splitType(SplitType.CUSTOM)
                .paidByUserId(1L)
                .splits(List.of(
                        ExpenseSplitRequest.builder().userId(1L).amount(new BigDecimal("500.00")).build(),
                        ExpenseSplitRequest.builder().userId(1L).amount(new BigDecimal("500.00")).build()
                ))
                .build();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group1));
        when(groupMemberRepository.existsByExpenseGroupIdAndUserId(1L, 1L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        InvalidExpenseException ex = assertThrows(InvalidExpenseException.class, () -> expenseService.createExpense(request));
        assertTrue(ex.getMessage().contains("Duplicate participant found"));
    }

    @Test
    @DisplayName("createExpense - Custom Split - Zero or negative amount throws exception")
    void createExpense_shouldThrowException_whenCustomSplitAmountIsZeroOrNegative() {
        CreateExpenseRequest request = CreateExpenseRequest.builder()
                .groupId(1L)
                .description("Hotel")
                .totalAmount(new BigDecimal("1000.00"))
                .splitType(SplitType.CUSTOM)
                .paidByUserId(1L)
                .splits(List.of(
                        ExpenseSplitRequest.builder().userId(1L).amount(new BigDecimal("0.00")).build(),
                        ExpenseSplitRequest.builder().userId(2L).amount(new BigDecimal("1000.00")).build()
                ))
                .build();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group1));
        when(groupMemberRepository.existsByExpenseGroupIdAndUserId(1L, 1L)).thenReturn(true);
        when(groupMemberRepository.existsByExpenseGroupIdAndUserId(1L, 2L)).thenReturn(true);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user1));

        InvalidExpenseException ex = assertThrows(InvalidExpenseException.class, () -> expenseService.createExpense(request));
        assertTrue(ex.getMessage().contains("Split amount must be greater than zero"));
    }

    // ==========================================
    // 3. GROUP BALANCES TESTS
    // ==========================================

    @Test
    @DisplayName("getGroupBalances - Success - Calculates net balances correctly")
    void getGroupBalances_shouldCalculateNetBalancesCorrectly() {
        GroupMember m1 = GroupMember.builder().id(10L).expenseGroup(group1).user(user1).build();
        GroupMember m2 = GroupMember.builder().id(11L).expenseGroup(group1).user(user2).build();
        GroupMember m3 = GroupMember.builder().id(12L).expenseGroup(group1).user(user3).build();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group1));
        when(groupMemberRepository.existsByExpenseGroupIdAndUserId(1L, 1L)).thenReturn(true);
        when(groupMemberRepository.findByExpenseGroupId(1L)).thenReturn(List.of(m1, m2, m3));

        Expense expense = Expense.builder()
                .id(1L)
                .expenseGroup(group1)
                .description("Dinner")
                .totalAmount(new BigDecimal("900.00"))
                .paidBy(user1)
                .build();

        ExpenseSplit s1 = ExpenseSplit.builder().expense(expense).user(user1).amount(new BigDecimal("300.00")).build();
        ExpenseSplit s2 = ExpenseSplit.builder().expense(expense).user(user2).amount(new BigDecimal("300.00")).build();
        ExpenseSplit s3 = ExpenseSplit.builder().expense(expense).user(user3).amount(new BigDecimal("300.00")).build();
        expense.setSplits(List.of(s1, s2, s3));

        when(expenseRepository.findByExpenseGroupId(1L)).thenReturn(List.of(expense));

        List<GroupBalanceResponse> balances = expenseService.getGroupBalances(1L);

        assertEquals(3, balances.size());

        GroupBalanceResponse b1 = balances.stream().filter(b -> b.getUserId().equals(1L)).findFirst().orElseThrow();
        assertEquals(new BigDecimal("900.00"), b1.getTotalPaid());
        assertEquals(new BigDecimal("300.00"), b1.getTotalOwed());
        assertEquals(new BigDecimal("600.00"), b1.getNetBalance());

        GroupBalanceResponse b2 = balances.stream().filter(b -> b.getUserId().equals(2L)).findFirst().orElseThrow();
        assertEquals(BigDecimal.ZERO, b2.getTotalPaid());
        assertEquals(new BigDecimal("300.00"), b2.getTotalOwed());
        assertEquals(new BigDecimal("-300.00"), b2.getNetBalance());

        GroupBalanceResponse b3 = balances.stream().filter(b -> b.getUserId().equals(3L)).findFirst().orElseThrow();
        assertEquals(BigDecimal.ZERO, b3.getTotalPaid());
        assertEquals(new BigDecimal("300.00"), b3.getTotalOwed());
        assertEquals(new BigDecimal("-300.00"), b3.getNetBalance());
    }

    @Test
    @DisplayName("getGroupBalances - Success - Group with no expenses")
    void getGroupBalances_shouldReturnZeroBalances_whenGroupHasNoExpenses() {
        GroupMember m1 = GroupMember.builder().id(10L).expenseGroup(group1).user(user1).build();
        GroupMember m2 = GroupMember.builder().id(11L).expenseGroup(group1).user(user2).build();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group1));
        when(groupMemberRepository.existsByExpenseGroupIdAndUserId(1L, 1L)).thenReturn(true);
        when(groupMemberRepository.findByExpenseGroupId(1L)).thenReturn(List.of(m1, m2));
        when(expenseRepository.findByExpenseGroupId(1L)).thenReturn(List.of());

        List<GroupBalanceResponse> balances = expenseService.getGroupBalances(1L);

        assertEquals(2, balances.size());
        assertEquals(BigDecimal.ZERO, balances.get(0).getNetBalance());
        assertEquals(BigDecimal.ZERO, balances.get(1).getNetBalance());
    }

    @Test
    @DisplayName("getGroupBalances - Throws GroupNotFoundException")
    void getGroupBalances_shouldThrowException_whenGroupNotFound() {
        when(groupRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(GroupNotFoundException.class, () -> expenseService.getGroupBalances(99L));
    }

    @Test
    @DisplayName("getGroupBalances - Throws AccessDeniedException when not member")
    void getGroupBalances_shouldThrowException_whenUserIsNotGroupMember() {
        when(groupRepository.findById(1L)).thenReturn(Optional.of(group1));
        when(groupMemberRepository.existsByExpenseGroupIdAndUserId(1L, 1L)).thenReturn(false);

        assertThrows(AccessDeniedException.class, () -> expenseService.getGroupBalances(1L));
    }

    // ==========================================
    // 4. DEBT SIMPLIFICATION / SETTLE UP TESTS
    // ==========================================

    @Test
    @DisplayName("calculateSettlements - Success - Minimum transactions generated")
    void calculateSettlements_shouldReturnMinimumTransactions() {
        GroupMember m1 = GroupMember.builder().id(10L).expenseGroup(group1).user(user1).build();
        GroupMember m2 = GroupMember.builder().id(11L).expenseGroup(group1).user(user2).build();
        GroupMember m3 = GroupMember.builder().id(12L).expenseGroup(group1).user(user3).build();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group1));
        when(groupMemberRepository.existsByExpenseGroupIdAndUserId(1L, 1L)).thenReturn(true);
        when(groupMemberRepository.findByExpenseGroupId(1L)).thenReturn(List.of(m1, m2, m3));

        Expense expense = Expense.builder()
                .id(1L)
                .expenseGroup(group1)
                .description("Dinner")
                .totalAmount(new BigDecimal("900.00"))
                .paidBy(user1)
                .build();

        ExpenseSplit s1 = ExpenseSplit.builder().expense(expense).user(user1).amount(new BigDecimal("300.00")).build();
        ExpenseSplit s2 = ExpenseSplit.builder().expense(expense).user(user2).amount(new BigDecimal("300.00")).build();
        ExpenseSplit s3 = ExpenseSplit.builder().expense(expense).user(user3).amount(new BigDecimal("300.00")).build();
        expense.setSplits(List.of(s1, s2, s3));

        when(expenseRepository.findByExpenseGroupId(1L)).thenReturn(List.of(expense));

        List<SettlementResponse> settlements = expenseService.calculateSettlements(1L);

        assertEquals(2, settlements.size());

        SettlementResponse sRec1 = settlements.get(0);
        assertEquals(2L, sRec1.getPayerId());
        assertEquals(1L, sRec1.getReceiverId());
        assertEquals(new BigDecimal("300.00"), sRec1.getAmount());

        SettlementResponse sRec2 = settlements.get(1);
        assertEquals(3L, sRec2.getPayerId());
        assertEquals(1L, sRec2.getReceiverId());
        assertEquals(new BigDecimal("300.00"), sRec2.getAmount());
    }

    @Test
    @DisplayName("calculateSettlements - Empty list when already settled")
    void calculateSettlements_shouldReturnEmptyList_whenGroupIsAlreadySettled() {
        GroupMember m1 = GroupMember.builder().id(10L).expenseGroup(group1).user(user1).build();

        when(groupRepository.findById(1L)).thenReturn(Optional.of(group1));
        when(groupMemberRepository.existsByExpenseGroupIdAndUserId(1L, 1L)).thenReturn(true);
        when(groupMemberRepository.findByExpenseGroupId(1L)).thenReturn(List.of(m1));
        when(expenseRepository.findByExpenseGroupId(1L)).thenReturn(List.of());

        List<SettlementResponse> settlements = expenseService.calculateSettlements(1L);

        assertTrue(settlements.isEmpty());
    }
}
