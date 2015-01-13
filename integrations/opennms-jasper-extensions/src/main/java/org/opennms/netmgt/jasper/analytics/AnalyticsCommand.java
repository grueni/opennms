/*******************************************************************************
 * This file is part of OpenNMS(R).
 *
 * Copyright (C) 2010-2015 The OpenNMS Group, Inc.
 * OpenNMS(R) is Copyright (C) 1999-2015 The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is a registered trademark of The OpenNMS Group, Inc.
 *
 * OpenNMS(R) is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License,
 * or (at your option) any later version.
 *
 * OpenNMS(R) is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with OpenNMS(R).  If not, see:
 *      http://www.gnu.org/licenses/
 *
 * For more information contact:
 *     OpenNMS(R) Licensing <license@opennms.org>
 *     http://www.opennms.org/
 *     http://www.opennms.com/
 *******************************************************************************/

package org.opennms.netmgt.jasper.analytics;

/**
 * Used to store the options of an ANALYTICS command
 * that has been parsed on the RRD query string.
 *
 * @see {@link org.opennms.netmgt.jasper.analytics.RrdDataSourceEnricher}
 * @author jwhite
 */
public class AnalyticsCommand {
    static final String CMD_IN_RRD_QUERY_STRING = "ANALYTICS";
    private final String m_module;
    private final  String m_columnNameOrPrefix;
    private final  String[] m_arguments;

    public AnalyticsCommand(String module, String columnNameOrPrefix, String[] arguments) {
        m_module = module;
        m_columnNameOrPrefix = columnNameOrPrefix;
        m_arguments = arguments;
    }

    public String getModule() {
        return m_module;
    }

    public String getColumnNameOrPrefix() {
        return m_columnNameOrPrefix;
    }

    public String[] getArguments() {
        return m_arguments;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(CMD_IN_RRD_QUERY_STRING);
        sb.append(":");
        sb.append(m_module);
        sb.append("=");
        sb.append(m_columnNameOrPrefix);
        for (String arg : m_arguments) {
            sb.append(":");
            sb.append(arg);
        }
        return sb.toString();
    }
}
