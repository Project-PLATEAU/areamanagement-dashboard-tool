package view3d.entity;

import java.io.Serializable;
import javax.persistence.*;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


/**
 * The persistent class for the page_graph_table database table.
 * 
 */
@Data
@Entity
@Table(name="theme_graph_list")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ThemeGraphList implements Serializable {
	private static final long serialVersionUID = 1L;

	@EmbeddedId
	private ThemeGraphListPK id;

	@Column(name="panel_height")
	private Integer panelHeight;

	@Column(name="panel_width")
	private Integer panelWidth;

	@Column(name="top_left_x")
	private Integer topLeftX;

	@Column(name="top_left_y")
	private Integer topLeftY;

}