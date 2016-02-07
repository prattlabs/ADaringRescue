package com.prattlabs.adaringrescue.actors;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by zppratt on 2/6/16.
 */
public class Player extends Actor {
    public Player(Context context, AttributeSet aSet, int pngId) {
        super(context, aSet, pngId);
    }

    public Player(Context context, AttributeSet aSet, int x, int y) {
        super(context, aSet, x , y);
    }
}
