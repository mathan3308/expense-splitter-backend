package com.mathan.expensesplitter.dto.auth.group;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateGroupRequest {

    @NotBlank(message = "Group name is required")
    private String name;

    private String description;
}