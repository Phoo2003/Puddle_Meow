package io.github.game;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.utils.Array;

/** Owns the spawn timers and creates falling objects at the top of the screen. */
public class Spawner {

    private final GameResources res;

    private float fishTimer = 0;
    private float boneTimer = 0;
    private float boxTimer = 0;

    public Spawner(GameResources res) {
        this.res = res;
    }

    public void update(float delta,
                       Array<Fish> fishes,
                       Array<FishBone> bones,
                       Array<MysteryBox> boxes) {

        fishTimer += delta;
        if (fishTimer >= GameConfig.FISH_SPAWN_INTERVAL) {
            fishes.add(new Fish(res.fish, randomX(GameConfig.FISH_WIDTH), GameConfig.WORLD_HEIGHT));
            fishTimer = 0;
        }

        boneTimer += delta;
        if (boneTimer >= GameConfig.BONE_SPAWN_INTERVAL) {
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
