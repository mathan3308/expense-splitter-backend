package com.mathan.expensesplitter.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @OneToMany(mappedBy = "user")
    private List<GroupMember> groupMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "paidBy")
    private List<Expense> paidExpenses = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<ExpenseSplit> expenseSplits = new ArrayList<>();

    public User() {
    }

    public User(Long id, String name, String email, String password, List<GroupMember> groupMemberships, List<Expense> paidExpenses, List<ExpenseSplit> expenseSplits) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.groupMemberships = groupMemberships != null ? groupMemberships : new ArrayList<>();
        this.paidExpenses = paidExpenses != null ? paidExpenses : new ArrayList<>();
        this.expenseSplits = expenseSplits != null ? expenseSplits : new ArrayList<>();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
    public List<GroupMember> getGroupMemberships() { return groupMemberships; }
    public void setGroupMemberships(List<GroupMember> groupMemberships) { this.groupMemberships = groupMemberships; }
    public List<Expense> getPaidExpenses() { return paidExpenses; }
    public void setPaidExpenses(List<Expense> paidExpenses) { this.paidExpenses = paidExpenses; }
    public List<ExpenseSplit> getExpenseSplits() { return expenseSplits; }
    public void setExpenseSplits(List<ExpenseSplit> expenseSplits) { this.expenseSplits = expenseSplits; }

    public static UserBuilder builder() {
        return new UserBuilder();
    }

    public static class UserBuilder {
        private Long id;
        private String name;
        private String email;
        private String password;
        private List<GroupMember> groupMemberships = new ArrayList<>();
        private List<Expense> paidExpenses = new ArrayList<>();
        private List<ExpenseSplit> expenseSplits = new ArrayList<>();

        public UserBuilder id(Long id) { this.id = id; return this; }
        public UserBuilder name(String name) { this.name = name; return this; }
        public UserBuilder email(String email) { this.email = email; return this; }
        public UserBuilder password(String password) { this.password = password; return this; }
        public UserBuilder groupMemberships(List<GroupMember> groupMemberships) { this.groupMemberships = groupMemberships; return this; }
        public UserBuilder paidExpenses(List<Expense> paidExpenses) { this.paidExpenses = paidExpenses; return this; }
        public UserBuilder expenseSplits(List<ExpenseSplit> expenseSplits) { this.expenseSplits = expenseSplits; return this; }

        public User build() {
            return new User(id, name, email, password, groupMemberships, paidExpenses, expenseSplits);
        }
    }
}