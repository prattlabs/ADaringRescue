package com.prattlabs.adaringrescue.actors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.prattlabs.adaringrescue.drawing.GameBoard;

/**
 * Created by zppratt on 1/28/16.
 */
public class Actor extends View {

    private static final int BMP_ROWS = 4;
    private static final int BMP_COLUMNS = 3;
    Point location;
    Point velocity;
    Bitmap bitmap;
    Bitmap bmp;
    Rect bounds;
    private int width;
    private int height;
    int maxX;
    int maxY;

    public Actor(Context context, AttributeSet aSet) {
        super(context, aSet);
    }

    public Actor(Context context, AttributeSet aSet, GameBoard canvas, int pngId) {
        this(context, aSet, 20, 20);
        bitmap = BitmapFactory.decodeResource(getResources(), pngId);
        bounds = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        this.width = bmp.getWidth() / BMP_COLUMNS;
        this.height = bmp.getHeight() / BMP_ROWS;
    }

    public Actor(Context context, AttributeSet aSet, int x, int y) {
        super(context, aSet);
        location = new Point(x, y);
        bounds = new Rect(0, 0, 0, 0);
        velocity = new Point(2, 2);
    }

    public Rect getBounds() {
        return bounds;
    }

    public void setBounds(Rect bounds) {
        this.bounds = bounds;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void updateLocation() {
        setLocation(getLocation().x + getVelocity().x, getLocation().y + getVelocity().y);
Log.e("Event", "maxX = " + maxX);
        Log.e("Event", "maxY = " + maxY);
        if (maxX != 0 && maxY != 0) {
            if (getLocation().x > maxX || getLocation().x < 5) {
                getVelocity().x *= -1;
            }
            if (getLocation().y > maxY || getLocation().y < 5) {
                getVelocity().y *= -1;
            }
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

    public int getMaxX() {
        return maxX;
    }

    public void setMaxX(int maxX) {
        this.maxX = maxX;
    }

    public int getMaxY() {
        return maxY;
    }

    public void setMaxY(int maxY) {
        this.maxY = maxY;
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

    public void setVelocity(int x, int y) {
        this.velocity = new Point(x, y);
    }
}
