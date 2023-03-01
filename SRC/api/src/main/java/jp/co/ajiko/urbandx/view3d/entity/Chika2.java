package jp.co.ajiko.urbandx.view3d.entity;

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
@Table(name = "chika2")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Chika2 implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id",updatable=false)
	private Integer chika2Id;

	@Column(name = "地点名")
	private String place_name ;

	@Column(name = "和暦")
	private String jp_ad;

	@Column(name = "西暦")
	private Integer ad;

	@Column(name = "地価")
	private Integer land_price;

	@Column(name = "区分")
	private String category;

	@Column(name = "エリア")
	private String area;
}
