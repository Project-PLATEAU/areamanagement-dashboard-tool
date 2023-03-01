package jp.co.ajiko.urbandx.view3d.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.ajiko.urbandx.view3d.entity.ActivityType;
import jp.co.ajiko.urbandx.view3d.repository.ActivityTypeRepository;

@Service
public class ActivityTypeService {
	@Autowired
	ActivityTypeRepository activityTypeRepository;
	
	public List<ActivityType> findByAll(){
		return activityTypeRepository.findAll();
	}
}
