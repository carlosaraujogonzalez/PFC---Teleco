package area.guias.pfc;

import android.os.Parcel;
import android.os.Parcelable;

/////////////////// CLASS GUIA //////////////////////////////

public class ActivityTextView implements Parcelable{
	// VARIABLES
	// GENERAL
	public int id; // id in the layout
	public int correspondingEditTextId; // in the layout
	public int textId; // on web
	public int textPosition; // on web
	public int componentId; // on web
	public int componentPosition; // on web
	public String text;
	public int correspondingDeleteTextImageId; // layout
	public int correspondingAddTextImageId; // layout
	
	// CONSTRUCTOR
	public ActivityTextView(){
		id = -1;
		correspondingEditTextId = -1;
		textId = -1;
		textPosition = -1;
		componentId = -1;
		componentPosition = -1;
		text = null;
		correspondingDeleteTextImageId = -1;
		correspondingAddTextImageId = -1;
	}
	
	
	
	
	
	public ActivityTextView(Parcel in){
		readFromParcel(in);
	}
	
	
	
	
	
	public static final Parcelable.Creator<ActivityTextView> CREATOR = new Parcelable.Creator<ActivityTextView>() {
		
        public ActivityTextView createFromParcel(Parcel in) {
        	return new ActivityTextView(in);
        }

		@Override
		public ActivityTextView[] newArray(int size) {
			return new ActivityTextView[size];
		}      
    };
    
    
    
    
	@Override
	public int describeContents() {
		return 0;
	}

	
	
	
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		// GENERAL
		dest.writeInt(id);
		dest.writeInt(correspondingEditTextId);
		dest.writeInt(textId);
		dest.writeInt(textPosition);
		dest.writeInt(componentId);
		dest.writeInt(componentPosition);
		dest.writeString(text);
		dest.writeInt(correspondingDeleteTextImageId);
		dest.writeInt(correspondingAddTextImageId);
	}
	
	
	
	
	
	private void readFromParcel(Parcel in) {
		 // GENERAL
		 id = in.readInt();
		 correspondingEditTextId = in.readInt();
		 textId = in.readInt();
		 textPosition = in.readInt();
		 componentId = in.readInt();
		 componentPosition = in.readInt();
		 text = in.readString();
		 correspondingDeleteTextImageId = in.readInt();
		 correspondingAddTextImageId = in.readInt();
    }
	
	
	public void setId(int id){
		this.id = id;
	}
	
	public int getId(){
		return this.id;
	}
	
	public void setCorrespondingEditTextId(int id){
		this.correspondingEditTextId = id;
	}
	
	public int getCorrespondingEditTextId(){
		return this.correspondingEditTextId;
	}
	
	public void setTextId(int id){
		this.textId = id;
	}
	
	public int getTextId(){
		return this.textId;
	}
	
	public void setTextPosition(int position){
		this.textPosition = position;
	}
	
	public int getTextPosition(){
		return this.textPosition;
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
	
	public void setCorrespondingDeleteTextImageId(int id){
		this.correspondingDeleteTextImageId = id;
	}
	
	public int getCorrespondingDeleteTextImageId(){
		return this.correspondingDeleteTextImageId;
	}
	
	public void setCorrespondingAddTextImageId(int id){
		this.correspondingAddTextImageId = id;
	}
	
	public int getCorrespondingAddTextImageId(){
		return this.correspondingAddTextImageId;
	}
	
}