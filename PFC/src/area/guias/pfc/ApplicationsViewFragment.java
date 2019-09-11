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
import android.widget.TextView;

public class ApplicationsViewFragment extends Fragment{
	public ApplicationsListAdapter applicationsAdapter;
	public ApplicationsStore applicationsStore = new ApplicationsArray();
	private TechnicalSetting technical_setting = new TechnicalSetting();
	private ProgressDialog progressDialog;
	private View rootView;
	private String applicationsType;
	private static final String LOGTAG = "LogsAndroid";
	private final static int MINIVIEW = 1, SMALLVIEW = 2, WHOLEVIEW = 3;
	public DownloadApplicationsImages downloadImages;
	private String from_activity;
	
	// SCROLL VARIABLES
	Scroll scroll;
	
	// LANGUAGE
	Language language;
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		applicationsAdapter = new ApplicationsListAdapter(getActivity(), applicationsStore.listaAplicaciones());
		GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
		gridView.setAdapter(applicationsAdapter); 
		
		gridView.setOnItemClickListener(new OnItemClickListener() {	     
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), DeviceView.class);
		    	intent.putExtra("titulo", applicationsStore.getApplication(position).name);
		    	intent.putExtra("imageUrl", applicationsStore.getApplication(position).imageUrl);
		    	intent.putExtra("description", applicationsStore.getApplication(position).description);
			    startActivity(intent);
			}
	    });
		
		technical_setting.id = getActivity().getIntent().getExtras().getInt("technical_setting_id");			
		
		language = getActivity().getIntent().getParcelableExtra("Language");
		
		from_activity = getActivity().getIntent().getStringExtra("from_activity");
		
		scroll = new Scroll();
              
        applicationsStore.borrarLista();    
		
		downloadImages = null;
		
		if( savedInstanceState != null ) {
        	
        	scroll = savedInstanceState.getParcelable("scroll");
        	configureListViewScroll();
        	
        	applicationsType = savedInstanceState.getString("applicationsType");    
        	
        	for (int i=0; i< savedInstanceState.getParcelableArrayList("applicationsList").size(); i++)
        		applicationsStore.listaAplicaciones().add((Application) savedInstanceState.getParcelableArrayList("applicationsList").get(i));      	
        	
        	if (applicationsStore.listaAplicaciones().size() > 0) {
        		changeVisibilityOfGridView(View.VISIBLE);
    			changeVisibilityOfEmptyView(View.INVISIBLE);
        	} else {
        		changeVisibilityOfGridView(View.INVISIBLE);
    			changeVisibilityOfEmptyView(View.VISIBLE);
        	}
        	
        	updateApplicationsList();
        }
		else{
			
			changeVisibilityOfGridView(View.INVISIBLE);
			changeVisibilityOfEmptyView(View.INVISIBLE);
			
			configureListViewScroll(); 
	        
			applicationsType = "user_applications";
			getApplications(applicationsType);
			
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
	        rootView = inflater.inflate(R.layout.applications_view_fragment, container, false);
	    } catch (InflateException e) {
	    	
	    }		
	    return rootView;
	}
	
	
	
	

	@Override
	public void onSaveInstanceState(Bundle outState) {
	   super.onSaveInstanceState(outState);
	   outState.putParcelable("scroll", scroll);
	   outState.putString("applicationsType", applicationsType);
	   outState.putParcelableArrayList("applicationsList", applicationsStore.listaAplicaciones());	 
	}
	
	
	
	
	
	public void addApplications(boolean finish){		
		int numApplications2Add=countApplicationsToAdd();
    	int numApplicationsAdded=countApplicationsAdded();
    	configureDialog(numApplications2Add, numApplicationsAdded, finish);      	
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
			        searchApplications(scroll.getSearch(), scroll.getPage(), scroll.getPageElements());		            
			     }
			}
		});
	}
	
	
	
	
	
	private void configureDialog(final int numApplications2Add, int numApplicationsAdded, final boolean finish){
		List<Application> AppList = applicationsStore.listaAplicaciones();
		
		final Dialog dialog = new Dialog(getActivity()); 		
		dialog.setCancelable(true);		
		dialog.setTitle("Advertencia");
		
		if (numApplications2Add > 0){		
			
			dialog.setContentView(R.layout.dialog_two_button_layout);
			TextView msg = (TextView) dialog.findViewById(R.id.message);
			
			if (numApplications2Add > 1){
				if (finish == false) msg.setText("¿Quieres añadir estas aplicaciones?");
				if (finish == true) msg.setText("¿Quieres añadir estas aplicaciones antes de salir?");
			}
			else if (numApplications2Add == 1){
				if (finish == false) msg.setText("¿Quieres añadir esta aplicación?");	
				if (finish == true) msg.setText("¿Quieres añadir esta aplicación antes de salir?");	
			}
			else Log.e(LOGTAG, "Número de aplicaciones para añadir menor que cero.");
			
			Button buttonYes = (Button) dialog.findViewById(R.id.yes);
			buttonYes.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
					new AddInvolvements(numApplications2Add).start();
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
			
			if (numApplications2Add == 0) msg.setText("¡No tienes ninguna aplicación marcada!");
			else if (numApplicationsAdded == AppList.size()) msg.setText("¡Ya tienes todas las aplicaciones!");
			
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
	
	
	
	
	
	public int countApplicationsAdded(){
		List<Application> applicationsList = applicationsStore.listaAplicaciones();
		int numApplicationsAdded=0;
		for (int i=0; i<applicationsList.size();i++)			
			if ((applicationsList.get(i).checkedString.equals("Añadido")))
					numApplicationsAdded++;
		
		return numApplicationsAdded;
	}
	
	
	
	
	
	public int countApplicationsToAdd(){
		List<Application> applicationsList = applicationsStore.listaAplicaciones();
		int numApplicationsToAdd=0;
		for (int i=0; i<applicationsList.size();i++){
			if ((applicationsList.get(i).checked == true)&&(applicationsList.get(i).checkedString.equals("Añadir")))
					numApplicationsToAdd++;
		}
		return numApplicationsToAdd;
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
			        	if (applicationsType.equals("user_applications")) getApplications(applicationsType);
			        }
			    });
		
		return dialog;
	}
	
	
	
	
	
	protected void downloadApplicationsImages(){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {
	        	downloadImages = new DownloadApplicationsImages();
	        	downloadImages.execute();
	        }
	    });
	}
	
	
	
	
	
	protected void finProgressBar(){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {
	        	progressDialog.dismiss();	  
	        	updateApplicationsList();
	        	GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
				gridView.setVisibility(View.VISIBLE);
				TextView textView = (TextView) getActivity().findViewById(android.R.id.empty);
				if (applicationsStore.listaAplicaciones().size()==0) textView.setVisibility(View.VISIBLE);
				else if (applicationsStore.listaAplicaciones().size() > 0) textView.setVisibility(View.INVISIBLE);
	        }
	    });
	}
	
	
	
	
	
	public List<Integer> getApplicationsIds() throws ClientProtocolException, IOException{
		Log.d(LOGTAG, "getApplicationsIds");
		HttpClient cliente = new DefaultHttpClient();
	    String BaseUrlPage = new IP().ip+"/" + language.getStringLanguage() + "/applications.json?applications_number=" + scroll.getPageElements() + "&page=" + scroll.getPage();
	    HttpGet solicitud = new HttpGet(BaseUrlPage);	
	    SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
	    solicitud.setHeader("Cookie", "_AREA_v0.1_session="+ preferences.getString("Cookie", "0"));		    
		HttpResponse respuesta = cliente.execute(solicitud);
		HttpEntity entity = respuesta.getEntity();
		List<Integer> IdList = new Funciones().readJsonIdsStream(entity);
		return IdList;
	}
	
	
	
	
	
	public void getApplications(final String appType){	
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {
	        	Log.d(LOGTAG, "getApplications");
	        	if ((downloadImages != null)&&(!downloadImages.isCancelled())) downloadImages.cancel(true);
	        	applicationsStore.borrarLista();
	        	scroll.setTotalCount(scroll.getPageElements());
	        	scroll.setPage(1);
	        	if (appType.equals("user_applications")){
	        		applicationsType = "user_applications";
	        		progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando aplicaciones de la guía...");
	        		new GetApplications("user_applications", scroll.getPage(), scroll.getPageElements()).start();
	        	}
	        	else{
	        		applicationsType = "system_applications";
	        		progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando aplicaciones del sistema...");
	        		new GetApplications("system_applications", scroll.getPage(), scroll.getPageElements()).start();
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
	
	
	
	
	
	public List<Application> getWholeViewApplications(List<Integer> IdList) throws ClientProtocolException, IOException{
		Log.d(LOGTAG, "getWholeViewApplications");
		String BaseUrlPage = new IP().ip + "/" + language.getStringLanguage() + "/applications/getWholeView.json?id=";
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
    	List<Application> AppList = new Funciones().readJsonAppStream(entity);
    	
    	// PRUEBA
    	respuesta = cliente.execute(solicitud);
    	System.out.println(EntityUtils.toString(respuesta.getEntity()));
    	// PRUEBA
    	
    	System.out.println(BaseUrlPage);
    	return AppList;
	}
	
	
	
	
	
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
	
	
	
	
	
	public int numSelectedApplications(){
		int numApplicationsChecked=0;
		List<Application> AppList = applicationsStore.listaAplicaciones();
		for (int i=0;i<AppList.size();i++){
			if (AppList.get(i).checked == true) numApplicationsChecked++;
		}
		return numApplicationsChecked;
	}
	
	
	
	
	
	public void removeApplications(final boolean finish){
		// Mirar el numero de dispositivos a eliminar
		int numApplicationsChecked = numSelectedApplications();
		
		final Dialog dialog = new Dialog(getActivity());
		dialog.setTitle("Advertencia");
		dialog.setCancelable(false);
		
		
		if (numApplicationsChecked == 0){
			dialog.setContentView(R.layout.dialog_one_button_layout);
			TextView msg = (TextView) dialog.findViewById(R.id.message);
			
			msg.setText("Tienes que marcar alguna aplicación para eliminarla.");
			Button buttonYes = (Button) dialog.findViewById(R.id.ok);
			buttonYes.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
					dialog.cancel();
				}
			});  	
		}
		else if (numApplicationsChecked>0){
			
			dialog.setContentView(R.layout.dialog_two_button_layout);
			TextView msg = (TextView) dialog.findViewById(R.id.message);
			
			if (numApplicationsChecked==1){
				if (finish == false) msg.setText("¿Estás seguro de que quieres eliminar esta aplicación de tu contexto técnico?");
				if (finish == true) msg.setText("¿Quieres eliminar esta aplicación de tu contexto técnico antes de salir?");
			}
			else if (numApplicationsChecked > 1){
				if (finish == false) msg.setText("¿Estás seguro de que quieres eliminar estas aplicaciones de tu contexto técnico?");
				if (finish == true) msg.setText("¿Quieres eliminar estas aplicaciones de tu contexto técnico antes de salir?");
			}
			
			Button buttonYes = (Button) dialog.findViewById(R.id.yes);
			buttonYes.setOnClickListener(new OnClickListener() {
		        @Override
		        public void onClick(View v) {
				   int numAppChecked = numSelectedApplications();				   				   
				   new DeleteInvolvements(numAppChecked).start();					   				   
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
		   	      if (finish == true) {
		   	    	  getActivity().finish();
		   	      }
			   }
	    	});  	
		}		  	
		dialog.show();		
		
    }
	
	
	
	
	
	public void searchApplications(String search, int page, int applications_number){
		if (page == 1) scroll.setPage(1);
		new SearchApplications(search, page, applications_number).start();
	}
	
	
	
	
	
	public void updateApplicationsList(){
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {	    

        		System.out.println("ACTUALIZAR LISTA APLICACIONES");
	        	for (int i=0;i<applicationsStore.listaAplicaciones().size();i++){
            		System.out.println("name: " + applicationsStore.getApplication(i).name);
            		System.out.println("description: " + applicationsStore.getApplication(i).description);
            		System.out.println("short description: " + applicationsStore.getApplication(i).short_description);
            	}
	            applicationsAdapter.notifyDataSetChanged();
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
            	intent.putExtra("model", "Application");
            	intent.putExtra("id", applicationsStore.getApplication(position).id);
            	intent.putExtra("title", applicationsStore.getApplication(position).name);
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
    	int num_applications_checked;
    	
    	public AddInvolvements(int num_applications_checked){
    		this.num_applications_checked = num_applications_checked;
    	}
    	
        @Override 
        public void run() {
        	try {
        		HttpClient cliente = new DefaultHttpClient();
            	String BaseUrlPage = new IP().ip+"/"+language.getStringLanguage() +"/technicalSettings/addInvolvements.json?id="+technical_setting.id+"&applications=";
            	
				List<Application> AppList = applicationsStore.listaAplicaciones();
				
				if (num_applications_checked != 0){
					for (int i=0; i<AppList.size();i++){
						if ((AppList.get(i).checked == true)&&(AppList.get(i).checkedString.equals("Añadir"))){	
							num_applications_checked--;
							BaseUrlPage+=AppList.get(i).id;
							
							if (from_activity.equals("GuideViewTabs")) GuideViewTabs.guide.technical_setting.applications_id.add(AppList.get(i).id);
							else TechnicalSettingView.technical_setting.applications_id.add(AppList.get(i).id);
							
							if (num_applications_checked > 0) BaseUrlPage += ",";
							AppList.get(i).checkedString = "Añadido";
							applicationsStore.listaAplicaciones().get(i).checkedString = "Añadido";
						}						
					}
					HttpPut solicitud = new HttpPut(BaseUrlPage);
					SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
					solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
					Log.d("ApplicationsViewFragment", "url: " + BaseUrlPage);					
					HttpResponse respuesta = cliente.execute(solicitud);
					
					Log.d(LOGTAG, EntityUtils.toString(respuesta.getEntity()));
					updateApplicationsList();
					Looper.prepare();
        				Dialog dialog = new Funciones().create_ok_dialog(getActivity(), "Advertencia", "Las aplicaciones han sido añadidas");
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
        	updateApplicationsList();
        }        
    }
    
    
    
    
    
    class DeleteInvolvements extends Thread {
        int numApplicationsChecked;
		public DeleteInvolvements(int numApplicationsChecked){
			this.numApplicationsChecked=numApplicationsChecked;
		}
		
        @Override 
        public void run() {        	
			try {				
				HttpClient cliente = new DefaultHttpClient();
				List<Application> AppList = applicationsStore.listaAplicaciones();
				String BaseUrlPage = new IP().ip+"/"+language.getStringLanguage()+"/technicalSettings/deleteInvolvements.json?id="+technical_setting.id+"&applications=";
				for (int i=0;i<AppList.size();i++){
					if (AppList.get(i).checked==true){
						this.numApplicationsChecked--;
						if (this.numApplicationsChecked==0) BaseUrlPage += AppList.get(i).id;
						else BaseUrlPage += AppList.get(i).id + ",";
						
						if (from_activity.equals("GuideViewTabs")){
							for (int j=0; j<GuideViewTabs.guide.technical_setting.applications_id.size(); j++)
								if (GuideViewTabs.guide.technical_setting.applications_id.get(j) == AppList.get(i).id)
									GuideViewTabs.guide.technical_setting.applications_id.remove(j);
						}
						else{
							for (int j=0; j<TechnicalSettingView.technical_setting.applications_id.size(); j++)
								if (TechnicalSettingView.technical_setting.applications_id.get(j) == AppList.get(i).id)
									TechnicalSettingView.technical_setting.applications_id.remove(j);
						}
					}
				}
				HttpDelete solicitudBorrar = new HttpDelete(BaseUrlPage);
				SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
				solicitudBorrar.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				System.out.println("Url para borrar aplicaciones: "+BaseUrlPage);
				HttpResponse respuesta = cliente.execute(solicitudBorrar);	
				Log.d(LOGTAG, "Respuesta: " + EntityUtils.toString(respuesta.getEntity()));
				
				Looper.prepare();
    				Dialog dialog = create_ok_dialog(getActivity(), "Advertencia", "Las aplicaciones han sido borradas");
    				dialog.show();
    			Looper.loop();
				
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
			updateApplicationsList();
        }        
    }
    
    
    
    
    
    public class ApplicationsListAdapter extends BaseAdapter {
		private final Activity actividad;
	    private final ArrayList<Application> lista;
	    
	   
	    
	    public ApplicationsListAdapter(Activity actividad, ArrayList<Application> applications) {
	        super();
	        this.actividad = actividad;
	        this.lista = applications;
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
					if (applicationsType.equals("user_applications")){
						if (isChecked) applicationsStore.listaAplicaciones().get(position).checked = true;
						else applicationsStore.listaAplicaciones().get(position).checked = false;
					}
					else {
						if (isChecked) applicationsStore.listaAplicaciones().get(position).checked = true;
						else if (!applicationsStore.listaAplicaciones().get(position).checkedString.equals("Añadido")) lista.get(position).checked = false;		
					}
				}
			 });
			
			 if (applicationsStore.listaAplicaciones().get(position).checked == true) view.setChecked(true);
			 else view.setChecked(false);
			 
			 if (applicationsType.equals("user_applications")) view.setCheckboxText("Eliminar");
			 else{						 
		         if (lista.get(position).checkedString.equals("Añadido")) view.setCheckboxText("Añadido");
		         else if (lista.get(position).checkedString.equals("Añadir")) view.setCheckboxText("Añadir");
			 }
			 
	         if (applicationsStore.listaAplicaciones().get(position).name != "") view.setTitle(lista.get(position).name);
	         else view.setTitle("");
	         
	         if (applicationsStore.listaAplicaciones().get(position).description != "") view.setDescription(lista.get(position).getShortDescription());
	         else view.setDescription("");
	         
	         if (applicationsStore.listaAplicaciones().get(position).image != null){
	        	 view.setImage(ImageHelper.getRoundedCornerBitmap(lista.get(position).image, 12, this.actividad));
	         }
	         else{ // Imagen por defecto (NO IMAGE)
	        	 Bitmap bm = BitmapFactory.decodeResource(this.actividad.getResources(), R.drawable.imagen);
	        	 view.setImage(ImageHelper.getRoundedCornerBitmap(bm, 12, this.actividad));
	         }
	         return view;
		}	
	}
    
    
    
    
    
    class DownloadApplicationsImages extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... activityList) {
        	HttpClient cliente = new DefaultHttpClient();
        	List<Application> AppList = applicationsStore.listaAplicaciones();
        	for(int i=0;i<AppList.size();i++){
        		if (AppList.get(i).imageUrl != null){
        			if (AppList.get(i).imageUrl.equals("none")==false){
        				try {
        					String BaseUrlPage = new IP().ip+AppList.get(i).imageUrl;
        					System.out.println("Url imagen aplicación");
        					System.out.println(BaseUrlPage);
        					HttpGet solicitud = new HttpGet(BaseUrlPage);	
        					SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
        					solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
        					if (isCancelled()) break;
        					HttpResponse respuesta = cliente.execute(solicitud);
        					HttpEntity entity = respuesta.getEntity();
        					Bitmap loadedImage = BitmapFactory.decodeStream(entity.getContent());
        					if (loadedImage != null) applicationsStore.setImage(AppList.get(i).id, loadedImage);        					   							
        				} catch (ClientProtocolException e) {
        					e.printStackTrace();
        				} catch (IOException e) {
        					e.printStackTrace();
        				}	
        			}
        		}
        	}
       		updateApplicationsList();
       		return null;
       	}
	}       
    
    
    
    
    
    public class GetApplications extends Thread {
		String appsType;
		int page, applications_number;
		
		public GetApplications(String devicesType, int page, int applications_number){
			this.appsType = devicesType;
			this.page = page;
			this.applications_number = applications_number;
		}
		
        @Override 
        public void run() {
            try {	 
            	Log.d(LOGTAG, "GetApplications");
            	List<Application> AppList = null;
            	// CONSEGUIR LISTA DE APLICACIONES PROPIAS DE ESTE TSETTING
            	List<TechnicalSetting> tsettingList = getWholeViewTechnicalSettings(technical_setting.id);
            	technical_setting = tsettingList.get(0);
            	
            	if (appsType.equals("system_applications")){	
            		// CONSEGUIR LISTA DE APLICACIONES DEL SISTEMA
    				List<Integer> IdList = getApplicationsIds();
    				AppList = getWholeViewApplications(IdList);
    				
    				for (int i=0; i<AppList.size(); i++){
    	        		for (int j=0; j<tsettingList.get(0).applications_id.size(); j++){
    		        		if (tsettingList.get(0).applications_id.get(j) == AppList.get(i).id){
    		        			AppList.get(i).checked = true;
    		        			AppList.get(i).checkedString = "Añadido";
    		        		}
    		        	}	 
    	        	}	
            	} else if (appsType.equals("user_applications")){
            		AppList = getWholeViewApplications(technical_setting.applications_id);
            	} else Log.e(LOGTAG, "Tipo de aplicaciones desconocido.");
           					
            	// Guardar dispositivos de esta guía
            	for (int i=0;i<AppList.size();i++)
            		if (AppList.get(i).id != -1) applicationsStore.guardarAplicacion(AppList.get(i));				
            		
            	// Cada dispositivo tendrá una imagen
            	if (AppList.size()>0) downloadApplicationsImages();					
            	
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
        	String url = new IP().ip + "/" + this.language + "/applications/" + typeOfView + ".json?" + this.urlIds; 
        	HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
			try {
				HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
				
				List<Application> applicationList = new Funciones().readJsonAppStream(httpResponse.getEntity());
				
				for (int i=0;i<applicationList.size();i++) applicationsStore.guardarAplicacion(applicationList.get(i));
						 				
				downloadApplicationsImages();				
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
    
    
    
    
    
	public class SearchApplications extends Thread {	
        String search;
        int page, applications_number;
            
        public SearchApplications(String search, int page, int applications_number) {
        	progressDialog = new Funciones().createProgressDialog(getActivity(), "Cargando aplicaciones...");
            this.search = search;
            this.page = page;
            this.applications_number = applications_number;
        }

        @Override 
        public void run() { 
    		try {
    			System.out.println("SearchApplications"); 
    			scroll.setMethod(Scroll.getMethodSearch());
    			scroll.setSearch(search);
    			if (scroll.getPage() == 1) applicationsStore.borrarLista();			
    			
    			String url = makeUrlApplications(search, page, applications_number);   			
    			
    			HttpGet httpGet = new HttpGet(url);
    			SharedPreferences preferences = getActivity().getSharedPreferences( "datos", Context.MODE_PRIVATE);
    	       	httpGet.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
    			
    	        //PRUEBA
    	       	System.out.println(url);
				HttpResponse httpResponsePrueba = new DefaultHttpClient().execute(httpGet);
				if (httpResponsePrueba.getEntity() != null) System.out.println(EntityUtils.toString(httpResponsePrueba.getEntity()));
				// FIN DE PRUEBA
				
    	       	HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);    			
    			HttpEntity httpEntity = httpResponse.getEntity();
    			
    			
    			if (httpEntity != null){
    				
    				List<Integer> listInteger = new Funciones().readJsonIdsStream(httpResponse.getEntity());
    				String urlIds = null;
    				if (applicationsType.equals("user_applications")){
    					// Mirar si algún id devuelto por "search" coincide con alguno id del contexto técnico
    					List<Integer> newList = new ArrayList<Integer>();
    					for (int i=0; i<technical_setting.applications_id.size(); i++){
    						for (int j=0; j<listInteger.size(); j++){
    							if (listInteger.get(j) == technical_setting.applications_id.get(i)){
    								newList.add(listInteger.get(j));
    							}
    						}	   					
    					}
    					urlIds = new Funciones().makeUrlIds(newList);
    				}
    				else if (applicationsType.equals("system_applications")){
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


