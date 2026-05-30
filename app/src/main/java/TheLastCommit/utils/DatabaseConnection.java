package TheLastCommit.utils;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import TheLastCommit.models.Hero;
import TheLastCommit.models.HeroFactory;
import TheLastCommit.models.User;

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
                defense INTEGER DEFAULT 5,
                basic_attack_damage INTEGER DEFAULT 10,
                basic_skill_damage INTEGER DEFAULT 25,
                ultimate_damage INTEGER DEFAULT 50,
                upgrade_points INTEGER DEFAULT 0,
                FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE
            );
            """;

        String inventoryTable = """
            CREATE TABLE IF NOT EXISTS inventory(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                user_id INTEGER NOT NULL,
                item_id TEXT NOT NULL,
                item_type TEXT,
                is_equipped INTEGER DEFAULT 0,
                quantity INTEGER DEFAULT 1,
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


            try {
                stmt.execute("ALTER TABLE game_progress ADD COLUMN defense INTEGER DEFAULT 5;");
            } catch (SQLException ignored) {

            }

            System.out.println("Database siap digunakan di: " + resolveDatabasePath().toAbsolutePath());

        } catch (Exception e) {
            throw new RuntimeException("Error Kritis: Gagal membuat skema database", e);
        }
    }

    public static Hero loadHeroForUser(User user) {
        String query = "SELECT * FROM game_progress WHERE user_id = ? AND hero_name IS NOT NULL";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setInt(1, user.getId());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Hero hero = HeroFactory.createHero(0, rs.getString("hero_name"), deriveHeroType(rs.getString("hero_name")));
                    hero.setGold(rs.getInt("gold"));
                    hero.setUpgradePoints(rs.getInt("upgrade_points"));
                    hero.setHighestWave(rs.getInt("current_wave") - 1);
                    hero.setBaseHp(rs.getInt("hp"));
                    hero.setMaxResource(rs.getInt("mana"));
                    hero.setBasicAtk(rs.getInt("basic_attack_damage"));
                    hero.setSkillAtk(rs.getInt("basic_skill_damage"));
                    hero.setUltAtk(rs.getInt("ultimate_damage"));


                    try {
                        int dbDefense = rs.getInt("defense");
                        if (!rs.wasNull()) {
                            hero.setDefense(dbDefense);
                        }
                    } catch (SQLException ignored) {

                    }


                    hero.setCurrentHp(hero.getTotalMaxHp());
                    hero.setCurrentResource(hero.getTotalMaxResource());
                    hero.setProgressId(user.getId());
                    return hero;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Hero createHeroSelection(User user, Hero hero) {
        String sql = "INSERT INTO game_progress (user_id, hero_name, current_wave, gold, hp, mana, defense, basic_attack_damage, basic_skill_damage, ultimate_damage, upgrade_points) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, user.getId());
            pstmt.setString(2, hero.getName());
            pstmt.setInt(3, 1);
            pstmt.setInt(4, hero.getGold());
            pstmt.setInt(5, hero.getBaseHp());
            pstmt.setInt(6, hero.getMaxResource());
            pstmt.setInt(7, hero.getDefense());
            pstmt.setInt(8, hero.getBasicAtk());
            pstmt.setInt(9, hero.getSkillAtk());
            pstmt.setInt(10, hero.getUltAtk());
            pstmt.setInt(11, hero.getUpgradePoints());
            pstmt.executeUpdate();
            hero.setProgressId(user.getId());
            return hero;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String deriveHeroType(String heroName) {
        return heroName.toLowerCase().contains("katagiri") ? "katagiri" : "kyotaka";
    }


    public static void saveHeroProgress(Hero hero) {
        String updateProgress = """
            UPDATE game_progress
            SET gold = ?, upgrade_points = ?, current_wave = ?,
                hp = ?, mana = ?, defense = ?,
                basic_attack_damage = ?, basic_skill_damage = ?, ultimate_damage = ?
            WHERE user_id = ?
            """;

        try (Connection conn = connect();
             PreparedStatement psProg = conn.prepareStatement(updateProgress)) {
            psProg.setInt(1, hero.getGold());
            psProg.setInt(2, hero.getUpgradePoints());
            psProg.setInt(3, hero.getHighestWave() + 1);
            psProg.setInt(4, hero.getBaseHp());
            psProg.setInt(5, hero.getMaxResource());
            psProg.setInt(6, hero.getDefense());
            psProg.setInt(7, hero.getBasicAtk());
            psProg.setInt(8, hero.getSkillAtk());
            psProg.setInt(9, hero.getUltAtk());
            psProg.setInt(10, hero.getProgressId());
            psProg.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static void resetHeroProgress(Hero hero) {

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement("DELETE FROM inventory WHERE user_id = ?")) {
            pstmt.setInt(1, hero.getProgressId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }


        Hero baseHero = HeroFactory.createHero(hero.getProgressId(), hero.getName(), hero.getType());
        baseHero.setHighestWave(0);
        saveHeroProgress(baseHero);
    }


    public static void deleteHeroProgress(User user) {
        try (Connection conn = connect()) {
            PreparedStatement psInv = conn.prepareStatement("DELETE FROM inventory WHERE user_id = ?");
            psInv.setInt(1, user.getId());
            psInv.executeUpdate();

            PreparedStatement psProg = conn.prepareStatement("DELETE FROM game_progress WHERE user_id = ?");
            psProg.setInt(1, user.getId());
            psProg.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
