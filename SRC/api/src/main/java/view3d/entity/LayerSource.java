package view3d.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@Table(name="layer_source")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LayerSource implements Serializable {
	
	/** シリアルバージョンUID */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "source_id", unique=true, nullable=false,updatable=false)
	private Integer sourceId;
	
	@Column(name = "layer_id")
	private Integer layerId;
	
	@Column(name = "table_name")
	private String tableName;
}
