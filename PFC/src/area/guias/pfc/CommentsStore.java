package area.guias.pfc;

import java.util.ArrayList;

public interface CommentsStore {
	
	public void saveComment(Comment comment);
	
	public Comment getComment(int position);

	public void deleteList();

    public ArrayList<Comment> commentsList();

}
