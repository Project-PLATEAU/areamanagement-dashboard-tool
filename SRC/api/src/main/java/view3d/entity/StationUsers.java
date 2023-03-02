package view3d.entity;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Table(name = "station_users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class StationUsers implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(generator = "station_users_id_seq")
	@Column(name = "id", updatable = false)
	private int station_id;

	@Column(name = "会社名", updatable = false)
	private String office_name;

	@Column(name = "年")
	private String year;

	@Column(name = "利用者数")
	private Integer user_num;
}
