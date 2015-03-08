package com.thinkaurelius.faunus.formats;

import com.thinkaurelius.faunus.mapreduce.FaunusCompiler;

/**
 * If an Input- or OutputFormat requires some pre-post processing, then a MapReduceFormat can be implemented.
 * For InputFormats, the MapReduce jobs are prepended to the job pipeline and the final job must yield a FaunusVertex stream.
 * For OutputFormats, the MapReduce jobs are appended to the job pipeline and the first job must consume a FaunusVertex stream.
 *
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public interface MapReduceFormat {

    public void addMapReduceJobs(final FaunusCompiler compiler);
}
