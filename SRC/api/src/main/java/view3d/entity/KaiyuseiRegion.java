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
@Table(name = "kaiyu_jinryu_chiiki_1")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KaiyuseiRegion implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id", updatable = false)
	private Integer id;

	@Column(name = "住所")
	private String address;

	@Column(name = "人数")
	private Integer users;

	@Column(name = "割合")
	private Double rate;

	@Column(name = "回数")
	private Integer number;
}
