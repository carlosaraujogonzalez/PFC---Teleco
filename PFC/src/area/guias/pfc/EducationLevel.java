package area.guias.pfc;

import android.os.Parcel;
import android.os.Parcelable;

public class EducationLevel implements Parcelable{
	int id;
	String term;
	
	public EducationLevel(){
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

	public EducationLevel(Parcel in){
		readFromParcel(in);
	}
	
	public static final Parcelable.Creator<EducationLevel> CREATOR = new Parcelable.Creator<EducationLevel>() {
		
        public EducationLevel createFromParcel(Parcel in) {
        	return new EducationLevel(in);
        }

		@Override
		public EducationLevel[] newArray(int size) {
			return new EducationLevel[size];
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
