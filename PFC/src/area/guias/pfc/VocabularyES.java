package area.guias.pfc;

import android.os.Parcel;
import android.os.Parcelable;

public class VocabularyES implements Parcelable{
	int id;
	String type, term;
	
	public VocabularyES(){
		this.id = -1;
		this.type = null;
		this.term = null;
	}
	
	public VocabularyES(Parcel in){
		this();
		readFromParcel(in);
	}
	
	public static final Parcelable.Creator<VocabularyES> CREATOR = new Parcelable.Creator<VocabularyES>() {
		
        public VocabularyES createFromParcel(Parcel in) {
        	return new VocabularyES(in);
        }

		@Override
		public VocabularyES[] newArray(int size) {
			return new VocabularyES[size];
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
		dest.writeString(type);
		dest.writeString(term);
	}
    
	private void readFromParcel(Parcel in) {
		 id = in.readInt();
		 type = in.readString();
		 term = in.readString();
    }
}
