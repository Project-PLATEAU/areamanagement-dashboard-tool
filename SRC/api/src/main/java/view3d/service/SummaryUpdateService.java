package view3d.service;

import java.time.chrono.JapaneseChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import view3d.entity.Chika2;
import view3d.entity.CitySummary;
import view3d.entity.GisJoint2;
import view3d.entity.SougouHyoukaResult;
import view3d.entity.StationUsers;
import view3d.repository.Chika2Repository;
import view3d.repository.CitySummaryRepository;
import view3d.repository.GisJoint2Repository;
import view3d.repository.SougouHyoukaResultRepository;
import view3d.repository.StationUsersRepository;

@Service
public class SummaryUpdateService {

	@Autowired
	Chika2Repository chika2Repository;

	@Autowired
	CitySummaryRepository citySummaryRepository;

	@Autowired
	SougouHyoukaResultRepository sougouHyoukaResultRepository;

	@Autowired
	GisJoint2Repository gisJoint2Repository;

	@Autowired
	StationUsersRepository stationUsersRepository;

	DateTimeFormatter japaseseAbbrFormat = new DateTimeFormatterBuilder().appendPattern("GGGGGy")
			.parseDefaulting(ChronoField.MONTH_OF_YEAR, 1)
			.parseDefaulting(ChronoField.DAY_OF_MONTH, 1).toFormatter()
			.withLocale(Locale.JAPAN)
			.withChronology(JapaneseChronology.INSTANCE)
			.withResolverStyle(ResolverStyle.STRICT);

	public void updateSHR(String category) throws Exception {
		boolean executeFlag = true;
		String[] categorySplit = null;
		//駅周辺の増加率。[0]：直近増加率、[1]：次直近増加率
		Double[][] rateSt = null;
		switch (category) {
		case "chika2":
			categorySplit = "公示地価".split("_");
			rateSt = updateChika(categorySplit);
			break;
		case "gis_joint2_household":
			categorySplit = "人口_世帯数".split("_");
			rateSt = updateJinkoSetai(categorySplit);
			break;
		case "gis_joint2_office":
			categorySplit = "事業所数".split("_");
			rateSt = updateJigyosyo(categorySplit);
			break;
		case "gis_joint2_employee":
			categorySplit = "従業者数".split("_");
			rateSt = updateJugyosya(categorySplit);
			break;
		case "station_users":
			categorySplit = "駅利用者数まとめ".split("_");
			rateSt = updateStationUsers(categorySplit);
			break;
		//市まとめを更新した際の処理を追加
		case "city_summary":
			categorySplit = "人口_世帯数_駅利用者数まとめ_事業所数_従業者数_公示地価".split("_");
			rateSt = updateAll(categorySplit);
			break;
		default:
			executeFlag = false;
			break;
		}

		//総合評価を更新
		if (executeFlag) {
			//市まとめの最新年度を取得
			Integer updateYear = citySummaryRepository.getMaxYear();
			//テーブル更新
			for (int i = 0; i < categorySplit.length; i++) {
				List<SougouHyoukaResult> tempSHR = sougouHyoukaResultRepository.findSHRByCategoryYear(categorySplit[i],
						updateYear);
				if (tempSHR.size() == 0) {
					sougouHyoukaResultRepository.insertSHRByCategoryAndYear(rateSt[i][0], rateSt[i][1],
							categorySplit[i], updateYear);
				} else {
					sougouHyoukaResultRepository.updateSHRByCategoryAndYear(rateSt[i][0], rateSt[i][1],
							categorySplit[i],
							updateYear);
				}

			}
			//総合評価を更新
			List<SougouHyoukaResult> tempSougouSHR = sougouHyoukaResultRepository.findSHRByCategoryYear("総合評価",
					updateYear);
			if (tempSougouSHR.size() == 0) {
				sougouHyoukaResultRepository.insertSougouHyoukaByYear(updateYear);
			} else {
				sougouHyoukaResultRepository.updateSougouHyoukaByYear(updateYear);
			}

		}
	}

	/**
	 * 市まとめから指定のカテゴリだけ抽出
	 * @param categorySplit
	 * @return
	 * @throws Exception
	 */
	private List<CitySummary> extractCitySummary(String category) throws Exception {
		List<CitySummary> cs = citySummaryRepository.findCitySummary();
		List<CitySummary> extratCS = new ArrayList<>();
		for (int i = 0; i < cs.size(); i++) {
			CitySummary tempCS = cs.get(i);
			if(tempCS.getValue()==null) {
				continue;
			}
			if (tempCS.getCategory().equals(category)) {
				extratCS.add(tempCS);
			}
		}
		return extratCS;
	}

	/**
	 * 公示地価の増加率を算出
	 * @return
	 * @throws Exception
	 */
	private Double[][] updateChika(String[] categorySplit) throws Exception {
		//[0]:全体比較、[1]:過去比較
		Double[][] res = new Double[categorySplit.length][2];
		List<Chika2> chikaData = chika2Repository.findChika2();
		//直近増加率（駅周辺）
		Double latestRateSt = new Double(0);
		//次直近増加率（駅周辺）
		Double subLatestRateSt = new Double(0);
		//直近増加率（市）
		Double latestRateCity = new Double(0);

		//駅周辺エリアの年度毎の値
		Map<String, Integer> areaSt = new HashMap<>();
		List<Integer> yearList = new ArrayList<>();
		for (int i = 0; i < chikaData.size(); i++) {
			Chika2 tempChika = chikaData.get(i);
			if (!yearList.contains(tempChika.getAd())) {
				yearList.add(tempChika.getAd());
			}
			String Era = tempChika.getAd() + "";
			if (areaSt.get(Era) != null) {
				Integer curValue = areaSt.get(Era);
				areaSt.put(Era, curValue + tempChika.getLand_price());
			} else {
				areaSt.put(Era, tempChika.getLand_price());
			}
		}
		Collections.sort(yearList);
		Integer oldestYear = yearList.get(0);
		Collections.reverse(yearList);
		Integer latestYear = yearList.get(0);
		Integer sublatestYear = yearList.get(1);

		//市まとめの値を取得
		for (int j = 0; j < categorySplit.length; j++) {
			List<CitySummary> extratCS = extractCitySummary(categorySplit[j]);
			//市の年度ごとの値
			Map<String, Double> chikaCity = new HashMap<>();
			yearList = new ArrayList<>();
			for (int i = 0; i < extratCS.size(); i++) {
				CitySummary tempCity = extratCS.get(i);
				if (!yearList.contains(tempCity.getYear())) {
					yearList.add(tempCity.getYear());
				}
				String Era = tempCity.getYear() + "";
				if (chikaCity.get(Era) != null) {
					Double curValue = chikaCity.get(Era);
					chikaCity.put(Era, curValue + tempCity.getValue());
				} else {
					chikaCity.put(Era, tempCity.getValue());
				}
			}
			Collections.sort(yearList);
			Integer cityOldestYear = yearList.get(0);
			Collections.reverse(yearList);
			Integer cityLatestYear = yearList.get(0);
			Integer citySublatestYear = yearList.get(1);
			if (cityOldestYear > oldestYear) {
				oldestYear = cityOldestYear;
			}
			if (cityLatestYear < latestYear) {
				latestYear = cityLatestYear;
			}
			if (citySublatestYear < sublatestYear) {
				sublatestYear = citySublatestYear;
			}

			//増加率の計算
			latestRateSt = (areaSt.get(latestYear + "") - areaSt.get(oldestYear + ""))
					/ Double.valueOf(areaSt.get(oldestYear + ""));
			subLatestRateSt = (areaSt.get(sublatestYear + "") - areaSt.get(oldestYear + ""))
					/ Double.valueOf(areaSt.get(oldestYear + ""));
			latestRateCity = (chikaCity.get(latestYear + "") - chikaCity.get(oldestYear + ""))
					/ Double.valueOf(chikaCity.get(oldestYear + ""));

			//評価の計算
			Double tempCompValue = Math.abs((1 + latestRateSt) / (1 + latestRateCity) * 3);
			res[j][0] = tempCompValue > 5 ? 5.0 : tempCompValue;
			tempCompValue = Math.abs((1 + latestRateSt) / (1 + subLatestRateSt) * 3);
			res[j][1] = tempCompValue > 5 ? 5.0 : tempCompValue;
		}

		return res;
	}

	/**
	 * 人口・世帯の更新
	 * @param categorySplit
	 * @return
	 * @throws Exception
	 */
	private Double[][] updateJinkoSetai(String[] categorySplit) throws Exception {
		//[0]:全体比較、[1]:過去比較
		Double[][] res = new Double[categorySplit.length][2];
		List<GisJoint2> jinkoSetaiData = gisJoint2Repository.findGisJoint2ByJinkoSetai();
		//直近増加率（駅周辺）
		Double latestRateSt = new Double(0);
		//次直近増加率（駅周辺）
		Double subLatestRateSt = new Double(0);
		//直近増加率（市）
		Double latestRateCity = new Double(0);

		//駅周辺エリアの年度毎の値

		for (int j = 0; j < categorySplit.length; j++) {
			Map<String, Integer> areaSt = new HashMap<>();
			List<Integer> yearList = new ArrayList<>();
			for (int i = 0; i < jinkoSetaiData.size(); i++) {
				GisJoint2 tempGis = jinkoSetaiData.get(i);
				if (!categorySplit[j].startsWith(tempGis.getCategory())) {
					continue;
				}
				if (!yearList.contains(tempGis.getAd())) {
					yearList.add(tempGis.getAd());
				}
				String Era = tempGis.getAd() + "";
				if (areaSt.get(Era) != null) {
					Integer curValue = areaSt.get(Era);
					areaSt.put(Era, curValue + tempGis.getNumber());
				} else {
					areaSt.put(Era, tempGis.getNumber());
				}
			}
			Collections.sort(yearList);
			Integer oldestYear = yearList.get(0);
			Collections.reverse(yearList);
			Integer latestYear = yearList.get(0);
			Integer sublatestYear = yearList.get(1);

			//市まとめの値を取得
			//市の年度ごとの値

			List<CitySummary> extratCS = extractCitySummary(categorySplit[j]);
			Map<String, Double> jinkoSetaiCity = new HashMap<>();
			yearList = new ArrayList<>();
			for (int i = 0; i < extratCS.size(); i++) {
				CitySummary tempCity = extratCS.get(i);
				if (!yearList.contains(tempCity.getYear())) {
					yearList.add(tempCity.getYear());
				}
				String Era = tempCity.getYear() + "";
				if (jinkoSetaiCity.get(Era) != null) {
					Double curValue = jinkoSetaiCity.get(Era);
					jinkoSetaiCity.put(Era, curValue + tempCity.getValue());
				} else {
					jinkoSetaiCity.put(Era, tempCity.getValue());
				}
			}
			Collections.sort(yearList);
			Integer cityOldestYear = yearList.get(0);
			Collections.reverse(yearList);
			Integer cityLatestYear = yearList.get(0);
			Integer citySublatestYear = yearList.get(1);
			if (cityOldestYear > oldestYear) {
				oldestYear = cityOldestYear;
			}
			if (cityLatestYear < latestYear) {
				latestYear = cityLatestYear;
			}
			if (citySublatestYear < sublatestYear) {
				sublatestYear = citySublatestYear;
			}

			//増加率の計算
			latestRateSt = (areaSt.get(latestYear + "") - areaSt.get(oldestYear + ""))
					/ Double.valueOf(areaSt.get(oldestYear + ""));
			subLatestRateSt = (areaSt.get(sublatestYear + "") - areaSt.get(oldestYear + ""))
					/ Double.valueOf(areaSt.get(oldestYear + ""));
			latestRateCity = (jinkoSetaiCity.get(latestYear + "") - jinkoSetaiCity.get(oldestYear + ""))
					/ Double.valueOf(jinkoSetaiCity.get(oldestYear + ""));

			//評価の計算
			Double tempCompValue = Math.abs((1 + latestRateSt) / (1 + latestRateCity) * 3);
			res[j][0] = tempCompValue > 5 ? 5.0 : tempCompValue;
			tempCompValue = Math.abs((1 + latestRateSt) / (1 + subLatestRateSt) * 3);
			res[j][1] = tempCompValue > 5 ? 5.0 : tempCompValue;
		}

		return res;
	}

	/**
	 * 事業所数の更新
	 * @param categorySplit
	 * @return
	 * @throws Exception
	 */
	private Double[][] updateJigyosyo(String[] categorySplit) throws Exception {
		//[0]:全体比較、[1]:過去比較
		Double[][] res = new Double[categorySplit.length][2];
		List<GisJoint2> gisData = gisJoint2Repository.findGisJoint2ByJimusyo();
		//直近増加率（駅周辺）
		Double latestRateSt = new Double(0);
		//次直近増加率（駅周辺）
		Double subLatestRateSt = new Double(0);
		//直近増加率（市）
		Double latestRateCity = new Double(0);

		//駅周辺エリアの年度毎の値
		Map<String, Integer> areaSt = new HashMap<>();
		List<Integer> yearList = new ArrayList<>();
		for (int i = 0; i < gisData.size(); i++) {
			GisJoint2 tempJigyosyo = gisData.get(i);
			if (!yearList.contains(tempJigyosyo.getAd())) {
				yearList.add(tempJigyosyo.getAd());
			}
			String Era = tempJigyosyo.getAd() + "";
			if (areaSt.get(Era) != null) {
				Integer curValue = areaSt.get(Era);
				areaSt.put(Era, curValue + tempJigyosyo.getNumber());
			} else {
				areaSt.put(Era, tempJigyosyo.getNumber());
			}
		}
		Collections.sort(yearList);
		Integer oldestYear = yearList.get(0);
		Collections.reverse(yearList);
		Integer latestYear = yearList.get(0);
		Integer sublatestYear = yearList.get(1);

		//市まとめの値を取得
		for (int j = 0; j < categorySplit.length; j++) {
			List<CitySummary> extratCS = extractCitySummary(categorySplit[j]);
			//市の年度ごとの値
			Map<String, Double> jigyosyoCity = new HashMap<>();
			yearList = new ArrayList<>();
			for (int i = 0; i < extratCS.size(); i++) {
				CitySummary tempCity = extratCS.get(i);
				if (!yearList.contains(tempCity.getYear())) {
					yearList.add(tempCity.getYear());
				}
				String Era = tempCity.getYear() + "";
				if (jigyosyoCity.get(Era) != null) {
					Double curValue = jigyosyoCity.get(Era);
					jigyosyoCity.put(Era, curValue + tempCity.getValue());
				} else {
					jigyosyoCity.put(Era, tempCity.getValue());
				}
			}
			Collections.sort(yearList);
			Integer cityOldestYear = yearList.get(0);
			Collections.reverse(yearList);
			Integer cityLatestYear = yearList.get(0);
			Integer citySublatestYear = yearList.get(1);
			if (cityOldestYear > oldestYear) {
				oldestYear = cityOldestYear;
			}
			if (cityLatestYear < latestYear) {
				latestYear = cityLatestYear;
			}
			if (citySublatestYear < sublatestYear) {
				sublatestYear = citySublatestYear;
			}

			//増加率の計算
			latestRateSt = (areaSt.get(latestYear + "") - areaSt.get(oldestYear + ""))
					/ Double.valueOf(areaSt.get(oldestYear + ""));
			subLatestRateSt = latestRateSt;
			//			subLatestRateSt = (areaSt.get(sublatestYear + "") - areaSt.get(oldestYear + ""))
			//					/ Double.valueOf(areaSt.get(oldestYear + ""));
			latestRateCity = (jigyosyoCity.get(latestYear + "") - jigyosyoCity.get(oldestYear + ""))
					/ Double.valueOf(jigyosyoCity.get(oldestYear + ""));

			//評価の計算
			Double tempCompValue = Math.abs((1 + latestRateSt) / (1 + latestRateCity) * 3);
			res[j][0] = tempCompValue > 5 ? 5.0 : tempCompValue;
			tempCompValue = Math.abs((1 + latestRateSt) / (1 + subLatestRateSt) * 3);
			res[j][1] = tempCompValue > 5 ? 5.0 : tempCompValue;
		}

		return res;
	}

	/**
	 * 従業者数の更新
	 * @param categorySplit
	 * @return
	 * @throws Exception
	 */
	private Double[][] updateJugyosya(String[] categorySplit) throws Exception {
		//[0]:全体比較、[1]:過去比較
		Double[][] res = new Double[categorySplit.length][2];
		List<GisJoint2> gisData = gisJoint2Repository.findGisJoint2ByJugyosya();
		//直近増加率（駅周辺）
		Double latestRateSt = new Double(0);
		//次直近増加率（駅周辺）
		Double subLatestRateSt = new Double(0);
		//直近増加率（市）
		Double latestRateCity = new Double(0);

		//駅周辺エリアの年度毎の値
		Map<String, Integer> areaSt = new HashMap<>();
		List<Integer> yearList = new ArrayList<>();
		for (int i = 0; i < gisData.size(); i++) {
			GisJoint2 tempJugyosya = gisData.get(i);
			if (!yearList.contains(tempJugyosya.getAd())) {
				yearList.add(tempJugyosya.getAd());
			}
			String Era = tempJugyosya.getAd() + "";
			if (areaSt.get(Era) != null) {
				Integer curValue = areaSt.get(Era);
				areaSt.put(Era, curValue + tempJugyosya.getNumber());
			} else {
				areaSt.put(Era, tempJugyosya.getNumber());
			}
		}
		Collections.sort(yearList);
		Integer oldestYear = yearList.get(0);
		Collections.reverse(yearList);
		Integer latestYear = yearList.get(0);
		Integer sublatestYear = yearList.get(1);

		//市まとめの値を取得
		for (int j = 0; j < categorySplit.length; j++) {
			List<CitySummary> extratCS = extractCitySummary(categorySplit[j]);
			//市の年度ごとの値
			Map<String, Double> jugyosyaCity = new HashMap<>();
			yearList = new ArrayList<>();
			for (int i = 0; i < extratCS.size(); i++) {
				CitySummary tempCity = extratCS.get(i);
				if (!yearList.contains(tempCity.getYear())) {
					yearList.add(tempCity.getYear());
				}
				String Era = tempCity.getYear() + "";
				if (jugyosyaCity.get(Era) != null) {
					Double curValue = jugyosyaCity.get(Era);
					jugyosyaCity.put(Era, curValue + tempCity.getValue());
				} else {
					jugyosyaCity.put(Era, tempCity.getValue());
				}
			}
			Collections.sort(yearList);
			Integer cityOldestYear = yearList.get(0);
			Collections.reverse(yearList);
			Integer cityLatestYear = yearList.get(0);
			Integer citySublatestYear = yearList.get(1);
			if (cityOldestYear > oldestYear) {
				oldestYear = cityOldestYear;
			}
			if (cityLatestYear < latestYear) {
				latestYear = cityLatestYear;
			}
			if (citySublatestYear < sublatestYear) {
				sublatestYear = citySublatestYear;
			}

			//増加率の計算
			latestRateSt = (areaSt.get(latestYear + "") - areaSt.get(oldestYear + ""))
					/ Double.valueOf(areaSt.get(oldestYear + ""));
			subLatestRateSt = latestRateSt;
			//			subLatestRateSt = (areaSt.get(sublatestYear + "") - areaSt.get(oldestYear + ""))
			//					/ Double.valueOf(areaSt.get(oldestYear + ""));
			latestRateCity = (jugyosyaCity.get(latestYear + "") - jugyosyaCity.get(oldestYear + ""))
					/ Double.valueOf(jugyosyaCity.get(oldestYear + ""));

			//評価の計算
			Double tempCompValue = Math.abs((1 + latestRateSt) / (1 + latestRateCity) * 3);
			res[j][0] = tempCompValue > 5 ? 5.0 : tempCompValue;
			tempCompValue = Math.abs((1 + latestRateSt) / (1 + subLatestRateSt) * 3);
			res[j][1] = tempCompValue > 5 ? 5.0 : tempCompValue;
		}

		return res;
	}

	/**
	 * 駅利用者数まとめの更新
	 * @param categorySplit
	 * @return
	 * @throws Exception
	 */
	private Double[][] updateStationUsers(String[] categorySplit) throws Exception {
		//[0]:全体比較、[1]:過去比較
		Double[][] res = new Double[categorySplit.length][2];
		List<StationUsers> usersData = stationUsersRepository.findStationUsers();
		//直近増加率（合計）
		Double latestRateSt = new Double(0);
		//次直近増加率（合計）
		Double subLatestRateSt = new Double(0);
		//直近増加率（市）
		Double latestRateCity = new Double(0);

		//駅周辺エリアの年度毎の値
		Map<String, Integer> areaSt = new HashMap<>();
		List<Integer> yearList = new ArrayList<>();
		for (int i = 0; i < usersData.size(); i++) {
			StationUsers tempUsers = usersData.get(i);
			if (!yearList.contains(Integer.parseInt(tempUsers.getYear().trim()))) {
				yearList.add(Integer.parseInt(tempUsers.getYear().trim()));
			}
			String Era = tempUsers.getYear().trim();
			if (areaSt.get(Era) != null) {
				Integer curValue = areaSt.get(Era);
				areaSt.put(Era, curValue + tempUsers.getUser_num());
			} else {
				areaSt.put(Era, tempUsers.getUser_num());
			}
		}
		Collections.sort(yearList);
		Integer oldestYear = yearList.get(0);
		Collections.reverse(yearList);
		Integer latestYear = yearList.get(0);
		Integer sublatestYear = yearList.get(1);

		//市まとめの値を取得
		for (int j = 0; j < categorySplit.length; j++) {
			List<CitySummary> extratCS = extractCitySummary(categorySplit[j]);
			//市の年度ごとの値
			Map<String, Double> usersCity = new HashMap<>();
			yearList = new ArrayList<>();
			for (int i = 0; i < extratCS.size(); i++) {
				CitySummary tempCity = extratCS.get(i);
				if (!yearList.contains(tempCity.getYear())) {
					yearList.add(tempCity.getYear());
				}
				String Era = tempCity.getYear() + "";
				if (usersCity.get(Era) != null) {
					Double curValue = usersCity.get(Era);
					usersCity.put(Era, curValue + tempCity.getValue());
				} else {
					usersCity.put(Era, tempCity.getValue());
				}
			}
			Collections.sort(yearList);
			Integer cityOldestYear = yearList.get(0);
			Collections.reverse(yearList);
			Integer cityLatestYear = yearList.get(0);
			Integer citySublatestYear = yearList.get(1);
			if (cityOldestYear > oldestYear) {
				oldestYear = cityOldestYear;
			}
			if (cityLatestYear < latestYear) {
				latestYear = cityLatestYear;
			}
			if (citySublatestYear < sublatestYear) {
				sublatestYear = citySublatestYear;
			}

			//増加率の計算
			latestRateSt = (areaSt.get(latestYear + "") - areaSt.get(oldestYear + ""))
					/ Double.valueOf(areaSt.get(oldestYear + ""));
			subLatestRateSt = (areaSt.get(sublatestYear + "") - areaSt.get(oldestYear + ""))
					/ Double.valueOf(areaSt.get(oldestYear + ""));
			latestRateCity = (usersCity.get(latestYear + "") - usersCity.get(oldestYear + ""))
					/ Double.valueOf(usersCity.get(oldestYear + ""));

			//評価の計算
			Double tempCompValue = Math.abs((1 + latestRateSt) / (1 + latestRateCity) * 3);
			res[j][0] = tempCompValue > 5 ? 5.0 : tempCompValue;
			tempCompValue = Math.abs((1 + latestRateSt) / (1 + subLatestRateSt) * 3);
			res[j][1] = tempCompValue > 5 ? 5.0 : tempCompValue;
		}

		return res;
	}

	/**
	 * 全カテゴリを更新
	 * @param categorySplit
	 * @return
	 * @throws Exception
	 */
	private Double[][] updateAll(String[] categorySplit) throws Exception {
		//[0]:全体比較、[1]:過去比較
		Double[][] res = new Double[categorySplit.length][2];
		List<GisJoint2> jinkoSetaiData = gisJoint2Repository.findGisJoint2ByJinkoSetai();
		List<GisJoint2> jigyosyoData = gisJoint2Repository.findGisJoint2ByJimusyo();
		List<GisJoint2> jugyosyaData = gisJoint2Repository.findGisJoint2ByJugyosya();
		List<StationUsers> usersData = stationUsersRepository.findStationUsers();
		List<Chika2> chikaData = chika2Repository.findChika2();
		//直近増加率（駅周辺）
		Double latestRateSt = new Double(0);
		//次直近増加率（駅周辺）
		Double subLatestRateSt = new Double(0);
		//直近増加率（市）
		Double latestRateCity = new Double(0);

		//駅周辺エリアの年度毎の値
		for (int j = 0; j < categorySplit.length; j++) {
			Map<String, Integer> areaSt = new HashMap<>();
			List<Integer> yearList = new ArrayList<>();
			//人口・世帯数
			for (int i = 0; i < jinkoSetaiData.size(); i++) {
				GisJoint2 tempGis = jinkoSetaiData.get(i);
				if (!categorySplit[j].startsWith(tempGis.getCategory().trim())) {
					continue;
				}
				if (!yearList.contains(tempGis.getAd())) {
					yearList.add(tempGis.getAd());
				}
				String Era = tempGis.getAd() + "";
				if (areaSt.get(Era) != null) {
					Integer curValue = areaSt.get(Era);
					areaSt.put(Era, curValue + tempGis.getNumber());
				} else {
					areaSt.put(Era, tempGis.getNumber());
				}
			}
			//駅利用者数まとめ
			for (int i = 0; i < usersData.size(); i++) {
				StationUsers tempUsers = usersData.get(i);
				if (!categorySplit[j].startsWith("駅利用者数まとめ")) {
					continue;
				}
				if (!yearList.contains(Integer.parseInt(tempUsers.getYear().trim()))) {
					yearList.add(Integer.parseInt(tempUsers.getYear().trim()));
				}
				String Era = tempUsers.getYear().trim();
				if (areaSt.get(Era) != null) {
					Integer curValue = areaSt.get(Era);
					areaSt.put(Era, curValue + tempUsers.getUser_num());
				} else {
					areaSt.put(Era, tempUsers.getUser_num());
				}
			}
			//事業所数
			for (int i = 0; i < jigyosyoData.size(); i++) {
				GisJoint2 tempJigyosyo = jigyosyoData.get(i);
				if (!categorySplit[j].startsWith(tempJigyosyo.getCategory().trim())) {
					continue;
				}
				if (!yearList.contains(tempJigyosyo.getAd())) {
					yearList.add(tempJigyosyo.getAd());
				}
				String Era = tempJigyosyo.getAd() + "";
				if (areaSt.get(Era) != null) {
					Integer curValue = areaSt.get(Era);
					areaSt.put(Era, curValue + tempJigyosyo.getNumber());
				} else {
					areaSt.put(Era, tempJigyosyo.getNumber());
				}
			}
			//従業者数
			for (int i = 0; i < jugyosyaData.size(); i++) {
				GisJoint2 tempJugyosya = jugyosyaData.get(i);
				if (!categorySplit[j].startsWith(tempJugyosya.getCategory().trim())) {
					continue;
				}
				if (!yearList.contains(tempJugyosya.getAd())) {
					yearList.add(tempJugyosya.getAd());
				}
				String Era = tempJugyosya.getAd() + "";
				if (areaSt.get(Era) != null) {
					Integer curValue = areaSt.get(Era);
					areaSt.put(Era, curValue + tempJugyosya.getNumber());
				} else {
					areaSt.put(Era, tempJugyosya.getNumber());
				}
			}
			//公示地価
			for (int i = 0; i < chikaData.size(); i++) {
				Chika2 tempChika = chikaData.get(i);
				if (!categorySplit[j].startsWith("公示地価")) {
					continue;
				}
				if (!yearList.contains(tempChika.getAd())) {
					yearList.add(tempChika.getAd());
				}
				String Era = tempChika.getAd() + "";
				if (areaSt.get(Era) != null) {
					Integer curValue = areaSt.get(Era);
					areaSt.put(Era, curValue + tempChika.getLand_price());
				} else {
					areaSt.put(Era, tempChika.getLand_price());
				}
			}
			Collections.sort(yearList);
			Integer oldestYear = yearList.get(0);
			Collections.reverse(yearList);
			Integer latestYear = yearList.get(0);
			Integer sublatestYear = yearList.get(1);

			//市まとめの値を取得
			//市の年度ごとの値

			List<CitySummary> extratCS = extractCitySummary(categorySplit[j]);
			Map<String, Double> city = new HashMap<>();
			yearList = new ArrayList<>();
			for (int i = 0; i < extratCS.size(); i++) {
				CitySummary tempCity = extratCS.get(i);
				if (!yearList.contains(tempCity.getYear())) {
					yearList.add(tempCity.getYear());
				}
				String Era = tempCity.getYear() + "";
				if (city.get(Era) != null) {
					Double curValue = city.get(Era);
					city.put(Era, curValue + tempCity.getValue());
				} else {
					city.put(Era, tempCity.getValue());
				}
			}
			Collections.sort(yearList);
			Integer cityOldestYear = yearList.get(0);
			Collections.reverse(yearList);
			Integer cityLatestYear = yearList.get(0);
			Integer citySublatestYear = yearList.get(1);
			if (cityOldestYear > oldestYear) {
				oldestYear = cityOldestYear;
			}
			if (cityLatestYear < latestYear) {
				latestYear = cityLatestYear;
			}
			if (citySublatestYear < sublatestYear) {
				sublatestYear = citySublatestYear;
			}

			//増加率の計算
			latestRateSt = (areaSt.get(latestYear + "") - areaSt.get(oldestYear + ""))
					/ Double.valueOf(areaSt.get(oldestYear + ""));
			subLatestRateSt = (areaSt.get(sublatestYear + "") - areaSt.get(oldestYear + ""))
					/ Double.valueOf(areaSt.get(oldestYear + ""));
			latestRateCity = (city.get(latestYear + "") - city.get(oldestYear + ""))
					/ Double.valueOf(city.get(oldestYear + ""));

			//評価の計算
			Double tempCompValue = Math.abs((1 + latestRateSt) / (1 + latestRateCity) * 3);
			res[j][0] = tempCompValue > 5 ? 5.0 : tempCompValue;
			tempCompValue = Math.abs((1 + latestRateSt) / (1 + subLatestRateSt) * 3);
			res[j][1] = tempCompValue > 5 ? 5.0 : tempCompValue;
		}

		return res;
	}
}
