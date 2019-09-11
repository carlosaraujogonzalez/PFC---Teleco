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

public class EducationalSettingsView extends ActionBarActivity{
		
	@Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_blue_dark));
        setContentView(R.layout.educational_settings_view);  
        getOverflowMenu();          
        
        Configuration configuration = getResources().getConfiguration();
        onConfigurationChanged(configuration);
    }
    
    
    
	
	
	@SuppressLint("NewApi")
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
		Log.d("NavigationDrawer", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        
        DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.educational_settings_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_guias));
                    
            GridView gridView = (GridView) findViewById(R.id.gridView);
            gridView.setNumColumns(newConfig.screenWidthDp/300);        
        } 
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.educational_settings_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_land_guias));
        	
        	GridView gridView = (GridView) findViewById(R.id.gridView);
            gridView.setNumColumns(newConfig.screenWidthDp/300);
        }
        
        
    }
	
	
	
	
	
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	getMenuInflater().inflate(R.menu.educational_settings_view, menu);
    	return true;
    }  
    
    
    
    
   
    @Override
	public boolean onPrepareOptionsMenu(Menu menu){
	    menu.clear();
	    getMenuInflater().inflate(R.menu.educational_settings_view, menu);  
	    return super.onPrepareOptionsMenu(menu);
	}
    
    
    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  
    	int id = item.getItemId();        
        if (id == R.id.revert){
        	finish();
        	return true;
        }        
        if (id == R.id.search_educational_settings){  
        	EditText editText = (EditText) findViewById(R.id.editText_educational_settings_view);
        	editText.setText("");        	
        	ImageView searchButton = (ImageView) findViewById(R.id.imageView_search_educational_settings_view);
        	searchButton.setOnClickListener(mSearchBarClickListener);
        	ImageView closeButton = (ImageView) findViewById(R.id.imageView_close_search_educational_settings_view);
        	closeButton.setOnClickListener(mCloseSearchBarClickListener);  	
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_educational_settings);
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
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_educational_settings);
        	linearLayout.setVisibility(View.GONE);
        }
    };
    
    
    
    
    
    private OnClickListener mSearchBarClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	   
        	EditText editText = (EditText) findViewById(R.id.editText_educationalSettings_view);
        	String search = editText.getText().toString();
        	int page = -1;
        	int techs_number = -1;
        	EducationalSettingsFragment educationalSettingsFragment = (EducationalSettingsFragment) getSupportFragmentManager().findFragmentById(R.id.educationalSettingsFragment);
        	educationalSettingsFragment.search_educational_settings(search, page, techs_number);
        }
    };
    
}
