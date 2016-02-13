package com.prattlabs.adaringrescue;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.VelocityTrackerCompat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.prattlabs.adaringrescue.actors.Actor;
import com.prattlabs.adaringrescue.drawing.GameBoard;

import java.util.Random;

import static com.prattlabs.adaringrescue.Constants.FRAME_RATE;

public class MainGame extends Activity implements OnClickListener {
    private static final String DEBUG_TAG = "Velocity";
    private Handler frame = new Handler();
    private Actor player;
    private Actor baddie;
    private boolean isAccelerating = false;
    private GameBoard mGameBoard;
    private Button mButton;
    private VelocityTracker mVelocityTracker = null;

    private Runnable frameUpdate = new Runnable() {
        @Override
        synchronized public void run() {
            if (mGameBoard.wasCollisionDetected()) {
                Point collisionPoint =
                        mGameBoard.getLastCollision();
                if (collisionPoint.x >= 0) {
                    ((TextView) findViewById(R.id.the_other_label)).setText(
                            new StringBuilder()
                                    .append("Last Collision XY(")
                                    .append(Integer.toString(collisionPoint.x))
                                    .append(",")
                                    .append(Integer.toString(collisionPoint.y))
                                    .append(")")
                    );
                }
                return;
            }
            frame.removeCallbacks(frameUpdate);
            //Add our call to increase or decrease velocity
            if (player != null) {
                player.updateVelocity(isAccelerating);
                //Display UFO speed
                ((TextView) findViewById(R.id.the_label)).setText(
                        String.format("Sprite Acceleration(%d, %d), Pos(%d, %d)",
                                player.getVelocity().x,
                                player.getVelocity().y,
                                player.getLocation().x,
                                player.getLocation().y
                        )
                );
                player.updateLocation();
            }
            if (baddie != null) {
                baddie.updateLocation();
            }
            mGameBoard.invalidate();
            frame.postDelayed(frameUpdate, FRAME_RATE);
        }
    };
    private Intent musicService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        Handler h = new Handler();
        mGameBoard = ((GameBoard) findViewById(R.id.the_canvas));
        mButton = ((Button) findViewById(R.id.the_button));
        mButton.setOnClickListener(this);
        musicService = new Intent(this, BGMusicService.class);
        startService(musicService); //OR stopService(svc);
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                initGfx();
            }
        }, 1000);
    }

    synchronized public void initGfx() {
        player = mGameBoard.getPlayer();
        baddie = mGameBoard.getBaddie();
        mGameBoard.resetStarField();
        Point randomPoint1, randomPoint2;
        do {
            randomPoint1 = getRandomPoint();
            randomPoint2 = getRandomPoint();
        } while (Math.abs(randomPoint1.x - randomPoint2.x) <
                mGameBoard.getBaddieWidth());
        mGameBoard.setBaddieLocation(randomPoint1.x, randomPoint1.y);
        mGameBoard.setPlayerLocation(randomPoint2.x, randomPoint2.y);
        if (baddie != null) {
            baddie.setVelocity(getRandomVelocity());
        }
        findViewById(R.id.the_button).setEnabled(true);
        frame.removeCallbacks(frameUpdate);
        mGameBoard.invalidate();
        frame.postDelayed(frameUpdate, FRAME_RATE);
    }

    @Override
    protected void onPause() {
        super.onPause();
        startService(musicService);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(musicService);
    }

    private Point getRandomPoint() {
        Random r = new Random();
        int minX = 0;
        int minY = 0;
        int maxX = mGameBoard.getWidth() - mGameBoard.getBaddieWidth();
        int maxY = mGameBoard.getHeight() - mGameBoard.getBaddieHeight();
        int x = r.nextInt(maxX - minX + 1) + minX;
        int y = r.nextInt(maxY - minY + 1) + minY;
        return new Point(x, y);
    }

    private Point getRandomVelocity() {
        Random r = new Random();
        int min = 1;
        int max = 5;
        int x = r.nextInt(max - min + 1) + min;
        int y = r.nextInt(max - min + 1) + min;
        return new Point(x, y);
    }

    //Method for getting touch state—requires android 2.1 or greater
    @Override
    synchronized public boolean onTouchEvent(MotionEvent event) {
        int index = event.getActionIndex();
        int action = event.getActionMasked();
        int pointerId = event.getPointerId(index);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                if (mVelocityTracker == null) {
                    // Retrieve a new VelocityTracker object to watch the velocity of a motion.
                    mVelocityTracker = VelocityTracker.obtain();
                } else {
                    // Reset the velocity tracker back to its initial state.
                    mVelocityTracker.clear();
                }
                // Add a user's movement to the tracker.
                mVelocityTracker.addMovement(event);
                break;
            case MotionEvent.ACTION_MOVE:
                mVelocityTracker.addMovement(event);
                // When you want to determine the velocity, call
                // computeCurrentVelocity(). Then call getXVelocity()
                // and getYVelocity() to retrieve the velocity for each pointer ID.
                mVelocityTracker.computeCurrentVelocity(1000);
                // Log velocity of pixels per second
                // Best practice to use VelocityTrackerCompat where possible.
                Log.e("", "X velocity: " +
                        VelocityTrackerCompat.getXVelocity(mVelocityTracker,
                                pointerId));
                Log.e("", "Y velocity: " +
                        VelocityTrackerCompat.getYVelocity(mVelocityTracker,
                                pointerId));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                // Return a VelocityTracker object back to be re-used by others.
                mVelocityTracker.recycle();
                mVelocityTracker = null;
                break;
        }
        return true;
    }

    @Override
    synchronized public void onClick(View v) {
        initGfx();
    }
}