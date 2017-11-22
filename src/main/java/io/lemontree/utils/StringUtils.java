package io.lemontree.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtils implements Serializable{

	private static final String NO_BREAK_SPACE = " ";
	public static final String LINE_BREAK = "\r\n";
	public static final String HTML_AWARE_WHITESPACE_EXPR = "\\s\\u00A0";
	
	private static final String HTML_AWARE_WHITESPACE_BEGIN_EXPR = "^["+HTML_AWARE_WHITESPACE_EXPR+"]+";
	private static final String HTML_AWARE_WHITESPACE_END_EXPR = "["+HTML_AWARE_WHITESPACE_EXPR+"]+$";

	public static PositionRange getFirstNestedEncapsulationPosition(
			String propertiesPart, String leftDelimiter, String rightDelimiter) {
		int startPos = propertiesPart.indexOf(leftDelimiter);
		if(startPos == -1){
			return new PositionRange(-1, propertiesPart.indexOf(rightDelimiter));
		}
		PositionRange range = new PositionRange();
		range.setStart(startPos);
		
		int nextStartPos = propertiesPart.indexOf(leftDelimiter, startPos+1);
		int endPos = propertiesPart.indexOf(rightDelimiter, startPos+1);
		
		if(endPos == -1 || nextStartPos==-1 || endPos < nextStartPos){
			return new PositionRange(startPos, endPos);
		}
		int openCnt = 1;
		boolean first = true;
		while(openCnt > 0){
			if(nextStartPos < endPos){
				openCnt++;
				nextStartPos = propertiesPart.indexOf(leftDelimiter, nextStartPos+1);
			}else{
				openCnt--;
				endPos = propertiesPart.indexOf(rightDelimiter, endPos+1);
			}
			if(first){
				openCnt--;
				first = false;
			}
			if(nextStartPos==-1||endPos==-1){
				break;
			}
		}
		if(openCnt==0){
			return new PositionRange(startPos, endPos);
		}
		return new PositionRange(startPos, -1);
	}

	
	/**
	 * Creates a new String with specified encoding. The same as new String(byte[], String) but without throwing an UnsupportedEncodingException.
	 * If the specified encoding is not supported a LagoonUtilsexception / RuntimeException is thrown instead.
	 * @param contentBytes
	 * @param encoding
	 * @return
	 */
	public static String createString(byte[] contentBytes, String encoding){
		try {
			return new String(contentBytes, encoding);
		} catch (UnsupportedEncodingException e) {
			throw new LagoonUtilsException("Could not create String with encoding '"+encoding+"'", e);
		}
	}


	/**
	 * Detects content within in nested structures, e.g. HTML tags
	 * @param openString
	 * @param closeString
	 * @param code
	 * @return
	 */
	public static String getEnclosedContentInNestedStructure(String openString, String closeString, String code, boolean includeBorders) {
		
		Integer[] positions = getEnclosedContentInNestedStructurePositions(openString, closeString, code, includeBorders);
		return code.substring(positions[0], positions[1]);
	}

	public static Integer[] getEnclosedContentInNestedStructurePositions(String openString, String closeString, String code, boolean includeBorders) {
	
		Integer[] out = new Integer[2];
		int startPos = code.indexOf(openString);
		if(startPos < 0){
			out[0] = -1;
			out[1] = -1;
		}
		int nextOpenTagPos = -1;
		int nextClosingTagPos = -1;
		int searchFromPos = startPos + openString.length();
		int openCount = 1;
		while(openCount != 0){
			nextOpenTagPos = code.indexOf(openString, searchFromPos);
			nextClosingTagPos = code.indexOf(closeString, searchFromPos);
			if(nextClosingTagPos < 0){
				throw new LagoonUtilsException("Nesting structure is not valid.");
			}
			if(nextOpenTagPos > -1 && nextOpenTagPos < nextClosingTagPos){
				openCount++;
				searchFromPos = nextOpenTagPos + openString.length(); 
			}else{
				openCount--;
				searchFromPos = nextClosingTagPos + closeString.length();
			}
		}
		if(includeBorders){
			out[0] = startPos;
			out[1] = nextClosingTagPos + closeString.length();
		}else{
			out[0] = startPos  + openString.length();
			out[1] = nextClosingTagPos;
		}
		return out; 
	}


	public static String toCamelCase(String string) {
		String [] splitStr = string.toLowerCase().split(" ");
		
		for(int i=0; i< splitStr.length; i++){
			splitStr[i] = toUpperCaseBegin(splitStr[i]);
		}
		return concatArray(splitStr, " ");
	}
	
	public static String convertSeparatorToCamelCase_LowerCaseBegin(final String rawName, String separator){
		return toLowerCaseBegin(convertSeparatorToCamelCase_UpperCaseBegin(rawName, separator));
	}
	
	public static String convertSeparatorToCamelCase_UpperCaseBegin(final String rawName, String separator){
		String [] nameParts = rawName.split(separator);
		String name = "";
		for(String namePart:nameParts){
			name += toUpperCaseBegin(namePart);
		}
		return name;
	}

	private static String concatArray(String[] splitStr, String seperator) {
		String out = "";
		for (int i=0; i < splitStr.length; i++){
			if(i > 0){
				out += seperator;
			}
			out += splitStr[i];
		}
		return out;
	}
	
	public static void createStringFileIfNotExists(final String targetFilePath, final String fileCode) {

		File targetFile = new File(targetFilePath);
		if(!targetFile.exists()){
			try {
				targetFile.createNewFile();
				writeStringToFile(fileCode, targetFile);
			} catch (IOException e) {
				throw new LagoonUtilsException(e);
			}
		}
	}
	public static void createOrOverwriteStringFile(final String targetFilePath, final String fileCode) {

		File targetFile = new File(targetFilePath);
		if(!targetFile.exists()){
			try {
				targetFile.createNewFile();
			} catch (IOException e) {
				throw new LagoonUtilsException(e);
			}
		}
		writeStringToFile(fileCode, targetFile);

	}
	
	private static final long serialVersionUID = 1L;

	public static String readFileAsString(String filePath)
			throws java.io.IOException {
		return readFileAsString(new File(filePath));
	}

	public static String readFileAsString(File file) {
		StringBuffer fileData = new StringBuffer(1000);
		try{
		BufferedReader reader = new BufferedReader(new FileReader(file));
		char[] buf = new char[1024];
		int numRead = 0;
		while ((numRead = reader.read(buf)) != -1) {
			String readData = String.valueOf(buf, 0, numRead);
			fileData.append(readData);
			buf = new char[1024];
		}
		reader.close();
		}catch(IOException e){
			throw new RuntimeException(e);
		}
		return fileData.toString();
	}

	public static String readResourceAsString(String resourceName) {
		InputStream is = StringUtils.class.getClassLoader()
				.getResourceAsStream(resourceName);
		return convertStreamToString(is);
	}
	
	public static String convertStreamToString(InputStream is) {
		
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} catch(Exception e){
				throw new LagoonUtilsException("Could not convert InputStream to String", e);
			}finally {
					IOUtils.closeQuietly(is);
			}
			return writer.toString();
		} 
		return "";
	}

	public static String getEncapsulatedString(String content, String from,
			String to, boolean includeBorders) {

		String substring = "";
		int startPos = content.indexOf(from);
		if (startPos == -1)
			return "";
		int endPos = content.indexOf(to, startPos + from.length() + 1);
		if (endPos == -1)
			return "";
		if (includeBorders) {
			substring = content.substring(startPos, endPos + to.length());
		} else {
			substring = content.substring(startPos + from.length(), endPos);
		}
		return substring;
	}

	public static List<String> getEncapsulatedStrings(String content,
			String from, String to, boolean includeBorders) {

		List<String> strings = new Vector<String>();

		int startPos = content.indexOf(from);

		while (startPos > -1) {
			String substring = "";
			int endPos = content.indexOf(to, startPos + from.length() );
			if (endPos == -1)
				break;

			if (includeBorders) {
				substring = content.substring(startPos, endPos + to.length());
			} else {
				substring = content.substring(startPos + from.length(), endPos);
			}
			strings.add(substring);
			startPos = content.indexOf(from, endPos + to.length());
		}
		return strings;
	}
	
	public static List<String> getEncapsulatedStrings_IgnoreEscaped(String content,
			String from, String to, boolean includeBorders, String escape) {

		List<String> strings = new Vector<String>();

		int startPos = content.indexOf(from);

		while (startPos > -1) {
			String substring = "";
			int endPos = content.indexOf(to, startPos + from.length() );
			
			while(isEscaped(content, escape, endPos)){
				endPos = content.indexOf(to, endPos + to.length() );
			}
			if (endPos == -1){
				break;
			}
			
			if (includeBorders) {
				substring = content.substring(startPos, endPos + to.length());
			} else {
				substring = content.substring(startPos + from.length(), endPos);
			}
			strings.add(substring);
			startPos = content.indexOf(from, endPos + to.length());
		}
		return strings;
	}


	private static boolean isEscaped(String content, String escape, int endPos) {
		
		if(endPos > escape.length()){
			String preceedingSigns = content.substring(endPos-escape.length(), endPos);
			if(preceedingSigns.equals(escape)){
				return true;
			}
		}
		return false;
	}

	public static String toLowerCaseBegin(String str) {
		return str.substring(0, 1).toLowerCase() + "" + str.substring(1);
	}

	public static String toUpperCaseBegin(String str) {
		if(!str.isEmpty()){
			return str.substring(0, 1).toUpperCase() + "" + str.substring(1);
		}
		return "";
	}

	public static String removeFromString(String containingString,
			String[] toBeRemoved) {
		for (String remove : toBeRemoved) {
			containingString = containingString.replace(remove, "");
		}
		return containingString;
	}

	public static int countOccurrence(String container, String find) {
		Pattern p = Pattern.compile(find);
		Matcher m = p.matcher(container);
		int count = 0;
		while (m.find()) {
			count += 1;
		}
		return count;
	}
	
	public static String escapeRegExCharacters(final String nonRegExString) {
		String[] regExExpressions = {"\\", "$", "[", "]", "(", ")", "{", "}", "|", "^", "*", "+", "?", "."};
		String regExString = nonRegExString;
		for(String replace:regExExpressions){
			regExString = regExString.replace(replace, "\\"+replace);
		}
		return regExString;
	}

	public static String replaceAtPosition(final String text,
			int replacePosStart, int replacePosEnd, final String replacement) {
		String pre = text.substring(0, replacePosStart);
		String post = text.substring(replacePosEnd);
		return pre+replacement+post;
	}

	public static int endIndexOf(String string, String find) {
		int pos = string.indexOf(find);
		if(pos > -1){
			pos += find.length();
		}
		return pos;
	}

	public static List<String> getDistinctValues(List<String> strings) {
		
		List<String> distincts = new ArrayList<String>();
		for(String s:strings){
			if(!isContainedInList(s, distincts)){
				distincts.add(s);
			}
		}
		return distincts;
	}
	
	public static boolean isContainedInList(String checkValue, Iterable<String> values){
		for(String listValue:values){
			if(listValue.equals(checkValue)){
				return true;
			}
		}
		return false;
	}
	public static boolean isContainedInArray(String checkValue, String... values){
		for(String listValue:values){
			if(listValue.equals(checkValue)){
				return true;
			}
		}
		return false;
	}
	
	public static void writeStringToFile(String output, File file) {
		try{
			FileWriter writer = new FileWriter(file);
			writer.write(output);
			writer.close();
		}catch(IOException e){
			throw new LagoonUtilsException(e);
		}
	}
	
	public static String getLongestCommonSubstring(String string1, String string2){
		int start = 0;
	    int max = 0;
	    for (int i = 0; i < string1.length(); i++)
	    {
	        for (int j = 0; j < string2.length(); j++)
	        {
	            int x = 0;
	            while (string1.charAt(i + x) == string2.charAt(j + x))
	            {
	                x++;
	                if (((i + x) >= string1.length()) || ((j + x) >= string2.length())) break;
	            }
	            if (x > max)
	            {
	                max = x;
	                start = i;
	            }
	         }
	    }
	    return string1.substring(start, (start + max));
	}

	public static int countOccurrenceInList(List<String> itemValues, String value) {
		int cnt =0;
		for(String listedName:itemValues){
			if(listedName.equals(value)){
				cnt++;
			}
		}
		return cnt;
	}

	public static List<String> copyList(List<String> strings) {
		return (List<String>) CollectionUtils.copyList(strings);
	}

	public static boolean containsWhitespace(String lookup) {
		Pattern pat = Pattern.compile("\\s");
		Matcher mat = pat.matcher(lookup);
		return mat.find();
	}

	public static boolean isSubstringContainedInList(List<String> subList,
			String lookup) {
		for(String s:subList){
			if(s.contains(lookup)){
				return true;
			}
		}
		return false;
	}

	public static void printList(List<String> strings) {
		for(String s:strings){
			System.out.println(s);
		}
	}
	
	public static String replaceFirstOccurrence(String containingString, String find,
			String replace) {
		int pos = containingString.indexOf(find);
		if(pos < 0){
			return containingString;
		}
			
		String pre = containingString.substring(0, pos);
		String post = containingString.substring(pos+find.length());
		
		return pre + replace +post;
	}
	
	public static String replaceLastOccurrence(String containingString, String find,
			String replace) {
		int pos = containingString.lastIndexOf(find);
		if(pos<0){
			return containingString;
		}
			
		String pre = containingString.substring(0, pos);
		String post = containingString.substring(pos+find.length());
		
		return pre + replace +post;
	}

	public static String[] trimAllItems(String[] toTrim) {
		String[] out = new String[toTrim.length];
		for(int i = 0; i < toTrim.length; i++){
			out[i] = toTrim[i].trim();
		}
		return out;
	}

	public static boolean startsWithIgnoreCase(String containingString, String prefix) {
		if(prefix.length()>containingString.length()){
			return false;
		}
		String containingPrefix = containingString.substring(0, prefix.length());
		return containingPrefix.equalsIgnoreCase(prefix);
	}
	
	public static boolean endsWithIgnoreCase(String containingString, String postfix) {
		if(postfix.length()>containingString.length()){
			return false;
		}
		int beginPos = containingString.length() - postfix.length();
		String containingPostfix= containingString.substring(beginPos, containingString.length());
		return containingPostfix.equalsIgnoreCase(postfix);
	}

	public static boolean isNullOrEmpty(String value) {
		return value==null || value.isEmpty();
	}
	public static boolean isNotNullNorEmpty(String value) {
		return !isNullOrEmpty(value);
	}
	
	public static String concatStrings(List<String> values) {
		String out = "";
		for(String value: values){
			out += value;
		}
		return out;
	}

	public static String emptyIfNull(final String value) {
		return value==null?"":value;
	}
	
	public static void printStream(InputStream is) {
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new InputStreamReader(is,
						"UTF-8");
				int n=-1;
				while ( (n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
					writer.flush();
					System.out.println("WRITER TEMP ***"+writer.toString()+"***");
				}
				
				if(n==-1){
					System.out.println("-- reader eof reached.");
				}
				
			} catch(IOException e){
				throw new RuntimeException("Error reading input stream.", e);
			}finally {
				//is.close();
			}
			//return writer.toString();
		} 
	}
	
	public static String trimHtmlAware(String s){
		String out = s.replaceFirst(HTML_AWARE_WHITESPACE_BEGIN_EXPR,"");
		out = out.replaceFirst(HTML_AWARE_WHITESPACE_END_EXPR,"");
		return out;
	}
	
	/**
	 * Replaces so called NO-BREAK SPACE (unicode 'u+200', html '&nbsp;') with regulare spaces;
	 * @param val
	 * @return
	 */
	public static String replaceNoBreakSpaces(String val) {
		return val.replace(NO_BREAK_SPACE, " ");
	}


	public static List<String> replaceInAll(List<String> strings, String find, String replace) {
		
		List<String> out = new ArrayList<String>();
		for(String s : strings){
			String replaced = s.replace(find, replace);
			out.add(replaced);
		}
		return out;
	}
}
