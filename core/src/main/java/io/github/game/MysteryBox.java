package io.github.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class MysteryBox extends FallingItems {

    public enum Reward {
        EXTRA_HEART,
        SCORE_BOOST,
        LIQUID_BOOST,
        BOMB
    }

    public MysteryBox(Texture texture, float x, float y) {
        super(texture, x, y, 50, 50, 200f);
    }

    public Reward getRandomReward() {
        int random = MathUtils.random(0, 3);

        switch (random) {
            case 0:
                return Reward.EXTRA_HEART;

            case 1:
                return Reward.SCORE_BOOST;

            case 2:
                return Reward.LIQUID_BOOST;

            default:
                return Reward.BOMB;
        }
    }
}
