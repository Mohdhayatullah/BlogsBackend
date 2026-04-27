package org.blogs.Blogs.service;


import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class EmailService {


    private final JavaMailSender mailSender;
    private final TemplateEngine templateEngine;
    //spring.mail.properties.mail.smtp.from
    @Value("${spring.mail.properties.mail.smtp.from}")
    private String fromEmail;

    public void sendHtmlEmail(String to, String subject, String body) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body, true);
            helper.setFrom(fromEmail);

            mailSender.send(message);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email");
        }
    }


    public void sendWelcomeEmail(String to, String name) {
        try {
            Context context = new Context();
            context.setVariable("name", name);

            String html = templateEngine.process("email/index", context);
            this.sendHtmlEmail(to,"Register Successfully",html);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send email");
        }
    }

    public void sendBlogCreatedEmail(String to, String name, String title, String blogUrl) {
        Context context = new Context();
        context.setVariable("name", name);
        context.setVariable("title", title);
        context.setVariable("blogUrl", blogUrl);

        String html = templateEngine.process("email/blogCreate", context);

        sendHtmlEmail(to, "Your blog is live 🚀", html);
    }
}
