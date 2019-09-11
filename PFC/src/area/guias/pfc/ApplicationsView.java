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

public class ApplicationsView extends ActionBarActivity  {
	String applicationsType;
	private Owner owner;
	
	@Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_blue_dark));
        setContentView(R.layout.applications_view);  
        
        Configuration configuration = getResources().getConfiguration();
        onConfigurationChanged(configuration);
        
        owner = getIntent().getParcelableExtra("owner");
        
        applicationsType = "user_applications";
        getOverflowMenu();            
    }
	
	
	
	
	   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	if (owner.getOwner() == Owner.NONE) getMenuInflater().inflate(R.menu.applications_view, menu);
    	else if (owner.getOwner() == Owner.NONE) getMenuInflater().inflate(R.menu.applications_view_user_menu, menu);
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
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.applications_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_guias));
                    
            GridView gridView = (GridView) findViewById(R.id.gridView);
            gridView.setNumColumns(newConfig.screenWidthDp/300);
           
         
        } 
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.applications_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_land_guias));
        	
        	 GridView gridView = (GridView) findViewById(R.id.gridView);
             gridView.setNumColumns(newConfig.screenWidthDp/300);
        }
        
        
    }
    
    
    
    
    
    @Override
    public void onDestroy(){
    	ApplicationsViewFragment appFrag = (ApplicationsViewFragment) getSupportFragmentManager().findFragmentById(R.id.aplicacionesItemListFragment);
    	if ((appFrag != null)&&(appFrag.downloadImages != null)) appFrag.downloadImages.cancel(!appFrag.downloadImages.isCancelled());
        super.onDestroy();        
    }
    
    
    
    
    
    @Override
	public boolean onPrepareOptionsMenu(Menu menu){
	    menu.clear();
	    if (owner.getOwner() == Owner.TRUE){
	    	if (applicationsType.equals("user_applications")){
	    		getMenuInflater().inflate(R.menu.applications_view_user_menu, menu);
	    		getOverflowMenu();
	    	}
	    	else getMenuInflater().inflate(R.menu.applications_view_system_menu, menu);
	    }
	    return super.onPrepareOptionsMenu(menu);
	}
    
    
    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {    	
        int id = item.getItemId();
        if (id == R.id.applications_view_user_revert){
        	ApplicationsViewFragment appFrag1 = (ApplicationsViewFragment) getSupportFragmentManager().findFragmentById(R.id.aplicacionesItemListFragment);
        	if (appFrag1.numSelectedApplications()==0){
        		finish();
        	}
        	else appFrag1.removeApplications(true);
        	return true;
        }
        if (id == R.id.applications_view_system_revert){
        	ApplicationsViewFragment appFrag1 = (ApplicationsViewFragment) getSupportFragmentManager().findFragmentById(R.id.aplicacionesItemListFragment);
        	if (appFrag1.countApplicationsToAdd()==0) finish();
        	else appFrag1.addApplications(true);
        	return true;
        }
        if (id == R.id.system_applications){
        	applicationsType = "system_applications";
        	invalidateOptionsMenu();
        	ApplicationsViewFragment appFrag1 = (ApplicationsViewFragment) getSupportFragmentManager().findFragmentById(R.id.aplicacionesItemListFragment);
        	appFrag1.getApplications(applicationsType);
        	return true;
        }
        if (id == R.id.delete_applications){
        	ApplicationsViewFragment appFrag1 = (ApplicationsViewFragment) getSupportFragmentManager().findFragmentById(R.id.aplicacionesItemListFragment);
        	appFrag1.removeApplications(false);
        	return true;
        }
        if (id == R.id.user_applications){
        	applicationsType = "user_applications";
        	invalidateOptionsMenu();
        	ApplicationsViewFragment appFrag1 = (ApplicationsViewFragment) getSupportFragmentManager().findFragmentById(R.id.aplicacionesItemListFragment);
        	appFrag1.getApplications(applicationsType);
        	return true;
        }
        if (id == R.id.add_applications){  
        	ApplicationsViewFragment appFrag1 = (ApplicationsViewFragment) getSupportFragmentManager().findFragmentById(R.id.aplicacionesItemListFragment);
        	appFrag1.addApplications(false);
        	return true;
        }
        if (id == R.id.search_user_applications){  
        	EditText editText = (EditText) findViewById(R.id.editText_applications_view);
        	editText.setText("");        	
        	ImageView searchButton = (ImageView) findViewById(R.id.imageView_search_applications_view);
        	searchButton.setOnClickListener(mSearchBarClickListener);
        	ImageView closeButton = (ImageView) findViewById(R.id.imageView_close_search_applications_view);
        	closeButton.setOnClickListener(mCloseSearchBarClickListener);
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_applications);
        	linearLayout.setVisibility(View.VISIBLE);       	
        	return true;
        }
        if (id == R.id.search_system_applications){  
        	EditText editText = (EditText) findViewById(R.id.editText_applications_view);
        	editText.setText("");        	
        	ImageView searchButton = (ImageView) findViewById(R.id.imageView_search_applications_view);
        	searchButton.setOnClickListener(mSearchBarClickListener);
        	ImageView closeButton = (ImageView) findViewById(R.id.imageView_close_search_applications_view);
        	closeButton.setOnClickListener(mCloseSearchBarClickListener);
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_applications);
        	linearLayout.setVisibility(View.VISIBLE);       	
        	return true;
        }
        if (id == R.id.revert){
        	finish();
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
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_applications);
        	linearLayout.setVisibility(View.GONE);
        }
    };
    
    
    
    
    
    private OnClickListener mSearchBarClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	EditText editText = (EditText) findViewById(R.id.editText_applications_view);
        	String search = editText.getText().toString();
        	int page = 1;
        	int applications_number = 15;
        	ApplicationsViewFragment appFrag1 = (ApplicationsViewFragment) getSupportFragmentManager().findFragmentById(R.id.aplicacionesItemListFragment);
        	appFrag1.searchApplications(search, page, applications_number);
        }
    };
}
