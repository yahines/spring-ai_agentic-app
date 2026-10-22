package nesrine_dev.agenticapp.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AIAgentController {
    private ChatClient chatClient;

    public AIAgentController(ChatClient.Builder builder, ChatMemory memory) {
        this.chatClient = builder
                .defaultAdvisors(new SimpleLoggerAdvisor())
                .defaultAdvisors(MessageChatMemoryAdvisor.builder(memory).build())
                .build();
    }

    @GetMapping("/chat")
    public String askLLM(@RequestParam String query,
                         @RequestParam(defaultValue = "default") String conversationId) {
        List<Message> examples = List.of(
                new UserMessage("6+4"),
                new AssistantMessage("le resultat est : 10")
        );
        return chatClient.prompt()
                .system("repond toujours en majuscule")
                .messages(examples)
                .user(query)
                .advisors(spec -> spec.param(ChatMemory.CONVERSATION_ID, conversationId))
                .call().content();
    }
}
