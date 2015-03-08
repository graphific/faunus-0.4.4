package com.thinkaurelius.faunus.formats.edgelist;

import com.thinkaurelius.faunus.BaseTest;
import com.thinkaurelius.faunus.FaunusEdge;
import com.thinkaurelius.faunus.FaunusElement;
import com.thinkaurelius.faunus.FaunusVertex;
import com.tinkerpop.blueprints.Direction;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.NullWritable;
import org.apache.hadoop.mrunit.mapreduce.MapReduceDriver;

import java.io.IOException;
import java.util.Map;

/**
 * @author Marko A. Rodriguez (http://markorodriguez.com)
 */
public class EdgeListInputMapReduceTest extends BaseTest {

    MapReduceDriver<NullWritable, FaunusElement, LongWritable, FaunusVertex, NullWritable, FaunusVertex> mapReduceDriver;

    public void setUp() {
        mapReduceDriver = new MapReduceDriver<NullWritable, FaunusElement, LongWritable, FaunusVertex, NullWritable, FaunusVertex>();
        mapReduceDriver.setMapper(new EdgeListInputMapReduce.Map());
        mapReduceDriver.setCombiner(new EdgeListInputMapReduce.Combiner());
        mapReduceDriver.setReducer(new EdgeListInputMapReduce.Reduce());
    }

    public void testSimpleElementList() throws IOException {
        mapReduceDriver.addInput(NullWritable.get(), new FaunusVertex(1));
        mapReduceDriver.addInput(NullWritable.get(), new FaunusVertex(2));
        mapReduceDriver.addInput(NullWritable.get(), new FaunusEdge(1, 2, "knows"));
        Map<Long, FaunusVertex> results = BaseTest.run(mapReduceDriver);
        assertEquals(results.size(), 2);
        assertEquals(results.get(1l).getIdAsLong(), 1);
        assertEquals(results.get(2l).getIdAsLong(), 2);
        assertEquals(count(results.get(1l).getEdges(Direction.OUT)), 1);
        assertEquals(count(results.get(1l).getEdges(Direction.IN)), 0);
        assertEquals(count(results.get(2l).getEdges(Direction.OUT)), 0);
        assertEquals(count(results.get(2l).getEdges(Direction.IN)), 1);
    }

    public void testMultiVertexElementList() throws IOException {
        mapReduceDriver.addInput(NullWritable.get(), new FaunusVertex(1));
        mapReduceDriver.addInput(NullWritable.get(), new FaunusVertex(2));
        mapReduceDriver.addInput(NullWritable.get(), new FaunusVertex(1));
        mapReduceDriver.addInput(NullWritable.get(), new FaunusVertex(2));
        mapReduceDriver.addInput(NullWritable.get(), new FaunusVertex(2));
        mapReduceDriver.addInput(NullWritable.get(), new FaunusEdge(1, 2, "knows"));
        Map<Long, FaunusVertex> results = BaseTest.run(mapReduceDriver);
        assertEquals(results.size(), 2);
        assertEquals(results.get(1l).getIdAsLong(), 1);
        assertEquals(results.get(2l).getIdAsLong(), 2);
        assertEquals(count(results.get(1l).getEdges(Direction.OUT)), 1);
        assertEquals(count(results.get(1l).getEdges(Direction.IN)), 0);
        assertEquals(count(results.get(2l).getEdges(Direction.OUT)), 0);
        assertEquals(count(results.get(2l).getEdges(Direction.IN)), 1);
    }

    public void testMultiVertexMultiEdgeElementList() throws IOException {
        mapReduceDriver.addInput(NullWritable.get(), new FaunusVertex(1));
        mapReduceDriver.addInput(NullWritable.get(), new FaunusVertex(2));
        mapReduceDriver.addInput(NullWritable.get(), new FaunusVertex(3));
        mapReduceDriver.addInput(NullWritable.get(), new FaunusVertex(2));
        mapReduceDriver.addInput(NullWritable.get(), new FaunusVertex(3));
        mapReduceDriver.addInput(NullWritable.get(), new FaunusEdge(1, 2, "likes"));
        mapReduceDriver.addInput(NullWritable.get(), new FaunusEdge(2, 3, "hates"));
        mapReduceDriver.addInput(NullWritable.get(), new FaunusEdge(3, 1, "likes"));
        Map<Long, FaunusVertex> results = BaseTest.run(mapReduceDriver);
        assertEquals(results.size(), 3);
        assertEquals(results.get(1l).getIdAsLong(), 1);
        assertEquals(results.get(2l).getIdAsLong(), 2);
        assertEquals(results.get(3l).getIdAsLong(), 3);

        assertEquals(count(results.get(1l).getEdges(Direction.OUT)), 1);
        assertEquals(results.get(1l).getEdges(Direction.OUT).iterator().next().getVertex(Direction.OUT).getId(), new Long(1));
        assertEquals(results.get(1l).getEdges(Direction.OUT).iterator().next().getVertex(Direction.IN).getId(), new Long(2));
        assertEquals(count(results.get(1l).getEdges(Direction.OUT, "likes")), 1);
        assertEquals(count(results.get(1l).getEdges(Direction.IN)), 1);
        assertEquals(count(results.get(1l).getEdges(Direction.IN, "likes")), 1);
        assertEquals(results.get(1l).getEdges(Direction.IN).iterator().next().getVertex(Direction.OUT).getId(), new Long(3));
        assertEquals(results.get(1l).getEdges(Direction.IN).iterator().next().getVertex(Direction.IN).getId(), new Long(1));

        assertEquals(count(results.get(2l).getEdges(Direction.OUT)), 1);
        assertEquals(results.get(2l).getEdges(Direction.OUT).iterator().next().getVertex(Direction.OUT).getId(), new Long(2));
        assertEquals(results.get(2l).getEdges(Direction.OUT).iterator().next().getVertex(Direction.IN).getId(), new Long(3));
        assertEquals(count(results.get(2l).getEdges(Direction.OUT, "hates")), 1);
        assertEquals(count(results.get(2l).getEdges(Direction.IN)), 1);
        assertEquals(count(results.get(2l).getEdges(Direction.IN, "likes")), 1);
        assertEquals(results.get(2l).getEdges(Direction.IN).iterator().next().getVertex(Direction.OUT).getId(), new Long(1));
        assertEquals(results.get(2l).getEdges(Direction.IN).iterator().next().getVertex(Direction.IN).getId(), new Long(2));

        assertEquals(count(results.get(3l).getEdges(Direction.OUT)), 1);
        assertEquals(results.get(3l).getEdges(Direction.OUT).iterator().next().getVertex(Direction.OUT).getId(), new Long(3));
        assertEquals(results.get(3l).getEdges(Direction.OUT).iterator().next().getVertex(Direction.IN).getId(), new Long(1));
        assertEquals(count(results.get(3l).getEdges(Direction.OUT, "likes")), 1);
        assertEquals(count(results.get(3l).getEdges(Direction.IN)), 1);
        assertEquals(count(results.get(3l).getEdges(Direction.IN, "hates")), 1);
        assertEquals(results.get(3l).getEdges(Direction.IN).iterator().next().getVertex(Direction.OUT).getId(), new Long(2));
        assertEquals(results.get(3l).getEdges(Direction.IN).iterator().next().getVertex(Direction.IN).getId(), new Long(3));
    }

    public void testElementProperties() throws IOException {
        FaunusVertex a = new FaunusVertex(1);
        a.setProperty("name", "marko");
        a.setProperty("age", 33);

        FaunusVertex b = new FaunusVertex(2);
        b.setProperty("name", "josh");

        FaunusVertex c = new FaunusVertex(1);
        c.setProperty("name", "marko");
        c.setProperty("ssn", "12345");

        FaunusEdge e = new FaunusEdge(a.getIdAsLong(), b.getIdAsLong(), "knows");
        e.setProperty("weight", 1.2f);

        mapReduceDriver.addInput(NullWritable.get(), a);
        mapReduceDriver.addInput(NullWritable.get(), b);
        mapReduceDriver.addInput(NullWritable.get(), c);
        mapReduceDriver.addInput(NullWritable.get(), e);
        Map<Long, FaunusVertex> results = BaseTest.run(mapReduceDriver);
        assertEquals(results.size(), 2);
        assertEquals(results.get(1l).getProperty("name"), "marko");
        assertEquals(results.get(1l).getProperty("age"), 33);
        assertEquals(results.get(1l).getProperty("ssn"), "12345");
        assertEquals(results.get(1l).getPropertyKeys().size(), 3);

        assertEquals(results.get(2l).getProperty("name"), "josh");
        assertEquals(results.get(2l).getPropertyKeys().size(), 1);

        assertEquals(results.get(1l).getEdges(Direction.OUT).iterator().next().getLabel(), "knows");
        assertEquals(results.get(2l).getEdges(Direction.IN).iterator().next().getLabel(), "knows");
        assertEquals(results.get(1l).getEdges(Direction.OUT).iterator().next().getPropertyKeys().size(), 1);
        assertEquals(results.get(2l).getEdges(Direction.IN).iterator().next().getPropertyKeys().size(), 1);
        assertEquals(results.get(1l).getEdges(Direction.OUT).iterator().next().getProperty("weight"), 1.2f);
        assertEquals(results.get(2l).getEdges(Direction.IN).iterator().next().getProperty("weight"), 1.2f);
    }
}
