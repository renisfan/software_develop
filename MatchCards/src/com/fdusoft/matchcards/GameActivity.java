package com.fdusoft.matchcards;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class GameActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// Initialize layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        
        // Test code: generate random score and return
        Random random = new Random();
        int score = random.nextInt(100)+1;
        TextView test = (TextView) findViewById(R.id.test);
        test.setText(String.format("Game Activity Preview\nRandom score: %d", score));
        
        Intent intent = new Intent();
        Bundle data = new Bundle();
        data.putInt("GAME_SCORE", score);
        intent.putExtras(data);
        GameActivity.this.setResult(0, intent);
        
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				GameActivity.this.finish();
			}
		}, 3000);
        
    }
    
}
