package com.crimsonlogic.open_petal_backend.skill.controller;
import com.crimsonlogic.open_petal_backend.skill.dto.SkillResponseDto;

import com.crimsonlogic.open_petal_backend.common.dto.ApiResponse;
import com.crimsonlogic.open_petal_backend.common.message.constants.MessageCodes;
import com.crimsonlogic.open_petal_backend.common.message.service.MessageService;
import com.crimsonlogic.open_petal_backend.skill.service.SkillService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/skills")
public class SkillController {

    private final MessageService messageService;

    private final SkillService skillService;

    public SkillController(SkillService skillService,
                           MessageService messageService) {
        this.messageService = messageService;
        this.skillService = skillService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<SkillResponseDto>>> getAllSkills() {
        List<SkillResponseDto> skills = skillService.getAllSkills();
        return ResponseEntity.ok(ApiResponse.success(MessageCodes.SKILLS_RETRIEVED, messageService.getMessage(MessageCodes.SKILLS_RETRIEVED), skills));
    }
}
