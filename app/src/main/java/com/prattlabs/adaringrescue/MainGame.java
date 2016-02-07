package com.prattlabs.adaringrescue;

import android.app.Activity;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.prattlabs.adaringrescue.actors.Baddie;
import com.prattlabs.adaringrescue.actors.Player;
import com.prattlabs.adaringrescue.drawing.GameBoard;

import java.util.Random;

public class MainGame extends Activity implements OnClickListener {
    private static final int FRAME_RATE = 20; //50 frames per second
    private Handler frame = new Handler();
    private Player player;
    private Baddie baddie;
    //acceleration flag
    private boolean isAccelerating = false;
    private GameBoard mGameBoard;
    private Button mButton;
    private Runnable frameUpdate = new Runnable() {
        @Override
        synchronized public void run() {
            if (mGameBoard.wasCollisionDetected()) {
                Point collisionPoint =
                        mGameBoard.getLastCollision();
                if (collisionPoint.x >= 0) {
                    ((TextView) findViewById(R.id.the_other_label)).setText("Last Collision XY("
                            + Integer.toString(collisionPoint.x) + "," + Integer.toString(collisionPoint.y) + ")");
                }
                return;
            }
            frame.removeCallbacks(frameUpdate);
            //Add our call to increase or decrease velocity
            updateVelocity();
            //Display UFO speed
            ((TextView) findViewById(R.id.the_label)).setText("Sprite Acceleration("
                    + Integer.toString(player.getVelocity().x) + "," + Integer.toString(player.getVelocity().y) + ")");
            baddie.setLocation(baddie.getLocation().x + baddie.getVelocity().x, baddie.getLocation().y + baddie
                    .getVelocity().y);
            player.setLocation(player.getLocation().x + player.getVelocity().x, player.getLocation().y + player
                    .getVelocity().y);
            if (baddie.getLocation().x > baddie.getMaxX() || baddie.getLocation().x < 5) {
                baddie.getVelocity().x *= -1;
            }
            if (baddie.getLocation().y > baddie.getMaxY() || baddie.getLocation().y < 5) {
                baddie.getVelocity().y *= -1;
            }
            if (player.getLocation().x > player.getMaxX() || player.getLocation().x < 5) {
                player.getVelocity().x *= -1;
            }
            if (player.getLocation().y > player.getMaxY() || player.getLocation().y < 5) {
                player.getVelocity().y *= -1;
            }
            mGameBoard.setBaddieLocation(baddie.getLocation().x,
                    baddie.getLocation().y
            );
            mGameBoard.setPlayerLocation(player.getLocation().x, player.getLocation
                    ().y);
            mGameBoard.invalidate();
            frame.postDelayed(frameUpdate, FRAME_RATE);
        }
    };

    //Increase the velocity towards five or decrease
    //back to one depending on state
    private void updateVelocity() {
        int xDir = (player.getVelocity().x > 0) ? 1 : -1;
        int yDir = (player.getVelocity().y > 0) ? 1 : -1;
        int speed = 0;
        if (isAccelerating) {
            speed = Math.abs(player.getVelocity().x) + 1;
        } else {
            speed = Math.abs(player.getVelocity().x) - 1;
        }
        if (speed > 5)
            speed = 5;
        if (speed < 1)
            speed = 1;
        player.getVelocity().x = speed * xDir;
        player.getVelocity().y = speed * yDir;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        Handler h = new Handler();
        mGameBoard = ((GameBoard) findViewById(R.id.the_canvas));
        mButton = ((Button) findViewById(R.id.the_button));
        mButton.setOnClickListener(this);
        player = mGameBoard.getPlayer();
        baddie = mGameBoard.getBaddie();
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                initGfx();
            }
        }, 1000);
    }

    //Method for getting touch state—requires android 2.1 or greater
    @Override
    synchronized public boolean onTouchEvent(MotionEvent ev) {
        final int action = ev.getAction();
        switch (action & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                isAccelerating = true;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
                isAccelerating = false;
                break;
        }
        return true;
    }

    synchronized public void initGfx() {
        mGameBoard.resetStarField();
        Point randomPoint1, randomPoint2;
        do {
            randomPoint1 = getRandomPoint();
            randomPoint2 = getRandomPoint();
        } while (Math.abs(randomPoint1.x - randomPoint2.x) <
                mGameBoard.getBaddieWidth());
        mGameBoard.setBaddieLocation(randomPoint1.x, randomPoint1.y);
        mGameBoard.setPlayerLocation(randomPoint2.x, randomPoint2.y);
        baddie.setVelocity(getRandomVelocity());
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
        int x = 0;
        int y = 0;
        x = r.nextInt(maxX - minX + 1) + minX;
        y = r.nextInt(maxY - minY + 1) + minY;
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
    synchronized public void onClick(View v) {
        initGfx();
    }
}