package io.github.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;

public class Cat {
    Sprite sprite;
    Texture solidTexture;
    Texture liquidTexture;
    CatState state = CatState.SOLID;
    int hp = 3;
    float speed = 5f;
    float liquidTimer = 0;

    public Cat(Texture solidTexture, Texture liquidTexture) {
        this.solidTexture = solidTexture;
        this.liquidTexture = liquidTexture;
        sprite = new Sprite(solidTexture);
        sprite.setSize(1, 1);
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
        float centerX = sprite.getX() + sprite.getWidth() / 2;
        state = newState;
        if (state == CatState.LIQUID) {
            sprite.setRegion(liquidTexture);
            sprite.setSize(1.8f, 0.55f);
        } else {
            sprite.setRegion(solidTexture);
            sprite.setSize(1, 1);
        }
        sprite.setCenterX(centerX);
    }

}
