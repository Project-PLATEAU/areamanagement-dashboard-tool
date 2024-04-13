import React from "react";
import createReactClass from "create-react-class";
import PropTypes from "prop-types";
import ExplorerWindow from "../ExplorerWindow/ExplorerWindow";
import MapColumn from "./MapColumn";
import ExperimentalFeatures from "../Map/ExperimentalFeatures";
import ProgressBar from "../Map/ProgressBar";
import SidePanel from "../SidePanel/SidePanel";
import FullScreenButton from "./../SidePanel/FullScreenButton.jsx";
import { Small, Medium } from "../Generic/Responsive";
import classNames from "classnames";
import "inobounce";
import { withTranslation } from "react-i18next";
import Styles from "./standard-user-interface.scss";
import Icon, { StyledIcon } from "../../Styled/Icon";
// import Variables from "../../Sass/common/variables";
import { observer } from "mobx-react";
import { action, runInAction } from "mobx";
export const showStoryPrompt = (viewState, terria) => {
  terria.configParameters.showFeaturePrompts &&
    terria.configParameters.storyEnabled &&
    terria.stories.length === 0 &&
    viewState.toggleFeaturePrompt("story", true);
};
import BottomDock from "../BottomDock/BottomDock";
import FeatureDetection from "terriajs-cesium/Source/Core/FeatureDetection";
import InformationPanel from "../Map/Panels/InformationPanel/InformationPanel";
import Config from "../../../customconfig.json";

const isIE = FeatureDetection.isInternetExplorer();
/**
 * 3dviewer
 */
const Viewer = observer(
  createReactClass({
    displayName: "Viewer",
    propTypes: {
      terria: PropTypes.object.isRequired,
      viewState: PropTypes.object.isRequired,
      animationDuration: PropTypes.object.isRequired,
      customElements: PropTypes.object.isRequired,
      allBaseMaps: PropTypes.object.isRequired,
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
      const terria = this.props.terria;
      let operationGuideText = "";
      const themeName = this.props.viewState.selectedThemeName;
      if((this.props.viewState.terria.mode == 1 || this.props.viewState.terria.mode == 4) && this.props.viewState.terria.clickLatLong == ""){
        operationGuideText = "※地図上から"+themeName+"の登録地点の選択を行います。再度登録ボタンを押下することで登録モードの解除が行えます。"
      }
      if(this.props.viewState.terria.mode == 2){
        operationGuideText = "※地図上から出発地点の選択を行います。"
      }
      if(this.props.viewState.terria.mode == 3){
        operationGuideText = "※地図上から到着地点の選択を行います。"
      }
      return (
        <div className={Styles.z_jianzhu} css={`height:100%;width:100%;`} >
            {
            // ----202307 ここから styleは仮で当て込み----
            }
            {!this.props.dashboardManagementScreenFlag && (
            <If condition={!this.props.viewState.hideMapUi}>
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
                    onTransitionEnd={() =>
                    this.props.viewState.triggerResizeEvent()
                    }
                    css={`
                        position: absolute;
                        top:0;
                        z-index:99999;
                        background:#fff;
                        height:100%;
                        width:40%;
                        user-drag:none;
                    `}
                    draggable="false"
                >
                    <SidePanel
                    terria={terria}
                    viewState={this.props.viewState}
                    />
                </div>
                </Medium>
            </If>
            )}
            {this.props.viewState.showInformationMenu && (
              <InformationPanel terria={terria} viewState={this.props.viewState} />
            )}
            {!this.props.dashboardManagementScreenFlag && (
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
                    animationDuration={this.props.animationDuration}
                    elementConfig={this.props.terria.elements.get(
                    "show-workbench"
                    )}

                />
                </div>
            </Medium>
            )}
                <ProgressBar terria={terria} />
            <div css={`height:100%;width:100%;`}>
                <MapColumn
                terria={terria}
                viewState={this.props.viewState}
                customFeedbacks={this.props.customElements.feedback}
                customElements={this.props.customElements}
                allBaseMaps={this.props.allBaseMaps}
                animationDuration={this.props.animationDuration}
                dashboardManagementScreenFlag = {this.props.dashboardManagementScreenFlag}
                />
                {operationGuideText != "" && (
                <div style={{position:"absolute",bottom:"30px",background:"rgba(255,241,0,.5)",fontWeight:"bold",color:"#000",width:"100%",textAlign:"center",fontSize:".9em"}}
                css={`
                ${this.props.terria.useSmallScreenInterface == true && 
                    `
                    padding-bottom:80px;
                    text-align:left!important;
                    font-size:.9em;
                    `}
                `}
                >
                    <p>{operationGuideText}</p>
                </div>)}
            </div>
            {!this.props.dashboardManagementScreenFlag && (
            <If condition={!this.props.viewState.hideMapUi}>
                <div css={`position:absolute;bottom:0;width:100%`} >
                <div>
                    <BottomDock
                    terria={this.props.terria}
                    viewState={this.props.viewState}
                    domElementRef={this.addBottomDock}
                    />
                </div>
                </div>
            </If>
            )}
            {!this.props.dashboardManagementScreenFlag && (
            <Medium>
                 <div className={Styles.z_guanbi}>
                    <If condition={!this.props.viewState.hideMapUi}>
                        {this.props.viewState.fullScreenMode == 0 && (
                        <span style={{ marginRight: "30px" }}>
                            <button 
                            css={`
                            color: #fff;
                            background-color: #00bebe;
                            padding: 7px;
                            padding-right: 10px
                            border:none!important;
                            position:absolute;
                            border-radius: 0 16px 16px 0;
                            border:0;
                            top:9%;
                            `} onClick={() => this.props.viewState.setFullScreenMode(1)} className={Styles.allDisplayBtn}>
                        {/* <StyledIcon
                            glyph={Icon.GLYPHS.close}
                            center
                            styledWidth={"15px"}
                            fillColor={"#fff"}
                        /> */}
                        <img src={Config.config.plateauPath + "/sample/img/icon09.png"}/>
                        </button>
                        </span>
                        )}
                        {this.props.viewState.fullScreenMode == 1 && (
                        <span style={{ marginRight: "30px" }}>
                            <button css={`
                            color: #fff;
                            background: rgb(42, 174, 122);
                            padding:.4em;
                            border:none!important;
                            position:absolute;
                            border-radius:15px;
                            top:7%;
                            `} onClick={() => {
                                this.props.viewState.setFullScreenMode(0);
                                window.setTimeout(() => {
                                    this.props.viewState.triggerResizeEvent();
                                }, 2000);
                            }}><span>全画面終了</span>
                        </button>
                        </span>
                        )}
                    </If>
                </div>
            </Medium>
            )}
            <main>
                {/*
                <ExplorerWindow
                terria={terria}
                viewState={this.props.viewState}
                />
                */}
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
                    experimentalItems={this.props.customElements?.experimentalMenu}
                />
                </If>
            </main>
            {
            // ----202307 ここまで----
            }
        </div>
      );
    }
  })
);

export default withTranslation()(Viewer);
