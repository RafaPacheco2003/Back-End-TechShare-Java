package com.techmate.techmate.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @org.springframework.scheduling.annotation.Async("eventExecutor")
    public void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
        } catch (Exception e) {
            // Log and swallow exceptions to avoid breaking caller flow.
            // AuthService will treat email delivery as best-effort.
            org.slf4j.LoggerFactory.getLogger(EmailService.class).error("Error sending plain email to {}", to, e);
        }
    }

    @org.springframework.scheduling.annotation.Async("eventExecutor")
    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true = HTML
            
            mailSender.send(message);
        } catch (MessagingException me) {
            org.slf4j.LoggerFactory.getLogger(EmailService.class).error("MessagingException sending HTML email to {}", to, me);
            // rethrow wrapped in RuntimeException so callers can optionally fallback, but since this runs async
            // we swallow here and rely on sendEmail fallback if needed synchronously.
        } catch (Exception e) {
            org.slf4j.LoggerFactory.getLogger(EmailService.class).error("Error sending HTML email to {}", to, e);
        }
    }
}


