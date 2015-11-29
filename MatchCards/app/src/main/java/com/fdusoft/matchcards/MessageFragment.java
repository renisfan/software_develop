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
 * Created by zhanghanyuan on 15/11/29.
 */
public class MessageFragment extends Fragment {




    public static MessageFragment getMessageFragment(String username) {
        MessageFragment fragment = new MessageFragment();

       // fragment.getUserInfo(username);
        return fragment;
    }
}
