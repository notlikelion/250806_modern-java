package step2.biz;

import step2.data.TextGenerationParam;
import step2.data.TextGenerationResult;

public interface LLM {
     TextGenerationResult textGeneration(TextGenerationParam param);
//    textToSpeech()
//    reasoning()
}
