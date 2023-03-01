package jp.co.ajiko.urbandx.view3d.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Kaiyusei implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id", updatable = false)
	private Integer id;

	@Column(name = "geom")
	private String geom;

	@Column(name = "移動経路")
	private String path;

	@Column(name = "合計")
	private Double sum;

	@Column(name = "orig_fid")
	private Integer orig_fid;

	@Column(name = "shape_leng")
	private Double shape_leng;

	@Column(name = "距離")
	private Double distance;
}
