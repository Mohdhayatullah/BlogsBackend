package org.blogs.Blogs.service;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender javaMailSender;

    @Value("${spring.mail.properties.mail.smtp.from}")
    private String email;

    public void sendMail(String to, String subject, String body){
        try{
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setFrom(email);
            msg.setTo(email);
            msg.setSubject(subject);
            msg.setText(body);
            javaMailSender.send(msg);
        }catch (Exception e){
            System.out.println("exception during sending mail :"+e.getMessage());
        }
    }
}
