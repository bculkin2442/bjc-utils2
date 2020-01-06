package bjc.utils.ioutils.properties;

// @TODO implement me - ben, 1/6/20
@SuppressWarnings("javadoc")
public class Property {
	public String name;
	public String comment;
	public String value;

	public Property(String name, String comment, String value) {
		this.name    = name;
		this.comment = comment;
		this.value   = value;
	}
}
