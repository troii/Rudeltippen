package models;

import java.util.Map;

public class WSResults {
	private Map<String, WSResult> wsResult;

	public Map<String, WSResult> getWsResult() {
		return wsResult;
	}
	
	public void setWsResult(Map<String, WSResult> wsResult) {
		this.wsResult = wsResult;
	}
}