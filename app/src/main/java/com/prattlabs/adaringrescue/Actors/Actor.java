package com.prattlabs.adaringrescue.actors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by zppratt on 1/28/16.
 */
public class Actor extends View {

    Point location;
    Point velocity;
    Bitmap bitmap;
    Rect bounds;
    int maxX;
    int maxY;

    public Actor(Context context, AttributeSet aSet) {
        super(context, aSet);
    }

    public Actor(Context context, AttributeSet aSet, int pngId) {
        super(context, aSet);
        bitmap = BitmapFactory.decodeResource(getResources(), pngId);
        location = new Point(0, 0);
        bounds = new Rect(0, 0, bitmap.getWidth() / 3, bitmap.getHeight() / 4);
        velocity = new Point(2, 2);
        maxX = 420;
        maxY = 500;
    }

    public Rect getBounds() {
        return bounds;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void updateLocation() {
        bounceOffSideOfCanvas();
        setLocation(getLocation().x + getVelocity().x, getLocation().y + getVelocity().y);
    }

    private void bounceOffSideOfCanvas() {
        if (getLocation().x > maxX || getLocation().x < 5) {
            setVelocity(getVelocity().x *= -1, getVelocity().y);
        }
        if (getLocation().y > maxY || getLocation().y < 5) {
            setVelocity(getVelocity().x, getVelocity().y *= -1);
        }
    }

    public void setLocation(int x, int y) {
        this.location = new Point(x, y);
    }

    public Point getLocation() {
        return location;
    }

    public Point getVelocity() {
        return velocity;
    }

    public void setVelocity(Point point) {
        this.velocity = point;
    }

    public void setVelocity(int x, int y) {
        this.velocity = new Point(x, y);
    }

    public void updateVelocity(boolean isAccelerating) {
        //Increase the velocity towards five or decrease
        //back to one depending on state
        int xDir = (getVelocity().x > 0) ? 1 : -1;
        int yDir = (getVelocity().y > 0) ? 1 : -1;
        int speed;
        if (isAccelerating) {
            speed = Math.abs(getVelocity().x) + 1;
        } else {
            speed = Math.abs(getVelocity().x) - 1;
        }
        if (speed > 5)
            speed = 5;
        if (speed < 1)
            speed = 1;
        setVelocity(speed * xDir, speed * yDir);
    }
}
