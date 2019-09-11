package area.guias.pfc;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class CommentsViewFragment extends ListFragment{
	View rootView;
	Language language;
	CommentsListAdapter commentsListAdapter;
	List<Comment> commentsList;
	public CommentsStore commentsStore = new CommentsArray();
	public GetComments getComments;
	public Scroll scroll;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
			super.onCreateView(inflater, container, savedInstanceState);
			if (rootView != null) {
    	        ViewGroup parent = (ViewGroup) rootView.getParent();
    	        if (parent != null)
    	            parent.removeView(rootView);
    	    }
    	    try {
    	        rootView = inflater.inflate(R.layout.comments_view_fragment, container, false);
    	    } 
    	    catch (InflateException e) {
    	    	
    	    }		
    	    return rootView;
	}
	
	
	
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		language = new Language();
		language = getActivity().getIntent().getParcelableExtra("language");
	
		commentsListAdapter = new CommentsListAdapter(commentsStore.commentsList());
		ListView list = (ListView) getActivity().findViewById(android.R.id.list);
        list.setAdapter(commentsListAdapter); 
		
		
		
		if (savedInstanceState != null){
			
			scroll = savedInstanceState.getParcelable("scroll");
			
			for (int i=0; i< savedInstanceState.getParcelableArrayList("commentsList").size(); i++)
        		commentsStore.commentsList().add((Comment) savedInstanceState.getParcelableArrayList("commentsList").get(i));      	
        	
        	updateCommentList();
		}
		else {
			scroll = new Scroll();
			configureCommentsScroll();
			
			getComments();
		}
	}
	
	
	
	

	@Override 
    public void onListItemClick(ListView listView, View view, int position, long id) {
    	super.onListItemClick(listView, view, position, id);
    }
	
	
	
	
	@Override
	public void onSaveInstanceState(Bundle outState) {
	   Log.d("NavigationDrawer", "onSaveInstanceState");
	   super.onSaveInstanceState(outState);
	   outState.putParcelable("scroll", scroll);
	   outState.putParcelableArrayList("commentsList", commentsStore.commentsList());	   
	}
	
	
	
	
	
	public void configureCommentsScroll(){
		Log.d("CommentsViewFragment", "configureCommentsScroll");
		ListView gridView = (ListView) getActivity().findViewById(android.R.id.list);
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
			
        	@Override
        	public void onScrollStateChanged(AbsListView view, int scrollState) {
				
        	}
			
        	@Override
        	public void onScroll(AbsListView view, int firstVisibleItem,int visibleItemCount, int totalItemCount) {
        		boolean loadMore = firstVisibleItem + visibleItemCount >= scroll.getTotalCount();
        		if(loadMore){
			    	scroll.setTotalCount(scroll.getTotalCount()+scroll.getPageElements());
			        scroll.setPage(scroll.getPage()+1);
			        Log.d("CommentsViewFragment", "LOAD PAGE("+scroll.getPage()+")");			        
			        getComments();			        	         
			    }
			}
		});
	}
	
	
	
	
	
	public void getComments(){
		int id =  getActivity().getIntent().getExtras().getInt("id");
		getComments = new GetComments(scroll.getPageElements(), scroll.getPage());
		getComments.execute(id);
	}
	
	
	
	
	
	public void newCommentsAdded(){
		scroll.setPage(1);
		getComments();
	}
	
	
	
	
	
	public class GetComments extends AsyncTask<Integer, Void, Void> {
		int comments_number, page;
		
		public GetComments(int comments_number, int page){
			this.comments_number = comments_number;
			this.page = page;
		}
		
        protected Void doInBackground(Integer... id) {
        	try {  
        		if (scroll.getPage() == 1) commentsStore.deleteList();
        		String model = getActivity().getIntent().getExtras().getString("model");
            	DefaultHttpClient cliente = new DefaultHttpClient();
                String Url = new IP().ip+"/"+language.getStringLanguage()+"/comments/getComments.json?model="+model+"&object_id="+id[0];    		
                
                if (this.page > 0){
    				Url+="&page="+this.page;        		
    				if (this.comments_number > 0) Url+="&comments_number="+this.comments_number; 
    			}
    			else if (this.comments_number > 0) Url+="&comments_number="+this.comments_number; 
                                
                HttpGet request = new HttpGet(Url);
                SharedPreferences preferences = getActivity().getSharedPreferences("datos", Context.MODE_PRIVATE);
                request.setHeader("Cookie", "_AREA_v0.1_session="+preferences.getString("Cookie", "0"));
       			
                // PRUEBA
                System.out.println("Url: "+ Url);
                HttpResponse response = cliente.execute(request); 
       			String json = EntityUtils.toString(response.getEntity());
       			System.out.println("respuesta: "+json);
       			// PRUEBA
       			
       			response = cliente.execute(request);
       			commentsList = new Funciones().readJsonCommentStream(response.getEntity());
       			
       			// Ordered List
       			List<Comment> orderedList = new ArrayList<Comment>();
       			for (int i=commentsList.size()-1; i>-1; i--){
       				orderedList.add(commentsList.get(i));
       			}
       			
       			// LISTA DE GUIAs
				for (int i=0;i<orderedList.size();i++){
					commentsStore.saveComment(orderedList.get(i));
				}	
				
       			updateCommentList();
            } catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}				
               
			return null; 			
        }
    }
	
	
	

	
	private void updateCommentList(){
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					commentsListAdapter.notifyDataSetChanged(); 
				}
			});
	}
	
	
	
	
	
	public class CommentsListAdapter extends BaseAdapter {
	    private final ArrayList<Comment> commentList;
	       
	    public CommentsListAdapter(ArrayList<Comment> comments) {
	        super();
	        this.commentList = comments;
	    }
	    	    
		@Override
		public int getCount() {
			return commentList.size();
		}
	
		@Override
		public Object getItem(int position) {
			return commentList.get(position);
		}
		
		@Override
		public long getItemId(int position) {
			return position;
		}
		
		class CommentHolder{
 	        TextView user, comment, date;
 	    }
				
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
	         CommentHolder myHolder;
	         CommentItemView view = (CommentItemView) convertView;
	         
	         if (view == null){
	        	 myHolder = new CommentHolder();
	        	 view = new CommentItemView(getActivity());
	        	
	        	 myHolder.user = (TextView) view.findViewById(R.id.user);
	        	 myHolder.comment = (TextView) view.findViewById(R.id.comment);
	        	 myHolder.date = (TextView) view.findViewById(R.id.date);
	         
	        	 view.setTag(myHolder);
	        	 view.createViews();
	        	   
	         }
	         else myHolder = (CommentHolder) view.getTag();
	             
        	 if (commentList.get(position).getUser() != "") view.setUser(commentList.get(position).getUser());
        	 else view.setUser("User");
        	 if (commentList.get(position).getComment() != "") view.setComment(commentList.get(position).getComment());  
        	 else view.setComment("Commentary");         
        	 if (commentList.get(position).getYear() != ""){
        		 String date = "Escrito el " + commentList.get(position).getDay()+"-"+commentList.get(position).getMonth()+"-"
        				 +commentList.get(position).getYear()+" a las "+commentList.get(position).getHour() + " horas";
        		 view.setDate(date);  
        	 }
        	 else view.setDate("Date");  
        	    	 
	         return view;
		}
	}





	
}
