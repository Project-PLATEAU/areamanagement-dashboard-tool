import { TFunction } from "i18next";
import { action, computed, observable, runInAction } from "mobx";
import { observer } from "mobx-react";
import Slider from "rc-slider";
import React, { ChangeEvent, ComponentProps, MouseEvent } from "react";
import { withTranslation, WithTranslation } from "react-i18next";
import styled, { DefaultTheme, withTheme } from "styled-components";
import ImagerySplitDirection from "terriajs-cesium/Source/Scene/ImagerySplitDirection";
import Cesium from "../../../Models/Cesium";
import DefaultTimelineModel from "../../../Models/DefaultTimelineModel";
import Terria from "../../../Models/Terria";
import ViewerMode, {
  MapViewers,
  setViewerMode
} from "../../../Models/ViewerMode";
import ViewState from "../../../ReactViewModels/ViewState";
import Box from "../../../Styled/Box";
import Button, { RawButton } from "../../../Styled/Button";
import Checkbox from "../../../Styled/Checkbox";
import { GLYPHS, StyledIcon } from "../../../Styled/Icon";
import Spacing from "../../../Styled/Spacing";
import Text, { TextSpan } from "../../../Styled/Text";
import withTerriaRef from "../../HOCs/withTerriaRef";
import MenuPanel from "../../StandardUserInterface/customizable/MenuPanel";
import Styles from "./setting-panel.scss";

// const sides = {
//   left: "settingPanel.terrain.left",
//   both: "settingPanel.terrain.both",
//   right: "settingPanel.terrain.right"
// };
const sides = ["Left", "Both", "Right"];

type PropTypes = WithTranslation & {
  terria: Terria;
  viewState: ViewState;
  refFromHOC?: React.Ref<HTMLDivElement>;
  theme: DefaultTheme;
  t: TFunction;
};

@observer
class SettingPanel extends React.Component<PropTypes> {
  /**
   * @param {Props} props
   */
  constructor(props: PropTypes) {
    super(props);
  }

  @observable _hoverBaseMap = null;

  @observable _enableCollisionDetection = true;

  @computed
  get activeMapName() {
    return this._hoverBaseMap
      ? this._hoverBaseMap
      : this.props.terria.mainViewer.baseMap
      ? (this.props.terria.mainViewer.baseMap as any).name
      : "(None)";
  }

  selectBaseMap(baseMap: any, event: MouseEvent<HTMLButtonElement>) {
    event.stopPropagation();
    this.props.terria.mainViewer.setBaseMap(baseMap);
    // this.props.terria.baseMapContrastColor = baseMap.contrastColor;

    // We store the user's chosen basemap for future use, but it's up to the instance to decide
    // whether to use that at start up.
    if (baseMap) {
      const baseMapId = baseMap.uniqueId;
      if (baseMapId) {
        this.props.terria.setLocalProperty("basemap", baseMapId);
      }
    }
  }

  mouseEnterBaseMap(baseMap: any) {
    runInAction(() => {
      this._hoverBaseMap = baseMap.item?.name;
    });
  }

  mouseLeaveBaseMap() {
    runInAction(() => {
      this._hoverBaseMap = null;
    });
  }

  @action
  selectViewer(
    viewer: keyof typeof MapViewers,
    event: MouseEvent<HTMLButtonElement>
  ) {
    const mainViewer = this.props.terria.mainViewer;
    event.stopPropagation();
    this.showTerrainOnSide("Both", undefined);
    setViewerMode(viewer, mainViewer);
    // We store the user's chosen viewer mode for future use.
    this.props.terria.setLocalProperty("viewermode", viewer);
    this.props.terria.currentViewer.notifyRepaintRequired();
  }

  @action
  showTerrainOnSide(side: any, event?: MouseEvent<HTMLButtonElement>) {
    event?.stopPropagation();

    switch (side) {
      case "Left":
        this.props.terria.terrainSplitDirection = ImagerySplitDirection.LEFT;
        this.props.terria.showSplitter = true;
        break;
      case "Right":
        this.props.terria.terrainSplitDirection = ImagerySplitDirection.RIGHT;
        this.props.terria.showSplitter = true;
        break;
      case "Both":
        this.props.terria.terrainSplitDirection = ImagerySplitDirection.NONE;
        break;
    }

    this.props.terria.currentViewer.notifyRepaintRequired();
  }

  @action
  toggleDepthTestAgainstTerrainEnabled(event: ChangeEvent<HTMLInputElement>) {
    event.stopPropagation();
    this.props.terria.depthTestAgainstTerrainEnabled = !this.props.terria
      .depthTestAgainstTerrainEnabled;
    this.props.terria.currentViewer.notifyRepaintRequired();
  }

  @action
  toggleCollisionDetection(event: ChangeEvent<HTMLInputElement>) {
    event.stopPropagation();
    runInAction(() => {
      this._enableCollisionDetection = !this._enableCollisionDetection;
      if (this.props.terria.cesium) {
        const cesium = this.props.terria.cesium;
        if (cesium) {
          cesium.scene.screenSpaceCameraController.enableCollisionDetection = this._enableCollisionDetection;
        }
      }
    });
    this.props.terria.currentViewer.notifyRepaintRequired();
  }

  onBaseMaximumScreenSpaceErrorChange(bmsse: number) {
    this.props.terria.setBaseMaximumScreenSpaceError(bmsse);
    this.props.terria.setLocalProperty(
      "baseMaximumScreenSpaceError",
      bmsse.toString()
    );
  }

  toggleUseNativeResolution() {
    this.props.terria.setUseNativeResolution(
      !this.props.terria.useNativeResolution
    );
    this.props.terria.setLocalProperty(
      "useNativeResolution",
      this.props.terria.useNativeResolution
    );
  }

  render() {
    if (!this.props.terria.mainViewer) {
      return null;
    }
    const { t } = this.props;

    const qualityLabels = {
      0: t("settingPanel.qualityLabels.maximumPerformance"),
      1: t("settingPanel.qualityLabels.balancedPerformance"),
      2: t("settingPanel.qualityLabels.lowerPerformance")
    };
    const currentViewer =
      this.props.terria.mainViewer.viewerMode === ViewerMode.Cesium
        ? this.props.terria.mainViewer.viewerOptions.useTerrain
          ? "3d"
          : "3dsmooth"
        : "2d";

    const useNativeResolution = this.props.terria.useNativeResolution;
    const nativeResolutionLabel = t("settingPanel.nativeResolutionLabel", {
      resolution1: useNativeResolution
        ? t("settingPanel.native")
        : t("settingPanel.screen"),
      resolution2: useNativeResolution
        ? t("settingPanel.screen")
        : t("settingPanel.native")
    });
    const dropdownTheme = {
      inner: Styles.dropdownInner,
      icon: "map"
    };

    const isCesiumWithTerrain =
      this.props.terria.mainViewer.viewerMode === ViewerMode.Cesium &&
      this.props.terria.mainViewer.viewerOptions.useTerrain &&
      this.props.terria.currentViewer &&
      this.props.terria.currentViewer instanceof Cesium &&
      this.props.terria.currentViewer.scene &&
      this.props.terria.currentViewer.scene.globe;

    const supportsDepthTestAgainstTerrain = isCesiumWithTerrain;
    const depthTestAgainstTerrainEnabled =
      supportsDepthTestAgainstTerrain &&
      this.props.terria.depthTestAgainstTerrainEnabled;

    const depthTestAgainstTerrainLabel = `クリックして地下を${
      depthTestAgainstTerrainEnabled ? "表示する" : "非表示にする"
    }`;

    const collisionDetectionLabel = `クリックして地下に${
      this._enableCollisionDetection ? "入らない" : "入る"
    }`;

    if (
      this.props.terria.configParameters.useCesiumIonTerrain ||
      this.props.terria.configParameters.cesiumTerrainUrl
    ) {
      MapViewers["3d"].available = true;
    }

    const supportsSide = isCesiumWithTerrain;

    let currentSide = "Both";
    if (supportsSide) {
      switch (this.props.terria.terrainSplitDirection) {
        case ImagerySplitDirection.LEFT:
          currentSide = "Left";
          break;
        case ImagerySplitDirection.RIGHT:
          currentSide = "Right";
          break;
      }
    }

    const timelineStack = this.props.terria.timelineStack;
    const alwaysShowTimeline =
      timelineStack.defaultTimeVarying !== undefined &&
      timelineStack.defaultTimeVarying.startTimeAsJulianDate !== undefined &&
      timelineStack.defaultTimeVarying.stopTimeAsJulianDate !== undefined &&
      timelineStack.defaultTimeVarying.currentTimeAsJulianDate !== undefined;

    const alwaysShowTimelineLabel = alwaysShowTimeline
      ? t("settingPanel.timeline.alwaysShowLabel")
      : t("settingPanel.timeline.hideLabel");

    return (
      //@ts-ignore - not yet ready to tackle tsfying MenuPanel
      <MenuPanel
        theme={dropdownTheme}
        btnRef={this.props.refFromHOC}
        btnTitle={t("settingPanel.btnTitle")}
        btnText={t("settingPanel.btnText")}
        viewState={this.props.viewState}
        smallScreen={this.props.viewState.useSmallScreenInterface}
      >
        <Spacing bottom={5} />
        <Box padded column style={{height:"100px",overflowY:"auto",paddingTop:"0",width:"93%",position:"relative",top:"-20px"}}>
          <>
            <Box column>
              <Box paddedVertically={1}>
                <Text as="label" mini>
                  {this.activeMapName}
                </Text>
              </Box>
              <FlexGrid gap={1} elementsNo={4}>
                {this.props.terria.baseMapsModel.baseMapItems.map(baseMap => (
                  <StyledBasemapButton
                    key={baseMap.item?.uniqueId}
                    isActive={
                      baseMap.item === this.props.terria.mainViewer.baseMap
                    }
                    onClick={event => this.selectBaseMap(baseMap.item, event)}
                    onMouseEnter={this.mouseEnterBaseMap.bind(this, baseMap)}
                    onMouseLeave={this.mouseLeaveBaseMap.bind(this, baseMap)}
                    onFocus={this.mouseEnterBaseMap.bind(this, baseMap)}
                  >
                    {baseMap.item === this.props.terria.mainViewer.baseMap ? (
                      <Box position="absolute" topRight>
                        <StyledIcon
                          light
                          glyph={GLYPHS.selected}
                          styledWidth={"22px"}
                        />
                      </Box>
                    ) : null}
                    <StyledImage
                      fullWidth
                      alt={baseMap.item ? (baseMap.item as any).name : ""}
                      src={baseMap.image}
                    />
                  </StyledBasemapButton>
                ))}
              </FlexGrid>
            </Box>
          </>
        </Box>
      </MenuPanel>
    );
  }
}

export const SETTING_PANEL_NAME = "MenuBarMapSettingsButton";
export default withTranslation()(
  withTheme(withTerriaRef(SettingPanel, SETTING_PANEL_NAME))
);

type IFlexGrid = {
  gap: number;
  elementsNo: number;
};

const FlexGrid = styled(Box).attrs({ flexWrap: true })<IFlexGrid>`
  gap: ${props => props.gap * 5}px;
  > * {
    flex: ${props => `1 0 ${getCalcWidth(props.elementsNo, props.gap)}`};
    max-width: ${props => getCalcWidth(props.elementsNo, props.gap)};
  }
`;
const getCalcWidth = (elementsNo: number, gap: number) =>
  `calc(${100 / elementsNo}% - ${gap * 5}px)`;

type IButtonProps = {
  isActive: boolean;
};

const SettingsButton = styled(Button)<IButtonProps>`
  background-color: ${props =>
    props.isActive ? props.theme.colorPrimary : "#ededed"};
  color: ${props => (props.isActive ? "#fff" : "#444444")};
  border: none;
  border-radius: 20px;
`;

const StyledBasemapButton = styled(RawButton)<IButtonProps>`
  border-radius: 4px;
  position: relative;
  border: 2px solid
    ${props =>
      props.isActive ? props.theme.turquoiseBlue : "rgba(255, 255, 255, 0.5)"};
`;

const StyledImage = styled(Box).attrs({
  as: "img"
})<ComponentProps<"img">>`
  border-radius: inherit;
`;