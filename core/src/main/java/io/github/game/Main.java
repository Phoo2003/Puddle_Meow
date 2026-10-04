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

public class Main extends ApplicationAdapter {

    private SpriteBatch batch;
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

        playerTexture = new Texture("Glossy Green Slime Cat Sticker.png");
        fishTexture = new Texture("cat.jpg");
        background = new Texture("ground.jpg");

        font = new BitmapFont();

        player = new Rectangle();
        player.x = 350;
        player.y = 20;
        player.width = 70;
        player.height = 100;

        fishes = new Array<>();

        score = 0;
        spawnTimer = 0;
    }

    private void spawnFish() {

        Rectangle fish = new Rectangle();

        fish.x = MathUtils.random(0, 700);
        fish.y = 480;

        fish.width = 40;
        fish.height = 50;

        fishes.add(fish);
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

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
        if (player.x > 736) player.x = 736;

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
            else if (fish.y < -32) {
                fishes.removeIndex(i);
            }
        }

        batch.begin();

        batch.draw(background, 0, 0, 800, 480);

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

        font.draw(batch, "Score: " + score, 20, 460);

        batch.end();
    }

    @Override
    public void dispose() {

        batch.dispose();
        playerTexture.dispose();
        fishTexture.dispose();
        font.dispose();
    }
}
