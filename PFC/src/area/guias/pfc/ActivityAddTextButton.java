package area.guias.pfc;

import android.os.Parcel;
import android.os.Parcelable;

/////////////////// CLASS GUIA //////////////////////////////

public class ActivityAddTextButton implements Parcelable{
	// VARIABLES
	// GENERAL
	public int buttonId;
	public int belowId;
	public int componentId;
	
	// CONSTRUCTOR
	public ActivityAddTextButton(){
		buttonId = -1;
		belowId = -1;
		componentId = -1;
	}
	
	
	
	
	
	public ActivityAddTextButton(Parcel in){
		readFromParcel(in);
	}
	
	
	
	
	
	public static final Parcelable.Creator<ActivityAddTextButton> CREATOR = new Parcelable.Creator<ActivityAddTextButton>() {
		
        public ActivityAddTextButton createFromParcel(Parcel in) {
        	return new ActivityAddTextButton(in);
        }

		@Override
		public ActivityAddTextButton[] newArray(int size) {
			return new ActivityAddTextButton[size];
		}      
    };
    
    
    
    
	@Override
	public int describeContents() {
		return 0;
	}

	
	
	
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// GENERAL
		dest.writeInt(buttonId);
		dest.writeInt(belowId);
		dest.writeInt(componentId);
	}
	
	
	
	
	
	private void readFromParcel(Parcel in) {
		 // GENERAL
		 buttonId = in.readInt();
		 belowId = in.readInt();
		 componentId = in.readInt();
    }





	public int getButtonId() {
		return buttonId;
	}





	public void setButtonId(int buttonId) {
		this.buttonId = buttonId;
	}





	public int getBelowId() {
		return belowId;
	}





	public void setBelowId(int belowId) {
		this.belowId = belowId;
	}





	public int getComponentId() {
		return componentId;
	}





	public void setComponentId(int componentId) {
		this.componentId = componentId;
	}
	

	
}