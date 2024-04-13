import React from "react";
import { useTranslation } from "react-i18next";

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
        title="エリマネ・イベント活動の登録モードを解除"
        className={Styles.helpBtn}
        onClick={evt => {
          evt.preventDefault();
          evt.stopPropagation();
          props.viewState.activityModeReset();
        }}
      >
        <Icon glyph={Icon.GLYPHS.people} />
        <span>活動登録モード解除</span>
      </button>
    </div>
  );
};
