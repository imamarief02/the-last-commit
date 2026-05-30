package TheLastCommit.controllers;

import TheLastCommit.models.BossEnemy;
import TheLastCommit.models.Enemy;
import TheLastCommit.models.Hero;
import TheLastCommit.utils.SoundAndAnimationHelper;
import javafx.scene.image.ImageView;
import java.util.List;


public class CombatServiceImpl implements CombatService {

    @Override
    public void executeHeroAction(
        String type,
        Enemy target,
        Hero hero,
        BattleViewBridge view,
        ImageView hView,
        ImageView eView,
        List<Enemy> enemies,
        BattleControllerContext context
    ) {

        view.updateTimerDisplay(-1);


        int cost = type.equals("SKILL") ? hero.getSkillCost() : (type.equals("ULT") ? hero.getUltCost() : 0);
        hero.setCurrentResource(hero.getCurrentResource() - cost);


        context.playAnim(hView, context.getHeroAttack(), context.getHeroIdle());
        SoundAndAnimationHelper.playAttackSound();


        int damage = type.equals("ULT") ? hero.getTotalUltAtk() : (type.equals("SKILL") ? hero.getTotalSkillAtk() : hero.getTotalBasicAtk());
        view.log("\n[HERO] Melancarkan " + type + "!");


        boolean isBlocked = false;
        if (target instanceof BossEnemy) {
            BossEnemy boss = (BossEnemy) target;
            if (boss.isBlockActive()) {
                if (hero.isAntiBlockActive()) {
                    view.log("[ANTI-BLOCK] Ramuan Anti-Block menembus pertahanan BLOCK Imam Voldigoad!");
                    isBlocked = false;
                } else {
                    isBlocked = true;
                }
            }
        }


        if (isBlocked) {
            view.log(target.getName() + " menangkis serangan! (0 Damage)");
        } else {
            target.takeDamage(damage);


            if (target instanceof BossEnemy) {
                context.playAnim(eView, context.getBossHit(), context.getBossIdle());
            } else {
                context.playAnim(eView, context.getEnemyHit(), context.getEnemyIdle());
            }


            SoundAndAnimationHelper.shakeAndTiltRight(eView);
            SoundAndAnimationHelper.playHitSound();

            view.log("-> " + target.getName() + " terkena " + damage + " DMG");


            if (!type.equals("BASIC")) {
                applySpreadDamage(type, target, damage, hero, enemies, view);
            }
        }


        if (type.equals("BASIC")) {
            int regenAmount = (int)(hero.getTotalMaxResource() * 0.08);
            int newRes = Math.min(hero.getCurrentResource() + regenAmount, hero.getTotalMaxResource());
            hero.setCurrentResource(newRes);
            view.log("[REGEN] +" + regenAmount + " " + hero.getResourceName());
        }


        context.startCooldown(type);


        context.finishHeroTurn(hView, eView);
    }


    private void applySpreadDamage(String type, Enemy primary, int damage, Hero hero, List<Enemy> enemies, BattleViewBridge view) {
        int center = enemies.indexOf(primary);
        if (center == -1) return;

        boolean isKatagiri = hero.getType().equalsIgnoreCase("katagiri");
        int spread = type.equals("ULT") ? (isKatagiri ? 8 : 12) : (isKatagiri ? 4 : 5);
        int half = spread / 2;
        int start = Math.max(0, center - half);
        int end = Math.min(enemies.size() - 1, start + spread);

        for (int i = start; i <= end; i++) {
            Enemy e = enemies.get(i);
            if (e != primary && !e.isDead()) {
                e.takeDamage(damage);
                view.log("-> " + e.getName() + " terkena " + damage + " DMG (spread)");
            }
        }
    }
}
