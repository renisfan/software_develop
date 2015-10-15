package com.fdusoft.matchcards;

import android.app.Activity;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends Activity
        implements NavigationDrawerFragment.NavigationDrawerCallbacks {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;
    
    // TODO: get this value from online user data
    private static int gameChance = 5;
    
    private static String username;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();
        
        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));
        
        // Get the user info
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        username = bundle.getString("USER_NAME");
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getFragmentManager();
		fragmentManager.beginTransaction()
			    .replace(R.id.container, PlaceholderFragment.newInstance(position+1))
			    .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.game_section);
                break;
            case 2:
                mTitle = getString(R.string.friend_section);
                break;
            case 3:
                mTitle = getString(R.string.group_section);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
    	
    	private static int currentSection = 1;
    	
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            currentSection = sectionNumber;
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView;
            switch (currentSection) {
			case 1:
				rootView = inflater.inflate(R.layout.fragment_game, container, false);		
				final TextView gameChanceHint = (TextView) rootView.findViewById(R.id.game_chance_hint);
				gameChanceHint.setText(String.format(getString(R.string.game_chance), gameChance));
				
				Button startGameButton = (Button) rootView.findViewById(R.id.start_game);
				startGameButton.setOnClickListener(new View.OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (gameChance <= 0) {
							Toast.makeText(getActivity().getApplicationContext(),
									"游戏次数不足!", Toast.LENGTH_SHORT).show();
						} else {
							--gameChance;
							gameChanceHint.setText(
									String.format(getString(R.string.game_chance),
									gameChance));
							// TODO: implement start game
							Intent intent = new Intent();
							intent.setClass(getActivity(), GameActivity.class);
							getActivity().startActivityForResult(intent, 0);

						}
					}
				});
				break;
			case 2:
				rootView = inflater.inflate(R.layout.fragment_friend, container, false);
				break;
			case 3:
				rootView = inflater.inflate(R.layout.fragment_group, container, false);
				break;
			default:
				rootView = inflater.inflate(R.layout.fragment_game, container, false);
				break;
			}
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }
    
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if (requestCode == 0 && resultCode == 0) {
    		int score = data.getIntExtra("GAME_SCORE", 0);
    		Toast.makeText(getApplicationContext(),
    				String.format("您的游戏分数：%d", score),
    				Toast.LENGTH_SHORT).show();
    	}
    }

}
