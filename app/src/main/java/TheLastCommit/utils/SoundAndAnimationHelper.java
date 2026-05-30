package TheLastCommit.utils;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import javafx.util.Duration;

public class SoundAndAnimationHelper {

    private static AudioClip wrongBuzzer;
    private static AudioClip popSound;
    private static AudioClip attackSound;
    private static AudioClip fireSpellSound;
    private static AudioClip heavySmashSound;
    private static AudioClip victorySound;
    private static AudioClip gameOverSound;
    private static AudioClip hitSound;

    static {
        try {
            var wrongResource = SoundAndAnimationHelper.class.getResource("/sounds/572938__bloodpixelhero__error-3.wav");
            if (wrongResource != null) {
                wrongBuzzer = new AudioClip(wrongResource.toExternalForm());
            } else {
                System.err.println("[SoundHelper] Warning: error-3 sound not found in resources!");
            }

            var popResource = SoundAndAnimationHelper.class.getResource("/sounds/463395__vilkas_sound__vs-pop_5.mp3");
            if (popResource != null) {
                popSound = new AudioClip(popResource.toExternalForm());
            } else {
                System.err.println("[SoundHelper] Warning: vs-pop sound not found in resources!");
            }

            var attackResource = SoundAndAnimationHelper.class.getResource("/sounds/522701__julianmateo__weapon-sword-metal-sword-swing-fast-impact.wav");
            if (attackResource != null) {
                attackSound = new AudioClip(attackResource.toExternalForm());
            } else {
                System.err.println("[SoundHelper] Warning: weapon-swing sound not found in resources!");
            }

            var fireResource = SoundAndAnimationHelper.class.getResource("/sounds/396500__alonsotm__firespell3.wav");
            if (fireResource != null) {
                fireSpellSound = new AudioClip(fireResource.toExternalForm());
            } else {
                System.err.println("[SoundHelper] Warning: fire-spell sound not found in resources!");
            }

            var heavyResource = SoundAndAnimationHelper.class.getResource("/sounds/135453__joelaudio__heavy_smash_001.wav");
            if (heavyResource != null) {
                heavySmashSound = new AudioClip(heavyResource.toExternalForm());
            } else {
                System.err.println("[SoundHelper] Warning: heavy-smash sound not found in resources!");
            }

            var victoryResource = SoundAndAnimationHelper.class.getResource("/sounds/Victory_Sound_Effect.mp3");
            if (victoryResource != null) {
                victorySound = new AudioClip(victoryResource.toExternalForm());
            } else {
                System.err.println("[SoundHelper] Warning: victory sound not found in resources!");
            }

            var gameOverResource = SoundAndAnimationHelper.class.getResource("/sounds/Game_Over_sound_effect.mp3");
            if (gameOverResource != null) {
                gameOverSound = new AudioClip(gameOverResource.toExternalForm());
            } else {
                System.err.println("[SoundHelper] Warning: game over sound not found in resources!");
            }

            var hitResource = SoundAndAnimationHelper.class.getResource("/sounds/hit.mp3");
            if (hitResource != null) {
                hitSound = new AudioClip(hitResource.toExternalForm());
            } else {
                System.err.println("[SoundHelper] Warning: hit sound not found in resources!");
            }
        } catch (Exception e) {
            System.err.println("[SoundHelper] Failed to load sound files: " + e.getMessage());
        }
    }


    public static void playErrorSound() {
        if (wrongBuzzer != null) {
            try {
                wrongBuzzer.play();
            } catch (Exception e) {
                System.err.println("[SoundHelper] Play buzzer failed: " + e.getMessage());
            }
        }
    }


    public static void playClickSound() {
        if (popSound != null) {
            try {
                popSound.play();
            } catch (Exception e) {
                System.err.println("[SoundHelper] Play pop failed: " + e.getMessage());
            }
        }
    }


    public static void playAttackSound() {
        if (attackSound != null) {
            try {
                attackSound.play();
            } catch (Exception e) {
                System.err.println("[SoundHelper] Play attack sound failed: " + e.getMessage());
            }
        }
    }


    public static void playFireSpellSound() {
        if (fireSpellSound != null) {
            try {
                fireSpellSound.play();
            } catch (Exception e) {
                System.err.println("[SoundHelper] Play fire spell sound failed: " + e.getMessage());
            }
        }
    }


    public static void playHeavySmashSound() {
        if (heavySmashSound != null) {
            try {
                heavySmashSound.play();
            } catch (Exception e) {
                System.err.println("[SoundHelper] Play heavy smash sound failed: " + e.getMessage());
            }
        }
    }


    public static void playVictorySound() {
        if (victorySound != null) {
            try {
                victorySound.play();
            } catch (Exception e) {
                System.err.println("[SoundHelper] Play victory sound failed: " + e.getMessage());
            }
        }
    }


    public static void playGameOverSound() {
        if (gameOverSound != null) {
            try {
                gameOverSound.play();
            } catch (Exception e) {
                System.err.println("[SoundHelper] Play game over sound failed: " + e.getMessage());
            }
        }
    }


    public static void playHitSound() {
        if (hitSound != null) {
            try {
                hitSound.play();
            } catch (Exception e) {
                System.err.println("[SoundHelper] Play hit sound failed: " + e.getMessage());
            }
        }
    }


    public static void addClickSoundToAllButtons(Parent parent) {
        if (parent == null) return;

        for (Node node : parent.getChildrenUnmodifiable()) {
            if (node instanceof Button) {
                addClickSound((Button) node);
            } else if (node instanceof Parent) {
                addClickSoundToAllButtons((Parent) node);
            }
        }
    }


    public static void addClickSound(Button button) {
        if (button == null) return;


        button.addEventHandler(MouseEvent.MOUSE_PRESSED, e -> playClickSound());
    }


    public static void shakeNode(Node node) {
        if (node == null) return;

        Timeline timeline = new Timeline();
        double startX = node.getTranslateX();

        timeline.getKeyFrames().addAll(
            new KeyFrame(Duration.millis(0), new KeyValue(node.translateXProperty(), startX)),
            new KeyFrame(Duration.millis(50), new KeyValue(node.translateXProperty(), startX - 10)),
            new KeyFrame(Duration.millis(100), new KeyValue(node.translateXProperty(), startX + 10)),
            new KeyFrame(Duration.millis(150), new KeyValue(node.translateXProperty(), startX - 8)),
            new KeyFrame(Duration.millis(200), new KeyValue(node.translateXProperty(), startX + 8)),
            new KeyFrame(Duration.millis(250), new KeyValue(node.translateXProperty(), startX - 5)),
            new KeyFrame(Duration.millis(300), new KeyValue(node.translateXProperty(), startX + 5)),
            new KeyFrame(Duration.millis(350), new KeyValue(node.translateXProperty(), startX - 2)),
            new KeyFrame(Duration.millis(400), new KeyValue(node.translateXProperty(), startX + 2)),
            new KeyFrame(Duration.millis(450), new KeyValue(node.translateXProperty(), startX))
        );
        timeline.play();
    }


    public static void shakeAndTiltLeft(Node node) {
        if (node == null) return;

        Timeline timeline = new Timeline();
        double startX = node.getTranslateX();
        double startRot = node.getRotate();

        timeline.getKeyFrames().addAll(
            new KeyFrame(Duration.millis(0), new KeyValue(node.translateXProperty(), startX), new KeyValue(node.rotateProperty(), startRot)),
            new KeyFrame(Duration.millis(40), new KeyValue(node.translateXProperty(), startX - 8), new KeyValue(node.rotateProperty(), -10.0)),
            new KeyFrame(Duration.millis(80), new KeyValue(node.translateXProperty(), startX - 4), new KeyValue(node.rotateProperty(), -8.0)),
            new KeyFrame(Duration.millis(120), new KeyValue(node.translateXProperty(), startX - 10), new KeyValue(node.rotateProperty(), -11.0)),
            new KeyFrame(Duration.millis(160), new KeyValue(node.translateXProperty(), startX - 6), new KeyValue(node.rotateProperty(), -9.0)),
            new KeyFrame(Duration.millis(200), new KeyValue(node.translateXProperty(), startX - 9), new KeyValue(node.rotateProperty(), -10.0)),
            new KeyFrame(Duration.millis(240), new KeyValue(node.translateXProperty(), startX - 5), new KeyValue(node.rotateProperty(), -8.0)),
            new KeyFrame(Duration.millis(280), new KeyValue(node.translateXProperty(), startX - 7), new KeyValue(node.rotateProperty(), -9.0)),
            new KeyFrame(Duration.millis(320), new KeyValue(node.translateXProperty(), startX), new KeyValue(node.rotateProperty(), startRot))
        );
        timeline.play();
    }


    public static void shakeAndTiltRight(Node node) {
        if (node == null) return;

        Timeline timeline = new Timeline();
        double startX = node.getTranslateX();
        double startRot = node.getRotate();

        timeline.getKeyFrames().addAll(
            new KeyFrame(Duration.millis(0), new KeyValue(node.translateXProperty(), startX), new KeyValue(node.rotateProperty(), startRot)),
            new KeyFrame(Duration.millis(40), new KeyValue(node.translateXProperty(), startX + 8), new KeyValue(node.rotateProperty(), 10.0)),
            new KeyFrame(Duration.millis(80), new KeyValue(node.translateXProperty(), startX + 4), new KeyValue(node.rotateProperty(), 8.0)),
            new KeyFrame(Duration.millis(120), new KeyValue(node.translateXProperty(), startX + 10), new KeyValue(node.rotateProperty(), 11.0)),
            new KeyFrame(Duration.millis(160), new KeyValue(node.translateXProperty(), startX + 6), new KeyValue(node.rotateProperty(), 9.0)),
            new KeyFrame(Duration.millis(200), new KeyValue(node.translateXProperty(), startX + 9), new KeyValue(node.rotateProperty(), 10.0)),
            new KeyFrame(Duration.millis(240), new KeyValue(node.translateXProperty(), startX + 5), new KeyValue(node.rotateProperty(), 8.0)),
            new KeyFrame(Duration.millis(280), new KeyValue(node.translateXProperty(), startX + 7), new KeyValue(node.rotateProperty(), 9.0)),
            new KeyFrame(Duration.millis(320), new KeyValue(node.translateXProperty(), startX), new KeyValue(node.rotateProperty(), startRot))
        );
        timeline.play();
    }
}
