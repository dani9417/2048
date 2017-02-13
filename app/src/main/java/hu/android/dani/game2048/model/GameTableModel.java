package hu.android.dani.game2048.model;


import android.content.Context;
import android.util.Log;

import com.orm.SugarRecord;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.Random;

public class GameTableModel extends SugarRecord {
    private int WIN_NUMBER;
    private int SIZE = 4;

    private static GameTableModel instance = null;
    private int gameScore;
    private boolean win;
    private boolean lose;


    public enum DIRECTION {
        UP, DOWN, LEFT, RIGHT, NOTMOVING
    }


    private DIRECTION movingDirection;


    public static GameTableModel getInstance() {
        if (instance == null) {
            instance = new GameTableModel();
        }

        return instance;
    }


    public GameTableModel() {
    }

    private Tile[] gameTiles = new Tile[SIZE * SIZE];

    public void resetModel() {
        movingDirection = DIRECTION.NOTMOVING;
        gameScore = 0;
        win = lose = false;

        gameTiles = new Tile[SIZE * SIZE];
        for (int i = 0; i < gameTiles.length; i++) {
            gameTiles[i] = new Tile();
        }


        addNewTile();
        addNewTile();
    }

    public void moveLeft() {
        movingDirection = DIRECTION.NOTMOVING;

        boolean needToAddNewTile = false;
        for (int i = 0; i < SIZE; i++) {
            Tile[] line = getLine(i);
            Tile[] mergedLine = mergeLine(moveLine(line));
            setLine(i, mergedLine);

            if (!needToAddNewTile && !compare(line, mergedLine)) {
                needToAddNewTile = true;
            }
        }
        if (needToAddNewTile)
            addNewTile();

        canMove();
    }

    public void moveRight() {
        gameTiles = mirroring();
        moveLeft();
        gameTiles = mirroring();
        movingDirection = DIRECTION.NOTMOVING;
    }

    public void moveUp() {
        gameTiles = transpose(gameTiles);
        moveLeft();
        gameTiles = transpose(gameTiles);
        movingDirection = DIRECTION.NOTMOVING;
    }


    public void moveDown() {
        gameTiles = transposeAntiDiagonal();
        moveLeft();
        gameTiles = transposeAntiDiagonal();
        movingDirection = DIRECTION.NOTMOVING;
    }


    private Tile[] mirroring() {
        Tile[] newTiles = new Tile[SIZE * SIZE];
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                newTiles[x + SIZE * y] = tileAt(SIZE - 1 - x, y);
            }
        }
        return newTiles;
    }

    private Tile[] transpose(Tile[] input) {
        Tile[] newTiles = new Tile[SIZE * SIZE];
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (x != y) {
                    newTiles[x + SIZE * y] = input[y + SIZE * x];
                } else {
                    newTiles[x + SIZE * y] = input[x + SIZE * y];
                }
            }
        }
        return newTiles;
    }

    private Tile[] transposeAntiDiagonal() {
        Tile[] newTiles = new Tile[SIZE * SIZE];
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (SIZE - 1 - x != y) {
                    newTiles[x + SIZE * y] = tileAt(SIZE - 1 - y, SIZE - 1 - x);
                } else {
                    newTiles[x + SIZE * y] = tileAt(x, y);
                }
            }
        }
        return newTiles;
    }

    private boolean compare(Tile[] line, Tile[] mergedLine) {
        if (line == mergedLine)
            return true;

        if (line.length != mergedLine.length)
            return false;

        for (int i = 0; i < line.length; i++) {
            if (line[i].getValue() != mergedLine[i].getValue())
                return false;
        }
        return true;
    }

    private void setLine(int i, Tile[] mergedLine) {
        System.arraycopy(mergedLine, 0, gameTiles, i * SIZE, SIZE);
    }

    private Tile[] mergeLine(Tile[] originalLine) {
        LinkedList<Tile> tileLinkedList = new LinkedList<>();
        for (int i = 0; i < SIZE && !originalLine[i].isEmpty(); i++) {
            int value = originalLine[i].getValue();
            if (i < SIZE - 1 && originalLine[i].getValue() == originalLine[i + 1].getValue()) {
                value *= 2;
                if (value == WIN_NUMBER)
                    win = true;
                gameScore += value;
                i++;
            }
            tileLinkedList.add(new Tile(value));
        }
        if (tileLinkedList.size() == 0)
            return originalLine;

        fillForSize(tileLinkedList);
        return tileLinkedList.toArray(new Tile[SIZE]);
    }

    private Tile[] moveLine(Tile[] originalLine) {
        LinkedList<Tile> tileLinkedList = new LinkedList<>();
        for (int i = 0; i < SIZE; i++) {
            if (!originalLine[i].isEmpty())
                tileLinkedList.addLast(originalLine[i]);
        }

        if (tileLinkedList.size() == 0)
            return originalLine;

        Tile[] newLine = new Tile[SIZE];
        fillForSize(tileLinkedList);

        for (int i = 0; i < newLine.length; i++) {
            newLine[i] = tileLinkedList.removeFirst();
        }
        return newLine;

    }

    private void fillForSize(LinkedList<Tile> tileLinkedList) {
        while (tileLinkedList.size() != SIZE)
            tileLinkedList.add(new Tile());
    }

    private Tile[] getLine(int row) {
        Tile[] newLine = new Tile[SIZE];
        for (int i = 0; i < SIZE; i++) {
            newLine[i] = tileAt(i, row);
        }
        return newLine;
    }

    private Tile tileAt(int x, int y) {
        return gameTiles[x + SIZE * y];
    }

    private void addNewTile() {
        ArrayList<Tile> emptyTiles = freeSpace();
        if (!freeSpace().isEmpty()) {
            int index = (int) ((Math.random() * emptyTiles.size()) % emptyTiles.size());
            Tile emptyTile = emptyTiles.get(index);
            emptyTile.setValue(Math.random() < 0.9 ? 2 : 4);
        }
    }

    private ArrayList<Tile> freeSpace() {
        ArrayList<Tile> tiles = new ArrayList<>(SIZE * SIZE);
        for (Tile t : gameTiles) {
            if (t.isEmpty())
                tiles.add(t);
        }
        return tiles;
    }

    private boolean canMove() {
        if (!isFull())
            return true;

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Tile tile = tileAt(x, y);
                if ((x < SIZE - 1 && tile.getValue() == tileAt(x + 1, y).getValue()
                        || (y < SIZE - 1 && tile.getValue() == tileAt(x, y + 1).getValue())))
                    return true;
            }
        }
        lose = true;
        win = false;
        return false;
    }

    private boolean isFull() {
        return freeSpace().size() == 0;
    }

    public int getSize() {
        return SIZE;
    }

    public Tile[] getGameTiles() {
        return gameTiles;
    }

    public Tile[] getTestTiles() {
        int maxPower = (int) (Math.log(WIN_NUMBER) / Math.log(2));
        Random random = new Random();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                if (random.nextFloat() > 0.5) {
                    gameTiles[x + SIZE * y] = new Tile((int) Math.pow(2, new Random(x + SIZE * y).nextInt(maxPower) + 1));
                } else
                    gameTiles[x + SIZE * y] = new Tile(0);
                //gameTiles[x + SIZE * y] = new Tile((int) Math.pow(2,x + SIZE * y + 1));
            }
        }
        return gameTiles;
    }


    public int getGameScore() {
        return gameScore;
    }

    public boolean isWin() {
        return win;
    }

    public boolean isLose() {
        return lose;
    }

    public void setSize(int size) {
        this.SIZE = size;
        setWinNumber();
        resetModel();
    }

    public int getWinNumber() {
        return WIN_NUMBER;
    }

    private void setWinNumber() {
        switch (this.SIZE) {
            case 3:
                WIN_NUMBER = 256;
                break;
            case 4:
                WIN_NUMBER = 2048;
                break;
            case 5:
                WIN_NUMBER = 8192;
                break;
            case 6:
                WIN_NUMBER = 65536;
                break;
            default:
                WIN_NUMBER = 2048;
                break;
        }
    }

    public DIRECTION getMovingDirection() {
        return movingDirection;
    }

    public void setMovingDirection(DIRECTION dir) {
        this.movingDirection = dir;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                sb.append(gameTiles[x + SIZE * y].getValue() + " ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }


    public Tile[] movedGameTable() {
        Tile[] movedTiles = new Tile[SIZE * SIZE];
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(getLine(i), 0, movedTiles, i * SIZE, SIZE);
        }

        switch (movingDirection) {
            case LEFT:
                break;
            case RIGHT:
                movedTiles = mirroring();
                break;
            case UP:
                movedTiles = transpose(movedTiles);
                break;
            case DOWN:
                movedTiles = transposeAntiDiagonal();
                break;
            default:
                break;
        }

        for (int i = 0; i < SIZE; i++) {
            Tile[] rowTile = Arrays.copyOfRange(movedTiles, i * SIZE, (i + 1) * SIZE);
            rowTile = moveLine(rowTile);
            System.arraycopy(rowTile, 0, movedTiles, i * SIZE, SIZE);
        }

        return movedTiles;
    }


    public boolean canMoveOrMerge() {
        switch (movingDirection) {
            case UP: {
                for (int x = 0; x < SIZE; x++) {
                    for (int y = 0; y < SIZE - 1; y++) {
                        if (gameTiles[x + SIZE * y].getValue() == gameTiles[x + SIZE * (y + 1)].getValue() && !gameTiles[x + SIZE * y].isEmpty() ||
                                (gameTiles[x + SIZE * y].isEmpty() && !gameTiles[x + SIZE * (y + 1)].isEmpty()))
                            return true;
                    }
                }
                break;
            }

            case DOWN: {
                for (int x = 0; x < SIZE; x++) {
                    for (int y = SIZE - 1; y > 0; y--) {
                        if (gameTiles[x + SIZE * y].getValue() == gameTiles[x + SIZE * (y - 1)].getValue() && !gameTiles[x + SIZE * y].isEmpty() ||
                                (gameTiles[x + SIZE * y].isEmpty() && !gameTiles[x + SIZE * (y - 1)].isEmpty()))
                            return true;
                    }
                }
                break;
            }

            case LEFT: {
                for (int y = 0; y < SIZE; y++) {
                    for (int x = 0; x < SIZE - 1; x++) {
                        if (gameTiles[x + SIZE * y].getValue() == gameTiles[x + 1 + SIZE * y].getValue() && !gameTiles[x + SIZE * y].isEmpty() ||
                                (gameTiles[x + SIZE * y].isEmpty() && !gameTiles[x + 1 + SIZE * y].isEmpty()))
                            return true;
                    }
                }
                break;
            }

            case RIGHT: {
                for (int y = 0; y < SIZE; y++) {
                    for (int x = SIZE - 1; x > 0; x--) {
                        if (gameTiles[x + SIZE * y].getValue() == gameTiles[x - 1 + SIZE * y].getValue() && !gameTiles[x + SIZE * y].isEmpty() ||
                                (gameTiles[x + SIZE * y].isEmpty() && !gameTiles[x - 1 + SIZE * y].isEmpty()))
                            return true;
                    }
                }
                break;
            }
        }
        return false;
    }
}
