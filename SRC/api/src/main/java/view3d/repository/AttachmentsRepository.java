package view3d.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import view3d.entity.Attachment;

@Transactional
@Repository
public interface AttachmentsRepository extends JpaRepository<Attachment, Integer> {
	
	@Query(value="SELECT id,activity_id,attachment_file_name,created FROM attachments WHERE activity_id = :activityId ORDER BY id",nativeQuery=true)
	List<Attachment> findByActivityId(@Param("activityId") Integer activityId);
	
	@Query(value="SELECT id,activity_id,attachment_file_name,created FROM attachments WHERE activity_id = :activityId AND attachment_file_name = :attachmentFileName",nativeQuery=true)
	Optional<Attachment> findByActivityIdAndAttachmentFileName(@Param("activityId") Integer activityId,@Param("attachmentFileName") String attachmentFileName);
	
	@Transactional
	void deleteById(Integer id);

}
