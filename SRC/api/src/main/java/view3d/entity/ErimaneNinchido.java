package view3d.entity;

import java.io.Serializable;

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
@Table(name = "erimane_ninchido")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ErimaneNinchido implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id", updatable = false)
	private Integer ninchido_id;

	@Column(name = "エリア", updatable = false)
	private String area;

	@Column(name = "和暦", updatable = false)
	private String jp_ad;

	@Column(name = "西暦")
	private Integer ad;

	@Column(name = "認知度")
	private Double ninchido;
}
