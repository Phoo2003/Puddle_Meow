package io.github.game;

import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

/** Game state and rules: movement, spawning, collisions, win/lose. No rendering of UI here. */
public class GameWorld {

    private final GameResources res;
    private final Spawner spawner;

    public final Cat cat;
    public final Array<Fish> fishes = new Array<>();
    public final Array<FishBone> bones = new Array<>();
    public final Array<MysteryBox> mysteryBoxes = new Array<>();
    public final ScoreBoost scoreBoost = new ScoreBoost();

    public int score = 0;
    public boolean win = false;
    public boolean gameOver = false;

    public GameWorld(GameResources res) {
        this.res = res;
        this.spawner = new Spawner(res);

        cat = new Cat(res.cat, res.liquidCat);
        cat.reset(GameConfig.WORLD_WIDTH);   // also resets cat HP to Cat.MAX_HP
    }

    public boolean isFinished() {
        return win || gameOver;
    }

    /** Touch control: move the cat to the touched world x. */
    public void moveCatTo(float worldX) {
        cat.sprite.setCenterX(worldX);
    }

    public void update(float delta) {
        cat.update(delta, GameConfig.WORLD_WIDTH);

        Rectangle catBounds = buildCatHitbox();
        // The liquid cat catches fish over its full wide sprite.
        Rectangle catchBounds = (cat.state == CatState.LIQUID) ? cat.getBounds() : catBounds;

        spawner.update(delta, fishes, bones, mysteryBoxes);
        scoreBoost.update(delta);

        updateFish(delta, catchBounds);
        updateBones(delta, catBounds);
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
            case EXTRA_HEART:
                cat.heal(1);
                break;
            case SCORE_BOOST:
                scoreBoost.activate(GameConfig.SCORE_BOOST_DURATION);
                break;
            case LIQUID_BOOST:
                cat.becomeLiquid();
                break;
        }
    }

    // ---- win / lose ---------------------------------------------------------

    private void checkEndConditions() {
        // Losing is checked first so it wins over a simultaneous win.
        if (cat.getHp() <= 0) {
            gameOver = true;
            res.theme.stop();
        }
        if (score >= GameConfig.TARGET_SCORE && !gameOver) {
            win = true;
        }
    }
}
