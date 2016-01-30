package com.prattlabs.adaringrescue;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.prattlabs.adaringrescue.drawing.GameBoard;

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

    /**
     * Initialize the graphics.
     */
    synchronized public void initGfx() {
        getCanvas().resetStarField();
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
        return ((Button)findViewById(R.id.the_button));
    }

    /**
     * Retrieves the game board for readability.
     */
    private GameBoard getCanvas() {
        return ((GameBoard)findViewById(R.id.the_canvas));
    }

}
