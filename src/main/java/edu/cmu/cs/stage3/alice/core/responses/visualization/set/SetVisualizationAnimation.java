package edu.cmu.cs.stage3.alice.core.responses.visualization.set;

import edu.cmu.cs.stage3.alice.core.property.SetOfModelsVisualizationProperty;

public class SetVisualizationAnimation extends edu.cmu.cs.stage3.alice.core.responses.Animation {
    public final SetOfModelsVisualizationProperty subject = new SetOfModelsVisualizationProperty( this, "subject", null );
    public class RuntimeSetVisualizationAnimation extends RuntimeAnimation {
		protected edu.cmu.cs.stage3.alice.core.Collection getCollection() {
			return subject.getCollectionOfModelsVisualizationValue().getItemsCollection();
		}
    }
}
