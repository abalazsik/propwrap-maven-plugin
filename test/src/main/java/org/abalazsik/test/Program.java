
package org.abalazsik.test;

import java.io.InputStreamReader;

/**
 *
 * @author ador
 */
public class Program {

	public static void main(String[] args) {
		System.out.println(String.format(hello.properties.template(), hello.properties.name()));
		hello.properties.load(Program.class.getResourceAsStream("/hello-ext.properties"));
		System.out.println(hello.properties.this_is_a_longer_property()); // new value
	}

}
