package com.fdusoft.matchcards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by admin on 2015/12/1.
 */
public class GroupAdapter extends BaseExpandableListAdapter {

    private Context context;

    // 创建一级条目容器
    private List<String> gruops = new ArrayList<String>();
    // 存放内容, 以便显示在列表中
    private List<List< String> > childs = new ArrayList<List<String> >();

    public GroupAdapter(Context context) {
        this.context = context;
        setListData();
    }

    @Override
    public int getGroupCount() {
        return gruops.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return childs.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return gruops.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return childs.get(groupPosition).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        TextView groupText;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.group_groups, null);
            groupText = (TextView) convertView.findViewById(R.id.textGroup);
            convertView.setTag(groupText);
        } else {
            groupText = (TextView) convertView.getTag();
        }
        groupText.setText(gruops.get(groupPosition));
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TextView childText;
        if (convertView == null) {
            convertView = View.inflate(context, R.layout.group_childs, null);
            childText = (TextView) convertView.findViewById(R.id.textChild);
            convertView.setTag(childText);
        } else {
            childText = (TextView) convertView.getTag();
        }
        childText.setText(childs.get(groupPosition).get(childPosition));
        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    public void onChildClick(int groupPosition, int childPosition) {
    }

    // 设置列表内容
    public void setListData() {

        // 创建二个一级条目标题
        String title_1 = "群组1";
        String title_2 = "群组2";
        gruops.add(title_1);
        gruops.add(title_2);

        // 创建二级条目内容
        // 内容一
        String title_1_content_1 = "a1";
        String title_1_content_2 = "a2";
        String title_1_content_3 = "a3";
        List<String> childs_1 = new ArrayList<String>();
        childs_1.add(title_1_content_1);
        childs_1.add(title_1_content_2);
        childs_1.add(title_1_content_3);
        // 内容二
        String title_2_content_1 = "b1";
        String title_2_content_2 = "b2";
        String title_2_content_3 = "b3";
        List<String> childs_2 = new ArrayList<String>();
        childs_2.add(title_2_content_1);
        childs_2.add(title_2_content_2);
        childs_2.add(title_2_content_3);

        childs.add(childs_1);
        childs.add(childs_2);
    }

}
