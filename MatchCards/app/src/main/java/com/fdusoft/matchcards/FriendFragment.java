package com.fdusoft.matchcards;

import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Fragment for My Friends
 */
public class FriendFragment extends Fragment {

    private SQLiteDatabase db;

    private TextView friendList;
    private TextView addFriendText;
    private TextView giveChanceText;

    private String myName;

    public static FriendFragment getFriendFragment(String username) {
        FriendFragment fragment = new FriendFragment();
        fragment.myName = username;
        return fragment;
    }

    public FriendFragment() {}

    private boolean userExists(String username) {
        try{
            String str="select * from tb_user where name=?";
            Cursor cursor = db.rawQuery(str, new String []{username});
            return cursor.getCount()>0;

        }catch(SQLiteException e){
        }
        return false;
    }

    private void updateFriendList() {
        // TODO: social functionalities
        String output = "";
        try {
            String str = "select * from tb_friend_"+this.myName;
            Cursor cursor = db.rawQuery(str, null);
            while (cursor.moveToNext()) {
                output += cursor.getString(cursor.getColumnIndex("name"));
                output += "\n";
            }
        } catch (Exception e) {}
        friendList.setText(output);
    }

    private void addFriend(String friendName) {
        db.execSQL(String.format("create table IF NOT EXISTS tb_friend_%s ( name varchar(30) primary key)", this.myName));

        String str = String.format("insert into tb_friend_%s values(?) ", this.myName);
        try {
            String str2 = String.format("select * from tb_friend_%s where name=?", this.myName);
            Cursor cursor = db.rawQuery(str2, new String[]{friendName});

            if (cursor.getCount() == 0)
                db.execSQL(str, new String[] { friendName });
        } catch (Exception e) {
            Log.e("MatchCards", "ERROR", e);
        }
    }

    private Boolean isFriend(String friendName) {
        db.execSQL(String.format("create table IF NOT EXISTS tb_friend_%s ( name varchar(30) primary key)", this.myName));

        Boolean result = false;
        try {
            String str2 = String.format("select * from tb_friend_%s where name=?", this.myName);
            Cursor cursor = db.rawQuery(str2, new String[]{friendName});
            result = (cursor.getCount() != 0);
        } catch (Exception e) {
            Log.e("MatchCards", "ERROR", e);
        }
        return result;
    }

    private int getChance(String username) {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString()
                + "/test.dbs", null);

        int chance = 0;
        try {
            Cursor cursor = db.rawQuery("select * from tb_chance where name=?", new String[]{username});
            cursor.moveToFirst();
            chance = cursor.getInt(cursor.getColumnIndex("chance"));
        } catch (Exception e) {
            Log.e("MatchCards", "ERROR", e);
        }
        return chance;
    }

    public void updateChance(String username,int newGameChance) {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString()
                + "/test.dbs", null);

        try {
            db.execSQL("update tb_chance set chance=? where name=?", new Object[]{newGameChance, username});
        } catch (Exception e) {
            Log.e("MatchCards", "ERROR", e);
        }
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString()
                + "/test.dbs", null);

        View view = inflater.inflate(R.layout.fragment_friend, container, false);

        friendList = (TextView) view.findViewById(R.id.friend_list);
        updateFriendList();

        addFriendText = (TextView) view.findViewById(R.id.add_friend_text);
        giveChanceText = (TextView) view.findViewById(R.id.give_chance_text);

        final Button addFriendButton = (Button) view.findViewById(R.id.add_friend_button);
        addFriendButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO : added user should get a notification

                String friendName = addFriendText.getText().toString();
                if (friendName.equals(myName)) {
                    Toast.makeText(getActivity().getApplicationContext(), "不能添加自己为好友！",
                            Toast.LENGTH_SHORT).show();
                }
                else if (!userExists(friendName)) {
                    Toast.makeText(getActivity().getApplicationContext(), "不存在该用户！",
                            Toast.LENGTH_SHORT).show();
                } else {
                    addFriend(friendName);
                    updateFriendList();
                }
            }
        });

        final Button giveChanceButton = (Button) view.findViewById(R.id.give_chance_button);
        giveChanceButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO : receiver user should get a message

                String friendName = giveChanceText.getText().toString();
                if (friendName.equals(myName)) {
                    Toast.makeText(getActivity().getApplicationContext(), "不能赠送机会给自己！",
                            Toast.LENGTH_SHORT).show();
                }
                else if (!userExists(friendName)) {
                    Toast.makeText(getActivity().getApplicationContext(), "不存在该用户！",
                            Toast.LENGTH_SHORT).show();
                } else if (!isFriend(friendName)) {
                    Toast.makeText(getActivity().getApplicationContext(), "该用户不是你的好友！",
                            Toast.LENGTH_SHORT).show();
                } else {
                    int myChance = getChance(myName);
                    int friendChance = getChance(friendName);
                    if (myChance == 0) {
                        Toast.makeText(getActivity().getApplicationContext(), "你没有游戏机会了！",
                                Toast.LENGTH_SHORT).show();
                    } else if (friendChance == 5) {
                        Toast.makeText(getActivity().getApplicationContext(), "好友的游戏机会已达上限！",
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        updateChance(myName,myChance - 1);
                        updateChance(friendName,friendChance + 1);
                        Toast.makeText(getActivity().getApplicationContext(), "已赠送一次机会给好友" + friendName,
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        return view;
    }
}