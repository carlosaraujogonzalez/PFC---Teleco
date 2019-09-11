package area.guias.pfc;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class DeviceView extends ActionBarActivity {
	public IP ip = new IP();
	private static BitmapFactory.Options generalOptions;
	private static final String LOGTAG = "LogsAndroid";
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_blue_dark));
          setContentView(R.layout.device_view);
          
          getOverflowMenu();
          
          Bundle extras = getIntent().getExtras();
          String imageUrl = extras.getString("imageUrl");
          Log.d(LOGTAG, "imageUrl: " + imageUrl);
          
          String title = extras.getString("titulo");
          
          TextView textView = (TextView) findViewById(R.id.titulo_vistadisp);      
      	  textView.setText(title);
      	  textView.setTypeface(null, Typeface.BOLD);
      	  
      	  String Descripcion = extras.getString("description");
      	  textView = (TextView) findViewById(R.id.descripcion_vistadisp);
      	  if (Descripcion.equals("")){}
      	  else textView.setText(Descripcion+"\n");
      	  
      	  ImageView imageView = (ImageView) findViewById(R.id.imagen_vistadisp);
      	  Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.imagen);
      	  imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, DeviceView.this));
		 	
      	  new DownloadDevicesImage(imageUrl).start();
     
      	  
    }

	
	
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.device_view, menu);
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
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.guide_view_tabs_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_guias));
        } 
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.guide_view_tabs_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_land_guias));
        }
        
        
    }
	
	
	
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.    	
        int id = item.getItemId();
        if (id == R.id.menu_revert){
        	finish();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	
	
	
	public Bitmap getImagePhotoUtils(Uri uri) {
        //Bitmap result = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream is = null;
        try {
            is = this.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(is, null, options);
            is.close();     
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        generalOptions = options;
        return scaleImage(options, uri, 300);
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
	

	
	
	
	
	public void loadImage(final Bitmap bm){
		runOnUiThread(new Runnable() {
	        public void run() {
	        	ImageView imageView = (ImageView) findViewById(R.id.imagen_vistadisp);
	        	if (bm != null) imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, DeviceView.this));
	        	else {
	        		Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.imagen);
		   		 	imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, DeviceView.this));
	        	}	        	
	        }
	    });
	}
	
	
	
	
	
	public static int nearest2pow(int value) {
        return value == 0 ? 0 : (32 - Integer.numberOfLeadingZeros(value - 1)) / 2;
    }
	
	
	
	
	
	public Bitmap scaleImage(BitmapFactory.Options options, Uri uri,int targetWidth) {
        if (options == null) options = generalOptions;
        Bitmap bitmap = null;
        double ratioWidth = ((float) targetWidth) / (float) options.outWidth;
        double ratioHeight = ((float) targetWidth) / (float) options.outHeight;
        double ratio = Math.min(ratioWidth, ratioHeight);
        int dstWidth = (int) Math.round(ratio * options.outWidth);
        int dstHeight = (int) Math.round(ratio * options.outHeight);
        ratio = Math.floor(1.0 / ratio);
        int sample = nearest2pow((int) ratio);
 
        options.inJustDecodeBounds = false;
        if (sample <= 0) {
            sample = 1;
        }
        options.inSampleSize = (int) sample;
        options.inPurgeable = true;
        try {
            InputStream is;
            is = getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(is, null, options);
            Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, dstWidth,
                    dstHeight, true);
            bitmap = bitmap2;
            is.close();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
 
        return bitmap;
    }
	

	
	
	
class DownloadDevicesImage extends Thread {
    String url;
    
	public DownloadDevicesImage(String Url){
		url = Url;
	}
	
        @Override 
        public void run() {
        	HttpClient cliente = new DefaultHttpClient();
        		if (url != null){
        			if (url.equals("none")==false){
        				try {
        					String BaseUrlPage = new IP().ip+url;
        					System.out.println("Url imagen dispositivo");
        					System.out.println(BaseUrlPage);
        					HttpGet solicitud = new HttpGet(BaseUrlPage);	
        					SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
        					solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
        					HttpResponse respuesta = cliente.execute(solicitud);
        					HttpEntity entity = respuesta.getEntity();
        					Bitmap loadedImage = BitmapFactory.decodeStream(entity.getContent());
        					loadImage(loadedImage);   							
        				} catch (ClientProtocolException e) {
        					e.printStackTrace();
        				} catch (IOException e) {
        					e.printStackTrace();
        				}	
        			}
        		}
        	
       	}
	}       
		
	
}
