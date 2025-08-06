package step2;

import step2.biz.Gemini;
import step2.biz.Groq;
import step2.biz.LLM;
import step2.data.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Application {
    public static void main(String[] args) {
//        LLM llm = new Groq();
        LLM llm = new Gemini();
//        llm.textGeneration(new TextGenerationParam("내일 아침 메뉴 추천 좀"));
//        String tem = """
//                {
//                    "messages": [
//                            {
//                            "role": "user",
//                            "content": "%s"
//                        }
//                     ],
//                    "model": "%s"
//                }
//                """;
        String tem = """
                {
                     "contents": [
                       {
                         "role": "user",
                         "parts": [
                           {
                             "text": "%s"
                           },
                         ]
                       },
                     ]
                 }
                """;
        String result = llm.textGeneration(new TextGenerationParam(
                AIModel.GEMINI_FLASH,
                tem,
                "내일 아침 메뉴 추천 좀")).content(); // 모델명도 넣을 수 있다.
        System.out.println(result);
//        String result2 = llm.textGeneration(new TextGenerationParam(
//                AIModel.VERSATILE,
//                tem,
//                "내일 아침 메뉴 추천 좀")).content(); // 모델명도 넣을 수 있다.
//        System.out.println(result2);
        String tem2 = """
            {
                "contents": [
                  {
                    "role": "user",
                    "parts": [
                      {
                        "text": "%s"
                      },
                    ]
                  },
                ],
                "generationConfig": {
                  "responseModalities": ["audio", ],
                  "speech_config": {
                    "voice_config": {
                      "prebuilt_voice_config": {
                        "voice_name": "Zephyr"
                      }
                    }
                  },
                }
            }
            """;
        String result2 = llm.textToSpeech(
                new TextToSpeechParam(
                        AIModel.GEMINI_FLASH_TTS,
                        tem2,
                        "오늘 서울은 하루 종일 흐림",
                        null
                )
        ).content();
        System.out.println(result2);
        ReasoningResult reasoningResult = llm.reasoning(
                new ReasoningParam(
                        AIModel.GEMINI_PRO,
                        tem,
                        "AWS에서 가장 중요한 것들"
                        ));
        System.out.println(reasoningResult.thinking());
//        System.out.println(reasoningResult.content());
        try {
            Path filename = Paths.get("%s.md".formatted(System.currentTimeMillis()));
            Files.writeString(
                    filename,
                    reasoningResult.content().replace("\\n", "\n"));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
