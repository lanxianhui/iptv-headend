package pl.lodz.p.cm.ctp.dao.model;

import java.sql.*;

public class Program {

	private Long id;
	private Long tvChannelId;
	private String title;
	private String description;
	private Timestamp begin;
	private Timestamp end;
	
	public Program() {
		
	}
	
	public Program(Long id, Long tvChannelId, String title, Timestamp begin, Timestamp end) {
		this.id = id;
		this.tvChannelId = tvChannelId;
		this.title = title;
		this.begin = begin;
		this.end = end;
	}
	
	public Program(Long id, Long tvChannelId, String title, String description, Timestamp begin, Timestamp end) {
		this(id, tvChannelId, title, begin, end);
		this.description = description;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getTvChannelId() {
		return tvChannelId;
	}

	public void setTvChannelId(Long tvChannelId) {
		this.tvChannelId = tvChannelId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Timestamp getBegin() {
		return begin;
	}

	public void setBegin(Timestamp begin) {
		this.begin = begin;
	}

	public Timestamp getEnd() {
		return end;
	}

	public void setEnd(Timestamp end) {
		this.end = end;
	}
	
	public boolean equals(Object other) {
		return (other instanceof Program) && (id != null) ? id.equals(((Program) other).id) : (other == this);
	}
	
	public int hashCode() {
        return (id != null) ? (this.getClass().hashCode() + id.hashCode()) : super.hashCode();
    }
	
	@SuppressWarnings("deprecation")
	public String toString() {
		return String.format("Program[id=%d,tvChannelId=%d,title=%s,begin=%s,end=%s]", 
	            id, tvChannelId, title, begin.toGMTString(), end.toGMTString());
	}
}
