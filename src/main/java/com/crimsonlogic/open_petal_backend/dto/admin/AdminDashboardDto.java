package com.crimsonlogic.open_petal_backend.dto.admin;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminDashboardDto {
    private long totalUsers;
    private long newUsers24h;
    
    private long totalSkills;
    private long totalSkillCategories;
    
    private long totalConflicts;
    
    private long totalSessions;
    private long sessions24h;
    
    private long newQueries24h;
}
