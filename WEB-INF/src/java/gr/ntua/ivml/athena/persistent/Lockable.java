package gr.ntua.ivml.athena.persistent;

public interface Lockable {
	public Long getDbID();
	public String getLockname();
}
