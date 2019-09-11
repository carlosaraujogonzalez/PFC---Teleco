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
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
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
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

public class TechnicalSettingsFragment extends Fragment{
	public TechnicalSettingsAdapter adapter;
	public TechnicalSettingsStore technicalSettingsStore = new TechnicalSettingsArray();
	private ProgressDialog progressDialog;	
	public Language language;
	public TechnicalSetting guide_technical_setting;
	private static final String LOGTAG = "LogsAndroid";
	private List<Integer> idsListOwnerTrue;
	private DownloadTechnicalSettingImages downloadImages;
	// SCROLL VARIABLES
	Scroll scroll;
	
	
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		language = getActivity().getIntent().getParcelableExtra("language");
		
		guide_technical_setting = getActivity().getIntent().getExtras().getParcelable("guide_technical_setting");
		for (int i=0; i<guide_technical_setting.devices_id.size(); i++)
			Log.d("", "deviceId recibido: " + guide_technical_setting.devices_id.get(i));
		GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
		gridView.setVisibility(View.INVISIBLE);
		TextView textView = (TextView) getActivity().findViewById(android.R.id.empty);
		textView.setVisibility(View.INVISIBLE);
		
		adapter = new TechnicalSettingsAdapter(getActivity(), technicalSettingsStore.technical_settings_List());
		gridView.setAdapter(adapter); 
		
		gridView.setOnItemClickListener(new OnItemClickListener() {	     
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), TechnicalSettingView.class);
		    	intent.putExtra("technical_setting", technicalSettingsStore.get(position));
		    	intent.putExtra("language", language);
		    	intent.putExtra("guide_technical_setting", guide_technical_setting);
		    	
		    	Owner owner = new Owner();
		    	owner.setOwner(Owner.NONE);
		    	for (int i=0;i<idsListOwnerTrue.size(); i++){
		    		if (technicalSettingsStore.get(position).id == idsListOwnerTrue.get(i)){
		    			owner.setOwner(Owner.TRUE);
		    		}
		    	}
		    	intent.putExtra("owner", owner);
			    
		    	startActivity(intent);
			}
	    });
		
		scroll = new Scroll();
        	      
        if(savedInstanceState != null) {
        	scroll = savedInstanceState.getParcelable("scroll");
        	
        	for (int i=0; i< savedInstanceState.getParcelableArrayList("technical_settings_list").size(); i++)
        		technicalSettingsStore.technical_settings_List().add((TechnicalSetting) savedInstanceState.getParcelableArrayList("technical_settings_list").get(i));      	
          	
        	if (technicalSettingsStore.technical_settings_List().size()>0) gridView.setVisibility(View.VISIBLE);
        	else textView.setVisibility(View.VISIBLE);
    		
        	updateTechnicalSettingsList();
        	        	
        }
		else{
			
	        configureListViewScroll(); 
			get_technicalSettings();			
			
		}
	}
	
	
	
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			return inflater.inflate(R.layout.technical_settings_fragment, container);	
	}
	

	

	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	   Log.d("TechnicalSettingsFragment", "onSaveInstanceState");
	   super.onSaveInstanceState(outState);
	   outState.putParcelable("scroll", scroll);
	   outState.putParcelableArrayList("technical_settings_list", technicalSettingsStore.technical_settings_List());	   
	}
	
	
	
	
	
	private void configureListViewScroll(){
		GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
			
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				
			}
			
			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
				boolean loadMore = firstVisibleItem + 1 + visibleItemCount >= scroll.getTotalCount();
			    if(loadMore){
			    	scroll.setTotalCount(scroll.getTotalCount()+scroll.getPageElements());
			    	scroll.setPage(scroll.getPage()+1);
			        System.out.println("LOAD MORE("+scroll.getPage()+")");
			        new GetTechnicalSettings().start();		            
			     }
			}
		});
	}
	
	
	
	
	
	public ProgressDialog createProgressDialog(String str){
    	Log.d(LOGTAG, "createProgressDialog");
    	progressDialog = new ProgressDialog(getActivity());   	
    	progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	progressDialog.setCancelable(false);
    	progressDialog.show();
    	progressDialog.setContentView(R.layout.progress_dialog);
    	TextView textView = (TextView) progressDialog.findViewById(R.id.textView);
    	textView.setText(str);
    	return progressDialog;
    }
	
	
	
	
	
	protected void downloadTechnicalSettingImages(){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {
	        	new DownloadTechnicalSettingImages().execute();
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
				
				System.out.println("toolsStore.toolsList().size= "+technicalSettingsStore.technical_settings_List().size());
				
				if (technicalSettingsStore.technical_settings_List().size() == 0) textView.setVisibility(View.VISIBLE);				
				else textView.setVisibility(View.INVISIBLE);
	        }
	    });
	}
	
	
	
	
	
	
	public void getTechnicalSettings(){
		Log.d(LOGTAG, "getTechnicalSettings");
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	        	new GetTechnicalSettings().start();
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
            	intent.putExtra("model", "TechnicalSetting");
            	intent.putExtra("id", technicalSettingsStore.get(position).id);
            	intent.putExtra("title", technicalSettingsStore.get(position).name);
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
                //showMessage("Bot�n Compartir " + position + " Pulsado");
            }
        }
    };
    
    
    
  
    
    public void get_technicalSettings(){	
    	Log.d(LOGTAG, "get_technicalSettings");
    	if ((downloadImages != null)&&(!downloadImages.isCancelled())) downloadImages.cancel(true);
		technicalSettingsStore.deleteList();
		scroll.setTotalCount(scroll.getPageElements());
		scroll.setPage(1);
		
		progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando contextos t�cnicos del sistema...");
		new GetTechnicalSettings().start();
	}
	
	
	
	
	
    public void reloadOnUiThread(final String toolsType){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	            get_technicalSettings();
	        }
	    });
	}
    
    
  
	
	
	public void restart(){
		new GetTechnicalSettings().start();
	}
	
	
	
	
	
	public void search_technical_settings(String search, int page, int techs_number){
		technicalSettingsStore.deleteList();
		new SearchTechnicalSettings(search, page, techs_number).start();
	}
    
	
	
	
	
	
	public void updateTechnicalSettingsList(){
		Log.d(LOGTAG, "updateTechnicalSettingsList");
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	            adapter.notifyDataSetChanged();
	        }
	    });
	}
	
	
	

    
    class DownloadTechnicalSettingImages extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... toolsList) {  	
        	HttpClient cliente = new DefaultHttpClient();
        	for(int i=0;i<technicalSettingsStore.size();i++){
        		if (technicalSettingsStore.get(i).getImageUrl() != null){
        			if (technicalSettingsStore.get(i).getImageUrl().equals("none")==false){
        				try {
        					String BaseUrlPage = new IP().ip+technicalSettingsStore.get(i).getImageUrl();
        					System.out.println("Url imagen herramienta");
        					System.out.println(BaseUrlPage);
        					HttpGet solicitud = new HttpGet(BaseUrlPage);	
        					SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        					solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
        					HttpResponse respuesta = cliente.execute(solicitud);
        					if (isCancelled()) break;
        					HttpEntity entity = respuesta.getEntity();
        					Bitmap loadedImage = BitmapFactory.decodeStream(entity.getContent());
        					technicalSettingsStore.setBitmapImage(technicalSettingsStore.get(i).getId(), loadedImage);  
        				} catch (ClientProtocolException e) {
        					e.printStackTrace();
        				} catch (IOException e) {
        					e.printStackTrace();
        				}	
        			}
        		}
        	}
       		updateTechnicalSettingsList();
       		return null;
       	}
	}       


    
    
    
    public class GetTechnicalSettings extends Thread {
    	List<TechnicalSetting> technicalSettingsList;
    	List<Integer> idsList;
	
	
	
    	@Override 
    	public void run() {
    	
    		try {
    			Log.d(LOGTAG, "GetTechnicalSettings");
    			HttpClient cliente = new DefaultHttpClient(); 
        	
    			// Url devices
    			String url_technical_settings = null;
    			
    			url_technical_settings =  new IP().ip+"/"+language.getStringLanguage()+"/technicalSettings.json?owner=true";
    			HttpGet solicitud = new HttpGet(url_technical_settings);
    			SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
    			solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
    			HttpResponse respuesta = cliente.execute(solicitud);
    			HttpEntity entity = respuesta.getEntity();
    			idsListOwnerTrue = new Funciones().readJsonIdsStream(entity);
    					
    			url_technical_settings =  new IP().ip+"/"+language.getStringLanguage()+"/technicalSettings.json";
    			solicitud = new HttpGet(url_technical_settings);
    			solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
    			respuesta = cliente.execute(solicitud);
    			entity = respuesta.getEntity();
    			idsList = new Funciones().readJsonIdsStream(entity);
		        
    			// PRUEBA		   
    			/*Log.d("TechnicalSettingsFragment", "url: " + url_technical_settings);
    			HttpResponse respuestaPrueba = cliente.execute(solicitud);
    			if (respuestaPrueba.getEntity() != null) System.out.println("JSON WHOLE VIEW TECHNICALSETTINGS IDS: "+EntityUtils.toString(respuestaPrueba.getEntity()));*/
    			// FIN DE PRUEBA
				
    			url_technical_settings =  new IP().ip+"/"+language.getStringLanguage()+"/technicalSettings/getWholeView.json?id=";
    			for (int i=0; i<idsList.size(); i++){
    				url_technical_settings +=  idsList.get(i);
    				if (i<idsList.size()-1) url_technical_settings += ",";        		
    			}    				    				    				    				    				    				    	   			
    			
    			// TECHNICAL SETTINGS   			
    			System.out.println("Url technical settings: "+ url_technical_settings);
    			solicitud = new HttpGet(url_technical_settings);
    			preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
    			solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
	        
    			// PRUEBA		        
    			//respuesta = cliente.execute(solicitud);	
    			//System.out.println("JSON WHOLE VIEW TECHNICAL SETTING: "+EntityUtils.toString(respuesta.getEntity()));
    			// FIN DE PRUEBA
			
    			respuesta = cliente.execute(solicitud);
    			entity = respuesta.getEntity();
    			
    			technicalSettingsList = new Funciones().readJsonTSStream(entity);	
    			
    			for (int i=0;i<technicalSettingsList.size();i++){
    				Log.d("", "ts description: " + technicalSettingsList.get(i).description);
    				Log.d("", "ts short_description: " + technicalSettingsList.get(i).short_description);
    				technicalSettingsStore.saveTechnicalSetting(technicalSettingsList.get(i));        		
    			}
    			
    			finProgressBar();
    			
    			updateTechnicalSettingsList();
			    
    			// IMAGES
    			if (technicalSettingsStore.size()>0) downloadTechnicalSettingImages();				
			
    			
    		} catch (ClientProtocolException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}				
    	}        
    }




    
    
    public class TechnicalSettingsAdapter extends BaseAdapter {
    	private final Activity actividad;
    	private final ArrayList<TechnicalSetting> lista;
    
   
    
    	public TechnicalSettingsAdapter(Activity actividad, ArrayList<TechnicalSetting> technicalSettings) {
    		super();
    		this.actividad = actividad;
    		this.lista = technicalSettings;
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
    			myHolder.checkbox.setVisibility(View.INVISIBLE);
    			myHolder.horizontalLine = (View) view.findViewById(R.id.horizontalLine);
    			myHolder.horizontalLine.setVisibility(View.INVISIBLE);
    			
    			view.setTag(myHolder);
    			view.createViews();
    		}
    		else myHolder = (ItemHolder) view.getTag();
	         
    		if (lista.get(position).getName() != "") view.setTitle(lista.get(position).getName());
    		else view.setTitle("T�tulo");    
         
    		if (lista.get(position).getShortDescription() != "") view.setDescription(lista.get(position).getShortDescription());
    		else view.setDescription("Descripci�n");       
         
    		if (lista.get(position).getBitmapImage() != null){
    			view.setImage(ImageHelper.getRoundedCornerBitmap(lista.get(position).getBitmapImage(), 12, this.actividad));
    		}
    		else { // Imagen por defecto (NO IMAGE)
    			Bitmap bm = BitmapFactory.decodeResource(this.actividad.getResources(), R.drawable.imagen);
    			view.setImage(ImageHelper.getRoundedCornerBitmap(bm, 12, this.actividad));
    		}
       
    		return view;
    	}
	
    }





    public class SearchTechnicalSettings extends Thread {	
        String search;
        int page, techs_number;
            
        public SearchTechnicalSettings(String search, int page, int techs_number) {
        	progressDialog = createProgressDialog("Buscando contextos t�cnicos...");
            this.search = search;
            this.page = page;
            this.techs_number = techs_number;
        }

        @Override 
        public void run() { 
    		try {
    			Log.d("TechnicalSettingsFragment", "SearchTechnicalSettings");       	
    	       	String url =  new IP().ip+"/"+language.getStringLanguage()+"/technicalSettings/search.json";
    	   		         	         			
    				if (this.search.equals("") == false){
    					url+="?search="+this.search;
    					if (this.page > 0) url+="&page="+this.page;        			
        				if (this.techs_number > 0) url+="&techs_number="+this.techs_number;  
    				}
    				else{
    					if (this.page > 0){
    						url+="?page="+this.page;        		
    						if (this.techs_number > 0) url+="&techs_number="+this.techs_number; 
    					}
    					else if (this.techs_number > 0) url+="techs_number="+this.techs_number; 
    				}
      			  		    						    				
    			HttpGet httpGet = new HttpGet(url);    			
    			SharedPreferences preferences = getActivity().getSharedPreferences( "datos", Context.MODE_PRIVATE);
    	       	httpGet.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
    			HttpClient httpClient = new DefaultHttpClient(); 
    			
    			Log.e(LOGTAG, "url: " + url);
				HttpResponse httpResponse = httpClient.execute(httpGet);				
				HttpEntity httpEntity = httpResponse.getEntity();
    			if (httpEntity != null){
    				List<Integer> idsList = new Funciones().readJsonIdsStream(httpEntity);
    				String url_technical_settings =  new IP().ip+"/"+language.getStringLanguage()+"/technicalSettings/getWholeView.json?id=";
        			for (int i=0; i<idsList.size(); i++){
        				url_technical_settings +=  idsList.get(i);
        				if (i<idsList.size()-1) url_technical_settings += ",";        		
        			}    				    				    				    				    				    				    	   			
        			
        			// TECHNICAL SETTINGS   			
        			System.out.println("Url technical settings: "+ url_technical_settings);
        			httpGet = new HttpGet(url_technical_settings);
        			preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        			httpGet.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
    	        
        			// PRUEBA		        
        			/*respuesta = cliente.execute(solicitud);
        			json = EntityUtils.toString(respuesta.getEntity());
        			System.out.println("JSON WHOLE VIEW TECHNICAL SETTING: "+json);*/
        			// FIN DE PRUEBA
    			
        			httpResponse = httpClient.execute(httpGet);
        			httpEntity = httpResponse.getEntity();
        			
        			ArrayList<TechnicalSetting> technicalSettingsList = new ArrayList<TechnicalSetting>();
        			technicalSettingsList = (ArrayList<TechnicalSetting>) new Funciones().readJsonTSStream(httpEntity);	
        			
        			// Guardar technical settings de este sistema
        			for (int i=0;i<technicalSettingsList.size();i++) technicalSettingsStore.saveTechnicalSetting(technicalSettingsList.get(i));        		
        			
    			    // IMAGES
        			if (technicalSettingsStore.size()>0) downloadTechnicalSettingImages();				
    			
        			finProgressBar();
        			updateTechnicalSettingsList();
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

