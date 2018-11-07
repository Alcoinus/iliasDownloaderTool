package utils;

public class DirectoryUtils {

	private static DirectoryUtils instance;

	private DirectoryUtils() {
	};

	private String[] oddChars = { ":", "/", "*", "?", "<", ">", "|", "\"", "\\" };
	private String[] oddCharsReplacements = { " - ", "-", "+", "_", "[", "]", "--", "", "-" };

	public String makeDirectoryNameValid(String directoryname) {
		return createValidWinDir(directoryname);
	}

	private String createValidWinDir(String directoryname) {
		String validDirectoryName = directoryname;
		for (int i = 0; i < oddChars.length; i++) {
			validDirectoryName = validDirectoryName.replace(oddChars[i], oddCharsReplacements[i]);
		}
		return validDirectoryName;
	}

	public static DirectoryUtils getInstance() {
		if (instance == null) {
			instance = new DirectoryUtils();
		}
		return instance;
	}
}
