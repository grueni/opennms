//
// This file is part of the OpenNMS(R) Application.
//
// OpenNMS(R) is Copyright (C) 2006-2009 The OpenNMS Group, Inc.  All rights reserved.
// OpenNMS(R) is a derivative work, containing both original code, included code and modified
// code that was published under the GNU General Public License. Copyrights for modified
// and included code are below.
//
// OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
//
// Modifications:
//
// 2009 Jan 28: Add display-string groping and dynamic-length substrings.
//              Enhancement bug #2997 - jeffg@opennms.org
// 2008 May 09: Make the sub index functionality look like a function. - dj@opennms.org
// 2008 May 09: Add support for sub-indexes in the reosurce label as well as
//              conversion from dotted integer strings to hex.  Enhancement bug
//              #2467. - dj@opennms.org
// 2007 Apr 05: Remove getRelativePathForAttribute and move attribute loading to
//              ResourceTypeUtils.getAttributesAtRelativePath. - dj@opennms.org
//
// Original code base Copyright (C) 1999-2001 Oculan Corp.  All rights reserved.
//
// This program is free software; you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation; either version 2 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA 02111-1307, USA.
//
// For more information contact:
//      OpenNMS Licensing       <license@opennms.org>
//      http://www.opennms.org/
//      http://www.opennms.com/
//
package org.opennms.netmgt.dao.support;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.opennms.core.utils.LazySet;
import org.opennms.core.utils.PropertiesUtils;
import org.opennms.core.utils.PropertiesUtils.SymbolTable;
import org.opennms.netmgt.config.StorageStrategy;
import org.opennms.netmgt.dao.ResourceDao;
import org.opennms.netmgt.model.ExternalValueAttribute;
import org.opennms.netmgt.model.OnmsAttribute;
import org.opennms.netmgt.model.OnmsResource;
import org.opennms.netmgt.model.OnmsResourceType;
import org.opennms.netmgt.model.StringPropertyAttribute;
import org.springframework.orm.ObjectRetrievalFailureException;

/**
 * <p>GenericIndexResourceType class.</p>
 *
 * @author ranger
 * @version $Id: $
 */
public class GenericIndexResourceType implements OnmsResourceType {
    private static final Pattern SUB_INDEX_PATTERN = Pattern.compile("^subIndex\\((.*)\\)$");
    private static final Pattern SUB_INDEX_ARGUMENTS_PATTERN = Pattern.compile("^(-?\\d+|n)(?:,\\s*(\\d+|n))?$");
    private static final Pattern HEX_PATTERN = Pattern.compile("^hex\\((.*)\\)$");
    private static final Pattern STRING_PATTERN = Pattern.compile("^string\\((.*)\\)$");

    private String m_name;
    private String m_label;
    private String m_resourceLabelExpression;
    private ResourceDao m_resourceDao;
    private StorageStrategy m_storageStrategy;

    /**
     * <p>Constructor for GenericIndexResourceType.</p>
     *
     * @param resourceDao a {@link org.opennms.netmgt.dao.ResourceDao} object.
     * @param name a {@link java.lang.String} object.
     * @param label a {@link java.lang.String} object.
     * @param resourceLabelExpression a {@link java.lang.String} object.
     * @param storageStrategy a {@link org.opennms.netmgt.config.StorageStrategy} object.
     */
    public GenericIndexResourceType(ResourceDao resourceDao, String name, String label, String resourceLabelExpression, StorageStrategy storageStrategy) {
        m_resourceDao = resourceDao;
        m_name = name;
        m_label = label;
        m_resourceLabelExpression = resourceLabelExpression;
        m_storageStrategy = storageStrategy;
    }
    
    /**
     * <p>getName</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getName() {
        return m_name;
    }
    
    /**
     * <p>getLabel</p>
     *
     * @return a {@link java.lang.String} object.
     */
    public String getLabel() {
        return m_label;
    }
    
    /**
     * <p>getStorageStrategy</p>
     *
     * @return a {@link org.opennms.netmgt.config.StorageStrategy} object.
     */
    public StorageStrategy getStorageStrategy() {
        return m_storageStrategy;
    }
    
    /** {@inheritDoc} */
    public boolean isResourceTypeOnNode(int nodeId) {
      return getResourceTypeDirectory(nodeId, false).isDirectory();
    }
    
    private File getResourceTypeDirectory(int nodeId, boolean verify) {
        File snmp = new File(m_resourceDao.getRrdDirectory(verify), DefaultResourceDao.SNMP_DIRECTORY);
        
        File node = new File(snmp, Integer.toString(nodeId));
        if (verify && !node.isDirectory()) {
            throw new ObjectRetrievalFailureException(File.class, "No node directory exists for node " + nodeId + ": " + node);
        }

        File generic = new File(node, getName());
        if (verify && !generic.isDirectory()) {
            throw new ObjectRetrievalFailureException(File.class, "No node directory exists for generic index " + getName() + ": " + generic);
        }

        return generic;
    }
    
    /** {@inheritDoc} */
    public List<OnmsResource> getResourcesForNode(int nodeId) {
        ArrayList<OnmsResource> resources = new ArrayList<OnmsResource>();

        List<String> indexes = getQueryableIndexesForNode(nodeId);
        for (String index : indexes) {
            resources.add(getResourceByNodeAndIndex(nodeId, index));
        }
        return OnmsResource.sortIntoResourceList(resources);
    }
    
    /**
     * <p>getQueryableIndexesForNode</p>
     *
     * @param nodeId a int.
     * @return a {@link java.util.List} object.
     */
    public List<String> getQueryableIndexesForNode(int nodeId) {
        File nodeDir = getResourceTypeDirectory(nodeId, true);
        
        List<String> indexes = new LinkedList<String>();
        
        File[] indexDirs =
            nodeDir.listFiles(RrdFileConstants.INTERFACE_DIRECTORY_FILTER);

        if (indexDirs == null) {
            return indexes;
        }
        
        for (File indexDir : indexDirs) {
            indexes.add(indexDir.getName());
        }
        
        return indexes;
    }

    
    /**
     * <p>getResourceByNodeAndIndex</p>
     *
     * @param nodeId a int.
     * @param index a {@link java.lang.String} object.
     * @return a {@link org.opennms.netmgt.model.OnmsResource} object.
     */
    public OnmsResource getResourceByNodeAndIndex(int nodeId, final String index) {
        final Set<OnmsAttribute> set = new LazySet<OnmsAttribute>(new AttributeLoader(nodeId, index));
        
        String label;
        if (m_resourceLabelExpression == null) {
            label = index;
        } else {
            SymbolTable symbolTable = new SymbolTable() {
                private int lastN;
                private boolean lastNSet = false;
                
                public String getSymbolValue(String symbol) {
                    if (symbol.equals("index")) {
                        return index;
                    }
 
                    Matcher subIndexMatcher = SUB_INDEX_PATTERN.matcher(symbol);
                    if (subIndexMatcher.matches()) {
                        Matcher subIndexArgumentsMatcher = SUB_INDEX_ARGUMENTS_PATTERN.matcher(subIndexMatcher.group(1));
                        if (!subIndexArgumentsMatcher.matches()) {
                            // Invalid arguments
                            return null;
                        }
                        
                        List<String> indexElements = tokenizeIndex(index);
                        
                        int start;
                        int offset;
                        if ("n".equals(subIndexArgumentsMatcher.group(1)) && lastNSet) {
                            start = lastN;
                            lastNSet = false;
                        } else if ("n".equals(subIndexArgumentsMatcher.group(1))) {
                            // Invalid use of "n" when lastN is not set
                            return null;
                        } else {
                            offset = Integer.parseInt(subIndexArgumentsMatcher.group(1));
                            if (offset < 0) {
                                start = indexElements.size() + offset;
                            } else {
                                start = offset;
                            }
                        }

                        int end;
                        if ("n".equals(subIndexArgumentsMatcher.group(2))) {
                            end = start + Integer.parseInt(indexElements.get(start)) + 1;
                            start++;
                            lastN = end;
                            lastNSet = true;
                        } else {
                            if (subIndexArgumentsMatcher.group(2) == null) {
                                end = indexElements.size();
                            } else {                            
                                end = start + Integer.parseInt(subIndexArgumentsMatcher.group(2));
                            }
                        }
                        
                        if (start < 0 || start >= indexElements.size()) {
                            // Bogus index start
                            return null;
                        }
                        
                        if (end < 0 || end > indexElements.size()) {
                            // Bogus index end
                            return null;
                        }

                        StringBuffer indexSubString = new StringBuffer();
                        for (int i = start; i < end; i++) {
                            if (indexSubString.length() != 0) {
                                indexSubString.append(".");
                            }
                            
                            indexSubString.append(indexElements.get(i));
                        }
                        
                        return indexSubString.toString();
                    }
                    
                    Matcher hexMatcher = HEX_PATTERN.matcher(symbol);
                    if (hexMatcher.matches()) {
                        String subSymbol = getSymbolValue(hexMatcher.group(1));
                        List<String> indexElements = tokenizeIndex(subSymbol);
                        
                        StringBuffer hexString = new StringBuffer();
                        for (String indexElement : indexElements) {
                            if (hexString.length() > 0) {
                                hexString.append(":");
                            }
                            try {
                                hexString.append(String.format("%02X", Integer.parseInt(indexElement)));
                            } catch (NumberFormatException e) {
                                return null;
                            }
                        }
                        
                        return hexString.toString();
                    }
                    
                    Matcher stringMatcher = STRING_PATTERN.matcher(symbol);
                    if (stringMatcher.matches()) {
                        String subSymbol = getSymbolValue(stringMatcher.group(1));
                        List<String> indexElements = tokenizeIndex(subSymbol);
                        
                        StringBuffer stringString = new StringBuffer();
                        for (String indexElement : indexElements) {
                            stringString.append(String.format("%c", Integer.parseInt(indexElement)));
                        }
                        
                        return stringString.toString();
                    }
                    
                    for (OnmsAttribute attr : set) {
                        if (symbol.equals(attr.getName())) {
                            if (StringPropertyAttribute.class.isAssignableFrom(attr.getClass())) {
                                StringPropertyAttribute stringAttr = (StringPropertyAttribute) attr;
                                return stringAttr.getValue();
                            }
                            if (ExternalValueAttribute.class.isAssignableFrom(attr.getClass())) {
                                ExternalValueAttribute extAttr = (ExternalValueAttribute) attr;
                                return extAttr.getValue();
                            }
                        }
                    }
                    
                    return null;
                }

                private List<String> tokenizeIndex(final String index) {
                    List<String> indexElements = new ArrayList<String>();
                    StringTokenizer t = new StringTokenizer(index, ".");
                    while (t.hasMoreTokens()) {
                        indexElements.add(t.nextToken());
                    }
                    return indexElements;
                }
            };
            
            label = PropertiesUtils.substitute(m_resourceLabelExpression, symbolTable);
        }

        return new OnmsResource(index, label, this, set);
    }


    public class AttributeLoader implements LazySet.Loader<OnmsAttribute> {
    
        private int m_nodeId;
        private String m_index;

        public AttributeLoader(int nodeId, String index) {
            m_nodeId = nodeId;
            m_index = index;
        }

        public Set<OnmsAttribute> load() {
            return ResourceTypeUtils.getAttributesAtRelativePath(m_resourceDao.getRrdDirectory(), getRelativePathForResource(m_nodeId, m_index)); 
        }
    }

    /**
     * <p>getRelativePathForResource</p>
     *
     * @param nodeId a int.
     * @param index a {@link java.lang.String} object.
     * @return a {@link java.lang.String} object.
     */
    public String getRelativePathForResource(int nodeId, String index) {
        return DefaultResourceDao.SNMP_DIRECTORY
            + File.separator + Integer.toString(nodeId)
            + File.separator + getName()
            + File.separator + index;
    }
    
    /**
     * {@inheritDoc}
     *
     * This resource type is never available for domains.
     * Only the interface resource type is available for domains.
     */
    public boolean isResourceTypeOnDomain(String domain) {
        return false;
    }
    

    /** {@inheritDoc} */
    @SuppressWarnings("unchecked")
    public List<OnmsResource> getResourcesForDomain(String domain) {
        return Collections.EMPTY_LIST;
    }

    /** {@inheritDoc} */
    public String getLinkForResource(OnmsResource resource) {
        return null;
    }
}
