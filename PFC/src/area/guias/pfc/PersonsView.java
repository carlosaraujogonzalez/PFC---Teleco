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

public class PersonsView extends ActionBarActivity{
	String activityType;
	String personsType;
	private static final String LOGTAG = "LogsAndroid";
		
	@Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_blue_dark));
        setContentView(R.layout.persons_view);  
        
        Configuration configuration = getResources().getConfiguration();
        onConfigurationChanged(configuration);
        
        personsType = "user_persons";
        activityType = getIntent().getExtras().getString("activityType");
        getOverflowMenu();          
    }
    
    
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	if (activityType.equals("userActivity")) getMenuInflater().inflate(R.menu.persons_view_user_menu, menu);
    	else if (activityType.equals("systemActivity")) getMenuInflater().inflate(R.menu.persons_view_menu, menu);
    	else Log.e(LOGTAG, "Error en el tipo de actividad");
        return true;
    }  
    
    
    
    
   
    @Override
    public void onDestroy(){
    	PersonsFragment personsFragment = (PersonsFragment) getSupportFragmentManager().findFragmentById(R.id.personsFragment);        	
    	if ((personsFragment != null)&&(personsFragment.downloadImages != null)) personsFragment.downloadImages.cancel(!personsFragment.downloadImages.isCancelled());
        super.onDestroy();        
    }
    
    
    
    
    
    @SuppressLint("NewApi")
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
		Log.d("NavigationDrawer", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        
        DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.persons_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_guias));
                    
            GridView gridView = (GridView) findViewById(R.id.gridView);
            gridView.setNumColumns(newConfig.screenWidthDp/300);
           
         
        } 
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.persons_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_land_guias));
        	
        	 GridView gridView = (GridView) findViewById(R.id.gridView);
             gridView.setNumColumns(newConfig.screenWidthDp/300);
        }
        
        
    }
    
    
    
    
    
    @Override
	public boolean onPrepareOptionsMenu(Menu menu){
	    menu.clear();
	    if (activityType.equals("userActivity")){
	    	if (personsType.equals("user_persons")){
	    		getMenuInflater().inflate(R.menu.persons_view_user_menu, menu);	
	    		getOverflowMenu();
	    	}
	    	else if (personsType.equals("system_persons")) {
	    		getMenuInflater().inflate(R.menu.persons_view_system_menu, menu);
	    		getOverflowMenu();
	    	}	    		  
	    	else Log.e(LOGTAG, "Imposible escoger tipo de menú.");	  
	    }
	    else if (activityType.equals("systemActivity")) getMenuInflater().inflate(R.menu.persons_view_menu, menu);
	    return super.onPrepareOptionsMenu(menu);
	}
    
    
    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  
    	int id = item.getItemId();
        if (id == R.id.persons_view_user_revert){
        	PersonsFragment personsFragment = (PersonsFragment) getSupportFragmentManager().findFragmentById(R.id.personsFragment);
        	if (personsFragment.numSelectedPersons().numPersonsChecked==0) finish();
        	else personsFragment.removePersons(true);
        	return true;
        }
        if (id == R.id.persons_view_system_revert){
        	finish();
        	return true;
        }
        if (id == R.id.system_persons){
        	personsType = "system_persons";
        	invalidateOptionsMenu();
        	PersonsFragment personsFragment = (PersonsFragment) getSupportFragmentManager().findFragmentById(R.id.personsFragment);        	
        	personsFragment.get_persons(personsType);
        	return true;
        }
        if (id == R.id.delete_persons){
        	PersonsFragment personsFragment = (PersonsFragment) getSupportFragmentManager().findFragmentById(R.id.personsFragment);
        	personsFragment.removePersons(false);
        	return true;
        }
        if (id == R.id.user_persons){
        	personsType = "user_persons";
        	invalidateOptionsMenu();
        	PersonsFragment personsFragment = (PersonsFragment) getSupportFragmentManager().findFragmentById(R.id.personsFragment);       	
        	personsFragment.get_persons(personsType);
        	return true;
        }
        if (id == R.id.add_persons){  
        	PersonsFragment personsFragment = (PersonsFragment) getSupportFragmentManager().findFragmentById(R.id.personsFragment);
        	personsFragment.addPersons();
        	return true;
        }
        if (id == R.id.search_user_persons){  
        	EditText editText = (EditText) findViewById(R.id.editText_persons_view);
        	editText.setText("");        	
        	ImageView searchButton = (ImageView) findViewById(R.id.imageView_search_persons_view);
        	searchButton.setOnClickListener(mSearchBarClickListener);
        	ImageView closeButton = (ImageView) findViewById(R.id.imageView_close_search_persons_view);
        	closeButton.setOnClickListener(mCloseSearchBarClickListener);
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_persons);
        	linearLayout.setVisibility(View.VISIBLE);       	
        	return true;
        }
        if (id == R.id.search_system_persons){  
        	EditText editText = (EditText) findViewById(R.id.editText_persons_view);
        	editText.setText("");        	
        	ImageView searchButton = (ImageView) findViewById(R.id.imageView_search_persons_view);
        	searchButton.setOnClickListener(mSearchBarClickListener);
        	ImageView closeButton = (ImageView) findViewById(R.id.imageView_close_search_persons_view);
        	closeButton.setOnClickListener(mCloseSearchBarClickListener);
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_persons);
        	linearLayout.setVisibility(View.VISIBLE);       	
        	return true;
        }
    	
        if (id == R.id.revert){
        	finish();
        	return true;
        }
        if (id == R.id.add_persons){
        	Intent intent = getIntent();
        	intent.putExtra("from_activity", "PersonsView");
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
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_persons);
        	linearLayout.setVisibility(View.GONE);
        }
    };
    
    
    
    
    
    private OnClickListener mSearchBarClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	EditText editText = (EditText) findViewById(R.id.editText_persons_view);
        	String search = editText.getText().toString();
        	int page = 1;
        	int persons_number = 15;
        	PersonsFragment personsFragment = (PersonsFragment) getSupportFragmentManager().findFragmentById(R.id.personsFragment);
        	personsFragment.personsSearch(search, page, persons_number);
        }
    };
    
}