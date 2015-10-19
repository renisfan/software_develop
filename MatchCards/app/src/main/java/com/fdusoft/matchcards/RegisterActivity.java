package com.fdusoft.matchcards;

import android.app.Activity;
import android.app.AlertDialog;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Activity for register new user.
 */
public class RegisterActivity extends Activity {

    private SQLiteDatabase db;
    private EditText ename;
    private EditText epassword;
    private EditText epassword2;

    @Override
    protected void onDestroy() {
        db.close();
        super.onDestroy();
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

                String name = ename.getText().toString();
                String password = epassword.getText().toString();

                if (!(ename.getText().toString().equals("") || epassword.getText().toString()
                        .equals("") || epassword2.getText().toString().equals(""))) {

                    if (epassword.getText().toString().equals(epassword2.getText().toString())) {

                        if (addUser(name, password)) {
                            Toast.makeText(RegisterActivity.this,
                                    getString(R.string.register_success),
                                    Toast.LENGTH_SHORT).show();
                            RegisterActivity.this.finish();
                        } else {
                            new AlertDialog.Builder(RegisterActivity.this)
                                    .setTitle("ERROR")
                                    .setMessage(getString(R.string.error_name_used))
                                    .setPositiveButton(getString(R.string.action_confirm), null)
                                    .show();
                        }

                    } else {
                        new AlertDialog.Builder(RegisterActivity.this)
                                .setTitle("ERROR")
                                .setMessage(getString(R.string.error_diffrent_password))
                                .setPositiveButton(getString(R.string.action_confirm), null).show();
                    }
                } else {
                    new AlertDialog.Builder(RegisterActivity.this)
                            .setTitle("ERROR")
                            .setMessage(getString(R.string.error_empty))
                            .setPositiveButton(getString(R.string.action_confirm), null).show();
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
