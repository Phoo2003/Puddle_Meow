package io.github.game;

/** A short message that is shown for a few seconds, then disappears. */
public class Notification {

    private String text = "";
    private float timer = 0;

    public void show(String text, float seconds) {
        this.text = text;
        this.timer = seconds;
    }

    public void clear() {
        timer = 0;
    }

    public void update(float delta) {
        if (timer > 0) {
            timer -= delta;
        }
    }

    public boolean isVisible() {
        return timer > 0;
    }

    public String getText() {
        return text;
    }
}
