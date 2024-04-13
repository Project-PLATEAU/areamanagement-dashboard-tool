package view3d.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * ログのユーティリティクラス
 */
@Component
public class LogUtil {
	public static boolean customLogFlg;

	@Value("${app.custom.log.flag}")
	public void setCustomLogFlg(boolean customLogFlg) {
		this.customLogFlg = customLogFlg;
	}

	/**
	 * ログ書き込み
	 * 
	 * @param filePath    ファイルパス
	 * @param headerArray ヘッダー
	 * @param dataArray   データ
	 */
	public static void writeLogToCsv(String filePath, String[] headerArray, Object[] dataArray) {
		PrintWriter pw = null;
		try {
			if (customLogFlg) {
				Path filePathCheck = Paths.get(filePath);
				boolean initFlg = !Files.exists(filePathCheck);
				if (initFlg) {
					File dir = new File(filePath.substring(0, filePath.lastIndexOf("/")));
					dir.mkdir();
				}
				// headerを読み込み設定ファイルが変更されているか否かを確認
				try (BufferedReader reader = Files.newBufferedReader(Paths.get(filePath),
						Charset.forName("Shift-JIS"))) {
					String headerString = reader.readLine();
					String currentHeaderString = String.join(",", headerArray);
					if (currentHeaderString != null && headerString != null) {
						if (!headerString.equals(currentHeaderString)) {
							File fOld = new File(filePath);
							File fNew = new File(filePath.replace(".csv", "") + "_" + randomFileName() + ".csv");
							if (fOld.exists()) {
								// ファイル名変更実行
								fOld.renameTo(fNew);
								initFlg = true;
							}
						}
					}
				} catch (Exception ex) {
					ex.printStackTrace();
				}
				// PrintWriterクラスのオブジェクトを生成
				pw = new PrintWriter(
						new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filePath, true), "Shift-JIS")));
				// ヘッダーの指定
				if (initFlg) {
					for (int i = 0; i < headerArray.length; i++) {
						pw.print(headerArray[i]);
						if (i != headerArray.length - 1) {
							pw.print(",");
						}
					}
					pw.println();
				}
				// データを書き込む
				for (int i = 0; i < dataArray.length; i++) {
					String dataString = "";
					if (dataArray[i] != null) {
						dataString = dataArray[i].toString();
						if (dataString != null) {
							dataString = dataString.replaceAll("\\r\\n|\\r|\\n", " ");
						}
					}
					pw.print(dataString);
					if (i != dataArray.length - 1) {
						pw.print(",");
					}
				}
				pw.println();
			}
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (Exception ex) {
			ex.printStackTrace();
		} finally {
			if (pw != null) {
				pw.close();
			}
		}
	}

	/**
	 * LocalDateTimeを文字列にフォーマット
	 * 
	 * @param date
	 * @return string
	 */
	public static String localDateTimeToString(LocalDateTime date) {
		// 書式を指定
		DateTimeFormatter datetimeformatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/ HH:mm:ss");
		// 指定の書式に日付データを渡す
		String datetimeformated = datetimeformatter.format(date);
		return datetimeformated;
	}

	/**
	 * ランダムなファイル名を作成 (形式:yyyy-MM-dd-HH-mm-ss-********)
	 * 
	 * @return string
	 */
	public static String randomFileName() {
		LocalDateTime date = LocalDateTime.now();
		// 書式を指定
		DateTimeFormatter datetimeformatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
		// 指定の書式に日付データを渡す
		String datetimeformated = datetimeformatter.format(date);
		datetimeformated = datetimeformated + "-" + randomString(10);
		return datetimeformated;
	}

	/**
	 * ランダムな文字列を生成して返す
	 * 
	 * @param stringLength 生成したい文字列の長さ
	 * @return string
	 */
	public static String randomString(int stringLength) {
		StringBuffer stringBuffer = new StringBuffer();
		String resultString = "";
		String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
		Random r = new Random();
		for (int i = 0; i < stringLength; i++) {
			char c = alphabet.charAt(r.nextInt(alphabet.length()));
			stringBuffer.append(c);
		}
		if (stringBuffer.length() > 0) {
			resultString = stringBuffer.toString();
		}
		return resultString;
	}

	/**
	 * Stringからユニコードに変換
	 * 
	 * @param val
	 * @return string
	 */
	public static String convertUnicode(String val) {
		// 変換する文字を格納するバッファ宣言
		StringBuffer sb = new StringBuffer();
		// 文字を一々探索する。
		for (int i = 0; i < val.length(); i++) {
			// 文字抽出をintタイプに持ち込む。
			int code = val.codePointAt(i);
			// 128以下ならasciiコードに変換しない。
			if (code < 128) {
				sb.append(String.format("%c", code));
			} else {
				// 16進数ユニコードに変換する。
				sb.append(String.format("\\u%04x", code));
			}
		}
		return sb.toString();
	}

	/**
	 * ユニコードからStringに変換
	 * 
	 * @param val
	 * @return string
	 */
	public static String convertString(String val) {
		// 変換する文字を格納するバッファに宣言
		StringBuffer sb = new StringBuffer();
		// 文字を一々探索する。
		for (int i = 0; i < val.length(); i++) {
			if ('\\' == val.charAt(i) && 'u' == val.charAt(i + 1)) {
				// その後の４文字はユニコードの16進数コードだ。intタイプに変換してcharタイプに強制変換する。
				Character r = (char) Integer.parseInt(val.substring(i + 2, i + 6), 16);
				// 変換する文字をバッファに入れる。
				sb.append(r);
				// forの増加値を５を加算して総6を増加
				i += 5;
			} else {
				// asciiコードならそのままバッファに入れる。
				sb.append(val.charAt(i));
			}
		}
		return sb.toString();
	}
}