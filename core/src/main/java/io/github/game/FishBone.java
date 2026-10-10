package io.github.game;

import com.badlogic.gdx.graphics.Texture;

public class FishBone extends FallingItems {

    private static final int DAMAGE = 1;

    public FishBone(Texture texture, float x, float y) {
        super(texture, x, y, 45, 60, 200f);
    }

    public int getDamage() {
        return DAMAGE;
    }
}
