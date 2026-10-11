package io.github.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

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

    /** One heart icon per HP slot: full = HP left, empty = HP lost. */
    private void drawHearts(SpriteBatch batch, Cat cat, float x, float y) {
        float size = GameConfig.HEART_SIZE;
        for (int i = 0; i < Cat.MAX_HP; i++) {
            Texture t = (i < cat.getHp()) ? res.heartFull : res.heartEmpty;
            batch.draw(t, x + i * (size + GameConfig.HEART_GAP), y, size, size);
        }
    }
}
