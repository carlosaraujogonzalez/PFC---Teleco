package area.guias.pfc;

import java.io.IOException;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ContentBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class CommentInsertFragment extends Fragment{
	View rootView;
	Language language;
	
	private CommentInsertFragmentListener mCallback = null;
    public interface CommentInsertFragmentListener{
        public void onFragmentIteration();
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			if (rootView != null) {
    	        ViewGroup parent = (ViewGroup) rootView.getParent();
    	        if (parent != null)
    	            parent.removeView(rootView);
    	    }
    	    try {
    	        rootView = inflater.inflate(R.layout.comment_insert_fragment, container, false);
    	    } catch (InflateException e) {
    	    	
    	    }		
    	    return rootView;
	}
	
	
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		language = new Language();
	}
	
	
	
	
	
	//El fragment se ha adjuntado al Activity
	@Override
	public void onAttach(android.app.Activity activity) {
	    super.onAttach(activity);
	    try{
	        mCallback = (CommentInsertFragmentListener) activity;
	    }catch(ClassCastException e){
	        Log.e("ExampleFragment", "El Activity debe implementar la interfaz FragmentIterationListener");
	    }
	}
	
	
	
	
	
	@Override
	public void onDetach() {
	    mCallback = null;
	    super.onDetach();
	}
	
	public void insert_comment(int id, String comment){
		new SendComment(id, comment).start();
	}
	
	
	
	
	
	public class SendComment extends Thread {
		int id;
		String comment;
		
		public SendComment(int id, String comment){
			this.id = id;
			this.comment = comment;
		}
		
        @Override 
        public void run() {       	
            try {           	
            	String model = getActivity().getIntent().getExtras().getString("model");
            	DefaultHttpClient cliente = new DefaultHttpClient();
                String Url = new IP().ip+"/"+language.getStringLanguage()+"/comments/create.json?model="+model+"&object_id="+this.id;
                HttpPost request = new HttpPost(Url);
                
                SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
                request.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
                
                MultipartEntity mpEntity = new MultipartEntity();
                ContentBody stringBody = new StringBody(this.comment);				
				mpEntity.addPart("comment", stringBody);
				request.setEntity(mpEntity);
       			
				cliente.execute(request);  
				
				// Comunica con CommentsView
       			mCallback.onFragmentIteration();
       			
				System.out.println("CreateComment: "+ Url);
            } catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}				
         }        
    }
}
