package TheLastCommit.controllers;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;


public interface BattleControllerContext {


    void startCooldown(String type);


    void finishHeroTurn(ImageView heroView, ImageView enemyView);


    void playAnim(ImageView imageView, Image actionSprite, Image idleSprite);


    Image getHeroAttack();
    Image getHeroIdle();
    Image getBossHit();
    Image getBossIdle();
    Image getEnemyHit();
    Image getEnemyIdle();
}
