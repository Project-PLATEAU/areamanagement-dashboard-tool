package view3d.util;

import org.springframework.stereotype.Component;

/**
 * SQLのユーティリティクラス
 * TODO:エスケープ処理に関してベース作成、確認要
 */
@Component
public class SQLUtil {
	public static String sqlEscape(String text){
	    StringBuffer sb=new StringBuffer();
	    for(int i=0;i<text.length();i++){
	        switch(text.charAt(i)){
	            case '\'' :
	                sb.append("\'\'");
	                break;
	            case '\\' :
	                sb.append("\\\\");
	                break;
	            case ';' :
	                break;
	            default :
	                sb.append(text.charAt(i));
	            break;
	        }
	    }
	    return sb.toString();
	}
	public static String aliasNameEscape(String text){
	    StringBuffer sb=new StringBuffer();
	    //禁則文字の置き換え
	    for(int i=0;i<text.length();i++){
	        switch(text.charAt(i)){
	            case '"' :
	                sb.append("");
	                break;
	            case '¨' :
	                sb.append("");
	                break;
	            case ' ' :
	                sb.append("");
	                break;
	            case '　' :
	                sb.append("");
	                break;
	            case '	' :
	                sb.append("");
	                break;
	            case ',' :
	                sb.append("");
	                break;
	            case '$' :
	                sb.append("");
	                break;
	            case '.' :
	                sb.append("");
	                break;
	            case '\'' :
	                sb.append("");
	                break;
	            case ';' :
	                sb.append("");
	                break;
	            case '{' :
	                sb.append("");
	                break;
	            case '}' :
	                sb.append("");
	                break;
	            default :
	                sb.append(text.charAt(i));
	            break;
	        }
	    }
	    //予約文字の置き換え
	    String result = sb.toString();
	    if(result != null) {
	    	result = result.replaceAll("\\Q as \\E","");
	    	result = result.replaceAll("\\Q_auto_query_identifier_\\E","");
	    	result = result.replaceAll("\\QGROUP BY\\E","");
	    	result = result.replaceAll("\\QSUM(\\E","");
	    	result = result.replaceAll("\\QAVG(\\E","");
	    	result = result.replaceAll("\\QMIN(\\E","");
	    	result = result.replaceAll("\\QMAX(\\E","");
	    	result = result.replaceAll("\\QCOUNT(\\E","");
	    	result = result.replaceAll("\\QORDER BY\\E","");
	    	result = result.replaceAll("\\Q LIMIT \\E","");
	    	result = result.replaceAll("\\Q DESC\\E","");
	    	result = result.replaceAll("\\Q ASC\\E","");
	    	result = result.replaceAll("\\QCASE WHEN\\E","");
	    }
	    //nullまたは空の場合自動で仮のカラム名(エイリアス名)を付与
	    if(result == null || "".equals(result)) {
	    	result = "automatic_column_name_"+System.currentTimeMillis();
	    }
	    return result;
	}
}
