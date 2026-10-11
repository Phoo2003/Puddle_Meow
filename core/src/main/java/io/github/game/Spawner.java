package io.github.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/** Owns the spawn timers for one level and creates falling objects at the top of the screen. */
public class Spawner {

    private final GameResources res;
    private final LevelConfig level;

    private float fishTimer = 0;
    private float boneTimer = 0;
    private float boxTimer = 0;

    public Spawner(GameResources res, LevelConfig level) {
        this.res = res;
        this.level = level;
    }

    public void update(float delta,
                       Array<Fish> fishes,
                       Array<FishBone> bones,
                       Array<MysteryBox> boxes) {

        fishTimer += delta;
        if (fishTimer >= level.fishInterval) {
            fishes.add(new Fish(res.fish, randomX(GameConfig.FISH_WIDTH), GameConfig.WORLD_HEIGHT));
            fishTimer = 0;
        }

        boneTimer += delta;
        if (boneTimer >= level.boneInterval) {
            bones.add(new FishBone(res.bone, randomX(GameConfig.BONE_WIDTH), GameConfig.WORLD_HEIGHT));
            boneTimer = 0;
        }

        boxTimer += delta;
        if (boxTimer >= GameConfig.MYSTERY_BOX_SPAWN_INTERVAL) {
            boxes.add(new MysteryBox(res.mysteryBox, randomX(GameConfig.MYSTERY_BOX_WIDTH), GameConfig.WORLD_HEIGHT));
            boxTimer = 0;
        }
    }

    private float randomX(float objectWidth) {
        return MathUtils.random(0, GameConfig.WORLD_WIDTH - objectWidth);
    }
}
