package data;

import java.io.File;
import java.io.IOException;
import exceptions.FileFormatException;

// Interface for all file types (in our case CSV and JSON)
// The GUI doesn't need to know which type is used; chooseHandler picks the appropriate FileHandler
public interface FileHandler {

	// Reads data from a file
	// IOException - if the file cannot be opened or read, FileFormatException - if the file content is invalid
	DataStorage load(File file) throws IOException, FileFormatException;

	// Writes data to a file
	// IOException - if the file cannot be created or written
	void save(File file, DataStorage storage) throws IOException;


	// Chooses a handler based on the file type
	// FileFormatException - if the extension is not supported
	static FileHandler chooseHandler(File file) throws FileFormatException {

		if (file == null) {
			throw new FileFormatException("No file selected.");
		}

		String name = file.getName().toLowerCase();

		if (name.endsWith(".csv")) {
			return new CsvFileHandler();
		}
		if (name.endsWith(".json")) {
			return new JsonFileHandler();
		}

		throw new FileFormatException("Only files with the .csv and .json extensions are supported (selected: " + file.getName() + ").");
	}
}