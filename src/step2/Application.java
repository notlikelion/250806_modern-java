package step2;

import step2.biz.Groq;
import step2.biz.LLM;
import step2.data.AIModel;
import step2.data.AIVoice;
import step2.data.TextGenerationParam;
import step2.data.TextToSpeechParam;

public class Application {
    public static void main(String[] args) {
        LLM llm = new Groq();
//        llm.textGeneration(new TextGenerationParam("내일 아침 메뉴 추천 좀"));
        String tem = """
                {
                    "messages": [
                            {
                            "role": "user",
                            "content": "%s"
                        }
                     ],
                    "model": "%s"
                }
                """;
        String result = llm.textGeneration(new TextGenerationParam(
                AIModel.KIMI,
                tem,
                "내일 아침 메뉴 추천 좀")).content(); // 모델명도 넣을 수 있다.
        System.out.println(result);
        String result2 = llm.textGeneration(new TextGenerationParam(
                AIModel.VERSATILE,
                tem,
                "내일 아침 메뉴 추천 좀")).content(); // 모델명도 넣을 수 있다.
        System.out.println(result2);
        String tem2 = """
                {
                         "input": "%s",
                         "model": "%s",
                         "voice": "%s-PlayAI",
                         "response_format": "wav"
                }
                """;
        String result3 = llm.textToSpeech(
                new TextToSpeechParam(
                        AIModel.PLAYAI,
                        tem2,
                        "I think I'm drowning.",
                        AIVoice.Fritz
                )
        ).content();
        System.out.println(result3);
    }
}
