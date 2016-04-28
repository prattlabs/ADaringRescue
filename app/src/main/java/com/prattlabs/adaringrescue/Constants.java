package com.prattlabs.adaringrescue;

import android.graphics.Color;

/**
 * Created by zppratt on 2/9/16.
 */
public interface Constants {
    int FRAME_RATE = 17; //1000/20 = 50 frames per second
    String WALK_DOWN= "WalkDown";
    String WALK_LEFT = "WalkLeft";
    String WALK_RIGHT = "WalkRight";
    String WALK_UP = "WalkUp";
    int NUM_ENEMIES = 5;
    int BACKGROUND_COLOR = Color.argb(100, 237, 201, 175);
    int BULLET_SPEED = 15;
    int BULLET_SIZE = 45;
    long TIME_TO_DELETE_BULLET=2000;
    int ACTOR_WIDTH = 75;
    int ACTOR_HEIGHT = 75;
}
