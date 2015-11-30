package com.fdusoft.matchcards;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.os.Bundle;
import android.app.ExpandableListActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.SimpleExpandableListAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class GroupActivity extends ExpandableListActivity {

    private SQLiteDatabase db;
    /**
     * 创建一级条目容器
     */
    List<Map<String, String>> groups;
    /**
     * 存放内容, 以便显示在列表中
     */
    List<List<Map<String, String>>> childs;

    private TextView createGroupText;
    private TextView joinGroupText;

    private String myName;

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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getIntent().getExtras();
        this.myName = bundle.getString("myName");
        setContentView(R.layout.fragment_group);
        db = SQLiteDatabase.openOrCreateDatabase(GroupActivity.this.getFilesDir().toString()
                + "/test.dbs", null);

        joinGroupText = (TextView) findViewById(R.id.join_group_text);
        createGroupText = (TextView) findViewById(R.id.create_group_text);

        final Button joinGroupButton = (Button) findViewById(R.id.join_group_button);
        joinGroupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String groupName = joinGroupText.getText().toString();
                if (!groupExists(groupName)) {
                    Toast.makeText(getApplicationContext(), "不存在该群组！",
                            Toast.LENGTH_SHORT).show();
                } else {
                    joinOrCreateGroup(groupName);
                    setListData();
                }
            }
        });

        final Button createGroupButton = (Button) findViewById(R.id.create_group_button);
        createGroupButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String groupName = createGroupText.getText().toString();
                if (groupExists(groupName)) {
                    Toast.makeText(getApplicationContext(), "已存在该群组！",
                            Toast.LENGTH_SHORT).show();
                } else {
                    joinOrCreateGroup(groupName);
                    setListData();
                }
            }
        });

        setListData();
    }

    /**
     * 设置列表内容
     */
    public void setListData() {
        groups = new ArrayList<Map<String, String>>();
        childs = new ArrayList<List<Map<String, String>>>();

        try {
            String str = "select * from tb_"+this.myName+"_group";
            Cursor cursor = db.rawQuery(str, null);
            while (cursor.moveToNext()) {
                //get a group which the user belongs to
                Map<String, String> title = new HashMap<String, String>();
                String groupName = cursor.getString(cursor.getColumnIndex("groupName"));
                title.put("group", groupName);
                groups.add(title);

                //get members of the group
                String str2 = "select * from tb_"+"group_"+groupName;
                Cursor cursor2 = db.rawQuery(str2, null);
                List<Map<String, String>> child = new ArrayList<Map<String, String>>();
                while (cursor2.moveToNext()) {
                    Map<String, String> title_content = new HashMap<String, String>();
                    title_content.put("child", cursor2.getString(cursor2.getColumnIndex("member")));
                    child.add(title_content);
                }
                childs.add(child);
            }
        } catch (Exception e) {}

        /**
         * 创建ExpandableList的Adapter容器 参数: 1.上下文 2.一级集合 3.一级样式文件 4. 一级条目键值
         * 5.一级显示控件名 6. 二级集合 7. 二级样式 8.二级条目键值 9.二级显示控件名
         *
         */
        SimpleExpandableListAdapter sela = new SimpleExpandableListAdapter(
                this, groups, R.layout.activity_group_groups, new String[] { "group" },
                new int[] { R.id.textGroup }, childs, R.layout.activity_group_childs,
                new String[] { "child" }, new int[] { R.id.textChild });
        // 加入列表
        setListAdapter(sela);
    }

    /**
     * 列表内容按下
     */
    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        Toast.makeText(
                GroupActivity.this,
                "您选择了"
                        + groups.get(groupPosition).toString()
                        + "子编号"
                        + childs.get(groupPosition).get(childPosition)
                        .toString(), Toast.LENGTH_SHORT).show();
        return super.onChildClick(parent, v, groupPosition, childPosition, id);
    }

    /**
     * 二级标题按下
     */
    @Override
    public boolean setSelectedChild(int groupPosition, int childPosition,
                                    boolean shouldExpandGroup) {
        return super.setSelectedChild(groupPosition, childPosition,
                shouldExpandGroup);
    }

    /**
     * 一级标题按下
     */
    @Override
    public void setSelectedGroup(int groupPosition) {
        super.setSelectedGroup(groupPosition);
    }
}