package TheLastCommit.controllers;

import TheLastCommit.models.Enemy;
import javafx.scene.image.ImageView;


public interface BattleViewBridge {


    void log(String message);


    void updateUI();


    void setSelectedTarget(Enemy target);


    Enemy getSelectedTarget();


    void toggleHeroControls(boolean enable);


    void updateTimerDisplay(int seconds);


    void updateCooldownDisplay(String type, int duration);


    ImageView getHeroImageView();


    ImageView getEnemyHordeView();
}
