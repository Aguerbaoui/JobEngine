package blocks.control;

import blocks.WorkflowBlock;
import io.je.utilities.runtimeobject.JEObject;

public class ExclusiveGatewayBlock extends WorkflowBlock{

	boolean exclusive;

	public boolean isExclusive() {
		return exclusive;
	}

	public void setExclusive(boolean exclusive) {
		this.exclusive = exclusive;
	}
}
