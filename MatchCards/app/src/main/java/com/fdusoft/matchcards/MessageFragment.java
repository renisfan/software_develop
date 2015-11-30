package com.fdusoft.matchcards;


import android.app.Fragment;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by zhanghanyuan on 15/11/29.
 */
public class MessageFragment extends Fragment {

    private SQLiteDatabase db;

    private String myName;


    public static MessageFragment getMessageFragment(String username) {
        MessageFragment fragment = new MessageFragment();
        fragment.myName = username;
       // fragment.getUserInfo(username);
        return fragment;
    }

    public MessageFragment() {
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        db = SQLiteDatabase.openOrCreateDatabase(getActivity().getFilesDir().toString()
                + "/test.dbs", null);
        View view = inflater.inflate(R.layout.fragment_message, container, false);

        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putString("USER_NAME", myName);
        intent.putExtras(bundle);
        intent.setClass(getActivity(), MessageActivity.class);
        getActivity().startActivityForResult(intent, MainActivity.REQUEST_GROUP);
        return view;
    }
}
