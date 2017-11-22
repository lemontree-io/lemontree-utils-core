package io.lemontree.utils;

import java.io.InputStream;

public class IOUtils {

	public static void closeQuietly(InputStream is){
		try{
			is.close();
		}catch(Exception e){/*expected*/}
	}
}
