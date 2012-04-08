package se.davor.dircbot;

import static org.junit.Assert.*;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

public class ConfigurationManagerTest {
	ConfigurationManager conf;
	BufferedWriter bw;
	
	@Before
	public void setUp() throws Exception {
		bw = new BufferedWriter(new FileWriter(new File("conftest.conf")));
		bw.write("TEST=yes\n");
		bw.close();
		conf = new ConfigurationManager("conftest.conf");
	}

	@After
	public void tearDown() throws Exception {
		conf = null;
		bw = null;
		File f = new File("conftest.conf");
		f.delete();
	}
	
	@Test
	public void testGetOneKey() {
		assertEquals("yes", conf.getKey("TEST"));
	}
	
	@Test
	public void testStandardFile() throws Exception {
		BufferedWriter bw2 = new BufferedWriter(new FileWriter(new File("settings.conf")));
		bw2.write("TESTDEFAULT=yes\n");
		bw2.close();
		
		ConfigurationManager conf2=null;
		try {
			conf2 = new ConfigurationManager();
		} catch (IOException e) {
			fail("Unable to load default file.");
		}
		
		assertEquals("yes", conf2.getKey("TESTDEFAULT"));
		File f = new File("settings.conf");
		f.delete();
	}



	@Test
	public void gtestGetMultipleKeys() throws IOException {
		bw = new BufferedWriter(new FileWriter(new File("conftest.conf")));
		bw.write("TEST=yes\n");
		bw.write("KEY2=no\n");
		bw.write("KEY3=maby");
		bw.close();
		conf = new ConfigurationManager("conftest.conf");
		
		assertEquals("maby", conf.getKey("KEY3"));
		assertEquals("yes", conf.getKey("TEST"));
		assertEquals("no", conf.getKey("KEY2"));
	}
}
