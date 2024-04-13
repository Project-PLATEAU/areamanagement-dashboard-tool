package view3d.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the page_layer database table.
 * 
 */
@Embeddable
public class ThemeLayerPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="theme_id", unique=true, nullable=false,updatable=false)
	private Integer themeId;

	@Column(name="layer_id", unique=true, nullable=false,updatable=false)
	private Integer layerId;

	public ThemeLayerPK() {
	}
	public Integer getThemeId() {
		return this.themeId;
	}
	public void setThemeId(Integer themeId) {
		this.themeId = themeId;
	}
	public Integer getLayerId() {
		return this.layerId;
	}
	public void setLayerId(Integer layerId) {
		this.layerId = layerId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof ThemeLayerPK)) {
			return false;
		}
		ThemeLayerPK castOther = (ThemeLayerPK)other;
		return 
			this.themeId.equals(castOther.themeId)
			&& this.layerId.equals(castOther.layerId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.themeId.hashCode();
		hash = hash * prime + this.layerId.hashCode();
		
		return hash;
	}
}