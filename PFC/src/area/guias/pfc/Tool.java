package area.guias.pfc;

import java.util.ArrayList;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;


public class Tool implements Parcelable {
	// VARIABLES
		public int toolId;
		public String type; // "device" or "application"
		public String name;
		public String description;
		public String shortDescription;
		public String imageUri;
		public String imageUrl;
		public Bitmap bitmapImage;
		public boolean checked;
		public String checkedString;
		public ArrayList<Integer> deviceIds;
		public ArrayList<Integer> applicationIds;
		
		// CONSTRUCTOR
		public Tool(){
			this.toolId = -1;
			this.type = null;
			this.name = null;
			this.description = null;
			this.shortDescription = null;
			this.imageUri = null;
			this.imageUrl = null;
			this.bitmapImage = null;
			this.checked = false;
			this.checkedString = "Añadir";
			this.deviceIds = new ArrayList<Integer>();
			this.applicationIds = new ArrayList<Integer>();
		}
	
		
		
		
		
		public Tool(Parcel in){
			readFromParcel(in);
		}
		
		
		
		
		
		public static final Parcelable.Creator<Tool> CREATOR = new Parcelable.Creator<Tool>() {
			
	        public Tool createFromParcel(Parcel in) {
	        	return new Tool(in);
	        }

			@Override
			public Tool[] newArray(int size) {
				return new Tool[size];
			}      
	    };
	    
	    
	    
	    
		@Override
		public int describeContents() {
			return 0;
		}

		
		
		
		
		@Override
		public void writeToParcel(Parcel dest, int flags) {
			dest.writeInt(toolId);
			dest.writeString(type);
			dest.writeString(name);
			dest.writeString(description);
			dest.writeString(shortDescription);
			dest.writeString(imageUri);
			dest.writeString(imageUrl);
			dest.writeParcelable(bitmapImage, flags);
			boolean checked_aux[] = {false};
			checked_aux[0] = checked;
			dest.writeBooleanArray(checked_aux);
			dest.writeString(checkedString);
			dest.writeSerializable(deviceIds);
			dest.writeSerializable(applicationIds);
		}
		
		
		
		
		
		@SuppressWarnings("unchecked")
		private void readFromParcel(Parcel in) {
			 // GENERAL
			 toolId = in.readInt();
			 type = in.readString();
			 name = in.readString();
			 description = in.readString();
			 shortDescription = in.readString();
			 imageUri = in.readString();
			 imageUrl = in.readString();
			 bitmapImage = in.readParcelable(Bitmap.class.getClassLoader());
			 boolean checked_aux[] = {false};
			 in.readBooleanArray(checked_aux);
			 checked = checked_aux[0];
			 checkedString = in.readString();
			 deviceIds = (ArrayList<Integer>) in.readSerializable();
			 applicationIds = (ArrayList<Integer>) in.readSerializable();
	    }
		
		public int getId(){
			return toolId;
		}
		
		public void setId(int id){
			toolId = id;
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
		
		public String getType(){
			return type;
		}
		
		public void setType(String str){
			type = str;
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
		
	    public ArrayList<Integer> getDeviceIds() {
	        return this.deviceIds;
	    }

	    public void setDeviceId(int id) {
	        this.deviceIds.add(id);
	    }

	    public ArrayList<Integer> getApplicationIds() {
	        return this.applicationIds;
	    }

	    public void setApplicationId(int id) {
	        this.applicationIds.add(id);
	    }
	    
	    
}
