package com.prattlabs.adaringrescue;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.prattlabs.adaringrescue.actors.Actor;
import com.prattlabs.adaringrescue.drawing.GameBoard;

import java.util.Random;

import static android.view.GestureDetector.OnGestureListener;
import static android.view.View.OnClickListener;
import static com.prattlabs.adaringrescue.Constants.FRAME_RATE;

public class MainGame extends Activity implements OnClickListener, OnGestureListener {
    private Handler frame = new Handler();
    private Actor player;
    private Actor baddie;
    private boolean isAccelerating = false;
    private GameBoard mGameBoard;
    private Button mButton;
    private GestureDetectorCompat mDetector;
    private Runnable frameUpdate = new FrameUpdate();
    private Intent musicService;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        Handler h = new Handler();
        mGameBoard = ((GameBoard) findViewById(R.id.the_canvas));
        mDetector = new GestureDetectorCompat(this, this);
        mButton = ((Button) findViewById(R.id.the_button));
        mButton.setOnClickListener(this);
        startMusic();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                initGfx();
            }
        }, 1000);
    }

    private void startMusic() {
        if (musicService == null) {
            musicService = new Intent(this, BGMusicService.class);
        }
        //startService(musicService);
    }

    synchronized public void initGfx() {
        player = mGameBoard.getPlayer();
        player.setVelocity(1, 1);
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
            baddie.setVelocity(1, 1);
        }
        findViewById(R.id.the_button).setEnabled(true);
        frame.removeCallbacks(frameUpdate);
        mGameBoard.invalidate();
        frame.postDelayed(frameUpdate, FRAME_RATE);
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

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
        // Be sure to call the superclass implementation
        return super.onTouchEvent(event);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopService(musicService);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startMusic();
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    synchronized public void onClick(View v) {
        initGfx();
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int x = ((Float) velocityX).intValue() / 100;
        int y = ((Float) velocityY).intValue() / 100;
        int maxVel = 3;
        if (Math.abs(x) > maxVel) {
            if (x > 0) {
                x = maxVel;
            } else {
                x = -maxVel;
            }
        }
        if (Math.abs(y) > maxVel) {
            if (y > 0) {
                y = maxVel;
            } else {
                y = -maxVel;
            }
        }
        player.setVelocity(x, y);
        return false;
    }

    class FrameUpdate implements Runnable {
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
            frame.removeCallbacks(this);
            //Add our call to increase or decrease velocity
            if (player != null) {
                //                player.updateVelocity(isAccelerating);
                ((TextView) findViewById(R.id.the_label)).setText(
                        String.format("Sprite Acceleration(%d, %d), Pos(%d, %d)",
                                player.getVelocity().x,
                                player.getVelocity().y,
                                player.getLocation().x,
                                player.getLocation().y
                        )
                );
                player.updateLocation(mGameBoard);
            }
            if (baddie != null) {
                baddie.updateLocation(mGameBoard);
            }
            mGameBoard.invalidate();
            frame.postDelayed(this, FRAME_RATE);
        }
    }
}