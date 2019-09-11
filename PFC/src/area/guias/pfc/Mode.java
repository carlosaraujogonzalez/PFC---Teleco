package area.guias.pfc;

import android.os.Parcel;
import android.os.Parcelable;


public class Mode implements Parcelable{
		final static int EDIT_MODE = 1;
		final static int VIEW_MODE = 2;
		final static int ERROR = -1;
	
		public int mode;
		
		// CONSTRUCTOR
		public Mode(){
			this.mode = VIEW_MODE;
		}
		
	    public int getMode() {
	    	if ((mode == VIEW_MODE)||(mode == EDIT_MODE)) return mode;
	    	else return ERROR;
	    }

	    public void setMode(int mode) {
	    	if ((mode == VIEW_MODE)||(mode == EDIT_MODE)) this.mode = mode;
	    }	    

	    public int EditMode() {
	    	return EDIT_MODE;
	    }
	    
	    public int ViewMode() {
	    	return VIEW_MODE;
	    }
	    
	    public Mode(Parcel in){
			readFromParcel(in);
		}
	    
	    public static final Parcelable.Creator<Mode> CREATOR = new Parcelable.Creator<Mode>() {
			
	        public Mode createFromParcel(Parcel in) {
	        	return new Mode(in);
	        }

			@Override
			public Mode[] newArray(int size) {
				return new Mode[size];
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
			dest.writeInt(mode);	
		}
	    	    
		private void readFromParcel(Parcel in) {
			mode = in.readInt();
	    }
}
