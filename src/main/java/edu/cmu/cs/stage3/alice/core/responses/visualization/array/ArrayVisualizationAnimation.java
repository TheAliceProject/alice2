package edu.cmu.cs.stage3.alice.core.responses.visualization.array;

import edu.cmu.cs.stage3.alice.core.property.ArrayOfModelsVisualizationProperty;

public class ArrayVisualizationAnimation extends edu.cmu.cs.stage3.alice.core.responses.Animation {
    public final ArrayOfModelsVisualizationProperty subject = new ArrayOfModelsVisualizationProperty( this, "subject", null );
	public class RuntimeArrayVisualizationAnimation extends RuntimeAnimation {
		protected edu.cmu.cs.stage3.alice.core.Collection getCollection() {
			return subject.getCollectionOfModelsVisualizationValue().getItemsCollection();
		}
	}
}
