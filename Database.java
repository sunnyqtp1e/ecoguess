import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * DATABASE LAYER - SQL Database for data storage, retrieval, and management
 * This class manages SQLite database with animals and game statistics
 */

// ========================================
// OBJECT-ORIENTED PROGRAMMING (OOP)
// Class encapsulates all database operations
// ========================================
public class Database {
    
    // OOP: Encapsulation - private data members
    private Connection connection;
    private Random random;
    private static final String DB_URL = "jdbc:sqlite:wordle_game.db";
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Constructor with step-by-step initialization
    // ========================================
    public Database() {
        this.random = new Random();
        
        // Step 1: Load SQLite JDBC driver
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        }
        
        // Step 2: Initialize database connection
        initializeConnection();
        
        // Step 3: Create database tables
        createTables();
        
        // Step 4: Populate with animal data if empty
        initializeAnimalData();
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Step-by-step database connection setup
    // ========================================
    private void initializeConnection() {
        try {
            // IMPERATIVE: Establish connection
            connection = DriverManager.getConnection(DB_URL);
            System.out.println("Database connection established.");
        } catch (SQLException e) {
            System.err.println("Error connecting to database: " + e.getMessage());
        }
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // SQL table creation
    // ========================================
    private void createTables() {
        try {
            Statement stmt = connection.createStatement();
            
            // Step 1: Create animals table
            String createAnimalsTable = 
                "CREATE TABLE IF NOT EXISTS animals (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL UNIQUE, " +
                "fun_fact TEXT NOT NULL, " +
                "endangered_reason TEXT NOT NULL)";
            stmt.execute(createAnimalsTable);
            
            // Step 2: Create game_stats table (tracks wins/losses per animal)
            String createStatsTable = 
                "CREATE TABLE IF NOT EXISTS game_stats (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "animal_name TEXT NOT NULL, " +
                "wins INTEGER DEFAULT 0, " +
                "losses INTEGER DEFAULT 0, " +
                "UNIQUE(animal_name))";
            stmt.execute(createStatsTable);
            
            // Step 3: Create global_stats table (overall wins/losses)
            String createGlobalStatsTable = 
                "CREATE TABLE IF NOT EXISTS global_stats (" +
                "id INTEGER PRIMARY KEY CHECK (id = 1), " +
                "total_wins INTEGER DEFAULT 0, " +
                "total_losses INTEGER DEFAULT 0)";
            stmt.execute(createGlobalStatsTable);
            
            // Step 4: Initialize global stats if not exists
            String initGlobalStats = 
                "INSERT OR IGNORE INTO global_stats (id, total_wins, total_losses) VALUES (1, 0, 0)";
            stmt.execute(initGlobalStats);
            
            stmt.close();
            System.out.println("Database tables initialized.");
        } catch (SQLException e) {
            System.err.println("Error creating tables: " + e.getMessage());
        }
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Sequential data initialization
    // ========================================
    private void initializeAnimalData() {
        try {
            // LOGIC: Check if animals table is empty
            Statement checkStmt = connection.createStatement();
            ResultSet rs = checkStmt.executeQuery("SELECT COUNT(*) FROM animals");
            int count = rs.getInt(1);
            rs.close();
            checkStmt.close();
            
            // LOGIC: Only insert if table is empty
            if (count == 0) {
                PreparedStatement pstmt = connection.prepareStatement(
                    "INSERT INTO animals (name, fun_fact, endangered_reason) VALUES (?, ?, ?)"
                );
                
                // ===== LAND ANIMALS (5) =====
                
                // Step 1: Add Tiger
                pstmt.setString(1, "TIGER");
                pstmt.setString(2, "Tigers are the largest cat species and can leap up to 30 feet in a single bound!");
                pstmt.setString(3, "Tigers are endangered due to habitat loss, poaching for their fur and body parts, and declining prey populations.");
                pstmt.executeUpdate();
                
                // Step 2: Add Panda
                pstmt.setString(1, "PANDA");
                pstmt.setString(2, "Giant pandas spend 12-16 hours a day eating bamboo and can eat up to 40 pounds of it daily!");
                pstmt.setString(3, "Pandas are endangered due to habitat fragmentation, low birth rates, and their highly specialized bamboo diet.");
                pstmt.executeUpdate();
                
                // Step 3: Add Rhino
                pstmt.setString(1, "RHINO");
                pstmt.setString(2, "Rhinos have been around for 50 million years and can run up to 30 miles per hour despite their massive size!");
                pstmt.setString(3, "Rhinos are critically endangered due to intensive poaching for their horns and severe habitat loss.");
                pstmt.executeUpdate();
                
                // Step 4: Add Lemur
                pstmt.setString(1, "LEMUR");
                pstmt.setString(2, "Lemurs are found only in Madagascar and can communicate using over 100 different vocalizations!");
                pstmt.setString(3, "Lemurs are endangered due to deforestation in Madagascar, illegal pet trade, and hunting for bushmeat.");
                pstmt.executeUpdate();
                
                // Step 5: Add Okapi
                pstmt.setString(1, "OKAPI");
                pstmt.setString(2, "Okapis have zebra-like stripes but are actually related to giraffes and have a 14-inch long tongue!");
                pstmt.setString(3, "Okapis are endangered due to habitat loss from logging, human settlement, and poaching in the Congo rainforest.");
                pstmt.executeUpdate();
                
                // ===== AQUATIC ANIMALS (5) =====
                
                // Step 6: Add Whale
                pstmt.setString(1, "WHALE");
                pstmt.setString(2, "Blue whales are the largest animals ever known to have lived on Earth, with hearts the size of a small car!");
                pstmt.setString(3, "Many whale species are endangered due to commercial whaling, ocean pollution, ship strikes, and climate change.");
                pstmt.executeUpdate();
                
                // Step 7: Add Otter
                pstmt.setString(1, "OTTER");
                pstmt.setString(2, "Sea otters use rocks as tools to crack open shellfish and hold hands while sleeping to avoid drifting apart!");
                pstmt.setString(3, "Sea otters are endangered due to oil spills, pollution, predation, and historic fur trade exploitation.");
                pstmt.executeUpdate();
                
                // Step 8: Add Vaquita (small porpoise)
                pstmt.setString(1, "SHARK");
                pstmt.setString(2, "Many shark species have been around for 450 million years and can detect a drop of blood in an Olympic pool!");
                pstmt.setString(3, "Several shark species are endangered due to overfishing, finning, bycatch, and habitat degradation.");
                pstmt.executeUpdate();
                
                // Step 9: Add Coral (as animal colony)
                pstmt.setString(1, "CORAL");
                pstmt.setString(2, "Corals are living animals that form massive reef structures and provide homes for 25% of all marine species!");
                pstmt.setString(3, "Corals are endangered due to ocean warming, acidification, pollution, and destructive fishing practices.");
                pstmt.executeUpdate();
                
                // Step 10: Add Seal
                pstmt.setString(1, "SEALS");
                pstmt.setString(2, "Monk seals can dive over 1,500 feet deep and hold their breath for up to 20 minutes while hunting!");
                pstmt.setString(3, "Hawaiian monk seals are endangered due to habitat loss, marine debris, disease, and limited breeding sites.");
                pstmt.executeUpdate();
                
                pstmt.close();
                
                // Initialize game_stats for each animal
                Statement initStats = connection.createStatement();
                initStats.execute(
                    "INSERT INTO game_stats (animal_name, wins, losses) " +
                    "SELECT name, 0, 0 FROM animals"
                );
                initStats.close();
                
                System.out.println("Animal data initialized in database.");
            }
        } catch (SQLException e) {
            System.err.println("Error initializing animal data: " + e.getMessage());
        }
    }
    
    // ========================================
    // DATABASE OPERATION: Data Retrieval
    // FUNCTIONAL PROGRAMMING - Returns data without side effects
    // ========================================
    public Animal getRandomAnimal() {
        try {
            Statement stmt = connection.createStatement();
            
            // SQL query to get random animal
            ResultSet rs = stmt.executeQuery(
                "SELECT name, fun_fact, endangered_reason FROM animals ORDER BY RANDOM() LIMIT 1"
            );
            
            // LOGIC: Check if result exists
            if (rs.next()) {
                String name = rs.getString("name");
                String funFact = rs.getString("fun_fact");
                String endangeredReason = rs.getString("endangered_reason");
                
                rs.close();
                stmt.close();
                
                return new Animal(name, funFact, endangeredReason);
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error getting random animal: " + e.getMessage());
        }
        
        return null;
    }
    
    // ========================================
    // DATABASE OPERATION: Data Retrieval
    // FUNCTIONAL PROGRAMMING - Pure query function
    // ========================================
    public Animal getAnimalByName(String name) {
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                "SELECT name, fun_fact, endangered_reason FROM animals WHERE name = ?"
            );
            pstmt.setString(1, name.toUpperCase());
            
            ResultSet rs = pstmt.executeQuery();
            
            // LOGIC: Check if animal found
            if (rs.next()) {
                Animal animal = new Animal(
                    rs.getString("name"),
                    rs.getString("fun_fact"),
                    rs.getString("endangered_reason")
                );
                
                rs.close();
                pstmt.close();
                return animal;
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error getting animal by name: " + e.getMessage());
        }
        
        return null;
    }
    
    // ========================================
    // DATABASE OPERATION: Score Retrieval
    // FUNCTIONAL PROGRAMMING - Query without modification
    // ========================================
    public int[] getGlobalScores() {
        int[] scores = new int[2]; // [wins, losses]
        
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT total_wins, total_losses FROM global_stats WHERE id = 1"
            );
            
            if (rs.next()) {
                scores[0] = rs.getInt("total_wins");
                scores[1] = rs.getInt("total_losses");
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error getting global scores: " + e.getMessage());
        }
        
        return scores;
    }
    
    // ========================================
    // DATABASE OPERATION: Score Retrieval per Animal
    // FUNCTIONAL PROGRAMMING
    // ========================================
    public int[] getAnimalScores(String animalName) {
        int[] scores = new int[2]; // [wins, losses]
        
        try {
            PreparedStatement pstmt = connection.prepareStatement(
                "SELECT wins, losses FROM game_stats WHERE animal_name = ?"
            );
            pstmt.setString(1, animalName.toUpperCase());
            
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                scores[0] = rs.getInt("wins");
                scores[1] = rs.getInt("losses");
            }
            
            rs.close();
            pstmt.close();
        } catch (SQLException e) {
            System.err.println("Error getting animal scores: " + e.getMessage());
        }
        
        return scores;
    }
    
    // ========================================
    // DATABASE OPERATION: Score Update
    // IMPERATIVE PROGRAMMING - Data modification
    // ========================================
    public void saveWin(String animalName) {
        try {
            connection.setAutoCommit(false); // Start transaction
            
            // Step 1: Update global stats
            PreparedStatement pstmt1 = connection.prepareStatement(
                "UPDATE global_stats SET total_wins = total_wins + 1 WHERE id = 1"
            );
            pstmt1.executeUpdate();
            pstmt1.close();
            
            // Step 2: Update animal-specific stats
            PreparedStatement pstmt2 = connection.prepareStatement(
                "UPDATE game_stats SET wins = wins + 1 WHERE animal_name = ?"
            );
            pstmt2.setString(1, animalName.toUpperCase());
            pstmt2.executeUpdate();
            pstmt2.close();
            
            connection.commit(); // Commit transaction
            connection.setAutoCommit(true);
            
            System.out.println("Win recorded for " + animalName);
        } catch (SQLException e) {
            System.err.println("Error saving win: " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back: " + ex.getMessage());
            }
        }
    }
    
    // ========================================
    // DATABASE OPERATION: Score Update
    // IMPERATIVE PROGRAMMING - Data modification
    // ========================================
    public void saveLoss(String animalName) {
        try {
            connection.setAutoCommit(false); // Start transaction
            
            // Step 1: Update global stats
            PreparedStatement pstmt1 = connection.prepareStatement(
                "UPDATE global_stats SET total_losses = total_losses + 1 WHERE id = 1"
            );
            pstmt1.executeUpdate();
            pstmt1.close();
            
            // Step 2: Update animal-specific stats
            PreparedStatement pstmt2 = connection.prepareStatement(
                "UPDATE game_stats SET losses = losses + 1 WHERE animal_name = ?"
            );
            pstmt2.setString(1, animalName.toUpperCase());
            pstmt2.executeUpdate();
            pstmt2.close();
            
            connection.commit(); // Commit transaction
            connection.setAutoCommit(true);
            
            System.out.println("Loss recorded for " + animalName);
        } catch (SQLException e) {
            System.err.println("Error saving loss: " + e.getMessage());
            try {
                connection.rollback();
            } catch (SQLException ex) {
                System.err.println("Error rolling back: " + ex.getMessage());
            }
        }
    }
    
    // ========================================
    // DATABASE OPERATION: Data Management
    // IMPERATIVE PROGRAMMING - Reset operation
    // ========================================
    public void resetAllScores() {
        try {
            Statement stmt = connection.createStatement();
            
            // Step 1: Reset global stats
            stmt.execute("UPDATE global_stats SET total_wins = 0, total_losses = 0 WHERE id = 1");
            
            // Step 2: Reset animal stats
            stmt.execute("UPDATE game_stats SET wins = 0, losses = 0");
            
            stmt.close();
            System.out.println("All scores reset.");
        } catch (SQLException e) {
            System.err.println("Error resetting scores: " + e.getMessage());
        }
    }
    
    // ========================================
    // FUNCTIONAL PROGRAMMING
    // Pure function - retrieval only
    // ========================================
    public List<Animal> getAllAnimals() {
        List<Animal> animals = new ArrayList<>();
        
        try {
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(
                "SELECT name, fun_fact, endangered_reason FROM animals"
            );
            
            while (rs.next()) {
                animals.add(new Animal(
                    rs.getString("name"),
                    rs.getString("fun_fact"),
                    rs.getString("endangered_reason")
                ));
            }
            
            rs.close();
            stmt.close();
        } catch (SQLException e) {
            System.err.println("Error getting all animals: " + e.getMessage());
        }
        
        return animals;
    }
    
    // ========================================
    // IMPERATIVE PROGRAMMING
    // Resource cleanup
    // ========================================
    public void close() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
                System.out.println("Database connection closed.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database: " + e.getMessage());
        }
    }
}

// ========================================
// OBJECT-ORIENTED PROGRAMMING (OOP)
// Data entity class with encapsulation
// ========================================
class Animal {
    // OOP: Private fields (encapsulation)
    private String name;
    private String funFact;
    private String endangeredReason;
    
    // OOP: Constructor
    public Animal(String name, String funFact, String endangeredReason) {
        this.name = name;
        this.funFact = funFact;
        this.endangeredReason = endangeredReason;
    }
    
    // OOP: Getter methods (accessors)
    // FUNCTIONAL: Pure functions
    public String getName() {
        return name;
    }
    
    public String getFunFact() {
        return funFact;
    }
    
    public String getEndangeredReason() {
        return endangeredReason;
    }
    
    @Override
    public String toString() {
        return "Animal{name='" + name + "'}";
    }
}