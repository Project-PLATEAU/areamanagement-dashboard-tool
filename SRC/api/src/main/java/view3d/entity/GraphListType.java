package view3d.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the graph_table_type database table.
 * 
 */
@Data
@Entity
@Table(name="graph_list_type")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GraphListType implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="graph_type_id", unique=true, nullable=false,updatable=false)
	private Integer graphTypeId;

	@Column(name="graph_type_name", length=100)
	private String graphTypeName;
	
	@Column(name="edit_flag", length=1)
	private String editFlag;
	
	@Column(name="default_query_text")
	private String defaultQueryText;

}