package org.opennms.netmgt.jasper.analytics;

import net.sf.jasperreports.engine.JRRewindableDataSource;

public class NoOpAnalyticsModule implements AnalyticsModule {
    @Override
    public Enricher getEnricher(AnalyticsCommand cmd) {
        if ("NoOp".equalsIgnoreCase(cmd.getModule())) {
            return new Enricher() {
                @Override
                public JRRewindableDataSource enrich(JRRewindableDataSource ds) {
                    return ds;
                }
            };
        };
        return null;
    }
}
