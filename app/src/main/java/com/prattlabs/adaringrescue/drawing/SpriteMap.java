package com.prattlabs.adaringrescue.drawing;

import android.graphics.Bitmap;
import android.graphics.RectF;

public class SpriteMap {

    private RectF[][] map;

    /**
     * Creates a matrix from a bitmap with the given rows and columns
     *
     * @param bitmap
     * @param rows
     * @param cols
     */
    public SpriteMap(Bitmap bitmap, int rows, int cols) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // A tile is a square in the matrix
        int tileWidth = width / cols;
        int tileHeight = height / rows;

        // These will track and increase along the width and height to record each tiles co-ordinates
        int left = 0;
        int top = 0;
        int right = 0 + tileWidth;
        int bottom = 0 + tileHeight;

        // Move across the tiles and down, measuring them and recording their co-ordinates
        map = new RectF[rows][cols];
        for (int i = 0; i < map.length; i++) {
            for (int j = 0; j < map[i].length; j++) {
                map[i][j] = new RectF(left, top, right, bottom);

                // Next tile right horizontally
                left += tileWidth;
                right += tileWidth;
            }
            // Next tile down vertically
            top += tileHeight;
            bottom += tileHeight;
            left = 0;
            right = 0 + tileWidth;
        }
        int i = 0;
    }

    public RectF[] getRow(int row) {
        return map[row];
    }

}
