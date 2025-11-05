import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * BACK-END LAYER - Server-side code logic and processing
 * This class contains all game logic, business rules, and state management
 */

// ========================================
// OBJECT-ORIENTED PROGRAMMING (OOP)
// Class encapsulates game state and behavior
// ========================================
public class GameLogic {
    
    // OOP: Private instance variables (encapsulation)
    private Animal targetAnimal;
    private String targetWord;
    private int maxAttempts;
    private int currentAttempt;
    private String currentGuess;
    private List<GuessResult> guesses;
    private boolean gameOver;
    private boolean won;
    private Map<Character, LetterStatus> keyboardState;
    
    // OOP: Enum for type safety and organization
    public enum LetterStatus {
        CORRECT, PRESENT, ABSENT, UNUSED
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Constructor with step-by-step initialization
    // ========================================
    public GameLogic(Animal animal) {
        // Step 1: Set target animal
        this.targetAnimal = animal;
        this.targetWord = animal.getName().toUpperCase();
        
        // Step 2: Configure game parameters
        this.maxAttempts = 6;
        this.currentAttempt = 0;
        
        // Step 3: Initialize state variables
        this.currentGuess = "";
        this.guesses = new ArrayList<>();
        
        // Step 4: Set game status flags
        this.gameOver = false;
        this.won = false;
        
        // Step 5: Initialize keyboard tracking
        this.keyboardState = new HashMap<>();
        initializeKeyboard();
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Sequential keyboard state setup
    // ========================================
    private void initializeKeyboard() {
        String alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        
        // IMPERATIVE: Loop through alphabet
        for (int i = 0; i < alphabet.length(); i++) {
            char letter = alphabet.charAt(i);
            keyboardState.put(letter, LetterStatus.UNUSED);
        }
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Direct state modification
    // ========================================
    public boolean addLetter(char letter) {
        // LOGIC: Conditional validation before action
        if (currentGuess.length() < targetWord.length() && !gameOver) {
            // IMPERATIVE: Modify state directly
            currentGuess += Character.toUpperCase(letter);
            return true;
        }
        return false;
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // State mutation operation
    // ========================================
    public void deleteLetter() {
        // LOGIC: Check condition before modification
        if (currentGuess.length() > 0) {
            // IMPERATIVE: Change state by removing character
            currentGuess = currentGuess.substring(0, currentGuess.length() - 1);
        }
    }
    
    // ========================================
    // LOGIC PROGRAMMING
    // Boolean logic for business rules
    // ========================================
    public boolean canSubmitGuess() {
        // LOGIC: Combine multiple conditions with AND
        return currentGuess.length() == targetWord.length() && !gameOver;
    }
    
    // ========================================
    // LOGIC PROGRAMMING
    // Boolean validation function
    // ========================================
    public boolean isValidLetter(char letter) {
        // LOGIC: Character validation
        return Character.isLetter(letter);
    }
    
    // ========================================
    // BACK-END PROCESSING
    // Core game logic combining multiple paradigms
    // IMPERATIVE + LOGIC + FUNCTIONAL
    // ========================================
    public GuessResult submitGuess() {
        // LOGIC: Validate before processing
        if (!canSubmitGuess()) {
            return null;
        }
        
        // IMPERATIVE: Create result object step-by-step
        GuessResult result = new GuessResult(currentGuess);
        
        // Step 1: Count letter frequencies (IMPERATIVE)
        Map<Character, Integer> letterCounts = new HashMap<>();
        for (int i = 0; i < targetWord.length(); i++) {
            char letter = targetWord.charAt(i);
            letterCounts.put(letter, letterCounts.getOrDefault(letter, 0) + 1);
        }
        
        // Step 2: First pass - mark correct positions (IMPERATIVE + LOGIC)
        LetterStatus[] evaluation = new LetterStatus[currentGuess.length()];
        for (int i = 0; i < currentGuess.length(); i++) {
            char guessLetter = currentGuess.charAt(i);
            char targetLetter = targetWord.charAt(i);
            
            // LOGIC: Position-based evaluation
            if (guessLetter == targetLetter) {
                evaluation[i] = LetterStatus.CORRECT;
                letterCounts.put(guessLetter, letterCounts.get(guessLetter) - 1);
            } else {
                evaluation[i] = null; // Mark for second pass
            }
        }
        
        // Step 3: Second pass - mark present/absent (IMPERATIVE + LOGIC)
        for (int i = 0; i < currentGuess.length(); i++) {
            // LOGIC: Skip if already marked correct
            if (evaluation[i] == LetterStatus.CORRECT) {
                continue;
            }
            
            char guessLetter = currentGuess.charAt(i);
            
            // LOGIC: Complex conditional for letter presence
            if (targetWord.indexOf(guessLetter) != -1 && 
                letterCounts.getOrDefault(guessLetter, 0) > 0) {
                evaluation[i] = LetterStatus.PRESENT;
                letterCounts.put(guessLetter, letterCounts.get(guessLetter) - 1);
            } else {
                evaluation[i] = LetterStatus.ABSENT;
            }
        }
        
        // Step 4: Store evaluation (IMPERATIVE)
        result.setEvaluation(evaluation);
        
        // Step 5: Update keyboard state (IMPERATIVE)
        updateKeyboardState(currentGuess, evaluation);
        
        // Step 6: Add to history (IMPERATIVE)
        guesses.add(result);
        
        // ========================================
        // LOGIC PROGRAMMING
        // Win/lose condition evaluation
        // ========================================
        // LOGIC: Check for exact match
        if (currentGuess.equals(targetWord)) {
            won = true;
            gameOver = true;
        }
        
        // IMPERATIVE: Update attempt counter
        currentAttempt++;
        
        // LOGIC: Check max attempts reached
        if (currentAttempt >= maxAttempts && !won) {
            gameOver = true;
        }
        
        // IMPERATIVE: Reset for next guess
        currentGuess = "";
        
        return result;
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Sequential keyboard state updates
    // ========================================
    private void updateKeyboardState(String guess, LetterStatus[] evaluation) {
        // IMPERATIVE: Loop through each letter
        for (int i = 0; i < guess.length(); i++) {
            char letter = guess.charAt(i);
            LetterStatus newStatus = evaluation[i];
            LetterStatus currentStatus = keyboardState.get(letter);
            
            // LOGIC: Priority-based status update
            // CORRECT > PRESENT > ABSENT > UNUSED
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
    
    // ========================================
    // FUNCTIONAL PROGRAMMING
    // Pure getter functions - no side effects
    // ========================================
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
    
    // FUNCTIONAL: Returns immutable copy
    public List<GuessResult> getGuesses() {
        return new ArrayList<>(guesses);
    }
    
    // FUNCTIONAL: Returns immutable copy
    public Map<Character, LetterStatus> getKeyboardState() {
        return new HashMap<>(keyboardState);
    }
}

// ========================================
// OBJECT-ORIENTED PROGRAMMING (OOP)
// Class representing a single guess result
// ========================================
class GuessResult {
    // OOP: Private fields (encapsulation)
    private String word;
    private GameLogic.LetterStatus[] evaluation;
    
    // OOP: Constructor
    public GuessResult(String word) {
        this.word = word;
        this.evaluation = new GameLogic.LetterStatus[word.length()];
    }
    
    // OOP: Setter method
    public void setEvaluation(GameLogic.LetterStatus[] evaluation) {
        this.evaluation = evaluation;
    }
    
    // OOP: Getter methods
    // FUNCTIONAL: Pure functions
    public String getWord() {
        return word;
    }
    
    public GameLogic.LetterStatus[] getEvaluation() {
        return evaluation;
    }
    
    // FUNCTIONAL: Pure function with validation
    // LOGIC: Bounds checking
    public GameLogic.LetterStatus getLetterStatus(int index) {
        if (index >= 0 && index < evaluation.length) {
            return evaluation[index];
        }
        return GameLogic.LetterStatus.UNUSED;
    }
}