package TheLastCommit.controllers;

import TheLastCommit.models.Enemy;
import TheLastCommit.models.Hero;
import javafx.scene.image.ImageView;
import java.util.List;


public interface CombatService {


    void executeHeroAction(
        String type,
        Enemy target,
        Hero hero,
        BattleViewBridge view,
        ImageView hView,
        ImageView eView,
        List<Enemy> enemies,
        BattleControllerContext context
    );
}
