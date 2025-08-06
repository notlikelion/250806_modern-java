package step1;

//import step1.llm.Gemini;
import step1.llm.Groq;
import step1.llm.LLM;

import java.util.List;

public class Application {
    public static void main(String[] args) {
        LLM llm = new Groq();
//        LLM llm = new Gemini();
        // 똑같이 실행이 될까요?
//        String prompt1 = "저녁 메뉴 추천해줘";
//        String prompt1 = "저녁 메뉴 추천해줘. 100자 이내로. 한식과 양식, 일식, 중식 중에서. 시켜먹거나 사먹는 걸로. 결과만 작성해주고, 꾸미는 문법은 쓰지 말아줘.";
//        String result1 = llm.generateText(prompt1); // kimi-k2
//        String kimi = llm.generateText(prompt1, "moonshotai/kimi-k2-instruct");
//        System.out.println("kimi : %s".formatted(kimi));
//        String maverick = llm.generateText(prompt1, "meta-llama/llama-4-maverick-17b-128e-instruct"); // llama-4-maverick
//        System.out.println("maverick : %s".formatted(maverick));
//        String scout = llm.generateText(prompt1, "meta-llama/llama-4-scout-17b-16e-instruct"); // llama-4-scout
//        System.out.println("scout : %s".formatted(scout));

        List<String> menus = List.of("한식", "중식", "양식", "일식");
            menus.stream()
                    .parallel()
                    .map(x -> "%s 하나를 추천 받는 프롬프트를 작성해줘.".formatted(x))
                    .map(x -> llm.generateText(x, "moonshotai/kimi-k2-instruct"))
                    .map(x -> "%s. 결과만 작성하고, 꾸미는 문법 없이 100글자 이내의 추천 메뉴와 추천 이유만 작성.".formatted(x))
                    .map(x -> llm.generateText(x,
                            "meta-llama/llama-4-maverick-17b-128e-instruct"))
                    .forEach(System.out::println);

        String prompt2 = "Hello World!";
        String result2 = llm.changeTextToSpeech(prompt2);
        System.out.println(result2);
        String prompt3 = "클라우드 엔지니어가 되는 방법을 상세히 알려줘";
        String result3 = llm.useReasoning(prompt3);
        System.out.println(result3);
    }
}
