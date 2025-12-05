package com.mt.access.port.adapter.sms;

import com.aliyun.dysmsapi20170525.models.SendSmsResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mt.access.domain.DomainRegistry;
import com.mt.access.domain.model.i18n.SupportedLocale;
import com.mt.access.domain.model.notification.SmsNotificationService;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sns.SnsClient;
import software.amazon.awssdk.services.sns.model.PublishRequest;
import software.amazon.awssdk.services.sns.model.PublishResponse;

@Slf4j
@Service
@ConditionalOnProperty(
    value = "mt.sms.type",
    havingValue = "mixed"
)
public class MixedSmsNotificationService implements SmsNotificationService {

    public static final String SMS_CANADA_BODY_KEY = "sms_login_verification_msg";
    @Value("${mt.sms.aliyun.key-id}")
    private String aliyunKeyId;
    @Value("${mt.sms.aliyun.key-secret}")
    private String aliyunKeySecret;
    @Value("${mt.sms.aliyun.url}")
    private String aliyunUrl;
    @Value("${mt.sms.aliyun.sign-name}")
    private String aliyunSignName;
    @Value("${mt.sms.aliyun.template-code-zhHans}")
    private String aliyunTemplateCodeZhHans;
    @Value("${mt.sms.aliyun.template-code-enUs}")
    private String aliyunTemplateCodeEnUs;

    @Value("${mt.sms.aws.key-id}")
    private String awsKeyId;
    @Value("${mt.sms.aws.key-secret}")
    private String awsKeySecret;

    private com.aliyun.dysmsapi20170525.Client chinaSmsClient;
    private final ObjectMapper om = new ObjectMapper();

    @PostConstruct
    public void initClient() {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
            .setAccessKeyId(aliyunKeyId)
            .setAccessKeySecret(aliyunKeySecret);
        config.endpoint = aliyunUrl;
        try {
            chinaSmsClient = new com.aliyun.dysmsapi20170525.Client(config);
        } catch (Exception ex) {
            log.error("error during sms client init", ex);
            throw new DefinedRuntimeException("unable to initialize sms client", "1096",
                HttpResponseCode.NOT_HTTP);
        }
    }

    @Override
    public void notify(String countryCode, String mobileNumber, String code,
                       SupportedLocale locale) {
        log.debug("start of sending sms");
        if ("86".equalsIgnoreCase(countryCode)) {
            sendSmsMainlandChina(mobileNumber, code, locale);
        } else if ("1".equalsIgnoreCase(countryCode)) {
            sendSmsCanada(countryCode, mobileNumber, code, locale);
        } else {
            log.debug("skip none supported country code");
        }
        log.debug("end of sending sms");
    }

    private void sendSmsMainlandChina(String mobileNumber, String code, SupportedLocale locale) {
        String aliyunTemplateCode;
        if (locale.equals(SupportedLocale.enUs)) {
            aliyunTemplateCode = aliyunTemplateCodeEnUs;
        } else if (locale.equals(SupportedLocale.zhHans)) {
            aliyunTemplateCode = aliyunTemplateCodeZhHans;
        } else {
            aliyunTemplateCode = aliyunTemplateCodeEnUs;
        }
        com.aliyun.dysmsapi20170525.models.SendSmsRequest request =
            new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setSignName(aliyunSignName)
                .setTemplateCode(aliyunTemplateCode)
                .setPhoneNumbers(mobileNumber)
                .setTemplateParam("{\"code\":\"" + code + "\"}");
        com.aliyun.teautil.models.RuntimeOptions runtime =
            new com.aliyun.teautil.models.RuntimeOptions();
        SendSmsResponse sendSmsResponse;
        try {
            sendSmsResponse = chinaSmsClient.sendSmsWithOptions(request, runtime);
            log.info("sms response {}", om.writeValueAsString(sendSmsResponse));
        } catch (Exception ex) {
            log.error("unable to send sms", ex);
            throw new DefinedRuntimeException("unable to send sms", "1097",
                HttpResponseCode.NOT_HTTP);
        }
        if (!"OK".equalsIgnoreCase(sendSmsResponse.getBody().getMessage())) {
            log.error("sms return none success code");
            throw new DefinedRuntimeException("unable to send sms", "1097",
                HttpResponseCode.NOT_HTTP);
        }
    }

    public void sendSmsCanada(String countryCode, String mobileNumber, String code,
                              SupportedLocale locale) {
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(awsKeyId, awsKeySecret);
        String messageBody =
            DomainRegistry.getI18nService().getI18nValue(SMS_CANADA_BODY_KEY, locale);
        PublishRequest request = PublishRequest.builder()
            .message(messageBody + code)
            .phoneNumber("+" + countryCode + mobileNumber)
            .build();
        try (
            SnsClient snsClient = SnsClient.builder().region(Region.US_EAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        ) {
            PublishResponse result = snsClient.publish(request);
            log.info("sms response message id {}", result.messageId());
        } catch (Exception ex) {
            log.error("unable to send sms", ex);
            throw new DefinedRuntimeException("unable to send sms", "1097",
                HttpResponseCode.NOT_HTTP);
        }
    }
}
