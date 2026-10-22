package nesrine_dev.agenticapp.controllers;

import org.springframework.ai.image.ImagePrompt;
import org.springframework.ai.image.ImageResponse;
import org.springframework.ai.stabilityai.StabilityAiImageModel;
import org.springframework.ai.stabilityai.api.StabilityAiImageOptions;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Base64;

@RestController
public class StableDiffusionController {

    private final StabilityAiImageModel imageModel;

    public StableDiffusionController(StabilityAiImageModel imageModel) {
        this.imageModel = imageModel;
    }

    @GetMapping(value = "/generateImageSD", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> generateImage(@RequestParam String query) {
        ImagePrompt prompt = new ImagePrompt(query,
                StabilityAiImageOptions.builder()
                        .model("stable-diffusion-v1-6")
                        .n(1)
                        .width(512)
                        .height(512)
                        .build());
        ImageResponse response = imageModel.call(prompt);
        byte[] imageBytes = Base64.getDecoder().decode(
                response.getResult().getOutput().getB64Json());
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_PNG)
                .body(imageBytes);
    }
}
