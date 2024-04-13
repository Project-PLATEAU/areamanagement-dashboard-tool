import { observer } from "mobx-react";
import PropTypes from "prop-types";
import React from "react";
import { withTranslation } from "react-i18next";
import { ThemeProvider, createGlobalStyle } from "styled-components";
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
import classNames from "classnames";
import Style from "./common.scss";
import StandardUserInterfaceStyle from "../../StandardUserInterface/standard-user-interface.scss";
import { terriaTheme } from "../../StandardUserInterface/StandardTheme";
import {
    MenuLeft,
    Nav,
    ExperimentalMenu
  } from "../../StandardUserInterface/customizable/Groups";
import Viewer from "../../StandardUserInterface/Viewer.jsx";
import processCustomElements from "../../StandardUserInterface/processCustomElements";
import FeatureInfoPanel from "../../FeatureInfo/FeatureInfoPanel";
import Table from './Table.jsx';
import {highlightGraphAndFilterList,setThemeData} from '../../../Models/GraphList/GraphList';


/**
 * 投稿情報管理画面
 * styleは仮で当て込み
 */
@observer
class PostSettingsScreen extends React.Component {
    static displayName = "PostSettingsScreen";

    static propTypes = {
        terria: PropTypes.object.isRequired,
        viewState: PropTypes.object.isRequired,
        theme: PropTypes.object,
        t: PropTypes.func.isRequired
    };

    constructor(props) {
        super(props);
        this.state = {
            //エリマネ活動・イベント活動の場合は1
            //TODO:投稿レイヤの場合は2
            postType:1,
            tableData:[],
            tableColumns:[],
            historyTableData:[],
            historyTableColumns:[],
            currentUniqueId:"",
            activityTypeList:[],
            groupTypeList:[],
            activityFormList:[],
            updateActivityFormList:[],
            userFormList:[],
            themeLayerList:[],
            postLayerLayerId:-1,
            postLayerAttributeFormList:[],
            postLayerFeatureFormList:[],
            updatePostLayerFeatureFormList:[],
            startPostDateAndTime:"",
            endPostDateAndTime:"",
            orderMode:"1"
        }
    }

    componentDidMount() {
        runInAction(() => {
            this.props.viewState.featureInfoPanelIsVisible = false;
            // give the close animation time to finish before unselecting, to avoid jumpiness
            setTimeout(
            action(() => {
                this.props.terria.pickedFeatures = undefined;
                this.props.terria.selectedFeature = undefined;
            }),
            200
            );
            this.props.viewState.setPostSttingsCurrentParentId(0);
            this.props.terria.setDashboardManagementScreenFlag(true);
        })
        runInAction(() => {
            const items = this.props.terria.workbench.items;
            for (const aItem of items) {
                runInAction(() => {
                    this.props.terria.workbench.remove(aItem);
                })
            }
            this.props.terria.customAdminLoadInitSource().then(res=>{
                const item = this.props.terria.getModelById(BaseModel, Config.layerUniqueIdCorrespondenceTable.activity);
                if(item){
                    item.forceLoadMapItems().then((res) =>{
                        item.loadMapItems();
                        this.props.terria.workbench.add(item);
                    }).catch((e) => {
                      try{
                        this.props.terria.workbench.add(item);
                      }catch(e){}
                  });
                }
                fetch(Config.config.apiUrl + "/user/all")
                .then(res => res.json())
                .then(res => {
                    //ユーザ情報取得
                    if(res != null && res.length > 0){
                        this.setState({
                            userFormList: res
                        });
                    }
                    //初期は活動情報を取得
                    fetch(Config.config.apiUrl + "/activity/type")
                    .then(res => res.json())
                    .then(res => {
                        if (res.activityTypeList && res.groupTypeList) {
                            this.setState({
                                activityTypeList: res.activityTypeList,
                                groupTypeList: res.groupTypeList
                            });
                            if(res.activityTypeList[0]){
                                fetch(Config.config.apiUrl + "/activity/all/"+Number(res.activityTypeList[0].id))
                                .then((res) => res.json())
                                .then(res => {
                                    if(res!=undefined && res != null){
                                        this.setState({
                                            activityFormList: res,
                                            currentUniqueId:Config.layerUniqueIdCorrespondenceTable.activity
                                        }, () => {
                                            this.generateActivityTableData();
                                            this.generateActivityHistoryTableData();
                                            this.props.viewState.setPostSttingsAttributePanaleAfterEvent(()=>{this.generateActivityHistoryTableData()});
                                            const themeLayerList = [];
                                            const naturalThemeLayerList = [...this.props.terria.themeLayerList];
                                            Object.keys(Config.layerUniqueIdCorrespondenceTable).map(key=>{
                                                let index = naturalThemeLayerList.findIndex(themeLayer=>themeLayer.uniqueId == Config.layerUniqueIdCorrespondenceTable[key]);
                                                if(index > -1){
                                                    themeLayerList.push(naturalThemeLayerList[index]);
                                                }
                                            })
                                            this.setState({
                                                themeLayerList:themeLayerList
                                            });
                                        });
                                    }else{
                                        alert('取得に失敗しました');
                                    }
                                }).catch(error => {
                                    console.error('通信処理に失敗しました', error);
                                    alert('通信処理に失敗しました');
                                })
                            }else{
                                console.error('取得処理に失敗しました', error);
                                alert('取得処理に失敗しました');
                            }
                        } else {
                            alert("地域活動種別・エリアマネジメント団体の取得に失敗しました");
                        }
                    }).catch(error => {
                        console.error('通信処理に失敗しました', error);
                        alert('通信処理に失敗しました');
                    });
                }).catch(error => {
                    console.error('通信処理に失敗しました', error);
                    alert('通信処理に失敗しました');
                });
            })
        })
    }

    componentWillUnmount(){
        runInAction(() => {
            this.props.viewState.clearLinkedFeatureId();
            this.props.viewState.featureInfoPanelIsVisible = false;
            // give the close animation time to finish before unselecting, to avoid jumpiness
            setTimeout(
            action(() => {
                this.props.terria.pickedFeatures = undefined;
                this.props.terria.selectedFeature = undefined;
            }),
            200
            );
            this.props.viewState.setPostSttingsCurrentParentId(0);
            this.props.terria.setDashboardManagementScreenFlag(false);
        })
    }

    displayCheckBoxCustomComponentForActivity = (props) => {
        const displayCheckBoxClickHandler = (event) => {
            event.stopPropagation();
            try{
                let activityFormList = [...this.state.activityFormList];
                let updateActivityFormList = [...this.state.updateActivityFormList];
                if(activityFormList && props.activityFormListIndex > -1 && activityFormList[props.activityFormListIndex]){
                    activityFormList[props.activityFormListIndex].publishFlag = props.publishFlag == 0?1:0;
                    const index = updateActivityFormList.findIndex(updateActivityForm=>updateActivityForm.activityId == activityFormList[props.activityFormListIndex].activityId);
                    if(index > -1){
                        updateActivityFormList[index] = activityFormList[props.activityFormListIndex];
                    }else{
                        updateActivityFormList.push(activityFormList[props.activityFormListIndex]);
                    }
                    this.setState({
                        activityFormList: activityFormList,
                        updateActivityFormList:updateActivityFormList
                    },()=>{
                        this.generateActivityTableData();
                        this.generateActivityHistoryTableData();
                    });
                }
            }catch(e){
                console.error('処理に失敗しました', error);
            }
        };
        return (
            <div style={{padding:"5px"}}>
                <div>
                    <input id={"checkbox-"+props.activityId} type="checkbox" onChange={(e)=>displayCheckBoxClickHandler(e)} className={Style.customCheckbox} value={props.publishFlag==1} checked={props.publishFlag==1}/>
                    <label for={"checkbox-"+props.activityId}></label>
                </div>
            </div>
        )
    }

    displayCheckBoxCustomComponentForPostLayer = (props) => {
        const displayCheckBoxClickHandler = (event) => {
            event.stopPropagation();
            try{
                let postLayerFeatureFormList = [...this.state.postLayerFeatureFormList];
                let updatePostLayerFeatureFormList = [...this.state.updatePostLayerFeatureFormList];
                if(postLayerFeatureFormList && props.postLayerFeatureFormListIndex > -1 && postLayerFeatureFormList[props.postLayerFeatureFormListIndex]){
                    postLayerFeatureFormList[props.postLayerFeatureFormListIndex].publishFlag = props.publishFlag == 0?1:0;
                    const index = updatePostLayerFeatureFormList.findIndex(updatePostLayerFeatureForm=>updatePostLayerFeatureForm.featureId == postLayerFeatureFormList[props.postLayerFeatureFormListIndex].featureId);
                    if(index > -1){
                        updatePostLayerFeatureFormList[index] = postLayerFeatureFormList[props.postLayerFeatureFormListIndex];
                    }else{
                        updatePostLayerFeatureFormList.push(postLayerFeatureFormList[props.postLayerFeatureFormListIndex]);
                    }
                    this.setState({
                        postLayerFeatureFormList: postLayerFeatureFormList,
                        updatePostLayerFeatureFormList:updatePostLayerFeatureFormList
                    },()=>{
                        this.generatePostLayerTableData();
                    });
                }
                
            }catch(e){
                console.error('処理に失敗しました', error);
            }
        };
        return (
            <div style={{padding:"5px"}}>
                <div>
                    <input id={"checkbox-"+props.featureId} type="checkbox" onChange={(e)=>displayCheckBoxClickHandler(e)} className={Style.customCheckbox} value={props.publishFlag==1} checked={props.publishFlag==1}/>
                    <label for={"checkbox-"+props.featureId}></label>
                </div>
            </div>
        )
    }

    generateActivityTableData(){
        const activityFormList = this.state.activityFormList;
        const userFormList = this.state.userFormList;
        const tableData = [];
        const tableColumns = [
            {
                Header:"",
                accessor:"checkBox",
                width:"9%",
                fontSize:".9em"
            },
            {
                Header:"活動名",
                accessor:"activityName",
                width:"21%",
                fontSize:".9em"
            },
            {
                Header:"活動場所",
                accessor:"activityPlace",
                width:"21%",
                fontSize:".9em"
            },
            {
                Header:"投稿者",
                accessor:"postUser",
                width:"21%",
                fontSize:".9em"
            },
            {
                Header:"投稿日時",
                accessor:"insertTime",
                width:"21%",
                fontSize:".9em"
            }
        ];
        const paretnActivityIdList = {};
        for(let i=0;i<activityFormList.length;i++){
            if(paretnActivityIdList[activityFormList[i].parentActivityId]){
                continue;
            }
            paretnActivityIdList[activityFormList[i].parentActivityId] = true;
            let userName = "";
            const index = userFormList.findIndex(userForm => userForm.userId == activityFormList[i].postUserId);
            if(index > -1){
                userName = userFormList[index].userName;
            }
            const data = {
                checkBox:{
                    width:"9%",
                    fontSize:".9em",
                    customComponent:this.displayCheckBoxCustomComponentForActivity,
                    props:{parentActivityId:activityFormList[i].parentActivityId,activityId:activityFormList[i].activityId,publishFlag:activityFormList[i].publishFlag,activityFormListIndex:i}
                },
                activityName:{
                    width:"21%",
                    fontSize:".9em",
                    value:activityFormList[i].activityName
                },
                activityPlace:{
                    width:"21%",
                    fontSize:".9em",
                    value:activityFormList[i].activityPlace
                },
                postUser:{
                    width:"21%",
                    fontSize:".9em",
                    value:userName
                },
                insertTime:{
                    width:"21%",
                    fontSize:".9em",
                    value:activityFormList[i].insertTime
                }
            }
            data["clickEventFlag"] = true;
            data["onClick"] = (parent) => {
                runInAction(() => {
                    parent.setCurrentParentIdForActivity(activityFormList[i].parentActivityId);
                    parent.props.viewState.terria.focusMapPlaceAndAttributeDisplay(activityFormList[i].longitude,activityFormList[i].latitude,[activityFormList[i].parentActivityId+""],["parent_activity_id"],parent.props.viewState,200);
                })
            }
            tableData.push(data);
        }
        this.setState({
            tableColumns: tableColumns,
            tableData:tableData
        });
    }

    generateActivityHistoryTableData(){
        const activityFormList = this.state.activityFormList;
        const userFormList = this.state.userFormList;
        const tableData = [];
        const tableColumns = [
            {
                Header:"",
                accessor:"checkBox",
                width:"9%",
                fontSize:".8em"
            },
            {
                Header:"活動名",
                accessor:"activityName",
                width:"14%",
                fontSize:".8em"
            },
            {
                Header:"活動場所",
                accessor:"activityPlace",
                width:"14%",
                fontSize:".8em"
            },
            {
                Header:"開始日時",
                accessor:"startDateAndTime",
                width:"14%",
                fontSize:".8em"
            },
            {
                Header:"終了日時",
                accessor:"endDateAndTime",
                width:"14%",
                fontSize:".8em"
            },
            {
                Header:"投稿者",
                accessor:"postUser",
                width:"14%",
                fontSize:".8em"
            },
            {
                Header:"投稿日時",
                accessor:"insertTime",
                width:"14%",
                fontSize:".8em"
            }
        ];
        for(let i=0;i<activityFormList.length;i++){
            if(activityFormList[i].parentActivityId != this.props.viewState.postSttingsCurrentParentId){
                continue;
            }
            let userName = "";
            const index = userFormList.findIndex(userForm => userForm.userId == activityFormList[i].postUserId);
            if(index > -1){
                userName = userFormList[index].userName;
            }
            const data = {
                checkBox:{
                    width:"9%",
                    fontSize:".8em",
                    customComponent:this.displayCheckBoxCustomComponentForActivity,
                    props:{parentActivityId:activityFormList[i].parentActivityId,activityId:activityFormList[i].activityId,publishFlag:activityFormList[i].publishFlag,activityFormListIndex:i}
                },
                activityName:{
                    width:"14%",
                    fontSize:".8em",
                    value:activityFormList[i].activityName
                },
                activityPlace:{
                    width:"14%",
                    fontSize:".8em",
                    value:activityFormList[i].activityPlace
                },
                startDateAndTime:{
                    width:"14%",
                    fontSize:".8em",
                    value:activityFormList[i].startDateAndTime
                },
                endDateAndTime:{
                    width:"14%",
                    fontSize:".8em",
                    value:activityFormList[i].endDateAndTime
                },
                postUser:{
                    width:"14%",
                    fontSize:".8em",
                    value:userName
                },
                insertTime:{
                    width:"14%",
                    fontSize:".8em",
                    value:activityFormList[i].insertTime
                }
            }
            tableData.push(data);
        }
        this.setState({
            historyTableColumns: tableColumns,
            historyTableData:tableData
        });
    }

    generatePostLayerTableData(){
        const postLayerAttributeFormList=this.state.postLayerAttributeFormList;
        const postLayerFeatureFormList= this.state.postLayerFeatureFormList;
        const userFormList = this.state.userFormList;
        const tableData = [];
        const tableColumns = [
            {
                Header:"",
                accessor:"checkBox",
                width:"100px",
                fontSize:".8em"
            },
            {
                Header:"投稿日時",
                accessor:"postDatetime",
                width:"100px",
                fontSize:".8em"
            },
            {
                Header:"投稿者",
                accessor:"postUser",
                width:"100px",
                fontSize:".8em"
            }
        ];
        for(let i=0;i<postLayerAttributeFormList.length;i++){
            tableColumns.push(
                {
                    Header:postLayerAttributeFormList[i]?.itemName,
                    accessor:"item"+(postLayerAttributeFormList[i].itemId),
                    width:"150px",
                    fontSize:".8em"
                }
            )
        }
        for(let i=0;i<postLayerFeatureFormList.length;i++){
            let userName = "";
            const index = userFormList.findIndex(userForm => userForm.userId == postLayerFeatureFormList[i].postUserId);
            if(index > -1){
                userName = userFormList[index].userName;
            }
            const data = {
                checkBox:{
                    width:"100px",
                    fontSize:".8em",
                    customComponent:this.displayCheckBoxCustomComponentForPostLayer,
                    props:{parentFeatureId:postLayerFeatureFormList[i].parentFeatureId,featureId:postLayerFeatureFormList[i].featureId,publishFlag:postLayerFeatureFormList[i].publishFlag,postLayerFeatureFormListIndex:i}
                },
                postDatetime:{
                    width:"100px",
                    fontSize:".8em",
                    value:postLayerFeatureFormList[i].postDatetime
                },
                postUser:{
                    width:"100px",
                    fontSize:".8em",
                    value:userName
                }
            }
            for(let j=0;j<postLayerAttributeFormList.length;j++){
                data["item"+postLayerAttributeFormList[j].itemId] = {
                    width:"150px",
                    fontSize:".8em",
                    value:postLayerFeatureFormList[i]["item"+postLayerAttributeFormList[j].itemId]
                };
            }
            data["clickEventFlag"] = true;
            data["onClick"] = (parent) => {
                runInAction(() => {
                    parent.props.viewState.terria.focusMapPlaceAndAttributeDisplay(postLayerFeatureFormList[i].longitude,postLayerFeatureFormList[i].latitude,[postLayerFeatureFormList[i].parentFeatureId+""],["parent_feature_id"],parent.props.viewState,200);
                })
            }
            tableData.push(data);
        }
        this.setState({
            tableColumns: tableColumns,
            tableData:tableData,
            historyTableColumns: [],
            historyTableData:[]
        });
    }

    setCurrentParentIdForActivity(parentActivityId){
        this.props.viewState.setPostSttingsCurrentParentId(parentActivityId);
        this.generateActivityHistoryTableData();
    }

    layerChange(){
        runInAction(() => {
            this.props.viewState.featureInfoPanelIsVisible = false;
            // give the close animation time to finish before unselecting, to avoid jumpiness
            setTimeout(
            action(() => {
                this.props.terria.pickedFeatures = undefined;
                this.props.terria.selectedFeature = undefined;
            }),
            200
            );
        })
        this.setState({
            startPostDateAndTime:"",
            endPostDateAndTime:"",
            orderMode:"1"
        })
        const postType = this.state.postType;
        const currentUniqueId = this.state.currentUniqueId;
        if(postType == 1){
            let activityType = 1;
            if(currentUniqueId == Config.layerUniqueIdCorrespondenceTable.event){
                activityType = 2;
            }
            runInAction(() => {
                const items = this.props.terria.workbench.items;
                for (const aItem of items) {
                    runInAction(() => {
                        this.props.terria.workbench.remove(aItem);
                    })
                }
                this.props.terria.customAdminLoadInitSource().then(res=>{
                    const item = this.props.terria.getModelById(BaseModel, currentUniqueId);
                    if(item){
                        item.forceLoadMapItems().then((res) =>{
                            item.loadMapItems();
                            this.props.terria.workbench.add(item);
                        }).catch((e) => {
                          try{
                            this.props.terria.workbench.add(item);
                          }catch(e){}
                      });
                    }
                    fetch(Config.config.apiUrl + "/activity/all/"+activityType)
                    .then((res) => res.json())
                    .then(res => {
                        if(res!=undefined && res != null){
                            this.setState({
                                activityFormList: res,
                                updateActivityFormList:[]
                            }, () => {
                                this.generateActivityTableData();
                                this.generateActivityHistoryTableData();
                            });
                        }else{
                            alert('取得に失敗しました');
                        }
                    }).catch(error => {
                        console.error('通信処理に失敗しました', error);
                        alert('通信処理に失敗しました');
                    })
                })
            })
        //TODO:投稿レイヤの場合
        }else{
            const postLayerLayerId = this.state.postLayerLayerId;
            runInAction(() => {
                const items = this.props.terria.workbench.items;
                for (const aItem of items) {
                    runInAction(() => {
                        this.props.terria.workbench.remove(aItem);
                    })
                }
                this.props.terria.customAdminLoadInitSource().then(res=>{
                    const item = this.props.terria.getModelById(BaseModel, currentUniqueId);
                    if(item){
                        item.forceLoadMapItems().then((res) =>{
                            item.loadMapItems();
                            this.props.terria.workbench.add(item);
                        }).catch((e) => {
                          try{
                            this.props.terria.workbench.add(item);
                          }catch(e){}
                      });
                    }
                    fetch(Config.config.apiUrl + "/layers/postLayerAttribute/"+postLayerLayerId)
                    .then((res) => res.json())
                    .then(res => {
                        if(res!=undefined && res != null){
                            this.setState({
                                postLayerAttributeFormList: res
                            }, () => {
                                fetch(Config.config.apiUrl + "/layers/admin/getPostLayer/"+postLayerLayerId)
                                .then((res) => res.json())
                                .then(res => {
                                    if(res!=undefined && res != null){
                                        this.setState({
                                            postLayerFeatureFormList: res,
                                            updatePostLayerFeatureFormList:[]
                                        }, () => {
                                            this.generatePostLayerTableData();
                                        });
                                    }else{
                                        alert('取得に失敗しました');
                                    }
                                }).catch(error => {
                                    console.error('通信処理に失敗しました', error);
                                    alert('通信処理に失敗しました');
                                })
                            });
                        }else{
                            alert('取得に失敗しました');
                        }
                    }).catch(error => {
                        console.error('通信処理に失敗しました', error);
                        alert('通信処理に失敗しました');
                    })
                })
            })
        }
    }

    search(){
        if(this.state.startPostDateAndTime && !this.state.endPostDateAndTime){
            alert("終了日時を選択してください");
            return;
        }else if(!this.state.startPostDateAndTime && this.state.endPostDateAndTime){
            alert("開始日時を選択してください");
            return;
        }
        if(!this.state.startPostDateAndTime && !this.state.endPostDateAndTime){
            this.setState({
                startPostDateAndTime:"",
                endPostDateAndTime:""
            })
        }else{
            let date1 = new Date(this.state.startPostDateAndTime);
            let date2 = new Date(this.state.endPostDateAndTime);
            if (date2 < date1) {
                alert("終了日時は開始日時より後を選択してください");
            }
        }
        const postType = this.state.postType;
        const currentUniqueId = this.state.currentUniqueId;
        if(postType == 1){
            let activityType = 1;
            if(currentUniqueId == Config.layerUniqueIdCorrespondenceTable.event){
                activityType = 2;
            }
            const item = this.props.terria.getModelById(BaseModel, currentUniqueId);
            if(item){
                item.forceLoadMapItems().then((res) =>{
                    item.loadMapItems();
                    this.props.terria.workbench.add(item);
                }).catch((e) => {
                    try{
                    this.props.terria.workbench.add(item);
                    }catch(e){}
                });
            }
            fetch(Config.config.apiUrl + "/activity/search/"+activityType, {
                method: 'POST',
                body: JSON.stringify({
                    startPostDateAndTime:this.state.startPostDateAndTime,
                    endPostDateAndTime:this.state.endPostDateAndTime,
                    sortFlag:this.state.orderMode
                }),
                headers: new Headers({ 'Content-type': 'application/json' }),
            })
            .then((res) => res.json())
            .then(res => {
                if(res!=undefined && res != null){
                    this.setState({
                        activityFormList: res,
                        updateActivityFormList:[]
                    }, () => {
                        this.generateActivityTableData();
                        this.generateActivityHistoryTableData();
                    });
                }else{
                    alert('取得に失敗しました');
                }
            }).catch(error => {
                console.error('通信処理に失敗しました', error);
                alert('通信処理に失敗しました');
            })
        //TODO:投稿レイヤの場合
        }else{
            const postLayerLayerId = this.state.postLayerLayerId;
            fetch(Config.config.apiUrl + "/layers/postLayerAttribute/"+postLayerLayerId)
            .then((res) => res.json())
            .then(res => {
                if(res!=undefined && res != null){
                    this.setState({
                        postLayerAttributeFormList: res
                    }, () => {
                        fetch(Config.config.apiUrl + "/layers/admin/search/"+postLayerLayerId, {
                            method: 'POST',
                            body: JSON.stringify({
                                startPostDateAndTime:this.state.startPostDateAndTime,
                                endPostDateAndTime:this.state.endPostDateAndTime,
                                sortFlag:this.state.orderMode
                            }),
                            headers: new Headers({ 'Content-type': 'application/json' }),
                        })
                        .then((res) => res.json())
                        .then(res => {
                            if(res!=undefined && res != null){
                                this.setState({
                                    postLayerFeatureFormList: res,
                                    updatePostLayerFeatureFormList:[]
                                }, () => {
                                    this.generatePostLayerTableData();
                                });
                            }else{
                                alert('取得に失敗しました');
                            }
                        }).catch(error => {
                            console.error('通信処理に失敗しました', error);
                            alert('通信処理に失敗しました');
                        })
                    });
                }else{
                    alert('取得に失敗しました');
                }
            }).catch(error => {
                console.error('通信処理に失敗しました', error);
                alert('通信処理に失敗しました');
            })
        }
    }

    output(){
        if(this.state.startPostDateAndTime && !this.state.endPostDateAndTime){
            alert("終了日時を選択してください");
            return;
        }else if(!this.state.startPostDateAndTime && this.state.endPostDateAndTime){
            alert("開始日時を選択してください");
            return;
        }
        if(!this.state.startPostDateAndTime && !this.state.endPostDateAndTime){
            this.setState({
                startPostDateAndTime:"",
                endPostDateAndTime:""
            })
        }else{
            let date1 = new Date(this.state.startPostDateAndTime);
            let date2 = new Date(this.state.endPostDateAndTime);
            if (date2 < date1) {
                alert("終了日時は開始日時より後を選択してください");
                return;
            }
        }
        const postType = this.state.postType;
        const currentUniqueId = this.state.currentUniqueId;
        let downloadUrl = "";
        if(postType == 1){
            let activityType = 1;
            if(currentUniqueId == Config.layerUniqueIdCorrespondenceTable.event){
                activityType = 2;
            }
            downloadUrl = Config.config.apiUrl + "/csv/download/activity/"+activityType;
        }else{
            const postLayerLayerId = this.state.postLayerLayerId;
            downloadUrl = Config.config.apiUrl + "/csv/download/postLayer/"+postLayerLayerId;
        }
        const xhr = new XMLHttpRequest();
        xhr.open('POST', downloadUrl , true);
        xhr.setRequestHeader("Content-Type", "application/json");
        xhr.withCredentials = true;
        xhr.responseType = 'arraybuffer';
        xhr.onload = function(e) {
            if (this.status == 200) {
                //仮のファイル名（上書き対象）
                let fileName = '投稿情報.csv';
                let disposition = xhr.getResponseHeader('Content-Disposition');
                if (disposition && disposition.indexOf('attachment') !== -1) {
                    let filenameRegex = /filename[^;=\n]=((['"]).*?\2|[^;\n]*)/;
                    let matches = filenameRegex.exec(disposition);
                    if (matches != null && matches[1]) {
                    fileName = decodeURI(matches[1].replace(/['"]/g, '').replace('UTF-8',''));
                    }
                }  
                let blob = this.response;
                if (navigator.appVersion.toString().indexOf('.NET') > 0) {
                    window.navigator.msSaveBlob(new Blob([blob]), fileName);
                } else {
                    let a = document.createElement('a');
                    let blobUrl = window.URL.createObjectURL(new Blob([blob], {
                        type: blob.type
                    }));
                    a.href = blobUrl;
                    a.download = fileName;
                    a.click();
                }
                return;
            } else {
                alert("該当するデータのダウンロードができませんでした。再度検索条件を確認してください。");
                return;
            }
        };
        const data = JSON.stringify({
            startPostDateAndTime:this.state.startPostDateAndTime,
            endPostDateAndTime:this.state.endPostDateAndTime,
            sortFlag:this.state.orderMode
        });
        xhr.send(data);
    }

    save(){
        const postType = this.state.postType;
        const updateActivityFormList = this.state.updateActivityFormList;
        const updatePostLayerFeatureFormList = this.state.updatePostLayerFeatureFormList;
        if(postType == 1){
            if(updateActivityFormList.length > 0){
                fetch(Config.config.apiUrl + "/activity/publish", {
                    method: 'POST',
                    body: JSON.stringify(updateActivityFormList),
                    headers: new Headers({ 'Content-type': 'application/json' }),
                })
                .then((res) => res.json())
                .then(res => {
                    if(res.status == 204){
                        alert('更新に成功しました');
                        this.setState({
                            updateActivityFormList:[]
                        });
                    }else{
                        alert('更新に失敗しました');
                    }
                }).catch(error => {
                    console.error('通信処理に失敗しました', error);
                    alert('通信処理に失敗しました');
                })
            }else{
                alert("変更対象の投稿がありません");
            }
        //投稿レイヤの場合
        }else{
            if(updatePostLayerFeatureFormList.length > 0){
                fetch(Config.config.apiUrl + "/layers/postLayer/publish", {
                    method: 'POST',
                    body: JSON.stringify(updatePostLayerFeatureFormList),
                    headers: new Headers({ 'Content-type': 'application/json' }),
                })
                .then((res) => res.json())
                .then(res => {
                    if(res.status == 204){
                        alert('更新に成功しました');
                        this.setState({
                            updatePostLayerFeatureFormList:[]
                        });
                    }else{
                        alert('更新に失敗しました');
                    }
                }).catch(error => {
                    console.error('通信処理に失敗しました', error);
                    alert('通信処理に失敗しました');
                })
            }else{
                alert("変更対象の投稿がありません");
            }
        }
    }

    render() {
        const customElements = processCustomElements(
            this.props.viewState.useSmallScreenInterface,
            this.props.children
          );
        const animationDuration = 250;
        const themeLayerList = this.state.themeLayerList;
        const updateActivityFormList = this.state.updateActivityFormList;
        const postType = this.state.postType;
        let viewerHeight = "50%";
        if(postType != 1){
            viewerHeight = "100%";
        }
        return (
            <ThemeProvider theme={terriaTheme}>
                <div style={{height:"80vh",width:"100vw"}}>
                    <div className={Style.complexHeader} style={{display:"flex",width:"100%",boxSizing:"border-box",padding:"1.042vw 1.563vw",borderBottom:"1px solid #EBEEF7"}}>
                        <Box col4 flex={true} className={Style.themeItem} style={{paddingBottom:"5px"}}>
                            <div style={{display:"flex",width:"100%"}}>
                                <Box>
                                        <select
                                            onChange={e => {
                                                if(updateActivityFormList.length > 0 && !window.confirm("保存されてない変更は破棄されますがよろしいですか？")){
                                                    return;
                                                }
                                                let targetValue = e.target.value;
                                                targetValue = targetValue.split(",");
                                                let postType = 1;
                                                let postLayerLayerId = -1;
                                                Object.keys(Config.layerUniqueIdCorrespondenceTable).map(key=>{
                                                    if(Config.layerUniqueIdCorrespondenceTable[key] == targetValue[0]
                                                        && key!='activity' && key!='event'){
                                                            postType = 2;
                                                            postLayerLayerId = targetValue[1];
                                                    }
                                                })
                                                this.setState({
                                                    currentUniqueId:targetValue[0],
                                                    postType:postType,
                                                    postLayerLayerId:postLayerLayerId
                                                },()=>{
                                                    this.layerChange();
                                                });
                                            }}
                                            style={{minWidth:"150px" }}>
                                            {Object.keys(themeLayerList).map(key => (
                                                <option value={themeLayerList[key]?.uniqueId +","+themeLayerList[key]?.layerId} selected={themeLayerList[key]?.uniqueId == this.state.currentUniqueId}>{themeLayerList[key]?.layerName}</option>
                                            ))}
                                        </select>
                                </Box>
                                <Box col6>
                                    <button
                                        className={Style.saveButton}
                                        onClick={evt => {
                                            this.save();
                                        }}
                                    >
                                        <span>保存</span>
                                    </button>
                                </Box>
                            </div>
                        </Box>
                        <Box col8 flex={true} className={Style.themeItem}>
                            <div style={{display:"flex",width:"100%",height:"30px"}}>
                                <Box col1 style={{justifyContent:"flex-end"}}>
                                    <span style={{lineHeight:"2vw",fontSize:".75vw"}}>投稿日時</span>
                                </Box>
                                <Box col7 style={{justifyContent:"flex-end"}}>
                                <input
                                    value={this.state.startPostDateAndTime}
                                    type="datetime-local"
                                    style={{height:"1.979vw",width:"9vw",fontSize:".75vw",border:"1px solid #EBEEF7"}}
                                    onChange={e => this.setState({ startPostDateAndTime: e.target.value })}
                                />
                                <span style={{fontSize:".75vw",marginLeft:"10px",marginRight:"10px",lineHeight:"2vw"}}>~</span>
                                <input
                                    value={this.state.endPostDateAndTime}
                                    type="datetime-local"
                                    style={{height:"1.979vw",width:"9vw",fontSize:".75vw",border:"1px solid #EBEEF7"}}
                                    onChange={e => this.setState({ endPostDateAndTime: e.target.value })}
                                /> 
                                <select
                                    onChange={e => this.setState({ orderMode: e.target.value })}
                                    style={{width:"7vw",fontSize:".75vw",marginLeft:"10px",marginRight:"10px" }}>
                                    <option value="1" selected={this.state.orderMode == 1}>降順</option>
                                    <option value="0" selected={this.state.orderMode == 0}>昇順</option>
                                </select>
                                </Box>
                                <Box col2>
                                    <button
                                        onClick={evt => {
                                            this.search();
                                        }}
                                        style={{background:"#2AAE7A",color:"#ffffff",border:"none",fontSize:"0.833vw"}}
                                    >
                                        <span>絞込み</span>
                                    </button>
                                </Box>
                                <Box col2 style={{justifyContent:"flex-start"}}>
                                    <button
                                        onClick={evt => {
                                            this.output();
                                        }}
                                        style={{background:"#2AAE7A",color:"#ffffff",border:"none",fontSize:"0.833vw",position:"relative",right:"20%"}}
                                    >
                                        <span>CSV出力</span>
                                    </button>
                                </Box>
                            </div>
                        </Box>
                    </div>
                    <div style={{display:"flex",width:"100%",height:"90%",boxSizing:"border-box",padding:"1.042vw 1.563vw"}}>
                        <Box col6 style={{display:"block",overflowY:"auto",overflowX:"auto",width:"50%",maxWidth:"37vw"}}>
                            <Table columns={this.state.tableColumns} data={this.state.tableData} this={this}/>
                        </Box>
                        <Box col6 style={{maxHeight:"100%",overflowY:"auto",display:"block",width:"50%",maxWidth:"37vw"}}>
                            <div style={{height:viewerHeight,width:"100%",overflow:"hidden"}}>
                                <Viewer terria={this.props.terria} viewState={this.props.viewState} animationDuration={animationDuration} customElements={customElements} allBaseMaps={this.props.allBaseMaps} dashboardManagementScreenFlag={true}/>
                            </div>
                            {postType == 1 && (
                            <div style={{height:"50%",width:"100%",overflowY:"auto"}}>
                                <Table columns={this.state.historyTableColumns} data={this.state.historyTableData}/>
                            </div>
                            )}
                        </Box>
                    </div>
                    <div
                        className={classNames(
                        StandardUserInterfaceStyle.featureInfo,
                        Style.customFeatureInfoPanel,
                        this.props.viewState.topElement === "FeatureInfo"
                            ? "top-element"
                            : "",
                        {
                            [StandardUserInterfaceStyle.featureInfoFullScreen]: this.props.viewState
                            .isMapFullScreen
                        }
                        )}
                        tabIndex={0}
                        onClick={action(() => {
                        this.props.viewState.topElement = "FeatureInfo";
                        })}
                    >
                        <FeatureInfoPanel
                        terria={this.props.terria}
                        viewState={this.props.viewState}
                        highlightGraphAndFilterList={highlightGraphAndFilterList}
                        maxHeight={"250px"}
                        />
                    </div>
                </div>
            </ThemeProvider>
        );
    }
}

export default withTranslation()(withTheme(PostSettingsScreen));