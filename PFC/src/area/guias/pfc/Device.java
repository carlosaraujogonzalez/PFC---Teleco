package area.guias.pfc;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Device implements Parcelable{
	// VARIABLES
		public int id;
		public String name;
		public String UrlImage;
		public String description;
		public String short_description;
		public Bitmap image;
		public String imageUri;
		public boolean checked;
		public String checkedString;
		public boolean privado;
		
		// CONSTRUCTOR
		public Device(){
			this.id = -1;
			this.name = null;
			this.description = null;	
			this.short_description = null;
			this.image = null;
			this.UrlImage = null;
			this.imageUri = null;
			this.checked = false;
			this.checkedString = "Añadir";
			this.privado = false;
		}
		
		public Device(Parcel in){
			readFromParcel(in);
		}
	    
	    public static final Parcelable.Creator<Device> CREATOR = new Parcelable.Creator<Device>() {
			
	        public Device createFromParcel(Parcel in) {
	        	return new Device(in);
	        }

			@Override
			public Device[] newArray(int size) {
				return new Device[size];
			}      
	    };
	    
		@Override
		public int describeContents() {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public void writeToParcel(Parcel dest, int flags) {
			// TODO Auto-generated method stub
			dest.writeInt(id);
			dest.writeString(name);
			dest.writeString(description);
			dest.writeString(short_description);
			dest.writeParcelable(image, flags);
			dest.writeString(UrlImage);
			dest.writeString(imageUri);
			boolean checked_aux[] = {false};
			checked_aux[0] = checked;
			dest.writeBooleanArray(checked_aux);
			dest.writeString(checkedString);
			boolean privado_aux[] = {false};
			privado_aux[0] = privado;
			dest.writeBooleanArray(privado_aux);			
		}
	    	    
		private void readFromParcel(Parcel in) {
			id = in.readInt();
			name = in.readString();
			description = in.readString();
			short_description = in.readString();
			image = in.readParcelable(Bitmap.class.getClassLoader());
			UrlImage = in.readString();
			imageUri = in.readString();
			boolean checked_aux[] = {false};
			in.readBooleanArray(checked_aux);
			checked = checked_aux[0];
			checkedString = in.readString();
			boolean privado_aux[] = {false};
			in.readBooleanArray(privado_aux);
			privado = privado_aux[0];
	    }
		
		public void setDescription(String str){
			this.description = str;
			setShortDescription(str);
		}
		
		public void setShortDescription(String str){
			if (str.length() > 100) short_description = str.substring(0, 99)+"...";
			else short_description = str;
		}
		
		public String getShortDescription(){
			return short_description;
		}
}
