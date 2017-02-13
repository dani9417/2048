package hu.android.dani.game2048.view;

import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;

import hu.android.dani.game2048.model.GameTableModel;


public class GestureListener extends GestureDetector.SimpleOnGestureListener {

    private static final int MIN_VELOCITY = 100;
    private static final int MIN_DISTANCE = 100;


    @Override
    public boolean onDown(MotionEvent e) {
        return true;
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        float xDist = Math.abs(e1.getX() - e2.getX());
        float yDist = Math.abs(e1.getY() - e2.getY());

        velocityX = Math.abs(velocityX);
        velocityY = Math.abs(velocityY);

        if (xDist > yDist && xDist > MIN_DISTANCE && velocityX > MIN_VELOCITY) {
            if (e1.getX() > e2.getX()) {
                GameTableModel.getInstance().setMovingDirection(GameTableModel.DIRECTION.LEFT);
                Log.d("FLING", "LEFT");
                return false;
            } else if (e1.getX() < e2.getX()) {
                GameTableModel.getInstance().setMovingDirection(GameTableModel.DIRECTION.RIGHT);
                Log.d("FLING", "RIGHT");
                return false;
            }
        } else if (yDist > xDist && yDist > MIN_DISTANCE && velocityY > MIN_VELOCITY) {
            if (e1.getY() > e2.getY()) {
                GameTableModel.getInstance().setMovingDirection(GameTableModel.DIRECTION.UP);
                Log.d("FLING", "UP");
                return false;
            } else if (e1.getY() < e2.getY()) {
                GameTableModel.getInstance().setMovingDirection(GameTableModel.DIRECTION.DOWN);
                Log.d("FLING", "DOWN");
                return false;
            }
        }

        return true;
    }
}
