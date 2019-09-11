package area.guias.pfc;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.TextView;

public class DevicesViewFragment extends Fragment{
	public DevicesListAdapter devicesAdapter;
	public DevicesStore devicesStore = new DevicesArray();
	private TechnicalSetting technical_setting = new TechnicalSetting();
	private ProgressDialog progressDialog;
	private View rootView;
	private String devicesType;
	private static final String LOGTAG = "LogsAndroid";
	private final static int MINIVIEW = 1, SMALLVIEW = 2, WHOLEVIEW = 3;
	public DownloadDevicesImages downloadImages;
	private String from_activity;
	
	// SCROLL VARIABLES
	Scroll scroll;
	
	// LANGUAGE
	Language language;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
			
		devicesAdapter = new DevicesListAdapter(getActivity(), devicesStore.listaDispositivos());
		GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
		gridView.setAdapter(devicesAdapter); 
		
		gridView.setOnItemClickListener(new OnItemClickListener() {	     
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), DeviceView.class);
		    	intent.putExtra("titulo", devicesStore.getDevice(position).name);
		    	intent.putExtra("imageUrl", devicesStore.getDevice(position).UrlImage);
		    	intent.putExtra("description", devicesStore.getDevice(position).description);
			    startActivity(intent);
			}
	    });
		
		technical_setting.id = getActivity().getIntent().getExtras().getInt("technical_setting_id");			
        
        language = getActivity().getIntent().getParcelableExtra("Language");
        
        from_activity = getActivity().getIntent().getStringExtra("from_activity");
        
        scroll = new Scroll();
        
        devicesStore.borrarLista();  
        
		downloadImages = null;
		
		if( savedInstanceState != null ) {
        	
        	scroll = savedInstanceState.getParcelable("scroll");
        	configureListViewScroll();
        	
        	devicesType = savedInstanceState.getString("devicesType");    
        	
        	for (int i=0; i< savedInstanceState.getParcelableArrayList("devicesList").size(); i++)
        		devicesStore.listaDispositivos().add((Device) savedInstanceState.getParcelableArrayList("devicesList").get(i));      	
        	
        	if (devicesStore.listaDispositivos().size() > 0) {
        		changeVisibilityOfGridView(View.VISIBLE);
    			changeVisibilityOfEmptyView(View.INVISIBLE);
        	} else {
        		changeVisibilityOfGridView(View.INVISIBLE);
    			changeVisibilityOfEmptyView(View.VISIBLE);
        	}
        	
        	updateDevicesList();
        }
		else{
			
			changeVisibilityOfGridView(View.INVISIBLE);
			changeVisibilityOfEmptyView(View.INVISIBLE);
			
			configureListViewScroll(); 
	        
			devicesType = "user_devices";
			getDevices(devicesType);
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
	        rootView = inflater.inflate(R.layout.devices_view_fragment, container, false);
	    } catch (InflateException e) {
	    	
	    }		
	    return rootView;
	}
	
	
	
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
	   Log.d("GuideViewTabs", "onSaveInstanceState DevicesViewFragment");
	   super.onSaveInstanceState(outState);
	   outState.putParcelable("scroll", scroll);
	   outState.putString("devicesType", devicesType);
	   outState.putParcelableArrayList("devicesList", devicesStore.listaDispositivos());	 
	}
	
	
	
	
	
	public void addDevices(boolean finish){		
		int numDevices2Add=countDevicesToAdd();
    	int numDevicesAdded=countDevicesAdded();
    	configureDialog(numDevices2Add, numDevicesAdded, finish);   
    	
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
			        searchDevices(scroll.getSearch(), scroll.getPage(), scroll.getPageElements());		            
			     }
			}
		});
	}
	
	
	
	
	
	private void configureDialog(final int numDevices2Add, int numDevicesAdded, final boolean finish){
		List<Device> DispList = devicesStore.listaDispositivos();
		
		final Dialog dialog = new Dialog(getActivity()); 
		
		dialog.setCancelable(true);		
		dialog.setTitle("Advertencia");
				
		if (numDevices2Add > 0){	
			
			dialog.setContentView(R.layout.dialog_two_button_layout);
			TextView msg = (TextView) dialog.findViewById(R.id.message);
			
			if (numDevices2Add > 1){
				if (finish == false) msg.setText("¿Quieres añadir estos dispositivos?");
				if (finish == true) msg.setText("¿Quieres añadir estos dispositivos antes de salir?");
			}
			else if (numDevices2Add == 1){
				if (finish == false) msg.setText("¿Quieres añadir este dispositivo?");	
				if (finish == true) msg.setText("¿Quieres añadir este dispositivo antes de salir?");	
			}
			else Log.e(LOGTAG, "Número de dispositivos para añadir menor que cero.");
			
			Button buttonYes = (Button) dialog.findViewById(R.id.yes);
			buttonYes.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
					new AddInvolvements(numDevices2Add).start();
					dialog.cancel();
					if (finish == true) getActivity().finish();
				}
			});  	
			
			Button buttonNo = (Button) dialog.findViewById(R.id.no);
			buttonNo.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
					dialog.cancel();
					if (finish == true) getActivity().finish();
				}
			});  	
			
			dialog.show();
		}
		else{
			dialog.setContentView(R.layout.dialog_one_button_layout);
			
			TextView msg = (TextView) dialog.findViewById(R.id.message);
			
			if (numDevices2Add == 0) msg.setText("¡No tienes ningún dispositivo marcado!");
			else if (numDevicesAdded == DispList.size()) msg.setText("¡Ya tienes todos los dispositivos!");
						
			Button buttonYes = (Button) dialog.findViewById(R.id.ok);
			buttonYes.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
					dialog.cancel();
				}
			});  
			
			dialog.show();
		}
	}
	
	
	
	
	
	public int countDevicesAdded(){
		List<Device> devicesList = devicesStore.listaDispositivos();
		int numDevicesAdded=0;
		for (int i=0; i<devicesList.size();i++)			
			if ((devicesList.get(i).checkedString.equals("Añadido")))
					numDevicesAdded++;
		
		return numDevicesAdded;
	}
	
	
	
	
	
	public int countDevicesToAdd(){
		List<Device> devicesList = devicesStore.listaDispositivos();
		int numDevicesToAdd=0;
		for (int i=0; i<devicesList.size();i++){
			if ((devicesList.get(i).checked == true)&&(devicesList.get(i).checkedString.equals("Añadir")))
					numDevicesToAdd++;
		}
		return numDevicesToAdd;
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
			        	if (devicesType.equals("user_devices")) getDevices(devicesType);
			        }
			    });
		
		return dialog;
	}
	
	
	
	
	
	protected void downloadDevicesImages(){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {
	        	downloadImages = new DownloadDevicesImages();
	        	downloadImages.execute();
	        }
	    });
	}
	
	
	
	
	
	
	
	
	
	
	
	protected void finProgressBar(){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {
	        	progressDialog.dismiss();	  
	        	updateDevicesList();
	        	GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
				gridView.setVisibility(View.VISIBLE);
				TextView textView = (TextView) getActivity().findViewById(android.R.id.empty);
				if (devicesStore.listaDispositivos().size()==0) textView.setVisibility(View.VISIBLE);
				else if(devicesStore.listaDispositivos().size()>0) textView.setVisibility(View.INVISIBLE);
	        }
	    });
	}
	
	
	
	
	
	public List<Integer> getDevicesIds() throws ClientProtocolException, IOException{
		Log.d(LOGTAG, "getDevicesIds");
		HttpClient cliente = new DefaultHttpClient();
	    String BaseUrlPage = new IP().ip+"/" + language.getStringLanguage() + "/devices.json?devices_number=" + scroll.getPageElements() + "&page=" + scroll.getPage();
	    HttpGet solicitud = new HttpGet(BaseUrlPage);	
	    SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
	    solicitud.setHeader("Cookie", "_AREA_v0.1_session="+ preferences.getString("Cookie", "0"));		    
		HttpResponse respuesta = cliente.execute(solicitud);
		HttpEntity entity = respuesta.getEntity();
		List<Integer> IdList = new Funciones().readJsonIdsStream(entity);
		return IdList;
	}
	
	
	
	
	
	public void getDevices(final String devType){	
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {
	        	Log.d(LOGTAG, "getDevices");
	        	if ((downloadImages != null)&&(!downloadImages.isCancelled())) downloadImages.cancel(true);
	        	devicesStore.borrarLista();
	        	scroll.setTotalCount(scroll.getPageElements());
	        	scroll.setPage(1);
	        	if (devType.equals("user_devices")){
	        		devicesType = "user_devices";
	        		progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando dispositivos de la guía...");
	        		new GetDevices("user_devices", scroll.getPage(), scroll.getPageElements()).start();
	        	}
	        	else{
	        		devicesType = "system_devices";
	        		progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando dispositivos del sistema...");
	        		new GetDevices("system_devices", scroll.getPage(), scroll.getPageElements()).start();
	        	}
	        }
	    });
	}
	
	
	
	
	
	public List<TechnicalSetting> getWholeViewTechnicalSettings(int ts_id) throws ClientProtocolException, IOException{
		Log.d(LOGTAG, "getWholeViewTechnicalSettings");
		HttpClient cliente = new DefaultHttpClient();
    	String BaseUrlPage = new IP().ip + "/" + language.getStringLanguage() + "/technicalSettings/getWholeView.json?id=" + ts_id;
    	System.out.println("URL TSETTING WHOLE VIEW:" + BaseUrlPage);
    	HttpGet	solicitud = new HttpGet(BaseUrlPage);	
    	SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
    	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));    	
		HttpResponse respuesta = cliente.execute(solicitud);
		HttpEntity entity = respuesta.getEntity();				
		List<TechnicalSetting> tsettingList = new Funciones().readJsonTSStream(entity);			
		return tsettingList; 
	}
	
	
	
	
	
	public void getTypeOfView(String language, String urlIds, int typeOfView){
		new GetTypeOfView(language, urlIds, typeOfView).start();
	}
	
	
	
	
	
	public List<Device> getWholeViewDevices(List<Integer> IdList) throws ClientProtocolException, IOException{
		Log.d(LOGTAG, "getWholeViewDevices");
		String BaseUrlPage = new IP().ip + "/" + language.getStringLanguage() + "/devices/getWholeView.json?id=";
    	for (int i=0; i<IdList.size(); i++){
    		BaseUrlPage = BaseUrlPage + IdList.get(i);
    		if (i<IdList.size()-1) BaseUrlPage = BaseUrlPage+",";        		
    	}		
    	
    	HttpGet solicitud = new HttpGet(BaseUrlPage);
    	SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
    	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
    	HttpClient cliente = new DefaultHttpClient();
        HttpResponse respuesta = cliente.execute(solicitud);
    	HttpEntity entity = respuesta.getEntity();
    	List<Device> DispList = new Funciones().readJsonDispStream(entity);
    	
    	// PRUEBA
    	respuesta = cliente.execute(solicitud);
    	System.out.println(EntityUtils.toString(respuesta.getEntity()));
    	// PRUEBA
    	
    	System.out.println(BaseUrlPage);
    	return DispList;
	}
	
	
	
	
	
	public String makeUrlDevices(String search, int page, int devices_number){
		String url =  new IP().ip+"/"+language.getStringLanguage()+"/devices/search.json";
       	String correct_search = new Funciones().adapt_search_string(search);
       	
		
			if (search.equals("") == false){
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
	
	
	
	
	
	public int numSelectedDevices(){
		int numDeviceChecked=0;
		List<Device> DispList = devicesStore.listaDispositivos();
		for (int i=0;i<DispList.size();i++){
			if (DispList.get(i).checked == true) numDeviceChecked++;
		}
		return numDeviceChecked;
	}
	
	
	
	
	
	public void removeDevices(final boolean finish){
		// Mirar el numero de dispositivos a eliminar
		int numDeviceChecked = numSelectedDevices();
		
		final Dialog dialog = new Dialog(getActivity());
		dialog.setTitle("Advertencia");
		dialog.setCancelable(false);
		
		
		if (numDeviceChecked == 0){
			
			dialog.setContentView(R.layout.dialog_one_button_layout);
			TextView msg = (TextView) dialog.findViewById(R.id.message);
			msg.setText("Tienes que marcar algún dispositivo para eliminarlo.");
			Button buttonYes = (Button) dialog.findViewById(R.id.ok);
			buttonYes.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
					dialog.cancel();
				}
			});  	
		}
		else if (numDeviceChecked>0){
			
			dialog.setContentView(R.layout.dialog_two_button_layout);
			TextView msg = (TextView) dialog.findViewById(R.id.message);
			
			if (numDeviceChecked==1){
				if (finish == false) msg.setText("¿Estás seguro de que quieres eliminar este dispositivo de tu contexto técnico?");
				if (finish == true) msg.setText("¿Quieres eliminar este dispositivo de tu contexto técnico antes de salir?");
			}
			else if (numDeviceChecked > 1){
				if (finish == false) msg.setText("¿Estás seguro de que quieres eliminar estos dispositivos de tu contexto técnico?");
				if (finish == true) msg.setText("¿Quieres eliminar estos dispositivos de tu contexto técnico antes de salir?");
			}
			
			Button buttonYes = (Button) dialog.findViewById(R.id.yes);
			buttonYes.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
				   int numDevChecked = numSelectedDevices();				   				   
				   new DeleteInvolvements(numDevChecked).start();					   				   
				   dialog.cancel();
				   if (finish == true){
					   getActivity().finish();
				   }
			   }
			});  	
			
			Button buttonNo = (Button) dialog.findViewById(R.id.no);
			buttonNo.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
		   	      dialog.cancel();
		   	      if (finish == true){
		   	    	  getActivity().finish();
		   	      }
			   }
	    	});  	
		}		  	
		dialog.show();		
		
    }
	
	
	
	
	
	public void searchDevices(String search, int page, int guides_number){
		if (page == 1) scroll.setPage(1);
		new SearchDevices(search, page, guides_number).start();
	}
	
	
	
	
	
	public void updateDevicesList(){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	    

        		System.out.println("ACTUALIZAR LISTA DISPOSITIVOS");
	        	for (int i=0;i<devicesStore.listaDispositivos().size();i++){
            		System.out.println("name: " + devicesStore.getDevice(i).name);
            		System.out.println("description: " + devicesStore.getDevice(i).description);
            		System.out.println("short description: " + devicesStore.getDevice(i).short_description);
            	}
	            devicesAdapter.notifyDataSetChanged();
	        }
	    });
	}
	
	

	
	
	private OnClickListener mCommentButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
        	final int position = gridView.getPositionForView(v);
            if (position != AdapterView.INVALID_POSITION) {
            	Intent intent = new Intent(getActivity(), CommentsView.class);
            	intent.putExtra("model", "Device");
            	intent.putExtra("id", devicesStore.getDevice(position).id);
            	intent.putExtra("title", devicesStore.getDevice(position).name);
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
    
    
    
    
    
    class AddInvolvements extends Thread {
    	int num_devices_checked;
    	
    	public AddInvolvements(int num_devices_checked){
    		this.num_devices_checked = num_devices_checked;
    	}
    	
        @Override 
        public void run() {
        	try {
        		HttpClient cliente = new DefaultHttpClient();
            	String BaseUrlPage = new IP().ip+"/"+language.getStringLanguage() +"/technicalSettings/addInvolvements.json?id="+technical_setting.id+"&devices=";
            	
				List<Device> DispList = devicesStore.listaDispositivos();
				
				if (num_devices_checked != 0){
					for (int i=0; i<DispList.size();i++){
						if ((DispList.get(i).checked == true)&&(DispList.get(i).checkedString.equals("Añadir"))){	
							num_devices_checked--;
							BaseUrlPage+=DispList.get(i).id;
							
							if (from_activity.equals("GuideViewTabs")) GuideViewTabs.guide.technical_setting.devices_id.add(DispList.get(i).id);
							else TechnicalSettingView.technical_setting.devices_id.add(DispList.get(i).id);
							
							if (num_devices_checked > 0) BaseUrlPage += ",";
							DispList.get(i).checkedString = "Añadido";
							devicesStore.listaDispositivos().get(i).checkedString = "Añadido";
						}						
					}
					HttpPut solicitud = new HttpPut(BaseUrlPage);
					SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
					solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
					Log.d("DevicesViewFragment", "url: " + BaseUrlPage);
					HttpResponse respuesta = cliente.execute(solicitud);
					
					Log.d(LOGTAG, EntityUtils.toString(respuesta.getEntity()));
					updateDevicesList();
					Looper.prepare();
            			Dialog dialog = new Funciones().create_ok_dialog(getActivity(), "Advertencia", "Los dispositivos han sido añadidos");
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
        	updateDevicesList();
        }        
    }
    
    
    
    
    
    class DeleteInvolvements extends Thread {
        int numDeviceChecked;
		public DeleteInvolvements(int numDeviceChecked){
			this.numDeviceChecked=numDeviceChecked;
		}
		
        @Override 
        public void run() {        	
			try {				
				HttpClient cliente = new DefaultHttpClient();
				List<Device> DispList = devicesStore.listaDispositivos();
				String BaseUrlPage = new IP().ip+"/"+language.getStringLanguage()+"/technicalSettings/deleteInvolvements.json?id="+technical_setting.id+"&devices=";
				for (int i=0;i<DispList.size();i++){
					if (DispList.get(i).checked==true){
						this.numDeviceChecked--;
						if (this.numDeviceChecked==0) BaseUrlPage += DispList.get(i).id;
						else BaseUrlPage += DispList.get(i).id + ",";
						
						if (from_activity.equals("GuideViewTabs")){
							for (int j=0; j<GuideViewTabs.guide.technical_setting.devices_id.size(); j++)
								if (GuideViewTabs.guide.technical_setting.devices_id.get(j) == DispList.get(i).id)
									GuideViewTabs.guide.technical_setting.devices_id.remove(j);
						}
						else{
							for (int j=0; j<TechnicalSettingView.technical_setting.devices_id.size(); j++)
								if (TechnicalSettingView.technical_setting.devices_id.get(j) == DispList.get(i).id)
									TechnicalSettingView.technical_setting.devices_id.remove(j);
						}
					}
				}
				HttpDelete solicitudBorrar = new HttpDelete(BaseUrlPage);
				SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
				solicitudBorrar.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				System.out.println("Url para borrar dispositivos: "+BaseUrlPage);
				HttpResponse respuesta = cliente.execute(solicitudBorrar);	
				Log.d(LOGTAG, "Respuesta: " + EntityUtils.toString(respuesta.getEntity()));
				
				Looper.prepare();
        			Dialog dialog = create_ok_dialog(getActivity(), "Advertencia", "Los dispositivos han sido borrados");
        			dialog.show();
        		Looper.loop();
        	
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
			updateDevicesList();
        }        
    }
    
    
    
    
    
    public class DevicesListAdapter extends BaseAdapter {
		private final Activity actividad;
	    private final ArrayList<Device> lista;
	    
	   
	    
	    public DevicesListAdapter(Activity actividad, ArrayList<Device> dispositivos) {
	        super();
	        this.actividad = actividad;
	        this.lista = dispositivos;
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
			 
	         
			 myHolder.checkbox.setOnCheckedChangeListener(new OnCheckedChangeListener() {				
				@Override
				public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
					if (devicesType.equals("user_devices")){
						if (isChecked) devicesStore.listaDispositivos().get(position).checked = true;
						else devicesStore.listaDispositivos().get(position).checked = false;
					}
					else {
						if (isChecked) devicesStore.listaDispositivos().get(position).checked = true;
						else if (!devicesStore.listaDispositivos().get(position).checkedString.equals("Añadido")) lista.get(position).checked = false;		
					}
				}
			 });
			
			 if (devicesStore.listaDispositivos().get(position).checked == true) view.setChecked(true);
			 else view.setChecked(false);
			 
			 if (devicesType.equals("user_devices")) view.setCheckboxText("Eliminar");
			 else{						 
		         if (lista.get(position).checkedString.equals("Añadido")) view.setCheckboxText("Añadido");
		         else if (lista.get(position).checkedString.equals("Añadir")) view.setCheckboxText("Añadir");
			 }
			 
	         if (devicesStore.listaDispositivos().get(position).name != "") view.setTitle(lista.get(position).name);
	         else view.setTitle("");
	         
	         if (devicesStore.listaDispositivos().get(position).description != "") view.setDescription(lista.get(position).getShortDescription());
	         else view.setDescription("");
	         
	         if (devicesStore.listaDispositivos().get(position).image != null){
	        	 view.setImage(ImageHelper.getRoundedCornerBitmap(lista.get(position).image, 12, this.actividad));
	         }
	         else{ // Imagen por defecto (NO IMAGE)
	        	 Bitmap bm = BitmapFactory.decodeResource(this.actividad.getResources(), R.drawable.imagen);
	        	 view.setImage(ImageHelper.getRoundedCornerBitmap(bm, 12, this.actividad));
	         }
	         return view;
		}	
	}
    
    
    
    
    
    class DownloadDevicesImages extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... activityList) {
        	HttpClient cliente = new DefaultHttpClient();
        	List<Device> DispList = devicesStore.listaDispositivos();
        	for(int i=0;i<DispList.size();i++){
        		if (DispList.get(i).UrlImage != null){
        			if (DispList.get(i).UrlImage.equals("none")==false){
        				try {
        					String BaseUrlPage = new IP().ip+DispList.get(i).UrlImage;
        					System.out.println("Url imagen dispositivo");
        					System.out.println(BaseUrlPage);
        					HttpGet solicitud = new HttpGet(BaseUrlPage);	
        					SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        					solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
        					if (isCancelled()) break;
        					HttpResponse respuesta = cliente.execute(solicitud);
        					HttpEntity entity = respuesta.getEntity();
        					Bitmap loadedImage = BitmapFactory.decodeStream(entity.getContent());
        					if (loadedImage != null){
        						devicesStore.setImage(DispList.get(i).id, loadedImage);
        					}
        					   							
        				} catch (ClientProtocolException e) {
        					e.printStackTrace();
        				} catch (IOException e) {
        					e.printStackTrace();
        				}	
        			}
        		}
        	}
       		updateDevicesList();
       		return null;
       	}
	}       
    
    
    
    
    
    public class GetDevices extends Thread {
		String devicesType;
		int page, devices_number;
		
		public GetDevices(String devicesType, int page, int devices_number){
			this.devicesType = devicesType;
			this.page = page;
			this.devices_number = devices_number;
		}
		
        @Override 
        public void run() {
            try {	 
            	Log.d(LOGTAG, "GetDevices");
            	List<Device> DispList = null;
            	// CONSEGUIR LISTA DE DISPOSITIVOS PROPIOS DE ESTE TSETTING
            	technical_setting = getWholeViewTechnicalSettings(technical_setting.id).get(0);
            	
            	if (devicesType.equals("system_devices")){	
            		// CONSEGUIR LISTA DE DISPOSITIVOS DEL SISTEMA
    				List<Integer> IdList = getDevicesIds();
    				DispList = getWholeViewDevices(IdList);
    				
    				for (int i=0; i<DispList.size(); i++){
    	        		for (int j=0; j<technical_setting.devices_id.size(); j++){
    		        		if (technical_setting.devices_id.get(j) == DispList.get(i).id){
    		        			DispList.get(i).checked = true;
    		        			DispList.get(i).checkedString = "Añadido";
    		        		}
    		        	}	 
    	        	}	
    				
            	} else if (devicesType.equals("user_devices")){
            		DispList = getWholeViewDevices(technical_setting.devices_id);
            	} else Log.e(LOGTAG, "Tipo de dispositivos desconocido.");
           					
            	// Guardar dispositivos de esta guía
            	for (int i=0;i<DispList.size();i++)
            		if (DispList.get(i).id != -1) devicesStore.guardarDispositivo(DispList.get(i));				
            		
            	// Cada dispositivo tendrá una imagen
            	if (DispList.size()>0) downloadDevicesImages();					
            	
				finProgressBar();
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
        	String url = new IP().ip + "/" + this.language + "/devices/" + typeOfView + ".json?" + this.urlIds; 
        	HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
			try {
				HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
				
				List<Device> deviceList = new Funciones().readJsonDispStream(httpResponse.getEntity());
				
				for (int i=0;i<deviceList.size();i++) devicesStore.guardarDispositivo(deviceList.get(i));
						 				
				downloadDevicesImages();				
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
    
    
    
    
    
	public class SearchDevices extends Thread {	
        String search;
        int page, devices_number;
            
        public SearchDevices(String search, int page, int devices_number) {
        	progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando dispositivos...");
            this.search = search;
            this.page = page;
            this.devices_number = devices_number;
        }

        @Override 
        public void run() { 
    		try {
    			System.out.println("SearchDevices");
    			scroll.setMethod(Scroll.getMethodSearch());
    			scroll.setSearch(search);
    			if (scroll.getPage() == 1) devicesStore.borrarLista();
    			
    			String url = makeUrlDevices(search, page, devices_number);
    			
    			HttpGet httpGet = new HttpGet(url);
    			SharedPreferences preferences = getActivity().getSharedPreferences( "datos", Context.MODE_PRIVATE);
    	       	httpGet.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
    	       	Log.d("DevicesFragment", "url: " + url);
    	      
    	       	//PRUEBA
				HttpResponse httpResponsePrueba = new DefaultHttpClient().execute(httpGet);
				if (httpResponsePrueba.getEntity() != null) System.out.println(EntityUtils.toString(httpResponsePrueba.getEntity()));
				// FIN DE PRUEBA
				
    			HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);   			
    			HttpEntity httpEntity = httpResponse.getEntity();
    			
    			if (httpEntity != null){
    				List<Integer> listInteger = new Funciones().readJsonIdsStream(httpResponse.getEntity());
    				
    				String urlIds = null;
    				if (devicesType.equals("user_devices")){
    					// Mirar si algún id devuelto por "search" coincide con alguno id del contexto técnico
    					List<Integer> newList = new ArrayList<Integer>();
    					for (int i=0; i<technical_setting.devices_id.size(); i++){
    						for (int j=0; j<listInteger.size(); j++){
    							if (listInteger.get(j) == technical_setting.devices_id.get(i)){
    								newList.add(listInteger.get(j));
    							}
    						}	   					
    					}
    					urlIds = new Funciones().makeUrlIds(newList);
    				}
    				else if (devicesType.equals("system_devices")){
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
}


