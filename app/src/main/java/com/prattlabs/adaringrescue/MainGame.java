package com.prattlabs.adaringrescue;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.view.GestureDetectorCompat;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.prattlabs.adaringrescue.actors.Actor;
import com.prattlabs.adaringrescue.drawing.GameBoard;

import java.util.ArrayList;
import java.util.Random;
import java.util.Set;

import static android.view.GestureDetector.OnGestureListener;
import static android.view.View.OnClickListener;
import static com.prattlabs.adaringrescue.Constants.BULLET_SPEED;
import static com.prattlabs.adaringrescue.Constants.FRAME_RATE;
import static com.prattlabs.adaringrescue.Constants.TIME_TO_DELETE_BULLET;

public class MainGame extends Activity implements OnClickListener, OnGestureListener {
    private Handler frame = new Handler();
    private Actor player;
    private Set<Actor> enemies;
    private Actor bullet;
    private GameBoard mGameBoard;
    private Button mButton;
    private GestureDetectorCompat mDetector;
    private Runnable frameUpdate = new FrameUpdate();
    private Intent musicService;
    private long instantFired;

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
        }, 1);
    }

    private void startMusic() {
        if (musicService == null) {
            musicService = new Intent(this, BGMusicService.class);
        }
        startService(musicService);
    }

    synchronized public void initGfx() {
        mGameBoard.resetRocksAndTrees();

        // Initialize player state
        player = mGameBoard.getPlayer();
        player.setVelocity(1, 1);

        // Initialize bad guy states
        enemies = mGameBoard.getEnemies();
        PointF randomPoint1, randomPoint2;
        randomPoint1 = getRandomPoint();
        mGameBoard.setActorLocation(player, randomPoint1.x, randomPoint1.y);
        for (Actor enemy : enemies) {
            do {
                randomPoint1 = getRandomPoint();
                randomPoint2 = getRandomPoint();
                enemy.setLocation(randomPoint2.x, randomPoint2.y);
                PointF velocity = getRandomVelocity();
                enemy.setVelocity(velocity.x, velocity.y);
            } while (Math.abs(randomPoint1.x - randomPoint2.x) < mGameBoard.getBaddieWidth());
        }

        // Initialize bullet state
        bullet = mGameBoard.getBullet();
        bullet.setLocation(-100, -100);
        bullet.setVelocity(0, 0);

        findViewById(R.id.the_button).setEnabled(true);
        frame.removeCallbacks(frameUpdate);
        mGameBoard.invalidate();
        frame.postDelayed(frameUpdate, FRAME_RATE);
    }

    private PointF getRandomPoint() {
        float maxX;
        float maxY;
        do {
            maxX = mGameBoard.getWidth() - mGameBoard.getBaddieWidth();
            maxY = mGameBoard.getHeight() - mGameBoard.getBaddieHeight();
            try {
                Thread.sleep(100);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } while (maxX <= 0 || maxY < 0);
        Random r = new Random();
        float x = r.nextInt(((int) maxX) + 1);
        float y = r.nextInt(((int) maxY) + 1);

        return new PointF(x, y);
    }

    private PointF getRandomVelocity() {
        ArrayList<PointF> options = new ArrayList<>(4);
        options.add(new PointF(1, 1));
        options.add(new PointF(1, -1));
        options.add(new PointF(-1, 1));
        options.add(new PointF(-1, -1));
        return options.get((int) (4 * Math.random()));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        this.mDetector.onTouchEvent(event);
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

        if (System.currentTimeMillis() - instantFired > TIME_TO_DELETE_BULLET) {
            instantFired = System.currentTimeMillis();
        }

        // If the bullet is traveling on the screen, don't fire
        if (bullet.getLocation().x > 0) {
            return true;
        }

        // Set location of the bullet to the center of the player sprite
        bullet.setLocation(player.getLocation().x + player.getBounds().centerX(),
                player.getLocation().y + player.getBounds().centerY());

        // Set the target of the bullet
        bullet.setTarget(e.getX(), e.getY());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    /**
     * Basically just handles the event from the "reset" button.
     *
     * @param v the button or view to handle
     */
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
        int x = ((Float) velocityX).intValue() / 1000;
        int y = ((Float) velocityY).intValue() / 1000;
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
        if (player != null)
            player.setVelocity(x, y);
        return true;
    }

    private void vanishBullet() {
        bullet.setLocation(-100, -100);
        bullet.setVelocity(0, 0);
    }

    class FrameUpdate implements Runnable {
        @Override
        synchronized public void run() {

            //delete bullet after n seconds
            if (System.currentTimeMillis() - instantFired > TIME_TO_DELETE_BULLET &&
                    instantFired != 0) {
                vanishBullet();
                instantFired = 0;
            }

            if (mGameBoard.wasCollisionDetected()) {
                PointF collisionPoint = mGameBoard.getLastCollision();
                if (collisionPoint.x >= 0) {
                    ((TextView) findViewById(R.id.the_other_label)).setText(
                            new StringBuilder()
                                    .append("Last Collision XY(")
                                    .append(Float.toString(collisionPoint.x))
                                    .append(",")
                                    .append(Float.toString(collisionPoint.y))
                                    .append(")")
                    );
                }
                initGfx();
                // Lose life or game over
                return;
            }
            frame.removeCallbacks(this);
            if (player != null) {
                ((TextView) findViewById(R.id.the_label)).setText(
                        String.format("Bullet Vel (%.2f, %.2f), Pos(%.2f, %.2f)",
                                bullet.getVelocity().x,
                                bullet.getVelocity().y,
                                bullet.getLocation().x,
                                bullet.getLocation().y
                        )
                );
                player.updateLocation(mGameBoard);
            }
            for (Actor baddie : enemies) {
                if (baddie != null) {
                    baddie.updateLocation(mGameBoard);
                }
            }

            // Bullet reaches target
            if (bullet.getLocation().x >= bullet.getTarget().x - 10
                    && bullet.getLocation().x <= bullet.getTarget().x + 10) {
                vanishBullet();
            }

            // Bullet is out of bounds
            if (bullet.getLocation().x > mGameBoard.getWidth() || bullet.getLocation().x < 0 ||
                    bullet.getLocation().y > mGameBoard.getHeight() || bullet.getLocation().y < 0) {
                vanishBullet();
            }

            if (bullet != null && bullet.getLocation().x > 0) {
                // Temp variables
                float targetX = bullet.getTarget().x;
                float targetY = bullet.getTarget().y;
                float bulletX = bullet.getLocation().x;
                float bulletY = bullet.getLocation().y;

                targetX -= 52; // Game frame offset
                targetY -= 330;

                // Version of setX(getX() + (speed * Math.cos(direction)));
                float deltaX = targetX - bulletX;
                float deltaY = targetY - bulletY;
                float direction = (float) Math.atan2(deltaY, deltaX);
                float x = bulletX + BULLET_SPEED * (float) Math.cos(direction);
                float y = bulletY + BULLET_SPEED * (float) Math.sin(direction);
                bullet.setLocation(x, y);

                ((TextView) findViewById(R.id.the_other_label)).setText(
                        "Bullet Target = " + targetX + ", " + targetY
                );

            }
            mGameBoard.invalidate();
            frame.postDelayed(this, FRAME_RATE);
        }
    }
}