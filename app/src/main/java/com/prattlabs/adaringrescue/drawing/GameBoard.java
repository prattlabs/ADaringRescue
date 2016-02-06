package com.prattlabs.adaringrescue.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.prattlabs.adaringrescue.R;
import com.prattlabs.adaringrescue.actors.Actor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class GameBoard extends View {
    private Paint p;
    private List<Point> starField = null;
    private int starAlpha = 80;
    private int starFade = 2;
    private Rect baddieBounds = new Rect(0,0,0,0);
    private Rect playerBounds = new Rect(0,0,0,0);
    private Actor baddie;
    private Actor player;
    Point playerLocation = player.getLocation();
    Point baddieLocation = baddie.getLocation();
    private Matrix m = null;
    AttributeSet aSet;
    //Collision flag and point
    private boolean collisionDetected = false;
    private Point lastCollision = new Point(-1,-1);
    private int baddieRotation = 0;

    private static final int NUM_OF_STARS = 25;
    //Allow our controller to get and set the sprite positions
    //sprite 1 setter
    synchronized public void setBaddie(int x, int y) {
        baddie = new Actor(getContext(), aSet,x, y);
    }
    //sprite 1 getter
    synchronized public int getBaddieX() {
        return baddie.getLocation().x;
    }

    synchronized public int getBaddieY() {
        return baddie.getLocation().y;
    }
    //sprite 2 setter
    synchronized public void setPlayer(int x, int y) {
        player.setLocation(x, y);
    }
    //sprite 2 getter
    synchronized public int getPlayerX() {
        return player.getLocation().x;
    }

    synchronized public int getPlayerY() {
        return player.getLocation().y;
    }

    synchronized public void resetStarField() {
        starField = null;
    }
    //expose sprite bounds to controller
    synchronized public int getBaddieWidth() {
        return baddieBounds.width();
    }

    synchronized public int getBaddieHeight() {
        return baddieBounds.height();
    }

    synchronized public int getPlayerWidth() {
        return playerBounds.width();
    }

    synchronized public int getPlayerHeight() {
        return playerBounds.height();
    }
    //return the point of the last collision
    synchronized public Point getLastCollision() {
        return lastCollision;
    }
    //return the collision flag
    synchronized public boolean wasCollisionDetected() {
        return collisionDetected;
    }

    public GameBoard(Context context, AttributeSet aSet) {
        super(context, aSet);
        this.aSet = aSet;
        p = new Paint();
        //load our bitmaps and set the bounds for the controller
        baddie = new Actor(context, aSet, R.drawable.asteroid);
        player = new Actor(context, aSet, R.drawable.ufo);
        //Define a matrix so we can rotate the asteroid
        m = new Matrix();
        p = new Paint();
        baddieBounds = new Rect(0,0, baddie.getBitmap().getWidth(), baddie.getBitmap().getHeight());
        playerBounds = new Rect(0,0, player.getBitmap().getWidth(), player.getBitmap().getHeight());
        playerLocation = player.getLocation();
        baddieLocation = baddie.getLocation();
    }

    synchronized private void initializeStars(int maxX, int maxY) {
        starField = new ArrayList<Point>();
        for (int i=0; i<NUM_OF_STARS; i++) {
            Random r = new Random();
            int x = r.nextInt(maxX-5+1)+5;
            int y = r.nextInt(maxY-5+1)+5;
            starField.add(new Point (x,y));
        }
        collisionDetected = false;
    }
    private boolean checkForCollision() {
        if (baddieLocation.x<0 && playerLocation.x<0 && baddieLocation.y<0 && playerLocation.y<0) return false;
        Rect r1 = new Rect(baddieLocation.x, baddieLocation.y, baddieLocation.x
                + baddieBounds.width(),  baddieLocation.y + baddieBounds.height());
        Rect r2 = new Rect(playerLocation.x, playerLocation.y, playerLocation.x +
                playerBounds.width(), playerLocation.y + playerBounds.height());
        Rect r3 = new Rect(r1);
        if(r1.intersect(r2)) {
            for (int i = r1.left; i<r1.right; i++) {
                for (int j = r1.top; j<r1.bottom; j++) {
                    if (baddie.getBitmap().getPixel(i - r3.left, j - r3.top)!=
                            Color.TRANSPARENT) {
                        if (player.getBitmap().getPixel(i - r2.left, j - r2.top) !=
                                Color.TRANSPARENT) {
                            lastCollision = new Point(playerLocation.x +
                                    i-r2.left, playerLocation.y + j-r2.top);
                            return true;
                        }
                    }
                }
            }
        }
        lastCollision = new Point(-1,-1);
        return false;
    }
    @Override
    synchronized public void onDraw(Canvas canvas) {
        p.setColor(Color.BLACK);
        p.setAlpha(255);
        p.setStrokeWidth(1);
        canvas.drawRect(0, 0, getWidth(), getHeight(), p);

        if (starField==null) {
            initializeStars(canvas.getWidth(), canvas.getHeight());
        }
        p.setColor(Color.CYAN);
        p.setAlpha(starAlpha+=starFade);
        if (starAlpha>=252 || starAlpha <=80) starFade=starFade*-1;
        p.setStrokeWidth(5);
        for (int i=0; i<NUM_OF_STARS; i++) {
            canvas.drawPoint(starField.get(i).x, starField.get(i).y, p);
        }

        if (baddieLocation.x>=0) {
            m.reset();
            m.postTranslate((float)(baddieLocation.x), (float)(baddieLocation.y));
            m.postRotate(baddieRotation,
                    (float)(baddieLocation.x+baddieBounds.width()/2.0),
                    (float)(baddieLocation.y+baddieBounds.width()/2.0));
            canvas.drawBitmap(baddie.getBitmap(), m, null);
            baddieRotation+=5;
            if (baddieRotation >= 360) baddieRotation=0;
        }
        if (playerLocation.x>=0) {
            canvas.drawBitmap(player.getBitmap(), playerLocation.x, playerLocation.y, null);
        }
        //The last order of business is to check for a collision
        collisionDetected = checkForCollision();
        if (collisionDetected ) {
            //if there is one lets draw a red X
            p.setColor(Color.RED);
            p.setAlpha(255);
            p.setStrokeWidth(5);
            canvas.drawLine(lastCollision.x - 5, lastCollision.y - 5,
                    lastCollision.x + 5, lastCollision.y + 5, p);
            canvas.drawLine(lastCollision.x + 5, lastCollision.y - 5,
                    lastCollision.x - 5, lastCollision.y + 5, p);
        }
    }
}