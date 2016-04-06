package com.prattlabs.adaringrescue.actors;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PointF;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.View;

import com.prattlabs.adaringrescue.actors.anim.Animation;
import com.prattlabs.adaringrescue.drawing.GameBoard;
import com.prattlabs.adaringrescue.drawing.SpriteMap;

import java.util.HashMap;
import java.util.Map;

import static com.prattlabs.adaringrescue.Constants.WALK_DOWN;
import static com.prattlabs.adaringrescue.Constants.WALK_LEFT;
import static com.prattlabs.adaringrescue.Constants.WALK_RIGHT;
import static com.prattlabs.adaringrescue.Constants.WALK_UP;
import static java.lang.Math.abs;
import static java.lang.Math.round;

public class Actor extends View {


    private PointF location;
    private PointF velocity;
    private Bitmap bitmap;
    private RectF bounds;
    private SpriteMap map;
    private Map<String, Animation> animations;
    private Animation currentAnimation;

    public Actor(Context context, AttributeSet aSet) {
        super(context, aSet);
    }

    public Actor(Context context, AttributeSet aSet, int pngId) {
        super(context, aSet);
        bitmap = BitmapFactory.decodeResource(getResources(), pngId);
        map = new SpriteMap(bitmap, 4, 3);
        setupAnimations();

        // TODO delete?
        bounds = new RectF(0, 0, bitmap.getWidth() / 3, bitmap.getHeight() / 4);
        location = new PointF(-100F, -100F);
        velocity = new PointF(2F, 2F);
    }

    private void setupAnimations() {
        animations = new HashMap<>();
        animations.put(WALK_DOWN, new Animation(map.getRow(0)).setSpeed(5));
        animations.put(WALK_LEFT, new Animation(map.getRow(1)).setSpeed(5));
        animations.put(WALK_RIGHT, new Animation(map.getRow(2)).setSpeed(5));
        animations.put(WALK_UP, new Animation(map.getRow(3)).setSpeed(5));
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

    /**
     * Based on what direction the player is going, create the next frame and draw it.
     *
     * @param canvas The canvas to draw to.
     */
    public void drawActor(Canvas canvas) {
        // If the player is moving more sideways then vertical play a sideways animation,
        // otherwise play a vertical animation.
        if (abs(getVelocity().x) > abs(getVelocity().y)) {
            // If player is walking left
            if (getVelocity().x < 0)
                currentAnimation = animations.get(WALK_LEFT);
            // If player is walking right
            else
                currentAnimation = animations.get(WALK_RIGHT);
        } else {
            // If player is walking down
            if (getVelocity().y < 0)
                currentAnimation = animations.get(WALK_UP);
            // If player is walking up
            else
                currentAnimation = animations.get(WALK_DOWN);
        }
        canvas.drawBitmap(getBitmap(), source(), destination(), null);
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    private RectF destination() {
        return new RectF(getLocation().x,
                getLocation().y,
                getLocation().x + 75,
                getLocation().y + 75
        );
    }

    @NonNull
    private Rect source() {
        RectF nextFrame = currentAnimation.getNextFrame();
        return new Rect(round(nextFrame.left),
                round(nextFrame.top),
                round(nextFrame.right),
                round(nextFrame.bottom)
        );
    }

    public RectF getBounds() {
        return bounds;
    }
}
