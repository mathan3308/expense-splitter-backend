package com.mathan.expensesplitter.dto.expense;

import com.mathan.expensesplitter.enums.SplitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public class ExpenseResponse {

    private Long expenseId;
    private String description;
    private BigDecimal totalAmount;
    private SplitType splitType;
    private Long paidByUserId;
    private String paidByName;
    private LocalDateTime expenseDate;
    private Long groupId;
    private List<ExpenseSplitResponse> splits;

    public ExpenseResponse() {
    }

    public ExpenseResponse(Long expenseId, String description, BigDecimal totalAmount, SplitType splitType, Long paidByUserId, String paidByName, LocalDateTime expenseDate, Long groupId, List<ExpenseSplitResponse> splits) {
        this.expenseId = expenseId;
        this.description = description;
        this.totalAmount = totalAmount;
        this.splitType = splitType;
        this.paidByUserId = paidByUserId;
        this.paidByName = paidByName;
        this.expenseDate = expenseDate;
        this.groupId = groupId;
        this.splits = splits;
    }

    public Long getExpenseId() { return expenseId; }
    public void setExpenseId(Long expenseId) { this.expenseId = expenseId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public SplitType getSplitType() { return splitType; }
    public void setSplitType(SplitType splitType) { this.splitType = splitType; }
    public Long getPaidByUserId() { return paidByUserId; }
    public void setPaidByUserId(Long paidByUserId) { this.paidByUserId = paidByUserId; }
    public String getPaidByName() { return paidByName; }
    public void setPaidByName(String paidByName) { this.paidByName = paidByName; }
    public LocalDateTime getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDateTime expenseDate) { this.expenseDate = expenseDate; }
    public Long getGroupId() { return groupId; }
    public void setGroupId(Long groupId) { this.groupId = groupId; }
    public List<ExpenseSplitResponse> getSplits() { return splits; }
    public void setSplits(List<ExpenseSplitResponse> splits) { this.splits = splits; }

    public static ExpenseResponseBuilder builder() {
        return new ExpenseResponseBuilder();
    }

    public static class ExpenseResponseBuilder {
        private Long expenseId;
        private String description;
        private BigDecimal totalAmount;
        private SplitType splitType;
        private Long paidByUserId;
        private String paidByName;
        private LocalDateTime expenseDate;
        private Long groupId;
        private List<ExpenseSplitResponse> splits;

        public ExpenseResponseBuilder expenseId(Long expenseId) { this.expenseId = expenseId; return this; }
        public ExpenseResponseBuilder description(String description) { this.description = description; return this; }
        public ExpenseResponseBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public ExpenseResponseBuilder splitType(SplitType splitType) { this.splitType = splitType; return this; }
        public ExpenseResponseBuilder paidByUserId(Long paidByUserId) { this.paidByUserId = paidByUserId; return this; }
        public ExpenseResponseBuilder paidByName(String paidByName) { this.paidByName = paidByName; return this; }
        public ExpenseResponseBuilder expenseDate(LocalDateTime expenseDate) { this.expenseDate = expenseDate; return this; }
        public ExpenseResponseBuilder groupId(Long groupId) { this.groupId = groupId; return this; }
        public ExpenseResponseBuilder splits(List<ExpenseSplitResponse> splits) { this.splits = splits; return this; }

        public ExpenseResponse build() {
            return new ExpenseResponse(expenseId, description, totalAmount, splitType, paidByUserId, paidByName, expenseDate, groupId, splits);
        }
    }
}
