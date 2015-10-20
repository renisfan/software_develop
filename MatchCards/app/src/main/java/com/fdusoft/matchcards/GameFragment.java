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
import android.os.Handler;
import android.os.Message;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Fragment for game interface
 */
public class GameFragment extends Fragment {

    private int gameChance = 5;
    private int highScore = 0;
    private int status = 0;

    private TextView gameChanceHint;
    private TextView highScoreHint;

    private SQLiteDatabase db;

    private final Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    updateGameChance(gameChance+1);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private TimerTask task = new TimerTask(){
        public void run() {
            status = 0;
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };
    private Timer timer;
    private int gap = 300000;

        /**
     * Create game fragment for the given user.
     * @param username Player's username.
     */
    public static GameFragment getGameFragment(String username) {
        GameFragment fragment = new GameFragment();
        fragment.getUserInfo(username);
        return fragment;
    }

    public GameFragment() {}

    private void getUserInfo(String username) {
        // TODO: get gameChance and highScore from database
       // db = SQLiteDatabase.openOrCreateDatabase(GameFragment.this.getFilesDir().toString()
        //        + "/test.dbs", null);
        //String str="select * from tb_user where name=?";
        //Cursor cursor = db.rawQuery(str, new String []{username});
    }

    public void updateGameChance(int newGameChance) {
        gameChance = newGameChance;
        // TODO: update user database
        gameChanceHint.setText(String.format(getString(R.string.game_chance), gameChance));
    }

    public void updateHighScore(int newScore) {
        highScore = Math.max(highScore, newScore);
        // TODO: update user database
        highScoreHint.setText(String.format(getString(R.string.high_score), highScore));
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);
        gameChanceHint = (TextView) view.findViewById(R.id.game_chance_hint);
        gameChanceHint.setText(String.format(getString(R.string.game_chance), gameChance));
        highScoreHint = (TextView) view.findViewById(R.id.high_score_hint);
        highScoreHint.setText(String.format(getString(R.string.high_score), highScore));

        timer = new Timer(true);
        timer.schedule(task, gap, gap);

        Button startGameButton = (Button) view.findViewById(R.id.start_game);
        startGameButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (gameChance <= 0) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "游戏次数不足!", Toast.LENGTH_SHORT).show();
                } else {
                    status = 1;
                    updateGameChance(gameChance - 1);
                    gameChanceHint.setText(String.format(getString(R.string.game_chance), gameChance));
                    Intent intent = new Intent();
                    intent.setClass(getActivity(), GameActivity.class);
                    getActivity().startActivityForResult(intent, MainActivity.REQUEST_GAME);
                }
            }
        });
        return view;
    }
    public void onStop() {
        if (status==0) {
            timer.cancel();
        }
        super.onStop();
    }
}
