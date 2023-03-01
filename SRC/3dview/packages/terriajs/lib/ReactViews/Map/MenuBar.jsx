import React from "react";
import styled from "styled-components";

import PropTypes from "prop-types";
import classNames from "classnames";
import SettingPanel from "./Panels/SettingPanel";
import SharePanel from "./Panels/SharePanel/SharePanel";
import ToolsPanel from "./Panels/ToolsPanel/ToolsPanel";
import StoryButton from "./StoryButton/StoryButton";
import RouteButton from "./RouteButton/RouteButton";
import ActivityButton from "./ActivityButton/ActivityButton";
import ResetActivityModeButton from "./ActivityButton/ResetActivityModeButton";
import DashboardButton from "./DashboardButton/DashboardButton";
import LangPanel from "./Panels/LangPanel/LangPanel";

import Styles from "./menu-bar.scss";
import { runInAction } from "mobx";
import { observer } from "mobx-react";

import withControlledVisibility from "../../ReactViews/HOCs/withControlledVisibility";
import HelpButton from "./HelpButton/HelpButton";
import Icon from "../../Styled/Icon";
import { useTranslation } from "react-i18next";

const StyledMenuBar = styled.div`
  pointer-events: none;
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

  return (
    <StyledMenuBar
      className={classNames(
        props.viewState.topElement === "MenuBar" ? "top-element" : "",
        Styles.menuBar,
        {
          [Styles.menuBarWorkbenchClosed]: props.viewState.isMapFullScreen
        }
      )}
      onClick={handleClick}
      trainerBarVisible={props.viewState.trainerBarVisible}
    >
      <section>
        <ul className={classNames(Styles.menu)}>
          {enableTools && (
            <li className={Styles.menuItem}>
              <ToolsPanel terria={props.terria} viewState={props.viewState} />
            </li>
          )}
          <If condition={!props.viewState.useSmallScreenInterface}>
            <For each="element" of={props.menuLeftItems} index="i">
              <li className={Styles.menuItem} key={i}>
                {element}
              </li>
            </For>
          </If>
        </ul>
      </section>
      <section className={classNames(Styles.flex)}>
        <ul className={classNames(Styles.menu)}>
          <li className={Styles.menuItem}>
            <SettingPanel terria={props.terria} viewState={props.viewState} />
          </li>
          <li className={Styles.menuItem}>
            {/* shareと位置入れ替え
            <HelpButton viewState={props.viewState} /> 
            */}
            <SharePanel
              terria={props.terria}
              viewState={props.viewState}
              animationDuration={props.animationDuration}
            />
          </li>

          {props.terria.configParameters?.languageConfiguration?.enabled ? (
            <li className={Styles.menuItem}>
              <LangPanel
                terria={props.terria}
                smallScreen={props.viewState.useSmallScreenInterface}
              />
            </li>
          ) : null}
        </ul>
        <If condition={storyEnabled}>
          <ul className={classNames(Styles.menu)}>
            <li className={Styles.menuItem}>
              <StoryButton
                terria={props.terria}
                viewState={props.viewState}
                theme={props.theme}
              />
            </li>
          </ul>
        </If>
        {props.terria.permission === "admin" && props.terria.mode !== 1 && (
        <ul className={classNames(Styles.menu)}>
          <li className={Styles.menuItem}>
            <ActivityButton
              viewState={props.viewState}
            />
          </li>
        </ul>
        )}
        {props.terria.permission === "admin" &&  props.terria.mode === 1  && (
        <ul className={classNames(Styles.menu)}>
          <li className={Styles.menuItem}>
            <ResetActivityModeButton
              viewState={props.viewState}
            />
          </li>
        </ul>
        )}
        <ul className={classNames(Styles.menu)}>
          <li className={Styles.menuItem}>
            <RouteButton
              viewState={props.viewState}
            />
          </li>
        </ul>
        <ul className={classNames(Styles.menu)}>
          <li className={Styles.menuItem}>
            {/* helpと位置入れ替え
            <SharePanel
              terria={props.terria}
              viewState={props.viewState}
              animationDuration={props.animationDuration}
            /> */}

            {/* HelpButton を使うべき？ */}
            {/* <button
              className={Styles.btn}
              title={t("helpMenu.btnTitle")}
              onClick={() => props.viewState.showHelpPanel()}
            >
              <Icon glyph={Icon.GLYPHS.help} />
              <span>{t("helpMenu.btnText")}</span>
            </button> */}
            <HelpButton viewState={props.viewState} />
          </li>
        </ul> 
        <ul className={classNames(Styles.menu)}>
          <li className={Styles.menuItem}>
            <DashboardButton
              viewState={props.viewState}
            />
          </li>
        </ul>
        <If condition={!props.viewState.useSmallScreenInterface}>
          <For each="element" of={menuItems} index="i">
            <li className={Styles.menuItem} key={i}>
              {element}
            </li>
          </For>
        </If>
        {/* 活動登録パネル表示 */}
        <div id="activityPanelControll" style={{ position: "absolute", left: -99999 + "px" }} onClick={evt => {
          evt.preventDefault();
          evt.stopPropagation();
          props.viewState.showActivityPanel();
        }}>
        </div>
      </section>
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
