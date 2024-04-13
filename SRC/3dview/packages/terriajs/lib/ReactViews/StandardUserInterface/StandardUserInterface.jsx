import React from "react";
import createReactClass from "create-react-class";
import { ThemeProvider, createGlobalStyle } from "styled-components";
import PropTypes from "prop-types";
import combine from "terriajs-cesium/Source/Core/combine";
import { terriaTheme } from "./StandardTheme";
import arrayContains from "../../Core/arrayContains";
import DesktopHeader from "../Desktop/DesktopHeader";
import Branding from "../SidePanel/Branding";
import DragDropFile from "../DragDropFile";
import DragDropNotification from "./../DragDropNotification";
import ExplorerWindow from "../ExplorerWindow/ExplorerWindow";
import FeedbackForm from "../Feedback/FeedbackForm";
import MapColumn from "./MapColumn";
import MapInteractionWindow from "../Notification/MapInteractionWindow";
import TrainerBar from "../Map/TrainerBar/TrainerBar";
import ExperimentalFeatures from "../Map/ExperimentalFeatures";
import MobileHeader from "../Mobile/MobileHeader";
import Notification from "../Notification/Notification";
import ProgressBar from "../Map/ProgressBar";
import SidePanel from "../SidePanel/SidePanel";
import processCustomElements from "./processCustomElements";
import FullScreenButton from "./../SidePanel/FullScreenButton.jsx";
import StoryPanel from "./../Story/StoryPanel.jsx";
import StoryBuilder from "./../Story/StoryBuilder.jsx";
import { useLocation } from "react-router-dom"
import PrintView from "../../ReactViews/Map/Panels/SharePanel/Print/PrintView";
import withFallback from "../HOCs/withFallback";
import TourPortal from "../Tour/TourPortal";
import SatelliteHelpPrompt from "../HelpScreens/SatelliteHelpPrompt";
import WelcomeMessage from "../WelcomeMessage/WelcomeMessage";
import { Small, Medium ,ExtraSmall } from "../Generic/Responsive";
import classNames from "classnames";
import "inobounce";
import { withTranslation } from "react-i18next";
import chartjsPluginDatalabels from 'chartjs-plugin-datalabels'
import Styles from "./standard-user-interface.scss";
import Icon, { StyledIcon } from "../../Styled/Icon";
import Box from "../../Styled/Box";
import Select from "../../Styled/Select";
// import Variables from "../../Sass/common/variables";
import { observer } from "mobx-react";
import { action, runInAction } from "mobx";
import FeatureInfoPanel from "../FeatureInfo/FeatureInfoPanel";
import LoginPanel from "../Map/Panels/LoginPanel/LoginPanel";
import HelpPanel from "../Map/Panels/HelpPanel/HelpPanel";
import RoutePanel from "../Map/Panels/RoutePanel/RoutePanel";
import SearchPanel from "../Map/Panels/SearchPanel/SearchPanel";
import ActivityPanel from "../Map/Panels/ActivityPanel/ActivityPanel";
import InformationPanel from "../Map/Panels/InformationPanel/InformationPanel";
import PostLayerPanel from "../Map/Panels/PostLayerPanel/PostLayerPanel";
import Tool from "../Tools/Tool";
import Disclaimer from "../Disclaimer";
import CollapsedNavigation from "../Map/Navigation/Items/OverflowNavigationItem";
import {
  getShareData
} from "../Map/Panels/SharePanel/BuildShareLink";
import Cartographic from "terriajs-cesium/Source/Core/Cartographic";
import sampleTerrainMostDetailed from "terriajs-cesium/Source/Core/sampleTerrainMostDetailed";
import Ellipsoid from "terriajs-cesium/Source/Core/Ellipsoid";
import webMapServiceCatalogItem from '../../Models/Catalog/Ows/WebMapServiceCatalogItem';
import CommonStrata from "../../Models/Definition/CommonStrata";
import Config from "../../../customconfig.json";
export const showStoryPrompt = (viewState, terria) => {
  terria.configParameters.showFeaturePrompts &&
    terria.configParameters.storyEnabled &&
    terria.stories.length === 0 &&
    viewState.toggleFeaturePrompt("story", true);
};
import Cartesian3 from "terriajs-cesium/Source/Core/Cartesian3";
import SceneTransforms from "terriajs-cesium/Source/Scene/SceneTransforms";
import CesiumMath from "terriajs-cesium/Source/Core/Math";
import { BaseModel } from "../../Models/Definition/Model";
import { Responsive, WidthProvider } from "react-grid-layout";
import { BrowserRouter, Route, Switch, NavLink, Routes, Link } from 'react-router-dom';
import {
  Chart as ChartJS,
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend,
} from "chart.js";
import { Line, Bar, Pie, Doughnut } from "react-chartjs-2";
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import {highlightGraphAndFilterList,setThemeData} from '../../Models/GraphList/GraphList';
import BottomDock from "../BottomDock/BottomDock";
import FeatureDetection from "terriajs-cesium/Source/Core/FeatureDetection";
import Viewer from "./Viewer";
import FullWidthCellRenderer from '../../Models/GraphList/fullWidthCellRenderer';
import MenuBar from "../Map/MenuBar";
import {AG_GRID_LOCALE_JA} from "../../../locale.js";

const isIE = FeatureDetection.isInternetExplorer();
ChartJS.register(
  CategoryScale,
  LinearScale,
  PointElement,
  LineElement,
  BarElement,
  ArcElement,
  Title,
  Tooltip,
  Legend
);
const ResponsiveGridLayout = WidthProvider(Responsive);

const GlobalTerriaStyles = createGlobalStyle`
  // Theme-ify sass classes until they are removed

  // We override the primary, secondary, map and share buttons here as they
  // are imported everywhere and used in various ways - until we remove sass
  // this is the quickest way to tackle them for now
  .tjs-_buttons__btn--map {
    ${p => p.theme.addTerriaMapBtnStyles(p)}
  }

  .tjs-_buttons__btn-primary {
    ${p => p.theme.addTerriaPrimaryBtnStyles(p)}
  }

  .tjs-_buttons__btn--secondary,
  .tjs-_buttons__btn--close-modal {
    ${p => p.theme.addTerriaSecondaryBtnStyles(p)}
  }

  .tjs-_buttons__btn--tertiary {
    ${p => p.theme.addTerriaTertiaryBtnStyles(p)}
  }

  .tjs-_buttons__btn-small:hover,
  .tjs-_buttons__btn-small:focus {
    color: ${p => p.theme.colorPrimary};
  }

  .tjs-share-panel__catalog-share-inner {
    background: ${p => p.theme.greyLightest};
  }

  .tjs-share-panel__btn--catalogShare {
    color: ${p => p.theme.colorPrimary};
    background:transparent;
    svg {
      fill: ${p => p.theme.colorPrimary};
    }
  }
  .tjs-dropdown__btn--dropdown {
    color: ${p => p.theme.textDark};
    background: ${p => p.theme.textLight};
    &:hover,
    &:focus {
      color: ${p => p.theme.textDark};
      background: ${p => p.theme.textLight};
      border: 1px solid ${p => p.theme.colorPrimary};
    }
    svg {
      fill: ${p => p.theme.textDark};
    }
  }
  .tjs-dropdown__btn--option.tjs-dropdown__is-selected {
    color: ${p => p.theme.colorPrimary};
  }

  .chartjs-tooltip-key {
    display:"none"
    }
  ${props =>
    props.experimentalFeatures &&
    `
    body {
      *:focus {
        outline: 3px solid #C390F9;
      }
    }
  `}
`;
export const animationDuration = 250;

/** blah */
const StandardUserInterface = observer(

  createReactClass({
    displayName: "StandardUserInterface",
    propTypes: {
      /**
       * Terria instance
       */
      terria: PropTypes.object.isRequired,
      /**
       * All the base maps.
       */
      allBaseMaps: PropTypes.array,
      themeOverrides: PropTypes.object,
      path: PropTypes.string,
      all: PropTypes.string,
      viewState: PropTypes.object.isRequired,
      minimumLargeScreenWidth: PropTypes.number,
      version: PropTypes.string,
      children: PropTypes.oneOfType([
        PropTypes.arrayOf(PropTypes.element),
        PropTypes.element
      ]),
      t: PropTypes.func.isRequired,
      dashboardManagementScreenFlag:PropTypes.bool
      // userInfo: PropTypes.object
    },

    getDefaultProps() {
      return { minimumLargeScreenWidth: 768 };
    },

    /* eslint-disable-next-line camelcase */
    UNSAFE_componentWillMount() {
      const { t } = this.props;
      const that = this;

      // only need to know on initial load
      this.dragOverListener = e => {
        if (
          !e.dataTransfer.types ||
          !arrayContains(e.dataTransfer.types, "Files")
        ) {
          return;
        }
        e.preventDefault();
        e.stopPropagation();
        e.dataTransfer.dropEffect = "copy";
        that.acceptDragDropFile();
      };

      this.resizeListener = () => {
        runInAction(() => {
          this.props.viewState.useSmallScreenInterface = this.shouldUseMobileInterface();
          this.props.terria.useSmallScreenInterface = this.shouldUseMobileInterface();
        });
      };

      window.addEventListener("resize", this.resizeListener, false);

      this.resizeListener();

      if (
        this.props.terria.configParameters.storyEnabled &&
        this.props.terria.stories &&
        this.props.terria.stories.length &&
        !this.props.viewState.storyShown
      ) {
        this.props.viewState.terria.notificationState.addNotificationToQueue({
          title: t("sui.notifications.title"),
          message: t("sui.notifications.message"),
          confirmText: t("sui.notifications.confirmText"),
          denyText: t("sui.notifications.denyText"),
          confirmAction: action(() => {
            this.props.viewState.storyShown = true;
          }),
          denyAction: action(() => {
            this.props.viewState.storyShown = false;
          }),
          type: "story",
          width: 300
        });
      }
    },

    componentDidMount() {
      this.props.viewState.setIsMapFullScreen(!Config.config.defaultShowWorkbench);
      //this._map_wrapper.addEventListener("dragover", this.dragOverListener, false);
      showStoryPrompt(this.props.viewState, this.props.terria);
      //認証Apiに問い合わせてログイン済みか確認
      const apiUrl = Config.config.apiUrl + "/user/checkAuth";
      fetch(apiUrl)
      .then((res) => res.json())
      .then(res => {
        if (!res.status) {
            const username = res.userName;
            const role = res.role;
            const userId = res.userId;
            this.props.terria.setName(username);
            this.props.terria.setPermission(role);
            this.props.terria.setUserId(userId);
        }
      })
    },

    componentWillUnmount() {
      window.removeEventListener("resize", this.resizeListener, false);
      document.removeEventListener("dragover", this.dragOverListener, false);
    },

    acceptDragDropFile() {
      runInAction(() => {
        this.props.viewState.isDraggingDroppingFile = true;
      });
      // if explorer window is already open, we open my data tab
      if (this.props.viewState.explorerPanelIsVisible) {
        this.props.viewState.openUserData();
      }
    },

    shouldUseMobileInterface() {
      return document.body.clientWidth < this.props.minimumLargeScreenWidth;
    },

    addBottomDock(bottomDock) {
      if (isIE) {
        this.observer.observe(bottomDock, {
          childList: true,
          subtree: true
        });
      }
    },

    render() {
      const { t } = this.props;
      // Merge theme in order of highest priority: themeOverrides props -> theme config parameter -> default terriaTheme
      const mergedTheme = combine(
        this.props.themeOverrides,
        combine(this.props.terria.configParameters.theme, terriaTheme, true),
        true
      );
      const theme = mergedTheme;
      const customElements = processCustomElements(
        this.props.viewState.useSmallScreenInterface,
        this.props.children
      );

      const terria = this.props.terria;
      const allBaseMaps = this.props.allBaseMaps;

      const showStoryBuilder =
        this.props.viewState.storyBuilderShown &&
        !this.shouldUseMobileInterface();
      const showStoryPanel =
        this.props.terria.configParameters.storyEnabled &&
        this.props.terria.stories.length > 0 &&
        this.props.viewState.storyShown &&
        !this.props.viewState.explorerPanelIsVisible &&
        !this.props.viewState.storyBuilderShown;

      // ----202307 ここから----
      const { innerWidth: width, innerHeight: height } = window;
      const themeData = this.props.viewState.themeData;
      let switchItemList = null;
      let viewrGridCols = 8;
      let rowHeight = 22;
      let viewerGridClass = Styles.mainRightGrid;
      let displayLayout = [...this.props.viewState.displayLayout];
      let graphList = {...this.props.viewState.graphList};
      let leftGraphList = {};
      let rightGraphList = {};
      let layout = [];
      let leftLayout = [];
      let rightLayout = [];
      if(innerHeight && innerHeight < 600){
        innerHeight = 600;
      }
      if(innerHeight){
        rowHeight = innerHeight * 0.7 / 22;
      }
      if(themeData && themeData["switchItemList"]){
        switchItemList = [...themeData["switchItemList"]];
      }
      if(displayLayout){
        layout = displayLayout.map(l => ({ i: l.i, x: l.x, y: l.y, w: l.w, h: l.h}));
      }
      //x座標が3dviewer未満であればleft,x座標が3dviewer以上であればright
      //grid layoutの仕様上の考慮（グルーピングでのスクロール制御を行う為）
      if(this.props.viewState.fullScreenMode == 0){
        Object.keys(graphList).map(key=>{
          const viewerIndex = layout.findIndex(element=>element.i == -1);
          const index = layout.findIndex(element=>element.i == key);
          if(layout[index]){
            if(layout[index].x < 4 ){
              leftGraphList[key] = graphList[key].graphListData;
              leftLayout.push(layout[index]);
            }else{
              rightGraphList[key] = graphList[key].graphListData;
              //left rightのx座標判別値を表示用の値に変換
              let newX = layout[index].x - layout[viewerIndex].x;
              if(newX > -1){
                layout[index].x = newX;
              }
              rightLayout.push(layout[index]);
            }
          }
        })
        const viewerIndex = layout.findIndex(element=>element.i == -1);
        if(viewerIndex > -1){
          layout[viewerIndex].x = 0;
          rightLayout.push(layout[viewerIndex]);
        }
      }
      if(this.props.viewState.fullScreenMode == 1 || this.props.viewState.useSmallScreenInterface){
        rightLayout = [{ i: "-1", x: 0, y: 0, w: 12, h: 22 }];
        viewrGridCols = 12;
        viewerGridClass = Styles.fullViewerGrid;
        if(this.props.viewState.useSmallScreenInterface){
          rightGraphList = {};
        }
      }
      // ----202307 ここまで----
      return (
        <ThemeProvider theme={mergedTheme}>
          <GlobalTerriaStyles
            experimentalFeatures={
              this.props.terria.configParameters.experimentalFeatures
            }
          />
          <TourPortal terria={terria} viewState={this.props.viewState} />
          <CollapsedNavigation
            terria={terria}
            viewState={this.props.viewState}
          />
          <SatelliteHelpPrompt
            terria={terria}
            viewState={this.props.viewState}
          />
          <div className={Styles.storyWrapper}>
            <If condition={!this.props.viewState.disclaimerVisible}>
              <WelcomeMessage viewState={this.props.viewState} />
            </If>
            {this.props.viewState.showLoginMenu && (
                <LoginPanel 
                  terria={terria} 
                  viewState={this.props.viewState}
                />
            )}
            <div
              className={classNames(Styles.uiRoot, {
                [Styles.withStoryBuilder]: showStoryBuilder
              })}
              css={`
                ${this.props.viewState.disclaimerVisible &&
                `filter: blur(10px);`}
                ${this.props.dashboardManagementScreenFlag &&
                `height: 100vh!important;
                width: 100vw;
                zoom: 74%;`}
              `}
              ref={w => (this._wrapper = w)}
            >
              <div className={Styles.ui}>
              <img src={Config.config.plateauPath + "/sample/img/bg.png"} className="bg"/>
                {!this.props.dashboardManagementScreenFlag && (
                <Medium>
                  <DesktopHeader
                    terria={this.props.terria}
                    version={this.props.version}
                    viewState={this.props.viewState}
                    allBaseMaps={this.props.allBaseMaps}
                    customElements={customElements}
                    animationDuration={animationDuration}
                  />
                </Medium>
                )}
                <div className={Styles.uiInner}>
                  {!this.props.dashboardManagementScreenFlag && (
                  <If condition={!this.props.viewState.hideMapUi}>
                    <Small>
                      <MobileHeader
                        terria={terria}
                        menuItems={customElements.menu}
                        menuLeftItems={customElements.menuLeft}
                        viewState={this.props.viewState}
                        version={this.props.version}
                        allBaseMaps={allBaseMaps}
                      />
                    </Small>
                  </If>
                  )}
                  {
                    // ----202307 ここから styleは仮で当て込み----
                  }
                    <section id="z_index" className={Styles.map}>
                    {/** 分割画面モード時 */}
                    {graphList != null && 
                    <>
                      {/** viewer側 grid-layout */}
                      <div className={viewerGridClass} css={`
                          ${this.props.dashboardManagementScreenFlag &&
                          `overflow-y:scroll;`}
                        `}>
                        <ResponsiveGridLayout
                          className="layout"
                          layouts={{ lg: rightLayout, md: rightLayout, sm: rightLayout, xs: rightLayout }}
                          breakpoints={{ lg: 1200, md: 996, sm: 950, xs: 480, xxs: 0 }}
                          cols={{ lg: viewrGridCols, md: viewrGridCols, sm: viewrGridCols, xs: viewrGridCols, xxs: viewrGridCols }}
                          rowHeight={rowHeight}
                          isDraggable={true}
                          isResizable={true}
                          draggableHandle={".drag-handle"}
                          onResize={(layout, oldItem, newItem, placeholder, e, element)=> {
                            if(this.props.dashboardManagementScreenFlag && oldItem.w < placeholder.w){
                              let newW = placeholder.w*1.2;
                              if(newW > 8){
                                newW = 8;
                              }
                              placeholder.w = newW;
                              newItem.w = newW;
                            }
                          }}
                          onLayoutChange={(layout, layouts) =>{
                            //grid layoutの仕様上の考慮　レイアウト定義更新時は3dviewer側のレイヤ定義登録時xは"4"を加算する
                            try{
                              const updateLayout = layout.map(l => ({ i: l.i, x: l.x, y: l.y, w: l.w, h: l.h}));
                              for(let i=0;i<updateLayout.length;i++){
                                updateLayout[i].x = Number(updateLayout[i].x) + 4;
                              }
                              runInAction(() => {
                                this.props.viewState.setUpdateLayout(updateLayout);
                              })
                            }catch(e){}
                          }}
                        >
                          <div key="-1" id="viewerGridLayout" css={`
                            ${this.props.viewState.fullScreenMode == 1 &&
                              `min-width:100%;`
                            }
                            `}>
                            {!this.props.dashboardManagementScreenFlag && (
                              <Viewer terria={this.props.terria} viewState={this.props.viewState} animationDuration={animationDuration} customElements={customElements} allBaseMaps={allBaseMaps}/>
                            )}
                            {this.props.dashboardManagementScreenFlag && (
                              <div className="drag-handle" style={{width:"100%",height:"100%",background:"gray"}}><h2 style={{position:"absolute",top:"50%",left:"50%",transform:"translateY(-50%) translateX(-50%)",color:"#fff"}}>3DVIEWER AREA</h2></div>
                            )}
                          </div>
                          {/** viewer側 下部にグラフ・リスト表示 */}
                          { Object.keys(rightGraphList).map(key => (
                              <div key={key + ""} className={rightGraphList[key].typeId != 5 || this.props.dashboardManagementScreenFlag ? "drag-handle" : ""} >
                                <div style={{border:rightGraphList[key].typeId == 6?"1px solid":"none",width:"100%",height:"100%",overflowX:rightGraphList[key].typeId == 6?"auto":"hidden",overflowY:"hidden"}}>
                                  {rightGraphList[key] && rightGraphList[key].typeId == 1 &&  (
                                    <Bar options={rightGraphList[key].options} data={rightGraphList[key].data} plugins={[rightGraphList[key].custom.customPlugin]} />
                                  )}
                                  {rightGraphList[key] && rightGraphList[key].typeId == 2 &&  (
                                    <Doughnut options={rightGraphList[key].options} data={rightGraphList[key].data} plugins={[rightGraphList[key].custom.centerText]} />
                                  )}
                                  {rightGraphList[key] && rightGraphList[key].typeId == 3 &&  (
                                    <Bar options={rightGraphList[key].options} data={rightGraphList[key].data} plugins={[rightGraphList[key].custom.customPlugin]} />
                                  )}
                                  {rightGraphList[key] && rightGraphList[key].typeId == 4 &&  (
                                    <Bar options={rightGraphList[key].options} data={rightGraphList[key].data} plugins={[rightGraphList[key].custom.customPlugin]} />
                                  )}
                                  {rightGraphList[key] && rightGraphList[key].typeId == 5 &&  (
                                    <>
                                        <div className="z_title">
                                      <img src={Config.config.plateauPath + "/sample/img/icon06.png"} className="icon"/>
                                            <h3 style={{margin:0,padding:"5px"}} className={!this.props.dashboardManagementScreenFlag?"drag-handle":""}>{rightGraphList[key].graphName}</h3>
                                      </div>
                                      <div className="z_biaodan ag-theme-alpine" style={{height: "85%", width: "100%"}}>
                                        <AgGridReact
                                            localeText={AG_GRID_LOCALE_JA}
                                            onRowDataUpdated={(grid) => {
                                              try{
                                                rightGraphList[key]?.initFillter(grid);
                                              }catch(e){}
                                            }}
                                            onRowClicked={(e) =>{
                                              rightGraphList[key].onRowClicked(e);
                                            }}
                                            rowSelection={rightGraphList[key].custom.selectType}
                                            rowData={rightGraphList[key].rowData}
                                            columnDefs={rightGraphList[key].columnDefs}
                                            defaultColDef={{
                                              resizable: true,
                                            }}
                                            isFullWidthRow = {(params) =>{
                                              if(rightGraphList[key].isFullWidthRow){
                                                return rightGraphList[key].isFullWidthRow(params);
                                              }
                                            }}
                                            fullWidthCellRenderer={FullWidthCellRenderer}
                                            doesExternalFilterPass={(node) =>{
                                              if(rightGraphList[key].doesExternalFilterPass){
                                                return rightGraphList[key].doesExternalFilterPass(node);
                                              }
                                            }}
                                            isExternalFilterPresent={(node) =>{
                                              if(rightGraphList[key].isExternalFilterPresent){
                                                return rightGraphList[key].isExternalFilterPresent();
                                              }
                                            }}
                                            suppressRowTransform={rightGraphList[key].suppressRowTransform}
                                            suppressScrollOnNewData={true}
                                            >
                                            
                                          </AgGridReact>
                                        </div>
                                      </>
                                    )}
                                    {rightGraphList[key] && rightGraphList[key].typeId == 6 &&  (
                                      <div style={{height:"98%",width:"99%"}}  onClick={(e) => rightGraphList[key].onRowClicked(themeData.switchPlaceholderName)} onTouchEnd={(e) => rightGraphList[key].onRowClicked(themeData.switchPlaceholderName)} 
                                      title={rightGraphList[key].layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm) => layerGraphCooporationForm.cooperationType==2)>-1?"クリックで地図を色塗りまたは強調表示します" : 
                                      rightGraphList[key].layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm) => layerGraphCooporationForm.cooperationType==3)>-1?"クリックで該当するカテゴリでリストを絞り込みます":""}>
                                          <div>
                                            <h3 style={{margin:0,padding:"5px",color:"#999",fontWeight:"normal",fontSize:".9em"}}>{rightGraphList[key].graphName}</h3>
                                            <p style={{whiteSpace:"pre",margin:0,padding:"5px",fontWeight:"bold",fontSize:"1.3em"}}>{rightGraphList[key].displayValue}</p>
                                          </div>
                                      </div>
                                    )}
                                </div>
                              </div>
                            ))}
                        </ResponsiveGridLayout>
                      </div>
                      {/** ダッシュボード側 grid-layout */}
                      <Medium>
                      {this.props.viewState.fullScreenMode == 0 && (
                      <div className={Styles.mainLeftGrid} css={`
                        ${this.props.dashboardManagementScreenFlag &&
                        `overflow-y:scroll;`}
                      `}>
                        {!this.props.dashboardManagementScreenFlag && this.props.terria.showActivityMenu && this.props.terria.permission != "" && (
                            <div style={{position:"fixed",zIndex:"9999",top:"7%",left:"0",height:"95%",width:"30%",display:"inline-block"}}>
                              <ActivityPanel terria={terria} viewState={this.props.viewState} />
                            </div>
                        )}
                        {!this.props.dashboardManagementScreenFlag && this.props.terria.showPostLayerMenu && this.props.terria.permission != "" && (
                          <div style={{position:"fixed",zIndex:"9999",top:"7%",left:"0",height:"95%",width:"30%",display:"inline-block"}}>
                            <PostLayerPanel terria={terria} viewState={this.props.viewState} />
                          </div>
                        )}
                        {!this.props.dashboardManagementScreenFlag && this.props.viewState.showSearchMenu && (
                          <div style={{position:"fixed",zIndex:"9999",top:"7%",left:"0",height:"95%",width:"30%",display:"inline-block"}}>
                            <SearchPanel terria={terria} viewState={this.props.viewState} />
                          </div>
                        )}
                        {!this.props.dashboardManagementScreenFlag && this.props.viewState.showRouteMenu && (
                          <div style={{position:"fixed",zIndex:"9999",top:"7%",left:"0",height:"95%",width:"30%",display:"inline-block"}}>
                          <RoutePanel terria={terria} viewState={this.props.viewState} />
                          </div>
                        )}
                        <div style={{display:"flex",alignItems:"center"}}>
                          <div style={{width:"50%"}}>
                            <h2 style={{marginLeft:"10px",fontSize:"1.1em"}}>{themeData.themeName}</h2>
                          </div>
                          <div style={{width:"40%"}}>
                          {
                            switchItemList && themeData.switchItemValueColumnName && themeData.switchItemNameColumnName && (
                              <Select
                                value={this.props.viewState.selectedWithThemeSwitchItemValue}
                                light={true}
                                dark={false}
                                id="withThemeSwitchItem"
                                onChange={evt => {
                                  evt.stopPropagation();
                                  const obj = {};
                                  obj[themeData.switchPlaceholderName] = evt.target.value;
                                  runInAction(() => {
                                    this.props.viewState.setSelectedWithThemeSwitchItemValue(evt.target.value);
                                    setThemeData(this.props.viewState,obj);
                                  })
                                }}
                                style={{ color: "#000",border:"1px solid",minWidth:"150px"  }}>
                                {Object.keys(switchItemList).map(key => (
                                  <option value={switchItemList[key][themeData.switchItemValueColumnName]}>{switchItemList[key][themeData.switchItemNameColumnName]}</option>
                                  
                                ))}
                              </Select>
                            )
                          }
                          </div>
                          <div style={{width:"10%"}}>
                            <Box
                            onClick={(e)=>{
                              runInAction(() => {
                                this.props.viewState.showInformationPanel();
                              })
                            }}
                            title="テーマの説明を表示">
                              <StyledIcon
                                  glyph={Icon.GLYPHS.information}
                                  center
                                  styledWidth={"27px"}
                                  styledHeight={"27px"}
                                  fillColor={"#fff"}
                                  style={{margin:"0 auto"}}
                              />
                            </Box>
                          </div>
                        </div>
                        <ResponsiveGridLayout
                          className="layout"
                          layouts={{ lg: leftLayout, md: leftLayout, sm: leftLayout, xs: leftLayout }}
                          breakpoints={{ lg: 1200, md: 996, sm: 950, xs: 480, xxs: 0 }}
                          cols={{ lg: 4, md: 4, sm: 4, xs: 4, xxs: 4 }}
                          rowHeight={rowHeight}
                          isDraggable={true}
                          isResizable={true}
                          draggableHandle={".drag-handle"}
                          resizeHandles={["se"]}
                          onLayoutChange={(layout, layouts) =>{
                            try{
                              const updateLayout = [...layout];
                              runInAction(() => {
                                this.props.viewState.setUpdateLayout(updateLayout);
                              })
                            }catch(e){}
                          }}
                        >
                          {/** グラフ・リスト表示 */}
                          { Object.keys(leftGraphList).map(key => (
                              <div key={key + ""} className={leftGraphList[key].typeId != 5 || this.props.dashboardManagementScreenFlag ? "drag-handle" : ""}>
                                <div style={{border:leftGraphList[key].typeId == 6?"1px solid transition":"none",width:"100%",height:"100%",background:"#fff",overflowX:leftGraphList[key].typeId == 6?"auto":"hidden",overflowY:"hidden"}}>
                                  {leftGraphList[key] && leftGraphList[key].typeId == 1 &&  (
                                    <Bar options={leftGraphList[key].options} data={leftGraphList[key].data} plugins={[leftGraphList[key].custom.customPlugin]} />
                                  )}
                                  {leftGraphList[key] && leftGraphList[key].typeId == 2 &&  (
                                    <Doughnut options={leftGraphList[key].options} data={leftGraphList[key].data} plugins={[leftGraphList[key].custom.centerText]} />
                                  )}
                                  {leftGraphList[key] && leftGraphList[key].typeId == 3 &&  (
                                    <Bar options={leftGraphList[key].options} data={leftGraphList[key].data} plugins={[leftGraphList[key].custom.customPlugin]} />
                                  )}
                                  {leftGraphList[key] && leftGraphList[key].typeId == 4 &&  (
                                    <Bar options={leftGraphList[key].options} data={leftGraphList[key].data} plugins={[leftGraphList[key].custom.customPlugin]} />
                                  )}
                                  {leftGraphList[key] && leftGraphList[key].typeId == 5 &&  (
                                    <>
                                      <div className="z_title">
                                            <img src={Config.config.plateauPath + "/sample/img/icon06.png"} className="icon"/>
                                            <h3 style={{margin:0,padding:"5px"}} className={!this.props.dashboardManagementScreenFlag?"drag-handle":""}>{leftGraphList[key].graphName}</h3>
                                      </div>
                                      <div className="z_biaodan ag-theme-alpine" style={{height: "85%", width: "100%"}}>
                                        <AgGridReact
                                            localeText={AG_GRID_LOCALE_JA}
                                            onRowDataUpdated={(grid) => {
                                              try{
                                                leftGraphList[key]?.initFillter(grid);
                                              }catch(e){}
                                            }}
                                            onRowClicked={(e) =>{
                                              leftGraphList[key].onRowClicked(e);
                                            }}
                                            rowSelection={leftGraphList[key].custom.selectType}
                                            rowData={leftGraphList[key].rowData}
                                            columnDefs={leftGraphList[key].columnDefs}
                                            defaultColDef={{
                                              resizable: true,
                                            }}
                                            isFullWidthRow = {(params) =>{
                                              if(leftGraphList[key].isFullWidthRow){
                                                return leftGraphList[key].isFullWidthRow(params);
                                              }
                                            }}
                                            fullWidthCellRenderer={FullWidthCellRenderer}
                                            doesExternalFilterPass={(node) =>{
                                              if(leftGraphList[key].doesExternalFilterPass){
                                                return leftGraphList[key].doesExternalFilterPass(node);
                                              }
                                            }}
                                            isExternalFilterPresent={(node) =>{
                                              if(leftGraphList[key].isExternalFilterPresent){
                                                return leftGraphList[key].isExternalFilterPresent();
                                              }
                                            }}
                                            suppressRowTransform={leftGraphList[key].suppressRowTransform}
                                            suppressScrollOnNewData={true}
                                            >
                                          </AgGridReact>
                                        </div>
                                      </>
                                    )}
                                    {leftGraphList[key] && leftGraphList[key].typeId == 6 &&  (
                                      <div style={{height:"98%",width:"99%"}} onClick={(e) => leftGraphList[key].onRowClicked()} onTouchEnd={(e) => leftGraphList[key].onRowClicked()} 
                                      title={leftGraphList[key].layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm) => layerGraphCooporationForm.cooperationType==2)>-1?"クリックで地図を色塗りまたは強調表示します" : 
                                      leftGraphList[key].layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm) => layerGraphCooporationForm.cooperationType==3)>-1?"クリックで該当するカテゴリでリストを絞り込みます":""}>
                                        <div>
                                            <h3 style={{margin:0,padding:"5px",color:"#999",fontWeight:"normal",fontSize:".9em"}}>{leftGraphList[key].graphName}</h3>
                                            <p style={{whiteSpace:"pre",margin:0,padding:"5px",fontWeight:"bold",fontSize:"1.3em"}}>{leftGraphList[key].displayValue}</p>
                                        </div>
                                      </div>
                                    )}
                                </div>
                              </div>
                            ))}
                        </ResponsiveGridLayout>
                      </div>
                      )}
                      </Medium>
                      </>
                    }
                    {this.props.viewState.useSmallScreenInterface && !this.props.dashboardManagementScreenFlag && (
                    <Small>
                      {switchItemList && themeData.switchItemValueColumnName && themeData.switchItemNameColumnName && !this.props.viewState.explorerPanelIsVisible && (
                          <div style={{maxWidth:"50%",position:"fixed",top:"55px",left:"5px",zIndex:"999",background:"#fff"}}>
                              <Select
                                value={this.props.viewState.selectedWithThemeSwitchItemValue}
                                light={true}
                                dark={false}
                                id="withThemeSwitchItem"
                                onChange={evt => {
                                  evt.stopPropagation();
                                  const obj = {};
                                  obj[themeData.switchPlaceholderName] = evt.target.value;
                                  runInAction(() => {
                                    this.props.viewState.setSelectedWithThemeSwitchItemValue(evt.target.value);
                                    setThemeData(this.props.viewState,obj);
                                  })
                                }}
                                style={{ color: "#000",border:"1px solid" }}>
                                {Object.keys(switchItemList).map(key => (
                                  <option value={switchItemList[key][themeData.switchItemValueColumnName]}>{switchItemList[key][themeData.switchItemNameColumnName]}</option>
                                  
                                ))}
                              </Select>
                          </div>
                        )
                      }
                          {this.props.terria.showActivityMenu && this.props.terria.permission != "" && (
                          <div style={{height:"100%",width:"100%",position:"fixed",zIndex:"99999",bottom:"0",left:"0"}}>
                              <ActivityPanel terria={terria} viewState={this.props.viewState} />
                          </div>
                          )}
                          {this.props.terria.showPostLayerMenu && this.props.terria.permission != "" && (
                          <div style={{height:"100%",width:"100%",position:"fixed",zIndex:"99999",bottom:"0",left:"0"}}>
                            <PostLayerPanel terria={terria} viewState={this.props.viewState} />
                          </div>
                          )}
                          {this.props.viewState.showSearchMenu && (
                          <div style={{height:"40%",width:"100%",position:"fixed",zIndex:"99999",bottom:"0",left:"0"}} >
                              <SearchPanel terria={terria} viewState={this.props.viewState} />
                          </div>
                          )}
                          {this.props.viewState.showRouteMenu && (
                          <div style={{height:"40%",width:"100%",position:"fixed",zIndex:"99999",bottom:"0",left:"0"}} >
                            <RoutePanel terria={terria} viewState={this.props.viewState} />
                          </div>
                          )}

                    </Small>
                    )}

                  </section>
                  {
                    // ----202307 ここまで----
                  }
                  {this.props.terria.configParameters.storyEnabled &&
                    showStoryBuilder && (
                      <StoryBuilder
                        isVisible={showStoryBuilder}
                        terria={terria}
                        viewState={this.props.viewState}
                        animationDuration={animationDuration}
                      />
                    )}
                </div>
              </div>

              <If condition={!this.props.viewState.hideMapUi}>
                <Medium>
                  <TrainerBar
                    terria={terria}
                    viewState={this.props.viewState}
                  />
                </Medium>
              </If>

              <Medium>
                {/* I think this does what the previous boolean condition does, but without the console error */}
                <If condition={this.props.viewState.isToolOpen}>
                  <Tool
                    {...this.props.viewState.currentTool}
                    viewState={this.props.viewState}
                    t={t}
                  />
                </If>
              </Medium>

              <If condition={this.props.viewState.panel}>
                {this.props.viewState.panel}
              </If>

              <Notification viewState={this.props.viewState} />
              <MapInteractionWindow
                terria={terria}
                viewState={this.props.viewState}
              />

              <If
                condition={
                  !customElements.feedback.length &&
                  this.props.terria.configParameters.feedbackUrl &&
                  !this.props.viewState.hideMapUi &&
                  this.props.viewState.feedbackFormIsVisible
                }
              >
                <FeedbackForm viewState={this.props.viewState} />
              </If>

              {!this.props.dashboardManagementScreenFlag && (
              <div
                className={classNames(
                  Styles.featureInfo,
                  this.props.viewState.topElement === "FeatureInfo"
                    ? "top-element"
                    : "",
                  {
                    [Styles.featureInfoFullScreen]: this.props.viewState
                      .isMapFullScreen
                  }
                )}
                tabIndex={0}
                onClick={action(() => {
                  this.props.viewState.topElement = "FeatureInfo";
                })}
                style={{zIndex:"999"}}
              >
                <FeatureInfoPanel
                  terria={terria}
                  viewState={this.props.viewState}
                  highlightGraphAndFilterList={highlightGraphAndFilterList}
                />
              </div>
              )}
              {/** 
              <DragDropFile
                terria={this.props.terria}
                viewState={this.props.viewState}
              />
              <DragDropNotification viewState={this.props.viewState} />
              {showStoryPanel && (
                <StoryPanel terria={terria} viewState={this.props.viewState} />
              )}
              */}
            </div>
            {this.props.viewState.showHelpMenu &&
              this.props.viewState.topElement === "HelpPanel" && (
                <HelpPanel terria={terria} viewState={this.props.viewState} />
              )}
            <Disclaimer viewState={this.props.viewState} />
          </div>
          {this.props.viewState.printWindow && (
            <PrintView
              window={this.props.viewState.printWindow}
              terria={terria}
              viewState={this.props.viewState}
              closeCallback={() => this.props.viewState.setPrintWindow(null)}
            />
          )}
        </ThemeProvider>
      );
    }
  })
);

export const StandardUserInterfaceWithoutTranslation = StandardUserInterface;

export default withFallback(withTranslation()(StandardUserInterface));
