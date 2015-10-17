package com.fdusoft.matchcards;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

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

public class GameActivity extends Activity {

    private static final int[] levelSize = {5, 5, 6, 6, 7, 7};
    private static final int[] levelTime = {45, 30, 60, 45, 90, 60};

    private static Handler handler;

	private int cardCount;
	private int level = 1;
	private int score = 0;
	private GridView gridView;
	private CardAdapter cardAdapter;
	private TextView scoreText;
	private TextView levelText;
	
	private int[] pattern = new int[50];
	
	private int[] card = new int[50];
	private int available;

    private int width;
    private int left;
    private boolean waiting;

	private Random random;
    private Timer timer;

	private CardItem firstCard = null;
	private CardItem secondCard = null;
			
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// Initialize layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        DisplayMetrics dMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
        width = dMetrics.widthPixels;

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                switch (msg.what) {
                    case 0:
                        firstCard.setState(0);
                        secondCard.setState(0);
                        firstCard = null;
                        secondCard = null;
                        cardAdapter.notifyDataSetChanged();
                        break;
                    case 1:
                        new AlertDialog.Builder(GameActivity.this).setTitle("时间到！")
                                .setMessage(String.format("您的分数:%d", score))
                                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        waiting=false;
                                    }
                                })
                                .setNeutralButton("分享", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // TODO: implement share
                                        waiting=false;
                                    }
                                }).show();
                        break;
                }
                super.handleMessage(msg);
            }
        };

        random = new Random();

        scoreText = (TextView) findViewById(R.id.score_text);
        levelText = (TextView) findViewById(R.id.level_text);
        gridView = (GridView) findViewById(R.id.gridview);

        startGame();

    }

    private void startGame() {

        scoreText.setText(String.format(getString(R.string.game_score), score));
        levelText.setText(String.format(getString(R.string.game_level), level, levelTime[level - 1]));

        cardAdapter = new CardAdapter(getApplicationContext(), levelSize[level-1], width);

        cardCount = cardAdapter.getCount();

        initializeCardPool();
        for (int i=1; i<=cardCount/2; ++i) {
            pattern[takeRandomCard()]=(i-1)%10+1;
            pattern[takeRandomCard()]=(i-1)%10+1;
        }
        left = cardCount;

        gridView.setNumColumns(levelSize[level - 1]);
        gridView.setAdapter(cardAdapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO Auto-generated method stub
                if (secondCard != null) {
                    return;
                }
                CardItem item = (CardItem) cardAdapter.getItem(position);
                if (!item.isOpen()) {
                    item.setState(pattern[position]);
                    if (firstCard == null) {
                        firstCard = item;
                    } else {
                        secondCard = item;
                        if (firstCard.getState() == secondCard.getState()) {
                            score += 10;
                            scoreText.setText(String.format(getString(R.string.game_score),
                                    score));
                            firstCard = null;
                            secondCard = null;
                            left -= 2;
                            if (left == 0) {
                                timer.cancel();
                                new AlertDialog.Builder(GameActivity.this)
                                        .setTitle("恭喜过关！")
                                        .setPositiveButton("进入下一关", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                ++level;
                                                GameActivity.this.startGame();
                                            }
                                        }).show();
                            }
                        } else {
                            Timer closeTimer = new Timer();
                            closeTimer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    Message message = new Message();
                                    message.what = 0;
                                    handler.sendMessage(message);
                                }
                            }, 500);
                        }
                    }
                }
                cardAdapter.notifyDataSetChanged();
            }

        });

        timer = new Timer();
        timer.schedule(new TimerTask() {

            @Override
            public void run() {
                waiting = true;
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
                while (waiting) {}
                Intent intent = new Intent();
                Bundle data = new Bundle();
                data.putInt("GAME_SCORE", score);
                intent.putExtras(data);
                GameActivity.this.setResult(0, intent);
                GameActivity.this.finish();
            }
        }, levelTime[level-1] * 1000);

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
