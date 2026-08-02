package com.mathan.expensesplitter.dto.expense;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExpenseSplitRequest {

    @NotNull(message = "User ID is required")
    private Long userId;

    private BigDecimal amount;
}
