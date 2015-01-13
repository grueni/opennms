package org.opennms.netmgt.jasper.analytics;

import net.sf.jasperreports.engine.JRRewindableDataSource;

/**
 * Enriches the data source by adding one or more columns and/or
 * modifying the existing values.
 *
 * @author jwhite
 */
public interface Enricher {
    public JRRewindableDataSource enrich(JRRewindableDataSource ds);
}
