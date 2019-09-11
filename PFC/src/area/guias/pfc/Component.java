package area.guias.pfc;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;


public class Component implements Parcelable{
	// VARIABLES
		public int id;
		public int type;
		public int position;
		public ArrayList<Text> texts;
		
		// CONSTRUCTOR
		public Component(){
			this.id = -1;
			this.type = -1;
			this.position = -1;
			this.texts = new ArrayList<Text>();
		}
		
		
		public Component(Parcel in){
			this();
			readFromParcel(in);
		}
	    
	    public static final Parcelable.Creator<Component> CREATOR = new Parcelable.Creator<Component>() {
			
	        public Component createFromParcel(Parcel in) {
	        	return new Component(in);
	        }

			@Override
			public Component[] newArray(int size) {
				return new Component[size];
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
			dest.writeInt(type);
			dest.writeInt(position);
			dest.writeList(texts);			
		}
	    	    
		private void readFromParcel(Parcel in) {
			id = in.readInt();
			type = in.readInt();
			position = in.readInt();
			in.readTypedList(texts, Text.CREATOR);
	    }
		
		public int getId() {
	        return this.id;
	    }

	    public void setId(int num) {
	        this.id = num;
	    }

	    public int getType() {
	        return this.type;
	    }

	    public void setType(int num) {
	        this.type = num;
	    }

	    public int getPosition() {
	        return this.position;
	    }

	    public void setPosition(int num) {
	        this.position = num;
	    }

	    public ArrayList<Text> getTexts() {
	        return this.texts;
	    }

	    public void setTexts(Text text) {
	        this.texts.add(text);
	    }
	    	    
	    
}
