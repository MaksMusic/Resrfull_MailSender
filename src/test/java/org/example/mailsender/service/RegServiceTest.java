package org.example.mailsender.service;

import org.example.mailsender.model.dto.AwaitingUser;
import org.example.mailsender.model.dto.UserRegDto;
import org.example.mailsender.model.entity.User;
import org.example.mailsender.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;


import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.openMocks;

class RegServiceTest {

    @Mock
    UserRepository userRepository;
    @Mock
    MailService mailService;

    @InjectMocks
    private RegService regService;

    private UserRegDto userRegDto;

    @BeforeEach
    void setUp() {
        openMocks(this);
        userRegDto = new UserRegDto();
        userRegDto.setUsername("testName");
        userRegDto.setEmail("test@test.com");
        userRegDto.setPassword("password");
        userRegDto.setPasswordRepeat("password");

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void isNotActive() {
        // Установка мока для случая, когда пользователь с указанным email и username существует
        when(userRepository.findByEmail(userRegDto.getEmail())).thenReturn(Optional.of(new User()));
        when(userRepository.findByUsername(userRegDto.getUsername())).thenReturn(Optional.of(new User()));

        // Проверка, что метод возвращает false, если оба пользователя активны
        boolean result = regService.isNotActive(userRegDto);
        assertFalse(result, "user is active");

        // Изменение мока для случая, когда email существует, а username нет
        when(userRepository.findByEmail(userRegDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(userRegDto.getUsername())).thenReturn(Optional.of(new User()));
        result = regService.isNotActive(userRegDto);
        assertTrue(result, "user is not active");

        // Изменение мока для случая, когда email нет, а username существует
        when(userRepository.findByEmail(userRegDto.getEmail())).thenReturn(Optional.of(new User()));
        when(userRepository.findByUsername(userRegDto.getUsername())).thenReturn(Optional.empty());
        result = regService.isNotActive(userRegDto);
        assertTrue(result, "user is not active");

        // Изменение мока для случая, когда оба пользователя не существуют
        when(userRepository.findByEmail(userRegDto.getEmail())).thenReturn(Optional.empty());
        when(userRepository.findByUsername(userRegDto.getUsername())).thenReturn(Optional.empty());
        result = regService.isNotActive(userRegDto);
        assertTrue(result, "user is not active");
    }

    @Test
    void sendCodeToEmail() {
        doNothing().when(mailService).sendEmail(anyString(), anyString(), anyString(), anyString());

        regService.sendCodeToEmail(userRegDto);

        AwaitingUser awaitingUser = regService.getAwaitingUsers().get(userRegDto.getEmail());

        assertNotNull(awaitingUser,"Awaiting user should be created");
        assertEquals(userRegDto,awaitingUser.getUserRegDto(),"user reg dto should match");
        assertNotNull(awaitingUser.getCode(),"code should be generated");
        assertEquals(userRegDto.getEmail(),"test@test.com");

        verify(mailService,times(1)).sendEmail(eq(userRegDto.getEmail()),
                eq("zxVendetta@yandex.ru"),
                eq("Confirmation"),
                anyString());
    }
}