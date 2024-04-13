package view3d.service;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManagerFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import view3d.dao.ChochomokukaiErimaneDao;
import view3d.entity.ChochomokukaiErimane;
import view3d.form.ChochomokukaiErimaneForm;

@Service
public class SearchService {

	/** LOGGER */
	private static final Logger LOGGER = LoggerFactory.getLogger(SearchService.class);

	/** Entityマネージャファクトリ */
	@Autowired
	protected EntityManagerFactory emf;

	/**
	 * 町丁一覧を取得する
	 * @return
	 */
	public List<ChochomokukaiErimane> searchChochomoku(String townName, String blockName) {
		int epsg = 6668;
		ChochomokukaiErimaneDao chochomokuDao = new ChochomokukaiErimaneDao(emf);
		final List<ChochomokukaiErimane> chochoList = chochomokuDao.searchChochomoku(epsg, townName, blockName);
		if (chochoList.size() > 0) {
			LOGGER.debug("町丁一覧の取得成功。");
			return chochoList;
		} else {
			LOGGER.debug("町丁一覧が得られませんでした。");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}
	
	/**
	 * 町名一覧を取得する
	 * @return
	 */
	public List<String> getTownList() {
		ChochomokukaiErimaneDao chochomokuDao = new ChochomokukaiErimaneDao(emf);
		final List<String> townList = chochomokuDao.getTownList();
		if (townList.size() > 0) {
			LOGGER.debug("町名一覧の取得成功。");
			return townList;
		} else {
			LOGGER.debug("町名一覧が得られませんでした。");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * 町丁一覧を取得する
	 * @return
	 */
	public List<Object> getChochomoku() {
		ChochomokukaiErimaneDao chochomokuDao = new ChochomokukaiErimaneDao(emf);
		final List<Object> chochoList = chochomokuDao.getChochoList();
		if (chochoList.size() > 0) {
			LOGGER.debug("町丁一覧の取得成功。");
			return chochoList;
		} else {
			LOGGER.debug("町丁一覧が得られませんでした。");
			throw new ResponseStatusException(HttpStatus.NOT_FOUND);
		}
	}
}
