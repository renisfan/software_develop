package com.fdusoft.matchcards;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
    	// Initialize layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        // Get components
        final EditText usernameText = (EditText) findViewById(R.id.username);
        final EditText passwordText = (EditText) findViewById(R.id.password);
        Button loginButton = (Button) findViewById(R.id.login_button);
        Button registerButton = (Button) findViewById(R.id.register_button);
        
        //Set listener
        loginButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				String username = usernameText.getText().toString();
				String password = passwordText.getText().toString();
				if (username.isEmpty()) {
					Toast.makeText(getApplicationContext(), "请输入用户名！",
							Toast.LENGTH_SHORT).show();
				} else {
					if (password.isEmpty()) {
						Toast.makeText(getApplicationContext(), "请输入密码！",
								Toast.LENGTH_SHORT).show();
					} else {
						// TODO: add login check
						// If username and password correct, switch to MainActivity
						Intent intent = new Intent();
						// Package user info and pass to MainActivity
						Bundle bundle = new Bundle();
						bundle.putString("USER_NAME", username);
						intent.putExtras(bundle);
						// Switch activity
						intent.setClass(LoginActivity.this, MainActivity.class);
						LoginActivity.this.startActivity(intent);
						LoginActivity.this.finish();
					}
				}
			}
		});
        registerButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, RegisterActivity.class);
				LoginActivity.this.startActivity(intent);
			}
		});
        
    }
    
}
