package io.github.game;

import com.badlogic.gdx.graphics.Texture;

public class Fish extends FallingItems {

    private static final int SCORE_VALUE = 1;

    public Fish(Texture texture, float x, float y) {
        super(texture, x, y, 50, 65, 200f);
    }

    public int getScoreValue() {
        return SCORE_VALUE;
    }
}
