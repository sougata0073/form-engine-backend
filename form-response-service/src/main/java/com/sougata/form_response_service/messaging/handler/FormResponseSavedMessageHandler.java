package com.sougata.form_response_service.messaging.handler;

import com.sougata.form_engine.constant.Messaging;
import com.sougata.form_engine.constant.MessagingChannelNames;
import org.jspecify.annotations.Nullable;
import org.springframework.data.redis.connection.Message;
import org.springframework.data.redis.connection.MessageListener;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;

@Component(MessagingChannelNames.FORM_RESPONSE_SAVED + "_" + Messaging.MESSAGE_HANDLER_SUFFIX)
public class FormResponseSavedMessageHandler implements MessageListener {

    @Override
    public void onMessage(Message message, byte @Nullable [] pattern) {
        System.out.println(
                "Message: " + new String(message.getBody(), StandardCharsets.UTF_8)
                        + ". Body: " + new String(message.getChannel(), StandardCharsets.UTF_8)
        );
    }
}
