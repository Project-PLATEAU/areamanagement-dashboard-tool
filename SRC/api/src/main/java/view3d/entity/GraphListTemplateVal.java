package view3d.entity;

import java.io.Serializable;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the graph_table_template_val database table.
 * 
 */
@Data
@Entity
@Table(name="graph_list_template_val")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GraphListTemplateVal implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private GraphListTemplateValPK id;

	@Column(name="item_value")
	private String itemValue;

}