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

public class PersonsFragment extends Fragment{
	private static area.guias.pfc.Activity activity;
	public PersonsAdapter adapter;
	public PersonsStore personsStore = new PersonsArray();
	private ProgressDialog progressDialog;	
	public Language language;
	private String personsType;
	private static final String LOGTAG = "LogsAndroid";
	public DownloadPersonImages downloadImages;
	private final static int MINIVIEW = 1, SMALLVIEW = 2, WHOLEVIEW = 3;
	
	// SCROLL VARIABLES
	Scroll scroll;
	
	
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		activity = new area.guias.pfc.Activity();
		activity.id = getActivity().getIntent().getExtras().getInt("activity_id");
		System.out.println("activity_id: "+activity.id+ " " + activity.getId());
		
		language = getActivity().getIntent().getParcelableExtra("language");
		
		adapter = new PersonsAdapter(getActivity(), personsStore.personsList());
		GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
		gridView.setAdapter(adapter); 
		
		gridView.setOnItemClickListener(new OnItemClickListener() {	     
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), DeviceView.class);
		    	intent.putExtra("titulo", personsStore.get(position).getName());	    	
		    	intent.putExtra("imageUrl", personsStore.get(position).getImageUrl());
		    	intent.putExtra("description", personsStore.get(position).getDescription());
			    startActivity(intent);
			}
	    });
		
		scroll = new Scroll();
            
        if (savedInstanceState != null){
        	scroll = savedInstanceState.getParcelable("scroll");
        	
        	personsType = savedInstanceState.getString("personsType");
        	
        	for (int i=0; i< savedInstanceState.getParcelableArrayList("personsList").size(); i++)
        		personsStore.personsList().add((Person) savedInstanceState.getParcelableArrayList("personsList").get(i));      	
        	
        	if (personsStore.personsList().size() > 0) {
        		changeVisibilityOfGridView(View.VISIBLE);
    			changeVisibilityOfEmptyView(View.INVISIBLE);
        	} else {
        		changeVisibilityOfGridView(View.INVISIBLE);
    			changeVisibilityOfEmptyView(View.VISIBLE);
        	}
        	
        	updatePersonsList();
        }
        else{
        	scroll = new Scroll();
        	changeVisibilityOfGridView(View.INVISIBLE);
        	changeVisibilityOfEmptyView(View.INVISIBLE);
        	configureListViewScroll(); 
        	personsType = "user_persons"; // or system_trips
            get_persons(personsType);
        }
 
	}
	
	
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			return inflater.inflate(R.layout.persons_fragment, container);	
	}
	

	
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	   Log.d("NavigationDrawer", "onSaveInstanceState");
	   super.onSaveInstanceState(outState);
	   outState.putParcelable("scroll", scroll);
	   outState.putString("personsType", personsType);
	   outState.putParcelableArrayList("personsList", personsStore.personsList());	   
	}
	
	
	
	
	
	public void addPersons(){	
		final SelectedPersons selectedPersons = new SelectedPersons();
    	
		for (int i=0; i<personsStore.personsList().size();i++){
			
			if ((personsStore.get(i).checked == true)&&(personsStore.get(i).checkedString.equals("Añadir"))){
				selectedPersons.numPersonsToAdd++;
			}
					
			if ((personsStore.get(i).checkedString.equals("Añadido"))){
				selectedPersons.numPersonsAdded++;
			}
					
		}
	
		final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity()); 
		dialog.setTitle("Advertencia");
		dialog.setCancelable(false);
		if (selectedPersons.numPersonsToAdd > 0){		
			if (selectedPersons.numPersonsToAdd > 1) dialog.setMessage("¿Quieres añadir estas personas?");
			else {
				dialog.setMessage("¿Quieres añadir esta persona?");								
			}
			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {   		 
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new AddPersons(selectedPersons).start();
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
			if (selectedPersons.numPersonsToAdd == 0) dialog.setMessage("¡No tienes ninguna persona marcada!");
			else if (selectedPersons.numPersonsAdded ==personsStore.personsList().size()) dialog.setMessage("¡Ya tienes todas las personas!");
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
			        personsSearch(scroll.getSearch(), scroll.getPage(), scroll.getPageElements());	            
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
	
	
	
	
	
	protected void downloadPersonImages(){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {
	        	downloadImages = new DownloadPersonImages();
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
				
				System.out.println("personsStore.personsList().size()= "+personsStore.personsList().size());
				
				if (personsStore.personsList().size() == 0) textView.setVisibility(View.VISIBLE);				
				else textView.setVisibility(View.INVISIBLE);
	        }
	    });
	}
	
	
	
	
	
	
	public void getPersons(){
		Log.d(LOGTAG, "getPersons");
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	        	new GetPersons().start();
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
            	intent.putExtra("model", "People");
            	intent.putExtra("id", personsStore.get(position).getId());
            	intent.putExtra("title", personsStore.get(position).getName());
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
    
    
    
    
    
    public int numPersonsToRemove(){
    	int numPersonsToDelete=0;
    	for (int i=0;i<personsStore.personsList().size();i++){
			if (personsStore.get(i).checked == true){
				numPersonsToDelete++;
			}
		}
    	return numPersonsToDelete;
    }
    
    
    
    
    
    public SelectedPersons numSelectedPersons(){
		SelectedPersons selectedPersons = new SelectedPersons();
		for (int i=0;i<personsStore.personsList().size();i++){
			if (personsStore.personsList().get(i).checked == true){
				selectedPersons.numPersonsChecked++;
			}
		}
		return selectedPersons;
	}
    
    
    
    
    
    public int numPersonsChecked(){
    	int numPersonsChecked=0;
		for (int i=0;i<personsStore.personsList().size();i++)
			if (personsStore.get(i).checked == true) numPersonsChecked++;
		return numPersonsChecked;
	}
    
    
    
    
    
    public void get_persons(String persons_type){	
    	personsType = persons_type;
    	Log.d(LOGTAG, "get_persons");
    	if ((downloadImages != null)&&(!downloadImages.isCancelled())) downloadImages.cancel(true);
		personsStore.deleteList();
		scroll.setTotalCount(scroll.getPageElements());
		scroll.setPage(1);
		if (persons_type.equals("user_persons")){
			progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando personas del usuario...");
			new GetActivity().start();
		}
		else if (persons_type.equals("system_persons")){
			progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando personas del sistema...");
			new GetPersons().start();
		}
		else Log.d(LOGTAG, "Tipo de personas incorrecto.");
	}
	
	
	
	
    public String makeUrlPersons(String search, int page, int persons_number){
		String url =  new IP().ip+"/"+language.getStringLanguage()+"/people/search.json";
		String correct_search = new Funciones().adapt_search_string(search);
		
		
			if (correct_search.equals("") == false){
				url+="?search="+correct_search;
				if (page > 0) url+="&page="+page;        			
				if (persons_number > 0) url+="&people_number="+persons_number;  
			}
			else{
				if (page > 0){
					url+="?page="+page;        		
					if (persons_number > 0) url+="&people_number="+persons_number; 
				}
				else if (persons_number > 0) url+="people_number="+persons_number; 
			}
		 		
		return url;
	}
    
    
    
    
    
    public void reloadOnUiThread(final String personsType){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	            get_persons(personsType);
	        }
	    });
	}
    
    
    
    
    
	public void removePersons(final boolean finish){
		// Mirar el numero de dispositivos a eliminar
		final SelectedPersons selectedPersons = numSelectedPersons();
		selectedPersons.numPersonsToRemove = numPersonsToRemove();
		selectedPersons.numPersonsChecked = numPersonsChecked();
		
		final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle("Advertencia");
		dialog.setCancelable(false);
		
		if (finish == false){
			if (selectedPersons.numPersonsChecked == 0){
				dialog.setMessage("Tienes que marcar alguna persona para eliminarla.");
				dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {   		 
				   @Override
				   public void onClick(DialogInterface dialog, int which) {
					  dialog.cancel();
				   }
				});  	
			}
			else if (selectedPersons.numPersonsChecked>0){
				
				if (selectedPersons.numPersonsChecked==1){
					if (selectedPersons.numPersonsToRemove == 1) {
						if (finish == false) dialog.setMessage("¿Estás seguro de que quieres eliminar esta persona de tu actividad?");
						else dialog.setMessage("¿Quieres eliminar esta persona de tu actividad antes de salir?");
					}
				}
				else {
					if (finish == false) dialog.setMessage("¿Estás seguro de que quieres eliminar estas personas de tu actividad?");
					else dialog.setMessage("¿Estás seguro de que quieres eliminar estas personas de tu actividad antes de salir?");
				}
				
				dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {   		 
				   @Override
				   public void onClick(DialogInterface dialog, int which) {				   
					   new RemovePersons(selectedPersons).start();					   				   
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
        	
		new GetPersons().start();
	}
	
	
	
	
	
	public void personsSearch(String search, int page, int persons_number){
		new Funciones().showToast(getActivity(), "No implementado en el servidor");
		/*if (page == 1) scroll.setPage(1);
		new PersonsSearch(search, page, persons_number).start();*/
	}
    
	
	
	
	
	
	public void updatePersonsList(){
		Log.d(LOGTAG, "updatePersonsList");
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	            adapter.notifyDataSetChanged();
	        }
	    });
	}
	
	
	

    
    class AddPersons extends Thread {
    	SelectedPersons selectedPersons;
    	
    	public AddPersons(SelectedPersons selectedPersons){
    		this.selectedPersons=selectedPersons;
    	}
    	
        @Override 
        public void run() {
        	HttpClient cliente = new DefaultHttpClient();
        	String BaseUrlPage = new IP().ip+"/"+language.getStringLanguage() +"/activities/"+activity.id+".json";
        	try {
        		
        		String url = "";
				if (this.selectedPersons.numPersonsToAdd > 0) url = "?addPerson=";
				
				for (int i=0; i<personsStore.size();i++){
					if ((personsStore.get(i).checked == true)&&(personsStore.get(i).checkedString.equals("Añadir"))){	
						selectedPersons.numPersonsToAdd--;
						if (selectedPersons.numPersonsToAdd > 0) url+=personsStore.get(i).personId+",";
						else url+=personsStore.get(i).personId;							
						personsStore.get(i).checkedString="Añadido";
					}						
				}
				
				updatePersonsList();
				BaseUrlPage+=url;
				HttpPut solicitud = new HttpPut(BaseUrlPage);
				SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
				solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				cliente.execute(solicitud);							
				System.out.println("Url de añadir salidas:");
				System.out.println(BaseUrlPage);
				
				Looper.prepare();
    				Dialog dialog = new Funciones().create_ok_dialog(getActivity(), "Advertencia", "Los colaboradores han sido añadidos");
    				dialog.show();
    			Looper.loop();
    		
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	updatePersonsList();
        }        
    }
    
    
    
    
    
    class DownloadPersonImages extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... activityList) {
        	HttpClient cliente = new DefaultHttpClient();
        	for(int i=0;i<personsStore.size();i++){
        		if (personsStore.get(i).getImageUrl() != null){
        			if (personsStore.get(i).getImageUrl().equals("none")==false){
        				try {
        					String BaseUrlPage = new IP().ip+personsStore.get(i).getImageUrl();
        					System.out.println("Url imagen salida");
        					System.out.println(BaseUrlPage);
        					HttpGet solicitud = new HttpGet(BaseUrlPage);	
        					SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        					solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
        					if (isCancelled()) break;
        					HttpResponse respuesta = cliente.execute(solicitud);
        					HttpEntity entity = respuesta.getEntity();
        					Bitmap loadedImage = BitmapFactory.decodeStream(entity.getContent());
        					personsStore.setBitmapImage(personsStore.get(i).getId(), loadedImage);  
        					//personsStore.get(i).setImageUri(new Funciones().getImageUriString(getActivity(), loadedImage));
        				} catch (ClientProtocolException e) {
        					e.printStackTrace();
        				} catch (IOException e) {
        					e.printStackTrace();
        				}	
        			}
        		}
        	}
       		updatePersonsList();
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
				HttpResponse respuesta = cliente.execute(solicitud);
				String json = EntityUtils.toString(respuesta.getEntity());
				Log.d(LOGTAG, "getWholeView activity:");
				Log.d(LOGTAG, json);
				
				Log.d(LOGTAG, BaseUrlPage);
				respuesta = cliente.execute(solicitud);
			    activity = new Funciones().readJsonActivityStream(respuesta.getEntity()).get(0);			    			    
			    
			    getPersons();
            } catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
        }        
    }
    
    
    
    
    
    public class GetPersons extends Thread {
    	List<Person> personsList;
    	List<Integer> idsList;
	
	
	
    	@Override 
    	public void run() {
    	
    		try {
    			Log.d(LOGTAG, "GetPersons");
    			HttpClient cliente = new DefaultHttpClient(); 
        	
    			// Url devices
    			String url_persons = null;
    			if (personsType.equals("user_persons")){
    				
    				url_persons =  new IP().ip+"/"+language.getStringLanguage()+"/people/getWholeView.json?id=";
    				for (int i=0; i<activity.requirements.get(0).colaborators.size(); i++){
    					url_persons = url_persons + activity.requirements.get(0).colaborators.get(i).getId();
    					if (i<activity.requirements.get(0).colaborators.size()-1) url_persons = url_persons+",";        		
    				}
    				    				
    			}
    			else if (personsType.equals("system_persons")){
    				
    				url_persons =  new IP().ip+"/"+language.getStringLanguage()+"/people.json";
    				HttpGet solicitud = new HttpGet(url_persons);
    				SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
    				solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
    				HttpResponse respuesta = cliente.execute(solicitud);
    				HttpEntity entity = respuesta.getEntity();
    				idsList = new Funciones().readJsonIdsStream(entity);
		        
    				// PRUEBA		        
    				respuesta = cliente.execute(solicitud);
    				String json = EntityUtils.toString(respuesta.getEntity());
    				System.out.println("JSON WHOLE VIEW COLABORATORS IDS: "+json);
    				// FIN DE PRUEBA
				
    				url_persons =  new IP().ip+"/"+language.getStringLanguage()+"/people/getWholeView.json?id=";
    				for (int i=0; i<idsList.size(); i++){
    					url_persons +=  idsList.get(i);
    					if (i<idsList.size()-1) url_persons += ",";        		
    				}    				    						
    				
    			}
    			
    			// TRIPS  			
    			System.out.println("Url people: "+url_persons);
    			HttpGet solicitud = new HttpGet(url_persons);
    			SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
    			solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
	        
    			// PRUEBA		        
    			/*HttpResponse respuesta = cliente.execute(solicitud);
    			String json = EntityUtils.toString(respuesta.getEntity());
    			System.out.println("JSON WHOLE VIEW PEOPLE: "+json);*/
    			// FIN DE PRUEBA
			
    			HttpResponse respuesta = cliente.execute(solicitud);
    			HttpEntity entity = respuesta.getEntity();
    			
    			personsList = new ArrayList<Person>();
    			personsList = new Funciones().readJsonPeopleStream(entity);				
    			// Guardar herramientas de esta guía
    			for (int i=0;i<personsList.size();i++) personsStore.savePerson(personsList.get(i));
    					    			 	        		    			
			    // IMAGES
    			if (personsStore.size()>0) downloadPersonImages();				
			
    			if (personsType.equals("system_persons")){
    				// Mirar que eventos ya tenemos añadidos
        			for (int i=0; i<activity.requirements.get(0).colaborators.size(); i++){
        				for (int j=0; j<idsList.size(); j++){
        					if (personsStore.get(j).getId() == activity.requirements.get(0).colaborators.get(i).getId()) 
        						personsStore.get(j).checkedString = "Añadido";
        				}
        			}
    			}
    			
    			finProgressBar();
    			updatePersonsList();
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
        	String url = new IP().ip + "/" + this.language + "/people/" + typeOfView + ".json?" + this.urlIds; 
        	HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
			try {
				HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
				
				List<Person> peopleList = new Funciones().readJsonPeopleStream(httpResponse.getEntity());
				
				for (int i=0;i<peopleList.size();i++) personsStore.savePerson(peopleList.get(i));
						 
				updatePersonsList();
				downloadPersonImages();
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
    
    
    
    
    
    public class SelectedPersons{
    	
    	int numPersonsChecked;
    	int numPersonsToAdd;
    	int numPersonsAdded;
    	int numPersonsToRemove;
    	
    	public SelectedPersons(){
    		this.numPersonsChecked=0;  		
    		this.numPersonsToAdd=0;
    		this.numPersonsAdded=0;
    		this.numPersonsToRemove=0;   				
    	}
		
    }





    public class PersonsAdapter extends BaseAdapter {
    	private final Activity actividad;
    	private final ArrayList<Person> lista;
    
   
    
    	public PersonsAdapter(Activity actividad, ArrayList<Person> persons) {
    		super();
    		this.actividad = actividad;
    		this.lista = persons;
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
    		if (personsType.equals("user_persons")) view.setCheckboxText("Eliminar");
    		else view.setCheckboxText("Añadir");
    		if (personsType.equals("system_persons"))
    			if (lista.get(position).checkedString.equals("Añadido")){
    				view.setChecked(true);
    				view.setCheckboxText("Añadido");
    			}
    		return view;
    	}
	
    }





    public class PersonsSearch extends Thread {	
        String search;
        int page, persons_number;
            
        public PersonsSearch(String search, int page, int persons_number) {
        	progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando colaboradores...");
            this.search = search;
            this.page = page;
            this.persons_number = persons_number;
        }

        @Override 
        public void run() {   
    		try {
    			if (scroll.getPage() == 1) personsStore.deleteList();
    			scroll.setMethod(Scroll.METHOD_SEARCH);
    			scroll.setSearch(search);
    			
    	        String Url = makeUrlPersons(search, page, persons_number);
    				
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
    				if (personsType.equals("user_persons")){
    					// Mirar si algún id devuelto por "search" coincide con alguno id del contexto técnico
    					List<Integer> newList = new ArrayList<Integer>();
    					for (int i=0; i<activity.requirements.get(0).colaborators.size(); i++){
    						for (int j=0; j<listInteger.size(); j++){
    							if (listInteger.get(j) == activity.requirements.get(0).colaborators.get(i).getId()){
    								newList.add(listInteger.get(j));
    							}
    						}	   					
    					}
    					urlIds = new Funciones().makeUrlIds(newList);
    				}
    				else if (personsType.equals("system_persons")){
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
    
    
    
    
    
    class RemovePersons extends Thread {
        SelectedPersons selectedPersons;
        
		public RemovePersons(SelectedPersons selectedPersons){
			this.selectedPersons=selectedPersons;
		}
		
        @Override 
        public void run() {        	
			try {				
				HttpClient cliente = new DefaultHttpClient();
				String Url = new IP().ip+"/"+language.getStringLanguage()+"/activities/"+activity.id+".json?";
				String urlPersons="";
				if (this.selectedPersons.numPersonsToRemove > 0){					
					urlPersons = "remPerson=";
				}				
				
				for (int i=0;i<personsStore.personsList().size();i++){
					
					if (personsStore.personsList().get(i).checked==true){
						this.selectedPersons.numPersonsChecked--;
						this.selectedPersons.numPersonsToRemove--;
						if (this.selectedPersons.numPersonsToRemove==0) urlPersons+=personsStore.personsList().get(i).personId;
						else urlPersons+=personsStore.personsList().get(i).personId+",";
										
						
					}
					
				}
				Url+=urlPersons;
				HttpPut solicitudBorrar = new HttpPut(Url);
				SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
				solicitudBorrar.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				System.out.println("Url para borrar personas: "+Url);
				cliente.execute(solicitudBorrar);
				
				Looper.prepare();
    				Dialog dialog = create_ok_dialog(getActivity(), "Advertencia", "Los colaboradores han sido eliminados");
    				dialog.show();
    			Looper.loop();
    		
				reloadOnUiThread("user_persons");
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


