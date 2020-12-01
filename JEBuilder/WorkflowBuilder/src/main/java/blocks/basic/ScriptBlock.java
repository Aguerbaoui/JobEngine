package blocks.basic;

import blocks.WorkflowBlock;
import io.je.utilities.runtimeobject.JEObject;

public class ScriptBlock extends WorkflowBlock{

	String script;

	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
	}
}
