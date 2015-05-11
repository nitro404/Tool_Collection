import java.io.*;
import javax.swing.*;
import javax.swing.filechooser.*;

public class LineCounter {
	
	public static void main(String[] args) {
		JFileChooser fileChooser = new JFileChooser(FileSystemView.getFileSystemView().getRoots()[0]);
		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		
		if(fileChooser.showOpenDialog(null) != JFileChooser.APPROVE_OPTION) { return; }
		
		String filters[] = new String[] { "All Files", "Text Files", "Code Files" };
		int filterIndex = 0;
		Object value = JOptionPane.showInputDialog(null, "Select file type filter", "File Type Filter", JOptionPane.QUESTION_MESSAGE, null, filters, filters[0]);
		if(value == null) { return; }
		for(int i=0;i<filters.length;i++) {
			if(filters[i].equals(value)) {
				filterIndex = i;
				break;
			}
		}
		
		int linesOfCode = countLines(fileChooser.getSelectedFile(), filterIndex);
		
		System.out.println("Total Lines of Text: " + linesOfCode);
		
		JOptionPane.showMessageDialog(null, "Total Lines of Text: " + linesOfCode, "Lines of Text", JOptionPane.INFORMATION_MESSAGE);
	}
	
	public static int countLines(File file, int filterIndex) {
		if(file == null) { return 0; }
		
		if(file.isDirectory()) {
			File[] contents = file.listFiles();
			
			int lines = 0;
			for(int i=0;i<contents.length;i++) {
				lines += countLines(contents[i], filterIndex);
			}
			return lines;
		}
		else {
			if( filterIndex == 0 ||
			   (filterIndex == 1 && file.getName().toLowerCase().matches(".*\\.(txt)$")) ||
			   (filterIndex == 2 && file.getName().toLowerCase().matches(".*\\.(java|cpp|h|cs)$"))) {
				int lines = 0;
				LineNumberReader in;
				try {
					in = new LineNumberReader(new FileReader(file));
					
					while(in.readLine() != null);
					
					lines = in.getLineNumber();
					
					System.out.println("File: " + file.getName().replaceAll(".*[\\\\/]", "") + " Lines of Text: " + lines);
					
					in.close();
				}
				catch(Exception e) { }
				
				return lines;
			}
			
			return 0;
		}
	}
	
}
