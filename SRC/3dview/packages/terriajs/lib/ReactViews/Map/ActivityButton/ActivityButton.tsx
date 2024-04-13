import React from "react";
import { useTranslation } from "react-i18next";
import { propertyIsEnumerable } from "../../../Map/Proj4Definitions";
import ViewState from "../../../ReactViewModels/ViewState";
import Icon from "../../../Styled/Icon";
import Styles from "../HelpButton/help-button.scss";

interface Props {
  viewState: ViewState;
}

export default (props: Props) => {

  const { t } = useTranslation();
 
  let buttonStyle = Styles.helpBtn;
  let icon = Icon.GLYPHS.people;
  const themeId = props.viewState.selectedThemeId;
  const themeName = props.viewState.selectedThemeName;
  let buttonTitle = themeName+"に登録";
  if(props.viewState.terria.mode == 1 || props.viewState.terria.mode == 4){
    buttonStyle = Styles.helpBtnSelected;
    icon = Icon.GLYPHS.peopleColorChange;
    buttonTitle = themeName+"の登録モードを解除";
  }

  return (
    <div>
      <button
        className={buttonStyle}
        title={buttonTitle}
        onClick={evt => {
          evt.preventDefault();
          evt.stopPropagation();
          const selectedTempThemeId = props.viewState.selectedTempThemeId;
          const selectedThemeId = props.viewState.selectedThemeId;
          if(selectedTempThemeId != selectedThemeId && !props.viewState.terria.themeSwitchingAlertCalledFlag){
            if(!window.confirm("現在は"+themeName+"がテーマ表示されています。登録を開始してよろしいですか？")){
              return;
            }else{
              props.viewState.terria.setThemeSwitchingAlertCalledFlag(true);
            }
          }
          if(props.viewState.terria.mode == 1 || props.viewState.terria.mode == 4){
            props.viewState.hideAllPanel();
          }else{
            props.viewState.hideAllPanel();
            props.viewState.changeActivityMode();
          }
        }}
      >
        <Icon glyph={icon} />
      </button>
    </div>
  );
};
