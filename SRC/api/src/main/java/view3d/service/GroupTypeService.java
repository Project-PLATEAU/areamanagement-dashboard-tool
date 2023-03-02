package view3d.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import view3d.entity.GroupType;
import view3d.repository.GroupTypeRepository;

@Service
public class GroupTypeService {
	@Autowired
	GroupTypeRepository groupTypeRepository;
	
	public List<GroupType> findByAll(){
		return groupTypeRepository.findAll();
	}
}
