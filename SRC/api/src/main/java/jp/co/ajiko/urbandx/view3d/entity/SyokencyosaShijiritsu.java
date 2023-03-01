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
@Table(name = "syokencyosa_shijiritsu")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SyokencyosaShijiritsu implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id", updatable = false)
	private Integer shijiritsu_id;

	@Column(name = "商圏エリア", updatable = false)
	private String syoken_area;

	@Column(name = "割合")
	private Double ratio;
}
