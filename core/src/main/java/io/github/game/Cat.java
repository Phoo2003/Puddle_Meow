package io.github.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Cat {
    public static final int MAX_HP = 3;

    Sprite sprite;
    Texture solidTexture;
    Texture liquidTexture;
    CatState state = CatState.SOLID;
    int hp = MAX_HP;
    float speed = 300f;
    float liquidTimer = 0;

    public Cat(Texture solidTexture, Texture liquidTexture) {
        this.solidTexture = solidTexture;
        this.liquidTexture = liquidTexture;
        sprite = new Sprite(solidTexture);
        sprite.setSize(85, 100);
        sprite.setY(20);
    }

    public void reset(float worldWidth) {
        hp = MAX_HP;
        liquidTimer = 0;
        setState(CatState.SOLID);
        sprite.setCenterX(worldWidth / 2);
    }

    public void update(float delta, float worldWidth) {
        // movement
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            sprite.translateX(speed * delta);
        } else if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            sprite.translateX(-speed * delta);
        }
        sprite.setX(MathUtils.clamp(sprite.getX(), 0, worldWidth - sprite.getWidth()));

        // liquid timer
        if (state == CatState.LIQUID) {
            liquidTimer -= delta;
            if (liquidTimer <= 0) {
                setState(CatState.SOLID);
            }
        }
    }

    public void setState(CatState newState) {
        float centerX = sprite.getX() + sprite.getWidth() / 2f;
        float bottomY = sprite.getY();

        state = newState;

        if (state == CatState.LIQUID) {
            sprite.setRegion(liquidTexture);
            sprite.setSize(190f, 65f);
        } else {
            sprite.setRegion(solidTexture);
            sprite.setSize(85f, 100f);
        }

        sprite.setPosition(
            centerX - sprite.getWidth() / 2f,
            bottomY
        );
    }

    public void becomeLiquid() {
        liquidTimer = 5f;
        setState(CatState.LIQUID);
    }

    /** Lose HP (liquid cat is immune to bones). */
    public void takeDamage(int amount) {
        if (state == CatState.SOLID) {
            hp = Math.max(0, hp - amount);
        }
    }

    public void takeDamage() {
        takeDamage(1);
    }

    /** Gain HP, never above MAX_HP. */
    public void heal(int amount) {
        hp = Math.min(MAX_HP, hp + amount);
    }

    public int getHp() {
        return hp;
    }

    public Rectangle getBounds() {
        return sprite.getBoundingRectangle();
    }

    public void draw(SpriteBatch batch) {
        sprite.draw(batch);
    }
}
