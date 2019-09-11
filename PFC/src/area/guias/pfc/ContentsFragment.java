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

public class ContentsFragment extends Fragment{
	private static area.guias.pfc.Activity activity;
	public ContentsAdapter adapter;
	public ContentsStore contentsStore = new ContentsArray();
	private ProgressDialog progressDialog;	
	public Language language;
	private String contentsType;
	private static final String LOGTAG = "LogsAndroid";
	public DownloadContentImages downloadImages;
	private final static int MINIVIEW = 1, SMALLVIEW = 2, WHOLEVIEW = 3;
	
	// SCROLL VARIABLES
	Scroll scroll;
	

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		activity = new area.guias.pfc.Activity();
		activity.id = getActivity().getIntent().getExtras().getInt("activity_id");
		
		language = getActivity().getIntent().getParcelableExtra("language");
		
		adapter = new ContentsAdapter(getActivity(), contentsStore.contentsList());
		GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
		gridView.setAdapter(adapter); 
		
		gridView.setOnItemClickListener(new OnItemClickListener() {	     
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), DeviceView.class);
		    	intent.putExtra("titulo", contentsStore.get(position).getName());	    	
		    	intent.putExtra("imageUrl", contentsStore.get(position).getImageUrl());
		    	intent.putExtra("description", contentsStore.get(position).getDescription());
			    startActivity(intent);
			}
	    });
		
		scroll = new Scroll();
        
        
        if (savedInstanceState != null){
        	scroll = savedInstanceState.getParcelable("scroll");
        	
        	contentsType = savedInstanceState.getString("contentsType");
        	
        	for (int i=0; i< savedInstanceState.getParcelableArrayList("contentsList").size(); i++)
        		contentsStore.contentsList().add((Content) savedInstanceState.getParcelableArrayList("contentsList").get(i));      	
        	
        	if (contentsStore.contentsList().size() > 0) {
        		changeVisibilityOfGridView(View.VISIBLE);
    			changeVisibilityOfEmptyView(View.INVISIBLE);
        	} else {
        		changeVisibilityOfGridView(View.INVISIBLE);
    			changeVisibilityOfEmptyView(View.VISIBLE);
        	}
        	
        	updateContentsList();
        }
        else{
        	scroll = new Scroll();
        	changeVisibilityOfGridView(View.INVISIBLE);
        	changeVisibilityOfEmptyView(View.INVISIBLE);
        	
        	configureListViewScroll(); 
        	
            contentsType = "user_contents"; // or system_contents
            get_contents(contentsType);
        }
	}
	
	
	
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			return inflater.inflate(R.layout.contents_fragment, container);	
	}
	

	
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	   Log.d("NavigationDrawer", "onSaveInstanceState");
	   super.onSaveInstanceState(outState);
	   outState.putParcelable("scroll", scroll);
	   outState.putString("contentsType", contentsType);
	   outState.putParcelableArrayList("contentsList", contentsStore.contentsList());	   
	}
	
	
	
	
	
	public void addContents(){	
		final SelectedContents selectedContents = new SelectedContents();
    	
		for (int i=0; i<contentsStore.contentsList().size();i++){
			
			if ((contentsStore.get(i).checked == true)&&(contentsStore.get(i).checkedString.equals("Añadir"))){
				selectedContents.numContentsToAdd++;
			}
					
			if ((contentsStore.get(i).checkedString.equals("Añadido"))){
				selectedContents.numContentsAdded++;
			}
					
		}
	
		final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity()); 
		dialog.setTitle("Advertencia");
		dialog.setCancelable(false);
		if (selectedContents.numContentsToAdd > 0){		
			if (selectedContents.numContentsToAdd > 1) dialog.setMessage("¿Quieres añadir estos contenidos?");
			else {
				dialog.setMessage("¿Quieres añadir este contenido?");								
			}
			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {   		 
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new AddContents(selectedContents).start();
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
			if (selectedContents.numContentsToAdd == 0) dialog.setMessage("¡No tienes ningún contenido marcado!");
			else if (selectedContents.numContentsAdded ==contentsStore.contentsList().size()) dialog.setMessage("¡Ya tienes todos los contenidos!");
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
			        contentsSearch(scroll.getSearch(), scroll.getPage(), scroll.getPageElements());		            
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
	
	
	
	
	
	
	protected void downloadContentImages(){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {
	        	downloadImages = new DownloadContentImages();
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
				
				System.out.println("contentsStore.contentsList().size()= "+contentsStore.contentsList().size());
				
				if (contentsStore.contentsList().size() == 0) textView.setVisibility(View.VISIBLE);				
				else textView.setVisibility(View.INVISIBLE);
	        }
	    });
	}
	
	
	
	
	
	
	public void getContents(){
		Log.d(LOGTAG, "getContents");
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	        	new GetContents().start();
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
            	intent.putExtra("model", "Content");
            	intent.putExtra("id", contentsStore.get(position).getId());
            	intent.putExtra("title", contentsStore.get(position).getName());
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
    
    
    
    
    public String makeUrlContents(String search, int page, int contents_number){
		String url =  new IP().ip+"/"+language.getStringLanguage()+"/contents/search.json";
		String correct_search = new Funciones().adapt_search_string(search);
		
		
			if (correct_search.equals("") == false){
				url+="?search="+correct_search;
				if (page > 0) url+="&page="+page;        			
				if (contents_number > 0) url+="&contents_number="+contents_number;  
			}
			else{
				if (page > 0){
					url+="?page="+page;        		
					if (contents_number > 0) url+="&contents_number="+contents_number; 
				}
				else if (contents_number > 0) url+="contents_number="+contents_number; 
			}
		  		
		return url;
	}
    
    
    
    
    
    public int numContentsToRemove(){
    	int numContentsToDelete=0;
    	for (int i=0;i<contentsStore.contentsList().size();i++){
			if (contentsStore.get(i).checked == true){
				numContentsToDelete++;
			}
		}
    	return numContentsToDelete;
    }
    
    
    
    
    
    public SelectedContents numSelectedContents(){
		SelectedContents selectedContents = new SelectedContents();
		for (int i=0;i<contentsStore.contentsList().size();i++){
			if (contentsStore.contentsList().get(i).checked == true){
				selectedContents.numContentsChecked++;
			}
		}
		return selectedContents;
	}
    
    
    
    
    
    public int numContentsChecked(){
    	int numContentsChecked=0;
		for (int i=0;i<contentsStore.contentsList().size();i++)
			if (contentsStore.get(i).checked == true) numContentsChecked++;
		return numContentsChecked;
	}
    
    
    
    
    
    public void get_contents(String contents_type){	
    	contentsType = contents_type;
    	Log.d(LOGTAG, "get_contents");
    	if ((downloadImages != null)&&(!downloadImages.isCancelled())) downloadImages.cancel(true);
		contentsStore.deleteList();
		scroll.setTotalCount(scroll.getPageElements());
		scroll.setPage(1);
		if (contents_type.equals("user_contents")){
			progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando contenidos del usuario...");
			new GetActivity().start();
		}
		else if (contents_type.equals("system_contents")){
			progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando contenidos del sistema...");
			new GetContents().start();
		}
		else Log.d(LOGTAG, "Tipo de contenidos incorrecto.");
	}
	
	
	
	
	
    public void reloadOnUiThread(final String contentsType){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	            get_contents(contentsType);
	        }
	    });
	}
    
    
    
    
    
	public void removeContents(final boolean finish){
		// Mirar el numero de contenidos a eliminar
		final SelectedContents selectedContents = numSelectedContents();
		selectedContents.numContentsToRemove = numContentsToRemove();
		selectedContents.numContentsChecked = numContentsChecked();
		
		final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle("Advertencia");
		dialog.setCancelable(false);
		
		if (finish == false){
			if (selectedContents.numContentsChecked == 0){
				dialog.setMessage("Tienes que marcar algún contenido para eliminarlo.");
				dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {   		 
				   @Override
				   public void onClick(DialogInterface dialog, int which) {
					  dialog.cancel();
				   }
				});  	
			}
			else if (selectedContents.numContentsChecked>0){
				
				if (selectedContents.numContentsChecked==1){
					if (selectedContents.numContentsToRemove == 1) {
						if (finish == false) dialog.setMessage("¿Estás seguro de que quieres eliminar este contenido de tu actividad?");
						else dialog.setMessage("¿Quieres eliminar este contenido de tu actividad antes de salir?");
					}
				}
				else {
					if (finish == false) dialog.setMessage("¿Estás seguro de que quieres eliminar estos contenidos de tu actividad?");
					else dialog.setMessage("¿Estás seguro de que quieres eliminar estos contenidos de tu actividad antes de salir?");
				}
				
				dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {   		 
				   @Override
				   public void onClick(DialogInterface dialog, int which) {				   
					   new RemoveContents(selectedContents).start();					   				   
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
        	
		new GetContents().start();
	}
	
	
	
	
	
	public void contentsSearch(String search, int page, int contents_number){
		new Funciones().showToast(getActivity(), "No implementado en el servidor");
		/*if (page == 1) scroll.setPage(1);
		new ContentsSearch(search, page, contents_number).start();*/
	}
    
	
	
	
	
	
	public void updateContentsList(){
		Log.d(LOGTAG, "updateContentsList");
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	            adapter.notifyDataSetChanged();
	        }
	    });
	}
	
	
	

    
    class AddContents extends Thread {
    	SelectedContents selectedContents;
    	
    	public AddContents(SelectedContents selectedContents){
    		this.selectedContents=selectedContents;
    	}
    	
        @Override 
        public void run() {
        	HttpClient cliente = new DefaultHttpClient();
        	String BaseUrlPage = new IP().ip+"/"+language.getStringLanguage() +"/activities/"+activity.id+".json";
        	try {
        		
        		String url = "";
				if (this.selectedContents.numContentsToAdd > 0) url = "?addContent=";
				
				for (int i=0; i<contentsStore.size();i++){
					if ((contentsStore.get(i).checked == true)&&(contentsStore.get(i).checkedString.equals("Añadir"))){	
						selectedContents.numContentsToAdd--;
						if (selectedContents.numContentsToAdd > 0) url+=contentsStore.get(i).contentId+",";
						else url+=contentsStore.get(i).contentId;	
						contentsStore.get(i).checkedString = "Añadido";
					}						
				}
				
				updateContentsList();
				BaseUrlPage+=url;
				HttpPut solicitud = new HttpPut(BaseUrlPage);
				SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
				solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				cliente.execute(solicitud);							
				System.out.println("Url de añadir salidas:");
				System.out.println(BaseUrlPage);
				
				Looper.prepare();
    				Dialog dialog = new Funciones().create_ok_dialog(getActivity(), "Advertencia", "Los contenidos han sido añadidos");
    				dialog.show();
    			Looper.loop();
    		
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	updateContentsList();
        }        
    }
    
    
    
    
    
    class DownloadContentImages extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... activityList) {
        	HttpClient cliente = new DefaultHttpClient();
        	for(int i=0;i<contentsStore.size();i++){
        		if (contentsStore.get(i).getImageUrl() != null){
        			if (contentsStore.get(i).getImageUrl().equals("none")==false){
        				try {
        					String BaseUrlPage = new IP().ip+contentsStore.get(i).getImageUrl();
        					System.out.println("Url imagen salida");
        					System.out.println(BaseUrlPage);
        					HttpGet solicitud = new HttpGet(BaseUrlPage);	
        					SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        					solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
        					if (isCancelled()) break;
        					HttpResponse respuesta = cliente.execute(solicitud);
        					HttpEntity entity = respuesta.getEntity();
        					Bitmap loadedImage = BitmapFactory.decodeStream(entity.getContent());
        					contentsStore.setBitmapImage(contentsStore.get(i).getId(), loadedImage);  
        					//contentsStore.get(i).setImageUri(new Funciones().getImageUriString(getActivity(), loadedImage));
        				} catch (ClientProtocolException e) {
        					e.printStackTrace();
        				} catch (IOException e) {
        					e.printStackTrace();
        				}	
        			}
        		}
        	}
       		updateContentsList();
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

            	// PRUEBA
				HttpResponse respuestaPrueba = cliente.execute(solicitud);
				String json = EntityUtils.toString(respuestaPrueba.getEntity());
				Log.d(LOGTAG, "getWholeView activity:");
				Log.d(LOGTAG, json);
				// PRUEBA
				
				Log.d(LOGTAG, BaseUrlPage);
				HttpResponse respuesta = cliente.execute(solicitud);
			    activity = new Funciones().readJsonActivityStream(respuesta.getEntity()).get(0);			    			    
			    Log.d("ContentsFragment", "size(): " + activity.requirements.get(0).contents.size());
			    getContents();
            } catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
        }        
    }
    
    
    
    
    
    public class GetContents extends Thread {
    	List<Content> contentsList;
    	List<Integer> idsList;
	
	
	
    	@Override 
    	public void run() {
    	
    		try {
    			Log.d(LOGTAG, "GetContents");
    			HttpClient cliente = new DefaultHttpClient(); 
        
    			String url_contents = null;
    			if (contentsType.equals("user_contents")){
    				
    				url_contents =  new IP().ip+"/"+language.getStringLanguage()+"/contents/getWholeView.json?id=";
    				Log.d("ContentsFragment", "contents.size(): " + activity.requirements.get(0).contents.size());
    				for (int i=0; i<activity.requirements.get(0).contents.size(); i++){
    					url_contents = url_contents + activity.requirements.get(0).contents.get(i).getId();
    					if (i<activity.requirements.get(0).contents.size()-1) url_contents = url_contents+",";        		
    				}
    				    				
    			}
    			else if (contentsType.equals("system_contents")){
    				
    				url_contents =  new IP().ip+"/"+language.getStringLanguage()+"/contents.json";
    				HttpGet solicitud = new HttpGet(url_contents);
    				SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
    				solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
    				HttpResponse respuesta = cliente.execute(solicitud);
    				HttpEntity entity = respuesta.getEntity();
    				idsList = new Funciones().readJsonIdsStream(entity);
		        
    				// PRUEBA		        
    				/*respuesta = cliente.execute(solicitud);
    				String json = EntityUtils.toString(respuesta.getEntity());
    				System.out.println("JSON WHOLE VIEW CONTENTS IDS: "+json);*/
    				// FIN DE PRUEBA
				
    				url_contents =  new IP().ip+"/"+language.getStringLanguage()+"/contents/getWholeView.json?id=";
    				for (int i=0; i<idsList.size(); i++){
    					url_contents +=  idsList.get(i);
    					if (i<idsList.size()-1) url_contents += ",";        		
    				}    				    						
    				
    			}
    			
    			// TRIPS  			
    			System.out.println("Url contents: "+url_contents);
    			HttpGet solicitud = new HttpGet(url_contents);
    			SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
    			solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
	        
    			// PRUEBA		        
    			/*HttpResponse respuesta = cliente.execute(solicitud);
    			String json = EntityUtils.toString(respuesta.getEntity());
    			System.out.println("JSON WHOLE VIEW CONTENT: "+json);*/
    			// FIN DE PRUEBA
			
    			HttpResponse respuesta = cliente.execute(solicitud);
    			HttpEntity entity = respuesta.getEntity();
    			
    			contentsList = new ArrayList<Content>();
    			contentsList = new Funciones().readJsonContentStream(entity);				
    			// Guardar contenidos de esta actividad
    			for (int i=0;i<contentsList.size();i++) contentsStore.saveContent(contentsList.get(i));
    			
    			if (contentsType.equals("system_contents")){
    				// Mirar que eventos ya tenemos añadidos
        			for (int i=0; i<activity.requirements.get(0).contents.size(); i++){
        				for (int j=0; j<idsList.size(); j++){
        					if (contentsStore.get(j).getId() == activity.requirements.get(0).contents.get(i).getId()) 
        						contentsStore.get(j).checkedString = "Añadido";
        				}
        			}
    			}
    			
			    // IMAGES
    			if (contentsStore.size()>0) downloadContentImages();				
			
    			finProgressBar();
    			updateContentsList();
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
        	String url = new IP().ip + "/" + this.language + "/contents/" + typeOfView + ".json?" + this.urlIds; 
        	HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
			try {
				HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
				
				List<Content> contentsList = new Funciones().readJsonContentStream(httpResponse.getEntity());
				
				for (int i=0;i<contentsList.size();i++) contentsStore.saveContent(contentsList.get(i));
					
				updateContentsList();
				downloadContentImages();				
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
    
    
    
    
    
    public class SelectedContents{
    	
    	int numContentsChecked;
    	int numContentsToAdd;
    	int numContentsAdded;
    	int numContentsToRemove;
    	
    	public SelectedContents(){
    		this.numContentsChecked=0;  		
    		this.numContentsToAdd=0;
    		this.numContentsAdded=0;
    		this.numContentsToRemove=0;   				
    	}
		
    }





    public class ContentsAdapter extends BaseAdapter {
    	private final Activity actividad;
    	private final ArrayList<Content> lista;
    
   
    
    	public ContentsAdapter(Activity actividad, ArrayList<Content> contents) {
    		super();
    		this.actividad = actividad;
    		this.lista = contents;
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
    		if (contentsType.equals("user_contents")) view.setCheckboxText("Eliminar");
    		else view.setCheckboxText("Añadir");
    		if (contentsType.equals("system_contents"))
    			if (lista.get(position).checkedString.equals("Añadido")){
    				view.setChecked(true);
    				view.setCheckboxText("Añadido");
    			}
    		return view;
    	}
	
    }





    public class ContentsSearch extends Thread {	
        String search;
        int page, contents_number;
            
        public ContentsSearch(String search, int page, int contents_number) {
        	progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando contenidos...");
            this.search = search;
            this.page = page;
            this.contents_number = contents_number;
        }

        @Override 
        public void run() {   
    		try {
    			if (scroll.getPage() == 1) contentsStore.deleteList();
    			scroll.setMethod(Scroll.METHOD_SEARCH);
    			scroll.setSearch(search);
    			
    	       	String Url = makeUrlContents(search, page, contents_number);
    				
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
    				if (contentsType.equals("user_contents")){
    					// Mirar si algún id devuelto por "search" coincide con alguno id del contexto técnico
    					List<Integer> newList = new ArrayList<Integer>();
    					for (int i=0; i<activity.requirements.get(0).contents.size(); i++){
    						for (int j=0; j<listInteger.size(); j++){
    							if (listInteger.get(j) == activity.requirements.get(0).contents.get(i).getId()){
    								newList.add(listInteger.get(j));
    							}
    						}	   					
    					}
    					urlIds = new Funciones().makeUrlIds(newList);
    				}
    				else if (contentsType.equals("system_contents")){
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
    
    
    
    
    
    class RemoveContents extends Thread {
        SelectedContents selectedContents;
        
		public RemoveContents(SelectedContents selectedContents){
			this.selectedContents=selectedContents;
		}
		
        @Override 
        public void run() {        	
			try {				
				HttpClient cliente = new DefaultHttpClient();
				String Url = new IP().ip+"/"+language.getStringLanguage()+"/activities/"+activity.id+".json?";
				String urlContents="";
				if (this.selectedContents.numContentsToRemove > 0){					
					urlContents = "remContent=";
				}				
				
				for (int i=0;i<contentsStore.contentsList().size();i++){
					
					if (contentsStore.contentsList().get(i).checked==true){
						this.selectedContents.numContentsChecked--;
						this.selectedContents.numContentsToRemove--;
						if (this.selectedContents.numContentsToRemove==0) urlContents+=contentsStore.contentsList().get(i).contentId;
						else urlContents+=contentsStore.contentsList().get(i).contentId+",";						
					}
					
				}
				
				Url+=urlContents;
				HttpPut solicitudBorrar = new HttpPut(Url);
				SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
				solicitudBorrar.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				System.out.println("Url para borrar contenidos: "+Url);
				cliente.execute(solicitudBorrar);	
				
				Looper.prepare();
    				Dialog dialog = create_ok_dialog(getActivity(), "Advertencia", "Los contenidos han sido añadidos");
    				dialog.show();
    			Looper.loop();
    		
				reloadOnUiThread("user_contents");
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


