package com.mathan.expensesplitter.controller;

import com.mathan.expensesplitter.dto.auth.group.CreateGroupRequest;
import com.mathan.expensesplitter.dto.auth.group.GroupResponse;
import com.mathan.expensesplitter.dto.auth.group.MemberResponse;
import com.mathan.expensesplitter.exception.InvalidExpenseException;
import com.mathan.expensesplitter.service.GroupService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
@Tag(name = "Expense Groups", description = "Group creation, listing, and member management endpoints")
@SecurityRequirement(name = "bearerAuth")
public class GroupController {

    private final GroupService groupService;

    @Operation(summary = "Create an expense group", description = "Creates a new expense group and automatically adds the creator as a member.")
    @ApiResponse(responseCode = "201", description = "Group created successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @PostMapping
    public ResponseEntity<GroupResponse> createGroup(
            @Valid @RequestBody CreateGroupRequest request) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(groupService.createGroup(request));
    }

    @Operation(summary = "Get my groups", description = "Retrieves all expense groups the authenticated user belongs to.")
    @ApiResponse(responseCode = "200", description = "Groups retrieved successfully")
    @ApiResponse(responseCode = "401", description = "Unauthorized")
    @GetMapping
    public ResponseEntity<List<GroupResponse>> getMyGroups() {

        return ResponseEntity.ok(
                groupService.getMyGroups());
    }

    @Operation(summary = "Get group by ID", description = "Retrieves group details by ID if the user is a group member.")
    @ApiResponse(responseCode = "200", description = "Group retrieved successfully")
    @ApiResponse(responseCode = "403", description = "User is not a member of this group")
    @ApiResponse(responseCode = "404", description = "Group not found")
    @GetMapping("/{id}")
    public ResponseEntity<GroupResponse> getGroup(
            @PathVariable Long id) {

        return ResponseEntity.ok(
                groupService.getGroup(id));
    }

    @Operation(summary = "Get group members", description = "Retrieves all members of an expense group if the user is a group member.")
    @ApiResponse(responseCode = "200", description = "Members retrieved successfully")
    @ApiResponse(responseCode = "403", description = "User is not a member of this group")
    @ApiResponse(responseCode = "404", description = "Group not found")
    @GetMapping("/{groupId}/members")
    public ResponseEntity<List<MemberResponse>> getMembers(
            @PathVariable Long groupId) {

        return ResponseEntity.ok(
                groupService.getMembers(groupId));
    }

    @Operation(summary = "Add member to group", description = "Adds a user to the group by email (accepts email via query parameter or JSON body).")
    @ApiResponse(responseCode = "200", description = "Member added successfully")
    @ApiResponse(responseCode = "400", description = "User already a member or email missing")
    @ApiResponse(responseCode = "403", description = "User is not a member of this group")
    @ApiResponse(responseCode = "404", description = "User or group not found")
    @PostMapping("/{groupId}/members")
    public ResponseEntity<Void> addMember(
            @PathVariable Long groupId,
            @RequestParam(required = false) String email,
            @RequestBody(required = false) Map<String, String> body) {

        String targetEmail = (email != null && !email.isBlank()) ? email : (body != null ? body.get("email") : null);
        if (targetEmail == null || targetEmail.isBlank()) {
            throw new InvalidExpenseException("Email is required");
        }

        groupService.addMember(groupId, targetEmail.trim());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "Remove member from group", description = "Removes a target user from the group.")
    @ApiResponse(responseCode = "200", description = "Member removed successfully")
    @ApiResponse(responseCode = "403", description = "User is not a member of this group")
    @ApiResponse(responseCode = "404", description = "Group or member not found")
    @DeleteMapping("/{groupId}/members/{userId}")
    public ResponseEntity<Void> removeMember(
            @PathVariable Long groupId,
            @PathVariable Long userId) {

        groupService.removeMember(groupId, userId);
        return ResponseEntity.ok().build();
    }
}