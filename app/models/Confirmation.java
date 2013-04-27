package models;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import models.enums.ConfirmationType;

import play.db.jpa.Model;

@Entity
@Table(name="rudeltippen_confirmations")
public class Confirmation extends Model{
	@ManyToOne
	private User user;

	@Column(nullable=false)
	private String token;

	@Column(nullable=false)
	private ConfirmationType confirmType;

	@Lob
	@Column(nullable=false)
	private String confirmValue;

	@Column(nullable=false)
	private Date created;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public ConfirmationType getConfirmType() {
		return confirmType;
	}

	public void setConfirmType(ConfirmationType confirmType) {
		this.confirmType = confirmType;
	}

	public String getConfirmValue() {
		return confirmValue;
	}

	public void setConfirmValue(String confirmValue) {
		this.confirmValue = confirmValue;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}
}