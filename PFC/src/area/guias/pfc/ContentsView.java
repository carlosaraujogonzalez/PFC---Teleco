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

public class ContentsView extends ActionBarActivity{
	String activityType;
	String contentsType;
	private static final String LOGTAG = "LogsAndroid";
		
	@Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_blue_dark));
        setContentView(R.layout.contents_view); 
        
        Configuration configuration = getResources().getConfiguration();
        onConfigurationChanged(configuration);
        
        contentsType = "user_contents";
        activityType = getIntent().getExtras().getString("activityType");
        getOverflowMenu();          
    }
    
    
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	if (activityType.equals("userActivity")) getMenuInflater().inflate(R.menu.contents_view_user_menu, menu);
    	else if (activityType.equals("systemActivity")) getMenuInflater().inflate(R.menu.contents_view_menu, menu);
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
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.contents_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_guias));
                    
            GridView gridView = (GridView) findViewById(R.id.gridView);
            gridView.setNumColumns(newConfig.screenWidthDp/300);
           
         
        } 
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.contents_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_land_guias));
        	
        	 GridView gridView = (GridView) findViewById(R.id.gridView);
             gridView.setNumColumns(newConfig.screenWidthDp/300);
        }
        
        
    }
    
    
    
    
    
   
    @Override
    public void onDestroy(){
    	ContentsFragment contentsFragment = (ContentsFragment) getSupportFragmentManager().findFragmentById(R.id.contentsFragment);
    	if ((contentsFragment != null)&&(contentsFragment.downloadImages != null)) contentsFragment.downloadImages.cancel(!contentsFragment.downloadImages.isCancelled());
        super.onDestroy();        
    }
    
    
    
    
    
    @Override
	public boolean onPrepareOptionsMenu(Menu menu){
	    menu.clear();
	    if (activityType.equals("userActivity")){
	    	if (contentsType.equals("user_contents")){
	    		getMenuInflater().inflate(R.menu.contents_view_user_menu, menu);	
	    		getOverflowMenu();
	    	}
	    	else if (contentsType.equals("system_contents")) {
	    		getMenuInflater().inflate(R.menu.contents_view_system_menu, menu);
	    		getOverflowMenu();
	    	}	    		  
	    	else Log.e(LOGTAG, "Error en el tipo de contenido.");	  
	    }
	    else if (activityType.equals("systemActivity")) getMenuInflater().inflate(R.menu.contents_view_menu, menu);  
	   	    
	    return super.onPrepareOptionsMenu(menu);
	}
    
    
    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {  
    	int id = item.getItemId();
        if (id == R.id.contents_view_user_revert){
        	ContentsFragment contentsFragment = (ContentsFragment) getSupportFragmentManager().findFragmentById(R.id.contentsFragment);
        	if (contentsFragment.numSelectedContents().numContentsChecked==0) finish();
        	else contentsFragment.removeContents(true);
        	return true;
        }
        if (id == R.id.contents_view_system_revert){
        	finish();
        	return true;
        }
        if (id == R.id.system_contents){
        	contentsType = "system_contents";
        	invalidateOptionsMenu();
        	ContentsFragment contentsFragment = (ContentsFragment) getSupportFragmentManager().findFragmentById(R.id.contentsFragment);        	
        	contentsFragment.get_contents(contentsType);
        	return true;
        }
        if (id == R.id.delete_contents){
        	ContentsFragment contentsFragment = (ContentsFragment) getSupportFragmentManager().findFragmentById(R.id.contentsFragment);
        	contentsFragment.removeContents(false);
        	return true;
        }
        if (id == R.id.user_contents){
        	contentsType = "user_contents";
        	invalidateOptionsMenu();
        	ContentsFragment contentsFragment = (ContentsFragment) getSupportFragmentManager().findFragmentById(R.id.contentsFragment);       	
        	contentsFragment.get_contents(contentsType);
        	return true;
        }
        if (id == R.id.add_contents){  
        	ContentsFragment contentsFragment = (ContentsFragment) getSupportFragmentManager().findFragmentById(R.id.contentsFragment);
        	contentsFragment.addContents();
        	return true;
        }
        if (id == R.id.search_user_contents){  
        	EditText editText = (EditText) findViewById(R.id.editText_contents_view);
        	editText.setText("");        	
        	ImageView searchButton = (ImageView) findViewById(R.id.imageView_search_contents_view);
        	searchButton.setOnClickListener(mSearchBarClickListener);
        	ImageView closeButton = (ImageView) findViewById(R.id.imageView_close_search_contents_view);
        	closeButton.setOnClickListener(mCloseSearchBarClickListener);
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_contents);
        	linearLayout.setVisibility(View.VISIBLE);
        	
        	return true;
        }
        if (id == R.id.search_system_contents){  
        	EditText editText = (EditText) findViewById(R.id.editText_contents_view);
        	editText.setText("");        	
        	ImageView searchButton = (ImageView) findViewById(R.id.imageView_search_contents_view);
        	searchButton.setOnClickListener(mSearchBarClickListener);
        	ImageView closeButton = (ImageView) findViewById(R.id.imageView_close_search_contents_view);
        	closeButton.setOnClickListener(mCloseSearchBarClickListener);
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_contents);
        	linearLayout.setVisibility(View.VISIBLE);
        	return true;
        }
    	
        if (id == R.id.revert){
        	finish();
        	return true;
        }
        if (id == R.id.add_contents){
        	Intent intent = getIntent();
        	intent.putExtra("from_activity", "ContentsView");
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
        	LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearLayout_search_contents);
        	linearLayout.setVisibility(View.GONE);
        }
    };
    
    
    
    
    
    private OnClickListener mSearchBarClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	EditText editText = (EditText) findViewById(R.id.editText_contents_view);
        	String search = editText.getText().toString();
        	int page = 1;
        	int contents_number = 15;
        	ContentsFragment contentsFragment = (ContentsFragment) getSupportFragmentManager().findFragmentById(R.id.contentsFragment);
        	contentsFragment.contentsSearch(search, page, contents_number);
        }
    };
    
    
    
    
    
    
    
}