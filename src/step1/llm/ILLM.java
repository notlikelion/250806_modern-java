package step1.llm;

public interface ILLM {
    /// Text Generation : https://console.groq.com/docs/text-chat
    String generateText(String prompt);
    // 오버로딩
    String generateText(String prompt, String model);
    /// Text to Speech : https://console.groq.com/docs/text-to-speech
    String changeTextToSpeech(String prompt);
    ///  Reasoning : https://console.groq.com/docs/reasoning
    String useReasoning(String prompt);
}
