package com.fdusoft.matchcards;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

public class GameActivity extends Activity {
	
	private int cardCount = 20;
	private int level = 4;
	private int score = 0;
	private GridView gridView;
	private CardAdapter cardAdapter;
	private TextView scoreText;
	
	private int[] pattern = new int[50];
	
	private int[] card = new int[50];
	private int[] p = new int[50];
	private int available;
	private Random random;
	
	private CardItem firstCard = null;
	private CardItem secondCard = null;
			
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// Initialize layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        // Test code: generate random score and return
        scoreText = (TextView) findViewById(R.id.score_text);
        scoreText.setText(String.format(getString(R.string.game_score), score));
        
        DisplayMetrics dMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dMetrics);
        int width = dMetrics.widthPixels;
        
        cardAdapter = new CardAdapter(getApplicationContext(), level, width);
        
        initializeCardPool();
        random = new Random();
        for (int i=1; i<=10; ++i) {
        	pattern[takeRandomCard()]=i;
        	pattern[takeRandomCard()]=i;
        }
        
        gridView = (GridView) findViewById(R.id.gridview);
        gridView.setNumColumns(level);
        gridView.setAdapter(cardAdapter);
        gridView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				if (secondCard != null) {
					firstCard.setState(0);
					secondCard.setState(0);
					firstCard=null;
					secondCard=null;					
				}
				CardItem item = (CardItem) cardAdapter.getItem(position);
				if (!item.isOpen()) {
					item.setState(pattern[position]);
					if (firstCard == null) {
						firstCard = item;
					} else {
						secondCard = item;
    					if (firstCard.getState() == secondCard.getState()) {
    						score += 100;
    						scoreText.setText(String.format(getString(R.string.game_score),
    								score));
    						firstCard=null;
    						secondCard=null;
    					}
					}
				}
				cardAdapter.notifyDataSetChanged();
			}
        	
		});
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
		        Intent intent = new Intent();
		        Bundle data = new Bundle();
		        data.putInt("GAME_SCORE", score);
		        intent.putExtras(data);
		        GameActivity.this.setResult(0, intent);
				GameActivity.this.finish();
			}
		}, 30 * 1000);
                
    }
    
    private void initializeCardPool() {
    	available = cardCount;
    	for(int i=0;i<cardCount;++i) {
    		card[i]=p[i]=i;
    	}
    }
    
    private int takeRandomCard() {
    	int pos = random.nextInt(available);
    	int cardNo = card[pos];
    	int exchangeCard = card[available-1];
    	p[cardNo] = available-1;
    	p[exchangeCard] = pos;
    	card[available-1] = cardNo;
    	card[pos] = exchangeCard;
    	--available;
    	return cardNo;
    }
    
}
