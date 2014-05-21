package models;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * Created by kedar on 5/20/14.
 */

public class QuestionGenerator {

    private static ArrayList<String> mandatoryQuestions = new ArrayList<String>();
    private static ArrayList<String> optionalQuestions = new ArrayList<String>();
    static {
        mandatoryQuestions.addAll(Arrays.asList(
            "What am I doing with my life?",
            "What do I like to do in my free time?"));
        optionalQuestions.addAll(Arrays.asList(
            "This is me in 3 words...",
            "If I could visit one country in the world, which would it be and why?",
            "If I could have any superpower, what would it be and why?",
            "What is my biggest goal in life right now?",
            "If I could get myself anything, what would I buy?",
            "What is my dream job?",
            "What skill or talent do I wish I had or were better at?",
            "What color best describes me and why?",
            "What's my idea of a perfect date?"));
    }

    public static ProfileQuestion[] generate (int amount) {
        ProfileQuestion[] result = new ProfileQuestion[amount];
        result[0] = new ProfileQuestion(mandatoryQuestions.get(0));
        result[1] = new ProfileQuestion(mandatoryQuestions.get(1));
        ArrayList<String> optionalCopy = (ArrayList<String>) optionalQuestions.clone();

        for (int j=2; j < amount; j++) {
            int index = (int) Math.round(Math.random()*optionalCopy.size());
            result[j] = new ProfileQuestion(optionalCopy.get(index));
            optionalCopy.remove(index);
        }

        return result;
    }
}
