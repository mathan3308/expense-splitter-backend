package com.mathan.expensesplitter.dto.expense;

import com.mathan.expensesplitter.enums.SplitType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ExpenseSummaryResponse {

    private Long expenseId;
    private String description;
    private BigDecimal totalAmount;
    private SplitType splitType;
    private Long paidByUserId;
    private String paidByName;
    private LocalDateTime expenseDate;

    public ExpenseSummaryResponse() {
    }

    public ExpenseSummaryResponse(Long expenseId, String description, BigDecimal totalAmount, SplitType splitType, Long paidByUserId, String paidByName, LocalDateTime expenseDate) {
        this.expenseId = expenseId;
        this.description = description;
        this.totalAmount = totalAmount;
        this.splitType = splitType;
        this.paidByUserId = paidByUserId;
        this.paidByName = paidByName;
        this.expenseDate = expenseDate;
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

    public static ExpenseSummaryResponseBuilder builder() {
        return new ExpenseSummaryResponseBuilder();
    }

    public static class ExpenseSummaryResponseBuilder {
        private Long expenseId;
        private String description;
        private BigDecimal totalAmount;
        private SplitType splitType;
        private Long paidByUserId;
        private String paidByName;
        private LocalDateTime expenseDate;

        public ExpenseSummaryResponseBuilder expenseId(Long expenseId) { this.expenseId = expenseId; return this; }
        public ExpenseSummaryResponseBuilder description(String description) { this.description = description; return this; }
        public ExpenseSummaryResponseBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public ExpenseSummaryResponseBuilder splitType(SplitType splitType) { this.splitType = splitType; return this; }
        public ExpenseSummaryResponseBuilder paidByUserId(Long paidByUserId) { this.paidByUserId = paidByUserId; return this; }
        public ExpenseSummaryResponseBuilder paidByName(String paidByName) { this.paidByName = paidByName; return this; }
        public ExpenseSummaryResponseBuilder expenseDate(LocalDateTime expenseDate) { this.expenseDate = expenseDate; return this; }

        public ExpenseSummaryResponse build() {
            return new ExpenseSummaryResponse(expenseId, description, totalAmount, splitType, paidByUserId, paidByName, expenseDate);
        }
    }
}
