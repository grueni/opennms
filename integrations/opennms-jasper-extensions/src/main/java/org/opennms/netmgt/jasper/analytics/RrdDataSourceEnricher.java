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

import java.util.List;
import java.util.ServiceLoader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRewindableDataSource;

/**
 * Allows an RRD data source to be enriched by analytics modules.
 * 
 * The list of modules to run, and their options are set with
 * additional commands in the query string. These commands are
 * removed from the query string before being passed to the actual
 * data source.
 * 
 * The commands are of the form:
 *   ANALYTICS:moduleName=columnNameOrPrefix:sourceColumn(:otherOptions)
 * where:
 *   - moduleName is a unique name for the module
 *   - columnNameOrPrefix identifies the name of the column, or the prefix of the
 *   column name if there are multiple where the additional values will be stored
 *   - sourceColumn identifies the name of the column where the source values are
 *   stored. This will typically be the name used in the XPORT statement of the
 *   RRD query.
 *   - otherOptions are optional and specific to the module in question
 *
 * Once the data source has been loaded, the modules are invoked
 * in order to successively enrich it.
 *
 * The modules are loaded are loaded at run-time using the
 * ServiceLoader paradigm.
 *
 * @author jwhite
 */
public class RrdDataSourceEnricher {
    private final String m_originalQueryString;
    private final String m_rrdQueryString;
    private final Pattern m_queryStringPattern = Pattern.compile(
            AnalyticsCommand.CMD_IN_RRD_QUERY_STRING + 
            ":([\\w]+)=([\\w]+):([^\\s]+)");
    private final List<AnalyticsCommand> m_analyticsCommands = Lists.newArrayList();
    private static final ServiceLoader<AnalyticsModule> m_analyticsModules =
            ServiceLoader.load(AnalyticsModule.class);

    public RrdDataSourceEnricher(String queryString) {
        m_originalQueryString = queryString;
        m_rrdQueryString = parseQs();
    }

    /**
     * Parses the analytics commands out of the query string
     */
    private String parseQs() {
        Matcher m = m_queryStringPattern.matcher(m_originalQueryString);

        // Build commands with all of the matches
        while(m.find()) {
            AnalyticsCommand cmd = new AnalyticsCommand(
                    m.group(1), m.group(2), m.group(3).split(":"));
            m_analyticsCommands.add(cmd);
        }

        // Remove all of our matches/commands from the query string
        return m.replaceAll("").trim();
    }

    /**
     * Returns the processed query string suitable for passing
     * to the RRD data source.
     */
    public String getRrdQueryString() {
        return m_rrdQueryString;
    }

    /**
     * Enriches the given data source by successively applying
     * all of the analytics commands.
     */
    public JRRewindableDataSource enrich(JRRewindableDataSource ds) throws JRException {
        JRRewindableDataSource enrichedDs = ds;
        for (AnalyticsCommand command : m_analyticsCommands) {
            Enricher enricher = getEnricher(command);
            if (enricher == null) {
                throw new JRException("No analytics module found for " + command);
            }

            enrichedDs = enricher.enrich(enrichedDs);
        }
        return enrichedDs;
    }

    /**
     * Retrieves an Enricher that supports the given analytics command
     *
     * @return
     *   null if no suitable Enricher was found
     * @throws JRException
     */
    private Enricher getEnricher(AnalyticsCommand command) throws JRException {
        Enricher enricher = null;
        for (AnalyticsModule module : m_analyticsModules) {
            enricher = module.getEnricher(command);
            if (enricher != null) {
                return enricher;
            }
        }
        return null;
    }

    /**
     * Used to testing
     */
    protected List<AnalyticsCommand> getAnalyticsCommands() {
        return m_analyticsCommands;
    }
}
