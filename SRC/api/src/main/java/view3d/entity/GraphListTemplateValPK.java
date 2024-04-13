package view3d.entity;

import java.io.Serializable;
import javax.persistence.*;

/**
 * The primary key class for the graph_table_template_val database table.
 * 
 */
@Embeddable
public class GraphListTemplateValPK implements Serializable {
	//default serial version id, required for serializable classes.
	private static final long serialVersionUID = 1L;

	@Column(name="graph_id", unique=true, nullable=false,updatable=false)
	private Integer graphId;

	@Column(name="item_id", unique=true, nullable=false,updatable=false)
	private Integer itemId;

	public GraphListTemplateValPK() {
	}
	public Integer getGraphId() {
		return this.graphId;
	}
	public void setGraphId(Integer graphId) {
		this.graphId = graphId;
	}
	public Integer getItemId() {
		return this.itemId;
	}
	public void setItemId(Integer itemId) {
		this.itemId = itemId;
	}

	public boolean equals(Object other) {
		if (this == other) {
			return true;
		}
		if (!(other instanceof GraphListTemplateValPK)) {
			return false;
		}
		GraphListTemplateValPK castOther = (GraphListTemplateValPK)other;
		return 
			this.graphId.equals(castOther.graphId)
			&& this.itemId.equals(castOther.itemId);
	}

	public int hashCode() {
		final int prime = 31;
		int hash = 17;
		hash = hash * prime + this.graphId.hashCode();
		hash = hash * prime + this.itemId.hashCode();
		
		return hash;
	}
}