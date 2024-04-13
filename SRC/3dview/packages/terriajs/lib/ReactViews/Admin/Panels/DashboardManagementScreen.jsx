import { observer } from "mobx-react";
import PropTypes from "prop-types";
import React from "react";
import { withTranslation } from "react-i18next";
import { withTheme } from "styled-components";
import Icon, { StyledIcon } from "../../../Styled/Icon";
import Spacing from "../../../Styled/Spacing";
import Text from "../../../Styled/Text";
import Input from "../../../Styled/Input";
import Box from "../../../Styled/Box";
import Select from "../../../Styled/Select";
import Button, { RawButton } from "../../../Styled/Button";
import { BaseModel } from "../../../Models/Definition/Model";
import Config from "../../../../customconfig.json";
import { action, runInAction } from "mobx";
import { Link } from "react-router-dom";
import Style from "./common.scss";
import {
    MenuLeft,
    Nav,
    ExperimentalMenu
  } from "../../StandardUserInterface/customizable/Groups";
import StandardUserInterface from "../../StandardUserInterface/StandardUserInterface.jsx";
import {setThemeData} from '../../../Models/GraphList/GraphList';
import {setAllThemeInformationByAdmin} from '../../../Models/Theme/Theme';
import GraphListSettingsScreen from './GraphListSettingsScreen.jsx';

/**
 * ダッシュボード管理画面
 * styleは仮で当て込み
 */
@observer
class DashboardManagementScreen extends React.Component {
    static displayName = "DashboardManagementScreen";

    static propTypes = {
        terria: PropTypes.object.isRequired,
        viewState: PropTypes.object.isRequired,
        theme: PropTypes.object,
        t: PropTypes.func.isRequired
    };

    constructor(props) {
        super(props);
        this.state = {
            buttonDisabled:true,
            //subPageNum=0 → ダッシュボード管理画面（デフォルトメイン）
            //subPageNum=1 → グラフリスト一覧画面
            //subPageNum=2 → グラフ・テーブル設定画面
            subPageNum:0

        }
    }

    componentDidMount() {
        this.props.viewState.setShowWelcomeMessage(false);
        setAllThemeInformationByAdmin(this.props.viewState).then((res)=>{
            this.setState({buttonDisabled:false});
        });
    }

    save(){
        try{
            const updateLayout = [...this.props.viewState.updateLayout];
            const themeGraphListFormList = [];
            if(updateLayout.length < 1){
                alert('変更対象のレイアウトが存在しません');
                return;
            }
            for(let i=0;i<updateLayout.length;i++){
                const themeGraphListForm = { themeId:this.props.viewState.selectedThemeId, graphId: updateLayout[i].i, panelHeight: updateLayout[i].h, panelWidth: updateLayout[i].w, topLeftX: updateLayout[i].x,topLeftY:updateLayout[i].y};
                themeGraphListFormList.push(themeGraphListForm);
            }
            if(themeGraphListFormList.length > 0){
                fetch(Config.config.apiUrl + "/graphs/update", {
                    method: 'POST',
                    body: JSON.stringify(themeGraphListFormList),
                    headers: new Headers({ 'Content-type': 'application/json' }),
                })
                .then((res) => res.json())
                .then(res => {
                    if(res.status == 204){
                        alert('更新に成功しました');
                    }else{
                        alert('更新に失敗しました');
                    }
                }).catch(error => {
                    console.error('通信処理に失敗しました', error);
                    alert('通信処理に失敗しました');
                }).finally(() => {
                    runInAction(() => {
                        const themeDataList = this.props.viewState.themeDataList;
                        const index = themeDataList.findIndex(themeData=>themeData.themeId == this.props.viewState.selectedThemeId);
                        if(index > -1){
                            this.setState({buttonDisabled:true});
                            runInAction(() => {
                                this.props.viewState.setDisplayLayout(updateLayout);
                                this.props.viewState.resetUpdateLayout();
                                this.props.viewState.setSelectedWithThemeSwitchItemValue("");
                                this.props.viewState.setThemeData(themeDataList[index]);
                                setThemeData(this.props.viewState).then((res)=>{
                                    this.setState({buttonDisabled:false});
                                });
                            })
                        }
                    })
                });
            }else{
                throw "error";
            }
        }catch(error){
            runInAction(() => {
                const themeDataList = this.props.viewState.themeDataList;
                const index = themeDataList.findIndex(themeData=>themeData.themeId == this.props.viewState.selectedThemeId);
                if(index > -1){
                    this.setState({buttonDisabled:true});
                    runInAction(() => {
                        this.props.viewState.resetUpdateLayout();
                        this.props.viewState.setSelectedWithThemeSwitchItemValue("");
                        this.props.viewState.setThemeData(themeDataList[index]);
                        setThemeData(this.props.viewState).then((res)=>{
                            this.setState({buttonDisabled:false});
                        });
                    })
                }
            })
            console.error('処理に失敗しました', error);
            alert('処理に失敗しました');
        }
    }

    render() {
        const themeDataList = this.props.viewState.themeDataList;
        const selectedThemeGroupName = this.props.viewState.selectedThemeGroupName;
        const selectedTempThemeId = this.props.viewState.selectedTempThemeId;
        const selectedThemeId = this.props.viewState.selectedThemeId;
        let themeGroupNameList = {};
        for(let i=0;i<themeDataList.length;i++){
          if(!themeGroupNameList[themeDataList[i].themeGroupName]){
            themeGroupNameList[themeDataList[i].themeGroupName] = {};
          }
          themeGroupNameList[themeDataList[i].themeGroupName][themeDataList[i].dispOrder] = {themeId:themeDataList[i].themeId,themeName:themeDataList[i].themeName};
        }
        const subPageNum = this.state.subPageNum;
        return (
            <>
                {subPageNum == 0 && (
                    <div style={{height:"80vh",width:"100vw"}}>
                        <div style={{display:"flex",width:"100%",boxSizing:"border-box",padding:"1.042vw 1.563vw",borderBottom:"1px solid #EBEEF7"}}>
                            <Box className={Style.themeItem}>
                                <div style={{display:"flex",width:"100%"}}>
                                    <Box>
                                        <select
                                            onChange={e => {
                                            runInAction(() => {
                                                if(e.target.value && e.target.value != ""){
                                                    this.props.viewState.setThemeGroupName(e.target.value);
                                                }else{
                                                    this.props.viewState.setThemeGroupName("");
                                                    this.props.viewState.setSelectedTempThemeId("");
                                                }
                                            })
                                            if(e.target.value && e.target.value != "" && this.props.viewState.selectedTempThemeId != this.props.viewState.selectedThemeId){
                                                const index = themeDataList.findIndex(themeData=>themeData.themeId == this.props.viewState.selectedTempThemeId);
                                                if(index > -1){
                                                    this.setState({buttonDisabled:true});
                                                    runInAction(() => {
                                                        this.props.viewState.resetUpdateLayout();
                                                        this.props.viewState.setSelectedWithThemeSwitchItemValue("");
                                                        this.props.viewState.setThemeData(themeDataList[index]);
                                                        setThemeData(this.props.viewState).then((res)=>{
                                                            this.setState({buttonDisabled:false});
                                                        });
                                                    })
                                                }
                                            }
                                            }}
                                            >
                                            <option value=""></option>
                                            {Object.keys(themeGroupNameList).map(key => (
                                                <option value={key} selected={key == selectedThemeGroupName}>{key}</option>
                                            ))}
                                        </select>
                                    </Box>
                                    <Box>
                                        <select
                                            onChange={e => {
                                            runInAction(() => {
                                                if(e.target.value && e.target.value != ""){
                                                    this.props.viewState.setSelectedTempThemeId(e.target.value);
                                                }else{
                                                    this.props.viewState.setSelectedTempThemeId("");
                                                }
                                            })
                                            if(e.target.value && e.target.value != "" && this.props.viewState.selectedTempThemeId != this.props.viewState.selectedThemeId){
                                                const index = themeDataList.findIndex(themeData=>themeData.themeId == this.props.viewState.selectedTempThemeId);
                                                if(index > -1){
                                                    this.setState({buttonDisabled:true});
                                                    runInAction(() => {
                                                        this.props.viewState.resetUpdateLayout();
                                                        this.props.viewState.setSelectedWithThemeSwitchItemValue("");
                                                        this.props.viewState.setThemeData(themeDataList[index]);
                                                        setThemeData(this.props.viewState).then((res)=>{
                                                            this.setState({buttonDisabled:false});
                                                        });
                                                    })
                                                }
                                            }
                                            }}
                                            >
                                            <option value=""></option>
                                            {themeGroupNameList[selectedThemeGroupName] && Object.keys(themeGroupNameList[selectedThemeGroupName]).map(key => (
                                                <option value={themeGroupNameList[selectedThemeGroupName][key].themeId} selected={themeGroupNameList[selectedThemeGroupName][key].themeId==selectedThemeId}>{themeGroupNameList[selectedThemeGroupName][key].themeName}</option>
                                            ))}
                                        </select>
                                    </Box>
                                </div>
                            </Box>
                            <Box col4 flex={true} className={Style.themeItem}>
                                <div style={{display:"flex",width:"100%"}}>
                                    <Box>
                                        <button
                                            onClick={evt => {
                                                this.setState({subPageNum:1});
                                            }}
                                            disabled={this.state.buttonDisabled}
                                            css={`
                                                ${this.state.buttonDisabled &&
                                                `opacity:.7;`
                                                }
                                            `}
                                        >
                                            <span>グラフ・リスト一覧</span>
                                        </button>
                                    </Box>
                                    <Box>
                                        <button
                                            className={Style.saveButton}
                                            onClick={evt => {
                                                this.save();
                                            }}
                                            disabled={this.state.buttonDisabled}
                                            css={`
                                                ${this.state.buttonDisabled &&
                                                `opacity:.7;`
                                                }
                                            `}
                                        >
                                            <span>保存</span>
                                        </button>
                                    </Box>
                                </div>
                            </Box>
                        </div>
                        <Spacing bottom={4} />
                        <StandardUserInterface {...this.props} dashboardManagementScreenFlag={true}>
                            <MenuLeft></MenuLeft>
                            <Nav></Nav>
                            <ExperimentalMenu></ExperimentalMenu>
                        </StandardUserInterface>
                    </div>
                )}
                {subPageNum == 1 && (
                    <GraphListSettingsScreen {...this.props} backFunc={()=>{
                        this.props.viewState.setAdminPageTitle("ダッシュボード管理画面");
                        this.setState({subPageNum:0})}
                    } />
                )}
            </>
        );
    }
}

export default withTranslation()(withTheme(DashboardManagementScreen));
