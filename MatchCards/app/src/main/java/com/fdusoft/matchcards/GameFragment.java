package com.fdusoft.matchcards;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.util.Log;
import android.os.Handler;
import android.os.Message;

import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Fragment for game interface
 */
public class GameFragment extends Fragment {

    private int gameChance;
    private int highScore = 0;
    private String username;
    private MainActivity currentMainActivity;

    private TextView gameChanceHint;
    private TextView highScoreHint;

    private SQLiteDatabase db;

    private String TAG = "myLogs";

        /**
     * Create game fragment for the given user.
     * @param username Player's username.
     */
    public static GameFragment getGameFragment(String username,MainActivity currentMainActivity) {
        GameFragment fragment = new GameFragment();
        fragment.currentMainActivity = currentMainActivity;
        fragment.getUserInfo(username);
        return fragment;
    }

    public GameFragment() {}

    public int getTime() {
        GregorianCalendar now = new GregorianCalendar(),
                start = new GregorianCalendar(2014,1,1);
        long diff = now.getTime().getTime() - start.getTime().getTime();
        long sec = TimeUnit.MILLISECONDS.toSeconds(diff);
        return (int)sec;
    }
    private void getUserInfo(String username) {
        // TODO: get highScore from database
        db = SQLiteDatabase.openOrCreateDatabase(currentMainActivity.getFilesDir().toString()
                + "/test.dbs", null);
        Cursor cursor = db.rawQuery("select * from tb_chance where name=?",new String[]{username});
        cursor.moveToFirst();
        this.username = username;
        this.gameChance = cursor.getInt(cursor.getColumnIndex("chance"));
    }

    public void awardGameChance(int newGameChance) {
        gameChance = newGameChance;
        gameChanceHint.setText(String.format(getString(R.string.game_chance), gameChance));
        int now = getTime();
        Log.e(TAG,"GameFragment Time " + now);
        db.execSQL("update tb_chance set chance=?,time=? where name=?", new Object[]{gameChance,now,username});
    }
    public void costGameChance(int newGameChance) {
        gameChance = newGameChance;
        gameChanceHint.setText(String.format(getString(R.string.game_chance), gameChance));
        db.execSQL("update tb_chance set chance=? where name=?", new Object[]{gameChance, username});
    }

    public void updateHighScore(int newScore) {
        highScore = Math.max(highScore, newScore);
        // TODO: update user database
        highScoreHint.setText(String.format(getString(R.string.high_score), highScore));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.e(TAG,"GameFragment.onCreateView()");
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        gameChanceHint = (TextView) view.findViewById(R.id.game_chance_hint);
        gameChanceHint.setText(String.format(getString(R.string.game_chance), gameChance));
        highScoreHint = (TextView) view.findViewById(R.id.high_score_hint);
        highScoreHint.setText(String.format(getString(R.string.high_score), highScore));

        Button startGameButton = (Button) view.findViewById(R.id.start_game);
        startGameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (gameChance <= 0) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "游戏次数不足!", Toast.LENGTH_SHORT).show();
                } else {
                    costGameChance(gameChance - 1);
                    gameChanceHint.setText(String.format(getString(R.string.game_chance), gameChance));
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), GameActivity.class);
                    getActivity().startActivityForResult(intent, MainActivity.REQUEST_GAME);
                }
            }
        });
        return view;
    }

    public void onPause() {
        super.onPause();
        Log.e(TAG,"GameFragment.onPause()");
    }

    public void onStop() {
        super.onStop();
        Log.e(TAG,"GameFragment.onStop()");
    }
    public void onResume() {
        super.onResume();
        Log.e(TAG, "GameFragment.onResume()");
    }
    public void onDestroy() {
        super.onDestroy();
        db.close();
        Log.e(TAG,"GameFragment.onDestroy()");
    }
}
