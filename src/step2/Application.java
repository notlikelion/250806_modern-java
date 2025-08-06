package step2;

import step2.biz.Groq;
import step2.biz.LLM;
import step2.data.AIModel;
import step2.data.TextGenerationParam;

public class Application {
    public static void main(String[] args) {
        LLM llm = new Groq();
//        llm.textGeneration(new TextGenerationParam("내일 아침 메뉴 추천 좀"));
        String result = llm.textGeneration(new TextGenerationParam(
                AIModel.KIMI,
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
                """,
                "내일 아침 메뉴 추천 좀")).content(); // 모델명도 넣을 수 있다.
        System.out.println(result);
    }
}
