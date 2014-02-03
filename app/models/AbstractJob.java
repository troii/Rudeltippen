package models;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="rudeltippen_jobs")
public class AbstractJob extends Model{
	@Column(unique=true, nullable=false)
	private String name;

	private boolean active;

	public String getName() {
		return name;
	}

	public void setName(final String name) {
		this.name = name;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(final boolean active) {
		this.active = active;
	}
}