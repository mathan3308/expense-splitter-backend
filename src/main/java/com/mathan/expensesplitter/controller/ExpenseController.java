package com.mathan.expensesplitter.controller;

import com.mathan.expensesplitter.dto.expense.*;
import com.mathan.expensesplitter.service.ExpenseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Tag(name = "Expenses", description = "Expense creation (Equal & Custom), history, balance calculation, and debt simplification")
@SecurityRequirement(name = "bearerAuth")
public class ExpenseController {

    private final ExpenseService expenseService;

    @Operation(summary = "Create an expense", description = "Adds an expense with EQUAL or CUSTOM split type.")
    @ApiResponse(responseCode = "201", description = "Expense created successfully")
    @ApiResponse(responseCode = "400", description = "Invalid expense input or custom split total mismatch")
    @ApiResponse(responseCode = "403", description = "User is not a member of the group")
    @PostMapping("/api/expenses")
    public ResponseEntity<ExpenseResponse> createExpense(
            @Valid @RequestBody CreateExpenseRequest request) {
        ExpenseResponse response = expenseService.createExpense(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get group expense history", description = "Retrieves all expenses for a group ordered by newest first.")
    @ApiResponse(responseCode = "200", description = "Expenses retrieved successfully")
    @ApiResponse(responseCode = "403", description = "User is not a member of the group")
    @ApiResponse(responseCode = "404", description = "Group not found")
    @GetMapping("/api/groups/{groupId}/expenses")
    public ResponseEntity<List<ExpenseSummaryResponse>> getExpensesByGroup(
            @PathVariable Long groupId) {
        List<ExpenseSummaryResponse> responses = expenseService.getExpensesByGroup(groupId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Get expense details", description = "Retrieves detailed information and split breakdown for an expense.")
    @ApiResponse(responseCode = "200", description = "Expense details retrieved successfully")
    @ApiResponse(responseCode = "403", description = "User is not a member of the group")
    @ApiResponse(responseCode = "404", description = "Expense not found")
    @GetMapping("/api/expenses/{expenseId}")
    public ResponseEntity<ExpenseResponse> getExpenseById(
            @PathVariable Long expenseId) {
        ExpenseResponse response = expenseService.getExpenseById(expenseId);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get group net balances", description = "Calculates net balances (totalPaid, totalOwed, netBalance) for all group members.")
    @ApiResponse(responseCode = "200", description = "Balances calculated successfully")
    @ApiResponse(responseCode = "403", description = "User is not a member of the group")
    @ApiResponse(responseCode = "404", description = "Group not found")
    @GetMapping("/api/groups/{groupId}/balances")
    public ResponseEntity<List<GroupBalanceResponse>> getGroupBalances(
            @PathVariable Long groupId) {
        List<GroupBalanceResponse> responses = expenseService.getGroupBalances(groupId);
        return ResponseEntity.ok(responses);
    }

    @Operation(summary = "Calculate Settle Up settlements", description = "Computes the minimum number of debt settlement transactions needed to resolve group debts using a greedy algorithm.")
    @ApiResponse(responseCode = "200", description = "Settlements calculated successfully")
    @ApiResponse(responseCode = "403", description = "User is not a member of the group")
    @ApiResponse(responseCode = "404", description = "Group not found")
    @GetMapping("/api/groups/{groupId}/settlements")
    public ResponseEntity<List<SettlementResponse>> getSettlements(
            @PathVariable Long groupId) {
        List<SettlementResponse> responses = expenseService.calculateSettlements(groupId);
        return ResponseEntity.ok(responses);
    }
}
