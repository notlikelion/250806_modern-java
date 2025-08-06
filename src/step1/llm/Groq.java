package step1.llm;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;

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

    @Override
    public String generateText(String prompt) {
        String url = "https://api.groq.com/openai/v1/chat/completions";
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
                             "model": "moonshotai/kimi-k2-instruct"
                     }
                     """.formatted(prompt)
                )) // POST 요청을 넣기 위해선 'body'
                .build();
        // response
        // -> body(POST)를 추출
        return "";
    }

    @Override
    public String changeTextToSpeech(String prompt) {
        return "";
    }

    @Override
    public String useReasoning(String prompt) {
        return "";
    }
}
