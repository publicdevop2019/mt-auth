package com.mt.access.messenger.application.email_delivery;

import com.mt.common.domain.CommonDomainRegistry;
import com.mt.messenger.domain.model.email_delivery.BizTypeEnum;
import com.mt.messenger.domain.model.email_delivery.CoolDownException;
import com.mt.messenger.domain.model.email_delivery.EmailDelivery;
import com.mt.messenger.domain.model.email_delivery.MessageRepository;
import com.mt.messenger.domain.model.email_delivery.event.PendingUserActivationCodeUpdated;
import com.mt.messenger.domain.model.email_delivery.event.UserPwdResetCodeUpdated;
import com.mt.messenger.port.adapter.email.GmailDeliveryException;
import com.mt.messenger.port.adapter.http.OAuthService;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import javax.persistence.EntityManager;
import java.io.IOException;
import java.util.*;

@Service
@Slf4j
@Transactional(propagation = Propagation.NOT_SUPPORTED)
public class EmailDeliveryApplicationService {
    private static final Set<String> EVENTS = new HashSet<>();

    static {
        EVENTS.add(PendingUserActivationCodeUpdated.class.getName());
        EVENTS.add(UserPwdResetCodeUpdated.class.getName());
    }

    @Autowired
    private MessageRepository messageRepository;
    @Autowired
    private JavaMailSender sender;
    @Autowired
    private Configuration freemarkerConfig;
    @Autowired
    private OAuthService oAuthService;
    @Autowired
    private PlatformTransactionManager transactionManager;
    @Autowired
    private EntityManager entityManager;

    public void sendPwdResetEmail(Map<String, String> map) {
        log.info("start of send email for pwd reset");
        Map<String, Object> model = new HashMap<>();
        model.put("token", map.get("token"));
        sendEmail(map.get("email"), "PasswordResetTemplate.ftl", "Your password reset token", model, BizTypeEnum.PWD_RESET);
    }

    public void sendActivationCodeEmail(Map<String, String> map) {
        log.info("start of send email for activation code");
        Map<String, Object> model = new HashMap<>();
        model.put("activationCode", map.get("activationCode"));
        sendEmail(map.get("email"), "ActivationCodeTemplate.ftl", "Your activation code", model, BizTypeEnum.NEW_USER_CODE);
    }

    public void sendNewOrderEmail() {
        log.info("start of send email for new order");
        String adminEmail = oAuthService.getAdminList();
        sendEmail(adminEmail, "NewOrderEmailTemplate.ftl", "New Order(s) Has Been Placed", new HashMap<>(), BizTypeEnum.NEW_ORDER);
    }

    private void sendEmail(String email, String templateUrl, String subject, Map<String, Object> model, BizTypeEnum bizType) {
        if (email == null)
            throw new IllegalArgumentException("email should not be empty");
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        Optional<EmailDelivery> message = transactionTemplate.execute(status -> messageRepository.findByDeliverToAndBizType(email, bizType));
        if (message != null && message.isPresent()) {
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    Optional<EmailDelivery> byDeliverToAndBizType = messageRepository.findByDeliverToAndBizType(email, bizType);
                    continueDeliverShared(email, byDeliverToAndBizType.get(), templateUrl, subject, model);
                    entityManager.persist(message.get());
                    entityManager.flush();
                }
            });
        } else {
            log.info("new message for {}", email);
            // below run in a separate transaction
            transactionTemplate.execute(new TransactionCallbackWithoutResult() {
                @Override
                protected void doInTransactionWithoutResult(TransactionStatus status) {
                    EmailDelivery message = EmailDelivery.create(CommonDomainRegistry.getUniqueIdGeneratorService().id(), email, bizType);
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
     * manually create new transaction as this is call internally
     *
     * @param email
     */
    private void continueDeliver(String email, String templateUrl, String subject, Map<String, Object> model, BizTypeEnum bizTypeEnum) {
        TransactionTemplate transactionTemplate = new TransactionTemplate(transactionManager);
        int isolationLevel = transactionTemplate.getIsolationLevel();
        log.info("isolation level " + isolationLevel);
        transactionTemplate.execute(new TransactionCallbackWithoutResult() {
            @Override
            protected void doInTransactionWithoutResult(TransactionStatus status) {
                log.info("after db save, read from db again");
                Optional<EmailDelivery> byDeliverTo = messageRepository.findByDeliverToAndBizType(email, bizTypeEnum);
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

    private void deliverEmail(String to, String templateUrl, String subject, Map<String, Object> model) throws GmailDeliveryException {
        log.info("deliver email");
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        Template t;
        try {
            t = freemarkerConfig.getTemplate(templateUrl);
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setText(text, true); // set to html
            mimeMessageHelper.setSubject(subject);
            sender.send(mimeMessage);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new GmailDeliveryException(e);
        }
    }

    private void continueDeliverShared(String email, EmailDelivery message, String templateUrl, String subject, Map model) {
        log.info("message was sent for {} before", email);
        Boolean aBoolean = message.hasCoolDown();
        if (!aBoolean)
            throw new CoolDownException();
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

}
