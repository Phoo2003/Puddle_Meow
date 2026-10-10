package io.github.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
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

    private com.badlogic.gdx.math.Rectangle player;

    private Array<Fish> fishes;
    private Array<FishBone> bones;

    private float fishSpawnTimer;
    private float boneSpawnTimer;

    private static final float FISH_SPAWN_INTERVAL = 2.0f;
    private static final float BONE_SPAWN_INTERVAL = 4.0f;

    private int score;
    private int hp;

    private static final int TARGET_SCORE = 100;

    private boolean win;
    private boolean gameOver;

    @Override
    public void create() {

        batch = new SpriteBatch();
        viewport = new FitViewport(800, 480);

        playerTexture = new Texture("Cat.png");
        fishTexture = new Texture("fish.png");
        boneTexture = new Texture("fishbone.png");
        background = new Texture("ground.jpg");

        font = new BitmapFont();

        player = new com.badlogic.gdx.math.Rectangle();

        player.width = 85;
        player.height = 100;

        player.x = viewport.getWorldWidth() / 2f
            - player.width / 2f;

        player.y = 20;

        fishes = new Array<>();
        bones = new Array<>();

        fishSpawnTimer = 0;
        boneSpawnTimer = 0;

        score = 0;
        hp = 3;

        win = false;
        gameOver = false;
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, true);
    }

    private void spawnFish() {

        float width = 40;

        float x = MathUtils.random(
            0,
            viewport.getWorldWidth() - width
        );

        float y = viewport.getWorldHeight();

        fishes.add(new Fish(fishTexture, x, y));
    }

    private void spawnBone() {

        float width = 30;

        float x = MathUtils.random(
            0,
            viewport.getWorldWidth() - width
        );

        float y = viewport.getWorldHeight();

        bones.add(new FishBone(boneTexture, x, y));
    }

    private void updateGame(float delta) {

        float screenWidth = viewport.getWorldWidth();

        // move the cat
        // Keyboard controls
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            player.x -= 300 * delta;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            player.x += 300 * delta;
        }

        // Touchscreen controls
        if (Gdx.input.isTouched()) {
            float touchX = Gdx.input.getX();

            // Convert screen coordinates to game world coordinates
            float worldX = viewport.unproject(
                new com.badlogic.gdx.math.Vector2(touchX, 0)
            ).x;

            // Move the cat to the touched horizontal position
            player.x = worldX - player.width / 2;
        }

        // Keep the cat inside the screen
        player.x = MathUtils.clamp(
            player.x,
            0,
            viewport.getWorldWidth() - player.width
        );

        // Keep the cat inside the screen.
        player.x = MathUtils.clamp(
            player.x,
            0,
            screenWidth - player.width
        );

        // Spawn fish at regular intervals.
        fishSpawnTimer += delta;

        if (fishSpawnTimer >= FISH_SPAWN_INTERVAL) {
            spawnFish();
            fishSpawnTimer = 0;
        }

        // Spawn bones at regular intervals.
        boneSpawnTimer += delta;

        if (boneSpawnTimer >= BONE_SPAWN_INTERVAL) {
            spawnBone();
            boneSpawnTimer = 0;
        }

        // Update fish and check collisions.
        for (int i = fishes.size - 1; i >= 0; i--) {

            Fish fish = fishes.get(i);

            fish.update(delta);

            if (fish.getBounds().overlaps(player)) {

                score += fish.getScoreValue();

                fishes.removeIndex(i);

            } else if (fish.getY() < -fish.getBounds().height) {

                fishes.removeIndex(i);
            }
        }

        // Update bones and check collisions.
        for (int i = bones.size - 1; i >= 0; i--) {

            FishBone bone = bones.get(i);

            bone.update(delta);

            if (bone.getBounds().overlaps(player)) {

                hp -= bone.getDamage();

                bones.removeIndex(i);

            } else if (bone.getY() < -bone.getBounds().height) {

                bones.removeIndex(i);
            }
        }

        // Check losing condition first.
        if (hp <= 0) {
            hp = 0;
            gameOver = true;
        }

        // Check winning condition.
        if (score >= TARGET_SCORE && !gameOver) {
            win = true;
        }
    }

    @Override
    public void render() {

        float delta = Gdx.graphics.getDeltaTime();

        float screenWidth = viewport.getWorldWidth();
        float screenHeight = viewport.getWorldHeight();

        ScreenUtils.clear(0.15f, 0.15f, 0.2f, 1f);

        // Stop updating the game after winning or losing.
        if (!win && !gameOver) {
            updateGame(delta);
        }

        viewport.apply();
        batch.setProjectionMatrix(viewport.getCamera().combined);

        batch.begin();

        // Draw background.
        batch.draw(
            background,
            0,
            0,
            screenWidth,
            screenHeight
        );

        // Draw cat.
        batch.draw(
            playerTexture,
            player.x,
            player.y,
            player.width,
            player.height
        );

        // Draw fish.
        for (Fish fish : fishes) {
            fish.draw(batch);
        }

        // Draw bones.
        for (FishBone bone : bones) {
            bone.draw(batch);
        }

        // Draw score and HP.
        font.draw(batch, "Score: " + score, 20, screenHeight - 20);
        font.draw(batch, "HP: " + hp, 20, screenHeight - 45);

        // Display the ending message.
        if (win) {
            font.draw(batch, "LEVEL COMPLETE!", 320, 240);
        }

        if (gameOver) {
            font.draw(batch, "GAME OVER", 350, 240);
        }

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
