package view3d.entity;

import java.io.Serializable;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the layer_graph_cooporation database table.
 * 
 */
@Data
@Entity
@Table(name="layer_graph_cooporation")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LayerGraphCooporation implements Serializable {
	private static final long serialVersionUID = 1L;

	@GeneratedValue(
	        strategy = GenerationType.SEQUENCE,
	        generator = "layer_graph_cooporation_cooperation_id_seq")
    @SequenceGenerator(
        name = "layer_graph_cooporation_cooperation_id_seq",
        sequenceName = "layer_graph_cooporation_cooperation_id_seq",
        initialValue = 1,
        allocationSize = 1)
	@Id
	@Column(name="cooperation_id", unique=true, nullable=false,updatable=false)
	private Integer cooperationId;

	@Column(name="cooperation_type")
	private Integer cooperationType;
	
	@Column(name="cooperation_option")
	private String cooperationOption;

	@Column(name="graph_id", nullable=false)
	private Integer graphId;

	@Column(name="layer_id", nullable=false)
	private Integer layerId;

}