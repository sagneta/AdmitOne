package com.admitone.utils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Collection;
import java.util.UUID;

import javax.naming.InitialContext;
import javax.validation.constraints.NotNull;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.uuid.EthernetAddress;
import com.fasterxml.uuid.Generators;
import com.fasterxml.uuid.impl.TimeBasedGenerator;
import com.google.common.base.CaseFormat;
import com.google.common.io.Files;

import lombok.val;
import lombok.extern.slf4j.Slf4j;



/** <p> Miscellaneous convenience methods.  </p>

 *
 * <a href="mailto:sagneta@gmail.com">Steve 'Crash' Agneta</a>
 *
 */

@Slf4j
final public class MiscUtils {
    public static final String DEFAULT_LIMIT  = "1000"; // arbitrary.
    public static final String DEFAULT_OFFSET = "0"; // Begining.
    public static final int iDEFAULT_LIMIT    = 1000; // arbitrary.
    public static final int iDEFAULT_OFFSET   = 0; // Begining.
    

    

    private static TimeBasedGenerator uuidGenerator;
    static {
        // need to pass Ethernet address; can either use real one (shown here)
        final EthernetAddress nic = EthernetAddress.fromInterface();
        // or bogus which would be gotten with: EthernetAddress.constructMulticastAddress()
        uuidGenerator = Generators.timeBasedGenerator(nic);
        // also: we don't specify synchronizer, getting an intra-JVM syncer; there is
        // also external file-locking-based synchronizer if multiple JVMs run JUG
        // UUID uuid = uuidGenerator.generate();
    }


    /**
	 * Given a JNDI path to the T resource this method will return 
     * a reference to the session bean or null if none found.
	 * 
	 * @param resource
	 * @return
	 */
    @SuppressWarnings("unchecked")
    public static <T> T obtainService(final String resource, Class<T> c) {
		try {
            return (T) new InitialContext().lookup(resource);
		}
		catch(final Exception e) {
            return null;
		}
	}

    
    /**
     *  <code>fromCamelCaseToLowerHyphen</code> method will convert
     *
     * CamelCase to camel-case (lower hyphen). Works only for ASCII equivalents which is 
     * good enough for keywords and internal strings etcetera
     *
     * @param in a <code>String</code> value
     * @return a <code>String</code> value
     */
    
    public static String fromCamelCaseToLowerHyphen(@NotNull(message="in must not be null.") final String in){
        return CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_HYPHEN, in);
    }

    public static int defaultIfNotNumeric(final String value, final int defaultValue) {
        return (StringUtils.isNumeric(value)) ? Integer.parseInt(value) : defaultValue;
    }

    /**
     *  <code>isNotNullOrBlank</code> method returns true if the string passed as an argument
     *  is not null or blank. 
     *
     * @param string a <code>String</code> value
     * @return a <code>boolean</code> value
     */
    public static boolean isNotNullOrBlank(final String string)
    {
        //Checks if a CharSequence is whitespace, empty ("") or null.
        return !StringUtils.isBlank(string);
    }

    public static boolean isNullOrBlank(final String string)
    {
        //Checks if a CharSequence is whitespace, empty ("") or null.
        return StringUtils.isBlank(string);
    }

    /**
     *  <code>generateUUID</code> method will generate a random UUID.
     *
     * @return a <code>String</code> value
     */
    public static String generateUUID() {
        //return UUID.randomUUID().toString();
        return uuidGenerator.generate().toString();
    }

    public static UUID generateUUIDObject() {
        //return UUID.randomUUID().toString();
        return uuidGenerator.generate();
    }

    public static UUID generateUUIDObject(final String uuid) {
        return UUID.fromString(uuid);
    }
    
    /**
     * <code>toArray</code> method will convert the collection of T to a corresponding array of T.
     * This is a convenience method which is somewhat simpler and hides some of the details.
     * The syntax of Java never makes this syntax easy. This is the best I can accomplish.
     *
     * example usage: 
            Collection&lt;User&gt; users = identityService.getAllMembers(group);
            Users[] userArray = MiscUtils.&lt;User&gt;toArray(users, User.class)
     * 
     *
     * @param <T> Type of class c returned.
     * @param collection <code>java.util.Collection&lt;T&gt;</code> value
     * @param c <code>Class&lt;T&gt;</code> value
     * @return T <code>T[]</code> value
     */

    @SuppressWarnings("unchecked")
    public static <T> T[] toArray(Collection<? extends T> collection, Class<T> c){
        return collection.toArray((T[])java.lang.reflect.Array.newInstance(c, collection.size()));
    }


    /**
     *  <code>isRunningUnderArquillian</code> method will return true if the system is 
     *  running beneath the Arquillian System testing framework. Some system components don't
     *  work in some configurations, for various reasons, with Arquillian thus we need to know.
     *
     * @return a <code>boolean</code> value TRUE if system is running under Arquillian integration test framework.
     */
    
    public static boolean isRunningUnderArquillian() {
        return System.getProperty("ARQUILLIAN") != null;
    }
    
	/**
	 * The file resource must be encoded in UTF-8.   
	 * 
	 * @param myClass Class associated with the resource (resource just have to in the same package as this class, I think).
	 * @param resourceName filename of the resource.
	 * @return Content of the file read in string.
	 * @throws IOException If any IO error occurs reading contents from Resource. Such as the resource not found.
	 */
	public static String readContentsFromResource(@SuppressWarnings("rawtypes") Class myClass, String resourceName) throws IOException {
		val rsc = myClass.getResource(resourceName);
		val path = rsc.getPath();
		val file = new File(path);
		return Files.toString(file, Charset.forName("UTF-8"));
	}
	
 
	/**
	 * Get a MessageDigest based on the algorithm passed.
	 * 
	 * @return the MessageDigest that conforms to MD5 hash.
	 */
	public static MessageDigest getMD5() {
    	MessageDigest digest = null;
    	try{
    		digest = MessageDigest.getInstance("MD5");
    	}
    	catch(NoSuchAlgorithmException e) {
    		log.error("", e);
    	}
    	return digest;
    }
	

	/**
	 * Encapsulates value in double quotes "value". 
     * Useful for cypher properties.
	 *
	 * @param value the value to quote.
	 * @return escaped String
	 */
    static public String doubleQuote(final String value){
        return "\"" + value + "\"";
    }

	/**
	 * Will escape every single and double quote within string s 
     * such that the string is Drools compatable
	 *
	 * @param s the string to escape.
	 * @return the escaped string
	 */
    static public String escapeSingleAndDoubleQuotes(final String s) {
        return s.replace("\"", "\\\"").replace("'", "\\'");
    }
    
}
