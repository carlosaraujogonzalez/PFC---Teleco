package area.guias.pfc;

import java.lang.reflect.Field;
import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

@SuppressLint("Recycle")
public class NavigationDrawer extends ActionBarActivity {
	private ActionBarDrawerToggle mActionBarDrawerToggle;
	private GuideListFragment guideListFragment;
	private String[] titles;
	private DrawerLayout NavDrawerLayout;
	private ListView NavList;
	private ArrayList<NavigationDrawerItem> NavItms;
	//private TypedArray NavIcons;
	NavigationAdapter NavAdapter;
	private int fragment_position;
	
	// CONSTANTS
	static final int RESOURCES = 1;
	static final int ACTIVITIES = 2;
	static final int SEQUENCES = 3;
	static final int GUIDES = 4;
	static final int EXPERIENCES = 5;
	static final int BOARDS = 6;
	
	// LANGUAGES
	public Language language;
	
	@SuppressLint("NewApi")
	@Override
	protected void onCreate(Bundle savedInstanceState){
		Log.d("NavigationDrawer", "onCreate");
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.navigation_drawer);
		
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_blue_dark));
	        
		create();
	    
		
		language = getIntent().getParcelableExtra("language");
		
		if( savedInstanceState != null ) {
			 Log.d("NavigationDrawer", "savedInstanceState != null");
			 fragment_position = savedInstanceState.getInt("fragment_position");
			 Fragment fragment = getFragmentManager().getFragment(savedInstanceState, "guideListFragment");
			 guideListFragment = (GuideListFragment) fragment;
		}
		else{
			Log.d("NavigationDrawer", "savedInstanceState == null");
			fragment_position = GUIDES;
			showFragment(fragment_position);
		}
		
				
	}
	
	
	
	
	
	@Override
    protected void onPostCreate(Bundle savedInstanceState) {
		Log.d("NavigationDrawer", "onPostCreate");
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        mActionBarDrawerToggle.syncState();
    }
	
	

	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
		Log.d("NavigationDrawer", "onCreateOptionsMenu");
		getMenuInflater().inflate(R.menu.default_menu, menu);
        return true;
    }  
	
	
	
	
	
	@Override
	public void onDestroy(){
		Log.d("NavigationDrawer", "onDestroy");
		
		if ((guideListFragment != null)&&(guideListFragment.search_guides != null)){
			guideListFragment.search_guides.cancel(!guideListFragment.search_guides.isCancelled());
			if (GuideListFragment.progressDialog != null) GuideListFragment.progressDialog.dismiss();
		}
			
		if ((guideListFragment != null)&&(guideListFragment.getTypeOfView != null)){
			guideListFragment.getTypeOfView.cancel(!guideListFragment.getTypeOfView.isCancelled());
		}
		
		if ((guideListFragment != null)&&(guideListFragment.downloadImages != null)) 
			guideListFragment.downloadImages.cancel(!guideListFragment.downloadImages.isCancelled());
		
		super.onDestroy();			
	}
	
	
	
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		Log.d("NavigationDrawer", "onPrepareOptionsMenu");
	    menu.clear();
	    boolean drawerOpen = NavDrawerLayout.isDrawerOpen(NavList);
	    
	    if (fragment_position == GUIDES){
	    	getMenuInflater().inflate(R.menu.navigation_drawer_guides, menu);
	    	getOverflowMenu();
	    	menu.findItem(R.id.menu_verguias).setVisible(!drawerOpen);
	    	menu.findItem(R.id.menu_vermisguias).setVisible(!drawerOpen);
	    	menu.findItem(R.id.menu_revert).setVisible(!drawerOpen);
	    	menu.findItem(R.id.refresh_view_menu).setVisible(!drawerOpen);
	    	menu.findItem(R.id.menu_nueva_guia).setVisible(!drawerOpen);
	    	menu.findItem(R.id.search_guides_view_menu).setVisible(!drawerOpen);
	    }
	    else getMenuInflater().inflate(R.menu.default_menu, menu);
	    return super.onPrepareOptionsMenu(menu);
	}
	
	
	
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
		Log.d("NavigationDrawer", "onOptionsItemSelected");
		if (mActionBarDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        int id = item.getItemId();        
        if (id == R.id.menu_revert){
        	exit();
    		return true;
    	}
        if (fragment_position == GUIDES){
        	if (id == R.id.menu_verguias){
        		guideListFragment.seeGuides();
        		return true;
        	}
        	if (id == R.id.menu_vermisguias) {
        		guideListFragment.seeMyGuides();
        		return true;
        	}
        	
        	if (id == R.id.menu_nueva_guia){
        		guideListFragment.newGuide();
        		return true;
        	}
        	if (id == R.id.search_guides_view_menu){
        		guideListFragment.openSearchItems();
        		return true;
        	}
        	if (id == R.id.refresh_view_menu){
        		guideListFragment.refresh();
        		return true;
        	}
        }
        return super.onOptionsItemSelected(item);        
    }
	
	
	

	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	   Log.d("NavigationDrawer", "onSaveInstanceState");
	   super.onSaveInstanceState(outState);
	   outState.putInt("fragment_position", fragment_position);
	   getFragmentManager().putFragment(outState, "guideListFragment", guideListFragment);
	}
	
	
	
	
	
	@SuppressLint("NewApi")
	public void create(){
		NavDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		NavList = (ListView) findViewById(R.id.lista);
		
		mActionBarDrawerToggle = new ActionBarDrawerToggle(this, NavDrawerLayout, R.drawable.ic_drawer, R.string.open_navigation_drawer, R.string.close_navigation_drawer ){
			
			public void onDrawerClosed(View view){
				Log.d("NavigationDrawer", "onDrawerClosed");
				if (fragment_position == RESOURCES){
					getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_orange_clair));
					getSupportActionBar().setTitle("Recursos");
				}
				if (fragment_position == ACTIVITIES){
					getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_orange_medium));
					getSupportActionBar().setTitle("Actividades");
				}
				if (fragment_position == SEQUENCES){
					getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_orange_dark));
					getSupportActionBar().setTitle("Secuencias");
				}
				if (fragment_position == GUIDES){
					getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_blue_dark));
					getSupportActionBar().setTitle("Guías");
				}
				if (fragment_position == EXPERIENCES){
					getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_blue_clair));
					getSupportActionBar().setTitle("Experiencias");
				}
				if (fragment_position == BOARDS){
					getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_brown_dark));
					getSupportActionBar().setTitle("Tableros");
				}
				
				invalidateOptionsMenu();
			}
			
			public void onDrawerOpened(View view){
				Log.d("NavigationDrawer", "onDrawerOpened");
				getSupportActionBar().setTitle("Selecciona una opción");
				invalidateOptionsMenu();
			}
		};
		
		NavDrawerLayout.setDrawerListener(mActionBarDrawerToggle);
		getSupportActionBar().setDisplayHomeAsUpEnabled(true);
		//getSupportActionBar().setHomeButtonEnabled(true);	
		
		
		NavList.setSelector(getResources().getDrawable(R.drawable.blue_button_list_selector));
		NavList.setBackground(getResources().getDrawable(R.drawable.white_box));
		
		View header = getLayoutInflater().inflate(R.layout.navigation_drawer_header, null);		
		NavList.addHeaderView(header);
				
		titles = getResources().getStringArray(R.array.nav_options);
		
		NavItms = new ArrayList<NavigationDrawerItem>();		
		NavItms.add(new NavigationDrawerItem(titles[0], R.drawable.recursos));
		NavItms.add(new NavigationDrawerItem(titles[1], R.drawable.actividades));
		NavItms.add(new NavigationDrawerItem(titles[2], R.drawable.secuencias));
		NavItms.add(new NavigationDrawerItem(titles[3], R.drawable.guides));
		NavItms.add(new NavigationDrawerItem(titles[4], R.drawable.experiencias));
		NavItms.add(new NavigationDrawerItem(titles[5], R.drawable.tableros));
		
		NavAdapter = new NavigationAdapter(this, NavItms);
		NavList.setAdapter(NavAdapter);
		
		NavList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
				Log.d("NavigationDrawer", "onItemClick");
				showFragment(position);				
			}
		});
		
		
	}
	
	
	
	
	
	public Dialog create_exit_dialog(String title, String message){
		final Dialog dialog = new Dialog(this);  
		dialog.setContentView(R.layout.dialog_two_button_layout);
		dialog.setCancelable(true);
		
		dialog.setTitle(title);
		
		TextView msg = (TextView) dialog.findViewById(R.id.message);
		msg.setText(message);
		
		Button buttonYes = (Button) dialog.findViewById(R.id.yes);
		buttonYes.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialog.dismiss();
	        	finish();
	        }
	    });
		
		Button buttonNo = (Button) dialog.findViewById(R.id.no);
		buttonNo.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialog.cancel();
	        }
	    });
		
		return dialog;
	}
	
	
	
	
	
	public void exit(){
		Dialog dialog = create_exit_dialog("Advertencia", "¿Quieres salir de la aplicación?");	
		dialog.show();
	}
	
	
	
	
	
	
    private void getOverflowMenu() {
    	Log.d("NavigationDrawer", "getOverflowMenu");
    	try {
    		ViewConfiguration config = ViewConfiguration.get(this);
    		Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
    		if(menuKeyField != null) {
    			menuKeyField.setAccessible(true);
    			menuKeyField.setBoolean(config, false);
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    
    
    
    
    private void showFragment(int position){
    	Log.d("NavigationDrawer", "showFragment");
    	
		Fragment fragment = null;
		switch(position){
		
		case RESOURCES:
			
			if (guideListFragment != null) 
				guideListFragment.downloadImages.cancel(!guideListFragment.downloadImages.isCancelled());
			
			fragment = new NavigationResourcesFragment();
			
			break;
			
		case ACTIVITIES:
			
			if (guideListFragment != null) 
				guideListFragment.downloadImages.cancel(!guideListFragment.downloadImages.isCancelled());
			
			fragment = new NavigationActivitiesFragment();
			
			break;
			
		case SEQUENCES:
			
			if (guideListFragment != null) 
				guideListFragment.downloadImages.cancel(!guideListFragment.downloadImages.isCancelled());
			
			fragment = new NavigationSequencesFragment();
			
			break;
			
		case GUIDES:
			
			fragment = new GuideListFragment();
			guideListFragment = (GuideListFragment) fragment;
			
			break;
			
		case EXPERIENCES:
			
			if (guideListFragment != null) 
				guideListFragment.downloadImages.cancel(!guideListFragment.downloadImages.isCancelled());
			
			fragment = new NavigationExperiencesFragment();
			
			break;
			
		case BOARDS:
			
			if (guideListFragment != null) 
				guideListFragment.downloadImages.cancel(!guideListFragment.downloadImages.isCancelled());
			
			fragment = new NavigationBoardsFragment();
			
			break;
			
		default:
			Toast.makeText(getApplicationContext(), "Opcion "+ titles[position-1]+" no disponible!", Toast.LENGTH_SHORT).show();	
			fragment = new GuideListFragment();
			guideListFragment = (GuideListFragment) fragment;
			position = 4;
		}
		
		if (fragment != null){
			FragmentManager fragmentManager = getFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.content_frame, fragment).commit();
			fragment_position = position;
			invalidateOptionsMenu();
			NavList.setItemChecked(position, true);
			NavList.setSelection(position);
			setTitle(titles[position-1]);
			NavDrawerLayout.closeDrawer(NavList);
		}
	}   
}
