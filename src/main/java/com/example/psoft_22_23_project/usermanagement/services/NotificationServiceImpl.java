package com.example.psoft_22_23_project.usermanagement.services;

import com.example.psoft_22_23_project.usermanagement.model.User;
import com.example.psoft_22_23_project.usermanagement.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{
    private final UserRepository userRepository;
    @Autowired
    private  JavaMailSender mailSender;

    @Value("${mail.entity}")
    private String mailEntity;
    @Override
    public void sendNotification(User user) {
        userRepository.findByUsername(user.getUsername())
                .ifPresent(users -> sendEmail(user.getEmail(), "Notification", "Your account passwword has been changed successfully!"));

    }

    public void sendEmail(String to, String subject, String body) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailEntity);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }

}
