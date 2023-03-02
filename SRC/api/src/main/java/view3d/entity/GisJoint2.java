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
@Table(name = "gis_joint2")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class GisJoint2 implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id", updatable = false)
	private Integer gis_id;

	@Column(name = "地点名")
	private String place_name;

	@Column(name = "エリマネ")
	private String erimane;

	@Column(name = "カテゴリ")
	private String category;

	@Column(name = "和暦")
	private String jp_ad;

	@Column(name = "西暦")
	private Integer ad;

	@Column(name = "数")
	private Integer number;
}
