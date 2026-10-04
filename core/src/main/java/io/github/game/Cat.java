package io.github.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.viewport.Viewport;

public class Cat {
    private static final float SPEED = 5f;
    private static final float SOLID_W = 1f, SOLID_H = 1f;
    private static final float LIQUID_W = 1.8f, LIQUID_H = 0.55f;

    public static final int START_HP = 3;
    public static final int MAX_HP = 5;
    public static final float LIQUID_TIME = 6f;
    public static final float BOOST_TIME = 10f;

    private final Texture solidTex, liquidTex;
    private final Sprite sprite;

    public CatState state = CatState.SOLID;
    public int hp = START_HP;
    public float liquidTimer = 0f;
    public float scoreBoostTimer = 0f;

    public Cat(Texture solidTex, Texture liquidTex) {
        this.solidTex = solidTex;
        this.liquidTex = liquidTex;
        sprite = new Sprite(solidTex);
        sprite.setSize(SOLID_W, SOLID_H);
    }

    public void reset(float worldWidth) {
        hp = START_HP;
        liquidTimer = 0;
        scoreBoostTimer = 0;
        setState(CatState.SOLID);
        sprite.setCenterX(worldWidth / 2f);
        sprite.setY(0);
    }

    public void setState(CatState newState) {
        float cx = sprite.getX() + sprite.getWidth() / 2f;
        state = newState;
        if (newState == CatState.LIQUID) {
            sprite.setRegion(liquidTex);
            sprite.setSize(LIQUID_W, LIQUID_H);
        } else {
            sprite.setRegion(solidTex);
            sprite.setSize(SOLID_W, SOLID_H);
        }
        sprite.setCenterX(cx);
        sprite.setY(0);
    }

    public void update(float delta, Viewport viewport, Vector2 touchPos) {
        // Input: arrow keys / A-D / touch
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) {
            sprite.translateX(SPEED * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) {
            sprite.translateX(-SPEED * delta);
        }
        if (Gdx.input.isTouched()) {
            touchPos.set(Gdx.input.getX(), Gdx.input.getY());
            viewport.unproject(touchPos);
            sprite.setCenterX(touchPos.x);
        }

        // Keep inside the world
        sprite.setX(MathUtils.clamp(sprite.getX(), 0, viewport.getWorldWidth() - sprite.getWidth()));

        // Timers
        if (scoreBoostTimer > 0) scoreBoostTimer = Math.max(0, scoreBoostTimer - delta);
        if (state == CatState.LIQUID) {
            liquidTimer -= delta;
            if (liquidTimer <= 0) setState(CatState.SOLID);
        }
    }

    /** Applies a random power-up and returns its display name. */
    public String applyRandomPowerUp() {
        switch (MathUtils.random(2)) {
            case 0:
                hp = Math.min(hp + 1, MAX_HP);
                return "Extra Heart! +1 HP";
            case 1:
                scoreBoostTimer = BOOST_TIME;
                return "Score Boost! x2";
            default:
                liquidTimer = LIQUID_TIME;
                setState(CatState.LIQUID);
                return "Liquid Boost!";
        }
    }

    public int scoreMultiplier() {
        return scoreBoostTimer > 0 ? 2 : 1;
    }

    public void takeDamage() {
        hp--;
    }

    public boolean isDead() {
        return hp <= 0;
    }

    public Rectangle getBounds() {
        return sprite.getBoundingRectangle();
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
