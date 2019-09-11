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
import java.util.Vector;
import java.util.concurrent.ExecutionException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
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
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
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
import android.view.ViewConfiguration;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class EducationalSettingView extends ActionBarActivity {
	public IP ip = new IP();
	private static BitmapFactory.Options generalOptions;
	private static final String LOGTAG = "LogsAndroid";
	private Mode mode;
	private Vocabulary vocabulary;
	private Language language;
	private EducationalSetting educational_setting;
	private EducationalSetting guide_educational_setting;
	private ProgressDialog progressDialog;
	public GetTab3 get_tab3;
	private DownloadImageTab3 downloadImageTab3;
	private static final int ACTIVITY_SELECT_IMAGE = 1020;
	private static final int ACTION_TAKE_PHOTO_B = 1;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private String mCurrentPhotoPath;
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	private Uri mImageUri;
	private Owner owner;
	private static final int YES = 1;
	
	// CALENDAR
	private static EditText startDateDisplay;
	private static EditText endDateDisplay;
		
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_blue_dark));
		setContentView(R.layout.educational_setting_view);
		
		getOverflowMenu();
		
		Configuration config = getResources().getConfiguration();
        onConfigurationChanged(config);
        
		mode = new Mode();
		
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
		} else {
			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
		}
		
		vocabulary = new Vocabulary();
		
		owner = getIntent().getExtras().getParcelable("owner");
		educational_setting = getIntent().getExtras().getParcelable("educational_setting");
		language = getIntent().getExtras().getParcelable("language");
		guide_educational_setting = getIntent().getExtras().getParcelable("guide_educational_setting");
		
		loadTextViewsTab3();
		
		ImageView imagen = (ImageView) findViewById(R.id.imagen_tab3);
		Log.d(LOGTAG, "imageUri: " + educational_setting.image_Uri);
		if (educational_setting.imageBitmap != null){
			imagen.setImageBitmap(ImageHelper.getRoundedCornerBitmap(educational_setting.imageBitmap, 12, this));   	
		} 
		else {
			Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.imagen);
			imagen.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, this));
		}		
		
		startDateDisplay = (EditText) findViewById(R.id.editText_fechaInicio);
		endDateDisplay = (EditText) findViewById(R.id.editText_fechaFin);
		
		progressDialog = createProgressDialog("Cargando contenido...");
		restart();
	}	
	
	
	
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.educational_setting_view_mode, menu);
        return true;
    }  
	
	
	
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		Log.d(LOGTAG, "onPrepareOptionsMenu");
	    menu.clear();
	    if (mode.getMode() == mode.ViewMode()){
	    	getMenuInflater().inflate(R.menu.educational_setting_view_mode, menu);
	    	getOverflowMenu();
	    }
	    else{
	    	getMenuInflater().inflate(R.menu.educational_setting_edit_mode, menu);
	    }
	    return super.onPrepareOptionsMenu(menu);
	}
	
	
	
	
	
	@SuppressLint("NewApi")
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
		Log.d("", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        
        DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.educational_setting_view_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_guias));
        } 
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.educational_setting_view_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_land_guias));
        }
                
    }
	
	
	
	
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.    	
        int id = item.getItemId();
        if (id == R.id.revert){
        	finish();
        	return true;
        }
        if (id == R.id.edit_mode) {        
        	if (owner.getOwner() == Owner.TRUE){
        		mode.setMode(mode.EditMode());    
        		edit_mode();
    			invalidateOptionsMenu();
        	}
        	else new Funciones().showToast(this, "No puedes editar este contexto. No eres el autor");
    		return true;
    	}    
        if (id == R.id.view_mode) {        
    		mode.setMode(mode.ViewMode());    
    		viewMode();
    		invalidateOptionsMenu();
    		return true;
    	}   
        if (id == R.id.copy){
        	copy_educational_setting_to_guide("Advertencia", "¿Estás seguro de que quieres copiar este contexto educativo a la guía?");
        	return true;
        }
        if (id == R.id.save){
        	try {
				saveTab3();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (ExecutionException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
        	return true;
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
    	
		if (data.getIntExtra("restart", -1) == YES){
			progressDialog = createProgressDialog("Cargando contenido...");
			restart();	
		}
    }
	
	
	
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d("GuideViewTabs", "onSaveInstanceState");
		if (mImageUri != null) outState.putString("Uri", mImageUri.toString());
		
		super.onSaveInstanceState(outState);
	}
	
	
	
	
	
	public void copy_educational_setting_to_guide(String title, String message){
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
			   
			   Bitmap bm = getBitmapFromImageView(R.id.imagen_tab3);
			   educational_setting.imagePath = new Funciones().getRealPathFromBitmap(EducationalSettingView.this, bm);
			   Log.d("", "image path: " + educational_setting.imagePath);
			   
			   new CopyEducationalSetting(educational_setting, guide_educational_setting.id).start();	
			   
			   dialog.cancel();			  
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
	
	
	
	
	
	public void verDireccion(View view){
		Intent intent = new Intent(this, MapView.class);
		intent.putExtra("educational_setting_id", educational_setting.id);
		intent.putExtra("language", language);
		intent.putExtra("mode", mode);
		intent.putExtra("owner", owner);
		startActivityForResult(intent, 1);
	}
	
	
	
	
	
	public void getPhotoDialog(View view) {
		if (mode.getMode() == mode.EditMode()){
    		AlertDialog.Builder dialog = new AlertDialog.Builder(this);    		
    		dialog.setTitle("Advertencia");
    		dialog.setMessage("Escoge entre cámara o galería");
    		dialog.setCancelable(true);
    		dialog.setPositiveButton("Cámara", new DialogInterface.OnClickListener() {   		 
    		   @Override
    		   public void onClick(DialogInterface dialog, int which) {
    			   dispatchTakePictureIntent(ACTION_TAKE_PHOTO_B);
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
	
	
	
	
	
	public void configEducationalLevelSpinner(){
		Log.d(LOGTAG, "configEducationalLevelSpinner");
		
		// BUSCAMOS EL EDUCATIONAL LEVEL EN LA GUIA Y CONSEGUIMOS SU ID Y SU TEXTO
    	String edLevel = ""; int edLevelId = -1;
    	Log.d("", "Buscando educational level en la guia...");
    	for (int i=0; i<educational_setting.vocabularies.size(); i++){
    		if (educational_setting.vocabularies.get(i).type.equals("education_levels")){
    			edLevel = educational_setting.vocabularies.get(i).term;
    			edLevelId = educational_setting.vocabularies.get(i).id;
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
       			Log.d("", "añadido term: " + edLevel + " en la posición: " + i);
       		}
        	if (edLevelId != vocabulary.educational_levels.get(i).getId()){
        		edLevelsVector.add(vocabulary.educational_levels.get(i).getTerm());  
        		Log.d("", "añadido term: " + vocabulary.educational_levels.get(i).getTerm() + " en la posición: " + i);
        	}
       	}
    	
    	// CONFIG SPINNER
    	Spinner spinner = (Spinner) findViewById(R.id.education_level_spinner);
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(EducationalSettingView.this,android.R.layout.simple_list_item_1, edLevelsVector);
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
    	for (int i=0; i<educational_setting.vocabularies.size(); i++){
    		if (educational_setting.vocabularies.get(i).type.equals("languages")){
    			language = educational_setting.vocabularies.get(i).term;
    			languageId = educational_setting.vocabularies.get(i).id;
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
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(EducationalSettingView.this,android.R.layout.simple_list_item_1, languagesVector);
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
    	for (int i=0; i<educational_setting.vocabularies.size(); i++){
    		if (educational_setting.vocabularies.get(i).type.equals("subjects")){
    			subject = educational_setting.vocabularies.get(i).term;
    			subjectId = educational_setting.vocabularies.get(i).id;
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
    	ArrayAdapter<String> adapter = new ArrayAdapter<String>(EducationalSettingView.this,android.R.layout.simple_list_item_1, subjectsVector);
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
	
	
	
	
	
	private File createImageFile() throws IOException {
		Log.d(LOGTAG, "createImageFile");
		
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
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
	
	
	
	
	
	protected void downloadImageTab3(){
		runOnUiThread(new Runnable() {
			public void run() {
				downloadImageTab3 = new DownloadImageTab3();
				downloadImageTab3.execute();
			}
		});
	}
	
	
	
	
	
	public void edit_mode(){
			runOnUiThread(new Runnable() {
				public void run() {
					mode.setMode(Mode.EDIT_MODE);
	        		edit_mode_tab3();	        		        	
				}
    		});
    	
    	
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
	
	
	
	
	
	private void galleryAddPic() {
		Log.d(LOGTAG, "galleryAddPic");
		
	    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(mCurrentPhotoPath);
	    Uri contentUri = Uri.fromFile(f);	    
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	    	   	
	    educational_setting.imagePath = mCurrentPhotoPath;
	    educational_setting.image_Uri = contentUri.toString();
	    
	  
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
        	        	
        	educational_setting.image_Uri = uri.toString();
        	educational_setting.imagePath = absolutePath;
        	
        	
            setImage(bounds, absolutePath);
            Log.d("GuideViewTabs", "Uri: " + uri.toString() + ";path: " + absolutePath);
        } else {
            new Funciones().showToast(EducationalSettingView.this, "imagen sin contenido"); 
        }
    }
	
	
	
	
	
	public Bitmap getImagePhotoUtils(Uri uri) {
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
	
	
	
	
	
	public void getTab3(){
		get_tab3 = new GetTab3();
		get_tab3.execute();
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
		Log.d(LOGTAG, "Url: " + Url);
		HttpResponse respuestaPrueba = new DefaultHttpClient().execute(solicitud);
		String json = EntityUtils.toString(respuestaPrueba.getEntity());
		Log.d(LOGTAG, "EducationLevels: " + json);
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
		Log.d(LOGTAG, "Url: " + Url);
		HttpResponse respuestaPrueba = new DefaultHttpClient().execute(solicitud);
		String json = EntityUtils.toString(respuestaPrueba.getEntity());
		Log.d(LOGTAG, "Languages: " + json);
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
		Log.d(LOGTAG, Url);
		HttpResponse respuestaPrueba = new DefaultHttpClient().execute(solicitud);
		String json = EntityUtils.toString(respuestaPrueba.getEntity());
		Log.d(LOGTAG, "Subjects: " + json);
		// FIN DE PRUEBA
		
		HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);
		return  new Funciones().readSubjectsStream(respuesta.getEntity());
	}
	
	
	
	
	
	public EducationalSetting getWholeViewEducationalSettings() throws ClientProtocolException, IOException{
		String Url = new IP().ip+"/"+language.getStringLanguage()+"/educationalSettings/getWholeView.json?id="+educational_setting.id;
       	HttpGet solicitud = new HttpGet(Url);	
       	SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
       	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));

        // PRUEBA
       	Log.d(LOGTAG, "Url: " + Url);
		HttpResponse respuestaPrueba = new DefaultHttpClient().execute(solicitud);
		Log.d(LOGTAG, "JSON WHOLE VIEW EducationalSETTING: "+EntityUtils.toString(respuestaPrueba.getEntity()));
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
	
	
	
	
	
	public void loadImageTab3(){
		runOnUiThread(new Runnable() {
	        public void run() {
	        	ImageView imageView = (ImageView) findViewById(R.id.imagen_tab3);
	        	if (educational_setting.imageBitmap != null) imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(educational_setting.imageBitmap, 12, EducationalSettingView.this));
	        	else {
	        		Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.imagen);
		   		 	imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, EducationalSettingView.this));
	        	}
	        }
	    });
	}
	
	
	
	
	
	public static int nearest2pow(int value) {
        return value == 0 ? 0 : (32 - Integer.numberOfLeadingZeros(value - 1)) / 2;
    }
	
	
	
	
	
	public void loadTextViewsTab3(){
		        	
	        	TextView textView = (TextView) findViewById(R.id.titulo_tab3);
	        	textView.setText(educational_setting.title);
	        	
	        	textView = (TextView) findViewById(R.id.description_tab3);
	        	textView.setText(educational_setting.description);
	        		       	   
	        	textView = (TextView) findViewById(R.id.textView_keywords);
	        	textView.setText(educational_setting.keywords);        		        	
	        	
	        	textView = (TextView) findViewById(R.id.textView_rangoEdades);
	        	textView.setText(educational_setting.age_range);
	        	
	        	textView = (TextView) findViewById(R.id.textView_fechaInicio);
	        	textView.setText(educational_setting.start_date);
	        	
	        	textView = (TextView) findViewById(R.id.textView_fechaFin);
	        	textView.setText(educational_setting.end_date);
	        	
	        	textView = (TextView) findViewById(R.id.textView_materia);
	        	String subject = null;
	        	for (int i=0; i<educational_setting.vocabularies.size(); i++){
	        		if (educational_setting.vocabularies.get(i).type.equals("subjects"))
	        			subject = educational_setting.vocabularies.get(i).term;
	        	}
	        	textView.setText(subject);
	        	
	        	textView = (TextView) findViewById(R.id.textView_nivelEducativo);
	        	String education_level = null;
	        	for (int i=0; i<educational_setting.vocabularies.size(); i++){
	        		if (educational_setting.vocabularies.get(i).type.equals("education_levels"))
	        			education_level = educational_setting.vocabularies.get(i).term;
	        	}
	        	textView.setText(education_level);
	        	
	        	textView = (TextView) findViewById(R.id.textView_lenguaje);
	        	String language = null;
	        	for (int i=0; i<educational_setting.vocabularies.size(); i++){
	        		if (educational_setting.vocabularies.get(i).type.equals("languages"))
	        			language = educational_setting.vocabularies.get(i).term;
	        	}
	        	textView.setText(language);
	        		        	
	        
	  
	}
	
	
	
	
	
	public void load_TextViews_tab3(){
		runOnUiThread(new Runnable() {
	        public void run() {
	        	
	        	TextView textView = (TextView) findViewById(R.id.titulo_tab3);
	        	textView.setText(educational_setting.title);
	        	
	        	textView = (TextView) findViewById(R.id.description_tab3);
	        	textView.setText(educational_setting.description);
	        	
	        	textView = (TextView) findViewById(R.id.textView_keywords);
	        	textView.setText(educational_setting.keywords);        	
	        		        	
	        	textView = (TextView) findViewById(R.id.textView_rangoEdades);
	        	textView.setText(educational_setting.age_range);
	        	
	        	textView = (TextView) findViewById(R.id.textView_fechaInicio);
	        	textView.setText(educational_setting.start_date);
	        	
	        	textView = (TextView) findViewById(R.id.textView_fechaFin);
	        	textView.setText(educational_setting.end_date);
	        	
	        	textView = (TextView) findViewById(R.id.address_textView);
	        	textView.setText(educational_setting.address);
	        	
	        	textView = (TextView) findViewById(R.id.textView_materia);
	        	String subject = null;
	        	for (int i=0; i<educational_setting.vocabularies.size(); i++){
	        		if (educational_setting.vocabularies.get(i).type.equals("subjects"))
	        			subject = educational_setting.vocabularies.get(i).term;
	        	}
	        	textView.setText(subject);
	        	
	        	textView = (TextView) findViewById(R.id.textView_nivelEducativo);
	        	String education_level = null;
	        	for (int i=0; i<educational_setting.vocabularies.size(); i++){
	        		if (educational_setting.vocabularies.get(i).type.equals("education_levels"))
	        			education_level = educational_setting.vocabularies.get(i).term;
	        	}
	        	textView.setText(education_level);
	        	
	        	textView = (TextView) findViewById(R.id.textView_lenguaje);
	        	String language = null;
	        	for (int i=0; i<educational_setting.vocabularies.size(); i++){
	        		if (educational_setting.vocabularies.get(i).type.equals("languages"))
	        			language = educational_setting.vocabularies.get(i).term;
	        	}
	        	textView.setText(language);
	        		        	
	        }
	    });
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
    	for (int i=0; i<educational_setting.vocabularies.size(); i++){
    		if (educational_setting.vocabularies.get(i).type.equals("subjects"))
    			subject = educational_setting.vocabularies.get(i).term;
    	}
    	editText.setText(subject);
    	
    	textView = (TextView) findViewById(R.id.textView_nivelEducativo);
    	editText = (EditText) findViewById(R.id.editText_nivelEducativo);
    	String edLevel="";
    	for (int i=0; i<educational_setting.vocabularies.size(); i++){
    		if (educational_setting.vocabularies.get(i).type.equals("education_levels"))
    			edLevel = educational_setting.vocabularies.get(i).term;
    	}
    	editText.setText(edLevel);
    	
    	textView = (TextView) findViewById(R.id.textView_lenguaje);
    	editText = (EditText) findViewById(R.id.editText_lenguaje);
    	String language="";
    	for (int i=0; i<educational_setting.vocabularies.size(); i++){
    		if (educational_setting.vocabularies.get(i).type.equals("languages"))
    			language = educational_setting.vocabularies.get(i).term;
    	}
    	editText.setText(language);
    }
	
	
	
	
	
	public void restart(){        
        
        getVocabulary(); 
                
        getTab3();       
	}





	public static Bitmap rotate_bitmap(Bitmap source, float angle){
	      Matrix matrix = new Matrix();
	      matrix.postRotate(angle);
	      return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
	
	
	
	
	
	public void saveTab3() throws InterruptedException, ExecutionException{
		System.out.println("saveTab3");
		
		EducationalSetting educationalSetting = new EducationalSetting();
    	
    	EditText editText = (EditText) findViewById(R.id.editText_titulo_tab3);
    	educationalSetting.title = editText.getText().toString();
    	educational_setting.title = educationalSetting.title;
    	
    	editText = (EditText) findViewById(R.id.edit_description_tab3);
    	educationalSetting.description = editText.getText().toString();
    	educational_setting.description = educationalSetting.description;
    	
    	editText = (EditText) findViewById(R.id.editText_rangoEdades);
    	educationalSetting.age_range = editText.getText().toString();
    	
    	editText = (EditText) findViewById(R.id.editText_fechaInicio);
    	educationalSetting.start_date = editText.getText().toString();
    	
    	editText = (EditText) findViewById(R.id.editText_fechaFin);
    	educationalSetting.end_date = editText.getText().toString();
    	
    	editText = (EditText) findViewById(R.id.editText_keywords);
    	educationalSetting.keywords = editText.getText().toString();
    	
    	editText = (EditText) findViewById(R.id.address_editText);
    	educationalSetting.address = editText.getText().toString();
    	
    	editText = (EditText) findViewById(R.id.editText_materia);
    	String subject = editText.getText().toString();
    	int subjectId = -1;
    	if ((subjectId = isInVocabulary(subject, "subject")) > 0){ // ya está en el vocabulario 
    		Log.d(LOGTAG, "subject is in vocabulary");
    		VocabularyES vocabulary = new VocabularyES();
    		vocabulary.id = subjectId;
    		vocabulary.type = "subject";
    		vocabulary.term = editText.getText().toString();
    		educationalSetting.vocabularies.add(vocabulary);
    	}else{ // no está en el vocabulario
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
    	int edLevelId = -1;
    	if ((edLevelId = isInVocabulary(edLevel, "education_level")) > 0){ // ya está en el vocabulario 
    		Log.d(LOGTAG, "education level is in vocabulary");
    		VocabularyES vocabulary = new VocabularyES();
    		vocabulary.id = edLevelId;
    		vocabulary.type = "education_level";
    		vocabulary.term = editText.getText().toString();
    		educationalSetting.vocabularies.add(vocabulary);
    	}else{ // no está en el vocabulario
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
    	int languageId = -1;
    	if ((languageId = isInVocabulary(language, "language")) > 0){ // ya está en el vocabulario
    		Log.d(LOGTAG, "language is in vocabulary");
    		VocabularyES vocabulary = new VocabularyES();
    		vocabulary.id = languageId;
    		vocabulary.type = "language";
    		vocabulary.term = editText.getText().toString();
    		educationalSetting.vocabularies.add(vocabulary);
    	}else{ // no está en el vocabulario
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
    	educationalSetting.image_Uri = educational_setting.image_Uri;
    	educationalSetting.imagePath = educational_setting.imagePath;
    	
    	new SendEducationalSetting(educationalSetting).start();
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
		
        ImageView iv = (ImageView) findViewById(R.id.imagen_tab3);
    	
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
			iv.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bitmap, 12, EducationalSettingView.this));
		} catch (IOException e) {
			e.printStackTrace();
		}	
    }
	
	
	
	
	
	private void setPic() throws IOException {
		Log.d(LOGTAG, "setPic");
		
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */
    	ImageView mImageView = (ImageView) findViewById(R.id.imagen_tab3);
    	
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
		Bitmap roundedCornerBitmap = ImageHelper.getRoundedCornerBitmap(bitmap, 12, EducationalSettingView.this);
		
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
		mCurrentPhotoPath = f.getAbsolutePath();
		
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
	
	
	
	
	
	public void viewMode(){ 
		mode.setMode(Mode.VIEW_MODE);
		
		changeVisibilityEditTextsTab3(View.INVISIBLE);
		changeVisibilityTextViewsTab3(View.VISIBLE);
		changeVisibilitySpinnersTab3(View.GONE);		       
        
		Button button = (Button) findViewById(R.id.button_fechaInicio);
		button.setVisibility(View.GONE);
		
		button = (Button) findViewById(R.id.button_fechaFin);
		button.setVisibility(View.GONE);
		
		progressDialog = createProgressDialog("Cargando contenido...");
        restart();
    }




	
	class CopyEducationalSetting extends Thread {
		EducationalSetting edSetting;
		int id;
        
        public CopyEducationalSetting(EducationalSetting edSetting, int id) {
        	this.edSetting = edSetting;
        	this.id = id;
        }

        @Override 
        public void run() {
        	System.out.println("CopyEducationalSetting");
        	
			try {
				String BaseUrlPage =  new IP().ip+"/"+language.getStringLanguage()+"/educationalSettings/"+this.id+".json";       	
	            MultipartEntity mpEntity = new MultipartEntity();
				SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
				
				mpEntity.addPart("title", new StringBody(edSetting.title, Charset.forName("UTF-8")));	
				
				mpEntity.addPart("description", new StringBody(edSetting.description, Charset.forName("UTF-8")));						
				
				if (edSetting.imagePath != null){
					File file = new File(edSetting.imagePath);
					mpEntity.addPart("element_image", new FileBody(file, "image/jpeg"));
				}					
				
				mpEntity.addPart("age_range", new StringBody(edSetting.age_range, Charset.forName("UTF-8")));
				
				mpEntity.addPart("start_date", new StringBody(edSetting.start_date, Charset.forName("UTF-8")));
				
				mpEntity.addPart("end_date", new StringBody(edSetting.end_date, Charset.forName("UTF-8")));
				
				mpEntity.addPart("keywords", new StringBody(edSetting.keywords, Charset.forName("UTF-8")));
			
				if (edSetting.address!=null) mpEntity.addPart("address", new StringBody(edSetting.address, Charset.forName("UTF-8")));
				
				String subject = null; String edLevel = null; String language = null;
				int subjectId = -1; int edLevelId = -1; int languageId = -1;								
				for (int i=0;i<edSetting.vocabularies.size();i++){
					if (edSetting.vocabularies.get(i).type.equals("subjects")){
						mpEntity.addPart("subject", new StringBody(""+edSetting.vocabularies.get(i).id));
						subject = edSetting.vocabularies.get(i).term;
						subjectId = edSetting.vocabularies.get(i).id;
					}
					if (edSetting.vocabularies.get(i).type.equals("education_levels")){
						mpEntity.addPart("education_level", new StringBody(""+edSetting.vocabularies.get(i).id));
						edLevel = edSetting.vocabularies.get(i).term;
						edLevelId = edSetting.vocabularies.get(i).id;
					}
					if (edSetting.vocabularies.get(i).type.equals("languages")){
						mpEntity.addPart("language", new StringBody(""+edSetting.vocabularies.get(i).id));
						language = edSetting.vocabularies.get(i).term;
						languageId = edSetting.vocabularies.get(i).id;
					}
				}
				
				String coordinates = "" + edSetting.coordinates.latitude + "," + edSetting.coordinates.longitude;
				if (coordinates.equals("")==false){
					mpEntity.addPart("coordinates", new StringBody(coordinates));
				}	
				
				HttpPut httpPut = new HttpPut(BaseUrlPage);
				httpPut.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				httpPut.setEntity(mpEntity);
				Log.d("", "Url: " + BaseUrlPage);
				new DefaultHttpClient().execute(httpPut);		
				
				System.out.println(BaseUrlPage);
				System.out.println("datos enviados:");
				System.out.println("title: "+edSetting.title);
				System.out.println("description: "+edSetting.description);
				System.out.println("age_range: "+edSetting.age_range);
				System.out.println("start_date: "+edSetting.start_date);
				System.out.println("end_date: "+edSetting.end_date);
				System.out.println("keywords: "+edSetting.keywords);
				if (edSetting.address!= null) System.out.println("address: " + edSetting.address);
				System.out.println("subject: "+subject+","+subjectId);
				System.out.println("edLevel: "+edLevel+","+edLevelId);
				System.out.println("language: "+language+","+languageId);
            	System.out.println("path: " + edSetting.imagePath);
            	
				Looper.prepare();
            	new Funciones().showToast(EducationalSettingView.this, "contexto educativo copiado");
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
	
	
	
	
	
	public class DownloadImageTab3 extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... guideList) {
			if (educational_setting.image_Url.equals("none") == false){
			
				try {					
					String BaseUrlPage = new IP().ip + educational_setting.image_Url;
					HttpGet solicitud = new HttpGet(BaseUrlPage);	
					SharedPreferences preferences = getSharedPreferences( "datos", Context.MODE_PRIVATE);
					solicitud.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
					HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);
					HttpEntity entity = respuesta.getEntity();
					Bitmap bm = BitmapFactory.decodeStream(entity.getContent());
					if (bm != null) educational_setting.imageBitmap = bm;					
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





	class GetTab3 extends AsyncTask<Void, Void, Void> {
		protected Void doInBackground(Void... empty) {        	
      	  try {
      		  Log.d(LOGTAG, "GetTab3");
      		  educational_setting = getWholeViewEducationalSettings();													
      		  load_TextViews_tab3();
			
      		  progressDialog.dismiss(); 
      		
      		  downloadImageTab3();	
			
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





	public class SendEducationalSetting extends Thread {
    	EducationalSetting edSetting;
        
        
        public SendEducationalSetting(EducationalSetting edSetting) {
        	this.edSetting = edSetting;
        }

        @Override 
        public void run() {
        	Log.d("GuideViewTabs", "SendEducationalSetting");
        	HttpClient cliente = new DefaultHttpClient();
        	
        	String BaseUrlPage =  new IP().ip+"/"+language.getStringLanguage()+"/educationalSettings/"+educational_setting.id+".json";     	
            MultipartEntity mpEntity = new MultipartEntity();
			try {
				SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
				mpEntity.addPart("title", new StringBody(edSetting.title));
				
				mpEntity.addPart("description", new StringBody(edSetting.description, Charset.forName("UTF-8")));
				
				mpEntity.addPart("age_range", new StringBody(edSetting.age_range, Charset.forName("UTF-8")));
				
				mpEntity.addPart("start_date", new StringBody(edSetting.start_date, Charset.forName("UTF-8")));
				
				mpEntity.addPart("end_date", new StringBody(edSetting.end_date, Charset.forName("UTF-8")));
				
				mpEntity.addPart("keywords", new StringBody(edSetting.keywords, Charset.forName("UTF-8")));
				
				if (edSetting.address!=null) mpEntity.addPart("address", new StringBody(edSetting.address, Charset.forName("UTF-8")));
				
				if (edSetting.imagePath != null){
					File file = new File(edSetting.imagePath);
					mpEntity.addPart("element_image", new FileBody(file, "image/jpeg"));
				}		
				
				String subject = null; String edLevel = null; String language = null;
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
				}
				
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
				if (edSetting.address!=null) System.out.println("address: " + edSetting.address);
				if (subjectId != -1) System.out.println("subject: "+subject+","+subjectId);
				if (edLevelId != -1) System.out.println("education_level: "+edLevel+","+edLevelId);
				if (languageId != -1) System.out.println("language: "+language+","+languageId);
            	System.out.println("path: " + edSetting.imagePath);
            	Log.d("", "response: [" + EntityUtils.toString(response.getEntity())+"]");
            	
				Looper.prepare();
            	new Funciones().showToast(EducationalSettingView.this, "contexto educativo guardado");
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
	
	
	
	
	
}
