package com.fdusoft.matchcards;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.database.Cursor;
import android.app.AlertDialog;
import android.database.sqlite.SQLiteException;

public class LoginActivity extends Activity {

	private SQLiteDatabase db;


	protected void onDestroy() {
		super.onDestroy();
		db.close();
	}
	
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

		db = SQLiteDatabase.openOrCreateDatabase(LoginActivity.this.getFilesDir().toString()
				+ "/test.dbs", null);

		db.execSQL("delete from tb_user");

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
						if (isUserinfo(username, password) == true) {
							Intent intent = new Intent();
							// Package user info and pass to MainActivity
							Bundle bundle = new Bundle();
							bundle.putString("USER_NAME", username);
							intent.putExtras(bundle);
							// Switch activity
							intent.setClass(LoginActivity.this, MainActivity.class);
							LoginActivity.this.startActivity(intent);
							LoginActivity.this.finish();
						} else {
							Toast.makeText(getApplicationContext(), "账号密码错误!",
									Toast.LENGTH_SHORT).show();
						}
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

	public Boolean isUserinfo(String name, String pwd) {
		try{
			String str="select * from tb_user where name=? and password=?";
			Cursor cursor = db.rawQuery(str, new String []{name,pwd});
			if(cursor.getCount()<=0){
				return false;
			}else{
				Toast.makeText(getApplicationContext(), "登录成功!",
						Toast.LENGTH_SHORT).show();
				return true;
			}

		}catch(SQLiteException e){
		}
		return false;
	}

}

