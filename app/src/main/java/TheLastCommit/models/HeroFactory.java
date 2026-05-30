package TheLastCommit.models;

public class HeroFactory {

    public static Hero createHero(int progressId, String name, String type) {
        if (type == null) {
            return new KyotakaHero(progressId, name);
        }

        if (type.equalsIgnoreCase("katagiri")) {
            return new KatagiriHero(progressId, name);
        } else {
            return new KyotakaHero(progressId, name);
        }
    }
}
