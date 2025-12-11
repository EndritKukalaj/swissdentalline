package ch.zhaw.swissdentalline.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenAiConfig {

    @Autowired
    OpenAiChatModel chatModel;

    @Bean
    public ChatClient chatClient() {
        return ChatClient.builder(chatModel).build();
    }
}
