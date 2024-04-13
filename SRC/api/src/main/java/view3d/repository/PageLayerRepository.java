package view3d.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.ThemeLayer;
import view3d.entity.ThemeLayerPK;

@Transactional
@Repository
public interface PageLayerRepository extends JpaRepository<ThemeLayer, ThemeLayerPK> {
}

