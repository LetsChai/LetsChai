package models;

/**
 * Created by kedar on 5/20/14.
 */
public class ProfileQuestion {

    private String question;
    private String answer;

    public ProfileQuestion (String question) {
        this.question = question;
    }

    public String getAnswer() { return answer;  }
    public void setAnswer(String answer) { this.answer = answer; }
    public String getQuestion() { return question; }

    public String toString () {
        return "question:" + question + ",answer:" + answer + "; ";
    }

}
