package hu.android.dani.game2048;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.health.ServiceHealthStats;

import java.util.Date;

import hu.android.dani.game2048.model.GameTableModel;

public class SPHelper {
    private static SPHelper instance = new SPHelper();

    public static SPHelper getInstance() {
        return instance;
    }

    private SPHelper() {
    }
    SharedPreferences sp;

    public static final String PREF = "PREF_NEW";
    public static final String PLAYER_NAME = "PLAYER_NAME";
    public static final String PLAYER_SCORE = "PLAYER_SCORE";
    public static final String GAME_SIZE = "GAME_SIZE";
    public static final String GAME_WIN = "GAME_WIN";
    public static final String GAME_DATE = "GAME_DATE";
    public static final String HIGH_SCORE_3 = "HIGH_SCORE_3";
    public static final String HIGH_SCORE_4 = "HIGH_SCORE_4";
    public static final String HIGH_SCORE_5 = "HIGH_SCORE_5";
    public static final String HIGH_SCORE_6 = "HIGH_SCORE_6";

    public void init(Context context) {
        sp = context.getSharedPreferences(PREF,Context.MODE_PRIVATE);
    }

    public String getPlayerName() {
        return sp.getString(PLAYER_NAME,"");
    }

    public int getPlayerScore() {
        return sp.getInt(PLAYER_SCORE,0);
    }

    public int getGameSize() {
        return sp.getInt(GAME_SIZE,0);
    }

    public boolean getPlayerWin() {
        return sp.getBoolean(GAME_WIN,false);
    }

    public Date getGameDate() {
        return new Date(sp.getLong(GAME_DATE,0));
    }

    public int getHighScore() {
        switch (GameTableModel.getInstance().getSize()) {
            case 3:
                return sp.getInt(HIGH_SCORE_3,0);
            case 4:
                return sp.getInt(HIGH_SCORE_4,0);
            case 5:
                return sp.getInt(HIGH_SCORE_5,0);
            case 6:
                return sp.getInt(HIGH_SCORE_6,0);
            default:
                return 0;
        }
    }


    public void clear() {
        SharedPreferences.Editor editor = sp.edit();
        editor.clear();
        editor.apply();
    }

    public void putInt(String KEY, int i) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putInt(KEY,i);
        editor.apply();
    }

    public void putString(String KEY, String s) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putString(KEY,s);
        editor.apply();
    }

    public void putBoolean(String KEY, boolean win) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putBoolean(KEY,win);
        editor.apply();
    }

    public void putLong(String KEY, long date) {
        SharedPreferences.Editor editor = sp.edit();
        editor.putLong(KEY,date);
        editor.apply();
    }

    public void delete(String KEY) {
        SharedPreferences.Editor editor = sp.edit();
        editor.remove(KEY);
        editor.apply();
    }
}
