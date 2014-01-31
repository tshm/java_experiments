import java.net.URL;
import java.io.*;
import java.util.Date;

class pageloader extends Thread {
	static File dir;            // saving directory
	static PrintWriter log;     // log file
	char mode;           
	URL url;

	public static void main(String argv[]) {
		if (1 != argv.length) {
			System.err.println("incorrect usuage!");
			System.exit(1);
		}
		try {
			log = new PrintWriter(new FileOutputStream("log"), true);
			BufferedReader in = new BufferedReader(new FileReader(argv[0]));
			dir = new File(new File(argv[0]).getPath());
			String line;
			while (null != (line = in.readLine())) {
				if (line.length() < 2) throw new Exception("invalid request");
				new pageloader(line.substring(2), line.charAt(0)).start();
			}
			while (activeCount() != 1) sleep(1000);
			writeLog("finished");
			log.close();
		} catch(Exception e) {
			System.err.println(e);
			System.exit(1);
		}
	}

	pageloader(String name, char mode) throws IOException {
		try {this.url = new URL(name);
		} catch(Exception e) {throw new IOException("URL error");}
		this.mode = mode;
	}

	public void run() {
		File file = new File(url.getHost() + url.getFile().replace('/', '.'));
		if (file.exists()) return;
		try {
			switch(mode) {
				case 'b': case 'B': binaryDown(file); break;
				case 'a': case 'A': asciiDown(file); break;
				case 'h': case 'H': pageDown(file); break;
				default: writeLog("invalid mode: " + mode); return;
			}
			writeLog((new Date().toString()).substring(0,20) + " " + file );
		} catch(Exception e) {writeLog(e.toString());}
	}

	public void binaryDown(File file) throws Exception {
		BufferedOutputStream writer = 
			new BufferedOutputStream(new FileOutputStream(file));
		BufferedInputStream reader = new BufferedInputStream(url.openStream());
		int data;
		while (-1 != (data = reader.read())) writer.write(data);
		writer.close();
		reader.close();
	}

	public void asciiDown(File file) throws Exception {
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		BufferedReader reader = 
			new BufferedReader(new InputStreamReader(url.openStream(), "EUCJIS"));
		String line;
		try {
			while (null != (line = reader.readLine())) {
				writer.write(line, 0, line.length());
				writer.newLine();
			}
		} catch (sun.io.MalformedInputException e) {
			reader.close();
			writer.close();
			file.delete();
			writer = new BufferedWriter(new FileWriter(file));
			reader = 
				new BufferedReader(new InputStreamReader(url.openStream(), "SJIS"));
			while (null != (line = reader.readLine())) {
				writer.write(line, 0, line.length());
				writer.newLine();
			}
		}
		writer.close();
		reader.close();
	}

	public void pageDown(File file) throws Exception {
		File tempfile = new File(file.toString() + "~");
		asciiDown(tempfile);
		BufferedReader reader = new BufferedReader(new FileReader(tempfile));
		BufferedWriter writer = new BufferedWriter(new FileWriter(file));
		String line, lnk;
		int i, j;
		while (null != (line = reader.readLine())) {
			while (-1 != (i = line.indexOf('\"'))) {               // "
				writer.write(line, 0, i+1);
				if (-1 == (j = line.indexOf('\"', i+1))) break;      // "
				lnk = line.substring(i+1, j);
				line = line.substring(j+1);
				if (lnk.endsWith(".gif") || lnk.endsWith(".GIF") ||
						lnk.endsWith(".Gif") || lnk.endsWith(".JPG") ||
						lnk.endsWith(".JPEG") || lnk.endsWith(".Jpg") ||
						lnk.endsWith(".jpeg") || lnk.endsWith(".jpg")) {
					if (!lnk.startsWith("http:") && !lnk.startsWith("ftp:")) {
						String temp = url.toExternalForm();
						lnk = temp.substring(0, temp.lastIndexOf('/')+1) + lnk;
					}
					new pageloader(lnk, 'b').start();
					URL url = new URL(lnk);
					lnk = url.getHost() + url.getFile().replace('/', '.');
						}
				writer.write(lnk + '\"' , 0, lnk.length()+1);      // "
			}
			writer.write(line, 0, line.length());   
			writer.newLine();
		}
		writer.close();
		reader.close();
	}

	static synchronized void writeLog(String st) {log.println(st);}
}
