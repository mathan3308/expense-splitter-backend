package com.mathan.expensesplitter.dto.auth.group;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class GroupResponse {

    private Long id;
    private String name;
    private String description;
}