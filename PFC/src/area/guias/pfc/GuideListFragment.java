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
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.DisplayMetrics;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class GuideListFragment extends Fragment{
	public GuidesAdapter ListGuideAdapter;
	public GuidesStore guidesStore = new GuidesArray();	
	private String guidesType;
	private View rootView;
	public static ProgressDialog progressDialog;
	private final static int MINIVIEW = 1, SMALLVIEW = 2, WHOLEVIEW = 3;
	private static final int YES = 1;
	public DownloadImages downloadImages;
	public SearchGuides search_guides;
	public GetTypeOfView getTypeOfView;
	
	// Scroll
	private Scroll scroll;
	
	// LANGUAGE
	private Language language;
	
	private static final String LOGTAG = "LogsAndroid";
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		Configuration configuration = getResources().getConfiguration();
	    onConfigurationChanged(configuration);
	    
		GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
		ListGuideAdapter = new GuidesAdapter(getActivity(), guidesStore.listaGuias());  
		gridView.setAdapter(ListGuideAdapter);			
		
		gridView.setOnItemClickListener(new OnItemClickListener() {	     
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(getActivity(), GuideViewTabs.class);
		    	intent.putExtra("guide", guidesStore.getGuide(position));
		    	intent.putExtra("language", language);
			    startActivityForResult(intent, 1);
			}
	    });
		
		scroll = new Scroll();
        
        language = new Language();
        language = getActivity().getIntent().getParcelableExtra("language");
        
        if( savedInstanceState != null ) {
        	Log.d("GuideListFragment", "savedInstanceState != null");      	
        	scroll = savedInstanceState.getParcelable("scroll");
        	
        	guidesType = savedInstanceState.getString("guidesType");    
        	
        	for (int i=0; i< savedInstanceState.getParcelableArrayList("guidesList").size(); i++)
        		guidesStore.listaGuias().add((Guide) savedInstanceState.getParcelableArrayList("guidesList").get(i));      	
        	
        	if (guidesStore.listaGuias().size() > 0) {
        		changeVisibilityOfGridView(View.VISIBLE);
    			changeVisibilityOfEmptyView(View.INVISIBLE);
        	} else {
        		changeVisibilityOfGridView(View.INVISIBLE);
    			changeVisibilityOfEmptyView(View.VISIBLE);
        	}
        	
        	updateList();     	
        }
		else{
			Log.d("GuideListFragment", "savedInstanceState = null");
			changeVisibilityOfGridView(View.INVISIBLE);
			changeVisibilityOfEmptyView(View.INVISIBLE);

	        configureGuideScroll();
	        
	        guidesType = "none";
	        
	        refresh();
	        
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
    	        rootView = inflater.inflate(R.layout.guide_list_fragment, container, false);
    	    } catch (InflateException e) {
    	    	
    	    }		
    	    return rootView;
	}
	

	
	
	
	@SuppressLint("NewApi")
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
		Log.d("NavigationDrawer", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        
        DisplayMetrics metrics = new DisplayMetrics();
    	getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int dpi = metrics.densityDpi; // densidad de pixeles por pulgada
        Log.d("NavigationDrawer", "Dpi: " + dpi);
        Log.d("NavigationDrawer", "ancho en dp: " + newConfig.screenWidthDp);
        // 1dp = tamaño de 1 pixel en una pantalla de 160dpi
        int screenWidthPixels = newConfig.screenWidthDp*dpi/160;
        Log.d("NaigationDrawer", "ancho en pixeles: " + screenWidthPixels);
        
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        	RelativeLayout rl = (RelativeLayout) rootView.findViewById(R.id.guides_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_guias));
                    
            GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
            // 300dp ocupará el ancho de 1 item
            gridView.setNumColumns(newConfig.screenWidthDp/300);
                  
        } 
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	RelativeLayout rl = (RelativeLayout) rootView.findViewById(R.id.guides_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_land_guias));
        	
        	 GridView gridView = (GridView) rootView.findViewById(R.id.gridView);
             gridView.setNumColumns(newConfig.screenWidthDp/300);
        }
        
        
    }
	
	
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
	    super.onActivityResult(requestCode, resultCode, data);
	    Log.d("result", "onActivityResult");	  
	    if (data.getIntExtra("restart", -1) == YES) refresh();	    	
	}
	
	
	
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	   Log.d("NavigationDrawer", "onSaveInstanceState");
	   super.onSaveInstanceState(outState);
	   outState.putParcelable("scroll", scroll);
	   outState.putString("guidesType", guidesType);
	   outState.putParcelableArrayList("guidesList", guidesStore.listaGuias());	   
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
    
	
	
	
	
    public void checkEmptyView(){
    	Log.d(LOGTAG, "checkEmptyView");
    	getActivity().runOnUiThread(new Runnable() {
			public void run() {
				TextView textView = (TextView) getActivity().findViewById(android.R.id.empty);
				if (guidesStore.listaGuias().size() == 0) textView.setVisibility(View.VISIBLE);
				else textView.setVisibility(View.INVISIBLE);
			}
		});
	}
	
    
    
    
    
	public void configureGuideScroll(){
		Log.d(LOGTAG, "configureGuideScroll");
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
			        searchGuides("", guidesType, scroll.getPage(), scroll.getPageElements(), language.getStringLanguage());		         
			    }
			}
		});
	}
	
	
	
	
	
	public Dialog create_finish_dialog(final Context context, String title, String message){
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
	        	refresh();
	        }
	    });
		
		return dialog;
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
	
	
	
	
	
	protected void downloadImages(final List<Guide> guideList){
		Log.d("GuideListFragment", "downloadImages");
		getActivity().runOnUiThread(new Runnable() {
			@SuppressWarnings("unchecked")
			public void run() {
				Log.d(LOGTAG, "downloadImages");
				downloadImages = new DownloadImages();
				downloadImages.execute(guideList);
			}
		});
	}
	
	
	
	
	
	protected void finProgressBar(){
		Log.d("GuideListFragment", "finProgressBar");
		getActivity().runOnUiThread(new Runnable() {
	        public void run() {
	        	Log.d(LOGTAG, "finProgressBar");
	        	progressDialog.dismiss();	  	        	
	        }
	    });
	}
	
	
	
	
	
	public void getTypeOfView(String language, String urlIds, int typeOfView){
		Log.d(LOGTAG, "getTypeOfView");
		getTypeOfView = new GetTypeOfView(language, urlIds, typeOfView);
		getTypeOfView.execute();
	}
	
	
	
	
	
    private OnClickListener mCloseSearchBarClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.linearLayout_search_guides);
        	linearLayout.setVisibility(View.GONE);
        }
    };
    
    
    
    
    
    private OnClickListener mSearchBarClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	EditText editText = (EditText) rootView.findViewById(R.id.editText_guides_view);
        	String search = editText.getText().toString();
        	
        	scroll.setPage(1);
    		scroll.setTotalCount(scroll.getPageElements()); 
    		guidesStore.borrarLista();
    		
        	searchGuides(search, guidesType, scroll.getPage(), scroll.getPageElements(), language.getStringLanguage());
        }
    };
    
    
    
    
    
	private OnClickListener mCommentButtonClickListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
        	GridView gridView = (GridView) getActivity().findViewById(R.id.gridView);
        	final int position = gridView.getPositionForView(v);
            if (position != AdapterView.INVALID_POSITION) {
            	Intent intent = new Intent(getActivity(), CommentsView.class);
            	intent.putExtra("model", "Guide");
            	intent.putExtra("id", guidesStore.getGuide(position).id);
            	intent.putExtra("title", guidesStore.getGuide(position).header.title);
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
                showMessage("Servicio compartir no disponible");
            }
        }
    };
    
    
    
    
    
    public void newGuide(){
    	
    	final Dialog dialog = new Dialog(getActivity());  
		dialog.setContentView(R.layout.dialog_two_button_layout);
		dialog.setCancelable(true);
		
		dialog.setTitle("Advertencia");
		
		TextView msg = (TextView) dialog.findViewById(R.id.message);
		msg.setText("¿Seguro que quieres crear una nueva guía?");
			
		Button buttonYes = (Button) dialog.findViewById(R.id.yes);
		buttonYes.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
			   
	        	dialog.cancel();
			      getActivity().runOnUiThread(new Runnable() {
		    	        public void run() {
		    	        	new NewGuide().start();
		    	        }
		    		});	  
		   }
		});  	
		
		Button buttonNo = (Button) dialog.findViewById(R.id.no);
		buttonNo.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	   	      dialog.cancel();
		   }
    	});  	
			  	
		dialog.show();		
    	
    }
    
    
    
    
    
    public static int nearest2pow(int value) {
    	Log.d(LOGTAG, "nearest2pow");
        return value == 0 ? 0 : (32 - Integer.numberOfLeadingZeros(value - 1)) / 2;
    }
	
	
	
	
	
	public void openSearchItems(){
		Log.d(LOGTAG,"openSearchItems");
		EditText editText = (EditText) rootView.findViewById(R.id.editText_guides_view);
		editText.setText("");        	
		ImageView searchButton = (ImageView) rootView.findViewById(R.id.imageView_search_guides_view);
		searchButton.setOnClickListener(mSearchBarClickListener);
		ImageView closeButton = (ImageView) rootView.findViewById(R.id.imageView_close_search_guides_view);
		closeButton.setOnClickListener(mCloseSearchBarClickListener);
		LinearLayout linearLayout = (LinearLayout) rootView.findViewById(R.id.linearLayout_search_guides);
		linearLayout.setVisibility(View.VISIBLE);    
    }
    
    
	
	
	
    public void refresh(){
    	Log.d(LOGTAG,"refresh");
    	scroll.setPage(1);
		scroll.setTotalCount(scroll.getPageElements()); 
    	guidesStore.borrarLista();
    	searchGuides("", guidesType, scroll.getPage(), scroll.getPageElements(), language.getStringLanguage());
    }
    
    
    
    
    
    public Bitmap scaleImage(Bitmap bitmap, int targetWidth) {
    	Log.d(LOGTAG,"scaleImage");
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        double ratioWidth = ((float) targetWidth) / (float) options.outWidth;
        double ratioHeight = ((float) targetWidth) / (float) options.outHeight;
        double ratio = Math.min(ratioWidth, ratioHeight);
        int dstWidth = (int) Math.round(ratio * options.outWidth);
        int dstHeight = (int) Math.round(ratio * options.outHeight);
        ratio = Math.floor(1.0 / ratio);
        int sample = nearest2pow((int) ratio);
 
        options.inJustDecodeBounds = false;
        if (sample <= 0) {
            sample = 1;
        }
        options.inSampleSize = (int) sample;
        options.inPurgeable = true;
        try {
            Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, dstWidth, dstHeight, true);
            bitmap = bitmap2;
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
 
        return bitmap;
    }
    
    
    
    
    
    public void searchGuides(String search, String type, int page, int guides_number, String language){
		Log.d(LOGTAG, "searchGuides");
		search_guides = new SearchGuides(search, type, page, guides_number, language );
		search_guides.execute();
	}
    
    
    
    
    
    public void seeGuides(){
    	Log.d(LOGTAG, "seeGuides");
		scroll.setPage(1);
		scroll.setTotalCount(scroll.getPageElements()); 
		guidesStore.borrarLista();
		guidesType = "none";
		searchGuides("", guidesType, scroll.getPage(), scroll.getPageElements(), language.getStringLanguage());
	}
	
	
	
	
	
	public void seeMyGuides(){
		Log.d(LOGTAG, "seeMyGuides");
		scroll.setPage(1);
		scroll.setTotalCount(scroll.getPageElements()); 
		guidesStore.borrarLista();
		guidesType = "true";
		searchGuides("", guidesType, scroll.getPage(), scroll.getPageElements(), language.getStringLanguage());
	}
	
	
	
	
    
	
	
	
	
    private void showMessage(String message) {
    	Log.d("GuideListFragment", "ShowMessage");
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }
    
    
    
    
    
    public void updateList(){
    	Log.d(LOGTAG, "updateList");
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				ListGuideAdapter.notifyDataSetChanged(); 			
			}
		});
	}

 

    
    
    public class GetTypeOfView extends AsyncTask<Void, Void, Void> {
    	String language, urlIds, typeOfView;
		
    	public GetTypeOfView(String language, String urlIds, int TypeOfView){
    		this.language = language;
    		this.urlIds = urlIds;
    		if (TypeOfView == MINIVIEW) typeOfView = "getMiniView";
    		else if (TypeOfView == SMALLVIEW) typeOfView = "getSmallView";
    		else if (TypeOfView == WHOLEVIEW) typeOfView = "getWholeView";
    		else Log.d(LOGTAG, "Ningún tipo de vista definida.");
    	}
    	   	
    	protected Void doInBackground(Void... something) {
        	System.out.println("GetTypeOfView");
        	SharedPreferences preferences = getActivity().getSharedPreferences( "datos", Context.MODE_PRIVATE);  
        	String url = new IP().ip + "/" + this.language + "/guides/" + typeOfView + ".json?" + this.urlIds; 
        	HttpGet httpGet = new HttpGet(url);
			httpGet.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
			try {
				Log.e(LOGTAG, "url: "+url);
				
				// PRUEBA				
				/*HttpResponse httpResponsePrueba = new DefaultHttpClient().execute(httpGet);
				Log.e(LOGTAG, "Response: " + EntityUtils.toString(httpResponsePrueba.getEntity()));*/
				// PRUEBA	
				
				HttpResponse httpResponse = new DefaultHttpClient().execute(httpGet);
				
				List<Guide> guideList = new Funciones().readJsonGuidesStream(httpResponse.getEntity());
				
				for (int i=0;i<guideList.size();i++) guidesStore.guardarGuia(guideList.get(i));
				
				downloadImages(guideList);				
				finProgressBar();
				updateList();
				changeVisibilityOfGridView(View.VISIBLE);
				checkEmptyView();			
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
        }             
    }
    
    
    
    
    
    public class SearchGuides extends AsyncTask<Void, Void, Void> {
    	String search, language, owner;
        int page, guides_number;
    	
        public SearchGuides(String search, String owner, int page, int guides_number, String language) {
        	progressDialog = createProgressDialog("Cargando guías...");
            this.search = search;
            this.owner = owner;
            this.page = page;
            this.guides_number = guides_number;
            this.language = language;
        }
    	
  
        protected Void doInBackground(Void... guideList) {
    
    		try {
    			Log.d("GuideListFragment", "SearchGuides");       	
    	       	String url =  new IP().ip+"/"+this.language+"/guides/search.json";
    	   		         	      
    			if (this.owner.equals("none") == false){
    				url+="?owner="+this.owner;
    				if (this.search.equals("") == false) url+="&search="+this.search;
    				if (this.page > 0) url+="&page="+this.page;        			
    				if (this.guides_number > 0) url+="&guides_number="+this.guides_number;        			
    			}
    			else {
    				if (this.search.equals("") == false){
    					url+="?search="+this.search;
    					if (this.page > 0) url+="&page="+this.page;        			
        				if (this.guides_number > 0) url+="&guides_number="+this.guides_number;  
    				}
    				else{
    					if (this.page > 0){
    						url+="?page="+this.page;        		
    						if (this.guides_number > 0) url+="&guides_number="+this.guides_number; 
    					}
    					else if (this.guides_number > 0) url+="guides_number="+this.guides_number; 
    				}
      			}   		
    						    				
    			HttpGet httpGet = new HttpGet(url);
    			
    			SharedPreferences preferences = getActivity().getSharedPreferences( "datos", Context.MODE_PRIVATE);
    	       	httpGet.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
    			HttpClient httpClient = new DefaultHttpClient(); 
    			//Log.e(LOGTAG, "url: " + url);
    			
				HttpResponse httpResponse = httpClient.execute(httpGet);
				
				HttpEntity httpEntity = httpResponse.getEntity();
    			if (httpEntity != null){
    				List<Integer> listInteger = new Funciones().readJsonIdsStream(httpEntity);
    				String urlIds = new Funciones().makeUrlIds(listInteger);
    				getTypeOfView(language, urlIds, WHOLEVIEW);	
    								
    				// PRUEBA   				
    				/*Log.e(LOGTAG, "url_ids: " + urlIds);
    				httpResponse = httpClient.execute(httpGet);
    				Log.e(LOGTAG, "response: " + EntityUtils.toString(httpResponse.getEntity()));*/
    				// FIN DE PRUEBA
    			}			
    			else finProgressBar();
				
    		} catch (UnsupportedEncodingException e1) {
    			e1.printStackTrace();
    		} catch (ClientProtocolException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}
			return null;				              
        }
    }

    
    
    
    
    public class GuidesAdapter extends BaseAdapter {
		private final Activity actividad;
	    private final ArrayList<Guide> lista;
	       
	    public GuidesAdapter(Activity actividad, ArrayList<Guide> guias) {
	        super();
	        this.actividad = actividad;
	        this.lista = guias;
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
		public View getView(int position, View convertView, ViewGroup parent) {
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
	        	 myHolder.checkbox.setVisibility(View.GONE);
	        	 myHolder.horizontalLine = (View) view.findViewById(R.id.horizontalLine);
	        	 myHolder.horizontalLine.setVisibility(View.GONE);
	        	 
	        	 view.setTag(myHolder);
	        	 view.createViews();
	         }
	         else myHolder = (ItemHolder) view.getTag();
	             
        	 if (lista.get(position).header.title != "") view.setTitle(lista.get(position).header.title);
        	 else view.setTitle("Título");
        	 
        	 if (lista.get(position).header.description != "") view.setDescription(lista.get(position).header.getShortDescription());  
        	 else view.setDescription("Descripción\n");   
        	 
        	 if (lista.get(position).header.imageBitmap != null){
        		 view.setImage(ImageHelper.getRoundedCornerBitmap(lista.get(position).header.imageBitmap, 12, this.actividad));
        	 }
        	 else{ // Imagen por defecto (NO IMAGE)
        		 Bitmap bm = BitmapFactory.decodeResource(this.actividad.getResources(), R.drawable.imagen);
        		 view.setImage(ImageHelper.getRoundedCornerBitmap(bm, 12, this.actividad));
        	 }      	
        	 
	         return view;
		}
	}
    
    
    
    
    
    class NewGuide extends Thread {

    	public NewGuide(){
    		progressDialog = createProgressDialog("Espera...");
    	}
    	
        @Override 
        public void run() {
        	try {        		
        		Log.d(LOGTAG, "NewGuide");
            	String BaseUrlPage = new IP().ip+"/"+language.getStringLanguage()+"/guides/create.json";
            	HttpPost solicitud = new HttpPost(BaseUrlPage);	
            	SharedPreferences preferences = getActivity().getSharedPreferences( "datos", Context.MODE_PRIVATE);
            	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0")); 
            	new DefaultHttpClient().execute(solicitud);
            	finProgressBar();
            	
            	Looper.prepare();
    			Dialog dialog = create_finish_dialog(getActivity(), "Advertencia", "La guía ha sido creada");
    			dialog.show();
    			Looper.loop();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}				
        }       
    }    
    
    
       
    
    
    public class DownloadImages extends AsyncTask<List<Guide>, Void, Void> {
        protected Void doInBackground(List<Guide>... guideList) {
        	Log.d(LOGTAG, "DownloadImages");
        	SharedPreferences preferences = getActivity().getSharedPreferences( "datos", Context.MODE_PRIVATE);			
			for(int i=0;i<guideList[0].size();i++){
				if (guideList[0].get(i).header.imageUrl.equals("none") == false){
					try {
						String BaseUrlPage = new IP().ip + guideList[0].get(i).header.imageUrl;
						HttpGet solicitud = new HttpGet(BaseUrlPage);	
						solicitud.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
						if (isCancelled()) break;
						HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);
						Bitmap loadedImage = BitmapFactory.decodeStream(respuesta.getEntity().getContent());
						if (loadedImage != null){
							guidesStore.setImage(guideList[0].get(i).id, loadedImage);							
						}		
						if (isCancelled()) break;
						updateList();
						changeVisibilityOfGridView(View.VISIBLE);
						checkEmptyView();
					} catch (ClientProtocolException e) {
						e.printStackTrace();
					} catch (IOException e) {
						e.printStackTrace();
					}	
				}
			}
			return null; 			
        }
    }

    
    
    
    
    
}

