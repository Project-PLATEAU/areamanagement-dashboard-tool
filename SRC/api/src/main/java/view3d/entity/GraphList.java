package view3d.entity;

import java.io.Serializable;
import javax.persistence.*;

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
@Table(name="graph_list")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GraphList implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@GeneratedValue(
	        strategy = GenerationType.SEQUENCE,
	        generator = "graph_list_graph_id_seq")
    @SequenceGenerator(
        name = "graph_list_graph_id_seq",
        sequenceName = "graph_list_graph_id_seq",
        initialValue = 1,
        allocationSize = 1)
	@Id
	@Column(name="graph_id", unique=true, nullable=false,updatable=false)
	private Integer graphId;

	@Column(name="graph_name", length=100)
	private String graphName;

	@Column(name="graph_type_id", nullable=false)
	private Integer graphTypeId;

	@Column(name="query_text")
	private String queryText;
	
	@Column(name="edit_flag", length=1)
	private String editFlag;
	
	@Column(name="source_id")
	private Integer sourceId;
	
	@Column(name="placeholder_flag", length=1)
	private String placeholderFlag;
	
}