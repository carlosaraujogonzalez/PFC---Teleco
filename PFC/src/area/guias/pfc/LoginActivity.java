package area.guias.pfc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;

public class LoginActivity extends ActionBarActivity {
	public Language language;
	private static final String LOGTAG = "LogsAndroid";
	private SendLogin send_login;
	ProgressDialog progressDialog;
	
	
	
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        PreferenceManager.getDefaultSharedPreferences(this);
        getOverflowMenu();
        
        language = new Language();
    	
    }

    
    
  
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.login_activity, menu);
        return true;
    }  
    
    
    
    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {   	
        int id = item.getItemId();
        if (id == R.id.spanish) {
        	language.setSpanish();
            return true;
        }
        if (id == R.id.english) {
        	language.setEnglish();
            return true;
        }
        if (id == R.id.galician) {
        	language.setGalician();
            return true;
        }
        if (id == R.id.back){
        	finish();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }

    
 
    
    
    public void login(View view) throws InterruptedException, ExecutionException{
    	Login login = new Login();
    	
    	EditText email = (EditText) findViewById(R.id.email);
        EditText password = (EditText) findViewById(R.id.password);    
    	
        login.setEmail(email.getText().toString()); 
    	login.setPassword(password.getText().toString());
 
    	progressDialog = createProgressDialog("Iniciando sesión...");
    	send_login = new SendLogin();
    	send_login.execute(login);  
    }
    

    
    
    
    public ProgressDialog createProgressDialog(String texto){
    	Log.d(LOGTAG, "createProgressDialog");
    	ProgressDialog progressDialog;
    	progressDialog = new ProgressDialog(this);   	
    	progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	progressDialog.setMessage(texto);
    	progressDialog.setCancelable(true);
    	
    	progressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            	if (!send_login.isCancelled()) send_login.cancel(true);
            }
        });
    	
    	progressDialog.show();
    	progressDialog.setContentView(R.layout.progress_dialog);
    	return progressDialog;
    }
    
    
    
    
    
    public void getOverflowMenu() {
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
    
    
    
    
    
    public void startNavigationDrawerActivity(){
  	  Intent intent = new Intent(this, NavigationDrawer.class);
  	  Log.d("language", language.getStringLanguage());
  	  intent.putExtra("language", language);
	  startActivity(intent);
	  finish();
	}  
    
    
    
    
    
    public class SendLogin extends AsyncTask<Login, Void, Void> {
        protected Void doInBackground(Login... login) {
        	try {  
        		Log.d(LOGTAG, "SendLogin");            	            			
            	IP ip = new IP();
    			ArrayList<NameValuePair> loginList = new ArrayList<NameValuePair>();
    			loginList.add(new BasicNameValuePair("user[email]", login[0].getEmail()));
    			loginList.add(new BasicNameValuePair("user[password]", login[0].getPassword()));
    			DefaultHttpClient defaultHttpClient = new DefaultHttpClient();
    			HttpPost httpPost = new HttpPost(ip.ip+"/gl/users/sign_in");
    			httpPost.setEntity(new UrlEncodedFormEntity(loginList));
    			if (isCancelled()) return null;
    			defaultHttpClient.execute(httpPost);
    			Cookie cookie = defaultHttpClient.getCookieStore().getCookies().get(0);
    			SharedPreferences preferences = getSharedPreferences("datos", MODE_PRIVATE); 
    			preferences.edit().putString("Cookie", cookie.getValue()).commit();
    			progressDialog.dismiss();
    			if (isCancelled()) return null;
    			startNavigationDrawerActivity();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}    	    			
        	return null; 			
        }
    }
}