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
import Style from "./common.scss"
import {
    MenuLeft,
    Nav,
    ExperimentalMenu
  } from "../../StandardUserInterface/customizable/Groups";
import StandardUserInterface from "../../StandardUserInterface/StandardUserInterface.jsx";
import {setThemeData} from '../../../Models/GraphList/GraphList';
import Table from './Table.jsx';
import GraphListRegisterScreen from './GraphListRegisterScreen.jsx';

/**
 * グラフ・リスト設定画面
 * styleは仮で当て込み
 */
@observer
class GraphListSettingsScreen extends React.Component {
    static displayName = "GraphListSettingsScreen";

    static propTypes = {
        terria: PropTypes.object.isRequired,
        viewState: PropTypes.object.isRequired,
        theme: PropTypes.object,
        t: PropTypes.func.isRequired
    };

    constructor(props) {
        super(props);
        this.state = {
            graphListTypeFormList:[],
            tableGraphListColumns:[],
            tableGraphListData:[],
            editGraphId:null,
            //subPageNum=0 → グラフ・リスト設定画面（デフォルトメイン）
            //subPageNum=1 → グラフリスト新規作成画面
            subPageNum:0

        }
    }

    componentDidMount() {
        this.getData();
    }

    displayCheckBoxCustomComponent = (props) => {
        const displayLayout = this.props.viewState.displayLayout?.map(l => ({ i: l.i, x: l.x, y: l.y, w: l.w, h: l.h}));
        const displayLayoutIndex = displayLayout.findIndex(layout =>layout.i == -1);
        const displayCheckBoxClickHandler = (event) => {
            try{
                if(event.target.value=="false"){
                    displayRadioButtonClickHandler(event,"left","/graphs/register")
                }else{
                    if(window.confirm("非搭載とする場合現在のレイアウト設定は破棄されますがよろしいですか？")){
                        const themeData = props.viewState.themeData;
                        const themeGraphListFormList = [];
                        const themeGraphListForm={themeId:themeData.themeId,graphId:props.graphId};
                        themeGraphListFormList.push(themeGraphListForm);
                        fetch(Config.config.apiUrl + "/graphs/delete", {
                            method: 'POST',
                            body: JSON.stringify(themeGraphListFormList),
                            headers: new Headers({ 'Content-type': 'application/json' }),
                        })
                        .then((res) => res.json())
                        .then(res => {
                            const status = res.status;
                            const themeDataList = props.viewState.themeDataList;
                            const index = themeDataList.findIndex(themeData=>themeData.themeId == props.viewState.selectedThemeId);
                            if(index > -1){
                                runInAction(() => {
                                    props.viewState.resetUpdateLayout();
                                    props.viewState.setSelectedWithThemeSwitchItemValue("");
                                    props.viewState.setThemeData(themeDataList[index]);
                                    setThemeData(props.viewState).then((res)=>{
                                        this.getData();
                                        if(status != 204){
                                            alert('更新に失敗しました');
                                        }
                                    });
                                })
                            }
                        }).catch(error => {
                            console.error('通信処理に失敗しました', error);
                            throw "error";
                        });
                    }
                }
            }catch(e){
                const themeDataList = props.viewState.themeDataList;
                const index = themeDataList.findIndex(themeData=>themeData.themeId == props.viewState.selectedThemeId);
                if(index > -1){
                    runInAction(() => {
                        props.viewState.resetUpdateLayout();
                        props.viewState.setSelectedWithThemeSwitchItemValue("");
                        props.viewState.setThemeData(themeDataList[index]);
                        setThemeData(props.viewState).then((res)=>{
                            this.getData();
                        });
                    })
                }
                alert('処理に失敗しました');
            }
        };
        const displayRadioButtonClickHandler = (event,direction,path="/graphs/update") => {
            try{
                if(event.target.value=="false" 
                        && ((path=="/graphs/update" && window.confirm("変更する場合現在のレイアウト設定は破棄されますがよろしいですか？")) 
                                            || (path=="/graphs/register" && window.confirm("新規搭載の場合デフォルトでは左側に追加します")))){
                    const themeData = props.viewState.themeData;
                    const graphList = props.viewState.graphList;
                    const displayLayout = this.props.viewState.displayLayout?.map(l => ({ i: l.i, x: l.x, y: l.y, w: l.w, h: l.h}));
                    const viewerIndex = displayLayout.findIndex(layout=>layout.i == -1);
                    let leftLayout = [];
                    let rightLayout = [];
                    Object.keys(graphList).map(key=>{
                        const index = displayLayout.findIndex(layout=>layout.i == key);
                        if(displayLayout[index]){
                            if(displayLayout[index].x < displayLayout[viewerIndex].x ){
                                leftLayout.push(displayLayout[index]);
                            }else{
                                rightLayout.push(displayLayout[index]);
                            }
                        }
                    })
                    if(viewerIndex > -1){
                        rightLayout.push(displayLayout[viewerIndex]);
                    }
                    const themeGraphListFormList = [];
                    let themeGraphListForm=null;
                    if(direction == "left"){
                        let maxLayoutIndex = -1;
                        let maxY = 0;
                        let maxH = 0;
                        for(let i=0;i<leftLayout.length;i++){
                            if(leftLayout[i].y > maxY){
                                maxY = leftLayout[i].y;
                                maxH = leftLayout[i].h;
                                maxLayoutIndex = i;
                            }
                        }
                        themeGraphListForm={themeId:themeData.themeId,graphId:props.graphId,panelHeight:Config.layout.leftInitialHeight,panelWidth:Config.layout.leftInitialWidth,topLeftX:0,topLeftY:maxY+maxH};
                    }else if(direction == "right"){
                        let maxLayoutIndex = -1;
                        let maxY = 0;
                        let maxH = 0;
                        for(let i=0;i<rightLayout.length;i++){
                            if(rightLayout[i].y > maxY){
                                maxY = rightLayout[i].y;
                                maxH = rightLayout[i].h;
                                maxLayoutIndex = i;
                            }
                        }
                        themeGraphListForm={themeId:themeData.themeId,graphId:props.graphId,panelHeight:Config.layout.rightInitialHeight,panelWidth:Config.layout.rightInitialWidth,topLeftX:displayLayout[viewerIndex].x,topLeftY:maxY+maxH};
                    }
                    if(themeGraphListForm){
                        themeGraphListFormList.push(themeGraphListForm);
                        fetch(Config.config.apiUrl + path, {
                            method: 'POST',
                            body: JSON.stringify(themeGraphListFormList),
                            headers: new Headers({ 'Content-type': 'application/json' }),
                        })
                        .then((res) => res.json())
                        .then(res => {
                            if(res.status == 204){
                                const themeDataList = props.viewState.themeDataList;
                                const index = themeDataList.findIndex(themeData=>themeData.themeId == props.viewState.selectedThemeId);
                                if(index > -1){
                                    runInAction(() => {
                                        props.viewState.resetUpdateLayout();
                                        props.viewState.setSelectedWithThemeSwitchItemValue("");
                                        props.viewState.setThemeData(themeDataList[index]);
                                        setThemeData(props.viewState).then((res)=>{
                                            this.getData();
                                        });
                                    })
                                }
                            }else{
                                alert('更新に失敗しました');
                            }
                        }).catch(error => {
                            console.error('通信処理に失敗しました', error);
                            throw "error";
                        });
                    }
                }
            }catch(e){
                const themeDataList = props.viewState.themeDataList;
                const index = themeDataList.findIndex(themeData=>themeData.themeId == props.viewState.selectedThemeId);
                if(index > -1){
                    runInAction(() => {
                        props.viewState.resetUpdateLayout();
                        props.viewState.setSelectedWithThemeSwitchItemValue("");
                        props.viewState.setThemeData(themeDataList[index]);
                        setThemeData(props.viewState).then((res)=>{
                            this.getData();
                        });
                    })
                }
                alert('処理に失敗しました');
            }
        };
        return (
            <div style={{padding:"5px"}}>
                <div>
                    <input id={"checkbox-"+props.graphId} type="checkbox" onChange={(e)=>displayCheckBoxClickHandler(e)} className={Style.customCheckbox} value={props.displayFlag==1} checked={props.displayFlag==1}/>
                    <label for={"checkbox-"+props.graphId}></label>
                </div>
                {props.displayFlag==1 && (
                <div>
                    <div className={Style.customRadio}>
                        <input id={"radio-1-"+props.graphId} name={"radio-1-"+props.graphId} type="radio" value={displayLayoutIndex>-1&&props.layout.x<displayLayout[displayLayoutIndex]?.x} checked={displayLayoutIndex>-1&&props.layout.x<displayLayout[displayLayoutIndex]?.x} onClick={(e)=>displayRadioButtonClickHandler(e,"left")}/>
                        <label for={"radio-1-"+props.graphId} className={Style.radioLabel}>左側</label>
                    </div>

                    <div className={Style.customRadio}>
                        <input id={"radio-2-"+props.graphId} name={"radio-1-"+props.graphId} type="radio" value={displayLayoutIndex>-1&&props.layout.x>=displayLayout[displayLayoutIndex]?.x} checked={displayLayoutIndex>-1&&props.layout.x>=displayLayout[displayLayoutIndex]?.x} onClick={(e)=>displayRadioButtonClickHandler(e,"right")}/>
                        <label  for={"radio-2-"+props.graphId} className={Style.radioLabel}>右側</label>
                    </div>
                </div>
                )}
            </div>
        )
    }

    editButtonCustomComponent = (props) => {
        const editButtonClickHandler = (event) => {
            this.setState({editGraphId:props.graphId,subPageNum:1});
        };
        return (
            <>
             {props.editFlag==1 && (
                <button onClick={editButtonClickHandler} style={{background:"blue",color:"#fff",padding:"5px",width:"98%",border:"none"}}>
                    <span>編集</span>
                </button>
             )}
            </>
        )
    }

    deleteButtonCustomComponent = (props) => {
        const deleteButtonClickHandler = (event) => {
            if(!window.confirm("本当にグラフリストデータを削除しますか？該当のグラフリストに関する全ての関連情報が削除されます")){
                return;
            }
            fetch(Config.config.apiUrl + "/graph/delete/"+props.graphId,{
                method: 'DELETE'
            })
            .then(res => res.json())
            .then(res => {
                if(res.status == 204){
                    const themeDataList = props.viewState.themeDataList;
                    const index = themeDataList.findIndex(themeData=>themeData.themeId == props.viewState.selectedThemeId);
                    if(index > -1){
                        runInAction(() => {
                            props.viewState.resetUpdateLayout();
                            props.viewState.setSelectedWithThemeSwitchItemValue("");
                            props.viewState.setThemeData(themeDataList[index]);
                            setThemeData(props.viewState).then((res)=>{
                                this.getData();
                            });
                        })
                    }  
                }else{
                    alert('削除処理に失敗しました');
                }
            }).catch(error => {
                console.error('通信処理に失敗しました', error);
                alert('処理に失敗しました');
            });
        };
        return (
            <>
            {props.deleteFlag==1 && (
                <button onClick={deleteButtonClickHandler} style={{background:"#fff",color:"red",padding:"5px",width:"98%",borderColor:"red"}}>
                    <span>削除</span>
                </button>
            )}
            </>
        )
    }

    getData(){
        try{
            fetch(Config.config.apiUrl + "/graph/type/all")
            .then((res) => res.json())
            .then(res => {
                const graphListTypeFormList = res;
                if(graphListTypeFormList && Object.keys(graphListTypeFormList).length > 0 && graphListTypeFormList[0].graphTypeId){
                    fetch(Config.config.apiUrl + "/graph/all")
                    .then((res) => res.json())
                    .then(res => {
                        const graphListFormList = res;
                        if(graphListFormList && Object.keys(graphListFormList).length > 0 && graphListFormList[0].graphId){
                            runInAction(() => {
                                this.props.viewState.setAdminPageTitle("グラフ・リスト一覧画面");
                                const graphList = this.props.viewState.graphList;
                                const themeData = this.props.viewState.themeData;
                                const displayLayout = this.props.viewState.displayLayout?.map(l => ({ i: l.i, x: l.x, y: l.y, w: l.w, h: l.h}));
                                const tableGraphListData = [];
                                const tableGraphListColumns = [
                                    {
                                        Header:"タイプ",
                                        accessor:"type"
                                    },
                                    {
                                        Header:"タイトル",
                                        accessor:"title"
                                    },
                                    {
                                        Header:"グループ",
                                        accessor:"group"
                                    },
                                    {
                                        Header:"テーマ",
                                        accessor:"theme"
                                    },
                                    {
                                        Header:"表示",
                                        accessor:"display"
                                    },
                                    {
                                        Header:"",
                                        accessor:"edit"
                                    },
                                    {
                                        Header:"",
                                        accessor:"delete"
                                    }
                                ];
                                Object.keys(graphList).map(key=>{
                                    const displayLayoutIndex = displayLayout.findIndex(layout =>layout.i == graphList[key]?.graphListForm?.graphId);
                                    const graphListTypeFormIndex = graphListTypeFormList.findIndex(graphListTypeForm=>graphListTypeForm.graphTypeId == graphList[key]?.graphListForm?.graphTypeId);
                                    const data = {
                                        type:{
                                            width:"17%",
                                            value:graphListTypeFormList[graphListTypeFormIndex]?.graphTypeName
                                        },
                                        title:{
                                            width:"17%",
                                            value:graphList[key]?.graphListForm?.graphName
                                        },
                                        group:{
                                            width:"17%",
                                            value:themeData?.themeGroupName
                                        },
                                        theme:{
                                            width:"17%",
                                            value:themeData?.themeName
                                        },
                                        display:{
                                            width:"10%",
                                            textAlign:"center",
                                            customComponent:this.displayCheckBoxCustomComponent,
                                            props:{displayFlag:1,graphId:graphList[key]?.graphListForm?.graphId,layout:displayLayout[displayLayoutIndex]?displayLayout[displayLayoutIndex]:null,viewState:this.props.viewState}
                                        },
                                        edit:{
                                            width:"10%",
                                            customComponent:this.editButtonCustomComponent,
                                            props:{editFlag:graphList[key]?.graphListForm?.editFlag,graphId:graphList[key]?.graphListForm?.graphId,viewState:this.props.viewState}
                                        },
                                        delete:{
                                            width:"10%",
                                            customComponent:this.deleteButtonCustomComponent,
                                            props:{deleteFlag:graphList[key]?.graphListForm?.deleteFlag,graphId:graphList[key]?.graphListForm?.graphId,viewState:this.props.viewState}
                                        },
                                        graphId:graphList[key]?.graphListForm?.graphId
                                    }
                                    tableGraphListData.push(data);
                                })
                                Object.keys(graphListFormList).map(key=>{
                                    const displayLayoutIndex = displayLayout.findIndex(layout =>layout.i == graphListFormList[key]?.graphId);
                                    const tableGraphListIndex = tableGraphListData.findIndex(data=>data.graphId == graphListFormList[key].graphId);
                                    if(tableGraphListIndex < 0){
                                        const graphListTypeFormIndex = graphListTypeFormList.findIndex(graphListTypeForm=>graphListTypeForm.graphTypeId == graphListFormList[key]?.graphTypeId);
                                        const data = {
                                            type:{
                                                width:"17%",
                                                value:graphListTypeFormList[graphListTypeFormIndex]?.graphTypeName
                                            },
                                            title:{
                                                width:"17%",
                                                value:graphListFormList[key]?.graphName
                                            },
                                            group:{
                                                width:"17%",
                                                value:themeData?.themeGroupName
                                            },
                                            theme:{
                                                width:"17%",
                                                value:themeData?.themeName
                                            },
                                            display:{
                                                width:"10%",
                                                textAlign:"center",
                                                customComponent:this.displayCheckBoxCustomComponent,
                                                props:{displayFlag:0,graphId:graphListFormList[key]?.graphId,layout:displayLayout[displayLayoutIndex]?displayLayout[displayLayoutIndex]:null,viewState:this.props.viewState}
                                            },
                                            edit:{
                                                width:"10%",
                                                customComponent:this.editButtonCustomComponent,
                                                props:{editFlag:graphListFormList[key]?.editFlag,graphId:graphListFormList[key]?.graphId,viewState:this.props.viewState}
                                            },
                                            delete:{
                                                width:"10%",
                                                customComponent:this.deleteButtonCustomComponent,
                                                props:{deleteFlag:graphListFormList[key]?.deleteFlag,graphId:graphListFormList[key]?.graphId,viewState:this.props.viewState}
                                            },
                                            graphId:graphListFormList[key]?.graphId
                                        }
                                        tableGraphListData.push(data);
                                    }

                                })
                                this.setState({subPageNum:0,graphListTypeFormList:graphListTypeFormList,tableGraphListData:tableGraphListData,tableGraphListColumns:tableGraphListColumns});
                            })
                        }
                    }).catch(error => {
                        console.error('通信処理に失敗しました', error);
                        alert('通信処理に失敗しました');
                    })
                }else{
                    alert('取得に失敗しました');
                }
            }).catch(error => {
                console.error('通信処理に失敗しました', error);
                alert('通信処理に失敗しました');
            })
        }catch(error){
            console.error('処理に失敗しました', error);
            alert('処理に失敗しました');
        }
    }

    render() {
        const themeDataList = this.props.viewState.themeDataList;
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
            {subPageNum==0 && (
            <div style={{height:"80vh",width:"100vw"}}>
               <div style={{display:"flex",width:"100%",boxSizing:"border-box",padding:"1.042vw 1.563vw",borderBottom:"1px solid #EBEEF7"}}>
                    <Box col6 flex={true} className={Style.themeItem}>
                        <div style={{display:"flex",width:"100%"}}>
                            <Box>
                                <button
                                    className={Style.backButton}
                                    onClick={evt => {   
                                        this.setState({editGraphId:null});
                                        this.setState({subPageNum:1});                            
                                    }}
                                >
                                    <span>グラフ・リスト追加</span>
                                </button>
                            </Box>
                            <Box>
                                <button
                                    className={Style.backButton1}
                                    onClick={evt => {
                                        this.props.backFunc();
                                    }}
                                >
                                    <span>ダッシュボード管理に戻る</span>
                                </button>
                            </Box>
                        </div>
                    </Box>
                </div>
                <div style={{maxHeight:"90%",overflowY:"auto",boxSizing:"border-box",padding:"1.042vw 1.563vw",paddingTop:"0",marginTop:"1.042vw"}}>
                    <Table columns={this.state.tableGraphListColumns} data={this.state.tableGraphListData}/>
                </div>
            </div>
            )}
            {subPageNum==1 && (
                <GraphListRegisterScreen {...this.props} backFunc={()=>{
                    this.props.viewState.setAdminPageTitle("グラフ・リスト一覧画面");
                    this.setState({subPageNum:0})}
                } updateFunc={()=>{this.getData();}} editGraphId={this.state.editGraphId} />
            )}
            </>
        );
    }
}

export default withTranslation()(withTheme(GraphListSettingsScreen));