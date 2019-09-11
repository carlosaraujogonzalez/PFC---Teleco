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

public class DevicesView extends ActionBarActivity  {
	String devicesType;
	private Owner owner;
	
	@Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.devices_view); 
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_blue_dark));         
        
        Configuration configuration = getResources().getConfiguration();
        onConfigurationChanged(configuration);
        
        owner = getIntent().getParcelableExtra("owner");
        
        devicesType = "user_devices";
        getOverflowMenu();            
    }
	
	
	
	
	   
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	if (owner.getOwner() == Owner.NONE) getMenuInflater().inflate(R.menu.devices_view, menu);
    	else if (owner.getOwner() == Owner.TRUE) getMenuInflater().inflate(R.menu.devices_view_user_menu, menu);
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
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.devices_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_guias));
                    
            GridView gridView = (GridView) findViewById(R.id.gridView);
            gridView.setNumColumns(newConfig.screenWidthDp/300);
           
         
        } 
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.devices_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_land_guias));
        	
        	 GridView gridView = (GridView) findViewById(R.id.gridView);
             gridView.setNumColumns(newConfig.screenWidthDp/300);
        }
        
        
    }
    
    
    
    
    
    @Override
    public void onDestroy(){
    	DevicesViewFragment dispFrag = (DevicesViewFragment) getSupportFragmentManager().findFragmentById(R.id.dispositivosItemListFragment);
    	if ((dispFrag != null)&&(dispFrag.downloadImages != null)) dispFrag.downloadImages.cancel(!dispFrag.downloadImages.isCancelled());
        super.onDestroy();        
    }
    
    
    
    
    
    @Override
	public boolean onPrepareOptionsMenu(Menu menu){
	    menu.clear();
	    
	    if (owner.getOwner() == Owner.TRUE){
	    	if (devicesType.equals("user_devices")){
	    		getMenuInflater().inflate(R.menu.devices_view_user_menu, menu);
	    		getOverflowMenu();
	    	}
	    	else if (devicesType.equals("system_devices")) 
	    		getMenuInflater().inflate(R.menu.devices_view_system_menu, menu);
	    }
	    
	    return super.onPrepareOptionsMenu(menu);
	}
    
    
    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {    	
        int id = item.getItemId();
        if (id == R.id.devices_view_user_revert){
        	DevicesViewFragment dispFrag1 = (DevicesViewFragment) getSupportFragmentManager().findFragmentById(R.id.dispositivosItemListFragment);
        	if (dispFrag1.numSelectedDevices()==0){
        		finish();
        	}
        	else dispFrag1.removeDevices(true);
        	return true;
        }
        if (id == R.id.devices_view_system_revert){
        	DevicesViewFragment dispFrag1 = (DevicesViewFragment) getSupportFragmentManager().findFragmentById(R.id.dispositivosItemListFragment);
        	if (dispFrag1.countDevicesToAdd()==0) finish();
        	else dispFrag1.addDevices(true);
        	return true;
        }
        if (id == R.id.system_devices){
        	devicesType = "system_devices";
        	invalidateOptionsMenu();
        	DevicesViewFragment dispFrag1 = (DevicesViewFragment) getSupportFragmentManager().findFragmentById(R.id.dispositivosItemListFragment);
        	dispFrag1.getDevices(devicesType);
        	return true;
        }
        if (id == R.id.delete_devices){
        	DevicesViewFragment dispFrag1 = (DevicesViewFragment) getSupportFragmentManager().findFragmentById(R.id.dispositivosItemListFragment);
        	dispFrag1.removeDevices(false);
        	return true;
        }
        if (id == R.id.user_devices){
        	devicesType = "user_devices";
        	invalidateOptionsMenu();
        	DevicesViewFragment dispFrag1 = (DevicesViewFragment) getSupportFragmentManager().findFragmentById(R.id.dispositivosItemListFragment);
        	dispFrag1.getDevices(devicesType);
        	return true;
        }
        if (id == R.id.add_devices){  
        	DevicesViewFragment dispFrag1 = (DevicesViewFragment) getSupportFragmentManager().findFragmentById(R.id.dispositivosItemListFragment);
        	dispFrag1.addDevices(false);
        	return true;
        }
        if (id == R.id.search_user_devices){  
        	EditText editText = (EditText) findViewById(R.id.editText_devices_view);
        	editText.setText("");        	
        	ImageView searchButton = (ImageView) findViewById(R.id.imageView_search_devices_view);
        	searchButton.setOnClickListener(mSearchBarClickListener);
        	ImageView closeButton = (ImageView) findViewById(R.id.imageView_close_search_devices_view);
        	closeButton.setOnClickListener(mCloseSearchBarClickListener);
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_devices);
        	linearLayout.setVisibility(View.VISIBLE);       	
        	return true;
        }
        if (id == R.id.search_system_devices){  
        	EditText editText = (EditText) findViewById(R.id.editText_devices_view);
        	editText.setText("");        	
        	ImageView searchButton = (ImageView) findViewById(R.id.imageView_search_devices_view);
        	searchButton.setOnClickListener(mSearchBarClickListener);
        	ImageView closeButton = (ImageView) findViewById(R.id.imageView_close_search_devices_view);
        	closeButton.setOnClickListener(mCloseSearchBarClickListener);
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_devices);
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
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_devices);
        	linearLayout.setVisibility(View.GONE);
        }
    };
    
    
    
    
    
    private OnClickListener mSearchBarClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	EditText editText = (EditText) findViewById(R.id.editText_devices_view);
        	String search = editText.getText().toString();
        	int page = 1;
        	int devices_number = 15;
        	DevicesViewFragment dispFrag1 = (DevicesViewFragment) getSupportFragmentManager().findFragmentById(R.id.dispositivosItemListFragment);
        	dispFrag1.searchDevices(search, page, devices_number);
        }
    };
}
