package step2.biz;

import step2.data.*;

import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent".formatted(modelName)))
                .headers("Content-Type", "application/json",
                        "X-goog-api-key", GEMINI_API_KEY
                )
                .POST(HttpRequest.BodyPublishers.ofString(
                        param.template().formatted(
                                param.prompt(),
                                param.voice().name()
                        ))) // POST 요청을 넣기 위해선 'body'
                .build();
        HttpClient client = HttpClient.newHttpClient();
        String filename = null;
        try {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            String body = response.body();
//            System.out.println(body);
            String mimeType = body.split("\"mimeType\": \"")[1]
                    .split("\",")[0];
            String content = body.split("\"data\": \"")[1]
                    .split("}")[0]
                    .replace("\\n", "")
                    .replace("\"", "")
                    .trim();
            System.out.println(body);


            byte[] pcmData = Base64.getDecoder().decode(content);

            float sampleRate = 0;
            Pattern ratePattern = Pattern.compile("rate=(\\d+)");
            Matcher matcher = ratePattern.matcher(mimeType);

            if (matcher.find()) {
                sampleRate = Float.parseFloat(matcher.group(1));
            } else {
                throw new IllegalArgumentException("mimeType에서 샘플링 속도(rate)를 찾을 수 없습니다.");
            }

            // 3. AudioFormat 객체 생성.
            // L16 코덱은 16비트, 1채널 PCM 데이터를 의미합니다.
            int channels = 1;
            int sampleSizeInBits = 16;
            boolean signed = true;
            boolean bigEndian = false;

            AudioFormat format = new AudioFormat(
                    AudioFormat.Encoding.PCM_SIGNED,
                    sampleRate,
                    sampleSizeInBits,
                    channels,
                    (sampleSizeInBits / 8) * channels, // 프레임 크기 계산
                    sampleRate,
                    bigEndian
            );

            ByteArrayInputStream bais = new ByteArrayInputStream(pcmData);
            AudioInputStream audioInputStream = new AudioInputStream(
                    bais,
                    format,
                    pcmData.length / format.getFrameSize()
            );
            filename = System.currentTimeMillis() + ".wav";
            File fileOut = new File(filename);
            AudioSystem.write(audioInputStream, AudioFileFormat.Type.WAVE, fileOut);

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return new TextToSpeechResult(
                filename,
                param.prompt()
        );
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
                .uri(URI.create("https://generativelanguage.googleapis.com/v1beta/models/%s:generateContent".formatted(modelName)))
                .headers("Content-Type", "application/json",
                        "X-goog-api-key", GEMINI_API_KEY
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
            String body = response.body();
            String content = body.split("text\": \"")[1]
                    .split("}")[0]
//                    .replace("\\n", "")
                    .replace("\"", "")
                    .trim();
            return new ReasoningResult(
                    content,
                    param.prompt(),
                    null
            );
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
