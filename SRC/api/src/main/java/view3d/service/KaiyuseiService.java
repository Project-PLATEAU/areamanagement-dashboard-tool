package view3d.service;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import view3d.dao.KaiyuseiDao;

@Service
public class KaiyuseiService {

	private static final String URL_EXCURSION_ANALYSIS_TYPE = "#excursionAnalysisType=";
	private static final String URL_EXCURSION_ANALYSIS_COUNT = "&excursionAnalysisCount=";
	private static final String URL_EXCURSION_ANALYSIS_ID = "&excursionAnalysisId=";
	private static final String URL_LONGITUDE = "&lon=";
	private static final String URL_LATITUDE = "&lat=";
	private static final String URL_HEIGHT = "&height=";

	private static final String MOVE_NUM_SUM_TYPE = "イベント回遊情報";
	private static final String FAV_SPOT_TYPE = "人気スポット(3D)";

	@Autowired
	KaiyuseiDao kaiyuseiDao;

	/**
	 * 最新回数を取得
	 * @return
	 * @throws Exception
	 */
	public int getLatestNumber() throws Exception {
		int res = 1;

		try {
			while (kaiyuseiDao.existTable("kaiyuusei_" + res) != null) {
				res++;
			}
			return --res;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 移動数合計データを取得
	 * @param number
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getMoveNumSum(int number) throws Exception {
		try {
			List<Map<String, Object>> res = kaiyuseiDao.getMoveNumSum(number);
			for (int i = 0; i < res.size(); i++) {
				Map<String, Object> tmp = res.get(i);
				String url = URL_EXCURSION_ANALYSIS_TYPE+ MOVE_NUM_SUM_TYPE
						+URL_EXCURSION_ANALYSIS_COUNT+number
						+URL_EXCURSION_ANALYSIS_ID+tmp.get("移動経路")
						+URL_LONGITUDE+tmp.get("経度")
						+URL_LATITUDE+tmp.get("緯度")
						+URL_HEIGHT+tmp.get("高さ");
				tmp.put("url", url);
				tmp.remove("経度");
				tmp.remove("緯度");
				tmp.remove("高さ");
			}
			return res;
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 人気スポットデータを取得
	 * @param number
	 * @return
	 * @throws Exception
	 */
	public List<Map<String, Object>> getFavSpot(int number) throws Exception {
		try {
			List<Map<String, Object>> res = kaiyuseiDao.getFavSpot(number);
			for (int i = 0; i < res.size(); i++) {
				Map<String, Object> tmp = res.get(i);
				String url = URL_EXCURSION_ANALYSIS_TYPE+ FAV_SPOT_TYPE
						+URL_EXCURSION_ANALYSIS_COUNT+number
						+URL_EXCURSION_ANALYSIS_ID+tmp.get("スポット名")
						+URL_LONGITUDE+tmp.get("経度")
						+URL_LATITUDE+tmp.get("緯度")
						+URL_HEIGHT+tmp.get("高さ");
				tmp.put("url", url);
				tmp.remove("経度");
				tmp.remove("緯度");
				tmp.remove("高さ");
			}
			return res;
		} catch (Exception e) {
			throw e;
		}
	}
}
