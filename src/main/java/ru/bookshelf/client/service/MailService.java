package ru.bookshelf.client.service;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
        log.debug("Sending email to '{}' from '{}' with subject '{}'", email.getTo(), email.getFrom(), email.getSubjectServerUnavailable());
        Context context = new Context();
        context.setVariable("error", exception.getMessage());

        String process = templateEngine.process(email.getTemplateServerUnavailable(), context);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setTo(email.getTo());
        helper.setFrom(email.getFrom());
        helper.setSubject(email.getSubjectServerUnavailable());
        helper.setText(process, true);
        javaMailSender.send(mimeMessage);
        log.info("Email was sent successfully");
    }
    @Async
    public void sendEmail(AppConfiguration.EmailConfig email, String errors, Long id) throws MessagingException {
        log.debug("Sending email to '{}' from '{}' with subject '{}'", email.getTo(), email.getFrom(), email.getSubjectCannotDeleteBook());
        Context context = new Context();
        context.setVariable("error", errors);
        context.setVariable("id", id.toString());

        String process = templateEngine.process(email.getTemplateCannotDeleteBook(), context);
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage);
        helper.setTo(email.getTo());
        helper.setFrom(email.getFrom());
        helper.setSubject(email.getSubjectCannotDeleteBook());
        helper.setText(process, true);
        javaMailSender.send(mimeMessage);
        log.info("Email was sent successfully");
    }
}
