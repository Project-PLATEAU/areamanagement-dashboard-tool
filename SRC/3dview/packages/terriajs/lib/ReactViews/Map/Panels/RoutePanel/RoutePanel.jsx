import { observer } from "mobx-react";
import PropTypes from "prop-types";
import React from "react";
import { withTranslation } from "react-i18next";
import { withTheme } from "styled-components";
import Icon, { StyledIcon } from "../../../../Styled/Icon";
import Spacing from "../../../../Styled/Spacing";
import Text from "../../../../Styled/Text";
import Box from "../../../../Styled/Box";
import Input from "../../../../Styled/Input";
import Select from "../../../../Styled/Select";
import Button, { RawButton } from "../../../../Styled/Button";
import CommonStrata from "../../../../Models/Definition/CommonStrata";
import Cesium from "../../../../Models/Cesium";
import Cartographic from "terriajs-cesium/Source/Core/Cartographic";
import webMapServiceCatalogItem from '../../../../Models/Catalog/Ows/WebMapServiceCatalogItem';
import CzmlCatalogItem from '../../../../Models/Catalog/CatalogItems/CzmlCatalogItem';
import createWorldTerrain from "terriajs-cesium/Source/Core/createWorldTerrain";
import sampleTerrainMostDetailed from "terriajs-cesium/Source/Core/sampleTerrainMostDetailed";
import Ellipsoid from "terriajs-cesium/Source/Core/Ellipsoid";
import Config from "../../../../../customconfig.json";
import Styles from "./RoutePanel.scss";
import { action, runInAction } from "mobx";
import {getShareData} from "../../../Map/Panels/SharePanel/BuildShareLink";

/**
 * 避難経路検索画面
 */
@observer
class RoutePanel extends React.Component {
  static displayName = "RoutePanel";

  static propTypes = {
    terria: PropTypes.object.isRequired,
    viewState: PropTypes.object.isRequired,
    theme: PropTypes.object,
    t: PropTypes.func.isRequired
  };

  constructor(props) {
    super(props);
    this.state = {
      viewState: props.viewState,
      terria: props.terria,
      isAnimatingOpen: true,
      routeStart: props.terria.routeStart,
      routeEnd: props.terria.routeEnd,
      routeItems: [
        { title: "最短のルート（健常者向け）" },
        { title: "階段・段差が少ないルート（車いす/杖・義手義足利用者向け）" },
        { title: "段差・勾配が少ないルート（高齢者・乳幼児むけ）" },
        { title: "点字ブロックを優先したルート（視覚障害者）" }
      ],
      showFlag:'hidden',
      networkId: "//データセット/共通/歩行者ネットワークデータ/歩行者空間ネットワーク(2D表示)",
    };
  }

  componentDidMount() {
    // The animation timing is controlled in the CSS so the timeout can be 0 here.
    setTimeout(() => this.setState({ isAnimatingOpen: false }), 0);
    this.showNetwork();
  }

  showNetwork = () =>{
    const id = this.state.networkId;
    const name = "歩行者空間ネットワーク(2D表示)";
    const layer = Config.config.layerTypeNamesForRouteSearch;
    try{
        const item = new webMapServiceCatalogItem(id, this.state.terria);
        const wmsUrl = Config.config.geoServerUrl + "/"+Config.config.geoServerWorkSpaceName+"/wms";
        const items = this.state.terria.workbench.items;
        for (const aItem of items) {
            if (aItem.uniqueId == id) {
                this.state.terria.workbench.remove(aItem);
                aItem.loadMapItems();
            }
        }
        item.setTrait(CommonStrata.definition, "url", wmsUrl);
        item.setTrait(CommonStrata.user, "name", name);
        item.setTrait(
            CommonStrata.user,
            "layers",
            layer);
        // console.log(item);
        item.loadMapItems();
        this.state.terria.workbench.add(item);
    }catch(error){
        console.error('処理に失敗しました', error);
    }
  }

  hideNetwork = () => {
    const id = this.state.networkId;
    try{
        const items = this.state.terria.workbench.items;
        for (const aItem of items) {
            if (aItem.uniqueId == id) {
                this.state.terria.workbench.remove(aItem);
                aItem.loadMapItems();
            }
        }
    }catch(error){
        console.error('処理に失敗しました', error);
    }
  }

  /**
   *CZMLテキスト生成後workbenchに探索結果を追加
   * @param {Object} coordinates
   * @param {string} type
   */
  createCZMLTextAndWorkbenchAdd(coordinates, type) {
    const item = new CzmlCatalogItem("経路探索", this.state.terria);
    const searchType = document.getElementById("searchType").value;
    const routeItems = this.state.routeItems;
    let czmlObj = [];
    let positionsArrayArray = [];
    const czmlTop = {
      id: "document",
      name: "line",
      version: "1.0"
    };
    czmlObj.push(czmlTop);


    if (type == "MultiLineString") {
      // マルチラインの場合
      for (let i = 0; i < coordinates.length; i++) {
        let positionsArray = [];
        let coordArray = [];
        for (let j = 0; j < coordinates[i].length; j++) {
          coordArray.push(coordinates[i][j][0]);
          coordArray.push(coordinates[i][j][1]);
          positionsArray.push(Cartographic.fromDegrees(coordinates[i][j][0], coordinates[i][j][1]));
          //2DのJSONが来た場合高さ0でセットする。
          (coordinates[i][j].length == 3) ? coordArray.push(parseFloat(coordinates[i][j][2])) : coordArray.push(0);
        }
        let featureObj = {
          id: i + 1,
          name: "routeSearchResult",
          polyline: {
            "positions": {
              "cartographicDegrees": coordArray,

            },
            "material": {
              "polylineOutline": {
                "color": {
                  "rgba": [255, 165, 0, 255],
                },
                "outlineColor": {
                  "rgba": [0, 0, 0, 255],
                },
                "outlineWidth": 2,
              },
            },
            "width": 5.0,
          }
        }
        czmlObj.push(featureObj);
        positionsArrayArray.push(positionsArray);
      }
    } else {
      let positionsArray = [];
      let coordArray = [];
      for (let i = 0; i < coordinates.length; i++) {
        coordArray.push(coordinates[i][0]);
        coordArray.push(coordinates[i][1]);
        positionsArray.push(Cartographic.fromDegrees(coordinates[i][0], coordinates[i][1]));
        //2DのJSONが来た場合高さ0でセットする。
        (coordinates[i].length == 3) ? coordArray.push(parseFloat(coordinates[i][2])) : coordArray.push(0);
      }
      let featureObj = {
        id: 1,
        name: "routeSearchResult",
        polyline: {
          "positions": {
            "cartographicDegrees": coordArray,

          },
          "material": {
            "polylineOutline": {
              "color": {
                "rgba": [255, 165, 0, 255],
              },
              "outlineColor": {
                "rgba": [0, 0, 0, 255],
              },
              "outlineWidth": 2,
            },
          },
          "width": 5.0,
        }

      };
      czmlObj.push(featureObj);
      positionsArrayArray.push(positionsArray);
    }

    console.log("高さ調整前");
    console.log(JSON.stringify(czmlObj));
    const scene = this.props.terria.cesium.scene;
    const terrainProvider = scene.terrainProvider;
    for (let i = 0; i < positionsArrayArray.length; i++) {
      const positions = positionsArrayArray[i];
      sampleTerrainMostDetailed(terrainProvider, positions).then((updatedPositions) => {
        try{
          if (czmlObj[i + 1]?.polyline?.positions?.cartographicDegrees) {
            for (let j = 2; j < czmlObj[i + 1]?.polyline?.positions?.cartographicDegrees.length; j = j + 3) {
              let newHeight = parseFloat(updatedPositions[((j + 1) / 3) - 1].height);
              czmlObj[i + 1].polyline.positions.cartographicDegrees[j] = newHeight + (parseFloat(czmlObj[i + 1].polyline.positions.cartographicDegrees[j]));
            }
            console.log("高さ調整後");
            console.log(JSON.stringify(czmlObj));
            const items = this.state.terria.workbench.items;
            for (const aItem of items) {
              if (aItem.uniqueId === '経路探索') {
                this.state.terria.workbench.remove(aItem);
                aItem.loadMapItems();
              }
            }
            item.setTrait(CommonStrata.user, "name", "経路探索 " + routeItems[searchType - 1]?.title);
            item.setTrait(CommonStrata.definition, "czmlString", JSON.stringify(czmlObj));
            item.loadMapItems();
            this.state.terria.workbench.add(item);
          }
        }catch(error){
          console.error('処理に失敗しました', error);
        }
      })
    }
  }

  /**
     * 結果行選択時に固有のフォーカスを行う
     * @param {*} maxLon 最大緯度
     * @param {*} maxLat 最大経度
     * @param {*} minLon 最小緯度
     * @param {*} minLat 最小経度
     */
  resultRowFocusMapPlace(maxLon, maxLat, minLon, minLat) {
    let lon = (maxLon + minLon) / 2;
    let lat = (maxLat + minLat) / 2;
    this.outputFocusMapPlaceWithTop(maxLon, maxLat, minLon, minLat, lon, lat);
  }

  /**
   * フォーカス処理（正面表示）
   * @param {number} maxLon 最大経度
   * @param {number} maxLat 最大緯度
   * @param {number} minLon 最小経度
   * @param {number} minLat 最小緯度
   * @param {number} lon 経度
   * @param {number} lat 緯度
   */
  outputFocusMapPlaceWithTop(maxLon, maxLat, minLon, minLat, lon, lat) {
    // console.log("(" + maxLat + "," + maxLon + ")(" + minLat + "," + minLon + ")")
    try{
      //現在のカメラ位置等を取得
      const currentSettings = getShareData(this.state.terria, this.props.viewState);
      const currentCamera = currentSettings.initSources[0].initialCamera;
      let newCamera = Object.assign(currentCamera);
      //新規の表示範囲を設定
      let currentLonDiff = Math.abs(maxLon - minLon);
      let currentLatDiff = Math.abs(maxLat - minLat);
      newCamera.north = maxLat + currentLonDiff / 2;
      newCamera.south = minLat - currentLonDiff / 2;
      newCamera.east = maxLon + currentLatDiff / 2;
      newCamera.west = minLon - currentLatDiff / 2;
      //スマホ画面の時は範囲調整
      const smallFlag = this.props.viewState.useSmallScreenInterface;
      if(smallFlag){
        const diffLat = (newCamera.north - newCamera.south) * 0.2;
        newCamera.south = newCamera.south - diffLat;
        lat = lat - diffLat;
      }
      console.log("(" + lat + "," + lon + ")");
      //camera.positionを緯度経度に合わせて設定
      const scene = this.props.terria.cesium.scene;
      const terrainProvider = scene.terrainProvider;
      const positions = [Cartographic.fromDegrees(lon, lat)];
      let height = 0;
      if(scene && terrainProvider){
        sampleTerrainMostDetailed(terrainProvider, positions).then((updatedPositions) => {
            height = updatedPositions[0].height
            //let coord_wgs84 = Cartographic.fromDegrees(lon, minLat, parseFloat(height) + parseInt((400000 * currentLatDiff )) + 200 );
            let coord_wgs84 = Cartographic.fromDegrees(lon, lat, parseFloat(height + 300) + parseInt((100000 * (currentLatDiff + currentLonDiff))));
            let coord_xyz = Ellipsoid.WGS84.cartographicToCartesian(coord_wgs84);
            //newCamera.position = { x: coord_xyz.x, y: coord_xyz.y, z: coord_xyz.z - parseInt((300000 * currentLatDiff )) - 170 };
            newCamera.position = { x: coord_xyz.x, y: coord_xyz.y, z: coord_xyz.z  };
            //カメラの向きは統一にさせる
            newCamera.direction = { x: 0.6984744646088341, y: -1.6617056496661655, z: 0.2725418417221117 };
            newCamera.up = { x: -0.21791222301017105, y: 0.1660947782238842, z: 0.9617311410729739 };
            runInAction(() => {
              this.props.viewState.terria.currentViewer.zoomTo(newCamera, 5);
            })
        })
      }
    }catch(e){
      console.log(e);
    }
  }

  render() {
    const isExpanded = this.props.viewState.routePanelExpanded;
    const routeStart = this.state.terria.routeStart;
    const routeEnd = this.state.terria.routeEnd;
    const isAnimatingOpen = this.state.isAnimatingOpen;
    const routeItems = this.state.routeItems;
    return (
      <Box
        displayInlineBlock
        backgroundColor={this.props.theme.textLight}
        styledWidth={"100%"}
        styledHeight={"100%"}
        overflow={"auto"}
        onClick={() => this.props.viewState.setTopElement("RoutePanel")}
        css={`
          z-index: ${this.props.viewState.topElement === "SearchPanel"
            ? 99999
            : 110};
            ${this.props.viewState.useSmallScreenInterface && 
              `
              -webkit-overflow-scrolling:auto;
              -webkit-overflow-scrolling:touch;
              `}
        `}
      >
        <Box position="absolute" paddedRatio={3} topRight>
          <RawButton onClick={() => {
            this.hideNetwork();
            this.clear();
            this.props.viewState.hideRoutePanel();
          }}>
            <StyledIcon
              styledWidth={"16px"}
              fillColor={this.props.theme.textDark}
              opacity={"0.5"}
              glyph={Icon.GLYPHS.closeLight}
              css={`
          cursor:pointer;
        `}
            />
          </RawButton>
        </Box>
       <div className={Styles.z_jingluo}>
       <Box
          centered
          paddedHorizontally={5}
          paddedVertically={10}
          displayInlineBlock
          className={Styles.gZBeA}
          css={`
            direction: ltr;
            min-width: 295px;
            padding-bottom: 0px;
          `
        }
        >
          <Text className={Styles.egvBvY} extraBold heading textDark textAlignLeft>
            経路の条件を選択
          </Text>
          <Spacing bottom={4} />
          <Box column>
            <Input
              light={true}
              dark={false}
              type="datextte"
              value={routeStart}
              id="routeStart"
              disabled={true}
              placeholder="出発地点を選択してください"
              className={Styles.CEiLi}
              onChange={e => this.setState({ routeStart: e.target.value })}
            />
          </Box>
          <Spacing bottom={3} />
          <Box column>
            <Input
              light={true}
              dark={false}
              type="datextte"
              value={routeEnd}
              id="routeEnd"
              disabled={true}
              placeholder="到着地点を選択してください"
              className={Styles.CEiLi}
              onChange={e => this.setState({ routeEnd: e.target.value })}
            />
          </Box>
          <Spacing bottom={3} />
          <Box column>
            <Select
              light={true}
              dark={false}
              id="searchType"
              className={Styles.CEiLi}
              style={{ color: "#000" }}>
              {routeItems.map((item, index) => (
                <option key={item.title} value={index + 1}>
                  {item.title}
                </option>
              ))}
            </Select>
            <div className={Styles.OKQLs}></div>
          </Box>
          <Spacing bottom={4} />
          <Button onClick={this.search} style={{ backgroundColor: "#2AAE7A", color: "#ffff" }}>
            検索
          </Button>
          &nbsp;&nbsp;
          <Button onClick={this.clear}>
            クリア
          </Button>
          <Spacing bottom={8} />
          <Box column>
            <div style={{visibility:this.state.showFlag}}>
              <Text bold textDark textAlignLeft subHeading>総延長：{this.state.distance} (m)</Text>
            </div>
          </Box>
          <Spacing bottom={8} />
        </Box>
       </div>
      </Box>
    );
  }

  //クリアボタン処理
  clear = () => {
    const items = this.state.terria.workbench.items;
    for (const aItem of items) {
      if (aItem.uniqueId === '出発地点' || aItem.uniqueId === '到着地点' || aItem.uniqueId === '経路探索') {
        this.state.terria.workbench.remove(aItem);
        aItem.loadMapItems();
      }
    }
    this.setState({ routeStart: "", routeEnd: "", showFlag: 'hidden'});
    this.state.viewState.clearRoutePanel();
  }
  //検索ボタン処理
  search = () => {
    const routeStartArray = document.getElementById("routeStart").value.split(',');
    const routeEndArray = document.getElementById("routeEnd").value.split(',');
    const searchType = document.getElementById("searchType").value;
    const viewparams = "?start=" + routeStartArray[1] + "," + routeStartArray[0] + "&end=" + routeEndArray[1] + "," + routeEndArray[0] + "&condition=" + searchType;
    const apiUrl = Config.config.apiUrl + "/route/search" + viewparams;
    const reg = /^\d+\.{1}\d+/;

    if (!reg.exec(routeStartArray[0]) || !reg.exec(routeStartArray[1])) {
      alert("出発地点の緯度経度を正しく指定してください");
      return false;
    }

    if (!reg.exec(routeEndArray[0]) || !reg.exec(routeEndArray[1])) {
      alert("到着地点の緯度経度を正しく指定してください");
      return false;
    }

    fetch(apiUrl)
      .then(res => res.json())
      .then(res => {
        if (res.result && JSON.parse(res.result) && JSON.parse(res.result).coordinates) {
          console.log(res);
          this.setState({showFlag: 'visible'});
          this.setState({distance: (Math.trunc(res.distance * 10) / 10)});
          this.createCZMLTextAndWorkbenchAdd(JSON.parse(res.result).coordinates, JSON.parse(res.result).type);
          this.resultRowFocusMapPlace(parseFloat(res.maxlon), parseFloat(res.maxlat), parseFloat(res.minlon), parseFloat(res.minlat))
        } else {
          if (res.status) {
            if (res.status == 400) {
              alert("指定した出発・到着地点の近辺に経路が見つかりませんでした。");
            } else if (res.status == 404) {
              alert("条件に合致した経路が見つかりませんでした。");
            } else {
              alert(res.status + "エラー 経路情報の取得に失敗しました");
            }
          } else {
            alert("経路情報の取得に失敗しました");
          }
        }
      }).catch(error => {
        console.error('経路探索処理に失敗しました', error);
        alert('経路探索処理に失敗しました');
      });
  }
}

export default withTranslation()(withTheme(RoutePanel));