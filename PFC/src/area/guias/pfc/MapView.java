package area.guias.pfc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class MapView extends ActionBarActivity {
public static GoogleMap mapa;
private static SharedPreferences preferences;
public EducationalSetting educational_setting;
public Language language;
private static final String LOGTAG = "LogsAndroid";
private ProgressDialog progressDialog;
private Mode mode;
private Owner owner;
private int restart;
private static final int YES = 1, NO = 0;

	@Override
    public void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_blue_dark));
          setContentView(R.layout.map_view);
          
          getOverflowMenu();
          
          restart = NO;
          
          educational_setting = new EducationalSetting();
          educational_setting.id = getIntent().getExtras().getInt("educational_setting_id");
          language = getIntent().getParcelableExtra("language");
          mode = getIntent().getParcelableExtra("mode");
          owner = getIntent().getParcelableExtra("owner");
          
          Log.d(LOGTAG, "modo edición: " + mode.getMode());
          
          preferences = getSharedPreferences("datos",MODE_PRIVATE);

      	  mapa = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
      	  mapa.setMapType(GoogleMap.MAP_TYPE_NORMAL);  
      	  
      	  // Muevo la camara
      	  // Madrid coordinates: 40.414916636865904,-3.7039526179432865
          restart();
          
          // MAPA
          // Si hacemos Click
      	  mapa.setOnMapClickListener(new OnMapClickListener() {
      			public void onMapClick(LatLng point) {
      				// Añadir marcador
      				if (mode.getMode() == mode.EditMode()){
      					mapa.clear();
      					mapa.addMarker(new MarkerOptions().position(new LatLng(point.latitude, point.longitude)));
      					educational_setting.coordinates.longitude = point.longitude;
      					educational_setting.coordinates.latitude = point.latitude;
      				}
      			}
      	  });      	  
      	  
      	  if (mode.getMode() == Mode.EDIT_MODE) edit_mode();
    }
	
	
	
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.map_view_viewmode, menu);
        return true;
    }  
	
	
	
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {  	
        int id = item.getItemId();
        if (id == R.id.revert){
        	finish_activity();
        	return true;
        }
        if (id == R.id.view_mode){
        	mode.setMode(mode.ViewMode());
        	invalidateOptionsMenu();
        	view_mode();
        	return true;
        }
        if (id == R.id.edit_mode){
        	mode.setMode(mode.EditMode());
        	invalidateOptionsMenu();
        	edit_mode();
        	return true;
        }
        if (id == R.id.save){
        	restart = YES;
        	save();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	
	
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
	    menu.clear();
	    if (owner.getOwner() == owner.OwnerNone()){
	    	getMenuInflater().inflate(R.menu.map_view_viewmode, menu);
    		getOverflowMenu();
	    }else if (owner.getOwner() == owner.OwnerTrue()){
	    	if (mode.getMode() == mode.ViewMode()){
	    		getMenuInflater().inflate(R.menu.map_view_viewmode, menu);
	    		getOverflowMenu();
	    	}
	    	else if (mode.getMode() == mode.EditMode()){
	    		getMenuInflater().inflate(R.menu.map_view_editmode, menu);
	    		getOverflowMenu();
	    	}
	    }
	    return super.onPrepareOptionsMenu(menu);
	}
	
	
	
	
	
	public void search_address(View view){
		Geocoder geocoder = new Geocoder(this);  
		List<Address> addresses;
		EditText editText = (EditText) findViewById(R.id.editText_map_view);
		String address = editText.getText().toString();
		try {
			addresses = geocoder.getFromLocationName(address, 1);
			if(addresses.size() > 0) {
			    double latitude = addresses.get(0).getLatitude();
			    double longitude = addresses.get(0).getLongitude();
			    update_map(latitude, longitude);
			    String Address = addresses.get(0).getLocality() + ", "
			    			   + addresses.get(0).getSubAdminArea() + ", "
			    		       + addresses.get(0).getAdminArea() + ", "
			    		       + addresses.get(0).getCountryName();
			    update_address(Address);
			}
		} catch (IOException e) {
			e.printStackTrace();
			new Funciones().showToast(this, "Servicio no disponible");
		}
		
	}
	
	
	
	
	
	public void edit_mode(){
    	if (owner.getOwner() == owner.OwnerNone()){
    		AlertDialog.Builder dialog = new AlertDialog.Builder(this);    		
    		dialog.setTitle("Advertencia");
    		dialog.setMessage("No puedes editar esta guía. No eres el autor.");
    		dialog.setCancelable(false);
    		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {   		 
    		   @Override
    		   public void onClick(DialogInterface dialog, int which) {
    		      dialog.cancel();
    		   }
    		});  		
    		dialog.show();
    	}
    	else {
    		LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout_search_address);
    		ll.setVisibility(View.VISIBLE);
    	}
	}
	
	
	
	
	private void finish_activity(){
		Intent i = new Intent();
		if (restart == YES){
			i.putExtra("restart", YES);
		}
		else{
			i.putExtra("restart", NO);
		}
		setResult(RESULT_OK, i);
		finish();
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
	
	
	

	
	public void getTab3(){
		new GetTab3().start();
	}
	
	
	
	
	
	public EducationalSetting getWholeViewEducationalSettings() throws ClientProtocolException, IOException{
		String Url = new IP().ip+"/"+language.getStringLanguage()+"/educationalSettings/getWholeView.json?id="+educational_setting.id;
       	HttpGet solicitud = new HttpGet(Url);	
       	SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
       	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));

        // PRUEBA
       	/*Log.d(LOGTAG, Url);
		HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);
		String json = EntityUtils.toString(respuesta.getEntity());
		Log.d(LOGTAG, "JSON WHOLE VIEW EducationalSETTING: "+json);*/
		// FIN DE PRUEBA
		
		HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);
		EducationalSetting educationalSetting = new Funciones().readJsonESStream(respuesta.getEntity()).get(0);
		
		return educationalSetting;
	}
	
	
	
	
	
	public void restart(){
		progressDialog = new Funciones().createProgressDialog(MapView.this, "Cargando contenido...");              
        getTab3();       
	}
	
	
	
	
	
	public void save(){
    	String coordinates = "" + educational_setting.coordinates.latitude + "," + educational_setting.coordinates.longitude;
    	new EnviarContextoEducativo(coordinates, educational_setting.address).start();
	}
	
	
	
	
	
	public void update_map(final double latitude, final double longitude){
		runOnUiThread(new Runnable() {
	        public void run() {
	        	
	        	CameraUpdate cam;
	        	mapa.clear();
	        	if ((latitude == 0.0)&&(longitude == 0.0)){
	        		// asigno un marcador
	        		cam = CameraUpdateFactory.newLatLng(new LatLng(40.414916636865904,-3.7039526179432865));  
		            mapa.addMarker(new MarkerOptions().position(new LatLng(40.414916636865904,-3.7039526179432865)));
	        	}       		
	        	else{
	        		cam = CameraUpdateFactory.newLatLng(new LatLng(latitude,longitude));  
	        		mapa.addMarker(new MarkerOptions().position(new LatLng(latitude, longitude)));
	        		educational_setting.coordinates.latitude = latitude;
	        		educational_setting.coordinates.longitude = longitude;
	        	}
	            mapa.moveCamera(cam);
	            	       	         	            
	            // Asigno un nivel de zoom
	            CameraUpdate ZoomCam = CameraUpdateFactory.zoomTo(5);
	            mapa.animateCamera(ZoomCam);
	            
	        }
    	});
	}
	
	
	
	
	
	public void update_address(String address){
		GuideViewTabs.textView_address.setText(address);
	}
	
	
	
	
	
	public void view_mode(){
    	mode.setMode(mode.ViewMode());
    	LinearLayout ll = (LinearLayout) findViewById(R.id.linearLayout_search_address);
		ll.setVisibility(View.GONE);
    	restart();
	}
	
	
	
	
	
	public class EnviarContextoEducativo extends Thread {
		String coordinates, address;
               
        public EnviarContextoEducativo(String coordinates, String address) {
        	this.coordinates = coordinates;
        	this.address = address;
        }

        @Override 
        public void run() {
        	HttpClient cliente = new DefaultHttpClient();
        	HttpPut solicitud;
        	String BaseUrlPage =  new IP().ip+"/"+language.getStringLanguage()+"/educationalSettings/"+educational_setting.id+".json";       	
            MultipartEntity mpEntity = new MultipartEntity();
			try {
				if (this.coordinates.equals("")==false){
					mpEntity.addPart("coordinates", new StringBody(this.coordinates));
				}	
				
				if (this.address!=null) mpEntity.addPart("address", new StringBody(this.address, Charset.forName("UTF-8")));
				
				solicitud = new HttpPut(BaseUrlPage);
				solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				solicitud.setEntity(mpEntity);
				cliente.execute(solicitud);	
				System.out.println(BaseUrlPage);
				
				Looper.prepare();
				new Funciones().showToast(MapView.this, "dirección guardada");
				Looper.loop();
    			
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}				
        }        
    }
	
	
	
	
	
	class GetTab3 extends Thread {
        @Override 
        public void run() {       	
            try {
            	Log.d(LOGTAG, "GetTab3");
				educational_setting = getWholeViewEducationalSettings();	
				update_map(educational_setting.coordinates.latitude, educational_setting.coordinates.longitude);
				progressDialog.dismiss(); 			
            } catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}				
        }        
    }
	
	
	
	
	// Mapa	        	
	//System.out.println("latitud: " + lista.get(0).latitude + " longitud: " + lista.get(0).longitude);
	//CameraUpdate cam = CameraUpdateFactory.newLatLng(new LatLng(lista.get(0).latitude, lista.get(0).longitude));  
    //mapa.moveCamera(cam);
    // Asigno un nivel de zoom
    //CameraUpdate ZoomCam = CameraUpdateFactory.zoomTo(8);
    //mapa.animateCamera(ZoomCam);
    // Añadir marcador
    //mapa.addMarker(new MarkerOptions().position(new LatLng(lista.get(0).latitude, lista.get(0).longitude)));
    // EJEMPLO
    /*mapa.setOnCameraChangeListener(new OnCameraChangeListener() {
	    public void onCameraChange(CameraPosition position) {
	        Toast.makeText(
	            VistaGuia.this,
	            "Cambio Cámara\n" +
	            "Lat: " + position.target.latitude + "\n" +
	            "Lng: " + position.target.longitude + "\n" +
	            "Zoom: " + position.zoom + "\n" +
	            "Orientación: " + position.bearing + "\n" +
	            "Ángulo: " + position.tilt,
	            Toast.LENGTH_SHORT).show();
	    }
	});*/
}
