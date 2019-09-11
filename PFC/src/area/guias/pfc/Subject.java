package area.guias.pfc;

import android.os.Parcel;
import android.os.Parcelable;

public class Subject implements Parcelable{
	int id;
	String term;
	
	public Subject(){
		this.id = -1;
		this.term = null;
	}
	
	public void setId(int num){
		this.id = num;
	}
	
	public int getId(){
		return id;
	}
	
	public void setTerm(String str){
		this.term = str;
	}
	
	public String getTerm(){
		return term;
	}

	public Subject(Parcel in){
		readFromParcel(in);
	}
	
	public static final Parcelable.Creator<Subject> CREATOR = new Parcelable.Creator<Subject>() {
		
        public Subject createFromParcel(Parcel in) {
        	return new Subject(in);
        }

		@Override
		public Subject[] newArray(int size) {
			return new Subject[size];
		}      
    };
    
	@Override
	public int describeContents() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int arg1) {
		dest.writeInt(id);
		dest.writeString(term);		
	}
	
	private void readFromParcel(Parcel in) {
		 id = in.readInt();
		 term = in.readString();
   }
}
