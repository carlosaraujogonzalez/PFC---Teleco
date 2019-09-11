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
import java.util.Date;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
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
import android.graphics.Typeface;
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
import android.view.View.OnClickListener;
import android.view.ViewConfiguration;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class TechnicalSettingView extends ActionBarActivity {
	private static BitmapFactory.Options generalOptions;
	private static final String LOGTAG = "LogsAndroid";
	private Language language;
	public static TechnicalSetting technical_setting; // nuevo
	private TechnicalSetting guide_technical_setting; // donde se copiara
	private Mode mode;
	private ProgressDialog progressDialog;
	public GetTab2 get_tab2;
	private DownloadImageTab2 downloadImageTab2;
	private static final int ACTIVITY_SELECT_IMAGE = 1020;
	private static final int ACTION_TAKE_PHOTO_B = 1;
	private Uri mImageUri;
	private static final String JPEG_FILE_PREFIX = "IMG_";
	private static final String JPEG_FILE_SUFFIX = ".jpg";
	private AlbumStorageDirFactory mAlbumStorageDirFactory = null;
	private Owner owner;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		Log.d("", "onCreate");
          super.onCreate(savedInstanceState);
          getSupportActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_blue_dark));
          setContentView(R.layout.technical_setting_view);
          
          getOverflowMenu();
          
          Configuration config = getResources().getConfiguration();
          onConfigurationChanged(config);
          
          mode = new Mode();
          
          if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.FROYO) {
  			mAlbumStorageDirFactory = new FroyoAlbumDirFactory();
  		  } else {
  			mAlbumStorageDirFactory = new BaseAlbumDirFactory();
  		  }
          
          owner = getIntent().getExtras().getParcelable("owner");         
          technical_setting = getIntent().getExtras().getParcelable("technical_setting");
          language = getIntent().getExtras().getParcelable("language");          
          guide_technical_setting = getIntent().getExtras().getParcelable("guide_technical_setting");
          Log.d("", "id technical setting de la guía: " + guide_technical_setting.id);
          Log.d("", "id technical setting almacen: " + technical_setting.id);
          
          TextView textView = (TextView) findViewById(R.id.title);      
      	  textView.setText(technical_setting.name);
      	  textView.setTypeface(null, Typeface.BOLD);
      	  
      	  textView = (TextView) findViewById(R.id.description);
      	  String description = technical_setting.description;
      	  if (description.equals("")){}
      	  else textView.setText(description+"\n");
      	  
      	  ImageView imagen = (ImageView) findViewById(R.id.image);
      	  Log.d(LOGTAG, "imageUri: " + technical_setting.imageUri);
      	  if (technical_setting.imageBitmap != null){
      		  imagen.setImageBitmap(ImageHelper.getRoundedCornerBitmap(technical_setting.imageBitmap, 12, this));   	
      	  } else {
      		  Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.imagen);
   		 	  imagen.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, this));
      	  }	    	  
      	  
      	  progressDialog = createProgressDialog("Cargando contenido...");
      	  getTab2();
    }

	
	
	
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.technical_setting_viewmode, menu);
        return true;
    }  
	
	
	
	
	
	@SuppressLint("NewApi")
	@Override
    public void onConfigurationChanged(Configuration newConfig) {
		Log.d("", "onConfigurationChanged");
        super.onConfigurationChanged(newConfig);
        
        DisplayMetrics metrics = new DisplayMetrics();
    	getWindowManager().getDefaultDisplay().getMetrics(metrics);
        
        if (newConfig.orientation == Configuration.ORIENTATION_PORTRAIT) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.technical_setting_view_relative_layout);
        	rl.setBackground(getResources().getDrawable(R.drawable.fondo_guias));
        } 
        else if (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE) {
        	RelativeLayout rl = (RelativeLayout) findViewById(R.id.technical_setting_view_relative_layout);
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
        	copy_technical_setting_to_guide("Advertencia", "¿Estás seguro de que quieres copiar este contexto técnico a la guía?");
        	return true;
        }
        if (id == R.id.save){
        	saveTab2();
        	return true;
        }
        return super.onOptionsItemSelected(item);
    }
	
	
	
	
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu){
		Log.d(LOGTAG, "onPrepareOptionsMenu");
	    menu.clear();
	    if (mode.getMode() == mode.ViewMode()){
	    	getMenuInflater().inflate(R.menu.technical_setting_viewmode, menu);
	    	getOverflowMenu();
	    }
	    else{
	    	getMenuInflater().inflate(R.menu.technical_setting_editmode, menu);
	    }
	    return super.onPrepareOptionsMenu(menu);
	}
	
	
	
	
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		Log.d("GuideViewTabs", "onSaveInstanceState");
		if (mImageUri != null) outState.putString("Uri", mImageUri.toString());
		
		super.onSaveInstanceState(outState);
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
	
	
	
	
	
	public void ver_aplicaciones(View view){
	      Intent intent = new Intent(this, ApplicationsView.class);
	      intent.putExtra("technical_setting_id", technical_setting.id);
	      intent.putExtra("Language", language);
	      intent.putExtra("owner", owner);
	      intent.putExtra("from_activity", "TechnicalSettingView");
	      startActivity(intent);
	}  
	
	
	
	
	public void ver_dispositivos(View view){
	      Intent intent = new Intent(this, DevicesView.class);
	      intent.putExtra("technical_setting_id", technical_setting.id);
	      intent.putExtra("Language", language);
	      intent.putExtra("owner", owner);
	      intent.putExtra("from_activity", "TechnicalSettingView");
	      startActivity(intent);
	}
	
	
	
	
	
	public void changeVisibilityEditTextsTab2(int visibility){
		
		EditText editText = (EditText) findViewById(R.id.edit_title);
    	editText.setVisibility(visibility);
    	
    	editText = (EditText) findViewById(R.id.edit_description);
    	editText.setVisibility(visibility);
    	
	}





	public void changeVisibilityTextViewsTab2(int visibility){
    	
		TextView textView = (TextView) findViewById(R.id.title);
    	textView.setVisibility(visibility);
    	
    	textView = (TextView) findViewById(R.id.description);
    	textView.setVisibility(visibility);	
    }





	public void copy_technical_setting_to_guide(String title, String message){
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
			   
			   Bitmap bm = getBitmapFromImageView(R.id.image);
			   technical_setting.imagePath = new Funciones().getRealPathFromBitmap(TechnicalSettingView.this, bm);
			   Log.d("", "image path: " + technical_setting.imagePath);
			   
			   new CopyTechnicalSetting(technical_setting, guide_technical_setting.id).start();	
			   new DeleteInvolvements(guide_technical_setting.devices_id, guide_technical_setting.applications_id).start();
			   
			   
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
	
	
	
	
	private File createImageFile() throws IOException {
		Log.d(LOGTAG, "createImageFile");
		
		// Create an image file name
		String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String imageFileName = JPEG_FILE_PREFIX + timeStamp + "_";
		File albumF = getAlbumDir();
		File imageF = File.createTempFile(imageFileName, JPEG_FILE_SUFFIX, albumF);
		return imageF;
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
				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
			} catch (IOException e) {
				e.printStackTrace();
				f = null;
				technical_setting.imagePath = null;
			}
			
			break;

		default:
			break;			
		} // switch

		startActivityForResult(takePictureIntent, actionCode);
	}
	
	
	
	
	
	protected void downloadImageTab2(){
		runOnUiThread(new Runnable() {
			public void run() {
				downloadImageTab2 = new DownloadImageTab2();
				downloadImageTab2.execute();
			}
		});
	}
	
	
	
	
	
	public void edit_mode(){
		runOnUiThread(new Runnable() {
			public void run() {	 
				edit_mode_tab2();				
			}
    	});
    }
	
	
	
	
	
	public void edit_mode_tab2(){
		// TITULO
		TextView titulo = (TextView) findViewById(R.id.title);
		titulo.setVisibility(View.INVISIBLE);
		String tituloActual = titulo.getText().toString();
		EditText editTitle = (EditText) findViewById(R.id.edit_title);
		editTitle.setText(tituloActual);
		editTitle.setVisibility(View.VISIBLE);
	
		// DESCRIPCION
		TextView descripcion = (TextView) findViewById(R.id.description);
		descripcion.setVisibility(View.INVISIBLE);
		String descripcionActual = descripcion.getText().toString();
		EditText editDesc = (EditText) findViewById(R.id.edit_description); 
		editDesc.setText(descripcionActual);
		editDesc.setVisibility(View.VISIBLE);
	}	
	
	
	
	
	
	private void galleryAddPic() {
		Log.d(LOGTAG, "galleryAddPic");
		
	    Intent mediaScanIntent = new Intent("android.intent.action.MEDIA_SCANNER_SCAN_FILE");
		File f = new File(technical_setting.imagePath);
	    Uri contentUri = Uri.fromFile(f);	    
	    mediaScanIntent.setData(contentUri);
	    this.sendBroadcast(mediaScanIntent);
	    
	    technical_setting.imageUri = contentUri.toString();
	    
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
        	
        	technical_setting.imageUri = uri.toString();
        	technical_setting.imagePath = absolutePath;
        	
            setImage(bounds, absolutePath);
            Log.d("GuideViewTabs", "Uri: " + uri.toString() + ";path: " + absolutePath);
        } else {
            new Funciones().showToast(this, "imagen sin contenido"); 
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
		Log.d("", "getOverFlowMenu");
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
	
	
	
	
	
	public void getTab2() {
		get_tab2 = new GetTab2();
		get_tab2.execute();
	}
	
	
	
	
	
	private void handleBigCameraPhoto() throws IOException {
		Log.d(LOGTAG, "handleBigCameraPhoto");
		
		if (technical_setting.imagePath != null) {
			Log.d("GuideViewTabs", "mCurrentPhotoPath: " + technical_setting.imagePath);
			setPic();
			galleryAddPic();
		}
	}
	
	
	
	
	
	public void loadImageTab2(){
		runOnUiThread(new Runnable() {
	        public void run() {
	        	ImageView imageView = (ImageView) findViewById(R.id.image);
	        	if (technical_setting.imageBitmap != null) imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(technical_setting.imageBitmap, 12, TechnicalSettingView.this));
	        	else {
	        		Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.imagen);
		   		 	imageView.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bm, 12, TechnicalSettingView.this));
	        	}	
	        }
	    });
	}
	
	
	
	
	
	public void load_TextViews_tab2(final String Titulo, final String Descripcion){
		runOnUiThread(new Runnable() {
	        public void run() {
	        	
	        	TextView textView = (TextView) findViewById(R.id.title);        	
	            if (technical_setting.name.equals("") == false) textView.setText(technical_setting.name);
	            else textView.setText(getResources().getString(R.string.title));
	            
	            textView = (TextView) findViewById(R.id.description);
	            if (technical_setting.description.equals("") == false) textView.setText(technical_setting.description);
	            else textView.setText(getResources().getString(R.string.description));
	        	
	        }
	    });
	}
	
	
	
	
	
	public static int nearest2pow(int value) {
        return value == 0 ? 0 : (32 - Integer.numberOfLeadingZeros(value - 1)) / 2;
    }
	
	
	
	
	
	public void restart(){
       
        getTab2();       
	}





	public static Bitmap rotate_bitmap(Bitmap source, float angle){
	      Matrix matrix = new Matrix();
	      matrix.postRotate(angle);
	      return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
	}
	
	
	
	
	
	public void saveTab2(){
		System.out.println("saveTab2");
		
    	TechnicalSetting techSetting = new TechnicalSetting();
    	
    	EditText editText = (EditText) findViewById(R.id.edit_title);
    	techSetting.name = editText.getText().toString();
    	technical_setting.name = techSetting.name;
    	
    	editText = (EditText) findViewById(R.id.edit_description);
    	techSetting.description = editText.getText().toString();
    	technical_setting.description = techSetting.description;
    	
    	techSetting.imageBitmap = getBitmapFromImageView(R.id.image);
    	techSetting.imageUri = technical_setting.imageUri;
    	techSetting.imagePath = technical_setting.imagePath;
    	
    	new SendTechnicalSetting(techSetting, technical_setting.id).start();
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
    	iv = (ImageView) findViewById(R.id.image);
    	
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
			iv.setImageBitmap(ImageHelper.getRoundedCornerBitmap(bitmap, 12, TechnicalSettingView.this));
		} catch (IOException e) {
			e.printStackTrace();
		}	
    }
	
	
	
	
	
	private void setPic() throws IOException {
		Log.d(LOGTAG, "setPic");
		
		/* There isn't enough memory to open up more than a couple camera photos */
		/* So pre-scale the target bitmap into which the file is decoded */
		ImageView mImageView = (ImageView) findViewById(R.id.image);
  
    	
		/* Get the size of the ImageView */
		int targetW = mImageView.getWidth();
		int targetH = mImageView.getHeight();

		/* Get the size of the image */
		BitmapFactory.Options bmOptions = new BitmapFactory.Options();
		bmOptions.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(technical_setting.imagePath, bmOptions);
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
		Bitmap bitmap = BitmapFactory.decodeFile(technical_setting.imagePath, bmOptions);
		Bitmap roundedCornerBitmap = ImageHelper.getRoundedCornerBitmap(bitmap, 12, TechnicalSettingView.this);
		
		// Rotate image
		ExifInterface ei = new ExifInterface(technical_setting.imagePath);
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
		technical_setting.imagePath = f.getAbsolutePath();
		
		return f;
	}
	
	

	
	
	public void viewMode(){ 
		
    	changeVisibilityEditTextsTab2(View.INVISIBLE);
    	changeVisibilityTextViewsTab2(View.VISIBLE);
        
		progressDialog = createProgressDialog("Cargando contenido...");
        restart();
    }




	class AddInvolvements extends Thread {
    	ArrayList<Integer> devicesList, appsList;
    	
    	public AddInvolvements(ArrayList<Integer> devicesList, ArrayList<Integer> appsList){
    		this.devicesList = devicesList;
    		this.appsList = appsList;
    	}
    	
        @Override 
        public void run() {
        	try {
        		Log.d("", "AddInvolvements");
            	String url = new IP().ip+"/"+language.getStringLanguage() +"/technicalSettings/addInvolvements.json?id="+guide_technical_setting.id+"&devices=";				
				for (int i=0; i<devicesList.size();i++){												
					if (i==devicesList.size()-1) url+=devicesList.get(i);
					else url += devicesList.get(i) + ",";
				}
				HttpPut solicitud = new HttpPut(url);
				SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
				solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				Log.d("", "url para añadir dispositivos: " + url);
				HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);					
				Log.d(LOGTAG, "response: " + EntityUtils.toString(respuesta.getEntity()));
				
				url = new IP().ip+"/"+language.getStringLanguage() +"/technicalSettings/addInvolvements.json?id="+guide_technical_setting.id+"&applications=";				
				for (int i=0; i<appsList.size();i++){												
					if (i==appsList.size()-1) url+=appsList.get(i);
					else url += appsList.get(i) + ",";
				}
				solicitud = new HttpPut(url);
				solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				Log.d("", "url para añadir aplicaciones: " + url);
				respuesta = new DefaultHttpClient().execute(solicitud);					
				Log.d(LOGTAG, "response: " + EntityUtils.toString(respuesta.getEntity()));
								
				Looper.prepare();
            		Dialog dialog = new Funciones().create_ok_dialog(TechnicalSettingView.this, "Advertencia", "El contexto técnico ha sido copiado");
            		dialog.show();
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
	
	
	
	
	
	class CopyTechnicalSetting extends Thread {
		TechnicalSetting technicalSetting;
		int id;
        
        public CopyTechnicalSetting(TechnicalSetting technicalSetting, int id) {
        	this.technicalSetting = technicalSetting;
        	this.id = id;
        }

        @Override 
        public void run() {
        	System.out.println("CopyTechnicalSetting");
        	
			try {
				String BaseUrlPage =  new IP().ip+"/"+language.getStringLanguage()+"/technicalSettings/"+this.id+".json";       	
	            MultipartEntity mpEntity = new MultipartEntity();
				SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
				
				mpEntity.addPart("title", new StringBody(technicalSetting.name, Charset.forName("UTF-8")));	
				
				mpEntity.addPart("description", new StringBody(technicalSetting.description, Charset.forName("UTF-8")));						
				
				if (technicalSetting.imagePath != null){
					File file = new File(technicalSetting.imagePath);
					mpEntity.addPart("element_image", new FileBody(file, "image/jpeg"));
				}					
				
				HttpPut httpPut = new HttpPut(BaseUrlPage);
				httpPut.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				httpPut.setEntity(mpEntity);
				Log.d("TechnicalSettingView", "Url: " + BaseUrlPage);
				new DefaultHttpClient().execute(httpPut);		
				
				System.out.println("Datos enviados: ");
            	System.out.println("Title: " + technicalSetting.name);
            	System.out.println("Description: "+technicalSetting.description);
            	System.out.println("path: " + technicalSetting.imagePath);
            			
            	Looper.prepare();
            	new Funciones().showToast(TechnicalSettingView.this, "contexto técnico copiado");
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
	
	
	
	
	
	class DeleteInvolvements extends Thread {
        ArrayList<Integer> devicesList, appsList;
        
		public DeleteInvolvements(ArrayList<Integer> devicesList, ArrayList<Integer> appsList){
			this.devicesList = devicesList;
			this.appsList = appsList;
		}
		
        @Override 
        public void run() {        	
			try {
				Log.d("", "DeleteInvolvements");
				String url = new IP().ip+"/"+language.getStringLanguage()+"/technicalSettings/deleteInvolvements.json?id="+guide_technical_setting.id+"&devices=";
				Log.d("", "tamaño lista dispositivos del ct de la guia: " + devicesList.size());
				for (int i=0;i<devicesList.size();i++){
						Log.d("", "deviceId:" + devicesList.get(i));
						if (i==devicesList.size()-1) url += devicesList.get(i);
						else url += devicesList.get(i) + ",";
				}
				HttpDelete solicitudBorrar = new HttpDelete(url);
				SharedPreferences preferences = getSharedPreferences("datos", Context.MODE_PRIVATE);
				solicitudBorrar.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				System.out.println("Url para borrar dispositivos: "+url);
				HttpResponse respuesta = new DefaultHttpClient().execute(solicitudBorrar);	
				Log.d(LOGTAG, "Respuesta: " + EntityUtils.toString(respuesta.getEntity()));
							
				url = new IP().ip+"/"+language.getStringLanguage()+"/technicalSettings/deleteInvolvements.json?id="+guide_technical_setting.id+"&applications=";
				for (int i=0;i<appsList.size();i++){
						if (i==appsList.size()-1) url += appsList.get(i);
						else url += appsList.get(i) + ",";
				}
				solicitudBorrar = new HttpDelete(url);
				solicitudBorrar.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				System.out.println("Url para borrar aplicaciones: "+url);
				respuesta = new DefaultHttpClient().execute(solicitudBorrar);	
				Log.d(LOGTAG, "Respuesta: " + EntityUtils.toString(respuesta.getEntity()));
											
				new AddInvolvements(technical_setting.devices_id, technical_setting.applications_id).start();
				
			} catch (UnsupportedEncodingException e1) {
				e1.printStackTrace();
			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}	
        }        
    }
	
	
	
	
	
	public class DownloadImageTab2 extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... guideList) {
        	SharedPreferences preferences = getSharedPreferences( "datos", Context.MODE_PRIVATE);
			if (technical_setting.imageUrl.equals("none") == false){
				try {
					String BaseUrlPage = new IP().ip + technical_setting.imageUrl;
					HttpGet solicitud = new HttpGet(BaseUrlPage);	
					solicitud.setHeader("Cookie", "_AREA_v0.1_session=" + preferences.getString("Cookie", "0"));
					HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);
					HttpEntity entity = respuesta.getEntity();
					Bitmap bm = BitmapFactory.decodeStream(entity.getContent());
					//technical_setting.imagePath = new Funciones().getRealPathFromBitmap(TechnicalSettingView.this, bm);
					//Log.d("", "image path: " + technical_setting.imagePath);
					if (bm != null){
						technical_setting.imageBitmap = bm;
					}
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
	
	
	
	
	
	class GetTab2 extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... empty) {   
        	
            	try {
            		Log.d(LOGTAG, "GetTab2");
                	String url =  new IP().ip+"/"+language.getStringLanguage()+"/technicalSettings/getWholeView.json?id="+technical_setting.id;
                	
                	HttpGet	solicitud = new HttpGet(url);	
                	SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
                	solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
                		
            		// PRUEBA
                	Log.d(LOGTAG, url);
					HttpResponse respuestaPrueba = new DefaultHttpClient().execute(solicitud);
					String json = EntityUtils.toString(respuestaPrueba.getEntity());
					Log.d(LOGTAG, "SMALL VIEW TSETTING: "+json);
                	Log.d(LOGTAG, "url: " + url);
					// FIN DE PRUEBA
					
					HttpResponse respuesta = new DefaultHttpClient().execute(solicitud);
					HttpEntity entity = respuesta.getEntity();
					List<TechnicalSetting> tsettingList = new Funciones().readJsonTSStream(entity);
					technical_setting = tsettingList.get(0);
					
					if (tsettingList.get(0).description.equals("")) tsettingList.get(0).description = "Descripción técnica";
					load_TextViews_tab2(tsettingList.get(0).name, tsettingList.get(0).description);
							
					progressDialog.dismiss();
										
					downloadImageTab2();
					
            	} catch (ClientProtocolException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}		
            	return null;
         }        
    }
	
	
	
	
	
	class SendTechnicalSetting extends Thread {
		TechnicalSetting technicalSetting;
		int id;
        
        public SendTechnicalSetting(TechnicalSetting technicalSetting, int id) {
        	this.technicalSetting = technicalSetting;
        	this.id = id;
        }

        @Override 
        public void run() {
        	System.out.println("SendTechnicalSetting");
        	HttpClient cliente = new DefaultHttpClient();
        	HttpPut solicitud;
        	String BaseUrlPage =  new IP().ip+"/"+language.getStringLanguage()+"/technicalSettings/"+this.id+".json";       	
            MultipartEntity mpEntity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);
			try {
				SharedPreferences preferences = getSharedPreferences("datos",MODE_PRIVATE); 
				
				mpEntity.addPart("title", new StringBody(technicalSetting.name, Charset.forName("UTF-8")));	
				
				mpEntity.addPart("description", new StringBody(technicalSetting.description, Charset.forName("UTF-8")));			
				
				if (technicalSetting.imagePath != null){
					File file = new File(technical_setting.imagePath);
					mpEntity.addPart("element_image", new FileBody(file, "image/jpeg"));
				}
				
				solicitud = new HttpPut(BaseUrlPage);
				solicitud.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
				solicitud.setEntity(mpEntity);
				Log.d("TechnicalSettingView", "Url: " + BaseUrlPage);
				cliente.execute(solicitud);		
				
				System.out.println("Datos enviados: ");
            	System.out.println("Title: " + technicalSetting.name);
            	System.out.println("Description: "+technicalSetting.description);
            	System.out.println("path: " + technicalSetting.imagePath);
				
            	Looper.prepare();
            	new Funciones().showToast(TechnicalSettingView.this, "contexto técnico guardado");
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
}
