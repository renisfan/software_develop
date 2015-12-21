package com.fdusoft.matchcards;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

/**
 * Fragment for Group Chat
 */

public class ChatFragment extends Fragment {

    private SQLiteDatabase db;

    private String username;
    private String currentGroupName = "";
    private String currentChatTable = "";

    private TextView chatHistory;

    private EditText enterChatText;
    private EditText sendWordsText;

    public static ChatFragment getChatFragment(String username) {
        ChatFragment fragment = new ChatFragment();
        fragment.username = username;
        return fragment;
    }

    public ChatFragment() {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString()
                + "/test.dbs", null);

        View view = inflater.inflate(R.layout.fragment_chat, container, false);

        chatHistory = (TextView) view.findViewById(R.id.chat_history);
        chatHistory.setMovementMethod(ScrollingMovementMethod.getInstance());

        enterChatText = (EditText) view.findViewById(R.id.enter_chat_text);
        sendWordsText = (EditText) view.findViewById(R.id.send_words_text);

        Button enterChatButton = (Button) view.findViewById(R.id.enter_chat_button);
        Button sendWordsButton = (Button) view.findViewById(R.id.send_words_button);

        enterChatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String group = enterChatText.getText().toString();

                if (group.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "群组名不能为空！",
                            Toast.LENGTH_SHORT).show();
                } else if (!groupExists(group) || !belongsToGroup(group)) {
                    Toast.makeText(getActivity().getApplicationContext(), "你未加入该群组！",
                            Toast.LENGTH_SHORT).show();
                } else {
                    currentGroupName = group;
                    currentChatTable = "chat_of_group_" + group;
                    db.execSQL(String.format("create table IF NOT EXISTS %s ( words varchar(350) primary key)", currentChatTable));
                    updateChatHistory();
                }

            }
        });

        sendWordsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String words = sendWordsText.getText().toString();
                if (currentGroupName.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "未指定群组！",
                            Toast.LENGTH_SHORT).show();
                }
                else if (words.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(), "发送消息不能为空！",
                            Toast.LENGTH_SHORT).show();
                } else if (words.length() > 140) {
                    Toast.makeText(getActivity().getApplicationContext(), "超过字数限制！",
                            Toast.LENGTH_SHORT).show();
                }
                else {
                    insertWords(words);
                    sendWordsText.setText("");
                }

            }
        });

        return view;
    }

    private void updateChatHistory() {
        if (currentGroupName.isEmpty()) return;

        String query = "select * from " + this.currentChatTable;
        Cursor cursor = db.rawQuery(query, null);
        String history = "";
        for (cursor.moveToFirst(); !cursor.isAfterLast();cursor.moveToNext()) {
            String words = cursor.getString(cursor.getColumnIndex("words"));
            history += words;
        }
        chatHistory.setText(history);
    }

    private void insertWords(String words) {
        if (this.currentGroupName.isEmpty() || words.isEmpty()) return;

        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        formatter.setTimeZone(TimeZone.getTimeZone("GMT+8"));
        String date = formatter.format(new Date(System.currentTimeMillis()));
        String info = this.username + " (" + date + ") :";
        String tail = "\n" + info + "\n" + words + "\n";
        chatHistory.append(tail);

        String sql = String.format("insert into %s values(?)", currentChatTable);
        db.execSQL(sql, new Object[]{tail});
    }

    private boolean groupExists(String groupName){
        try{
            String str = "select * from tb_group_"+groupName;
            Cursor cursor = db.rawQuery(str, null);
            return cursor.getCount() > 0;
        }catch(SQLiteException e){
        }
        return false;
    }

    private boolean belongsToGroup(String groupName) {
        if (!groupExists(groupName)) {
            return false;
        }
        else {
            String str = String.format("insert into tb_group_%s values(?) ", groupName);
            try {
                String str2 = String.format("select * from tb_group_%s where member=?", groupName);
                Cursor cursor = db.rawQuery(str2, new String[]{this.username});
                return cursor.getCount() > 0;
            } catch (Exception e) {
                Log.e("MatchCards", "ERROR", e);
            }
        }
        return false;
    }
}
