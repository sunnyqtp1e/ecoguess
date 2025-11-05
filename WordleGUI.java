import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;

/**
 * FRONT-END LAYER - User interface providing interaction with the system
 * This class handles all visual display and user input
 */

// ========================================
// OBJECT-ORIENTED PROGRAMMING (OOP)
// Class inheritance - extends JFrame
// ========================================
public class WordleGUI extends JFrame {
    
    // OOP: Private instance variables (encapsulation)
    private Database database;
    private GameLogic gameLogic;
    private AnimalAPIService apiService;
    
    // UI Components
    private JPanel gameBoard;
    private JPanel keyboardPanel;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel errorLabel;
    private JLabel scoreLabel;
    private JLabel hintLabel;
    private JButton resetScoreButton;
    private JButton hintButton;
    private JButton[][] tiles;
    private Map<Character, JButton> keyButtons;
    
    // Constants
    private static final int TILE_SIZE = 60;
    private static final Color COLOR_CORRECT = new Color(106, 170, 100);
    private static final Color COLOR_PRESENT = new Color(201, 180, 88);
    private static final Color COLOR_ABSENT = new Color(120, 124, 126);
    private static final Color COLOR_UNUSED = new Color(211, 214, 218);
    private static final Color COLOR_FILLED = new Color(135, 138, 140);
    private static final Color COLOR_EMPTY = Color.WHITE;
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Constructor with sequential initialization steps
    // ========================================
    public WordleGUI() {
        // Step 1: Initialize database (SQL connection)
        this.database = new Database();
        
        // Step 1.5: Initialize API service
        this.apiService = new AnimalAPIService();
        
        // Step 2: Start new game
        startNewGame();
        
        // Step 3: Configure window properties
        setTitle("Endangered Animals Wordle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Step 4: Build user interface
        initializeUI();
        
        // Step 5: Setup keyboard input
        addKeyboardListener();
        
        // Step 6: Add window listener for cleanup
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                database.close();
            }
        });
        
        // Step 7: Finalize and display window
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Sequential UI component initialization
    // ========================================
    private void initializeUI() {
        // Step 1: Create main container
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(102, 126, 234));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Step 2: Create header with title and score
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(500, 50));
        
        // Step 3: Add title (left side)
        titleLabel = new JLabel("Endangered Wordle");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Step 4: Create score panel (right side)
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        scorePanel.setOpaque(false);
        
        // Step 5: Add score label
        scoreLabel = new JLabel();
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        scoreLabel.setForeground(Color.WHITE);
        updateScoreDisplay(); // DATABASE: Load and display scores from SQL
        scorePanel.add(scoreLabel);
        
        // Step 6: Add reset button
        resetScoreButton = new JButton("Reset");
        resetScoreButton.setFont(new Font("Arial", Font.PLAIN, 12));
        resetScoreButton.setPreferredSize(new Dimension(60, 25));
        resetScoreButton.setFocusable(false);
        resetScoreButton.setToolTipText("Reset Scores");
        resetScoreButton.addActionListener(e -> resetScores());
        scorePanel.add(resetScoreButton);
        
        headerPanel.add(scorePanel, BorderLayout.EAST);
        mainPanel.add(headerPanel);
        
        // Step 7: Add subtitle
        subtitleLabel = new JLabel("Guess the endangered animal");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(230, 230, 230));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitleLabel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Step 8: Create game board
        createGameBoard();
        mainPanel.add(gameBoard);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        // Step 9: Add error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(248, 113, 113));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(errorLabel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Step 9.5: Add hint button
        hintButton = new JButton("💡 Get Hint");
        hintButton.setFont(new Font("Arial", Font.BOLD, 14));
        hintButton.setPreferredSize(new Dimension(120, 35));
        hintButton.setMaximumSize(new Dimension(120, 35));
        hintButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        hintButton.setFocusable(false);
        hintButton.setBackground(new Color(255, 193, 7));
        hintButton.addActionListener(e -> showHint());
        mainPanel.add(hintButton);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Step 9.6: Add hint label
        hintLabel = new JLabel(" ");
        hintLabel.setFont(new Font("Arial", Font.ITALIC, 13));
        hintLabel.setForeground(new Color(255, 235, 59));
        hintLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(hintLabel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        
        // Step 10: Create keyboard
        createKeyboard();
        mainPanel.add(keyboardPanel);
        
        // Step 11: Add to frame
        add(mainPanel);
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Step-by-step game board creation
    // ========================================
    private void createGameBoard() {
        gameBoard = new JPanel();
        gameBoard.setLayout(new GridLayout(gameLogic.getMaxAttempts(), 
                                           gameLogic.getTargetWord().length(), 5, 5));
        gameBoard.setOpaque(false);
        
        int rows = gameLogic.getMaxAttempts();
        int cols = gameLogic.getTargetWord().length();
        tiles = new JButton[rows][cols];
        
        // IMPERATIVE: Nested loops for tile creation
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                JButton tile = new JButton("");
                tile.setPreferredSize(new Dimension(TILE_SIZE, TILE_SIZE));
                tile.setFont(new Font("Arial", Font.BOLD, 24));
                tile.setFocusable(false);
                tile.setBackground(COLOR_EMPTY);
                tile.setBorder(BorderFactory.createLineBorder(COLOR_UNUSED, 2));
                tiles[i][j] = tile;
                gameBoard.add(tile);
            }
        }
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Sequential keyboard construction
    // ========================================
    private void createKeyboard() {
        keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new BoxLayout(keyboardPanel, BoxLayout.Y_AXIS));
        keyboardPanel.setOpaque(false);
        
        keyButtons = new java.util.HashMap<>();
        
        String[] rows = {"QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM"};
        
        // IMPERATIVE: Loop through keyboard rows
        for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            row.setOpaque(false);
            
            // Add ENTER button for last row
            if (rowIndex == 2) {
                JButton enterBtn = createKeyButton("ENTER", true);
                row.add(enterBtn);
            }
            
            // Add letter keys
            String letters = rows[rowIndex];
            for (int i = 0; i < letters.length(); i++) {
                char letter = letters.charAt(i);
                JButton keyBtn = createKeyButton(String.valueOf(letter), false);
                keyButtons.put(letter, keyBtn);
                row.add(keyBtn);
            }
            
            // Add DELETE button for last row
            if (rowIndex == 2) {
                JButton delBtn = createKeyButton("DEL", true);
                row.add(delBtn);
            }
            
            keyboardPanel.add(row);
        }
    }
    
    // ========================================
    // FUNCTIONAL PROGRAMMING
    // Factory method - creates and returns button
    // ========================================
    private JButton createKeyButton(String text, boolean isWide) {
        JButton button = new JButton(text);
        
        // IMPERATIVE: Configure button properties
        if (isWide) {
            button.setPreferredSize(new Dimension(70, 45));
        } else {
            button.setPreferredSize(new Dimension(40, 45));
        }
        
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusable(false);
        button.setBackground(COLOR_UNUSED);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        
        // FUNCTIONAL: Lambda expression for event handling
        button.addActionListener(e -> handleKeyPress(text));
        
        return button;
    }
    
    // ========================================
    // LOGIC PROGRAMMING
    // Event handling with conditional logic
    // ========================================
    private void handleKeyPress(String key) {
        errorLabel.setText(" ");
        
        // LOGIC: Multiple conditional branches
        if (gameLogic.isGameOver()) {
            return;
        }
        
        if (key.equals("ENTER")) {
            submitGuess();
        } else if (key.equals("DEL")) {
            gameLogic.deleteLetter();
            updateBoard();
        } else if (key.length() == 1) {
            // LOGIC: Validate input
            if (gameLogic.isValidLetter(key.charAt(0))) {
                gameLogic.addLetter(key.charAt(0));
                updateBoard();
            }
        }
    }
    
    // ========================================
    // IMPERATIVE + LOGIC PROGRAMMING
    // Guess submission with validation and processing
    // ========================================
    private void submitGuess() {
        // LOGIC: Pre-submission validation
        if (!gameLogic.canSubmitGuess()) {
            showError("Not enough letters!");
            return;
        }
        
        // IMPERATIVE: Process guess
        GuessResult result = gameLogic.submitGuess();
        
        // LOGIC: Check if successful
        if (result != null) {
            // IMPERATIVE: Update UI components
            updateBoard();
            updateKeyboard();
            
            // LOGIC: Check game end condition
            if (gameLogic.isGameOver()) {
                // IMPERATIVE: Save score to SQL database
                String animalName = gameLogic.getTargetAnimal().getName();
                
                if (gameLogic.isWon()) {
                    database.saveWin(animalName); // DATABASE: SQL INSERT/UPDATE
                } else {
                    database.saveLoss(animalName); // DATABASE: SQL INSERT/UPDATE
                }
                
                updateScoreDisplay(); // DATABASE: SQL SELECT query
                
                // IMPERATIVE: Show dialog after delay
                Timer timer = new Timer(500, e -> {
                    showEndGameDialog();
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Sequential board update based on game state
    // ========================================
    private void updateBoard() {
        int currentRow = gameLogic.getCurrentAttempt();
        String currentGuess = gameLogic.getCurrentGuess();
        
        // FIXED: Check bounds before accessing array
        if (currentRow < gameLogic.getMaxAttempts()) {
            // Update current guess row
            for (int col = 0; col < gameLogic.getTargetWord().length(); col++) {
                JButton tile = tiles[currentRow][col];
                
                // LOGIC: Conditional display logic
                if (col < currentGuess.length()) {
                    tile.setText(String.valueOf(currentGuess.charAt(col)));
                    tile.setBackground(COLOR_FILLED);
                    tile.setBorder(BorderFactory.createLineBorder(COLOR_FILLED, 2));
                } else {
                    tile.setText("");
                    tile.setBackground(COLOR_EMPTY);
                    tile.setBorder(BorderFactory.createLineBorder(COLOR_UNUSED, 2));
                }
            }
        }
        
        // Update previous guesses
        List<GuessResult> guesses = gameLogic.getGuesses();
        for (int row = 0; row < guesses.size(); row++) {
            GuessResult guess = guesses.get(row);
            String word = guess.getWord();
            
            for (int col = 0; col < word.length(); col++) {
                JButton tile = tiles[row][col];
                tile.setText(String.valueOf(word.charAt(col)));
                
                // LOGIC: Status-based coloring
                GameLogic.LetterStatus status = guess.getLetterStatus(col);
                Color color = getColorForStatus(status);
                tile.setBackground(color);
                tile.setForeground(Color.WHITE);
                tile.setBorder(BorderFactory.createLineBorder(color, 2));
            }
        }
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Keyboard visual update
    // ========================================
    private void updateKeyboard() {
        Map<Character, GameLogic.LetterStatus> keyboardState = gameLogic.getKeyboardState();
        
        // IMPERATIVE: Iterate and update each key
        for (Map.Entry<Character, GameLogic.LetterStatus> entry : keyboardState.entrySet()) {
            char letter = entry.getKey();
            GameLogic.LetterStatus status = entry.getValue();
            
            JButton keyButton = keyButtons.get(letter);
            if (keyButton != null) {
                Color color = getColorForStatus(status);
                keyButton.setBackground(color);
                
                // LOGIC: Conditional text color
                if (status != GameLogic.LetterStatus.UNUSED) {
                    keyButton.setForeground(Color.WHITE);
                }
            }
        }
    }
    
    // ========================================
    // FUNCTIONAL PROGRAMMING
    // Pure function mapping status to color
    // ========================================
    private Color getColorForStatus(GameLogic.LetterStatus status) {
        // LOGIC: Pattern matching via switch
        switch (status) {
            case CORRECT:
                return COLOR_CORRECT;
            case PRESENT:
                return COLOR_PRESENT;
            case ABSENT:
                return COLOR_ABSENT;
            default:
                return COLOR_UNUSED;
        }
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Display error message with timer
    // ========================================
    private void showError(String message) {
        errorLabel.setText(message);
        
        Timer timer = new Timer(2000, e -> errorLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
    
    // ========================================
    // DATABASE INTERACTION - SQL Query
    // Retrieve and display scores from database
    // ========================================
    private void updateScoreDisplay() {
        // DATABASE: SQL SELECT query for global scores
        int[] scores = database.getGlobalScores();
        int wins = scores[0];
        int losses = scores[1];
        
        // IMPERATIVE: Format and display
        scoreLabel.setText("W:" + wins + " L:" + losses);
    }
    
    // ========================================
    // DATABASE INTERACTION - SQL Update
    // Reset scores in database
    // ========================================
    private void resetScores() {
        // LOGIC: Confirmation dialog
        int result = JOptionPane.showConfirmDialog(
            this,
            "Reset all scores to 0?",
            "Reset Scores",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            // DATABASE: SQL UPDATE query to reset all scores
            database.resetAllScores();
            updateScoreDisplay(); // DATABASE: SQL SELECT to refresh display
        }
    }
    
    // ========================================
    // LOGIC PROGRAMMING
    // Conditional dialog display
    // ========================================
    private void showEndGameDialog() {
        Animal animal = gameLogic.getTargetAnimal();
        
        // LOGIC: Branch based on game outcome
        if (gameLogic.isWon()) {
            showWinDialog(animal);
        } else {
            showLoseDialog(animal);
        }
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Win dialog construction - FIXED: Made bigger
    // ========================================
    private void showWinDialog(Animal animal) {
        JDialog dialog = new JDialog(this, "You Won!", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPanel.setBackground(COLOR_CORRECT);
        
        JLabel nameLabel = new JLabel(animal.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 28));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(nameLabel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JTextArea factArea = new JTextArea(animal.getFunFact());
        factArea.setWrapStyleWord(true);
        factArea.setLineWrap(true);
        factArea.setEditable(false);
        factArea.setFont(new Font("Arial", Font.PLAIN, 14));
        factArea.setForeground(Color.WHITE);
        factArea.setBackground(COLOR_CORRECT);
        factArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        factArea.setPreferredSize(new Dimension(400, 80));
        contentPanel.add(factArea);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JButton restartBtn = new JButton("Play Again");
        restartBtn.setFont(new Font("Arial", Font.BOLD, 16));
        restartBtn.setPreferredSize(new Dimension(150, 40));
        restartBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        restartBtn.addActionListener(e -> {
            dialog.dispose();
            restartGame();
        });
        contentPanel.add(restartBtn);
        
        dialog.add(contentPanel);
        dialog.setMinimumSize(new Dimension(500, 300));
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Lose dialog construction - FIXED: Made bigger
    // ========================================
    private void showLoseDialog(Animal animal) {
        JDialog dialog = new JDialog(this, "Game Over", true);
        dialog.setLayout(new BorderLayout(10, 10));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPanel.setBackground(new Color(248, 113, 113));
        
        JLabel resultLabel = new JLabel("The answer was:");
        resultLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(resultLabel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JLabel nameLabel = new JLabel(animal.getName());
        nameLabel.setFont(new Font("Arial", Font.BOLD, 28));
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(nameLabel);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        JLabel reasonTitle = new JLabel("Why endangered:");
        reasonTitle.setFont(new Font("Arial", Font.BOLD, 14));
        reasonTitle.setForeground(Color.WHITE);
        reasonTitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        contentPanel.add(reasonTitle);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        JTextArea reasonArea = new JTextArea(animal.getEndangeredReason());
        reasonArea.setWrapStyleWord(true);
        reasonArea.setLineWrap(true);
        reasonArea.setEditable(false);
        reasonArea.setFont(new Font("Arial", Font.PLAIN, 13));
        reasonArea.setForeground(Color.WHITE);
        reasonArea.setBackground(new Color(248, 113, 113));
        reasonArea.setAlignmentX(Component.CENTER_ALIGNMENT);
        reasonArea.setPreferredSize(new Dimension(400, 100));
        contentPanel.add(reasonArea);
        
        contentPanel.add(Box.createRigidArea(new Dimension(0, 25)));
        
        JButton restartBtn = new JButton("Try Another");
        restartBtn.setFont(new Font("Arial", Font.BOLD, 16));
        restartBtn.setPreferredSize(new Dimension(150, 40));
        restartBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        restartBtn.addActionListener(e -> {
            dialog.dispose();
            restartGame();
        });
        contentPanel.add(restartBtn);
        
        dialog.add(contentPanel);
        dialog.setMinimumSize(new Dimension(500, 350));
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }
    
    // ========================================
    // API INTEGRATION - Fetch and display hint
    // ========================================
    private void showHint() {
        // LOGIC: Don't show hint if game is over
        if (gameLogic.isGameOver()) {
            hintLabel.setText("Game is over!");
            return;
        }
        
        // Check if API is configured
        if (!apiService.isConfigured()) {
            hintLabel.setText("⚠️ API key not configured. Check AnimalAPIService.java");
            return;
        }
        
        // Disable button temporarily
        hintButton.setEnabled(false);
        hintLabel.setText("Loading hint...");
        
        // Fetch hint in background thread to avoid UI freeze
        new Thread(() -> {
            String animalName = gameLogic.getTargetAnimal().getName();
            String hint = apiService.getRandomAnimalHint(animalName);
            
            // Update UI on Event Dispatch Thread
            SwingUtilities.invokeLater(() -> {
                hintLabel.setText(hint);
                hintButton.setEnabled(true);
            });
        }).start();
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Keyboard event listener setup
    // ========================================
    private void addKeyboardListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                // LOGIC: Early return if game over
                if (gameLogic.isGameOver()) {
                    return;
                }
                
                int keyCode = e.getKeyCode();
                
                // LOGIC: Key code branching
                if (keyCode == KeyEvent.VK_ENTER) {
                    handleKeyPress("ENTER");
                } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
                    handleKeyPress("DEL");
                } else {
                    char keyChar = e.getKeyChar();
                    // LOGIC: Character validation
                    if (Character.isLetter(keyChar)) {
                        handleKeyPress(String.valueOf(keyChar).toUpperCase());
                    }
                }
            }
        });
        
        setFocusable(true);
        requestFocusInWindow();
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Initialize new game
    // ========================================
    private void startNewGame() {
        // DATABASE: SQL query to get random animal
        Animal randomAnimal = database.getRandomAnimal();
        gameLogic = new GameLogic(randomAnimal);
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Complete game restart sequence
    // ========================================
    private void restartGame() {
        // Step 1: Clear existing UI
        getContentPane().removeAll();
        
        // Step 2: Initialize new game
        startNewGame();
        
        // Step 3: Rebuild interface
        initializeUI();
        
        // Step 4: Refresh display
        revalidate();
        repaint();
        
        // Step 5: Set focus
        requestFocusInWindow();
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Application entry point
    // Main method - starts the application
    // ========================================
    public static void main(String[] args) {
        // IMPERATIVE: Run on Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            new WordleGUI();
        });
    }
}