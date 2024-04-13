import DropdownPanel from "../../Map/Panels/DropdownPanel";
import MobilePanel from "../../Map/Panels/MobilePanel";
import withControlledVisibility from "../../HOCs/withControlledVisibility";
import responsiveSwitch from "./ResponsiveSwitch";

const MenuPanel = withControlledVisibility(
  DropdownPanel
);

export default MenuPanel;
