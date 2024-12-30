package com.xzymon.sylar.model;

import java.util.List;

public class CsvOutput {
	private String fileName;
	private String headerLine;
	private String previousDayCloseLine;
	private List<String> content;

	public CsvOutput() {
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getHeaderLine() {
		return headerLine;
	}

	public void setHeaderLine(String headerLine) {
		this.headerLine = headerLine;
	}

	public String getPreviousDayCloseLine() {
		return previousDayCloseLine;
	}

	public void setPreviousDayCloseLine(String previousDayCloseLine) {
		this.previousDayCloseLine = previousDayCloseLine;
	}

	public List<String> getContent() {
		return content;
	}

	public void setContent(List<String> content) {
		this.content = content;
	}
}
