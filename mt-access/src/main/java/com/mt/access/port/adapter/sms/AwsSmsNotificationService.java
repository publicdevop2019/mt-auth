package com.mt.access.port.adapter.sms;

import com.mt.access.domain.model.notification.SmsNotificationService;
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
    havingValue = "aws"
)
public class AwsSmsNotificationService implements SmsNotificationService {
    @Value("${mt.sms.aws.key-id}")
    private String keyId;
    @Value("${mt.sms.aws.key-secret}")
    private String keySecret;

    @Override
    public void notify(String countryCode, String mobileNumber, String code) {
        // Provide credentials explicitly
        AwsBasicCredentials awsCreds = AwsBasicCredentials.create(
            keyId,
            keySecret
        );
            PublishRequest request = PublishRequest.builder()
                .message("Your verification code: " + code)
                .phoneNumber("+" + countryCode + mobileNumber)
                .build();
        try (
            SnsClient snsClient = SnsClient.builder().region(Region.US_EAST_2)
                .credentialsProvider(StaticCredentialsProvider.create(awsCreds))
                .build();
        ) {
            PublishResponse result = snsClient.publish(request);
            log.info("sms success with {}", result.messageId());
        }


    }
}
