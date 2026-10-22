package nesrine_dev.agenticapp.controllers;

import nesrine_dev.agenticapp.outputs.MovieList;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import nesrine_dev.agenticapp.outputs.Movie;

@RestController
public class AIStructuredAgentController {
    private ChatClient chatClient;
    public AIStructuredAgentController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient
                .defaultAdvisors(new SimpleLoggerAdvisor())
                 .build();
    }

    @GetMapping("/askAgent")
    public MovieList askLLM(@RequestParam String query) {
        String systemMessage = """
                Vous etes specialiste dans le domaine du cinema
                repond à à la question de l'utilisateur a ce propos
                """;
        return chatClient.prompt()
                .system(systemMessage)
                .user(query)
                .call()
                .entity(MovieList.class);
    }
}
