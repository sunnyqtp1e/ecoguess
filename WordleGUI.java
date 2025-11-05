import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.Map;
import javax.swing.*;

public class WordleGUI extends JFrame {
    
    private Database database;
    private GameLogic gameLogic;
    
    private JPanel gameBoard;
    private JPanel keyboardPanel;
    private JLabel titleLabel;
    private JLabel subtitleLabel;
    private JLabel errorLabel;
    private JLabel scoreLabel;
    private JButton resetScoreButton;
    private JButton[][] tiles;
    private Map<Character, JButton> keyButtons;
    
    private static final int TILE_SIZE = 60;
    private static final Color COLOR_CORRECT = new Color(106, 170, 100);
    private static final Color COLOR_PRESENT = new Color(201, 180, 88);
    private static final Color COLOR_ABSENT = new Color(120, 124, 126);
    private static final Color COLOR_UNUSED = new Color(211, 214, 218);
    private static final Color COLOR_FILLED = new Color(135, 138, 140);
    private static final Color COLOR_EMPTY = Color.WHITE;
    
    public WordleGUI() {
        this.database = new Database();
        startNewGame();
        setTitle("Endangered Animals Wordle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        initializeUI();
        addKeyboardListener();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                database.close();
            }
        });
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
    
    private void initializeUI() {
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(new Color(102, 126, 234));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setOpaque(false);
        headerPanel.setMaximumSize(new Dimension(500, 50));
        
        titleLabel = new JLabel("Endangered Wordle");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(Color.WHITE);
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        JPanel scorePanel = new JPanel();
        scorePanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        scorePanel.setOpaque(false);
        
        scoreLabel = new JLabel();
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 14));
        scoreLabel.setForeground(Color.WHITE);
        updateScoreDisplay();
        scorePanel.add(scoreLabel);
        
        resetScoreButton = new JButton("Reset");
        resetScoreButton.setFont(new Font("Arial", Font.PLAIN, 12));
        resetScoreButton.setPreferredSize(new Dimension(60, 25));
        resetScoreButton.setFocusable(false);
        resetScoreButton.setToolTipText("Reset Scores");
        resetScoreButton.addActionListener(e -> resetScores());
        scorePanel.add(resetScoreButton);
        
        headerPanel.add(scorePanel, BorderLayout.EAST);
        mainPanel.add(headerPanel);
        
        subtitleLabel = new JLabel("Guess the endangered animal");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(230, 230, 230));
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(subtitleLabel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        createGameBoard();
        mainPanel.add(gameBoard);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        errorLabel = new JLabel(" ");
        errorLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        errorLabel.setForeground(new Color(248, 113, 113));
        errorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(errorLabel);
        
        mainPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        
        createKeyboard();
        mainPanel.add(keyboardPanel);
        
        add(mainPanel);
    }
    
    private void createGameBoard() {
        gameBoard = new JPanel();
        gameBoard.setLayout(new GridLayout(gameLogic.getMaxAttempts(), 
                                           gameLogic.getTargetWord().length(), 5, 5));
        gameBoard.setOpaque(false);
        
        int rows = gameLogic.getMaxAttempts();
        int cols = gameLogic.getTargetWord().length();
        tiles = new JButton[rows][cols];
        
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
    
    private void createKeyboard() {
        keyboardPanel = new JPanel();
        keyboardPanel.setLayout(new BoxLayout(keyboardPanel, BoxLayout.Y_AXIS));
        keyboardPanel.setOpaque(false);
        
        keyButtons = new java.util.HashMap<>();
        
        String[] rows = {"QWERTYUIOP", "ASDFGHJKL", "ZXCVBNM"};
        
        for (int rowIndex = 0; rowIndex < rows.length; rowIndex++) {
            JPanel row = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            row.setOpaque(false);
            
            if (rowIndex == 2) {
                JButton enterBtn = createKeyButton("ENTER", true);
                row.add(enterBtn);
            }
            
            String letters = rows[rowIndex];
            for (int i = 0; i < letters.length(); i++) {
                char letter = letters.charAt(i);
                JButton keyBtn = createKeyButton(String.valueOf(letter), false);
                keyButtons.put(letter, keyBtn);
                row.add(keyBtn);
            }
            
            if (rowIndex == 2) {
                JButton delBtn = createKeyButton("DEL", true);
                row.add(delBtn);
            }
            
            keyboardPanel.add(row);
        }
    }
    
    private JButton createKeyButton(String text, boolean isWide) {
        JButton button = new JButton(text);
        
        if (isWide) {
            button.setPreferredSize(new Dimension(70, 45));
        } else {
            button.setPreferredSize(new Dimension(40, 45));
        }
        
        button.setFont(new Font("Arial", Font.BOLD, 12));
        button.setFocusable(false);
        button.setBackground(COLOR_UNUSED);
        button.setBorder(BorderFactory.createLineBorder(Color.GRAY, 1));
        button.addActionListener(e -> handleKeyPress(text));
        
        return button;
    }
    
    private void handleKeyPress(String key) {
        errorLabel.setText(" ");
        
        if (gameLogic.isGameOver()) {
            return;
        }
        
        if (key.equals("ENTER")) {
            submitGuess();
        } else if (key.equals("DEL")) {
            gameLogic.deleteLetter();
            updateBoard();
        } else if (key.length() == 1) {
            if (gameLogic.isValidLetter(key.charAt(0))) {
                gameLogic.addLetter(key.charAt(0));
                updateBoard();
            }
        }
    }
    
    private void submitGuess() {
        if (!gameLogic.canSubmitGuess()) {
            showError("Not enough letters!");
            return;
        }
        
        GuessResult result = gameLogic.submitGuess();
        
        if (result != null) {
            updateBoard();
            updateKeyboard();
            
            if (gameLogic.isGameOver()) {
                String animalName = gameLogic.getTargetAnimal().getName();
                
                if (gameLogic.isWon()) {
                    database.saveWin(animalName);
                } else {
                    database.saveLoss(animalName);
                }
                
                updateScoreDisplay();
                
                Timer timer = new Timer(500, e -> {
                    showEndGameDialog();
                });
                timer.setRepeats(false);
                timer.start();
            }
        }
    }
    
    private void updateBoard() {
        int currentRow = gameLogic.getCurrentAttempt();
        String currentGuess = gameLogic.getCurrentGuess();
        
        if (currentRow < gameLogic.getMaxAttempts()) {
            for (int col = 0; col < gameLogic.getTargetWord().length(); col++) {
                JButton tile = tiles[currentRow][col];
                
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
        
        List<GuessResult> guesses = gameLogic.getGuesses();
        for (int row = 0; row < guesses.size(); row++) {
            GuessResult guess = guesses.get(row);
            String word = guess.getWord();
            
            for (int col = 0; col < word.length(); col++) {
                JButton tile = tiles[row][col];
                tile.setText(String.valueOf(word.charAt(col)));
                
                GameLogic.LetterStatus status = guess.getLetterStatus(col);
                Color color = getColorForStatus(status);
                tile.setBackground(color);
                tile.setForeground(Color.WHITE);
                tile.setBorder(BorderFactory.createLineBorder(color, 2));
            }
        }
    }
    
    private void updateKeyboard() {
        Map<Character, GameLogic.LetterStatus> keyboardState = gameLogic.getKeyboardState();
        
        for (Map.Entry<Character, GameLogic.LetterStatus> entry : keyboardState.entrySet()) {
            char letter = entry.getKey();
            GameLogic.LetterStatus status = entry.getValue();
            
            JButton keyButton = keyButtons.get(letter);
            if (keyButton != null) {
                Color color = getColorForStatus(status);
                keyButton.setBackground(color);
                
                if (status != GameLogic.LetterStatus.UNUSED) {
                    keyButton.setForeground(Color.WHITE);
                }
            }
        }
    }
    
    private Color getColorForStatus(GameLogic.LetterStatus status) {
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
    
    private void showError(String message) {
        errorLabel.setText(message);
        
        Timer timer = new Timer(2000, e -> errorLabel.setText(" "));
        timer.setRepeats(false);
        timer.start();
    }
    
    private void updateScoreDisplay() {
        int[] scores = database.getGlobalScores();
        int wins = scores[0];
        int losses = scores[1];
        
        scoreLabel.setText("W:" + wins + " L:" + losses);
    }
    
    private void resetScores() {
        int result = JOptionPane.showConfirmDialog(
            this,
            "Reset all scores to 0?",
            "Reset Scores",
            JOptionPane.YES_NO_OPTION
        );
        
        if (result == JOptionPane.YES_OPTION) {
            database.resetAllScores();
            updateScoreDisplay();
        }
    }
    
    private void showEndGameDialog() {
        Animal animal = gameLogic.getTargetAnimal();
        
        if (gameLogic.isWon()) {
            showWinDialog(animal);
        } else {
            showLoseDialog(animal);
        }
    }
    
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
    
    private void addKeyboardListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (gameLogic.isGameOver()) {
                    return;
                }
                
                int keyCode = e.getKeyCode();
                
                if (keyCode == KeyEvent.VK_ENTER) {
                    handleKeyPress("ENTER");
                } else if (keyCode == KeyEvent.VK_BACK_SPACE) {
                    handleKeyPress("DEL");
                } else {
                    char keyChar = e.getKeyChar();
                    if (Character.isLetter(keyChar)) {
                        handleKeyPress(String.valueOf(keyChar).toUpperCase());
                    }
                }
            }
        });
        
        setFocusable(true);
        requestFocusInWindow();
    }
    
    private void startNewGame() {
        Animal randomAnimal = database.getRandomAnimal();
        gameLogic = new GameLogic(randomAnimal);
    }
    
    private void restartGame() {
        getContentPane().removeAll();
        startNewGame();
        initializeUI();
        revalidate();
        repaint();
        requestFocusInWindow();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WordleGUI();
        });
    }
}