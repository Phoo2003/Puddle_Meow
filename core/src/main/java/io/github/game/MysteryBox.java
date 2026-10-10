package io.github.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;

public class MysteryBox extends FallingItems {

    public enum Reward {
        EXTRA_HEART,
        SCORE_BOOST,
        LIQUID_BOOST
    }

    public MysteryBox(Texture texture, float x, float y) {
        super(texture, x, y, 50, 50, 100f);
    }

    public Reward getRandomReward() {
        int random = MathUtils.random(0, 2);

        switch (random) {
            case 0:
                return Reward.EXTRA_HEART;

            case 1:
                return Reward.SCORE_BOOST;

            default:
                return Reward.LIQUID_BOOST;
        }
    }
}
