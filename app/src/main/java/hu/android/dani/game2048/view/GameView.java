package hu.android.dani.game2048.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.test.filters.SdkSuppress;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import hu.android.dani.game2048.R;
import hu.android.dani.game2048.model.GameTableModel;
import hu.android.dani.game2048.model.Tile;


public class GameView extends View {
    public static final int MAGIC_WIDTH = 984;
    GameTableModel gameTableModel = GameTableModel.getInstance();

    private int BASE_TEXT_SIZE = 150;
    private int BASE_FIELD_SIZE;

    Paint paintBg;
    Paint paintFields;
    Paint paintTiles;
    Paint paintText;


    private int SIZE;
    private int MARGIN = 20;
    private int FIELD_SIZE;


    TilePosition[] tilePositions;

    public GameView(Context context) {
        super(context);
        initPaints(context);

        gameTableModel.resetModel();
        SIZE = gameTableModel.getSize();
        initTilePositions();
    }

    public GameView(Context context, AttributeSet attrs) {
        super(context, attrs);

        initPaints(context);

        gameTableModel.resetModel();
        SIZE = gameTableModel.getSize();
        initTilePositions();
    }

    public void initTilePositions() {
        SIZE = gameTableModel.getSize();
        MARGIN = getWidth() / 50;
        FIELD_SIZE = calculateFieldSize(SIZE);

        tilePositions = new TilePosition[SIZE * SIZE];
        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                int x0 = x * (FIELD_SIZE + MARGIN) + MARGIN;
                int y0 = y * (FIELD_SIZE + MARGIN) + MARGIN;
                tilePositions[x + SIZE * y] = new TilePosition(x0, y0);

            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        SIZE = gameTableModel.getSize();
        MARGIN = getWidth() / 50;
        FIELD_SIZE = calculateFieldSize(SIZE);
        BASE_FIELD_SIZE = calculateFieldSize(4);

        canvas.drawRoundRect(0, 0, getWidth(), getHeight(), 15, 15, paintBg);

        drawFields(canvas);
        drawTiles(canvas);


    }

    private int calculateFieldSize(int size) {
        return (getWidth() - (size + 1) * MARGIN) / size;
    }


    private void drawTiles(Canvas canvas) {
        Tile[] tiles = gameTableModel.getGameTiles();

        for (int y = 0; y < SIZE; y++) {
            for (int x = 0; x < SIZE; x++) {
                Tile tile = tiles[x + SIZE * y];
                TilePosition tp = tilePositions[x + SIZE * y];

                if (!tile.isEmpty()) {
                    drawSingleTile(canvas, tile, tp);
                }
            }
        }
    }

    private void drawSingleTile(Canvas canvas, Tile tile, TilePosition tp) {
        paintTiles.setColor(tile.getColor());
        int valueLength = tile.getValueLength();
        int textSize = (int) ((BASE_TEXT_SIZE - (valueLength - 1) * 20) * sizeModifier());
        paintText.setTextSize(textSize);
        canvas.drawRoundRect(tp.getX(), tp.getY(), tp.getX() + FIELD_SIZE, tp.getY() + FIELD_SIZE, 15, 15, paintTiles);

        int tx = FIELD_SIZE / 2;
        int ty = (int) (FIELD_SIZE / 2 - (paintText.descent() + paintText.ascent()) / 2);
        canvas.drawText(String.valueOf(tile.getValue()),
                tp.getX() + tx,
                tp.getY() + ty,
                paintText);
    }

    private double sizeModifier() {
        return calculateFieldSize(SIZE) / (double) BASE_FIELD_SIZE;
    }

    private void drawFields(Canvas canvas) {
        for (int y = MARGIN; y < getHeight(); y += (FIELD_SIZE + MARGIN)) {
            for (int x = MARGIN; x < getWidth(); x += (FIELD_SIZE + MARGIN)) {
                canvas.drawRoundRect(x, y, x + FIELD_SIZE, y + FIELD_SIZE, 15, 15, paintFields);
                //canvas.drawRect(x, y, x + FIELD_SIZE, y + FIELD_SIZE, paintFields);
            }
        }
    }

    private void initPaints(Context context) {
        paintBg = new Paint();
        paintBg.setColor(ContextCompat.getColor(context, R.color.game_area_background_color));
        paintBg.setStyle(Paint.Style.FILL);

        paintFields = new Paint();
        paintFields.setColor(ContextCompat.getColor(context, R.color.game_def_tile_color));
        paintFields.setStyle(Paint.Style.FILL);

        paintTiles = new Paint();
        paintTiles.setStyle(Paint.Style.FILL);

        paintText = new Paint();
        paintText.setColor(Color.WHITE);
        paintText.setStyle(Paint.Style.FILL);
        paintText.setTextAlign(Paint.Align.CENTER);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int w = MeasureSpec.getSize(widthMeasureSpec);
        int h = MeasureSpec.getSize(heightMeasureSpec);
        int d = w == 0 ? h : h == 0 ? w : w < h ? w : h;

        setMeasuredDimension(d, d);

    }

    public void setTilePositions(TilePosition[] tilePositions) {
        this.tilePositions = tilePositions;
    }

    public TilePosition[] getTilePositions() {
        return tilePositions;
    }

    public int getMARGIN() {
        return MARGIN;
    }

    public int getFIELD_SIZE() {
        return FIELD_SIZE;
    }

    public void resetTiles() {
        SIZE = gameTableModel.getSize();
        FIELD_SIZE = calculateFieldSize(SIZE);
        initTilePositions();
        invalidate();
    }


    public void initTextSize() {
        BASE_TEXT_SIZE *= ((double) getWidth() / MAGIC_WIDTH);
    }
}
