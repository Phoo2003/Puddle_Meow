package io.github.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
    private FitViewport viewport;
    private Texture playerTexture;
    private Texture fishTexture;
    private Texture background;
    private BitmapFont font;

    private Rectangle player;

    private Array<Rectangle> fishes;

    private float spawnTimer;

    private int score;

    @Override
    public void create() {

        batch = new SpriteBatch();
        viewport = new FitViewport(800, 480);

        playerTexture = new Texture("cat.png");
        fishTexture = new Texture("Fish2.png");
        background = new Texture("ground.jpg");

        font = new BitmapFont();

        player = new Rectangle();
        player.width = 70;
        player.height = 100;
        player.x = viewport.getWorldWidth() / 2f - player.width / 2f;
        player.y = 20;

        fishes = new Array<>();

        score = 0;
        spawnTimer = 0;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    private void spawnFish() {

        Rectangle fish = new Rectangle();

        fish.width = 40;
        fish.height = 50;

        fish.x = MathUtils.random(0, viewport.getWorldWidth() - fish.width);
        fish.y = viewport.getWorldHeight();

        fishes.add(fish);
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();
        float screenWidth = viewport.getWorldWidth();
        float screenHeight = viewport.getWorldHeight();

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Move player
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.x -= 300 * delta;
        }

        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.x += 300 * delta;
        }

        // Keep inside screen
        if (player.x < 0) player.x = 0;
        if (player.x > screenWidth - player.width) player.x = screenWidth - player.width;

        // Spawn fish every second
        spawnTimer += delta;

        if (spawnTimer >= 1f) {
            spawnFish();
            spawnTimer = 0;
        }

        // Update fish
        for (int i = fishes.size - 1; i >= 0; i--) {

            Rectangle fish = fishes.get(i);

            fish.y -= 200 * delta;

            // Collision
            if (fish.overlaps(player)) {

                score++;

                fishes.removeIndex(i);
            }

            // Remove if off screen
            else if (fish.y < -fish.height) {
                fishes.removeIndex(i);
            }
        }

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);
        batch.begin();

        batch.draw(background, 0, 0, screenWidth, screenHeight);

        // Draw player
        batch.draw(
            playerTexture,
            player.x,
            player.y,
            player.width,
            player.height
        );

        // Draw fish
        for (Rectangle fish : fishes) {

            batch.draw(
                fishTexture,
                fish.x,
                fish.y,
                fish.width,
                fish.height
            );
        }

        font.draw(batch, "Score: " + score, 20, screenHeight - 20);

        batch.end();
    }

    @Override
    public void dispose() {

        batch.dispose();
        playerTexture.dispose();
        fishTexture.dispose();
        background.dispose();
        font.dispose();
    }
}
