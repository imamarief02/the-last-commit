package the.last.commit;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import the.last.commit.models.Hero;

class AppTest {
    @Test void testHeroInitialization() {
        Hero hero = new Hero(1, "Katagiri Rafly", "katagiri");
        assertEquals("Katagiri Rafly", hero.getName());
        assertEquals("katagiri", hero.getType());
        assertTrue(hero.getTotalMaxHp() > 0, "Hero max HP should be positive");
    }
}
