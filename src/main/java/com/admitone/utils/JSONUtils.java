package com.admitone.utils;

// Jackson Engine
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ser.impl.SimpleFilterProvider;

import lombok.val;

/** <p> Contains all JSON related utilities and abstracts the
    underlying JSON engine implementation </p>

 *
 * <a href="mailto:Stephen.Agneta@bjondinc.com">Steve 'Cräsh' Agneta</a>
 *
 */

public class JSONUtils {
    private final static ObjectMapper mapper;


    
    // Never change the ObjectMapper configuration outside of this static block. 
    static {
        // JAX RS ObjectMapper. Tell it that any field of any visibility (private, protected, whatever) is accessible.
        mapper = new ObjectMapper().setVisibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY);

        // Don't get confused by empty lists.
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // Null values are expected and are ok. Don't freak out about this either.
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // no more null-valued properties
        
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_CONTROL_CHARS, true);
        mapper.configure(JsonParser.Feature.ALLOW_BACKSLASH_ESCAPING_ANY_CHARACTER, true);

        // Don't fail when a custom property filter can't be resolved.
		SimpleFilterProvider filters = new SimpleFilterProvider().setFailOnUnknownId(false);
		mapper.setFilters(filters); // Use the deprecated method here until we resolve Jackson version issues.
    }

	/**
	 *  <code>toJSON</code> method will extract the OBJ passed as a parameter and return the JSON String
	 *  representation. 
	 * 
	 * NOTE: Written for performance thus NULL checks are not performed.
	 *
	 * @param obj an <code>Object</code> value
	 * @return a <code>String</code> value
	 * @exception IOException if an error occurs
	 */
	public static @NotNull(message="obj must not be null.") String toJSON(final Object obj) throws IOException{
        val writer = new StringWriter();
	    constructJackson().writeValue(writer, obj);
	
	    return writer.toString();
	}
	
	public static @NotNull(message="obj must not be null.") String toPrettyJSON(final Object obj) throws IOException{
	    return constructJackson().writerWithDefaultPrettyPrinter().writeValueAsString(obj);
	}
	
	public static String prettyPrint(String jsonString) throws IOException {
	    val obj = toMap(jsonString);
	    return toPrettyJSON(obj);
	}
	
	public static Map<String, Object> toMap(String json) throws IOException {
	    val mapper = new ObjectMapper();
	    return mapper.readValue(json, new TypeReference<HashMap<String, Object>>(){});
	}
	
	public static Map<Object, Object> toObjectMap(String json) throws IOException {
	    val mapper = new ObjectMapper();
	    return mapper.readValue(json, new TypeReference<HashMap<Object, Object>>(){});
	}
	
	/**
	 * fromJSON method will accept a JSON string and Class template
	 * and will deserialize the JSON to that Class.
	 *
	 * NOTE: Written for perfomance thus NULL checks are not performed.
     *
	 * @param <T> Type of the class object.	 *
	 * @param json
	 *            A valid JSON document.
	 * @param c
	 *            The class that matches the JSON document.
	 * @return an object of class c
	 * @throws IOException
	 *             IO operation failure.
	 */
	public static <T> T fromJSON(final String json, final Class<T> c) throws IOException {
	    return constructJackson().readValue(json, c);
	}

	public static <T> T fromJSON(final InputStream json, final Class<T> c) throws IOException {
	    return constructJackson().readValue(json, c);
	}

	/**
	 * constructJackson method will return the JaxRS ObjectMapper.
	 * It is configured to NOT fail on unknown properties as it gets mighty
	 * confused around empty List generic types in hibernate beans.
	 *
	 * @return an ObjectMapper value
	 */
	public static @NotNull(message="return must not be null.") ObjectMapper constructJackson() {
	    return mapper;
	}

	/**
	 * Will clone Object obj of class type 'c' by washing it through the JSON
	 * serialization engine.
	 *
	 * @param <T> Type of the class object.
	 * @param obj
	 *            Any object
	 * @param c
	 *            The class of the object
	 * @return Returns a type of that object cloned using JSON to marshall the
	 *         values.
	 *
	 * @throws IOException
	 *             Tossed if JSON marshalling fails.
	 */
    public static <T> T clone(final Object obj, final Class<T> c) throws IOException {
        return fromJSON(toJSON(obj), c);
	}
    
}

