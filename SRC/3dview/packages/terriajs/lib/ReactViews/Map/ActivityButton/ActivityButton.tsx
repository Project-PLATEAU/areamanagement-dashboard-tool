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

  return (
    <div>
      <button
        className={Styles.helpBtn}
        title="エリマネ・イベント活動を登録"
        onClick={evt => {
          evt.preventDefault();
          evt.stopPropagation();
          if(props.viewState.topElement !== "RoutePanel"){
            alert("活動結果の登録地点を選択してください");
          }
          props.viewState.changeActivityMode();
        }}
      >
        <Icon glyph={Icon.GLYPHS.people} />
        <span>活動登録モード</span>
      </button>
    </div>
  );
};
