package area.guias.pfc;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


public class Person implements Parcelable{
		// VARIABLES
		public int personId;
		public String name;
		public String description;
		public String shortDescription;
		public String imageUri;
		public String imageUrl;
		public String url;
		public Bitmap bitmapImage;
		public Boolean checked;
		public String checkedString;
		
		// CONSTRUCTOR
		public Person(){
			this.personId = -1;
			this.name = null;
			this.description = null;
			this.shortDescription = null;
			this.imageUri = null;
			this.imageUrl = null;
			this.url = null;
			this.bitmapImage = null;
			this.checked = false;
			this.checkedString = "Añadir";
		}
		
		public Person(Parcel in){
			readFromParcel(in);
		}
				
		public static final Parcelable.Creator<Person> CREATOR = new Parcelable.Creator<Person>() {
			
	        public Person createFromParcel(Parcel in) {
	        	return new Person(in);
	        }

			@Override
			public Person[] newArray(int size) {
				return new Person[size];
			}      
	    };
	    
	    
	    
	    
		@Override
		public int describeContents() {
			return 0;
		}

		
		
		
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(personId);
			dest.writeString(name);
			dest.writeString(description);
			dest.writeString(shortDescription);
			dest.writeString(imageUri);
			dest.writeString(imageUrl);
			dest.writeString(url);
			dest.writeParcelable(bitmapImage, flags);
			boolean checked_aux[] = {false};
			checked_aux[0] = checked;
			dest.writeBooleanArray(checked_aux);
			dest.writeString(checkedString);
		}
		
		
		
		
		
		private void readFromParcel(Parcel in) {
			 personId = in.readInt();
			 name = in.readString();
			 description = in.readString();
			 shortDescription = in.readString();
			 imageUri = in.readString();
			 imageUrl = in.readString();
			 url = in.readString();
			 bitmapImage = in.readParcelable(Bitmap.class.getClassLoader());
			 boolean checked_aux[] = {false};
			 in.readBooleanArray(checked_aux);
			 checked = checked_aux[0];
			 checkedString = in.readString();
	    }
		
		public int getId(){
			return personId;
		}
		
		public void setId(int id){
			personId = id;
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
			return shortDescription;
		}
		
		public void setShortDescription(String str){
			if (str.length()>100) shortDescription = str.substring(0, 99)+"...";
			else shortDescription = str;
		}
		
		public String getUrl(){
			return url;
		}
		
		public void setUrl(String str){
			url = str;
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
			return bitmapImage;
		}
		
		public void setBitmapImage(Bitmap bm){
			bitmapImage = bm;
		}
		
		public boolean getChecked(){
			return checked;
		}
		
		public void setChecked(boolean value){
			checked = value;
		}
		
		public String getText(){
			return checkedString;
		}
		
		public void setText(String str){
			checkedString = str;
		}
	    
}
