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
import {setThemeData,GraphList} from '../../../Models/GraphList/GraphList';
import Table from './Table.jsx';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import chartjsPluginDatalabels from 'chartjs-plugin-datalabels'
import {
    Chart as ChartJS,
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    BarElement,
    ArcElement,
    Title,
    Tooltip,
    Legend,
  } from "chart.js";
  import { Line, Bar, Pie, Doughnut } from "react-chartjs-2";

  ChartJS.register(
    CategoryScale,
    LinearScale,
    PointElement,
    LineElement,
    BarElement,
    ArcElement,
    Title,
    Tooltip,
    Legend
  );

/**
 * グラフ・リスト新規作成画面
 * styleは仮で当て込み
 */
@observer
class GraphListRegisterScreen extends React.Component {
    static displayName = "GraphListRegisterScreen";

    static propTypes = {
        terria: PropTypes.object.isRequigray,
        viewState: PropTypes.object.isRequigray,
        theme: PropTypes.object,
        t: PropTypes.func.isRequigray
    };

    constructor(props) {
        super(props);
        this.state = {
            graphId:props.editGraphId,
            title:"",
            graphTypeId:-1,
            sourceId:-1,
            yColumn:"",
            XColumn:"",
            sortColumn:"",
            sortDirection:"",
            limitSize:15,
            graphDirection:"",
            graphListTypeFormList:[],
            layerSourceFormList:[],
            graphListData:null,
            displayColumnNameList:{},
            editRestrictionFlag:"",
            groupByFlag:"0",
            aggregationType:-1
        }
    }

    componentDidMount() {
        this.getInitData();
    }

    getInitData(){
        fetch(Config.config.apiUrl + "/graph/type/all")
        .then(res => res.json())
        .then(res => {
            if (res && res.length > 0) {
                const graphListTypeFormList = res;
                fetch(Config.config.apiUrl + "/layers/layerSource/all")
                .then(res => res.json())
                .then(res => {
                    if (res && res.length > 0) {
                        this.setState({
                            graphListTypeFormList: graphListTypeFormList,
                            layerSourceFormList: res
                        },()=>{
                            this.getGraphListData();
                        });
                    } else {
                        alert('処理に失敗しました');
                    }
                }).catch(error => {
                    console.error('通信処理に失敗しました', error);
                    alert('処理に失敗しました');
                });
            } else {
                alert('処理に失敗しました');
            }
        }).catch(error => {
            console.error('通信処理に失敗しました', error);
            alert('処理に失敗しました');
        });
    }

    getGraphListData(){
        if(this.state.graphId != null && this.state.graphId != undefined && this.state.graphId > -1){
            this.props.viewState.setAdminPageTitle("グラフ・リスト編集画面");
            fetch(Config.config.apiUrl + "/graph/admin/"+this.state.graphId)
            .then(res => res.json())
            .then(res => {
                if(res.graphId && (res.previewFlag == undefined || res.previewFlag == null || res.previewFlag != 1)){
                    this.updateGraphListData(res,true);
                }
                if(res.graphTypeId){
                    const graphListClass = new GraphList(this.props.viewState);
                    let graphListData = null;
                    switch (res.graphTypeId) {
                        case 1:
                            graphListData = graphListClass.getComplexGraphData(res, res.dataList);
                            break;
                        case 2:
                            graphListData = graphListClass.getDoughnutGraphData(res, res.dataList);
                            break;
                        case 3:
                            graphListData = graphListClass.getBarGraphData(res, res.dataList);
                            break;
                        case 4:
                            graphListData = graphListClass.getLineGraphData(res, res.dataList);
                            break;
                        case 5:
                            graphListData = graphListClass.getAgGridData(res, res.dataList);
                            break;
                        case 6:
                            graphListData = graphListClass.getSingleListData(res, res.dataList);
                            break;
                        default:
                            break;
                    }
                    this.setState({graphListData:null});
                    this.setState({graphListData:graphListData});
                }else{
                    alert('データの取得に失敗しました');
                }
            }).catch(error => {
                console.error('通信処理に失敗しました', error);
                alert('処理に失敗しました');
            });

        }else{
            this.props.viewState.setAdminPageTitle("グラフ・リスト作成画面");
        }
    }

    previewOrRegister(path){
        //必須項目チェック
        let limitSize = this.state.limitSize;
        if(!this.state.graphTypeId || this.state.title == "" || limitSize == "" || (this.state.graphTypeId < 5 && (this.state.yColumn == "" ||  this.state.XColumn == ""))){
            alert('必須項目を入力or選択してください');
            return;
        }
        //ソート属性が指定されているかつソート方向が未指定の場合defaultで昇順
        let sortObj = null;
        if(this.state.sortColumn != ""){
            sortObj = {};
            let sortDirection = this.state.sortDirection;
            if(sortDirection == ""){
                sortDirection = "ASC";
            }
            sortObj[this.state.sortColumn]=sortDirection;
        }
        //リストの並び順格納処理及びチェック処理
        let columnMap = null;
        let orderBlankCheckFlag = false;
        let shortageCheckFlag = false;
        let duplicateCheckFlag = false;
        let orderList = {};
        if(this.state.graphTypeId == 5){
            const layerSourceFormList = [...this.state.layerSourceFormList];
            const layerSourceFormListIndex = layerSourceFormList.findIndex(layerSourceForm=>layerSourceForm.sourceId == this.state.sourceId);
            for(let i=0;i<layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList.length;i++){
                if(layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList[i].fieldName != "geom" && layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList[i].fieldName != "geometry"){
                    let order = this.state.displayColumnNameList[layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList[i].fieldName];
                    if(order == null || order ==undefined){
                        if(this.state.editRestrictionFlag == 1 || (this.state.graphId != null && this.state.graphId != undefined && this.state.graphId > 0)){
                            order = 0;
                        }else{
                            order = i+1;
                        }
                    }
                    if(order === ""){
                        orderBlankCheckFlag = true;
                        break;
                    }
                    if(order == 0 && layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList[i].fieldName == this.state.sortColumn){
                        shortageCheckFlag = true;
                        break;
                    }
                    if(order != 0 && orderList[order]){
                        duplicateCheckFlag = true;
                        break;
                    }else{
                        orderList[order] = true;
                    }
                    if(!columnMap){
                        columnMap = {};
                    }
                    columnMap[layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList[i].fieldName] = order;
                }
            }
        }
        //整合性チェック
        if(this.state.graphTypeId < 5 && this.state.yColumn == this.state.XColumn){
            alert('ラベルと値は別の属性を指定する必要があります');
            return;
        }
        if(this.state.graphTypeId < 5 && this.state.sortColumn != "" && this.state.yColumn != this.state.sortColumn &&  this.state.XColumn != this.state.sortColumn){
            alert('ソート属性はラベル属性もしくは値属性のいずれかの属性で指定してください');
            return;
        }
        if(this.state.graphTypeId == 5 && orderBlankCheckFlag){
            alert('リストの表示順には0=表示しない、またはn=表示順のどちらかが必須です');
            return;
        }
        if(this.state.graphTypeId == 5 && duplicateCheckFlag){
            alert('重複しているリストの表示順があります');
            return;
        }
        if(this.state.graphTypeId == 5 && shortageCheckFlag){
            alert('ソート属性はリストの表示対象に指定する必要があります');
            return;
        }
        if(!limitSize || limitSize<1 || limitSize>5000){
            alert('リミット数は1~5000の間で指定してください');
            return;
        }
        fetch(Config.config.apiUrl + "/graph/"+path, {
            method: 'POST',
            body: JSON.stringify({
                graphId: this.state.graphId,
                graphTypeId: this.state.graphTypeId,
                graphName: this.state.title,
                sourceId: this.state.sourceId,
                graphYColumn: this.state.yColumn,
                graphXColumn: this.state.XColumn,
                limitSize:this.state.limitSize,
                sortModeMap:sortObj,
                graphDirection:this.state.graphDirection,
                columnMap:columnMap,
                editRestrictionFlag:this.state.editRestrictionFlag,
                groupByFlag:this.state.groupByFlag,
                aggregationType:this.state.aggregationType
            }),
            headers: new Headers({ 'Content-type': 'application/json' }),
        })
        .then(res => res.json())
        .then(res => {
            if(res.graphId){
                this.updateGraphListData(res,"preview" == path);
            }
            if(res.graphTypeId){
                const graphListClass = new GraphList(this.props.viewState);
                let graphListData = null;
                switch (res.graphTypeId) {
                    case 1:
                        graphListData = graphListClass.getComplexGraphData(res, res.dataList);
                        break;
                    case 2:
                        graphListData = graphListClass.getDoughnutGraphData(res, res.dataList);
                        break;
                    case 3:
                        graphListData = graphListClass.getBarGraphData(res, res.dataList);
                        break;
                    case 4:
                        graphListData = graphListClass.getLineGraphData(res, res.dataList);
                        break;
                    case 5:
                        graphListData = graphListClass.getAgGridData(res, res.dataList);
                        break;
                    case 6:
                        graphListData = graphListClass.getSingleListData(res, res.dataList);
                        break;
                    default:
                        break;
                }
                this.setState({graphListData:null});
                this.setState({graphListData:graphListData});
            }else{
                alert('処理に失敗しました。属性が正しく指定されているか確認してください。');
            }
        }).catch(error => {
            console.error('通信処理に失敗しました', error);
            alert('処理に失敗しました。');
        });
    }

    updateGraphListData(res,fetchFlag = false){
        const sourceId = res.layerSourceForm?.sourceId;
        const graphTypeId = res.graphTypeId;
        const title = res.graphName;
        const graphListTemplateValFormList = res.graphListTemplateValFormList;
        let queryText = res.queryText;
        let yColumn = "";
        let xColumn = "";
        let graphDirection = "";
        let aliasColumnNameList = {};
        let displayColumnNameList = {};
        
        for(let i=0;i<graphListTemplateValFormList.length;i++){
            if(graphTypeId != 5){
                const itemId = graphListTemplateValFormList[i].itemId;
                let itemValue = graphListTemplateValFormList[i].itemValue;
                try{
                    itemValue = itemValue.substring(0,itemValue.indexOf(" as ")!=-1?itemValue.indexOf(" as "):itemValue.length);
                    itemValue = itemValue.trim().replace(/"/g, '');
                }catch(e){}
                if(itemId == 100){
                    yColumn = itemValue;
                }else if(itemId == 101){
                    xColumn = itemValue;
                }else if(itemId == 11){
                    graphDirection = itemValue;
                }
            }else{
                const itemId = graphListTemplateValFormList[i].itemId;
                let itemValue = graphListTemplateValFormList[i].itemValue;
                if(itemId == 2){
                    try{
                        displayColumnNameList = JSON.parse(itemValue);
                    }catch(e){}
                }
                if(itemId == 100){
                    itemValue = itemValue.split(",");
                    for(let i=0;i<itemValue.length;i++){
                        try{
                            let tempItemValue = itemValue[i];
                            aliasColumnNameList[tempItemValue.substring(tempItemValue.indexOf(" as ")!=-1?tempItemValue.indexOf(" as ")+" as ".length:0).trim().replace(/"/g, '')]
                                                =   tempItemValue.substring(0,tempItemValue.indexOf(" as ")!=-1?tempItemValue.indexOf(" as "):tempItemValue.length).trim().replace(/"/g, '');
                        }catch(e){}
                    }
                }

            }
        }

        let sortColumn = "";
        let sortDirection = "";
        let limitSize = 15;
        let editRestrictionFlag = "";
        let groupByFlag = "0";
        let aggregationType = -1;
        if(queryText.indexOf("_auto_query_identifier_") > -1){

            if(queryText.indexOf("GROUP BY") > -1){
                groupByFlag = "1";
            }

            if(queryText.indexOf("SUM(") > -1){
                aggregationType = 1;
            }else if(queryText.indexOf("AVG(") > -1){
                aggregationType = 2;
            }else if(queryText.indexOf("MIN(") > -1){
                aggregationType = 3;
            }else if(queryText.indexOf("MAX(") > -1){
                aggregationType = 4;
            }else if(queryText.indexOf("COUNT(") > -1){
                aggregationType = 5;
            }

            try{
                if(queryText.indexOf("ORDER BY") !== -1){
                    sortDirection = queryText.substring(queryText.indexOf("ORDER BY")+"ORDER BY".length);
                    sortDirection = sortDirection.substring(0,sortDirection.indexOf(" LIMIT "));
                    sortDirection = sortDirection.trim().replace(/"/g, '');
                    if(sortDirection.indexOf(" DESC") !== -1){
                        sortDirection = "DESC";
                    }else if(sortDirection.indexOf(" ASC") !== -1){
                        sortDirection = "ASC";
                    }
                }
            }catch(e){}

            try{
                if(queryText.indexOf("ORDER BY") !== -1){
                    sortColumn = queryText.substring(queryText.indexOf("ORDER BY")+"ORDER BY".length);
                    sortColumn = sortColumn.substring(0,sortColumn.indexOf(" "+sortDirection+" "));

                    const layerSourceFormList = this.state.layerSourceFormList;
                    const layerSourceFormListIndex = layerSourceFormList.findIndex(layerSourceForm=>layerSourceForm.sourceId == sourceId);

                    //投稿レイヤのテキスト型数値を考慮したソート構文の場合
                    if(sortColumn.indexOf("CASE WHEN") !== -1){
                        Object.keys(layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList).map(key => {
                            if(sortColumn.indexOf('"'+layerSourceFormList[layerSourceFormListIndex]?.layerSourceFieldFormList[key]?.fieldName+'"') !== -1){
                                sortColumn = '"'+layerSourceFormList[layerSourceFormListIndex]?.layerSourceFieldFormList[key]?.fieldName+'"';
                            }
                        })
                    }
                    
                    sortColumn = sortColumn.trim().replace(/"/g, '');
                    
                    Object.keys(layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList).map(key => {
                        if(layerSourceFormList[layerSourceFormListIndex]?.layerSourceFieldFormList[key]?.alias == sortColumn){
                            sortColumn = layerSourceFormList[layerSourceFormListIndex]?.layerSourceFieldFormList[key]?.fieldName;
                        }
                    })
                }
            }catch(e){}

            try{
                limitSize = queryText.substring(queryText.indexOf(" LIMIT ")+" LIMIT ".length);
                limitSize = limitSize.replace(";","");
                limitSize = limitSize.trim().replace(/"/g, '');
                limitSize = Number(limitSize);
            }catch(e){}
        }else{
            //SE設定のグラフ編集の場合
            editRestrictionFlag = "1";
        }
        if(graphTypeId != 5){
            this.setState({
                graphId:res.graphId,
                title:title,
                graphTypeId:graphTypeId,
                sourceId:sourceId,
                yColumn:yColumn,
                XColumn:xColumn,
                sortColumn:sortColumn,
                sortDirection:sortDirection,
                limitSize:limitSize,
                graphDirection:graphDirection,
                editRestrictionFlag:editRestrictionFlag,
                groupByFlag:groupByFlag,
                aggregationType:aggregationType
            })

            if(!fetchFlag){
                this.props.updateFunc();
                alert("保存に成功しました");
                setThemeData(this.props.viewState);
            }

        }else{
            let newDisplayColumnNameList = {};
            try{
                Object.keys(displayColumnNameList).forEach(key=>{
                    newDisplayColumnNameList[aliasColumnNameList[key]] = displayColumnNameList[key];
                })
            }catch(e){}
            this.setState({
                graphId:res.graphId,
                title:title,
                graphTypeId:graphTypeId,
                sourceId:sourceId,
                displayColumnNameList:newDisplayColumnNameList,
                sortColumn:sortColumn,
                sortDirection:sortDirection,
                limitSize:limitSize,
                editRestrictionFlag:editRestrictionFlag,
                groupByFlag:false,
                aggregationType:-1
            })
            if(!fetchFlag){
                this.props.updateFunc();
                alert("保存に成功しました");
                setThemeData(this.props.viewState);
            }

        }
    }

    inputCustomComponent = (props) => {
        const inputHandler = (event) => {
            let { value, min, max } = event.target;
            if(value != ""){
                value = Math.max(Number(min), Math.min(Number(max), Number(value)));
            }
            const displayColumnNameList = {...this.state.displayColumnNameList};
            displayColumnNameList[props.fieldName] = value;
            this.setState({displayColumnNameList:displayColumnNameList});
        };
        return (
            <>
             <input
                onChange={inputHandler}
                value={props.order}
                type="number"
                min={0}
                max={100}
                style={{ color: "#000", width:"40%",scale:"1.3",position:"relative",left:"15%" }}/>
            </>
        )
    }

    render() {
        const graphListTypeFormList = this.state.graphListTypeFormList;
        const layerSourceFormList = this.state.layerSourceFormList;
        const layerSourceFormListIndex = layerSourceFormList.findIndex(layerSourceForm=>layerSourceForm.sourceId == this.state.sourceId);
        const graphListData = this.state.graphListData;
        const tableGraphListColumns = [];
        const tableGraphListData = [];
        if(this.state.graphTypeId == 5 && layerSourceFormListIndex > -1 && layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList){
            tableGraphListColumns.push({
                Header:"表示順(1~100)※0=非表示",
                accessor:"order"
            });
            tableGraphListColumns.push({
                Header:"属性",
                accessor:"attribute"
            });
            for(let i=0;i<layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList.length;i++){
                if(layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList[i].fieldName != "geom" && layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList[i].fieldName != "geometry"){
                    const order = this.state.displayColumnNameList[layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList[i].fieldName];
                    let props = {order:order!=null&&order!=undefined?order:i+1,fieldName:layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList[i].fieldName};
                    if(this.state.editRestrictionFlag == 1 || (this.state.graphId != null && this.state.graphId != undefined && this.state.graphId > 0)){
                        props = {order:order!=null&&order!=undefined?order:0,fieldName:layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList[i].fieldName};
                    }
                    const data = {
                        order:{
                            width:"50%",
                            customComponent:this.inputCustomComponent,
                            props:props
                        },
                        attribute:{
                            width:"50%",
                            value:layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList[i].alias
                        }
                    }
                    tableGraphListData.push(data)
                }
            }
        }
        return (
            <div style={{height:"80vh",width:"100%",display:"flex",boxSizing:"border-box",padding:"1.042vw 1.563vw"}}>
                <div style={{width:"58%",display:"flex",flexDirection:"column",marginRight:"2%"}}>
                <div style={{width:"100%",overflowY:"auto",paddingBottom:"20px"}}>

<div className={Style.z_li} style={{marginTop:"3%"}}>
    <span style={{display:"inline-block"}}><span style={{color:"red"}}>※</span>タイトル</span>
    <input
        onChange={e => {
            this.setState({title:e.target.value});
        }}
        value={this.state.title}
        style={{ color: "#000", width:"40%",scale:"1.3",position:"relative",left:"15%" }}/>
</div>
<div className={Style.z_li} style={{marginTop:"3%"}}>
    <span style={{display:"inline-block"}}><span style={{color:"red"}}>※</span>タイプ</span>
    <select
        disabled={this.state.editRestrictionFlag == 1 || (this.state.graphId != undefined && this.state.graphId != null && this.state.graphId >0)}
        onChange={e => {
            this.setState({graphTypeId:e.target.value,groupByFlag:"0",aggregationType:-1});
        }}
        style={{ color: "#000", width:"40%",scale:"1.3",position:"relative",left:"15%" }}>
            <option value={-1}></option>
        {Object.keys(graphListTypeFormList).map(key => (
            (graphListTypeFormList[key].editFlag == 1) && (
                <option key={"graphTypeId"+graphListTypeFormList[key].graphTypeId} value={graphListTypeFormList[key].graphTypeId} selected={graphListTypeFormList[key].graphTypeId == this.state.graphTypeId}>
                    {graphListTypeFormList[key].graphTypeName}
                </option>
            )
        ))}
    </select>
</div>
{this.state.graphTypeId == 3 && (<div className={Style.z_li} style={{marginTop:"3%"}}>
    <span style={{display:"inline-block"}}><span style={{color:"red"}}>※</span>グラフ方向</span>
    <select
        onChange={e => {
            this.setState({graphDirection:e.target.value});
        }}
        style={{ color: "#000", width:"40%",scale:"1.3",position:"relative",left:"15%" }}>
            <option value={"vertical"} selected={this.state.graphDirection == "vertical"}>縦棒グラフ</option>
            <option value={"horizontal"} selected={this.state.graphDirection == "horizontal"}>横棒グラフ</option>
    </select>
</div>)}
<div className={Style.z_li} style={{marginTop:"3%"}}>
    <span style={{display:"inline-block"}}><span style={{color:"red"}}>※</span>レイヤ</span>
    <select
        disabled={(this.state.editRestrictionFlag == 1 || (this.state.graphId != undefined && this.state.graphId != null && this.state.graphId >0))}
        onChange={e => {
            this.setState({sourceId:e.target.value,yColumn:"",XColumn:"",sortColumn:""});
        }}
        style={{ color: "#000", width:"40%",scale:"1.3",position:"relative",left:"15%" }}>
            <option value={-1}></option>
        {Object.keys(layerSourceFormList).map(key => (
            <option key={"sourceId"+layerSourceFormList[key].sourceId} value={layerSourceFormList[key].sourceId} selected={layerSourceFormList[key].sourceId == this.state.sourceId}>
                {layerSourceFormList[key].layerForm?.layerName}
            </option>
        ))}
    </select>
</div>
{this.state.graphTypeId < 5 && (<div className={Style.z_li} style={{marginTop:"3%"}}>
    <span style={{display:"inline-block"}}><span style={{color:"red"}}>※</span>値属性(数値)</span>
    <div style={{display:"flex",flexDirection:"column"}}>
    <select
        onChange={e => {
            this.setState({yColumn:e.target.value});
        }}
        style={{ color: "#000", width:"100%",scale:"1.3",position:"relative",left:"15%" }}>
            <option value={""}></option>
        {layerSourceFormListIndex > -1 && Object.keys(layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList).map(key => (
            <option key={"yColumn"+layerSourceFormList[layerSourceFormListIndex]?.layerSourceFieldFormList[key]?.fieldId} value={layerSourceFormList[layerSourceFormListIndex]?.layerSourceFieldFormList[key]?.fieldName} selected={layerSourceFormList[layerSourceFormListIndex]?.layerSourceFieldFormList[key]?.fieldName == this.state.yColumn}>
                {layerSourceFormList[layerSourceFormListIndex]?.layerSourceFieldFormList[key]?.alias}
            </option>
        ))}
    </select>
    {this.state.groupByFlag == "1" && this.state.editRestrictionFlag != 1 && (
    <div style={{ color: "#000", width:"50%",position:"relative",left:"39%",display:"flex",marginTop:"10px" }}>
        <div className={Style.customRadio} style={{width:"15%"}}>
            <input id={"radio-1"} name={"radio-1"} type="radio" value={1} checked={this.state.aggregationType == 1} onClick={(e)=>{
                this.setState({aggregationType:e.target.value});
            }}/>
            <label for={"radio-1"} className={Style.radioLabel}>SUM<br/>関数</label>
        </div>

        <div className={Style.customRadio} style={{width:"15%"}}>
            <input id={"radio-2"} name={"radio-2"} type="radio" value={2} checked={this.state.aggregationType == 2} onClick={(e)=>{
                this.setState({aggregationType:e.target.value});
            }}/>
            <label  for={"radio-2"} className={Style.radioLabel}>AVG<br/>関数</label>
        </div>

        <div className={Style.customRadio} style={{width:"15%"}}>
            <input id={"radio-3"} name={"radio-3"} type="radio" value={3} checked={this.state.aggregationType == 3} onClick={(e)=>{
                this.setState({aggregationType:e.target.value});
            }}/>
            <label  for={"radio-3"} className={Style.radioLabel}>MIN<br/>関数</label>
        </div>

        <div className={Style.customRadio} style={{width:"15%"}}>
            <input id={"radio-4"} name={"radio-4"} type="radio" value={4} checked={this.state.aggregationType == 4} onClick={(e)=>{
                this.setState({aggregationType:e.target.value});
            }}/>
            <label  for={"radio-4"} className={Style.radioLabel}>MAX<br/>関数</label>
        </div>

        <div className={Style.customRadio} style={{width:"15%"}}>
            <input id={"radio-5"} name={"radio-5"} type="radio" value={5} checked={this.state.aggregationType == 5} onClick={(e)=>{
                this.setState({aggregationType:e.target.value});
            }}/>
            <label  for={"radio-5"} className={Style.radioLabel}>COUNT<br/>関数</label>
        </div>
    </div>
    )}
    </div>
</div>
)}
{this.state.graphTypeId < 5 && (<div className={Style.z_li} style={{marginTop:"3%"}}>
    <span style={{display:"inline-block"}}><span style={{color:"red"}}>※</span>ラベル属性</span>
    <div style={{display:"flex",flexDirection:"column"}}>
    <select
        onChange={e => {
            this.setState({XColumn:e.target.value});
        }}
        style={{ color: "#000", width:"100%",scale:"1.3",position:"relative",left:"15%" }}>
            <option value={""}></option>
        {layerSourceFormListIndex > -1 && Object.keys(layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList).map(key => (
            <option key={"xColumn"+layerSourceFormList[layerSourceFormListIndex]?.layerSourceFieldFormList[key]?.fieldId} value={layerSourceFormList[layerSourceFormListIndex]?.layerSourceFieldFormList[key]?.fieldName} selected={layerSourceFormList[layerSourceFormListIndex]?.layerSourceFieldFormList[key]?.fieldName == this.state.XColumn}>
                {layerSourceFormList[layerSourceFormListIndex]?.layerSourceFieldFormList[key]?.alias}
            </option>
        ))}
    </select>
    {this.state.editRestrictionFlag != 1 && (
    <div style={{ color: "#000", width:"100%",position:"relative",left:"39%" }}>
        集約関数を使用する  
        <input id={"checkbox1"} type="checkbox" onChange={(e)=>{
                if(this.state.groupByFlag == "1"){
                    this.setState({groupByFlag:"0"});
                }else{
                    this.setState({groupByFlag:"1"});
                    this.setState({aggregationType:1});
                }
            }} className={Style.customCheckbox} value={this.state.groupByFlag} checked={this.state.groupByFlag=="1"} />
        <label for={"checkbox1"} style={{ position:"relative",top:"3px" }}></label>
        <br/><span style={{fontSize:".5em"}}>※集計関数を使用する場合地点情報の紐づけは行われません</span>
    </div>
    )}
    </div>
</div>
)}

{this.state.graphTypeId == 5 && (<div className={Style.z_li} style={{marginTop:"3%"}}>
    <span>※投稿系レイヤの添付ファイル表示には属性IDもしくは活動IDの表示が必須となります</span>
    <Table columns={tableGraphListColumns} data={tableGraphListData}/>
</div>)}
{this.state.editRestrictionFlag != 1 && (
<div className={Style.z_li} style={{marginTop:"3%"}}>
    <span style={{display:"inline-block"}}>ソート属性</span>
    <select
        onChange={e => {
            this.setState({sortColumn:e.target.value});
        }}
        style={{ color: "#000", width:"40%",scale:"1.3",position:"relative",left:"15%" }}>
            <option value={""}></option>
        {layerSourceFormListIndex > -1 && Object.keys(layerSourceFormList[layerSourceFormListIndex].layerSourceFieldFormList).map(key => (
            <option key={"sortColumn"+layerSourceFormList[layerSourceFormListIndex]?.layerSourceFieldFormList[key]?.fieldId} value={layerSourceFormList[layerSourceFormListIndex]?.layerSourceFieldFormList[key]?.fieldName} selected={layerSourceFormList[layerSourceFormListIndex]?.layerSourceFieldFormList[key]?.fieldName == this.state.sortColumn}>
                {layerSourceFormList[layerSourceFormListIndex]?.layerSourceFieldFormList[key]?.alias}
            </option>
        ))}
    </select>
</div>
)}
{this.state.editRestrictionFlag != 1 && (
<div className={Style.z_li} style={{marginTop:"3%"}}>
    <span style={{display:"inline-block"}}>ソート方向</span>
    <select
        onChange={e => {
            this.setState({sortDirection:e.target.value});
        }}
        style={{ color: "#000", width:"40%",scale:"1.3",position:"relative",left:"15%" }}>
            <option value={""}></option>
            <option value={"DESC"} selected={this.state.sortDirection == "DESC"}>降順</option>
            <option value={"ASC"} selected={this.state.sortDirection == "ASC"}>昇順</option>
    </select>
</div>
)}
{this.state.editRestrictionFlag != 1 && (
<div className={Style.z_li} style={{marginTop:"3%"}}>
        <span style={{display:"inline-block"}}><span style={{color:"red"}}>※</span>リミット数(1~5000)</span>
        <input
            min={1}
            max={5000}
            onChange={event => {
                let { value, min, max } = event.target;
                if(value != ""){
                    value = Math.max(Number(min), Math.min(Number(max), Number(value)));
                }
                this.setState({limitSize:value});
            }}
            value={this.state.limitSize}
            type="number"
            style={{ color: "#000", width:"40%",scale:"1.3",position:"relative",left:"15%" }}/>
</div>
)}

</div>
<div className={Style.z_backButton} style={{width:"100%",height:"10%",display:"flex",justifyContent:"flex-end"}}>
<button
                onClick={evt => {
                    this.previewOrRegister("preview");
                }}
            >
                <span>サンプル表示</span>
            </button>
</div>
                </div>
                <div style={{width:"40%"}}>
                    <div style={{width:"100%",height:"90%"}}>
                        {graphListData && graphListData.typeId == 1 &&  (
                        <Bar options={graphListData.options} data={graphListData.data} />
                        )}
                        {graphListData && graphListData.typeId == 2 &&  (
                        <Doughnut options={graphListData.options} data={graphListData.data} plugins={[graphListData.custom.centerText]} />
                        )}
                        {graphListData && graphListData.typeId == 3 &&  (
                        <Bar options={graphListData.options} data={graphListData.data} />
                        )}
                        {graphListData && graphListData.typeId == 4 &&  (
                        <Bar options={graphListData.options} data={graphListData.data} />
                        )}
                        {graphListData && graphListData.typeId == 5 &&  (
                        <div className="ag-theme-alpine" style={{width: "100%",height:"90%"}}>
                            <AgGridReact
                                rowData={graphListData.rowData}
                                columnDefs={graphListData.columnDefs}
                                defaultColDef={{
                                    resizable: true,
                                }}
                            >
                            </AgGridReact>
                        </div>
                        )}
                    </div>
                    <div style={{width:"100%",height:"10%",display:"flex",justifyContent:"flex-end",boxSizing:"border-box",paddingRight:"2.604vw"}} className={Style.themeItem}>
                                <button
                                    className={Style.backButton}
                                    style={{marginRight:"0",marginLeft:"1.042vw"}}
                                    onClick={evt => {
                                        this.previewOrRegister("register");
                                    }}
                                >
                                    <span>保存</span>
                                </button>
                                <button
                                    className={Style.backButtonWhite}
                                    style={{marginRight:"0",marginLeft:"1.042vw"}}
                                    onClick={evt => {
                                        this.props.backFunc();
                                    }}
                                >
                                    <span>戻る</span>
                                </button>
                    </div>
                </div>
            </div>
        );
    }
}

export default withTranslation()(withTheme(GraphListRegisterScreen));