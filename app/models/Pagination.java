package models;

public class Pagination {
	private String url;
	private long number;
	private long offsetStart;
	private long offset;
	private long offsetEnd;
	
	public String getUrl() {
		return url;
	}
	
	public void setUrl(String url) {
		this.url = url;
	}
	
	public long getOffsetStart() {
		return offsetStart;
	}
	
	public void setOffsetStart(long offsetStart) {
		this.offsetStart = offsetStart;
	}
	
	public long getOffset() {
		return offset;
	}
	
	public void setOffset(long offset) {
		this.offset = offset;
	}
	
	public long getOffsetEnd() {
		return offsetEnd;
	}
	
	public void setOffsetEnd(long offsetEnd) {
		this.offsetEnd = offsetEnd;
	}

	public long getNumber() {
		return number;
	}

	public void setNumber(long number) {
		this.number = number;
	}
	
	public int getNumberAsInt() {
		return (int) this.number;
	}
}