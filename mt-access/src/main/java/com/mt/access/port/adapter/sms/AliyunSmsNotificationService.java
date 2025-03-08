package com.mt.access.port.adapter.sms;

import com.mt.access.domain.model.notification.SmsNotificationService;
import com.mt.common.domain.model.exception.DefinedRuntimeException;
import com.mt.common.domain.model.exception.HttpResponseCode;
import javax.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@ConditionalOnProperty(
    value = "mt.sms.type",
    havingValue = "aliyun"
)
public class AliyunSmsNotificationService implements SmsNotificationService {
    @Value("${mt.sms.aliyun.key-id}")
    private String keyId;
    @Value("${mt.sms.aliyun.key-secret}")
    private String keySecret;
    @Value("${mt.sms.aliyun.url}")
    private String url;
    @Value("${mt.sms.aliyun.sign-name}")
    private String signName;
    @Value("${mt.sms.aliyun.template-code}")
    private String templateCode;
    private com.aliyun.dysmsapi20170525.Client smsClient;

    @PostConstruct
    public void initClient() {
        com.aliyun.teaopenapi.models.Config config = new com.aliyun.teaopenapi.models.Config()
            .setAccessKeyId(keyId)
            .setAccessKeySecret(keySecret);
        config.endpoint = url;
        try {
            smsClient = new com.aliyun.dysmsapi20170525.Client(config);
        } catch (Exception ex) {
            log.error("error during sms client init", ex);
            throw new DefinedRuntimeException("unable to initialize sms client", "1096",
                HttpResponseCode.NOT_HTTP);
        }
    }

    @Override
    public void notify(String countryCode, String mobileNumber, String code) {
        log.debug("start of sending sms");
        if ("86".equalsIgnoreCase(countryCode)) {
            sendSmsMainlandChina(mobileNumber, code);
        } else {
            log.debug("skip none mainland china sms message request");
        }
        log.debug("end of sending sms");
    }

    private void sendSmsMainlandChina(String mobileNumber, String code) {
        com.aliyun.dysmsapi20170525.models.SendSmsRequest request =
            new com.aliyun.dysmsapi20170525.models.SendSmsRequest()
                .setSignName(signName)
                .setTemplateCode(templateCode)
                .setPhoneNumbers(mobileNumber)
                .setTemplateParam("{\"code\":\"" + code + "\"}");
        com.aliyun.teautil.models.RuntimeOptions runtime =
            new com.aliyun.teautil.models.RuntimeOptions();
        try {
            smsClient.sendSmsWithOptions(request, runtime);
        } catch (Exception ex) {
            log.error("unable to send sms", ex);
            throw new DefinedRuntimeException("unable to send sms", "1097",
                HttpResponseCode.NOT_HTTP);
        }
    }


}