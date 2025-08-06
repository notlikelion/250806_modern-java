package step2.biz;

import step2.data.*;

public interface LLM {
     TextGenerationResult textGeneration(TextGenerationParam param);
     TextToSpeechResult textToSpeech(TextToSpeechParam param);
    ReasoningResult reasoning(ReasoningParam param);
}
