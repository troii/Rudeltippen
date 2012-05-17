package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="rudeltippen_users")
public class User extends Model{
	@Column(nullable=false)
	private String username;

	@Column(nullable=false)
	private String userpass;

	@Column(nullable=false)
	private String nickname;

	@Column(nullable=false)
	private String salt;

	@Column(nullable=false)
	private Date registered;

	@Column(length=512)
	private String picture;

    @Column(length=1024)
    private String pictureLarge;

	@OneToMany(mappedBy = "user")
	private List<GameTip> gameTips;

	@OneToMany(mappedBy = "user")
	private List<ExtraTip> extraTips;

	@OneToMany(mappedBy = "user")
	private List<Confirmation> confirmations;

	private boolean reminder;
	private boolean admin;
	private boolean active;
	private Date lastLogin;
	private int tipPoints;
	private int extraPoints;
	private int points;
	private int place;

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getUserpass() {
		return userpass;
	}

	public void setUserpass(String userpass) {
		this.userpass = userpass;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Date getRegistered() {
		return registered;
	}

	public void setRegistered(Date registered) {
		this.registered = registered;
	}

	public String getPicture() {
		return picture;
	}

	public void setPicture(String picture) {
		this.picture = picture;
	}

	public String getPictureLarge() {
		return pictureLarge;
	}

	public void setPictureLarge(String pictureLarge) {
		this.pictureLarge = pictureLarge;
	}

	public List<GameTip> getGameTips() {
		return gameTips;
	}

	public void setGameTips(List<GameTip> gameTips) {
		this.gameTips = gameTips;
	}

	public List<ExtraTip> getExtraTips() {
		return extraTips;
	}

	public void setExtraTips(List<ExtraTip> extraTips) {
		this.extraTips = extraTips;
	}

	public List<Confirmation> getConfirmations() {
		return confirmations;
	}

	public void setConfirmations(List<Confirmation> confirmations) {
		this.confirmations = confirmations;
	}

	public boolean isReminder() {
		return reminder;
	}

	public void setReminder(boolean reminder) {
		this.reminder = reminder;
	}

	public boolean isAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public Date getLastLogin() {
		return lastLogin;
	}

	public void setLastLogin(Date lastLogin) {
		this.lastLogin = lastLogin;
	}

	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public int getTipPoints() {
		return tipPoints;
	}

	public void setTipPoints(int tipPoints) {
		this.tipPoints = tipPoints;
	}

	public int getExtraPoints() {
		return extraPoints;
	}

	public void setExtraPoints(int extraPoints) {
		this.extraPoints = extraPoints;
	}

    public static User connect(String username, String userpass) {
	    return find("byUsernameAndUserpassAndActive", username, userpass, true).first();
	}

	public int getPlace() {
		return place;
	}

	public void setPlace(int place) {
		this.place = place;
	}
}