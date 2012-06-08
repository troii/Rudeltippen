package models;

import java.util.Map;

public class WSResults {
	private boolean updated;
	private Map<String, WSResult> wsResult;

	public Map<String, WSResult> getWsResult() {
		return wsResult;
	}
	
	public void setWsResult(Map<String, WSResult> wsResult) {
		this.wsResult = wsResult;
	}

	public boolean isUpdated() {
		return updated;
	}

	public void setUpdated(boolean updated) {
		this.updated = updated;
	}
}