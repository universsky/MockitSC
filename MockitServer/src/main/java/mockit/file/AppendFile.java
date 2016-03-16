/**
 * 
 */
package mockit.file;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

/**
 * @author jack
 *
 */
public class AppendFile {

	public static void write(String file, String conent) {
		File f = new File(file);

		if (!f.exists()) {
			try {
				if (f.createNewFile()) {
					System.out.println("Created file:" + file);

				}
			} catch (IOException e1) {
				System.out.println("Create file:" + file + " Failed!");
				e1.printStackTrace();
			}
		}

		BufferedWriter out = null;
		try

		{
			out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file, true)));
			out.write(conent);
		} catch (

		Exception e)

		{
			e.printStackTrace();
		} finally

		{
			try {
				if (out != null) {
					out.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
