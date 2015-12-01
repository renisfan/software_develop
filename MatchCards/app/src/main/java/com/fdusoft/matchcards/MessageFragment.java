package com.fdusoft.matchcards;


import android.app.AlertDialog;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Created by zhanghanyuan on 15/11/29.
 */
public class MessageFragment extends Fragment {

    private SQLiteDatabase db;
    private String myName;
    private EditText ename;
    private EditText econtent;

    public static MessageFragment getMessageFragment(String username) {
        MessageFragment fragment = new MessageFragment();
        fragment.myName = username;
        return fragment;
    }

    public MessageFragment() {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_message, container, false);
        TextView myTextView = (TextView) view.findViewById(R.id.oldMessage);

        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString()
                + "/test.dbs", null);

        String query = "select * from " + myName + "_oldMessage";
        Cursor cursor = db.rawQuery(query, null);

        if (cursor.moveToFirst()) {

            String str = "";
            int cnt = 0;
            for (cursor.moveToFirst(); !cursor.isAfterLast();cursor.moveToNext()) {

                String detail = cursor.getString(cursor.getColumnIndex("detail"));
                String sender = cursor.getString(cursor.getColumnIndex("sender"));
                str = "<font color='red' align = 'left'> *      " + detail + "<br><br></font>"
                        + "<font color='gray' align = 'right'> ---from &nbsp;&nbsp; "
                        + sender + "</font>  <br> <br>" + str;
                cnt ++;
                if (cnt > 5) break;
            }

            myTextView.setText(Html.fromHtml(str));

            query = "delete  from " + myName + "_oldMessage";
            db.execSQL(query);

        }

        ename = (EditText)view.findViewById(R.id.send_message_username);
        econtent = (EditText)view.findViewById(R.id.send_message_content);
        Button messageButton = (Button) view.findViewById(R.id.send_button);

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
                        db.execSQL("insert into " + name + "_oldMessage values(?,?)", new Object[]{content, myName});
                    } catch (SQLiteException e) {
                    }

                    Toast.makeText(getActivity(),
                            getString(R.string.send_success),
                            Toast.LENGTH_SHORT).show();

                } else {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("ERROR")
                            .setMessage(getString(R.string.error_message_empty))
                            .setPositiveButton(getString(R.string.action_confirm), null)
                            .show();
                }
            }
        });

        return view;
    }
}
