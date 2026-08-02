package com.mathan.expensesplitter.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "expense_groups")   // <-- changed
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;
}