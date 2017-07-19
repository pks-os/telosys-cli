package org.telosys.tools.cli.commands;

import java.io.File;
import java.io.FilenameFilter;
import java.io.PrintWriter;

import org.telosys.tools.cli.Command;
import org.telosys.tools.cli.Environment;
import org.telosys.tools.commons.FileUtil;

public class LsCommand extends Command {
	
	/**
	 * Constructor
	 * @param out
	 */
	public LsCommand(PrintWriter out) {
		super(out);
	}

	@Override
	public String getName() {
		return "ls";
	}

	@Override
	public String getShortName() {
		return null ;
	}

	@Override
	public String getDescription() {
		return "List the content of a directory ( 'ls' or 'ls [dir]' ) ";
	}
	
	@Override
	public String execute(Environment environment, String[] args) {
		if ( args.length > 1 ) {
			return ls(environment, args[1]);
		}
		else {
			return ls(environment, null);
		}
	}

	private String ls(Environment environment, final String parameter) {
		File currentDir = new File(environment.getCurrentDirectory());
		if (parameter == null) {
			// --- No parameter => list current directory
			return buildFilesList(currentDir, currentDir.listFiles());
		} else {
			// --- Parameter : is it a directory ?
			File argFile = new File(FileUtil.buildFilePath(environment.getCurrentDirectory(), parameter));
			if (argFile.exists() && argFile.isDirectory()) {
				// --- List parameter directory
				return buildFilesList(argFile, argFile.listFiles());
			} else {
				// --- Parameter is not a directory => it's a RegExp
				FilenameFilter filenameFilter = new FilenameFilter() {
					@Override
					public boolean accept(File dir, String name) {
						return (name.matches(buildRegExp(parameter)));
					}
				};
				// File[] files = currentDir.listFiles(filenameFilter);
				return buildFilesList(currentDir, currentDir.listFiles(filenameFilter));
			}
		}
	}

	private String buildFilesList(File dir, File[] files) {
		int n = 0;
		StringBuffer sb = new StringBuffer();
		sb.append(dir.getPath() + " : ");
		appendEndOfLine(sb);
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					sb.append(" d");
				} else {
					sb.append(" -");
				}
				sb.append(" ");
				sb.append(file.getName());
				appendEndOfLine(sb);
				n++;
			}
		}
		// buf.append(" "+n+" file(s)");
		sb.append("        " + n + " file(s)");
		appendEndOfLine(sb);
		return sb.toString();
	}

	private String buildRegExp(String filterPattern) {
		StringBuffer buf = new StringBuffer();
		// "*" --> ".*"
		// "." --> "\."
		// "$" --> "\$"
		byte[] bytes = filterPattern.getBytes();
		for (byte b : bytes) {
			if (b == '*') {
				buf.append(".*");
			} else if (b == '.' || b == '$' || b == '^' || b == '[' || b == ']') {
				buf.append('\\');
				buf.append((char) b);
			} else {
				buf.append((char) b);
			}
		}
		return buf.toString();
	}
}
