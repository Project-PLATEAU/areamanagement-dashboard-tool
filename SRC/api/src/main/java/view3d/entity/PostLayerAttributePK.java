package view3d.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 * The primary key class for the graph_table_template_settings database table.
 * 
 */
@Embeddable
public class PostLayerAttributePK implements Serializable {
	
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="item_id", unique=true, nullable=false,updatable=false)
	private Integer itemId;

	@Column(name="layer_id", unique=true, nullable=false,updatable=false)
	private Integer layerId;

	public PostLayerAttributePK() {
	}
	public Integer getItemId() {
		return this.itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
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
		if (!(other instanceof PostLayerAttributePK)) {
			return false;
		}
		PostLayerAttributePK castOther = (PostLayerAttributePK)other;
		return 
			this.itemId.equals(castOther.itemId)
			&& this.layerId.equals(castOther.layerId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.itemId.hashCode();
		hash = hash * prime + this.layerId.hashCode();
		
		return hash;
	}

}
