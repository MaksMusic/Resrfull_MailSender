package org.example.mailsender.service;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Data

public class MailService {
    private final JavaMailSender mailSender;

    public void sendEmail(String to, String from, String title, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setFrom(from);
        message.setSubject(title);
        message.setText(body);
        mailSender.send(message);
    }
}