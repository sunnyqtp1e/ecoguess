import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class GameLogic {
    
    private Animal targetAnimal;
    private String targetWord;
    private int maxAttempts;
    private int currentAttempt;
    private String currentGuess;
    private List<GuessResult> guesses;
    private boolean gameOver;
    private boolean won;
    private Map<Character, LetterStatus> keyboardState;
    
    public enum LetterStatus {
        CORRECT, PRESENT, ABSENT, UNUSED
    }
    
    public GameLogic(Animal animal) {
        this.targetAnimal = animal;
        this.targetWord = animal.getName().toUpperCase();
        this.maxAttempts = 6;
        this.currentAttempt = 0;
        this.currentGuess = "";
        this.guesses = new ArrayList<>();
        this.gameOver = false;
        this.won = false;
        this.keyboardState = new HashMap<>();
        initializeKeyboard();
    }
    
    private void initializeKeyboard() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
        for (int i = 0; i < alphabet.length(); i++) {
            char letter = alphabet.charAt(i);
            keyboardState.put(letter, LetterStatus.UNUSED);
        }
    }
    
    public boolean addLetter(char letter) {
        if (currentGuess.length() < targetWord.length() && !gameOver) {
            currentGuess += Character.toUpperCase(letter);
            return true;
        }
        return false;
    }
    
    public void deleteLetter() {
        if (currentGuess.length() > 0) {
            currentGuess = currentGuess.substring(0, currentGuess.length() - 1);
        }
    }
    
    public boolean canSubmitGuess() {
        return currentGuess.length() == targetWord.length() && !gameOver;
    }
    
    public boolean isValidLetter(char letter) {
        return Character.isLetter(letter);
    }
    
    public GuessResult submitGuess() {
        if (!canSubmitGuess()) {
            return null;
        }
        
        GuessResult result = new GuessResult(currentGuess);
        
        Map<Character, Integer> letterCounts = new HashMap<>();
        for (int i = 0; i < targetWord.length(); i++) {
            char letter = targetWord.charAt(i);
            letterCounts.put(letter, letterCounts.getOrDefault(letter, 0) + 1);
        }
        
        LetterStatus[] evaluation = new LetterStatus[currentGuess.length()];
        for (int i = 0; i < currentGuess.length(); i++) {
            char guessLetter = currentGuess.charAt(i);
            char targetLetter = targetWord.charAt(i);
            
            if (guessLetter == targetLetter) {
                evaluation[i] = LetterStatus.CORRECT;
                letterCounts.put(guessLetter, letterCounts.get(guessLetter) - 1);
            } else {
                evaluation[i] = null;
            }
        }
        
        for (int i = 0; i < currentGuess.length(); i++) {
            if (evaluation[i] == LetterStatus.CORRECT) {
                continue;
            }
            
            char guessLetter = currentGuess.charAt(i);
            
            if (targetWord.indexOf(guessLetter) != -1 && 
                letterCounts.getOrDefault(guessLetter, 0) > 0) {
                evaluation[i] = LetterStatus.PRESENT;
                letterCounts.put(guessLetter, letterCounts.get(guessLetter) - 1);
            } else {
                evaluation[i] = LetterStatus.ABSENT;
            }
        }
        
        result.setEvaluation(evaluation);
        updateKeyboardState(currentGuess, evaluation);
        guesses.add(result);
        
        if (currentGuess.equals(targetWord)) {
            won = true;
            gameOver = true;
        }
        
        currentAttempt++;
        
        if (currentAttempt >= maxAttempts && !won) {
            gameOver = true;
        }
        
        currentGuess = "";
        
        return result;
    }
    
    private void updateKeyboardState(String guess, LetterStatus[] evaluation) {
        for (int i = 0; i < guess.length(); i++) {
            char letter = guess.charAt(i);
            LetterStatus newStatus = evaluation[i];
            LetterStatus currentStatus = keyboardState.get(letter);
            
            if (currentStatus != LetterStatus.CORRECT) {
                if (newStatus == LetterStatus.CORRECT) {
                    keyboardState.put(letter, LetterStatus.CORRECT);
                } else if (newStatus == LetterStatus.PRESENT && 
                           currentStatus != LetterStatus.CORRECT) {
                    keyboardState.put(letter, LetterStatus.PRESENT);
                } else if (newStatus == LetterStatus.ABSENT && 
                           currentStatus == LetterStatus.UNUSED) {
                    keyboardState.put(letter, LetterStatus.ABSENT);
                }
            }
        }
    }
    
    public String getCurrentGuess() {
        return currentGuess;
    }
    
    public int getCurrentAttempt() {
        return currentAttempt;
    }
    
    public int getMaxAttempts() {
        return maxAttempts;
    }
    
    public boolean isGameOver() {
        return gameOver;
    }
    
    public boolean isWon() {
        return won;
    }
    
    public Animal getTargetAnimal() {
        return targetAnimal;
    }
    
    public String getTargetWord() {
        return targetWord;
    }
    
    public List<GuessResult> getGuesses() {
        return new ArrayList<>(guesses);
    }
    
    public Map<Character, LetterStatus> getKeyboardState() {
        return new HashMap<>(keyboardState);
    }
}

class GuessResult {
    private String word;
    private GameLogic.LetterStatus[] evaluation;
    
    public GuessResult(String word) {
        this.word = word;
        this.evaluation = new GameLogic.LetterStatus[word.length()];
    }
    
    public void setEvaluation(GameLogic.LetterStatus[] evaluation) {
        this.evaluation = evaluation;
    }
    
    public String getWord() {
        return word;
    }
    
    public GameLogic.LetterStatus[] getEvaluation() {
        return evaluation;
    }
    
    public GameLogic.LetterStatus getLetterStatus(int index) {
        if (index >= 0 && index < evaluation.length) {
            return evaluation[index];
        }
        return GameLogic.LetterStatus.UNUSED;
    }
}