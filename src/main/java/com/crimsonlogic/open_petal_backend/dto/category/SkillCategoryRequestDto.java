package com.crimsonlogic.open_petal_backend.dto.category;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SkillCategoryRequestDto {

    // 1. Rejects null, "", and "   " (whitespace-only strings)
    @NotBlank(message = "Category name cannot be empty or blank")
    @Size(min = 2, max = 60, message = "Category name must be between 2 and 60 characters")
    @Pattern(regexp = ".*\\S.*", message = "Category name must contain at least one non-whitespace character")
    private String name;

    // Optional field: Trims spaces; if empty spaces are sent, handles within max limit
    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    // --- Automatic Whitespace Trimming on Deserialization ---

    public void setName(String name) {
        this.name = name != null ? name.trim() : null;
    }

    public void setDescription(String description) {
        if (description != null && description.trim().isEmpty()) {
            this.description = null; // Convert whitespace-only descriptions to clean null
        } else {
            this.description = description != null ? description.trim() : null;
        }
    }
}