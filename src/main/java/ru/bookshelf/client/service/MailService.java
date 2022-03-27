package ru.bookshelf.client.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring5.SpringTemplateEngine;
import ru.bookshelf.client.config.AppConfiguration;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Slf4j
@Getter
@Service
@RequiredArgsConstructor
public class MailService {
    private final JavaMailSender javaMailSender;
    private final SpringTemplateEngine templateEngine;

    @Async
    public void sendEmail(AppConfiguration.EmailConfig email, Throwable exception) throws MessagingException {
        log.debug("Send email to '{}' from '{}' with subject '{}'", email.getTo(), email.getFrom(), email.getSubject());
        Context context = new Context();
        context.setVariable("error", exception.getMessage());

        String process = templateEngine.process(email.getTemplate(), context);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setTo(email.getTo());
        helper.setFrom(email.getFrom());
        helper.setSubject(email.getSubject());
        helper.setText(process, true);
        javaMailSender.send(mimeMessage);
        log.info("Email was send successfully");
    }
}
