package com.prattlabs.adaringrescue.drawing;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.prattlabs.adaringrescue.R;
import com.prattlabs.adaringrescue.actors.Actor;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static com.prattlabs.adaringrescue.Constants.BACKGROUND_COLOR;
import static java.lang.Math.round;

public class GameBoard extends View {
    private static final int NUM_OF_ROCKS = 25;
    private static final int NUM_OF_TREES = 15;
    AttributeSet aSet;
    private Paint paintBrush;
    private List<Point> rocks = null;
    private List<Point> trees = null;
    private Actor player;
    private Actor enemy;
    private RectF baddieBounds;
    private RectF playerBounds;
    private boolean collisionDetected = false;
    private PointF lastCollision = new PointF(-1F, -1F);
    private Canvas canvas;
    private Bitmap tree;

    public GameBoard(Context context, AttributeSet aSet) {
        super(context, aSet);
        this.aSet = aSet;
        paintBrush = new Paint();

        enemy = new Actor(getContext(), aSet, R.drawable.baddie);
        player = new Actor(getContext(), aSet, R.drawable.player);

        baddieBounds = enemy.getBounds();
        playerBounds = player.getBounds();

        tree = BitmapFactory.decodeResource(getResources(), R.drawable.tree);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Actor getPlayer() {
        return player;
    }

    public Actor getBaddie() {
        return enemy;
    }

    synchronized public void setActorLocation(Actor actor, float x, float y) {
        actor.setLocation(x, y);
    }

    synchronized public void resetStarField() {
        rocks = null;
    }

    synchronized public float getBaddieWidth() {
        return baddieBounds.width();
    }

    synchronized public float getBaddieHeight() {
        return baddieBounds.height();
    }

    synchronized public PointF getLastCollision() {
        return lastCollision;
    }

    synchronized public boolean wasCollisionDetected() {
        return collisionDetected;
    }

    @Override
    synchronized public void onDraw(Canvas canvas) {
        if (this.canvas == null) {
            this.canvas = canvas;
        }
        paintBackground(canvas);
        drawActors(canvas);
        paintRocks(canvas);
        paintTrees(canvas);
        paintXOnCollision(canvas);
    }

    private void paintBackground(Canvas canvas) {
        paintBrush.setColor(BACKGROUND_COLOR);
        paintBrush.setAlpha(255);
        paintBrush.setStrokeWidth(1);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paintBrush);
    }

    private void paintRocks(Canvas canvas) {
        if (rocks == null) {
            rocks = new ArrayList<>();
            for (int i1 = 0; i1 < NUM_OF_ROCKS; i1++) {
                Random r = new Random();
                int x = r.nextInt(canvas.getWidth() - 5 + 1) + 5;
                int y = r.nextInt(canvas.getHeight() - 5 + 1) + 5;
                rocks.add(new Point(x, y));
            }
            collisionDetected = false;
        } else {
            for (int i = 0; i < NUM_OF_ROCKS; i++) {
                paintBrush.setColor(Color.BLACK);
                paintBrush.setAlpha(255);
                paintBrush.setStrokeWidth(5);
                canvas.drawPoint(rocks.get(i).x, rocks.get(i).y, paintBrush);
            }
        }
    }

    private void paintTrees(Canvas canvas) {
        if (trees == null) {
            synchronized (this) {
                trees = new ArrayList<>();
                for (int i1 = 0; i1 < NUM_OF_TREES; i1++) {
                    Random r = new Random();
                    int x = r.nextInt(canvas.getWidth() - 5 + 1) + 5;
                    int y = r.nextInt(canvas.getHeight() - 5 + 1) + 5;
                    trees.add(new Point(x, y));
                }
            }
        } else {
            collisionDetected = false;
            for (int i = 0; i < NUM_OF_TREES; i++) {
                // Paint tree bitmap
                RectF loc = new RectF(trees.get(i).x, trees.get(i).y, trees.get(i).x + 100, trees.get(i).y + 100);
                canvas.drawBitmap(tree, null, loc, null);
            }
        }
    }

    private void drawActors(Canvas canvas) {
        player.drawActor(canvas);
        enemy.drawActor(canvas);
    }

    private void paintXOnCollision(Canvas canvas) {
        collisionDetected = checkForCollision();
        if (collisionDetected) {
            paintBrush.setColor(Color.RED);
            paintBrush.setAlpha(255);
            paintBrush.setStrokeWidth(5);
            canvas.drawLine(lastCollision.x - 5, lastCollision.y - 5,
                    lastCollision.x + 5, lastCollision.y + 5, paintBrush);
            canvas.drawLine(lastCollision.x + 5, lastCollision.y - 5,
                    lastCollision.x - 5, lastCollision.y + 5, paintBrush);
        }
    }

    private boolean checkForCollision() {
        if (enemy.getLocation().x < 0 && player.getLocation().x < 0
                && enemy.getLocation().y < 0 && player.getLocation().y < 0)
            return false;
        RectF r1 = new RectF(enemy.getLocation().x, enemy.getLocation().y, enemy.getLocation().x
                + baddieBounds.width(), enemy.getLocation().y + baddieBounds.height());
        RectF r2 = new RectF(player.getLocation().x, player.getLocation().y, player.getLocation().x +
                playerBounds.width(), player.getLocation().y + playerBounds.height());
        RectF r3 = new RectF(r1);
        if (r1.intersect(r2)) {
            for (int i = round(r1.left); i < round(r1.right); i++) {
                for (int j = round(r1.top); j < round(r1.bottom); j++) {
                    if (enemy.getBitmap().getPixel(i - round(r3.left), j - round(r3.top)) !=
                            Color.TRANSPARENT) {
                        if (player.getBitmap().getPixel(i - round(r2.left), j - round(r2.top)) !=
                                Color.TRANSPARENT) {
                            lastCollision = new PointF(player.getLocation().x +
                                    i - r2.left, player.getLocation().y + j - r2.top);
                            return true;
                        }
                    }
                }
            }
        }
        lastCollision = new PointF(-1F, -1F);
        return false;
    }
}