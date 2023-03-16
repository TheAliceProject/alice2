package edu.cmu.cs.stage3.alice.core.responses.visualization.queue;

public class Enqueue extends QueueVisualizationWithItemAnimation {
	public class RuntimeEnqueue extends RuntimeQueueVisualizationWithItemAnimation {
		
		public void epilogue( double t ) {
			super.epilogue( t );
			getCollection().values.addValue( getItem() );
		}
	}
}
