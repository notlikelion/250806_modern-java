package step2.biz;

import step2.data.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Gemini implements LLM {
    final private String GEMINI_API_KEY = System.getenv("GEMINI_API_KEY");
    @Override
    public TextGenerationResult textGeneration(TextGenerationParam param) {
        String modelName = "";
        switch (param.model()) {
            case GEMINI_FLASH:
                modelName = "gemini-2.0-flash";
                break;
            default:
                throw new RuntimeException("지원하지 않는 모델");
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent".formatted(modelName)))
                .headers("Content-Type", "application/json",
                        "X-goog-api-key", GEMINI_API_KEY
                )
                .POST(HttpRequest.BodyPublishers.ofString(
                        param.template().formatted(param.prompt())
                )) // POST 요청을 넣기 위해선 'body'
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            String body = response.body(); // block 때문에...
//            System.out.println(body);
            String result = body
                    .split("text\": \"")[1]
                    .split("}")[0]
                    .replace("\\n", "")
                    .replace("\"", "")
                    .trim();
            return new TextGenerationResult(
                    result,
                    param.prompt()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public TextToSpeechResult textToSpeech(TextToSpeechParam param) {
        String modelName = "";
        switch (param.model()) {
            case GEMINI_FLASH_TTS:
                modelName = "gemini-2.5-flash-preview-tts";
                break;
            default:
                throw new RuntimeException("지원하지 않는 모델");
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.groq.com/openai/v1/audio/speech"))
                .headers("Content-Type", "application/json",
                        "Authorization", "Bearer %s".formatted(GEMINI_API_KEY)
                )
                .POST(HttpRequest.BodyPublishers.ofString(
                        param.template().formatted(
                                param.prompt(),
                                modelName,
                                // name()
                                param.voice().name())
                )) // POST 요청을 넣기 위해선 'body'
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<byte[]> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofByteArray()
            );
            byte[] body = response.body();
            String filename = "%s.wav".formatted(System.currentTimeMillis());
            try {
                Path path = Paths.get(filename.formatted(System.currentTimeMillis()));
                Files.write(
                        path,
                        body);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }

            return new TextToSpeechResult(
                    filename,
                    param.prompt()
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public ReasoningResult reasoning(ReasoningParam param) {
        String modelName = "";
        switch (param.model()) {
            // 가지고 있는 enum을 기준으로 switch할 수 있게 해줌.
            case GEMINI_PRO:
                modelName = "gemini-2.5-pro";
                break;
            default:
                throw new RuntimeException("지원하지 않는 모델");
        }
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.groq.com//openai/v1/chat/completions"))
                .headers("Content-Type", "application/json",
                        "Authorization", "Bearer %s".formatted(GEMINI_API_KEY)
                )
                .POST(HttpRequest.BodyPublishers.ofString(
                        param.template().formatted(param.prompt(),
                                modelName)
                )) // POST 요청을 넣기 위해선 'body'
                .build();
        HttpClient client = HttpClient.newHttpClient();
        try {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            String body = response.body(); // block 때문에...
            System.out.println(body);
            String[] tmp = body
                    .split("\"content\":\"")[1]
                    .split(",\"reasoning\"");
            String content = tmp[0].trim();
            String thinking = tmp[1].split("\"}")[0].trim();
            return new ReasoningResult(
                    content,
                    param.prompt(),
                    thinking
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
