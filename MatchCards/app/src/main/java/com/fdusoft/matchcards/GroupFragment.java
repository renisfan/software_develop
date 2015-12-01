
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
import android.widget.BaseExpandableListAdapter;
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

    private TextView createGroupText;
    private TextView joinGroupText;
    private ExpandableListView elv;

    public static GroupFragment getGroupFragment(String username) {
        GroupFragment fragment = new GroupFragment();
        fragment.myName = username;
        return fragment;
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

        public Score(int score, String name) {
            this.score = score;
            this.name = name;
        }

        @Override
        public int compareTo(Score o) {
            return score < o.score ? 1 : score > o.score ? -1 : 0;
        }
    }

    private SavedTabsListAdapter getAdapter(){
        SavedTabsListAdapter ret = new SavedTabsListAdapter();
        ArrayList<String> groups= new ArrayList<String>();
        ArrayList<ArrayList<String> > children= new ArrayList<ArrayList<String> >();

        try {
            String str = "select * from tb_"+this.myName+"_group";
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

                    scores.add(new Score(highScore, memberName));

                }

                Collections.sort(scores);
                ArrayList<String> child = new ArrayList<String>();

                for (Score temp : scores) {
                    child.add(temp.name + "    score: " + temp.score);
                }

                children.add(child);
            }
        } catch (Exception e) {Log.e("ERROR", e.toString());}

        ret.setAdapter(groups, children);
        return ret;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString()
                + "/test.dbs", null);

        View view = inflater.inflate(R.layout.fragment_group, container, false);
        elv = (ExpandableListView) view.findViewById(R.id.list);
        elv.setAdapter(getAdapter());

        joinGroupText = (TextView) view.findViewById(R.id.join_group_text);
        createGroupText = (TextView) view.findViewById(R.id.create_group_text);

        final Button joinGroupButton = (Button) view.findViewById(R.id.join_group_button);
        joinGroupButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String groupName = joinGroupText.getText().toString();
                if (!groupExists(groupName)) {
                    Toast.makeText(getActivity().getApplicationContext(), "不存在该群组！",
                            Toast.LENGTH_SHORT).show();
                } else {
                    joinOrCreateGroup(groupName);
                    elv.setAdapter(getAdapter());
                }
            }
        });

        final Button createGroupButton = (Button) view.findViewById(R.id.create_group_button);
        createGroupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String groupName = createGroupText.getText().toString();
                if (groupExists(groupName)) {
                    Toast.makeText(getActivity().getApplicationContext(), "已存在该群组！",
                            Toast.LENGTH_SHORT).show();
                } else {
                    joinOrCreateGroup(groupName);
                    elv.setAdapter(getAdapter());
                }
            }
        });

        return view;
    }

    public class SavedTabsListAdapter extends BaseExpandableListAdapter {

        private ArrayList<String> groups = new ArrayList<String>();

        private ArrayList<ArrayList<String> > children = new ArrayList<ArrayList<String> >();

        public void setAdapter(ArrayList<String> groups, ArrayList<ArrayList<String> > children) {
            this.groups = groups;
            this.children = children;
        }

        @Override
        public int getGroupCount() {
                return groups.size();
            }

        @Override
        public int getChildrenCount(int i) {
                return children.get(i).size();
        }

        @Override
        public Object getGroup(int i) {
                return groups.get(i);
            }

        @Override
        public Object getChild(int i, int i1) {
                return children.get(i).get(i1);
            }

        @Override
        public long getGroupId(int i) {
                return i;
            }

        @Override
        public long getChildId(int i, int i1) {
            return i1;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int i, boolean b, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(GroupFragment.this.getActivity());
            textView.setText(getGroup(i).toString());
            return textView;
        }

        @Override
        public View getChildView(int i, int i1, boolean b, View view, ViewGroup viewGroup) {
            TextView textView = new TextView(GroupFragment.this.getActivity());
            textView.setText(getChild(i, i1).toString());
            return textView;
        }

        @Override
        public boolean isChildSelectable(int i, int i1) {
                return true;
        }
    }
}