package io.github.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Align;

/** Draws everything that is not part of the playfield: level, score, hearts, status, end messages. */
public class Hud {

    private final GameResources res;

    public Hud(GameResources res) {
        this.res = res;
    }

    public void draw(SpriteBatch batch, GameWorld world) {
        float top = GameConfig.WORLD_HEIGHT;

        res.font.draw(batch,
            "Level " + world.getLevelNumber() + "/" + LevelConfig.count()
                + "   Score: " + world.score + "/" + world.getTargetScore(),
            20, top - 20);

        drawHearts(batch, world.cat, 20, top - 50 - GameConfig.HEART_SIZE);
        drawStatusLines(batch, world, top - 100);
        drawCenterMessages(batch, world);
    }

    /** All boost / pick-up messages share one area and stack from top to bottom. */
    private void drawStatusLines(SpriteBatch batch, GameWorld world, float startY) {
        float y = startY;

        if (world.scoreBoost.isActive()) {
            res.font.draw(batch, "DOUBLE SCORE: " + world.scoreBoost.secondsLeft() + "s", 20, y);
            y -= GameConfig.STATUS_LINE_HEIGHT;
        }

        if (world.cat.state == CatState.LIQUID) {
            res.font.draw(batch, "PUDDLE MEOW: " + (int) Math.ceil(world.cat.liquidTimer) + "s", 20, y);
            y -= GameConfig.STATUS_LINE_HEIGHT;
        }

        if (world.liquidStored) {
            res.font.draw(batch, "PUDDLE MEOW READY: press SPACE", 20, y);
            y -= GameConfig.STATUS_LINE_HEIGHT;
        }

        if (world.notice.isVisible()) {
            res.font.draw(batch, world.notice.getText(), 20, y);
        }
    }

    private void drawCenterMessages(SpriteBatch batch, GameWorld world) {
        if (world.levelComplete) {
            res.font.draw(batch, "LEVEL " + world.getLevelNumber() + " COMPLETE!", 320, 250);
            res.font.draw(batch, "Get ready for level " + (world.getLevelNumber() + 1) + "...", 310, 225);
        }
        if (world.win) {
            res.font.draw(batch, "YOU WIN!", 360, 240);
            res.font.draw(batch, "Press R to play again", 325, 215);
        }
        if (world.gameOver) {
            res.font.draw(batch, "GAME OVER", 350, 240);
            res.font.draw(batch, "Press R to restart", 335, 215);
        }
    }

    /** Title, controls and "press to start". Every line is centered on the screen. */
    public void drawStartScreen(SpriteBatch batch) {
        float w = GameConfig.WORLD_WIDTH;

        res.font.getData().setScale(3f);
        res.font.draw(batch, "PUDDLE MEOW", 0, 400, w, Align.center, false);
        res.font.getData().setScale(1f);

        res.font.draw(batch, "Catch the fish, dodge the bones!", 0, 330, w, Align.center, false);

        res.font.draw(batch, "Move: LEFT / RIGHT keys or touch", 0, 270, w, Align.center, false);
        res.font.draw(batch, "SPACE: use stored liquid boost", 0, 245, w, Align.center, false);
        res.font.draw(batch, "UP / W: jump (when dogs appear)", 0, 220, w, Align.center, false);
        res.font.draw(batch, "R: restart after the game ends", 0, 195, w, Align.center, false);

        res.font.getData().setScale(1.5f);
        res.font.draw(batch, "Press SPACE or ENTER to start", 0, 120, w, Align.center, false);
        res.font.getData().setScale(1f);
    }

    /** One heart icon per HP slot: full = HP left, empty = HP lost. */
    private void drawHearts(SpriteBatch batch, Cat cat, float x, float y) {
        float size = GameConfig.HEART_SIZE;
        for (int i = 0; i < Cat.MAX_HP; i++) {
            Texture t = (i < cat.getHp()) ? res.heartFull : res.heartEmpty;
            batch.draw(t, x + i * (size + GameConfig.HEART_GAP), y, size, size);
        }
    }
}
