package com.mathan.expensesplitter.repository;

import com.mathan.expensesplitter.entity.ExpenseGroup;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ExpenseGroupRepository
        extends JpaRepository<ExpenseGroup, Long> {

}