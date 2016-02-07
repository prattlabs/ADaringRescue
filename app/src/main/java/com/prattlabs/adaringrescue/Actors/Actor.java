package com.prattlabs.adaringrescue.actors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.prattlabs.adaringrescue.R;

/**
 * Created by zppratt on 1/28/16.
 */
public class Actor extends View {

    protected Point location;
    protected Point velocity;
    protected Bitmap bitmap;
    protected Rect bounds;
    protected int maxX;
    protected int maxY;

    public Actor(Context context, AttributeSet aSet) {
        super(context, aSet);
    }

    public Actor(Context context, AttributeSet aSet, int pngId) {
        this(context, aSet, -1, -1);
        bitmap = BitmapFactory.decodeResource(getResources(), pngId);
        bounds = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    public Actor(Context context, AttributeSet aSet, int x, int y) {
        super(context, aSet);
        this.location = new Point(x, y);
        bounds = new Rect(0, 0, 0, 0);
        maxX = findViewById(R.id.the_canvas).getWidth() - getWidth();
        maxY = findViewById(R.id.the_canvas).getHeight() - getHeight();
        velocity = new Point(1,1);
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

    public Point getVelocity() {
        return velocity;
    }

    public void setVelocity(int x, int y) {
        this.velocity = new Point(x, y);
    }

    public void setVelocity(Point point) {
        this.velocity = point;
    }

    public Point getLocation() {
        return location;
    }

    public void setLocation(int x, int y) {
        this.location = new Point(x, y);
    }
}
