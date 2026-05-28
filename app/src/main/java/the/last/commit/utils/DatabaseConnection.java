package the.last.commit.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;

import the.last.commit.models.Hero;

public class DatabaseConnection {

    private static final String DB_FILE_NAME = "game.db";

    private static Path resolveDatabasePath() {
        Path currentDir = Paths.get(System.getProperty("user.dir"));
        
        if (Files.exists(currentDir.resolve("app"))) {
            return currentDir.resolve("app").resolve("db").resolve(DB_FILE_NAME);
        }
        return currentDir.resolve("db").resolve(DB_FILE_NAME);
    }

    private static String getJdbcUrl() {
        try {
            Path dbPath = resolveDatabasePath();
            Path parentDir = dbPath.getParent();

            if (parentDir != null) {
                Files.createDirectories(parentDir);
            }

            return "jdbc:sqlite:" + dbPath.toAbsolutePath();
        } catch (Exception e) {
            throw new RuntimeException("Gagal konfigurasi folder database: " + e.getMessage(), e);
        }
    }

    public static Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            
            Connection conn = DriverManager.getConnection(getJdbcUrl());
            try (Statement stmt = conn.createStatement()) {
                stmt.execute("PRAGMA foreign_keys = ON;");
            }
            return conn;
        } catch (Exception e) {
            throw new RuntimeException("Gagal koneksi ke database", e);
        }
    }

    public static void initializeDatabase() {
        String usersTable = """
            CREATE TABLE IF NOT EXISTS users(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                username TEXT UNIQUE NOT NULL,
                password TEXT NOT NULL
            );
            """;

        String progressTable = """
            CREATE TABLE IF NOT EXISTS game_progress(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                hero_name TEXT,
                current_wave INTEGER DEFAULT 1,
                gold INTEGER DEFAULT 0,
                hp INTEGER DEFAULT 100,
                mana INTEGER DEFAULT 50,
                basic_attack_damage INTEGER DEFAULT 10,
                basic_skill_damage INTEGER DEFAULT 25,
                ultimate_damage INTEGER DEFAULT 50,
                upgrade_points INTEGER DEFAULT 0,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
            );
            """;

        String itemsTable = """
            CREATE TABLE IF NOT EXISTS items(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                item_name TEXT NOT NULL,
                item_type TEXT,
                effect_value INTEGER,
                price INTEGER
            );
            """;

        String inventoryTable = """
            CREATE TABLE IF NOT EXISTS inventory(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                item_id INTEGER NOT NULL,
                quantity INTEGER DEFAULT 1,
                UNIQUE(user_id, item_id),
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
                FOREIGN KEY(item_id) REFERENCES items(id) ON DELETE CASCADE
            );
            """;

        String defaultItems = """
            INSERT OR IGNORE INTO items (id, item_name, item_type, effect_value, price)
            VALUES
            (1, 'Potion', 'heal', 50, 100),
            (2, 'Iron Sword', 'weapon', 15, 300),
            (3, 'Shield', 'defense', 10, 250),
            (4, 'Mega Potion', 'heal', 100, 250),
            (5, 'Critical Ring', 'crit', 5, 500);
            """;

        try (
            Connection conn = connect();
            Statement stmt = conn.createStatement()
        ) {
            stmt.execute(usersTable);
            stmt.execute(progressTable);
            stmt.execute(itemsTable);
            stmt.execute(inventoryTable);
            stmt.execute(defaultItems);

            System.out.println("Database siap digunakan di: " + resolveDatabasePath().toAbsolutePath());

        } catch (Exception e) {
            throw new RuntimeException("Error Kritis: Gagal membuat skema database", e);
        }
    }

    public static void saveHeroProgress(Hero hero) {
        String updateProgress = "UPDATE game_progress SET level = ?, exp = ?, gold = ?, upgrade_points = ?, highest_wave = ? WHERE progress_id = ?";
        String updateStats = "UPDATE hero_stats SET base_hp = ?, base_mana_energy = ?, base_defense = ?, base_atk = ?, base_skill = ?, base_ult = ? WHERE progress_id = ?";

        try (Connection conn = connect()) {
            conn.setAutoCommit(false);
            try (PreparedStatement psProg = conn.prepareStatement(updateProgress);
                PreparedStatement psStats = conn.prepareStatement(updateStats)) {
                
                psProg.setInt(1, hero.getLevel());
                psProg.setInt(2, hero.getExp());
                psProg.setInt(3, hero.getGold());
                psProg.setInt(4, hero.getUpgradePoints());
                psProg.setInt(5, hero.getHighestWave());
                psProg.setInt(6, hero.getProgressId());
                psProg.executeUpdate();

                psStats.setInt(1, hero.getMaxHp());
                psStats.setInt(2, hero.getMaxResource());
                psStats.setInt(3, hero.getDefense());
                psStats.setInt(4, hero.getBasicAtk());
                psStats.setInt(5, hero.getSkillAtk());
                psStats.setInt(6, hero.getUltAtk());
                psStats.setInt(7, hero.getProgressId());
                psStats.executeUpdate();

                conn.commit();
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

        public static void resetHeroProgress(Hero hero) {
        try (Connection conn = connect();
            PreparedStatement pstmt = conn.prepareStatement("DELETE FROM inventory WHERE progress_id = ?")) {
            pstmt.setInt(1, hero.getProgressId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        Hero baseHero = new Hero(hero.getProgressId(), hero.getName(), hero.getType());
        saveHeroProgress(baseHero);
    }
}
