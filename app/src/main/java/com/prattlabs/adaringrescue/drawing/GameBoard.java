package com.prattlabs.adaringrescue.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;

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
    private static final int NUM_OF_STARS = 25;

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
}
