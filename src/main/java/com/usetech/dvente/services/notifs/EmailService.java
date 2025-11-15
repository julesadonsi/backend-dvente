package com.usetech.dvente.services.notifs;


import com.usetech.dvente.entities.users.Shop;
import com.usetech.dvente.entities.users.User;
import com.usetech.dvente.services.users.UserService;
import com.usetech.dvente.utils.MemoryCodeStorage;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.util.Map;


@Service
public class EmailService  extends AbstractEmailService {

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

            setDefaultFrom(helper);
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

        } catch (Exception e) {
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

            setDefaultFrom(helper);
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
     * @param code  the reset code to be sent
     */
    public void sendResetCode(String email, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            setDefaultFrom(helper);
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

        }catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void sendHtmlEmail(String to, String subject, String templateName, Map<String, Object> variables) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            setDefaultFrom(helper);

            Context context = new Context();
            context.setVariables(variables);

            String htmlContent = templateEngine.process(templateName, context);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true);

            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
    }

    @Async
    public void sendUpdateEmailVerificationCode(String email, String code) {
        try {
            String subject = "Code de verification - " + appName + "!";

            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            setDefaultFrom(helper);
            Context context = new Context();
            context.setVariable("code", code);

            String htmlContent = templateEngine.process("emails/updateEmailVerificationCode", context);
            helper.setText(htmlContent, true);
            helper.setSubject(subject);
            helper.setTo(email);
            mailSender.send(message);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Envoie un email de validation de boutique
     */
    @Async
    public void sendShopValidatedEmail(String email, String name, String shopName, String slug) {
        try {
            Map<String, Object> vars = Map.of(
                    "name", name,
                    "shopName", shopName,
                    "dashboardUrl", appUrl + "/u/dashboard",
                    "shopLink", appUrl + "/@" + slug,
                    "shopUrl", appUrl + "/@" + slug
            );

            String subject = "Boutique " + shopName.toUpperCase() + " validée!";
            MimeMessage message = mailSender.createMimeMessage();

            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            setDefaultFrom(helper);

            Context context = new Context();
            context.setVariables(vars);

            String htmlContent = templateEngine.process("emails/shopValidated", context);
            helper.setText(htmlContent, true);
            helper.setSubject(subject);
            helper.setTo(email);
            mailSender.send(message);

        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }


    public void sendEmailInfoAttemptChangeEmail(User user, String code) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            Map<String, Object> vars = Map.of(
                    "name", user.getName(),
                    "email", user.getEmail(),
                    "code", code,
                    "appName", appName
            );

            String subject = "Tentative de changement de votre e-mail";
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            setDefaultFrom(helper);

            Context context = new Context();
            context.setVariables(vars);

            String htmlContent = templateEngine.process("emails/infoAttemptChangeEmail", context);
            helper.setText(htmlContent, true);
            helper.setSubject(subject);
            helper.setTo(user.getEmail());
            mailSender.send(message);
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
