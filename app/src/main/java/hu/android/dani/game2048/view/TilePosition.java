package hu.android.dani.game2048.view;


import hu.android.dani.game2048.model.Tile;

class TilePosition {
    private int x;
    private int y;

    TilePosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    int getX() {
        return x;
    }

    void setX(int x) {
        this.x = x;
    }

    int getY() {
        return y;
    }

    void setY(int y) {
        this.y = y;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(x).append(" ").append(y);
        return sb.toString();
    }


}
