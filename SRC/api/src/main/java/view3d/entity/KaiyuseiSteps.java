package view3d.entity;

import java.io.Serializable;
import java.util.Date;

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
@Table(name = "kaiyu_jinryu_hosuu_1")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class KaiyuseiSteps implements Serializable {
	private static final long serialVersionUID = 1L;
	@Id
	@Column(name = "id", updatable = false)
	private Integer id;

	@Column(name = "日付")
	private Date date;

	@Column(name = "歩数")
	private Integer step;

	@Column(name = "天気")
	private String weather;

	@Column(name = "最低気温")
	private Double min_temp;

	@Column(name = "最高気温")
	private Double max_temp;

	@Column(name = "回数")
	private Integer number;
}
