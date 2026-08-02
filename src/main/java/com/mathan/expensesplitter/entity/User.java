package com.mathan.expensesplitter.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;
@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Builder.Default
    private List<GroupMember> groupMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "paidBy")
    @Builder.Default
    private List<Expense> paidExpenses = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    @Builder.Default
    private List<ExpenseSplit> expenseSplits = new ArrayList<>();
}