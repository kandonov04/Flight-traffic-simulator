package exceptions;

// ERROR IN FILE CONTENT!
// The file was successfully opened and read, but its content does not match the expected format
public class FileFormatException extends Exception {

	public FileFormatException(String message) {
		super(message);
	}
}