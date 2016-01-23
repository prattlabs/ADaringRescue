package com.prattlabs.adaringrescue;

import com.badlogic.gdx.Screen;

/**
 * Created by zppratt on 1/23/16.
 */
public class GameScreen implements Screen {
    @Override
    public void show() {
        System.err.println(getClass().getEnclosingMethod().getName());
    }

    @Override
    public void render(float delta) {
        System.err.println(getClass().getEnclosingMethod().getName());
    }

    @Override
    public void resize(int width, int height) {
        System.err.println(getClass().getEnclosingMethod().getName());
    }

    @Override
    public void pause() {
        System.err.println(getClass().getEnclosingMethod().getName());
    }

    @Override
    public void resume() {
        System.err.println(getClass().getEnclosingMethod().getName());
    }

    @Override
    public void hide() {
        System.err.println(getClass().getEnclosingMethod().getName());
    }

    @Override
    public void dispose() {
        System.err.println(getClass().getEnclosingMethod().getName());
    }
}
