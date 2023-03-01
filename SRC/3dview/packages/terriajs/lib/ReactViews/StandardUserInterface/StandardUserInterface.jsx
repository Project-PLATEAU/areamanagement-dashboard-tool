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
import FeatureInfoPanel from "../FeatureInfo/FeatureInfoPanel";
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

import PrintView from "../../ReactViews/Map/Panels/SharePanel/Print/PrintView";

import withFallback from "../HOCs/withFallback";
import TourPortal from "../Tour/TourPortal";
import SatelliteHelpPrompt from "../HelpScreens/SatelliteHelpPrompt";
import WelcomeMessage from "../WelcomeMessage/WelcomeMessage";

import { Small, Medium } from "../Generic/Responsive";
import classNames from "classnames";
import "inobounce";

import { withTranslation } from "react-i18next";

import Styles from "./standard-user-interface.scss";
// import Variables from "../../Sass/common/variables";
import { observer } from "mobx-react";
import { action, runInAction } from "mobx";
import HelpPanel from "../Map/Panels/HelpPanel/HelpPanel";
import RoutePanel from "../Map/Panels/RoutePanel/RoutePanel";
import ActivityPanel from "../Map/Panels/ActivityPanel/ActivityPanel";
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
      viewState: PropTypes.object.isRequired,
      minimumLargeScreenWidth: PropTypes.number,
      version: PropTypes.string,
      children: PropTypes.oneOfType([
        PropTypes.arrayOf(PropTypes.element),
        PropTypes.element
      ]),
      t: PropTypes.func.isRequired
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
      this._wrapper.addEventListener("dragover", this.dragOverListener, false);
      showStoryPrompt(this.props.viewState, this.props.terria);
      this.focusMapPlaceDriver();
      window.addEventListener('hashchange', () => { this.focusMapPlaceDriver() }, false);
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

    focusMapPlaceDriver(){
      const urlParameter = window.location.hash.substring(1);
      let params = {};
      urlParameter.split("&").forEach(param => {
          const temp = param.split("=");
          params[temp[0]] = temp[1];
      });
      if((params["activityType"] || params["excursionAnalysisType"]) && params["lon"] && params["lat"]){
        // 3dmodeにセット
        try{
          this.props.viewState.set3dMode();
          clearInterval(this.focusMapPlaceAction);
          let count = 0;
          this.focusMapPlaceAction = setInterval(() => {
            console.log("focusMapPlaceAction setInterval");
            count = count + 1;
            if(this.props.terria.cesium?.scene){
              clearInterval(this.focusMapPlaceAction);
              this.focusMapPlaceAndAttributeDisplay(params["lon"],params["lat"],params["height"],params["activityType"],params["parentActivityId"],params["excursionAnalysisType"],params["excursionAnalysisCount"],params["excursionAnalysisMaxCount"],params["excursionAnalysisId"]);
            }
            if(count > 50){
              clearInterval(this.focusMapPlaceAction);
            }
          }, 1000);
        }catch(error){
          console.error('処理に失敗しました', error);
        }
      }
    },

    /**
     * 地図上の指定した緯度経度にフォーカスを行い属性情報を表示
     * @param {number} lon 
     * @param {number} lat 
     * @param {string} activityType　エリマネ・イベント活動タイプ
     * @param {string} parentActivityId　活動ID
     * @param {string} excursionAnalysisType　回避分析タイプ
     * @param {string} excursionAnalysisCount　回避分析回数
     * @param {string} excursionAnalysisMaxCount　回避分析最大回数
     * @param {string} excursionAnalysisId
     */
    focusMapPlaceAndAttributeDisplay(lon,lat,height,activityType,parentActivityId,excursionAnalysisType,excursionAnalysisCount,excursionAnalysisMaxCount,excursionAnalysisId) {
      lon = parseFloat(lon);
      lat = parseFloat(lat);
      if(excursionAnalysisType){
        excursionAnalysisType = decodeURI(excursionAnalysisType);
      }
      if(excursionAnalysisId){
        excursionAnalysisId = decodeURI(excursionAnalysisId);
      }
      //現在のカメラ位置等を取得
      const currentSettings = getShareData(this.props.terria, this.props.viewState);
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
      const scene = this.props.terria.cesium.scene;
      const terrainProvider = scene.terrainProvider;
      const positions = [Cartographic.fromDegrees(lon, lat)];
      let currentHeight = 0;
      sampleTerrainMostDetailed(terrainProvider, positions).then((updatedPositions) => {
          currentHeight = parseFloat(updatedPositions[0].height);
          let diff = 500;

          //回遊情報の場合の高さ調整用
          if(excursionAnalysisType === Config.queryParameter.excursionAnalysisType.excursion){
            if(parseFloat(height) < 20){
              height = 20;
            }
            diff = parseFloat(height) * 3.5;
          }else if(excursionAnalysisType === Config.queryParameter.excursionAnalysisType.spot){
            if(parseFloat(height) < 100){
              height = 100;
            }
            diff = parseFloat(height) * 1.5;
          }

          let coord_wgs84 = Cartographic.fromDegrees(lon, lat, currentHeight + diff);
          let coord_xyz = Ellipsoid.WGS84.cartographicToCartesian(coord_wgs84);

          //回遊情報の場合
          if(excursionAnalysisType === Config.queryParameter.excursionAnalysisType.excursion || excursionAnalysisType === Config.queryParameter.excursionAnalysisType.spot){
            //回遊情報の場合のcoord_xyz.z値調整用
            let zDiff = (currentHeight + diff) / 5;
            if(zDiff < 100){
              zDiff = 100;
            }
            if(excursionAnalysisType === Config.queryParameter.excursionAnalysisType.excursion){
              zDiff  = (currentHeight + diff) / 3;
            }
            newCamera.position = { x: coord_xyz.x, y: coord_xyz.y, z: coord_xyz.z - zDiff};
            newCamera.direction = { x: 0.6984744646088341, y: -0.9617056496661655, z: 0.2725418417221117 };
          //エリマネ・イベント活動の場合
          }else{
            newCamera.position = { x: coord_xyz.x, y: coord_xyz.y - 20, z: coord_xyz.z - 400};
            newCamera.direction = { x: 0.6984744646088341, y: -0.6617056496661655, z: 0.2725418417221117 };
          }
          
          newCamera.up = { x: -0.21791222301017105, y: 0.1660947782238842, z: 0.9617311410729739 };

          //フォーカス移動後レイヤー表示及び属性表示を行う
          this.props.terria.currentViewer.zoomTo(newCamera, 3).then(r => {
            this.props.terria.allowFeatureInfoRequests = true;
            //エリマネ・イベント活動情報の場合
            if(activityType && activityType !== ""){
              const items = this.props.terria.workbench.items;
              for (const aItem of items) {
                  if (aItem.uniqueId === Config.layerId.erimane || aItem.uniqueId === Config.layerId.event) {
                    this.props.terria.workbench.remove(aItem);
                  }
              }
              let layerId = "";
              if(activityType === Config.queryParameter.activityType.erimane + "" || activityType === Config.queryParameter.activityType.erimane){
                layerId = Config.layerId.erimane;
              }else if(activityType === Config.queryParameter.activityType.event + "" || activityType === Config.queryParameter.activityType.event){
                layerId = Config.layerId.event;
              }
              if(layerId !==""){
                try{
                  const item = this.props.terria.getModelById(BaseModel, layerId);
                  this.props.terria.workbench.add(item).then(r => {
                    //属性情報の表示追加
                    clearInterval(this.focusMapPlaceAction);
                    this.focusMapPlaceAction = setInterval(() => {
                      if(parseInt(document.getElementById("ProgressBar").style.width) >= 50){
                        clearInterval(this.focusMapPlaceAction);
                        const windowPosition = SceneTransforms.wgs84ToWindowCoordinates(this.props.terria.cesium.scene, Cartesian3.fromDegrees(lon, lat, currentHeight));
                        console.log(windowPosition);
                        this.props.terria.cesium.pickFromScreenPosition(windowPosition, false, parentActivityId+"");
                      }
                    }, 2000)
                  })
                }catch(error){
                  console.error('処理に失敗しました', error);
                }
              }
            //回避分析の場合
            }else if((excursionAnalysisType === Config.queryParameter.excursionAnalysisType.excursion || excursionAnalysisType === Config.queryParameter.excursionAnalysisType.spot) && excursionAnalysisCount && excursionAnalysisCount > 0){
              const stringId = Config.layerId.excursionAnalysis.replace("[excursionAnalysisCount]",excursionAnalysisCount).replace("[excursionAnalysisType]",excursionAnalysisType);
              try{
                const item = this.props.terria.getModelById(BaseModel, stringId);
                const items = this.props.terria.workbench.items;
                this.props.terria.workbench.add(item).then(r => {
                  //属性情報の表示追加
                  clearInterval(this.focusMapPlaceAction);
                  this.focusMapPlaceAction = setInterval(() => {
                    if(parseInt(document.getElementById("ProgressBar").style.width) >= 70){
                      clearInterval(this.focusMapPlaceAction);
                      if(excursionAnalysisMaxCount && excursionAnalysisMaxCount > 0){
                        for (const aItem of items) {
                          for(let i=1;i<=excursionAnalysisMaxCount;i++){
                            const uniqueId = Config.layerId.excursionAnalysis.replace("[excursionAnalysisCount]",i).replace("[excursionAnalysisType]",excursionAnalysisType);
                            if (aItem.uniqueId === uniqueId && aItem.uniqueId !== stringId) {
                              this.props.terria.workbench.remove(aItem);
                            }
                          }
                        }
                      }
                      let positionHeight = (currentHeight + diff) * 0.5;
                      if(excursionAnalysisType === Config.queryParameter.excursionAnalysisType.excursion){
                        positionHeight = (currentHeight + diff) * 0.3;
                      }
                      const windowPosition = SceneTransforms.wgs84ToWindowCoordinates(this.props.terria.cesium.scene, Cartesian3.fromDegrees(lon, lat, positionHeight));
                      console.log(windowPosition);
                      this.props.terria.cesium.pickFromScreenPosition(windowPosition, false, excursionAnalysisId + "");
                    }
                  }, 3000)
                });
              }catch(error){
                console.error('処理に失敗しました', error);
              }
            } 
          })
        })
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
            <div
              className={classNames(Styles.uiRoot, {
                [Styles.withStoryBuilder]: showStoryBuilder
              })}
              css={`
                ${this.props.viewState.disclaimerVisible &&
                  `filter: blur(10px);`}
              `}
              ref={w => (this._wrapper = w)}
            >
              <div className={Styles.ui}>
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
                <div className={Styles.uiInner}>
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
                    <Medium>
                      <div
                        className={classNames(
                          Styles.sidePanel,
                          this.props.viewState.topElement === "SidePanel"
                            ? "top-element"
                            : "",
                          {
                            [Styles.sidePanelHide]: this.props.viewState
                              .isMapFullScreen
                          }
                        )}
                        tabIndex={0}
                        onClick={action(() => {
                          this.props.viewState.topElement = "SidePanel";
                        })}
                        // TODO: debounce/batch
                        onTransitionEnd={() =>
                          this.props.viewState.triggerResizeEvent()
                        }
                      >
                        <SidePanel
                          terria={terria}
                          viewState={this.props.viewState}
                        />
                      </div>
                    </Medium>
                  </If>
                  <Medium>
                    <div
                      className={classNames(Styles.showWorkbenchButton, {
                        [Styles.showWorkbenchButtonTrainerBarVisible]: this
                          .props.viewState.trainerBarVisible,
                        [Styles.showWorkbenchButtonisVisible]: this.props
                          .viewState.isMapFullScreen,
                        [Styles.showWorkbenchButtonisNotVisible]: !this.props
                          .viewState.isMapFullScreen
                      })}
                    >
                      <FullScreenButton
                        terria={this.props.terria}
                        viewState={this.props.viewState}
                        minified={false}
                        btnText={t("sui.showWorkbench")}
                        animationDuration={animationDuration}
                        elementConfig={this.props.terria.elements.get(
                          "show-workbench"
                        )}
                      />
                    </div>
                  </Medium>

                  <section className={Styles.map}>
                    <ProgressBar terria={terria} />
                    <MapColumn
                      terria={terria}
                      viewState={this.props.viewState}
                      customFeedbacks={customElements.feedback}
                      customElements={customElements}
                      allBaseMaps={allBaseMaps}
                      animationDuration={animationDuration}
                    />
                    <main>
                      <ExplorerWindow
                        terria={terria}
                        viewState={this.props.viewState}
                      />
                      <If
                        condition={
                          this.props.terria.configParameters
                            .experimentalFeatures &&
                          !this.props.viewState.hideMapUi
                        }
                      >
                        <ExperimentalFeatures
                          terria={terria}
                          viewState={this.props.viewState}
                          experimentalItems={customElements.experimentalMenu}
                        />
                      </If>
                    </main>
                  </section>
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
              >
                <FeatureInfoPanel
                  terria={terria}
                  viewState={this.props.viewState}
                />
              </div>
              <DragDropFile
                terria={this.props.terria}
                viewState={this.props.viewState}
              />
              <DragDropNotification viewState={this.props.viewState} />
              {showStoryPanel && (
                <StoryPanel terria={terria} viewState={this.props.viewState} />
              )}
            </div>
            {this.props.viewState.showHelpMenu &&
              this.props.viewState.topElement === "HelpPanel" && (
                <HelpPanel terria={terria} viewState={this.props.viewState} />
              )}
            {this.props.viewState.showRouteMenu && (
                <RoutePanel terria={terria} viewState={this.props.viewState} />
              )}
            {this.props.viewState.showActivityMenu && this.props.terria.permission === "admin" && (
                <ActivityPanel terria={terria} viewState={this.props.viewState} />
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
