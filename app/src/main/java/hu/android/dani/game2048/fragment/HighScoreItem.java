package hu.android.dani.game2048.fragment;

import com.orm.SugarRecord;

import java.util.Date;


public class HighScoreItem extends SugarRecord {
    public String name;
    public int score;
    public Date date;
    public int fieldSize;
    public boolean isWin;

}
