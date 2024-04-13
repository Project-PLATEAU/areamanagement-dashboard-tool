import React from "react";
import { useTranslation } from "react-i18next";

import ViewState from "../../../ReactViewModels/ViewState";
import Icon, { GLYPHS ,StyledIcon} from "../../../Styled/Icon";

import Styles from "../HelpButton/help-button.scss";

interface Props {
  viewState: ViewState;
}

export default (props: Props) => {
  const { t } = useTranslation();

  return (
    <div>
      <button
        title="町丁目を検索"
        className={Styles.helpBtn}
        onClick={evt => {
          evt.preventDefault();
          evt.stopPropagation();
          props.viewState.hideAllPanel();
          props.viewState.showSearchPanel();
        }}
      >
        <StyledIcon
          glyph={Icon.GLYPHS.search}
          styledWidth={"25px"}
         />
      </button>
    </div>
  );
};
