package view3d.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Table(name = "post_layer_icon_path")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class PostLayerIconPath implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "id",updatable=false)
	private Integer id;
	
	@Column(name = "layer_id")
	private Integer layerId;
	
	@Column(name = "image_path")
	private String imagePath;
	
	@Column(name = "Judgment_value")
	private String JudgmentValue;

}
