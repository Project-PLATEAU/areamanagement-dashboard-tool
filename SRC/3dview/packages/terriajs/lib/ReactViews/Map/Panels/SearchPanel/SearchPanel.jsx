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
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import { action, runInAction } from "mobx";
import {getShareData} from "../SharePanel/BuildShareLink";
import Styles from "./SearchPanel.scss";

/**
 * 避難経路検索画面
 */
@observer
class SearchPanel extends React.Component {
  static displayName = "SearchPanel";

  static propTypes = {
    terria: PropTypes.object.isRequired,
    viewState: PropTypes.object.isRequired,
    theme: PropTypes.object,
    t: PropTypes.func.isRequired
  };

  constructor(props) {
    super(props);

    const apiUrl = Config.config.apiUrl + "/district/getChochomoku";
    fetch(apiUrl)
    .then((res) => res.json())
    .then(res => {
      // console.log(res);
      if(!res.status){
        let townList = res.townList;
        townList.unshift("すべての町名");
        const chochomoku = res.chochomoku;
        this.setState({townItems: townList});
        this.setState({chochomoku: chochomoku});
        // console.log(this.state);
      }
    })

    this.state = {
      viewState: props.viewState,
      terria: props.terria,
      isAnimatingOpen: true,
      townItems:[
        "すべての町名"
      ],
      blockItems: [
        "すべての丁目名"
      ],
      showFlag:'hidden',
      highLightId: "//データセット/地域・エリマネ活動可視化/町丁目一覧/",
    };
    // console.log(this.state);
  }

  componentDidMount() {
    // The animation timing is controlled in the CSS so the timeout can be 0 here.
    setTimeout(() => this.setState({ isAnimatingOpen: false }), 0);
  }

  
  /**
   * 202305 検証用 地図上の指定した緯度経度にフォーカスを行い属性情報を表示
   * @param {number} lon 
   * @param {number} lat 
   * @param {number} height 
   * @param {string} layerId
   * @param {string} featureId
   * @param {string} featureKeyName
   */
  focusMapPlaceAndAttributeDisplay(lon, lat, height, featureId, featureKeyName) {
    // console.log(lon + " " + lat + " " + featureId + " " + featureKeyName);
    try{
      lon = parseFloat(lon);
      lat = parseFloat(lat);
      //現在のカメラ位置等を取得
      const currentSettings = getShareData(this.props.viewState.terria, this.props.viewState);
      const currentCamera = currentSettings.initSources[0].initialCamera;
      let newCamera = Object.assign(currentCamera);
      //新規の表示範囲を設定
      let currentLonDiff = Math.abs(currentCamera.east - currentCamera.west);
      let currentLatDiff = Math.abs(currentCamera.north - currentCamera.south);
      newCamera.north = lat + currentLatDiff / 2;
      newCamera.south = lat - currentLatDiff / 2;
      newCamera.east = lon + currentLonDiff / 2;
      newCamera.west = lon - currentLonDiff / 2;
      //camera.positionを緯度経度に合わせて設定
      const scene = this.props.viewState.terria.cesium?.scene;
      const terrainProvider = scene?.terrainProvider;
      const positions = [Cartographic.fromDegrees(lon, lat)];
      let currentHeight = 0;
      if(scene && terrainProvider){
        sampleTerrainMostDetailed(terrainProvider, positions).then((updatedPositions) => {
          currentHeight = parseFloat(updatedPositions[0].height);
          //初期高さ調整値
          let diff = 500;
          if(height != undefined && height != null){
            let newDiff = height;
            if(newDiff > diff){
              diff = newDiff;
            }
          }
          let coord_wgs84 = Cartographic.fromDegrees(lon, lat, currentHeight + diff);
          let coord_xyz = Ellipsoid.WGS84.cartographicToCartesian(coord_wgs84);
          newCamera.position = { x: coord_xyz.x, y: coord_xyz.y, z: coord_xyz.z };
          newCamera.direction = { x: 0.6984744646088341, y: -1.6617056496661655, z: 0.2725418417221117 };
          newCamera.up = { x: -0.21791222301017105, y: 0.1660947782238842, z: 0.9617311410729739 };
          //フォーカス移動後レイヤー表示及び属性表示を行う
          runInAction(() => {
            this.props.viewState.terria.currentViewer.zoomTo(newCamera, 3);
          })
        })
      }
    }catch(e){
      console.log(e);
    }
  }

  render() {
    const townItems = this.state.townItems;
    const blockItems = this.state.blockItems;
    return (
      <Box
        displayInlineBlock
        backgroundColor={this.props.theme.textLight}
        fullWidth
        fullHeight
        overflow={"auto"}
        onClick={() => this.props.viewState.setTopElement("SearchPanel")}
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
            this.props.viewState.hideSearchPanel();
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
        <div className={Styles.z_jiansuo}>
        <Box
          centered
          paddedHorizontally={5}
          paddedVertically={10}
          displayInlineBlock
          className={Styles.gZBeA}
          css={`
            direction: ltr;
            width: 100%;
            min-width: 295px;
            height:100%;
          `}
        >
          <Text className={Styles.egvBvY} extraBold heading textDark textAlignLeft>
            町丁目名を検索
          </Text>
          <Spacing bottom={4} />
          <Box className={Styles.z_OKQLs} column>
            <Select
              light={true}
              dark={false}
              id="townName"
              className={Styles.CEiLi}
              style={{ color: "#000" }}
              onChange={e=> this.change(e.target.value)}>
              <option value="" disabled selected hidden>町名をリストから選択してください</option>
              {townItems.map((item, index) => (
                <option value={item}>
                  {item}
                </option>
              ))}
            </Select>
            <div className={Styles.OKQLs}></div>
          </Box>
          <Spacing bottom={3} />
          <Box className={Styles.z_OKQLs} column>
            <Select
              light={true}
              dark={false}
              id="blockName"
              className={Styles.CEiLi}
              style={{ color: "#000" }}>
                <option value="" disabled selected hidden>丁目名をリストから選択してください</option>
              {blockItems.map((item, index) => (
                <option value={item}>
                  {item}
                </option>
              ))}
            </Select>
            <div className={Styles.OKQLs}></div>
          </Box>
          <Spacing bottom={4} />
          <Button className={Styles.eRJlwo} onClick={this.search} style={{ backgroundColor: "#2AAE7A", color: "#ffff", align: "right" }}>
            検索
          </Button>
          &nbsp;&nbsp;
          <Button onClick={this.clear}>
            クリア
          </Button>
          <Spacing bottom={5} />
          <Text className={Styles.egvBvY} extraBold heading textDark textAlignLeft>
            検索結果
          </Text>
          <Box
            centered
            displayInlineBlock
            css={`
              direction: ltr;
              padding-bottom: 0px;
              width: 100%;
              height:60%;
            `}
          >
            <div className="ag-theme-alpine z_jiansuo1" style={{height: "100%", width: "100%", visibility:this.state.showFlag}}>
              <AgGridReact
                onGridReady={(grid) => {
                  //this.props.viewState.setAgGridList(key,grid);
                }}
                onRowClicked={(e) => {
                //   // 0: ダッシュボード->3D都市モデルビューワ（フォーカス)
                  if(e.data){
                    this.highlightSelect(e.data.sname, e.data.sarea);
                    this.focusMapPlaceAndAttributeDisplay(e.data.lon, e.data.lat, 2500, null, null)
                  }
                }}
                // rowSelection={leftGraphList[key].custom.selectType}
                rowData={this.state.rowData}
                columnDefs={this.state.columnDefs}
                gridOptions={{suppressDragLeaveHidesColumns: true}}>
              </AgGridReact>
            </div>
          </Box>
          <Spacing bottom={3} />
        </Box>
        </div>
      </Box>
    );
  }

  //検索ボタン処理
  search = () => {
    let townName = document.getElementById("townName").value;
    let blockName = document.getElementById("blockName").value;
    if(blockName == "" || townName == ""){
      alert("検索条件を選択してください")
      return;
    }
    if(townName == "すべての町名") townName = "町名"
    if(blockName == "すべての丁目名") blockName = "丁目名"
    console.log(townName + "  " + blockName)
    
    const param = "?townName=" + townName + "&blockName=" + blockName;
    const apiUrl = Config.config.apiUrl + "/district/search" + param;
    fetch(apiUrl)
    .then((res) => res.json())
    .then(res => {
      console.log(res);
      const columnDefs = [
        {field: 'cityName', headerName: '自治体名', filter: true, sortable: true, lockVisible: true},
        {field: 'sname', headerName: '町丁目名', filter: true, sortable: true, lockVisible: true}
      ]
      this.setState({rowData: res});
      this.setState({columnDefs: columnDefs});
      this.setState({showFlag: 'visible'});
    })
  }

  highlightSelect = (sname, sarea) =>{
    // console.log(sname + "  " + sarea);
    let id = this.state.highLightId;
    let layer = Config.config.layerTypeNamesForMachiChomeSearch;
    try{
        const item = new webMapServiceCatalogItem(id + sname, this.state.terria);
        const wmsUrl = Config.config.geoServerUrl + "/"+Config.config.geoServerWorkSpaceName+"/wms";
        const items = this.state.terria.workbench.items;
        for (const aItem of items) {
            if (aItem.uniqueId.indexOf(id) !== -1) {
                this.state.terria.workbench.remove(aItem);
                aItem.loadMapItems();
            }
        }
        item.setTrait(CommonStrata.definition, "url", wmsUrl);
        item.setTrait(CommonStrata.user, "name", sname);
        item.setTrait(
            CommonStrata.user,
            "layers",
            layer);
        item.setTrait(CommonStrata.user,
            "parameters",
            {
                "viewparams": "s_area:" + sarea
            });
        // console.log(item);
        item.loadMapItems();
        this.state.terria.workbench.add(item);
    }catch(error){
        console.error('処理に失敗しました', error);
    }
  }

  change = () => {
    console.log("change");
    const townName = document.getElementById("townName").value;
    const chochomokuList = this.state.chochomoku;
    let blockList = ["すべての丁目名"];
    // console.log(chochomokuList);
    chochomokuList.filter(function(value){
      // console.log(value);
      if(value[1] == townName && value[2] != null && value[2] != "" && value[2].trim().length > 0){
        blockList.push(value[2]);
      }
    })
    // console.log(blockList);
    this.setState({blockItems: blockList});
  }

  clear = () => {
    console.log("clear highlight");
    const id = this.state.highLightId;
    try{
      const items = this.state.terria.workbench.items;
      for (const aItem of items) {
          if (aItem.uniqueId.indexOf(id) !== -1) {
              this.state.terria.workbench.remove(aItem);
              aItem.loadMapItems();
          }
      }
      this.setState({showFlag: 'hidden'})
    }catch(error){
        console.error('処理に失敗しました', error);
    }
  }
}

export default withTranslation()(withTheme(SearchPanel));
