package io.github.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

/** Entry point: wires the parts together and runs the loop. */
public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private FitViewport viewport;

    private GameResources res;
    private GameWorld world;
    private Hud hud;

    private boolean started = false;   // false = start screen is showing

    @Override
    public void create() {
        batch = new SpriteBatch();
        viewport = new FitViewport(GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);

        res = new GameResources();
        world = new GameWorld(res);
        hud = new Hud(res);

    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    @Override
    public void render() {
        // R restarts the game after a win or a game over.
        if (!started) {
            // Start screen: SPACE, ENTER or a tap begins the game.
            if (Gdx.input.isKeyJustPressed(Keys.SPACE)
                || Gdx.input.isKeyJustPressed(Keys.ENTER)
                || Gdx.input.justTouched()) {
                started = true;
                res.theme.play();
            }
        } else {
            // R restarts the game after a win or a game over.
            if (world.isFinished() && Gdx.input.isKeyJustPressed(Keys.R)) {
                world.restart();
            }

            if (!world.isFinished()) {
                handleInput();
                world.update(Gdx.graphics.getDeltaTime());
            }
        }

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);
        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();
        batch.draw(res.backgrounds[world.levelIndex], 0, 0, GameConfig.WORLD_WIDTH, GameConfig.WORLD_HEIGHT);
        if (started) {
            world.draw(batch);
            hud.draw(batch, world);
        } else {
            hud.drawStartScreen(batch);
        }
        batch.end();
    }

    /** Space uses the stored liquid boost; touch moves the cat. */
    private void handleInput() {
        if (Gdx.input.isKeyJustPressed(Keys.SPACE)) {
            world.useStoredLiquid();
        }

        // Convert the touch position to world coordinates and move the cat there.
        if (Gdx.input.isTouched()) {
            float worldX = viewport.unproject(new Vector2(Gdx.input.getX(), 0)).x;
            world.moveCatTo(worldX);
        }
    }

    @Override
    public void dispose() {
        batch.dispose();
        res.dispose();
    }
}
