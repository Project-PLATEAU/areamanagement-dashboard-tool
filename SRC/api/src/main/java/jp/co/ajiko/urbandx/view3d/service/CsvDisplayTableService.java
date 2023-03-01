package jp.co.ajiko.urbandx.view3d.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jp.co.ajiko.urbandx.view3d.entity.Chika2;
import jp.co.ajiko.urbandx.view3d.entity.CitySummary;
import jp.co.ajiko.urbandx.view3d.entity.ErimaneNinchido;
import jp.co.ajiko.urbandx.view3d.entity.GisJoint2;
import jp.co.ajiko.urbandx.view3d.entity.KaiyuseiAge;
import jp.co.ajiko.urbandx.view3d.entity.KaiyuseiGender;
import jp.co.ajiko.urbandx.view3d.entity.KaiyuseiPeople;
import jp.co.ajiko.urbandx.view3d.entity.KaiyuseiRegion;
import jp.co.ajiko.urbandx.view3d.entity.KaiyuseiSteps;
import jp.co.ajiko.urbandx.view3d.entity.StationUsers;
import jp.co.ajiko.urbandx.view3d.entity.Syogyoshisetsu;
import jp.co.ajiko.urbandx.view3d.entity.SyokencyosaShijiritsu;
import jp.co.ajiko.urbandx.view3d.repository.Chika2Repository;
import jp.co.ajiko.urbandx.view3d.repository.CitySummaryRepository;
import jp.co.ajiko.urbandx.view3d.repository.ErimaneNinchidoRepository;
import jp.co.ajiko.urbandx.view3d.repository.GisJoint2Repository;
import jp.co.ajiko.urbandx.view3d.repository.KaiyuAgeRepository;
import jp.co.ajiko.urbandx.view3d.repository.KaiyuGenderRepository;
import jp.co.ajiko.urbandx.view3d.repository.KaiyuPeopleRepository;
import jp.co.ajiko.urbandx.view3d.repository.KaiyuRegionRepository;
import jp.co.ajiko.urbandx.view3d.repository.KaiyuStepsRepository;
import jp.co.ajiko.urbandx.view3d.repository.StationUsersRepository;
import jp.co.ajiko.urbandx.view3d.repository.SyogyoshisetsuRepository;
import jp.co.ajiko.urbandx.view3d.repository.SyokencyosaShijiritsuRepository;

@Service
public class CsvDisplayTableService {

	@Autowired
	Chika2Repository chika2Repository;

	@Autowired
	ErimaneNinchidoRepository erimaneNinchidoRepository;

	@Autowired
	GisJoint2Repository gisJoint2Repository;

	@Autowired
	StationUsersRepository stationUsersRepository;

	@Autowired
	SyogyoshisetsuRepository syogyoshisetsuRepository;

	@Autowired
	SyokencyosaShijiritsuRepository syokencyosaShijiritsuRepository;

	@Autowired
	CitySummaryRepository citySummaryRepository;

	@Autowired
	KaiyuPeopleRepository kaiyuPeopleRepository;

	@Autowired
	KaiyuAgeRepository kaiyuAgeRepository;

	@Autowired
	KaiyuGenderRepository kaiyuGenderRepository;

	@Autowired
	KaiyuRegionRepository kaiyuRegionRepository;

	@Autowired
	KaiyuStepsRepository kaiyuStepsRepository;


	private static final int CHIKA2_STATIC_DATA_NUM = 3;
	private static final int NINCHIDO_STATIC_DATA_NUM = 1;
	private static final int GIS_JOINT2_DATA_NUM = 2;
	private static final int STATION_USERS_DATA_NUM = 1;
	private static final int CITY_SUMMARY_DATA_NUM = 1;

	private final SimpleDateFormat hyphensdf=new SimpleDateFormat("yyyy-MM-dd");

	/**
	 * 地域表示価格のテーブル作成
	 */
	public Map<String, Object> createChika2Table() {
		Map<String, Object> table = new HashMap<>();
		List<String> th = new ArrayList<>();
		//固定ヘッダ
		th.add("地点名");
		th.add("区分");
		th.add("エリア");

		List<Chika2> chika2 = chika2Repository.findChika2();
		int RowNum = 0;
		String placeName = "";
		String category = "";
		String area = "";
		boolean sameRowFlag = true;
		//ヘッダ作成
		for (int i = 0; i < chika2.size(); i++) {
			String jp_ad = chika2.get(i).getJp_ad();
			if (!th.contains(jp_ad)) {
				th.add(jp_ad);
			}
			sameRowFlag = true;
			if (!placeName.equals(chika2.get(i).getPlace_name())) {
				placeName = chika2.get(i).getPlace_name();
				sameRowFlag = false;
			}
			if (!category.equals(chika2.get(i).getCategory())) {
				category = chika2.get(i).getCategory();
				sameRowFlag = false;
			}
			if (!area.equals(chika2.get(i).getArea())) {
				area = chika2.get(i).getArea();
				sameRowFlag = false;
			}

			if (!sameRowFlag) {
				RowNum++;
			}
		}

		List<Map<String, Object>> data = new ArrayList<>();
		int headerCnt = CHIKA2_STATIC_DATA_NUM;
		int j = 0;
		List<String> tempDataWareki = null;
		for (int i = 0; i < (th.size() - CHIKA2_STATIC_DATA_NUM) * RowNum; i++) {
			Map<String, Object> td = new HashMap<>();
			String jp_ad = chika2.get(j).getJp_ad();
			if (i % (th.size() - CHIKA2_STATIC_DATA_NUM) != 0) {
				tempDataWareki.add(jp_ad);
				if (tempDataWareki.contains(th.get(++headerCnt))) {
					data.get(data.size() - 1).put(jp_ad, chika2.get(j).getLand_price());
					j++;
				} else {
					data.get(data.size() - 1).put(th.get(headerCnt), null);
				}
			} else {
				headerCnt = CHIKA2_STATIC_DATA_NUM;
				tempDataWareki = new ArrayList<>();
				tempDataWareki.add(jp_ad);
				td.put(th.get(0), chika2.get(j).getPlace_name());
				td.put(th.get(1), chika2.get(j).getCategory());
				td.put(th.get(2), chika2.get(j).getArea());
				if (tempDataWareki.contains(th.get(headerCnt))) {
					td.put(jp_ad, chika2.get(j).getLand_price());
					j++;
				} else {
					td.put(th.get(headerCnt), null);
				}
				data.add(td);
			}
		}
		table.put("header", th);
		table.put("data", data);
		return table;
	}

	/**
	 * 活動前と比較した認知度の推移のテーブル作成
	 */
	public Map<String, Object> createErimaneNinchidoTable() {
		Map<String, Object> table = new HashMap<>();
		List<String> th = new ArrayList<>();
		//固定ヘッダ
		th.add("エリア");

		List<ErimaneNinchido> erimaneNinchido = erimaneNinchidoRepository.findErimaneNinchido();
		int RowNum = 0;
		String area = "";
		boolean sameRowFlag = true;
		//ヘッダ作成
		for (int i = 0; i < erimaneNinchido.size(); i++) {
			String jp_ad = erimaneNinchido.get(i).getJp_ad();
			if (!th.contains(jp_ad)) {
				th.add(jp_ad);
			}
			sameRowFlag = true;
			if (!area.equals(erimaneNinchido.get(i).getArea())) {
				area = erimaneNinchido.get(i).getArea();
				sameRowFlag = false;
			}

			if (!sameRowFlag) {
				RowNum++;
			}
		}

		List<Map<String, Object>> data = new ArrayList<>();
		int headerCnt = NINCHIDO_STATIC_DATA_NUM;
		int j = 0;
		List<String> tempDataWareki = null;
		for (int i = 0; i < (th.size() - NINCHIDO_STATIC_DATA_NUM) * RowNum; i++) {
			String jp_ad = erimaneNinchido.get(j).getJp_ad();
			Map<String, Object> td = new HashMap<>();
			String ninchidoStr = erimaneNinchido.get(j).getNinchido() * 100 + "";
			if (i % (th.size() - NINCHIDO_STATIC_DATA_NUM) != 0) {
				tempDataWareki.add(jp_ad);
				if (tempDataWareki.contains(th.get(++headerCnt))) {
					data.get(data.size() - 1).put(jp_ad, Integer.parseInt(ninchidoStr.split("\\.")[0]) + "%");
					j++;
				} else {
					data.get(data.size() - 1).put(th.get(headerCnt), null);
				}

			} else {
				headerCnt = NINCHIDO_STATIC_DATA_NUM;
				tempDataWareki = new ArrayList<>();
				tempDataWareki.add(jp_ad);
				td.put(th.get(0), erimaneNinchido.get(j).getArea());
				td.put(jp_ad, Integer.parseInt(ninchidoStr.split("\\.")[0]) + "%");
				if (tempDataWareki.contains(th.get(headerCnt))) {
					td.put(jp_ad, Integer.parseInt(ninchidoStr.split("\\.")[0]) + "%");
					j++;
				} else {
					td.put(th.get(headerCnt), null);
				}
				data.add(td);
			}
		}
		table.put("header", th);
		table.put("data", data);
		return table;
	}

	/**
	 * GIS統合系の統計項目のテーブル作成
	 * @param categoryList
	 * @return
	 */
	public Map<String, Object> createGisJoint2Table(String itemName) {
		Map<String, Object> table = new HashMap<>();
		List<String> th = new ArrayList<>();
		//固定ヘッダ
		th.add("地点名");
		th.add("エリマネ");

		int[] categoryHeaderNum = new int[100];
		List<GisJoint2> gisJoint2 = null;
		switch (itemName) {
		case "gis_joint2_household":
			categoryHeaderNum = new int[2];
			gisJoint2 = gisJoint2Repository.findGisJoint2ByJinkoSetai();
			break;
		case "gis_joint2_population":
			categoryHeaderNum = new int[3];
			gisJoint2 = gisJoint2Repository.findGisJoint2ByNenreiJinko();
			break;
		case "gis_joint2_size":
			categoryHeaderNum = new int[5];
			gisJoint2 = gisJoint2Repository.findGisJoint2BySetaijinin();
			break;
		case "gis_joint2_office":
			categoryHeaderNum = new int[1];
			gisJoint2 = gisJoint2Repository.findGisJoint2ByJimusyo();
			break;
		case "gis_joint2_employee":
			categoryHeaderNum = new int[1];
			gisJoint2 = gisJoint2Repository.findGisJoint2ByJugyosya();
			break;
		default:
			break;
		}

		int RowNum = 0;
		List<String> altCategoryList = new ArrayList<>();
		String currentCategory = "";
		//ヘッダ作成
		int j = -1;
		for (int i = 0; i < gisJoint2.size(); i++) {
			if (!currentCategory.equals(gisJoint2.get(i).getCategory())) {
				currentCategory = gisJoint2.get(i).getCategory();
				altCategoryList.add(currentCategory);
				j++;
			}
			String jp_ad = gisJoint2.get(i).getJp_ad() + "_" + altCategoryList.get(j);
			if (!th.contains(jp_ad)) {
				categoryHeaderNum[j]++;
				th.add(jp_ad);
			}

		}
		//行数計算
		List<String> placeName = new ArrayList<>();
		List<String> erimane = new ArrayList<>();
		boolean sameRowFlag = true;
		for (int i = 0; i < gisJoint2.size(); i++) {
			sameRowFlag = true;
			if (!placeName.contains(gisJoint2.get(i).getPlace_name())) {
				placeName.add(gisJoint2.get(i).getPlace_name());
				sameRowFlag = false;
			}
			if (!erimane.contains(gisJoint2.get(i).getErimane())) {
				erimane.add(gisJoint2.get(i).getErimane());
				sameRowFlag = false;
			}

			if (!sameRowFlag) {
				RowNum++;
			}
		}
		j = 0;
		int k = -1;
		int dataIndex = -1;
		int indexCnt = 0;
		currentCategory = "";
		List<Map<String, Object>> data = new ArrayList<>();
		int headerCnt = GIS_JOINT2_DATA_NUM;
		int constHeaderCnt = GIS_JOINT2_DATA_NUM;
		List<String> tempDataWareki = null;
		for (int i = 0; i < (th.size() - GIS_JOINT2_DATA_NUM) * RowNum; i++) {
			if (!currentCategory.equals(gisJoint2.get(j).getCategory())) {
				currentCategory = gisJoint2.get(j).getCategory();
				if (k >= 0) {
					constHeaderCnt += categoryHeaderNum[k];
					indexCnt = i;
				}
				k++;
				dataIndex = -1;
			}
			String jp_ad = gisJoint2.get(j).getJp_ad() + "_" + altCategoryList.get(k);
			if (k == 0) {
				Map<String, Object> td = new HashMap<>();
				if ((i % categoryHeaderNum[k]) != 0) {
					tempDataWareki.add(jp_ad);
					if (tempDataWareki.contains(th.get(++headerCnt))) {
						data.get(data.size() - 1).put(jp_ad, gisJoint2.get(j).getNumber());
						j++;
					} else {
						data.get(data.size() - 1).put(th.get(headerCnt), null);
					}
				} else {
					headerCnt = constHeaderCnt;
					tempDataWareki = new ArrayList<>();
					tempDataWareki.add(jp_ad);
					td.put(th.get(0), gisJoint2.get(j).getPlace_name());
					td.put(th.get(1), gisJoint2.get(j).getErimane());
					td.put(jp_ad, gisJoint2.get(j).getNumber());
					if (tempDataWareki.contains(th.get(headerCnt))) {
						td.put(jp_ad, gisJoint2.get(j).getNumber());
						j++;
					} else {
						td.put(th.get(headerCnt), null);
					}
					data.add(td);
				}
			} else {
				if (((i - indexCnt) % categoryHeaderNum[k]) == 0) {
					headerCnt = constHeaderCnt;
					tempDataWareki = new ArrayList<>();
					dataIndex++;
				}
				tempDataWareki.add(jp_ad);
				if (tempDataWareki.contains(th.get(headerCnt))) {
					data.get(dataIndex).put(jp_ad, gisJoint2.get(j).getNumber());
					j++;
				} else {
					data.get(dataIndex).put(th.get(headerCnt), null);
				}
				headerCnt++;
			}
		}
		table.put("header", th);
		table.put("data", data);
		return table;
	}

	/**
	 * 駅の乗降客数をテーブル表示
	 * @return
	 */
	public Map<String, Object> createStationUsers() {
		Map<String, Object> table = new HashMap<>();
		List<String> th = new ArrayList<>();
		//固定ヘッダ
		th.add("会社名");

		List<StationUsers> stationUsers = stationUsersRepository.findStationUsers();
		int RowNum = 0;
		String officeName = "";
		boolean sameRowFlag = true;
		//ヘッダ作成
		for (int i = 0; i < stationUsers.size(); i++) {
			String ad = stationUsers.get(i).getYear();
			if (!th.contains(ad)) {
				th.add(ad);
			}
			sameRowFlag = true;
			if (!officeName.equals(stationUsers.get(i).getOffice_name())) {
				officeName = stationUsers.get(i).getOffice_name();
				sameRowFlag = false;
			}

			if (!sameRowFlag) {
				RowNum++;
			}
		}

		List<Map<String, Object>> data = new ArrayList<>();
		int headerCnt = STATION_USERS_DATA_NUM;
		int j = 0;
		List<String> tempDataWareki = null;
		for (int i = 0; i < (th.size() - STATION_USERS_DATA_NUM) * RowNum; i++) {
			String ad = stationUsers.get(j).getYear();
			Map<String, Object> td = new HashMap<>();
			if (i % (th.size() - STATION_USERS_DATA_NUM) != 0) {
				tempDataWareki.add(ad);
				if (tempDataWareki.contains(th.get(++headerCnt))) {
					data.get(data.size() - 1).put(ad, stationUsers.get(j).getUser_num());
					j++;
				} else {
					data.get(data.size() - 1).put(th.get(headerCnt), null);
				}

			} else {
				headerCnt = STATION_USERS_DATA_NUM;
				tempDataWareki = new ArrayList<>();
				tempDataWareki.add(ad);
				td.put(th.get(0), stationUsers.get(j).getOffice_name());
				td.put(ad, stationUsers.get(j).getUser_num());
				if (tempDataWareki.contains(th.get(headerCnt))) {
					td.put(ad, stationUsers.get(j).getUser_num());
					j++;
				} else {
					td.put(th.get(headerCnt), null);
				}
				data.add(td);
			}
		}
		table.put("header", th);
		table.put("data", data);
		return table;
	}

	/**
	 * 商業施設をテーブル表示
	 * @return
	 */
	public Map<String, Object> createSyogyoshisetsu() {
		Map<String, Object> table = new HashMap<>();
		List<String> th = new ArrayList<>();
		//固定ヘッダ
		th.add("店舗名");
		th.add("住所");
		th.add("開設年");
		th.add("店舗面積");

		List<Syogyoshisetsu> syogyoshisetsu = syogyoshisetsuRepository.findSyogyoshisetsu();

		List<Map<String, Object>> data = new ArrayList<>();
		for (int i = 0; i < syogyoshisetsu.size(); i++) {
			Map<String, Object> td = new HashMap<>();
			td.put(th.get(0), syogyoshisetsu.get(i).getShop_name());
			td.put(th.get(1), syogyoshisetsu.get(i).getAddress());
			if (syogyoshisetsu.get(i).getYear() == null) {
				td.put(th.get(2), null);
			} else {
				td.put(th.get(2), syogyoshisetsu.get(i).getYear() + "");
			}

			td.put(th.get(3), syogyoshisetsu.get(i).getShop_area());
			data.add(td);
		}
		table.put("header", th);
		table.put("data", data);
		return table;
	}

	/**
	 * 商圏調査の支持率のテーブル表示
	 * @return
	 */
	public Map<String, Object> createSyokencyosaShijiritsu() {
		Map<String, Object> table = new HashMap<>();
		List<String> th = new ArrayList<>();
		//固定ヘッダ
		th.add("商圏エリア");
		th.add("割合");

		List<SyokencyosaShijiritsu> syokencyosaShijiritsu = syokencyosaShijiritsuRepository.findSyokencyosaShijiritsu();

		List<Map<String, Object>> data = new ArrayList<>();
		for (int i = 0; i < syokencyosaShijiritsu.size(); i++) {
			Map<String, Object> td = new HashMap<>();
			td.put(th.get(0), syokencyosaShijiritsu.get(i).getSyoken_area());
			td.put(th.get(1), syokencyosaShijiritsu.get(i).getRatio());
			data.add(td);
		}
		table.put("header", th);
		table.put("data", data);
		return table;
	}

	/**
	 * 市まとめのテーブル表示
	 * @return
	 */
	public Map<String, Object> createCitySummary() {
		Map<String, Object> table = new HashMap<>();
		List<String> th = new ArrayList<>();
		//固定ヘッダ
		th.add("");

		List<CitySummary> citySummary = citySummaryRepository.findCitySummary();
		int RowNum = 0;
		String categoryName = "";
		boolean sameRowFlag = true;
		//ヘッダ作成
		for (int i = 0; i < citySummary.size(); i++) {
			String ad = citySummary.get(i).getEra_jp();
			if (!th.contains(ad)) {
				th.add(ad);
			}
			sameRowFlag = true;
			if (!categoryName.equals(citySummary.get(i).getCategory())) {
				categoryName = citySummary.get(i).getCategory();
				sameRowFlag = false;
			}

			if (!sameRowFlag) {
				RowNum++;
			}
		}

		List<Map<String, Object>> data = new ArrayList<>();
		int headerCnt = CITY_SUMMARY_DATA_NUM;
		int j = 0;
		List<String> tempDataWareki = null;
		for (int i = 0; i < (th.size() - CITY_SUMMARY_DATA_NUM) * RowNum; i++) {
			String ad = citySummary.get(j).getEra_jp();
			Map<String, Object> td = new HashMap<>();
			if (i % (th.size() - CITY_SUMMARY_DATA_NUM) != 0) {
				tempDataWareki.add(ad);
				if (tempDataWareki.contains(th.get(++headerCnt))) {
					data.get(data.size() - 1).put(ad, citySummary.get(j).getValue());
					j++;
				} else {
					data.get(data.size() - 1).put(th.get(headerCnt), null);
				}
			} else {
				headerCnt = CITY_SUMMARY_DATA_NUM;
				tempDataWareki = new ArrayList<>();
				tempDataWareki.add(ad);
				td.put(th.get(0), citySummary.get(j).getCategory());
				td.put(ad, citySummary.get(j).getValue());
				if (tempDataWareki.contains(th.get(headerCnt))) {
					td.put(ad, citySummary.get(j).getValue());
					j++;
				} else {
					td.put(th.get(headerCnt), null);
				}
				data.add(td);
			}
		}
		table.put("header", th);
		table.put("data", data);
		return table;
	}

	/**
	 * 回遊性_来場者人数のテーブル表示
	 * @return
	 */
	public Map<String, Object> createKaiyuseiPeople() {
		Map<String, Object> table = new HashMap<>();
		List<String> th = new ArrayList<>();
		//固定ヘッダ
		th.add("日付");
		th.add("利用者数");
		th.add("回数");

		List<KaiyuseiPeople> kaiyuseiPeople = kaiyuPeopleRepository.findKaiyuPeople();

		List<Map<String, Object>> data = new ArrayList<>();
		for (int i = 0; i < kaiyuseiPeople.size(); i++) {
			Map<String, Object> td = new HashMap<>();
			td.put(th.get(0), hyphensdf.format(kaiyuseiPeople.get(i).getDate()));
			td.put(th.get(1), kaiyuseiPeople.get(i).getUsers());
			td.put(th.get(2), kaiyuseiPeople.get(i).getNumber());
			data.add(td);
		}
		table.put("header", th);
		table.put("data", data);
		return table;
	}

	/**
	 * 回遊性_来場者年齢のテーブル表示
	 * @return
	 */
	public Map<String, Object> createKaiyuseiAge() {
		Map<String, Object> table = new HashMap<>();
		List<String> th = new ArrayList<>();
		//固定ヘッダ
		th.add("項目");
		th.add("人数");
		th.add("割合");
		th.add("回数");

		List<KaiyuseiAge> kaiyuseiAge = kaiyuAgeRepository.findKaiyuAge();

		List<Map<String, Object>> data = new ArrayList<>();
		for (int i = 0; i < kaiyuseiAge.size(); i++) {
			Map<String, Object> td = new HashMap<>();
			td.put(th.get(0), kaiyuseiAge.get(i).getItem());
			td.put(th.get(1), kaiyuseiAge.get(i).getUsers());
			td.put(th.get(2), kaiyuseiAge.get(i).getRate());
			td.put(th.get(3), kaiyuseiAge.get(i).getNumber());
			data.add(td);
		}
		table.put("header", th);
		table.put("data", data);
		return table;
	}

	/**
	 * 回遊性_来場者性別のテーブル表示
	 * @return
	 */
	public Map<String, Object> createKaiyuseiGender() {
		Map<String, Object> table = new HashMap<>();
		List<String> th = new ArrayList<>();
		//固定ヘッダ
		th.add("性別");
		th.add("人数");
		th.add("割合");
		th.add("回数");

		List<KaiyuseiGender> kaiyuseiGender = kaiyuGenderRepository.findKaiyuGender();

		List<Map<String, Object>> data = new ArrayList<>();
		for (int i = 0; i < kaiyuseiGender.size(); i++) {
			Map<String, Object> td = new HashMap<>();
			td.put(th.get(0), kaiyuseiGender.get(i).getGender());
			td.put(th.get(1), kaiyuseiGender.get(i).getUsers());
			td.put(th.get(2), kaiyuseiGender.get(i).getRate());
			td.put(th.get(3), kaiyuseiGender.get(i).getNumber());
			data.add(td);
		}
		table.put("header", th);
		table.put("data", data);
		return table;
	}

	/**
	 * 回遊性_来場者地域のテーブル表示
	 * @return
	 */
	public Map<String, Object> createKaiyuseiRegion() {
		Map<String, Object> table = new HashMap<>();
		List<String> th = new ArrayList<>();
		//固定ヘッダ
		th.add("住所");
		th.add("人数");
		th.add("割合");
		th.add("回数");

		List<KaiyuseiRegion> kaiyuseiRegion = kaiyuRegionRepository.findKaiyuRegion();

		List<Map<String, Object>> data = new ArrayList<>();
		for (int i = 0; i < kaiyuseiRegion.size(); i++) {
			Map<String, Object> td = new HashMap<>();
			td.put(th.get(0), kaiyuseiRegion.get(i).getAddress());
			td.put(th.get(1), kaiyuseiRegion.get(i).getUsers());
			td.put(th.get(2), kaiyuseiRegion.get(i).getRate());
			td.put(th.get(3), kaiyuseiRegion.get(i).getNumber());
			data.add(td);
		}
		table.put("header", th);
		table.put("data", data);
		return table;
	}

	/**
	 * 回遊性_来場者歩数のテーブル表示
	 * @return
	 */
	public Map<String, Object> createKaiyuseiSteps() {
		Map<String, Object> table = new HashMap<>();
		List<String> th = new ArrayList<>();
		//固定ヘッダ
		th.add("日付");
		th.add("歩数");
		th.add("天気");
		th.add("最低気温");
		th.add("最高気温");
		th.add("回数");

		List<KaiyuseiSteps> kaiyuseiSteps = kaiyuStepsRepository.findKaiyuSteps();

		List<Map<String, Object>> data = new ArrayList<>();
		for (int i = 0; i < kaiyuseiSteps.size(); i++) {
			Map<String, Object> td = new HashMap<>();
			td.put(th.get(0), hyphensdf.format(kaiyuseiSteps.get(i).getDate()));
			td.put(th.get(1), kaiyuseiSteps.get(i).getStep());
			td.put(th.get(2), kaiyuseiSteps.get(i).getWeather());
			td.put(th.get(3), kaiyuseiSteps.get(i).getMin_temp());
			td.put(th.get(4), kaiyuseiSteps.get(i).getMax_temp());
			td.put(th.get(5), kaiyuseiSteps.get(i).getNumber());
			data.add(td);
		}
		table.put("header", th);
		table.put("data", data);
		return table;
	}
}
