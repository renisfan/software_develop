package com.fdusoft.matchcards;

import android.app.Fragment;
import android.content.Intent;
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
 * Fragment template for My Friends
 */
public class GroupFragment extends Fragment {

    private SQLiteDatabase db;

    private String myName;

    public static GroupFragment getGroupFragment(String username) {
        GroupFragment fragment = new GroupFragment();
        fragment.myName = username;
        return fragment;
    }

    public GroupFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString()
                + "/test.dbs", null);

        View view = inflater.inflate(R.layout.fragment_group, container, false);

        Intent intent = new Intent();
        intent.setClass(getActivity(), GroupActivity.class);
        Bundle bundle = new Bundle();
        bundle.putString("myName", this.myName);
        intent.putExtras(bundle);
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_GROUP);

        return view;
    }
}