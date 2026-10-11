package io.github.game;

/** Timed "double score" power-up. */
public class ScoreBoost {

    private float timer = 0;

    public void activate(float seconds) {
        timer = seconds;
    }

    public void update(float delta) {
        if (timer > 0) {
            timer -= delta;
            if (timer < 0) timer = 0;
        }
    }

    public boolean isActive() {
        return timer > 0;
    }

    public int secondsLeft() {
        return (int) Math.ceil(timer);
    }

    /** Returns the points to award, doubled while the boost is active. */
    public int apply(int points) {
        return isActive() ? points * 2 : points;
    }
}
