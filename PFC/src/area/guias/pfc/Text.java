package area.guias.pfc;


import android.os.Parcel;
import android.os.Parcelable;



public class Text implements Parcelable{
	// VARIABLES
		public int id;
		public int position;
		public String content;
		
		// CONSTRUCTOR
		public Text(){
			this.id = -1;
			this.position = -1;
			this.content = null;
		}
		
		
		public Text(Parcel in){
			readFromParcel(in);
		}
	    
	    public static final Parcelable.Creator<Text> CREATOR = new Parcelable.Creator<Text>() {
			
	        public Text createFromParcel(Parcel in) {
	        	return new Text(in);
	        }

			@Override
			public Text[] newArray(int size) {
				return new Text[size];
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
			dest.writeInt(position);
			dest.writeString(content);			
		}
	    	    
		private void readFromParcel(Parcel in) {
			id = in.readInt();
			position = in.readInt();
			content = in.readString();
	    }
		
		
		public int getId() {
	        return this.id;
	    }

	    public void setId(int num) {
	        this.id = num;
	    }

	    public int getPosition() {
	        return this.position;
	    }

	    public void setPosition(int num) {
	        this.position = num;
	    }

	    public String getContent(){
	    	return this.content;
	    }
	    	    
	    public void setContent(String name){
	    	this.content = name;
	    }
}
