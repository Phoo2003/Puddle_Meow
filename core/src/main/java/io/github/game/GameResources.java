package io.github.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.utils.Disposable;

/** Loads and disposes every texture, sound, music track and font. */
public class GameResources implements Disposable {

    public final Texture cat = new Texture("Cat.png");
    public final Texture liquidCat = new Texture("Liquidform.png");
    public final Texture fish = new Texture("pixel_fish.png");
    public final Texture bone = new Texture("pixel_fishbone.png");
    public final Texture mysteryBox = new Texture("mysterybox.png");
    public final Texture heartFull = new Texture("heart.png");
    public final Texture heartEmpty = new Texture("heart_empty.png");

    /** One background per level, indexed by level index. */
    public final Texture[] backgrounds;

    public final Sound boneSound =
        Gdx.audio.newSound(Gdx.files.internal("Cat eat the bone sound.wav"));
    public final Sound mysteryBoxSound =
        Gdx.audio.newSound(Gdx.files.internal("Mysterybox sound.wav"));
    public final Music theme =
        Gdx.audio.newMusic(Gdx.files.internal("Theme music.mp3"));

    public final BitmapFont font = new BitmapFont();

    public GameResources() {
        theme.setLooping(true);
        theme.setVolume(GameConfig.MUSIC_VOLUME);

        backgrounds = new Texture[LevelConfig.count()];
        for (int i = 0; i < backgrounds.length; i++) {
            String file = LevelConfig.get(i).backgroundFile;

            // If a level background is missing, fall back to the level 1 image instead of crashing.
            if (!Gdx.files.internal(file).exists()) {
                Gdx.app.log("GameResources", "Missing " + file + ", using level 1 background");
                file = LevelConfig.get(0).backgroundFile;
            }
            backgrounds[i] = new Texture(file);
        }
    }

    @Override
    public void dispose() {
        cat.dispose();
        liquidCat.dispose();
        fish.dispose();
        bone.dispose();
        mysteryBox.dispose();
        heartFull.dispose();
        heartEmpty.dispose();

        for (Texture t : backgrounds) t.dispose();

        boneSound.dispose();
        mysteryBoxSound.dispose();
        theme.dispose();

        font.dispose();
    }
}
