package com.mathan.expensesplitter.dto.expense;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSplitResponse {

    private Long userId;
    private String userName;
    private BigDecimal amount;
}
