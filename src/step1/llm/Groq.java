package step1.llm;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Groq extends LLM {
    private final String GROQ_API_KEY;

    public Groq() {
        // 생성자가 이 Groq 객체를 만들 때 검사하게 하는 것.
        GROQ_API_KEY = System.getenv("GROQ_API_KEY");
        if (GROQ_API_KEY == null) {
            throw new RuntimeException("GROQ_API_KEY가 세팅되지 않았습니다");
        }
    }

    // 복잡한 걸 세팅할 필요 없이 바로 요청용 클라이언트(역할)
    private final HttpClient client = HttpClient.newHttpClient();
    // 반복해서 쓸 수 있는 친구
    // 온갖 메서드에서 계속 쓸 것.

    private String useGroq(String url, String prompt, String model) {
        // request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url)) // URI 타입으로 변환
                .headers("Content-Type", "application/json",
                        // 구글은 x-googl-api-key?
                        "Authorization", "Bearer %s".formatted(GROQ_API_KEY)
                        // JWT 인증방법과 연관
                )
                .POST(HttpRequest.BodyPublishers.ofString(
                        """
                            {
                                    "messages": [
                                    {
                                        "role": "user",
                                        "content": "%s"
                                    }
                                     ],
                                    "model": "%s"
                            }
                            """.formatted(prompt, model)
                )) // POST 요청을 넣기 위해선 'body'
                .build();
        // response
        String body = null; // 초기화를 안하면 뒤에서 쓰기 곤란.
        try {
            HttpResponse<String> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofString()
            );
            // String body = response.body(); // block 때문에...
            return response.body();
            // body = response.body();
            // 1번 : 그냥 여기 안에서 body를 써서 return...
            // 2번 : 이 친구를 처리한 다음...
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        return body;
    }

    private byte[] useGroqAudio(String url, String prompt, String model, String template) {
        // request
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url)) // URI 타입으로 변환
                .headers("Content-Type", "application/json",
                        // 구글은 x-googl-api-key?
                        "Authorization", "Bearer %s".formatted(GROQ_API_KEY)
                        // JWT 인증방법과 연관
                )
                .POST(HttpRequest.BodyPublishers.ofString(
                        template.formatted(prompt, model)
                )) // POST 요청을 넣기 위해선 'body'
                .build();
        // response
        String body = null; // 초기화를 안하면 뒤에서 쓰기 곤란.
        try {
            HttpResponse<byte[]> response = client.send(
                    request,
                    HttpResponse.BodyHandlers.ofByteArray()
            );
            // String body = response.body(); // block 때문에...
            return response.body();
            // body = response.body();
            // 1번 : 그냥 여기 안에서 body를 써서 return...
            // 2번 : 이 친구를 처리한 다음...
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
//        return body;
    }

    final private String groqChatURL = "https://api.groq.com/openai/v1/chat/completions";

    @Override
    public String generateText(String prompt) {
        // -> body(POST)를 추출
        String result = useGroq(
                groqChatURL,
                prompt,
                "moonshotai/kimi-k2-instruct")
                // 정규표현식...
                // "content":
                // "},
                .split("\"content\":\"")[1]
                .split("\"}")[0]
                .trim(); // 추출...
        return result;
    }

    @Override
    public String generateText(String prompt, String model) {
        String result = useGroq(
                groqChatURL,
                prompt,
                model)
                // 정규표현식...
                // "content":
                // "},
                .split("\"content\":\"")[1]
                .split("\"}")[0]
                .trim(); // 추출...
        return result;
    }

    private final String groqSpeechURL = "https://api.groq.com/openai/v1/audio/speech";

    @Override
    public byte[] changeTextToSpeech(String prompt) {
        byte[] result = useGroqAudio(groqSpeechURL, prompt, "playai-tts", """
                {
                         "input": "%s",
                         "model": "%s",
                         "voice": "Aaliyah-PlayAI",
                         "response_format": "wav"
                }
                """); // formatted -> %s1 => prompt, %s2 => model.
        return result;
    }

    @Override
    public String useReasoning(String prompt) {
        return "";
    }
}
