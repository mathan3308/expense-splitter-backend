package com.mathan.expensesplitter.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "group_members")
public class GroupMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "group_id", nullable = false)
    private ExpenseGroup expenseGroup;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public GroupMember() {
    }

    public GroupMember(Long id, ExpenseGroup expenseGroup, User user) {
        this.id = id;
        this.expenseGroup = expenseGroup;
        this.user = user;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public ExpenseGroup getExpenseGroup() { return expenseGroup; }
    public void setExpenseGroup(ExpenseGroup expenseGroup) { this.expenseGroup = expenseGroup; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }

    public static GroupMemberBuilder builder() {
        return new GroupMemberBuilder();
    }

    public static class GroupMemberBuilder {
        private Long id;
        private ExpenseGroup expenseGroup;
        private User user;

        public GroupMemberBuilder id(Long id) { this.id = id; return this; }
        public GroupMemberBuilder expenseGroup(ExpenseGroup expenseGroup) { this.expenseGroup = expenseGroup; return this; }
        public GroupMemberBuilder user(User user) { this.user = user; return this; }

        public GroupMember build() {
            return new GroupMember(id, expenseGroup, user);
        }
    }
}