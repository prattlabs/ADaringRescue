package com.prattlabs.adaringrescue;

import com.badlogic.gdx.Game;

/**
 * Created by zppratt on 1/23/16.
 */
public class MainMenuGame extends Game {
    @Override
    public void create() {
        setScreen(new MenuScreen(this));
    }
}
