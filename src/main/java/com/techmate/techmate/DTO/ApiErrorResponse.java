package com.techmate.techmate.dto;

import java.time.Instant;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiErrorResponse {
    private Instant timestamp;
    private int status;
    private String path;
    private String code; // business code opcional
    private List<String> errors;

    public static ApiErrorResponse of(int status, String path, String code, List<String> errors) {
        return ApiErrorResponse.builder()
                .timestamp(Instant.now())
                .status(status)
                .path(path)
                .code(code)
                .errors(errors)
                .build();
    }
}
