package view3d.service;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import view3d.entity.Chika2;
import view3d.entity.CitySummary;
import view3d.entity.ErimaneNinchido;
import view3d.entity.GisJoint2;
import view3d.entity.KaiyuseiAge;
import view3d.entity.KaiyuseiGender;
import view3d.entity.KaiyuseiPeople;
import view3d.entity.KaiyuseiRegion;
import view3d.entity.KaiyuseiSteps;
import view3d.entity.StationUsers;
import view3d.entity.Syogyoshisetsu;
import view3d.entity.SyokencyosaShijiritsu;
import view3d.repository.Chika2Repository;
import view3d.repository.CitySummaryRepository;
import view3d.repository.ErimaneNinchidoRepository;
import view3d.repository.GisJoint2Repository;
import view3d.repository.KaiyuAgeRepository;
import view3d.repository.KaiyuGenderRepository;
import view3d.repository.KaiyuPeopleRepository;
import view3d.repository.KaiyuRegionRepository;
import view3d.repository.KaiyuStepsRepository;
import view3d.repository.StationUsersRepository;
import view3d.repository.SyogyoshisetsuRepository;
import view3d.repository.SyokencyosaShijiritsuRepository;

@Service
public class CsvDownloadService {

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
	 * 地域表示価格のCSVダウンロード
	 */
	public Map<String, List<String>> createChika2CsvData() {
		Map<String, List<String>> res = new HashMap<>();
		List<String> csvHeader = new ArrayList<>();
		List<String> csvData = new ArrayList<>();
		//固定ヘッダ
		csvHeader.add("");
		csvHeader.add("区分");
		csvHeader.add("エリア");
		List<Chika2> chika2 = chika2Repository.findChika2();
		int RowNum = 0;
		String placeName = "";
		String category = "";
		String area = "";
		boolean sameRowFlag = true;
		//ヘッダ作成
		for (int i = 0; i < chika2.size(); i++) {
			String jp_ad = chika2.get(i).getJp_ad();
			if (!csvHeader.contains(jp_ad)) {
				csvHeader.add(jp_ad);
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

		int headerCnt = CHIKA2_STATIC_DATA_NUM;
		int j = 0;
		List<String> tempDataWareki = null;
		for (int i = 0; i < (csvHeader.size() - CHIKA2_STATIC_DATA_NUM) * RowNum; i++) {
			String jp_ad = chika2.get(j).getJp_ad();
			if (i % (csvHeader.size() - CHIKA2_STATIC_DATA_NUM) != 0) {
				tempDataWareki.add(jp_ad);
				if (tempDataWareki.contains(csvHeader.get(++headerCnt))) {
					if (chika2.get(j).getLand_price() == null) {
						csvData.add("");
					} else {
						csvData.add(chika2.get(j).getLand_price() + "");
					}
					j++;
				} else {
					csvData.add("");
				}
			} else {
				headerCnt = CHIKA2_STATIC_DATA_NUM;
				tempDataWareki = new ArrayList<>();
				tempDataWareki.add(jp_ad);
				csvData.add(chika2.get(j).getPlace_name().trim());
				csvData.add(chika2.get(j).getCategory().trim());
				csvData.add(chika2.get(j).getArea().trim());
				if (tempDataWareki.contains(csvHeader.get(headerCnt))) {
					if (chika2.get(j).getLand_price() == null) {
						csvData.add("");
					} else {
						csvData.add(chika2.get(j).getLand_price() + "");
					}
					j++;
				} else {
					csvData.add("");
				}
			}
		}

		for (int i = 0; i < csvHeader.size(); i++) {
			csvHeader.set(i, csvHeader.get(i).trim());
		}

		res.put("header", csvHeader);
		res.put("data", csvData);
		return res;
	}

	/**
	 * 活動前と比較した認知度の推移のCSVダウンロード
	 */
	public Map<String, List<String>> createNinchidoCsvData() {
		Map<String, List<String>> res = new HashMap<>();
		List<String> csvHeader = new ArrayList<>();
		List<String> csvData = new ArrayList<>();
		//固定ヘッダ
		csvHeader.add("");

		List<ErimaneNinchido> erimaneNinchido = erimaneNinchidoRepository.findErimaneNinchido();
		int RowNum = 0;
		String area = "";
		boolean sameRowFlag = true;
		for (int i = 0; i < erimaneNinchido.size(); i++) {
			sameRowFlag = true;
			if (!area.equals(erimaneNinchido.get(i).getArea())) {
				area = erimaneNinchido.get(i).getArea();
				sameRowFlag = false;
			}

			String jp_ad = erimaneNinchido.get(i).getJp_ad();
			if (!csvHeader.contains(jp_ad)) {
				csvHeader.add(jp_ad);
			}

			if (!sameRowFlag) {
				RowNum++;
			}
		}
		int headerCnt = NINCHIDO_STATIC_DATA_NUM;
		int j = 0;
		List<String> tempDataWareki = null;
		for (int i = 0; i < (csvHeader.size() - NINCHIDO_STATIC_DATA_NUM) * RowNum; i++) {
			String jp_ad = erimaneNinchido.get(i).getJp_ad();
			if (i % (csvHeader.size() - NINCHIDO_STATIC_DATA_NUM) != 0) {
				tempDataWareki.add(jp_ad);
				if (tempDataWareki.contains(csvHeader.get(++headerCnt))) {
					if (erimaneNinchido.get(j).getNinchido() == null) {
						csvData.add("");
					} else {
						csvData.add(erimaneNinchido.get(i).getNinchido() + "");
					}
					j++;
				} else {
					csvData.add("");
				}
			} else {
				headerCnt = NINCHIDO_STATIC_DATA_NUM;
				tempDataWareki = new ArrayList<>();
				tempDataWareki.add(jp_ad);
				csvData.add(erimaneNinchido.get(i).getArea().trim());
				if (tempDataWareki.contains(csvHeader.get(headerCnt))) {
					if (erimaneNinchido.get(j).getNinchido() == null) {
						csvData.add("");
					} else {
						csvData.add(erimaneNinchido.get(i).getNinchido() + "");
					}
					j++;
				} else {
					csvData.add("");
				}
			}
		}
		for (int i = 0; i < csvHeader.size(); i++) {
			csvHeader.set(i, csvHeader.get(i).trim());
		}

		res.put("header", csvHeader);
		res.put("data", csvData);
		return res;
	}

	/**
	 * GIS統合系の統計項目のテーブル作成
	 * @param categoryList
	 * @return
	 */
	public Map<String, List<String>> createGisJoint2CsvData(String itemName) {
		Map<String, List<String>> res = new HashMap<>();
		List<String> csvHeader = new ArrayList<>();
		List<String> csvData = new ArrayList<>();
		//固定ヘッダ
		csvHeader.add("地点名");
		csvHeader.add("エリマネ");

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
			if (!csvHeader.contains(jp_ad)) {
				categoryHeaderNum[j]++;
				csvHeader.add(jp_ad);
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
		int headerCnt = GIS_JOINT2_DATA_NUM;
		int conscsvHeadereaderCnt = GIS_JOINT2_DATA_NUM;
		List<String> tempDataWareki = null;
		int tmpRowCnt = 0;
		for (int i = 0; i < (csvHeader.size() - GIS_JOINT2_DATA_NUM) * RowNum; i++) {
			if (!currentCategory.equals(gisJoint2.get(j).getCategory())) {
				currentCategory = gisJoint2.get(j).getCategory();
				if (k >= 0) {
					conscsvHeadereaderCnt += categoryHeaderNum[k];
					tmpRowCnt = 0;
					indexCnt = i;
				}
				k++;
				dataIndex = -1;
			}
			String jp_ad = gisJoint2.get(j).getJp_ad() + "_" + altCategoryList.get(k);
			if (k == 0) {
				if ((i % categoryHeaderNum[k]) != 0) {
					tempDataWareki.add(jp_ad);
					if (tempDataWareki.contains(csvHeader.get(++headerCnt))) {
						if (gisJoint2.get(j).getNumber() == null) {
							csvData.add("");
						} else {
							csvData.add(gisJoint2.get(j).getNumber() + "");
						}
						j++;
					} else {
						csvData.add("");
					}
				} else {
					headerCnt = conscsvHeadereaderCnt;
					tempDataWareki = new ArrayList<>();
					tempDataWareki.add(jp_ad);
					csvData.add(gisJoint2.get(j).getPlace_name().trim());
					csvData.add(gisJoint2.get(j).getErimane().trim());
					if (tempDataWareki.contains(csvHeader.get(headerCnt))) {
						if (gisJoint2.get(j).getNumber() == null) {
							csvData.add("");
						} else {
							csvData.add(gisJoint2.get(j).getNumber() + "");
						}
						j++;
					} else {
						csvData.add("");
					}
				}
			} else {
				if (((i - indexCnt) % categoryHeaderNum[k]) == 0) {
					tmpRowCnt++;
					headerCnt = conscsvHeadereaderCnt;
					tempDataWareki = new ArrayList<>();
					dataIndex = tmpRowCnt * conscsvHeadereaderCnt + (tmpRowCnt - 1) * categoryHeaderNum[k];
				}
				tempDataWareki.add(jp_ad);
				if (tempDataWareki.contains(csvHeader.get(headerCnt))) {
					if (gisJoint2.get(j).getNumber() == null) {
						csvData.add(dataIndex, "");
					} else {
						csvData.add(dataIndex, gisJoint2.get(j).getNumber() + "");
					}
					j++;
				} else {
					csvData.add(dataIndex, "");
				}
				dataIndex++;
				headerCnt++;
			}
		}

		for (int i = 0; i < csvHeader.size(); i++) {
			csvHeader.set(i, csvHeader.get(i).replaceAll(" ", ""));
		}

		res.put("header", csvHeader);
		res.put("data", csvData);
		return res;
	}

	/**
	 * 駅の乗降客数のCSVダウンロード
	 * @return
	 */
	public Map<String, List<String>> createStationUsersCsvData() {
		Map<String, List<String>> res = new HashMap<>();
		List<String> csvHeader = new ArrayList<>();
		List<String> csvData = new ArrayList<>();
		//固定ヘッダ
		csvHeader.add("");

		List<StationUsers> stationUsers = stationUsersRepository.findStationUsers();
		int RowNum = 0;
		String officeName = "";
		boolean sameRowFlag = true;
		//ヘッダ作成
		for (int i = 0; i < stationUsers.size(); i++) {
			String ad = stationUsers.get(i).getYear();
			if (!csvHeader.contains(ad)) {
				csvHeader.add(ad);
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

		int headerCnt = STATION_USERS_DATA_NUM;
		int j = 0;
		List<String> tempDataWareki = null;
		for (int i = 0; i < (csvHeader.size() - STATION_USERS_DATA_NUM) * RowNum; i++) {
			String ad = stationUsers.get(j).getYear();
			if (i % (csvHeader.size() - STATION_USERS_DATA_NUM) != 0) {
				tempDataWareki.add(ad);
				if (tempDataWareki.contains(csvHeader.get(++headerCnt))) {
					if (stationUsers.get(j).getUser_num() == null) {
						csvData.add("");
					} else {
						csvData.add(stationUsers.get(j).getUser_num() + "");
					}
					j++;
				} else {
					csvData.add("");
				}
			} else {
				headerCnt = STATION_USERS_DATA_NUM;
				tempDataWareki = new ArrayList<>();
				tempDataWareki.add(ad);
				csvData.add(stationUsers.get(j).getOffice_name().trim());
				if (tempDataWareki.contains(csvHeader.get(headerCnt))) {
					if (stationUsers.get(j).getUser_num() == null) {
						csvData.add("");
					} else {
						csvData.add(stationUsers.get(j).getUser_num() + "");
					}
					j++;
				} else {
					csvData.add("");
				}
			}
		}

		for (int i = 0; i < csvHeader.size(); i++) {
			csvHeader.set(i, csvHeader.get(i).trim());
		}

		res.put("header", csvHeader);
		res.put("data", csvData);
		return res;
	}

	/**
	 * 商業施設をCSVダウンロード
	 * @return
	 */
	public Map<String, List<String>> createSyogyoshisetsu() {
		Map<String, List<String>> res = new HashMap<>();
		List<String> csvHeader = new ArrayList<>();
		List<String> csvData = new ArrayList<>();
		//固定ヘッダ
		csvHeader.add("店舗名");
		csvHeader.add("住所");
		csvHeader.add("開設年");
		csvHeader.add("店舗面積");

		List<Syogyoshisetsu> syogyoshisetsu = syogyoshisetsuRepository.findSyogyoshisetsu();

		for (int i = 0; i < syogyoshisetsu.size(); i++) {
			csvData.add(syogyoshisetsu.get(i).getShop_name());
			if (syogyoshisetsu.get(i).getAddress() == null) {
				csvData.add("");
			} else {
				csvData.add(syogyoshisetsu.get(i).getAddress());
			}
			if (syogyoshisetsu.get(i).getYear() == null) {
				csvData.add("");
			} else {
				csvData.add(syogyoshisetsu.get(i).getYear() + "");
			}
			if (syogyoshisetsu.get(i).getShop_area() == null) {
				csvData.add("");
			} else {
				csvData.add(syogyoshisetsu.get(i).getShop_area() + "");
			}
		}

		for (int i = 0; i < csvHeader.size(); i++) {
			csvHeader.set(i, csvHeader.get(i).trim());
		}

		res.put("header", csvHeader);
		res.put("data", csvData);
		return res;
	}

	/**
	 * 商圏調査の支持率をCSVダウンロード
	 *
	 * @return
	 */
	public Map<String, List<String>> createSyokencyosaShijiritsuCsvData() {
		Map<String, List<String>> res = new HashMap<>();
		List<String> csvHeader = new ArrayList<>();
		List<String> csvData = new ArrayList<>();
		//固定ヘッダ
		csvHeader.add("商圏エリア");
		csvHeader.add("割合");

		List<SyokencyosaShijiritsu> syokencyosaShijiritsu = syokencyosaShijiritsuRepository.findSyokencyosaShijiritsu();

		for (int i = 0; i < syokencyosaShijiritsu.size(); i++) {
			csvData.add(syokencyosaShijiritsu.get(i).getSyoken_area());
			csvData.add(syokencyosaShijiritsu.get(i).getRatio() + "");

		}

		for (int i = 0; i < csvHeader.size(); i++) {
			csvHeader.set(i, csvHeader.get(i).trim());
		}

		res.put("header", csvHeader);
		res.put("data", csvData);
		return res;
	}

	/**
	 * 市まとめをCSVダウンロード
	 * @return
	 */
	public Map<String, List<String>> createCitySummaryCsvData() {
		Map<String, List<String>> res = new HashMap<>();
		List<String> csvHeader = new ArrayList<>();
		List<String> csvData = new ArrayList<>();
		//固定ヘッダ
		csvHeader.add("");

		List<CitySummary> citySummary = citySummaryRepository.findCitySummary();
		int RowNum = 0;
		String categoryName = "";
		boolean sameRowFlag = true;
		//ヘッダ作成
		for (int i = 0; i < citySummary.size(); i++) {
			String ad = citySummary.get(i).getEra_jp();
			if (!csvHeader.contains(ad)) {
				csvHeader.add(ad);
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

		int headerCnt = CITY_SUMMARY_DATA_NUM;
		int j = 0;
		List<String> tempDataWareki = null;
		for (int i = 0; i < (csvHeader.size() - CITY_SUMMARY_DATA_NUM) * RowNum; i++) {
			String ad = citySummary.get(j).getEra_jp();
			if (i % (csvHeader.size() - CITY_SUMMARY_DATA_NUM) != 0) {
				tempDataWareki.add(ad);
				if (tempDataWareki.contains(csvHeader.get(++headerCnt))) {
					if (citySummary.get(j).getValue() == null) {
						csvData.add("");
					} else {
						csvData.add(citySummary.get(j).getValue() + "");
					}
					j++;
				} else {
					csvData.add("");
				}
			} else {
				headerCnt = CITY_SUMMARY_DATA_NUM;
				tempDataWareki = new ArrayList<>();
				tempDataWareki.add(ad);
				csvData.add(citySummary.get(j).getCategory().trim());
				if (tempDataWareki.contains(csvHeader.get(headerCnt))) {
					if (citySummary.get(j).getValue() == null) {
						csvData.add("");
					} else {
						csvData.add(citySummary.get(j).getValue() + "");
					}
					j++;
				} else {
					csvData.add("");
				}
			}
		}

		for (int i = 0; i < csvHeader.size(); i++) {
			csvHeader.set(i, csvHeader.get(i).trim());
		}

		res.put("header", csvHeader);
		res.put("data", csvData);
		return res;
	}

	/**
	 * 回遊性_来場者人数をCSVダウンロード
	 * @return
	 */
	public Map<String, List<String>> createKaiyuPeopleCsvData() {
		Map<String, List<String>> res = new HashMap<>();
		List<String> csvHeader = new ArrayList<>();
		List<String> csvData = new ArrayList<>();
		//固定ヘッダ
		csvHeader.add("日付");
		csvHeader.add("利用者数");
		csvHeader.add("回数");

		List<KaiyuseiPeople> kaiyuseiPeople = kaiyuPeopleRepository.findKaiyuPeople();

		for (int i = 0; i < kaiyuseiPeople.size(); i++) {
			csvData.add(hyphensdf.format(kaiyuseiPeople.get(i).getDate()));
			if (kaiyuseiPeople.get(i).getUsers() == null) {
				csvData.add("");
			} else {
				csvData.add(kaiyuseiPeople.get(i).getUsers() + "");
			}
			csvData.add(kaiyuseiPeople.get(i).getNumber() + "");
		}

		for (int i = 0; i < csvHeader.size(); i++) {
			csvHeader.set(i, csvHeader.get(i).trim());
		}

		res.put("header", csvHeader);
		res.put("data", csvData);
		return res;
	}

	/**
	 * 回遊性_来場者年齢をCSVダウンロード
	 * @return
	 */
	public Map<String, List<String>> createKaiyuAgeCsvData() {
		Map<String, List<String>> res = new HashMap<>();
		List<String> csvHeader = new ArrayList<>();
		List<String> csvData = new ArrayList<>();
		//固定ヘッダ
		csvHeader.add("項目");
		csvHeader.add("人数");
		csvHeader.add("割合");
		csvHeader.add("回数");

		List<KaiyuseiAge> kaiyuseiAge = kaiyuAgeRepository.findKaiyuAge();

		for (int i = 0; i < kaiyuseiAge.size(); i++) {
			csvData.add(kaiyuseiAge.get(i).getItem());
			if (kaiyuseiAge.get(i).getUsers() == null) {
				csvData.add("");
			} else {
				csvData.add(kaiyuseiAge.get(i).getUsers() + "");
			}
			if (kaiyuseiAge.get(i).getRate() == null) {
				csvData.add("");
			} else {
				csvData.add(kaiyuseiAge.get(i).getRate() + "");
			}
			csvData.add(kaiyuseiAge.get(i).getNumber() + "");
		}

		for (int i = 0; i < csvHeader.size(); i++) {
			csvHeader.set(i, csvHeader.get(i).trim());
		}

		res.put("header", csvHeader);
		res.put("data", csvData);
		return res;
	}

	/**
	 * 回遊性_来場者性別をCSVダウンロード
	 * @return
	 */
	public Map<String, List<String>> createKaiyuGenderCsvData() {
		Map<String, List<String>> res = new HashMap<>();
		List<String> csvHeader = new ArrayList<>();
		List<String> csvData = new ArrayList<>();
		//固定ヘッダ
		csvHeader.add("性別");
		csvHeader.add("人数");
		csvHeader.add("割合");
		csvHeader.add("回数");

		List<KaiyuseiGender> kaiyuseiGender = kaiyuGenderRepository.findKaiyuGender();

		for (int i = 0; i < kaiyuseiGender.size(); i++) {
			csvData.add(kaiyuseiGender.get(i).getGender());
			if (kaiyuseiGender.get(i).getUsers() == null) {
				csvData.add("");
			} else {
				csvData.add(kaiyuseiGender.get(i).getUsers() + "");
			}
			if (kaiyuseiGender.get(i).getRate() == null) {
				csvData.add("");
			} else {
				csvData.add(kaiyuseiGender.get(i).getRate() + "");
			}
			csvData.add(kaiyuseiGender.get(i).getNumber() + "");
		}

		for (int i = 0; i < csvHeader.size(); i++) {
			csvHeader.set(i, csvHeader.get(i).trim());
		}

		res.put("header", csvHeader);
		res.put("data", csvData);
		return res;
	}

	/**
	 * 回遊性_来場者地域をCSVダウンロード
	 * @return
	 */
	public Map<String, List<String>> createKaiyuRegionCsvData() {
		Map<String, List<String>> res = new HashMap<>();
		List<String> csvHeader = new ArrayList<>();
		List<String> csvData = new ArrayList<>();
		//固定ヘッダ
		csvHeader.add("住所");
		csvHeader.add("人数");
		csvHeader.add("割合");
		csvHeader.add("回数");

		List<KaiyuseiRegion> kaiyuseiRegion = kaiyuRegionRepository.findKaiyuRegion();

		for (int i = 0; i < kaiyuseiRegion.size(); i++) {
			csvData.add(kaiyuseiRegion.get(i).getAddress());
			if (kaiyuseiRegion.get(i).getUsers() == null) {
				csvData.add("");
			} else {
				csvData.add(kaiyuseiRegion.get(i).getUsers() + "");
			}
			if (kaiyuseiRegion.get(i).getRate() == null) {
				csvData.add("");
			} else {
				csvData.add(kaiyuseiRegion.get(i).getRate() + "");
			}
			csvData.add(kaiyuseiRegion.get(i).getNumber() + "");
		}

		for (int i = 0; i < csvHeader.size(); i++) {
			csvHeader.set(i, csvHeader.get(i).trim());
		}

		res.put("header", csvHeader);
		res.put("data", csvData);
		return res;
	}

	/**
	 * 回遊性_来場者歩数をCSVダウンロード
	 * @return
	 */
	public Map<String, List<String>> createKaiyuStepsCsvData() {
		Map<String, List<String>> res = new HashMap<>();
		List<String> csvHeader = new ArrayList<>();
		List<String> csvData = new ArrayList<>();
		//固定ヘッダ
		csvHeader.add("日付");
		csvHeader.add("歩数");
		csvHeader.add("天気");
		csvHeader.add("最低気温");
		csvHeader.add("最高気温");
		csvHeader.add("回数");

		List<KaiyuseiSteps> kaiyuseiSteps = kaiyuStepsRepository.findKaiyuSteps();

		for (int i = 0; i < kaiyuseiSteps.size(); i++) {
			csvData.add(hyphensdf.format(kaiyuseiSteps.get(i).getDate()));
			if (kaiyuseiSteps.get(i).getStep() == null) {
				csvData.add("");
			} else {
				csvData.add(kaiyuseiSteps.get(i).getStep() + "");
			}
			if (kaiyuseiSteps.get(i).getWeather() == null) {
				csvData.add("");
			} else {
				csvData.add(kaiyuseiSteps.get(i).getWeather() + "");
			}
			if (kaiyuseiSteps.get(i).getMin_temp() == null) {
				csvData.add("");
			} else {
				csvData.add(kaiyuseiSteps.get(i).getMin_temp() + "");
			}
			if (kaiyuseiSteps.get(i).getMax_temp() == null) {
				csvData.add("");
			} else {
				csvData.add(kaiyuseiSteps.get(i).getMax_temp() + "");
			}
			csvData.add(kaiyuseiSteps.get(i).getNumber() + "");
		}

		for (int i = 0; i < csvHeader.size(); i++) {
			csvHeader.set(i, csvHeader.get(i).trim());
		}

		res.put("header", csvHeader);
		res.put("data", csvData);
		return res;
	}
}
