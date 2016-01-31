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

    //Velocity includes the speed and the direction of our sprite motion
    private Point sprite1Velocity;
    private Point sprite2Velocity;
    private int sprite1MaxX;
    private int sprite1MaxY;
    private int sprite2MaxX;
    private int sprite2MaxY;

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

    private Point getRandomVelocity() {
        Random r = new Random();
        int min = 1;
        int max = 5;
        int x = r.nextInt(max-min+1)+min;
        int y = r.nextInt(max-min+1)+min;
        return new Point (x,y);
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
        getCanvas().setSprite1(p1.x, p1.y);
        getCanvas().setSprite2(p2.x, p2.y);

        //Give the asteroid a random velocity
        sprite1Velocity = getRandomVelocity();
        //Fix the ship velocity at a constant speed for now
        sprite2Velocity = new Point(1,1);

        sprite1MaxX = findViewById(R.id.the_canvas).getWidth() -
                ((GameBoard)findViewById(R.id.the_canvas)).getSprite1Width();
        sprite1MaxY = findViewById(R.id.the_canvas).getHeight() -
                ((GameBoard)findViewById(R.id.the_canvas)).getSprite1Height();
        sprite2MaxX = findViewById(R.id.the_canvas).getWidth() -
                ((GameBoard)findViewById(R.id.the_canvas)).getSprite2Width();
        sprite2MaxY = findViewById(R.id.the_canvas).getHeight() -
                ((GameBoard)findViewById(R.id.the_canvas)).getSprite2Height();

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

            //First get the current positions of both sprites
            Point sprite1 = new Point
                    (((GameBoard)findViewById(R.id.the_canvas)).getSprite1X(),
                            ((GameBoard)findViewById(R.id.the_canvas)).getSprite1Y()) ;
            Point sprite2 = new Point
                    (((GameBoard)findViewById(R.id.the_canvas)).getSprite2X(),
                            ((GameBoard)findViewById(R.id.the_canvas)).getSprite2Y());
            //Now calc the new positions.
            //Note if we exceed a boundary the direction of the velocity gets reversed.
            sprite1.x = sprite1.x + sprite1Velocity.x;
            if (sprite1.x > sprite1MaxX || sprite1.x < 5) {
                sprite1Velocity.x *= -1;
            }
            sprite1.y = sprite1.y + sprite1Velocity.y;
            if (sprite1.y > sprite1MaxY || sprite1.y < 5) {
                sprite1Velocity.y *= -1;
            }
            sprite2.x = sprite2.x + sprite2Velocity.x;
            if (sprite2.x > sprite2MaxX || sprite2.x < 5) {
                sprite2Velocity.x *= -1;
            }
            sprite2.y = sprite2.y + sprite2Velocity.y;
            if (sprite2.y > sprite2MaxY || sprite2.y < 5) {
                sprite2Velocity.y *= -1;
            }
            ((GameBoard)findViewById(R.id.the_canvas)).setSprite1(sprite1.x,
                    sprite1.y);
            ((GameBoard)findViewById(R.id.the_canvas)).setSprite2(sprite2.x, sprite2.y);

            // Update objects on screen, refresh canvas
            getCanvas().invalidate();
            frame.postDelayed(frameUpdate, FRAME_RATE);
        }
    };

    /**
     * Retrieves the game's button object for readability.
     */
    private Button getButton() {
        return ((Button)findViewById(R.id.the_button));
    }

    /**
     * Retrieves the game board for readability.
     */
    private GameBoard getCanvas() {
        return ((GameBoard)findViewById(R.id.the_canvas));
    }

}
