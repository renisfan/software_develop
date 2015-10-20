package com.fdusoft.matchcards;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Handler;
import android.os.Message;
import java.util.Timer;
import java.util.TimerTask;
import java.util.GregorianCalendar;
import java.util.concurrent.TimeUnit;
import android.util.Log;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    public static final int REQUEST_GAME = 0;

    private static String username = null;

    private GameFragment currentGameFragment = null;
    private FriendFragment currenFriendFragment;

    private SQLiteDatabase db;

    private String TAG = "myLogs";

    private Timer timer;
    private int gap = 300,freqence = 1,maxChance = 1000;

    public void addChance() {
        String str = "select * from tb_chance where name=?";
        Cursor cursor = db.rawQuery(str, new String[]{username});
        cursor.moveToFirst();
        int chance = cursor.getInt(cursor.getColumnIndex("chance"));
        int now = getTime(), pre = cursor.getInt(cursor.getColumnIndex("time")), time = now - pre;
        Log.e(TAG,"MainActivity " + username + " "+chance+" "+time);
        if (chance >= maxChance) {
            Log.e(TAG,"MainActivity Time " + now);
            db.execSQL("update tb_chance set time=? where name=?", new Object[]{now,username});
        }
        else if (time>=gap) {
            Log.e(TAG,"MainActivity Time " + now);
            chance += time / gap;
            if (chance > maxChance) chance = maxChance;
            if (currentGameFragment != null) currentGameFragment.awardGameChance(chance);
            else db.execSQL("update tb_chance set chance=?,time=? where name=?", new Object[]{chance,now,username});
        }
    }
    public final Handler handler = new Handler(){
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    addChance();
                    break;
            }
            super.handleMessage(msg);
        }
    };

    private TimerTask task = new TimerTask(){
        public void run() {
            Message message = new Message();
            message.what = 1;
            handler.sendMessage(message);
        }
    };
    public void startTimer() {
        timer = new Timer(true);
        timer.schedule(task, 0, freqence*1000);
    }
    public void cancelTimer() {
        timer.cancel();
    }

    public int getTime() {
        GregorianCalendar now = new GregorianCalendar(),
                start = new GregorianCalendar(2014,1,1);
        long diff = now.getTime().getTime() - start.getTime().getTime();
        long sec = TimeUnit.MILLISECONDS.toSeconds(diff);
        return (int)sec;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        db = SQLiteDatabase.openOrCreateDatabase(MainActivity.this.getFilesDir().toString()
                + "/test.dbs", null);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close) {
            @Override
            public void onDrawerOpened(View drawerView) {
                TextView userName = (TextView) findViewById(R.id.profile_username);
                userName.setText(username);
            }
        };
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        getFragmentManager().beginTransaction().replace(R.id.container, new WelcomeFragment()).commit();

        // Get the user info
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        username = bundle.getString("USER_NAME");
        startTimer();
    }

    // Override finish() to make it pop confirmation window before user logout
    // CAUTION: DONNOT call finish() when you start sub activities
    @Override
    public void finish() {
        new AlertDialog.Builder(MainActivity.this).setTitle("确定要退出吗？")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        username = null;
                        cancelTimer();
                        MainActivity.super.finish();
                    }
                })
                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                    }
                }).show();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            // TODO: implement user settings
            return true;
        } else if (id == R.id.action_logout) {
            MainActivity.this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_game) {
            currentGameFragment = GameFragment.getGameFragment(username,this);
            getFragmentManager().beginTransaction().replace(R.id.container,
                    currentGameFragment).commit();
        } else if (id == R.id.nav_friend) {
            currentGameFragment = null;
            currenFriendFragment = FriendFragment.getFriendFragment(username);
            getFragmentManager().beginTransaction().replace(R.id.container,
                    currenFriendFragment).commit();
        } else if (id == R.id.nav_group) {
            currentGameFragment = null;
            getFragmentManager().beginTransaction().replace(R.id.container,
                    new GroupFragment()).commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_GAME && resultCode == RESULT_OK) {
            int score = data.getIntExtra("GAME_SCORE", 0);
            currentGameFragment.updateHighScore(score);
        }
    }

    public static class WelcomeFragment extends Fragment {
        public WelcomeFragment() {}
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_welcome, container, false);
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        db.close();
        Log.e(TAG,"MainActivity.onDestroy()");
    }
}
