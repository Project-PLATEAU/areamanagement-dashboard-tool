import React from "react";
import styled from "styled-components";
import PropTypes from "prop-types";
import classNames from "classnames";
import SettingPanel from "./Panels/SettingPanel";
import SharePanel from "./Panels/SharePanel/SharePanel";
import ToolsPanel from "./Panels/ToolsPanel/ToolsPanel";
import StoryButton from "./StoryButton/StoryButton";
import RouteButton from "./RouteButton/RouteButton";
import LoginButton from "./LoginButton/LoginButton";
import ActivityButton from "./ActivityButton/ActivityButton";
import ResetActivityModeButton from "./ActivityButton/ResetActivityModeButton";
import DashboardButton from "./DashboardButton/DashboardButton";
import LangPanel from "./Panels/LangPanel/LangPanel";

import Styles from "./menu-bar.scss";
import Select from "../../Styled/Select";
import Box from "../../Styled/Box";
import { action,runInAction } from "mobx";
import { observer } from "mobx-react";

import withControlledVisibility from "../../ReactViews/HOCs/withControlledVisibility";
import HelpButton from "./HelpButton/HelpButton";
import Icon,{StyledIcon} from "../../Styled/Icon";
import { useTranslation } from "react-i18next";
import {setThemeData} from '../../Models/GraphList/GraphList';
import { Small, Medium ,ExtraSmall } from "../Generic/Responsive";
import { right } from "../Story/story-panel.scss";
import Config from "../../../customconfig.json";

const StyledMenuBar = styled.div`
  display:flex;
  ${p =>
    p.trainerBarVisible &&
    `
    top: ${Number(p.theme.trainerHeight) + Number(p.theme.mapButtonTop)}px;
  `}
`;
// The map navigation region
const MenuBar = observer(props => {
  const { t } = useTranslation();
  const menuItems = props.menuItems || [];
  const handleClick = () => {
    runInAction(() => {
      props.viewState.topElement = "MenuBar";
    });
  };
  const storyEnabled = props.terria.configParameters.storyEnabled;
  const enableTools = props.terria.getUserProperty("tools") === "1";

  let userId = props.terria.userId;
  let userName = props.terria.name;
  let background = "#0A8253";
  if(userName == "" || userName == null) userName="ログイン済み";
  if(!userId || userId == -1){
    userName = "未ログイン";
  }
  const themeDataList = props.viewState.themeDataList;
  const selectedThemeGroupName = props.viewState.selectedThemeGroupName;
  const selectedTempThemeId = props.viewState.selectedTempThemeId;
  const selectedThemeId = props.viewState.selectedThemeId;
  let themeGroupNameList = {};
  for(let i=0;i<themeDataList.length;i++){
    if(!themeGroupNameList[themeDataList[i].themeGroupName]){
      themeGroupNameList[themeDataList[i].themeGroupName] = {};
    }
    themeGroupNameList[themeDataList[i].themeGroupName][themeDataList[i].dispOrder] = {themeId:themeDataList[i].themeId,themeName:themeDataList[i].themeName};
  }

  return (
    <StyledMenuBar
      className={classNames(
        props.viewState.topElement === "MenuBar" ? "top-element" : "",
        Styles.menuBar,
        {
          [Styles.menuBarWorkbenchClosed]: props.viewState.isMapFullScreen
        }
      )}
      trainerBarVisible={props.viewState.trainerBarVisible}
    >
        {
          // ----202307 ここから styleは仮で当て込み----
        }
        <Box styledMargin={"0 10px 0 0"} className={Styles.themeItem}>
          <Select
            light={true}
            dark={false}
            onChange={e => {
              runInAction(() => {
                if(e.target.value && e.target.value != ""){
                  props.viewState.setThemeGroupName(e.target.value);
                  props.viewState.setSelectedTempThemeId("");
                }else{
                  props.viewState.setThemeGroupName("");
                  props.viewState.setSelectedTempThemeId("");
                }
                props.viewState.terria.setThemeSwitchingAlertCalledFlag(false);
              })
            }}
            className={Styles.z_select}>
            <option value=""></option>
            {Object.keys(themeGroupNameList).map(key => (
              <option value={key} selected={key == selectedThemeGroupName}>{key}</option>
            ))}
          </Select>
        </Box>
        <Box styledMargin={"0 10px 0 0"} className={Styles.themeItem}>
          <Select
            light={true}
            dark={false}
            onChange={e => {
              runInAction(() => {
                if(e.target.value && e.target.value != ""){
                  const temp = e.target.value
                  if(temp != selectedThemeId){
                    const index = themeDataList.findIndex(themeData=>themeData.themeId == temp);
                    if(index > -1){
                      runInAction(() => {
                        props.viewState.resetUpdateLayout();
                        props.viewState.setIsMapFullScreen(!Config.config.defaultShowWorkbench);
                        props.viewState.setSelectedWithThemeSwitchItemValue("");
                        props.viewState.setThemeData(themeDataList[index]);
                        setThemeData(props.viewState);
                        props.viewState.hideAllPanel();
                        props.viewState.hideInformationPanel();
                        //属性表示をクリアにしておく
                        props.viewState.clearLinkedFeatureId();
                        props.viewState.featureInfoPanelIsVisible = false;
                        setTimeout(
                        action(() => {
                            props.terria.pickedFeatures = undefined;
                            props.terria.selectedFeature = undefined;
                        }),
                        200
                        );
                      })
                    }
                  }
                }
                props.viewState.terria.setThemeSwitchingAlertCalledFlag(false);
              })
            }}
            className={Styles.z_select}>
            <option value=""></option>
            {themeGroupNameList[selectedThemeGroupName] && Object.keys(themeGroupNameList[selectedThemeGroupName]).map(key => (
              <option value={themeGroupNameList[selectedThemeGroupName][key].themeId} selected={themeGroupNameList[selectedThemeGroupName][key].themeId==selectedTempThemeId}>{themeGroupNameList[selectedThemeGroupName][key].themeName}</option>
            ))}
          </Select>
        </Box>
        <Small>
          <Box styledMargin={"10px 10px 0 0"} style={{width:"100%",height:"50px"}}>
          <div className={Styles.z_anniu_mob} style={{position:"absolute",right:"0",display:"flex"}}>
              <LoginButton
                viewState={props.viewState}
                terria={props.terria}
              />
              <Box className={Styles.bbZcMN} style={{color:"#ffff",padding:"10px",maxWidth:"150px",overflow:"hidden",wordBreak:"keep-all"}}>
                {userName}
              </Box>
              <img src={Config.config.plateauPath + "/sample/img/icon01.png"} className={Styles.z_bg}/>
            </div>
          </Box>
        </Small>
        <Medium>
          <Box className={Styles.z_anniu} style={{position:"absolute",right:"10px"}}>
            <img src={Config.config.plateauPath + "/sample/img/icon01.png"} className={Styles.z_bg}/>
            <LoginButton 
              viewState={props.viewState}
              terria={props.terria}
            />
            <Box className={Styles.bbZcMN} style={{color:"#ffff",padding:"10px"}}>
              {userName}
            </Box>
          </Box>
        </Medium>
        {
          // ----202307 ここまで----
        }
    </StyledMenuBar>
  );
});
MenuBar.displayName = "MenuBar";
MenuBar.propTypes = {
  terria: PropTypes.object,
  viewState: PropTypes.object.isRequired,
  allBaseMaps: PropTypes.array, // Not implemented yet
  animationDuration: PropTypes.number,
  menuItems: PropTypes.arrayOf(PropTypes.element),
  menuLeftItems: PropTypes.arrayOf(PropTypes.element)
};

export default withControlledVisibility(MenuBar);
