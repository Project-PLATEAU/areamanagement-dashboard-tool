package view3d.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.GroupType;

@Transactional
@Repository
public interface GroupTypeRepository extends JpaRepository<GroupType, Integer> {

}