package area.guias.pfc;

import android.graphics.Bitmap;
import android.os.Parcel;
import android.os.Parcelable;

public class Application implements Parcelable{
	
	public int id;
	public String name;
	public String description;
	public String short_description;
	public String url;
	public Bitmap image;
	public String imageUrl;
	public String imageUri;
	public Boolean checked;
	public String checkedString;
	public Boolean privado;
			
	public Application(){
		this.id = -1;
		this.name = null;
		this.description = null;	
		this.short_description = null;
		this.url = null;
		this.image = null;
		this.imageUri = null;
		this.imageUrl = null;
		this.checked = false;
		this.checkedString = "Añadir";
		this.privado = false;
	}
	
	public Application(Parcel in){
		readFromParcel(in);
	}
    
    public static final Parcelable.Creator<Application> CREATOR = new Parcelable.Creator<Application>() {
		
        public Application createFromParcel(Parcel in) {
        	return new Application(in);
        }

		@Override
		public Application[] newArray(int size) {
			return new Application[size];
		}      
    };
    
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(id);
		dest.writeString(name);
		dest.writeString(description);
		dest.writeString(short_description);
		dest.writeString(url);
		dest.writeParcelable(image, flags);
		dest.writeString(imageUrl);
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
		url = in.readString();
		image = in.readParcelable(Bitmap.class.getClassLoader());
		imageUrl = in.readString();
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
	}
	
	public String getDescription(){
		return this.description;
	}
	
	public void setShortDescription(String str){
		if (str.length() > 100) short_description = str.substring(0, 99)+"...";
		else short_description = str;
	}
	
	public String getShortDescription(){
		return short_description;
	}
}
