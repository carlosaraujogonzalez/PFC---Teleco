package area.guias.pfc;

import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;

/////////////////// CLASS GUIA //////////////////////////////

public class Guide implements Parcelable{
	// VARIABLES
	// GENERAL
	public int id;	
	public String from_activity;
	public ArrayList<String> keywords;
	public Owner owner;
	
	// TAB1	
	public Header header;	
	
	// TAB2
	public TechnicalSetting technical_setting;
	
	// TAB3
	public EducationalSetting educational_setting;
	
	// TAB4
	public ActivitySequence activity_sequence;
	
	// CONSTRUCTOR
	public Guide(){
		id = -1;
		from_activity = "null";
		keywords = new ArrayList<String>();
		owner = new Owner();
		
		// TAB 1		
		header = new Header();
		
		// TAB2
		technical_setting = new TechnicalSetting();
		
		// TAB3
		educational_setting = new EducationalSetting();
		
		// TAB4
		activity_sequence = new ActivitySequence();
		
	}
	
	
	
	
	
	public Guide(Parcel in){
		readFromParcel(in);
	}
	
	
	
	
	
	public static final Parcelable.Creator<Guide> CREATOR = new Parcelable.Creator<Guide>() {
		
        public Guide createFromParcel(Parcel in) {
        	return new Guide(in);
        }

		@Override
		public Guide[] newArray(int size) {
			return new Guide[size];
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
		dest.writeString(from_activity);
		dest.writeSerializable(keywords);
		dest.writeParcelable(owner, flags);
		
		// TAB 1		
		dest.writeParcelable(header,  flags);
		// TAB 2
		dest.writeParcelable(technical_setting, flags);
		// TAB 3
		dest.writeParcelable(educational_setting, flags);
		// TAB 4
		dest.writeParcelable(activity_sequence, flags);
	}
	
	
	
	
	
	@SuppressWarnings("unchecked")
	private void readFromParcel(Parcel in) {
		 // GENERAL
		 id = in.readInt();
		 from_activity = in.readString();
		 keywords = (ArrayList<String>) in.readSerializable();
		 owner = (Owner) in.readParcelable(Owner.class.getClassLoader());
		 
		 // TAB 1
		 header = (Header) in.readParcelable(Header.class.getClassLoader());
		 // TAB 2
		 technical_setting = (TechnicalSetting) in.readParcelable(TechnicalSetting.class.getClassLoader());
		 // TAB 3
		 educational_setting = (EducationalSetting) in.readParcelable(EducationalSetting.class.getClassLoader());
		 // TAB 4
		 activity_sequence = (ActivitySequence) in.readParcelable(ActivitySequence.class.getClassLoader());
    }
	
	public void setHeader(Header header){
		this.header = header;
	}
}