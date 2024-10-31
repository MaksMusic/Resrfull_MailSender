package org.example.mailsender.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.example.mailsender.mapper.UserMapper;
import org.example.mailsender.model.dto.AwaitingUser;
import org.example.mailsender.model.entity.User;
import org.example.mailsender.model.dto.UserRegDto;
import org.example.mailsender.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
@Data
public class RegService {

    private final UserRepository userRepository;
    private final Map<String, AwaitingUser> awaitingUsers = new HashMap<>();
    private final MailService mailService;
    private final UserMapper userMapper;

    public boolean isNotActive(UserRegDto userRegDto) {
        Optional<User> byEmail = userRepository.findByEmail(userRegDto.getEmail());
        Optional<User> byLogin = userRepository.findByUsername(userRegDto.getUsername());
        if (byEmail.isPresent() && byLogin.isPresent()) {
            return false;
        }
        return true;
    }

    public void sendCodeToEmail(UserRegDto userRegDto) {
        AwaitingUser awaitingUser = new AwaitingUser();
        awaitingUser.setUserRegDto(userRegDto);
        awaitingUser.setCode(generateCode());
        awaitingUser.setCreatedAt(LocalDateTime.now());

        awaitingUsers.put(userRegDto.getEmail(), awaitingUser);

        mailService.sendEmail(userRegDto.getEmail(), "MaksMusic05@yandex.ru"
                ,"Confirmation",awaitingUser.getCode());

    }


    public User confirmEmail(String code, String mail){
        AwaitingUser awaitingUser = awaitingUsers.get(mail);

        if (awaitingUser == null) {
            throw new IllegalArgumentException("Code expired");
        }

        if (!awaitingUser.getCode().equals(code)) {
            throw new IllegalArgumentException("Code expired(код не совпадает)");
        }

        if (awaitingUser.isExpiredCode(7)) {
            awaitingUsers.remove(mail);
            throw new IllegalArgumentException("Time expired");
        }

        User user = userMapper.toUser(awaitingUser.getUserRegDto());
        user.setActive(true);
        return userRepository.save(user);
    }


    private String generateCode() {
        return String.valueOf(new Random().nextInt(1_000_000));
    }

}
