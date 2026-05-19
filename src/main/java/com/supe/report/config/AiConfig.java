package com.supe.report.config;

import org.springframework.ai.chat.model.ChatModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@Configuration
public class AiConfig {

    @Value("${supe.ai.provider:anthropic}")
    private String provider;

    @Bean
    @Primary
    public ChatModel primaryChatModel(
            @Qualifier("anthropicChatModel") ChatModel anthropicChatModel,
            @Qualifier("openAiChatModel") ChatModel openAiChatModel,
            @Qualifier("dashScopeChatModel") ChatModel dashScopeChatModel) {
        return switch (provider) {
            case "openai" -> openAiChatModel;
            case "dashscope" -> dashScopeChatModel;
            default -> anthropicChatModel;
        };
    }
}
