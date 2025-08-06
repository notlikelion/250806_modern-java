package step2.biz;

import step2.data.TextGenerationParam;
import step2.data.TextGenerationResult;
import step2.data.TextToSpeechParam;
import step2.data.TextToSpeechResult;

public interface LLM {
     TextGenerationResult textGeneration(TextGenerationParam param);
     TextToSpeechResult textToSpeech(TextToSpeechParam param);
//    reasoning()
}
