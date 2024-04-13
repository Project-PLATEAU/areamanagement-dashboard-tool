package view3d.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Table(name = "region_summary")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CitySummary implements Serializable {
	private static final long serialVersionUID = 1L;

	@GeneratedValue(
	        strategy = GenerationType.SEQUENCE,
	        generator = "region_summary_id_seq")
    @SequenceGenerator(
        name = "region_summary_id_seq",
        sequenceName = "region_summary_id_seq",
        initialValue = 1,
        allocationSize = 1)
	@Id
	@Column(name = "id", updatable = false)
	private int shr_id;

	@Column(name = "カテゴリ", updatable = false)
	private String category;

	@Column(name = "和暦")
	private String era_jp;

	@Column(name = "西暦")
	private Integer year;

	@Column(name = "値")
	private Double value;
}
