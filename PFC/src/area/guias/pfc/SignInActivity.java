package area.guias.pfc;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class SignInActivity extends ActionBarActivity {
	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sign_in_activity);
        PreferenceManager.getDefaultSharedPreferences(this);   	
    }
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.sign_in_activity, menu);
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
}
