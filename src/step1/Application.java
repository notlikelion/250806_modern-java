package step1;

import step1.llm.Gemini;
import step1.llm.Groq;
import step1.llm.LLM;

public class Application {
    public static void main(String[] args) {
        LLM llm = new Groq();
//        LLM llm = new Gemini();
        // 똑같이 실행이 될까요?
        String prompt1 = "저녁 메뉴 추천해줘";
        String result1 = llm.generateText(prompt1);
        System.out.println(result1);
        String prompt2 = "Hello World!";
        String result2 = llm.changeTextToSpeech(prompt2);
        System.out.println(result2);
        String prompt3 = "클라우드 엔지니어가 되는 방법을 상세히 알려줘";
        String result3 = llm.useReasoning(prompt3);
        System.out.println(result3);
    }
}
