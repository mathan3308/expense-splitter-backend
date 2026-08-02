package com.mathan.expensesplitter.dto.expense;

import com.mathan.expensesplitter.enums.SplitType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSummaryResponse {

    private Long expenseId;
    private String description;
    private BigDecimal totalAmount;
    private SplitType splitType;
    private Long paidByUserId;
    private String paidByName;
    private LocalDateTime expenseDate;
}
