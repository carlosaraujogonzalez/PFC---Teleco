package area.guias.pfc;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;


public class Owner implements Parcelable{
		final static int TRUE = 1;
		//final static int FALSE = 2;
		final static int NONE = 3;
		final static int ERROR = -1;
	
		public int owner;
		
		// CONSTRUCTOR
		public Owner(){
			this.owner = NONE;
		}
		
	    public int getOwner() {
	    	if ((owner == TRUE)||(owner == NONE)) return owner;
	    	else return ERROR;
	    }

	    public void setOwner(int owner) {
	    	if ((owner == TRUE)||(owner == NONE)) this.owner = owner;	    	
	    	else Log.e("Error", "Error en el tipo de propietario");	    	
	    }	    

	    public int OwnerTrue() {
	    	return TRUE;
	    }
	    
	    /*public int OwnerFalse() {
	    	return FALSE;
	    }*/
	    
	    public int OwnerNone(){
	    	return NONE;
	    }
	    
	    public Owner(Parcel in){
			readFromParcel(in);
		}
	    
	    public static final Parcelable.Creator<Owner> CREATOR = new Parcelable.Creator<Owner>() {
			
	        public Owner createFromParcel(Parcel in) {
	        	return new Owner(in);
	        }

			@Override
			public Owner[] newArray(int size) {
				return new Owner[size];
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
			dest.writeInt(owner);	
		}
	    	    
		private void readFromParcel(Parcel in) {
			owner = in.readInt();
	    }
}
