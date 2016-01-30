package com.prattlabs.adaringrescue;

import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.prattlabs.adaringrescue.drawing.GameBoard;

import java.util.Random;

/**
 * Created by zppratt on 1/29/16.
 * Based on a tutorial by William J. Francis
 * http://www.techrepublic.com/blog/software-engineer/the-abcs-of-android-game-development-prepare-the-canvas/
 */
public class MainGame extends AppCompatActivity implements OnClickListener {

    private Handler frame = new Handler();

    // Divide the frame by 1000 to calculate how many times per second the screen will update.
    private static final int FRAME_RATE = 20; // 50 fps

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        Handler h = new Handler();
        getButton().setOnClickListener(this);

        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                initGfx();
            }
        }, 1000); // Wait 1 second for the layout manager to run before initializing the graphics.
    }

    private Point getRandomPoint() {
        System.err.println("getRandomPoint()");
        Random r = new Random();
        int minX = 0;
        int maxX = getCanvas().getWidth() - getCanvas().getSprite1Width();
        int x = 0;
        int minY = 0;
        int maxY = getCanvas().getHeight() - getCanvas().getSprite1Height();
        int y = 0;
        x = r.nextInt(maxX-minX+1)+minX;
        y = r.nextInt(maxY-minY+1)+minY;
        return new Point (x,y);
    }

    /**
     * Initialize the graphics.
     */
    synchronized public void initGfx() {
        Log.i("initGfx", "initGfx");
        getCanvas().resetStarField();
        //Select two random points for our initial sprite placement.
        //The loop is just to make sure we don't accidentally pick
        //two points that overlap.
        Point p1, p2;
        do {
            p1 = getRandomPoint();
            p2 = getRandomPoint();
        } while (Math.abs(p1.x - p2.x) < getCanvas().getSprite1Width());
        getCanvas().setSprite1(p1);
        getCanvas().setSprite2(p2);
        getButton().setEnabled(true);
        // Remove callbacks to keep from stacking up over time.
        frame.removeCallbacks(frameUpdate);
        frame.postDelayed(frameUpdate, FRAME_RATE);
    }

    @Override
    synchronized public void onClick(View v) {
        initGfx();
    }

    private Runnable frameUpdate = new Runnable() {
        @Override
        public void run() {
            frame.removeCallbacks(frameUpdate);

            // Update objects on screen, refresh canvas
            getCanvas().invalidate();
            frame.postDelayed(frameUpdate, FRAME_RATE);
        }
    };

    /**
     * Retrieves the game's button object for readability.
     */
    private Button getButton() {
        return getButton();
    }

    /**
     * Retrieves the game board for readability.
     */
    private GameBoard getCanvas() {
        return getCanvas();
    }

}
