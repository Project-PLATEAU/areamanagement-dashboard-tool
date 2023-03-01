# <center>令和4年度<br>開発ユースケース「XXXX」の成果物</center>

## **概要**

本ソフトウェアは、国土交通省の[Project PLATEAU](https://www.mlit.go.jp/plateau/)をベースにエリアマネジメント機能の拡張を行いOSS化されたものです。

【OSS化するものに対するどのようなドキュメントか】サンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキスト

本ソフトウェアの利用にあたっては下表の環境構築が別途必要となります。

|ソフトウェア名|プロジェクトフォルダ|
| - | - |
|1.Metabase|[/SRC/metabase](https://github.com/sample/metabase)|
|2.GeoServer|-|
|3.API|[/SRC/api](./SRC/api/)|

システム全体の環境構築の詳細については[環境構築手順書](https://<user>.github.io/<repository>/sample)を参照ください。

## **「XXX」（ユースケース名）について**

### **ユースケースの概要**

サンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキスト

### **開発システムの概要**

サンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキストサンプルテキスト

## **利用手順**

### **前提ソフトウェア**
* 前提に必要となるソフトウェアを下表に示します。
先に[環境構築手順書](https://<user>.github.io/<repository>/sample)を参照の上、前提ソフトウェアのインストール及びセットアップを完了させてください。

    |ソフトウェア|プロジェクトフォルダ|
    | - | - |
    |1.Metabase|[リポジトリ](https://github.com/sample/metabase)|
    |2.GeoServer|-|
    |3.API|[/SRC/api](./SRC/api/)|

    ※データベースの構築及び認証で使用するミドルウェアのセットアップも合わせて必要となります。

### **インストール**
* 構築時に必要となるファイル等を下表に示します。

    |ファイル|プロジェクトフォルダ|備考|
    | - | - | - |
    |1.環境設定ファイル一式|[/Release/environmant_settings.zip](/Release/environmant_settings.zip)|zipファイル内の各ファイルについては[環境構築手順書](http://example.com)を参照してください。
    |2.サンプルデータ一式|[/Release/area_management_sample_data.zip](/Release/area_management_sample_data.zip)|zipファイル内の各ファイルについては[環境構築手順書](http://example.com)を参照してください。
  
### **ビルド及びデプロイ時に必要な動作環境**

 * Node.js v10.0 以降、npm v6.0 以降、yarn v1.19.0 以降を必要に応じてマシーンにインストールしてください。

### **SRC/3dview/wwwroot/init/erimane_init.jsonの設定**
ビルド及びデプロイ実施前に下記の設定を確認してください。
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
- エリマネ・イベント活動、歩行者ネットワークの表示レイヤ設定

    エリマネ・イベント活動、歩行者ネットワークの表示レイヤ設定を行います。本ソフトウェアで必須の設定となります。

    GeoServerでのレイヤ作成方法については[環境構築手順書](https://<user>.github.io/<repository>/sample)を参照ください。

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

    GeoServerでのレイヤ作成、回遊性情報のczml作成方法については[環境構築手順書](https://<user>.github.io/<repository>/sample)を参照ください。

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

### **SRC/3dview/packages/terriajs/customconfig.jsonの設定**

ビルド及びデプロイ実施前に下記の設定を確認してください。

構築済みの[API](./SRC/api/)、[GeoServer](https://geoserver.org/release/stable/)のURL、[Metabase](https://github.com/sample/metabase)の共有URLを設定します。

SRC/3dview/packages/terriajs/customconfig.jsonの設定で下表の箇所を確認してください。

その他のプロパティについては[環境構築手順書](https://<user>.github.io/<repository>/sample)を参照ください。

|プロパティ名|説明|
| - | - |
|apiUrl|構築済みの[API](./SRC/api/)のURLパスを設定<br>末尾の/(スラッシュ)は省略|
|geoServerUrl|構築済みの[GeoServer](https://geoserver.org/release/stable/)のURLパスを設定<br>末尾の/(スラッシュ)は省略|
|activityDetailsUrlForErimane|構築済みの[Metabase](https://github.com/sample/metabase)で取得したエリマネ活動情報の共有URLを設定|
|activityDetailsUrlForEvent|構築済みの[Metabase](https://github.com/sample/metabase)で取得したイベント活動情報の共有URLを設定|


### **ビルド及びデプロイ**

1. [環境構築手順書](https://<user>.github.io/<repository>/sample)の手順に沿って前提ソフトウェアの環境構築を済ませてください。
※データベースの構築及び認証で使用するミドルウェアのセットアップも合わせて必要となります。
1. gitコマンド又は手動で3dviewのソースコードをダウンロードし、任意のディレクトリに置きます。前提ソフトウェアの環境構築時に既にダウンロードしている場合は不要です。
    
    - gitコマンドで一式ダウンロードする場合
        ```bash
        git clone 本リポジトリURL erimane-dashboard-tool
        cd erimane-dashboard-tool/SRC/3dview
        ```

    - gitコマンドで対象のソースコードのみダウンロードしたい場合

        ```bash
        mkdir 3dview
        cd 3dview
        git init
        git config core.sparsecheckout true
        git remote add origin 本リポジトリURL
        vi .git/info/sparse-checkoutsparse-checkout
        ```
        ※以下を記載して保存してください 
        
        ```bash
        SRC/3dview
        ```

        ```bash
        git pull origin master
        cd SRC/3dview
        ```
1. SRC/3dview/wwwroot/init/erimane_init.json及びSRC/3dview/packages/terriajs/customconfig.jsonを設定してください。
1. 3dviewのプロジェクトフォルダ直下でnodejs依存モジュールのインストールを行います。
    ```bash
    cd SRC/3dview
    export NODE_OPTIONS=--max_old_space_size=4096
    yarn
    ```
1. ビルドを実施します。
    ```bash
    yarn gulp release
    ```
1. 3dviewのプロジェクトフォルダ直下のwwwroot、node_modules、ecosystem.config.js、ecosystem-production.config.js、productionserverconfig.jsonをサーバに配置してください。
/home/upload/に転送後の配置例を下記に示します。
    ```bash
    cd /home/upload/
    mv wwwroot /opt/PLATEAU_VIEW/
    mv node_modules /opt/PLATEAU_VIEW/
    mv ecosystem.config.js /opt/PLATEAU_VIEW/
    mv ecosystem-production.config.js /opt/PLATEAU_VIEW/
    mv productionserverconfig.json /opt/PLATEAU_VIEW/
    ```

1. 3dviewのプロジェクトフォルダ直下のdashboard-uiフォルダ(ダッシュボード画面)の静的リソースを/usr/local/apache2/htdocs/に配置をしてください。対象のリソースは下表に示します。
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

    dashboard-uiフォルダを/home/upload/に転送後の配置例を下記に示します。
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
    meta/config.jsonは下表のプロパティ設定を確認してください。
    |プロパティ名|説明|
    | - | - |
    |title|ログイン画面、メニュー画面で使用するトップタイトルテキスト|
    |kaiyuseiMetabaseTemplateUrl|回遊性情報で読み込むiframeのテンプレートURL|
    |dashboardMenu|地域情報ダッシュボード画面のメニュー設定|
    |statisticsItemsInformation|地域統計／回遊性情報ダッシュボード画面で使用する項目名|

1. 3dviewのプロジェクトフォルダ直下のapache-confフォルダにあるhttpd.conf及び認証で使用するauthentication.rbの配置を行います。httpd.confは置き換え前に必ず退避させてください。
    |リソース名|説明|
    | - | - |
    |httpd.conf|apache設定ファイル|
    |authentication.rb|認証スクリプト|

    apache-confフォルダを/home/upload/に転送後の配置例を下記に示します。
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

1. http(s)：//【サーバのIP or domain】/login/にアクセスしログインを行い3DVIEWERを選択からエリアマネジメント3D都市モデルビューワを起動できます。
    
    1. ログイン画面にアクセスします。
    
        ![エリアマネジメントDASHBOARD-ログイン画面](./img/capture1.PNG "エリアマネジメントDASHBOARD-メニュー画面")
    1. 3DVIEWERを選択してください。
    
        ![エリアマネジメントDASHBOARD-ログイン画面](./img/capture2.PNG "エリアマネジメントDASHBOARD-メニュー画面")
    1. エリアマネジメント3D都市モデルビューワが起動されます。
    
        ![エリアマネジメント3D都市モデルビューワ](./img/capture3.PNG "エリアマネジメント3D都市モデルビューワ")

1. エラーが出力された場合、ログファイルを参照し、エラーの原因を分析して下さい。

1. その他、本ソフトウェアのデプロイについては[Project PLATEAU](https://github.com/Project-PLATEAU/PLATEAU-VIEW)及び[TerriaJs](https://docs.terria.io/guide/deploying/deploying-terriamap/)を参考にしてください。

### **使い方**
#### **利用できる動作環境**

最新のデスクトップ版　Chrome、Edge上で動作します。

最小システム要件　CPU：2GHｚデュアルコア以上、システムメモリ（RAM）：4GB

#### **権限別による拡張機能の利用モードについて**
- 管理者権限の場合
    
    DASHBOARD画面での管理者機能の使用が行えます。
    
    ![エリアマネジメントDASHBOARD画面](./img/capture4.PNG "エリアマネジメントDASHBOARD画面")

    3D都市モデルビューワで活動情報の閲覧・登録・編集、経路探索機能が使用できます。

    ![エリアマネジメント3D都市モデルビューワ](./img/capture5.PNG "エリアマネジメント3D都市モデルビューワ")

- 利用者権限の場合

    DASHBOARD画面での管理者機能以外の使用が行えます。

    ![エリアマネジメントDASHBOARD画面](./img/capture6.PNG "エリアマネジメントDASHBOARD画面")

    3D都市モデルビューワで活動情報の閲覧、経路探索機能が使用できます。

    ![エリアマネジメント3D都市モデルビューワ](./img/capture7.PNG "エリアマネジメント3D都市モデルビューワ")

#### **活動情報の閲覧・登録・編集**

1. まず、管理者でログイン後、3D都市モデルビューワを表示してください。

    ![エリアマネジメント3D都市モデルビューワ](./img/capture8.PNG "エリアマネジメント3D都市モデルビューワ")

1. 「活動登録モード」ボタンを押下すると、選択した地点に活動の登録を行えるようになります。また、「活動登録モード解除」ボタンを押下すると活動登録モードを終了できます。

    ![エリアマネジメント3D都市モデルビューワ](./img/capture9.PNG "エリアマネジメント3D都市モデルビューワ")

1. 活動の登録を行いたい対象の地点を選択すると、活動登録画面が表示されます。内容に従って記載を行い登録をしてください。また、活動登録画面では画像ファイル及びPDFファイルを対象とした活動情報に紐づく添付ファイルの登録が行えます。（1ファイル上限10MB）

    ![エリアマネジメント3D都市モデルビューワ](./img/capture10.PNG "エリアマネジメント3D都市モデルビューワ")

1. 活動の編集を行いたい場合、対象地点のレイヤを選択してください。属性情報で登録済みの活動情報を閲覧することができます。また、「詳細」ボタンでmetabaseのdashboard画面を開くことが可能です。

    ![エリアマネジメント3D都市モデルビューワ](./img/capture11.PNG "エリアマネジメント3D都市モデルビューワ")
    ※「詳細」ボタン押下後の遷移
    ![エリアマネジメント3D都市モデルビューワ](./img/capture18.PNG "エリアマネジメント3D都市モデルビューワ")

1. 属性情報の「編集」ボタンで活動情報の編集画面が表示されます。編集画面では活動情報の編集及び、対象地点への追加登録が行えます。

    ![エリアマネジメント3D都市モデルビューワ](./img/capture12.PNG "エリアマネジメント3D都市モデルビューワ")

#### **経路探索**

1. 歩行者ネットワークレイヤを表示後、「経路」ボタンを押下すると経路探索パネルが表示されます。

    ![エリアマネジメント3D都市モデルビューワ](./img/capture13.PNG "エリアマネジメント3D都市モデルビューワ")

1. 経路探索を行いたい開始地点を選択してください。開始地点に選択した地点の緯度経度情報が表示され、黄色の開始ポイントが表示されます。

    ![エリアマネジメント3D都市モデルビューワ](./img/capture14.PNG "エリアマネジメント3D都市モデルビューワ")

1. 次に経路探索を行いたい終了地点を選択してください。終了地点に選択した地点の緯度経度情報が表示され、赤色の終了ポイントが表示されます。

    ![エリアマネジメント3D都市モデルビューワ](./img/capture15.PNG "エリアマネジメント3D都市モデルビューワ")

1. 条件を指定後、「検索」ボタンで経路探索結果が表示されます。
    ![エリアマネジメント3D都市モデルビューワ](./img/capture17.PNG "エリアマネジメント3D都市モデルビューワ")

#### **回遊性情報**

1. 回遊性情報のDASHBOARD画面を表示して各統計情報の閲覧が行えます。

    ![エリアマネジメント3D都市モデルビューワ](./img/capture21.PNG "エリアマネジメント3D都市モデルビューワ")

1. 回遊性情報又は人気スポットのグラフを押下することで3D都市モデルビューワ上の対象地点に遷移できます。

    ![エリアマネジメント3D都市モデルビューワ](./img/capture22.PNG "エリアマネジメント3D都市モデルビューワ")
    ![エリアマネジメント3D都市モデルビューワ](./img/capture23.PNG "エリアマネジメント3D都市モデルビューワ")

#### **統計情報**

その他各種統計情報をmetabaseで設定することで各自治体に応じた統計情報の可視化が行えます。

![エリアマネジメント3D都市モデルビューワ](./img/capture20.PNG "エリアマネジメント3D都市モデルビューワ")

使用可能な統計項目一覧を下表に示します。
|項目名|
| - |
|地価公示価格|
|認知度|
|町丁目別人口世帯数|
|町丁目別年齢別人口|
|町丁目別世帯人員別世帯数|
|事務所数|
|従業者数|
|駅の乗降客数|
|商業施設|
|商圏調査の支持率|
|市まとめ|
|回遊性情報|

## **ライセンス** <!-- 定型文のため変更しない -->
* ソースコードおよび関連ドキュメントの著作権は国土交通省に帰属します。
* 本ドキュメントは[Project PLATEAUのサイトポリシー](https://www.mlit.go.jp/plateau/site-policy/)（CCBY4.0および政府標準利用規約2.0）に従い提供されています。

## **注意事項** <!-- 定型文のため変更しない -->

* 本レポジトリは参考資料として提供しているものです。動作保証は行っておりません。
* 予告なく変更・削除する可能性があります。
* 本レポジトリの利用により生じた損失及び損害等について、国土交通省はいかなる責任も負わないものとします。

## **参考資料**　 <!-- 各リンクは納品時に更新 -->
* （ユースケース名）技術検証レポート: https://www.mlit.go.jp/plateau/libraries/technical-reports/
*  PLATEAU Webサイト Use caseページ「（ユースケース名）」: https://www.mlit.go.jp/plateau/use-case/
* （利用しているライブラリなどへのリンク）
