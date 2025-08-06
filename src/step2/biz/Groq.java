package step2.biz;

import step2.data.TextGenerationParam;
import step2.data.TextGenerationResult;
import step2.data.TextToSpeechParam;
import step2.data.TextToSpeechResult;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Groq implements LLM {
    @Override
    public TextGenerationResult textGeneration(TextGenerationParam param) {
        // Record => 생성자로 무조건 private final 형태의 바꿀 수 없는 데이터를 주입 받고,
        // 그거를 getter(본인 속성명())를 자동으로 생성해주는 형태
//        param.model() // 딱딱 떨어지는 값.
        String modelName = "";
        switch (param.model()) {
            // 가지고 있는 enum을 기준으로 switch할 수 있게 해줌.
            case KIMI:
                modelName = "moonshotai/kimi-k2-instruct";
                break;
            case MAVERICK:
                modelName = "meta-llama/llama-4-maverick-17b-128e-instruct";
                break;
            case VERSATILE:
                modelName = "llama-3.3-70b-versatile";
                break;
            default:
                throw new RuntimeException("지원하지 않는 모델");
        }
        String GROQ_API_KEY = System.getenv("GROQ_API_KEY");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.groq.com//openai/v1/chat/completions"))
                .headers("Content-Type", "application/json",
                        "Authorization", "Bearer %s".formatted(GROQ_API_KEY)
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
//            System.out.println(body);
            String result = body
                    .split("\"content\":\"")[1]
                    .split("\"}")[0]
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
            // 가지고 있는 enum을 기준으로 switch할 수 있게 해줌.
            case PLAYAI:
                modelName = "playai-tts";
                break;
            default:
                throw new RuntimeException("지원하지 않는 모델");
        }
        String GROQ_API_KEY = System.getenv("GROQ_API_KEY");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.groq.com/openai/v1/audio/speech"))
                .headers("Content-Type", "application/json",
                        "Authorization", "Bearer %s".formatted(GROQ_API_KEY)
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
}
