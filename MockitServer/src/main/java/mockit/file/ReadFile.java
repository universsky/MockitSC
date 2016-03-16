/**
 * 
 */
package mockit.file;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

/**
 * @author jack
 *
 */
public class ReadFile {

	@SuppressWarnings("resource")
	public static String read(String file) {

		StringBuilder sb = new StringBuilder();
		FileInputStream fis = null;
		RandomAccessFile raf = null;
		try {
			fis = new FileInputStream(file);
			raf = new RandomAccessFile(new File(file), "r");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		String line;
		try {
			while ((line = raf.readLine()) != null) {
				System.out.println(line);
				sb.append(line);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			raf.close();
			fis.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return sb.toString();

	}

}
