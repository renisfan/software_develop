package com.fdusoft.matchcards;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Register template
 */
public class RegisterActivity extends Activity {

    private SQLiteDatabase db;
    private Button btregister1;
    private EditText ename;
    private EditText epassword;
    private EditText epassword2;

    protected void onDestroy() {
        super.onDestroy();
        db.close();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initialize layout
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        ename = (EditText)findViewById(R.id.eusername);
        epassword = (EditText)findViewById(R.id.epassword);
        epassword2 = (EditText)findViewById(R.id.epassword2);
        db = SQLiteDatabase.openOrCreateDatabase(RegisterActivity.this.getFilesDir().toString()
                + "/test.dbs", null);



        Button registerButton = (Button) findViewById(R.id.button);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: implement register

                String name = ename.getText().toString();
                String password = epassword.getText().toString();

                if (!(ename.getText().toString().equals("") || epassword.getText().toString()
                        .equals("") || epassword2.getText().toString().equals(""))) {

                    if (epassword.getText().toString().equals(epassword2.getText().toString())) {

                        if (addUser(name, password)) {
                            Toast.makeText(RegisterActivity.this, "注册成功",
                                    Toast.LENGTH_SHORT).show();
                        //    Intent intent = new Intent();
                        //    intent.setClass(RegisterActivity.this, LoginActivity.class);
                        //    RegisterActivity.this.startActivity(intent);
                            RegisterActivity.this.finish();
                        } else {
                            new AlertDialog.Builder(RegisterActivity.this)
                                    .setTitle("ERROR").setMessage("该用户名已被注册")
                                    .setPositiveButton("确定", null).show();
                        }

                    } else {
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("ERROR").setMessage("两次密码不相等")
                                .setPositiveButton("确定", null).show();
                    }
                } else {
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("ERROR").setMessage("帐号密码不能为空")
                            .setPositiveButton("确定", null).show();
                }



            }
        });

    }

    public boolean addUser(String name,String password) {
        String str = "insert into tb_user values(?,?) ";
        try {
            db.execSQL(str, new String[] { name, password });
            return true;
        } catch (Exception e) {
            try {
                db.execSQL("create table tb_user( name varchar(30) primary key,password varchar(30))");
            } catch (Exception e1) {

            }
        }
        return false;
    }

}
