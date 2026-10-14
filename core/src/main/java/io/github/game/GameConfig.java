package io.github.game;

/** All tunable numbers of the game in one place. */
public final class GameConfig {

    private GameConfig() {}

    // World size
    public static final float WORLD_WIDTH = 800f;
    public static final float WORLD_HEIGHT = 480f;

    // Mystery box interval (fish and bone intervals are per level, see LevelConfig)
    public static final float MYSTERY_BOX_SPAWN_INTERVAL = 8f;

    // Sprite widths used when choosing a random spawn x
    public static final float FISH_WIDTH = 40f;
    public static final float BONE_WIDTH = 30f;
    public static final float MYSTERY_BOX_WIDTH = 35f;

    // Rules
    public static final float LEVEL_PAUSE = 2.5f;   // pause between levels (seconds)
    public static final float SCORE_BOOST_DURATION = 10f;

    // Cat hitbox ratios (relative to the sprite size)
    public static final float SOLID_HITBOX_W = 0.40f;
    public static final float SOLID_HITBOX_H = 0.45f;
    public static final float LIQUID_HITBOX_W = 0.95f;
    public static final float LIQUID_HITBOX_H = 0.75f;

    // HUD
    public static final float NOTICE_DURATION = 2f;
    public static final float STATUS_LINE_HEIGHT = 25f;
    public static final float HEART_SIZE = 32f;
    public static final float HEART_GAP = 6f;
    public static final float MUSIC_VOLUME = 0.5f;
}
