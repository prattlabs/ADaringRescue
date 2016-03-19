package com.prattlabs.adaringrescue.actors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.prattlabs.adaringrescue.drawing.GameBoard;

public class Actor extends View {

    PointF location;
    PointF velocity;
    Bitmap bitmap;
    RectF bounds;

    public Actor(Context context, AttributeSet aSet) {
        super(context, aSet);
    }

    public Actor(Context context, AttributeSet aSet, int pngId) {
        super(context, aSet);
        bitmap = BitmapFactory.decodeResource(getResources(), pngId);
        location = new PointF(-100F, -100F);
        bounds = new RectF(0, 0, bitmap.getWidth() / 3, bitmap.getHeight() / 4);
        velocity = new PointF(2F, 2F);
    }

    public RectF getBounds() {
        return bounds;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void updateLocation(GameBoard gameBoard) {
        bounceOffSideOfCanvas(gameBoard);
        setLocation(getLocation().x + getVelocity().x, getLocation().y + getVelocity().y);
    }

    private void bounceOffSideOfCanvas(GameBoard gameBoard) {
        if (getLocation().x > gameBoard.getCanvas().getWidth() - bitmap.getWidth() / 2 || getLocation().x < 5) {
            setVelocity(getVelocity().x *= -1, getVelocity().y);
        }
        if (getLocation().y > gameBoard.getCanvas().getHeight() - bitmap.getHeight() / 2 || getLocation().y < 5) {
            setVelocity(getVelocity().x, getVelocity().y *= -1);
        }
    }

    public void setLocation(float x, float y) {
        this.location = new PointF(x, y);
    }

    public PointF getLocation() {
        return location;
    }

    public PointF getVelocity() {
        return velocity;
    }

    public void setVelocity(float x, float y) {
        this.velocity = new PointF(x, y);
    }

    public void moveToDest(float destX, float destY) {

    }
}
