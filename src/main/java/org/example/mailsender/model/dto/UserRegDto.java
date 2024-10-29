package org.example.mailsender.model.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor

public class UserRegDto {
    @Size(min = 6, max = 32,message = "длина логина должна быть от 6 до 32 символов")
    @NotBlank

    private String username;
    @Size(min = 6, max = 32,message = "длина пароля должна быть от 6 до 32 символов")
    @NotBlank
    @Pattern(regexp = ".*[a-zA-Zа-яА-Я].*",message = "в пароле должна быть хотя бы 1 буква")
    private String password;

    @NotBlank(message = "повторите пароль")
    private String passwordRepeat;

    @Email(message = "некоректный формат почты")
    @NotBlank
    private String email;
}
