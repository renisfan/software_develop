package com.fdusoft.matchcards;


import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by zhanghanyuan on 15/11/29.
 */
public class MessageFragment extends Fragment {

    private TextView myTextView;
    private SQLiteDatabase db;
    private String myName;

    public static MessageFragment getMessageFragment(String username) {
        MessageFragment fragment = new MessageFragment();
        fragment.myName = username;
       // fragment.getUserInfo(username);
        return fragment;
    }

    public MessageFragment() {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString()
                + "/test.dbs", null);
        View view = inflater.inflate(R.layout.fragment_message, container, false);
        myTextView = (TextView) view.findViewById(R.id.oldMessage);
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
        }
        return view;
    }

}
