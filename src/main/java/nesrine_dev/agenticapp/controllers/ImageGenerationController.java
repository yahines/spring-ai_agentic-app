package nesrine_dev.agenticapp.controllers;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.image.ImageModel;
import org.springframework.ai.image.ImageOptions;
import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.openai.OpenAiImageModel;
import org.springframework.ai.openai.OpenAiImageOptions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class ImageGenerationController {

    private OpenAiImageModel openAiImageModel;

    public ImageGenerationController(OpenAiImageModel openAiImageModel) {
        this.openAiImageModel = openAiImageModel;
    }

    @GetMapping("/generateImage")
    public String generateImage(@RequestParam String query) {
        ImageOptions imageOptions = OpenAiImageOptions.builder()
                                    .quality("hd")
                                    .model("dall-e-3")
                                    .width(640)
                                    .height(480)
                                    .build();
        ImagePrompt imagePrompt = new ImagePrompt(query, imageOptions);
        String url = openAiImageModel.call(imagePrompt).getResult().getOutput().getUrl();
        return url;
    }
}
