package area.guias.pfc;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

@SuppressLint("NewApi")
public class ActivitiesView extends ActionBarActivity{
	String activitiesType;
	private Owner owner;
	
    @Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_blue_dark));
        setContentView(R.layout.activities_view);
        
        Configuration configuration = getResources().getConfiguration();
        onConfigurationChanged(configuration);
        
        owner = getIntent().getParcelableExtra("owner");
        activitiesType = "userActivities";
        getOverflowMenu();               
        
    }
    
    
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	if (owner.getOwner() == Owner.NONE) getMenuInflater().inflate(R.menu.activities_view, menu);
    	else if (owner.getOwner() == Owner.TRUE) getMenuInflater().inflate(R.menu.activities_view_user_menu, menu);
        return true;
    }  
    
    
    
    
    
    @Override
    public void onDestroy(){
    	ActivitiesViewFragment actFrag = (ActivitiesViewFragment) getSupportFragmentManager().findFragmentById(R.id.activities_fragment);    	
    	if ((actFrag != null)&&(actFrag.downloadImages != null)) actFrag.downloadImages.cancel(!actFrag.downloadImages.isCancelled());
        super.onDestroy();        
    }
    
    
    
    
    
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
		Log.d("NavigationDrawer", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        
        DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.activities_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_guias));
                    
            GridView gridView = (GridView) findViewById(R.id.gridView);
            gridView.setNumColumns(newConfig.screenWidthDp/300);
           
         
        } 
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.activities_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_land_guias));
        	
        	 GridView gridView = (GridView) findViewById(R.id.gridView);
             gridView.setNumColumns(newConfig.screenWidthDp/300);
        }
        
        
    }
    
    
    
    
    
    
    @Override
	public boolean onPrepareOptionsMenu(Menu menu){
	    menu.clear();
	    if (owner.getOwner() == Owner.TRUE){
	    	if (activitiesType.equals("userActivities")){
	    		getMenuInflater().inflate(R.menu.activities_view_user_menu, menu);
	    		getOverflowMenu();
	    	}
	    	else getMenuInflater().inflate(R.menu.activities_view_system_menu, menu);
	    }
	    return super.onPrepareOptionsMenu(menu);
	}
    
    
    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.activities_view_user_revert){
        	ActivitiesViewFragment actFrag = (ActivitiesViewFragment) getSupportFragmentManager().findFragmentById(R.id.activities_fragment);
        	if (actFrag.numSelectedActivities()==0) finish();
        	else actFrag.removeActivities(true);
        	return true;
        }
        if (id == R.id.activities_view_system_revert){
        	finish();
        	return true;
        }
        if (id == R.id.system_activities){   
        	activitiesType = "systemActivities";
        	invalidateOptionsMenu();
        	ActivitiesViewFragment actFrag = (ActivitiesViewFragment) getSupportFragmentManager().findFragmentById(R.id.activities_fragment);
        	actFrag.restart("systemActivities");
        	return true;
        }
        if (id == R.id.delete_activities){
        	ActivitiesViewFragment actFrag = (ActivitiesViewFragment) getSupportFragmentManager().findFragmentById(R.id.activities_fragment);
        	actFrag.removeActivities(false);
        	return true;
        }
        if (id == R.id.user_activities){
        	activitiesType = "userActivities";
        	invalidateOptionsMenu();
        	ActivitiesViewFragment actFrag = (ActivitiesViewFragment) getSupportFragmentManager().findFragmentById(R.id.activities_fragment);
        	actFrag.restart("userActivities");
        	return true;
        }
        if (id == R.id.add_activities){  
        	ActivitiesViewFragment actFrag = (ActivitiesViewFragment) getSupportFragmentManager().findFragmentById(R.id.activities_fragment);
        	actFrag.addActivities();
        	return true;
        }
        if (id == R.id.search_user_activities){  
        	EditText editText = (EditText) findViewById(R.id.editText_activities_view);
        	editText.setText("");        	
        	ImageView searchButton = (ImageView) findViewById(R.id.imageView_search_activities_view);
        	searchButton.setOnClickListener(mSearchBarClickListener);
        	ImageView closeButton = (ImageView) findViewById(R.id.imageView_close_search_activities_view);
        	closeButton.setOnClickListener(mCloseSearchBarClickListener);
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_activities);
        	linearLayout.setVisibility(View.VISIBLE);       	
        	return true;
        }
        if (id == R.id.search_system_activities){  
        	EditText editText = (EditText) findViewById(R.id.editText_activities_view);
        	editText.setText("");        	
        	ImageView searchButton = (ImageView) findViewById(R.id.imageView_search_activities_view);
        	searchButton.setOnClickListener(mSearchBarClickListener);
        	ImageView closeButton = (ImageView) findViewById(R.id.imageView_close_search_activities_view);
        	closeButton.setOnClickListener(mCloseSearchBarClickListener);
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_activities);
        	linearLayout.setVisibility(View.VISIBLE);       	
        	return true;
        }
        
        return super.onOptionsItemSelected(item);
    }

    
    
    
    
    private void getOverflowMenu() {
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
    
    
    
    
    private OnClickListener mCloseSearchBarClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_activities);
        	linearLayout.setVisibility(View.GONE);
        }
    };
    
    
    
    
    
    private OnClickListener mSearchBarClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	EditText editText = (EditText) findViewById(R.id.editText_activities_view);
        	String search = editText.getText().toString();
        	int page = 1;
        	int activities_number = 15;
        	ActivitiesViewFragment actFrag = (ActivitiesViewFragment) getSupportFragmentManager().findFragmentById(R.id.activities_fragment);
        	actFrag.activitiesSearch(search, page, activities_number);
        }
    };
    
}