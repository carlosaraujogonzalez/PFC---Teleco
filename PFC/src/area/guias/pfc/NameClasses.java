package area.guias.pfc;

import android.os.Parcel;
import android.os.Parcelable;

public class NameClasses implements Parcelable{
	final static int GUIDEVIEWTABS=1, TECHNICALSETTINGVIEW=2;
	final static int NONE=0;
	int name_class;
	
	
	
	public NameClasses(){
		name_class = NONE; 
	}

		
	
	public NameClasses(Parcel in){
			readFromParcel(in);
	}
	    
	
	
	public static final Parcelable.Creator<NameClasses> CREATOR = new Parcelable.Creator<NameClasses>() {
			
		public NameClasses createFromParcel(Parcel in) {
			return new NameClasses(in);
	    }

		@Override
		public NameClasses[] newArray(int size) {
			return new NameClasses[size];
		}      
		
	};
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(name_class);	
	}
	    	    
	private void readFromParcel(Parcel in) {
		name_class = in.readInt();
	}
		
	public int getName_class() {
		return name_class;
	}

	public void setName_class(int name_class) {
		this.name_class = name_class;
	}

	public static int getGuideviewtabs() {
		return GUIDEVIEWTABS;
	}

	public static int getTechnicalsettingview() {
		return TECHNICALSETTINGVIEW;
	}

	public static int getNone() {
		return NONE;
	}
		
}
