package ru.itis.oris.chat.game;

import java.util.HashMap;
import java.util.Map;

public class GameLogic {
    private static Map<String, String> questionAnswers = new HashMap<>();

    public static boolean isCorrectAnswer(String question, String answer) {
        if (questionAnswers.containsKey(question)) {
            if (questionAnswers.get(question) == answer) {
                return true;
            }
        }

        return false;
    }
}
