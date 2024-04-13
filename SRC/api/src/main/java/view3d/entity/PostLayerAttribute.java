package view3d.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The persistent class for the graph_table database table.
 * 
 */
@Data
@Entity
@Table(name="post_layer_attribute")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostLayerAttribute implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@EmbeddedId
	private PostLayerAttributePK id;

	@Column(name="item_name")
	private String itemName;
	
	@Column(name="item_type")
	private Integer itemType;
	
	@Column(name="disp_order")
	private Integer dispOrder;
	
	@Column(name="require_flag")
	private String requireFlag;
}
