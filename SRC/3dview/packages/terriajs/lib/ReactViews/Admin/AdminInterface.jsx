import { observer } from "mobx-react";
import PropTypes from "prop-types";
import React from "react";
import { withTranslation } from "react-i18next";
import { withTheme } from "styled-components";
import Icon, { StyledIcon } from "../../Styled/Icon";
import Spacing from "../../Styled/Spacing";
import Text from "../../Styled/Text";
import Input from "../../Styled/Input";
import Box from "../../Styled/Box";
import Select from "../../Styled/Select";
import Button, { RawButton } from "../../Styled/Button";
import { BaseModel } from "../../Models/Definition/Model";
import Config from "../../../customconfig.json";
import { action, runInAction } from "mobx";
import { Link } from "react-router-dom";
import Style from "./admin-interface.scss";
import DashboardManagementScreen from "./Panels/DashboardManagementScreen";
import PostSettingsScreen from "./Panels/PostSettingsScreen";
import AdminLogin from "./AdminLogin";
import ThemeLayerManagementScreen from "./Panels/ThemeLayerManagementScreen/ThemeLayerManagementScreen";
import UserManagementScreen from "./Panels/UserManagementScreen/UserManagementScreen";
import {
    MenuLeft,
    Nav,
    ExperimentalMenu
  } from "../StandardUserInterface/customizable/Groups";
import {highlightGraphAndFilterList,setThemeData} from '../../Models/GraphList/GraphList';
import {setAllThemeInformation} from '../../Models/Theme/Theme';
import RegionalStatisticsManagementScreen from "./Panels/RegionalStatisticsManagementScreen"

/**
 * 管理者画面Interface
 * styleは仮で当て込み
 */
@observer
class AdminInterface extends React.Component {
    static displayName = "AdminInterface";

    static propTypes = {
        terria: PropTypes.object.isRequired,
        viewState: PropTypes.object.isRequired,
        theme: PropTypes.object,
        t: PropTypes.func.isRequired
    };

    constructor(props) {
        super(props);
    }

    componentDidMount() {        
        //認証チェック用API等に問い合わせ、ログイン画面を初期表示するかな否かの処理を追加
        //this.props.viewState.setAdminPageNum(0) がログイン画面
        this.authenticationCheck();
        
    }

    authenticationCheck(){
        let pageNum = 0;
        const apiUrl = Config.config.apiUrl + "/auth/checkAdminAuth";
        fetch(apiUrl)
        .then(res => {
            if(res.status != 200){
                runInAction(() => {
                    this.props.viewState.setAdminPageNum(0);
                });
            }else{
                pageNum = 1;
                let hashString = window.location.hash;
                if(hashString.indexOf("#pageNum=") > -1){
                    let hashArray = hashString.split("#")[1].split("&");
                    let searchParams = {};
                    if(hashArray.length > 0){
                        for(let i = 0; i < hashArray.length; i++){
                        let tempArray =  hashArray[i].split("=");
                        if(tempArray[0] && tempArray[1]){
                            searchParams[tempArray[0]] = tempArray[1];
                        }
                        }
                        if(searchParams["pageNum"]){
                            pageNum = Number(searchParams["pageNum"]);
                        }
                    }
                }
                runInAction(() => {
                    this.props.viewState.setAdminPageNum(pageNum);
                });
            }
        })
    }

    componentWillUnmount(){
        //ブラウザバック対応の再認証
        window.addEventListener('popstate', () => {
            this.authenticationCheck();
        }, {once: true});
        setAllThemeInformation(this.props.viewState).then((res)=>{
            const themeDataList = this.props.viewState.themeDataList;
            const index = themeDataList.findIndex(themeData=>themeData.themeId == themeDataList[0].themeId);
            if(index > -1){
                runInAction(() => {
                    this.props.viewState.setPostSttingsCurrentParentId(0);
                    this.props.terria.setDashboardManagementScreenFlag(false);
                    this.props.viewState.clearLinkedFeatureId();
                    this.props.terria.setDashboardManagementScreenFlag(false);
                    this.props.viewState.setSelectedWithThemeSwitchItemValue("");
                    this.props.viewState.setThemeData(themeDataList[index]);
                    setThemeData(this.props.viewState);
                })
            }
        });
    }

    /**
     * ページ切替
     * @param pageNum ページ番号
     */
    pageSwitching(pageNum){
        runInAction(() => {
            if(pageNum != this.props.viewState.adminPageNum){
                this.props.viewState.setAdminPageNum(pageNum);
            }
        });
    }

    logout(){
        fetch(Config.config.apiUrl + "/auth/logout")
        .then(res => {
        //ログアウト後及びログイン後は権限が変わる為テーマデータを再読み込み
        runInAction(() => {
            this.props.terria.setName("");
            this.props.terria.setPermission("");
            this.props.terria.setUserId(-1);
            //テーマ内切替項目を一旦リセット
            this.props.viewState.setSelectedWithThemeSwitchItemValue("");
            //現在のテーマデータを再セットしてグラフリストとレイヤを再読み込み
            setThemeData(this.props.viewState);
            this.props.viewState.setAdminPageNum(0);
        })
        }).catch(error => {
        console.error('通信処理に失敗しました', error);
        alert('通信処理に失敗しました');
        });
    }

    render() {
        const adminPageNum = this.props.viewState.adminPageNum;
        return (
            <div style={{height:"100%",backgroundColor:"#f6f6f6"}}>
                {/** adminPageNumが1以上の場合は管理画面 */}
                {adminPageNum > 0 && (
                    <>
                        <Box col12 className={Style.header}>
                                    <div className={Style.logo}>
                                    <img src={Config.config.plateauPath + "/sample/logo.png"}/>
                                    </div>
                                    <h1>{this.props.viewState.adminPageTitle}</h1>
                       
                                    <div className={Style.sampleLogout}>
                                        <img src={Config.config.plateauPath + "/sample/sample-logout.png"} className={Style.sampleLogoutIcon} style={{cursor:"pointer"}} onClick={()=>{this.logout();}} />
                                        
                                        ログアウト
                                    </div>
    
                        </Box>
                        <Box col12 style={{height:"88%"}}>
                            <Box className={Style.sideBar}>
                                <ul className={Style.menuUl}>
                                    <li onClick={(e)=>{this.pageSwitching(1)}} className={adminPageNum == 1 ? Style.liActive:""}><img src={adminPageNum == 1 ? Config.config.plateauPath + "/sample/sample-icon11.png":Config.config.plateauPath + "/sample/sample-icon1.png"} className={Style.sampleIcon}/>テーマ・レイヤ公開管理</li>
                                    <li onClick={(e)=>{this.pageSwitching(2)}} className={adminPageNum == 2 ? Style.liActive:""}><img src={adminPageNum == 2 ? Config.config.plateauPath + "/sample/sample-icon12.png":Config.config.plateauPath + "/sample/sample-icon2.png"} className={Style.sampleIcon}/>ダッシュボード管理</li>
                                    <li onClick={(e)=>{this.pageSwitching(3)}} className={adminPageNum == 3 ? Style.liActive:""}><img src={adminPageNum == 3 ? Config.config.plateauPath + "/sample/sample-icon13.png":Config.config.plateauPath + "/sample/sample-icon3.png"} className={Style.sampleIcon}/>活動・投稿情報管理</li>
                                    <li onClick={(e)=>{this.pageSwitching(4)}} className={adminPageNum == 4 ? Style.liActive:""}><img src={adminPageNum == 4 ? Config.config.plateauPath + "/sample/sample-icon14.png":Config.config.plateauPath + "/sample/sample-icon4.png"} className={Style.sampleIcon}/>地域統計・回遊性情報管理</li>
                                    <li onClick={(e)=>{this.pageSwitching(5)}} className={adminPageNum == 5 ? Style.liActive:""}><img src={adminPageNum == 5 ? Config.config.plateauPath + "/sample/sample-icon15.png":Config.config.plateauPath + "/sample/sample-icon5.png"} className={Style.sampleIcon}/>ユーザ管理</li>
                                    <li onClick={(e)=>{
                                        if(window.confirm("公開サイトを表示しますか？")){
                                            window.open(Config.config.plateauPath + '/','plateauwindow');
                                        }
                                    }}><img src={Config.config.plateauPath + "/sample/sample-icon6.png"} className={Style.sampleIcon} />公開サイト</li>
                                </ul>
                            </Box>
                            <Box className={Style.mainContent}>
                                {/** ここでadminPageNumに応じて各コンポーネントを切り替える */}
                                {adminPageNum == 1 && (
                                    <ThemeLayerManagementScreen terria={this.props.terria} viewState={this.props.viewState}/>
                                )}
                                {adminPageNum == 2 && (
                                    <DashboardManagementScreen terria={this.props.terria} viewState={this.props.viewState} allBaseMaps={this.props.allBaseMaps} themeOverrides={this.props.Variables}/>
                                )}
                                {adminPageNum == 3 && (
                                    <PostSettingsScreen terria={this.props.terria} viewState={this.props.viewState} allBaseMaps={this.props.allBaseMaps} themeOverrides={this.props.Variables}>
                                        <MenuLeft></MenuLeft>
                                        <Nav></Nav>
                                        <ExperimentalMenu></ExperimentalMenu>
                                    </PostSettingsScreen>
                                )}
                                {adminPageNum == 4 && (
                                    <RegionalStatisticsManagementScreen terria={this.props.terria} viewState={this.props.viewState}/>
                                )}
                                {adminPageNum == 5 && (
                                    <UserManagementScreen terria={this.props.terria} viewState={this.props.viewState}/>
                                )}
                            </Box>
                        </Box>
                    </>
                )}
                {adminPageNum == 0 && (
                    <AdminLogin terria={this.props.terria} viewState={this.props.viewState} />
                )}
            </div>
        );
    }
}

export default withTranslation()(withTheme(AdminInterface));
