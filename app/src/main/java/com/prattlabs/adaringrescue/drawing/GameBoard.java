package com.prattlabs.adaringrescue.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.prattlabs.adaringrescue.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static android.graphics.Color.BLACK;
import static android.graphics.Color.CYAN;

/**
 * Created by zppratt on 1/29/16.
 * Based on a tutorial by William J. Francis
 * http://www.techrepublic.com/blog/software-engineer/the-abcs-of-android-game-development-prepare-the-canvas/
 */
public class GameBoard extends View {
    private final Paint p;
    private List<Point> starField = null;
    private int starAlpha = 80;
    private int starFade = 2;
    private Rect sprite1Bounds = new Rect(0,0,0,0);
    private Rect sprite2Bounds = new Rect(0,0,0,0);
    private Point sprite1;
    private Point sprite2;
    private Bitmap bm1 = null;
    private Bitmap bm2 = null;
    private static final int NUM_OF_STARS = 25;

    synchronized public void setSprite1(Point p) {
        sprite1=p;
    }
    synchronized public Point getSprite1() {
        return sprite1;
    }

    synchronized public void setSprite2(Point p) {
        sprite2=p;
    }
    synchronized public Point getSprite2() {
        return sprite2;
    }

    synchronized public int getSprite1Width() {
        return sprite1Bounds.width();
    }
    synchronized public int getSprite1Height() {
        return sprite1Bounds.height();
    }

    synchronized public int getSprite2Width() {
        return sprite2Bounds.width();
    }
    synchronized public int getSprite2Height() {
        return sprite2Bounds.height();
    }

    /**
     * Resets the starfield
     */
    synchronized public void resetStarField() {
        starField = null;
    }

    /**
     * Default constructor
     * @param context the Android application context of the game
     * @param aSet the Android application attributes
     */
    public GameBoard(Context context, AttributeSet aSet) {
        super(context, aSet);
        p = new Paint();
        sprite1 = new Point(-1,-1);
        sprite2 = new Point(-1,-1);
        bm1 = BitmapFactory.decodeResource(getResources(), R.drawable.asteroid);
        bm2 = BitmapFactory.decodeResource(getResources(), R.drawable.ufo);
        sprite1Bounds = new Rect(0,0, bm1.getWidth(), bm1.getHeight());
        sprite2Bounds = new Rect(0,0, bm2.getWidth(), bm2.getHeight());
    }

    /**
     * Creates the starfield, setting each star into position
     * @param maxX the rightmost edge of the screen? // TODO
     * @param maxY the bottom-most edge of the screen? //TODO
     */
    private void initializeStars(int maxX, int maxY) {
        starField = new ArrayList<>();
        for (int i = 0; i < NUM_OF_STARS; i++) {
            Random r = new Random();
            int x = r.nextInt(maxX-5+1)+5;
            int y = r.nextInt(maxY-5+1)+5;
            starField.add(new Point(x, y));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // Create a blank canvas
        p.setColor(BLACK);
        p.setAlpha(255);
        p.setStrokeWidth(1);
        canvas.drawRect(0, 0, getWidth(), getHeight(), p);
        drawStars(canvas);
        drawSprites(canvas);
    }

    private void drawStars(Canvas canvas) {
        // Initialize the starfield, if needed, and draw the stars
        if (starField==null) {
            initializeStars(canvas.getWidth(),
                    canvas.getHeight());
        }
        p.setColor(CYAN);
        p.setAlpha(starAlpha += starFade);
        // Fade the stars in and out
        if (starAlpha >= 252 || starAlpha <= 80) {
            starFade = starFade * -1;
        }
        p.setStrokeWidth(5);
        for (int i = 0; i < NUM_OF_STARS; i++) {
            canvas.drawPoint(starField.get(i).x, starField.get(i).y, p);
        }
    }

    private void drawSprites(Canvas canvas) {
        if (sprite1.x>=0) {
            canvas.drawBitmap(bm1, sprite1.x, sprite1.y, null);
        }
        if (sprite2.x>=0) {
            canvas.drawBitmap(bm2, sprite2.x, sprite2.y, null);
        }
    }

}
