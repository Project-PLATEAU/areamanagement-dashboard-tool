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
@Table(name="layer_source_field")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class LayerSourceField implements Serializable {
	
	/** シリアルバージョンUID */
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "field_id", unique=true, nullable=false,updatable=false)
	private Integer fieldId;
	
	@Column(name = "source_id")
	private Integer sourceId;
	
	@Column(name = "field_name")
	private String fieldName;
	
	@Column(name = "alias")
	private String alias;
}
