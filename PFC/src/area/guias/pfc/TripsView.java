package area.guias.pfc;

import java.lang.reflect.Field;

import android.annotation.SuppressLint;
import android.content.Intent;
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

public class TripsView extends ActionBarActivity{
	String activityType;
	String tripsType;
	private static final String LOGTAG = "LogsAndroid";
		
	@Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_blue_dark));
        setContentView(R.layout.trips_view);  
        
        Configuration configuration = getResources().getConfiguration();
        onConfigurationChanged(configuration);
        
        tripsType = "user_trips";
        activityType = getIntent().getExtras().getString("activityType");
        getOverflowMenu();          
    }
    
    
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	if (activityType.equals("userActivity")) getMenuInflater().inflate(R.menu.trips_view_user_menu, menu);
    	else if (activityType.equals("systemActivity")) getMenuInflater().inflate(R.menu.trips_view_menu, menu);
    	else Log.e(LOGTAG, "Error en el tipo de actividad");
        return true;
    }  
    
    
    
    
   
    @SuppressLint("NewApi")
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
		Log.d("NavigationDrawer", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        
        DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.trips_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_guias));
                    
            GridView gridView = (GridView) findViewById(R.id.gridView);
            gridView.setNumColumns(newConfig.screenWidthDp/300);
           
         
        } 
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.trips_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_land_guias));
        	
        	 GridView gridView = (GridView) findViewById(R.id.gridView);
             gridView.setNumColumns(newConfig.screenWidthDp/300);
        }
        
        
    }
    
    
    
    
    
    @Override
    public void onDestroy(){
    	TripsFragment tripsFragment = (TripsFragment) getSupportFragmentManager().findFragmentById(R.id.tripsFragment);
    	if ((tripsFragment != null)&&(tripsFragment.downloadImages != null)) tripsFragment.downloadImages.cancel(!tripsFragment.downloadImages.isCancelled());
        super.onDestroy();        
    }
    
    
    
    
    
    @Override
	public boolean onPrepareOptionsMenu(Menu menu){
	    menu.clear();
	    if (activityType.equals("userActivity")){
	    	if (tripsType.equals("user_trips")){
	    		getMenuInflater().inflate(R.menu.trips_view_user_menu, menu);	
	    		getOverflowMenu();
	    	}
	    	else if (tripsType.equals("system_trips")) {
	    		getMenuInflater().inflate(R.menu.trips_view_system_menu, menu);
	    		getOverflowMenu();
	    	}	    		  
	    	else Log.e(LOGTAG, "Imposible escoger tipo de menú.");	  
	    }
	    else if (activityType.equals("systemActivity")) getMenuInflater().inflate(R.menu.trips_view_menu, menu); 
	    return super.onPrepareOptionsMenu(menu);
	}
    
    
    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  
    	int id = item.getItemId();
        if (id == R.id.trips_view_user_revert){
        	TripsFragment tripsFragment = (TripsFragment) getSupportFragmentManager().findFragmentById(R.id.tripsFragment);
        	if (tripsFragment.numSelectedTrips().numTripsChecked==0) finish();
        	else tripsFragment.removeTrips(true);
        	return true;
        }
        if (id == R.id.trips_view_system_revert){
        	finish();
        	return true;
        }
        if (id == R.id.system_trips){
        	tripsType = "system_trips";
        	invalidateOptionsMenu();
        	TripsFragment tripsFragment = (TripsFragment) getSupportFragmentManager().findFragmentById(R.id.tripsFragment);        	
        	tripsFragment.get_trips(tripsType);
        	return true;
        }
        if (id == R.id.delete_trips){
        	TripsFragment tripsFragment = (TripsFragment) getSupportFragmentManager().findFragmentById(R.id.tripsFragment);
        	tripsFragment.removeTrips(false);
        	return true;
        }
        if (id == R.id.user_trips){
        	tripsType = "user_trips";
        	invalidateOptionsMenu();
        	TripsFragment tripsFragment = (TripsFragment) getSupportFragmentManager().findFragmentById(R.id.tripsFragment);       	
        	tripsFragment.get_trips(tripsType);
        	return true;
        }
        if (id == R.id.add_trips){  
        	TripsFragment tripsFragment = (TripsFragment) getSupportFragmentManager().findFragmentById(R.id.tripsFragment);
        	tripsFragment.addTrips();
        	return true;
        }
        if (id == R.id.search_user_trips){  
        	EditText editText = (EditText) findViewById(R.id.editText_trips_view);
        	editText.setText("");        	
        	ImageView searchButton = (ImageView) findViewById(R.id.imageView_search_trips_view);
        	searchButton.setOnClickListener(mSearchBarClickListener);
        	ImageView closeButton = (ImageView) findViewById(R.id.imageView_close_search_trips_view);
        	closeButton.setOnClickListener(mCloseSearchBarClickListener);
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_trips);
        	linearLayout.setVisibility(View.VISIBLE);       	
        	return true;
        }
        if (id == R.id.search_system_trips){  
        	EditText editText = (EditText) findViewById(R.id.editText_trips_view);
        	editText.setText("");        	
        	ImageView searchButton = (ImageView) findViewById(R.id.imageView_search_trips_view);
        	searchButton.setOnClickListener(mSearchBarClickListener);
        	ImageView closeButton = (ImageView) findViewById(R.id.imageView_close_search_trips_view);
        	closeButton.setOnClickListener(mCloseSearchBarClickListener);
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_trips);
        	linearLayout.setVisibility(View.VISIBLE);       	
        	return true;
        }
    	
        if (id == R.id.revert){
        	finish();
        	return true;
        }
        if (id == R.id.add_trips){
        	Intent intent = getIntent();
        	intent.putExtra("from_activity", "TripsView");
        	finish();
        	startActivity(intent);
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
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_trips);
        	linearLayout.setVisibility(View.GONE);
        }
    };
    
    
    
    
    
    private OnClickListener mSearchBarClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	EditText editText = (EditText) findViewById(R.id.editText_trips_view);
        	String search = editText.getText().toString();
        	int page = 1;
        	int trips_number = 15;
        	TripsFragment tripsFragment = (TripsFragment) getSupportFragmentManager().findFragmentById(R.id.tripsFragment);
        	tripsFragment.tripsSearch(search, page, trips_number);
        }
    };
    
}