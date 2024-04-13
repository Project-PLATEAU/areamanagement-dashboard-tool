package view3d.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the graph_table_template_settings database table.
 * 
 */
@Embeddable
public class GraphListTemplateSettingPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="item_id", unique=true, nullable=false,updatable=false)
	private Integer itemId;

	@Column(name="graph_type_id", unique=true, nullable=false,updatable=false)
	private Integer graphTypeId;

	public GraphListTemplateSettingPK() {
	}
	public Integer getItemId() {
		return this.itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}
	public Integer getGraphTypeId() {
		return this.graphTypeId;
	}
	public void setGraphTypeId(Integer graphTypeId) {
		this.graphTypeId = graphTypeId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof GraphListTemplateSettingPK)) {
			return false;
		}
		GraphListTemplateSettingPK castOther = (GraphListTemplateSettingPK)other;
		return 
			this.itemId.equals(castOther.itemId)
			&& this.graphTypeId.equals(castOther.graphTypeId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.itemId.hashCode();
		hash = hash * prime + this.graphTypeId.hashCode();
		
		return hash;
	}
}