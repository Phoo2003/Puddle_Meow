package io.github.game;

/** Difficulty settings and background for one level. Edit the table below to tune the game. */
public final class LevelConfig {

    public final String name;
    public final int targetScore;
    public final float fishInterval;      // seconds between fish (smaller = more fish)
    public final float boneInterval;      // seconds between bones (smaller = more bones)
    public final float speedMultiplier;   // 1.0 = normal falling speed
    public final String backgroundFile;

    private LevelConfig(String name, int targetScore, float fishInterval,
                        float boneInterval, float speedMultiplier, String backgroundFile) {
        this.name = name;
        this.targetScore = targetScore;
        this.fishInterval = fishInterval;
        this.boneInterval = boneInterval;
        this.speedMultiplier = speedMultiplier;
        this.backgroundFile = backgroundFile;
    }

    private static final LevelConfig[] LEVELS = {
        //                  name      target fish  bone  speed  background
        new LevelConfig("Easy",   50, 0.7f, 1.2f, 1.25f, "ground.jpg"),
        new LevelConfig("Medium", 75, 0.5f, 1f, 1.50f, "blue night.jpg"),
        new LevelConfig("Hard",   100, 0.3f, 0.8f, 2.00f, "Space.jpg"),
    };

    public static int count() {
        return LEVELS.length;
    }

    public static LevelConfig get(int index) {
        return LEVELS[index];
    }
}
