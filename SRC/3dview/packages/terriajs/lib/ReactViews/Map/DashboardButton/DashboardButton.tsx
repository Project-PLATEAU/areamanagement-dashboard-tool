import React from "react";
import { useTranslation } from "react-i18next";
import WindingOrder from "terriajs-cesium/Source/Core/WindingOrder";

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
        title="DASHBOARDへ移動"
        className={Styles.helpBtn}
        onClick={evt => {
          evt.preventDefault();
          evt.stopPropagation();
          window.open('../dashboard/','_metabase'); 
        }}
      >
        <Icon glyph={Icon.GLYPHS.dashboard} />
        <span>DASHBOARD</span>
      </button>
    </div>
  );
};
