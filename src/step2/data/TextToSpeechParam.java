package step2.data;

public record TextToSpeechParam(
        AIModel model, String template, String prompt, AIVoice voice) {
}
