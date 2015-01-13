package org.opennms.netmgt.jasper.analytics;

/**
 * Used to perform a particular type of enrichment on the data source.
 *
 * @see {@link org.opennms.netmgt.jasper.analytics.RrdDataSourceEnricher}
 * @author jwhite
 */
public interface AnalyticsModule {
    public Enricher getEnricher(AnalyticsCommand cmd); 
}
