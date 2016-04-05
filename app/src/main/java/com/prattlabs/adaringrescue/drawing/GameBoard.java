package com.prattlabs.adaringrescue.drawing;

import android.content.Context;
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

import static java.lang.Math.round;

public class GameBoard extends View {
    private static final int NUM_OF_STARS = 25;
    AttributeSet aSet;
    private Paint paintBrush;
    private List<Point> starField = null;
    private int starAlpha = 80;
    private int starFade = 2;
    private Actor player;
    private Actor enemy;
    private RectF baddieBounds;
    private RectF playerBounds;
    private boolean collisionDetected = false;
    private PointF lastCollision = new PointF(-1F, -1F);
    private Canvas canvas;

    public GameBoard(Context context, AttributeSet aSet) {
        super(context, aSet);
        this.aSet = aSet;
        paintBrush = new Paint();

        enemy = new Actor(getContext(), aSet, R.drawable.baddie);
        player = new Actor(getContext(), aSet, R.drawable.player);

        baddieBounds = enemy.getBounds();
        playerBounds = player.getBounds();
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
        starField = null;
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
        paintStars(canvas);
        drawActors(canvas);
        paintXOnCollision(canvas);
    }

    private void paintBackground(Canvas canvas) {
        paintBrush.setColor(Color.BLACK);
        paintBrush.setAlpha(255);
        paintBrush.setStrokeWidth(1);
        canvas.drawRect(0, 0, getWidth(), getHeight(), paintBrush);
    }

    private void paintStars(Canvas canvas) {
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
    }

    private void drawActors(Canvas canvas) {
        //TODO Create animations here
        player.drawActor(canvas);
        enemy.drawActor(canvas);

        // Draws X's on the actors for debugging
        paintBrush.setColor(Color.RED);
        paintBrush.setAlpha(255);
        paintBrush.setStrokeWidth(5);

        canvas.drawLine(getPlayer().getLocation().x - 5, getPlayer().getLocation().y - 5,
                getPlayer().getLocation().x + 5, getPlayer().getLocation().y + 5, paintBrush);
        canvas.drawLine(getPlayer().getLocation().x + 5, getPlayer().getLocation().y - 5,
                getPlayer().getLocation().x - 5, getPlayer().getLocation().y + 5, paintBrush);

        canvas.drawLine(getPlayer().getLeft() - 5, getPlayer().getLeft() - 5,
                getPlayer().getLeft() + 5, getPlayer().getLeft() + 5, paintBrush);
        canvas.drawLine(getPlayer().getLeft() + 5, getPlayer().getLeft() - 5,
                getPlayer().getLeft() - 5, getPlayer().getLeft() + 5, paintBrush);

        paintBrush.setColor(Color.YELLOW);
        canvas.drawLine(enemy.getLocation().x - 5, enemy.getLocation().y - 5,
                enemy.getLocation().x + 5, enemy.getLocation().y + 5, paintBrush);
        canvas.drawLine(enemy.getLocation().x + 5, enemy.getLocation().y - 5,
                enemy.getLocation().x - 5, enemy.getLocation().y + 5, paintBrush);
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