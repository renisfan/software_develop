package com.fdusoft.matchcards;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Adapter for group ExpandableListView
 */
public class GroupAdapter extends BaseExpandableListAdapter {

    private Context context;

    // 创建一级条目容器
    private ArrayList<String> groups = new ArrayList<String>();
    // 存放内容, 以便显示在列表中
    private ArrayList<ArrayList< String> > children = new ArrayList<ArrayList<String> >();

    public void setAdapter(ArrayList<String> groups, ArrayList<ArrayList<String> > children) {
        this.groups = groups;
        this.children = children;
    }

    public GroupAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getGroupCount() {
        return groups.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return children.get(groupPosition).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groups.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return children.get(groupPosition).get(childPosition);
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
        TextView textView = new TextView(context);
        textView.setText(getGroup(groupPosition).toString());
        return textView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        TextView childText;
        TextView textView = new TextView(context);
        textView.setText(getChild(groupPosition, childPosition).toString());
        return textView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}
