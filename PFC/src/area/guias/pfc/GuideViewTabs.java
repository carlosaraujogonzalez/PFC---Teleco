package area.guias.pfc;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Field;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GuideViewTabs extends ActionBarActivity implements ActionBar.TabListener {
	
	SectionsPagerAdapter mSectionsPagerAdapter;
	ViewPager mViewPager;
	
	public static Guide guide = new Guide();
	public static TextView textView_address; 
	
	public static int tab_selected, previous_tab;
	private static final int ACTIVITY_SELECT_IMAGE = 1020;
	private static final int ACTION_TAKE_PHOTO_B = 1;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private static final int TAB1=1, TAB2=2, TAB3=3, TAB4=4;
	private static final int YES = 1, NO = 0;
	private String mCurrentPhotoPath;
	
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	
	private Uri mImageUri;
	
	private static BitmapFactory.Options generalOptions;
	
	private boolean tab1_loading, tab2_loading, tab3_loading, tab4_loading;
	
	private ProgressDialog progressDialog;
	
	private Language language;
	
	private Vocabulary vocabulary;
	
	private DownloadImageTab1 downloadImageTab1;
	private DownloadImageTab2 downloadImageTab2;
	private DownloadImageTab3 downloadImageTab3;
	private DownloadImageTab4 downloadImageTab4;
	
	private Owner owner;
	
	private static final String LOGTAG = "LogsAndroid";
	
	private Mode mode;
	
	// ASYNCTASKS
	GetTab1 get_tab1;
	GetTab2 get_tab2;
	GetTab3 get_tab3;
	GetTab4 get_tab4;
	
	private int restart;
	
	// CALENDAR
	private static EditText startDateDisplay;
	private static EditText endDateDisplay;
	
	// ImageViews
	private String image_header_path;
	private String image_technical_setting_path;
	private String image_educational_setting_path;
	private String image_activity_sequence_path;
	
	@Override
    protected void onCreate(Bundle savedInstanceState) {
		System.out.println("onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.guide_view_tabs);
		
        Configuration config = getResources().getConfiguration();
        onConfigurationChanged(config);
        
        getOverflowMenu();
        
        mode = new Mode();
        
        configViewPager();
         
        language = getIntent().getParcelableExtra("language");
                
        vocabulary = new Vocabulary();
        
        owner = new Owner();
               
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
        
		
        
        if (savedInstanceState != null){
        	mode = savedInstanceState.getParcelable("mode");
        	guide = savedInstanceState.getParcelable("guide");
        	vocabulary = savedInstanceState.getParcelable("vocabulary");
        	owner = savedInstanceState.getParcelable("owner");
        	restart = savedInstanceState.getInt("restart");
        }
        else{
        	mode.setMode(mode.ViewMode());
        	guide = getIntent().getParcelableExtra("guide");
        	restart = NO;
        	progressDialog = createProgressDialog("Cargando contenido...");
            start();
        }
    }
       
	
	
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d("GuideViewTabs", "onSaveInstanceState");
		if (mImageUri != null) outState.putString("Uri", mImageUri.toString());
		
		outState.putParcelable("mode", mode);
		outState.putParcelable("guide", guide);
		outState.putParcelable("vocabulary", vocabulary);
		outState.putParcelable("owner", owner);   
		   
		outState.putInt("restart", restart);
		
		super.onSaveInstanceState(outState);
	}
	 
	


	
	@SuppressLint("NewApi")
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
		Log.d("NavigationDrawer", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        
        DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.guide_view_tabs_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_guias));
        } 
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.guide_view_tabs_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_land_guias));
        }
        
    }
        
        
    
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {       
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.guideview_viewmode_menu, menu);
        return true;
    }
	
	
	
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		Log.d(LOGTAG, "onPrepareOptionsMenu");
	    menu.clear();
	    if (mode.getMode() == mode.ViewMode()){
	    	getMenuInflater().inflate(R.menu.guideview_viewmode_menu, menu);
	    	getOverflowMenu();
	    }
	    else{
	    	getMenuInflater().inflate(R.menu.guideview_editmode_menu, menu);
	    }
	    return super.onPrepareOptionsMenu(menu);
	}
	
	
	
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        
        if (id == R.id.refresh){
    		progressDialog = createProgressDialog("Cargando contenido...");
        	restart();
        	return true;
        }
        if (id == R.id.menu_vg_revert){
        	finish_activity();
        	return true;
        }
        if (mode.getMode() == mode.EditMode()){
        	if (id == R.id.menu_vg_save) {
        		try {
					save();
				} catch (InterruptedException e) {
					e.printStackTrace();
				} catch (ExecutionException e) {
					e.printStackTrace();
				}
        		return true;
        	}
        	if (id == R.id.menu_vg_view){
        		saveAuto();
        		mode.setMode(mode.ViewMode());
        		invalidateOptionsMenu();
        		viewMode();
        		return true;
        	}
        }else{
        	if (id == R.id.menu_vg_edit) {        
        		mode.setMode(mode.EditMode());    
        		edit_mode();
        		invalidateOptionsMenu();
        		return true;
        	}     
        }
        return super.onOptionsItemSelected(item);
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
    	
    		case ACTION_TAKE_PHOTO_B: {
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
		Log.d("GuideViewTabs", "onDestroy");
		if (downloadImageTab1 != null) downloadImageTab1.cancel(!downloadImageTab1.isCancelled());
		if (downloadImageTab2 != null) downloadImageTab2.cancel(!downloadImageTab2.isCancelled());
		if (downloadImageTab3 != null) downloadImageTab3.cancel(!downloadImageTab3.isCancelled());
		if (downloadImageTab4 != null) downloadImageTab4.cancel(!downloadImageTab4.isCancelled());
		super.onDestroy();			
	}
	
	
	
	
	
	@Override
	public void onBackPressed(){
		finish_activity();
	}
	
	
	
	
	
	@Override
	public void onTabReselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		
	}

	
	
	
	
	@Override
	public void onTabSelected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
        // the ViewPager.
		previous_tab = tab_selected;
		tab_selected = tab.getPosition()+1;		
		saveAutoOnTabsChange();
        mViewPager.setCurrentItem(tab.getPosition());
	}

	
	
	
	
	@Override
	public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction fragmentTransaction) {
	}

	

	
	
//////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////														  ////////////////////
////////////////////														  ////////////////////
////////////////////				M�TODOS DE LAS VISTAS				      ////////////////////
////////////////////														  ////////////////////
////////////////////                                                          ////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
	
	public void comentar(View view){
		Intent intent = new Intent(this, CommentsView.class);
    	intent.putExtra("model", "Guide");
    	intent.putExtra("id", guide.id);
    	intent.putExtra("title", guide.header.title);
    	intent.putExtra("language", language);
    	startActivity(intent);
    }
	
	
	
	
	
	public void copiar(View view){
		new Funciones().showToast(this, "M�todo disponible pr�ximamente");
		/*Dialog dialog = create_copy_guide_dialog("Advertencia", "�Quieres copiar esta gu�a?"); 	
		dialog.show();*/
    }

	
	
	
	
	public void copy_educational_setting(View view){
		Dialog dialog = create_copy_educational_setting_dialog("Advertencia", "�Quieres copiar un contexto educativo de la biblioteca?");		
		dialog.show();
	}
	
	
	
	
	
	public void copy_technical_setting(View viw){
		Dialog dialog = create_copy_technical_setting_dialog("Advertencia", "�Quieres copiar un contexto t�cnico de la biblioteca?");		
		dialog.show();
	}
	
	
	
	
	
	public void create_educational_setting(View view){
		Dialog dialog = create_educational_setting_dialog("Advertencia", "Quieres crear un nuevo contexto educativo?");
		dialog.show();
	}
	
	
	
	
	
	public void create_technical_setting(View view){
		Dialog dialog = create_technical_setting_dialog("Advertencia", "Quieres crear un nuevo contexto t�cnico?");
		dialog.show();
	}
	
	
	
	
	
	public void eliminar(View view){
    	removeGuide();   	
    }
	
	
	
	
	
	public void getPhotoDialog(View view) {
		if (mode.getMode() == mode.EditMode()){
    		AlertDialog.Builder dialog = new AlertDialog.Builder(this);    		
    		dialog.setTitle("Advertencia");
    		dialog.setMessage("Escoge entre c�mara o galer�a");
    		dialog.setCancelable(true);
    		dialog.setPositiveButton("C�mara", new DialogInterface.OnClickListener() {   		 
    		   @Override
    		   public void onClick(DialogInterface dialog, int which) {
    			   dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
               }
    		});  		
    		dialog.setNegativeButton("Galer�a", new DialogInterface.OnClickListener() {   		 
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
	
	
	
	
	
	public void hideKeyboard(View view){
		hideMobileKeyboard();
	}
	
	
	
	
	
	public void seeActivities(View view){
		Intent intent = new Intent(this, ActivitiesView.class);
		intent.putExtra("guia", guide);
    	intent.putExtra("Language", language);
    	intent.putExtra("mode", mode);
    	intent.putExtra("owner", owner);
    	Log.d("owner", "owner= " + owner.getOwner());
	    startActivity(intent);
	}  
	
	
	
	
	
	public void showKeyboard(View view){
		showMobileKeyboard();
	}
	
	
	
	
	
	public void ver_aplicaciones(View view){
	      Intent intent = new Intent(this, ApplicationsView.class);
	      intent.putExtra("technical_setting_id", guide.technical_setting.id);
	      intent.putExtra("Language", language);
	      intent.putExtra("owner", owner);
	      intent.putExtra("from_activity", "GuideViewTabs");
	      startActivity(intent);
	}  

	
	
	
	
	public void verDireccion(View view){
		Intent intent = new Intent(this, MapView.class);
		intent.putExtra("educational_setting_id", guide.educational_setting.id);
		intent.putExtra("language", language);
		intent.putExtra("mode", mode);
		intent.putExtra("owner", owner);
	    startActivity(intent);
	}
	
	
	
	
	
	public void ver_dispositivos(View view){
	      Intent intent = new Intent(this, DevicesView.class);
	      intent.putExtra("technical_setting_id", guide.technical_setting.id);
	      intent.putExtra("Language", language);
	      intent.putExtra("owner", owner);
	      intent.putExtra("from_activity", "GuideViewTabs");
	      startActivity(intent);
	}
	
//////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////                                                          ////////////////////
////////////////////                                                          ////////////////////
////////////////////	         			M�TODOS				              ////////////////////
////////////////////														  ////////////////////
////////////////////														  ////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////	
	
	
	
	
	private boolean changes_detected_tab1(){
		Log.d("", "changes_detected_tab1");
		EditText title = (EditText) findViewById(R.id.editar_titulo_tab1);
		EditText description = (EditText) findViewById(R.id.editar_descripcion_tab1);
		boolean changes_detected = false;
		
		if (title.getText().toString().equals(guide.header.title) == false){
			changes_detected = true;
		}	
		
		if (description.getText().toString().equals(guide.header.description) == false){
			changes_detected = true;
		}		
				
		if (image_header_path != null){
			if (guide.header.imagePath == null) changes_detected = true;
			else if (guide.header.imagePath != image_header_path) changes_detected = true;
		}
		
		// Si hay cambios en la cabecera reiniciamos al salir para que se vean los cambios en la lista principal
		if (changes_detected == true) restart = YES;
		return changes_detected;	
	}
	
	
	
	
	
	private boolean changes_detected_tab2(){
		Log.d("", "changes_detected_tab2");
		EditText title = (EditText) findViewById(R.id.editar_titulo_tab2);
		EditText description = (EditText) findViewById(R.id.editar_descripcion_tab2);
		boolean changes_detected = false;
		if (title.getText().toString().equals(guide.technical_setting.name) == false){
			changes_detected = true;
		}			
		if (description.getText().toString().equals(guide.technical_setting.description) == false){
			changes_detected = true;
		}
		
		if (image_technical_setting_path != null){
			if (guide.technical_setting.imagePath == null) changes_detected = true;
			else if (guide.technical_setting.imagePath != image_technical_setting_path) changes_detected = true;
		}
		return changes_detected;	
	}
	
	
	
	
	
	private boolean changes_detected_tab3(){
		Log.d("", "changes_detected_tab3");
		boolean changes_detected = false;
		
		EditText edit_text = (EditText) findViewById(R.id.editText_titulo_tab3);
		if (edit_text.getText().toString().equals(guide.educational_setting.title) == false){
			changes_detected = true;
		}		
		
		edit_text = (EditText) findViewById(R.id.edit_description_tab3);
		if (edit_text.getText().toString().equals(guide.educational_setting.description) == false){
			changes_detected = true;
		}
		
		if (image_educational_setting_path != null){
			if (guide.educational_setting.imagePath == null) changes_detected = true;
			else if (guide.educational_setting.imagePath != image_educational_setting_path) changes_detected = true;
		}
		
		edit_text = (EditText) findViewById(R.id.editText_keywords);
		if (edit_text.getText().toString().equals(guide.educational_setting.keywords) == false){
			changes_detected = true;
		}	
		
		edit_text = (EditText) findViewById(R.id.editText_rangoEdades);
		if (edit_text.getText().toString().equals(guide.educational_setting.age_range) == false){
			changes_detected = true;
		}	
		
		edit_text = (EditText) findViewById(R.id.editText_fechaInicio);
		if (edit_text.getText().toString().equals(guide.educational_setting.start_date) == false){
			changes_detected = true;
		}	
		
		edit_text = (EditText) findViewById(R.id.editText_fechaFin);
		if (edit_text.getText().toString().equals(guide.educational_setting.end_date) == false){
			changes_detected = true;
		}	
		
		edit_text = (EditText) findViewById(R.id.address_editText);
		if (edit_text.getText().toString().equals(guide.educational_setting.address) == false){
			changes_detected = true;
		}	
		
		edit_text = (EditText) findViewById(R.id.editText_materia);
		for (int i=0; i<guide.educational_setting.vocabularies.size(); i++){
			if (guide.educational_setting.vocabularies.get(i).type.equals("subjects")){
				if (edit_text.getText().toString().equals(guide.educational_setting.vocabularies.get(i).term) == false){
					changes_detected = true;
				}	
			}			
		}	
		
		edit_text = (EditText) findViewById(R.id.editText_nivelEducativo);
		for (int i=0; i<guide.educational_setting.vocabularies.size(); i++){
			if (guide.educational_setting.vocabularies.get(i).type.equals("education_levels")){
				if (edit_text.getText().toString().equals(guide.educational_setting.vocabularies.get(i).term) == false){
					changes_detected = true;
				}	
			}			
		}
		
		edit_text = (EditText) findViewById(R.id.editText_lenguaje);
		for (int i=0; i<guide.educational_setting.vocabularies.size(); i++){
			if (guide.educational_setting.vocabularies.get(i).type.equals("languages")){
				if (edit_text.getText().toString().equals(guide.educational_setting.vocabularies.get(i).term) == false){
					changes_detected = true;
				}	
			}			
		}
				
		return changes_detected;	
	}
	
	
	
	
	
	private boolean changes_detected_tab4(){
		Log.d("", "changes_detected_tab4");
		EditText title = (EditText) findViewById(R.id.editar_titulo_tab4);
		EditText description = (EditText) findViewById(R.id.editar_descripcion_tab4);
		boolean changes_detected = false;
		
		if (title.getText().toString().equals(guide.activity_sequence.title) == false){
			changes_detected = true;
		}			
		
		if (description.getText().toString().equals(guide.activity_sequence.description) == false){
			changes_detected = true;
		}			
		
		if (image_activity_sequence_path != null){
			if (guide.activity_sequence.imagePath == null) changes_detected = true;
			else if (guide.activity_sequence.imagePath != image_activity_sequence_path) changes_detected = true;
		}
		
		return changes_detected;	
	}
	
	
	
	
	
	public void changeVisibilityEditTextsTab1(int visibility){
		
		EditText editText = (EditText) findViewById(R.id.editar_titulo_tab1);
    	editText.setVisibility(visibility);
    	
    	editText = (EditText) findViewById(R.id.editar_descripcion_tab1);
    	editText.setVisibility(visibility);

	}
	
	
	
	
	
	public void changeVisibilityEditTextsTab2(int visibility){
		
		EditText editText = (EditText) findViewById(R.id.editar_titulo_tab2);
    	editText.setVisibility(visibility);
    	
    	editText = (EditText) findViewById(R.id.editar_descripcion_tab2);
    	editText.setVisibility(visibility);
    	
	}
	
	
	
	
	
	public void changeVisibilityEditTextsTab3(int visibility){
		Log.d(LOGTAG, "changeVisibilityEditTextsTab3");
    	EditText editText = (EditText) findViewById(R.id.editText_titulo_tab3);
    	editText.setVisibility(visibility);
    	
    	editText = (EditText) findViewById(R.id.edit_description_tab3);
    	editText.setVisibility(visibility);
    	
    	editText = (EditText) findViewById(R.id.editText_rangoEdades);
    	editText.setVisibility(visibility);
    	
    	editText = (EditText) findViewById(R.id.editText_fechaInicio);
    	editText.setVisibility(visibility);
    	
    	editText = (EditText) findViewById(R.id.editText_fechaFin);
    	editText.setVisibility(visibility);
    	
    	editText = (EditText) findViewById(R.id.editText_keywords);
    	editText.setVisibility(visibility);
    	
    	editText = (EditText) findViewById(R.id.address_editText);
    	editText.setVisibility(visibility);
    	
    	editText = (EditText) findViewById(R.id.editText_materia);
    	editText.setVisibility(visibility);
    	
    	editText = (EditText) findViewById(R.id.editText_nivelEducativo);
    	editText.setVisibility(visibility);
    	
    	editText = (EditText) findViewById(R.id.editText_lenguaje);
    	editText.setVisibility(visibility);
    }
	
	
	
	
	
	public void changeVisibilityEditTextsTab4(int visibility){
		
		EditText editText = (EditText) findViewById(R.id.editar_titulo_tab4);
    	editText.setVisibility(visibility);
    	
    	editText = (EditText) findViewById(R.id.editar_descripcion_tab4);
    	editText.setVisibility(visibility);
    	
	}
	
	
	
	
	
	public void changeVisibilityTextViewsTab1(int visibility){
    	TextView textView = (TextView) findViewById(R.id.titulo_tab1);
    	textView.setVisibility(visibility);
    	
    	textView = (TextView) findViewById(R.id.descripcion_tab1);
    	textView.setVisibility(visibility);
	
    }
	
	
	
	
	
	public void changeVisibilityTextViewsTab2(int visibility){
    	
		TextView textView = (TextView) findViewById(R.id.titulo_tab2);
    	textView.setVisibility(visibility);
    	
    	textView = (TextView) findViewById(R.id.descripcion_tab2);
    	textView.setVisibility(visibility);	
    }
	
	
	
	
	
	public void changeVisibilitySpinnersTab3(int visibility){
    	
		Spinner spinner = (Spinner) findViewById(R.id.materia_spinner);
    	spinner.setVisibility(visibility);
    	
    	spinner = (Spinner) findViewById(R.id.education_level_spinner);
    	spinner.setVisibility(visibility);
    	
    	spinner = (Spinner) findViewById(R.id.language_spinner);
    	spinner.setVisibility(visibility);
    	
    }
	
	
	
	
	
	public void changeVisibilityTextViewsTab3(int visibility){
		Log.d(LOGTAG, "changeVisibilityTextViewsTab3");
    	TextView textView = (TextView) findViewById(R.id.titulo_tab3);
    	textView.setVisibility(visibility);
    	
    	textView = (TextView) findViewById(R.id.description_tab3);
    	textView.setVisibility(visibility);
    	
    	textView = (TextView) findViewById(R.id.textView_rangoEdades);
    	textView.setVisibility(visibility);
    	
    	textView = (TextView) findViewById(R.id.textView_fechaInicio);
    	textView.setVisibility(visibility);
    	
    	textView = (TextView) findViewById(R.id.textView_fechaFin);
    	textView.setVisibility(visibility);
    	
    	textView = (TextView) findViewById(R.id.textView_keywords);
    	textView.setVisibility(visibility);
    	
    	textView = (TextView) findViewById(R.id.address_textView);
    	textView.setVisibility(visibility);
    	
    	textView = (TextView) findViewById(R.id.textView_materia);
    	textView.setVisibility(visibility);
    	
    	textView = (TextView) findViewById(R.id.textView_nivelEducativo);
    	textView.setVisibility(visibility);
    	
    	textView = (TextView) findViewById(R.id.textView_lenguaje);
    	textView.setVisibility(visibility);
    	
    }
    
	
	
	
	
	public void changeVisibilityTextViewsTab4(int visibility){
		
    	TextView textView = (TextView) findViewById(R.id.titulo_tab4);
    	textView.setVisibility(visibility);
    	
    	textView = (TextView) findViewById(R.id.descripcion_tab4);
    	textView.setVisibility(visibility);
	
    }
    
    
	
	
 
	public void configEducationalLevelSpinner(){
		Log.d(LOGTAG, "configEducationalLevelSpinner");
		
		// BUSCAMOS EL EDUCATIONAL LEVEL EN LA GUIA Y CONSEGUIMOS SU ID Y SU TEXTO
    	String edLevel = ""; int edLevelId = -1;
    	Log.d("", "Buscando educational level en la guia...");
    	for (int i=0; i<guide.educational_setting.vocabularies.size(); i++){
    		if (guide.educational_setting.vocabularies.get(i).type.equals("education_levels")){
    			edLevel = guide.educational_setting.vocabularies.get(i).term;
    			edLevelId = guide.educational_setting.vocabularies.get(i).id;
    			Log.d("", "educational level encontrado: "+" term: "+ edLevel + " id: " + edLevelId);
    			break;
    		}
    	}
    	
    	// PREPARE VECTOR
    	final Vector<String> edLevelsVector = new Vector<String>();
    	Log.d("" , "preparando vector de educational levels...");
     	for (int i=0; i<vocabulary.educational_levels.size();i++){
       		if (i==0){
       			edLevelsVector.add(edLevel);
       			Log.d("", "a�adido term: " + edLevel + " en la posici�n: " + i);
       		}
        	if (edLevelId != vocabulary.educational_levels.get(i).getId()){
        		edLevelsVector.add(vocabulary.educational_levels.get(i).getTerm());  
        		Log.d("", "a�adido term: " + vocabulary.educational_levels.get(i).getTerm() + " en la posici�n: " + i);
        	}
       	}
    	
    	// CONFIG SPINNER
    	Spinner spinner = (Spinner) findViewById(R.id.education_level_spinner);
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(GuideViewTabs.this,android.R.layout.simple_list_item_1, edLevelsVector);
    	spinner.setAdapter(adapter);
    	spinner.setOnItemSelectedListener(new OnItemSelectedListener() {					
    		
    		@Override
			public void onItemSelected(AdapterView<?> parent, View view,int pos, long id) {
    			Log.d(LOGTAG, "onItemSelected (configEducationalLevelSpinner)");
    			EditText editText = (EditText) findViewById(R.id.editText_nivelEducativo);
    			editText.setText(edLevelsVector.get(pos));    			
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});	        	 
	}
	
	
	
	
	
	public void configLanguageSpinner(){
		Log.d(LOGTAG, "configLanguageSpinner");
		
		// BUSCAMOS EL IDIOMA EN LA GUIA Y CONSEGUIMOS SU ID Y SU TEXTO
    	String language = ""; int languageId = -1;
    	for (int i=0; i<guide.educational_setting.vocabularies.size(); i++){
    		if (guide.educational_setting.vocabularies.get(i).type.equals("languages")){
    			language = guide.educational_setting.vocabularies.get(i).term;
    			languageId = guide.educational_setting.vocabularies.get(i).id;
    			break;
    		}
    	}
    	
    	// PREPARE VECTOR
    	final Vector<String> languagesVector = new Vector<String>();
     	for (int i=0; i<vocabulary.languages.size();i++){
       		if (i==0) languagesVector.add(language);
        		if (languageId != vocabulary.languages.get(i).getId()) languagesVector.add(vocabulary.languages.get(i).getTerm());    		
       	}
    	    	
    	// CONFIG SPINNER
    	Spinner spinner = (Spinner) findViewById(R.id.language_spinner);
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(GuideViewTabs.this,android.R.layout.simple_list_item_1, languagesVector);
    	spinner.setAdapter(adapter);
    	spinner.setOnItemSelectedListener(new OnItemSelectedListener() {					
    		
    		@Override
			public void onItemSelected(AdapterView<?> parent, View view,int pos, long id) {
    			Log.d(LOGTAG, "onItemSelected (configLanguageSpinner)");   		
    			EditText editText = (EditText) findViewById(R.id.editText_lenguaje);
    			editText.setText(languagesVector.get(pos));    			
    		}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				
			}
		});	        	
	}
	
	
	
	
	
	public void configSubjectSpinner(){
		Log.d(LOGTAG, "configSubjectSpinner");
		
		// BUSCAMOS LA MATERIA EN LA GUIA Y CONSEGUIMOS SU ID Y SU TEXTO
    	String subject = ""; int subjectId = -1;
    	for (int i=0; i<guide.educational_setting.vocabularies.size(); i++){
    		if (guide.educational_setting.vocabularies.get(i).type.equals("subjects")){
    			subject = guide.educational_setting.vocabularies.get(i).term;
    			subjectId = guide.educational_setting.vocabularies.get(i).id;
    			break;
    		}
    	}
    	
		// PREPARE VECTOR
		final Vector<String> subjectsVector = new Vector<String>();
    	for (int i=0; i<vocabulary.subjects.size();i++){
    		Log.d("", "vocabulary subject: " +vocabulary.subjects.get(i).getTerm()+ "vocabulary subjectId: "+vocabulary.subjects.get(i).id);
    		if (i==0) subjectsVector.add(subject);
    		if (subjectId != vocabulary.subjects.get(i).getId()) subjectsVector.add(vocabulary.subjects.get(i).getTerm());    		
    	}
    	
    	// CONFIG SPINNER
    	Spinner spinner = (Spinner) findViewById(R.id.materia_spinner);
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(GuideViewTabs.this,android.R.layout.simple_list_item_1, subjectsVector);
    	spinner.setAdapter(adapter);
    	spinner.setOnItemSelectedListener(new OnItemSelectedListener() {					
    		
    		@Override
			public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
    			Log.d(LOGTAG, "onItemSelected (configSubjectSpinner):" + pos);    			
    			EditText editText = (EditText) findViewById(R.id.editText_materia);
    			editText.setText(subjectsVector.get(pos));   			
			}

			@Override
			public void onNothingSelected(AdapterView<?> arg0) {
				System.out.println("onNothingSelected");
			}
		});	   
    	
    	
	}
	
	
	
	
	
	private void configViewPager(){
		final ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);     
		// Crea el adaptador que devolver� un fragmento para cada una de las 3 secciones
        // de la Activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        // Configura el ViewPager con el adaptador de secciones.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);       
        // Cuando te mueves entre secciones, selecciona el correspondiente tab. 
        // Podemos usar ActionBar.Tab#select() para hacer esto si tenemos una referencia
        // al Tab
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                actionBar.setSelectedNavigationItem(position);              
            }
        });
        // Indicamos cuantas p�ginas queremos que est�n precargadas en memoria, en nuestro caso 4
        // 3 precargadas m�s 1 que se est� mostrando
        mViewPager.setOffscreenPageLimit(3);
        // Para cada una de las secciones, a�ade un tab a la action bar.
        actionBar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_blue_dark));
        actionBar.setStackedBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.orange_dark)));
        for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
        	// Crea un tab con el texto correspondiente al titulo de la pagina definido por el adaptador.
        	// Tambi�n especifica este Activity object, el que implementa el TabListener interface, 
        	// como la llamada (listener) cuando este tab est� seleccionado.
            actionBar.addTab(actionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
        }
	}
	
	
	
	
	
	public void copy_e_setting(){
		Intent intent = new Intent(this, EducationalSettingsView.class);
		intent.putExtra("guide_educational_setting", guide.educational_setting);
		intent.putExtra("language", language);
    	startActivity(intent);
	}
	
	
	
	
	
	public void copy_guide(){
		new GetDuplicate().execute();
	}
	
	
	
	
	
	public void copy_t_setting(){
		Intent intent = new Intent(this, TechnicalSettingsView.class);
		for (int i=0; i<guide.technical_setting.devices_id.size(); i++)
			Log.d("", "deviceId enviado: " + guide.technical_setting.devices_id.get(i));
		
		intent.putExtra("guide_technical_setting", guide.technical_setting);
		intent.putExtra("language", language);
    	startActivity(intent);
	}
	
	
	
	
	
	public void create_e_setting(){
		new CreateEducationalSetting().start();
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
	        	restart = YES;
	        	finish_activity();
	        }
	    });
		
		return dialog;
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
	
	
	
	
	
	public void create_t_setting(){
		new CreateTechnicalSetting().start();
	}
	
	
	
	
	
	public Dialog create_copy_guide_dialog(String title, String message){
		final Dialog dialog = new Dialog(this);  
		dialog.setContentView(R.layout.dialog_two_button_layout);
		dialog.setCancelable(true);
		
		dialog.setTitle(title);
		
		TextView msg = (TextView) dialog.findViewById(R.id.message);
		msg.setText(message);
		
		Button buttonYes = (Button) dialog.findViewById(R.id.yes);
		buttonYes.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialog.dismiss();
	        	copy_guide();
	        }
	    });
		
		Button buttonNo = (Button) dialog.findViewById(R.id.no);
		buttonNo.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialog.dismiss();
	        }
	    });
		
		return dialog;
	}
	
	
	
	
	
	public Dialog create_copy_educational_setting_dialog(String title, String message){
		final Dialog dialog = new Dialog(this);  
		dialog.setContentView(R.layout.dialog_two_button_layout);
		dialog.setCancelable(true);
		
		dialog.setTitle(title);
		
		TextView msg = (TextView) dialog.findViewById(R.id.message);
		msg.setText(message);
		
		Button buttonYes = (Button) dialog.findViewById(R.id.yes);
		buttonYes.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialog.dismiss();
	        	copy_e_setting();
	        }
	    });
		
		Button buttonNo = (Button) dialog.findViewById(R.id.no);
		buttonNo.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialog.dismiss();
	        }
	    });
		
		return dialog;
	}
	
	
	
	
	
	public Dialog create_copy_technical_setting_dialog(String title, String message){
		final Dialog dialog = new Dialog(this);  
		dialog.setContentView(R.layout.dialog_two_button_layout);
		dialog.setCancelable(true);
		
		dialog.setTitle(title);
		
		TextView msg = (TextView) dialog.findViewById(R.id.message);
		msg.setText(message);
		
		Button buttonYes = (Button) dialog.findViewById(R.id.yes);
		buttonYes.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialog.dismiss();
	        	copy_t_setting();
	        }
	    });
		
		Button buttonNo = (Button) dialog.findViewById(R.id.no);
		buttonNo.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialog.dismiss();
	        }
	    });
		
		return dialog;
	}
	
	
	
	
	
	public Dialog create_educational_setting_dialog(String title, String message){
		final Dialog dialog = new Dialog(this);  
		dialog.setContentView(R.layout.dialog_two_button_layout);
		dialog.setCancelable(true);
		
		dialog.setTitle(title);
		
		TextView msg = (TextView) dialog.findViewById(R.id.message);
		msg.setText(message);
		
		Button buttonYes = (Button) dialog.findViewById(R.id.yes);
		buttonYes.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialog.dismiss();
	        	create_e_setting();
	        }
	    });
		
		Button buttonNo = (Button) dialog.findViewById(R.id.no);
		buttonNo.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialog.dismiss();
	        }
	    });
		
		return dialog;
	}
	
	
	
	
	
	public Dialog create_technical_setting_dialog(String title, String message){
		final Dialog dialog = new Dialog(this);  
		dialog.setContentView(R.layout.dialog_two_button_layout);
		dialog.setCancelable(true);
		
		dialog.setTitle(title);
		
		TextView msg = (TextView) dialog.findViewById(R.id.message);
		msg.setText(message);
		
		Button buttonYes = (Button) dialog.findViewById(R.id.yes);
		buttonYes.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialog.dismiss();
	        	create_t_setting();
	        }
	    });
		
		Button buttonNo = (Button) dialog.findViewById(R.id.no);
		buttonNo.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialog.dismiss();
	        }
	    });
		
		return dialog;
	}
	
	
	
	
	
	public ProgressDialog createProgressDialog(String texto){
    	Log.d(LOGTAG, "createProgressDialog");
    	ProgressDialog progressDialog;
    	progressDialog = new ProgressDialog(this);   	
    	progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	progressDialog.setMessage(texto);
    	progressDialog.setCancelable(false);
    	progressDialog.show();
    	progressDialog.setContentView(R.layout.progress_dialog);
    	return progressDialog;
    }
	
	
	
	
	
	private void dispatchTakePictureIntent(int actionCode) {
		Log.d(LOGTAG, "dispatchTakePictureIntent");
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		switch(actionCode) {
		case ACTION_TAKE_PHOTO_B:
			File f = null;
			
			try {
				f = setUpPhotoFile();				
				mCurrentPhotoPath = f.getAbsolutePath();
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				mCurrentPhotoPath = null;
			}
			
			break;

		default:
			break;			
		} // switch

		startActivityForResult(takePictureIntent, actionCode);
	}

	
	
	
	
	
	protected void downloadImageTab1(){
		runOnUiThread(new Runnable() {
			public void run() {
				downloadImageTab1 = new DownloadImageTab1();
				downloadImageTab1.execute();
			}
		});
	}
	
	
	
	
	
	protected void downloadImageTab2(){
		runOnUiThread(new Runnable() {
			public void run() {
				downloadImageTab2 = new DownloadImageTab2();
				downloadImageTab2.execute();
			}
		});
	}
	
	
	
	
	
	protected void downloadImageTab3(){
		runOnUiThread(new Runnable() {
			public void run() {
				downloadImageTab3 = new DownloadImageTab3();
				downloadImageTab3.execute();
			}
		});
	}
	
	
	
	
	
	protected void downloadImageTab4(){
		runOnUiThread(new Runnable() {
			public void run() {
				downloadImageTab4 = new DownloadImageTab4();
				downloadImageTab4.execute();
			}
		});
	}
	
	
	
	
	
	public void edit_mode(){
		if (owner.getOwner() == owner.OwnerTrue()){
			runOnUiThread(new Runnable() {
				public void run() {
	        	
					// BOTONES COMENTAR COPIAR Y ELIMINAR
					Button comentar = (Button) findViewById(R.id.comentar);
					comentar.setVisibility(View.INVISIBLE);
					Button copiar = (Button) findViewById(R.id.copiar);
					copiar.setVisibility(View.INVISIBLE);
					Button eliminar = (Button) findViewById(R.id.eliminar);
					eliminar.setVisibility(View.INVISIBLE);  
	        	
					// BOTON SHOW/HIDE KEYBOARD
					Button hide_keyboard_button = (Button) findViewById(R.id.show_hide_keyboard);
					hide_keyboard_button.setVisibility(View.VISIBLE);
	        	
					// TAB 1
					edit_mode_tab1();
        
					// TAB 2
					edit_mode_tab2();

					// TAB 3
					edit_mode_tab3();
	        		        		        	
					// TAB 4
					edit_mode_tab4();
				}
    		});
    	}
    	else{   		
    		Looper.prepare();
    		AlertDialog.Builder dialog = new AlertDialog.Builder(this);    		
    		dialog.setTitle("Advertencia");
    		dialog.setMessage("No puedes editar esta gu�a. No eres el autor.");
    		dialog.setCancelable(false);
    		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {   		 
    		   @Override
    		   public void onClick(DialogInterface dialog, int which) {
    		      dialog.cancel();
    		   }
    		});  		
    		dialog.show();
    		Looper.loop();
    	}
    }
	
	
	
	
	
	public void edit_mode_tab1(){
		// TITULO
    	TextView titulo = (TextView) findViewById(R.id.titulo_tab1);
    	titulo.setVisibility(View.INVISIBLE);
    	String tituloActual = titulo.getText().toString();
    	EditText editTitle = (EditText) findViewById(R.id.editar_titulo_tab1);
    	editTitle.setText(tituloActual);
    	editTitle.setVisibility(View.VISIBLE);

    	// DESCRIPCION
    	TextView descripcion = (TextView) findViewById(R.id.descripcion_tab1);
    	descripcion.setVisibility(View.INVISIBLE);
    	String descripcionActual = descripcion.getText().toString();
    	EditText editDesc = (EditText) findViewById(R.id.editar_descripcion_tab1); 
    	editDesc.setText(descripcionActual);
    	editDesc.setVisibility(View.VISIBLE);
	}
	
	
	
	
	
	public void edit_mode_tab2(){
		// TITULO
    	TextView titulo = (TextView) findViewById(R.id.titulo_tab2);
    	titulo.setVisibility(View.INVISIBLE);
    	String tituloActual = titulo.getText().toString();
    	EditText editTitle = (EditText) findViewById(R.id.editar_titulo_tab2);
    	editTitle.setText(tituloActual);
    	editTitle.setVisibility(View.VISIBLE);
    	
    	// DESCRIPCION
    	TextView descripcion = (TextView) findViewById(R.id.descripcion_tab2);
    	descripcion.setVisibility(View.INVISIBLE);
    	String descripcionActual = descripcion.getText().toString();
    	EditText editDesc = (EditText) findViewById(R.id.editar_descripcion_tab2); 
    	editDesc.setText(descripcionActual);
    	editDesc.setVisibility(View.VISIBLE);
	}
	
	
	
	
	public void edit_mode_tab3(){
		configSubjectSpinner();
    	configEducationalLevelSpinner();
    	configLanguageSpinner();
    	
    	Spinner spinner = (Spinner) findViewById(R.id.materia_spinner);
    	spinner.setVisibility(View.VISIBLE);
    	
    	spinner = (Spinner) findViewById(R.id.education_level_spinner);
    	spinner.setVisibility(View.VISIBLE);
    	
    	spinner = (Spinner) findViewById(R.id.language_spinner);
    	spinner.setVisibility(View.VISIBLE);
    	
    	prepareEditTextsTab3ToEdit();
    	changeVisibilityTextViewsTab3(View.INVISIBLE);
    	changeVisibilityEditTextsTab3(View.VISIBLE);
    	
    	Button button = (Button) findViewById(R.id.button_fechaInicio);
    	button.setVisibility(View.VISIBLE);
    	
    	button = (Button) findViewById(R.id.button_fechaFin);
    	button.setVisibility(View.VISIBLE);
	}
	
	
	
	
	public void edit_mode_tab4(){
		// TITULO
    	Log.d(LOGTAG, "TAB4");
    	TextView titulo = (TextView) findViewById(R.id.titulo_tab4);
    	titulo.setVisibility(View.INVISIBLE);
    	String tituloActual = titulo.getText().toString();
    	EditText editTitle = (EditText) findViewById(R.id.editar_titulo_tab4);
    	editTitle.setText(tituloActual);
    	editTitle.setVisibility(View.VISIBLE);
    	
    	// DESCRIPCION
    	TextView descripcion = (TextView) findViewById(R.id.descripcion_tab4);
    	descripcion.setVisibility(View.INVISIBLE);
    	String descripcionActual = descripcion.getText().toString();
    	EditText editDesc = (EditText) findViewById(R.id.editar_descripcion_tab4); 
    	editDesc.setText(descripcionActual);
    	editDesc.setVisibility(View.VISIBLE);
	}
	
	
	
	
	
	public void finish_activity(){
		Log.d(LOGTAG, "finish_activity");
		
		saveAuto();
						
		Intent i = new Intent();
		if (restart == YES){
			i.putExtra("restart", YES);
		}
		else{
			i.putExtra("restart", NO);
		}
		setResult(RESULT_OK, i);
		finish();
	}
	
	
	
	
	
	private void galleryAddPic() {
		Log.d(LOGTAG, "galleryAddPic");
		
	    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);	    
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	    
	    if (tab_selected == TAB1){
	    	//guide.header.imagePath = mCurrentPhotoPath;
	    	image_header_path = mCurrentPhotoPath;
	    	guide.header.imageUri = contentUri.toString();
	    }
	    if (tab_selected == TAB2){
	    	//guide.technical_setting.imagePath = mCurrentPhotoPath;
	    	image_technical_setting_path = mCurrentPhotoPath;
	    	guide.technical_setting.imageUri = contentUri.toString();
	    }
	    if (tab_selected == TAB3){
	    	//guide.educational_setting.imagePath = mCurrentPhotoPath;
	    	image_educational_setting_path = mCurrentPhotoPath;
	    	guide.educational_setting.image_Uri = contentUri.toString();
	    }
	    if (tab_selected == TAB4){
	    	//guide.activity_sequence.imagePath = mCurrentPhotoPath;
	    	image_activity_sequence_path = mCurrentPhotoPath;
	    	guide.activity_sequence.image_Uri = contentUri.toString();
	    }
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
	
	
	
	
	
	public Bitmap getBitmapFromImageView(int imageViewId){
		ImageView iv = (ImageView) findViewById(imageViewId);	
		iv.buildDrawingCache();
		
		return iv.getDrawingCache();		
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
        	
        	if (tab_selected == TAB1){
        		guide.header.imageUri = uri.toString();
        		image_header_path = absolutePath;
        		//guide.header.imagePath = absolutePath;
        	}
        	if (tab_selected == TAB2){
        		guide.technical_setting.imageUri = uri.toString();
        		image_technical_setting_path = absolutePath;
        		//guide.technical_setting.imagePath = absolutePath;
        	}
        	if (tab_selected == TAB3){
        		guide.educational_setting.image_Uri = uri.toString();
        		image_educational_setting_path = absolutePath;
        		//guide.educational_setting.imagePath = absolutePath;
        	}
        	if (tab_selected == TAB4){
        		guide.activity_sequence.image_Uri = uri.toString();
        		image_activity_sequence_path = absolutePath;
        		//guide.activity_sequence.imagePath = absolutePath;
        	}
        	
            setImage(bounds, absolutePath);
            Log.d("GuideViewTabs", "Uri: " + uri.toString() + ";path: " + absolutePath);
        } else {
            showErrorToast("imagen sin contenido"); 
        }
    }
	
	
	
	
	
	public Bitmap getImagePhotoUtils(Uri uri) {
		Log.d(LOGTAG, "getImagePhotoUtils");
		
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
	
	
	
	
	
	private void getOverflowMenu() {
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
	
	
	
	
	
	public void getOwner(){
		new GetOwner().execute();
	}
	
	
	
	
	
	public String getRealPathFromURI(Uri uri) {
        Cursor cursor = getContentResolver().query(uri, null, null, null, null); 
        cursor.moveToFirst(); 
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
        return cursor.getString(idx); 
    }
	
	
	

	
	public void getTab1() {
		get_tab1 = new GetTab1();
		get_tab1.execute();
	}
	
	
	
	
	
	public void getTab2() {
		get_tab2 = new GetTab2();
		get_tab2.execute();
	}
	
	
	
	
	
	public void getTab3(){
		get_tab3 = new GetTab3();
		get_tab3.execute();
	}
	
	
	
	
	
	public void getTab4(){
		get_tab4 = new GetTab4();
		get_tab4.execute();
	}
	
	
	
	
	
	public void getVocabulary(){
		Log.d(LOGTAG, "getVocabulary");
    	new GetVocabularySubjects().execute();		
		new GetVocabularyEducationLevels().execute();
		new GetVocabularyLanguages().execute();
	}
	
	
	
	
	
	public ArrayList<EducationLevel> getVocabularyEducationLevels() throws ClientProtocolException, IOException{
		Log.d("GuideViewTabs", "getVocabularyEducationLevels");
		String Url = new IP().ip+"/"+language.getStringLanguage()+"/vocabularies.json?type=education_levels";
		HttpGet solicitud = new HttpGet(Url);
		SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
		solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));				
		
		// PRUEBA
		/*Log.d(LOGTAG, "Url: " + Url);
		HttpResponse respuestaPrueba = new DefaultHttpClient().execute(solicitud);
		String json = EntityUtils.toString(respuestaPrueba.getEntity());
		Log.d(LOGTAG, "EducationLevels: " + json);*/
		// FIN DE PRUEBA
		
		HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);
		return new Funciones().readEducationLevelsStream(respuesta.getEntity());
	}
	
	
	
	
	
	public ArrayList<VocabularyLanguage> getVocabularyLanguages() throws ClientProtocolException, IOException{
		Log.d("GuideViewTabs", "getVocabularyLanguages");
		String Url = new IP().ip+"/"+language.getStringLanguage()+"/vocabularies.json?type=languages";
		HttpGet solicitud = new HttpGet(Url);
		SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
		solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));				
		
		// PRUEBA
		/*Log.d(LOGTAG, "Url: " + Url);
		HttpResponse respuestaPrueba = new DefaultHttpClient().execute(solicitud);
		String json = EntityUtils.toString(respuestaPrueba.getEntity());
		Log.d(LOGTAG, "Languages: " + json);*/
		// FIN DE PRUEBA
		
		HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);
		return new Funciones().readLanguagesStream(respuesta.getEntity());
	}
	
	
	
	
	
	public ArrayList<Subject> getVocabularySubjects() throws ClientProtocolException, IOException{
		String Url = new IP().ip+"/"+language.getStringLanguage()+"/vocabularies.json?type=subjects";
		HttpGet solicitud = new HttpGet(Url);
		SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
		solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
		
		// PRUEBA
		/*Log.d(LOGTAG, Url);
		HttpResponse respuestaPrueba = new DefaultHttpClient().execute(solicitud);
		String json = EntityUtils.toString(respuestaPrueba.getEntity());
		Log.d(LOGTAG, "Subjects: " + json);*/
		// FIN DE PRUEBA
		
		HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);
		return  new Funciones().readSubjectsStream(respuesta.getEntity());
	}
	
	
	
	
	
	public EducationalSetting getWholeViewEducationalSettings() throws ClientProtocolException, IOException{
		String Url = new IP().ip+"/"+language.getStringLanguage()+"/educationalSettings/getWholeView.json?id="+guide.educational_setting.id;
       	HttpGet solicitud = new HttpGet(Url);	
       	SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
       	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));

        // PRUEBA
       	/*Log.d(LOGTAG, "Url: " + Url);
		HttpResponse respuestaPrueba = new DefaultHttpClient().execute(solicitud);
		Log.d(LOGTAG, "JSON WHOLE VIEW EducationalSETTING: "+EntityUtils.toString(respuestaPrueba.getEntity()));*/
		// FIN DE PRUEBA
		
		HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);
		EducationalSetting educationalSetting = new Funciones().readJsonESStream(respuesta.getEntity()).get(0);
		
		return educationalSetting;
	}
	
	
	
	
		
	private void handleBigCameraPhoto() throws IOException {
		Log.d(LOGTAG, "handleBigCameraPhoto");
		
		if (mCurrentPhotoPath != null) {
			Log.d("GuideViewTabs", "mCurrentPhotoPath: " + mCurrentPhotoPath);
			setPic();
			galleryAddPic();
			mCurrentPhotoPath = null;
		}
	}
	

	
	
	
	private void hideMobileKeyboard(){
    	// OCULTAR TECLADO
    	EditText editText = (EditText) findViewById(R.id.editar_descripcion_tab4);
    	InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }
	
	
	
	
	
	public int isInVocabulary(String string, String type){
    	if (type.equals("subject")){
    		int id = -1;
    		for (int i=0; i<vocabulary.subjects.size(); i++)
    			if (string.equals(vocabulary.subjects.get(i).getTerm())) id = vocabulary.subjects.get(i).getId();
    		return id;
    	}
    	else if (type.equals("education_level")){
    		int id = -1;
    		for (int i=0; i<vocabulary.educational_levels.size(); i++)
    			if (string.equals(vocabulary.educational_levels.get(i).getTerm())) id = vocabulary.educational_levels.get(i).getId();
    		return id;
    	}
    	else if (type.equals("language")){
    		int id = -1;
    		for (int i=0; i<vocabulary.languages.size(); i++)
    			if (string.equals(vocabulary.languages.get(i).getTerm())) id = vocabulary.languages.get(i).getId();
    		return id;
    	}
    	else return 0;
    }
	
	
	
	
	
	public void loadImageTab1(){
		runOnUiThread(new Runnable() {
	        public void run() {
	        	ImageView imageView = (ImageView) findViewById(R.id.imagen_tab1);
	        	if (guide.header.imageBitmap != null) imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(guide.header.imageBitmap, 12, GuideViewTabs.this));
	        	else {
	        		Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.imagen);
		   		 	imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, GuideViewTabs.this));
	        	}	        	
	        }
	    });
	}
	
	
	
	
	
	public void loadImageTab2(){
		runOnUiThread(new Runnable() {
	        public void run() {
	        	ImageView imageView = (ImageView) findViewById(R.id.imagen_tab2);
	        	if (guide.technical_setting.imageBitmap != null) imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(guide.technical_setting.imageBitmap, 12, GuideViewTabs.this));
	        	else {
	        		Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.imagen);
		   		 	imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, GuideViewTabs.this));
	        	}	
	        }
	    });
	}
	
	
	
	
	
	public void loadImageTab3(){
		runOnUiThread(new Runnable() {
	        public void run() {
	        	ImageView imageView = (ImageView) findViewById(R.id.imagen_tab3);
	        	if (guide.educational_setting.imageBitmap != null) imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(guide.educational_setting.imageBitmap, 12, GuideViewTabs.this));
	        	else {
	        		Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.imagen);
		   		 	imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, GuideViewTabs.this));
	        	}
	        }
	    });
	}
	
	
	
	
	
	public void loadImageTab4(){
		runOnUiThread(new Runnable() {
	        public void run() {
	        	ImageView imageView = (ImageView) findViewById(R.id.imagen_tab4);
	        	if (guide.activity_sequence.imageBitmap != null) imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(guide.activity_sequence.imageBitmap, 12, GuideViewTabs.this));
	        	else {
	        		Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.imagen);
		   		 	imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, GuideViewTabs.this));
	        	}	
	        }
	    });
	}
	
	
	
	
	
	public void load_TextViews_tab1(final String Titulo, final String Descripcion){
		runOnUiThread(new Runnable() {
	        public void run() {
	        	
	        	TextView textView = (TextView) findViewById(R.id.titulo_tab1);        	
	            if (guide.header.title.equals("") == false) textView.setText(guide.header.title);
	            else textView.setText(getResources().getString(R.string.title));
	            
	            textView = (TextView) findViewById(R.id.descripcion_tab1);
	            if (guide.header.description.equals("") == false) textView.setText(guide.header.description);
	            else textView.setText(getResources().getString(R.string.description));
	        	
	        }
	    });
	}
	
	
	
	
	
	public void load_TextViews_tab2(final String Titulo, final String Descripcion){
		runOnUiThread(new Runnable() {
	        public void run() {
	        	
	        	TextView textView = (TextView) findViewById(R.id.titulo_tab2);        	
	            if (guide.technical_setting.name.equals("") == false) textView.setText(guide.technical_setting.name);
	            else textView.setText(getResources().getString(R.string.title));
	            
	            textView = (TextView) findViewById(R.id.descripcion_tab2);
	            if (guide.technical_setting.description.equals("") == false) textView.setText(guide.technical_setting.description);
	            else textView.setText(getResources().getString(R.string.description));
	        	
	        }
	    });
	}
	
	
	
	
	
	public void load_TextViews_tab3(){
		runOnUiThread(new Runnable() {
	        public void run() {
	        	
	        	TextView textView = (TextView) findViewById(R.id.titulo_tab3);
	        	textView.setText(guide.educational_setting.title);
	        	
	        	textView = (TextView) findViewById(R.id.description_tab3);
	        	textView.setText(guide.educational_setting.description);
	        	
	        	textView = (TextView) findViewById(R.id.textView_keywords);
	        	textView.setText(guide.educational_setting.keywords);        		        	
	        	
	        	textView = (TextView) findViewById(R.id.textView_rangoEdades);
	        	textView.setText(guide.educational_setting.age_range);
	        	
	        	textView = (TextView) findViewById(R.id.textView_fechaInicio);
	        	textView.setText(guide.educational_setting.start_date);
	        	
	        	textView = (TextView) findViewById(R.id.textView_fechaFin);
	        	textView.setText(guide.educational_setting.end_date);
	        	
	            textView_address = (TextView) findViewById(R.id.address_textView);
	        	textView_address.setText(guide.educational_setting.address);
	        	
	        	textView = (TextView) findViewById(R.id.textView_materia);
	        	String subject = null;
	        	for (int i=0; i<guide.educational_setting.vocabularies.size(); i++){
	        		if (guide.educational_setting.vocabularies.get(i).type.equals("subjects"))
	        			subject = guide.educational_setting.vocabularies.get(i).term;
	        	}
	        	textView.setText(subject);
	        	
	        	textView = (TextView) findViewById(R.id.textView_nivelEducativo);
	        	String education_level = null;
	        	for (int i=0; i<guide.educational_setting.vocabularies.size(); i++){
	        		if (guide.educational_setting.vocabularies.get(i).type.equals("education_levels"))
	        			education_level = guide.educational_setting.vocabularies.get(i).term;
	        	}
	        	textView.setText(education_level);
	        	
	        	textView = (TextView) findViewById(R.id.textView_lenguaje);
	        	String language = null;
	        	for (int i=0; i<guide.educational_setting.vocabularies.size(); i++){
	        		if (guide.educational_setting.vocabularies.get(i).type.equals("languages"))
	        			language = guide.educational_setting.vocabularies.get(i).term;
	        	}
	        	textView.setText(language);
	        		        	
	        }
	    });
	}
	
	
	
	
	
	public void load_TextViews_tab4(){
		runOnUiThread(new Runnable() {
	        public void run() {
	        	TextView titulo = (TextView) findViewById(R.id.titulo_tab4);
	        	if (guide.activity_sequence.title.equals("") == false) titulo.setText(guide.activity_sequence.title);
	        	TextView descripcion = (TextView) findViewById(R.id.descripcion_tab4);
	        	if (guide.activity_sequence.description.equals("") == false) descripcion.setText(guide.activity_sequence.description);
	        }
	    });
	}

	

	
	
	public static int nearest2pow(int value) {
        return value == 0 ? 0 : (32 - Integer.numberOfLeadingZeros(value - 1)) / 2;
    }
	
	
	
	
	
	public void prepareEditTextsTab3ToEdit(){
		Log.d(LOGTAG, "prepareEditTextsTab3ToEdit");
    	TextView textView = (TextView) findViewById(R.id.titulo_tab3);
    	EditText editText = (EditText) findViewById(R.id.editText_titulo_tab3);
    	editText.setText(textView.getText().toString());
    	
    	textView = (TextView) findViewById(R.id.description_tab3);
    	editText = (EditText) findViewById(R.id.edit_description_tab3);
    	editText.setText(textView.getText().toString());
    	
    	textView = (TextView) findViewById(R.id.textView_rangoEdades);
    	editText = (EditText) findViewById(R.id.editText_rangoEdades);
    	editText.setText(textView.getText().toString());
    	
    	textView = (TextView) findViewById(R.id.textView_fechaInicio);
    	editText = (EditText) findViewById(R.id.editText_fechaInicio);
    	editText.setText(textView.getText().toString());
    	
    	textView = (TextView) findViewById(R.id.textView_fechaFin);
    	editText = (EditText) findViewById(R.id.editText_fechaFin);
    	editText.setText(textView.getText().toString());
    	
    	textView = (TextView) findViewById(R.id.textView_keywords);
    	editText = (EditText) findViewById(R.id.editText_keywords);
    	editText.setText(textView.getText().toString());
    	
    	textView = (TextView) findViewById(R.id.address_textView);
    	editText = (EditText) findViewById(R.id.address_editText);
    	editText.setText(textView.getText().toString());
    	
    	textView = (TextView) findViewById(R.id.textView_materia);
    	editText = (EditText) findViewById(R.id.editText_materia);
    	String subject="";
    	for (int i=0; i<guide.educational_setting.vocabularies.size(); i++){
    		if (guide.educational_setting.vocabularies.get(i).type.equals("subjects"))
    			subject = guide.educational_setting.vocabularies.get(i).term;
    	}
    	editText.setText(subject);
    	
    	textView = (TextView) findViewById(R.id.textView_nivelEducativo);
    	editText = (EditText) findViewById(R.id.editText_nivelEducativo);
    	String edLevel="";
    	for (int i=0; i<guide.educational_setting.vocabularies.size(); i++){
    		if (guide.educational_setting.vocabularies.get(i).type.equals("education_levels"))
    			edLevel = guide.educational_setting.vocabularies.get(i).term;
    	}
    	editText.setText(edLevel);
    	
    	textView = (TextView) findViewById(R.id.textView_lenguaje);
    	editText = (EditText) findViewById(R.id.editText_lenguaje);
    	String language="";
    	for (int i=0; i<guide.educational_setting.vocabularies.size(); i++){
    		if (guide.educational_setting.vocabularies.get(i).type.equals("languages"))
    			language = guide.educational_setting.vocabularies.get(i).term;
    	}
    	editText.setText(language);
    }
	
	
	
	
	
	public void removeGuide(){
		if (owner.getOwner() == owner.OwnerTrue()){
    		AlertDialog.Builder dialog = new AlertDialog.Builder(this);    		
    		dialog.setTitle("Advertencia");
    		dialog.setMessage("�Seguro que quieres eliminar?");
    		dialog.setCancelable(false);
    		dialog.setPositiveButton("Si", new DialogInterface.OnClickListener() {   		 
    		   @Override
    		   public void onClick(DialogInterface dialog, int which) {
    		      dialog.cancel();
    		      runOnUiThread(new Runnable() {
    	    	        public void run() {
    	    	        	new RemoveGuide(guide.id).start();
    	    	        }
    	    		});
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
    		AlertDialog.Builder dialog = new AlertDialog.Builder(this);    		
    		dialog.setTitle("Advertencia");
    		dialog.setMessage("No puedes eliminar esta gu�a. No eres el autor.");
    		dialog.setCancelable(false);
    		dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {   		 
    		   @Override
    		   public void onClick(DialogInterface dialog, int which) {
    		      dialog.cancel();
    		   }
    		});  		
    		dialog.show();
    	}
    }
	
	
	
	
	
	public void restart(){
        
		tab1_loading = true;
        tab2_loading = true;
        tab3_loading = true;
        tab4_loading = true;               
        
        getVocabulary(); 
                
        getTab1();       
	}
	
	
	
	
	
	public static Bitmap rotate_bitmap(Bitmap source, float angle){
	      Matrix matrix = new Matrix();
	      matrix.postRotate(angle);
	      return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
	
	
	
	
	
	public void save() throws InterruptedException, ExecutionException{
		System.out.println("save");
		if (tab_selected == TAB1) saveTab1();
		if (tab_selected == TAB2) saveTab2();
		if (tab_selected == TAB3) saveTab3();
		if (tab_selected == TAB4) saveTab4();
    	hideMobileKeyboard();
    }
	
	
	

	
	public void saveAuto(){
		if (mode.getMode() == Mode.EDIT_MODE){
			if (tab_selected == TAB1)				
				if (changes_detected_tab1()) saveTab1();
			
			if (tab_selected == TAB2)	
				if (changes_detected_tab2()) saveTab2();
			
			if (tab_selected == TAB3)	
				if (changes_detected_tab3()) 
					try {
						saveTab3();
					} catch (InterruptedException e) {
						e.printStackTrace();
						new Funciones().showToast(this, "Interrupted Exception");
					} catch (ExecutionException e) {
						e.printStackTrace();
						new Funciones().showToast(this, "Execution Exception");
					}
						
			if (tab_selected == TAB4)	
				if (changes_detected_tab4()) saveTab4();
		
		}	
	}
	
	
	
	
	
	public void saveAutoOnTabsChange(){
		Log.d("", "saveAuto");
		if (mode.getMode() == Mode.EDIT_MODE){
			if (tab_selected == TAB1){				
				if (previous_tab == TAB2){
					if (changes_detected_tab2()) saveTab2();
				}
				if (previous_tab == TAB3){
					if (changes_detected_tab3())
						try {
							saveTab3();
						} catch (InterruptedException e) {
							e.printStackTrace();
							new Funciones().showToast(this, "Interrupted Exception");
						} catch (ExecutionException e) {
							e.printStackTrace();
							new Funciones().showToast(this, "Execution Exception");
						}
				}
				if (previous_tab == TAB4){
					if (changes_detected_tab4()) saveTab4();
				}
			}
		
			if (tab_selected == TAB2){
				if (previous_tab == TAB1){
					if (changes_detected_tab1()) saveTab1();
				}
				if (previous_tab == TAB3){
					if (changes_detected_tab3()) 
						try {
							saveTab3();
						} catch (InterruptedException e) {
							e.printStackTrace();
							new Funciones().showToast(this, "Interrupted Exception");
						} catch (ExecutionException e) {
							e.printStackTrace();
							new Funciones().showToast(this, "Execution Exception");
						}
				}
				if (previous_tab == TAB4){
					if (changes_detected_tab4()) saveTab4();
				}
			}
		
			if (tab_selected == TAB3){
				if (previous_tab == TAB1){
					if (changes_detected_tab1()) saveTab1();
				}
				if (previous_tab == TAB2){
					if (changes_detected_tab2()) saveTab2();
				}
				if (previous_tab == TAB4){
					if (changes_detected_tab4()) saveTab4();
				}
			}
		
			if (tab_selected == TAB4){
				if (previous_tab == TAB1){
					if (changes_detected_tab1()) saveTab1();
				}
				if (previous_tab == TAB2){
					if (changes_detected_tab2()) saveTab2();
				}
				if (previous_tab == TAB3){
					if (changes_detected_tab3()) 
						try {
							saveTab3();
						} catch (InterruptedException e) {
							e.printStackTrace();
							new Funciones().showToast(this, "Interrupted Exception");
						} catch (ExecutionException e) {
							e.printStackTrace();
							new Funciones().showToast(this, "Execution Exception");
						}
				}
			}
		}
	}
	
	
	
	
	
	public void saveTab1(){
		System.out.println("saveTab1");
		
    	Header header = new Header();
    	EditText editText = (EditText) findViewById(R.id.editar_titulo_tab1);
    	header.title = editText.getText().toString();
    	guide.header.title = header.title;
    	
    	editText = (EditText) findViewById(R.id.editar_descripcion_tab1);
    	header.description = editText.getText().toString();
    	guide.header.description = header.description;
    	
    	header.imageBitmap = getBitmapFromImageView(R.id.imagen_tab1);
    	header.imageUri = guide.header.imageUri;
    	header.imagePath = image_header_path;
    	guide.header.imagePath = image_header_path;
    	
    	new SendHeader(header).start();
	}
	
	
	
	
	
	public void saveTab2(){
		System.out.println("saveTab2");
		
    	TechnicalSetting technicalSetting = new TechnicalSetting();
    	
    	EditText editText = (EditText) findViewById(R.id.editar_titulo_tab2);
    	technicalSetting.name = editText.getText().toString();
    	guide.technical_setting.name = technicalSetting.name;
    	
    	editText = (EditText) findViewById(R.id.editar_descripcion_tab2);
    	technicalSetting.description = editText.getText().toString();
    	guide.technical_setting.description = technicalSetting.description;
    	
    	technicalSetting.imageBitmap = getBitmapFromImageView(R.id.imagen_tab2);
    	technicalSetting.imageUri = guide.technical_setting.imageUri;
    	technicalSetting.imagePath = image_technical_setting_path;
    	guide.technical_setting.imagePath = image_technical_setting_path;
    	
    	new SendTechnicalSetting(technicalSetting).start();
	}
	
	
	
	
	
	public void saveTab3() throws InterruptedException, ExecutionException{
		System.out.println("saveTab3");
		
		EducationalSetting educationalSetting = new EducationalSetting();
    	
    	EditText editText = (EditText) findViewById(R.id.editText_titulo_tab3);
    	educationalSetting.title = editText.getText().toString();
    	guide.educational_setting.title = educationalSetting.title;
    	
    	editText = (EditText) findViewById(R.id.edit_description_tab3);
    	educationalSetting.description = editText.getText().toString();
    	guide.educational_setting.description = educationalSetting.description;
    	
    	editText = (EditText) findViewById(R.id.editText_rangoEdades);
    	educationalSetting.age_range = editText.getText().toString();
    	guide.educational_setting.age_range = educationalSetting.age_range;
    	
    	editText = (EditText) findViewById(R.id.editText_fechaInicio);
    	educationalSetting.start_date = editText.getText().toString();
    	guide.educational_setting.start_date = educationalSetting.start_date;
    	
    	editText = (EditText) findViewById(R.id.editText_fechaFin);
    	educationalSetting.end_date = editText.getText().toString();
    	guide.educational_setting.end_date = educationalSetting.end_date;
    	
    	editText = (EditText) findViewById(R.id.editText_keywords);
    	educationalSetting.keywords = editText.getText().toString();
    	guide.educational_setting.keywords = educationalSetting.keywords;
    	
    	editText = (EditText) findViewById(R.id.address_editText);
    	educationalSetting.address = editText.getText().toString();
    	guide.educational_setting.address = educationalSetting.address;
    	
    	editText = (EditText) findViewById(R.id.editText_materia);
    	String subject = editText.getText().toString();
    	for (int i=0; i<guide.educational_setting.vocabularies.size();i++){
    		if (guide.educational_setting.vocabularies.get(i).type.equals("subjects"))
    			guide.educational_setting.vocabularies.get(i).term = subject;
    	}
    	int subjectId = -1;
    	if ((subjectId = isInVocabulary(subject, "subject")) > 0){ // ya est� en el vocabulario 
    		Log.d(LOGTAG, "subject is in vocabulary");
    		VocabularyES vocabulary = new VocabularyES();
    		vocabulary.id = subjectId;
    		vocabulary.type = "subject";
    		vocabulary.term = editText.getText().toString();
    		educationalSetting.vocabularies.add(vocabulary);
    	}else{ // no est� en el vocabulario
    		Log.d(LOGTAG, "subject is not in vocabulary");
    		SendVocabulary sVX = new SendVocabulary();
    		sVX.execute(subject, "subject");
    		int id = sVX.get();
    		VocabularyES vocabulary = new VocabularyES();
    		vocabulary.id = id;
    		vocabulary.type = "subject";
    		vocabulary.term = editText.getText().toString();
    		educationalSetting.vocabularies.add(vocabulary);  		
    	}
    	
    	editText = (EditText) findViewById(R.id.editText_nivelEducativo);
    	String edLevel = editText.getText().toString();
    	for (int i=0; i<guide.educational_setting.vocabularies.size();i++){
    		if (guide.educational_setting.vocabularies.get(i).type.equals("education_levels"))
    			guide.educational_setting.vocabularies.get(i).term = edLevel;
    	}
    	int edLevelId = -1;
    	if ((edLevelId = isInVocabulary(edLevel, "education_level")) > 0){ // ya est� en el vocabulario 
    		Log.d(LOGTAG, "education level is in vocabulary");
    		VocabularyES vocabulary = new VocabularyES();
    		vocabulary.id = edLevelId;
    		vocabulary.type = "education_level";
    		vocabulary.term = editText.getText().toString();
    		educationalSetting.vocabularies.add(vocabulary);
    	}else{ // no est� en el vocabulario
    		Log.d(LOGTAG, "education level is not in vocabulary");
    		SendVocabulary sVX = new SendVocabulary();
    		sVX.execute(edLevel, "education_level");
    		int id = sVX.get();
    		VocabularyES vocabulary = new VocabularyES();
    		vocabulary.id = id;
    		vocabulary.type = "education_level";
    		vocabulary.term = editText.getText().toString();
    		educationalSetting.vocabularies.add(vocabulary);
    	}
    	
		
    	editText = (EditText) findViewById(R.id.editText_lenguaje);    	
    	String language = editText.getText().toString();
    	for (int i=0; i<guide.educational_setting.vocabularies.size();i++){
    		if (guide.educational_setting.vocabularies.get(i).type.equals("languages"))
    			guide.educational_setting.vocabularies.get(i).term = language;
    	}
    	int languageId = -1;
    	if ((languageId = isInVocabulary(language, "language")) > 0){ // ya est� en el vocabulario
    		Log.d(LOGTAG, "language is in vocabulary");
    		VocabularyES vocabulary = new VocabularyES();
    		vocabulary.id = languageId;
    		vocabulary.type = "language";
    		vocabulary.term = editText.getText().toString();
    		educationalSetting.vocabularies.add(vocabulary);
    	}else{ // no est� en el vocabulario
    		Log.d(LOGTAG, "language is not in vocabulary");
    		SendVocabulary sVX = new SendVocabulary();
    		sVX.execute(language, "language");
    		int id = sVX.get();
    		VocabularyES vocabulary = new VocabularyES();
    		vocabulary.id = id;
    		vocabulary.type = "language";
    		vocabulary.term = editText.getText().toString();
    		educationalSetting.vocabularies.add(vocabulary);
    	}
 
    	educationalSetting.imageBitmap = getBitmapFromImageView(R.id.imagen_tab3);
    	educationalSetting.image_Uri = guide.educational_setting.image_Uri;
    	educationalSetting.imagePath = image_educational_setting_path;
    	guide.educational_setting.imagePath = image_educational_setting_path;
    	
    	new SendEducationalSetting(educationalSetting).start();
	}




	
	public void saveTab4(){
		System.out.println("saveTab4");
		
    	ActivitySequence activitySequence = new ActivitySequence();
    	
    	EditText editText = (EditText) findViewById(R.id.editar_titulo_tab4);
    	activitySequence.title = editText.getText().toString();
    	guide.activity_sequence.title = activitySequence.title;
    	
    	editText = (EditText) findViewById(R.id.editar_descripcion_tab4);
    	activitySequence.description = editText.getText().toString();
    	guide.activity_sequence.description = activitySequence.description;
    	
    	activitySequence.imageBitmap = getBitmapFromImageView(R.id.imagen_tab4);
    	activitySequence.image_Uri = guide.activity_sequence.image_Uri;
    	activitySequence.imagePath = image_activity_sequence_path;
    	guide.activity_sequence.imagePath = image_activity_sequence_path;
    	
    	new SendActivitySequence(activitySequence).start();   	   	
    	
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
	
	
	
	
	
	
	
	
	
	
	
	public void setImage(Bitmap bitmap, String filename){
		Log.d(LOGTAG, "setImage");
		
        ImageView iv = null;
    	if (tab_selected == TAB1) iv = (ImageView) findViewById(R.id.imagen_tab1);
    	if (tab_selected == TAB2) iv = (ImageView) findViewById(R.id.imagen_tab2);
    	if (tab_selected == TAB3) iv = (ImageView) findViewById(R.id.imagen_tab3);
    	if (tab_selected == TAB4) iv = (ImageView) findViewById(R.id.imagen_tab4);
    	
    	// Rotate image   	
		try {
			ExifInterface ei = new ExifInterface(filename);
			int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch(orientation) {
    			
				case ExifInterface.ORIENTATION_ROTATE_90:
					Log.d("GuideViewTabs", "rotate 90");
    				bitmap = rotate_bitmap(bitmap, 90);
    			break;
    			
				case ExifInterface.ORIENTATION_ROTATE_180:
					Log.d("GuideViewTabs", "rotate 180");
    				bitmap = rotate_bitmap(bitmap, 180);
    				
				case ExifInterface.ORIENTATION_ROTATE_270:
					Log.d("GuideViewTabs", "rotate 270");
					bitmap = rotate_bitmap(bitmap, 270);
    			break;
    			
    			default:
    				Log.d("GuideViewTabs", "rotate unknown");
    			break;
			}
			iv.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bitmap, 12, GuideViewTabs.this));
		} catch (IOException e) {
			e.printStackTrace();
		}	
    }
	
	
	
	
	
	private void setPic() throws IOException {
		Log.d(LOGTAG, "setPic");
		
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */
		ImageView mImageView = null;
		if (tab_selected == TAB1) mImageView = (ImageView) findViewById(R.id.imagen_tab1);
    	if (tab_selected == TAB2) mImageView = (ImageView) findViewById(R.id.imagen_tab2);
    	if (tab_selected == TAB3) mImageView = (ImageView) findViewById(R.id.imagen_tab3);
    	if (tab_selected == TAB4) mImageView = (ImageView) findViewById(R.id.imagen_tab4);
    	
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
		bmOptions.inPurgeable = true;

		/* Decode the JPEG file into a Bitmap */
		Bitmap bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, bmOptions);
		Bitmap roundedCornerBitmap = ImageHelper.getRoundedCornerBitmap(bitmap, 12, GuideViewTabs.this);
		
		// Rotate image
		ExifInterface ei = new ExifInterface(mCurrentPhotoPath);
		int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

				switch(orientation) {
				    case ExifInterface.ORIENTATION_ROTATE_90:
				    	Log.d("GuideViewTabs", "rotate 90");
				    	roundedCornerBitmap = rotate_bitmap(roundedCornerBitmap, 90);
				        break;
				    case ExifInterface.ORIENTATION_ROTATE_180:
				    	Log.d("GuideViewTabs", "rotate 180");
				    	roundedCornerBitmap = rotate_bitmap(roundedCornerBitmap, 180);
				        break;
				    case ExifInterface.ORIENTATION_ROTATE_270:
				    	Log.d("GuideViewTabs", "rotate 270");
				    	roundedCornerBitmap = rotate_bitmap(roundedCornerBitmap, 270);
				    default:
				    	Log.d("GuideViewTabs", "rotate unknown");
				    	break;
				}
				
		/* Associate the Bitmap to the ImageView */
		mImageView.setImageBitmap(roundedCornerBitmap);
		
	}
	
	
	
	
	
	private File setUpPhotoFile() throws IOException {
		Log.d(LOGTAG, "setUpPhotoFile");
		
		File f = createImageFile();
		
		return f;
	}





	public void showStartDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(R.id.editText_fechaInicio);
        newFragment.show(getFragmentManager(), "startDatePicker");
    }

	
	
	
	
	public void showEndDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment(R.id.editText_fechaFin);
        newFragment.show(getFragmentManager(), "endDatePicker");
    }

	
	
	
	
	public void showErrorToast(String phrase){
    	Toast.makeText(this, phrase, Toast.LENGTH_SHORT).show();    	
    }
    
	
	
	
	
	public void showMobileKeyboard(){
		EditText editText = (EditText) findViewById(R.id.editar_descripcion_tab4);
		InputMethodManager inputMethodManager=(InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
	    inputMethodManager.toggleSoftInputFromWindow(editText.getApplicationWindowToken(), InputMethodManager.SHOW_FORCED, 0);
	}
	
	
	
	
	
	public void start(){
        getOwner();
        restart();
	}
	
	
	
	
	
	public void viewMode(){	
    	changeVisibilityEditTextsTab1(View.INVISIBLE);
    	changeVisibilityTextViewsTab1(View.VISIBLE);
		
    	changeVisibilityEditTextsTab2(View.INVISIBLE);
    	changeVisibilityTextViewsTab2(View.VISIBLE);
    	
		changeVisibilityEditTextsTab3(View.INVISIBLE);
		changeVisibilityTextViewsTab3(View.VISIBLE);
		changeVisibilitySpinnersTab3(View.GONE);
		
		Button button = (Button) findViewById(R.id.button_fechaInicio);
		button.setVisibility(View.GONE);
		
		button = (Button) findViewById(R.id.button_fechaFin);
		button.setVisibility(View.GONE);
		
		changeVisibilityEditTextsTab4(View.INVISIBLE);
		changeVisibilityTextViewsTab4(View.VISIBLE);
    	
    	hideMobileKeyboard();		       
		
    	// BOTON HIDEKEYBOARD
    	Button hide_keyboard_button = (Button) findViewById(R.id.show_hide_keyboard);
    	hide_keyboard_button.setVisibility(View.INVISIBLE);
    	
		// BOTONES COMENTAR COPIAR EDITAR Y ELIMINAR
        Button comentar = (Button) findViewById(R.id.comentar);
        comentar.setVisibility(View.VISIBLE);
        Button copiar = (Button) findViewById(R.id.copiar);
        copiar.setVisibility(View.VISIBLE);
        Button eliminar = (Button) findViewById(R.id.eliminar);
        eliminar.setVisibility(View.VISIBLE);
        
		progressDialog = createProgressDialog("Cargando contenido...");
        restart();
    }
	
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////															////////////////////
////////////////////															////////////////////
////////////////////					    CLASES                              ////////////////////
////////////////////															////////////////////
////////////////////															////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////////////////////////////////////////////////////////////////////////////////////
	
	class CreateEducationalSetting extends Thread {
        
        @Override 
        public void run() {
        	System.out.println("CreateEducationalSetting");
        	HttpClient cliente = new DefaultHttpClient();
        	HttpPost solicitud;
        	String BaseUrlPage =  new IP().ip+"/"+language.getStringLanguage()+"/educationalSettings/create.json";       	
            
			try {
				SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 				
				solicitud = new HttpPost(BaseUrlPage);
				solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				Log.d(LOGTAG, "Url: " + BaseUrlPage);
				HttpResponse response = cliente.execute(solicitud);	
				Log.d(LOGTAG, "Response: " + EntityUtils.toString(response.getEntity()));								
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}				
        }        
    }





	class CreateTechnicalSetting extends Thread {
        
        

        @Override 
        public void run() {
        	System.out.println("CreateTechnicalSetting");
        	HttpClient cliente = new DefaultHttpClient();
        	HttpPost solicitud;
        	String BaseUrlPage =  new IP().ip+"/"+language.getStringLanguage()+"/technicalSettings/create.json";       	
            
			try {
				SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 				
				solicitud = new HttpPost(BaseUrlPage);
				solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				Log.d(LOGTAG, "Url: " + BaseUrlPage);
				HttpResponse response = cliente.execute(solicitud);	
				Log.d(LOGTAG, "Response: " + EntityUtils.toString(response.getEntity()));
								
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}				
        }        
    }
	
	
	
	
	
	public static class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    	private int year; 
    	private int month; 
    	private int day; 
    	private int id;
    	
    	public DatePickerFragment(int id){
    		this.id=id;
    	}
    	
    	@Override
    	public Dialog onCreateDialog(Bundle savedInstanceState) {
    		// Use the current date as the default date in the picker
    		final Calendar c = Calendar.getInstance();
    		int year = c.get(Calendar.YEAR);
    		int month = c.get(Calendar.MONTH);
    		int day = c.get(Calendar.DAY_OF_MONTH);

    		// Create a new instance of DatePickerDialog and return it
    		return new DatePickerDialog(getActivity(), this, year, month, day);
    	}

    	public void onDateSet(DatePicker view, int yearOf, int monthOfYear, int dayOfMonth) {
    		// Do something with the date chosen by the user
    			year = yearOf; 
	    		month = monthOfYear; 
	    		day = dayOfMonth; 
	    		if (id == R.id.editText_fechaInicio) updateStartDate();
	    		else if (id == R.id.editText_fechaFin) updateEndDate();
    	}
    	
    	private void updateStartDate() { 
    		Log.d("", "updateStartDate");
        	startDateDisplay.setText( 
        			new StringBuilder() 
        			//Constant Month is 0 based so we have to add 1    			
        			.append(day)
        			.append("-") 
        			.append(month + 1)
        			.append("-") 
        			.append(year)
        			.append(" "));
        }
    	
    	private void updateEndDate() { 
    		Log.d("", "updateEndDate");
        	endDateDisplay.setText( 
        			new StringBuilder() 
        			//Constant Month is 0 based so we have to add 1    			
        			.append(day)
        			.append("-") 
        			.append(month + 1)
        			.append("-") 
        			.append(year)
        			.append(" "));
        }
    }

	
	public class DownloadImageTab1 extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... guideList) {
        	SharedPreferences preferences = getSharedPreferences( "datos", Context.MODE_PRIVATE);
			if (guide.header.imageUrl.equals("none") == false){
				try {
					String BaseUrlPage = new IP().ip + guide.header.imageUrl;
					HttpGet solicitud = new HttpGet(BaseUrlPage);	
					solicitud.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
					HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);
					HttpEntity entity = respuesta.getEntity();
					Bitmap bm = BitmapFactory.decodeStream(entity.getContent());
					if (bm != null) {
						guide.header.imageBitmap = bm;
					}
					loadImageTab1();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
			 
			return null; 			
        }
    }
	
	
	
	
	
	public class DownloadImageTab2 extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... guideList) {
        	SharedPreferences preferences = getSharedPreferences( "datos", Context.MODE_PRIVATE);
			if (guide.technical_setting.imageUrl.equals("none") == false){
				try {
					String BaseUrlPage = new IP().ip + guide.technical_setting.imageUrl;
					HttpGet solicitud = new HttpGet(BaseUrlPage);	
					solicitud.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
					HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);
					HttpEntity entity = respuesta.getEntity();
					Bitmap bm = BitmapFactory.decodeStream(entity.getContent());
					if (bm != null) guide.technical_setting.imageBitmap = bm;
					loadImageTab2();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
			
			return null; 			
        }
    }
	
	
	
	
	
	public class DownloadImageTab3 extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... guideList) {
        	if (guide.educational_setting.image_Url.equals("none") == false){
				
				try {					
					String BaseUrlPage = new IP().ip + guide.educational_setting.image_Url;
					HttpGet solicitud = new HttpGet(BaseUrlPage);	
					SharedPreferences preferences = getSharedPreferences( "datos", Context.MODE_PRIVATE);
					solicitud.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
					HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);
					HttpEntity entity = respuesta.getEntity();
					Bitmap bm = BitmapFactory.decodeStream(entity.getContent());
					if (bm != null) guide.educational_setting.imageBitmap = bm;					
					loadImageTab3();	
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
			
			
			return null; 			
        }
    }
	
	
	
	
	
	public class DownloadImageTab4 extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... guideList) {
        	SharedPreferences preferences = getSharedPreferences( "datos", Context.MODE_PRIVATE);
			HttpClient cliente = new DefaultHttpClient();
			HttpGet solicitud;
			HttpResponse respuesta;
			String BaseUrlPage;
			if (guide.activity_sequence.image_Url.equals("none") == false){
				try {
					BaseUrlPage = new IP().ip + guide.activity_sequence.image_Url;
					solicitud = new HttpGet(BaseUrlPage);	
					solicitud.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
					respuesta = cliente.execute(solicitud);
					HttpEntity entity = respuesta.getEntity();
					Bitmap bm = BitmapFactory.decodeStream(entity.getContent());
					if (bm != null) guide.activity_sequence.imageBitmap = bm;										
					loadImageTab4();
				} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}	
			}
			return null; 			
        }
    }
	


	
	
	public class GetOwner extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... empty) {
        	try {  
        		Log.d(LOGTAG, "GetOwner");
        		Log.d("owner", "owner= " + owner.getOwner());	        	
        		String baseUrl =  new IP().ip+"/"+language.getStringLanguage()+"/guides/search.json?owner=true&page=";
        		int numPage = 1;
        		String urlPage = baseUrl+numPage;
        		HttpGet solicitud = new HttpGet(urlPage);
        		SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
        		solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
        		List<Integer> mainIdList = new ArrayList<Integer>();
        		List<Integer> auxIdList = null;
        		do{	        		
        			HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);
        			HttpEntity entity = respuesta.getEntity();
        			if (entity == null) break;
        			auxIdList = new Funciones().readJsonIdsStream(entity);  			
        			numPage++;
        			urlPage = baseUrl+numPage;
        			solicitud = new HttpGet(urlPage);
        			for (int i=0; i<auxIdList.size(); i++){
        				mainIdList.add(auxIdList.get(i));
        			}
        		}while(auxIdList.size()==15);
        		// LISTA DE IDs
        		owner.setOwner(owner.OwnerNone());
        		for (int i=0;i<mainIdList.size();i++){
        			if (mainIdList.get(i) == guide.id){
        				owner.setOwner(owner.OwnerTrue());
        				break;
        			}    			
        		}
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}    	    			
        	return null; 			
        }
    }
    
    
    
    
    
	class GetDuplicate extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... empty) {       	
        	
            	try {
            		Log.d(LOGTAG, "GetDuplicate");
                	HttpClient cliente = new DefaultHttpClient();
                	String BaseUrlPage = new IP().ip+"/"+language.getStringLanguage()+"/guides/duplicate/id="+guide.id+".json";	
                	
                	HttpGet	solicitud = new HttpGet(BaseUrlPage);	
                	SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
                	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
					
                	Log.d("", "url: " + BaseUrlPage);
					HttpResponse respuesta = cliente.execute(solicitud);
					HttpEntity entity = respuesta.getEntity();
					
					// PRUEBA
					String json = EntityUtils.toString(entity);
					Log.d(LOGTAG, "Response Duplicate.json : "+json);
					// PRUEBA
					
					Looper.prepare();
            			Dialog dialog = new Funciones().create_ok_dialog(GuideViewTabs.this, "Advertencia", "La gu�a ha sido copiada");
            			dialog.show();
            		Looper.loop();
            		
            		restart = YES;
            		
            	} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}		
            	return null;
         }        
    }
	
	
	
	
	
    class GetTab1 extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... empty) {       	
        	
            	try {
            		Log.d(LOGTAG, "GetTab1");
                	String BaseUrlPage = new IP().ip+"/"+language.getStringLanguage()+"/guides/getWholeView.json?id="+guide.id;	
                	
                	HttpGet	solicitud = new HttpGet(BaseUrlPage);	
                	SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
                	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
                	
            		// PRUEBA
                	/*Log.d(LOGTAG, BaseUrlPage);
					HttpResponse respuestaP = new DefaultHttpClient().execute(solicitud);
					String json = EntityUtils.toString(respuestaP.getEntity());
					Log.d(LOGTAG, "JSON WHOLE VIEW HEADER: "+json);*/
					// FIN DE PRUEBA
					
					HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);
					HttpEntity entity = respuesta.getEntity();
					guide = new Funciones().readJsonGuidesStream(entity).get(0);
					
					load_TextViews_tab1(guide.header.title, guide.header.description);
					
					tab1_loading = false;
					if (((tab1_loading||tab2_loading||tab3_loading||tab4_loading) == false)) progressDialog.dismiss();
											
					downloadImageTab1();
					
					getTab2();
					getTab3();
					getTab4();
										
            	} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}		
            	return null;
         }        
    }
    
    
    
    
    
    class GetTab2 extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... empty) {   
        	
            	try {
            		Log.d(LOGTAG, "GetTab2");
                	String url =  new IP().ip+"/"+language.getStringLanguage()+"/technicalSettings/getWholeView.json?id="+guide.technical_setting.id;
                	
                	HttpGet	solicitud = new HttpGet(url);	
                	SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
                	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
                		
            		// PRUEBA
                	/*Log.d(LOGTAG, url);
					HttpResponse respuestaPrueba = new DefaultHttpClient().execute(solicitud);
					String json = EntityUtils.toString(respuestaPrueba.getEntity());
					Log.d(LOGTAG, "SMALL VIEW TSETTING: "+json);
                	Log.d(LOGTAG, "url: " + url);*/
					// FIN DE PRUEBA
					
					HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);
					HttpEntity entity = respuesta.getEntity();
					List<TechnicalSetting> tsettingList = new Funciones().readJsonTSStream(entity);
					guide.technical_setting = tsettingList.get(0);
					
					if (tsettingList.get(0).description.equals("")) tsettingList.get(0).description = "Descripci�n t�cnica";
					load_TextViews_tab2(tsettingList.get(0).name, tsettingList.get(0).description);
					
					tab2_loading = false;
					if (((tab1_loading||tab2_loading||tab3_loading||tab4_loading) == false)){
						progressDialog.dismiss();
					}
					
					downloadImageTab2();
					
            	} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}		
            	return null;
         }        
    }
	
	
	
	
	
	class GetTab3 extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... empty) {        	
            try {
            	Log.d(LOGTAG, "GetTab3");
				guide.educational_setting = getWholeViewEducationalSettings();													
				load_TextViews_tab3();
				
				tab3_loading = false;
				if (((tab1_loading||tab2_loading||tab3_loading||tab4_loading) == false)){
					progressDialog.dismiss(); 
				}
				downloadImageTab3();	
				
            } catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
            return null;
        }        
    }




	
	class GetTab4 extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... empty) {   
            try {
            	Log.d(LOGTAG, "GetTab4");
            	// GET WHOLE VIEW DEL ACTIVITY SEQUENCE
            	HttpClient cliente = new DefaultHttpClient();
            	String BaseUrlPage = new IP().ip+"/"+language.getStringLanguage()+"/activitySequence/getSmallView.json?id="+guide.activity_sequence.id;
            	HttpGet solicitud = new HttpGet(BaseUrlPage);	
            	SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
            	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
            	
            	// PRUEBA
            	/*Log.d(LOGTAG, BaseUrlPage);
				HttpResponse respuestaPrueba = cliente.execute(solicitud);
				String json = EntityUtils.toString(respuestaPrueba.getEntity());
				Log.d(LOGTAG, "SMALLVIEW ACTIVITY SEQUENCE: " + json);*/
				// PRUEBA
				
            	HttpResponse respuesta = cliente.execute(solicitud);
				ActivitySequence activitySequence = new Funciones().readJsonASStream(respuesta.getEntity()).get(0);
				guide.activity_sequence = activitySequence;
				Log.e(LOGTAG, "guide.activity_sequence.description: " + guide.activity_sequence.description);
				load_TextViews_tab4();		
				
				tab4_loading = false;
				if (((tab1_loading||tab2_loading||tab3_loading||tab4_loading) == false)){
					progressDialog.dismiss(); 
				}
				downloadImageTab4();
				
				
            } catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
            return null;
        }        
    }
	
	
	
	
	
 
    
    
    
    
	public class  GetVocabularySubjects extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... empty) {
        	try {  
        		Log.d(LOGTAG, "GetVocabularySubjects");
            	vocabulary.subjects = getVocabularySubjects();	
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}    	    			
        	return null; 			
        }
    }
	

	
	
	
    class GetVocabularyEducationLevels extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... empty) {     	
            try {
            	Log.d(LOGTAG, "GetVocabularyEducationLevels");
            	vocabulary.educational_levels = getVocabularyEducationLevels();					
            } catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}		
            return null;
        }        
    }




    class GetVocabularyLanguages extends AsyncTask<Void, Void, Void> {	
    	 protected Void doInBackground(Void... empty) {      	
    		try {
    			Log.d(LOGTAG, "GetVocabularyLanguages");
    			vocabulary.languages = getVocabularyLanguages();					
    		} catch (ClientProtocolException e) {
    			e.printStackTrace();
    		} catch (IOException e) {
    			e.printStackTrace();
    		}				
    		return null;
    	}        
    }
    
    
    
    
    
    class RemoveGuide extends Thread {
    	int id;
        
        public RemoveGuide(int id) {
        	this.id = id;
        }

        @Override 
        public void run() {
        	try {
        	HttpClient cliente = new DefaultHttpClient();
        	HttpDelete solicitud;
        	String BaseUrlPage;
        	BaseUrlPage =  new IP().ip+"/"+language.getStringLanguage()+"/guides/"+this.id+".json";
        	solicitud = new HttpDelete(BaseUrlPage);
        	SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
        	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));	
			cliente.execute(solicitud);
			
			Looper.prepare();
    			Dialog dialog = create_finish_dialog(GuideViewTabs.this, "Advertencia", "La gu�a ha sido eliminada");
    			dialog.show();
    		Looper.loop();
    		    		
    		
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}							
            
        }  
        	
    }


	

	
	class SendActivitySequence extends Thread {
		ActivitySequence activitySequence;
        
        public SendActivitySequence(ActivitySequence activitySequence) {
        	this.activitySequence = activitySequence;
        }

        @Override 
        public void run() {        	
			try {
				SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
				HttpClient cliente = new DefaultHttpClient();        	
	        	String BaseUrlPage =  new IP().ip+"/"+language.getStringLanguage()+"/activitySequence/"+guide.activity_sequence.id+".json";
	        	HttpPut solicitud = new HttpPut(BaseUrlPage);	
	        	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
	            MultipartEntity mpEntity = new MultipartEntity();	            
				mpEntity.addPart("title", new StringBody(activitySequence.title, Charset.forName("UTF-8")));
				mpEntity.addPart("description", new StringBody(activitySequence.description, Charset.forName("UTF-8")));
			
				if (activitySequence.imagePath != null){
					File file = new File(activitySequence.imagePath);
					mpEntity.addPart("element_image", new FileBody(file, "image/jpeg"));
				}
				
				/*if (activitySequence.privacity.equals("true")){
					ContentBody privacityBody = new StringBody(this.privacity);
					mpEntity.addPart("private", privacityBody);
				}*/
				
				solicitud.setEntity(mpEntity);
            	cliente.execute(solicitud);	
            	
            	Looper.prepare();
            	new Funciones().showToast(GuideViewTabs.this, "secuencia de actividades guardada");
            	Looper.loop();
            	
            	System.out.println(BaseUrlPage);
            	System.out.println("Datos enviados:");
            	System.out.println("title: "+activitySequence.title);
            	System.out.println("Description: "+activitySequence.description);
            	System.out.println("path: " + activitySequence.imagePath);
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}				
        }        
    }
	
	
	
	
	
	public class SendEducationalSetting extends Thread {
    	EducationalSetting edSetting;
        
        
        public SendEducationalSetting(EducationalSetting edSetting) {
        	this.edSetting = edSetting;
        }

        @Override 
        public void run() {
        	Log.d("GuideViewTabs", "SendEducationalSetting");
        	HttpClient cliente = new DefaultHttpClient();
        	
        	String BaseUrlPage =  new IP().ip+"/"+language.getStringLanguage()+"/educationalSettings/"+guide.educational_setting.id+".json";     	
            MultipartEntity mpEntity = new MultipartEntity();
			try {
				SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
				mpEntity.addPart("title", new StringBody(edSetting.title, Charset.forName("UTF-8")));
				
				mpEntity.addPart("description", new StringBody(edSetting.description, Charset.forName("UTF-8")));
				
				mpEntity.addPart("age_range", new StringBody(edSetting.age_range, Charset.forName("UTF-8")));
				
				mpEntity.addPart("start_date", new StringBody(edSetting.start_date, Charset.forName("UTF-8")));
				
				mpEntity.addPart("end_date", new StringBody(edSetting.end_date, Charset.forName("UTF-8")));
				
				mpEntity.addPart("keywords", new StringBody(edSetting.keywords, Charset.forName("UTF-8")));
				
				if (edSetting.address!=null) mpEntity.addPart("address", new StringBody(edSetting.address, Charset.forName("UTF-8")));
				
				if (edSetting.imagePath != null){
					Log.d("", "se env�a imagen");
					File file = new File(edSetting.imagePath);
					mpEntity.addPart("element_image", new FileBody(file, "image/jpeg"));
				}		
				
				/*String subject = null; String edLevel = null; String language = null;
				int subjectId = -1; int edLevelId = -1; int languageId = -1;								
				for (int i=0;i<edSetting.vocabularies.size();i++){
					if (edSetting.vocabularies.get(i).type.equals("subject")){
						mpEntity.addPart("subject", new StringBody(""+edSetting.vocabularies.get(i).id));
						subject = edSetting.vocabularies.get(i).term;
						subjectId = edSetting.vocabularies.get(i).id;
					}
					if (edSetting.vocabularies.get(i).type.equals("education_level")){
						mpEntity.addPart("education_level", new StringBody(""+edSetting.vocabularies.get(i).id));
						edLevel = edSetting.vocabularies.get(i).term;
						edLevelId = edSetting.vocabularies.get(i).id;
					}
					if (edSetting.vocabularies.get(i).type.equals("language")){
						mpEntity.addPart("language", new StringBody(""+edSetting.vocabularies.get(i).id));
						language = edSetting.vocabularies.get(i).term;
						languageId = edSetting.vocabularies.get(i).id;
					}
				}*/
				
				HttpPut solicitud = new HttpPut(BaseUrlPage);
				solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				solicitud.setEntity(mpEntity);
				HttpResponse response = cliente.execute(solicitud);	
			
				Log.d("", "url: " + BaseUrlPage);
				System.out.println("datos enviados:");
				System.out.println("title: "+edSetting.title);
				System.out.println("description: "+edSetting.description);
				System.out.println("age_range: "+edSetting.age_range);
				System.out.println("start_date: "+edSetting.start_date);
				System.out.println("end_date: "+edSetting.end_date);
				System.out.println("keywords: "+edSetting.keywords);
				if (edSetting.address!= null) System.out.println("address: " + edSetting.address);
				/*if (subjectId != -1) System.out.println("subject: "+subject+","+subjectId);
				if (edLevelId != -1) System.out.println("education_level: "+edLevel+","+edLevelId);
				if (languageId != -1) System.out.println("language: "+language+","+languageId);*/
            	System.out.println("path: " + edSetting.imagePath);
            	Log.d("", "response: [" + EntityUtils.toString(response.getEntity())+"]");
            	
				Looper.prepare();
            	new Funciones().showToast(GuideViewTabs.this, "contexto educativo guardado");
            	Looper.loop();
            	
				
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}				
        }        
    }
	
	
	
	
	
	class SendHeader extends Thread {
    	Header header;
        
        public SendHeader(Header header) {
        	this.header = header;
        }

        @Override 
        public void run() {        	
			try {
				System.out.println("SendHeader");
				SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
				HttpClient cliente = new DefaultHttpClient();        	
	        	String BaseUrlPage =  new IP().ip+"/"+language.getStringLanguage()+"/guides/"+guide.id+".json";
	        	HttpPut solicitud = new HttpPut(BaseUrlPage);	
	        	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
	        	
	        	MultipartEntity mpEntity = new MultipartEntity();	
    				        
	        	mpEntity.addPart("title", new StringBody(header.title, Charset.forName("UTF-8")));
				
				mpEntity.addPart("description", new StringBody(header.description, Charset.forName("UTF-8")));
				
				if (header.imagePath != null){					
					File file = new File(header.imagePath);
					ContentBody fotoBody = new FileBody(file, "image/jpeg");
					mpEntity.addPart("image", fotoBody);
				}							
				
				solicitud.setEntity(mpEntity);
				cliente.execute(solicitud);	
            	
				System.out.println(BaseUrlPage);
            	System.out.println("Datos enviados: ");
            	Log.d("", "t�tulo: " + header.title); 
            	System.out.println("Descripci�n: "+header.description);
            	System.out.println("path: " + header.imagePath);
            	
            	Looper.prepare();
            	new Funciones().showToast(GuideViewTabs.this, "cabecera guardada");
            	Looper.loop();        	
            	
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}				
        }        
    }
	
	
	
	
	
	class SendTechnicalSetting extends Thread {
		TechnicalSetting technicalSetting;
        
        public SendTechnicalSetting(TechnicalSetting technicalSetting) {
        	this.technicalSetting = technicalSetting;
        }

        @Override 
        public void run() {
        	System.out.println("SendTechnicalSetting");
        	HttpClient cliente = new DefaultHttpClient();
        	HttpPut solicitud;
        	String BaseUrlPage =  new IP().ip+"/"+language.getStringLanguage()+"/technicalSettings/"+guide.technical_setting.id+".json";       	
            MultipartEntity mpEntity = new MultipartEntity();
			try {
				SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
				mpEntity.addPart("title", new StringBody(technicalSetting.name, Charset.forName("UTF-8")));	
				mpEntity.addPart("description", new StringBody(technicalSetting.description, Charset.forName("UTF-8")));			
				if (technicalSetting.imagePath != null){
					File file = new File(guide.technical_setting.imagePath);
					mpEntity.addPart("element_image", new FileBody(file, "image/jpeg"));
				}				
				solicitud = new HttpPut(BaseUrlPage);
				solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				solicitud.setEntity(mpEntity);
				cliente.execute(solicitud);		
				
				System.out.println(BaseUrlPage);
				System.out.println("Datos enviados: ");
            	System.out.println("Title: "+technicalSetting.name);
            	System.out.println("Description: "+technicalSetting.description);
            	System.out.println("path: " + technicalSetting.imagePath);
            	
				Looper.prepare();
            	new Funciones().showToast(GuideViewTabs.this, "contexto t�cnico guardado");
            	Looper.loop();
            	
				
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}				
        }        
    }
	
	
	
	
	
	private class SendVocabulary extends AsyncTask<String, Integer, Integer> {
	     protected Integer doInBackground(String... str) {
	         String term = str[0];
	         String type = str[1];
	         int id = -1;
	         try {
	            	String Url = new IP().ip+"/"+language.getStringLanguage()+"/vocabularies.json";         	 
	               	HttpPost solicitud = new HttpPost(Url);	
	               	SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
	               	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));              	
	                MultipartEntity mpEntity = new MultipartEntity();    		
	    			if (type.equals("subject"))	mpEntity.addPart("subject", new StringBody(term, Charset.forName("UTF-8")));
	    			if (type.equals("education_level"))	mpEntity.addPart("education_level", new StringBody(term, Charset.forName("UTF-8")));
	    			if (type.equals("language"))	mpEntity.addPart("language", new StringBody(term, Charset.forName("UTF-8")));
	    			solicitud.setEntity(mpEntity);
    			
	    			Log.d("SendVocabulary", "url: " + Url);
	    			HttpResponse response = new DefaultHttpClient().execute(solicitud);
	    			id = new Funciones().readJsonId(response.getEntity());
	    			if (type.equals("subject")) Log.d("SendVocabulary", "id_subject: " + id);
	    			if (type.equals("education_level")) Log.d("SendVocabulary", "id_education_level: " + id);
	    			if (type.equals("language")) Log.d("SendVocabulary", "id_language: " + id);
	    			
	    			if (type.equals("subject"))	Log.d(LOGTAG, "subject enviada: " + term);
	    			if (type.equals("education_level"))	Log.d(LOGTAG, "nivel educativo enviado: " + term);
	    			if (type.equals("language")) Log.d(LOGTAG, "idioma enviado: " + term);
	    			
	    			
	    			// Send educational setting with id of (subject, education_level, language)
	            } catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			return id;				
	     }

	 }
	
	

	
//////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
////////////////////                                                          ////////////////////
////////////////////                                                          ////////////////////
////////////////////                CLASES DE LOS FRAGMENTS                   ////////////////////
////////////////////                                                          ////////////////////
////////////////////                                                          ////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////	
	
	

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
        	// getItem se llama para instanciar el fragmento para la pagina dada.
        	// Devuelve un PlaceholderFragment (definido debajo como una clase interna static).
        	System.out.println("position=" + position);
        	Fragment fragment = null;
        	if (position == 0) return HeaderFragment.newInstance();
        	if (position == 1) return TechnicalSettingFragment.newInstance();
        	if (position == 2) return PlaceholderMapFragment.newInstance();
        	if (position == 3) return ActivitySequenceFragment.newInstance();
        	else return fragment;
                     
        }

        @Override
        public int getCount() {
            // Show 4 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
                case 3:
                	return getString(R.string.title_section4).toUpperCase(l);
            }
            return null;
        }
        
    }
    
    
    
    

    
 
    public static class HeaderFragment extends Fragment {   
    private static View rootView;
        
        public static HeaderFragment newInstance() {  
            HeaderFragment fragment = new HeaderFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }
       
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        	System.out.println("onCreateView: Header");
        	
        	if (rootView != null) {
        		ViewGroup parent = (ViewGroup) rootView.getParent();
        	    if (parent != null) parent.removeView(rootView);
        	}
        	
        	try {
        		rootView = inflater.inflate(R.layout.vista_guia_fragment1, container, false);       	       	
            	load_views_tab1(rootView);	        		
        	} catch (InflateException e) {
        		
        	}
        		        
        	return rootView;        	
        }       
        
        @Override
    	public void onActivityCreated(Bundle savedInstanceState) {
    		super.onActivityCreated(savedInstanceState);
    	}
        
        
        
        public void load_views_tab1(View rootView){
        	TextView textView = (TextView) rootView.findViewById(R.id.titulo_tab1);        	
            if (guide.header.title.equals("") == false) textView.setText(guide.header.title);
            else textView.setText(getActivity().getResources().getString(R.string.title));
            
            textView = (TextView) rootView.findViewById(R.id.descripcion_tab1);
            if (guide.header.description.equals("") == false) textView.setText(guide.header.description);
            else textView.setText(getActivity().getResources().getString(R.string.description));
            
            ImageView imageView = (ImageView) rootView.findViewById(R.id.imagen_tab1);      
            Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.imagen);
            if (guide.header.imageBitmap != null) 
            	imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(guide.header.imageBitmap, 12, getActivity()));
            else imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, getActivity()));
            bm.recycle();
        }
        
    }

        
    
   
    
    public static class TechnicalSettingFragment extends Fragment {  
    	private static View rootView;
    	
        public static TechnicalSettingFragment newInstance() {        	
        	TechnicalSettingFragment fragment = new TechnicalSettingFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }
       
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) { 
        	System.out.println("onCreateView: TechnicalSetting");
        	if (rootView != null) {
        		ViewGroup parent = (ViewGroup) rootView.getParent();
        	    if (parent != null) parent.removeView(rootView);
        	}
        	
        	try {
        		rootView = inflater.inflate(R.layout.vista_guia_fragment2, container, false);       	       	
            	load_views_tab2(rootView);	        		
        	} catch (InflateException e) {
        		
        	}
        		        
        	return rootView;        	
        }    
        
        @Override
    	public void onActivityCreated(Bundle savedInstanceState) {
    		super.onActivityCreated(savedInstanceState);
    	}
        
        public void load_views_tab2(View rootView){
        	TextView titulo = (TextView) rootView.findViewById(R.id.titulo_tab2);
        	titulo.setText(guide.technical_setting.name);
        	
        	ImageView imageView = (ImageView) rootView.findViewById(R.id.imagen_tab2);      
            Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.imagen);
            if (guide.technical_setting.imageBitmap != null) 
            	imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(guide.header.imageBitmap, 12, getActivity()));
            else imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, getActivity()));
   		 	
        	TextView descripcion = (TextView) rootView.findViewById(R.id.descripcion_tab2);
        	descripcion.setText(guide.technical_setting.description);  	
            
        }
    }
    
    
    
   
    
    public static class PlaceholderMapFragment extends Fragment {
        private static View rootView;
        
        public static PlaceholderMapFragment newInstance() {
            PlaceholderMapFragment fragment = new PlaceholderMapFragment();
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {        	
        	System.out.println("onCreateView: Educational Setting");
        	if (rootView != null) {
        		ViewGroup parent = (ViewGroup) rootView.getParent();
        	    if (parent != null) parent.removeView(rootView);
        	}
        	
        	try {
        		rootView = inflater.inflate(R.layout.vista_guia_fragment3, container, false);       	       	
            	load_views_tab3(rootView);	        		
        	} catch (InflateException e) {
        		
        	}
        		        
        	return rootView;     
        }   
        
        @Override
    	public void onActivityCreated(Bundle savedInstanceState) {
    		super.onActivityCreated(savedInstanceState);
    	} 
        
        public void load_views_tab3(View rootView){
        	TextView textView = (TextView) rootView.findViewById(R.id.titulo_tab3);
        	textView.setText(guide.educational_setting.title);
        	
        	ImageView imageView = (ImageView) rootView.findViewById(R.id.imagen_tab3);      
            Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.imagen);
            if (guide.educational_setting.imageBitmap != null) 
            	imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(guide.educational_setting.imageBitmap, 12, getActivity()));
            else imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, getActivity()));
            
        	textView = (TextView) rootView.findViewById(R.id.description_tab3);
        	textView.setText(guide.educational_setting.description);
        	
        	textView = (TextView) rootView.findViewById(R.id.textView_keywords);
        	textView.setText(guide.educational_setting.keywords);        	
        	        	
        	textView = (TextView) rootView.findViewById(R.id.textView_rangoEdades);
        	textView.setText(guide.educational_setting.age_range);
        	
        	textView = (TextView) rootView.findViewById(R.id.textView_fechaInicio);
        	textView.setText(guide.educational_setting.start_date);
        	
        	textView = (TextView) rootView.findViewById(R.id.textView_fechaFin);
        	textView.setText(guide.educational_setting.end_date);
        	
        	textView = (TextView) rootView.findViewById(R.id.textView_materia);
        	String subject = null;
        	for (int i=0; i<guide.educational_setting.vocabularies.size(); i++){
        		if (guide.educational_setting.vocabularies.get(i).type.equals("subjects"))
        			subject = guide.educational_setting.vocabularies.get(i).term;
        	}
        	textView.setText(subject);
        	
        	textView = (TextView) rootView.findViewById(R.id.textView_nivelEducativo);
        	String education_level = null;
        	for (int i=0; i<guide.educational_setting.vocabularies.size(); i++){
        		if (guide.educational_setting.vocabularies.get(i).type.equals("education_levels"))
        			education_level = guide.educational_setting.vocabularies.get(i).term;
        	}
        	textView.setText(education_level);
        	
        	textView = (TextView) rootView.findViewById(R.id.textView_lenguaje);
        	String language = null;
        	for (int i=0; i<guide.educational_setting.vocabularies.size(); i++){
        		if (guide.educational_setting.vocabularies.get(i).type.equals("languages"))
        			language = guide.educational_setting.vocabularies.get(i).term;
        	}
        	textView.setText(language);
        	
        	startDateDisplay = (EditText) rootView.findViewById(R.id.editText_fechaInicio);
    		endDateDisplay = (EditText) rootView.findViewById(R.id.editText_fechaFin);
        }
    }     
    
    
    
    
    
    public static class ActivitySequenceFragment extends Fragment {  
    	private static View rootView;
    	
        public static ActivitySequenceFragment newInstance() {        	
        	ActivitySequenceFragment fragment = new ActivitySequenceFragment();
            Bundle args = new Bundle();
            fragment.setArguments(args);
            return fragment;
        }
       
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        	System.out.println("onCreateView: Activity Sequence");
        	if (rootView != null) {
        		ViewGroup parent = (ViewGroup) rootView.getParent();
        	    if (parent != null) parent.removeView(rootView);
        	}
        	
        	try {
        		rootView = inflater.inflate(R.layout.vista_guia_fragment4, container, false);       	       	           		        		
        	} catch (InflateException e) {
        		
        	}
        		        
        	return rootView;     
        }       
        
        @Override
    	public void onActivityCreated(Bundle savedInstanceState) {
        	load_views_tab4(rootView);
    		super.onActivityCreated(savedInstanceState);
    	}
        
        public void load_views_tab4(View rootView){
        	TextView titulo = (TextView) rootView.findViewById(R.id.titulo_tab4);
        	titulo.setText(guide.activity_sequence.title);
        	
        	ImageView imageView = (ImageView) rootView.findViewById(R.id.imagen_tab4);      
            Bitmap bm = BitmapFactory.decodeResource(getActivity().getResources(), R.drawable.imagen);
            if (guide.activity_sequence.imageBitmap != null) 
            	imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(guide.activity_sequence.imageBitmap, 12, getActivity()));
            else imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, getActivity()));
   		 	
        	TextView descripcion = (TextView) rootView.findViewById(R.id.descripcion_tab4);
        	descripcion.setText(guide.activity_sequence.description);  	
            
        }
        
    }
    
    
    
    
    
    
}
