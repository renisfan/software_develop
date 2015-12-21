package com.fdusoft.matchcards;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.ref.WeakReference;
import java.util.GregorianCalendar;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String LOGTAG = "MatchCards";

    public static final int REQUEST_GAME = 0;

    private static final int MAX_CHANCE = 5;
    private static final int GAP = 300;
    private static final int FREQUENCY = 1;
    private static final int ALERT_TIME = 3600;

    private static String username = null;

    private GameFragment currentGameFragment = null;
    private FriendFragment currentFriendFragment = null;
    private GroupFragment currentGroupFragment = null;
    private MessageFragment currentMessageFragment = null;
    private ChatFragment currentChatFragment = null;

    private SQLiteDatabase db;

    private int startTime;
    private boolean resetStartTime = true;
    private boolean recovering = true;

    private Timer timer;
    private TimerHandler handler;

    public void checkChance() {
        String str = "select * from tb_chance where name=?";
        Cursor cursor = db.rawQuery(str, new String[]{username});
        cursor.moveToFirst();
        int chance = cursor.getInt(cursor.getColumnIndex("chance"));
        int now = getTime(), pre = cursor.getInt(cursor.getColumnIndex("time")), time = now - pre;
        Log.d(LOGTAG, username + " "+pre+" "+time);
        //Log.d(LOGTAG, "MainActivity Time " + now);
        if (chance >= MAX_CHANCE) {
            recovering = false;
        }
        else {
            if(recovering) {
                if (time>=GAP) {
                    chance = Math.min(chance + time / GAP, MAX_CHANCE);
                    if(currentGameFragment != null) {
                        currentGameFragment.updateGameChance(chance);
                    } else {
                        db.execSQL("update tb_chance set chance=? where name=?", new Object[]{chance, username});
                    }
                    setChanceRecoverTime(now-(time%GAP));
                    Log.d(LOGTAG, "Recover time set to " + (now-(time%GAP)) );
                }
            } else {
                recovering = true;
                setChanceRecoverTime(now);
                Log.d(LOGTAG, "Recover time set to " + now);
            }
        }
    }

    public void checkPlayTime() {
        int playTime = getTime() - startTime;
        if ((playTime % ALERT_TIME == 0) && (playTime / ALERT_TIME > 0)) {
            new AlertDialog.Builder(MainActivity.this).setTitle("游戏时长提醒")
                    .setMessage(String.format(getString(R.string.alert_play_time), playTime / ALERT_TIME))
                    .setPositiveButton("好", null).show();
        }
    }

    private static class TimerHandler extends Handler {

        private WeakReference<MainActivity> mOuter;

        public TimerHandler(MainActivity activity) {
            mOuter = new WeakReference<MainActivity>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    MainActivity activity = mOuter.get();
                    if (activity!=null) {
                        activity.checkChance();
                        activity.checkPlayTime();
                    }
                    break;
            }
            super.handleMessage(msg);
        }
    }

    private void startTimer() {
        timer = new Timer(true);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Message message = new Message();
                message.what = 1;
                handler.sendMessage(message);
            }
        }, 0, FREQUENCY * 1000);
    }

    private int getTime() {
        GregorianCalendar now = new GregorianCalendar(),
                start = new GregorianCalendar(2014,1,1);
        long diff = now.getTime().getTime() - start.getTime().getTime();
        long sec = TimeUnit.MILLISECONDS.toSeconds(diff);
        return (int)sec;
    }

    private void setChanceRecoverTime(int time) {
        db.execSQL("update tb_chance set time=? where name=?", new Object[]{time,username});
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

        handler = new TimerHandler(MainActivity.this);
        //DONNOT start timer here. onResume() will be called soon.
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (resetStartTime) {
            startTime = getTime();
            resetStartTime = false;
        }
        checkChance();
        startTimer();
    }

    @Override
    protected void onPause() {
        timer.cancel();
        resetStartTime = true;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        Log.d(LOGTAG, "MainActivity.onDestroy()");
        timer.cancel();
        db.close();
        super.onDestroy();
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
                        timer.cancel();
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
            currentGameFragment = GameFragment.getGameFragment(username, MainActivity.this);
            getFragmentManager().beginTransaction().replace(R.id.container,
                    currentGameFragment).commit();
            currentFriendFragment = null;
            currentGroupFragment = null;
            currentChatFragment = null;
            currentMessageFragment = null;
        } else if (id == R.id.nav_friend) {
            currentFriendFragment = FriendFragment.getFriendFragment(username);
            getFragmentManager().beginTransaction().replace(R.id.container,
                    currentFriendFragment).commit();
            currentGameFragment = null;
            currentGroupFragment = null;
            currentChatFragment = null;
            currentMessageFragment = null;
        } else if (id == R.id.nav_group) {
            currentGroupFragment = GroupFragment.getGroupFragment(username);
            getFragmentManager().beginTransaction().replace(R.id.container,
                    currentGroupFragment).commit();
            currentGameFragment = null;
            currentFriendFragment = null;
            currentChatFragment = null;
            currentMessageFragment = null;
        } else if (id == R.id.nav_chat) {
            currentChatFragment = ChatFragment.getChatFragment(username);
            getFragmentManager().beginTransaction().replace(R.id.container,
                    currentChatFragment).commit();
            currentGameFragment = null;
            currentFriendFragment = null;
            currentGroupFragment = null;
            currentMessageFragment = null;
        } else if (id == R.id.nav_message) {
            currentMessageFragment = MessageFragment.getMessageFragment(username);
            getFragmentManager().beginTransaction().replace(R.id.container,
                    currentMessageFragment).commit();
            currentGameFragment = null;
            currentFriendFragment = null;
            currentGroupFragment = null;
            currentChatFragment = null;
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        resetStartTime = false;
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

}
