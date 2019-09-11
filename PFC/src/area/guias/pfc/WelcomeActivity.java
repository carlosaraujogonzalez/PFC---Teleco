package area.guias.pfc;

import android.content.Intent;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class WelcomeActivity extends ActionBarActivity{
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        PreferenceManager.getDefaultSharedPreferences(this);          	
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.welcome_activity, menu);
        return true;
    }  
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {   	
        int id = item.getItemId();
        if (id == R.id.back) {
        	finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	public void log_in(View view){
		Intent intent = new Intent(this, LoginActivity.class);
		startActivity(intent);
		finish();
	}
	
	public void sign_in(View view){
		Intent intent = new Intent(this, SignInActivity.class);
		startActivity(intent);
	}
}
