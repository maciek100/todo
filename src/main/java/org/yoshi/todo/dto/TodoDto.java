package org.yoshi.todo.dto;

import jakarta.validation.constraints.NotBlank;

public record TodoDto(
        Long id,
        @NotBlank(message = "Task cannot be blank")
        String task,
        boolean done
) {}

