package area.guias.pfc;

import android.content.Context;
import android.widget.RelativeLayout;
import android.widget.TextView;
import area.guias.pfc.CommentsViewFragment.CommentsListAdapter.CommentHolder;

public class CommentItemView extends RelativeLayout {
	private TextView user, comment, date;
	private CommentHolder myHolder;

	public CommentItemView(Context context){
		super(context);
		inflate(context, R.layout.comment_item_view, this);
	}
	
	public void createViews(){
		myHolder = (CommentHolder) this.getTag();
	    user = myHolder.user;
	    comment = myHolder.comment;
	    date = myHolder.date;
	}
	
	public void setUser(String str){
		user.setText(str);
	}
	
	public void setComment(String str){
		comment.setText(str);
	}
	
	public void setDate(String str){
		date.setText(str);
	}
	
}
