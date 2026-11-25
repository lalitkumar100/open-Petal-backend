package com.crimsonlogic.open_petal_backend.query.controller;

import com.crimsonlogic.open_petal_backend.common.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.common.message.constants.MessageCodes;
import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import com.crimsonlogic.open_petal_backend.query.dto.UserQueryRequestDto;
import com.crimsonlogic.open_petal_backend.query.dto.UserQueryResponseDto;
import com.crimsonlogic.open_petal_backend.query.entity.UserQuery;
import com.crimsonlogic.open_petal_backend.query.service.UserQueryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/queries")
public class UserQueryController {

    private final MessageService messageService;

    private final UserQueryService userQueryService;

    @Autowired
    public UserQueryController(UserQueryService userQueryService,
                           MessageService messageService) {
        this.messageService = messageService;
        this.userQueryService = userQueryService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserQueryResponseDto>> submitQuery(
            @Valid @RequestBody UserQueryRequestDto requestDto, Long userId) {
        UserQueryResponseDto createdQuery = UserQueryResponseDto.from(userQueryService.submitQuery(userId, requestDto));
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.QUERY_SUBMITTED, messageService.getMessage(MessageCodes.QUERY_SUBMITTED), createdQuery));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserQueryResponseDto>> getQueryById(@PathVariable Long id) {
        UserQueryResponseDto query = UserQueryResponseDto.from(userQueryService.getQueryById(id));
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.QUERY_RETRIEVED, messageService.getMessage(MessageCodes.QUERY_RETRIEVED), query));
    }
}
