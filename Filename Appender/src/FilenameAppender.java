import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

public class FilenameAppender {
	
	final public static boolean DEBUG_OUTPUT = true;
	
	public static void main(String[] args) {
		JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getRoots()[0]);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		if(fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) { return; }
		
		String filters[] = new String[] { "Image Files", "Compressed Files", "Text Files", "Code Files", "All Files" };
		int filterIndex = 0;
		Object value = JOptionPane.showInputDialog(null, "Select file type filter:", "File Type Filter", JOptionPane.QUESTION_MESSAGE, null, filters, filters[0]);
		if(value == null) { return; }
		for(int i=0;i<filters.length;i++) {
			if(filters[i].equals(value)) {
				filterIndex = i;
				break;
			}
		}
		
		int answer = JOptionPane.showConfirmDialog(null, "Recurse to subdirectories?", "Recurse Subdirectories", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
		if(answer == JOptionPane.CANCEL_OPTION) { return; }
		boolean recurseSubdirectories = answer == JOptionPane.YES_OPTION;
		
		String modes[] = new String[] { "Append / Prepend", "Replace Part" };
		int modeIndex = 0;
		value = JOptionPane.showInputDialog(null, "Choose mode:", "Mode Selection", JOptionPane.QUESTION_MESSAGE, null, modes, modes[0]);
		if(value == null) { return; }
		for(int i=0;i<modes.length;i++) {
			if(modes[i].equals(value)) {
				modeIndex = i;
				break;
			}
		}
		
		if(modeIndex == 0) {
			append(fileChooser, filterIndex, recurseSubdirectories);
		}
		else if(modeIndex == 1) {
			replace(fileChooser, filterIndex, recurseSubdirectories);
		}
	}
	
	public static void replace(JFileChooser fileChooser, int filterIndex, boolean recurseSubdirectories) {
		String text = JOptionPane.showInputDialog(null, "Enter the text to replace in each file name:", "Enter Text", JOptionPane.QUESTION_MESSAGE);
		if(text == null || text.length() == 0) { return; }
		
		String newText = JOptionPane.showInputDialog(null, "Enter the replacement text (leave empty to remove):", "Enter Text", JOptionPane.QUESTION_MESSAGE);
		
		int numberOfFilesRenamed = modifyFiles(fileChooser.getSelectedFile(), 1, filterIndex, -1, text, newText, recurseSubdirectories, true, DEBUG_OUTPUT);
		
		System.out.println("Total number of files renamed: " + numberOfFilesRenamed);
		
		JOptionPane.showMessageDialog(null, "Total number of files renamed: " + numberOfFilesRenamed, "Number of Files Renamed", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static void append(JFileChooser fileChooser, int filterIndex, boolean recurseSubdirectories) {
		String modifications[] = new String[] { "Prepend", "Append", "Both" };
		int modificationIndex = 0;
		Object value = JOptionPane.showInputDialog(null, "Select modification type:", "Modification Type Selecter", JOptionPane.QUESTION_MESSAGE, null, modifications, modifications[0]);
		if(value == null) { return; }
		for(int i=0;i<modifications.length;i++) {
			if(modifications[i].equals(value)) {
				modificationIndex = i;
				break;
			}
		}
		
		String text = JOptionPane.showInputDialog(null, "Enter the text to " + modifications[modificationIndex] + " to each file name:", "Enter Text", JOptionPane.QUESTION_MESSAGE);
		if(text == null) { return; }
		
		int numberOfFilesRenamed = modifyFiles(fileChooser.getSelectedFile(), 0, filterIndex, modificationIndex, text, null, recurseSubdirectories, true, DEBUG_OUTPUT);
		
		System.out.println("Total number of files renamed: " + numberOfFilesRenamed);
		
		JOptionPane.showMessageDialog(null, "Total number of files renamed: " + numberOfFilesRenamed, "Number of Files Renamed", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static int modifyFiles(File file, int modeIndex, int filterIndex, int modificationIndex, String text, String newText, boolean recurseSubdirectories, boolean rootDirectory, boolean debugOutput) {
		if(file == null || text == null) { return 0; }
		
		if(file.isDirectory()) {
			if(recurseSubdirectories || rootDirectory) {
				File[] contents = file.listFiles();
				
				int numberOfFiles = 0;
				for(int i=0;i<contents.length;i++) {
					numberOfFiles += modifyFiles(contents[i], modeIndex, filterIndex, modificationIndex, text, newText, recurseSubdirectories, false, debugOutput);
				}
				return numberOfFiles;
			}
			return 0;
		}
		else {
			if((filterIndex == 0 && file.getName().toLowerCase().matches(".*\\.(jpg|png|bmp|gif)$")) ||
			   (filterIndex == 1 && file.getName().toLowerCase().matches(".*\\.(zip|rar)$")) ||
			   (filterIndex == 2 && file.getName().toLowerCase().matches(".*\\.(txt)$")) ||
			   (filterIndex == 3 && file.getName().toLowerCase().matches(".*\\.(java|cpp|h|cs)$")) ||
			    filterIndex == 4) {
				
				boolean renamed = false;
				int index = file.getName().lastIndexOf('.');
				if(index > 0) {
					String fileName = file.getName().substring(0, index);
					String extension = file.getName().substring(index + 1, file.getName().length()).toLowerCase();
					
					String newFileName = null;
					File newFile = null;
					if(modeIndex == 0) {
						newFileName = (modificationIndex == 0 || modificationIndex == 2 ? text : "") + fileName + (modificationIndex == 1 || modificationIndex == 2 ? text : "");
						newFile = new File(file.getAbsolutePath().replace(file.getName(), "") + newFileName + "." + extension);
					}
					else if(modeIndex == 1) {
						newFileName = fileName.replace(text, newText == null ? "" : newText);
						newFile = new File(file.getAbsolutePath().replace(file.getName(), "") + newFileName + "." + extension);
					}
					
					if(newFile.exists() && !(fileName + "." + extension).equals(newFileName + "." + extension)) {
						if(debugOutput) {
							System.out.println("Failed to rename file: \"" + file.getName() + "\" to \"" + newFileName + "." + extension + "\", new file already exists.");
						}
					}
					
					if(debugOutput) {
						System.out.println("Renaming: \"" + file.getName() + "\" to \"" + newFileName + "." + extension + "\".");
					}
					
					renamed = file.renameTo(newFile);
				}
				
				if(!renamed && debugOutput) {
					System.out.println("Failed to rename file: \"" + file.getName() + "\".");
				}
				
				return renamed ? 1 : 0;
			}
			
			return 0;
		}
	}
	
}
