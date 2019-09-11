package area.guias.pfc;

import java.util.ArrayList;

public class CommentsArray implements CommentsStore {
	private ArrayList<Comment> commentsArray;
	
	public CommentsArray(){
		commentsArray = new ArrayList<Comment>();
	}

	@Override
	public void saveComment(Comment comment) {
		commentsArray.add(comment);		
	}

	@Override
	public Comment getComment(int position) {
		return commentsArray.get(position);
	}

	@Override
	public void deleteList() {
		commentsArray.clear();
	}

	@Override
	public ArrayList<Comment> commentsList() {
		return commentsArray;
	}

}
