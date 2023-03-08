import json
import math
import os
#
# 2Dのポリラインを3次元の放物線に変換する
#  coorditates = xy原点 maxHeight = 最大高さ separateCount = 始点から終点までの分割回数.
def convert_to_parabora(lonlat_coordinates, plain_coordinates, maxHeight, separateCount):
    res_coords = []
    try:
        # 始点（メートル座標）
        start_point_x = plain_coordinates[0][0][0]
        start_point_y = plain_coordinates[0][0][1]
        # 終点（メートル座標）
        end_point_x = plain_coordinates[0][len(plain_coordinates[0]) -1][0]
        end_point_y = plain_coordinates[0][len(plain_coordinates[0]) -1][1]
        # 放物線は z =a(t-t0)(t+t0) の二次関数で表せる。
        # 2*t0 = (始点から終点の距離)
        # -t0とt0が地表との交点、すなわち始点と終点になる。
        # z=maxHeight, t=0を代入し、放物線の傾きaを求める
        a_value = -1.0 * (4.0 * maxHeight)/(((end_point_x - start_point_x) ** 2) + ((end_point_y - start_point_y) ** 2))
        # 始点から終点に向けてseparateCount回区切り、区切り位置のxy座標と上空に描画する放物線のZ値を求める
        for i in range(separateCount + 1):
            # 緯度経度の座標値を求める
            start_lonlat_x = lonlat_coordinates[0][0][0]
            start_lonlat_y = lonlat_coordinates[0][0][1]
            end_lonlat_x = lonlat_coordinates[0][len(lonlat_coordinates[0]) -1][0]
            end_lonlat_y = lonlat_coordinates[0][len(lonlat_coordinates[0]) -1][1]
            x_t_lonlat = start_lonlat_x + (float(i) * ((end_lonlat_x - start_lonlat_x) / float(separateCount)))
            y_t_lonlat = start_lonlat_y + (float(i) * ((end_lonlat_y - start_lonlat_y) / float(separateCount)))
            # z値を求める
            x_t = start_point_x + (float(i) * ((end_point_x - start_point_x) / float(separateCount)))
            y_t = start_point_y + (float(i) * ((end_point_y - start_point_y) / float(separateCount)))
            a_new_coord = []
            t = abs(((x_t - ((start_point_x + end_point_x) / 2.0)) ** 2) + ((y_t - ((start_point_y + end_point_y) / 2.0)) ** 2))
            z_t = (a_value * t) - (a_value * ((((end_point_x - start_point_x) ** 2) + ((end_point_y - start_point_y) ** 2)) / 4.0))
            if i == 0 or i == separateCount:
                # 始点と終点のz座標は強制的に0とする。
                z_t = 0
            # かさ上げ
            z_t = z_t + float(os.environ["RAISE_HEIGHT"])
            a_new_coord = [x_t_lonlat, y_t_lonlat, z_t]
            res_coords.extend(a_new_coord)
    except Exception as e:
        print(e)
    return res_coords
#
#
# GeoJsonオブジェクトをCMZLオブジェクトに変換する
def convert_geojson_to_czml(lonlat_geojsonObj, plain_geojsoObj):
    try:
        features = lonlat_geojsonObj["features"]
        plain_features = plain_geojsoObj["features"]
        maxHeight = int(os.environ["MAX_HEIGHT"])
        separateCount = int(os.environ["SEPARATE_COUNT"])
        czml_list = []
        czml_top = {
            "id": "document",
            "name": "line",
            "version": "1.0"
        }
        threshold = int(os.environ["DISPLAY_THRESHOLD"])
        czml_list.append(czml_top)
        for i in range(len(features)):
            aCoordinates = features[i]["geometry"]["coordinates"]
            pCoordinates = plain_features[i]["geometry"]["coordinates"]
            addCoords = convert_to_parabora(aCoordinates, pCoordinates, features[i]["properties"]["距離"] / 2, separateCount)
            total = int(features[i]["properties"]["合計"])
            color = setColor(total)
            width = setWidth(total)
            features[i]["properties"]["合計"] = total
            czml_obj = {
                "id": str(i + 1),
                "name": features[i]["properties"]["移動経路"],
                "properties": features[i]["properties"],
                "polyline": {
                    "positions": {
                        "cartographicDegrees": addCoords
                    },
                    "material": {
                        "solidColor" : {
                            "color" : {
                                "rgba" : color
                            }
                        }
                    },
                    "width": width
                }
            }
            # 属性情報を追加
            # propDict = features[i]["properties"]
            # for aKey in propDict.keys():
            #     czml_obj[aKey] = propDict[aKey]
            # 閾値以上のデータを追加する
            if total >= threshold:
                czml_list.append(czml_obj)
        return czml_list
    except Exception as e:
        print(e)
        raise e
#
#
# 太さを設定
def setWidth(value):
    width = 1.0
    try:
        if value < 10:
            width = 8.0
        elif value >= 10 and value < 20:
            width = 8.0
        elif value >= 20 and value < 50:
            width = 8.0
        elif value >= 50 and value < 100:
            width = 8.0
        elif value >= 100 and value < 200:
            width = 8.0
        elif value >= 200 and value < 500:
            width = 8.0
        elif value >= 500 and value < 1000:
            width = 8.0
        elif value >= 1000:
            width = 8.0
    except Exception as e:
        print(e)
    return width
#
# 色を設定
def setColor(value):
    color = [255, 255, 255, 255]
    try:
        if value < 10:
            color = [255,255,179,255]
        elif value >= 10 and value < 20:
            color = [247,245,169,255]
        elif value >= 20 and value < 50:
            color = [248,225,166,255]
        elif value >= 50 and value < 100:
            color = [255,216,192,255]
        elif value >= 100 and value < 200:
            color = [255,183,183,255]
        elif value >= 200 and value < 500:
            color = [255,145,145,255]
        elif value >= 500 and value < 1000:
            color = [242,133,201,255]
        elif value >= 1000:
            color = [220,122,220,255]
    except Exception as e:
        print(e)
    return color
#
#
# GeoJsonファイルを読み取る
def read_geojson(filePath):
    try:
        geo_json = {}
        with open(filePath, encoding="utf-8") as f:
            geo_json = json.load(f)
        return geo_json
    except Exception as e:
        print(e)
#
#
# CZMLファイルを保存する
def export_czml(czml_obj, savePath):
    try:
        with open(savePath, 'w', encoding="utf-8") as f:
            json.dump(czml_obj, f, indent=4)
    except Exception as e:
        print(e)
#
# メイン関数
def main():
    try:
        geojsonFilePath = os.environ['GEOJSON_LONLAT_FILE_PATH']
        plainGeoJsonFilePath = os.environ['GEOJSON_PLAIN_FILE_PATH']
        czmlFilePath = os.environ['CZML_FILE_PATH']
        lonlat_geojson_obj = read_geojson(geojsonFilePath)
        plain_geojson_obj = read_geojson(plainGeoJsonFilePath)
        czml_obj = convert_geojson_to_czml(lonlat_geojson_obj, plain_geojson_obj)
        #print(czml_obj)
        export_czml(czml_obj, czmlFilePath)
    except Exception as e:
        print(e)

if __name__ == '__main__':
    # 緯度経度系のGeoJson
    os.environ['GEOJSON_LONLAT_FILE_PATH'] = "kaiyuu.geojson"
    # 平面直角座標系のGeoJson
    os.environ['GEOJSON_PLAIN_FILE_PATH'] = "kaiyuu_6671.geojson"
    # 出力CZMLファイル名
    os.environ['CZML_FILE_PATH'] = "./ver3/kaiyuu1_parabola.czml"
    # 放物線最大高さ
    os.environ["MAX_HEIGHT"] = "100"
    # 分割回数
    os.environ["SEPARATE_COUNT"] = "20"
    # かさ上げ高さ
    os.environ["RAISE_HEIGHT"] = "40.0"
    # 表示閾値
    os.environ["DISPLAY_THRESHOLD"] = "50"
    main()