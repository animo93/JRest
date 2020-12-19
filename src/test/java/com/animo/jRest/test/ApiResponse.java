package com.animo.jRest.test;


import java.util.Map;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiResponse {

	@JsonProperty(value = "Meta Data")
	private Map<String, String> metaData;
	@JsonProperty(value = "Time Series (Daily)")
	private Map<String, Map<String, String>> timeSeriesDaily;

	public Map<String, String> getMetaData() {
		return metaData;
	}

	public void setMetaData(Map<String, String> metaData) {
		this.metaData = metaData;
	}

	public Map<String, Map<String, String>> getTimeSeriesDaily() {
		return timeSeriesDaily;
	}

	public void setTimeSeriesDaily(Map<String, Map<String, String>> timeSeriesDaily) {
		this.timeSeriesDaily = timeSeriesDaily;
	}
	
	
}
