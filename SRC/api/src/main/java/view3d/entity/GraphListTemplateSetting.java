package view3d.entity;

import java.io.Serializable;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the graph_table_template_settings database table.
 * 
 */
@Data
@Entity
@Table(name="graph_list_template_settings")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GraphListTemplateSetting implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private GraphListTemplateSettingPK id;

	@Column(name="attribute_name", length=100)
	private String attributeName;

	@Column(name="attribute_type")
	private Integer attributeType;

	@Column(name="display_name", length=100)
	private String displayName;

	@Column(name="display_type")
	private Integer displayType;

	@Column(name="group_type")
	private Integer groupType;
	
	@Column(name="placeholder_flag", length=1)
	private String placeholderFlag;

}