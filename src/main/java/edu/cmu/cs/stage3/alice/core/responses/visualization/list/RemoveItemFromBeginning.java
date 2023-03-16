package edu.cmu.cs.stage3.alice.core.responses.visualization.list;

public class RemoveItemFromBeginning extends ListVisualizationAnimation {
	public class RuntimeRemoveItemFromBeginning extends RuntimeListVisualizationAnimation {
        
		public void epilogue( double t ) {
            super.epilogue( t );
            getCollection().values.remove( 0 );
        }
	}
}
