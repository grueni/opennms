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
