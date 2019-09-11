package area.guias.pfc;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;


public class Vocabulary implements Parcelable{
	public ArrayList<Subject> subjects;
	public ArrayList<EducationLevel> educational_levels;
	public ArrayList<VocabularyLanguage> languages;
	
	public Vocabulary(){
		this.subjects = new ArrayList<Subject>();
		this.educational_levels = new ArrayList<EducationLevel>();
		this.languages = new ArrayList<VocabularyLanguage>();
	}
	
	public Vocabulary(Parcel in){
		this();
		readFromParcel(in);
	}
	
	public static final Parcelable.Creator<Vocabulary> CREATOR = new Parcelable.Creator<Vocabulary>() {
		
        public Vocabulary createFromParcel(Parcel in) {
        	return new Vocabulary(in);
        }

		@Override
		public Vocabulary[] newArray(int size) {
			return new Vocabulary[size];
		}      
    };
    
    @Override
	public int describeContents() {
		return 0;
	}
    
    @Override
	public void writeToParcel(Parcel dest, int flags) {
		// GENERAL
		dest.writeList(subjects);
		dest.writeList(educational_levels);
		dest.writeList(languages);
	}
    
	private void readFromParcel(Parcel in) {
		 in.readTypedList(subjects, Subject.CREATOR);
		 in.readTypedList(educational_levels, EducationLevel.CREATOR);
		 in.readTypedList(languages, VocabularyLanguage.CREATOR);
    }
}
