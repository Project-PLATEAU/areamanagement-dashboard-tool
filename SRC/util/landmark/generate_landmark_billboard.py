from PIL import Image,ImageFont,ImageDraw
import os
import json

#
#
# ビルボード色を決定する
def define_bilboard_color(checkValue):
    try:
        color_define = [
            ("駅", (230,121,40)),
            ("学校", (132,245,211)),
            ("警察", (0,0,255)),
            ("消防", (209,0,128)),
            ("地方の機関", (255,0,0)),
            ("博物館", (128,78,87)),
            ("病院", (132,181,87)),
            ("郵便局", (255,0,0))
        ]
        for aDefine in color_define:
            if aDefine[0] == checkValue:
                return aDefine[1]
    except Exception as e:
        print(e)
    # デフォルト
    return (0,190,190)
#
#
# ビルボード画像を作成する
def create_land_mark_image(text, save_path, define_value):
    try:
        # 画像高さ
        image_height = 150
        # ラベル高さ
        label_height = 50
        # ラベル半円半径（ラベル高さの半分）
        label_cirrcle_radius = 25
        # 軸高さ
        axis_height = 100
        # 軸太さ
        axis_width = 2
        # ビルボード色
        billboard_color = define_bilboard_color(define_value)
        # 文字大きさ
        font_size = 20 
        # 文字色
        text_color = (255,255,255)
        # 画像幅
        image_width = len(text) * font_size + label_cirrcle_radius * 2
        font_path = os.environ["FONT_PATH"]
        # 画像オブジェクトを生成
        initImage = Image.new("RGBA", (image_width, image_height), (0,0,0,0))
        # ラベル部図形（四角形）を描画
        draw = ImageDraw.Draw(initImage)
        draw.rectangle([(label_cirrcle_radius,0),(image_width - label_cirrcle_radius, label_height)], fill=billboard_color)
        # ラベル部図形（半円）を描画
        draw.ellipse((0,0,label_height, label_height), fill=billboard_color)
        draw.ellipse((image_width - label_cirrcle_radius*2, 0, image_width, label_height), fill=billboard_color)
        # 軸を描画
        draw.line([(image_width/2, label_height),(image_width/2, label_height + axis_height)], fill=billboard_color, width=axis_width)
        # ラベル文字を描画
        font = ImageFont.truetype(font_path,font_size)
        draw.text((label_cirrcle_radius,10),text,font=font,fill=text_color)
        initImage.save(save_path)
        print("画像を作成:" + save_path)
    except Exception as e:
        print(e)
#
#
# 属性値を設定 使うときに変更。
def set_properties(srcProps):
    res = {}
    try:
        props = [
            {
                "key": "name",
                "alias": "名称"
            },
            {
                "key": "kind",
                "alias": "種別"
            }
        ]
        for aProp in props:
            res[aProp["alias"]] = srcProps[aProp["key"]]
    except Exception as e:
        print(e)
    return res
    
#
#
# CZMLオブジェクトを生成する
def generate_czml_obj(geojson_obj):
    czmlList = []
    try:
        # ヘッダ
        header = {
            "id": "document",
            "name": os.environ["CZML_HEADER_NAME"],
            "version": "1.0"
        }
        czmlList.append(header)
        # 画像格納用フォルダ作成
        folderPath = os.environ["DEST_IMG_FOLDER"]
        if not os.path.exists(folderPath):
            os.mkdir(folderPath)
        # GeoJSONからCZMLオブジェクトを作成
        features = geojson_obj["features"]
        for i in range(len(features)):
            
            label_text = features[i]["properties"][os.environ["LABEL_PROPERTIES"]]
            coordinates = features[i]["geometry"]["coordinates"]
            save_file_name = folderPath + "/" + os.environ["IMAGE_NAME_TEMPLATE"].replace("%s", str(i+1))
            if os.environ["COLOR_LEGEND_PROPERTIES"] in features[i]["properties"]:
                define_value = features[i]["properties"][os.environ["COLOR_LEGEND_PROPERTIES"]]
            else:
                define_value = "aaa"
            # ビルボード画像を生成
            create_land_mark_image(label_text, "./" + save_file_name, define_value)
            # 高さはheightReferenceをRELATIVE_TO_GROUNDとしているため設定不要
            czmlCoords = [coordinates[0], coordinates[1], 0.0]
            a_czml_obj = {
                "id": str(i + 1),
                "name": label_text,
                "properties": set_properties(features[i]["properties"]),
                "billboard": {
                    "eyeOffset": {
                        "cartesian": [0,0,0]
                    },
                    "horizontalOrigin": "CENTER",
                    "image": save_file_name,
                    "pixelOffset": {
                        "cartesian2": [0,0]
                    },
                    "scale": 0.5,
                    "show": True,
                    "verticalOrigin": "BOTTOM",
                    "sizeInMeters": True,
                    "heightReference": "RELATIVE_TO_GROUND"
		        },
                "position": {
                    "cartographicDegrees": czmlCoords
                }
            }
            czmlList.append(a_czml_obj)
    except Exception as e:
        print(e)
    return czmlList
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
#
# メイン処理
def main():
    try:
        # GeoJSONファイルを読み込み
        geojsonFilePath = os.environ['SRC_JSON_FILE']
        geojsonObj = read_geojson(geojsonFilePath)
        # CZMLオブジェクトを作成
        czmlList = generate_czml_obj(geojsonObj)
        # CZMLファイルを保存
        czmlFilePath = os.environ['CZML_FILE_PATH']
        export_czml(czmlList, czmlFilePath)
    except Exception as e:
        print(e)

if __name__ == '__main__':
    # ビルボードフォントパス
    os.environ["FONT_PATH"] = "C:/Windows/Fonts/meiryo.ttc"
    # ソースJSONファイル
    os.environ["SRC_JSON_FILE"] = "./landmark_chino.geojson"
    # 画像保存パス
    os.environ["DEST_IMG_FOLDER"] = "billboard_image"
    os.environ["SAVE_CZML_NAME"] = "./landmark.czml"
    # ラベルとして表示するプロパティ
    os.environ["LABEL_PROPERTIES"] = "name"
    # 色分け表示プロパティ
    os.environ["COLOR_LEGEND_PROPERTIES"] = "kind"
    # 画像名テンプレート
    os.environ["IMAGE_NAME_TEMPLATE"] = "landmark_%s.png"
    # CZMLヘッダのname属性
    os.environ["CZML_HEADER_NAME"] = "landmark"
    # CZMLファイル保存パス
    os.environ['CZML_FILE_PATH'] = "./landmark.czml"
    # メイン処理
    main()