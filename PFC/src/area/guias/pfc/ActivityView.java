package area.guias.pfc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

public class ActivityView extends ActionBarActivity{
	public IP ip = new IP();
	private ActivityMini miniActivity;
	private static SharedPreferences preferences;
	public static Activity activity;
	private ScrollView scrollview;
	private int newId; 
	private ProgressDialog progressDialog;
	private String activityType;
	private Mode mode;
	private Owner owner;
	private static final String LOGTAG = "LogsAndroid";
	private ArrayList<ActivityTextView> activityTextViewsList;
	private ArrayList<Integer> removedTextsIdList;
	private ArrayList<ActivityAddedText> addedTextsList;
	private ArrayList<Integer> addedEditTextsIdList;
	private ArrayList<ActivityAddTextButton> addTextButtonList;
	private Language language;
	private static final int ACTION_TAKE_PHOTO = 1;
	private String mCurrentPhotoPath;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	private ImageView mImageView;
	
	// CLASE PARA FOTOS
	private static BitmapFactory.Options generalOptions;
    private Uri mImageUri;
    private static final int ACTIVITY_SELECT_IMAGE = 1020;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
          getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_blue_dark));
          setContentView(R.layout.activity_view);
          
          Configuration configuration = getResources().getConfiguration();
          onConfigurationChanged(configuration);
          
          getOverflowMenu();

          preferences = getSharedPreferences("datos",MODE_PRIVATE);  
                    
          miniActivity = getIntent().getParcelableExtra("activity");
          owner = getIntent().getParcelableExtra("owner");
          activityType = getIntent().getExtras().getString("activityType");
          language = getIntent().getParcelableExtra("language");
          
          scrollview = (ScrollView) findViewById(R.id.scrollView_activity);
          scrollview.setVisibility(View.INVISIBLE);         
          	      
          activityTextViewsList = new ArrayList<ActivityTextView>();
          removedTextsIdList = new ArrayList<Integer>();
          addedTextsList = new ArrayList<ActivityAddedText>();
          addedEditTextsIdList = new ArrayList<Integer>();
          addTextButtonList = new ArrayList<ActivityAddTextButton>();
          
	      mode = new Mode();
	      	      
	      if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
				mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		  } else {
				mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		  }
	      
	      newId = 1000000;
	      
          if (savedInstanceState != null){
        	  
        	  /*mode = savedInstanceState.getParcelable("mode");
        	  
        	  activity = savedInstanceState.getParcelable("activity"); 
              for (int i=0; i<savedInstanceState.getParcelableArrayList("boxes").size(); i++){
            	  activity.boxes.add((Box) savedInstanceState.getParcelableArrayList("boxes").get(i));
              }
              
              load_main_views();
      	    
      	      RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.aux_layout);
      	      relativeLayout.removeAllViewsInLayout();
        	  
        	  cargarVistas();*/
          }
          else {
        	  progressDialog = new Funciones().createProgressDialog(this, "Cargando contenido...");
        	  restart();
          }
    }
	
	
	
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.	
        getMenuInflater().inflate(R.menu.activity_view_view_mode, menu);
        return true;
    }  
	
	
	
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		menu.clear();
		if ((activityType.equals("systemActivity"))||(owner.getOwner() == owner.OwnerNone())) getMenuInflater().inflate(R.menu.activity_view_system, menu);
		else if (activityType.equals("userActivity")){
			if (mode.getMode() == mode.ViewMode()) getMenuInflater().inflate(R.menu.activity_view_view_mode, menu);
			else if (mode.getMode() == mode.EditMode()) getMenuInflater().inflate(R.menu.activity_view_edit_mode, menu);
			else Log.d(LOGTAG, "Error en el modo");
		}
		else Log.e(LOGTAG, "Error en el tipo de actividad");
		return super.onPrepareOptionsMenu(menu);
	}
	
	
	
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.    	
        int id = item.getItemId();
        if (id == R.id.edit) {
        	mode.setMode(mode.EditMode());
        	invalidateOptionsMenu();
        	edit_mode();
            return true;
        }
        if (id == R.id.view_mode) {
        	mode.setMode(mode.ViewMode());
        	invalidateOptionsMenu();
        	view_mode();
            return true;
        }
        if (id == R.id.save){
        	save_activity();
        	return true;
        }
        if (id == R.id.revert){
        	finish();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
		
	
	
	
	
	@SuppressLint("NewApi")
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
		Log.d("NavigationDrawer", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        
        DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_guias));
        } 
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.activity_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_land_guias));
        }
        
        
    }
	
	
	
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d("ActivityView", "onSaveInstanceState");
	    super.onSaveInstanceState(outState);
	    if (mImageUri != null) outState.putString("Uri", mImageUri.toString());
	    
		outState.putParcelable("mode", mode);
		outState.putParcelable("activity", activity);  
		outState.putParcelableArrayList("boxes", activity.boxes);
	}
	
	
	
	
	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
	    super.onRestoreInstanceState(savedInstanceState);
	    if (savedInstanceState.containsKey("Uri")) {
	        mImageUri = Uri.parse(savedInstanceState.getString("Uri"));
	    }
	}
	
	
	
	
	
	@Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){
		Log.d(LOGTAG, "onActivityResult");
		switch(requestCode){
			case ACTIVITY_SELECT_IMAGE: {
				if (data != null){
					mImageUri = data.getData();
					getImage(mImageUri, data);
				}
				break;
			}
    	
    		case ACTION_TAKE_PHOTO: {
    			if (resultCode == RESULT_OK) {
    				try {
						handleBigCameraPhoto();
					} catch (IOException e) {
						e.printStackTrace();
					}
    			}
    			break;
    		}
		}		
	}
	
	
	
	
	
	
	@Override
    public void onDestroy(){
		Log.d("ActivityView", "onDestroy");
        super.onDestroy();
    }
	
	
	public void getPhotoDialog(View view) {
		if (mode.getMode() == mode.EditMode()){
    		AlertDialog.Builder dialog = new AlertDialog.Builder(this);    		
    		dialog.setTitle("Advertencia");
    		dialog.setMessage("Escoge entre cámara o galería");
    		dialog.setCancelable(false);
    		dialog.setPositiveButton("Cámara", new DialogInterface.OnClickListener() {   		 
    		   @Override
    		   public void onClick(DialogInterface dialog, int which) {
    			   dispatchTakePictureIntent(ACTION_TAKE_PHOTO);
               }
    		});  		
    		dialog.setNegativeButton("Galería", new DialogInterface.OnClickListener() {   		 
    			   @Override
    			   public void onClick(DialogInterface dialog, int which) {
    				   Intent selectPicture = new Intent(
                               Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                       selectPicture.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                       startActivityForResult(selectPicture, ACTIVITY_SELECT_IMAGE);
    			   }
    		});  		
    		dialog.show();
    	}
    }
	
	
	
	
	
	public int addAddTextImageInRelativeLayout(final boolean disappear, int verticalLayoutId, final int relativeLayoutId, final int belowId, final int componentId, final int componentPosition){
		Log.d(LOGTAG, "addAddTextImageInRelativeLayout");
		final ImageView imageView = new ImageView(ActivityView.this);
		LinearLayout.LayoutParams params =
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(5, 10, 5, 5);		
		imageView.setLayoutParams(params);	
		imageView.setImageDrawable(getResources().getDrawable(R.drawable.add));
		LinearLayout vertical_layout = (LinearLayout) findViewById(verticalLayoutId);
		
		ActivityAddTextButton aatb = new ActivityAddTextButton();
		aatb.setButtonId(newId);
		aatb.setBelowId(belowId);
		aatb.setComponentId(componentId);
		addTextButtonList.add(aatb);
		
		imageView.setId(newId);		
		imageView.setVisibility(View.GONE);
		imageView.setOnClickListener(new OnClickListener() {
		
			@Override
            public void onClick(View arg0) {
							
				// así encontraremos el ID del EditText último (belowID) para poder poner el nuevo
				// EditText debajo
				int belowID=-1;
				for (int i=0; i<addTextButtonList.size(); i++){
					if (addTextButtonList.get(i).getButtonId() == arg0.getId()){
						belowID = addTextButtonList.get(i).getBelowId();
					}
				}
				
				boolean bold;
				if (componentPosition == 1) bold = true;
				else bold = false;
				// ponemos el nuevo EditText debajo del último EditText
				belowID = addEditTextInRelativeLayout(relativeLayoutId, belowID, bold, View.VISIBLE);
				addedEditTextsIdList.add(belowID);
				addDeleteTextImageInRelativeLayout(relativeLayoutId, belowID, android.R.drawable.ic_delete, belowID, -1);
				
				// actualizamos el id del nuevo EditText
				for (int i=0; i<addTextButtonList.size(); i++){
					if (addTextButtonList.get(i).getButtonId() == arg0.getId()){
						addTextButtonList.get(i).setBelowId(belowID);
					}
				}
				
				ActivityAddedText aat = new ActivityAddedText();
				aat.setEditTextId(belowID);
				aat.setComponentId(componentId);
				aat.setComponentPosition(componentPosition);
				aat.setText("");
				addedTextsList.add(aat);
				
				ImageView iv = null;
				if (disappear == true){
					iv = (ImageView) findViewById(arg0.getId());
					iv.setVisibility(View.GONE);
					for (int i=0; i<addTextButtonList.size(); i++){
						if (addTextButtonList.get(i).getButtonId() == arg0.getId()){
							addTextButtonList.remove(i);
						}
					}
				}
            }
        });
		newId++;	
		vertical_layout.addView(imageView, params);
		return(newId-1);
	}
	
	
	
	
	
	@SuppressLint("NewApi")
	public int addButtonInLinearLayout(int mainLayoutId, String str){
		Log.d(LOGTAG, "addButonInLinearLayout");
		Button button = new Button(ActivityView.this);
		LinearLayout.LayoutParams params =
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(5, 10, 5, 5);
		button.setLayoutParams(params);
		LinearLayout linear_layout = (LinearLayout) findViewById(mainLayoutId);
		button.setId(newId);
		button.setBackground(getResources().getDrawable(R.drawable.orange_button_list_selector));
		newId++;
		button.setText(str);	
		linear_layout.addView(button, params);
		return(newId-1);
	}
	
	
	
	
	
	public int addEditTextInRelativeLayout(int relativeLayoutId, int belowId, boolean bold, int visibility){
		Log.d(LOGTAG, "addEditTextInRelativeLayout");
		EditText editText = new EditText(ActivityView.this);
		RelativeLayout.LayoutParams params =
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(5, 10, 5, 5);
		params.addRule(RelativeLayout.BELOW, belowId);
		editText.setLayoutParams(params);
		if (bold == true){
			editText.setTextAppearance(ActivityView.this, android.R.style.TextAppearance_Medium);
			editText.setTypeface(null, Typeface.BOLD);	
		}		
		RelativeLayout main_layout = (RelativeLayout) findViewById(relativeLayoutId);
		editText.setId(newId);
		editText.setHint("Escribe aquí...");
		editText.setVisibility(visibility);
		newId++;	
		main_layout.addView(editText, params);
		return(newId-1);
	}
	
	
	
	
	
	public int addDeleteTextImageInRelativeLayout(int relativeLayoutId, final int belowId, int imageId, final int correspondingEditTextId, final int textId){
		Log.d(LOGTAG, "addDeleteTextImageInRelativeLayout");
		final ImageView imageView = new ImageView(ActivityView.this);
		RelativeLayout.LayoutParams params =
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(5, 10, 5, 5);
		params.addRule(RelativeLayout.RIGHT_OF, belowId);
		params.addRule(RelativeLayout.ALIGN_BOTTOM, belowId);
		imageView.setLayoutParams(params);	
		imageView.setImageDrawable(getResources().getDrawable(imageId));
		RelativeLayout main_layout = (RelativeLayout) findViewById(relativeLayoutId);
		imageView.setId(newId);		
		if (textId != -1) imageView.setVisibility(View.GONE);
		else imageView.setVisibility(View.VISIBLE);
		imageView.setOnClickListener(new OnClickListener() {
			@Override
            public void onClick(View arg0) {
				deleteText(correspondingEditTextId, imageView.getId(), textId);
				// Si es un texto que aun no existe en la Web (no tiene id)
				if (textId == -1){
					for (int i=0; i<addedTextsList.size();i++){
						// Solo se borra de la lista para añadir los textos a la web
						if (addedTextsList.get(i).getEditTextId() == belowId) addedTextsList.remove(i);
					}
				}
            }
        });
		newId++;	
		main_layout.addView(imageView, params);
		return(newId-1);
	}
	
	
	
	
	
	public int addLine(int mainLayoutId, int belowId){
		Log.d(LOGTAG, "addLine");
		View linea = new View(ActivityView.this);
		linea.setBackgroundColor(getResources().getColor(R.color.grey_eduarea));
		RelativeLayout.LayoutParams params =
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, 5);
		params.setMargins(5, 50, 5, 5);
		params.addRule(RelativeLayout.BELOW, belowId);
		linea.setLayoutParams(params);
		linea.setId(newId);
		newId++;
		RelativeLayout main_layout = (RelativeLayout) findViewById(mainLayoutId);
		main_layout.addView(linea, params);	
		return (newId-1);
	}
	
	
	
	
	
	public int addTextViewInMainLayout(int mainLayoutId, int belowId, String str){
		Log.d(LOGTAG, "addTextViewInMainLayout");
		TextView text = new TextView(ActivityView.this);
		RelativeLayout.LayoutParams params =
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(5, 10, 5, 5);
		params.addRule(RelativeLayout.BELOW, belowId);
		text.setLayoutParams(params);
		text.setTextAppearance(ActivityView.this, android.R.style.TextAppearance_Medium);
		text.setTypeface(null, Typeface.BOLD);		
		RelativeLayout main_layout = (RelativeLayout) findViewById(mainLayoutId);
		text.setId(newId);
		newId++;
		text.setText(str);	
		main_layout.addView(text, params);
		return(newId-1);
	}
	
	
	
	
	
	public int addTextViewInRelativeLayout(int relativeLayoutId, int belowId, String str, boolean bold){
		Log.d(LOGTAG, "addTextViewInMainLayout");
		TextView text = new TextView(ActivityView.this);
		RelativeLayout.LayoutParams params =
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(5, 10, 5, 5);
		params.addRule(RelativeLayout.BELOW, belowId);
		text.setLayoutParams(params);
		if (bold == true){
			text.setTextAppearance(ActivityView.this, android.R.style.TextAppearance_Medium);
			text.setTypeface(null, Typeface.BOLD);	
		}		
		RelativeLayout main_layout = (RelativeLayout) findViewById(relativeLayoutId);
		text.setId(newId);
		newId++;
		text.setText(str);	
		text.setHint("Escribe aquí...");
		main_layout.addView(text, params);
		return(newId-1);
	}
	
	
	
	
	
	public int addHorizontalLayout(int mainLayoutId, int belowId){
		Log.d(LOGTAG, "addHorizontalLayout");
		LinearLayout layout = new LinearLayout(ActivityView.this);
		
		RelativeLayout.LayoutParams params =
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		params.setMargins(5, 10, 5, 5);
		params.addRule(RelativeLayout.BELOW, belowId);
		layout.setLayoutParams(params);
		layout.setOrientation(LinearLayout.HORIZONTAL);
		layout.setId(newId);
		newId++;
		RelativeLayout main_layout = (RelativeLayout) findViewById(mainLayoutId);
		main_layout.addView(layout, params);	
		return (newId-1);
	}
	
	
	
	
	
	public int addRelativeLayout(int linearLayoutId){
		Log.d(LOGTAG, "addRelativeLayout");
		RelativeLayout layout = new RelativeLayout(ActivityView.this);
		
		RelativeLayout.LayoutParams relativeParams =
				new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		
		layout.setLayoutParams(relativeParams);
		layout.setId(newId);
		newId++;
		
		LinearLayout main_layout = (LinearLayout) findViewById(linearLayoutId);
		main_layout.addView(layout, relativeParams);
		return (newId-1);
	}
	
	
	
	
	
	public int addVerticalLayout(int linearLayoutId){
		Log.d(LOGTAG, "addVerticalLayout");
		LinearLayout layout = new LinearLayout(ActivityView.this);
		
		LinearLayout.LayoutParams linearParams =
				new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
		linearParams.weight = 1;
		
		layout.setLayoutParams(linearParams);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setWeightSum(1);
		layout.setId(newId);
		newId++;
		LinearLayout main_layout = (LinearLayout) findViewById(linearLayoutId);
		main_layout.addView(layout, linearParams);
		return (newId-1);
	}
	
	
	
	
	
	public void cargarImagen(final Bitmap bm){
		runOnUiThread(new Runnable() {
	        public void run() {
	        	activity.bitmapImage = bm;
	        	ImageView imagen = (ImageView) findViewById(R.id.imagen);
	        	if (bm != null) imagen.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, ActivityView.this));	
	        	else{
		        	Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.imagen);
		        	imagen.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, ActivityView.this));
		        }
	        }
	    });
	}  
	
	
	
	
	
	public void cargar_vistas(){
		runOnUiThread(new Runnable() {
	        public void run() {	        	      
	        	TextView titulo = (TextView) findViewById(R.id.titulo);
	        	titulo.setText(activity.getName());
	        	
	        	TextView descripcion = (TextView) findViewById(R.id.descripcion);
	        	descripcion.setText(activity.getDescription()); 
	        	
	        	ImageView imageView = (ImageView) findViewById(R.id.imagen);      
	            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.imagen);
	            if (activity.bitmapImage != null) 
	            	imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(activity.bitmapImage, 12, ActivityView.this));
	            else imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, ActivityView.this));
	            
	        	final RadioButton radiobuttonPublic = (RadioButton) findViewById(R.id.radiobutton_public);
	        	final RadioButton radiobuttonPrivate = (RadioButton) findViewById(R.id.radiobutton_private);
	        	
	        	// funcionamiento de los radiobutton
	        	radiobuttonPublic.setOnCheckedChangeListener(new OnCheckedChangeListener() {				
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked) radiobuttonPublic.setChecked(false);
						else radiobuttonPublic.setChecked(true);
					}
				});
	    	
	        	radiobuttonPrivate.setOnCheckedChangeListener(new OnCheckedChangeListener() {				
				@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked) radiobuttonPrivate.setChecked(false);
						else radiobuttonPrivate.setChecked(true);
					}
	        	});
	    	
	        	
	        	int belowId = -1;
	        	int horizontalLayoutId = -1;
	        	int verticalLayoutId = -1;
	        	int relativeLayoutId = -1;
	        	boolean bold = false;	        	
	        	
	        	belowId = addLine(R.id.aux_layout, R.id.padlock);
	        	
	        	for (int i=0; i<activity.boxes.size(); i++){
	        		
	        		/////////////////////////
	        		///// LEFT HALF BOX /////
	        		/////////////////////////
	        		
	        		if (activity.boxes.get(i).getType().equals("left_half_box")){
	        			Log.d(LOGTAG, "left_half_box");
	        			
	        			if ((i>0)&&(activity.boxes.get(i-1).getType().equals("full_box"))){
	        				belowId = addLine(R.id.aux_layout, horizontalLayoutId);
	        				horizontalLayoutId = addHorizontalLayout(R.id.aux_layout, belowId);	   
	        			}
	        			else if ((i>0)&&(activity.boxes.get(i-1).getType().equals("right_half_box"))){
	        				horizontalLayoutId = addHorizontalLayout(R.id.aux_layout, horizontalLayoutId);
	        			}
	        			else if (i==0){
	        				horizontalLayoutId = addHorizontalLayout(R.id.aux_layout, belowId);
	        			}
	        			     			
	        			verticalLayoutId = addVerticalLayout(horizontalLayoutId);
	        			
	        			for(int j=0; j<activity.boxes.get(i).components.size(); j++){
	        				
	        				relativeLayoutId = addRelativeLayout(verticalLayoutId);
	        				
	        				if (activity.boxes.get(i).components.get(j).getPosition() == 1){	
	        					if (activity.boxes.get(i).components.get(j).texts.size()==0){
	        						addAddTextImageInRelativeLayout(true, verticalLayoutId, relativeLayoutId, -1, activity.boxes.get(i).components.get(j).getId(), 1); 
	        					}
	        					else{
	        						for(int k=0; k<activity.boxes.get(i).components.get(j).texts.size(); k++){	        						
	        							bold = true;
	        							belowId = addTextViewInRelativeLayout(relativeLayoutId, belowId, activity.boxes.get(i).components.get(j).texts.get(k).getContent(), bold);	        						
	        						
	        							ActivityTextView atv= new ActivityTextView();	        						
	        							atv.setId(belowId);
	        							atv.setTextId(activity.boxes.get(i).components.get(j).texts.get(k).getId());
	        							atv.setTextPosition(activity.boxes.get(i).components.get(j).texts.get(k).getPosition());
	        							atv.setComponentId(activity.boxes.get(i).components.get(j).getId());
	        							atv.setComponentPosition(activity.boxes.get(i).components.get(j).getPosition());
	        							atv.setText(activity.boxes.get(i).components.get(j).texts.get(k).getContent());
	   
	        							int edit_text_id = addEditTextInRelativeLayout(relativeLayoutId, belowId, bold, View.INVISIBLE);
	        							atv.setCorrespondingEditTextId(edit_text_id);	        												        		     					
	        							activityTextViewsList.add(atv);	        						
	        						}  
	        					}  					
	        				}
	        				
	        				if (activity.boxes.get(i).components.get(j).getPosition() == 2){	
	        					if (activity.boxes.get(i).components.get(j).texts.size()==0){
	        						addAddTextImageInRelativeLayout(false, verticalLayoutId, relativeLayoutId, -1, activity.boxes.get(i).components.get(j).getId(), 2); 
	        					}
	        					else{
	        						for(int k=0; k<activity.boxes.get(i).components.get(j).texts.size(); k++){	        						
	        							bold = false;
	        							belowId = addTextViewInRelativeLayout(relativeLayoutId, belowId, activity.boxes.get(i).components.get(j).texts.get(k).getContent(), bold);	        							
	        						
	        							ActivityTextView atv= new ActivityTextView();	        						
	        							atv.setId(belowId);
	        							atv.setTextId(activity.boxes.get(i).components.get(j).texts.get(k).getId());
	        							atv.setTextPosition(activity.boxes.get(i).components.get(j).texts.get(k).getPosition());
	        							atv.setComponentId(activity.boxes.get(i).components.get(j).getId());
	        							atv.setComponentPosition(activity.boxes.get(i).components.get(j).getPosition());
	        							atv.setText(activity.boxes.get(i).components.get(j).texts.get(k).getContent());
	   
	        							int edit_text_id = addEditTextInRelativeLayout(relativeLayoutId, belowId, bold, View.INVISIBLE);
	        							atv.setCorrespondingEditTextId(edit_text_id);	        												        		     					
	        							activityTextViewsList.add(atv);
	        						
	        							int delete_text_id = addDeleteTextImageInRelativeLayout(relativeLayoutId, edit_text_id, android.R.drawable.ic_delete, edit_text_id, activity.boxes.get(i).components.get(j).texts.get(k).getId());
	        							atv.setCorrespondingDeleteTextImageId(delete_text_id);
        							
	        							if (k==activity.boxes.get(i).components.get(j).texts.size()-1){	        						
	        								int add_text_button_id = addAddTextImageInRelativeLayout(false, verticalLayoutId, relativeLayoutId, edit_text_id, activity.boxes.get(i).components.get(j).getId(), 2);
	        								atv.setCorrespondingAddTextImageId(add_text_button_id);	        							
	        							}  
        							
	        							activityTextViewsList.add(atv);
	        						}
	        					}
	        					
	        					
								
	        				}
	        				
	        			}
	        			
	        		}	
	        		
	        		//////////////////////////
	        		///// RIGHT HALF BOX /////
	        		//////////////////////////
	        		
	        		if (activity.boxes.get(i).getType().equals("right_half_box")){
	        			Log.d(LOGTAG, "right_half_box");
	        			
	        			verticalLayoutId = addVerticalLayout(horizontalLayoutId);
	        			
	        			for(int j=0; j<activity.boxes.get(i).components.size(); j++){	 	        		        				
	        				
	        				relativeLayoutId = addRelativeLayout(verticalLayoutId);
	        				
	        				if (activity.boxes.get(i).components.get(j).getPosition() == 1){	        					
	        					if (activity.boxes.get(i).components.get(j).texts.size()==0){
	        						addAddTextImageInRelativeLayout(true, verticalLayoutId, relativeLayoutId, -1, activity.boxes.get(i).components.get(j).getId(), 1); 
	        					}
	        					else{
	        						for(int k=0; k<activity.boxes.get(i).components.get(j).texts.size(); k++){	        						
	        							bold = true;
	        							belowId = addTextViewInRelativeLayout(relativeLayoutId, belowId, activity.boxes.get(i).components.get(j).texts.get(k).getContent(), bold);	        						
	        						
	        							ActivityTextView atv= new ActivityTextView();	        						
	        							atv.setId(belowId);
	        							atv.setTextId(activity.boxes.get(i).components.get(j).texts.get(k).getId());
	        							atv.setTextPosition(activity.boxes.get(i).components.get(j).texts.get(k).getPosition());
	        							atv.setComponentId(activity.boxes.get(i).components.get(j).getId());
	        							atv.setComponentPosition(activity.boxes.get(i).components.get(j).getPosition());
	        							atv.setText(activity.boxes.get(i).components.get(j).texts.get(k).getContent());
	   
	        							int edit_text_id = addEditTextInRelativeLayout(relativeLayoutId, belowId, bold, View.INVISIBLE);
	        							atv.setCorrespondingEditTextId(edit_text_id);	        												        		     					
	        							activityTextViewsList.add(atv);	        						
	        						}  
	        					}  		        					
	        				}
	        				
	        				if (activity.boxes.get(i).components.get(j).getPosition() == 2){	 
	        					if (activity.boxes.get(i).components.get(j).texts.size()==0){
	        						addAddTextImageInRelativeLayout(false, verticalLayoutId, relativeLayoutId, -1, activity.boxes.get(i).components.get(j).getId(), 2); 
	        					}
	        					else{
	        						for(int k=0; k<activity.boxes.get(i).components.get(j).texts.size(); k++){	        						
	        							bold = false;
	        							belowId = addTextViewInRelativeLayout(relativeLayoutId, belowId, activity.boxes.get(i).components.get(j).texts.get(k).getContent(), bold);	        						
	        					
	        							ActivityTextView atv= new ActivityTextView();	        						
	        							atv.setId(belowId);
	        							atv.setTextId(activity.boxes.get(i).components.get(j).texts.get(k).getId());
	        							atv.setTextPosition(activity.boxes.get(i).components.get(j).texts.get(k).getPosition());
	        							atv.setComponentId(activity.boxes.get(i).components.get(j).getId());
	        							atv.setComponentPosition(activity.boxes.get(i).components.get(j).getPosition());
	        							atv.setText(activity.boxes.get(i).components.get(j).texts.get(k).getContent());
	   
	        							int edit_text_id = addEditTextInRelativeLayout(relativeLayoutId, belowId, bold, View.INVISIBLE);
	        							atv.setCorrespondingEditTextId(edit_text_id);	        												        		     					
	        							activityTextViewsList.add(atv);
	        						
	        							int delete_text_id = addDeleteTextImageInRelativeLayout(relativeLayoutId, edit_text_id, android.R.drawable.ic_delete, edit_text_id, activity.boxes.get(i).components.get(j).texts.get(k).getId());
	        							atv.setCorrespondingDeleteTextImageId(delete_text_id);
        							
	        							if (k==activity.boxes.get(i).components.get(j).texts.size()-1){	        						
	        								int add_text_button_id = addAddTextImageInRelativeLayout(false, verticalLayoutId, relativeLayoutId, edit_text_id, activity.boxes.get(i).components.get(j).getId(), 2);
	        								atv.setCorrespondingAddTextImageId(add_text_button_id);	        							
	        							}  
        							
	        							activityTextViewsList.add(atv);
	        						}	        				
	        					}
	        				}
	        				
	        			}
	        			
	        		}
	        		
	        		//////////////////////////
	        		//////// FULL BOX ////////
	        		//////////////////////////
	        		
	        		if (activity.boxes.get(i).getType().equals("full_box")){
	        			Log.d(LOGTAG, "full_box");
	        			
	        			if (i>0) belowId = addLine(R.id.aux_layout, horizontalLayoutId);
	        			
	        			horizontalLayoutId = addHorizontalLayout(R.id.aux_layout, belowId);
	        			relativeLayoutId = addRelativeLayout(horizontalLayoutId);
	        			
	        			for(int j=0; j<activity.boxes.get(i).components.size(); j++){	 
	        				
	        			}

	        		}
	        		
	        	}
	
	        	belowId = addLine(R.id.aux_layout, horizontalLayoutId);
	        	
	        	belowId = addTextViewInMainLayout(R.id.aux_layout, belowId, "Precisas...");
	        	
	        	horizontalLayoutId = addHorizontalLayout(R.id.aux_layout, belowId);
	        	
	        	verticalLayoutId = addVerticalLayout(horizontalLayoutId);
	        	
	        	belowId = addButtonInLinearLayout(verticalLayoutId, "Ferramentas");
	        	Button button = (Button) findViewById(belowId);
	        	button.setOnClickListener(new OnClickListener() {				
					@Override
					public void onClick(View arg0) {
						 Intent intent = new Intent(ActivityView.this, ToolsView.class);
						 intent.putExtra("activity_id", activity.id);
						 intent.putExtra("activityType", activityType);
						 intent.putExtra("language", language);
						 startActivity(intent);						
					}
	    		});
	        	
	        	belowId = addButtonInLinearLayout(verticalLayoutId, "Saídas");
	        	button = (Button) findViewById(belowId);
	        	button.setOnClickListener(new OnClickListener() {				
					@Override
					public void onClick(View arg0) {
						 Intent intent = new Intent(ActivityView.this, TripsView.class);
						 intent.putExtra("activity_id", activity.id);
						 intent.putExtra("activityType", activityType);
						 intent.putExtra("language", language);
						 startActivity(intent);						
					}
	    		});
	        	
	        	belowId = addButtonInLinearLayout(verticalLayoutId, "Contidos");
	        	button = (Button) findViewById(belowId);
	        	button.setOnClickListener(new OnClickListener() {				
					@Override
					public void onClick(View arg0) {
						 Intent intent = new Intent(ActivityView.this, ContentsView.class);
						 intent.putExtra("activity_id", activity.id);
						 intent.putExtra("activityType", activityType);
						 intent.putExtra("language", language);
						 startActivity(intent);						
					}
	    		});
	        	
	        	belowId = addButtonInLinearLayout(verticalLayoutId, "Colaboradores");
	        	button = (Button) findViewById(belowId);
	        	button.setOnClickListener(new OnClickListener() {				
					@Override
					public void onClick(View arg0) {
						 Intent intent = new Intent(ActivityView.this, PersonsView.class);
						 intent.putExtra("activity_id", activity.id);
						 intent.putExtra("activityType", activityType);
						 intent.putExtra("language", language);
						 startActivity(intent);						
					}
	    		});
	        	
	        	belowId = addLine(R.id.aux_layout, horizontalLayoutId);
	        	
	        	if (progressDialog != null) progressDialog.dismiss();
	        	scrollview.setVisibility(View.VISIBLE);
	        	
	        }
	    });
	}
	
	
	
	
	
	public void changeVisibilityOfAddTextButtons(int visibility){
		for (int i=0; i<addTextButtonList.size(); i++){
			if (addTextButtonList.get(i).getButtonId() > 0){
				ImageView iv = (ImageView) findViewById(addTextButtonList.get(i).getButtonId());
				iv.setVisibility(visibility);
			}
		}
	}
	
	
	
	
	
	public void changeVisibilityOfEditText(int visibility, int EditTextId){
		EditText editText = (EditText) findViewById(EditTextId);
		editText.setVisibility(visibility);		
	}
	
	
	
	
	
	public void changeVisibilityOfEditTexts(int visibility){
		EditText editText = (EditText) findViewById(R.id.edit_title);
		editText.setVisibility(visibility);
		editText = (EditText) findViewById(R.id.editDescription);
		editText.setVisibility(visibility);
		
		for (int i=0; i<activityTextViewsList.size(); i++){
			editText = (EditText) findViewById(activityTextViewsList.get(i).getCorrespondingEditTextId());
			editText.setVisibility(visibility);
		}
		
		for (int i=0; i<addedEditTextsIdList.size(); i++){
			editText = (EditText) findViewById(addedEditTextsIdList.get(i));
			editText.setVisibility(visibility);
		}
	}
	
	
	
	
	
	public void changeVisibilityOfImageView(int visibility, int imageViewId){
		ImageView imageView = (ImageView) findViewById(imageViewId);
		imageView.setVisibility(visibility);
	}
	
	
	
	
	
	public void changeVisibilityOfImageViews(int visibility){
		ImageView imageView = null;
		for (int i=0; i<activityTextViewsList.size(); i++){
			imageView = (ImageView) findViewById(activityTextViewsList.get(i).getCorrespondingDeleteTextImageId());
			if (imageView != null) imageView.setVisibility(visibility);
			if (activityTextViewsList.get(i).getCorrespondingAddTextImageId() != -1){
				imageView = (ImageView) findViewById(activityTextViewsList.get(i).getCorrespondingAddTextImageId());
				imageView.setVisibility(visibility);
			}			
		}
	}
	
	
	
	
	
	public void changeVisibilityOfTextViews(int visibility){
		TextView textView = (TextView) findViewById(R.id.titulo);
		textView.setVisibility(visibility);
		textView = (TextView) findViewById(R.id.descripcion);
		textView.setVisibility(visibility);
		
		for (int i=0;i<activityTextViewsList.size(); i++){
			textView = (TextView) findViewById(activityTextViewsList.get(i).getId());
			if (textView != null) textView.setVisibility(visibility);
		}
	}
	
	
	
	
	
	private File createImageFile() throws IOException {
		Log.d(LOGTAG, "createImageFile");		
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
	}
	
	
	
	
	
	public void deleteText(int editTextId, int imageViewId, int textId){
		changeVisibilityOfEditText(View.GONE, editTextId);
		changeVisibilityOfImageView(View.GONE, imageViewId);
		if (textId != -1) removedTextsIdList.add(textId);
	}




	
	private void dispatchTakePictureIntent(int actionCode) {
		Log.d(LOGTAG, "dispatchTakePictureIntent");
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		
		switch(actionCode) {
			case ACTION_TAKE_PHOTO:
				File f = null;			
				try {
					f = setUpPhotoFile();
					takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
				} catch (IOException e) {
					e.printStackTrace();
					f = null;
					mCurrentPhotoPath = null;
				}
			
				break;

			default:
				break;			
		} 
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			startActivityForResult(takePictureIntent, actionCode);
	    }
		
	}
	
	
	
	
	
	public void edit_mode(){
		passTextsFromTextViewsToEditTexts();
		changeVisibilityOfTextViews(View.INVISIBLE);
		setAddedEditTextHints();
		changeVisibilityOfEditTexts(View.VISIBLE);
		changeVisibilityOfImageViews(View.VISIBLE);
		changeVisibilityOfAddTextButtons(View.VISIBLE);
	}
	
	
	
	
	
	private void galleryAddPic() {
		Log.d(LOGTAG, "galleryAddPic");		
	    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);	    
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);	    
	    activity.imageUri = contentUri.toString();
	}
	
	
	
	
	
	@SuppressLint("NewApi")
	private String getAbsolutePathForKitKat(Uri originalUri){
		Log.d("GuideViewTabs", "getAbsolutePathForKitKat");

		String selectedImagePath = "path";	    

		String[] filePathColumn = {MediaStore.Images.Media.DATA};

        Cursor cursor = getContentResolver().query(originalUri,
                filePathColumn, null, null, null);
        cursor.moveToFirst();

        int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
        selectedImagePath = cursor.getString(columnIndex);
        System.out.println("Imagen seleccionada: " + selectedImagePath);
        cursor.close();
        
	    return selectedImagePath;
	}
	
	
	
	
	
	private String getAbsolutePathForOlderVersions(Uri originalUri){
		Log.d("GuideViewTabs", "getAbsolutePathForOlderVersions");
		String [] projection={MediaStore.Images.Media.DATA};
		Cursor cursor = this.getContentResolver().query(originalUri, projection, null, null, null);
		int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
		cursor.moveToFirst();
		String fpath = cursor.getString(column_index);
		return fpath;
	}
	
	
	
	
	
	private File getAlbumDir() {
		Log.d(LOGTAG, "getAlbumDir");
		
		File storageDir = null;

		if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
			storageDir = mAlbumStorageDirFactory.getAlbumStorageDir(getAlbumName());

			if (storageDir != null) {
				if (! storageDir.mkdirs()) {
					if (! storageDir.exists()){
						Log.d("CameraSample", "failed to create directory");
						return null;
					}
				}
			}
			
		} else {
			Log.v(getString(R.string.app_name), "External storage is not mounted READ/WRITE.");
		}
		
		return storageDir;
	}
	
	
	
	
	
	private String getAlbumName() {
		return getResources().getString(R.string.album_name);
	}
	
	
	
	
	
	public void getImage(Uri uri, Intent data) {
		Log.d(LOGTAG, "getImage");
        Bitmap bounds = getImagePhotoUtils(uri);
        if (bounds != null) {
        	String absolutePath;
        	if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        		absolutePath = getAbsolutePathForKitKat(data.getData());
        	} else {
    			absolutePath = getAbsolutePathForOlderVersions(data.getData());
    		}
            setImage(bounds, absolutePath);
            Log.d("GuideViewTabs", "Uri: " + uri.toString() + ";path: " + absolutePath);
        } else {
            showErrorToast("imagen sin contenido"); 
        }
    }
	
	
	
	
	
	
	public Bitmap getImagePhotoUtils(Uri uri) {
		Log.d(LOGTAG, "getImagePhotoUtils");
		
        //Bitmap result = null;
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        InputStream is = null;
        try {
            is = this.getContentResolver().openInputStream(uri);
            BitmapFactory.decodeStream(is, null, options);
            is.close();     
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        generalOptions = options;
        return scaleImage(options, uri, 300);
    }
	
	
	
	
	
	public void getOverflowMenu() {
        try {
           ViewConfiguration config = ViewConfiguration.get(this);
           Field menuKeyField = ViewConfiguration.class.getDeclaredField("sHasPermanentMenuKey");
           if(menuKeyField != null) {
               menuKeyField.setAccessible(true);
               menuKeyField.setBoolean(config, false);
           }
       } catch (Exception e) {
           e.printStackTrace();
       }
     }
	
	
	
	
	
	public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null); 
        cursor.moveToFirst(); 
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
        return cursor.getString(idx); 
    }
	
	
	
	
	
	private void handleBigCameraPhoto() throws IOException {
		Log.d(LOGTAG, "handleBigCameraPhoto");		
		if (mCurrentPhotoPath != null) {
			setPic();
			galleryAddPic();
			mCurrentPhotoPath = null;
		}
	}

	
	
	
		
	public void load_main_views(){
		TextView textView = (TextView) findViewById(R.id.titulo);
	    textView.setText(miniActivity.getName());
	      
	    textView = (TextView) findViewById(R.id.descripcion);
	    textView.setText(miniActivity.getDescription());
        
        ImageView image_view = (ImageView) findViewById(R.id.imagen);      
	    Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.imagen);
	    image_view.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bitmap, 12, ActivityView.this));
	}
	
	
	
	

	
	
	
	
	public static int nearest2pow(int value) {
        return value == 0 ? 0 : (32 - Integer.numberOfLeadingZeros(value - 1)) / 2;
    }
	
	
	
	
	
	public void passTextsFromEditTextsToTextViews(){
		EditText editText = (EditText) findViewById(R.id.edit_title);
		TextView textView = (TextView) findViewById(R.id.titulo);
		textView.setText(editText.getText().toString());
		
		editText = (EditText) findViewById(R.id.editDescription);
		textView = (TextView) findViewById(R.id.descripcion);
		textView.setText(editText.getText().toString());
	}
	
	
	
	
	
	public void passTextsFromTextViewsToEditTexts(){
		EditText editText = (EditText) findViewById(R.id.edit_title);
		TextView textView = (TextView) findViewById(R.id.titulo);
		editText.setText(textView.getText().toString());
		
		editText = (EditText) findViewById(R.id.editDescription);
		textView = (TextView) findViewById(R.id.descripcion);
		editText.setText(textView.getText().toString());
		
		for (int i=0;i<activityTextViewsList.size(); i++){
			textView = (TextView) findViewById(activityTextViewsList.get(i).getId());
			editText = (EditText) findViewById(activityTextViewsList.get(i).getCorrespondingEditTextId());
			if (textView != null) editText.setText(textView.getText().toString());
			else editText.setText("");
		}
	}
	
	
	
	
	
	public void restart(){
		
		activityTextViewsList.clear();
        removedTextsIdList.clear();
        addedTextsList.clear();
        addedEditTextsIdList.clear();
        addTextButtonList.clear();
        
		newId = 1000000;
        
        load_main_views();
	       
	    mode = new Mode();
	    
	    RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.aux_layout);
	    relativeLayout.removeAllViewsInLayout();
	    
        new GetActivity().start();
	}
	
	
	
	
	
	public static Bitmap RotateBitmap(Bitmap source, float angle){
	      Matrix matrix = new Matrix();
	      matrix.postRotate(angle);
	      return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
	
	
	
	
	
	public void save_activity(){
		Log.d(LOGTAG, "save_activity");
		Activity activity = new Activity();
		EditText editText = (EditText) findViewById(R.id.edit_title);
		activity.name = editText.getText().toString();
		editText = (EditText) findViewById(R.id.editDescription);
		activity.description = editText.getText().toString();
		
		EditText edit_text;
		for (int i=0; i<activityTextViewsList.size(); i++){
			edit_text = (EditText) findViewById(activityTextViewsList.get(i).getCorrespondingEditTextId());
			activityTextViewsList.get(i).setText(edit_text.getText().toString());
		}
		
		new SaveActivity(activity).start();
	}

	
	
	
	
	
	public Bitmap scaleImage(BitmapFactory.Options options, Uri uri,int targetWidth) {
        if (options == null) options = generalOptions;
        Bitmap bitmap = null;
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
            InputStream is;
            is = getContentResolver().openInputStream(uri);
            bitmap = BitmapFactory.decodeStream(is, null, options);
            Bitmap bitmap2 = Bitmap.createScaledBitmap(bitmap, dstWidth,
                    dstHeight, true);
            bitmap = bitmap2;
            is.close();
        } catch (OutOfMemoryError e) {
            e.printStackTrace();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
 
        return bitmap;
    }
	
	
	
	
	
	public void setAddedEditTextHints(){
		for (int i=0;i<addedEditTextsIdList.size(); i++){
			EditText editText = (EditText) findViewById(addedEditTextsIdList.get(i));
			editText.setHint("Añadir texto");
		}
	}
	
	
	
	
	
	public void setImage(Bitmap bitmap, String filename){
		Log.d(LOGTAG, "setImage");
		
        ImageView iv = (ImageView) findViewById(R.id.imagen);
    	
    	// Rotate image
    	
		try {
			ExifInterface ei = new ExifInterface(filename);
			int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch(orientation) {
    			
				case ExifInterface.ORIENTATION_ROTATE_90:
					Log.d("ActivityView", "rotate 90");
    				bitmap = RotateBitmap(bitmap, 90);
    			break;
    			
				case ExifInterface.ORIENTATION_ROTATE_180:
					Log.d("ActivityView", "rotate 180");
    				bitmap = RotateBitmap(bitmap, 180);
    				
				case ExifInterface.ORIENTATION_ROTATE_270:
					Log.d("ActivityView", "rotate 270");
					bitmap = RotateBitmap(bitmap, 270);
    			break;
    			
    			default:
    				Log.d("ActivityView", "rotate unknown");
    			break;
			}
			iv.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bitmap, 12, ActivityView.this));
		} catch (IOException e) {
			e.printStackTrace();
		}	
    }
	
	
	
	
	
	private void setPic() throws IOException {
		Log.d(LOGTAG, "setPic");
		
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */
		mImageView = (ImageView) findViewById(R.id.imagen);
    	
		/* Get the size of the ImageView */
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		int photoW = bmOptions.outWidth;
		int photoH = bmOptions.outHeight;
		
		/* Figure out which way needs to be reduced less */
		int scaleFactor = 1;
		if ((targetW > 0) || (targetH > 0)) {
			scaleFactor = Math.min(photoW/targetW, photoH/targetH);	
		}

		/* Set bitmap options to scale the image decode target */
		bmOptions.inJustDecodeBounds = false;
		bmOptions.inSampleSize = scaleFactor;
		bmOptions.inSampleSize = 5;
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		Bitmap roundedCornerBitmap = ImageHelper.getRoundedCornerBitmap(bitmap, 12, ActivityView.this);
		
		// Rotate image
		ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
		int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

		switch(orientation) {
		    case ExifInterface.ORIENTATION_ROTATE_90:
		    	roundedCornerBitmap = RotateBitmap(roundedCornerBitmap, 90);
		        break;
		    case ExifInterface.ORIENTATION_ROTATE_180:
		    	roundedCornerBitmap = RotateBitmap(roundedCornerBitmap, 180);
		        break;
		}
		/* Associate the Bitmap to the ImageView */
		mImageView.setImageBitmap(roundedCornerBitmap);
	}
	

	
	
	
	private File setUpPhotoFile() throws IOException {
		Log.d(LOGTAG, "setUpPhotoFile");
		
		File f = createImageFile();
		mCurrentPhotoPath = f.getAbsolutePath();
		
		return f;
	}
	
	
	
	
	
	public void showErrorToast(String phrase){
    	Toast.makeText(this, phrase, Toast.LENGTH_SHORT).show();    	
    }
	
	
	
	
	
	public void view_mode(){
		changeVisibilityOfTextViews(View.VISIBLE);
		changeVisibilityOfEditTexts(View.INVISIBLE);
		changeVisibilityOfImageViews(View.GONE);
		progressDialog = new Funciones().createProgressDialog(this, "Cargando contenido...");
		restart();
	}
	
	
	
	
	
	class DownloadImageActivity extends Thread {
  
        @Override
        public void run() {
        		Log.d("DownloadImageActivity", "DownloadImageActivity");
                if(!activity.getImageUrl().equals("none")) {
                    String BaseUrlPage = "http://" + getString(R.string.server_address) + activity.getImageUrl();
                    HttpGet request = new HttpGet(BaseUrlPage);
                    try {
                    	
                    	// PRUEBA
                    	/*HttpResponse responsePrueba = new DefaultHttpClient().execute(request);
                    	Log.d("ActivityView", "response: "+ EntityUtils.toString(responsePrueba.getEntity()));*/
                    	// PRUEBA
                    	
                        HttpResponse response = new DefaultHttpClient().execute(request);
                        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        	Log.d("ActivityView", "RESPUESTA SERVIDOR OK");
                            HttpEntity entity = response.getEntity();
                            Bitmap loadedImage = BitmapFactory.decodeStream(entity.getContent());
                            cargarImagen(loadedImage);
                        }
                    } catch (ClientProtocolException exc) {
                        exc.printStackTrace();
                    } catch (IOException exc) {
                        exc.printStackTrace();
                    }
                }     
        }
    }
	
	
	
	
	
	class GetActivity extends Thread {

        @Override 
        public void run() {  
            try {           	
            	Funciones funciones = new Funciones();
            	HttpClient cliente = new DefaultHttpClient();
            	String BaseUrlPage = ip.ip+"/gl/activities/getWholeView.json?id="+miniActivity.getId();
            	HttpGet solicitud = new HttpGet(BaseUrlPage);	
            	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));

            	// PRUEBA
            	Log.d(LOGTAG, "Url: " + BaseUrlPage);
				HttpResponse respuesta_prueba = cliente.execute(solicitud);
				String json = EntityUtils.toString(respuesta_prueba.getEntity());
				System.out.println("respuesta: " + json);
				// PRUEBA
				
				HttpResponse respuesta = cliente.execute(solicitud);
			    activity = funciones.readJsonActivityStream(respuesta.getEntity()).get(0);			    
				
				cargar_vistas();
				new DownloadImageActivity().start();	
            } catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
        }        
    }
	
	
	
	
	class SaveActivity extends Thread {
		Activity activit;
		
		public SaveActivity(Activity activit){
			this.activit = activit;
		}
		
        @Override 
        public void run() {  
            try {   
            	Log.d(LOGTAG, "SaveActivity");
            	HttpClient cliente = new DefaultHttpClient();
            	String BaseUrlPage = ip.ip+"/gl/activities/"+miniActivity.getId() + ".json";
            	HttpPut solicitud = new HttpPut(BaseUrlPage);	
            	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
            	
            	MultipartEntity mpEntity = new MultipartEntity();
            	
            	mpEntity.addPart("title", new StringBody(activit.name, Charset.forName("UTF-8")));	
            	
				mpEntity.addPart("description", new StringBody(activit.description, Charset.forName("UTF-8")));
				
				if (activit.imageUri != null){
					File file = new File(getRealPathFromURI(Uri.parse(activit.imageUri)));
					mpEntity.addPart("image", new FileBody(file, "image/jpeg"));
				}				
			
				for (int i=0; i < activityTextViewsList.size(); i++){
					if (activityTextViewsList.get(i).getId() > 0){
						String name = "texts["+activityTextViewsList.get(i).getTextId()+"]";
						mpEntity.addPart(name, new StringBody(activityTextViewsList.get(i).getText(), Charset.forName("UTF-8")));
					}
				}
				
				String idsUrl="";
				for (int i=0; i<removedTextsIdList.size(); i++){
					if (i==removedTextsIdList.size()-1) idsUrl+=removedTextsIdList.get(i);
					else idsUrl+=removedTextsIdList.get(i)+",";
				}
				mpEntity.addPart("text_delete", new StringBody(idsUrl));
				
				int position=0;
				for (int i=0; i<addedTextsList.size(); i++){
					String name = "text_new["+addedTextsList.get(i).getComponentId()+"]["+position+"]";
					EditText edit_text = (EditText) findViewById(addedTextsList.get(i).getEditTextId());
					mpEntity.addPart(name, new StringBody(edit_text.getText().toString(), Charset.forName("UTF-8")));
					position++;
				}
				
				solicitud.setEntity(mpEntity);
				
            	// PRUEBA
            	Log.d(LOGTAG, "url: " + BaseUrlPage);
				HttpResponse respuesta = cliente.execute(solicitud);
				String json = EntityUtils.toString(respuesta.getEntity());
				System.out.println("response: " + json);
				// PRUEBA
								
				// DATOS ENVIADOS
				Log.d(LOGTAG, "Datos enviados: ");
				Log.d(LOGTAG, "title: " + activity.name);
				Log.d(LOGTAG, "description: " + activity.description);
				
				for (int i=0; i < activityTextViewsList.size(); i++){
					if (activityTextViewsList.get(i).getId() > 0){
						String name = "texts["+activityTextViewsList.get(i).getTextId()+"]";
						Log.d(LOGTAG, name + ";" + activityTextViewsList.get(i).getText());
					}
				}
				
				idsUrl="";
				for (int i=0; i<removedTextsIdList.size(); i++){
					if (i==removedTextsIdList.size()-1) idsUrl+=removedTextsIdList.get(i);
					else idsUrl+=removedTextsIdList.get(i)+",";
				}
				Log.d(LOGTAG, "text_delete" + ";" + idsUrl);
            
				position=0;
				for (int i=0; i<addedTextsList.size(); i++){
					String name = "text_new["+addedTextsList.get(i).getComponentId()+"]["+position+"]";
					Log.d(LOGTAG, name + ";" + addedTextsList.get(i).getText());
					position++;
				}
				
				Looper.prepare();
				new Funciones().showToast(ActivityView.this, "Actividad guardada");
				Looper.loop();
				
            } catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}			
        }        
    }
	
	
}
