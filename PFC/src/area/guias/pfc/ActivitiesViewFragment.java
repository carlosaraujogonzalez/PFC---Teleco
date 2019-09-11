package area.guias.pfc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

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
import android.view.InflateException;
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
import android.widget.ListView;
import android.widget.TextView;

public class ActivitiesViewFragment extends Fragment{
	private MiniActivityListAdapter adapter;
	public ActivitiesStore store = new ActivitiesArray<ActivityMini>();
	private ProgressDialog progressDialog;
	private String activitiesType;
	Language language;
	private static final String LOGTAG = "LogsAndroid";
	private Mode mode;
	private Owner owner;
	private Guide guia;
	public DownloadActivityImages downloadImages;
	private final static int MINIVIEW = 1, SMALLVIEW = 2, WHOLEVIEW = 3;
	private View rootView;
	
	// Scroll
	private Scroll scroll;
		
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
				
		owner = getActivity().getIntent().getParcelableExtra("owner");

		language = getActivity().getIntent().getParcelableExtra("Language");
		
		adapter = new MiniActivityListAdapter(getActivity(), store.getList());  
		
		guia =  getActivity().getIntent().getParcelableExtra("guia");
		
		GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
		gridView.setAdapter(adapter);		
		gridView.setOnItemClickListener(new OnItemClickListener() {	     
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), ActivityView.class);
                intent.putExtra("activity", store.getList().get(position));
                intent.putExtra("owner", owner);
                intent.putExtra("language", language);
                if (activitiesType.equals("userActivities")) intent.putExtra("activityType", "userActivity");
                else if (activitiesType.equals("systemActivities")) intent.putExtra("activityType", "systemActivity");
                startActivity(intent);
			}
	    });
				
		if( savedInstanceState != null ) {
        	
        	scroll = savedInstanceState.getParcelable("scroll");
        	
        	mode = savedInstanceState.getParcelable("mode");
        	
        	activitiesType = savedInstanceState.getString("activitiesType");    
        	
        	for (int i=0; i< savedInstanceState.getParcelableArrayList("activitiesList").size(); i++)
        		store.getList().add((ActivityMini) savedInstanceState.getParcelableArrayList("activitiesList").get(i));      	
        	
        	if (store.getList().size() > 0) {
        		changeVisibilityOfGridView(View.VISIBLE);
    			changeVisibilityOfEmptyView(View.INVISIBLE);
        	} else {
        		changeVisibilityOfGridView(View.INVISIBLE);
    			changeVisibilityOfEmptyView(View.VISIBLE);
        	}
        	
        	actualizarListaActividades();
        }
		else{
			
			changeVisibilityOfGridView(View.INVISIBLE);
			changeVisibilityOfEmptyView(View.INVISIBLE);
			
			scroll = new Scroll();
	        configureActivitiesScroll();
			
			mode = getActivity().getIntent().getParcelableExtra("mode");
			
			activitiesType = "userActivities";
			
			restart(activitiesType);
		}
	}
	
	
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		if (rootView != null) {
	        ViewGroup parent = (ViewGroup) rootView.getParent();
	        if (parent != null) parent.removeView(rootView);
	    }
	    try {
	        rootView = inflater.inflate(R.layout.activities_view_fragment, container, false);
	    } catch (InflateException e) {
	    	
	    }	
		return rootView;	
	}
	
	
	
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	   Log.d("NavigationDrawer", "onSaveInstanceState");
	   super.onSaveInstanceState(outState);
	   outState.putParcelable("scroll", scroll);
	   outState.putParcelable("mode", mode);
	   outState.putString("activitiesType", activitiesType);
	   outState.putParcelableArrayList("activitiesList", store.getList());	   
	}
	
	
	
	
	
	public void restart(String actType){	
		activitiesType = actType;
		store.deleteList();
		scroll = new Scroll();
		if ((downloadImages != null)&&(!downloadImages.isCancelled())) downloadImages.cancel(true);
		if (activitiesType.equals("userActivities")){
			progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando actividades del usuario...");
			new GetActivities(activitiesType, scroll.getPage(), scroll.getPageElements()).start();
		}
		else if (activitiesType.equals("systemActivities")){
			progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando actividades del sistema...");
			new GetActivities(activitiesType, scroll.getPage(), scroll.getPageElements()).start();
		}
		else Log.d(LOGTAG, "Tipo de actividades incorrecto.");
	}
	
	
	
	
	
	public void configureActivitiesScroll(){
		Log.d(LOGTAG, "configureActivitiesScroll");
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
			        Log.d(LOGTAG, "LOAD MORE("+scroll.getPage()+")");
			        
			        if (activitiesType.equals("userActivities")) progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando actividades del usuario...");
			        else progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando actividades del sistema...");
			        
			        if (scroll.getMethod() == Scroll.getMethodGet()) new GetActivities(activitiesType, scroll.getPage(), scroll.getPageElements()).start();
			        else if (scroll.getMethod() == Scroll.getMethodSearch()) new ActivitiesSearch(scroll.getSearch(), scroll.getPage(), scroll.getPageElements()).start();	         
			    }
			}
		});
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
	        	restart(activitiesType);
	        }
	    });
		
		return dialog;
	}
	
	
	
	
	
	protected void downloadImages(final List<ActivityMini> activityList){
		Log.d("ActivitiesViewFragment", "downloadImages");
		getActivity().runOnUiThread(new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				Log.d(LOGTAG, "downloadImages");
				downloadImages = new DownloadActivityImages();
				downloadImages.execute(activityList);
			}
		});
	}
	
	
	
	
	
	protected void finProgressBar(){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {
	        	progressDialog.dismiss();
	        	adapter.notifyDataSetChanged();	        	
	        	GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
	    		gridView.setVisibility(View.VISIBLE);
	        }
	    });
	}
	
	
	
	
	
	public void updateUI(){
        getActivity().runOnUiThread(new Runnable() {
            public void run() {
                adapter.notifyDataSetChanged();
            }
        });
    }
	
	
	

	
	public void removeActivities(boolean finish){
		// Mirar el numero de dispositivos a eliminar
		int numActivityChecked=numSelectedActivities();
				
		final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle("Advertencia");
		dialog.setCancelable(false);
		if (finish == false){
			if (numActivityChecked == 0){
				dialog.setMessage("Tienes que marcar alguna actividad para eliminarla.");
				dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {   		 
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});  	
			}
			else if (numActivityChecked>0){
				if (numActivityChecked==1) dialog.setMessage("¿Estás seguro de que quieres eliminar esta actividad de tu secuencia de actividades?");
				else dialog.setMessage("¿Estás seguro de que quieres eliminar estas actividades de tu secuencia de actividades?");
				dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {   		 
					@Override
					public void onClick(DialogInterface dialog, int which) {
					   int numActivityChecked=0;
					   List<ActivityMini> activityList = store.getList();
					   for (int i=0;i<activityList.size();i++)
						   if (activityList.get(i).getChecked() == true) numActivityChecked++;				
						   new RemoveActivities(numActivityChecked).start();					   				   
						   dialog.cancel();
						   adapter.notifyDataSetChanged();	
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
		if (finish ==  true){
			if (numActivityChecked==1) dialog.setMessage("¿Quieres eliminar esta actividad de tu secuencia de actividades antes de salir?");
			else dialog.setMessage("¿Quieres eliminar estas actividades de tu secuencia de actividades antes de salir?");
			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {   		 
				@Override
				public void onClick(DialogInterface dialog, int which) {
				   int numActivityChecked=0;
				   List<ActivityMini> activityList = store.getList();
				   for (int i=0;i<activityList.size();i++)
					   if (activityList.get(i).getChecked() == true){
						   store.removeActivity(activityList.get(i).getId());
						   numActivityChecked++;
					   }					   
					   new RemoveActivities(numActivityChecked).start();					   				   
					   dialog.cancel();
					   adapter.notifyDataSetChanged();	
					   getActivity().finish();
				   }
			});  	
			dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {   		 
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
					getActivity().finish();
				}
		    });  	
			dialog.show();
		}
	}
	
	
	
	
	
	public int numSelectedActivities(){
		int numActivitiesChecked=0;
		for (int i=0;i<store.getList().size();i++){
			if (store.getList().get(i).getChecked() == true) numActivitiesChecked++;
		}
		return numActivitiesChecked;
	}
	
	
	

	
	public void updateActivityList(){   	
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	 
	            adapter.notifyDataSetChanged();	         
	        }
	    });
	}
	
	
	
	
	
	public void addActivities(){
		Log.d("", "addActivities");
		int numActivities2Add=0;
    	
		Log.d("", "contando actividades a añadir...");
		for (int i=0; i<store.getList().size();i++){
			if ((store.getList().get(i).getChecked() == true)&&(store.getList().get(i).getCheckedString().equals("Añadir")))
					numActivities2Add++;
		}
		Log.d("", "actividades a añadir: " + numActivities2Add);
		final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity()); 
		dialog.setTitle("Advertencia");
		dialog.setCancelable(false);
		if (numActivities2Add > 0){		
			if (numActivities2Add > 1){
				dialog.setMessage("No puedes añadir más de una actividad a la vez");
				dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {   		 
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.cancel();
					}
				});  	
				dialog.show();
			}
			else{
				final int act2add = numActivities2Add;
				dialog.setMessage("¿Quieres añadir esta actividad?");								
				dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {   		 
				
					@Override
					public void onClick(DialogInterface dialog, int which) {
						new AddActivities(act2add).start();
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
		}
		else{
			if (numActivities2Add == 0) dialog.setMessage("¡No tienes ninguna actividad marcada!");
			dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {   		 
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.cancel();
				}
			});  	
			dialog.show();
		}
	}
	
	
	
	
	
	public void actualizarListaActividades(){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	        	adapter.notifyDataSetChanged();
	        }
	    });
	}
	
	
	
	
	
	public void activitiesSearch(String search, int page, int activities_number){
		if (page == 1) scroll.setPage(1);
		new ActivitiesSearch(search, page, activities_number).start();
	}
	
	
	
	
	public void getTypeOfView(String language, String urlIds, int typeOfView){
		new GetTypeOfView(language, urlIds, typeOfView).start();
	}
	
	
	
	
	
	public String makeUrlActivities(String search, int page, int activities_number){
		String url =  new IP().ip+"/"+language.getStringLanguage()+"/activities/search.json";
       	String correct_search = new Funciones().adapt_search_string(search);
       	
		
			if (search.equals("") == false){
				url+="?search="+correct_search;
				if (page > 0) url+="&page="+page;        			
				if (activities_number > 0) url+="&activities_number="+activities_number;  
			}
			else{
				if (page > 0){
					url+="?page="+page;        		
					if (activities_number > 0) url+="&activities_number="+activities_number; 
				}
				else if (activities_number > 0) url+="activities_number="+activities_number; 
			}
		  		
		return url;
	}
	
	
	
	
	
	public void select_mode(int mod){
		mode.setMode(mod);
		updateActivityList();
	}
	
	
	
	
	
	private OnClickListener mCommentButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
        	final int position = gridView.getPositionForView(v);
            if (position != ListView.INVALID_POSITION) {
            	Intent intent = new Intent(getActivity(), CommentsView.class);
            	intent.putExtra("model", "Activity");
            	intent.putExtra("id", store.getList().get(position).getId());
            	intent.putExtra("title", store.getList().get(position).getName());
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
            if (position != ListView.INVALID_POSITION) {
                //showMessage("Botón Compartir " + position + " Pulsado");
            }
        }
    };
    
    
    
    
    
	public class GetActivities extends Thread {
		String activitiesType;
		int page, activities_number;
		
		public GetActivities(String activitiesType, int page, int activities_number){
			this.activitiesType = activitiesType;
			this.page = page;
			this.activities_number = activities_number;
		}
		
        @Override 
        public void run() {       	
            try {           	
            	scroll.setMethod(Scroll.getMethodGet());
            	if (activitiesType.equals("userActivities")){
            		Guide guia =  getActivity().getIntent().getParcelableExtra("guia");
            		DefaultHttpClient cliente = new DefaultHttpClient();
                	String BaseUrlPage = new IP().ip+"/"+language.getStringLanguage()+"/activitySequence/getWholeView.json?id="+guia.activity_sequence.id;
                	HttpGet solicitud = new HttpGet(BaseUrlPage);
                	SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
                	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
       				HttpResponse respuesta = cliente.execute(solicitud);
    				List<ActivitySequence> asequence = new Funciones().readJsonASStream(respuesta.getEntity());
    				
    				guia.activity_sequence.activitiesIds = asequence.get(0).activitiesIds;    				
                	String UrlId = new IP().ip+"/"+language.getStringLanguage()+"/activities/getSmallView.json?id=";                	
                	for (int i=0; i<guia.activity_sequence.activitiesIds.size(); i++)
                        if (i==guia.activity_sequence.activitiesIds.size()-1) UrlId = UrlId+guia.activity_sequence.activitiesIds.get(i);
                        else UrlId = UrlId+guia.activity_sequence.activitiesIds.get(i)+",";
                    System.out.println("Url Id= " + UrlId);
                    solicitud = new HttpGet(UrlId);
                    solicitud.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));          
    				respuesta = cliente.execute(solicitud);
                    if(respuesta.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                        store.deleteList();
                        List<ActivityMini> activityList = new Funciones().readJsonMiniActivityStream(respuesta.getEntity(), "User");
                        // LISTA DE Activities
                        for (ActivityMini mi : activityList) {
                            System.out.println(mi.toString());
                            store.saveActivity(mi);
                        }
                    }
            	}
            	
            	if (activitiesType.equals("systemActivities")){            		
            		DefaultHttpClient cliente = new DefaultHttpClient();
                	String url = new IP().ip+"/"+language.getStringLanguage()+"/activities.json";
                	    			        			        	        				
        			if (this.page > 0){
        				url+="?page="+this.page;        		
        				if (this.activities_number > 0) url+="&activities_number="+this.activities_number; 
        			}
        			else if (this.activities_number > 0) url+="?activities_number="+this.activities_number; 
        				         			  		        		
                    HttpGet solicitud = new HttpGet(url);
                    SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
                    solicitud.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));                	
    				HttpResponse respuesta = cliente.execute(solicitud);
    				List<ActivityMini> activityList = new Funciones().readJsonMiniActivityStream(respuesta.getEntity(), "System");	
    				Guide guia =  getActivity().getIntent().getParcelableExtra("guia");
    				
    				for (int i=0; i<activityList.size(); i++){
    	        		for (int j=0; j<guia.activity_sequence.activitiesIds.size(); j++){
    		        		if (guia.activity_sequence.activitiesIds.get(j) == activityList.get(i).getId()){
    		        			activityList.get(i).setChecked(true);
    		        			activityList.get(i).setCheckedString("Añadida");
    		        		}
    		        	}	 
    	        	}	
    				
    				String UrlId = new IP().ip+"/"+language.getStringLanguage()+"/activities/getSmallView.json?id=";
    				for (int i=0; i<activityList.size(); i++)
                        if (i==activityList.size()-1) UrlId += activityList.get(i).getId();
                        else UrlId += activityList.get(i).getId()+",";
                    System.out.println("UrlId= " + UrlId);
                    solicitud = new HttpGet(UrlId);
                    solicitud.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));                  	
    				respuesta = cliente.execute(solicitud);
                    if(respuesta.getStatusLine().getStatusCode() == HttpStatus.SC_OK){
                        //store.deleteList();
                        activityList = new Funciones().readJsonMiniActivityStream(respuesta.getEntity(), "System");
                        // LISTA DE Activities
                        for (ActivityMini mi : activityList) {
                            System.out.println(mi.toString());
                            store.saveActivity(mi);
                        }
                    }                             
            	}
            	
            	finProgressBar(); 
            	if(store.getList() != null) downloadImages(store.getList());
            	
            } catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}				
         }        
    }
	
	
	
	
	
	class DownloadActivityImages extends AsyncTask<List<ActivityMini>, Void, Void> {
        protected Void doInBackground(List<ActivityMini>... activityList) {
        	HttpClient cliente = new DefaultHttpClient();
        	for(int i=0;i<activityList[0].size();i++){
        		if (activityList[0].get(i).getElement_image_file_name() != null){
        			if (activityList[0].get(i).getElement_image_file_name().equals("none")==false){
        				try {
        					String BaseUrlPage = new IP().ip+activityList[0].get(i).getElement_image_file_name();
        					System.out.println("Url imagen actividad");
        					System.out.println(BaseUrlPage);
        					HttpGet solicitud = new HttpGet(BaseUrlPage);	
        					SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        					solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
        					if (isCancelled()) break;
        					HttpResponse respuesta = cliente.execute(solicitud);
        					HttpEntity entity = respuesta.getEntity();
        					Bitmap loadedImage = BitmapFactory.decodeStream(entity.getContent());
        					if (loadedImage != null) store.setImage(activityList[0].get(i).getId(), loadedImage);   							
        				} catch (ClientProtocolException e) {
        					e.printStackTrace();
        				} catch (IOException e) {
        					e.printStackTrace();
        				}	
        			}
        		}
        	}
       		updateUI();
       		return null;
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
        	String url = new IP().ip + "/" + this.language + "/activities/" + typeOfView + ".json?" + this.urlIds; 
        	HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
			try {
				Log.d("", "url: " + url);
				HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
				
				List<ActivityMini> activityList = new Funciones().readJsonMiniActivityStream(httpResponse.getEntity(), activitiesType);
				
				for (int i=0;i<activityList.size();i++) store.saveActivity(activityList.get(i));
						 				
				downloadImages(activityList);				
				
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
	
	
	
	
	
	class AddActivities extends Thread {
		int num_activities_to_add;
		
		public AddActivities(int numActivitiesToAdd){
			num_activities_to_add = numActivitiesToAdd;
		}

        @Override 
        public void run() {
        	Log.d("", "AddActivities");
        	Guide guia =  getActivity().getIntent().getParcelableExtra("guia");
        	HttpClient cliente = new DefaultHttpClient();
        	String BaseUrlPage = new IP().ip+"/"+language.getStringLanguage()+"/activitySequence/addActivity.json?activitySequence_id="+guia.activity_sequence.id+"&activity_id=";
        	try {
				
				Log.d("", "numero de actividades a añadir: " + num_activities_to_add);
				if (num_activities_to_add != 0){
					Log.d("", "creando url...");
					for (int i=0; i<store.getList().size();i++){
						if ((store.getList().get(i).getChecked() == true)&&(store.getList().get(i).getCheckedString().equals("Añadir"))){	
							num_activities_to_add--;
							BaseUrlPage+=store.getList().get(i).getId();
							if (num_activities_to_add > 0) BaseUrlPage += ",";
						}						
					}
					HttpPost solicitud = new HttpPost(BaseUrlPage);
					SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
					solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
					
					Log.d("", "url: " + BaseUrlPage);					
					HttpResponse response = cliente.execute(solicitud);
					Log.d("", "response: " + EntityUtils.toString(response.getEntity()));
					
					Looper.prepare();
        				Dialog dialog = new Funciones().create_ok_dialog(getActivity(), "Advertencia", "La actividad ha sido añadida");
        				dialog.show();
        			Looper.loop();
        		
				}				
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	updateActivityList();
        }        
    }
	
	
	
	
	
	class RemoveActivities extends Thread {
        int numActivityChecked;
		public RemoveActivities(int numActivityChecked){
			this.numActivityChecked=numActivityChecked;
		}
		
        @Override 
        public void run() {        	
			try {	
				Log.d("ActivitiesViewFragment", "RemoveActivities");
				Guide guia =  getActivity().getIntent().getParcelableExtra("guia");
				HttpClient cliente = new DefaultHttpClient();
				String BaseUrlPage = new IP().ip+"/"+language.getStringLanguage()+"/activitySequence/deleteActivity.json?activitySequence_id="+guia.activity_sequence.id+"&activity_id=";
				for (int i=0;i<store.getList().size();i++){
					if (store.getList().get(i).getChecked()==true){
						this.numActivityChecked--;
						if (this.numActivityChecked==0) BaseUrlPage += store.getList().get(i).getId();
						else BaseUrlPage += store.getList().get(i).getId() + ",";
					}
				}
				HttpDelete solicitudBorrar = new HttpDelete(BaseUrlPage);
				SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
				solicitudBorrar.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				System.out.println("Url para borrar actividades: "+BaseUrlPage);
				cliente.execute(solicitudBorrar);	
				
				Looper.prepare();
    				Dialog dialog = create_ok_dialog(getActivity(), "Advertencia", "La actividad ha sido borrada");
    				dialog.show();
    			Looper.loop();

			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
			actualizarListaActividades();
        }        
	}
	
	
	
	
	
	public class ActivitiesSearch extends Thread {	
        String search;
        int page, activities_number;
            
        public ActivitiesSearch(String search, int page, int activities_number) {
        	progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando actividades...");
            this.search = search;
            this.page = page;
            this.activities_number = activities_number;
        }

        @Override 
        public void run() { 
    		try {
    			scroll.setMethod(Scroll.getMethodSearch());
    			scroll.setSearch(search);
    			if (scroll.getPage() == 1) store.deleteList();
    			
    			String url = makeUrlActivities(search, page, activities_number);
      	
    			HttpGet httpGet = new HttpGet(url);
    			SharedPreferences preferences = getActivity().getSharedPreferences( "datos", Context.MODE_PRIVATE);
    	       	httpGet.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
    	       	Log.d("ActivitiesFragment", "url: " + url);
    	      
    	       	//PRUEBA
				HttpResponse httpResponsePrueba = new DefaultHttpClient().execute(httpGet);
				if (httpResponsePrueba.getEntity() != null) System.out.println(EntityUtils.toString(httpResponsePrueba.getEntity()));
				// FIN DE PRUEBA
				
				HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);  	
               	HttpEntity httpEntity = httpResponse.getEntity();
               	              	
               	if (httpEntity != null){
    				List<Integer> listInteger = new Funciones().readJsonIdsStream(httpResponse.getEntity());
    				
    				String urlIds = null;
    				if (activitiesType.equals("userActivities")){
    					// Mirar si algún id devuelto por "search" coincide con alguno id de la secuencia
    					List<Integer> newList = new ArrayList<Integer>();
    					for (int i=0; i<guia.activity_sequence.activitiesIds.size(); i++){
    						for (int j=0; j<listInteger.size(); j++){
    							if (listInteger.get(j) == guia.activity_sequence.activitiesIds.get(i)){
    								newList.add(listInteger.get(j));
    							}
    						}	   					
    					}
    					urlIds = new Funciones().makeUrlIds(newList);
    				}
    				else if (activitiesType.equals("systemActivities")){
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
	
	
	
	
    
	public class MiniActivityListAdapter extends BaseAdapter {

	    private final Context context;
	    private final ArrayList<ActivityMini> list;

	    public MiniActivityListAdapter(Context context, ArrayList<ActivityMini> list) {
	        this.context = context;
	        this.list = list;
	    }

	    @Override
	    public int getCount() {
	        return this.list.size();
	    }

	    @Override
	    public Object getItem(int position) {
	        return this.list.get(position);
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
	        
	        myHolder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (isChecked) list.get(position).setChecked(true);
					else list.get(position).setChecked(false);
				}
			  });
	        if (list.get(position).getChecked() == true) view.setChecked(true);
	        else view.setChecked(false);
	        
	        if (activitiesType.equals("systemActivities")){
	        	if (list.get(position).getCheckedString().equals("Añadida")) view.setCheckboxText("Añadida");
	        	else view.setCheckboxText("Añadir");
	        } else view.setCheckboxText("Eliminar");
	               
	        if(list.get(position).getName().length() != 0) view.setTitle(list.get(position).getName());
	        else view.setTitle(context.getString(R.string.title));
	        
	        if(list.get(position).getImage() != null) view.setImage(ImageHelper.getRoundedCornerBitmap(list.get(position).getImage(), 12, context));
	        else{
	        	Bitmap bm = BitmapFactory.decodeResource(context.getResources(), R.drawable.imagen);
	        	view.setImage(ImageHelper.getRoundedCornerBitmap(bm, 12, context));
	        }
	       
	        if (list.get(position).getShortDescription() != null)
	   	 		if (list.get(position).getShortDescription().length() > 0)
	   	 			view.setDescription(list.get(position).getShortDescription());
	   	 		else view.setDescription(context.getString(R.id.activity_short_description));    	
	   	 	else view.setDescription(context.getString(R.id.activity_short_description));
	       
	        return view;
	    }
	}
}


