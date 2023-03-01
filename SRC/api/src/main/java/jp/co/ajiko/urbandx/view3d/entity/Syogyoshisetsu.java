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
@Table(name = "syogyoshisetsu")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Syogyoshisetsu implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id", updatable = false)
	private Integer shisestu_id;

	@Column(name = "店舗名", updatable = false)
	private String shop_name;

	@Column(name = "住所")
	private String address;

	@Column(name = "開設年")
	private Integer year;

	@Column(name = "店舗面積")
	private Integer shop_area;
}
