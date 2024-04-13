import { observer } from "mobx-react";
import PropTypes from "prop-types";
import React from "react";
import { withTranslation } from "react-i18next";
import { withTheme } from "styled-components";
import Icon, { StyledIcon } from "../../../../Styled/Icon";
import Spacing from "../../../../Styled/Spacing";
import Text from "../../../../Styled/Text";
import Input from "../../../../Styled/Input";
import Box from "../../../../Styled/Box";
import Button, { RawButton } from "../../../../Styled/Button";

/**
 * お知らせ画面 ドラッグで画面位置操作可能に TODO:style
 */
@observer
class InformationPanel extends React.Component {
    static displayName = "InformationPanel";

    static propTypes = {
        terria: PropTypes.object.isRequired,
        viewState: PropTypes.object.isRequired,
        theme: PropTypes.object,
        t: PropTypes.func.isRequired
    };

    constructor(props) {
        super(props);
        this.state = {
            viewState: props.viewState,
            terria: props.terria,
            diffX: 0,
            diffY: 0,
            dragging: false,
            styles: {}
        };
        //this._dragStart = this._dragStart.bind(this);
        //this._dragging = this._dragging.bind(this);
        //this._dragEnd = this._dragEnd.bind(this);
    }

    componentDidMount() {
       /* const { innerWidth: width, innerHeight: height } = window;
        if(width <= 769){
            this.setState({
                styles: {
                    left: "0",
                    top: "10%"
                }
            }); 
        }else{
            this.setState({
                styles: {
                    left: "30%",
                    top: "10%",
                    maxWidth:"400px"
                }
            });
        }
        */
    }

    _dragStart(e) {
        this.setState({
            diffX: e.screenX - e.currentTarget.getBoundingClientRect().left,
            diffY: e.screenY - e.currentTarget.getBoundingClientRect().top,
            dragging: true
        });
    }

    _dragging(e) {
        const { innerWidth: width, innerHeight: height } = window;
        if(this.state.dragging) {
            var left = e.screenX - this.state.diffX;
            var top = e.screenY - this.state.diffY;
            if(width <= 769){
                this.setState({
                    styles: {
                        left: left,
                        top: top
                    }
                }); 
            }else{
                this.setState({
                    styles: {
                        left: left,
                        top: top,
                        maxWidth:"400px"
                    }
                });
            }
        }
    }

    _dragEnd() {
        this.setState({
            dragging: false
        });
    }

    render() {
        let informationText = this.props.viewState.themeData?.informationText;
        if(!informationText){
            informationText = "";
        }
        /*
        let maxHeight = 550;
        if(document.getElementById("viewerGridLayout")){
            if(document.getElementById("viewerGridLayout").offsetHeight != undefined && document.getElementById("viewerGridLayout").offsetHeight != null
            && maxHeight > document.getElementById("viewerGridLayout").offsetHeight){
                maxHeight = document.getElementById("viewerGridLayout").offsetHeight;
            }
        }
        */
        //下記は一時的なサンプル表示用（実際は削除）
        //informationText = '<h2>lorem ipsum</h2><p><a href="https://ja.wikipedia.org/wiki/Lorem_ipsum">Lorem ipsum</a> dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p><h3>lorem ipsum</h3><ul><li>Lorem ipsum dolor sit amet, consectetuer adipiscing elit.</li><li>Aliquam tincidunt mauris eu risus.</li><li>Vestibulum auctor dapibus neque.</li></ul><h3>lorem ipsum</h3><p><a href="https://ja.wikipedia.org/wiki/Lorem_ipsum">Lorem ipsum</a> dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui officia deserunt mollit anim id est laborum.</p>';
        return (
            <Box
                displayInlineBlock
                backgroundColor={this.props.theme.textLight}
                fullHeight
                overflow={"hidden"}
                onClick={() => this.props.viewState.setTopElement("InformationPanel")}
                css={`
                position: absolute;
                top:0;
                left:0;
                padding-right:5px;
                padding-left:5px;
                z-index: ${this.props.viewState.topElement === "InformationPanel"
                                ? 99999
                                : 99};
                width:35%;
                height:100%;
                `}
                style={this.state.styles} 
                //onMouseDown={this._dragStart}
                //onMouseMove={this._dragging}
                //onMouseUp={this._dragEnd}
            >
                     <Box position="absolute" paddedRatio={3} topRight>
                        <RawButton onClick={() => {
                            this.props.viewState.hideInformationPanel();
                        }}>
                            <StyledIcon
                                styledWidth={"16px"}
                                fillColor={"#000"}
                                opacity={"0.5"}
                                glyph={Icon.GLYPHS.closeLight}
                                css={`
                                    cursor:pointer;
                                `}
                            />
                        </RawButton>
                    </Box>
                    <Box style={{position:"relative",top:"5%",padding:"10px",width:"100%",height:"100%"}} overflowY={"auto"}>
                        <span dangerouslySetInnerHTML={{ __html: informationText }}></span>
                    </Box>
            </Box >
        );
    }
}

export default withTranslation()(withTheme(InformationPanel));
