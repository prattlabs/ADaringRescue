package com.prattlabs.adaringrescue.actors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zppratt on 1/28/16.
 */
public class Actor extends View {

    private Point location;
    private Point velocity;
    private Bitmap bitmap;

    public Actor(Context context, AttributeSet aSet, int pngId) {
        super(context);
        setVelocity(-1, -1);
        bitmap = BitmapFactory.decodeResource(getResources(), pngId);
    }

    public Actor(Context context, AttributeSet aSet, int x, int y) {
        super(context, aSet);
        this.location = new Point(x, y);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public Point getVelocity() {
        return velocity;
    }

    public void setVelocity(int x, int y) {
        this.velocity = new Point(x, y);
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(int x, int y) {
        this.location = new Point(x, y);
    }
}
