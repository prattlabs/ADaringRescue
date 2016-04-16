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
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.prattlabs.adaringrescue.Constants.BACKGROUND_COLOR;
import static com.prattlabs.adaringrescue.Constants.BULLET_SIZE;
import static java.lang.Math.round;

public class GameBoard extends View {
    private static final int NUM_OF_ROCKS = 25;
    private static final int NUM_OF_TREES = 15;
    AttributeSet aSet;
    private Paint paintBrush;
    private List<Point> rocks = null;
    private List<Point> trees = null;
    private Actor player;

    public Set<Actor> getEnemies() {
        return enemies;
    }

    private Set<Actor> enemies;
    private Iterator<Actor> enemyIterator;
    Actor currentEnemy;
    private Actor bullet;
    private boolean collisionDetected = false;
    private PointF lastCollision = new PointF(-1F, -1F);
    private Canvas canvas;
    private Bitmap tree;

    public GameBoard(Context context, AttributeSet aSet) {
        super(context, aSet);
        this.aSet = aSet;
        paintBrush = new Paint();

        player = new Actor(getContext(), aSet, R.drawable.player);
        enemies = new HashSet<>(10);
        for (int i = 0; i < 10; i++) {
            enemies.add(new Actor(getContext(), aSet, R.drawable.baddie));
        }
        bullet = new Actor(getContext(), aSet, R.drawable.star);
        bullet.setTarget(player.getLocation().x, player.getLocation().y);

        enemyIterator = enemies.iterator();
        tree = BitmapFactory.decodeResource(getResources(), R.drawable.tree);
    }

    public Canvas getCanvas() {
        return canvas;
    }

    public Actor getPlayer() {
        return player;
    }

    synchronized public void setActorLocation(Actor actor, float x, float y) {
        actor.setLocation(x, y);
    }

    synchronized public void resetRocksAndTrees() {
        rocks = null;
        trees= null;
    }

    synchronized public float getBaddieWidth() {
        if (currentEnemy != null) {
            return currentEnemy.getBounds().width();
        }
        else return -1;
    }

    synchronized public float getBaddieHeight() {
        if (currentEnemy != null) {
            return currentEnemy.getBounds().height();
        }
        else return -1;
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
        paintRocks(canvas);
        paintBullet(canvas);
        drawActors(canvas);
        paintTrees(canvas);
        paintXOnCollision(canvas);
    }

    private void paintBullet(Canvas canvas) {
        // Draw bullet
        canvas.drawBitmap(bullet.getBitmap(), null, bulletSizeToDest(), null);
    }

    private RectF bulletSizeToDest() {
        return new RectF(bullet.getLocation().x, bullet.getLocation().y,
                bullet.getLocation().x + BULLET_SIZE, bullet.getLocation().y + BULLET_SIZE);
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
        for (Actor enemy : enemies) {
            enemy.drawActor(canvas);
        }
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
        for (Actor enemy : enemies) {
            if (enemy.getLocation().x < 0 && player.getLocation().x < 0
                    && enemy.getLocation().y < 0 && player.getLocation().y < 0)
                return false;
            RectF r1 = new RectF(enemy.getLocation().x, enemy.getLocation().y, enemy.getLocation().x
                    + enemy.getBounds().width(), enemy.getLocation().y + enemy.getBounds().height());
            RectF r2 = new RectF(player.getLocation().x, player.getLocation().y, player.getLocation().x +
                    player.getBounds().width(), player.getLocation().y + player.getBounds().height());
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
        }
        lastCollision = new PointF(-1F, -1F);
        return false;
    }

    public Actor getBullet() {
        return bullet;
    }
}