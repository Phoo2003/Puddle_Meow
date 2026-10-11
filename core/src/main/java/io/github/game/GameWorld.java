package io.github.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;

/** Game state and rules: levels, movement, spawning, collisions, win/lose. No UI drawing here. */
public class GameWorld {

    private final GameResources res;

    private Spawner spawner;
    private LevelConfig level;

    public final Cat cat;
    public final Array<Fish> fishes = new Array<>();
    public final Array<FishBone> bones = new Array<>();
    public final Array<MysteryBox> mysteryBoxes = new Array<>();
    public final ScoreBoost scoreBoost = new ScoreBoost();
    public final Notification notice = new Notification();

    // Liquid boost is stored (max one) and activated by the player.
    public boolean liquidStored = false;

    // Level progress
    public int levelIndex = 0;
    public boolean levelComplete = false;      // short pause between levels
    private float levelCompleteTimer = 0;

    public int score = 0;                      // score of the current level
    public boolean win = false;
    public boolean gameOver = false;

    public GameWorld(GameResources res) {
        this.res = res;

        cat = new Cat(res.cat, res.liquidCat);
        cat.reset(GameConfig.WORLD_WIDTH);   // also resets cat HP to Cat.MAX_HP

        startLevel(0);
    }

    // ---- levels --------------------------------------------------------------

    public int getLevelNumber() {
        return levelIndex + 1;
    }

    public int getTargetScore() {
        return level.targetScore;
    }

    /** Resets everything that belongs to a level. HP and the stored liquid boost carry over. */
    private void startLevel(int index) {
        levelIndex = index;
        level = LevelConfig.get(index);
        spawner = new Spawner(res, level);

        fishes.clear();
        bones.clear();
        mysteryBoxes.clear();

        score = 0;
        levelComplete = false;
    }

    // ---- public API ------------------------------------------------------------

    public boolean isFinished() {
        return win || gameOver;
    }

    /** Touch control: move the cat to the touched world x. */
    public void moveCatTo(float worldX) {
        cat.sprite.setCenterX(worldX);
    }

    /** Start a brand new game from level 1 (after a win or a game over). */
    public void restart() {
        cat.reset(GameConfig.WORLD_WIDTH);   // HP back to full, cat back to start position
        scoreBoost.reset();
        notice.clear();
        liquidStored = false;

        win = false;
        gameOver = false;
        startLevel(0);

        res.theme.play();   // music is stopped on game over
    }

    /** Player pressed the "use liquid boost" key. */
    public void useStoredLiquid() {
        // Do nothing if nothing is stored or the cat is already liquid.
        if (liquidStored && cat.state == CatState.SOLID) {
            cat.becomeLiquid();
            liquidStored = false;
        }
    }

    public void update(float delta) {
        notice.update(delta);

        // Pause between levels: wait, then start the next one.
        if (levelComplete) {
            levelCompleteTimer -= delta;
            if (levelCompleteTimer <= 0) {
                startLevel(levelIndex + 1);
            }
            return;
        }

        cat.update(delta, GameConfig.WORLD_WIDTH);

        Rectangle catBounds = buildCatHitbox();
        // The liquid cat catches fish over its full wide sprite.
        Rectangle catchBounds = (cat.state == CatState.LIQUID) ? cat.getBounds() : catBounds;

        spawner.update(delta, fishes, bones, mysteryBoxes);
        scoreBoost.update(delta);

        // Faster levels: fish and bones fall faster by scaling the time passed to them.
        float fallDelta = delta * level.speedMultiplier;
        updateFish(fallDelta, catchBounds);
        updateBones(fallDelta, catBounds);
        updateMysteryBoxes(delta, catBounds);

        checkEndConditions();
    }

    public void draw(SpriteBatch batch) {
        cat.draw(batch);
        for (Fish fish : fishes) fish.draw(batch);
        for (FishBone bone : bones) bone.draw(batch);
        for (MysteryBox box : mysteryBoxes) box.draw(batch);
    }

    // ---- hitbox ------------------------------------------------------------

    private Rectangle buildCatHitbox() {
        boolean liquid = cat.state == CatState.LIQUID;

        float w = cat.sprite.getWidth()
            * (liquid ? GameConfig.LIQUID_HITBOX_W : GameConfig.SOLID_HITBOX_W);
        float h = cat.sprite.getHeight()
            * (liquid ? GameConfig.LIQUID_HITBOX_H : GameConfig.SOLID_HITBOX_H);

        return new Rectangle(
            cat.sprite.getX() + (cat.sprite.getWidth() - w) / 2f,
            cat.sprite.getY() + (cat.sprite.getHeight() - h) / 2f,
            w,
            h
        );
    }

    // ---- per-object updates -----------------------------------------------

    private void updateFish(float delta, Rectangle catchBounds) {
        for (int i = fishes.size - 1; i >= 0; i--) {
            Fish fish = fishes.get(i);
            fish.update(delta);

            if (fish.getBounds().overlaps(catchBounds)) {
                score += scoreBoost.apply(fish.getScoreValue());
                fishes.removeIndex(i);
            } else if (fish.getY() < -fish.getBounds().height) {
                fishes.removeIndex(i);
            }
        }
    }

    private void updateBones(float delta, Rectangle catBounds) {
        for (int i = bones.size - 1; i >= 0; i--) {
            FishBone bone = bones.get(i);
            bone.update(delta);

            if (bone.getBounds().overlaps(catBounds)) {
                res.boneSound.play();

                // Only the solid cat takes damage; the liquid cat just catches it.
                if (cat.state == CatState.SOLID) {
                    cat.takeDamage(bone.getDamage());
                }
                bones.removeIndex(i);
            } else if (bone.getY() < -bone.getBounds().height) {
                bones.removeIndex(i);
            }
        }
    }

    private void updateMysteryBoxes(float delta, Rectangle catBounds) {
        for (int i = mysteryBoxes.size - 1; i >= 0; i--) {
            MysteryBox box = mysteryBoxes.get(i);
            box.update(delta);

            if (box.getBounds().overlaps(catBounds)) {
                res.mysteryBoxSound.play();
                applyReward(box.getRandomReward());
                mysteryBoxes.removeIndex(i);
            } else if (box.getY() + box.getBounds().height < 0) {
                mysteryBoxes.removeIndex(i);
            }
        }
    }

    private void applyReward(MysteryBox.Reward reward) {
        switch (reward) {
            case EXTRA_HEART: {
                int before = cat.getHp();
                cat.heal(1);
                notice.show(
                    cat.getHp() > before ? "EXTRA HEART! +1 HP" : "HEARTS ALREADY FULL",
                    GameConfig.NOTICE_DURATION
                );
                break;
            }
            case SCORE_BOOST:
                // Used immediately.
                scoreBoost.activate(GameConfig.SCORE_BOOST_DURATION);
                break;
            case LIQUID_BOOST:
                // Stored, not used immediately. Only one can be kept.
                if (liquidStored) {
                    notice.show("LIQUID BOOST ALREADY STORED", GameConfig.NOTICE_DURATION);
                } else {
                    liquidStored = true;
                }
                break;
        }
    }

    // ---- win / lose / next level -------------------------------------------

    private void checkEndConditions() {
        // Losing is checked first so it wins over a simultaneous win.
        if (cat.getHp() <= 0) {
            gameOver = true;
            res.theme.stop();
        }

        if (!gameOver && score >= level.targetScore) {
            boolean lastLevel = levelIndex == LevelConfig.count() - 1;

            if (lastLevel) {
                win = true;
            } else {
                levelComplete = true;
                levelCompleteTimer = GameConfig.LEVEL_PAUSE;
            }
        }
    }
}
