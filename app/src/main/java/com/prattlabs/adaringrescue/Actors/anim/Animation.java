package com.prattlabs.adaringrescue.actors.anim;

import android.graphics.RectF;

/**
 * Holds an animation as a series of rectangles that should correspond to a bitmap.
 */
public class Animation {

    // The frames of the animation. Must correspond to a bitmap.
    RectF[] frames;
    BackForth bf = BackForth.FORTH;
    private int currentFrame;
    private int speed = 1;
    private int tempSpeed;

    /**
     * Creates an animation with the set of rectangles provided
     * @param frames The frame co-ordinates of the animation
     */
    public Animation(RectF[] frames) {
        if (frames == null) {
            System.err.println("Null frames given to Animation on create:\n" + frames);
            return;
        }
        this.frames = frames;
        currentFrame = 0;
    }

    public int getSpeed() {
        return speed;
    }

    public Animation setSpeed(int speed) {
        this.speed = speed;
        tempSpeed = speed;
        return this;
    }

    /**
     * Gets the next frame in the animation as a RectF.  Will increment to next frame on every call.
     * @return The next frame in the animation
     */
    public RectF getNextFrame() {
        // Loop to beginning of animation when end is reached
        // TODO have a flag for looping?

        tempSpeed--;
        if (tempSpeed == 0) {
            tempSpeed = speed;
            // If on the first frame, go toward last frame
            if (currentFrame == 0) {
                bf = BackForth.FORTH;
            }
            // If on the last frame, go toward first frame
            else if (currentFrame == frames.length - 1) {
                bf = BackForth.BACK;
            }
            // increment to the next frame
            if (bf == BackForth.BACK)
                return frames[--currentFrame];
            else
                return frames[++currentFrame];
        } else {
            return frames[currentFrame];
        }
    }

    /**
     * Gets the current animation frame
     * @return the current animation frame
     */
    public RectF getCurrentFrame() {
        if (currentFrame >= frames.length) {
            currentFrame = 0;
        }
        return frames[currentFrame];
    }

    private enum BackForth {
        BACK, FORTH;
    }
}
