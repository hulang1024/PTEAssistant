package assistant.domain;

public class User {
	public Long uid;
	public String username;
	public String password;
	
	public User() {}
	public User(Long uid) {
		this(uid, null, null);
	}
	public User(String username, String password) {
		this(null, username, password);
	}
	public User(Long uid, String username, String password) {
		this.uid = uid;
		this.username = username;
		this.password = password;
	}
	
	@Override
	public String toString() {
		return String.format("[uid=%d, username=%s, password=%s]",
		    this.uid, this.username, this.password);
	}
}
