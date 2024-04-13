package view3d.service;

import java.io.BufferedReader;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.chrono.JapaneseChronology;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
public class CsvUpdateService {

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
	private static final int SYOGYOSHISETSU_DATA_NUM = 4;
	private static final int SYOKENCYOSA_SHIJIRITSU_DATA_NUM = 2;
	private static final int CITY_SUMMARY_DATA_NUM = 1;
	private static final int KAIYU_PEOPLE_DATA_NUM = 3;
	private static final int KAIYU_AGE_DATA_NUM = 4;
	private static final int KAIYU_GENDER_DATA_NUM = 4;
	private static final int KAIYU_REGION_DATA_NUM = 4;
	private static final int KAIYU_STEPS_DATA_NUM = 6;

	private final DateTimeFormatter japaseseFormat = new DateTimeFormatterBuilder().appendPattern("GGGGy年")
			.parseDefaulting(ChronoField.MONTH_OF_YEAR, 7)
			.parseDefaulting(ChronoField.DAY_OF_MONTH, 1).toFormatter()
			.withLocale(Locale.JAPAN)
			.withChronology(JapaneseChronology.INSTANCE)
			.withResolverStyle(ResolverStyle.STRICT);
	private final DateTimeFormatter japaseseAbbrFormat = new DateTimeFormatterBuilder().appendPattern("GGGGGy")
			.parseDefaulting(ChronoField.MONTH_OF_YEAR, 7)
			.parseDefaulting(ChronoField.DAY_OF_MONTH, 1).toFormatter()
			.withLocale(Locale.JAPAN)
			.withChronology(JapaneseChronology.INSTANCE)
			.withResolverStyle(ResolverStyle.STRICT);

	private final SimpleDateFormat slashsdf=new SimpleDateFormat("yyyy/MM/dd");
	private final SimpleDateFormat hyphensdf=new SimpleDateFormat("yyyy-MM-dd");
	/**
	 * 元年を１年に変換
	 * @param gannen
	 * @return
	 */
	private static String convertGanToOne(String gannen) {
		return gannen.replace("元", "1");
	}

	private String dateFormat(String date) throws Exception{
		try {

			Date d=slashsdf.parse(date);

			return hyphensdf.format(d);
		}catch(Exception e) {
			throw e;
		}
	}

	/**
	 * 令和を平成に変換
	 * @param reiwa
	 * @return
	 */
//	private static String convertReiwaToHeisei(String reiwa) {
//		String heisei = reiwa.replace("令和", "平成");
//		if (!heisei.equals(reiwa)) {
//			int yearOfEra = Integer.parseInt(heisei.replaceAll("[^0-9]", ""));
//			if (yearOfEra < 10) {
//				heisei = heisei.replaceAll("[0-9]", yearOfEra + 30 + "");
//			} else {
//				heisei = heisei.replace("[0-9]", "");
//				heisei = heisei.replace("[0-9]", yearOfEra + 30 + "");
//			}
//		}
//		return heisei;
//	}

	/**
	 * R ⇒ Hへ変換
	 * @param reiwa
	 * @return
	 */
//	private static String convertRToH(String reiwa) {
//		String heisei = reiwa.replace("R", "H");
//		if (!heisei.equals(reiwa)) {
//			int yearOfEra = Integer.parseInt(heisei.replaceAll("[^0-9]", ""));
//			if (yearOfEra < 10) {
//				heisei = heisei.replaceAll("[0-9]", yearOfEra + 30 + "");
//			} else {
//				heisei = heisei.replace("[0-9]", "");
//				heisei = heisei.replace("[0-9]", yearOfEra + 30 + "");
//			}
//		}
//		return heisei;
//	}

	/**
	 * 地域表示価格のデータ更新
	 */
	@Transactional
	public void updateChika2(BufferedReader br) throws Exception {
		try {
			String line;
			int lineNum = 1;
			String[] header = null;
			int dataNum = 0;
			//CSVを１行ごとに読み取り
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");

				if (lineNum == 1) {
					header = line.split(",");
					//ヘッダの数を計算
					dataNum = header.length;
				} else {
					//データが無い場合
					if (dataNum <= CHIKA2_STATIC_DATA_NUM) {
						throw new Exception();
					} else {
						for (int i = CHIKA2_STATIC_DATA_NUM; i < dataNum; i++) {
							Chika2 chika2 = new Chika2();
							Integer chika2Id = chika2Repository.findChika2_id(split[0], split[1], split[2],
									header[i]);
							if (chika2Id != null) {
								chika2.setChika2Id(chika2Id);
							} else {
								int newChika2Id = (int) chika2Repository.count() + 1;
								chika2.setChika2Id(newChika2Id);
							}
							chika2.setPlace_name(split[0].trim());
							chika2.setCategory(split[1].trim());
							chika2.setArea(split[2].trim());
							chika2.setJp_ad(header[i].trim());
							LocalDate year = null;
							try {
								year = LocalDate.parse(
										convertGanToOne(header[i]).trim(),
										japaseseFormat);
							}catch(Exception e) {
								year = LocalDate.parse(
										convertGanToOne(header[i]).trim(),
										japaseseAbbrFormat);
							}
							chika2.setAd(year.getYear());
							//DBを更新
							if (i < split.length) {
								//データがある場合
								if (!split[i].isEmpty() || !split[i].equals("")) {
									chika2.setLand_price(Integer.parseInt(split[i].trim()));
									chika2Repository.save(chika2);
								} else {
									chika2.setLand_price(null);
									chika2Repository.save(chika2);
								}
							} else {
								chika2.setLand_price(null);
								chika2Repository.save(chika2);
							}
						}
					}

				}
				lineNum++;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 活動前と比較した認知度の推移のデータ更新
	 */
	public void updateNinchido(BufferedReader br) throws Exception {
		try {
			String line;
			int lineNum = 1;
			String[] header = null;
			int dataNum = 0;
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");

				if (lineNum == 1) {
					header = line.split(",");
					dataNum = header.length;
				} else {
					if (dataNum <= NINCHIDO_STATIC_DATA_NUM) {
						throw new Exception();
					} else {
						for (int i = NINCHIDO_STATIC_DATA_NUM; i < dataNum; i++) {
							ErimaneNinchido ninchido = new ErimaneNinchido();
							Integer ninchidoId = erimaneNinchidoRepository.findNinchido_id(split[0], header[i]);
							if (ninchidoId != null) {
								ninchido.setNinchido_id(ninchidoId);
							} else {
								int newNinchidoId = (int) erimaneNinchidoRepository.count() + 1;
								ninchido.setNinchido_id(newNinchidoId);
							}
							ninchido.setArea(split[0].trim());
							ninchido.setJp_ad(header[i].trim());
							LocalDate year = LocalDate.parse(convertGanToOne(header[i]).trim(),
									japaseseAbbrFormat);
							ninchido.setAd(year.getYear());
							if (i < split.length) {
								if (!split[i].isEmpty() || !split[i].equals("")) {
									ninchido.setNinchido(Double.parseDouble(split[i].trim()));
									erimaneNinchidoRepository.save(ninchido);
								} else {
									ninchido.setNinchido(null);
									erimaneNinchidoRepository.save(ninchido);
								}
							} else {
								ninchido.setNinchido(null);
								erimaneNinchidoRepository.save(ninchido);
							}
						}
					}

				}
				lineNum++;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * GIS統合系データのデータ更新
	 * @param itemName
	 * @param br
	 * @throws Exception
	 */
	public void updateGisJoint2(String itemName, BufferedReader br) throws Exception {
		try {
			String line;
			int lineNum = 1;
			String[] header = null;
			int dataNum = 0;
			List<String> categoryList = new ArrayList<>();

			switch (itemName) {
			case "gis_joint2_household":
				categoryList.add("人口");
				categoryList.add("世帯");
				break;
			case "gis_joint2_population":
				categoryList.add("年少人口");
				categoryList.add("生産年齢人口");
				categoryList.add("老年人口");
				break;
			case "gis_joint2_size":
				categoryList.add("単身世帯");
				categoryList.add("2人世帯");
				categoryList.add("3人世帯");
				categoryList.add("4人世帯");
				categoryList.add("5人世帯以上");
				break;
			case "gis_joint2_office":
				categoryList.add("事業所");
				break;
			case "gis_joint2_employee":
				categoryList.add("従業者数");
				break;
			default:
				break;
			}

			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");

				if (lineNum == 1) {
					header = line.split(",");
					dataNum = header.length;
					for (int i = GIS_JOINT2_DATA_NUM; i < dataNum; i++) {
						if (!categoryList.contains(header[i].split("_")[1].trim())) {
							throw new Exception();
						}
					}
				} else {
					if (dataNum <= GIS_JOINT2_DATA_NUM) {
						throw new Exception();
					} else {
						for (int i = GIS_JOINT2_DATA_NUM; i < dataNum; i++) {
							GisJoint2 gisJoint2 = new GisJoint2();
							Integer gisJoint2Id = gisJoint2Repository.findGisJoint2_id(split[0], split[1],
									header[i].split("_")[0], header[i].split("_")[1]);
							if (gisJoint2Id != null) {
								gisJoint2.setGis_id(gisJoint2Id);
							} else {
								int newGisJointId = (int) gisJoint2Repository.count() + 1;
								gisJoint2.setGis_id(newGisJointId);
							}
							gisJoint2.setPlace_name(split[0].trim());
							gisJoint2.setErimane(split[1].trim());
							gisJoint2.setCategory(header[i].split("_")[1].trim());
							gisJoint2.setJp_ad(header[i].split("_")[0].trim());
							LocalDate year = LocalDate.parse(
									convertGanToOne(header[i].split("_")[0]).trim(),
									japaseseAbbrFormat);
							gisJoint2.setAd(year.getYear());
							if (i < split.length) {
								if (!split[i].isEmpty() || !split[i].equals("")) {
									gisJoint2.setNumber(Integer.parseInt(split[i].trim()));
									gisJoint2Repository.save(gisJoint2);
								} else {
									gisJoint2.setNumber(null);
									gisJoint2Repository.save(gisJoint2);
								}
							} else {
								gisJoint2.setNumber(null);
								gisJoint2Repository.save(gisJoint2);
							}
						}
					}

				}
				lineNum++;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 駅の乗降客数のデータ更新
	 * @param br
	 * @throws Exception
	 */
	@Transactional
	public void updateStationUsers(BufferedReader br) throws Exception {
		try {
			String line;
			int lineNum = 1;
			String[] header = null;
			int dataNum = 0;
			//CSVを１行ごとに読み取り
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");

				if (lineNum == 1) {
					header = line.split(",");
					//ヘッダの数を計算
					dataNum = header.length;
				} else {
					//データが無い場合
					if (dataNum <= STATION_USERS_DATA_NUM) {
						throw new Exception();
					} else {
						for (int i = STATION_USERS_DATA_NUM; i < dataNum; i++) {
							StationUsers stationUsers = new StationUsers();
							Integer stationUsersId = stationUsersRepository.findStationUsers_id(split[0], header[i]);
							if (stationUsersId != null) {
								stationUsers.setStation_id(stationUsersId);
							}
							stationUsers.setOffice_name(split[0].trim());
							stationUsers.setYear(header[i].trim());
							//DBを更新
							if (i < split.length) {
								//データがある場合
								if (!split[i].isEmpty() || !split[i].equals("")) {
									stationUsers.setUser_num(Integer.parseInt(split[i].trim()));
									stationUsersRepository.save(stationUsers);
								} else {
									stationUsers.setUser_num(null);
									stationUsersRepository.save(stationUsers);
								}
							} else {
								stationUsers.setUser_num(null);
								stationUsersRepository.save(stationUsers);
							}
						}
					}

				}
				lineNum++;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 商業施設のデータ更新
	 * @param br
	 * @throws Exception
	 */
	@Transactional
	public void updateSyogyoshisetsu(BufferedReader br) throws Exception {
		try {
			String line;
			int lineNum = 1;
			//CSVを１行ごとに読み取り
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");

				if (lineNum != 1) {
					Syogyoshisetsu syogyoshisetsu = new Syogyoshisetsu();
					Integer syogyoshisetsuId = syogyoshisetsuRepository.findSyogyoshisetsu_id(split[0]);
					if (syogyoshisetsuId != null) {
						syogyoshisetsu.setShisestu_id(syogyoshisetsuId);
					} else {
						int newSyogyoshisetsuId = (int) syogyoshisetsuRepository.count() + 1;
						syogyoshisetsu.setShisestu_id(newSyogyoshisetsuId);
					}
					syogyoshisetsu.setShop_name(split[0].trim());
					if (!split[1].isEmpty() || !split[1].equals("")) {
						syogyoshisetsu.setAddress(split[1].trim());
					} else {
						syogyoshisetsu.setAddress(null);
					}
					if (!split[2].isEmpty() || !split[2].equals("")) {
						syogyoshisetsu.setYear(Integer.parseInt(split[2].trim()));
					} else {
						syogyoshisetsu.setYear(null);
					}
					if (split.length == SYOGYOSHISETSU_DATA_NUM) {
						if (!split[3].isEmpty() || !split[3].equals("")) {
							syogyoshisetsu.setShop_area(Integer.parseInt(split[3]));
						} else {
							syogyoshisetsu.setShop_area(null);
						}
					} else {
						syogyoshisetsu.setShop_area(null);
					}

					//DBを更新
					syogyoshisetsuRepository.save(syogyoshisetsu);
				}
				lineNum++;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 商圏調査の支持率のデータ更新
	 * @param br
	 * @throws Exception
	 */
	@Transactional
	public void updateSyokencyosaShijiritsu(BufferedReader br) throws Exception {
		try {
			String line;
			int lineNum = 1;
			//CSVを１行ごとに読み取り
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");

				if (lineNum != 1) {
					for (int i = 0; i < split.length; i++) {
						SyokencyosaShijiritsu syokencyosaShijiritsu = new SyokencyosaShijiritsu();
						Integer syokencyosaShijiritsuId = syokencyosaShijiritsuRepository
								.findSyokencyosaShijiritsu_id(split[0]);
						if (syokencyosaShijiritsuId != null) {
							syokencyosaShijiritsu.setShijiritsu_id(syokencyosaShijiritsuId);
						} else {
							int newSyokencyosaShijiritsuId = (int) syokencyosaShijiritsuRepository.count() + 1;
							syokencyosaShijiritsu.setShijiritsu_id(newSyokencyosaShijiritsuId);
						}
						syokencyosaShijiritsu.setSyoken_area(split[0].trim());
						if (split.length == SYOKENCYOSA_SHIJIRITSU_DATA_NUM) {
							if (!split[1].isEmpty() || !split[1].equals("")) {
								syokencyosaShijiritsu.setRatio(Double.parseDouble(split[1]));
							} else {
								syokencyosaShijiritsu.setRatio(null);
							}
						} else {
							syokencyosaShijiritsu.setRatio(null);
						}

						//DBを更新
						syokencyosaShijiritsuRepository.save(syokencyosaShijiritsu);
					}
				}
				lineNum++;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 市まとめのデータ更新
	 * @param br
	 * @throws Exception
	 */
	@Transactional
	public void updateCitySummary(BufferedReader br) throws Exception {
		try {
			String line;
			int lineNum = 1;
			String[] header = null;
			int dataNum = 0;
			//CSVを１行ごとに読み取り
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");

				if (lineNum == 1) {
					header = line.split(",");
					//ヘッダの数を計算
					dataNum = header.length;
				} else {
					//データが無い場合
					if (dataNum <= CITY_SUMMARY_DATA_NUM) {
						throw new Exception();
					} else {
						for (int i = CITY_SUMMARY_DATA_NUM; i < dataNum; i++) {
							CitySummary citySummary = new CitySummary();
							Integer citySummaryId = citySummaryRepository.getCitySummaryId(split[0], header[i]);
							if (citySummaryId != null) {
								citySummary.setShr_id(citySummaryId);
							}
							citySummary.setCategory(split[0].trim());
							citySummary.setEra_jp(header[i].trim());
							LocalDate year = LocalDate.parse(convertGanToOne(header[i]).trim(),
									japaseseAbbrFormat);
							citySummary.setYear(year.getYear());
							//DBを更新
							if (i < split.length) {
								//データがある場合
								if (!split[i].isEmpty() || !split[i].equals("")) {
									citySummary.setValue(Double.parseDouble(split[i].trim()));
									citySummaryRepository.save(citySummary);
								} else {
									citySummary.setValue(null);
									citySummaryRepository.save(citySummary);
								}
							} else {
								citySummary.setValue(null);
								citySummaryRepository.save(citySummary);
							}
						}
					}

				}
				lineNum++;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 回遊性_来場者人数のデータ更新
	 * @param br
	 * @throws Exception
	 */
	@Transactional
	public void updateKaiyuPeople(BufferedReader br) throws Exception {
		try {
			String line;
			int lineNum = 1;
			//CSVを１行ごとに読み取り
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");

				if (lineNum != 1) {
					for (int i = 0; i < split.length; i++) {
						KaiyuseiPeople kaiyuseiPeople = new KaiyuseiPeople();
						Integer kaiyuseiPeopleId = kaiyuPeopleRepository
								.findKaiyuseiPeople_id(split[0].contains("/")?dateFormat(split[0].trim()):split[0].trim(),Integer.parseInt(split[2]));
						if (kaiyuseiPeopleId != null) {
							kaiyuseiPeople.setId(kaiyuseiPeopleId);
						} else {
							int newKaiyuseiPeopleId = (int) kaiyuPeopleRepository.count() + 1;
							kaiyuseiPeople.setId(newKaiyuseiPeopleId);
						}
						kaiyuseiPeople.setDate(split[0].contains("/")?slashsdf.parse(split[0].trim()):hyphensdf.parse(split[0].trim()));
						kaiyuseiPeople.setNumber(Integer.parseInt(split[2].trim()));
						if (split.length == KAIYU_PEOPLE_DATA_NUM) {
							if (!split[1].isEmpty() || !split[1].equals("")) {
								kaiyuseiPeople.setUsers(Integer.parseInt(split[1].trim()));
							} else {
								kaiyuseiPeople.setUsers(null);
							}
						} else {
							kaiyuseiPeople.setUsers(null);
						}

						//DBを更新
						kaiyuPeopleRepository.save(kaiyuseiPeople);
					}
				}
				lineNum++;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 回遊性_来場者年齢のデータ更新
	 * @param br
	 * @throws Exception
	 */
	@Transactional
	public void updateKaiyuAge(BufferedReader br) throws Exception {
		try {
			String line;
			int lineNum = 1;
			//CSVを１行ごとに読み取り
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");

				if (lineNum != 1) {
					for (int i = 0; i < split.length; i++) {
						KaiyuseiAge kaiyuseiAge = new KaiyuseiAge();
						Integer kaiyuseiAgeId = kaiyuAgeRepository
								.findKaiyuseiAge_id(split[0].trim(),Integer.parseInt(split[3]));
						if (kaiyuseiAgeId != null) {
							kaiyuseiAge.setId(kaiyuseiAgeId);
						} else {
							int newKaiyuseiAgeId = (int) kaiyuAgeRepository.count() + 1;
							kaiyuseiAge.setId(newKaiyuseiAgeId);
						}
						kaiyuseiAge.setItem(split[0].trim());
						kaiyuseiAge.setNumber(Integer.parseInt(split[3].trim()));
						kaiyuseiAge.setItem_id(kaiyuAgeRepository.findKaiyuseiAgeItemId(split[0].trim()));
						if (split.length == KAIYU_AGE_DATA_NUM) {
							if (!split[1].isEmpty() || !split[1].equals("")) {
								kaiyuseiAge.setUsers(Double.parseDouble(split[1].trim()));
							} else {
								kaiyuseiAge.setUsers(null);
							}
							if (!split[2].isEmpty() || !split[2].equals("")) {
								kaiyuseiAge.setRate(Double.parseDouble(split[2].trim()));
							} else {
								kaiyuseiAge.setRate(null);
							}
						} else {
							kaiyuseiAge.setUsers(null);
							kaiyuseiAge.setRate(null);
						}

						//DBを更新
						kaiyuAgeRepository.save(kaiyuseiAge);
					}
				}
				lineNum++;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 回遊性_来場者性別のデータ更新
	 * @param br
	 * @throws Exception
	 */
	@Transactional
	public void updateKaiyuGender(BufferedReader br) throws Exception {
		try {
			String line;
			int lineNum = 1;
			//CSVを１行ごとに読み取り
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");

				if (lineNum != 1) {
					for (int i = 0; i < split.length; i++) {
						KaiyuseiGender kaiyuseiGender = new KaiyuseiGender();
						Integer kaiyuseiGenderId = kaiyuGenderRepository
								.findKaiyuseiGender_id(split[0].trim(),Integer.parseInt(split[3]));
						if (kaiyuseiGenderId != null) {
							kaiyuseiGender.setId(kaiyuseiGenderId);
						} else {
							int newKaiyuseiGenderId = (int) kaiyuGenderRepository.count() + 1;
							kaiyuseiGender.setId(newKaiyuseiGenderId);
						}
						kaiyuseiGender.setGender(split[0].trim());
						kaiyuseiGender.setNumber(Integer.parseInt(split[3].trim()));
						if (split.length == KAIYU_GENDER_DATA_NUM) {
							if (!split[1].isEmpty() || !split[1].equals("")) {
								kaiyuseiGender.setUsers(Double.parseDouble(split[1].trim()));
							} else {
								kaiyuseiGender.setUsers(null);
							}
							if (!split[2].isEmpty() || !split[2].equals("")) {
								kaiyuseiGender.setRate(Double.parseDouble(split[2].trim()));
							} else {
								kaiyuseiGender.setRate(null);
							}
						} else {
							kaiyuseiGender.setUsers(null);
							kaiyuseiGender.setRate(null);
						}

						//DBを更新
						kaiyuGenderRepository.save(kaiyuseiGender);
					}
				}
				lineNum++;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 回遊性_来場者地域のデータ更新
	 * @param br
	 * @throws Exception
	 */
	@Transactional
	public void updateKaiyuRegion(BufferedReader br) throws Exception {
		try {
			String line;
			int lineNum = 1;
			//CSVを１行ごとに読み取り
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");

				if (lineNum != 1) {
					for (int i = 0; i < split.length; i++) {
						KaiyuseiRegion kaiyuseiRegion= new KaiyuseiRegion();
						Integer kaiyuseiRegionId = kaiyuRegionRepository
								.findKaiyuseiRegion_id(split[0].trim(),Integer.parseInt(split[3]));
						if (kaiyuseiRegionId != null) {
							kaiyuseiRegion.setId(kaiyuseiRegionId);
						} else {
							int newKaiyuseiRegionId = (int) kaiyuRegionRepository.count() + 1;
							kaiyuseiRegion.setId(newKaiyuseiRegionId);
						}
						kaiyuseiRegion.setAddress(split[0].trim());
						kaiyuseiRegion.setNumber(Integer.parseInt(split[3].trim()));
						if (split.length == KAIYU_REGION_DATA_NUM) {
							if (!split[1].isEmpty() || !split[1].equals("")) {
								kaiyuseiRegion.setUsers(Integer.parseInt(split[1].trim()));
							} else {
								kaiyuseiRegion.setUsers(null);
							}
							if (!split[2].isEmpty() || !split[2].equals("")) {
								kaiyuseiRegion.setRate(Double.parseDouble(split[2].trim()));
							} else {
								kaiyuseiRegion.setRate(null);
							}
						} else {
							kaiyuseiRegion.setUsers(null);
							kaiyuseiRegion.setRate(null);
						}

						//DBを更新
						kaiyuRegionRepository.save(kaiyuseiRegion);
					}
				}
				lineNum++;
			}
		} catch (Exception e) {
			throw e;
		}
	}

	/**
	 * 回遊性_来場者歩数のデータ更新
	 * @param br
	 * @throws Exception
	 */
	@Transactional
	public void updateKaiyuSteps(BufferedReader br) throws Exception {
		try {
			String line;
			int lineNum = 1;
			//CSVを１行ごとに読み取り
			while ((line = br.readLine()) != null) {
				String[] split = line.split(",");

				if (lineNum != 1) {
					for (int i = 0; i < split.length; i++) {
						KaiyuseiSteps kaiyuseiSteps= new KaiyuseiSteps();
						Integer kaiyuseiStepsId = kaiyuStepsRepository
								.findKaiyuseiSteps_id(split[0].contains("/")?dateFormat(split[0].trim()):split[0].trim(),Integer.parseInt(split[5]));
						if (kaiyuseiStepsId != null) {
							kaiyuseiSteps.setId(kaiyuseiStepsId);
						} else {
							int newKaiyuseiStepsId = (int) kaiyuStepsRepository.count() + 1;
							kaiyuseiSteps.setId(newKaiyuseiStepsId);
						}
						kaiyuseiSteps.setDate(split[0].contains("/")?slashsdf.parse(split[0].trim()):hyphensdf.parse(split[0].trim()));
						kaiyuseiSteps.setNumber(Integer.parseInt(split[5].trim()));
						if (split.length == KAIYU_STEPS_DATA_NUM) {
							if (!split[1].isEmpty() || !split[1].equals("")) {
								kaiyuseiSteps.setStep(Integer.parseInt(split[1].trim()));
							} else {
								kaiyuseiSteps.setStep(null);
							}
							if (!split[2].isEmpty() || !split[2].equals("")) {
								kaiyuseiSteps.setWeather(split[2].trim());
							} else {
								kaiyuseiSteps.setWeather(null);
							}
							if (!split[3].isEmpty() || !split[3].equals("")) {
								kaiyuseiSteps.setMin_temp(Double.parseDouble(split[3].trim()));
							} else {
								kaiyuseiSteps.setMin_temp(null);
							}
							if (!split[4].isEmpty() || !split[4].equals("")) {
								kaiyuseiSteps.setMax_temp(Double.parseDouble(split[4].trim()));
							} else {
								kaiyuseiSteps.setMax_temp(null);
							}
						} else {
							kaiyuseiSteps.setStep(null);
							kaiyuseiSteps.setWeather(null);
							kaiyuseiSteps.setMin_temp(null);
							kaiyuseiSteps.setMax_temp(null);
						}

						//DBを更新
						kaiyuStepsRepository.save(kaiyuseiSteps);
					}
				}
				lineNum++;
			}
		} catch (Exception e) {
			throw e;
		}
	}
}
