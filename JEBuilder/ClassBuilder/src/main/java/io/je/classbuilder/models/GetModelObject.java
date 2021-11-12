package io.je.classbuilder.models;

public class GetModelObject {

	private String type = "GETMODELBYID";
	private String modelId;
	private String workspaceId;
	
	
	
	public GetModelObject(String modelId, String workspaceId) {
		super();
		this.modelId = modelId;
		this.workspaceId = workspaceId;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getModelId() {
		return modelId;
	}
	public void setModelId(String modelId) {
		this.modelId = modelId;
	}
	public String getWorkspaceId() {
		return workspaceId;
	}
	public void setWorkspaceId(String workspaceId) {
		this.workspaceId = workspaceId;
	}
	@Override
	public String toString() {
		return "GetModelObject [type=" + type + ", modelId=" + modelId + ", workspaceId=" + workspaceId + "]";
	}
	
	

}
