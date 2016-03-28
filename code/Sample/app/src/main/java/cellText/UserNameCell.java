package cellText;


public class UserNameCell extends ColorTextCell {
	
	private static final long serialVersionUID = 90745655000732280L;
	public UserNameCell() {
		this(TextCell.SIGN_USER);		
	}

	public UserNameCell(int type) {
		this(type, null);
	}

	public UserNameCell(int type, String text) {
		super(type, text);
		init(type, text);
	}
	
	private void init(int type, String text) {
		/*
		setTextColor(DLApp.getContext()
				.getResources().getColor(R.color.feed_user_name));
				*/
	}
}
