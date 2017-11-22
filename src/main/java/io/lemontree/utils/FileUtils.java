package io.lemontree.utils;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class FileUtils {
	
	public static File getResourceAsTempFile(String resourceName) throws IOException, URISyntaxException{
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		
		InputStream is = classLoader.getResourceAsStream(resourceName);
		return streamToTempFile(is);
	}
	
	public static List<File> readFilesRecursive(String dirPath, FilenameFilter filter){
		File dir = new File(dirPath);
		return readFilesRecursive(dir, filter);
	}
	
	public static List<File> readFilesRecursive(File dir, FilenameFilter filter){
		if(!dir.isDirectory()){
			throw new RuntimeException("The provided file "+dir.getAbsolutePath()+" is not a directory!");
		}
		List<File> out = new ArrayList<File>();
		for(File f:dir.listFiles()){
			if(f.isDirectory() && !f.getName().equals(".") && !f.getName().equals("..")){
				out.addAll(readFilesRecursive(f, filter));
			}else if ((filter==null)||
					filter!=null && filter.accept(f, f.getName())){
				
				out.add(f);
			}
		}
		return out;
	}

	public static void copyFile(String srcFile, String targetFile) {
		
		try {
			File source = new File(srcFile);
			
			File target = new File(targetFile);
			target.getParentFile().mkdirs();
			target.createNewFile();
			
			InputStream in = new FileInputStream(source);
			OutputStream out = new FileOutputStream(target);

			copyStream(in, out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void copyStreamToFile(String fromResource, String targetFile) {

		InputStream in = FileUtils.class.getClassLoader().getResourceAsStream(
				fromResource);
		File target = new File(targetFile);
		target.getParentFile().mkdirs();
		
		OutputStream out = null;
		try {
			target.createNewFile();
			out = new FileOutputStream(target);
			copyStream(in, out);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void copyStreamToFile(InputStream is, String targetFile){
		File target = new File(targetFile);
		target.getParentFile().mkdirs();
		
		OutputStream out = null;
		try {
			target.createNewFile();
			out = new FileOutputStream(target);
			copyStream(is, out);
		} catch (IOException e) {
			throw new LagoonUtilsException("Could not copy input stream to output file "+targetFile, e);
		}
	}

	private static void copyStream(InputStream in, OutputStream out) {
		try {
			byte[] buf = new byte[1024];
			int len;
			while ((len = in.read(buf)) > 0) {
				out.write(buf, 0, len);
			}
			in.close();
			out.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void copyFileToOutputStream(File downloadFile, OutputStream responseOutputStream) throws FileNotFoundException, IOException {
		OutputStream output = null;
	    InputStream input = null;
	    
	    try {
	        input = new BufferedInputStream(new FileInputStream(downloadFile));
	        output = new BufferedOutputStream(responseOutputStream);
	        byte[] buffer = new byte[8192];

	        for (int length; (length = input.read(buffer)) != -1;) {
	            output.write(buffer, 0, length);
	        }
	    } finally {
	        if (output != null) try { output.close(); } catch (IOException ignore) {}
	        if (input != null) try { input.close(); } catch (IOException ignore) {}
	    }
	}
	
	public static File streamToTempFile (InputStream in) throws IOException {
        final File tempFile = File.createTempFile("_lagoon_runtime_", ".tmp");
        tempFile.deleteOnExit();
        try (FileOutputStream out = new FileOutputStream(tempFile)) {
        	copyStream(in, out);
        }
        return tempFile;
    }

	/**
	 * Recursively deletes a directory in the file system
	 * @param dir
	 */
	public static void deleteDir(File dir) {
		File[] files = dir.listFiles();
		if(files!=null) { //some JVMs return null for empty dirs
			for(File f: files) {
				if(f.isDirectory()) {
					deleteDir(f);
		         } else {
		        	 f.delete();
		         }
		    }
		 }
		dir.delete();
	}
	
	public static void createDir(String url) {
		File f = new File(url);
		f.mkdir();
	}
	public static void deleteDir(String url) {
		File f = new File(url);
		FileUtils.deleteDir(f);
	}

}
