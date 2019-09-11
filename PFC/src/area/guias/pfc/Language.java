package area.guias.pfc;

import android.os.Parcel;
import android.os.Parcelable;

public class Language implements Parcelable{
	final static int ENGLISH = 1, SPANISH = 2, GALICIAN = 3;
	int language;
	
	public Language(){
		this.language = GALICIAN;
	}
	
	public void setEnglish(){
		this.language = ENGLISH;
	}
	
	public void setSpanish(){
		this.language = SPANISH;
	}
	
	public void setGalician(){
		this.language = GALICIAN;
	}
	
	public int getLanguage(){
		return this.language;
	}
	
	public void setLanguage(int language){
		if ((language > 0) && (language < 4))
			this.language = language;
	}

	public int getSpanish(){
		return SPANISH;
	}
	
	public int getEnglish(){
		return ENGLISH;
	}
	
	public int getGalician(){
		return GALICIAN;
	}
	
	public Language(Parcel in){
		readFromParcel(in);
	}
	
	public String getStringLanguage(){
		if (this.language == SPANISH) return "es";
		else if (this.language == ENGLISH) return "en";
		else if (this.language == GALICIAN) return "gl";
		else return "gl";
			
	}
	
	public static final Parcelable.Creator<Language> CREATOR = new Parcelable.Creator<Language>() {
		
        public Language createFromParcel(Parcel in) {
        	return new Language(in);
        }

		@Override
		public Language[] newArray(int size) {
			return new Language[size];
		}      
    };
    
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeInt(language);
	}
	
	// We have to read in the same order that we write
	private void readFromParcel(Parcel in) {
		 language = in.readInt();
    }
}
