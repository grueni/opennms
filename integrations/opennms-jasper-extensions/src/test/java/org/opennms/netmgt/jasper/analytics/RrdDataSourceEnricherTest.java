package org.opennms.netmgt.jasper.analytics;

import static org.junit.Assert.assertEquals;

import java.util.Date;
import java.util.List;

import net.sf.jasperreports.data.cache.ColumnValues;
import net.sf.jasperreports.data.cache.ColumnValuesDataSource;
import net.sf.jasperreports.data.cache.DoubleArrayValues;
import net.sf.jasperreports.data.cache.ObjectArrayValues;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRRewindableDataSource;

import org.junit.Test;

public class RrdDataSourceEnricherTest {
    @Test
    public void canParseQueryString() {
        final String expectedQs = "--start 0 --end 1 "
                + "DEF:xx=/path/to/my/file.jrb:test:AVERAGE"
                + "XPORT:xx:Values";   
        final String qs = expectedQs + " "
                + "ANALYTICS:fn1=A:Values "
                + "ANALYTICS:fn2=B:Values "
                + "ANALYTICS:fn3=C:Values:1:2:3:4";

        RrdDataSourceEnricher dse = new RrdDataSourceEnricher(qs);
        
        // All of the analytics commands should be removed from the qs
        assertEquals(expectedQs, dse.getRrdQueryString());

        // Verify the resulting commands
        List<AnalyticsCommand> commands = dse.getAnalyticsCommands();

        AnalyticsCommand fn1 = commands.get(0);
        assertEquals("fn1", fn1.getModule());
        assertEquals("A", fn1.getColumnNameOrPrefix());
        assertEquals(1, fn1.getArguments().length);
        assertEquals("Values", fn1.getArguments()[0]);

        AnalyticsCommand fn3 = commands.get(2);
        assertEquals("fn3", fn3.getModule());
        assertEquals("C", fn3.getColumnNameOrPrefix());
        assertEquals(5, fn3.getArguments().length);
        assertEquals("Values", fn3.getArguments()[0]);
        assertEquals("4", fn3.getArguments()[4]);
    }

    @Test(expected = JRException.class)
    public void failsWithUnknownModule() throws JRException {
        final String qs = "ANALYTICS:shouldNotExist=NA:Series";
        RrdDataSourceEnricher dse = new RrdDataSourceEnricher(qs);
        dse.enrich(new JREmptyDataSource());
    }

    @Test
    public void canEnrich() throws JRException {
        final String qs = "ANALYTICS:NOOP=NA:Y";
        RrdDataSourceEnricher dse = new RrdDataSourceEnricher(qs);

        // Build a data source
        ColumnValues timestamp = new ObjectArrayValues(
                new Object[] {new Date()}
        );

        ColumnValues y = new DoubleArrayValues(
                new double[] {0}
        );

        JRRewindableDataSource dsToEnrich = new ColumnValuesDataSource(
                new String[] {"Timestamp", "Y"},
                1,
                new ColumnValues[] {timestamp, y}
        );

        // Enrich the ds
        dse.enrich(dsToEnrich);
    }
}
