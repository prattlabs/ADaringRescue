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

import com.prattlabs.adaringrescue.actors.Actor;
import com.prattlabs.adaringrescue.drawing.GameBoard;

import java.util.Random;
public class MainGame extends Activity implements OnClickListener{
    private Handler frame = new Handler();
    private Actor player;
    private Point baddieVelocity;
    private Point playerVelocity
    private int baddieMaxX;
    private int baddieMaxY;
    private int playerMaxX;
    private int playerMaxY;
    //acceleration flag
    private boolean isAccelerating = false;
    private static final int FRAME_RATE = 20; //50 frames per second

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
    //Increase the velocity towards five or decrease
    //back to one depending on state
    private void updateVelocity() {
        int xDir = (playerVelocity.x > 0) ? 1 : -1;
        int yDir = (playerVelocity.y > 0) ? 1 : -1;
        int speed = 0;
        if (isAccelerating) {
            speed = Math.abs(playerVelocity.x)+1;
        } else {
            speed = Math.abs(playerVelocity.x)-1;
        }
        if (speed>5) speed =5;
        if (speed<1) speed =1;
        playerVelocity.x=speed*xDir;
        playerVelocity.y=speed*yDir;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_game);
        player = new Actor();
        Handler h = new Handler();
        ((Button)findViewById(R.id.the_button)).setOnClickListener(this);
        h.postDelayed(new Runnable() {
            @Override
            public void run() {
                initGfx();
            }
        }, 1000);
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
        Random r = new Random();
        int minX = 0;
        int maxX = findViewById(R.id.the_canvas).getWidth() -
                ((GameBoard)findViewById(R.id.the_canvas)).getBaddieWidth();
        int x = 0;
        int minY = 0;
        int maxY = findViewById(R.id.the_canvas).getHeight() -
                ((GameBoard)findViewById(R.id.the_canvas)).getBaddieHeight();
        int y = 0;
        x = r.nextInt(maxX-minX+1)+minX;
        y = r.nextInt(maxY-minY+1)+minY;
        return new Point (x,y);
    }

    synchronized public void initGfx() {
        ((GameBoard)findViewById(R.id.the_canvas)).resetStarField();
        Point p1, p2;
        do {
            p1 = getRandomPoint();
            p2 = getRandomPoint();
        } while (Math.abs(p1.x - p2.x) <
                ((GameBoard)findViewById(R.id.the_canvas)).getBaddieWidth());
        ((GameBoard)findViewById(R.id.the_canvas)).setBaddie(p1.x, p1.y);
        ((GameBoard)findViewById(R.id.the_canvas)).setPlayer(p2.x, p2.y);
        baddieVelocity = getRandomVelocity();
        baddieMaxX = findViewById(R.id.the_canvas).getWidth() -
                ((GameBoard)findViewById(R.id.the_canvas)).getBaddieWidth();
        baddieMaxY = findViewById(R.id.the_canvas).getHeight() -
                ((GameBoard)findViewById(R.id.the_canvas)).getBaddieHeight();
        playerMaxX = findViewById(R.id.the_canvas).getWidth() -
                ((GameBoard)findViewById(R.id.the_canvas)).getPlayerWidth();
        playerMaxY = findViewById(R.id.the_canvas).getHeight() -
                ((GameBoard)findViewById(R.id.the_canvas)).getPlayerHeight();
        ((Button)findViewById(R.id.the_button)).setEnabled(true);
        frame.removeCallbacks(frameUpdate);
        ((GameBoard)findViewById(R.id.the_canvas)).invalidate();
        frame.postDelayed(frameUpdate, FRAME_RATE);
    }
    @Override
    synchronized public void onClick(View v) {
        initGfx();
    }

    private Runnable frameUpdate = new Runnable() {
        @Override
        synchronized public void run() {
            if (((GameBoard)findViewById(R.id.the_canvas)).wasCollisionDetected()) {
                Point collisionPoint =
                        ((GameBoard)findViewById(R.id.the_canvas)).getLastCollision();
                if (collisionPoint.x>=0) {
                    ((TextView)findViewById(R.id.the_other_label)).setText("Last Collision XY("
                            +Integer.toString(collisionPoint.x)+","+Integer.toString(collisionPoint.y)+")");
                }
                return;
            }
            frame.removeCallbacks(frameUpdate);
            //Add our call to increase or decrease velocity
            updateVelocity();
            //Display UFO speed
            ((TextView)findViewById(R.id.the_label)).setText("Sprite Acceleration("
                    +Integer.toString(playerVelocity.x)+","+Integer.toString(playerVelocity.y)+")");
            Point baddie = new Point
                    (((GameBoard)findViewById(R.id.the_canvas)).getBaddieX(),
                            ((GameBoard)findViewById(R.id.the_canvas)).getBaddieY()) ;
            Point player;
            baddie.x = baddie.x + baddieVelocity.x;
            if (baddie.x > baddieMaxX || baddie.x < 5) {
                baddieVelocity.x *= -1;
            }
            baddie.y = baddie.y + baddieVelocity.y;
            if (baddie.y > baddieMaxY || baddie.y < 5) {
                baddieVelocity.y *= -1;
            }
            player.x = player.x + playerVelocity.x;
            if (player.x > playerMaxX || player.x < 5) {
                playerVelocity.x *= -1;
            }
            player.y = player.y + playerVelocity.y;
            if (player.y > playerMaxY || player.y < 5) {
                playerVelocity.y *= -1;
            }
            ((GameBoard)findViewById(R.id.the_canvas)).setBaddie(baddie.x,
                    baddie.y);
            ((GameBoard)findViewById(R.id.the_canvas)).setPlayer(player.x, player.y);
            ((GameBoard)findViewById(R.id.the_canvas)).invalidate();
            frame.postDelayed(frameUpdate, FRAME_RATE);
        }
    };
}