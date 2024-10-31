package org.example.mailsender.service;

import org.example.mailsender.exceptions.CodeExpiredException;
import org.example.mailsender.exceptions.IllegalCodeValueException;
import org.example.mailsender.exceptions.TimeExpiredException;
import org.example.mailsender.mapper.UserMapper;
import org.example.mailsender.model.dto.AwaitingUser;
import org.example.mailsender.model.dto.UserRegDto;
import org.example.mailsender.model.entity.User;
import org.example.mailsender.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;

import java.time.LocalDateTime;
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
    @Mock
    UserMapper userMapper;

    @InjectMocks
    private RegService regService;

    private UserRegDto userRegDto;
    private AwaitingUser awaitingUser;

    @BeforeEach
    void setUp() {
        openMocks(this);
        userRegDto = new UserRegDto();
        userRegDto.setUsername("testName");
        userRegDto.setEmail("test@test.com");
        userRegDto.setPassword("password");
        userRegDto.setPasswordRepeat("password");

        awaitingUser = new AwaitingUser();
        awaitingUser.setUserRegDto(userRegDto);
        awaitingUser.setCode("123456");
        awaitingUser.setCreatedAt(LocalDateTime.now());
        regService.getAwaitingUsers().put(userRegDto.getEmail(), awaitingUser);
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

        assertNotNull(awaitingUser, "Awaiting user should be created");
        assertEquals(userRegDto, awaitingUser.getUserRegDto(), "user reg dto should match");
        assertNotNull(awaitingUser.getCode(), "code should be generated");
        assertEquals(userRegDto.getEmail(), "test@test.com");

        verify(mailService, times(1)).sendEmail(eq(userRegDto.getEmail()),
                eq("MaksMusic05@yandex.ru"),
                eq("Confirmation"),
                anyString());
    }

    @Test
    void confirmEmail() {
        // Настройка
        User mockUser = new User();
        when(userMapper.toUser(any())).thenReturn(mockUser);
        when(userRepository.save(any(User.class))).thenReturn(mockUser); // Мокаем метод save

        // Выполнение
        User result = regService.confirmEmail("123456", "test@test.com");

        // Проверка
        assertNotNull(result);
        assertTrue(result.isActive());
        verify(userRepository).save(any(User.class));

        assertThrows(IllegalCodeValueException.class, () -> regService.confirmEmail("ne verniy kod", "test@test.com"));

        assertThrows(CodeExpiredException.class, () -> regService.confirmEmail("123456", "ne verni email"));

        awaitingUser.setCreatedAt(LocalDateTime.now().minusMinutes(8));
        assertThrows(TimeExpiredException.class, () -> regService.confirmEmail("123456", "test@test.com"));

        assertNull(regService.getAwaitingUsers().get("test@test.com"));
    }
}