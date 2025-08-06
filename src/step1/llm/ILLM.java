package step1.llm;

public interface ILLM {
    /// Text Generation : https://console.groq.com/docs/text-chat
    String generateText(String prompt);
    /// Text to Speech : https://console.groq.com/docs/text-to-speech
    String changeTextToSpeech(String prompt);
    ///  Reasoning : https://console.groq.com/docs/reasoning
    String useReasoning(String prompt);
}
