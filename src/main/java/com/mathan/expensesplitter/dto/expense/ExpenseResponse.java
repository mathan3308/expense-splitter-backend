package com.mathan.expensesplitter.dto.expense;

import com.mathan.expensesplitter.enums.SplitType;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseResponse {

    private Long expenseId;
    private String description;
    private BigDecimal totalAmount;
    private SplitType splitType;
    private Long paidByUserId;
    private String paidByName;
    private LocalDateTime expenseDate;
    private Long groupId;
    private List<ExpenseSplitResponse> splits;
}
