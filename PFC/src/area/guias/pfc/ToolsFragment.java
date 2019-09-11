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

public class ToolsFragment extends Fragment{
	private static area.guias.pfc.Activity activity;
	public ToolsAdapter adapter;
	public ToolsStore toolsStore = new ToolsArray();
	private ProgressDialog progressDialog;	
	public Language language;
	private String toolsType;
	private static final String LOGTAG = "LogsAndroid";
	public DownloadToolImages downloadImages;
	private final static int MINIVIEW = 1, SMALLVIEW = 2, WHOLEVIEW = 3;
	
	// SCROLL VARIABLES
	Scroll scroll;
	
	
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		activity = new area.guias.pfc.Activity();
		activity.id = getActivity().getIntent().getExtras().getInt("activity_id");
		
		language = getActivity().getIntent().getParcelableExtra("language");
		
		adapter = new ToolsAdapter(getActivity(), toolsStore.toolsList());
		GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
		gridView.setAdapter(adapter); 
		
		gridView.setOnItemClickListener(new OnItemClickListener() {	     
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), DeviceView.class);
		    	intent.putExtra("titulo", toolsStore.get(position).getName());	    	
		    	intent.putExtra("imageUrl", toolsStore.get(position).getImageUrl());
		    	intent.putExtra("description", toolsStore.get(position).getDescription());
			    startActivity(intent);
			}
	    });

		scroll = new Scroll();
		
        if (savedInstanceState != null){
        	scroll = savedInstanceState.getParcelable("scroll");
        	
        	toolsType = savedInstanceState.getString("toolsType");
        	
        	for (int i=0; i< savedInstanceState.getParcelableArrayList("toolsList").size(); i++)
        		toolsStore.toolsList().add((Tool) savedInstanceState.getParcelableArrayList("toolsList").get(i));      	
        	
        	if (toolsStore.toolsList().size() > 0) {
        		changeVisibilityOfGridView(View.VISIBLE);
    			changeVisibilityOfEmptyView(View.INVISIBLE);
        	} else {
        		changeVisibilityOfGridView(View.INVISIBLE);
    			changeVisibilityOfEmptyView(View.VISIBLE);
        	}
        	
        	updateToolsList();
        }
        else{
        	scroll = new Scroll();
        	changeVisibilityOfGridView(View.INVISIBLE);
        	changeVisibilityOfEmptyView(View.INVISIBLE);
        	        	
            configureListViewScroll(); 
            
        	toolsType = "user_tools";
        	
        	get_tools(toolsType);       	
        }
	}
	
	

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			return inflater.inflate(R.layout.tools_fragment, container);	
	}
	

	
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	   Log.d("NavigationDrawer", "onSaveInstanceState");
	   super.onSaveInstanceState(outState);
	   outState.putParcelable("scroll", scroll);
	   outState.putString("toolsType", toolsType);
	   outState.putParcelableArrayList("toolsList", toolsStore.toolsList());	   
	}
	
	
	
	
	
	public void addTools(){	
		final SelectedTools selectedTools = new SelectedTools();
    	
		for (int i=0; i<toolsStore.toolsList().size();i++){
			
			if ((toolsStore.get(i).checked == true)&&(toolsStore.get(i).checkedString.equals("Añadir"))){
				selectedTools.numToolsToAdd++;
				if (toolsStore.get(i).type.equals("Device")) selectedTools.numDevicesToAdd++;
				else if (toolsStore.get(i).type.equals("Application")) selectedTools.numAppsToAdd++;
			}
					
			if ((toolsStore.get(i).checkedString.equals("Añadido"))){
				selectedTools.numToolsAdded++;
				if (toolsStore.get(i).type.equals("Device")) selectedTools.numDevicesAdded++;
			    else if (toolsStore.get(i).type.equals("Application")) selectedTools.numAppsAdded++;
			}
					
		}
	
		final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity()); 
		dialog.setTitle("Advertencia");
		dialog.setCancelable(false);
		if (selectedTools.numToolsToAdd > 0){		
			if (selectedTools.numToolsToAdd > 1) dialog.setMessage("¿Quieres añadir estas herramientas?");
			else {
				dialog.setMessage("¿Quieres añadir esta herramienta?");								
			}
			dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {   		 
				@Override
				public void onClick(DialogInterface dialog, int which) {
					new AddTools(selectedTools).start();
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
			if (selectedTools.numToolsToAdd == 0) dialog.setMessage("¡No tienes ninguna herramienta marcada!");
			else if (selectedTools.numToolsAdded ==toolsStore.toolsList().size()) dialog.setMessage("¡Ya tienes todas las herramientas!");
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
			        
			        toolsSearch(scroll.getSearch(), scroll.getPage(), scroll.getPageElements());		            
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
	
	
	
	
	
	protected void downloadToolImages(){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {
	        	downloadImages = new DownloadToolImages();
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
				
				System.out.println("toolsStore.toolsList().size= "+toolsStore.toolsList().size());
				
				if (toolsStore.toolsList().size() == 0) textView.setVisibility(View.VISIBLE);				
				else textView.setVisibility(View.INVISIBLE);
	        }
	    });
	}
	
	
	
	
	
	
	public void getTools(){
		Log.d(LOGTAG, "getTools");
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	        	new GetTools().start();
	        }
	    });		
	}

	
	
	
	public void getTypeOfView(String language, String urlIds, int typeOfView, String typeOfTools){
		new GetTypeOfView(language, urlIds, typeOfView, typeOfTools).start();
	}
	
	
	
	
	
	private OnClickListener mCommentButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
        	final int position = gridView.getPositionForView(v);
            if (position != AdapterView.INVALID_POSITION) {
            	Intent intent = new Intent(getActivity(), CommentsView.class);
            	intent.putExtra("model", toolsStore.get(position).getType());
            	intent.putExtra("id", toolsStore.get(position).getId());
            	intent.putExtra("title", toolsStore.get(position).getName());
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
    
    
    
    
    
    public String makeUrlApplications(String search, int page, int applications_number){
		String url =  new IP().ip+"/"+language.getStringLanguage()+"/applications/search.json";
		String correct_search = new Funciones().adapt_search_string(search);
		
		
			if (correct_search.equals("") == false){
				url+="?search="+correct_search;
				if (page > 0) url+="&page="+page;        			
				if (applications_number > 0) url+="&applications_number="+applications_number;  
			}
			else{
				if (page > 0){
					url+="?page="+page;        		
					if (applications_number > 0) url+="&applications_number="+applications_number; 
				}
				else if (applications_number > 0) url+="applications_number="+applications_number; 
			}
		 		
		return url;
	}
    
    
    
    
    
    public String makeUrlDevices(String search, int page, int devices_number){
		String url =  new IP().ip+"/"+language.getStringLanguage()+"/devices/search.json";
		String correct_search = new Funciones().adapt_search_string(search);
		
		
			if (correct_search.equals("") == false){
				url+="?search="+correct_search;
				if (page > 0) url+="&page="+page;        			
				if (devices_number > 0) url+="&devices_number="+devices_number;  
			}
			else{
				if (page > 0){
					url+="?page="+page;        		
					if (devices_number > 0) url+="&devices_number="+devices_number; 
				}
				else if (devices_number > 0) url+="devices_number="+devices_number; 
			}
				
		return url;
	}
    
    
    
    
    
    public int numAppsToRemove(){
    	int numAppsToDelete=0;
    	for (int i=0;i<toolsStore.toolsList().size();i++){
			if (toolsStore.get(i).checked == true){
				if (toolsStore.get(i).type.equals("Application")) numAppsToDelete++;
			}
		}
    	return numAppsToDelete;
    }
    
    
    
    
    
    public int numDevicesToRemove(){
    	int numDevicesToDelete=0;
    	for (int i=0;i<toolsStore.toolsList().size();i++){
			if (toolsStore.get(i).checked == true){
				if (toolsStore.get(i).type.equals("Device")) numDevicesToDelete++;
			}
		}
    	return numDevicesToDelete;
    }
    
    
    
    
    
    public SelectedTools numSelectedTools(){
		SelectedTools selectedTools = new SelectedTools();
		for (int i=0;i<toolsStore.toolsList().size();i++){
			if (toolsStore.toolsList().get(i).checked == true){
				if (toolsStore.toolsList().get(i).type.equals("device")) selectedTools.numDevicesChecked++;
				else if (toolsStore.toolsList().get(i).type.equals("application")) selectedTools.numAppsChecked++;
				selectedTools.numToolsChecked++;
			}
		}
		return selectedTools;
	}
    
    
    
    
    
    public int numToolsChecked(){
    	int numToolsChecked=0;
		for (int i=0;i<toolsStore.toolsList().size();i++)
			if (toolsStore.get(i).checked == true) numToolsChecked++;
		return numToolsChecked;
	}
    
    
    
    
    
    public void get_tools(String tools_type){	
    	toolsType = tools_type;
    	Log.d(LOGTAG, "get_tools");
    	if ((downloadImages != null)&&(!downloadImages.isCancelled())) downloadImages.cancel(true);
		toolsStore.deleteList();
		scroll.setTotalCount(scroll.getPageElements());
		scroll.setPage(1);
		if (tools_type.equals("user_tools")){
			progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando herramientas del usuario...");
			new GetActivity().start();
		}
		else if (tools_type.equals("system_tools")){
			progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando herramientas del sistema...");
			new GetTools().start();
		}
		else Log.d(LOGTAG, "Tipo de herramientas incorrecto.");
	}
	
	
	
	
	
    public void reloadOnUiThread(final String toolsType){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	            get_tools(toolsType);
	        }
	    });
	}
    
    
    
    
    
	public void removeTools(final boolean finish){
		// Mirar el numero de dispositivos a eliminar
		final SelectedTools selectedTools = numSelectedTools();
		selectedTools.numDevicesToRemove = numDevicesToRemove();
		selectedTools.numAppsToRemove = numAppsToRemove();
		selectedTools.numToolsChecked = numToolsChecked();
		
		final AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
		dialog.setTitle("Advertencia");
		dialog.setCancelable(false);
		
		if (finish == false){
			if (selectedTools.numToolsChecked == 0){
				dialog.setMessage("Tienes que marcar alguna herramienta para eliminarla.");
				dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {   		 
				   @Override
				   public void onClick(DialogInterface dialog, int which) {
					  dialog.cancel();
				   }
				});  	
			}
			else if (selectedTools.numToolsChecked>0){
				
				if (selectedTools.numToolsChecked==1){
					if (selectedTools.numDevicesToRemove == 1) {
						if (finish == false) dialog.setMessage("¿Estás seguro de que quieres eliminar este dispositivo de tu actividad?");
						else dialog.setMessage("¿Quieres eliminar este dispositivo de tu actividad antes de salir?");
					}
					if (selectedTools.numAppsToRemove == 1){
						if (finish == false) dialog.setMessage("¿Estás seguro de que quieres eliminar esta aplicación de tu actividad?");
						else dialog.setMessage("¿Quieres eliminar esta aplicación de tu actividad antes de salir?");
					}
				}
				else {
					if (finish == false) dialog.setMessage("¿Estás seguro de que quieres eliminar estas herramientas de tu actividad?");
					else dialog.setMessage("¿Estás seguro de que quieres eliminar estas herramientas de tu actividad antes de salir?");
				}
				
				dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {   		 
				   @Override
				   public void onClick(DialogInterface dialog, int which) {				   
					   new RemoveTools(selectedTools).start();					   				   
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
        	
		get_tools(toolsType);
	}
	
	
	
	
	
	public void toolsSearch(String search, int page, int tools_number){
		new Funciones().showToast(getActivity(), "No implementado en el servidor");
		/*if (page == 1) scroll.setPage(1);
		new ToolsSearch(search, page, tools_number).start();*/
	}
    
	
	
	
	
	
	public void updateToolsList(){
		Log.d(LOGTAG, "updateToolsList");
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	            adapter.notifyDataSetChanged();
	        }
	    });
	}
	
	
	

    
    class AddTools extends Thread {
    	SelectedTools selectedTools;
    	
    	public AddTools(SelectedTools selectedTools){
    		this.selectedTools=selectedTools;
    	}
    	
        @Override 
        public void run() {
        	HttpClient cliente = new DefaultHttpClient();
        	String BaseUrlPage = new IP().ip+"/"+language.getStringLanguage() +"/activities/"+activity.id+".json";
        	try {
        		
        		String urlDevices = ""; String urlApplications="";
				if (this.selectedTools.numDevicesToAdd > 0) urlDevices = "?addDevice=";
				if ((this.selectedTools.numAppsToAdd > 0)&&(this.selectedTools.numDevicesToAdd>0)) urlApplications = "&addApplication=";
				if ((this.selectedTools.numAppsToAdd > 0)&&(this.selectedTools.numDevicesToAdd==0)) urlApplications = "?addApplication=";
				
				for (int i=0; i<toolsStore.size();i++){
					if ((toolsStore.get(i).checked == true)&&(toolsStore.get(i).checkedString.equals("Añadir"))){	
						selectedTools.numToolsToAdd--;
						if (toolsStore.get(i).type.equals("Device")){
							selectedTools.numDevicesToAdd--;
							if (selectedTools.numDevicesToAdd > 0) urlDevices+=toolsStore.get(i).toolId+",";
							else urlDevices+=toolsStore.get(i).toolId;
							toolsStore.get(i).checkedString = "Añadido";
						}
						else{
							selectedTools.numAppsToAdd--;
							if (selectedTools.numAppsToAdd > 0) urlApplications+=toolsStore.get(i).toolId + ",";
							else urlApplications+=toolsStore.get(i).toolId;		
							toolsStore.get(i).checkedString = "Añadido";
						}
					}						
				}
				
				updateToolsList();
				BaseUrlPage+=urlDevices+urlApplications;
				HttpPut solicitud = new HttpPut(BaseUrlPage);
				SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
				solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				cliente.execute(solicitud);							
				System.out.println("Url de añadir herramientas:");
				System.out.println(BaseUrlPage);
				
				Looper.prepare();
    				Dialog dialog = new Funciones().create_ok_dialog(getActivity(), "Advertencia", "Las herramientas han sido añadidas");
    				dialog.show();
    			Looper.loop();
    		
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
        	updateToolsList();
        }        
    }
    
    
    
    
    
    class DownloadToolImages extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... toolsList) {
        	HttpClient cliente = new DefaultHttpClient();
        	for(int i=0;i<toolsStore.size();i++){
        		if (toolsStore.get(i).getImageUrl() != null){
        			if (toolsStore.get(i).getImageUrl().equals("none")==false){
        				try {
        					String BaseUrlPage = new IP().ip+toolsStore.get(i).getImageUrl();
        					System.out.println("Url imagen herramienta");
        					System.out.println(BaseUrlPage);
        					HttpGet solicitud = new HttpGet(BaseUrlPage);	
        					SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        					solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
        					if (isCancelled()) break;
        					HttpResponse respuesta = cliente.execute(solicitud);
        					HttpEntity entity = respuesta.getEntity();
        					Bitmap loadedImage = BitmapFactory.decodeStream(entity.getContent());
        					toolsStore.setBitmapImage(toolsStore.get(i).getId(), loadedImage);  
        				} catch (ClientProtocolException e) {
        					e.printStackTrace();
        				} catch (IOException e) {
        					e.printStackTrace();
        				}	
        			}
        		}
        	}
       		updateToolsList();
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
			    
			    if (activity.getRequirements().get(0).getTools().get(0).getDeviceIds().size() > 0)
			    	System.out.println("Dispositivos de la activity: "+ activity.getRequirements().get(0).getTools().get(0).getDeviceIds().get(0));
			    
			    if (activity.getRequirements().get(0).getTools().get(0).getApplicationIds().size() > 0)
			    	System.out.println("Aplicaciones de la activity: "+ activity.getRequirements().get(0).getTools().get(0).getApplicationIds().get(0));
			    
			    getTools();
            } catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
        }        
    }
    
    
    
    
    
    public class GetTools extends Thread {
    	List<Tool> devicesList, appsList;
    	List<Integer> idsDevicesList, idsApplicationsList;
	
	
	
    	@Override 
    	public void run() {
    	
    		try {
    			Log.d(LOGTAG, "GetTools");
    			HttpClient cliente = new DefaultHttpClient(); 
        	
    			// Url devices
    			String url_devices = null;
    			String url_applications = null;
    			if (toolsType.equals("user_tools")){
    				
    				url_devices =  new IP().ip+"/"+language.getStringLanguage()+"/devices/getWholeView.json?id=";
    				for (int i=0; i<activity.requirements.get(0).tools.get(0).deviceIds.size(); i++){
    					url_devices = url_devices + activity.requirements.get(0).tools.get(0).deviceIds.get(i);
    					if (i<activity.requirements.get(0).tools.get(0).deviceIds.size()-1) url_devices = url_devices+",";        		
    				}
    				
    				url_applications =  new IP().ip+"/"+language.getStringLanguage()+"/applications/getWholeView.json?id=";
    				for (int i=0; i<activity.requirements.get(0).tools.get(0).applicationIds.size(); i++){
    					url_applications += activity.requirements.get(0).tools.get(0).applicationIds.get(i);
    					if (i<activity.requirements.get(0).tools.get(0).applicationIds.size()-1) url_applications += ",";        		
    				}
    			}
    			else if (toolsType.equals("system_tools")){
    				
    				url_devices =  new IP().ip+"/"+language.getStringLanguage()+"/devices.json";
    				HttpGet solicitud = new HttpGet(url_devices);
    				SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
    				solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
    				HttpResponse respuesta = cliente.execute(solicitud);
    				HttpEntity entity = respuesta.getEntity();
    				idsDevicesList = new Funciones().readJsonIdsStream(entity);
				
    				url_devices =  new IP().ip+"/"+language.getStringLanguage()+"/devices/getWholeView.json?id=";
    				for (int i=0; i<idsDevicesList.size(); i++){
    					url_devices +=  idsDevicesList.get(i);
    					if (i<idsDevicesList.size()-1) url_devices += ",";        		
    				}
    						
    				url_applications =  new IP().ip+"/"+language.getStringLanguage()+"/applications.json";
    				solicitud = new HttpGet(url_applications);
    				solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
    				respuesta = cliente.execute(solicitud);
    				entity = respuesta.getEntity();
    				idsApplicationsList = new Funciones().readJsonIdsStream(entity);
    				
    				url_applications =  new IP().ip+"/"+language.getStringLanguage()+"/applications/getWholeView.json?id=";
    				for (int i=0; i<idsApplicationsList.size(); i++){
    					url_applications +=  idsApplicationsList.get(i);
    					if (i<idsApplicationsList.size()-1) url_applications += ",";        		
    				}
    				
    			}
    			
    			// DEVICES   			
    			System.out.println("Url tools (devices): "+url_devices);
    			HttpGet solicitud = new HttpGet(url_devices);
    			SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
    			solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
	        
    			// PRUEBA		        
    			/*HttpResponse respuesta = cliente.execute(solicitud);
    			String json = EntityUtils.toString(respuesta.getEntity());
    			System.out.println("JSON WHOLE VIEW DEVICE TOOL: "+json);*/
    			// FIN DE PRUEBA
			
    			HttpResponse respuesta = cliente.execute(solicitud);
    			HttpEntity entity = respuesta.getEntity();
    			
    			devicesList = new ArrayList<Tool>();
    			devicesList = new Funciones().readJsonToolStream(entity, "Device");				
    			// Guardar herramientas de esta guía
    			for (int i=0;i<devicesList.size();i++) toolsStore.saveTool(devicesList.get(i));
    			
			
    			 			
        		// APPLICATIONS	        
    			System.out.println("Url tools (applications): " + url_applications);
    			solicitud = new HttpGet(url_applications);
    			solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
    			
    			// PRUEBA
    			/*cliente = new DefaultHttpClient(); 
    			respuesta = cliente.execute(solicitud);
    			json = EntityUtils.toString(respuesta.getEntity());
    			System.out.println("JSON WHOLE VIEW APP TOOL: "+json);*/
    			// FIN DE PRUEBA
			
    			respuesta = cliente.execute(solicitud);
    			entity = respuesta.getEntity();
    			appsList = new ArrayList<Tool>();
    			appsList = new Funciones().readJsonToolStream(entity, "Application");    			
    			
    			// Guardar herramientas de esta guía
    			for (int i=0;i<appsList.size();i++) toolsStore.saveTool(appsList.get(i));
    			
    			if (toolsType.equals("system_tools")){
    				for (int i=0; i<activity.requirements.get(0).tools.get(0).deviceIds.size(); i++){
        				for (int j=0; j<toolsStore.size(); j++){
        					if (toolsStore.get(j).getType().equals("Device"))
        						if (toolsStore.get(j).getId() == activity.requirements.get(0).tools.get(0).deviceIds.get(i)) 
        							toolsStore.get(j).checkedString = "Añadido";
        				}
    				}
    				for (int i=0; i<activity.requirements.get(0).tools.get(0).applicationIds.size(); i++){
    					for (int j=0; j<toolsStore.size(); j++){
    						if (toolsStore.get(j).getType().equals("Application"))
    							if (toolsStore.get(j).getId() == activity.requirements.get(0).tools.get(0).applicationIds.get(i)) 
    								toolsStore.get(j).checkedString = "Añadido";
        				}
        			}
    			}
    			
			    // IMAGES
    			if (toolsStore.size()>0) downloadToolImages();				
			
    			finProgressBar();
    			updateToolsList();
    		} catch (ClientProtocolException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}				
    	}        
    }




    
    public class GetTypeOfView extends Thread {  	   	
    	String language, urlIds, typeOfView, typeOfTools;
		
    	public GetTypeOfView(String language, String urlIds, int TypeOfView, String typeOfTools){
    		this.language = language;
    		this.urlIds = urlIds;
    		if (TypeOfView == MINIVIEW) typeOfView = "getMiniView";
    		else if (TypeOfView == SMALLVIEW) typeOfView = "getSmallView";
    		else typeOfView = "getWholeView";
    		this.typeOfTools = typeOfTools;
    	}
    	   	
        @Override 
        public void run() {	
        	System.out.println("GetTypeOfView");
        	SharedPreferences preferences = getActivity().getSharedPreferences( "datos", Context.MODE_PRIVATE);  
        	
        	if (typeOfTools.equals("devices")){
        		String url = new IP().ip + "/" + this.language + "/devices/" + typeOfView + ".json?" + this.urlIds; 
        		HttpGet httpGet = new HttpGet(url);
        		httpGet.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
        		try {
        			HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
				
        			List<Tool> deviceList = new Funciones().readJsonToolStream(httpResponse.getEntity(), "Device");
				
        			for (int i=0;i<deviceList.size();i++) toolsStore.saveTool(deviceList.get(i));
							
        			updateToolsList();
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
        	else if (typeOfTools.equals("applications")){								
				String url = new IP().ip + "/" + this.language + "/applications/" + typeOfView + ".json?" + this.urlIds; 
	        	HttpGet httpGet = new HttpGet(url);
				httpGet.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				try {
					HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
					
					List<Tool> appList = new Funciones().readJsonToolStream(httpResponse.getEntity(), "Application");
					
					for (int i=0;i<appList.size();i++) toolsStore.saveTool(appList.get(i));
							
					updateToolsList();
					downloadToolImages();				
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
    }
    
    
    
    
    
    public class SelectedTools{
    	
    	int numDevicesChecked, numAppsChecked, numToolsChecked;
    	int numToolsToAdd, numDevicesToAdd ,numAppsToAdd;
    	int numDevicesAdded, numAppsAdded, numToolsAdded;
    	int numToolsToRemove, numDevicesToRemove, numAppsToRemove;
    	
    	public SelectedTools(){
    		this.numDevicesChecked=0;
    		this.numAppsChecked=0;
    		this.numToolsChecked=0;
    		
    		this.numToolsToAdd=0;
    		this.numDevicesToAdd=0;
    		this.numAppsToAdd=0;
    		
    		this.numToolsAdded=0;
    		this.numAppsAdded=0;  
    		this.numDevicesAdded=0;
    		
    		this.numToolsToRemove=0;
    		this.numDevicesToRemove=0;
    		this.numAppsToRemove=0;    				
    	}
		
    }





    public class ToolsAdapter extends BaseAdapter {
    	private final Activity actividad;
    	private final ArrayList<Tool> lista;
    
   
    
    	public ToolsAdapter(Activity actividad, ArrayList<Tool> tools) {
    		super();
    		this.actividad = actividad;
    		this.lista = tools;
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
    		if (toolsType.equals("user_tools")) view.setCheckboxText("Eliminar");
    		else view.setCheckboxText("Añadir");
    		if (toolsType.equals("system_tools"))
    			if (lista.get(position).checkedString.equals("Añadido")){
    				view.setChecked(true);
    				view.setCheckboxText("Añadido");
    			}
    		return view;
    	}
	
    }





    public class ToolsSearch extends Thread {	
        String search;
        int page, tools_number;
            
        public ToolsSearch(String search, int page, int tools_number) {
        	progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando herramientas...");
            this.search = search;
            this.page = page;
            this.tools_number = tools_number;
        }

        @Override 
        public void run() {   
        	
    		try {
    			System.out.println("SearchTools"); 
    			if (scroll.getPage() == 1) toolsStore.deleteList();
    			scroll.setMethod(Scroll.METHOD_SEARCH);
    			scroll.setSearch(search);
    			
    			////////////////////////////////////////////
    			///// DEVICES /////
    			////////////////////////////////////////////
    			
    	       	String url_search_devices = makeUrlDevices(search, page, tools_number);
    	       	
    	       	HttpGet httpGet = new HttpGet(url_search_devices);
    			SharedPreferences preferences = getActivity().getSharedPreferences( "datos", Context.MODE_PRIVATE);
    	       	httpGet.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
    	       	Log.d("", "url: " + url_search_devices);
    	      
    	       	//PRUEBA
				HttpResponse httpResponsePrueba = new DefaultHttpClient().execute(httpGet);
				if (httpResponsePrueba.getEntity() != null) System.out.println(EntityUtils.toString(httpResponsePrueba.getEntity()));
				// FIN DE PRUEBA
				
    			HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);   			
    			HttpEntity httpEntity = httpResponse.getEntity();
    			
    			if (httpEntity != null){
    				List<Integer> listInteger = new Funciones().readJsonIdsStream(httpResponse.getEntity());
    				
    				String urlIds = null;
    				if (toolsType.equals("user_tools")){
    					// Mirar si algún id devuelto por "search" coincide con alguno id del contexto técnico
    					List<Integer> newList = new ArrayList<Integer>();
    					for (int i=0; i<activity.requirements.get(0).tools.get(0).deviceIds.size(); i++){
    						for (int j=0; j<listInteger.size(); j++){
    							if (listInteger.get(j) == activity.requirements.get(0).tools.get(0).deviceIds.get(i)){
    								newList.add(listInteger.get(j));
    							}
    						}	   					
    					}
    					urlIds = new Funciones().makeUrlIds(newList);
    				}
    				else if (toolsType.equals("system_tools")){
    					urlIds = new Funciones().makeUrlIds(listInteger);
    				}
    				     				
    				getTypeOfView(language.getStringLanguage(), urlIds, WHOLEVIEW, "devices");	
    			}
    			
    			
    			/////////////////////////////////
    			///// APPLICATIONS /////
    			/////////////////////////////////
    			
    			String url_search_applications = makeUrlApplications(search, page, tools_number);
    	       	
    	       	httpGet = new HttpGet(url_search_applications);
    			preferences = getActivity().getSharedPreferences( "datos", Context.MODE_PRIVATE);
    	       	httpGet.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
    	       	Log.d("", "url: " + url_search_applications);
    	      
    	       	//PRUEBA
				httpResponsePrueba = new DefaultHttpClient().execute(httpGet);
				if (httpResponsePrueba.getEntity() != null) System.out.println(EntityUtils.toString(httpResponsePrueba.getEntity()));
				// FIN DE PRUEBA
				
    			httpResponse = new DefaultHttpClient().execute(httpGet);   			
    			httpEntity = httpResponse.getEntity();
    			
    			if (httpEntity != null){
    				List<Integer> listInteger = new Funciones().readJsonIdsStream(httpResponse.getEntity());
    				
    				String urlIds = null;
    				if (toolsType.equals("user_tools")){
    					// Mirar si algún id devuelto por "search" coincide con alguno id del contexto técnico
    					List<Integer> newList = new ArrayList<Integer>();
    					for (int i=0; i<activity.requirements.get(0).tools.get(0).applicationIds.size(); i++){
    						for (int j=0; j<listInteger.size(); j++){
    							if (listInteger.get(j) == activity.requirements.get(0).tools.get(0).applicationIds.get(i)){
    								newList.add(listInteger.get(j));
    							}
    						}	   					
    					}
    					urlIds = new Funciones().makeUrlIds(newList);
    				}
    				else if (toolsType.equals("system_tools")){
    					urlIds = new Funciones().makeUrlIds(listInteger);
    				}
    				     				
    				getTypeOfView(language.getStringLanguage(), urlIds, WHOLEVIEW, "applications");	
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
    
    
    
    
    
    class RemoveTools extends Thread {
        SelectedTools selectedTools;
        
		public RemoveTools(SelectedTools selectedTools){
			this.selectedTools=selectedTools;
		}
		
        @Override 
        public void run() {        	
			try {				
				HttpClient cliente = new DefaultHttpClient();
				String Url = new IP().ip+"/"+language.getStringLanguage()+"/activities/"+activity.id+".json?";
				String urlDevices="", urlApplications="";
				if (this.selectedTools.numDevicesToRemove > 0){					
					urlDevices = "remDevice=";
					if (this.selectedTools.numAppsToRemove > 0) urlApplications = "&remApplication=";
				}
				else if (this.selectedTools.numAppsToRemove > 0){
					urlApplications = "remApplication=";
				}
				
				for (int i=0;i<toolsStore.toolsList().size();i++){
					
					if (toolsStore.toolsList().get(i).checked==true){
						this.selectedTools.numToolsChecked--;
						if (toolsStore.toolsList().get(i).type.equals("Device")){
							this.selectedTools.numDevicesToRemove--;
							if (this.selectedTools.numDevicesToRemove==0) urlDevices+=toolsStore.toolsList().get(i).toolId;
							else urlDevices+=toolsStore.toolsList().get(i).toolId+",";
						}
						else if (toolsStore.toolsList().get(i).type.equals("Application")){
							this.selectedTools.numAppsToRemove--;
							if (this.selectedTools.numAppsToRemove==0) urlApplications+=toolsStore.toolsList().get(i).toolId;
							else urlApplications+=toolsStore.toolsList().get(i).toolId+",";
						}
						
					}
					
				}
				Url+=urlDevices+urlApplications;
				HttpPut solicitudBorrar = new HttpPut(Url);
				SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
				solicitudBorrar.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				System.out.println("Url para borrar dispositivos: "+Url);
				cliente.execute(solicitudBorrar);
				
				Looper.prepare();
    				Dialog dialog = create_ok_dialog(getActivity(), "Advertencia", "Las herramientas han sido borradas");
    				dialog.show();
    			Looper.loop();
    		
				reloadOnUiThread("user_tools");
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


