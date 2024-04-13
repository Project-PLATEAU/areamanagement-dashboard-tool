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
import Select from "../../../../Styled/Select";
import Button, { RawButton } from "../../../../Styled/Button";
import { BaseModel } from "../../../../Models/Definition/Model";
import Config from "../../../../../customconfig.json";
import { action, runInAction } from "mobx";
import { Link } from "react-router-dom";
import Style from "../common.scss";
import {
    MenuLeft,
    Nav,
    ExperimentalMenu
  } from "../../../StandardUserInterface/customizable/Groups";
import StandardUserInterface from "../../../StandardUserInterface/StandardUserInterface.jsx";
import {setThemeData} from '../../../../Models/GraphList/GraphList';
import {setAllThemeInformation} from '../../../../Models/Theme/Theme';
import {AgGridColumn, AgGridReact} from 'ag-grid-react';
import LayerPanel from "./LayerPanel";
import ThemeLayerTable from './ThemeLayerTable'
import LayerTable from './LayerTable'
import ThemeTable from './ThemeTable'
import Style1 from "./style.scss";

/**
 * テーマ・レイヤ公開管理画面
 */
@observer
class ThemeLayerManagementScreen extends React.Component {
    static displayName = "DashboardManagementScreen";

    static propTypes = {
        terria: PropTypes.object.isRequired,
        viewState: PropTypes.object.isRequired,
        theme: PropTypes.object,
        t: PropTypes.func.isRequired
    };

    constructor(props) {
        super(props);
        const columnDefsTheme = [
          {field: 'publishFlag', headerName: '公開', width: "70px",customComponent:this.publishCheckCustomComponent, textAlign:"center"},
          {field: 'postFlag', headerName: '投稿', width: "70px",customComponent:this.postCheckCustomComponent, textAlign:"center"},
          {field: 'themeGroupName', headerName: 'テーマグループ', width: "200px"},
          {field: 'themeName', headerName: 'テーマ', width: "200px"},
          {field: 'dispOrder', headerName: '表示順', width: "75px", textAlign:"center"},
        ]
        const columnDefsLayer = [
          {field: 'publishFlag', headerName: '公開', width: "70px", customComponent:this.dispCheckCustomComponent, textAlign:"center"},
          {field: 'layerName', headerName: 'レイヤ名称', width: "250px"},
          {field: 'button', headerName: '', width: "75px", customComponent:this.editButtonCustomComponent, props:this.viewState, textAlign:"center"},
        ]
        const columnDefsThemeLayer = [
          {field: 'postFlag', headerName: '投稿', width: "70px", customComponent:this.postRadioCustomComponent, textAlign:"center"},
          {field: 'layerName', headerName: 'レイヤ名称', width: "300px", textAlign:"left"},
          {field: 'dispOrder', headerName: '表示順', width: "75px", textAlign:"center"},
          {field: 'button', headerName: '', width: "75px", customComponent:this.deleteButtonCustomComponent, textAlign:"center"},
        ]
        this.state = {
            columnDefsTheme: columnDefsTheme,
            columnDefsLayer: columnDefsLayer,
            columnDefsThemeLayer: columnDefsThemeLayer,
            rowDataTheme: [],
            rowDataLayer: [],
            rowDataThemeLayer: [],
            deleteThemeLayer:[],
        }
    }

    componentDidMount() {
        let firstThemeId = 0;
        const apiThemeUrl = Config.config.apiUrl + "/theme/admin/all";
        fetch(apiThemeUrl)
        .then((res) => res.json())
        .then(res => {
            console.log(res);
                firstThemeId = res[0]["themeId"];
                this.setState({rowDataTheme: res});
                const flag = res[0].postFlag;
                this.setState({selectedThemePostFlag: flag});
                const apiThemeLayerUrl = Config.config.apiUrl + "/layers/" + firstThemeId;
                fetch(apiThemeLayerUrl)
                .then((res) => res.json())
                .then(res => {
                    console.log(res);
                    let themeLayerData = [];
                    if(!res.status) themeLayerData = res;
                    this.setState({rowDataThemeLayer: themeLayerData});
                    this.setState({selectedTheme: firstThemeId});
                })
        })
        const apiLayerUrl = Config.config.apiUrl + "/layers/getAll";
        fetch(apiLayerUrl)
        .then((res) => res.json())
        .then(res => {
            console.log(res);
            let layerData = [];
            if(!res.status) layerData = res;
            this.setState({rowDataLayer: layerData});
        })
    }

    getThemeLayer = (themeId) => {
        console.log("change");
        if(themeId == "") return;
        const apiThemeLayerUrl = Config.config.apiUrl + "/layers/" + themeId;
        fetch(apiThemeLayerUrl)
        .then((res) => res.json())
        .then(res => {
            // console.log(res);
            let themeLayerData = [];
            this.setState({rowDataThemeLayer: themeLayerData});
            if(!res.status) themeLayerData = res;
            this.setState({rowDataThemeLayer: themeLayerData});
        })
        this.setState({selectedTheme: themeId});
        const index = this.state.rowDataTheme.findIndex(function(theme){
            return theme.themeId == themeId
        })
        this.setState({selectedThemePostFlag: this.state.rowDataTheme[index].postFlag});
        this.setState({deleteThemeLayer: []});
    }

    addLayer = () => {
        console.log("add");
        const selectedLayers = document.getElementsByClassName("display");
        let themeLayer = this.state.rowDataThemeLayer;
        console.log(themeLayer);
        let layerList = this.state.rowDataLayer;
        // console.log(layerList);
        let latestDispOrder = 0;
        // console.log(latestDispOrder);
        for(let i = 0; i < Object.keys(selectedLayers).length; i++){
            const layerId = layerList[selectedLayers[i].value].layerId
            if(selectedLayers[i].checked == true){
                let check;
                if(themeLayer.length > 0){
                    latestDispOrder = themeLayer[themeLayer.length - 1].dispOrder;
                    check = themeLayer.find(layer => { if(layer.layerId == layerId){
                        return true;
                    }})
                }
                if(check){
                    alert("すでにレイヤが存在します");
                    return;
                }
                let newLayer = {};
                latestDispOrder++;
                newLayer.postFlag = "0";
                newLayer.dispOrder = latestDispOrder;
                newLayer.layerForm = layerList[selectedLayers[i].value];
                newLayer.layerId = layerId;
                newLayer.themeId = this.state.selectedTheme;
                // console.log(newLayer);
                themeLayer.push(newLayer);
            }
        }
        this.setState({rowDataThemeLayer: themeLayer});
    }

    registerTheme = () => {
        console.log("register Theme");
        const publishFlags = document.getElementsByClassName("themeCheckPublish");
        const postFlags = document.getElementsByClassName("themeCheckPost");
        const themeNames = document.getElementsByClassName("themeName");
        const themeGroupNames = document.getElementsByClassName("themeGroupName");
        const dispOrders = document.getElementsByClassName("dispNumForTheme");
        const theme = this.state.rowDataTheme;
        for(let i = 0; i < Object.keys(theme).length; i++){
            let publishFlag = 0;
            let postFlag = 0;
            if(publishFlags[i].checked == true) publishFlag = 1;
            if(postFlags[i].checked == true) postFlag = 1;
            if(theme[i].publishFlag != publishFlag || theme[i].postFlag != postFlag){
                theme[i].publishFlag = publishFlag;
                theme[i].postFlag = postFlag;
            }
            theme[i].dispOrder = dispOrders[i].value;
            theme[i].themeName = themeNames[i].value;
            theme[i].themeGroupName = themeGroupNames[i].value;
            console.log("update theme" + theme[i].themeName + " id=" + theme[i].themeId);
            // console.log(theme[i]);
            const apiUrl = Config.config.apiUrl + "/theme/update";
            fetch(apiUrl, {
                method: 'POST',
                body: JSON.stringify(theme[i]),
                headers: new Headers({ 'Content-type': 'application/json' }),
            })
            .then((res) => res.json())
            .then(res => {
                // console.log(res);
                this.resetTheme();
            })
        }
    }
    
    registerThemeLayer = () => {
        console.log("register ThemeLayer");
        const themeLayer = this.state.rowDataThemeLayer;
        const themeId = this.state.selectedTheme;
        const dispOrders = document.getElementsByClassName("dispNumForThemeLayer");
        const deleteList = this.state.deleteThemeLayer;
        for(let i = 0; i < Object.keys(themeLayer).length; i++){
            const postFlag = document.getElementById("radio" + themeLayer[i].layerId);
            if(postFlag){
                if(postFlag.checked == true) themeLayer[i].postFlag = '1';
                if(postFlag.checked == false) themeLayer[i].postFlag = '0';
            }
            themeLayer[i].dispOrder = dispOrders[i].value;
            const flag = deleteList.findIndex(function(val){
                return themeLayer[i].layerId == val.layerId
            })
            if(flag >= 0) deleteList.splice(flag, 1);
        }
        // console.log(themeLayer);
        // console.log(deleteList);
        const apiUrl = Config.config.apiUrl + "/layers/updateThemeLayer/" + themeId;
        fetch(apiUrl, {
            method: 'POST',
            body: JSON.stringify(themeLayer),
            headers: new Headers({ 'Content-type': 'application/json' }),
        })
        .then((res) => res.json())
        .then(res => {
            console.log(res);
            if(deleteList.length > 0){
                const url = Config.config.apiUrl + "/layers/deleteThemeLayer/" + themeId;
                fetch(url, {
                    method: 'POST',
                    body: JSON.stringify(deleteList),
                    headers: new Headers({ 'Content-type': 'application/json' }),
                })
                .then((res) => res.json())
                .then(res => {
                    // console.log(res);
                    if(res.status == 201){
                        this.getThemeLayer(themeId);
                        this.setState({deleteThemeLayer: []});
                    }
                })
            }else{
                this.getThemeLayer(themeId);
            }
        })
    }

    resetTheme = () => {
        console.log("theme reset");
        const apiThemeUrl = Config.config.apiUrl + "/theme/admin/all";
        fetch(apiThemeUrl)
        .then((res) => res.json())
        .then(res => {
            console.log(res);
            this.setState({rowDataTheme: []});
            if(!res.status){
                this.setState({rowDataTheme: res});
            }
        })
    }
    
    resetThemeLayer = () => {
        console.log("themeLayer reset");
        const themeId = this.state.selectedTheme;
        this.getThemeLayer(themeId);
    }

    setEdittedLayer = () => {
        console.log("set layer");
        const apiLayerUrl = Config.config.apiUrl + "/layers/getAll";
        fetch(apiLayerUrl)
        .then((res) => res.json())
        .then(res => {
            console.log(res);
            let layerData = [];
            if(!res.status) layerData = res;
            this.setState({rowDataLayer: layerData});
        })
    }

    render(){
        return(
            <>
            {this.props.viewState.showLayer && (
                <LayerPanel 
                terria={this.props.terria} 
                viewState={this.props.viewState}
                setLayer={this.setEdittedLayer}
                />
            )}
            <div style={{height:"96%",width:"100%"}}>
                    <Box 
                        column
                        centered
                        displayInlineBlock
                        css={`
                        padding: 1em;
                        width: 50%;
                        height:50%;
                    `}>
                        <div style={{height:"100%"}}>
                            <div style={{display:"flex"}}>
                                <Box col8><Text className={Style1.title} extraExtraLarge={true} bold={true}>テーマ管理</Text></Box>
                                <Box col4 right>
                                    <button className={Style1.z_button1} onClick={this.registerTheme}><span>保存</span></button>
                                    <button className={Style1.z_button2} style={{marginLeft:"20px"}} id="resetTheme" onClick={this.resetTheme}><span>元に戻す</span></button>
                                </Box>
                            </div>
                            <Spacing bottom={3} />
                            <div style={{height: "90%", overflowY:"auto"}}>
                                <ThemeTable rowData={this.state.rowDataTheme} columnDefs={this.state.columnDefsTheme}/>
                            </div>
                        </div>
                    </Box>
                    <Spacing bottom={3} />
                    <Box
                        displayInlineBlock
                        css={`
                        display:flex;
                        padding: 1em;
                        width: 100%;
                        height:50%;
                    `}>
                        <Box col4 column style={{flex:"1"}}>
                            <div>
                                <Text className={Style1.title} extraExtraLarge={true} bold={true}>レイヤ管理</Text>
                            </div>
                            <Spacing bottom={3} />
                            <div style={{height: "100%", width: "100%", overflowY:"auto"}}>
                                <LayerTable rowData={this.state.rowDataLayer} columnDefs={this.state.columnDefsLayer}/>
                            </div>
                        </Box>
                        <Box centered>
                            <div style={{textAlign:"center", margin:"2em"}}>
                                <Text className={Style1.title1} extraExtraLarge={true}>表示追加</Text>
                                <img className={Style1.img}  src={Config.config.plateauPath + "/sample/arrow.png"} onClick={this.addLayer}/>
                            </div>
                        </Box>
                        <Box col6 column>
                            <Text className={Style1.title} extraExtraLarge={true} bold={true}>テーマ・レイヤ管理</Text>
                            <Spacing bottom={3} />
                            <div style={{display:"flex"}}>
                                <Box col8 className={Style1.z_select}>
                                    <select 
                                        id="selectedTheme"
                                        onChange={e => {
                                            this.getThemeLayer(e.target.value)
                                        }}
                                    >
                                    <option value=""></option>
                                    {Object.keys(this.state.rowDataTheme).map(key => (
                                      <option value={this.state.rowDataTheme[key]["themeId"]} selected={key == 0}>{this.state.rowDataTheme[key]["themeName"]}</option>
                                    ))}
                                    </select>
                                </Box>
                                <Box col4 right>
                                    <button className={Style1.z_button1} onClick={this.registerThemeLayer}><span>保存</span></button>
                                    <button className={Style1.z_button2} style={{marginLeft:"20px"}} id="resetThemeLayer" onClick={this.resetThemeLayer}><span>元に戻す</span></button>
                                </Box>
                            </div>
                            <Spacing bottom={1} />
                            <div style={{maxHeight: "100%", width: "100%", overflowY:"auto"}}>
                                <ThemeLayerTable rowData={this.state.rowDataThemeLayer} columnDefs={this.state.columnDefsThemeLayer}/>
                            </div>
                        </Box>
                    </Box>
            </div>
            </>
        )
    }

    publishCheckCustomComponent = (index) =>{
        let themeData = this.state.rowDataTheme;
        // console.log(themeData[index]['publishFlag']);
        return(
            <>
                <input type="checkbox" defaultChecked={themeData[index].publishFlag == '1'} className="themeCheckPublish"
                    value={themeData[index]["publishFlag"]} 
                    onChange={(e) => {publishCheckButtonClickHandler(index, e.target.value)}}></input>
            </>
        )
    }

    postCheckCustomComponent = (index) =>{
        let themeData = this.state.rowDataTheme;
        // console.log(themeData[index]['postFlag']);
        return(
            <>  
                <input type="checkbox" defaultChecked={themeData[index].postFlag == '1'} className="themeCheckPost"
                    value={themeData[index]["postFlag"]} 
                    onChange={(e) => {publishCheckButtonClickHandler(index)}}></input>
            </>
        )
    }

    dispCheckCustomComponent = (index) =>{
        return(
            <>
                <input type="checkbox" className="display" value={index}></input>
            </>
        )
    }

    editButtonCustomComponent = (index) =>{
        const editButtonClickHandler = (index) => {
            // 編集画面に遷移
            const layers = this.state.rowDataLayer;
            const layer = layers[index];
            this.props.viewState.showLayerPanel(layer);
        };
        return (
            <>
                <button onClick={(e) => {editButtonClickHandler(index)}} style={{background:"#ccddf5",color:"#1b63ee",padding:"5px",border:"1px solid #1b63ee", borderRadius:"3px"}}>
                    <span>編集</span>
                </button>
            </>
        )
    }

    postRadioCustomComponent = (index) =>{
        let layerThemeData = this.state.rowDataThemeLayer;
        const layerForm = layerThemeData[index].layerForm
        const displayRadioButtonClickHandler = (index) => {
            layerThemeData = this.state.rowDataThemeLayer;
            switch(document.getElementById("radio" + layerThemeData[index].layerId).checked){
                case false:
                    layerThemeData[index].postFlag = "0";
                    break;
                case true:
                    layerThemeData[index].postFlag = "1";
                    break;
            }
            this.setState({rowDataThemeLayer: layerThemeData});
        };
        // console.log(layerThemeData[index] + "   " + layerThemeData[index].postFlag);
        // console.log(layerThemeData[index].postFlag)
        return(
            <>
                {this.state.selectedThemePostFlag == "1" && layerForm.layerType == 1 && (
                <input type="radio" defaultChecked={layerThemeData[index].postFlag == '1'} id={"radio" + layerThemeData[index].layerId} name="postFlag"
                    value={layerThemeData[index].postFlag}></input>
                )}
                {this.state.selectedThemePostFlag == "0" && (<></>)}
            </>
        )
    }
    
    deleteButtonCustomComponent = (index) =>{
        const deleteButtonClickHandler = () => {
            let layerThemeData = this.state.rowDataThemeLayer;
            let deleteList = this.state.deleteThemeLayer;
            const flag = deleteList.find(function(val){
                return layerThemeData[index].layerId == val.layerId
            })
            if(!flag) deleteList.push(layerThemeData[index]);
            layerThemeData.splice(index, 1);
            let layerTheme = [...layerThemeData];
            this.setState({rowDataThemeLayer: layerTheme})
            this.setState({deleteThemeLayer: deleteList})
            for(let i = 0; i < Object.keys(layerTheme).length; i++){
                let row = document.getElementById("dispOrder" + i);
                row.value = layerTheme[i].dispOrder;
            }
        };
        return (
            <>
                <button onClick={deleteButtonClickHandler} style={{background:"#f9dbd8",color:"#de2c17",padding:"5px",border:"1px solid #de2c17", borderRadius:"3px"}}>
                    <span>削除</span>
                </button>
            </>
        )
    }
}


export default withTranslation()(withTheme(ThemeLayerManagementScreen));