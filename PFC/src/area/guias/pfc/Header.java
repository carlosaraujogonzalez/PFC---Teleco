package area.guias.pfc;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class Header implements Parcelable {
	String title, description, shortDescription, imageUrl, imageUri, imagePath;
	Bitmap imageBitmap;
	Drawable imageDrawable;
	
	public Header(){
		title = null;
		description = null;
		shortDescription = null;
		imagePath = null;
		imageUrl = null;
		imageUri = null;
		imageBitmap = null;
		imageDrawable = null;		
	}
	
	
	
	
	
	public Header(Parcel in){
		readFromParcel(in);
	}
	
	
	
	
	
	public static final Parcelable.Creator<Header> CREATOR = new Parcelable.Creator<Header>() {
		
        public Header createFromParcel(Parcel in) {
        	return new Header(in);
        }

		@Override
		public Header[] newArray(int size) {
			return new Header[size];
		}      
    };
	
    
    
    
    
	@Override
	public int describeContents() {
		return 0;
	}

	
	
	
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(title);
		dest.writeString(description);
		dest.writeString(shortDescription);
		dest.writeString(imagePath);
		dest.writeString(imageUrl);
		dest.writeString(imageUri);
		//dest.writeParcelable(imageBitmap, flags);
	}
	
	
	
	

	private void readFromParcel(Parcel in) {
		 title = in.readString();
		 description = in.readString();
		 shortDescription = in.readString();
		 imagePath = in.readString();
		 imageUrl = in.readString();
		 imageUri = in.readString();
		 //imageBitmap = (Bitmap) in.readParcelable(Bitmap.class.getClassLoader());
	}
	
	
	
	
	
	public void setTitle(String str){
		if (str.equals("") == false) this.title = str;
		else this.title = "Title";
	}
	
	public String getTitle(String str){
		return this.title;
	}
	
	
	
	
	
	public void setDescription(String str){
		
		if (str.equals("") == false){
			this.description = str;
		}
		else {
			this.description = "Description";
		}
		
		setShortDescription(str);
	}
		
	public String getDescription(){
		return this.description;
	}
	
	
	
	
	
	public void setShortDescription(String str){
		if (str.length() > 90) shortDescription = str.substring(0, 89)+"...";
		else shortDescription = str;
	}
		
	public String getShortDescription(){
		return shortDescription;
	}
}
