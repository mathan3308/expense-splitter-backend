package com.mathan.expensesplitter.dto.expense;

import java.math.BigDecimal;

public class SettlementResponse {

    private Long payerId;
    private String payerName;
    private Long receiverId;
    private String receiverName;
    private BigDecimal amount;

    public SettlementResponse() {
    }

    public SettlementResponse(Long payerId, String payerName, Long receiverId, String receiverName, BigDecimal amount) {
        this.payerId = payerId;
        this.payerName = payerName;
        this.receiverId = receiverId;
        this.receiverName = receiverName;
        this.amount = amount;
    }

    public Long getPayerId() { return payerId; }
    public void setPayerId(Long payerId) { this.payerId = payerId; }
    public String getPayerName() { return payerName; }
    public void setPayerName(String payerName) { this.payerName = payerName; }
    public Long getReceiverId() { return receiverId; }
    public void setReceiverId(Long receiverId) { this.receiverId = receiverId; }
    public String getReceiverName() { return receiverName; }
    public void setReceiverName(String receiverName) { this.receiverName = receiverName; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public static SettlementResponseBuilder builder() {
        return new SettlementResponseBuilder();
    }

    public static class SettlementResponseBuilder {
        private Long payerId;
        private String payerName;
        private Long receiverId;
        private String receiverName;
        private BigDecimal amount;

        public SettlementResponseBuilder payerId(Long payerId) { this.payerId = payerId; return this; }
        public SettlementResponseBuilder payerName(String payerName) { this.payerName = payerName; return this; }
        public SettlementResponseBuilder receiverId(Long receiverId) { this.receiverId = receiverId; return this; }
        public SettlementResponseBuilder receiverName(String receiverName) { this.receiverName = receiverName; return this; }
        public SettlementResponseBuilder amount(BigDecimal amount) { this.amount = amount; return this; }

        public SettlementResponse build() {
            return new SettlementResponse(payerId, payerName, receiverId, receiverName, amount);
        }
    }
}
