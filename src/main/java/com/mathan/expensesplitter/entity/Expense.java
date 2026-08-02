package com.mathan.expensesplitter.entity;

import com.mathan.expensesplitter.enums.SplitType;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "expenses")
public class Expense {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SplitType splitType;

    @Column(nullable = false)
    private LocalDateTime expenseDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id", nullable = false)
    private ExpenseGroup expenseGroup;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paid_by", nullable = false)
    private User paidBy;

    @OneToMany(
            mappedBy = "expense",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ExpenseSplit> splits = new ArrayList<>();

    public Expense() {
    }

    public Expense(Long id, String description, BigDecimal totalAmount, SplitType splitType, LocalDateTime expenseDate, ExpenseGroup expenseGroup, User paidBy, List<ExpenseSplit> splits) {
        this.id = id;
        this.description = description;
        this.totalAmount = totalAmount;
        this.splitType = splitType;
        this.expenseDate = expenseDate;
        this.expenseGroup = expenseGroup;
        this.paidBy = paidBy;
        this.splits = splits != null ? splits : new ArrayList<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public void setTotalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; }
    public SplitType getSplitType() { return splitType; }
    public void setSplitType(SplitType splitType) { this.splitType = splitType; }
    public LocalDateTime getExpenseDate() { return expenseDate; }
    public void setExpenseDate(LocalDateTime expenseDate) { this.expenseDate = expenseDate; }
    public ExpenseGroup getExpenseGroup() { return expenseGroup; }
    public void setExpenseGroup(ExpenseGroup expenseGroup) { this.expenseGroup = expenseGroup; }
    public User getPaidBy() { return paidBy; }
    public void setPaidBy(User paidBy) { this.paidBy = paidBy; }
    public List<ExpenseSplit> getSplits() { return splits; }
    public void setSplits(List<ExpenseSplit> splits) { this.splits = splits; }

    public static ExpenseBuilder builder() {
        return new ExpenseBuilder();
    }

    public static class ExpenseBuilder {
        private Long id;
        private String description;
        private BigDecimal totalAmount;
        private SplitType splitType;
        private LocalDateTime expenseDate;
        private ExpenseGroup expenseGroup;
        private User paidBy;
        private List<ExpenseSplit> splits = new ArrayList<>();

        public ExpenseBuilder id(Long id) { this.id = id; return this; }
        public ExpenseBuilder description(String description) { this.description = description; return this; }
        public ExpenseBuilder totalAmount(BigDecimal totalAmount) { this.totalAmount = totalAmount; return this; }
        public ExpenseBuilder splitType(SplitType splitType) { this.splitType = splitType; return this; }
        public ExpenseBuilder expenseDate(LocalDateTime expenseDate) { this.expenseDate = expenseDate; return this; }
        public ExpenseBuilder expenseGroup(ExpenseGroup expenseGroup) { this.expenseGroup = expenseGroup; return this; }
        public ExpenseBuilder paidBy(User paidBy) { this.paidBy = paidBy; return this; }
        public ExpenseBuilder splits(List<ExpenseSplit> splits) { this.splits = splits; return this; }

        public Expense build() {
            return new Expense(id, description, totalAmount, splitType, expenseDate, expenseGroup, paidBy, splits);
        }
    }
}