package models;

import java.util.Date;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import play.db.jpa.Model;

@Entity
@Table(name="rudeltippen_extras")
public class Extra extends Model{
	@OneToMany(mappedBy = "extra")
	private List<ExtraTip> extraTips;

	@Column(nullable=false)
	private String question;

	@Column(nullable=false)
	private String questionShort;

	@Column(nullable=false)
	private int points;

	@Column(nullable=false)
	private Date ending;

	@ManyToMany
	private List<Team> answers;

	@Column(nullable=false)
	private String extraReference;

	@ManyToMany
	private List<Game> gameReferences;

	@ManyToOne
	private Team answer;

	public List<ExtraTip> getExtraTipps() {
		return extraTips;
	}

	public void setExtraTipps(List<ExtraTip> extraTips) {
		this.extraTips = extraTips;
	}

	public String getQuestion() {
		return question;
	}

	public void setQuestion(String question) {
		this.question = question;
	}

	public String getQuestionShort() {
		return questionShort;
	}

	public void setQuestionShort(String questionShort) {
		this.questionShort = questionShort;
	}

	public int getPoints() {
		return points;
	}

	public void setPoints(int points) {
		this.points = points;
	}

	public Date getEnding() {
		return ending;
	}

	public void setEnding(Date ending) {
		this.ending = ending;
	}

	public List<Team> getAnswers() {
		return answers;
	}

	public void setAnswers(List<Team> answers) {
		this.answers = answers;
	}

	public String getExtraReference() {
		return extraReference;
	}

	public void setExtraReference(String extraReference) {
		this.extraReference = extraReference;
	}

	public List<Game> getGameReferences() {
		return gameReferences;
	}

	public void setGameReferences(List<Game> gameReferences) {
		this.gameReferences = gameReferences;
	}

	public Team getAnswer() {
		return answer;
	}

	public void setAnswer(Team answer) {
		this.answer = answer;
	}
	
	public boolean isTippable() {
		final Date now = new Date();
		if (now.getTime() >= ending.getTime()) {
			return false;
		}
		
		return true;
	}
}
