package area.guias.pfc;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;


public class Box implements Parcelable{
	// VARIABLES
		public String type;
		public int position;
		public ArrayList<Component> components;
		
		// CONSTRUCTOR
		public Box(){
			this.type = null;
			this.position = -1;
			this.components = new ArrayList<Component>();
		}
		
	    public String getType() {
	        return this.type;
	    }

	    public void setType(String name) {
	        this.type = name;
	    }

	    public int getPosition() {
	        return this.position;
	    }

	    public void setPosition(int num) {
	        this.position = num;
	    }

	    public ArrayList<Component> getComponents() {
	        return this.components;
	    }

	    public void setComponents(Component comp) {
	        this.components.add(comp);
	    }

	    public Box(Parcel in){
	    	this();
			readFromParcel(in);
		}
	    
	    public static final Parcelable.Creator<Box> CREATOR = new Parcelable.Creator<Box>() {
			
	        public Box createFromParcel(Parcel in) {
	        	return new Box(in);
	        }

			@Override
			public Box[] newArray(int size) {
				return new Box[size];
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
			dest.writeInt(position);
			dest.writeString(type);
			dest.writeList(components);			
		}
	    	    
		private void readFromParcel(Parcel in) {
			position = in.readInt();
			type = in.readString();
			in.readTypedList(components, Component.CREATOR);
	    }
}
