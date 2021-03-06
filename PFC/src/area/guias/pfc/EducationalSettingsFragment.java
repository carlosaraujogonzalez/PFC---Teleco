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

public class EducationalSettingsFragment extends Fragment{
	public EducationalSettingsAdapter adapter;
	public EducationalSettingsStore educationalSettingsStore = new EducationalSettingsArray();
	private ProgressDialog progressDialog;	
	public Language language;
	public EducationalSetting guide_educational_setting;
	private static final String LOGTAG = "LogsAndroid";
	private List<Integer> idsListOwnerTrue;
	private DownloadEducationalSettingImages downloadImages;
	// SCROLL VARIABLES
	Scroll scroll;
	
	
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);	
		
		language = getActivity().getIntent().getParcelableExtra("language");
		
		guide_educational_setting = getActivity().getIntent().getExtras().getParcelable("guide_educational_setting");
		
		GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
		gridView.setVisibility(View.INVISIBLE);
		TextView textView = (TextView) getActivity().findViewById(android.R.id.empty);
		textView.setVisibility(View.INVISIBLE);
		
		adapter = new EducationalSettingsAdapter(getActivity(), educationalSettingsStore.educational_settings_List());
		gridView.setAdapter(adapter); 
		
		gridView.setOnItemClickListener(new OnItemClickListener() {	     
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), EducationalSettingView.class);
		    	intent.putExtra("educational_setting", educationalSettingsStore.get(position));
		    	intent.putExtra("language", language);
		    	intent.putExtra("guide_educational_setting", guide_educational_setting);
		    	
		    	Owner owner = new Owner();
		    	owner.setOwner(Owner.NONE);
		    	for (int i=0;i<idsListOwnerTrue.size(); i++){
		    		if (educationalSettingsStore.get(position).id == idsListOwnerTrue.get(i)){
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
        	
        	for (int i=0; i< savedInstanceState.getParcelableArrayList("educational_settings_list").size(); i++)
        		educationalSettingsStore.educational_settings_List().add((EducationalSetting) savedInstanceState.getParcelableArrayList("educational_settings_list").get(i));      	
        	
        	if (educationalSettingsStore.educational_settings_List().size()>0) gridView.setVisibility(View.VISIBLE);
        	else textView.setVisibility(View.VISIBLE);
        	
        	updateEducationalSettingsList();
        	
        }
		else{
			
			configureListViewScroll(); 
	        get_educationalSettings();			
			
		}
	}
	
	

	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			return inflater.inflate(R.layout.educational_settings_fragment, container);	
	}
	

	

	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	   Log.d("EducationalSettingsFragment", "onSaveInstanceState");
	   super.onSaveInstanceState(outState);
	   outState.putParcelable("scroll", scroll);
	   outState.putParcelableArrayList("educational_settings_list", educationalSettingsStore.educational_settings_List());	   
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
			        new GetEducationalSettings().start();		            
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
	
	
	
	
	
	protected void downloadEducationalSettingImages(){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {
	        	downloadImages = new DownloadEducationalSettingImages();
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
				
				System.out.println("educationalSettingsStore.edSettingList().size= "+educationalSettingsStore.educational_settings_List().size());
				
				if (educationalSettingsStore.educational_settings_List().size() == 0) textView.setVisibility(View.VISIBLE);				
				else textView.setVisibility(View.INVISIBLE);
	        }
	    });
	}
	
	
	
	
	
	
	public void getEducationalSettings(){
		Log.d(LOGTAG, "getEducationalSettings");
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	        	new GetEducationalSettings().start();
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
            	intent.putExtra("model", "EducationalSetting");
            	intent.putExtra("id", educationalSettingsStore.get(position).id);
            	intent.putExtra("title", educationalSettingsStore.get(position).title);
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
    
    
    
  
    
    public void get_educationalSettings(){	
    	Log.d(LOGTAG, "get_educationalSettings");
    	if ((downloadImages != null)&&(!downloadImages.isCancelled())) downloadImages.cancel(true);
		educationalSettingsStore.deleteList();
		scroll.setTotalCount(scroll.getPageElements());
		scroll.setPage(1);
		
		progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando contextos educativos del sistema...");
		new GetEducationalSettings().start();
	}
	
	
	
	
	
    public void reloadOnUiThread(final String toolsType){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	            get_educationalSettings();
	        }
	    });
	}
    
    
  
	
	
	public void restart(){
		new GetEducationalSettings().start();
	}
	
	
	
	
	
	public void search_educational_settings(String search, int page, int educs_number){
		educationalSettingsStore.deleteList();
		new SearchEducationalSettings(search, page, educs_number).start();
	}
    
	
	
	
	
	
	public void updateEducationalSettingsList(){
		Log.d(LOGTAG, "updateEducationalSettingsList");
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	        	         		        	
	            adapter.notifyDataSetChanged();
	        }
	    });
	}
	
	
	

    
  
    	class DownloadEducationalSettingImages extends AsyncTask<Void, Void, Void> {
            protected Void doInBackground(Void... toolsList) {
        	HttpClient cliente = new DefaultHttpClient();
        	for(int i=0;i<educationalSettingsStore.size();i++){
        		if (educationalSettingsStore.get(i).getImageUrl() != null){
        			if (educationalSettingsStore.get(i).getImageUrl().equals("none")==false){
        				try {
        					String BaseUrlPage = new IP().ip+educationalSettingsStore.get(i).getImageUrl();
        					System.out.println("Url imagen educationalSetting");
        					System.out.println(BaseUrlPage);
        					HttpGet solicitud = new HttpGet(BaseUrlPage);	
        					SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        					solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
        					HttpResponse respuesta = cliente.execute(solicitud);
        					if (isCancelled()) break;
        					HttpEntity entity = respuesta.getEntity();
        					Bitmap loadedImage = BitmapFactory.decodeStream(entity.getContent());
        					educationalSettingsStore.setBitmapImage(educationalSettingsStore.get(i).getId(), loadedImage);  
        				} catch (ClientProtocolException e) {
        					e.printStackTrace();
        				} catch (IOException e) {
        					e.printStackTrace();
        				}	
        			}
        		}
        	}
       		updateEducationalSettingsList();
       		return null;
       	}
	}       


    
    
    
    public class GetEducationalSettings extends Thread {
    	List<EducationalSetting> educationalSettingsList;
    	List<Integer> idsList;
	
	
	
    	@Override 
    	public void run() {
    	
    		try {
    			Log.d(LOGTAG, "GetEducationalSettings");
    			HttpClient cliente = new DefaultHttpClient(); 
        	
    			// Url devices
    			String url_educational_settings = null;
    			
    			url_educational_settings =  new IP().ip+"/"+language.getStringLanguage()+"/educationalSettings.json?owner=true";
    			HttpGet solicitud = new HttpGet(url_educational_settings);
    			SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
    			solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
    			HttpResponse respuesta = cliente.execute(solicitud);
    			HttpEntity entity = respuesta.getEntity();
    			idsListOwnerTrue = new Funciones().readJsonIdsStream(entity);
    			
    			url_educational_settings =  new IP().ip+"/"+language.getStringLanguage()+"/educationalSettings.json";
    			solicitud = new HttpGet(url_educational_settings);solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
    			respuesta = cliente.execute(solicitud);
    			entity = respuesta.getEntity();
    			idsList = new Funciones().readJsonIdsStream(entity);
		        
    			// PRUEBA		        
    			/*respuesta = cliente.execute(solicitud);
    			String json = EntityUtils.toString(respuesta.getEntity());
    			System.out.println("JSON WHOLE VIEW EducationalSETTINGS IDS: "+json);*/
    			// FIN DE PRUEBA
				
    			url_educational_settings =  new IP().ip+"/"+language.getStringLanguage()+"/educationalSettings/getWholeView.json?id=";
    			for (int i=0; i<idsList.size(); i++){
    				url_educational_settings +=  idsList.get(i);
    				if (i<idsList.size()-1) url_educational_settings += ",";        		
    			}    				    				    				    				    				    				    	   			
    			
    			   			
    			System.out.println("Url educational settings: "+ url_educational_settings);
    			solicitud = new HttpGet(url_educational_settings);
    			preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
    			solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
	        
    			// PRUEBA		        
    			/*respuesta = cliente.execute(solicitud);
    			json = EntityUtils.toString(respuesta.getEntity());
    			System.out.println("JSON WHOLE VIEW EDUCATIONAL SETTING: "+json);*/
    			// FIN DE PRUEBA
			
    			respuesta = cliente.execute(solicitud);
    			entity = respuesta.getEntity();
    			
    			educationalSettingsList = new ArrayList<EducationalSetting>();
    			educationalSettingsList = new Funciones().readJsonESStream(entity);	
    			
    			// Guardar technical settings de este sistema
    			for (int i=0;i<educationalSettingsList.size();i++) educationalSettingsStore.saveEducationalSetting(educationalSettingsList.get(i));        		
    			
			    // IMAGES
    			if (educationalSettingsStore.size()>0) downloadEducationalSettingImages();				
			
    			finProgressBar();
    			updateEducationalSettingsList();
    		} catch (ClientProtocolException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}				
    	}        
    }




    
    public class EducationalSettingsAdapter extends BaseAdapter {
    	private final Activity actividad;
    	private final ArrayList<EducationalSetting> lista;
    
   
    
    	public EducationalSettingsAdapter(Activity actividad, ArrayList<EducationalSetting> educationalSettings) {
    		super();
    		this.actividad = actividad;
    		this.lista = educationalSettings;
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





    public class SearchEducationalSettings extends Thread {	
        String search;
        int page, educs_number;
            
        public SearchEducationalSettings(String search, int page, int educs_number) {
        	progressDialog = createProgressDialog("Buscando contextos educativos...");
            this.search = search;
            this.page = page;
            this.educs_number = educs_number;
        }

        @Override 
        public void run() { 
    		try {
    			Log.d("EducationalSettingsFragment", "SearchEducationalSettings");       	
    	       	String url =  new IP().ip+"/"+language.getStringLanguage()+"/educationalSettings/search.json";
    	   		         	         			
    				if (this.search.equals("") == false){
    					url+="?search="+this.search;
    					if (this.page > 0) url+="&page="+this.page;        			
        				if (this.educs_number > 0) url+="&educs_number="+this.educs_number;  
    				}
    				else{
    					if (this.page > 0){
    						url+="?page="+this.page;        		
    						if (this.educs_number > 0) url+="&educs_number="+this.educs_number; 
    					}
    					else if (this.educs_number > 0) url+="educs_number="+this.educs_number; 
    				}
      			  		    						    				
    			HttpGet httpGet = new HttpGet(url);    			
    			SharedPreferences preferences = getActivity().getSharedPreferences( "datos", Context.MODE_PRIVATE);
    	       	httpGet.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
    			HttpClient httpClient = new DefaultHttpClient(); 
    			
    			// PRUEBA	
    			/*Log.e(LOGTAG, "url: " + url);
    			HttpResponse respuestaPrueba = httpClient.execute(httpGet);
    			System.out.println("JSON WHOLE VIEW Educational SETTING: "+EntityUtils.toString(respuestaPrueba.getEntity()));*/
    			// FIN DE PRUEBA
    			
    			Log.e(LOGTAG, "url: " + url);
				HttpResponse httpResponse = httpClient.execute(httpGet);				
				HttpEntity httpEntity = httpResponse.getEntity();
    			if (httpEntity != null){
    				List<Integer> idsList = new Funciones().readJsonIdsStream(httpEntity);
    				String url_educational_settings =  new IP().ip+"/"+language.getStringLanguage()+"/educationalSettings/getWholeView.json?id=";
        			for (int i=0; i<idsList.size(); i++){
        				url_educational_settings +=  idsList.get(i);
        				if (i<idsList.size()-1) url_educational_settings += ",";        		
        			}    				    				    				    				    				    				    	   			
        			
        			// EDUCATIONAL SETTINGS   			
        			System.out.println("Url educational settings: "+ url_educational_settings);
        			httpGet = new HttpGet(url_educational_settings);
        			preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        			httpGet.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
    			
        			httpResponse = httpClient.execute(httpGet);
        			httpEntity = httpResponse.getEntity();
        			
        			ArrayList<EducationalSetting> educationalSettingsList = new ArrayList<EducationalSetting>();
        			educationalSettingsList = (ArrayList<EducationalSetting>) new Funciones().readJsonESStream(httpEntity);	
        			
        			// Guardar technical settings de este sistema
        			for (int i=0;i<educationalSettingsList.size();i++) educationalSettingsStore.saveEducationalSetting(educationalSettingsList.get(i));        		
        			
    			    // IMAGES
        			if (educationalSettingsStore.size()>0) downloadEducationalSettingImages();				
    			
        			finProgressBar();
        			updateEducationalSettingsList();
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


