package com.usetech.dvente.services.notifs;


import com.usetech.dvente.entities.users.User;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;


    @Value("${app.mail.from}")
    private String fromEmail;

    @Value("${app.name}")
    private String appName;

    @Value("${app.url}")
    private String appUrl;


    public EmailService(JavaMailSender mailSender, TemplateEngine templateEngine) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
    }


    public void sendWelcomeEmail(User user) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(user.getEmail());
            helper.setSubject("Welcome to " + appName + "!");

            Context context = new Context();
            context.setVariable("userName", user.getName());
            context.setVariable("userEmail", user.getEmail());
            context.setVariable("appUrl", appUrl);
            context.setVariable("appName", appName);

            String htmlContent = templateEngine.process("emails/welcome", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);

        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a verification code to the specified email address using a predefined template.
     *
     * @param email the recipient's email address
     * @param code the verification code to be sent
     * @return true if the email is successfully sent, otherwise throws a RuntimeException
     */
    public boolean sendVerificationCode(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Code de verification - " + appName + "!");
            Context context = new Context();

            context.setVariable("code", code);
            context.setVariable("appUrl", appUrl);
            context.setVariable("appName", appName);
            context.setVariable("expirationMinutes", 15);

            String htmlContent = templateEngine.process("emails/verificationCode", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Sends a reset code to the specified email address using a predefined email template.
     *
     * @param email the recipient's email address
     * @param code the reset code to be sent
     * @return true if the email is successfully sent, otherwise throws a RuntimeException
     */
    public  boolean sendResetCode(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setFrom(fromEmail);
            helper.setTo(email);
            helper.setSubject("Code de verification - " + appName + "!");

            Context context = new Context();
            context.setVariable("code", code);
            context.setVariable("appUrl", appUrl);
            context.setVariable("appName", appName);
            context.setVariable("expirationMinutes", 15);

            String htmlContent = templateEngine.process("emails/resetCode", context);
            helper.setText(htmlContent, true);

            mailSender.send(message);
            return true;

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            Context context = new Context();
            context.setVariables(variables);

            String htmlContent = templateEngine.process(templateName, context);

            helper.setFrom(fromEmail);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
    }

}
