package com.prattlabs.adaringrescue.drawing;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
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
    private static final int NUM_OF_STARS = 25;
    AttributeSet aSet;
    private Paint paintBrush;
    private List<Point> starField = null;
    private int starAlpha = 80;
    private int starFade = 2;
    private Actor player;
    private Actor enemy;
    private Rect baddieBounds;
    private Rect playerBounds;
    private Rect playerDst = new Rect();
    private Rect baddieDst = new Rect();
    private int canvasWidth;
    private int canvasHeight;
    //Collision flag and point
    private boolean collisionDetected = false;
    private Point lastCollision = new Point(-1, -1);

    public GameBoard(Context context, AttributeSet aSet) {
        super(context, aSet);
        this.aSet = aSet;
        paintBrush = new Paint();
        //Define a matrix so we can rotate the asteroid
        paintBrush = new Paint();

        canvasHeight = getHeight();
        canvasWidth = getWidth();

        //load our bitmaps and set the bounds for the controller
        enemy = new Actor(getContext(), aSet, R.drawable.baddie);
        player = new Actor(getContext(), aSet, R.drawable.player);

        baddieBounds = enemy.getBounds();
        playerBounds = player.getBounds();
    }

    public int getCanvasHeight() {
        return canvasHeight;
    }

    public void setCanvasHeight(int canvasHeight) {
        this.canvasHeight = canvasHeight;
    }

    public int getCanvasWidth() {
        return canvasWidth;
    }

    public void setCanvasWidth(int canvasWidth) {
        this.canvasWidth = canvasWidth;
    }

    public Actor getPlayer() {
        return player;
    }

    public Actor getBaddie() {
        return enemy;
    }

    //Allow our controller to get and set the sprite positions
    //sprite 1 setter
    synchronized public void setBaddieLocation(int x, int y) {
        enemy.setLocation(x, y);
    }

    //sprite 2 setter
    synchronized public void setPlayerLocation(int x, int y) {
        player.setLocation(x, y);
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

    @Override
    synchronized public void onDraw(Canvas canvas) {

        paintBrush.setColor(Color.BLACK);
        paintBrush.setAlpha(255);
        paintBrush.setStrokeWidth(1);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paintBrush);

        if (starField == null) {
            initializeStars(canvas.getWidth(), canvas.getHeight());
        }
        paintBrush.setColor(Color.CYAN);
        paintBrush.setAlpha(starAlpha += starFade);
        if (starAlpha >= 252 || starAlpha <= 80)
            starFade = starFade * -1;
        paintBrush.setStrokeWidth(5);
        for (int i = 0; i < NUM_OF_STARS; i++) {
            canvas.drawPoint(starField.get(i).x, starField.get(i).y, paintBrush);
        }
        playerDst.set(player.getLocation().x, player.getLocation().y,
                player.getLocation().x + 50, player.getLocation().y + 50);
        canvas.drawBitmap(player.getBitmap(), player.getBounds(), playerDst, null);
        baddieDst.set(enemy.getLocation().x, enemy.getLocation().y,
                enemy.getLocation().x + 50, enemy.getLocation().y + 50);
        canvas.drawBitmap(enemy.getBitmap(), enemy.getBounds(), baddieDst, null);
        //The last order of business is to check for a collision
        //        collisionDetected = checkForCollision();
        //        if (collisionDetected) {
        //            //if there is one lets draw a red X
        //            paintBrush.setColor(Color.RED);
        //            paintBrush.setAlpha(255);
        //            paintBrush.setStrokeWidth(5);
        //            canvas.drawLine(lastCollision.x - 5, lastCollision.y - 5,
        //                    lastCollision.x + 5, lastCollision.y + 5, paintBrush);
        //            canvas.drawLine(lastCollision.x + 5, lastCollision.y - 5,
        //                    lastCollision.x - 5, lastCollision.y + 5, paintBrush);
        //        }
    }

    synchronized private void initializeStars(int maxX, int maxY) {
        starField = new ArrayList<>();
        for (int i = 0; i < NUM_OF_STARS; i++) {
            Random r = new Random();
            int x = r.nextInt(maxX - 5 + 1) + 5;
            int y = r.nextInt(maxY - 5 + 1) + 5;
            starField.add(new Point(x, y));
        }
        collisionDetected = false;
    }

    private boolean checkForCollision() {
        if (enemy.getLocation().x < 0 && player.getLocation().x < 0
                && enemy.getLocation().y < 0 && player.getLocation().y < 0)
            return false;
        Rect r1 = new Rect(enemy.getLocation().x, enemy.getLocation().y, enemy.getLocation().x
                + baddieBounds.width(), enemy.getLocation().y + baddieBounds.height());
        Rect r2 = new Rect(player.getLocation().x, player.getLocation().y, player.getLocation().x +
                playerBounds.width(), player.getLocation().y + playerBounds.height());
        Rect r3 = new Rect(r1);
        if (r1.intersect(r2)) {
            for (int i = r1.left; i < r1.right; i++) {
                for (int j = r1.top; j < r1.bottom; j++) {
                    if (enemy.getBitmap().getPixel(i - r3.left, j - r3.top) !=
                            Color.TRANSPARENT) {
                        if (player.getBitmap().getPixel(i - r2.left, j - r2.top) !=
                                Color.TRANSPARENT) {
                            lastCollision = new Point(player.getLocation().x +
                                    i - r2.left, player.getLocation().y + j - r2.top);
                            return true;
                        }
                    }
                }
            }
        }
        lastCollision = new Point(-1, -1);
        return false;
    }
}