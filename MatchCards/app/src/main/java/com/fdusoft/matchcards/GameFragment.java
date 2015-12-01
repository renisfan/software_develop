package com.fdusoft.matchcards;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Fragment for game interface
 */
public class GameFragment extends Fragment {

    private static final String LOGTAG = "MatchCards";

    private MainActivity currentMainActivity;

    private int gameChance;
    private int highScore = 0;
    private String username;

    private TextView gameChanceHint;
    private TextView highScoreHint;

    private SQLiteDatabase db;

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

    private void getUserInfo(String username) {

        this.username = username;
        //Initialize database
        db = SQLiteDatabase.openOrCreateDatabase(currentMainActivity.getFilesDir().toString()
                + "/test.dbs", null);

        //Get gameChance
        Cursor cursor = db.rawQuery("select * from tb_chance where name=?", new String[]{username});
        cursor.moveToFirst();
        this.gameChance = cursor.getInt(cursor.getColumnIndex("chance"));
        //Get highScore
        cursor = db.rawQuery("select * from tb_score where name=?",new String[]{username});
        cursor.moveToFirst();
        this.highScore = cursor.getInt(cursor.getColumnIndex("highScore"));
    }

    public void updateGameChance(int newGameChance) {
        gameChance = newGameChance;
        gameChanceHint.setText(String.format(getString(R.string.game_chance), gameChance));
        db.execSQL("update tb_chance set chance=? where name=?", new Object[]{gameChance, username});
    }

    public void updateHighScore(int newScore) {
        highScore = Math.max(highScore, newScore);
        highScoreHint.setText(String.format(getString(R.string.high_score), highScore));
        db.execSQL("update tb_score set highScore=? where name=?", new Object[]{newScore, username});
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOGTAG,"GameFragment.onCreateView()");
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
                    updateGameChance(gameChance - 1);
                    currentMainActivity.checkChance();
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), GameActivity.class);
                    getActivity().startActivityForResult(intent, MainActivity.REQUEST_GAME);
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        Log.d(LOGTAG, "GameFragment.onDestroy()");
        db.close();
        super.onDestroy();
    }
}
