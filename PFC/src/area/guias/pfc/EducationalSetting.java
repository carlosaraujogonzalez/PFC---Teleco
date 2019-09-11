package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class EducationalSetting implements Parcelable{
		public int id;
		public String title;
		public String imagePath;
		public String image_Url;
		public String image_Uri;
		public Bitmap imageBitmap;
		public String description;
		public String short_description;
		public String keywords;
		public String age_range;
		public String start_date;
		public String end_date;	
		public String address;
		public Coordinates coordinates;
		public Boolean privado;
		public ArrayList<VocabularyES> vocabularies;
		
		
		// CONSTRUCTOR
		public EducationalSetting(){
			this.id = -1;
			this.title = null;
			this.imagePath = null;
			this.image_Url = null;
			this.image_Uri = null;
			this.imageBitmap = null;
			this.description = null;	
			this.short_description = null;
			this.age_range = null;
			this.start_date = null;
			this.end_date = null;
			this.keywords = null;
			this.address = null;
			this.coordinates = new Coordinates();
			this.privado = null;	
			this.vocabularies = new ArrayList<VocabularyES>();
		}

		public EducationalSetting(Parcel in){
			this();
			readFromParcel(in);
		}
		
		public int getId(){
			return id;
		}
		
		public void setId(int id){
			this.id = id;
		}
		
		public String getName(){
			return title;
		}
		
		public void setName(String str){
			title = str;
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
			return image_Uri;
		}
		
		public void setImageUri(String str){
			image_Uri = str;
		}
		
		public String getImageUrl(){
			return image_Url;
		}
		
		public void setImageUrl(String str){
			image_Url = str;
		}
		
		public Bitmap getBitmapImage(){
			return imageBitmap;
		}
		
		public void setBitmapImage(Bitmap bm){
			imageBitmap = bm;
		}
		
		
		
		public static final Parcelable.Creator<EducationalSetting> CREATOR = new Parcelable.Creator<EducationalSetting>() {
			
	        public EducationalSetting createFromParcel(Parcel in) {
	        	return new EducationalSetting(in);
	        }

			@Override
			public EducationalSetting[] newArray(int size) {
				return new EducationalSetting[size];
			}      
	    };
	    
		@Override
		public int describeContents() {
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(id);
			dest.writeString(title);
			dest.writeString(imagePath);
			dest.writeString(image_Url);
			dest.writeString(image_Uri);
			dest.writeString(description);
			dest.writeString(short_description);
			dest.writeString(keywords);
			dest.writeString(age_range);
			dest.writeString(start_date);
			dest.writeString(end_date);
			dest.writeString(address);
			dest.writeParcelable(coordinates, flags);
			//dest.writeParcelable(imageBitmap, flags);
			//dest.writeList(vocabularies);
			//dest.writeBooleanArray(privado);		
		}
		
		private void readFromParcel(Parcel in) {
			 id = in.readInt();
			 title = in.readString();
			 imagePath = in.readString();
			 image_Url = in.readString();
			 image_Uri = in.readString();
			 description = in.readString();	 
			 short_description = in.readString();
			 keywords = in.readString();
			 age_range = in.readString();
			 start_date = in.readString();
			 end_date = in.readString();
			 address = in.readString();
			 coordinates = in.readParcelable(Coordinates.class.getClassLoader());
			 //imageBitmap = in.readParcelable(Bitmap.class.getClassLoader());
			 //in.readTypedList(vocabularies, VocabularyES.CREATOR);
	   }
		
	   
	   
}
