package step2.data;

public record TextGenerationParam(
//        String prompt, String model) {
        AIModel model, String template, String prompt) {
    // 1. 생성자 관련 작성 안해줘도 알아서 만들어주고
    // 2. setter도 이미 주어져있는 상태에서 final 불변.
    // 데이터 옮기는데 쓰는 객체. DTO.
//    public TextGenerationParam(String prompt) { // 프롬프트만 있는 생성자
//        this(prompt, AIModel.KIMI); // default
//    }
}
