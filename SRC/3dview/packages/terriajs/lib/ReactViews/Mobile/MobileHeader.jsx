import classNames from "classnames";
import createReactClass from "create-react-class";
import { runInAction } from "mobx";
import { observer } from "mobx-react";
import PropTypes from "prop-types";
import React from "react";
import { withTranslation } from "react-i18next";
import styled, { withTheme } from "styled-components";
import { removeMarker } from "../../Models/LocationMarkerUtils";
import Box from "../../Styled/Box";
import { RawButton } from "../../Styled/Button";
import Icon, { StyledIcon } from "../../Styled/Icon";
import SearchBox from "../Search/SearchBox";
import Branding from "../SidePanel/Branding";
import Styles from "./mobile-header.scss";
import MobileMenu from "./MobileMenu";
import MobileModalWindow from "./MobileModalWindow";
import MenuBar from "../Map/MenuBar";
import {setAllThemeInformation} from '../../Models/Theme/Theme';

const MobileHeader = observer(
  createReactClass({
    displayName: "MobileHeader",

    propTypes: {
      terria: PropTypes.object,
      viewState: PropTypes.object.isRequired,
      allBaseMaps: PropTypes.array,
      version: PropTypes.string,
      menuLeftItems: PropTypes.array,
      menuItems: PropTypes.array,
      theme: PropTypes.object,
      t: PropTypes.func.isRequired
    },

    getInitialState() {
      return {};
    },

    componentDidMount() {
      setAllThemeInformation(this.props.viewState);
    },

    showSearch() {
      const viewState = this.props.viewState;
      const mobileView = viewState.mobileView;
      const mobileViewOptions = viewState.mobileViewOptions;
      const searchState = viewState.searchState;
      runInAction(() => {
        if (
          mobileView === mobileViewOptions.data ||
          mobileView === mobileViewOptions.preview
        ) {
          searchState.showMobileCatalogSearch = true;
        } else {
          searchState.showMobileLocationSearch = true;
          this.showLocationSearchResults();
        }
      });
    },

    closeLocationSearch() {
      runInAction(() => {
        this.props.viewState.searchState.showMobileLocationSearch = false;
        this.props.viewState.explorerPanelIsVisible = false;
        this.props.viewState.switchMobileView(null);
      });
    },

    closeCatalogSearch() {
      runInAction(() => {
        this.props.viewState.searchState.showMobileCatalogSearch = false;
        this.props.viewState.searchState.catalogSearchText = "";
      });
    },

    onMobileDataCatalogClicked() {
      this.props.viewState.setTopElement("DataCatalog");
      this.toggleView(this.props.viewState.mobileViewOptions.data);
    },

    onMobileNowViewingClicked() {
      this.props.viewState.setTopElement("NowViewing");
      this.toggleView(this.props.viewState.mobileViewOptions.nowViewing);
    },

    changeLocationSearchText(newText) {
      runInAction(() => {
        this.props.viewState.searchState.locationSearchText = newText;
      });

      if (newText.length === 0) {
        removeMarker(this.props.terria);
      }

      this.showLocationSearchResults();
    },

    showLocationSearchResults() {
      runInAction(() => {
        const text = this.props.viewState.searchState.locationSearchText;
        if (text && text.length > 0) {
          this.props.viewState.explorerPanelIsVisible = true;
          this.props.viewState.mobileView = this.props.viewState.mobileViewOptions.locationSearchResults;
        } else {
          // TODO: return to the preview mobileView, rather than dropping back to the map
          this.props.viewState.explorerPanelIsVisible = false;
          this.props.viewState.mobileView = null;
        }
      });
    },

    changeCatalogSearchText(newText) {
      runInAction(() => {
        this.props.viewState.searchState.catalogSearchText = newText;
      });
    },

    searchLocations() {
      this.props.viewState.searchState.searchLocations();
    },

    searchCatalog() {
      this.props.viewState.searchState.searchCatalog();
    },

    toggleView(viewname) {
      runInAction(() => {
        if (this.props.viewState.mobileView !== viewname) {
          this.props.viewState.explorerPanelIsVisible = true;
          this.props.viewState.switchMobileView(viewname);
        } else {
          this.props.viewState.explorerPanelIsVisible = false;
          this.props.viewState.switchMobileView(null);
        }
      });
    },

    onClickFeedback(e) {
      e.preventDefault();
      runInAction(() => {
        this.props.viewState.feedbackFormIsVisible = true;
      });
      this.setState({
        menuIsOpen: false
      });
    },

    render() {
      const searchState = this.props.viewState.searchState;
      const { t } = this.props;
      const nowViewingLength =
        this.props.terria.workbench.items !== undefined
          ? this.props.terria.workbench.items.length
          : 0;

      return (
        <div className={Styles.ui} style={{minHeight:"100px",paddingTop:"10px"}}>
          <div>
            <MenuBar
              terria={this.props.terria}
              viewState={this.props.viewState}
              allBaseMaps={this.props.allBaseMaps}
              menuItems={this.props.menuItems}
              menuLeftItems={this.props.menuLeftItems}
              animationDuration={this.props.animationDuration}
            />
            {!this.props.viewState.isMapInteractionActive && (
              <MobileModalWindow
                terria={this.props.terria}
                viewState={this.props.viewState}
              />
            )}
          </div>
        </div>
      );
    }
  })
);

const HamburgerButton = styled(RawButton)`
  border-radius: 2px;
  padding: 0 5px;
  margin-right: 3px;
  width: 50px;
  height: 38px;
  box-sizing: content-box;
  display: flex;
  justify-content: center;
  align-items: center;
  &:hover,
  &:focus,
  & {
    border: 1px solid #939393;
  }
`;

module.exports = withTranslation()(withTheme(MobileHeader));
