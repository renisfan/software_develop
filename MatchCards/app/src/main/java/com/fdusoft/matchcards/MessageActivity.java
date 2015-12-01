package com.fdusoft.matchcards;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.view.TextureView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.text.Html;
import android.widget.Toast;

import java.lang.ref.WeakReference;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by zhanghanyuan on 15/11/30.
 */
public class MessageActivity extends Activity {

    private TextView myTextView;
    private SQLiteDatabase db;
    private static String username = null;
    private EditText ename;
    private EditText econtent;

    protected void onCreate(Bundle savedInstanceState) {
        // Initialize layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_message);

        // Get the user info
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        username = bundle.getString("USER_NAME");


        myTextView = (TextView) findViewById(R.id.oldMessage);

        db = SQLiteDatabase.openOrCreateDatabase(MessageActivity.this.getFilesDir().toString()
                + "/test.dbs", null);

        String query = "select * from "+username+"_oldMessage";

        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            String str = "";
            int cnt = 0;

            for (cursor.moveToFirst(); !cursor.isAfterLast();cursor.moveToNext()) {

                String detail = cursor.getString(cursor.getColumnIndex("detail"));
                String sender = cursor.getString(cursor.getColumnIndex("sender"));

                str = "<font color='red' align = 'left'> *      "+ detail + "<br><br></font>"+
                        "<font color='gray' align = 'right'> ---from &nbsp;&nbsp; "+sender+"</font>  <br> <br>" + str;

                cnt ++;

                if (cnt > 5) break;
            }


            myTextView.setText(Html.fromHtml(str));

            query = "delete  from "+username+"_oldMessage";
            db.execSQL(query);

        }

        ename = (EditText)findViewById(R.id.username);
        econtent = (EditText)findViewById(R.id.content);
        Button messageButton = (Button) findViewById(R.id.send);

        db = SQLiteDatabase.openOrCreateDatabase(MessageActivity.this.getFilesDir().toString()
                + "/test.dbs", null);

        messageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = ename.getText().toString();
                String content = econtent.getText().toString();

                if (!(ename.getText().toString().isEmpty() || econtent.getText().toString()
                        .isEmpty())) {


                    try {
                        db.execSQL("create table " + name + "_oldMessage( detail varchar(200), sender varchar(20))");
                    } catch (SQLiteException e) {
                    }

                    try {
                        db.execSQL("insert into " + name + "_oldMessage values(?,?)", new Object[]{content, username});
                    } catch (SQLiteException e) {
                    }


                    Toast.makeText(MessageActivity.this,
                            getString(R.string.send_success),
                            Toast.LENGTH_SHORT).show();

                } else {
                    new AlertDialog.Builder(MessageActivity.this)
                            .setTitle("ERROR")
                            .setMessage(getString(R.string.error_message_empty))
                            .setPositiveButton(getString(R.string.action_confirm), null)
                            .show();
                }
            }



        });
    }
}
