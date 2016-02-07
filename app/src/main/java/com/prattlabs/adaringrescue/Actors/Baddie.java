package com.prattlabs.adaringrescue.actors;

import android.content.Context;
import android.util.AttributeSet;

/**
 * Created by zppratt on 2/6/16.
 */
public class Baddie extends Actor {
    public Baddie(Context context, AttributeSet aSet, int pngId) {
        super(context, aSet, pngId);
    }
    public Baddie(Context context, AttributeSet attributeSet, int x, int y) {
        super(context, attributeSet, x, y);
    }
}
