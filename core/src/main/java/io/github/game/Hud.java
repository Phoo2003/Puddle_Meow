package io.github.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/** Draws everything that is not part of the playfield: score, hearts, timers, end messages. */
public class Hud {

    private final GameResources res;

    public Hud(GameResources res) {
        this.res = res;
    }

    public void draw(SpriteBatch batch, GameWorld world) {
        float top = GameConfig.WORLD_HEIGHT;

        res.font.draw(batch, "Score: " + world.score, 20, top - 20);
        drawHearts(batch, world.cat, 20, top - 50 - GameConfig.HEART_SIZE);

        drawStatusLines(batch, world, top - 100);

        if (world.win) {
            res.font.draw(batch, "LEVEL COMPLETE!", 320, 240);
        }
        if (world.gameOver) {
            res.font.draw(batch, "GAME OVER", 350, 240);
        }
    }

    /** All boost / pick-up messages share one area and stack from top to bottom. */
    private void drawStatusLines(SpriteBatch batch, GameWorld world, float startY) {
        float y = startY;

        if (world.scoreBoost.isActive()) {
            res.font.draw(batch, "DOUBLE SCORE: " + world.scoreBoost.secondsLeft() + "s", 20, y);
            y -= GameConfig.STATUS_LINE_HEIGHT;
        }

        if (world.cat.state == CatState.LIQUID) {
            res.font.draw(batch, "LIQUID CAT: " + (int) Math.ceil(world.cat.liquidTimer) + "s", 20, y);
            y -= GameConfig.STATUS_LINE_HEIGHT;
        }

        if (world.liquidStored) {
            res.font.draw(batch, "LIQUID CAT IS READY: press SPACE", 20, y);
            y -= GameConfig.STATUS_LINE_HEIGHT;
        }

        if (world.notice.isVisible()) {
            res.font.draw(batch, world.notice.getText(), 20, y);
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
