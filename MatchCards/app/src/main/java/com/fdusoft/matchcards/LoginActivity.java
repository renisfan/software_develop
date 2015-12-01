package com.fdusoft.matchcards;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;

public class LoginActivity extends Activity {

    private SQLiteDatabase db;

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
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

        //delete former database
     /*   try {
           String myPath = LoginActivity.this.getFilesDir().toString()
                   + "/test.dbs";
           SQLiteDatabase.deleteDatabase(new File(myPath));
        }catch(SQLiteException e) {

        } */

        db = SQLiteDatabase.openOrCreateDatabase(LoginActivity.this.getFilesDir().toString()
                + "/test.dbs", null);


		//create table tb_user
		try {
			db.execSQL("create table tb_user( name varchar(30) primary key,password varchar(30))");
		}catch(SQLiteException e){
		}
		//create table tb_score
		try {
			db.execSQL("create table tb_score(name varchar(30) primary key,highScore int)");
		}catch(SQLiteException e) {
		}

        try {
            db.execSQL("create table tb_chance(name varchar(30) primary key,chance int,time int)");
        }catch(SQLiteException e) {
        }

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
                            if (db.rawQuery("select * from tb_chance where name=?",new String[]{username}).getCount()==0) {
                                db.execSQL("insert into tb_chance values(?,?,?)",new Object[]{username,5,getTime()});
                            }
                            Intent intent = new Intent();
                            // Package user info and pass to MainActivity
                            Bundle bundle = new Bundle();
                            bundle.putString("USER_NAME", username);
                            intent.putExtras(bundle);
                            // Switch activity
                            intent.setClass(LoginActivity.this, MainActivity.class);
                            LoginActivity.this.startActivity(intent);
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
    public int getTime() {
        GregorianCalendar now = new GregorianCalendar(),
                start = new GregorianCalendar(2014,1,1);
        long diff = now.getTime().getTime() - start.getTime().getTime();
        long sec = TimeUnit.MILLISECONDS.toSeconds(diff);
        return (int)sec;
    }
    public Boolean isUserinfo(String name, String pwd) {
        try {
            String str = "select * from tb_user where name=? and password=?";
            Cursor cursor = db.rawQuery(str, new String[]{name, pwd});
            if (cursor.getCount() <= 0) {
                return false;
            } else {
                Toast.makeText(getApplicationContext(), "登录成功!",
                        Toast.LENGTH_SHORT).show();
                return true;
            }

        } catch (SQLiteException e) {
        }
        return false;
    }

}

