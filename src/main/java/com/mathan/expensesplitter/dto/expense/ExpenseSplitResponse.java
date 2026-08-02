package com.mathan.expensesplitter.dto.expense;

import java.math.BigDecimal;

public class ExpenseSplitResponse {

    private Long userId;
    private String userName;
    private BigDecimal amount;

    public ExpenseSplitResponse() {
    }

    public ExpenseSplitResponse(Long userId, String userName, BigDecimal amount) {
        this.userId = userId;
        this.userName = userName;
        this.amount = amount;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public static ExpenseSplitResponseBuilder builder() {
        return new ExpenseSplitResponseBuilder();
    }

    public static class ExpenseSplitResponseBuilder {
        private Long userId;
        private String userName;
        private BigDecimal amount;

        public ExpenseSplitResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public ExpenseSplitResponseBuilder userName(String userName) { this.userName = userName; return this; }
        public ExpenseSplitResponseBuilder amount(BigDecimal amount) { this.amount = amount; return this; }

        public ExpenseSplitResponse build() {
            return new ExpenseSplitResponse(userId, userName, amount);
        }
    }
}
