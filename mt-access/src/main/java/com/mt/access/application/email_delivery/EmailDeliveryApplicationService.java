package com.mt.access.application.email_delivery;

import com.mt.access.domain.model.cross_domain_validation.event.CrossDomainValidationFailureCheck;
import com.mt.access.domain.model.email_delivery.BizType;
import com.mt.access.domain.model.email_delivery.CoolDownException;
import com.mt.access.domain.model.email_delivery.EmailDelivery;
import com.mt.access.domain.model.email_delivery.EmailDeliveryRepository;
import com.mt.access.domain.model.email_delivery.GmailDeliveryException;
import com.mt.access.domain.model.pending_user.event.PendingUserActivationCodeUpdated;
import com.mt.access.domain.model.user.event.UserPwdResetCodeUpdated;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.Resource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionCallbackWithoutResult;
import org.springframework.transaction.support.TransactionTemplate;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

@Service
@Slf4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class EmailDeliveryApplicationService {
    @Autowired
    private EmailDeliveryRepository emailDeliveryRepository;
    @Autowired
    private JavaMailSender sender;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private EntityManager entityManager;
    @Value("${mt.email.admin}")
    private String adminEmail;

    public void sendPwdResetEmail(Map<String, String> map) {
        log.info("start of send email for pwd reset");
        Map<String, Object> model = new HashMap<>();
        model.put("token", map.get("token"));
        sendEmail(map.get("email"), "PasswordResetTemplate.ftl", "Your password reset token", model,
            BizType.PWD_RESET);
    }

    public void sendAdminNotificationEmail() {
        log.info("start of send email for admin notification");
        Map<String, Object> model = new HashMap<>();
        sendEmail(adminEmail, "AdminNotification.ftl", "Application validation failed", model,
            BizType.ADMIN_NOTIFICATION);
    }

    public void sendActivationCodeEmail(Map<String, String> map) {
        log.info("start of send email for activation code");
        Map<String, Object> model = new HashMap<>();
        model.put("activationCode", map.get("activationCode"));
        sendEmail(map.get("email"), "ActivationCodeTemplate.ftl", "Your activation code", model,
            BizType.NEW_USER_CODE);
    }

    private void sendEmail(String email, String templateUrl, String subject,
                           Map<String, Object> model, BizType bizType) {
        if (email == null) {
            throw new IllegalArgumentException("email should not be empty");
        }
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        Optional<EmailDelivery> message = transactionTemplate
            .execute(status -> emailDeliveryRepository.getEmailDelivery(email, bizType));
        if (message != null && message.isPresent()) {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    Optional<EmailDelivery> byDeliverToAndBizType =
                        emailDeliveryRepository.getEmailDelivery(email, bizType);
                    continueDeliverShared(email, byDeliverToAndBizType.get(), templateUrl, subject,
                        model);
                }
            });
        } else {
            log.info("new message for {}", email);
            // below run in a separate transaction
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    EmailDelivery message = EmailDelivery.create(email, bizType);
                    log.info("save to db first for concurrency scenario");
                    entityManager.persist(message);
                    entityManager.flush();
                }
            });
            // below run in a new transaction
            continueDeliver(email, templateUrl, subject, model, bizType);
        }
    }

    /**
     * manually create new transaction as this is call internally.
     *
     * @param email user email
     */
    private void continueDeliver(String email, String templateUrl, String subject,
                                 Map<String, Object> model, BizType bizType) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        int isolationLevel = transactionTemplate.getIsolationLevel();
        log.info("isolation level " + isolationLevel);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                log.info("after db save, read from db again");
                Optional<EmailDelivery> byDeliverTo =
                    emailDeliveryRepository.getEmailDelivery(email, bizType);
                if (byDeliverTo.isPresent()) {
                    log.info("found previously saved entity");
                    continueDeliverShared(email, byDeliverTo.get(), templateUrl, subject, model);
                    entityManager.persist(byDeliverTo.get());
                    entityManager.flush();
                } else {
                    log.error("read nothing from db");
                }
            }
        });
    }

    private void deliverEmail(String to, String templateUrl, String subject,
                              Map<String, Object> model) throws GmailDeliveryException {
        log.info("deliver email");
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        Template t;
        Configuration freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_28);
        Resource path = new DefaultResourceLoader().getResource("email/templates");
        try {
            freemarkerConfiguration.setDirectoryForTemplateLoading(path.getFile());
            t = freemarkerConfiguration.getTemplate(templateUrl);
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setText(text, true); // set to html
            mimeMessageHelper.setSubject(subject);
            sender.send(mimeMessage);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new GmailDeliveryException(e);
        }
    }

    private void continueDeliverShared(String email, EmailDelivery message, String templateUrl,
                                       String subject, Map model) {
        log.info("message was sent for {} before", email);
        Boolean cool = message.hasCoolDown();
        if (!cool) {
            throw new CoolDownException();
        }
        log.info("message has cool down");
        deliverEmail(email, templateUrl, subject, model);
        log.info("updating message status after email deliver");
        message.onMsgSendSuccess();
    }

    public void handle(UserPwdResetCodeUpdated event) {
        log.info("handling UserPwdResetCodeUpdated");
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("email", event.getEmail());
        stringStringHashMap.put("token", event.getCode());
        sendPwdResetEmail(stringStringHashMap);
        log.debug("deliver password reset code email successfully");
    }

    public void handle(PendingUserActivationCodeUpdated event) {
        log.info("handling PendingUserActivationCodeUpdated");
        HashMap<String, String> stringStringHashMap = new HashMap<>();
        stringStringHashMap.put("email", event.getEmail());
        stringStringHashMap.put("activationCode", event.getCode());
        sendActivationCodeEmail(stringStringHashMap);
        log.debug("deliver activation code email successfully");
    }

    public void handle(CrossDomainValidationFailureCheck deserialize) {
        log.info("handling CrossDomainValidationFailureCheck");
        sendAdminNotificationEmail();
        log.debug("notify admin successfully");
    }
}
