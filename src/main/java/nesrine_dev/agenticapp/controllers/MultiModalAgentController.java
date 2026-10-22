package nesrine_dev.agenticapp.controllers;

import nesrine_dev.agenticapp.outputs.CniModal;
import nesrine_dev.agenticapp.outputs.MovieList;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
public class MultiModalAgentController {
    private ChatClient chatClient;
    @Value("classpath:/images/cni_fr.jpg")
    private Resource image;

    @Value("classpath:/images/manuscrit.jpg")
    private Resource image2;


    public MultiModalAgentController(ChatClient.Builder chatClient) {
        this.chatClient = chatClient
                .defaultAdvisors(new SimpleLoggerAdvisor())
                 .build();
    }

    @GetMapping("/describe")
    public CniModal describeImage() {
        String systemMessage = """
                Donnes moi une description de limage fournie
                """;
        return chatClient.prompt()
                .system(systemMessage)
                .user(promptUserSpec -> promptUserSpec.text("decrire cette image")
                        .media(MediaType.IMAGE_JPEG, image))
                .call()
                .entity(CniModal.class);
    }

    @PostMapping(value = "/askImage", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public String askImage(@RequestParam String query,
                           @RequestParam(name = "file") MultipartFile file) throws IOException {
        byte[] bytes = file.getBytes();
        String systemMessage = """
                reponds à la question de lutilisateur à propos de limage manuscrite fournie
                """;
        return chatClient.prompt()
                .system(systemMessage)
                .user(promptUserSpec -> promptUserSpec.text(query)
                        .media(MediaType.IMAGE_JPEG, new ByteArrayResource(bytes)))
                .call()
                .content();
    }
}
