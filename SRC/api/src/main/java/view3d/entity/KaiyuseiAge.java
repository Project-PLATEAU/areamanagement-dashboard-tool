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
@Table(name = "kaiyu_jinryu_nenrei_1")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KaiyuseiAge implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id", updatable = false)
	private Integer id;

	@Column(name = "項目")
	private String item;

	@Column(name = "人数")
	private Double users;

	@Column(name = "割合")
	private Double rate;

	@Column(name = "回数")
	private Integer number;

	@Column(name = "項目id")
	private Integer item_id;
}
