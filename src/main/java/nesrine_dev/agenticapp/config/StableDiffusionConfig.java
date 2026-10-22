package nesrine_dev.agenticapp.config;

import org.springframework.ai.stabilityai.StabilityAiImageModel;
import org.springframework.ai.stabilityai.api.StabilityAiApi;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class StableDiffusionConfig {

    @Value("${spring.ai.stabilityai.api-key:none}")
    private String apiKey;

    @Bean
    public StabilityAiImageModel stabilityAiImageModel() {
        return new StabilityAiImageModel(new StabilityAiApi(apiKey));
    }
}
