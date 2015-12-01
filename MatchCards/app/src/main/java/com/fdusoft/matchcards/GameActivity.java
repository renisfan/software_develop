package com.fdusoft.matchcards;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class GameActivity extends Activity {

    private static final int[] levelCol  = { 4,  5,  5,  5,  5,  6,  6,  6,  6};
    private static final int[] levelSize = {12, 20, 20, 30, 30, 42, 42, 42, 42};
    private static final int[] levelTime = {45, 60, 45, 90, 60, 90, 60, 45, 30};

    private static final int EVENT_CLOSE_CARD = 0;
    private static final int EVENT_TIME_UP = 1;

	private int cardCount;
	private int level = 0;
    private int displayLevel = 1;
	private int score = 0;
	private GridView gridView;
	private CardAdapter cardAdapter;
	private TextView scoreText;
	private TextView levelText;
	
	private int[] pattern = new int[50];
    private static final int TOTAL_PATTERN = 10;
	
	private int[] card = new int[50];
	private int available;

    private int width;
    private int left;
    private boolean waiting;

	private Random random;
    private Timer timer;
    private TimerHandler handler;

	private CardItem firstCard = null;
	private CardItem secondCard = null;

    private static class TimerHandler extends Handler {

        private WeakReference<GameActivity> mOuter;

        public TimerHandler(GameActivity activity) {
            mOuter = new WeakReference<GameActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            GameActivity activity = mOuter.get();
            switch (msg.what) {
                case EVENT_CLOSE_CARD:
                    activity.closeCards();
                    break;
                case EVENT_TIME_UP:
                    activity.showResultDialog();
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private void closeCards() {
        firstCard.setState(0);
        secondCard.setState(0);
        firstCard = null;
        secondCard = null;
        cardAdapter.notifyDataSetChanged();
    }

    private void showResultDialog() {
        new AlertDialog.Builder(GameActivity.this).setTitle("时间到！")
                .setMessage(String.format("您的分数:%d", score))
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        waiting = false;
                    }
                })
                .setNeutralButton("分享", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: implement share
                        Intent intent=new Intent(Intent.ACTION_SEND);
                        intent.setType("text/plain");
                        intent.putExtra(Intent.EXTRA_SUBJECT, "分享");
                        intent.putExtra(Intent.EXTRA_TEXT, String.format(getString(R.string.share_text), score));
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(Intent.createChooser(intent, getTitle()));
                        showResultDialog();
                    }
                })
                .setCancelable(false).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// Initialize layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        DisplayMetrics dMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
        width = dMetrics.widthPixels;

        handler = new TimerHandler(GameActivity.this);

        random = new Random();

        scoreText = (TextView) findViewById(R.id.score_text);
        levelText = (TextView) findViewById(R.id.level_text);
        gridView = (GridView) findViewById(R.id.gridview);

        startGame();

    }

    @Override
    public void onBackPressed()
    {
        new AlertDialog.Builder(GameActivity.this).setTitle("确定要放弃吗？")
                .setMessage("您的成绩将不会被保存")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        timer.cancel();
                        GameActivity.this.setResult(RESULT_CANCELED);
                        GameActivity.super.onBackPressed();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).show();
    }

    private void startGame() {

        scoreText.setText(String.format(getString(R.string.game_score), score));
        levelText.setText(String.format(getString(R.string.game_level), displayLevel, levelTime[level]));

        cardAdapter = new CardAdapter(getApplicationContext(), levelCol[level], levelSize[level], width);

        cardCount = cardAdapter.getCount();

        // Generate random game pool
        initializeCardPool();
        for (int i=1; i<=cardCount/2; ++i) {
            pattern[takeRandomCard()]=(i-1)%TOTAL_PATTERN+1;
            pattern[takeRandomCard()]=(i-1)%TOTAL_PATTERN+1;
        }
        left = cardCount;

        gridView.setNumColumns(levelCol[level]);
        gridView.setAdapter(cardAdapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // Do nothing if the last pair of mismatched cards aren't closed yet
                if (secondCard != null) {
                    return;
                }
                CardItem item = (CardItem) cardAdapter.getItem(position);
                if (!item.isOpen()) {
                    // Open the card
                    item.setState(pattern[position]);
                    if (firstCard == null) {
                        firstCard = item;
                    } else {
                        secondCard = item;
                        if (firstCard.getState() == secondCard.getState()) {
                            // Add score if the 2 cards matches, leave them open.
                            score += 10;
                            scoreText.setText(String.format(getString(R.string.game_score),
                                    score));
                            firstCard = null;
                            secondCard = null;
                            // Move to next level if all cards are matched
                            left -= 2;
                            if (left == 0) {
                                timer.cancel();
                                new AlertDialog.Builder(GameActivity.this)
                                        .setTitle("恭喜过关！")
                                        .setPositiveButton("进入下一关", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ++displayLevel;
                                                level = Math.min(level+1, levelSize.length-1);
                                                GameActivity.this.startGame();
                                            }
                                        }).show();
                            }
                        } else {
                            // Close mismatched cards after a while
                            Timer closeTimer = new Timer();
                            closeTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Message message = new Message();
                                    message.what = EVENT_CLOSE_CARD;
                                    handler.sendMessage(message);
                                }
                            }, 500);
                        }
                    }
                }
                // Refresh grid view
                cardAdapter.notifyDataSetChanged();
            }

        });

        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                waiting = true;
                // Call handler to open result confirmation dialog
                Message message = new Message();
                message.what = EVENT_TIME_UP;
                handler.sendMessage(message);
                // Wait for the dialog to close
                while (waiting) {}
                // Send the result back and finish
                Intent intent = new Intent();
                Bundle data = new Bundle();
                data.putInt("GAME_SCORE", score);
                intent.putExtras(data);
                GameActivity.this.setResult(RESULT_OK, intent);
                GameActivity.this.finish();
            }
        }, levelTime[level] * 1000);

    }
    
    private void initializeCardPool() {
    	available = cardCount;
    	for(int i=0;i<cardCount;++i) {
    		card[i]=i;
    	}
    }
    
    private int takeRandomCard() {
    	int pos = random.nextInt(available);
    	int cardNo = card[pos];
    	int exchangeCard = card[available-1];
    	card[available-1] = cardNo;
    	card[pos] = exchangeCard;
    	--available;
    	return cardNo;
    }
    
}
