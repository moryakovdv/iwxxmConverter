/**
 * Copyright (C) 2018 Dmitry Moryakov, Main aeronautical meteorological center, Moscow, Russia
 * moryakovdv[at]gmail[dot]com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


package common;

import java.util.TreeMap;

import com.sun.xml.bind.marshaller.NamespacePrefixMapper;

/**
 * This class extends {@link NamespacePrefixMapper} and provides correct namespace prefix mapping
 * Somehow namespace from xlink ignore attribute 
 * xmlns={@XmlNs(namespaceURI = "http://some.url", prefix = "prfx" )}
 * in the package-info classes.
 * 
 * @author moryakov
 * */
@SuppressWarnings("restriction")
public class NamespaceMapper extends NamespacePrefixMapper {

  private TreeMap<String, String> prefixMapping = new TreeMap<String, String>();
  
  public NamespaceMapper() {
	populateMappings();
  }
  
  @Override
  public String getPreferredPrefix(String namespaceUri, String suggestion, boolean requirePrefix) {
	  String mapPrefix = prefixMapping.get(namespaceUri);
	   
      return mapPrefix!=null?mapPrefix:suggestion;
  }

  @Override
  public String[] getPreDeclaredNamespaceUris() {
      return new String[] {};
  }
  
  /**Insert namespaces and their prefixes to a storage
   * TODO: Think about reading them from config file */
  private void populateMappings() {
	  prefixMapping.put("http://www.w3.org/1999/xlink", "xlink");
	  prefixMapping.put("http://def.wmo.int/opm/2013", "opm");
  }

}