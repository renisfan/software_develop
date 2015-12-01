package com.fdusoft.matchcards;

import android.app.Fragment;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;

/**
 * Fragment template for My Group
 */
public class GroupFragment extends Fragment {

    private SQLiteDatabase db;
    private String myName;

    private ExpandableListView expandableListView;
    private GroupAdapter groupAdapter;

    public static GroupFragment getGroupFragment(String username) {
        GroupFragment fragment = new GroupFragment();
        fragment.myName = username;
        return fragment;
    }

    public GroupFragment() {}

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString()
                + "/test.dbs", null);
        View view = inflater.inflate(R.layout.fragment_group, container, false);
        expandableListView = (ExpandableListView)view.findViewById(R.id.group_list);
        groupAdapter = new GroupAdapter(getActivity());
        expandableListView.setAdapter(groupAdapter);
        expandableListView.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
                Toast.makeText(getActivity(), String.format(getString(R.string.group_hint),
                                groupAdapter.getGroup(groupPosition),
                                groupAdapter.getChild(groupPosition, childPosition)),
                        Toast.LENGTH_SHORT).show();
                return true;
            }
        });

        return view;
    }
}