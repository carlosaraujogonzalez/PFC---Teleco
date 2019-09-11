package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class TechnicalSetting implements Parcelable{
	public int id;
	public String name;
	public String imagePath;
	public String imageUrl;
	public String imageUri;
	public Bitmap imageBitmap;
	public String description;
	public String short_description;
	public ArrayList<String> keywords;
	public ArrayList<Integer> devices_id;
	public ArrayList<Integer> applications_id;
	
	public TechnicalSetting(){
		this.id = -1;
		this.name = null;
		this.imagePath = null;
		this.imageUrl = null;
		this.imageUri = null;
		this.imageBitmap = null;
		this.description = null;	
		this.short_description = null;
		this.keywords = new ArrayList<String>();
		this.devices_id = new ArrayList<Integer>();
		this.applications_id = new ArrayList<Integer>();
	}

	
	
	
	
	public TechnicalSetting(Parcel in){
		readFromParcel(in);
	}
	
	
	
	
	
	public int getId(){
		return id;
	}
	
	public void setId(int id){
		this.id = id;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String str){
		name = str;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setDescription(String str){
		description = str;
		setShortDescription(str);
	}
	
	public String getShortDescription(){
		return short_description;
	}
	
	public void setShortDescription(String str){
		if (str.length()>100) short_description = str.substring(0, 99)+"...";
		else short_description = str;
	}
	
	public String getImageUri(){
		return imageUri;
	}
	
	public void setImageUri(String str){
		imageUri = str;
	}
	
	public String getImageUrl(){
		return imageUrl;
	}
	
	public void setImageUrl(String str){
		imageUrl = str;
	}
	
	public Bitmap getBitmapImage(){
		return imageBitmap;
	}
	
	public void setBitmapImage(Bitmap bm){
		imageBitmap = bm;
	}
	
	
	
	
	
	public static final Parcelable.Creator<TechnicalSetting> CREATOR = new Parcelable.Creator<TechnicalSetting>() {
		
        public TechnicalSetting createFromParcel(Parcel in) {
        	return new TechnicalSetting(in);
        }

		@Override
		public TechnicalSetting[] newArray(int size) {
			return new TechnicalSetting[size];
		}      
    };
	
    
    
    
    
	@Override
	public int describeContents() {
		return 0;
	}

	
	
	
	
	@Override
	public void writeToParcel(Parcel arg0, int arg1) {
		arg0.writeInt(id);
		arg0.writeString(name);
		arg0.writeString(imagePath);
		arg0.writeString(imageUrl);
		arg0.writeString(imageUri);
		arg0.writeString(description);
		arg0.writeSerializable(keywords);
		arg0.writeSerializable(devices_id);
		arg0.writeSerializable(applications_id);
		//arg0.writeParcelable(imageBitmap, arg1);
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	private void readFromParcel(Parcel in) {
		 // GENERAL
		 id = in.readInt();
		 name = in.readString();
		 imagePath = in.readString();
		 imageUrl = in.readString();
		 imageUri = in.readString();
		 description = in.readString();	 
		 keywords = (ArrayList<String>) in.readSerializable();
		 devices_id = (ArrayList<Integer>) in.readSerializable();
		 applications_id = (ArrayList<Integer>) in.readSerializable();
		 //imageBitmap = in.readParcelable(Bitmap.class.getClassLoader());
   }
}