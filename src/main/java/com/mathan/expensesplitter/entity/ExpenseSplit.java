package com.mathan.expensesplitter.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;

@Entity
@Table(name = "expense_splits")
public class ExpenseSplit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal amount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_id", nullable = false)
    private Expense expense;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public ExpenseSplit() {
    }

    public ExpenseSplit(Long id, BigDecimal amount, Expense expense, User user) {
        this.id = id;
        this.amount = amount;
        this.expense = expense;
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public Expense getExpense() { return expense; }
    public void setExpense(Expense expense) { this.expense = expense; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public static ExpenseSplitBuilder builder() {
        return new ExpenseSplitBuilder();
    }

    public static class ExpenseSplitBuilder {
        private Long id;
        private BigDecimal amount;
        private Expense expense;
        private User user;

        public ExpenseSplitBuilder id(Long id) { this.id = id; return this; }
        public ExpenseSplitBuilder amount(BigDecimal amount) { this.amount = amount; return this; }
        public ExpenseSplitBuilder expense(Expense expense) { this.expense = expense; return this; }
        public ExpenseSplitBuilder user(User user) { this.user = user; return this; }

        public ExpenseSplit build() {
            return new ExpenseSplit(id, amount, expense, user);
        }
    }
}