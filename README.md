# エリアマネジメント・ダッシュボード
![キービジュアル](./img/key_visual.jpg "エリアマネジメント・ダッシュボード")

## 更新履歴
| 更新日時 | リリース | 更新内容 |
| :--- | :--- | :--- |
|2024/3/29 | **2nd Release** <br> 3D都市モデルビューワと地域情報ダッシュボードを同一画面に集約<br>スマートフォンからの情報閲覧、地域情報の登録機能を実装 | エリアマネジメント・ダッシュボードv2.0 |
|2023/3/30 | **1st Release** <br>エリアマネジメント団体向けの3D都市モデルビューワとダッシュボードを組み合わせた地域情報プラットフォームを開発| エリアマネジメント・ダッシュボードv1.0 |

## **1.概要**

<p>本リポジトリでは、2023年度のProject PLATEAUで開発した「エリアマネジメント・ダッシュボードv2.0」のソースコードを公開しています。  
「エリアマネジメント・ダッシュボードv2.0」は、エリアマネジメント団体向けの3D都市モデルビューワとダッシュボードを組み合わせた地域情報プラットフォームです。</p>

## **2.「エリアマネジメント・ダッシュボードv2.0」について**

<p>「エリアマネジメント・ダッシュボードv2.0」は、イベント情報やバリアフリー情報、災害リスク情報などの地域情報を3D空間上で確認できる「3D都市モデルビューワ」と、各地域情報の⼀覧や集計結果を表やグラフ形式で確認できる「地域情報ダッシュボード」が同一画面にて構成され、建物や場所に紐づいた地点の情報と、地域単位で集計した情報を相互に連携し閲覧できるシステムです。  
また、スマートフォンからの情報閲覧、地域情報の登録機能も実装しており、地域情報の集約、公開が可能です。<br>
詳細は<a href="https://www.mlit.go.jp/plateau/file/libraries/doc/plateau_tech_doc_0080_ver01.pdf">技術検証レポート</a>を参照してください。</p>

## **3.利用手順**

### **インストール**

* 本システムで必要となるソフトウェアを下表に示します。 [稼働環境構築手順書](https://project-plateau.github.io/areamanagement-dashboard-tool/manual/devMan.html)を参照の上、ソフトウェアのインストール及びセットアップを完了させてください。

    [エリアマネジメント・ダッシュボード　稼働環境構築手順書](https://project-plateau.github.io/areamanagement-dashboard-tool/manual/devMan.html)

    |ソフトウェア|プロジェクトフォルダ|
    | - | - |
    |1. PLATEAU VIEW|[/SRC/3dview](./SRC/3dview/)|
    |2. API|[/SRC/api](./SRC/api/)|
    |3. GeoServer|-|

    ※データベースの構築及び認証で使用するミドルウェアのセットアップも合わせて必要となります。

* 構築時に必要となるファイルを下表に示します。

    |ファイル|プロジェクトフォルダ|
    | - | - |
    |1. Postgres環境設定ファイル一式|[/Settings/postgres_settings/](./Settings/postgres_settings/)|
    |2. GeoServer環境設定ファイル一式|[/Settings/geoserver_settings/](./Settings/geoserver_settings/)|
    |3. サンプルデータ一式|[/Settings/area_management_sample_data/](./Settings/area_management_sample_data/)|
    |4. 各種生成ツール|[/SRC/util/](./SRC/util/)|
  

### **使い方**

#### **利用できる動作環境**

最新のデスクトップ版　Chrome、Safari、Edge上

最小システム要件　CPU：2 GHz 4コア以上、システムメモリ（RAM）：4GB以上

#### **操作マニュアル**

本システムの使い方は下記の操作マニュアルを参照ください。

[エリアマネジメント・ダッシュボード　操作マニュアル](https://project-plateau.github.io/areamanagement-dashboard-tool/manual/userMan.html)

## **4.システム概要**

**【一般ユーザ機能】**

* **地域情報の登録・編集機能**

    PC・スマートフォンから3D都市モデルビューワ上で情報を登録、編集できる機能

* **表示テーマ切替機能**

    イベント情報や災害情報といったテーマ単位のページを切り替える機能

* **経路検索機能**

    3D都市モデルビューワ上で、利用者の種別単位(最短・階段、段差が少ない・段差、勾配が少ない・点字ブロック優先)で経路検索ができる機能

* **町丁目検索機能**

    町丁目名で検索ができる機能

**【管理者機能】**

* **テーマ・レイヤ公開管理機能**

    公開するテーマやレイヤを設定できる機能

* **ダッシュボード管理機能**

    ダッシュボードに表示する情報やレイアウトを設定できる機能

* **活動・投稿情報管理機能**

    登録された情報を整理、公開できる機能　

* **地域統計・回遊性情報管理機能**

    地域統計情報・回遊性情報の追加、更新、出力ができる機能

* **ユーザ管理機能**

    ユーザの発行、ユーザの権限管理ができる機能

## **5.利用技術**
|名称|説明|
| --- | --- |
|[Apache HTTP Server](https://httpd.apache.org/)|Webアプリで配信を行うためのWebサーバソフトウェア|
|[TerriaJS](https://terria.io/)|UIの提供及びUIを介してCesiumJSの描画機能を制御するためのライブラリ|
|[CesiumJS](https://cesium.com/platform/cesiumjs/)|3Dビューワ上にデータを描画するためのライブラリ|
|[Node.js](https://nodejs.org/en)|3Dビューワの実行環境|
|[Apache Tomcat](https://tomcat.apache.org/)|GeoServer、カスタムアプリを起動するJ2EEのSDK|
|[Spring Boot](https://spring.io/projects/spring-boot/)|Javaで利用可能なWebアプリのフレームワーク|
|[GeoServer](https://geoserver.org/)|各種データをWMS及びWFSなどで配信するためのGISサーバ|
|[PostgreSQL](https://www.postgresql.org/)|各種配信するデータを格納するリレーショナルデータベース|
|[pgRouting](https://pgrouting.org/)|PostgreSQLでルート検索を可能とする拡張機能|
|[PostGIS](https://postgis.net/)|PostgreSQLで位置情報を扱うことを可能とする拡張機能|
|[React.js](https://ja.react.dev/)|JavaScriptのフレームワーク内で機能するUIを構築するためのライブラリ|
|[react-grid-layout](https://github.com/react-grid-layout/react-grid-layout)|グリッドレイアウトを実現するためのライブラリ|
|[Chart.js](https://www.chartjs.org/)|グラフ・チャートを描画するライブラリ|
|[ag-grid](https://www.ag-grid.com/)|グルーピング・集計・フィルタリング等をするためのライブラリ|

## **6.動作環境**
最新のデスクトップ版　Chrome、Safari、Edge上

最小システム要件　CPU：2 GHz 4コア以上、システムメモリ（RAM）：4GB以上

## **7.本リポジトリのフォルダ構成**
```Text
/
│
├─Settings
│  ├─area_management_sample_data
│  ├─geoserver_settings
│  └─postgres_settings
│
└─SRC
    ├─3dview
    ├─api
    └─util
```
|対象フォルダ|説明|
| --- | --- |
|[/Settings/postgres_settings/](./Settings/postgres_settings/)|Postgres環境設定ファイル一式|
|[/Settings/geoserver_settings/](./Settings/geoserver_settings/)|GeoServer環境設定ファイル一式|
|[/Settings/area_management_sample_data/](./Settings/area_management_sample_data/)|サンプルデータ一式|
|[/SRC/3dview](./SRC/3dview/)|フロント側のソースコード一式|
|[/SRC/api](./SRC/api/)|API側のソースコード一式|
|[/SRC/util/](./SRC/util/)|各種生成ツール|
## **8.ライセンス** <!-- 定型文のため変更しない -->
* ソースコードおよび関連ドキュメントの著作権は国土交通省に帰属します。
* 本ドキュメントは[Project PLATEAUのサイトポリシー](https://www.mlit.go.jp/plateau/site-policy/)（CCBY4.0および政府標準利用規約2.0）に従い提供されています。

## **9.注意事項** <!-- 定型文のため変更しない -->

* 本レポジトリは参考資料として提供しているものです。動作保証は行っておりません。
* 予告なく変更・削除する可能性があります。
* 本レポジトリの利用により生じた損失及び損害等について、国土交通省はいかなる責任も負わないものとします。

## **10.参考資料**　 <!-- 各リンクは納品時に更新 -->

* エリアマネジメント・ダッシュボードの構築v2.0 技術検証レポート:https://www.mlit.go.jp/plateau/file/libraries/doc/plateau_tech_doc_0080_ver01.pdf
* エリアマネジメント・ダッシュボードの構築v1.0 技術検証レポート:https://www.mlit.go.jp/plateau/file/libraries/doc/plateau_tech_doc_0023_ver01.pdf
* PLATEAU Webサイト Use caseページ「エリアマネジメント・ダッシュボードの構築v2.0」:https://www.mlit.go.jp/plateau/use-case/uc23-10/
* Project-PLATEAU PLATEAU-VIEW:https://github.com/Project-PLATEAU/PLATEAU-VIEW
* Project-PLATEAU terriajs:https://github.com/Project-PLATEAU/terriajs
