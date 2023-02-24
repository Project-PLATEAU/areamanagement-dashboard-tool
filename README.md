# エリアマネジメント3D都市モデルビューワ

## 概要

![エリアマネジメント3D都市モデルビューワ](https://hiroshimauc-open.s3.ap-northeast-1.amazonaws.com/readme/sample.PNG "エリアマネジメント3D都市モデルビューワ")

本ソフトウェアは、国土交通省の[Project PLATEAU](https://www.mlit.go.jp/plateau/)をベースとしエリアマネジメント機能が拡張されたものとなります。

[Project PLATEAU](https://www.mlit.go.jp/plateau/)の標準機能に加え、各自治体の統計データの可視化、エリマネ・イベント活動の登録及び管理、利用者条件に基づく経路探索機能を利用することができます。

本ソフトウェアの利用にあたっては下表の環境構築が別途必要となります。

|ソフトウェア名|リポジトリ|
| - | - |
|1.Metabase|http://example.com|
|2.GeoServer|-|
|3.エリアマネジメントAPI|http://example.com|

システム全体の環境構築の詳細については[実証環境構築マニュアル](http://example.com)を参照ください。

ここでは、本ソフトウェアの利用方法について説明します。

## 1．/wwwroot/init/erimane_init.jsonの設定
- homeCamera、initialCameraの設定

表示させたい位置に合わせてホームカメラ、初期カメラ位置の設定値を変更してください。
```json
"homeCamera": {
        "north": 35.81489,　　// ホーム表示北限
        "east": 139.95451, // ホーム表示東限
        "south": 35.53022, // ホーム表示南限
        "west": 139.63595 // ホーム表示西限
},
"initialCamera": {
    "west": 139.7383852716238, // 初期表示西限
    "south": 35.669499299672474, // 初期表示南限
    "east": 139.78656930734346, // 初期表示東限
    "north": 35.691640758557284, // 初期表示北限
    "position": {
        "x": -3962169.8364513335, // 初期表示カメラ位置(x)
        "y": 3352586.704708458, // 初期表示カメラ位置(y)
        "z": 3698023.4610325196 // // 初期表示カメラ位置(z)
    },
    "direction": {
        "x": 0.7259273188627007,   //初期表示カメラ視線方向(x)
        "y": -0.5727789601171913,  //初期表示カメラ視線方向(y)
        "z": 0.380727974511829 //初期表示カメラ視線方向(z)
    },
    "up": {
        "x": -0.27833913197344085, //初期表示カメラ回転方向(x)
        "y": 0.2615484931093981,　 //初期表示カメラ回転方向(y)
        "z": 0.9241859733649255　//初期表示カメラ回転方向(z)
    }
}

```
- 航空写真の設定

表示させたい航空写真の設定が行えます。
設定の際は、baseMapsのitemsを編集します。
```json
"item": {
    "type": "composite",
    // idは一意の値
    "id": "/basemap/航空写真（○○市）",
    // 画面に表示する名称
    "name": "航空写真（○○市）",
    "members": [
        {
            "type": "cesium-terrain",
            "id": "/basemap/3D地形データ(○○市)/terrain",
            "name": "sample-terrain",
            // 地形(terrain)データをCesium ionに登録済みの
            // アクセストークンとアセットID
            "ionAccessToken": "ionAccessTokenを設定",
            "ionAssetId": 99999,
            "attribution": "地形データ：Cesium ionで配信"
        },
        {
            "type": "open-street-map",
            "opacity": 1,
            "id": "basemap//航空写真(○○市)/imagery",
            "name": "航空写真(○○市)",
            // 参照する航空写真のURL
            "url": "/resource/photo/",
            "fileExtension": "png",
            "attribution": "sample photo"
        }
    ]
},
// 参照する航空写真のURL
"image": "./images/bgmap_○○_areaphoto.png"

...

"enabledBaseMaps": [
    "/basemap/全国最新写真 (シームレス)",
    "/basemap/空中写真 (Bing)",
    "/basemap/地理院地図 (淡色)",
    "/basemap/Dark Matter",
    // 追加した航空写真のid
    "/basemap/航空写真（○○市）"
]

```
- 建物モデルの設定

表示させたい建物モデルの設定が行えます。
catalogのmembersの中の「//データセット/建物モデル」を編集します。
```json
"members": [
    {
        "id": "//データセット/建物モデル",
        // 画面に表示する名称
        "name": "建物モデル（○○市）",
        "type": "3d-tiles",
        "hideSource": true,
        // 設定済みのtileset.jsonへのアクセスURL
        "url":"/resource/3dtiles/…/tileset.json"
    },
```
- エリマネ・イベント活動、歩行者空間ネットワークの表示レイヤ設定

エリマネ・イベント活動、歩行者空間ネットワークの表示レイヤ設定を行います。本ソフトウェアで必須の設定となります。

GeoServerでのレイヤ作成方法については[実証環境構築マニュアル](http://example.com)を参照ください。

対象：「//データセット/共通/歩行者ネットワークデータ」、「//データセット/地域・エリマネ活動可視化」、「//データセット/賑わい創出/イベント活動結果」
```json
{
    "id": "//データセット/地域・エリマネ活動可視化",
    "name": "地域・エリマネ活動可視化",
    "type": "group",
    "members": [
        {
            "id": "//データセット/地域・エリマネ活動可視化/エリマネ活動結果",
            // 画面に表示する名称
            "name": "エリマネ活動結果",
            "type": "wms",
            "hideSource": true,
            // GeoServerのURL
            "url": "/geoserver/dummy/wms",
            // GeoServerのレイヤ名
            "layers": "dummy:activity"
        }
    ]
}
```
- 回遊性情報の表示レイヤ設定

回遊性情報の表示レイヤ設定を行います。本ソフトウェアで必須の設定となります。

GeoServerでのレイヤ作成、回遊性情報のczml作成方法については[実証環境構築マニュアル](http://example.com)を参照ください。

```json
"id": "//データセット/賑わい創出/回遊分析情報/1回目",
"name": "回遊性分析結果_1回目",
"type": "group",
"members": [
    {
        // IDは定型　回数のみ変更可能
        "id": "//データセット/賑わい創出/回遊分析情報/1回目/人気スポット(3D)",
        "name": "人気スポット(3D)_1回目",
        "type": "composite",
        "members": [
            {
               "id": "//データセット/賑わい創出/回遊分析情報/1回目/人気スポット3D表示",
                "name": "人気スポット(3D)",
                "type": "wfs",
                "hideSource": true,
                // GeoServerのURL
                "url": "/geoserver/dummy/ows",
                // GeoServerのレイヤ名
                "typeNames": "dummy:回遊性人気スポット1",
                  …
            },
            {
                "id": "//データセット/賑わい創出/回遊分析情報/1回目/人気スポット3Dラベル",
                "name": "人気スポット(3D)",
                "type": "wfs",
                "hideSource": true,
                // GeoServerのURL
                "url": "/geoserver/dummy/ows",
                // GeoServerのレイヤ名
                "typeNames": "dummy:人気スポット1",
                 …
            },
            {
                "id": "//データセット/賑わい創出/回遊分析情報/1回目/人気スポット(2D)",
                "name": "人気スポット(2D)_1回目",
                "type": "wms",
                "hideSource": true,
                // GeoServerのURL
                "url": "/geoserver/dummy/ows",
                // GeoServerのレイヤ名
                "layers": "dummy:回遊性人気スポット1"
            },
            {
                "id": "//データセット/賑わい創出/回遊分析情報/1回目/イベント回遊情報",
                "name": "イベント回遊情報_1回目",
                "type": "czml",
                // イベント回遊情報のczmlを参照するURL
                "url": "/resource/kaiyuu/kaiyuu1_parabola.czml",
                …
            }

```

## 2．エリアマネジメントAPI、GeoServer及びMetabaseの連携設定

構築済みの[エリアマネジメントAPI](http://example.com)、[GeoServer](http://example.com)のURL、[Metabase](http://example.com)の共有URLを設定します。

/packages/terriajs/customconfig.jsonの設定で下表の箇所を確認してください。

その他のプロパティについては[実証環境構築マニュアル](http://example.com)を参照ください。

|プロパティ名|説明|
| - | - |
|apiUrl|構築済みの[エリアマネジメントAPI](http://example.com)のURLパスを設定<br>末尾の/(スラッシュ)は省略|
|geoServerUrl|構築済みの[GeoServer](http://example.com)のURLパスを設定<br>末尾の/(スラッシュ)は省略|
|activityDetailsUrlForErimane|構築済みの[Metabase](http://example.com)で取得したエリマネ活動情報の共有URLを設定|
|activityDetailsUrlForEvent|構築済みの[Metabase](http://example.com)で取得したイベント活動情報の共有URLを設定|

## 動作環境、前提ソフトウェア

動作環境

* Node.js v10.0 以降、npm v6.0 以降、yarn v1.19.0 以降

前提ソフトウェア

* 前提に必要となるソフトウェアを示します。

|ソフトウェア|リポジトリ|
| - | - |
|1.Metabase|http://example.com|
|2.GeoServer|-|
|3.エリアマネジメントAPI|http://example.com|

## 利用方法

1. [実証環境構築マニュアル](http://example.com)の手順に沿って前提ソフトウェアの環境構築を済ませてください。
※データベースの構築及び認証で使用するミドルウェアのセットアップも合わせて必要となります。
1. 本レポジトリの一式をダウンロードし、任意のディレクトリに置きます。
	1. プロジェクトフォルダ直下でnodejs依存モジュールのインストールを行います。
        ```bash
        export NODE_OPTIONS=--max_old_space_size=4096
        yarn
        ```
	1. ビルドを実施します。
        ```bash
        yarn gulp release
        ```
	1. プロジェクトフォルダ直下のwwwroot、node_modules、ecosystem.config.js、ecosystem-production.config.js、productionserverconfig.jsonをサーバに配置してください。

    1. プロジェクトフォルダ直下のdashboard-uiフォルダ(ダッシュボード画面)の静的リソースを/usr/local/apache2/htdocs/に配置をしてください。対象のリソースは下表に示します。
        |リソース名|説明|
        | - | - |
        |csvdata|地域統計／回遊性情報の更新画面|
        |dashboard|地域情報ダッシュボード画面|
        |login|ログイン画面| 
        |logout|ログアウト画面| 
        |menu|メニュー画面| 
        |meta|ダッシュボード画面設定ファイル| 
        |migratoryinfo|地域統計／回遊性情報ダッシュボード画面|
        |redirect|リダイレクト画面| 
        |image|イベント回遊情報の説明画像等| 
        ```bash
        cd /home/upload/dashboard-ui/
        mv csvdata /usr/local/apache2/htdocs/
        mv dashboard/usr/local/apache2/htdocs/
        mv login /usr/local/apache2/htdocs/
        mv logout /usr/local/apache2/htdocs/
        mv menu /usr/local/apache2/htdocs/
        mv meta /usr/local/apache2/htdocs/
        mv migratoryinfo /usr/local/apache2/htdocs/
        mv redirect /usr/local/apache2/htdocs/
        mv image /usr/local/apache2/htdocs/
        ```
        meta/config.jsonは下表のプロパティ設定を確認してださい。
        |プロパティ名|説明|
        | - | - |
        |title|ログイン画面、メニュー画面で使用するトップタイトルテキスト|
        |kaiyuseiMetabaseTemplateUrl|回遊性情報で読み込むiframeのテンプレートURL|
        |dashboardMenu|地域情報ダッシュボード画面のメニュー設定|
        |statisticsItemsInformation|地域統計／回遊性情報ダッシュボード画面で使用する項目名|
    
    1. プロジェクトフォルダ直下のapache-confフォルダにあるhttpd.conf及び認証で使用するauthentication.rbの配置を行います。httpd.confは置き換え前に必ず退避させてください。
        |リソース名|説明|
        | - | - |
        |httpd.conf|apache設定ファイル|
        |authentication.rb|認証スクリプト|
        ```bash
        mkdir /home/backup/
        mv /usr/local/apache2/conf/httpd.conf /home/backup/
        cd /home/upload/apache-conf/
        mv httpd.conf /usr/local/apache2/conf/
        mv authentication.rb /usr/local/apache2/conf/
        /usr/local/apache2/bin/apachectl restart
        ```
        authentication.rbでは下表のプロパティ設定を確認してださい。
        |プロパティ名|説明|
        | - | - |
        |SECRET_KEY|JWTの署名検証を行う秘密鍵|

    1. エリアマネジメント3D都市モデルビューワを実行します。
        ```bash
        cd /opt/PLATEAU_VIEW
        
        ./node_modules/.bin/pm2 start ecosystem-production.config.js --update-env --env production

        ```
    
    1. http://【サーバのIP】/login/にアクセスしログインを行い3DVIEWERを選択からエリアマネジメント3D都市モデルビューワを起動できます。
        
        1. ログイン画面にアクセスします。
        
            ![エリアマネジメントDASHBOARD-ログイン画面](https://hiroshimauc-open.s3.ap-northeast-1.amazonaws.com/readme/login.PNG "エリアマネジメントDASHBOARD-メニュー画面")
        1. 3DVIEWERを選択してください。
        
            ![エリアマネジメントDASHBOARD-ログイン画面](https://hiroshimauc-open.s3.ap-northeast-1.amazonaws.com/readme/menu.PNG "エリアマネジメントDASHBOARD-メニュー画面")
        1. エリアマネジメント3D都市モデルビューワが起動されます。
        
            ![エリアマネジメント3D都市モデルビューワ](https://hiroshimauc-open.s3.ap-northeast-1.amazonaws.com/readme/3dviewer.PNG "エリアマネジメント3D都市モデルビューワ")

    1. エラーが出力された場合、ログファイルを参照し、エラーの原因を分析して下さい。

    1. その他、本ソフトウェアのデプロイについては[Project PLATEAU](https://github.com/Project-PLATEAU/PLATEAU-VIEW)及び[TerriaJs](https://docs.terria.io/guide/deploying/deploying-terriamap/)を参考にしてください。

## ライセンス

Copyright (C) 2023 xxxxxxxxxx, xxxxxxxxxx.

本ソフトウェアは[Apache-2.0 License](LICENSE)を適用します。

## 注意事項

* 本レポジトリは参考資料として提供しているものです。動作保証は行っておりません。
* 予告なく変更・削除する可能性があります。
* 本レポジトリの利用により生じた損失及び損害等について、国土交通省及び著作権者はいかなる責任も負わないものとします。

## 参考資料

* Project PLATEAU: https://github.com/Project-PLATEAU
* TerriaJS: https://docs.terria.io/guide/deploying/deploying-terriamap/
