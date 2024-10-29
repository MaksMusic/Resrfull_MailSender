package org.example.mailsender.model.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AwaitingUser {
    private UserRegDto userRegDto;
    private String code;
    private LocalDateTime createdAt;
}
