import { TFunction } from "i18next";
import { debounce } from "lodash-es";
import {
  action,
  computed,
  IReactionDisposer,
  observable,
  reaction,
  runInAction
} from "mobx";
import { observer } from "mobx-react";
import React from "react";
import { WithTranslation, withTranslation } from "react-i18next";
import styled, { DefaultTheme, withTheme } from "styled-components";
import isDefined from "../../../Core/isDefined";
import ViewState from "../../../ReactViewModels/ViewState";
import Terria from "../../../Models/Terria";
import Box from "../../../Styled/Box";
import Spacing from "../../../Styled/Spacing";
import Icon, { GLYPHS ,StyledIcon} from "../../../Styled/Icon";
import MapNavigationModel, {
  IMapNavigationItem,
  OVERFLOW_ITEM_ID
} from "../../../ViewModels/MapNavigation/MapNavigationModel";
import withControlledVisibility from "../../HOCs/withControlledVisibility";
import MapIconButton from "../../MapIconButton/MapIconButton";
import { Control, MapNavigationItem } from "./Items/MapNavigationItem";
import { registerMapNavigations } from "./registerMapNavigations";
import RouteButton from "../RouteButton/RouteButton";
import SearchButton from "../SearchButton/SearchButton";
import ActivityButton from "../ActivityButton/ActivityButton";
import { Small, Medium } from "../../Generic/Responsive";
import SettingPanel from "../Panels/SettingPanel";

const OVERFLOW_ACTION_SIZE = 42;

interface StyledMapNavigationProps {
  trainerBarVisible: boolean;
  theme: DefaultTheme;
}

/**
 * TODO: fix this so that we don't need to override pointer events like this.
 * a fix would look like breaking up the top and bottom parts, so there is
 * no element "drawn/painted" between the top and bottom parts of map
 * navigation
 */
const StyledMapNavigation = styled.div<StyledMapNavigationProps>`
  position: absolute;
  right: 5px;
  z-index: 9;
  top: 100px;
  @media (min-width: ${props => props.theme.sm}px) {
    top: 25px;
    bottom: 50px;
    right: 25px;
  }
  @media (max-width: ${props => props.theme.mobile}px) {
    & > div {
      flex-direction: row;
    }
  }
  pointer-events: none;

  button {
    pointer-events: auto;
  }

  ${p =>
    p.trainerBarVisible &&
    `
    top: ${Number(p.theme.trainerHeight) + Number(p.theme.mapNavigationTop)}px;
  `}
`;

const ControlWrapper = styled(Box)`
  @media (min-width: ${props => props.theme.sm}px) {
    & > :first-child {
      margin-top: 0 !important;
      padding-top: 0 !important;
    }
  }
`;

interface PropTypes extends WithTranslation {
  viewState: ViewState;
  terria: Terria;
  theme: DefaultTheme;
  t: TFunction;
  navItems: any[];
}

enum Orientation {
  HORIZONTAL,
  VERTICAL
}

@observer
class MapNavigation extends React.Component<PropTypes> {
  static displayName = "MapNavigation";
  private navigationRef = React.createRef<HTMLDivElement>();
  private readonly resizeListener: () => any;
  private readonly viewState: ViewState;
  private readonly terria: Terria;
  private itemSizeInBar: Map<string, number>;
  @observable private model: MapNavigationModel;
  @observable private overflows: boolean;
  private viewerModeReactionDisposer: IReactionDisposer | undefined;

  constructor(props: PropTypes) {
    super(props);
    registerMapNavigations(props.viewState);
    this.viewState = props.viewState;
    this.terria = props.terria;
    this.model = props.viewState.terria.mapNavigationModel;
    this.resizeListener = debounce(() => this.updateNavigation(), 250);
    this.itemSizeInBar = new Map<string, number>();
    this.computeSizes();
    this.overflows = runInAction(() =>
      this.model.visibleItems.some(item => item.controller.collapsed)
    );
    this.viewerModeReactionDisposer = reaction(
      () => this.viewState.terria.currentViewer,
      () => this.updateNavigation(),
      {
        equals: (a, b) => {
          return a === b;
        }
      }
    );
  }

  componentDidMount() {
    this.computeSizes();
    this.updateNavigation();
    window.addEventListener("resize", this.resizeListener, false);
  }

  componentWillUnmount() {
    window.removeEventListener("resize", this.resizeListener);
    if (this.viewerModeReactionDisposer) {
      this.viewerModeReactionDisposer();
    }
  }

  @computed
  get orientation(): Orientation {
    return Orientation.VERTICAL;
  }

  @action
  private computeSizes(items?: IMapNavigationItem[]): void {
    (items ?? this.model.visibleItems).forEach(item => {
      if (this.orientation === Orientation.VERTICAL) {
        if (item.controller.height && item.controller.height > 0) {
          this.itemSizeInBar.set(item.id, item.controller.height || 42);
        }
      } else {
        if (item.controller.width && item.controller.width > 0) {
          this.itemSizeInBar.set(item.id, item.controller.width || 42);
        }
      }
    });
  }

  /**
   * Check if we need to collapse navigation items and determine which one need to be collapsed.
   */
  @action
  private updateNavigation(): void {
    if (!this.navigationRef.current) {
      // navigation bar has not been rendered yet so there is nothing to update.
      return;
    }
    if (this.computeSizes.length !== this.model.visibleItems.length) {
      this.computeSizes();
    }
    let itemsToShow = this.model.visibleItems.filter(item =>
      filterViewerAndScreenSize(item, this.viewState)
    );
    // items we have to show in the navigation bar
    let pinnedItems = this.model.pinnedItems.filter(item =>
      filterViewerAndScreenSize(item, this.viewState)
    );
    // items that are possible to be collapsed
    let possibleToCollapse = itemsToShow
      .filter(
        item => !pinnedItems.some(pinnedItem => pinnedItem.id === item.id)
      )
      .reverse();

    // Ensure we are not showing more composites than we have height for
    let overflows = false;
    let maxVisible = itemsToShow.length;
    let size = 0;
    if (this.overflows) {
      size += OVERFLOW_ACTION_SIZE;
    }
    const limit =
      this.orientation === Orientation.VERTICAL
        ? this.navigationRef.current.clientHeight
        : this.navigationRef.current.parentElement?.parentElement
        ? this.navigationRef.current.parentElement?.parentElement?.clientWidth -
          100
        : this.navigationRef.current.clientWidth;

    for (let i = 0; i < itemsToShow.length; i++) {
      size += this.itemSizeInBar.get(itemsToShow[i].id) || 0;
      if (size <= limit) {
        maxVisible = i + 1;
      }
    }

    if (pinnedItems.length > maxVisible) {
      possibleToCollapse.forEach(item => {
        this.model.setCollapsed(item.id, true);
      });
      //there is nothing else we can do, we have to show the rest of items as it is.
      return;
    }
    overflows = itemsToShow.length > maxVisible;
    const itemsToCollapseId: string[] = [];
    const activeCollapsible: string[] = [];
    if (overflows) {
      if (!this.overflows) {
        // overflow is not currently visible so add its height here
        size += OVERFLOW_ACTION_SIZE;
        this.overflows = true;
      }
      maxVisible = maxVisible - pinnedItems.length;
      // first try to collapse inactive items and then active ones if needed
      for (let i = 0; i < possibleToCollapse.length; i++) {
        const item = possibleToCollapse[i];
        if (item.controller.active) {
          activeCollapsible.push(item.id);
          continue;
        }
        itemsToCollapseId.push(item.id);
        size -= this.itemSizeInBar.get(item.id) || 0;
        if (size <= limit) {
          break;
        }
      }
      if (size > limit) {
        for (let i = 0; i < activeCollapsible.length; i++) {
          const itemId = activeCollapsible[i];
          itemsToCollapseId.push(itemId);
          size -= this.itemSizeInBar.get(itemId) || 0;
          if (size <= limit) {
            break;
          }
        }
      }
    } else {
      this.overflows = false;
    }

    this.model.visibleItems.forEach(item => {
      if (itemsToCollapseId.includes(item.id)) {
        this.model.setCollapsed(item.id, true);
      } else {
        this.model.setCollapsed(item.id, false);
      }
    });
  }

  toggleView(viewname:any) {
    runInAction(() => {
      if (this.props.viewState.mobileView !== viewname) {
        this.props.viewState.explorerPanelIsVisible = true;
        this.props.viewState.switchMobileView(viewname);
      } else {
        this.props.viewState.explorerPanelIsVisible = false;
        this.props.viewState.switchMobileView(null);
      }
    });
  }

  render() {
    const { viewState, t } = this.props;
    const terria = viewState.terria;
    let items = terria.mapNavigationModel.visibleItems.filter(
      item =>
        !item.controller.collapsed &&
        filterViewerAndScreenSize(item, this.viewState)
    );
    let bottomItems: IMapNavigationItem[] | undefined;
    
    if (!this.overflows && this.orientation !== Orientation.HORIZONTAL) {
      bottomItems = items.filter(item => item.location === "BOTTOM");
      items = items.filter(item => item.location === "TOP");
    }

    items = items.filter(item => ["measure-tool"].indexOf(item.id) < 0);
    const themeLayerList = [...this.terria.themeLayerList];
    const themeData = viewState.themeData;
    let postAble:any = "-1";
    if(themeLayerList && themeData){
      if(themeData.postFlag == 1){
        postAble = themeLayerList.findIndex((themeLayer:any)=>(themeLayer.layerType == 1 || themeLayer.layerType == 2) && themeLayer.postFlag == 1);
      }
    }
    return (
      <StyledMapNavigation trainerBarVisible={viewState.trainerBarVisible}>
        <Box
          centered
          column
          justifySpaceBetween
          fullHeight
          alignItemsFlexEnd
          ref={this.navigationRef}
          className="z_mapbutton2"
        >
          <ControlWrapper
            column={this.orientation === Orientation.VERTICAL}
            className="active"
            css={`
              ${this.orientation === Orientation.HORIZONTAL &&
                `margin-bottom: 5px;
                flex-wrap: wrap;`}
            `}
          >
            { /*items.map(item => {
              // Do not expand in place for horizontal orientation
              // as it results in buttons overlapping and hiding neighboring buttons.
              if (
                item.id === "split-tool" &&
                terria.configParameters.disableSplitter
              ) {
                return null;
              }
              return (
                <MapNavigationItem
                  expandInPlace={this.orientation !== Orientation.HORIZONTAL}
                  key={item.id}
                  item={item}
                  terria={terria}
                />
              );
            }) */}
                { items.map(item => {
                  if (
                    item.id != "compass") {
                    return null;
                  }
                  return (
                    <MapNavigationItem
                      expandInPlace={this.orientation !== Orientation.HORIZONTAL}
                      key={item.id}
                      item={item}
                      terria={terria}
                    />
                  );
                })}
                <Spacing bottom={2} />
                { items.map(item => {
                  if (
                    item.id != "my-location") {
                    return null;
                  }
                  return (
                    <MapNavigationItem
                      expandInPlace={this.orientation !== Orientation.HORIZONTAL}
                      key={item.id}
                      item={item}
                      terria={terria}
                    />
                  );
                })}
                <Control>
                  <div className="a">
                    <SettingPanel terria={this.props.terria} viewState={this.props.viewState} />
                  </div>
                </Control>
                <Control>
                <div className="a">
                  <button
                    type="button"
                    id="toggle-workbench"
                    aria-label={
                      viewState.isMapFullScreen
                        ? "凡例を表示"
                        : "凡例を非表示"
                    }
                    onClick={() =>
                      runInAction(() => {
                        if(!viewState.useSmallScreenInterface){
                          viewState.setIsMapFullScreen(
                            !viewState.isMapFullScreen
                          )
                        }else{
                          viewState.setTopElement("NowViewing");
                          this.toggleView(viewState.mobileViewOptions.nowViewing);
                        }
                      })
                    }
                    title={
                      viewState.isMapFullScreen
                        ? "凡例を表示"
                        : "凡例を非表示"
                    }
                    style={{
                      background:"#2AAE7A",
                      cursor: "pointer",
                      border:"none",
                      padding:"8px",
                    }}
                  >
                    <StyledIcon
                        glyph={Icon.GLYPHS.mapmenu}
                        styledWidth={"20px"}
                      />
                  </button>
                  </div>
                </Control>
                <Control>
                <div className="a">
                  <SearchButton
                    viewState={this.viewState}
                  />
                 </div>
                </Control>
                <Control>
                <div className="a">
                  <RouteButton
                    viewState={this.viewState}
                  />
                 </div>
                </Control>
                {this.terria.userId > 0 && postAble != -1 && (
                <Control>
                    <div className="a">
                  <ActivityButton
                    viewState={this.viewState}
                  />
                </div>
                </Control>
                )}      
          </ControlWrapper>
          <ControlWrapper column={this.orientation === Orientation.VERTICAL}>
            {/*bottomItems?.map(item => (
              <MapNavigationItem key={item.id} item={item} terria={terria} />
            ))*/}
          </ControlWrapper>
        </Box>
      </StyledMapNavigation>
    );
  }
}

export default withTranslation()(
  withTheme(withControlledVisibility(MapNavigation))
);

export function filterViewerAndScreenSize(
  item: IMapNavigationItem,
  viewState: ViewState
) {
  const currentViewer = viewState.terria.mainViewer.viewerMode;
  if (viewState.useSmallScreenInterface) {
    return (
      (!isDefined(item.controller.viewerMode) ||
        item.controller.viewerMode === currentViewer) &&
      (!isDefined(item.screenSize) || item.screenSize === "small")
    );
  } else {
    return (
      (!isDefined(item.controller.viewerMode) ||
        item.controller.viewerMode === currentViewer) &&
      (!isDefined(item.screenSize) || item.screenSize === "medium")
    );
  }
}
