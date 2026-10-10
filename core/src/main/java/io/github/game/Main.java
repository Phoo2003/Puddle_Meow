package io.github.game;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
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

    // Player (Swan's Cat class)
    private Cat cat;

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

    // Mystery box texture and objects
    private Texture mysteryBoxTexture;
    private Array<MysteryBox> mysteryBoxes;

    // Mystery box spawn timer
    private float mysteryBoxSpawnTimer = 0;
    private static final float MYSTERY_BOX_SPAWN_INTERVAL = 10f;

    // Power-up states
    private boolean scoreBoostActive = false;
    private float scoreBoostTimer = 0;

    @Override
    public void create() {

        batch = new SpriteBatch();
        viewport = new FitViewport(800, 480);

        playerTexture = new Texture("Cat.png");
        fishTexture = new Texture("fish.png");
        boneTexture = new Texture("fishbone.png");
        background = new Texture("ground.jpg");

        mysteryBoxTexture = new Texture("Cat.png");
        mysteryBoxes = new Array<>();

        font = new BitmapFont();

        // liquid form uses the same image for now (swap in a puddle image later)
        cat = new Cat(playerTexture, playerTexture);
        cat.reset(viewport.getWorldWidth());

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

    // Create a method to spawn mystery boxes
    private void spawnMysteryBox() {
        float x = MathUtils.random(
            0,
            viewport.getWorldWidth() - 35
        );

        mysteryBoxes.add(
            new MysteryBox(
                mysteryBoxTexture,
                x,
                viewport.getWorldHeight()
            )
        );
    }

    private void updateGame(float delta) {

        float screenWidth = viewport.getWorldWidth();

        // Touchscreen controls
        if (Gdx.input.isTouched()) {
            float touchX = Gdx.input.getX();

            // Convert screen coordinates to game world coordinates
            float worldX = viewport.unproject(
                new com.badlogic.gdx.math.Vector2(touchX, 0)
            ).x;

            // Move the cat to the touched horizontal position
            cat.sprite.setCenterX(worldX);
        }

        // Cat: keyboard movement, keep inside screen, liquid timer
        cat.update(delta, screenWidth);

        Rectangle catBounds = cat.getBounds();

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

        // Spawn mystery boxes at regular intervals.
        mysteryBoxSpawnTimer += delta;

        if (mysteryBoxSpawnTimer >= MYSTERY_BOX_SPAWN_INTERVAL) {
            spawnMysteryBox();
            mysteryBoxSpawnTimer = 0;
        }

        // Update mystery boxes and remove boxes off-screen
        for (int i = mysteryBoxes.size - 1; i >= 0; i--) {
            MysteryBox box = mysteryBoxes.get(i);

            box.update(delta);

            if (box.getY() + box.getBounds().height < 0) {
                mysteryBoxes.removeIndex(i);
            }
        }

        // Score boost expire
        if (scoreBoostActive) {
            scoreBoostTimer -= delta;

            if (scoreBoostTimer <= 0) {
                scoreBoostActive = false;
                scoreBoostTimer = 0;
            }
        }

        // Update fish and check collisions.
        for (int i = fishes.size - 1; i >= 0; i--) {

            Fish fish = fishes.get(i);

            fish.update(delta);

            if (fish.getBounds().overlaps(catBounds)) {

                // double score when get score boost
                if (scoreBoostActive) {
                    score += fish.getScoreValue() * 2;
                } else {
                    score += fish.getScoreValue();
                }

                fishes.removeIndex(i);

            } else if (fish.getY() < -fish.getBounds().height) {

                fishes.removeIndex(i);
            }
        }

        // Update bones and check collisions.
        for (int i = bones.size - 1; i >= 0; i--) {

            FishBone bone = bones.get(i);

            bone.update(delta);

            // Liquid cat is immune: bones pass through it
            boolean hit = bone.getBounds().overlaps(catBounds)
                && cat.state == CatState.SOLID;

            if (hit) {

                hp -= bone.getDamage();

                bones.removeIndex(i);

            } else if (bone.getY() < -bone.getBounds().height) {

                bones.removeIndex(i);
            }
        }

        // Update mystery boxes and check collisions
        for (int i = mysteryBoxes.size - 1; i >= 0; i--) {
            MysteryBox box = mysteryBoxes.get(i);

            box.update(delta);

            if (box.getBounds().overlaps(catBounds)) {
                MysteryBox.Reward reward = box.getRandomReward();

                switch (reward) {
                    case EXTRA_HEART:
                        if (hp < 3) {
                            hp++;
                        }
                        break;

                    case SCORE_BOOST:
                        scoreBoostActive = true;
                        scoreBoostTimer = 10f;
                        break;

                    case LIQUID_BOOST:
                        cat.becomeLiquid();
                        break;
                }

                mysteryBoxes.removeIndex(i);

            } else if (box.getY() + box.getBounds().height < 0) {
                mysteryBoxes.removeIndex(i);
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
        cat.draw(batch);

        // Draw fish.
        for (Fish fish : fishes) {
            fish.draw(batch);
        }

        // Draw bones.
        for (FishBone bone : bones) {
            bone.draw(batch);
        }

        // Draw mystery boxes
        for (MysteryBox box : mysteryBoxes) {
            box.draw(batch);
        }

        // Draw score and HP.
        font.draw(batch, "Score: " + score, 20, screenHeight - 20);
        font.draw(batch, "HP: " + hp, 20, screenHeight - 45);

        // Display the remaining time of score boost
        if (scoreBoostActive) {
            font.draw(
                batch,
                "DOUBLE SCORE: " + (int) Math.ceil(scoreBoostTimer) + "s",
                20,
                screenHeight - 70
            );
        }

        // Display the remaining time of liquid boost
        if (cat.state == CatState.LIQUID) {
            font.draw(
                batch,
                "LIQUID: " + (int) Math.ceil(cat.liquidTimer) + "s",
                20,
                screenHeight - 95
            );
        }

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

        mysteryBoxTexture.dispose();

        font.dispose();
    }
}
