package io.github.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;

public abstract class FallingItems {

    protected Texture texture;
    protected Rectangle bounds;
    protected float speed;

    public FallingItems(
        Texture texture,
        float x,
        float y,
        float width,
        float height,
        float speed
    ) {
        this.texture = texture;
        this.speed = speed;

        bounds = new Rectangle(x, y, width, height);
    }

    // Move the object downward.
    public void update(float delta) {
        bounds.y -= speed * delta;
    }

    // Draw the object.
    public void draw(SpriteBatch batch) {
        batch.draw(
            texture,
            bounds.x,
            bounds.y,
            bounds.width,
            bounds.height
        );
    }

    // Return the object's collision area.
    public Rectangle getBounds() {
        return bounds;
    }

    public float getY() {
        return bounds.y;
    }
}
