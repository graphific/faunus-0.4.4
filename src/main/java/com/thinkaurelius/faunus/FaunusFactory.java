package com.thinkaurelius.faunus;

import org.apache.hadoop.conf.Configuration;

import java.io.FileInputStream;
import java.util.Map;
import java.util.Properties;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class FaunusFactory {

    public static FaunusGraph open(final String propertiesFile, final Configuration configuration) throws Exception {
        Properties properties = new Properties();
        properties.load(new FileInputStream(propertiesFile));
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            configuration.set(entry.getKey().toString(), entry.getValue().toString());
        }
        return new FaunusGraph(configuration);
    }

    public static FaunusGraph open(final String propertiesFile) throws Exception {
        return FaunusFactory.open(propertiesFile, new Configuration());
    }
}
