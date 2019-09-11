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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import area.guias.pfc.CommentInsertFragment.CommentInsertFragmentListener;

public class CommentsView extends ActionBarActivity implements CommentInsertFragmentListener{
	String model;
	String title; 
	int id;
	
	@Override 
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_blue_dark));
        setContentView(R.layout.comments_view); 
        
        Configuration configuration = getResources().getConfiguration();
        onConfigurationChanged(configuration);
        
        getOverflowMenu();
        model = getIntent().getExtras().getString("model");
        id = getIntent().getExtras().getInt("id");
        title = getIntent().getExtras().getString("title");
        
        TextView textView = (TextView) findViewById(R.id.title);
        textView.setText(title);
    }
    
    

	
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.comments_view, menu);
        return true;
    }  
    
    
    
    
    
    @Override
	public void onDestroy(){
		System.out.println("onDestroy");
		CommentsViewFragment commentsViewFragment = (CommentsViewFragment) getSupportFragmentManager().findFragmentById(R.id.comments_view_fragment);
		if ((commentsViewFragment != null)&&(commentsViewFragment.getComments != null)) commentsViewFragment.getComments.cancel(!commentsViewFragment.getComments.isCancelled());
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
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.comments_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_guias));
        } 
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.comments_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_land_guias));
        }
        
        
    }
    
    
    
    
    
    @Override
	public void onFragmentIteration() {
		updateCommentsList();
	}
    
    
    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {   	
        int id = item.getItemId();
        if (id == R.id.revert){
        	finish();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
    
    
    
    
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
      super.onSaveInstanceState(savedInstanceState);
    }
    
    
    
    
    
    public void insert_comment(View view){
		EditText editText = (EditText) findViewById(R.id.commentInsert_EditText);
		String comment = editText.getText().toString();
		CommentInsertFragment insertCommentFragment = (CommentInsertFragment) getSupportFragmentManager().findFragmentById(R.id.comment_insert_fragment);
		insertCommentFragment.insert_comment(id, comment);
		editText.setText("");
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
    
    
    
 
    
    public void updateCommentsList(){
		CommentsViewFragment commentsViewFragment = (CommentsViewFragment) getSupportFragmentManager().findFragmentById(R.id.comments_view_fragment);
		commentsViewFragment.newCommentsAdded();
    }



	
}
