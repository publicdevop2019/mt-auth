package com.mt.access.port.adapter.email;

import com.mt.access.domain.model.notification.EmailNotificationService;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.Map;
import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

@Service
@Slf4j
@ConditionalOnProperty(
    value = "mt.email.type",
    havingValue = "aliyun")
public class AliyunEmailNotificationService implements EmailNotificationService {
    @Value("${mt.email.aliyun.key-id}")
    private String keyId;
    @Value("${mt.email.aliyun.key-secret}")
    private String keySecret;
    @Value("${mt.email.aliyun.url}")
    private String url;
    @Value("${mt.email.aliyun.account-name}")
    private String accountName;
    private com.aliyun.dm20151123.Client emailClient;

    @PostConstruct
    public void initClient() {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
            .setAccessKeyId(keyId)
            .setAccessKeySecret(keySecret);
        config.endpoint = url;
        try {
            emailClient = new com.aliyun.dm20151123.Client(config);
        } catch (Exception e) {
            throw new DefinedRuntimeException("unable to initialize email client", "1098",
                HttpResponseCode.NOT_HTTP);
        }
    }

    @Override
    public void notify(String deliverTo, String templateUrl, String subject,
                       Map<String, String> model) {
        log.debug("start of deliver email");

        Template template;
        Configuration freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_28);
        String emailBody;
        freemarkerConfiguration.setClassForTemplateLoading(this.getClass(), "/email/templates");
        try {
            template = freemarkerConfiguration.getTemplate(templateUrl);
            emailBody = FreeMarkerTemplateUtils.processTemplateIntoString(template, model);
        } catch (Exception ex) {
            log.error("unable to send email", ex);
            throw new DefinedRuntimeException("unable to send email", "1098",
                HttpResponseCode.NOT_HTTP);
        }

        com.aliyun.dm20151123.models.SingleSendMailRequest request =
            new com.aliyun.dm20151123.models.SingleSendMailRequest()
                .setAccountName(accountName)
                .setAddressType(1)
                .setReplyToAddress(false)
                .setToAddress(deliverTo)
                .setSubject(subject)
                .setHtmlBody(emailBody);
        com.aliyun.teautil.models.RuntimeOptions runtime =
            new com.aliyun.teautil.models.RuntimeOptions();
        try {
            emailClient.singleSendMailWithOptions(request, runtime);
        }  catch (Exception ex) {
            log.error("unable to send email", ex);
            throw new DefinedRuntimeException("unable to send email", "1098",
                HttpResponseCode.NOT_HTTP);
        }
        log.debug("end of deliver email");
    }

}
