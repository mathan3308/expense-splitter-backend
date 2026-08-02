package com.mathan.expensesplitter.dto.expense;

import com.mathan.expensesplitter.enums.SplitType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateExpenseRequest {

    @NotNull(message = "Group ID is required")
    private Long groupId;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Total amount is required")
    @DecimalMin(value = "0.01", message = "Total amount must be greater than zero")
    private BigDecimal totalAmount;

    @NotNull(message = "Split type is required")
    private SplitType splitType;

    @NotNull(message = "Paid by user ID is required")
    private Long paidByUserId;

    @NotEmpty(message = "At least one participant is required")
    private List<Long> participantIds;

    @Valid
    private List<ExpenseSplitRequest> splits;
}
