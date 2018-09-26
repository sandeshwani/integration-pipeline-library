class ToolRunner {
	static String TOKEN_PRECEDING_DEPENDENCY = '---'
	static String DEPENDENCY_TRAILING_DELIMITER = ' '

    String getProjectVersionString(File dir) {
        try {
	    List<String> propsOutputLines = execute(dir, "./gradlew", "properties", "-q")
            int versionLineIndex = 0
            for (int i = 0; i < propsOutputLines.size(); i++) {
                String trimmedLine = propsOutputLines[i].trim()
                if (trimmedLine.startsWith('version: ')) {
                    String versionLine = trimmedLine
                    String version = versionLine.substring(versionLine.indexOf(':') + 1).trim()
                    return version
                }
            }
            println "Failed to find the project version in the gradle properties command"
        } catch (Exception e) {
            String msg = "Error running the gradle properties command to get the project version, or interpreting its output: ${e.getMessage()}"
	    println msg
	    throw new RuntimeException(msg, e)
        }
        return null
    }

    List<String> getCompileDependencies(File dir) {
	List<String> dependencies = new ArrayList<>()
	    List<String> dependenciesOutputLines = execute(dir, "./gradlew", "dependencies", "--configuration", "compile")
 	    boolean inDependenciesList = false
            for (String dependenciesOutputLine : dependenciesOutputLines) {
		if (!inDependenciesList) {
			// we haven't reached dependencies yet
			if (dependenciesOutputLine.startsWith("compile - Dependencies")) {
				inDependenciesList = true
			}
			continue
		}
		// we're in, or done with, dependencies
		if (dependenciesOutputLine.contains(TOKEN_PRECEDING_DEPENDENCY)) {
			int tokenPrecedingDependencyIndex = dependenciesOutputLine.indexOf(TOKEN_PRECEDING_DEPENDENCY);
			String dep = dependenciesOutputLine.substring(tokenPrecedingDependencyIndex+(TOKEN_PRECEDING_DEPENDENCY.length()))
			dep = dep.trim()
			int delimeterFollowingDependency = dep.indexOf(DEPENDENCY_TRAILING_DELIMITER);
			if (delimeterFollowingDependency >= 0) {
				dep = dep.substring(0, delimeterFollowingDependency)
			}
			dependencies.add(dep)
			continue
		}
		// we've reached the line after end of dependencies
		break
            }
        return dependencies
    }

    List<String> getDiffOutput(File libraryDir) {
	return execute(libraryDir, "git", "diff")
    }

    void reset(File libraryDir) {
	execute(libraryDir, "git", "reset", "--hard")
    }

    List<String> execute(File dir, String ...args) {
	if (!dir.isDirectory()) {
		String msg = "ERROR: directory ${dir.getAbsolutePath()} does not exist or is not a directory"
		throw new RuntimeException(msg)
	}
        ProcessBuilder pb = new ProcessBuilder(args)
        pb = pb.directory(dir)
	Process process = pb.start()
	process.waitFor()
	InputStream is = process.getInputStream()
	List<String> outputLines = new ArrayList<>()
	String line = null;
	BufferedReader bufferedReader = null
	try {
		bufferedReader = new BufferedReader(new InputStreamReader(is, java.nio.charset.StandardCharsets.UTF_8))
		while ((line = bufferedReader.readLine()) != null) {
			outputLines.add(line);
		}
	} finally {
		if (bufferedReader != null) {
			bufferedReader.close()
		}
	}
	return outputLines
    }
}