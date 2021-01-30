
package org.abalazsik.propwrap;

import java.io.InputStream;
import java.io.PrintWriter;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 *
 * @author ador
 */
public class WrapperGenerator {

	private static final String TEMPLATE;
	
	static {
		try (InputStream inputStream = WrapperGenerator.class.getResourceAsStream("/wrapper.tmpl")){
			
			TEMPLATE = new String(inputStream.readAllBytes());

		} catch(Exception e) {
			throw new RuntimeException("Failed to read wrapper.tmpl!", e);
		}
	}
	
	public static String normalize(String str) {
		char[] result = new char[str.length()];
		
		for (int i = 0; i < str.length(); i++) {
			if (Character.isAlphabetic(str.charAt(i)) || Character.isDigit(str.charAt(i))) {
				result[i] = str.charAt(i);
			} else {
				result[i] = '_';
			}
		}
		
		return new String(result);
	}
	
	public static void writeWrapper(PrintWriter output, String pkg, String name, Properties properties) {
		String className = normalize(name);
		
		String filename = name + ".properties";
		
		output.write(
				TEMPLATE
					.replaceAll("%CLASSNAME%", className)
					.replaceAll("%PACKAGE%", pkg)
					.replaceAll("%FILENAME%", filename)
					.replace("%PROPERTIES%",
						String.join("\n\n",
							properties.stringPropertyNames()
								.stream()
									.map(WrapperGenerator::asProp)
										.collect(Collectors.toList())))
		);
	}
	
	public static String asProp(String propName) {
		return String.format(
				"\n\tpublic String %s(){"
				+ "\n\t\treturn props.getProperty(\"%s\");"
				+ "\n\t}\n",
				normalize(propName), propName);
	}

}
