package com.mathan.expensesplitter.dto.expense;

import java.math.BigDecimal;

public class GroupBalanceResponse {

    private Long userId;
    private String userName;
    private BigDecimal totalPaid;
    private BigDecimal totalOwed;
    private BigDecimal netBalance;

    public GroupBalanceResponse() {
    }

    public GroupBalanceResponse(Long userId, String userName, BigDecimal totalPaid, BigDecimal totalOwed, BigDecimal netBalance) {
        this.userId = userId;
        this.userName = userName;
        this.totalPaid = totalPaid;
        this.totalOwed = totalOwed;
        this.netBalance = netBalance;
    }

    public Long getUserId() { return userId; }
    public void setUserId(Long userId) { this.userId = userId; }
    public String getUserName() { return userName; }
    public void setUserName(String userName) { this.userName = userName; }
    public BigDecimal getTotalPaid() { return totalPaid; }
    public void setTotalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; }
    public BigDecimal getTotalOwed() { return totalOwed; }
    public void setTotalOwed(BigDecimal totalOwed) { this.totalOwed = totalOwed; }
    public BigDecimal getNetBalance() { return netBalance; }
    public void setNetBalance(BigDecimal netBalance) { this.netBalance = netBalance; }

    public static GroupBalanceResponseBuilder builder() {
        return new GroupBalanceResponseBuilder();
    }

    public static class GroupBalanceResponseBuilder {
        private Long userId;
        private String userName;
        private BigDecimal totalPaid;
        private BigDecimal totalOwed;
        private BigDecimal netBalance;

        public GroupBalanceResponseBuilder userId(Long userId) { this.userId = userId; return this; }
        public GroupBalanceResponseBuilder userName(String userName) { this.userName = userName; return this; }
        public GroupBalanceResponseBuilder totalPaid(BigDecimal totalPaid) { this.totalPaid = totalPaid; return this; }
        public GroupBalanceResponseBuilder totalOwed(BigDecimal totalOwed) { this.totalOwed = totalOwed; return this; }
        public GroupBalanceResponseBuilder netBalance(BigDecimal netBalance) { this.netBalance = netBalance; return this; }

        public GroupBalanceResponse build() {
            return new GroupBalanceResponse(userId, userName, totalPaid, totalOwed, netBalance);
        }
    }
}
