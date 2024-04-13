package view3d.service;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import view3d.dao.PostLayerDao;
import view3d.entity.Attachment;
import view3d.entity.PostLayerFeature;
import view3d.form.AttachmentForm;
import view3d.form.PostLayerFeatureForm;
import view3d.repository.AttachmentsRepository;
import view3d.repository.jdbc.PostLayerJdbc;

@Service
public class AttachmentsService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AttachmentsService.class);
	
	@Autowired
	AttachmentsRepository attachmentsRepository;

	@Autowired
	PostLayerJdbc postLayerJdbc;
	
	@Value("${app.file.rootpath}")
	protected String fileRootPath;
	
	/**
	 * 添付ファイルの取得
	 * 
	 * @param id 取得対象のid
	 * @return Optional<Attachment>
	 */
	public Optional<Attachment> findById(Integer id) {
		return attachmentsRepository.findById(id);
	}

	/**
	 * 添付ファイルの取得
	 * 
	 * @param activityId         取得対象の活動id
	 * @param attachmentFileName 取得対象のフィル名
	 * @return Optional<Attachment>
	 */
	public Optional<Attachment> findByActivityIdAndAttachmentFileName(Integer activityId, String attachmentFileName) {
		return attachmentsRepository.findByActivityIdAndAttachmentFileName(activityId, attachmentFileName);
	}
	
	/**
	 * 添付ファイルの取得
	 * 
	 * @param activityId 取得対象の活動id
	 * @return List<Attachment>
	 */
	public List<Attachment> findByActivityId(Integer activityId) {
		return attachmentsRepository.findByActivityId(activityId);
	}

	/**
	 * 添付ファイルのアップロード
	 * 
	 * @param attachmentForm アップロード対象のAttachmentForm
	 */
	@Transactional
	public Optional<Attachment> upload(AttachmentForm attachmentForm) {
		try {
			String absoluteFolderPath = fileRootPath + attachmentForm.getActivityId();
			Path directoryPath = Paths.get(absoluteFolderPath);
			if (!Files.exists(directoryPath)) {
				LOGGER.info("フォルダ生成: " + absoluteFolderPath);
				Files.createDirectories(directoryPath);
			}
			String fileName = getNewFileName(attachmentForm.getAttachmentFileName());
			String filePath = absoluteFolderPath + "/" + fileName;
			exportFile(attachmentForm.getUploadFile(), filePath);
			Attachment attachment = new Attachment();
			attachment.setActivityId(attachmentForm.getActivityId());
			attachment.setAttachmentFileName(fileName);
			Optional<Attachment> attachmentOpt = Optional.ofNullable(attachmentsRepository.save(attachment));
			LOGGER.info("添付ファイルのアップロード終了: " + filePath);
			return attachmentOpt;
		} catch (Exception ex) {
			LOGGER.error("添付ファイルのアップロードで例外発生", ex);
			throw new RuntimeException(ex);
		}
	}
	
	/**
	 * 添付ファイルのアップロード(投稿レイヤ)
	 * 
	 * @param attachmentForm アップロード対象のAttachmentForm
	 */
	@Transactional
	public void uploadFeature(AttachmentForm attachmentForm) {
		try {
			String absoluteFolderPath = fileRootPath + "feature_" + attachmentForm.getFeatureId();
			Path directoryPath = Paths.get(absoluteFolderPath);
			if (!Files.exists(directoryPath)) {
				LOGGER.info("フォルダ生成: " + absoluteFolderPath);
				Files.createDirectories(directoryPath);
			}
			String fileName = getNewFileName(attachmentForm.getAttachmentFileName());
			String filePath = absoluteFolderPath + "/" + fileName;
			exportFile(attachmentForm.getUploadFile(), filePath);
			attachmentForm.setAttachmentFileName(fileName);
			String updates = postLayerJdbc.saveNewFile(attachmentForm);
			if(updates.equals(attachmentForm.getAttachmentFileName())) {
				LOGGER.info("添付ファイルのアップロード終了: " + filePath);
			}else {
				throw new Exception();
			}
		} catch (Exception ex) {
			LOGGER.error("添付ファイルのアップロードで例外発生", ex);
			throw new RuntimeException(ex);
		}
	}

	/**
	 * idからファイルを削除(論理削除)
	 * 
	 * @param attachmentFormDeleteList 削除対象のAttachmentForm List
	 */
	@Transactional
	public void deleteByIdDbOnly(List<AttachmentForm> attachmentFormDeleteList) {
		if (attachmentFormDeleteList != null) {
			for (AttachmentForm attachmentForm : attachmentFormDeleteList) {
				Optional<Attachment> attachmentOpt = findById(attachmentForm.getId());
				if (attachmentOpt.isPresent()) {
					Attachment attachment = attachmentOpt.get();
					attachmentsRepository.deleteById(attachment.getId());
					LOGGER.info("添付ファイル論理削除 id: " + attachment.getId());
				}

			}
		}
	}

	/**
	 * ファイル出力
	 * 
	 * @param fileData     ファイルデータ
	 * @param filePathText 出力ファイルパス
	 * @throws IOException 例外
	 */
	private void exportFile(MultipartFile fileData, String filePathText) throws IOException {
        try {
        	File uploadFile =
                    new File(filePathText);
            byte[] bytes = fileData.getBytes();
            BufferedOutputStream uploadFileStream =
                    new BufferedOutputStream(new FileOutputStream(uploadFile));
            uploadFileStream.write(bytes);
            uploadFileStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
	}

	/**
	 * ファイル削除
	 * 
	 * @param filePathText ファイルパス
	 * @return resultFlg 削除結果
	 */
	private boolean deleteFile(String filePathText) {
		boolean resultFlg = true;
		try {
			Path filePath = Paths.get(filePathText);
			Files.delete(filePath);
		} catch (Exception ex) {
			resultFlg = false;
			LOGGER.error("添付ファイル削除で例外発生", ex);
		}
		return resultFlg;
	}

	/**
	 * 新規ファイル名作成
	 * 
	 * @param fileName ファイル名
	 * @return newFileName 新規ファイル名
	 */
	private String getNewFileName(String fileName) {
		String newFileName = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss-SSS")
				.format(LocalDateTime.ofInstant(Instant.now(), ZoneId.systemDefault()));
		Random rand = new Random();
		newFileName = newFileName + "-" + rand.nextInt(10000);
		int point = fileName.lastIndexOf(".");
		if (point != -1) {
			newFileName = newFileName + "." + fileName.substring(point + 1);
		}
		return newFileName;
	}

}
