package hu.android.dani.game2048.view;

import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.Transformation;

import hu.android.dani.game2048.model.GameTableModel;
import hu.android.dani.game2048.model.Tile;


public class TileAnimation extends Animation {
    private GameView gameView;
    private Tile[] gameTiles;
    private TilePosition[] tilePositions;
    private int SIZE;
    private int MARGIN;
    private int FIELD_SIZE;
    private int direction; // UP = 0, DOWN = 1, LEFT = 2, RIGHT = 3
    private TilePosition[] movedGameTablePositions;


    public TileAnimation(GameView gv) {
        this.gameView = gv;
        this.gameTiles = gv.gameTableModel.getGameTiles();
        this.SIZE = GameTableModel.getInstance().getSize();
        this.MARGIN = gameView.getMARGIN();
        tilePositions = gv.getTilePositions();
        FIELD_SIZE = gameView.getFIELD_SIZE();
    }

    private void initTilePositions() {
        this.tilePositions = new TilePosition[SIZE * SIZE];
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                int x0 = x * (MARGIN + FIELD_SIZE) + MARGIN;
                int y0 = y * (MARGIN + FIELD_SIZE) + MARGIN;
                tilePositions[x + SIZE * y] = new TilePosition(x0, y0);
            }
        }
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        switch (direction) {
            case 0:
                animateUp(interpolatedTime);
                break;
            case 1:
                animateDown(interpolatedTime);
                break;
            case 2:
                animateLeft(interpolatedTime);
                break;
            case 3:
                animateRight(interpolatedTime);
                break;
            default:
                return;
        }


        gameView.setTilePositions(tilePositions);
        gameView.invalidate();
    }

    private void animateRight(float interpolatedTime) {
        if (interpolatedTime >= 1.0)
            return;

        for (int y = 0; y < SIZE; y++) {
            int k = 0;
            for (int x = SIZE - 1; x >= 0; x--) {
                if (!gameTiles[x + SIZE * y].isEmpty()) {
                    if (movedGameTablePositions[SIZE - 1 - k + SIZE * y].getX() > tilePositions[x + SIZE * y].getX()) {
                        TilePosition tp = movedGameTablePositions[SIZE - 1 - k + SIZE * y];
                        tilePositions[x + SIZE * y] = createPosition(x, y, interpolatedTime, tp);
                    }
                    k++;
                }
            }
        }

    }

    private void animateLeft(float interpolatedTime) {
        if (interpolatedTime >= 1.0) {
            return;
        }
        for (int y = 0; y < SIZE; y++) {
            int k = 0;
            for (int x = 0; x < SIZE; x++) {
                if (!gameTiles[x + SIZE * y].isEmpty()) {
                    if (movedGameTablePositions[k + SIZE * y].getX() < tilePositions[x + SIZE * y].getX()) {
                        TilePosition tp = movedGameTablePositions[k + SIZE * y];
                        tilePositions[x + SIZE * y] = createPosition(x, y, interpolatedTime, tp);
                    }
                    k++;
                }
            }
        }

    }

    private void animateUp(float interpolatedTime) {
        if (interpolatedTime >= 1.0)
            return;

        for (int x = 0; x < SIZE; x++) {
            int k = 0;
            for (int y = 0; y < SIZE; y++) {
                if (!gameTiles[x + SIZE * y].isEmpty()) {
                    if (movedGameTablePositions[x + SIZE * k].getY() < tilePositions[x + SIZE * y].getY()) {
                        TilePosition tp = movedGameTablePositions[x + SIZE * k];
                        tilePositions[x + SIZE * y] = createPosition(x, y, interpolatedTime, tp);
                    }
                    k++;
                }
            }
        }
    }

    private void animateDown(float interpolatedTime) {
        if (interpolatedTime >= 1.0)
            return;

        for (int x = 0; x < SIZE; x++) {
            int k = 0;
            for (int y = SIZE - 1; y >= 0; y--) {
                if (!gameTiles[x + SIZE * y].isEmpty()) {
                    if (movedGameTablePositions[x + SIZE * (SIZE - 1 - k)].getY() > tilePositions[x + SIZE * y].getY()) {
                        TilePosition tp = movedGameTablePositions[x + SIZE * (SIZE - 1 - k)];
                        tilePositions[x + SIZE * y] = createPosition(x, y, interpolatedTime, tp);
                    }
                    k++;
                }
            }
        }
    }


    private TilePosition createPosition(int x, int y, float interpolatedTime, TilePosition end) {
        int x0 = tilePositions[x + SIZE * y].getX();
        int y0 = tilePositions[x + SIZE * y].getY();

        switch (direction) {
            case 0: //UP
                y0 = (int) (y0 + (end.getY() - y0) * interpolatedTime);
                break;
            case 1: //DOWN
                y0 = (int) (y0 + (end.getY() - y0) * interpolatedTime);
                break;
            case 2: //LEFT
                x0 = (int) (x0 + (end.getX() - x0) * interpolatedTime);
                break;
            case 3: //RIGHT
                x0 = (int) (x0 + (end.getX() - x0) * interpolatedTime);
                break;
        }
        return new TilePosition(x0, y0);
    }


    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void initParameters() {
        this.gameTiles = GameTableModel.getInstance().getGameTiles();
        this.SIZE = GameTableModel.getInstance().getSize();
        this.FIELD_SIZE = gameView.getFIELD_SIZE();
        this.MARGIN = gameView.getMARGIN();
        tilePositions = gameView.getTilePositions();

        createMovedGameTablePositions();
        printTiles(movedGameTablePositions);

        alterPositionsForMerge();
        printTiles(movedGameTablePositions);
    }


    private void createMovedGameTablePositions() {
        movedGameTablePositions = new TilePosition[SIZE * SIZE];
        Tile[] movedTiles = GameTableModel.getInstance().movedGameTable();
        switch (direction) {
            case 0:
                upMovedGameTablePositions(movedTiles);
                break;
            case 1:
                downMovedGameTablePositions(movedTiles);
                break;
            case 2:
                leftMovedGameTablePositions(movedTiles);
                break;
            case 3:
                rightMovedGameTablePositions(movedTiles);
                break;
            default:
                break;
        }
    }

    private void rightMovedGameTablePositions(Tile[] movedTiles) {
        int lastOne;
        for (int y = 0; y < SIZE; y++) {
            lastOne = MARGIN;
            for (int x = 0; x < SIZE; x++) {
                int x0 = (SIZE - 1 - x) * (FIELD_SIZE + MARGIN) + MARGIN;
                int y0 = y * (FIELD_SIZE + MARGIN) + MARGIN;
                if (movedTiles[x + SIZE * y].isEmpty())
                    movedGameTablePositions[(SIZE - 1 - x) + SIZE * y] = new TilePosition(lastOne, y0);
                else {
                    movedGameTablePositions[(SIZE - 1 - x) + SIZE * y] = new TilePosition(x0, y0);
                    lastOne = x0;
                }
            }
        }
    }

    private void leftMovedGameTablePositions(Tile[] movedTiles) {
        int lastOne;
        for (int y = 0; y < SIZE; y++) {
            lastOne = MARGIN;
            for (int x = 0; x < SIZE; x++) {
                int x0 = x * (FIELD_SIZE + MARGIN) + MARGIN;
                int y0 = y * (FIELD_SIZE + MARGIN) + MARGIN;
                if (movedTiles[x + SIZE * y].isEmpty()) {
                    movedGameTablePositions[x + SIZE * y] = new TilePosition(lastOne, y0);
                } else {
                    movedGameTablePositions[x + SIZE * y] = new TilePosition(x0, y0);
                    lastOne = x0;
                }

            }
        }
    }

    private void downMovedGameTablePositions(Tile[] movedTiles) {
        for (int y = 0; y < SIZE; y++) {
            int lastOne = MARGIN;
            for (int x = 0; x < SIZE; x++) {
                int x0 = (SIZE - 1 - y) * (FIELD_SIZE + MARGIN) + MARGIN;
                int y0 = (SIZE - 1 - x) * (FIELD_SIZE + MARGIN) + MARGIN;
                if (movedTiles[x + SIZE * y].isEmpty()) {
                    movedGameTablePositions[(SIZE - 1 - y) + SIZE * (SIZE - 1 - x)] = new TilePosition(x0, lastOne);
                } else {
                    movedGameTablePositions[(SIZE - 1 - y) + SIZE * (SIZE - 1 - x)] = new TilePosition(x0, y0);
                    lastOne = y0;
                }
            }
        }
    }

    private void upMovedGameTablePositions(Tile[] movedTiles) {
        for (int y = 0; y < SIZE; y++) {
            int lastOne = MARGIN;
            for (int x = 0; x < SIZE; x++) {
                int x0 = y * (FIELD_SIZE + MARGIN) + MARGIN;
                int y0 = x * (FIELD_SIZE + MARGIN) + MARGIN;
                if (movedTiles[x + SIZE * y].isEmpty()) {
                    movedGameTablePositions[y + SIZE * x] = new TilePosition(x0, lastOne);
                } else {
                    movedGameTablePositions[y + SIZE * x] = new TilePosition(x0, y0);
                    lastOne = x0;
                }

            }
        }
    }

    private void printTiles(TilePosition[] tilePositions) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                sb.append("(");
                sb.append(tilePositions[x + SIZE * y].getX()).append(" ").append(tilePositions[x + SIZE * y].getY());
                sb.append(")");
            }
            sb.append("\n");
        }
        Log.d("table", "moved positions\n" + sb.toString());
    }

    private void alterPositionsForMerge() {
        Tile[] movedTiles = GameTableModel.getInstance().movedGameTable();
        switch (direction) {
            case 0:
                alterPositionsMergeUp(movedTiles);
                break;
            case 1:
                alterPositionsMergeDownn(movedTiles);
                break;
            case 2:
                alterPositionsMergeLeft(movedTiles);
                break;
            case 3:
                alterPositionsMergeRight(movedTiles);
                break;
        }
    }

    private void alterPositionsMergeDownn(Tile[] movedTiles) {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (x < SIZE - 1) {
                    if (movedTiles[x + SIZE * y].getValue() == movedTiles[x + 1 + SIZE * y].getValue()
                            && !movedTiles[x + SIZE * y].isEmpty()) {
                        for (int i = SIZE - 2 - x; i >= 0; i--) {
                            movedGameTablePositions[(SIZE - 1 - y) + SIZE * i].setY(movedGameTablePositions[(SIZE - 1 - y) + SIZE * i].getY() + (FIELD_SIZE + MARGIN));
                        }
                    }
                }
            }
        }
    }

    private void alterPositionsMergeRight(Tile[] movedTiles) {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (x < SIZE - 1) {
                    if (movedTiles[x + SIZE * y].getValue() == movedTiles[x + 1 + SIZE * y].getValue()
                            && !movedTiles[x + SIZE * y].isEmpty()) {
                        for (int i = SIZE - 2 - x; i >= 0; i--) {
                            movedGameTablePositions[i + SIZE * y].setX(movedGameTablePositions[i + SIZE * y].getX() + (FIELD_SIZE + MARGIN));
                        }
                    }
                }
            }
        }
    }

    private void alterPositionsMergeUp(Tile[] movedTiles) {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (x < SIZE - 1) {
                    if (movedTiles[x + SIZE * y].getValue() == movedTiles[x + 1 + SIZE * y].getValue()
                            && !movedTiles[x + SIZE * y].isEmpty()) {
                        for (int i = x + 1; i < SIZE; i++) {
                            movedGameTablePositions[y + SIZE * i].setY(movedGameTablePositions[y + SIZE * i].getY() - (FIELD_SIZE + MARGIN));
                        }
                    }
                }
            }
        }
    }

    private void alterPositionsMergeLeft(Tile[] movedTiles) {
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (x < SIZE - 1) {
                    if (movedTiles[x + SIZE * y].getValue() == movedTiles[x + 1 + SIZE * y].getValue()
                            && !movedTiles[x + SIZE * y].isEmpty()) {

                        int shift = FIELD_SIZE + MARGIN;
                        for (int i = x + 1; i < SIZE; i++) {
                            movedGameTablePositions[i + SIZE * y].setX(movedGameTablePositions[i + SIZE * y].getX() - shift);
                        }
                        x++;
                    }
                }
            }
        }
    }


    public void resetPositions() {
        gameTiles = GameTableModel.getInstance().getGameTiles();
        initTilePositions();
    }
}
