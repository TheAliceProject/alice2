package edu.cmu.cs.stage3.alice.core.responses.visualization.queue;

import edu.cmu.cs.stage3.alice.core.property.QueueOfModelsVisualizationProperty;

public class QueueVisualizationAnimation extends edu.cmu.cs.stage3.alice.core.responses.Animation {
    public final QueueOfModelsVisualizationProperty subject = new QueueOfModelsVisualizationProperty( this, "subject", null );
    public class RuntimeQueueVisualizationAnimation extends RuntimeAnimation {
		protected edu.cmu.cs.stage3.alice.core.Collection getCollection() {
			return subject.getCollectionOfModelsVisualizationValue().getItemsCollection();
		}
    }
}
