package area.guias.pfc;

import android.os.Parcel;
import android.os.Parcelable;

/////////////////// CLASS GUIA //////////////////////////////

public class ActivityAddedText implements Parcelable{
	// VARIABLES
	// GENERAL
	public int editTextId;
	public int componentId;
	public int componentPosition;
	public String text;
	
	// CONSTRUCTOR
	public ActivityAddedText(){
		editTextId = -1;
		componentId = -1;
		componentPosition = -1;
		text = null;
	}
	
	
	
	
	
	public ActivityAddedText(Parcel in){
		readFromParcel(in);
	}
	
	
	
	
	
	public static final Parcelable.Creator<ActivityAddedText> CREATOR = new Parcelable.Creator<ActivityAddedText>() {
		
        public ActivityAddedText createFromParcel(Parcel in) {
        	return new ActivityAddedText(in);
        }

		@Override
		public ActivityAddedText[] newArray(int size) {
			return new ActivityAddedText[size];
		}      
    };
    
    
    
    
	@Override
	public int describeContents() {
		return 0;
	}

	
	
	
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// GENERAL
		dest.writeInt(editTextId);
		dest.writeInt(componentId);
		dest.writeInt(componentPosition);
		dest.writeString(text);
	}
	
	
	
	
	
	private void readFromParcel(Parcel in) {
		 // GENERAL
		 editTextId = in.readInt();
		 componentId = in.readInt();
		 componentPosition = in.readInt();
		 text = in.readString();
    }
		
	public int getEditTextId() {
		return editTextId;
	}

	public void setEditTextId(int editTextId) {
		this.editTextId = editTextId;
	}

	public void setComponentId(int id){
		this.componentId = id;
	}
	
	public int getComponentId(){
		return this.componentId;
	}
	
	public void setComponentPosition(int position){
		this.componentPosition = position;
	}
	
	public int getComponentPosition(){
		return this.componentPosition;
	}
	
	public void setText(String text){
		this.text = text;
	}
	
	public String getText(){
		return this.text;
	}	
	
}