package view3d.entity;

import java.io.Serializable;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the layer database table.
 * 
 */
@Data
@Entity
@Table(name="layer")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Layer implements Serializable {
	private static final long serialVersionUID = 1L;

	@GeneratedValue(
	        strategy = GenerationType.SEQUENCE,
	        generator = "layer_layer_id_seq")
    @SequenceGenerator(
        name = "layer_layer_id_seq",
        sequenceName = "layer_layer_id_seq",
        initialValue = 1,
        allocationSize = 1)
	@Id
	@Column(name="layer_id", unique=true, nullable=false,updatable=false)
	private Integer layerId;

	@Column(name="icon_path")
	private String iconPath;

	@Column(name="layer_name", length=100)
	private String layerName;

	@Column(name="layer_settings")
	private String layerSettings;

	@Column(name="layer_type")
	private Integer layerType;
	
	@Column(name="placeholder_flag")
	private String placeHolderFlag;

}