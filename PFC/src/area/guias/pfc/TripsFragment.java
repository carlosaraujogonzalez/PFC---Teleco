package area.guias.pfc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class TripsFragment extends Fragment{
	private static area.guias.pfc.Activity activity;
	public TripsAdapter adapter;
	public String modo_edicion;
	public TripsStore tripsStore = new TripsArray();
	private ProgressDialog progressDialog;	
	public Language language;
	private String tripsType;
	private static final String LOGTAG = "LogsAndroid";
	public DownloadTripImages downloadImages;
	private final static int MINIVIEW = 1, SMALLVIEW = 2, WHOLEVIEW = 3;
	
	// SCROLL VARIABLES
	Scroll scroll;
		
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
		
		activity = new area.guias.pfc.Activity();
		activity.id = getActivity().getIntent().getExtras().getInt("activity_id");
		
		language = getActivity().getIntent().getParcelableExtra("language");
		
		adapter = new TripsAdapter(getActivity(), tripsStore.tripsList());
		GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
		gridView.setAdapter(adapter); 
		
		gridView.setOnItemClickListener(new OnItemClickListener() {	     
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), DeviceView.class);
		    	intent.putExtra("titulo", tripsStore.get(position).getName());	    	
		    	intent.putExtra("imageUrl", tripsStore.get(position).getImageUrl());
		    	intent.putExtra("description", tripsStore.get(position).getDescription());
			    startActivity(intent);
			}
	    });
		       
        if (savedInstanceState != null){
        	scroll = savedInstanceState.getParcelable("scroll");
        	
        	tripsType = savedInstanceState.getString("tripsType");
        	
        	for (int i=0; i< savedInstanceState.getParcelableArrayList("tripsList").size(); i++)
        		tripsStore.tripsList().add((Trip) savedInstanceState.getParcelableArrayList("tripsList").get(i));      	
        	
        	if (tripsStore.tripsList().size() > 0) {
        		changeVisibilityOfGridView(View.VISIBLE);
    			changeVisibilityOfEmptyView(View.INVISIBLE);
        	} else {
        		changeVisibilityOfGridView(View.INVISIBLE);
    			changeVisibilityOfEmptyView(View.VISIBLE);
        	}
        	
        	updateTripsList();
        }
        else{
        	
        	
        	changeVisibilityOfGridView(View.INVISIBLE);
        	changeVisibilityOfEmptyView(View.INVISIBLE);
        	
        	scroll = new Scroll();
            configureListViewScroll(); 
            	
            tripsType = "user_trips";
            
            get_trips(tripsType);
        }
	}
	
	
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			return inflater.inflate(R.layout.trips_fragment, container);	
	}
	

	
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	   Log.d("NavigationDrawer", "onSaveInstanceState");
	   super.onSaveInstanceState(outState);
	   outState.putParcelable("scroll", scroll);
	   outState.putString("tripsType", tripsType);
	   outState.putParcelableArrayList("tripsList", tripsStore.tripsList());	   
	}
	
	
	
	
	
	public void addTrips(){	
		final SelectedTrips selectedTrips = new SelectedTrips();
    	
		for (int i=0; i<tripsStore.tripsList().size();i++){
			
			if ((tripsStore.get(i).checked == true)&&(tripsStore.get(i).checkedString.equals("Añadir"))){
				selectedTrips.numTripsToAdd++;
			}
					
			if ((tripsStore.get(i).checkedString.equals("Añadido"))){
				selectedTrips.numTripsAdded++;
			}
					
		}
	
		final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity()); 
		dialog.setTitle("Advertencia");
		dialog.setCancelable(false);
		if (selectedTrips.numTripsToAdd > 0){		
			if (selectedTrips.numTripsToAdd > 1) dialog.setMessage("¿Quieres añadir estas salidas?");
			else {
				dialog.setMessage("¿Quieres añadir esta salida?");								
			}
			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {   		 
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new AddTrips(selectedTrips).start();
					dialog.cancel();
				}
			});  	
			dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {   		 
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});  	
			dialog.show();
		}
		else{
			if (selectedTrips.numTripsToAdd == 0) dialog.setMessage("¡No tienes ninguna salida marcada!");
			else if (selectedTrips.numTripsAdded ==tripsStore.tripsList().size()) dialog.setMessage("¡Ya tienes todas las salidas!");
			dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {   		 
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});  	
			dialog.show();
		}
    	
    }
	
	
	
	
	
	public void changeVisibilityOfEmptyView(int visibility){
		Log.d(LOGTAG, "changeVisibilityOfEmptyView");
    	getActivity().runOnUiThread(new Runnable() {
			public void run() {
				TextView textView = (TextView) getActivity().findViewById(android.R.id.empty);
				textView.setVisibility(View.INVISIBLE);
			}
		});
	}
	
	
	
	
	
	public void changeVisibilityOfGridView(int visibility){
		Log.d(LOGTAG, "changeVisibilityOfGridView");
    	getActivity().runOnUiThread(new Runnable() {
			public void run() {
				GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
				gridView.setVisibility(View.VISIBLE);
			}
		});
	}
	
	
	
	
	
	private void configureListViewScroll(){
		GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				boolean loadMore = firstVisibleItem + visibleItemCount >= scroll.getTotalCount();
			    if(loadMore){
			    	scroll.setTotalCount(scroll.getTotalCount()+scroll.getPageElements());
			    	scroll.setPage(scroll.getPage()+1);
			        System.out.println("LOAD MORE("+scroll.getPage()+")");
			        tripsSearch(scroll.getSearch(), scroll.getPage(), scroll.getPageElements());		            
			     }
			}
		});
	}
	
	
	
	
	
	public Dialog create_ok_dialog(Context context, String title, String message){
		final Dialog dialog = new Dialog(context);  
		dialog.setContentView(R.layout.dialog_one_button_layout);
		dialog.setCancelable(true);
		
		dialog.setTitle(title);
		
		TextView msg = (TextView) dialog.findViewById(R.id.message);
		msg.setText(message);
		
		Button buttonYes = (Button) dialog.findViewById(R.id.ok);
		buttonYes.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialog.dismiss();
	        	restart();
	        }
	    });
		
		return dialog;
	}
	
	
	
	
	
	protected void downloadTripImages(){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {
	        	downloadImages = new DownloadTripImages();
	        	downloadImages.execute();
	        }
	    });
	}
	
	
	
	
	
	protected void finProgressBar(){
		Log.d(LOGTAG, "finProgressBar");
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {
	        	progressDialog.dismiss();	
	        	
	        	GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
				gridView.setVisibility(View.VISIBLE);
				TextView textView = (TextView) getActivity().findViewById(android.R.id.empty);
				
				System.out.println("tripsStore.tripsList().size= "+tripsStore.tripsList().size());
				
				if (tripsStore.tripsList().size() == 0) textView.setVisibility(View.VISIBLE);				
				else textView.setVisibility(View.INVISIBLE);
	        }
	    });
	}
	
	
	
	
	
	
	public void getTrips(){
		Log.d(LOGTAG, "getTrips");
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	        	new GetTrips().start();
	        }
	    });		
	}

	
	
	
	public void getTypeOfView(String language, String urlIds, int typeOfView){
		new GetTypeOfView(language, urlIds, typeOfView).start();
	}
	
	
	
	
	
	private OnClickListener mCommentButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
        	final int position = gridView.getPositionForView(v);
            if (position != AdapterView.INVALID_POSITION) {
            	Intent intent = new Intent(getActivity(), CommentsView.class);
            	intent.putExtra("model", "Event");
            	intent.putExtra("id", tripsStore.get(position).getId());
            	intent.putExtra("title", tripsStore.get(position).getName());
            	intent.putExtra("language", language);
            	startActivity(intent);
            }
        }
    };

		
		
	    

    private OnClickListener mShareButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
        	final int position = gridView.getPositionForView(v);
            if (position != AdapterView.INVALID_POSITION) {
                //showMessage("Botón Compartir " + position + " Pulsado");
            }
        }
    };
    
    
    
    
    public String makeUrlTrips(String search, int page, int trips_number){
		String url =  new IP().ip+"/"+language.getStringLanguage()+"/events/search.json";
		String correct_search = new Funciones().adapt_search_string(search);
		
		
			if (correct_search.equals("") == false){
				url+="?search="+correct_search;
				if (page > 0) url+="&page="+page;        			
				if (trips_number > 0) url+="&events_number="+trips_number;  
			}
			else{
				if (page > 0){
					url+="?page="+page;        		
					if (trips_number > 0) url+="&events_number="+trips_number; 
				}
				else if (trips_number > 0) url+="events_number="+trips_number; 
			}
	  		
		return url;
	}
    
    
    
    
    
    public int numTripsToRemove(){
    	int numTripsToDelete=0;
    	for (int i=0;i<tripsStore.tripsList().size();i++){
			if (tripsStore.get(i).checked == true){
				numTripsToDelete++;
			}
		}
    	return numTripsToDelete;
    }
    
    
    
    
    
    public SelectedTrips numSelectedTrips(){
		SelectedTrips selectedTrips = new SelectedTrips();
		for (int i=0;i<tripsStore.tripsList().size();i++){
			if (tripsStore.tripsList().get(i).checked == true){
				selectedTrips.numTripsChecked++;
			}
		}
		return selectedTrips;
	}
    
    
    
    
    
    public int numTripsChecked(){
    	int numTripsChecked=0;
		for (int i=0;i<tripsStore.tripsList().size();i++)
			if (tripsStore.get(i).checked == true) numTripsChecked++;
		return numTripsChecked;
	}
    
    
    
    
    
    public void get_trips(String tools_type){	
    	tripsType = tools_type;
    	Log.d(LOGTAG, "get_trips");
    	if ((downloadImages != null)&&(!downloadImages.isCancelled())) downloadImages.cancel(true);
		tripsStore.deleteList();
		scroll.setTotalCount(scroll.getPageElements());
		scroll.setPage(1);
		if (tools_type.equals("user_trips")){
			progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando salidas del usuario...");
			new GetActivity().start();
		}
		else if (tools_type.equals("system_trips")){
			progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando salidas del sistema...");
			new GetTrips().start();
		}
		else Log.d(LOGTAG, "Tipo de salidas incorrecto.");
	}
	
	
	
	
	
    public void reloadOnUiThread(final String tripsType){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	            get_trips(tripsType);
	        }
	    });
	}
    
    
    
    
    
	public void removeTrips(final boolean finish){
		// Mirar el numero de dispositivos a eliminar
		final SelectedTrips selectedTrips = numSelectedTrips();
		selectedTrips.numTripsToRemove = numTripsToRemove();
		selectedTrips.numTripsChecked = numTripsChecked();
		
		final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle("Advertencia");
		dialog.setCancelable(false);
		
		if (finish == false){
			if (selectedTrips.numTripsChecked == 0){
				dialog.setMessage("Tienes que marcar alguna salida para eliminarla.");
				dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {   		 
				   @Override
				   public void onClick(DialogInterface dialog, int which) {
					  dialog.cancel();
				   }
				});  	
			}
			else if (selectedTrips.numTripsChecked>0){
				
				if (selectedTrips.numTripsChecked==1){
					if (selectedTrips.numTripsToRemove == 1) {
						if (finish == false) dialog.setMessage("¿Estás seguro de que quieres eliminar esta salida de tu actividad?");
						else dialog.setMessage("¿Quieres eliminar esta salida de tu actividad antes de salir?");
					}
				}
				else {
					if (finish == false) dialog.setMessage("¿Estás seguro de que quieres eliminar estas salidas de tu actividad?");
					else dialog.setMessage("¿Estás seguro de que quieres eliminar estas salidas de tu actividad antes de salir?");
				}
				
				dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {   		 
				   @Override
				   public void onClick(DialogInterface dialog, int which) {				   
					   new RemoveTrips(selectedTrips).start();					   				   
					   dialog.cancel();
					   //adapter.notifyDataSetChanged();	
				   }
				});  	
				dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {   		 
		    		   @Override
		    		   public void onClick(DialogInterface dialog, int which) {
		    		      dialog.cancel();
		    		   }
		    	});  	
			}		  	
			dialog.show();
		}
		
		
    }
	
	
	
	
	
	public void restart(){
		scroll = new Scroll();
        configureListViewScroll(); 
        	
        
        get_trips(tripsType);
	}
	
	
	
	
	
	public void tripsSearch(String search, int page, int trips_number){
		new Funciones().showToast(getActivity(), "No implementado en el servidor");
		/*if (page == 1) scroll.setPage(1);
		new TripsSearch(search, page, trips_number).start();*/
	}
    
	
	
	
	
	
	public void updateTripsList(){
		Log.d(LOGTAG, "updateTripsList");
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	            adapter.notifyDataSetChanged();
	        }
	    });
	}
	
	
	

    
    class AddTrips extends Thread {
    	SelectedTrips selectedTrips;
    	
    	public AddTrips(SelectedTrips selectedTrips){
    		this.selectedTrips=selectedTrips;
    	}
    	
        @Override 
        public void run() {
        	HttpClient cliente = new DefaultHttpClient();
        	String BaseUrlPage = new IP().ip+"/"+language.getStringLanguage() +"/activities/"+activity.id+".json";
        	try {
        		
        		String url = "";
				if (this.selectedTrips.numTripsToAdd > 0) url = "?addEvent=";
				
				for (int i=0; i<tripsStore.size();i++){
					if ((tripsStore.get(i).checked == true)&&(tripsStore.get(i).checkedString.equals("Añadir"))){	
						selectedTrips.numTripsToAdd--;
						if (selectedTrips.numTripsToAdd > 0) url+=tripsStore.get(i).tripId+",";
						else url+=tripsStore.get(i).tripId;							
						tripsStore.get(i).checkedString = "Añadido";
					}						
				}
				
				updateTripsList();
				BaseUrlPage+=url;
				HttpPut solicitud = new HttpPut(BaseUrlPage);
				SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
				solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				cliente.execute(solicitud);							
				System.out.println("Url de añadir salidas:");
				System.out.println(BaseUrlPage);
				
				Looper.prepare();
    				Dialog dialog = new Funciones().create_ok_dialog(getActivity(), "Advertencia", "Los eventos han sido añadidos");
    				dialog.show();
    			Looper.loop();
    		
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	updateTripsList();
        }        
    }
    
    
    
    
    
    class DownloadTripImages extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... activityList) {
        	HttpClient cliente = new DefaultHttpClient();
        	for(int i=0;i<tripsStore.size();i++){
        		if (tripsStore.get(i).getImageUrl() != null){
        			if (tripsStore.get(i).getImageUrl().equals("none")==false){
        				try {
        					String BaseUrlPage = new IP().ip+tripsStore.get(i).getImageUrl();
        					System.out.println("Url imagen salida");
        					System.out.println(BaseUrlPage);
        					HttpGet solicitud = new HttpGet(BaseUrlPage);	
        					SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        					solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
        					if (isCancelled()) break;
        					HttpResponse respuesta = cliente.execute(solicitud);
        					HttpEntity entity = respuesta.getEntity();
        					Bitmap loadedImage = BitmapFactory.decodeStream(entity.getContent());
        					tripsStore.setBitmapImage(tripsStore.get(i).getId(), loadedImage);  
        					//tripsStore.get(i).setImageUri(new Funciones().getImageUriString(getActivity(), loadedImage));
        				} catch (ClientProtocolException e) {
        					e.printStackTrace();
        				} catch (IOException e) {
        					e.printStackTrace();
        				}	
        			}
        		}
        	}
       		updateTripsList();
       		return null;
       	}
	}       





    class GetActivity extends Thread {

        @Override 
        public void run() {  
            try {           	
            	Log.d(LOGTAG, "GetActivity");
            	
            	HttpClient cliente = new DefaultHttpClient();
            	String BaseUrlPage = new IP().ip+"/gl/activities/getWholeView.json?id="+activity.getId();
            	HttpGet solicitud = new HttpGet(BaseUrlPage);	
            	SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
            	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));

            	//VER EN LOGCAT EL WHOLE VIEW JSON DEL ACTIVITY SEQUENCE
				/*HttpResponse respuesta = cliente.execute(solicitud);
				String json = EntityUtils.toString(respuesta.getEntity());
				Log.d(LOGTAG, "getWholeView activity:");
				Log.d(LOGTAG, json);*/
				
				Log.d(LOGTAG, BaseUrlPage);
				HttpResponse respuesta = cliente.execute(solicitud);
			    activity = new Funciones().readJsonActivityStream(respuesta.getEntity()).get(0);			    			    
			    
			    getTrips();
            } catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
        }        
    }
    
    
    
    
    
    public class GetTrips extends Thread {
    	List<Trip> tripsList;
    	List<Integer> idsList;
	
	
	
    	@Override 
    	public void run() {
    	
    		try {
    			Log.d(LOGTAG, "GetTrips");
    			HttpClient cliente = new DefaultHttpClient(); 
        	
    			// Url devices
    			String url_trips = null;
    			if (tripsType.equals("user_trips")){
    				
    				url_trips =  new IP().ip+"/"+language.getStringLanguage()+"/events/getWholeView.json?id=";
    				for (int i=0; i<activity.requirements.get(0).events.size(); i++){
    					url_trips = url_trips + activity.requirements.get(0).events.get(i).getId();
    					if (i<activity.requirements.get(0).events.size()-1) url_trips = url_trips+",";        		
    				}
    				    				
    			}
    			else if (tripsType.equals("system_trips")){
    				
    				url_trips =  new IP().ip+"/"+language.getStringLanguage()+"/events.json";
    				HttpGet solicitud = new HttpGet(url_trips);
    				SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
    				solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
    				HttpResponse respuesta = cliente.execute(solicitud);
    				HttpEntity entity = respuesta.getEntity();
    				idsList = new Funciones().readJsonIdsStream(entity);
		        
    				// PRUEBA		        
    				/*respuesta = cliente.execute(solicitud);
    				String json = EntityUtils.toString(respuesta.getEntity());
    				System.out.println("JSON WHOLE VIEW EVENTS IDS: "+json);*/
    				// FIN DE PRUEBA
				
    				url_trips =  new IP().ip+"/"+language.getStringLanguage()+"/events/getWholeView.json?id=";
    				for (int i=0; i<idsList.size(); i++){
    					url_trips +=  idsList.get(i);
    					if (i<idsList.size()-1) url_trips += ",";        		
    				}    				    						
        			
    			}
    			
    			// TRIPS  			
    			System.out.println("Url events: "+url_trips);
    			HttpGet solicitud = new HttpGet(url_trips);
    			SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
    			solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
	        
    			// PRUEBA		        
    			/*HttpResponse respuesta = cliente.execute(solicitud);
    			String json = EntityUtils.toString(respuesta.getEntity());
    			System.out.println("JSON WHOLE VIEW EVENT: "+json);*/
    			// FIN DE PRUEBA
			
    			HttpResponse respuesta = cliente.execute(solicitud);
    			HttpEntity entity = respuesta.getEntity();
    			
    			tripsList = new ArrayList<Trip>();
    			tripsList = new Funciones().readJsonEventsStream(entity);	
    			
    			if (tripsType.equals("system_trips")){
    				// Mirar que eventos ya tenemos añadidos
        			for (int i=0; i<activity.requirements.get(0).events.size(); i++){
        				for (int j=0; j<idsList.size(); j++){
        					if (tripsList.get(j).getId() == activity.requirements.get(0).events.get(i).getId()) 
        						tripsList.get(j).checkedString = "Añadido";
        				}
        			}
    			}
    			
    			
    			for (int i=0;i<tripsList.size();i++) tripsStore.saveTrip(tripsList.get(i));
    					    			 	        		
    			
			    // IMAGES
    			if (tripsStore.size()>0) downloadTripImages();				
			
    			finProgressBar();
    			updateTripsList();
    		} catch (ClientProtocolException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}				
    	}        
    }




    public class GetTypeOfView extends Thread {  	   	
    	String language, urlIds, typeOfView;
		
    	public GetTypeOfView(String language, String urlIds, int TypeOfView){
    		this.language = language;
    		this.urlIds = urlIds;
    		if (TypeOfView == MINIVIEW) typeOfView = "getMiniView";
    		else if (TypeOfView == SMALLVIEW) typeOfView = "getSmallView";
    		else typeOfView = "getWholeView";
    	}
    	   	
        @Override 
        public void run() {	
        	System.out.println("GetTypeOfView");
        	SharedPreferences preferences = getActivity().getSharedPreferences( "datos", Context.MODE_PRIVATE);  
        	String url = new IP().ip + "/" + this.language + "/events/" + typeOfView + ".json?" + this.urlIds; 
        	HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
			try {
				HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
				
				List<Trip> eventsList = new Funciones().readJsonTripStream(httpResponse.getEntity());
				
				for (int i=0;i<eventsList.size();i++) tripsStore.saveTrip(eventsList.get(i));
						 	
				updateTripsList();
				downloadTripImages();				
				finProgressBar();
								
				// PRUEBA
				/*System.out.println(url);
				httpResponse = new DefaultHttpClient().execute(httpGet);
				System.out.println(EntityUtils.toString(httpResponse.getEntity()));*/
				// PRUEBA				
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
        }             
    }
    
    
    
    

    public class SelectedTrips{
    	
    	int numTripsChecked;
    	int numTripsToAdd;
    	int numTripsAdded;
    	int numTripsToRemove;
    	
    	public SelectedTrips(){
    		this.numTripsChecked=0;  		
    		this.numTripsToAdd=0;
    		this.numTripsAdded=0;
    		this.numTripsToRemove=0;   				
    	}
		
    }





    public class TripsAdapter extends BaseAdapter {
    	private final Activity actividad;
    	private final ArrayList<Trip> lista;
    
   
    
    	public TripsAdapter(Activity actividad, ArrayList<Trip> trips) {
    		super();
    		this.actividad = actividad;
    		this.lista = trips;
    	}
    
    
    
    	@Override
    	public int getCount() {
    		return lista.size();
    	}

	
	
    	@Override
    	public Object getItem(int position) {
    		return lista.get(position);
    	}

	

    	@Override
    	public long getItemId(int position) {
    		return position;
    	}

	
    	
    	@Override
    	public View getView(final int position, View convertView, ViewGroup parent) {
    		ItemHolder myHolder = new ItemHolder();
    		ItemHolderView view = (ItemHolderView) convertView;
		
    		if (view == null){
    			view = new ItemHolderView(getActivity());
  	 
    			myHolder.title = (TextView) view.findViewById(R.id.title);
    			myHolder.description = (TextView) view.findViewById(R.id.description);
    			myHolder.keywords = (TextView) view.findViewById(R.id.keywords);
    			myHolder.image = (ImageView) view.findViewById(R.id.image);
    			myHolder.comment = (ImageButton) view.findViewById(R.id.comment);
    			myHolder.comment.setOnClickListener(mCommentButtonClickListener);
    			myHolder.share = (ImageButton) view.findViewById(R.id.share);
    			myHolder.share.setOnClickListener(mShareButtonClickListener);
    			myHolder.checkbox = (CheckBox) view.findViewById(R.id.checkbox);	
    			myHolder.horizontalLine = (View) view.findViewById(R.id.horizontalLine);
        	 
    			view.setTag(myHolder);
    			view.createViews();
    		}
    		else myHolder = (ItemHolder) view.getTag();
	         
    		if (lista.get(position).getName() != "") view.setTitle(lista.get(position).getName());
    		else view.setTitle("Título");    
         
    		if (lista.get(position).getShortDescription() != "") view.setDescription(lista.get(position).getShortDescription());
    		else view.setDescription("Descripción");       
         
    		if (lista.get(position).getBitmapImage() != null){
    			view.setImage(ImageHelper.getRoundedCornerBitmap(lista.get(position).getBitmapImage(), 12, this.actividad));
    		}
    		else { // Imagen por defecto (NO IMAGE)
    			Bitmap bm = BitmapFactory.decodeResource(this.actividad.getResources(), R.drawable.imagen);
    			view.setImage(ImageHelper.getRoundedCornerBitmap(bm, 12, this.actividad));
    		}
         
    		myHolder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) lista.get(position).setChecked(true);
					else lista.get(position).setChecked(false);
				}
    		});
    		if (lista.get(position).getChecked() == true) view.setChecked(true);
    		else view.setChecked(false);
    		if (tripsType.equals("user_trips")) view.setCheckboxText("Eliminar");
    		else view.setCheckboxText("Añadir");
    		if (tripsType.equals("system_trips"))
    			if (lista.get(position).checkedString.equals("Añadido")){
    				view.setChecked(true);
    				view.setCheckboxText("Añadido");
    			}
         
    		return view;
    	}
	
    }





    public class TripsSearch extends Thread {	
        String search;
        int page, trips_number;
            
        public TripsSearch(String search, int page, int trips_number) {
        	progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando eventos...");
            this.search = search;
            this.page = page;
            this.trips_number = trips_number;
        }

        @Override 
        public void run() {   
    		try {
    			if (scroll.getPage() == 1) tripsStore.deleteList();
    			scroll.setMethod(Scroll.METHOD_SEARCH);
    			scroll.setSearch(search);
    			 
    	       	String Url = makeUrlTrips(search, page, trips_number);
    				
    	       	HttpGet httpGet = new HttpGet(Url);
    			SharedPreferences preferences = getActivity().getSharedPreferences( "datos", Context.MODE_PRIVATE);
    	       	httpGet.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
    	       	Log.d("", "url: " + Url);
    	      
    	       	//PRUEBA
				HttpResponse httpResponsePrueba = new DefaultHttpClient().execute(httpGet);
				if (httpResponsePrueba.getEntity() != null) System.out.println(EntityUtils.toString(httpResponsePrueba.getEntity()));
				// FIN DE PRUEBA
				
    			HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);   			
    			HttpEntity httpEntity = httpResponse.getEntity();
    			
    			if (httpEntity != null){
    				List<Integer> listInteger = new Funciones().readJsonIdsStream(httpResponse.getEntity());
    				
    				String urlIds = null;
    				if (tripsType.equals("user_trips")){
    					// Mirar si algún id devuelto por "search" coincide con alguno id del contexto técnico
    					List<Integer> newList = new ArrayList<Integer>();
    					for (int i=0; i<activity.requirements.get(0).events.size(); i++){
    						for (int j=0; j<listInteger.size(); j++){
    							if (listInteger.get(j) == activity.requirements.get(0).events.get(i).getId()){
    								newList.add(listInteger.get(j));
    							}
    						}	   					
    					}
    					urlIds = new Funciones().makeUrlIds(newList);
    				}
    				else if (tripsType.equals("system_trips")){
    					urlIds = new Funciones().makeUrlIds(listInteger);
    				}
    				     				
    				getTypeOfView(language.getStringLanguage(), urlIds, WHOLEVIEW);	
    			}
    			else finProgressBar();
				
    		} catch (UnsupportedEncodingException e1) {
    			e1.printStackTrace();
    		} catch (ClientProtocolException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}				              
        }
    }
    
    
    
    
    
    class RemoveTrips extends Thread {
        SelectedTrips selectedTrips;
        
		public RemoveTrips(SelectedTrips selectedTrips){
			this.selectedTrips=selectedTrips;
		}
		
        @Override 
        public void run() {        	
			try {				
				HttpClient cliente = new DefaultHttpClient();
				String Url = new IP().ip+"/"+language.getStringLanguage()+"/activities/"+activity.id+".json?";
				String urlEvents="";
				if (this.selectedTrips.numTripsToRemove > 0){					
					urlEvents = "remEvent=";
				}				
				
				for (int i=0;i<tripsStore.tripsList().size();i++){
					
					if (tripsStore.tripsList().get(i).checked==true){
						this.selectedTrips.numTripsChecked--;
						this.selectedTrips.numTripsToRemove--;
						if (this.selectedTrips.numTripsToRemove==0) urlEvents+=tripsStore.tripsList().get(i).tripId;
						else urlEvents+=tripsStore.tripsList().get(i).tripId+",";						
					}
					
				}
				Url+=urlEvents;
				HttpPut solicitudBorrar = new HttpPut(Url);
				SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
				solicitudBorrar.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				System.out.println("Url para borrar eventos: "+Url);
				cliente.execute(solicitudBorrar);	
				
				Looper.prepare();
    				Dialog dialog = create_ok_dialog(getActivity(), "Advertencia", "Los eventos han sido eliminados");
    				dialog.show();
    			Looper.loop();
    		
				reloadOnUiThread("user_trips");
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
        }        
    }
    
    
	
}


