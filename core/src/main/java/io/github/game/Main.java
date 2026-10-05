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
    private Texture boneTexture;
    private Texture background;
    private BitmapFont font;

    private Rectangle player;

    private Array<Rectangle> fishes;
    private Array<Rectangle> bones;

    private float spawnTimer;

    private int score;
    private int hp;

    @Override
    public void create() {

        batch = new SpriteBatch();
        viewport = new FitViewport(800, 480);

        playerTexture = new Texture("Cat.png");
        fishTexture = new Texture("fish.png");
        boneTexture = new Texture("fishbone.png");
        background = new Texture("ground.jpg");

        font = new BitmapFont();

        player = new Rectangle();
        player.width = 70;
        player.height = 100;
        player.x = viewport.getWorldWidth() / 2f - player.width / 2f;
        player.y = 20;

        fishes = new Array<>();
        bones = new Array<>();

        score = 0;
        hp = 3;
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

    private void spawnBone() {

        Rectangle bone = new Rectangle();

        bone.width = 40;
        bone.height = 50;

        bone.x = MathUtils.random(0, viewport.getWorldWidth() - bone.width);
        bone.y = viewport.getWorldHeight();

        bones.add(bone);
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

        // Spawn fish or bone every second
        spawnTimer += delta;

        if (spawnTimer >= 1f) {
            if (MathUtils.random() < 0.3f) spawnBone();
            else spawnFish();
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

        // Update bones
        for (int i = bones.size - 1; i >= 0; i--) {

            Rectangle bone = bones.get(i);

            bone.y -= 200 * delta;

            // Collision: lose HP
            if (bone.overlaps(player)) {

                hp--;

                bones.removeIndex(i);
            }

            // Remove if off screen
            else if (bone.y < -bone.height) {
                bones.removeIndex(i);
            }
        }

        // Game over: restart
        if (hp <= 0) {
            hp = 3;
            score = 0;
            fishes.clear();
            bones.clear();
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

        // Draw bones
        for (Rectangle bone : bones) {

            batch.draw(
                boneTexture,
                bone.x,
                bone.y,
                bone.width,
                bone.height
            );
        }

        font.draw(batch, "Score: " + score, 20, screenHeight - 20);
        font.draw(batch, "HP: " + hp, 20, screenHeight - 45);

        batch.end();
    }

    @Override
    public void dispose() {

        batch.dispose();
        playerTexture.dispose();
        fishTexture.dispose();
        boneTexture.dispose();
        background.dispose();
        font.dispose();
    }
}
