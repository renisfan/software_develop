
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
import android.widget.ExpandableListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GroupFragment extends Fragment {

    private SQLiteDatabase db;
    private String myName;

    private ExpandableListView expandableListView;
    private TextView createGroupText;
    private TextView joinGroupText;
    private GroupAdapter adapter;

    public static GroupFragment getGroupFragment(String username) {
        GroupFragment fragment = new GroupFragment();
        fragment.myName = username;
        return fragment;
    }

    public GroupFragment() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString()
                + "/test.dbs", null);

        View view = inflater.inflate(R.layout.fragment_group, container, false);
        expandableListView = (ExpandableListView)view.findViewById(R.id.group_list);
        adapter = getAdapter();
        expandableListView.setAdapter(adapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v,
                                        int groupPosition, int childPosition, long id) {
                String text;
                text = adapter.getChild(groupPosition, childPosition).toString();
                String memberName = text.substring(0, text.indexOf(" "));

                String strLike = "select * from tb_like where name=?";
                Cursor cursorLike;
                int like = 0;
                cursorLike = db.rawQuery(strLike, new String[]{memberName});
                if (cursorLike.moveToFirst())
                    like = cursorLike.getInt(cursorLike.getColumnIndex("like"));
                db.execSQL("update tb_like set like=? where name=?", new Object[]{like+1, memberName});

                adapter = getAdapter();
                expandableListView.setAdapter(adapter);
                return true;
            }
        });

        joinGroupText = (TextView) view.findViewById(R.id.join_group_text);
        createGroupText = (TextView) view.findViewById(R.id.create_group_text);

        Button joinGroupButton = (Button) view.findViewById(R.id.join_group_button);
        joinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = joinGroupText.getText().toString();
                if (!groupExists(groupName)) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            getString(R.string.group_not_exist),
                            Toast.LENGTH_SHORT).show();
                } else {
                    joinOrCreateGroup(groupName);
                    adapter = getAdapter();
                    expandableListView.setAdapter(adapter);
                }
            }
        });

        Button createGroupButton = (Button) view.findViewById(R.id.create_group_button);
        createGroupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String groupName = createGroupText.getText().toString();
                if (groupExists(groupName)) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            getString(R.string.group_already_exist),
                            Toast.LENGTH_SHORT).show();
                } else {
                    joinOrCreateGroup(groupName);
                    adapter = getAdapter();
                    expandableListView.setAdapter(adapter);
                }
            }
        });

        return view;
    }

    private boolean groupExists(String groupName){
        try{
            String str="select * from tb_group_"+groupName;
            Cursor cursor = db.rawQuery(str, null);
            return cursor.getCount()>0;
        }catch(SQLiteException e){
        }
        return false;
    }

    private void joinOrCreateGroup(String groupName) {
        db.execSQL(String.format("create table IF NOT EXISTS tb_group_%s ( member varchar(30) primary key)", groupName));
        String str = String.format("insert into tb_group_%s values(?) ", groupName);
        try {
            String str2 = String.format("select * from tb_group_%s where member=?", groupName);
            Cursor cursor = db.rawQuery(str2, new String[]{this.myName});

            if (cursor.getCount() == 0) {
                db.execSQL(str, new String[]{this.myName});
                db.execSQL(String.format("create table IF NOT EXISTS tb_%s_group ( groupName varchar(30) primary key)", this.myName));
                String str3 = String.format("insert into tb_%s_group values(?)", this.myName);
                db.execSQL(str3, new String[]{groupName});
            }
        } catch (Exception e) {
            Log.e("MatchCards", "ERROR", e);
        }
    }

    class Score implements Comparable<Score> {
        int score;
        String name;
        int like;

        public Score(int score, String name, int like) {
            this.score = score;
            this.name = name;
            this.like = like;
        }

        @Override
        public int compareTo(Score o) {
            return score < o.score ? 1 : score > o.score ? -1 : 0;
        }
    }

    private GroupAdapter getAdapter(){
        GroupAdapter ret = new GroupAdapter(getActivity());
        ArrayList<String> groups= new ArrayList<String>();
        ArrayList<ArrayList<String> > children= new ArrayList<ArrayList<String> >();

        try {
            String str = "select * from tb_" + myName + "_group";
            Cursor cursor = db.rawQuery(str, null);
            while (cursor.moveToNext()) {
                //get a group which the user belongs to
                String groupName = cursor.getString(cursor.getColumnIndex("groupName"));
                groups.add( groupName );

                //get members of the group
                String str2 = "select * from tb_"+"group_"+groupName;
                Cursor cursor2 = db.rawQuery(str2, null);
                List<Score> scores = new ArrayList<Score>();
                while (cursor2.moveToNext()) {
                    String memberName = cursor2.getString(cursor2.getColumnIndex("member"));

                    String strScore = "select * from tb_score where name=?";
                    Cursor cursorScore = db.rawQuery(strScore, new String[]{memberName});

                    int highScore = 0;
                    if (cursorScore.moveToFirst())
                        highScore = cursorScore.getInt(cursorScore.getColumnIndex("highScore"));

                    String strLike = "select * from tb_like where name=?";
                    Cursor cursorLike;
                    int like = 0;
                    cursorLike = db.rawQuery(strLike, new String[]{memberName});
                    if (cursorLike.moveToFirst())
                        like = cursorLike.getInt(cursorLike.getColumnIndex("like"));
                    else {
                        String strTemp = "insert into tb_like values(?,?)";
                        db.execSQL(strTemp, new String[] { memberName, "0"});
                    }

                    scores.add(new Score(highScore, memberName, like));

                }

                Collections.sort(scores);
                ArrayList<String> child = new ArrayList<String>();

                for (Score temp : scores) {
                    child.add(temp.name + "    score: " + temp.score + "    ❤: " + temp.like);
                }

                children.add(child);
            }
        } catch (Exception e) {Log.e("ERROR", e.toString());}

        ret.setAdapter(groups, children);
        return ret;
    }
}