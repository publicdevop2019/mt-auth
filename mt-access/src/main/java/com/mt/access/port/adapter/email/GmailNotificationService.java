package com.mt.access.port.adapter.email;

import com.mt.access.domain.model.notification.EmailNotificationService;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.ExceptionCatalog;
import com.mt.common.domain.model.exception.HttpResponseCode;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import java.io.IOException;
import java.util.Map;
import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

@Service
@Slf4j
public class GmailNotificationService implements EmailNotificationService {
    @Autowired
    private JavaMailSender sender;

    @Override
    public void notify(String deliverTo, String templateUrl, String subject,
                       Map<String, String> model) {
        log.info("start of deliver email");
        MimeMessage mimeMessage = sender.createMimeMessage();
        MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage);
        Template t;
        Configuration freemarkerConfiguration = new Configuration(Configuration.VERSION_2_3_28);
        try {
            freemarkerConfiguration.setClassForTemplateLoading(this.getClass(), "/email/templates");
            t = freemarkerConfiguration.getTemplate(templateUrl);
            String text = FreeMarkerTemplateUtils.processTemplateIntoString(t, model);
            mimeMessageHelper.setTo(deliverTo);
            mimeMessageHelper.setText(text, true); // set to html
            mimeMessageHelper.setSubject(subject);
            sender.send(mimeMessage);
        } catch (IOException | TemplateException | MessagingException e) {
            throw new DefinedRuntimeException("unable to send email", "0071",
                HttpResponseCode.INTERNAL_SERVER_ERROR,
                ExceptionCatalog.OPERATION_ERROR, e);
        }
        log.info("end of deliver email");
    }
}
