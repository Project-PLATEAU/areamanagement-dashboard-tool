package view3d.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the page_graph_table database table.
 * 
 */
@Embeddable
public class ThemeGraphListPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="theme_id", unique=true, nullable=false,updatable=false)
	private Integer themeId;

	@Column(name="graph_id", unique=true, nullable=false,updatable=false)
	private Integer graphId;

	public ThemeGraphListPK() {
	}
	public Integer getThemeId() {
		return this.themeId;
	}
	public void setThemeId(Integer themeId) {
		this.themeId = themeId;
	}
	public Integer getGraphId() {
		return this.graphId;
	}
	public void setGraphId(Integer graphId) {
		this.graphId = graphId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ThemeGraphListPK)) {
			return false;
		}
		ThemeGraphListPK castOther = (ThemeGraphListPK)other;
		return 
			this.themeId.equals(castOther.themeId)
			&& this.graphId.equals(castOther.graphId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.themeId.hashCode();
		hash = hash * prime + this.graphId.hashCode();
		
		return hash;
	}
}