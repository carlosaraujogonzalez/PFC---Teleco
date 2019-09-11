package area.guias.pfc;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.MediaStore.Images;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.stream.JsonReader;

@SuppressLint("DefaultLocale")
public class Funciones {
	private final static int ENGLISH = 1, SPANISH = 2, GALICIAN = 3;
	
	public String adapt_search_string(String search_string){
		String aux = "";
		String delimitadores= "[ ]";
		String[] palabrasSeparadas = search_string.split(delimitadores);
		for (int i=0; i<palabrasSeparadas.length; i++){
			if (i == palabrasSeparadas.length - 1) aux += palabrasSeparadas[i];
			else aux += palabrasSeparadas[i] + "%20";
		}
		return aux;
	}
	
	
	
	
	
	public boolean isTablet(Context context) {
	    return (context.getResources().getConfiguration().screenLayout
	            & Configuration.SCREENLAYOUT_SIZE_MASK)
	            >= Configuration.SCREENLAYOUT_SIZE_LARGE;
	}
	
	
	
	
	
	public String makeUrlIds(List<Integer> listInteger){
		String url="id=";
		for (int i=0; i<listInteger.size(); i++){
			if (i == listInteger.size()-1) url += listInteger.get(i);
			else url += listInteger.get(i)+",";
		}
		return url;
	}
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////			  	     Obtiene un Id a partir de Json                         /////////////
//////////////																		    /////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public Integer readJsonId(HttpEntity in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
		try {
			return readJsonIdObject(reader);
		} 
		finally {
			reader.close();
		}
	}
	
	
	
	
	public int readJsonIdObject(JsonReader reader) throws IOException {
		int id = -1;
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				id = reader.nextInt();
			} else {
				reader.skipValue();
			}			
		}
		reader.endObject();
		return id;
	}
	
	
	
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////		      Obtiene una lista de Ids a partir de Json                     /////////////
//////////////																			/////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public List<Integer> readJsonIdsStream(HttpEntity in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
		try {
			return readIdsArray(reader);
		} 
		finally {
			reader.close();
		}
	}
	
	
	
	
	
	public List<Integer> readIdsArray(JsonReader reader) throws IOException {
		List<Integer> ids = new ArrayList<Integer>();
		reader.beginArray();
		while (reader.hasNext()) {
			ids.add(readId(reader));
		}
		reader.endArray();
		return ids;
	}
	
	
	
	
	
	public int readId(JsonReader reader) throws IOException {
		int id = -1;
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				id = reader.nextInt();
			} else {
				reader.skipValue();
			}			
		}
		reader.endObject();
		return id;
	}
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////				     Obtiene las guías a partir de Json                     /////////////
//////////////																			/////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	
	
	public List<Guide> readJsonGuidesStream(HttpEntity in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
		try {
			return readGuiasArray(reader);
		} 
		finally {
			reader.close();
		}
	}





	public List<Guide> readGuiasArray(JsonReader reader) throws IOException {
		List<Guide> guias = new ArrayList<Guide>();
		reader.beginArray();
		while (reader.hasNext()) {
			guias.add(readGuia(reader));
		}
		reader.endArray();
		return guias;
	}





	public Guide readGuia(JsonReader reader) throws IOException {
		Guide guide = new Guide();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				guide.id = reader.nextInt();
			} 
			else if (name.equals("name")){
				guide.header.title = reader.nextString();
			} else if (name.equals("image")){
				guide.header.imageUrl = reader.nextString();
			} else if (name.equals("description")){
				guide.header.description = reader.nextString();				
				guide.header.setShortDescription(guide.header.description);
			} else if (name.equals("components")){
				reader.beginObject();
				while(reader.hasNext()){
					name = reader.nextName();
					if (name.equals("technical_setting_id")){
						guide.technical_setting.id = reader.nextInt();
					} else if (name.equals("educational_setting_id")){
						guide.educational_setting.id = reader.nextInt();
					} else if (name.equals("activity_sequence_id")){
						guide.activity_sequence.id = reader.nextInt();
					} else {
						reader.skipValue();
					}
				}
				reader.endObject();
			} else {
				reader.skipValue();
			}			
		}
		reader.endObject();
		return guide;
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																		    /////////////
//////////////				Obtiene el Technical Setting a partir de Json               /////////////
//////////////																		    /////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public List<TechnicalSetting> readJsonTSStream(HttpEntity in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
		try {
			return readTSArray(reader);
		} 
		finally {
			reader.close();
		}
	}
    
    
    
    
    
    public List<TechnicalSetting> readTSArray(JsonReader reader) throws IOException {
		List<TechnicalSetting> tsettings = new ArrayList<TechnicalSetting>();
		reader.beginArray();
		while (reader.hasNext()) tsettings.add(readTSetting(reader));		
		reader.endArray();
		return tsettings;
	}

    
    
    
    
	public TechnicalSetting readTSetting(JsonReader reader) throws IOException {
		TechnicalSetting tsetting = new TechnicalSetting();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) tsetting.id = reader.nextInt();			
			else if (name.equals("name")) tsetting.setName(reader.nextString());			
			else if (name.equals("image")) tsetting.imageUrl = reader.nextString();
			else if (name.equals("description")){
				tsetting.setDescription(reader.nextString());				
				tsetting.setShortDescription(tsetting.getDescription());
			}
			else if (name.equals("keywords")){
				reader.beginArray();
				reader.endArray();
			}
			else if (name.equals("devices_id")){
				reader.beginArray();	
				while (reader.hasNext()){
					reader.beginObject();
					String name2 = reader.nextName();					
					if (name2.equals("id")) tsetting.devices_id.add(reader.nextInt());					
					reader.endObject();
				}				
				reader.endArray();
			} else if (name.equals("applications_id")){
				reader.beginArray();
				while (reader.hasNext()){
					reader.beginObject();
					String name3 = reader.nextName();
					if (name3.equals("id")) tsetting.applications_id.add(reader.nextInt());					
					reader.endObject();
				}
				reader.endArray();
			} else reader.skipValue();			
		}
		reader.endObject();
		return tsetting;
	}
	
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////	     Obtiene las WholeView de las aplicaciones a partir de Json         /////////////
//////////////																			/////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public List<Application> readJsonAppStream(HttpEntity in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
		try {
			return readAppArray(reader);
		} 
		finally {
			reader.close();
		}
	}
	
	
	
	
	
	public List<Application> readAppArray(JsonReader reader) throws IOException {
		List<Application> dispositivos = new ArrayList<Application>();
		reader.beginArray();
		while (reader.hasNext()) dispositivos.add(readApp(reader));		
		reader.endArray();
		return dispositivos;
	}

	
	
	
	
	public Application readApp(JsonReader reader) throws IOException {
		Application aplicacion = new Application();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) aplicacion.id = reader.nextInt();			
			else if (name.equals("name")) aplicacion.name = reader.nextString();
			else if (name.equals("description")){
				aplicacion.description = reader.nextString();
				aplicacion.setShortDescription(aplicacion.description);
			}
			else if (name.equals("url")) aplicacion.url = reader.nextString();
			else if (name.equals("image")) aplicacion.imageUrl = reader.nextString();
			else reader.skipValue();
		}
		reader.endObject();
		return aplicacion;
	}
	
	
	
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////		Obtiene las WholeView de los dispositivos a partir de Json          /////////////
//////////////																			/////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	
	
	
	
	public List<Device> readJsonDispStream(HttpEntity in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
		try {
			return readDispositivosArray(reader);
		} 
		finally {
			reader.close();
		}
	}
	
	
	
	
	
	public List<Device> readDispositivosArray(JsonReader reader) throws IOException {
		List<Device> dispositivos = new ArrayList<Device>();
		reader.beginArray();
		while (reader.hasNext()) {
			dispositivos.add(readDispositivo(reader));
		}
		reader.endArray();
		return dispositivos;
	}

	
	
	
	
	public Device readDispositivo(JsonReader reader) throws IOException {
		Device dispositivo = new Device();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				dispositivo.id = reader.nextInt();
			} else if (name.equals("name")){
				dispositivo.name = reader.nextString();
			} else if (name.equals("description")){
				dispositivo.setDescription(reader.nextString());
			}else if (name.equals("image")){
				dispositivo.UrlImage = reader.nextString();
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return dispositivo;
	}
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////		  Obtiene las WholeView de los contenidos a partir de Json          /////////////
//////////////																			/////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public List<Content> readJsonContentStream(HttpEntity in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
		try {
			return readContentsArray(reader);
		} 
		finally {
			reader.close();
		}
	}
	
	
	
	
	
	public List<Content> readContentsArray(JsonReader reader) throws IOException {
		List<Content> contents = new ArrayList<Content>();
		reader.beginArray();
		while (reader.hasNext()) {
			contents.add(readContent(reader));
		}
		reader.endArray();
		return contents;
	}

	
	
	
	
	public Content readContent(JsonReader reader) throws IOException {
		Content content = new Content();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				content.setId(reader.nextInt());
			} else if (name.equals("name")){
				content.setName(reader.nextString());
			} else if (name.equals("url")){
				content.setUrl(reader.nextString());
			} else if (name.equals("description")){
				String description = reader.nextString();
				content.setDescription(description);
			}else if (name.equals("image")){
				content.setImageUrl(reader.nextString());
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return content;
	}
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////		Obtiene las WholeView de los colaboradores a partir de Json         /////////////
//////////////																			/////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public List<Person> readJsonPeopleStream(HttpEntity in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
		try {
			return readPeopleArray(reader);
		} 
		finally {
			reader.close();
		}
	}
	
	
	
	
	
	public List<Person> readPeopleArray(JsonReader reader) throws IOException {
		List<Person> people = new ArrayList<Person>();
		reader.beginArray();
		while (reader.hasNext()) {
			people.add(readPeople(reader));
		}
		reader.endArray();
		return people;
	}

	
	
	
	
	public Person readPeople(JsonReader reader) throws IOException {
		Person people = new Person();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				people.setId(reader.nextInt());
			} else if (name.equals("name")){
				people.setName(reader.nextString());
			} else if (name.equals("url")){
				people.setUrl(reader.nextString());
			} else if (name.equals("description")){
				String description = reader.nextString();
				people.setDescription(description);
			}else if (name.equals("image")){
				people.setImageUrl(reader.nextString());
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return people;
	}
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////		 	Obtiene las WholeView de las salidas a partir de Json          	/////////////
//////////////																			/////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public List<Trip> readJsonTripStream(HttpEntity in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
		try {
			return readTripsArray(reader);
		} 
		finally {
			reader.close();
		}
	}
	
	
	
	
	
	public List<Trip> readTripsArray(JsonReader reader) throws IOException {
		List<Trip> trips = new ArrayList<Trip>();
		reader.beginArray();
		while (reader.hasNext()) {
			trips.add(readTrip(reader));
		}
		reader.endArray();
		return trips;
	}

	
	
	
	
	public Trip readTrip(JsonReader reader) throws IOException {
		Trip content = new Trip();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				content.setId(reader.nextInt());
			} else if (name.equals("name")){
				content.setName(reader.nextString());
			} else if (name.equals("url")){
				content.setUrl(reader.nextString());
			} else if (name.equals("description")){
				String description = reader.nextString();
				content.setDescription(description);
			}else if (name.equals("image")){
				content.setImageUri(reader.nextString());
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return content;
	}
	
	
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////		Obtiene las WholeView de las herramientas a partir de Json          /////////////
//////////////																			/////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	public List<Tool> readJsonToolStream(HttpEntity in, String type) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
		try {
			return readToolsArray(reader, type);
		} 
		finally {
			reader.close();
		}
	}
	
	
	
	
	
	public List<Tool> readToolsArray(JsonReader reader, String type) throws IOException {
		List<Tool> tools = new ArrayList<Tool>();
		reader.beginArray();
		while (reader.hasNext()) {
			tools.add(readTool(reader, type));
		}
		reader.endArray();
		return tools;
	}

	
	
	
	
	public Tool readTool(JsonReader reader, String type) throws IOException {
		Tool tool = new Tool();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				tool.setId(reader.nextInt());
			} else if (name.equals("name")){
				tool.setName(reader.nextString());
			} else if (name.equals("description")){
				String description = reader.nextString();
				tool.setDescription(description);
			}else if (name.equals("image")){
				tool.setImageUrl(reader.nextString());
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		tool.setType(type);
		return tool;
	}
	
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////		Obtiene las WholeView de los eventos a partir de Json               /////////////
//////////////																			/////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	public List<Trip> readJsonEventsStream(HttpEntity in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
		try {
			return readEventsArray(reader);
		} 
		finally {
			reader.close();
		}
	}
	
	
	
	
	
	public List<Trip> readEventsArray(JsonReader reader) throws IOException {
		List<Trip> trips = new ArrayList<Trip>();
		reader.beginArray();
		while (reader.hasNext()) {
			trips.add(readEvent(reader));
		}
		reader.endArray();
		return trips;
	}

	
	
	
	
	public Trip readEvent(JsonReader reader) throws IOException {
		Trip trip = new Trip();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) {
				trip.setId(reader.nextInt());
			} else if (name.equals("name")){
				trip.setName(reader.nextString());
			} else if (name.equals("description")){
				String description = reader.nextString();
				trip.setDescription(description);
			}else if (name.equals("image")){
				trip.setImageUrl(reader.nextString());
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return trip;
	}
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////				Obtiene el Educational Setting a partir de Json             /////////////
//////////////																			/////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	
	
	
	
	
	
	public List<EducationalSetting> readJsonESStream(HttpEntity in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
		try {
			return readESArray(reader);
		} 
		finally {
			reader.close();
		}
	}
    
    
    
    
    
    public List<EducationalSetting> readESArray(JsonReader reader) throws IOException {
		List<EducationalSetting> esettings = new ArrayList<EducationalSetting>();
		reader.beginArray();
		while (reader.hasNext()) {
			esettings.add(readESetting(reader));
		}
		reader.endArray();
		return esettings;
	}

    
    
    
    
	public EducationalSetting readESetting(JsonReader reader) throws IOException {
		String aux;
		EducationalSetting esetting = new EducationalSetting();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) esetting.id = reader.nextInt();
			else if (name.equals("name")) esetting.title = reader.nextString();
			else if (name.equals("image")) esetting.image_Url = reader.nextString();
			else if (name.equals("description")) esetting.setDescription(reader.nextString());
			else if (name.equals("age_range")) esetting.age_range = reader.nextString();
			else if (name.equals("start_date")) esetting.start_date = reader.nextString();
			else if (name.equals("end_date")) esetting.end_date = reader.nextString();
			else if (name.equals("keywords")) esetting.keywords = reader.nextString();
			else if (name.equals("address")) esetting.address = reader.nextString();
			else if (name.equals("latitude")){
				if ((aux=reader.nextString()).equals("")){}
				else esetting.coordinates.latitude = Double.parseDouble(aux);
			}else if (name.equals("longitude")){
				if ((aux=reader.nextString()).equals("")){}
				else esetting.coordinates.longitude = Double.parseDouble(aux);
			}else if (name.equals("private")){
				esetting.privado = reader.nextBoolean();
			}else if (name.equals("vocabularies")){
				reader.beginArray();
				ArrayList<VocabularyES> vocabularies = new ArrayList<VocabularyES>();
				while (reader.hasNext()){
					reader.beginObject();
					VocabularyES vocabulary = new VocabularyES();
					while(reader.hasNext()){
						name = reader.nextName();
						if (name.equals("id")) vocabulary.id = reader.nextInt();
						else if (name.equals("type")) vocabulary.type = reader.nextString();
						else if (name.equals("term")) vocabulary.term = reader.nextString();
					}
					reader.endObject();
					vocabularies.add(vocabulary);
				}
				reader.endArray();
				esetting.vocabularies = vocabularies;
			} else {
				reader.skipValue();
			}
		}
		reader.endObject();
		return esetting;
	}
	
	

	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////		Obtiene las WholeView de los activity sequence a partir de Json     /////////////
//////////////																			/////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	
	
	
	public List<ActivitySequence> readJsonASStream(HttpEntity in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
		try {
			return readASArray(reader);
		} 
		finally {
			reader.close();
		}
	}
    
    
    
    
    
    public List<ActivitySequence> readASArray(JsonReader reader) throws IOException {
		List<ActivitySequence> asequences = new ArrayList<ActivitySequence>();
		reader.beginArray();
		while (reader.hasNext()) asequences.add(readASequence(reader));		
		reader.endArray();
		return asequences;
	}

    
    
    
    
	public ActivitySequence readASequence(JsonReader reader) throws IOException {
		ActivitySequence asequence = new ActivitySequence();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) asequence.id = reader.nextInt();			
			else if (name.equals("name")) asequence.title = reader.nextString();
			else if (name.equals("description")) asequence.description = reader.nextString();
			else if (name.equals("image")) asequence.image_Url = reader.nextString();
			else if (name.equals("activities")){
				reader.beginArray();	
				while (reader.hasNext()){
					reader.beginObject();
						if (reader.nextName().equals("id"))
							asequence.activitiesIds.add(reader.nextInt());
					reader.endObject();
				}				
				reader.endArray();
			} else reader.skipValue();			
		}
		reader.endObject();
		return asequence;
	}
	
	
	
	
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////		Obtiene las MiniView de las activities a partir de Json		        /////////////
//////////////																			/////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
	
	
	public List<ActivityMini> readJsonMiniActivityStream(HttpEntity in, String activitiesType) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
        try {
            return readMiniActivityList(reader, activitiesType);
        }
        finally {
            reader.close();
        }
    }

    public List<ActivityMini> readMiniActivityList(JsonReader reader, String activitiesType) throws IOException {
        List<ActivityMini> activities = new ArrayList<ActivityMini>();
        reader.beginArray();
        while (reader.hasNext()) {
            activities.add(readMiniActivity(reader, activitiesType));
        }
        reader.endArray();
        return activities;
    }

    public ActivityMini readMiniActivity(JsonReader reader, String activitiesType) throws IOException {
        ActivityMini activity = new ActivityMini();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                activity.setId(reader.nextInt());
            } else if (name.equals("name")){
                activity.setName(reader.nextString());
            } else if (name.equals("image")){
                activity.setElement_image_file_name(reader.nextString());
            }else if (name.equals("description")){
            	activity.setDescription(reader.nextString());
            	activity.setShortDescription(activity.getDescription());
            	
            } else {
                reader.skipValue();
            }
        }
        if (activitiesType.equals("user_activities")) activity.setCheckedString("Eliminar");
        else activity.setCheckedString("Añadir");
        reader.endObject();
        return activity;
    }
    
    
    
    
    
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////		Obtiene las WholeView de las activities a partir de Json		    /////////////
//////////////																			/////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public List<Activity> readJsonActivityStream(HttpEntity in) throws IOException {
        JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
        try {
            return readActivityList(reader);
        }
        finally {
            reader.close();
        }
    }

    public List<Activity> readActivityList(JsonReader reader) throws IOException {
        List<Activity> activities = new ArrayList<Activity>();
        reader.beginArray();
        while (reader.hasNext()) {
            activities.add(readActivity(reader));
        }
        reader.endArray();
        return activities;
    }

    public Activity readActivity(JsonReader reader) throws IOException {
        Activity activity = new Activity();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            if (name.equals("id")) {
                activity.setId(reader.nextInt());
            } else if (name.equals("name")){
                activity.setName(reader.nextString());
            } else if (name.equals("image")){
                activity.setImageUrl(reader.nextString());
            } else if (name.equals("description")){
            	activity.setDescription(reader.nextString());
            } else if (name.equals("keywords")){
            	reader.beginArray();
            	while (reader.hasNext()) activity.setKeywords(reader.nextString());
            	reader.endArray();
            } else if (name.equals("start")){     
            	activity.setStart(reader.nextString());
            } else if (name.equals("end")){
            	activity.setEnd(reader.nextString());
            } else if (name.equals("progress")){
            	activity.setProgress(reader.nextString());
            } else if (name.equals("boxes")){
            	reader.beginArray();
            	int num_boxes = 0;
            	while (reader.hasNext()){
            		Box box = new Box();
            		activity.setBox(box);
            		num_boxes++;
            		reader.beginObject();
            		while (reader.hasNext()){            			
            			name = reader.nextName();
            			if (name.equals("type")){
            				String type = reader.nextString();
            				activity.boxes.get(num_boxes-1).setType(type);
            			} else if (name.equals("position")){
            				activity.boxes.get(num_boxes-1).setPosition(reader.nextInt());
            			} else if (name.equals("components")){
            				reader.beginArray();
            				int num_components = 0;
            				while (reader.hasNext()){
            					Component component = new Component();
            					activity.boxes.get(num_boxes-1).setComponents(component);
            					num_components++;
            					reader.beginObject();
            					while (reader.hasNext()){
            						name = reader.nextName();
            						if (name.equals("id")){ 
            							int id = reader.nextInt();
            							System.out.println("id=" + id);
            							activity.boxes.get(num_boxes-1).components.get(num_components-1).setId(id);
            						} else if (name.equals("type"))
            							activity.boxes.get(num_boxes-1).components.get(num_components-1).setType(reader.nextInt());
            						else if (name.equals("position"))
            							activity.boxes.get(num_boxes-1).components.get(num_components-1).setPosition(reader.nextInt());
            						else if (name.equals("texts")){
            							reader.beginArray();
            							int num_texts = 0;
            							while (reader.hasNext()){
            								Text text = new Text();
            								activity.boxes.get(num_boxes-1).components.get(num_components-1).setTexts(text);
            								num_texts++;
            								reader.beginObject();
            								while(reader.hasNext()){
            									name = reader.nextName();
            									if (name.equals("id"))
            										activity.boxes.get(num_boxes-1).components.get(num_components-1).texts.get(num_texts-1).setId(reader.nextInt());
            									else if (name.equals("position"))
            										activity.boxes.get(num_boxes-1).components.get(num_components-1).texts.get(num_texts-1).setPosition(reader.nextInt());
            									else if (name.equals("content"))
            										activity.boxes.get(num_boxes-1).components.get(num_components-1).texts.get(num_texts-1).setContent(reader.nextString());		
            								}
            								reader.endObject();
            							}
            							reader.endArray();
            						}
            							
            					}
            					reader.endObject();			
            				}
            				reader.endArray();
            			}
            			
            		}
            		reader.endObject();
            	}
            	reader.endArray();
            }
            else if (name.equals("requirements")){
				reader.beginObject();
				
				Requirement requirement = new Requirement();
				activity.setRequirement(requirement);
				while (reader.hasNext()){
					
					int num_tools=0;
					name = reader.nextName();
					if (name.equals("tools")){
						reader.beginObject();
						Tool tool = new Tool();
						activity.requirements.get(0).setTool(tool);						
						num_tools++;
						while (reader.hasNext()){
							name = reader.nextName();
							if (name.equals("devices")){
    							reader.beginArray();
    							while (reader.hasNext()){
    								reader.beginObject();
    								while (reader.hasNext()){
    									name = reader.nextName();
    									if (name.equals("id")){
    										int id = reader.nextInt();
    										activity.getRequirements().get(0).getTools().get(num_tools-1).setDeviceId(id);
    									}
    								}
    								reader.endObject();
    							}
    							reader.endArray();
    						}
    						if (name.equals("applications")){
    							reader.beginArray();
    							while (reader.hasNext()){
    								reader.beginObject();
    								while (reader.hasNext()){
    									name = reader.nextName();
    									if (name.equals("id")){
    										int id = reader.nextInt();
    										activity.getRequirements().get(0).getTools().get(num_tools-1).setApplicationId(id);
    									}
    								}
    								reader.endObject();
    							}
    							reader.endArray();
    						}
						}            						
						reader.endObject();
					}
					else if (name.equals("contents")){
						reader.beginArray();
						int num_contents = 0;
						while (reader.hasNext()){
							Content content = new Content();
							Log.d("Funciones", "new Content()");
							activity.getRequirements().get(0).setContent(content);
							num_contents++;
							reader.beginObject();
							while (reader.hasNext()){
								name = reader.nextName();
								if (name.equals("id")){
									int id = reader.nextInt();
									activity.getRequirements().get(0).getContents().get(num_contents-1).setId(id);
									Log.d("Funciones", "id: " + id);
								}
							}
							reader.endObject();
						}
						reader.endArray();
					}
					else if (name.equals("events")){
						reader.beginArray();
						int num_events = 0;
						while (reader.hasNext()){
							Trip event = new Trip();
							activity.getRequirements().get(0).setEvent(event);
							num_events++;
							reader.beginObject();
							while (reader.hasNext()){
								name = reader.nextName();
								if (name.equals("id")){
									int id = reader.nextInt();
									activity.getRequirements().get(0).getEvents().get(num_events-1).setId(id);
								}
							}
							reader.endObject();
						}
						reader.endArray();
					}
					else if (name.equals("colaborators")){
						reader.beginArray();
						int num_colaborators = 0;
						while (reader.hasNext()){
							Person colaborator = new Person();
							activity.getRequirements().get(0).setColaborator(colaborator);
							num_colaborators++;
							reader.beginObject();
							while (reader.hasNext()){
								name = reader.nextName();
								if (name.equals("id")){
									int id = reader.nextInt();
									activity.getRequirements().get(0).getColaborators().get(num_colaborators-1).setId(id);
								}
							}
							reader.endObject();
						}
						reader.endArray();
					}
				}
				reader.endObject();
			}
            else {
                reader.skipValue();
            }
        }
        reader.endObject();
        return activity;
    }
    
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////		Obtiene las WholeView de los comentarios a partir de Json		    /////////////
//////////////																			/////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
    
	
        
    public List<Comment> readJsonCommentStream(HttpEntity in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
		try {
			return readCommentArray(reader);
		} 
		finally {
			reader.close();
		}
	}
    
    
    
    
    
    public List<Comment> readCommentArray(JsonReader reader) throws IOException {
		List<Comment> comments = new ArrayList<Comment>();
		reader.beginArray();
		while (reader.hasNext()) comments.add(readComment(reader));		
		reader.endArray();
		return comments;
	}

    
    
    
    
	public Comment readComment(JsonReader reader) throws IOException {
		Comment comment = new Comment();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("user")) comment.setUser(reader.nextString());			
			else if (name.equals("comment")) comment.setComment(reader.nextString());
			else if (name.equals("date")) {
				CommentDate time = new CommentDate();
				time = adapt_comment_date(reader.nextString());
				comment.setDay(time.getDay());
				comment.setHour(time.getHoursMinSec());
				comment.setMonth(time.getMonth());
				comment.setYear(time.getYear());
			}
			else reader.skipValue();			
		}
		reader.endObject();
		return comment;
	}
	
	
	public CommentDate adapt_comment_date(String search_string){
		CommentDate time = new CommentDate();
		String delimitadores= "[T\\-\\+]";
		String[] palabrasSeparadas = search_string.split(delimitadores);
		time.setYear(palabrasSeparadas[0]);
		time.setMonth(palabrasSeparadas[1]);
		time.setDay(palabrasSeparadas[2]);
		time.setHoursMinSec(palabrasSeparadas[3]);
		return time;
	}
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////		   Obtiene las subjects de Vocabularies a partir de Json		    /////////////
//////////////																			/////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////	
	
	
	public ArrayList<Subject> readSubjectsStream(HttpEntity in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
		try {
			return readSubjectArray(reader);
		} 
		finally {
			reader.close();
		}
	}
    

	
	
	
    public ArrayList<Subject> readSubjectArray(JsonReader reader) throws IOException {
		ArrayList<Subject> subjects = new ArrayList<Subject>();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("subjects")) {
				reader.beginArray();
				while (reader.hasNext()){
					subjects.add(readSubject(reader));	
				}
				reader.endArray();
			}
			else reader.skipValue();
		}
		reader.endObject();
		return subjects;
	}

   
    
    
    public Subject readSubject(JsonReader reader) throws IOException {
		Subject subject = new Subject();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) subject.setId(reader.nextInt());			
			else if (name.equals("term")) subject.setTerm(reader.nextString());
			else reader.skipValue();			
		}
		reader.endObject();
		return subject;
	}
    
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////	   Obtiene los Education_levels de Vocabularies a partir de Json	    /////////////
//////////////																			/////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////	
    
    public ArrayList<EducationLevel> readEducationLevelsStream(HttpEntity in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
		try {
			return readEducationLevelArray(reader);
		} 
		finally {
			reader.close();
		}
	}
    

	
	
	
    public ArrayList<EducationLevel> readEducationLevelArray(JsonReader reader) throws IOException {
		ArrayList<EducationLevel> education_levels = new ArrayList<EducationLevel>();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("education_levels")) {
				reader.beginArray();
				while (reader.hasNext()){
					education_levels.add(readEducationLevel(reader));	
				}
				reader.endArray();
			}
			else reader.skipValue();
		}
		reader.endObject();
		return education_levels;
	}

   
    
    
    public EducationLevel readEducationLevel(JsonReader reader) throws IOException {
    	EducationLevel education_level = new EducationLevel();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) education_level.setId(reader.nextInt());			
			else if (name.equals("term")) education_level.setTerm(reader.nextString());
			else reader.skipValue();			
		}
		reader.endObject();
		return education_level;
	}
    

/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////																			/////////////
//////////////				Obtiene los languages de Vocabularies a partir de Json	    /////////////
//////////////																			/////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////
/////////////////////////////////////////////////////////////////////////////////////////////////////	
    
    public ArrayList<VocabularyLanguage> readLanguagesStream(HttpEntity in) throws IOException {
		JsonReader reader = new JsonReader(new InputStreamReader(in.getContent(), "UTF-8"));
		try {
			return readLanguageArray(reader);
		} 
		finally {
			reader.close();
		}
	}
    

	
	
	
    public ArrayList<VocabularyLanguage> readLanguageArray(JsonReader reader) throws IOException {
		ArrayList<VocabularyLanguage> languages = new ArrayList<VocabularyLanguage>();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("languages")) {
				reader.beginArray();
				while (reader.hasNext()){
					languages.add(readVocabularyLanguage(reader));	
				}
				reader.endArray();
			}
			else reader.skipValue();
		}
		reader.endObject();
		return languages;
	}

   
    
    
    public VocabularyLanguage readVocabularyLanguage(JsonReader reader) throws IOException {
    	VocabularyLanguage language = new VocabularyLanguage();
		reader.beginObject();
		while (reader.hasNext()) {
			String name = reader.nextName();
			if (name.equals("id")) language.setId(reader.nextInt());			
			else if (name.equals("term")) language.setTerm(reader.nextString());
			else reader.skipValue();			
		}
		reader.endObject();
		return language;
	}
    
    
    
    
//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////////////////////
    
    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
    
    public String getImageUriString(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return path;
    }
    
    public String getRealPathFromBitmap(Context inContext, Bitmap inImage) {
        return getRealPathFromURI(inContext, getImageUri(inContext, inImage));
    }
    
    public String getRealPathFromURI(Context context, Uri uri) {
    	final String[] imageColumns = {MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, imageColumns, null, null, null); 
        cursor.moveToFirst(); 
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA); 
        return cursor.getString(idx); 
    }
	
	
	
	
	public Dialog create_ok_dialog(Context context, String title, String message){
		final Dialog dialog = new Dialog(context);  
		dialog.setContentView(R.layout.dialog_one_button_layout);
		dialog.setCancelable(true);
		
		dialog.setTitle(title);
		
		TextView msg = (TextView) dialog.findViewById(R.id.message);
		msg.setText(message);
		
		Button buttonYes = (Button) dialog.findViewById(R.id.ok);
		buttonYes.setOnClickListener(new OnClickListener() {
	        @Override
	        public void onClick(View v) {
	        	dialog.dismiss();
	        }
	    });
		
		return dialog;
	}
	
	public ProgressDialog createProgressDialog(Context context, String text){
    	ProgressDialog progressDialog;
    	progressDialog = new ProgressDialog(context);   
    	progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    	progressDialog.setCancelable(false);
    	progressDialog.setOnCancelListener(new OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
            	
            }
        });   	
    	progressDialog.show();
    	progressDialog.setContentView(R.layout.progress_dialog);
    	TextView tv = (TextView) progressDialog.findViewById(R.id.textView);
    	tv.setText(text);
    	return progressDialog;
    }
	
	public void changeLanguage(String language, Context context){
    	Resources res = context.getResources();
        // Change locale settings in the app.
        DisplayMetrics dm = res.getDisplayMetrics();
        android.content.res.Configuration conf = res.getConfiguration();
        conf.locale = new Locale(language.toLowerCase());
        res.updateConfiguration(conf, dm);
    }
	
	public String getLanguage(int language){
		if (language == SPANISH) return "es";
		else if (language == ENGLISH) return "en";
		else if (language == GALICIAN) return "gl";
		else return "gl";
	}
	
	public Uri string2Uri(String uriString){
		return Uri.parse(uriString);
	}
	
	
	
	
	
	
	
	//////////////////////////////////////////////////
	////////////////////////////////////////////////////
	//////////////////////////////////////////////////
	
	public FilePath createImageFile() throws IOException {
		Log.d("funciones", "createImageFile");
		FilePath fp = new FilePath();
		String mCurrentPhotoPath;
	    // Create an image file name
	    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
	    String imageFileName = "JPEG_" + timeStamp + "_";
	    File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
	    File image = File.createTempFile(imageFileName, ".jpg", storageDir);

	    // Save a file: path for use with ACTION_VIEW intents
	    mCurrentPhotoPath = "file:" + image.getAbsolutePath();
	    fp.setFile(mCurrentPhotoPath);
	    fp.setPath(mCurrentPhotoPath);
	    return fp;
	}
	
	
	
	
	
	public FilePath galleryAddPic(FilePath fp, Context context) {
		Log.d("funciones", "galleryAddPic");
	    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	    File f = new File(fp.getPath());
	    Uri contentUri = Uri.fromFile(f);	
	    fp.setUri(contentUri);
	    mediaScanIntent.setData(contentUri);
	    context.sendBroadcast(mediaScanIntent);
	    return fp;
	}
	
	
	
	
	
	public void showToast(Context context, CharSequence text){
    	int duration = Toast.LENGTH_SHORT;
    	Toast toast = Toast.makeText(context, text, duration);
    	toast.show();
    }
	
}
