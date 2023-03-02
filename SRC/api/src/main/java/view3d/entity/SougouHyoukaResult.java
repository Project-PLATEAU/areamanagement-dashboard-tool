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
@Table(name = "sougou_hyouka_result")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class SougouHyoukaResult implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "id", updatable = false)
	private int shr_id;

	@Column(name = "カテゴリ", updatable = false)
	private String category;

	@Column(name = "年度")
	private Integer year;

	@Column(name = "全体比較")
	private Float city_comp;

	@Column(name = "過去比較")
	private Float past_comp;
}
