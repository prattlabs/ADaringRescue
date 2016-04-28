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
import java.util.List;
import java.util.Random;
import java.util.Set;

import static android.graphics.Color.RED;
import static com.prattlabs.adaringrescue.Constants.ACTOR_HEIGHT;
import static com.prattlabs.adaringrescue.Constants.ACTOR_WIDTH;
import static com.prattlabs.adaringrescue.Constants.BACKGROUND_COLOR;
import static com.prattlabs.adaringrescue.Constants.BULLET_SIZE;
import static com.prattlabs.adaringrescue.Constants.NUM_ENEMIES;
import static java.lang.Math.round;

public class GameBoard extends View {
    private static final int NUM_OF_ROCKS = 25;
    private static final int NUM_OF_TREES = 15;
    AttributeSet aSet;
    private Paint redPaint;
    private List<Point> rocks = null;
    private List<Point> trees = null;
    private Actor player;
    private Set<Actor> enemies;
    private Actor bullet;
    private boolean collisionDetected = false;
    private PointF lastCollision = new PointF(-1F, -1F);
    private Canvas canvas;
    private Bitmap tree;
    private boolean bulletCollisionDetected;
    private PointF bulletLastCollision;
    private PointF lastBulletCollision;

    public GameBoard(Context context, AttributeSet aSet) {
        super(context, aSet);
        this.aSet = aSet;
        redPaint = new Paint();

        player = new Actor(getContext(), aSet, R.drawable.player);
        enemies = new HashSet<>(NUM_ENEMIES);
            for (int i = 0; i < NUM_ENEMIES; i++) {
                enemies.add(new Actor(getContext(), aSet, R.drawable.baddie));
            }

        bullet = new Actor(getContext(), aSet, R.drawable.star);
        bullet.setTarget(player.getLocation().x, player.getLocation().y);

        tree = BitmapFactory.decodeResource(getResources(), R.drawable.tree);

        redPaint = redBrush();
    }

    private Paint redBrush() {
        Paint p = new Paint();
        p.setColor(RED);
        p.setAlpha(255);
        p.setStrokeWidth(5);
        return p;
    }

    public Set<Actor> getEnemies() {

            return enemies;

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
        trees = null;
    }

    synchronized public float getBaddieWidth() {
        return ACTOR_WIDTH;
    }

    synchronized public float getBaddieHeight() {
        return ACTOR_HEIGHT;
    }

    synchronized public PointF getLastCollision() {
        return lastCollision;
    }

    synchronized public boolean wasCollisionDetected() {
        return collisionDetected;
    }

    synchronized public boolean wasBulletCollisionDetected() {
        return bulletCollisionDetected;
    }

    @Override
    synchronized public void onDraw(Canvas canvas) {
        if (this.canvas == null) {
            this.canvas = canvas;
        }
        paintBackground();
        paintRocks();
        paintBullet();
        drawActors();
        paintTrees();
        collisionDetected = checkForPlayerCollision();
        bulletCollisionDetected = killEnemyOnBulletCollision();
    }

    private void paintBackground() {
        Paint p = new Paint();
        p.setColor(BACKGROUND_COLOR);
        p.setAlpha(255);
        p.setStrokeWidth(1);
        canvas.drawRect(0, 0, getWidth(), getHeight(), p);
    }

    private void paintRocks() {
        if (rocks == null) {
            rocks = new ArrayList<>();
            for (int i1 = 0; i1 < NUM_OF_ROCKS; i1++) {
                Random r = new Random();
                int x = r.nextInt(canvas.getWidth() - 5 + 1) + 5;
                int y = r.nextInt(canvas.getHeight() - 5 + 1) + 5;
                rocks.add(new Point(x, y));
            }
        } else {
            for (int i = 0; i < NUM_OF_ROCKS; i++) {
                Paint p = new Paint();
                p.setColor(Color.BLACK);
                p.setAlpha(255);
                p.setStrokeWidth(5);
                canvas.drawPoint(rocks.get(i).x, rocks.get(i).y, p);
            }
        }
    }

    private void paintBullet() {
        // Draw bullet
        canvas.drawBitmap(bullet.getBitmap(), null, bulletDrawSize(), null);
    }

    private void drawActors() {
        player.drawActor(canvas);

            for (Actor enemy : enemies) {
                enemy.drawActor(canvas);
                // Remove enemy if off-screen
                if (enemy.getLocation().x < 0) {
                    enemies.remove(enemy);
                    enemy = null;
                }
            }

    }

    private void paintTrees() {
        if (trees == null) {

                trees = new ArrayList<>();
                for (int i1 = 0; i1 < NUM_OF_TREES; i1++) {
                    Random r = new Random();
                    int x = r.nextInt(canvas.getWidth() - 5 + 1) + 5;
                    int y = r.nextInt(canvas.getHeight() - 5 + 1) + 5;
                    trees.add(new Point(x, y));
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

    private boolean killEnemyOnBulletCollision() {

            for (Actor enemy : enemies) {
                if (enemy.getLocation().x < 0 && bullet.getLocation().x < 0
                        && enemy.getLocation().y < 0 && bullet.getLocation().y < 0)
                    continue;
                RectF r1 = new RectF(enemy.getLocation().x, enemy.getLocation().y, enemy.getLocation().x
                        + ACTOR_WIDTH, enemy.getLocation().y + ACTOR_HEIGHT);
                RectF r2 = new RectF(bullet.getLocation().x, bullet.getLocation().y, bullet.getLocation().x +
                        BULLET_SIZE, bullet.getLocation().y + BULLET_SIZE);
                RectF r3 = new RectF(r1);
                if (r1.intersect(r2)) {

//                    enemy.setLocation(-100, -100);
//                    enemy.setVelocity(0, 0);

                    for (int i = round(r1.left); i < round(r1.right); i++) {
                        for (int j = round(r1.top); j < round(r1.bottom); j++) {
                            if (enemy.getBitmap().getPixel(i - round(r3.left), j - round(r3.top)) !=
                                    Color.TRANSPARENT) {
                                if (bullet.getBitmap().getPixel(i - round(r2.left), j - round(r2.top)) !=
                                        Color.TRANSPARENT) {
                                    bulletLastCollision = new PointF(bullet.getLocation().x +
                                            i - r2.left, bullet.getLocation().y + j - r2.top);

                                    bullet.setVelocity(0, 0);
                                    bullet.setLocation(-100, -100);

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

    private RectF bulletDrawSize() {
        return new RectF(bullet.getLocation().x, bullet.getLocation().y,
                bullet.getLocation().x + BULLET_SIZE, bullet.getLocation().y + BULLET_SIZE);
    }

    public void paintXMark(float x, float y) {
        canvas.drawLine(x - 5, y - 5,
                x + 5, y + 5, redPaint);
        canvas.drawLine(x + 5, y - 5,
                x - 5, y + 5, redPaint);
    }

    private boolean checkForPlayerCollision() {

            for (Actor enemy : enemies) {
                if (enemy.getLocation().x < 0 && player.getLocation().x < 0
                        && enemy.getLocation().y < 0 && player.getLocation().y < 0)
                    continue;
                RectF r1 = new RectF(enemy.getLocation().x, enemy.getLocation().y, enemy.getLocation().x
                        + ACTOR_WIDTH, enemy.getLocation().y + ACTOR_HEIGHT);
                RectF r2 = new RectF(player.getLocation().x, player.getLocation().y, player.getLocation().x +
                        ACTOR_WIDTH, player.getLocation().y + ACTOR_HEIGHT);
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

    public PointF getLastBulletCollision() {
        return lastBulletCollision;
    }
}