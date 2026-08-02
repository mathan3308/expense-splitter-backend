package com.mathan.expensesplitter.service;

import com.mathan.expensesplitter.dto.expense.CreateExpenseRequest;
import com.mathan.expensesplitter.dto.expense.ExpenseResponse;
import com.mathan.expensesplitter.dto.expense.ExpenseSummaryResponse;
import com.mathan.expensesplitter.dto.expense.GroupBalanceResponse;
import com.mathan.expensesplitter.dto.expense.SettlementResponse;

import java.util.List;

public interface ExpenseService {

    ExpenseResponse createExpense(CreateExpenseRequest request);

    List<ExpenseSummaryResponse> getExpensesByGroup(Long groupId);

    ExpenseResponse getExpenseById(Long expenseId);

    List<GroupBalanceResponse> getGroupBalances(Long groupId);

    List<SettlementResponse> calculateSettlements(Long groupId);
}
