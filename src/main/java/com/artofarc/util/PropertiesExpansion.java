/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.artofarc.util;

import java.util.*;
import java.io.*;

/**
*  The tag ${} is used to introduce substitutable parameters so it
*  expands properties with ${} described in a property file; it is possible to
*  have different sections with the same names as:<br>
*
*  global_name1=z1 <br>
*  sec_name=v      <br>
*  x1=2            <br>
*  y1=0            <br>
*  [section_1]     <br>
*  x1=${x1}        <br>
*  [section2]      <br>
*  x1=zxc          <br>
*  [${sec_name}]   <br>
*  ...             <br>
*
*  @author  Serguei Eremenko, Andre Karalus
*  @version 1.1
*  @see https://www.techrepublic.com/article/enhance-persistent-application-properties-in-your-java-application/
*/
public class PropertiesExpansion extends Properties {
   /**
	 *
	 */
	private static final long serialVersionUID = -6596119768697292788L;

   /**
   *  Creates an empty property list with no default values.
   */
   public PropertiesExpansion(){ super();}
   /**
   *  Creates an empty property list with the specified defaults.
   *  @param def  the defaults.
   */
   public PropertiesExpansion(final Properties def){ super(def);}
   /**
   *  Searches for the property with the specified key in this property list
   *  and substitutes any ${} occurrence that may be in the key.
   *  @param  key the property key.
   *  @return the value in this property list with the specified key value or
   *          null if not found.
   */
   public String getProperty(final String key) {
      return replace(super.getProperty(replace(key)));
   }

   /**
    *  Searches for the properties with the specified key and his subelements
    *  in this property list and substitutes any ${} occurrence that may be
    *  in the key.
    *
    *  eg. you can do this in the property:
    *
    *  section.elements   = value1
    *  section.elements.0 = value2
    *  section.elements.1 = value3
    *  section.elements.y = value3
    *
    *  @param  key the property key.
    *  @return the array of Strings with key's name and its subelements
    */
    public String[] getProperties(final String key) {
       ArrayList<String> result = new ArrayList<String>();

       if(getProperty(key)!=null){
          result.add(getProperty(key));
       }

       Properties properties = getPropertiesForPrefix(key);
       Enumeration e = properties.keys();
       while(e.hasMoreElements()){
          String propKey = ((String)e.nextElement());
          result.add(properties.getProperty(propKey));
       }

       return result.toArray(new String[0]);
    }

   /**
    *  Searches for the property with the specified key in this property list
    *  and substitutes any ${} occurrence that may be in the key.
    *  @param  key the property key.
    *  @return the value in this property list with the specified key value or
    *          null if not found.
    */
    public String getProperty(final String key,final String defaultValue) {
       String s = getProperty(key);
       if (s == null) s = defaultValue;
       return replace(s);
    }


   /**
    *  Gets the Properties for the specified section.
    *  @param  section the property section.
    *  @return the PropertiesExpansion for the specified section.
    */
   public PropertiesExpansion getPropertiesFromSection(final String section) {
      return (PropertiesExpansion) get(replace(section));
   }

   /**
   *  Searches for the property within the specified section and with the
   *  specified key in this property list and substitutes any ${} occurrence
   *  that may be in the key.
   *  @param  section the property section.
   *  @param  key the property key.
   *  @return the value in this property list within the specified section and
   *          with the specified key value or null if not found.
   */
   public String getPropertyFromSection(final String section,final String key) {
      // fallback if no section is wanted
      if (section.equals("")) return getProperty(key);
      Object p = get(replace(section));
      if (p instanceof Properties)  {
          return replace(((Properties) p).getProperty(replace(key)));
      }
	  return null;
   }
   /**
   *  @param  section the property section.
   *  @param  key the property key.
   *  @param  def the default value.
   *  @return the value in this property list within the specified section and
   *          with the specified key value or this default value.
   */
   public String getPropertyFromSection(final String section,final String key,final String def) {
      String s = getPropertyFromSection(section,key);
      if (s == null) s = def;
      return replace(s);
   }
   /**
   *  Casts a found property to its integer value
   *  @param prop the Properties to be searched in
   *  @param key the property key
   *  @param def the default value
   *  @return the found value cast to integer or the default value
   *  @see #getProperty
   */
   public static int getInt(final Properties prop, final String key,final int def) {
      int out = def;
      try{ out = getInt(prop,key); }catch (RuntimeException e){}
      return out;
   }
   /**
   *  @see #getProperty
   */
   public static int getInt(final Properties prop,final String key) {
      String s = prop.getProperty(key);
      if (s == null) throw new NoSuchElementException("No key "+key+" found");
      return Integer.parseInt(s);
   }

   public static long getLong(final Properties prop, final String key,final long def) {
      long out = def;
      try{ out = getLong(prop,key); }catch (RuntimeException e){}
      return out;
   }

   public static long getLong(final Properties prop,final String key) {
      String s = prop.getProperty(key);
      if (s == null) throw new NoSuchElementException("No key "+key+" found");
      return Long.parseLong(s);
   }

   public static float getFloat(final Properties prop, final String key,final float def) {
      float out = def;
      try{ out = getFloat(prop,key); }catch (RuntimeException e){}
      return out;
   }

   public static float getFloat(final Properties prop,final String key) {
      String s = prop.getProperty(key);
      if (s == null) throw new NoSuchElementException("No key "+key+" found");
     return Float.parseFloat(s);
   }

   public static double getDouble(final Properties prop, final String key,final double def) {
      double out = def;
      try{ out = getDouble(prop,key); }catch (RuntimeException e){}
      return out;
   }

   public static double getDouble(final Properties prop,final String key) {
      String s = prop.getProperty(key);
      if (s == null) throw new NoSuchElementException("No key "+key+" found");
      return Double.parseDouble(s);
   }

   public static boolean getBoolean(final Properties prop, final String key,final boolean def) {
      boolean out = def;
      try{ out = getBoolean(prop,key); }catch (RuntimeException e){}
      return out;
   }

   public static boolean getBoolean(final Properties prop,final String key) {
      String s = prop.getProperty(key);
      if (s == null) throw new NoSuchElementException("No key "+key+" found");
      return Boolean.valueOf(s);
   }

   public int getInt(final String key,final int def) {
      int out = def;
      try{ out = getInt(key); }catch (RuntimeException e){}
      return out;
   }

   public int getInt(final String key) {
      String s = getProperty(key);
      if (s == null) throw new NoSuchElementException("No key "+key+" found");
      return Integer.parseInt(s);
   }

   public long getLong(final String key,final long def) {
     long out = def;
      try{ out = getLong(key); }catch (RuntimeException e){}
      return out;
   }

   public long getLong(final String key) {
      String s = getProperty(key);
      if (s == null) throw new NoSuchElementException("No key "+key+" found");
      return Long.parseLong(s);
   }

   public float getFloat(final String key,final float def) {
      float out = def;
      try{ out = getFloat(key); }catch (RuntimeException e){}
      return out;
   }

   public float getFloat(final String key) {
      String s = getProperty(key);
      if (s == null) throw new NoSuchElementException("No key "+key+" found");
      return Float.parseFloat(s);
   }

   public double getDouble(final String key,final double def) {
      double out = def;
      try{ out = getDouble(key); }catch (RuntimeException e){}
      return out;
   }

   public double getDouble(final String key) {
      String s = getProperty(key);
      if (s == null) throw new NoSuchElementException("No key "+key+" found");
      return Double.parseDouble(s);
   }

   public boolean getBoolean(final String key,final boolean def) {
      boolean out = def;
      try{ out = getBoolean(key); }catch (RuntimeException e){}
      return out;
   }

   public boolean getBoolean(final String key) {
      String s = getProperty(key);
      if (s == null) throw new NoSuchElementException("No key "+key+" found");
      return Boolean.valueOf(s);
   }
   /**
   *  @param  key the key to be placed into this property list.
   *  @param  value the value corresponding to key.
   *  @return the previous value of the specified key in this property list,
   *          or null if it did not have one.
   */
   public Object setProperty(final String key,final String value) {
      return super.setProperty(replace(key),replace(value));
   }
   /**
   *  @param  section the section where the key to be placed into this property
   *          list, the section is created if the property list did not have one.
   *  @param  key the key to be placed into this property list.
   *  @param  value the value corresponding to key.
   *  @return the previous value of the specified key in this property list,
   *          or null if it did not have one.
   */
   public Object setProperty(final String section,final String key,final String value) {
      Properties p = (Properties) get(replace(section));
      if (p == null){
         p = new PropertiesExpansion();
         put(replace(section),p);
      }
      return p.setProperty(key,replace(value));
   }
   /**
   *  @return an enumeration of the sections in this property list.
   */
   public synchronized Enumeration sections() {
      final int len = keySet().size();
      final String[] all = keySet().toArray(new String[len]);
      return new Enumeration () {
         public boolean hasMoreElements() {
            next = null;
           while (i < len){
               String key = all[i++];
               if (get(key) instanceof Properties){
                  next = key;
                  break;
               }else{
                  next = null;
               }
            }
            return next != null;
         }
         public Object nextElement() {
            if (next == null) throw new NoSuchElementException();
            return next;
         }
         private Object next = null;
         private int i = 0;
      };
   }
   /**
   *  @param  section the section name where to enumerate the keys from.
   *  @return an enumeration of the section keys in this property list or
   *          empty enumeration if there is no such section or the section
   *          does not contain any keys.
   */
   public synchronized Enumeration sectionKeys(final String section) {
      Properties p = (Properties) get(replace(section));
      Enumeration en = null;
      if (p == null){
         en = new Enumeration(){
            public boolean hasMoreElements(){ return false;}
            public Object nextElement(){
               throw new NoSuchElementException();
            }
         };
      }else{
         en = p.keys();
      }
      return en;
   }
   /**
   *  Overrides the Hashtable's put method and its use is strongly discouraged.
   *  @see java.util.Hashtable#put
   *  @see #setProperty
   */
   @Override
   public Object put(final Object key,final Object val) {
      if (key == null || val == null) throw new NullPointerException();
      Object o = null;
      if (key instanceof String && val instanceof String){
         String s = (String) key;
         if (((String) val).length() != 0) {
        	 s += ":" + val;
         }
         int i = s.indexOf(startSecSep);
         if (i == 0){
            curSec = replace(s.substring(1,s.indexOf(endSecSep,1)));
            Object p = get(curSec);
            if ((p == null || !(p instanceof Properties)) && curSec.length() > 0){
               PropertiesExpansion pe = new PropertiesExpansion();
               o = super.put(curSec,pe);
               if (p != null) {
                  pe.put(".", p);
               }
            }
         }else{
            Properties p = (Properties) get(curSec);
            if (p != null){
               o = p.put(key,val);
            }else{
               o = super.put(key,val);
            }
         }
      }else{
         o = super.put(key,val);
      }
      return o;
   }
   /**
   *  Reads a property list (key and element pairs) from the input stream.
   *  @param is the input stream.
   */
   public synchronized void load(final InputStream is) throws IOException {
      curSec = "";
      super.load(is);
      curSec = "";
      replaceAll(this);
   }
   /**
   *  Writes this property list (key and element pairs) in this Properties
   *  table to the output stream in a format suitable for loading into a
   *  Properties table using the load method.
   *  @param os an output stream.
   *  @param header a description of the property list.
   */
   public synchronized void store(final OutputStream os,final String header)
      throws IOException {
      Properties p = null;
      for (Enumeration en=keys();en.hasMoreElements();){
         String k = (String) en.nextElement();
        Object v = get(k);
         if (v instanceof String) {
            if (p == null) p = new Properties();
            p.put(k,v);
         }
      }
      if (p != null) p.store(os,header);
      for (Enumeration en=keys();en.hasMoreElements();){
        String k = (String) en.nextElement();
         Object v = get(k);
         if (v instanceof Properties) {
            byte[] b = (startSecSep+k+endSecSep+"\r\n").getBytes();
            os.write(b,0,b.length);
            os.flush();
            ((Properties)v).store(os,null);
         }
      }
   }
   /**
   *  @param  in the string where substitutable parameters ${} will be replaces
   *          with values contained in the hashtable of keys
   *  @param  keys the hashtable of keys
   *  @return the substituted string
   */
   public static String replace(final String in,final Hashtable keys) {
      if (in == null) return null;
      if (keys == null) throw new NullPointerException("Keys source is null");
      StringBuilder out = new StringBuilder();
      int i = 0, index;
      String key = null;
      while ((index = in.indexOf(startTag, i)) >= 0) {
         out.append (in.substring(i, index));
         index += 2;
         key = in.substring(index, in.indexOf(endTag, index));
         if (keys.containsKey (key) ) {
            out.append(keys.get(key));
         } else {
            out.append(startTag).append(key).append(endTag);
         }
         i = index + 1 + key.length();
      }
      return i == 0 ? in : out.append(in.substring(i)).toString();
   }
   /**
   *  @param in the key which string value to be replaced with values from
   *         this property list
   */
   protected String replace(final String in) {
      if (in == null) return null;
      StringBuilder out = new StringBuilder();
      int i = 0, index;
      while ((index = in.indexOf(startTag, i)) >= 0) {
         out.append(in.substring(i, index));
         index += 2;
         String key = in.substring(index, in.indexOf(endTag, index));
         String val = super.getProperty(key);
         if (val != null) {
            // be careful here
            out.append(replace(val));
         } else {
            out.append(startTag).append(key).append(endTag);
         }
         i = index + 1 + key.length();
      }
      return i == 0 ? in : out.append(in.substring(i)).toString();
   }

   /**
   *  Replaces all occurrences of the substitution tag in the given Properties.
   */
   protected void replaceAll(final Properties p) {
      for (Enumeration en=p.keys();en.hasMoreElements();){
         String k = (String) en.nextElement();
         Object v = p.get(k);
         if (v instanceof String) {
            String nv = replace((String)v);
            p.put(k,nv);
         }
         if (v instanceof Properties) replaceAll((Properties) v);
      }
   }

   /**
    * <p>returns all Properties with a given prefix</p>
    * <b>Note: the returned Properties contain the Properties without the prefix!<br>
    * for ex. : if prefix is "testMessagingSessionInstance.properties" the
    * original entry "testMessagingSessionInstance.properties.TestProperty2=value Of Property 2"
    * is transformed to "TestProperty2=value Of Property 2"
    * </b>
    * @param prefix nomen es omen for ex. : "testMessagingSessionInstance.properties"
    * @return Properties without prefix
    */
   public Properties getPropertiesForPrefix(final String prefix) {
      String trimmedSectionName = ( prefix == null ? "" : prefix.trim() );
      if ( trimmedSectionName.length() < 1 ) {
         throw new IllegalArgumentException( "Please provide sectionName!" );
      }
      trimmedSectionName += ".";
      Enumeration allPropertiesEnumeration = propertyNames();
      Properties propertiesForPrefix = new Properties();
      while ( allPropertiesEnumeration.hasMoreElements() ) {
         String propertyName = (String) allPropertiesEnumeration.nextElement();
         if ( propertyName.startsWith( trimmedSectionName ) ) {
            String strippedPropertyName = propertyName.substring( trimmedSectionName.length() );
            propertiesForPrefix.setProperty( strippedPropertyName, getProperty( propertyName ) );
         }
      }
      return propertiesForPrefix;
   }

   final protected static char    startSecSep ='[';
   final protected static char    endSecSep   = ']';
   final protected static String  startTag    = "${";
   final protected static char    endTag      = '}';

   private String           curSec      = "";

}
