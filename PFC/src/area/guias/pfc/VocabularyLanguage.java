package area.guias.pfc;

import android.os.Parcel;
import android.os.Parcelable;

public class VocabularyLanguage implements Parcelable{
	int id;
	String term;
	
	public VocabularyLanguage(){
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

	public VocabularyLanguage(Parcel in){
		readFromParcel(in);
	}
	
	public static final Parcelable.Creator<VocabularyLanguage> CREATOR = new Parcelable.Creator<VocabularyLanguage>() {
		
        public VocabularyLanguage createFromParcel(Parcel in) {
        	return new VocabularyLanguage(in);
        }

		@Override
		public VocabularyLanguage[] newArray(int size) {
			return new VocabularyLanguage[size];
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
