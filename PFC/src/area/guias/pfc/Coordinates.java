package area.guias.pfc;

import android.os.Parcel;
import android.os.Parcelable;

public class Coordinates implements Parcelable{
	double latitude, longitude;
	
	// Vigo coordinates 42.217840940451474,-8.707640022039413
	public Coordinates(){
		this.latitude = 42.217840940451474;
		this.longitude = -8.707640022039413;
	}
	
	public void setCoordinates(double latitude, double longitude){
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	public void setLatitude(double doub){
		this.latitude = doub;
	}
	
	public double getLatitude(){
		return this.latitude;
	}
	
	public void setLongitude(double doub){
		this.longitude = doub;
	}
	
	public double getLongitude(){
		return this.longitude;
	}

	public Coordinates(Parcel in){
		readFromParcel(in);
	}
	
	public static final Parcelable.Creator<Coordinates> CREATOR = new Parcelable.Creator<Coordinates>() {
		
        public Coordinates createFromParcel(Parcel in) {
        	return new Coordinates(in);
        }

		@Override
		public Coordinates[] newArray(int size) {
			return new Coordinates[size];
		}      
    };
    
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeDouble(latitude);
		dest.writeDouble(longitude);		
	}
	
	private void readFromParcel(Parcel in) {
		 latitude = in.readDouble();
		 longitude = in.readDouble();
	}
}
