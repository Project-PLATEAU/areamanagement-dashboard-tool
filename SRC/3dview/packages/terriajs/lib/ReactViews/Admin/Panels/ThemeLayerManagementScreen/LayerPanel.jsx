import { observer } from "mobx-react";
import React, { useEffect } from "react";
import { withTranslation } from "react-i18next";
import { withTheme } from "styled-components";
import Icon, { StyledIcon } from "../../../../Styled/Icon";
import Spacing, { SpacingSpan } from "../../../../Styled/Spacing";
import Text from "../../../../Styled/Text";
import Box from "../../../../Styled/Box";
import Input from "../../../../Styled/Input";
import Button, { RawButton } from "../../../../Styled/Button";
import Styles from "./layer-panel.scss";
import { action } from "mobx";
import Config from "../../../../../customconfig.json";
import { method } from "lodash-es";
import { runInAction } from "mobx";
import {setThemeData} from '../../../../Models/GraphList/GraphList';
import { Padding } from "terriajs-protomaps";

@observer
class LayerPanel extends React.Component {
  static displayName = "LayerPanel";

  constructor(props) {
    super(props);
    this.state = {data: {}};
  }

  // 初期描画後
  componentDidMount() {
    setTimeout(() => this.setState({ isAnimatingOpen: false }), 0);
    const layer = this.props.viewState.layer;
    this.fetchData(layer.layerId);
    this.setState({layerData: layer});
  }

  fetchData = (layerId) => {
    const apiUrl = Config.config.apiUrl + "/layers/postLayerAttribute/" + layerId;
    fetch(apiUrl)
    .then((res) => res.json())
    .then(res => {
        // console.log(res);
        if(!res.status){
            this.setState({attributedata: res});
        }
    })
    const url = Config.config.apiUrl + "/layers/admin/getPostLayer/" + layerId;
    fetch(url)
    .then((res) => res.json())
    .then(res => {
        // console.log(res);
        let flag = false;
        if(!res.status && res.length > 0){
          flag = true;
        }
        this.setState({flag: flag});
    })
  }

  checkStringByte(str) {
    const len = encodeURI(str).replace(/%../g, "*").length;
    if(len > 63){
        str = this.getDivisionString(str,63);
    }
    return str;
  }

  getDivisionString(str,len){
      let arr = str.split('');
      let cnt = 0;
      let ret = "";
      for(let i=0;i<arr.length;i++){
          let enc = escape(arr[i]);
          if(enc.length <= 3){
              cnt += 1;
          }else{
              cnt += 2;
          }
          if(cnt > len){
              return ret;
          }
          ret += arr[i];
      }
      return ret;
  }

  render() {
    const data = this.state.attributedata;
    const layerData = this.props.viewState.layer;
    const dataFlag = this.state.flag;
    return(
      <div 
        className={Styles.ModalWrapper}
        onClick={this.close}
      >
        <div 
          className={Styles.Modal}
          onClick={e => e.stopPropagation()}
        >
          <Text className={Styles.title} subHeading bold textAlignCenter>レイヤ編集</Text>

          <Spacing bottom={4} />

          <Box textAlignCenter style={{maxHeight: "80%", overflowY:"auto",width:"100%",flex:"1"}}>
            <div style={{width:"100%"}}>
            <table className={Styles.z_table}>
              <thead>
                <tr style={{position:"sticky"}}><td style={{width:"75px", textAlign:"right"}}>レイヤ名</td>
                <td colSpan={4}>
                  <Input
                  type="text"
                  defaultValue={layerData.layerName}
                  width={100}
                  id="layerName"
                  style={{border:"1px solid lightgrey"}}
                /></td></tr>

                
                <tr style={{position:"sticky", height:"50px"}}><td style={{width:"75px", textAlign:"right"}}>アイコン</td>
                <td colSpan={4}>
                  <select 
                    id="icon"
                    style={{border:"1px solid lightgrey", width:"100%"}}
                  >
                    {Object.keys(Config.postLayerIconCorrespondenceTable).map(key => (
                        <option value={Config.postLayerIconCorrespondenceTable[key]} selected={layerData["iconPath"].includes(Config.postLayerIconCorrespondenceTable[key])}>
                          {key}
                        </option>
                    ))}
                  </select>
                  </td></tr>

                <tr style={{position:"sticky"}}><td colSpan={4}></td><td css={'text-align:center;background:#f2f2f2;color:#999;'}>表示順</td></tr>
              </thead>
              <tbody>
              
              {data?.map((value1) =>
                <tr height="50">
                        <td style={{width:"75px", textAlign:"right"}} css={'padding:3px'}>
                          属性名
                        </td>
                        <td  css={'padding:3px'}>
                          <Input
                              type="text"
                              className={"itemName"}
                              defaultValue={value1["itemName"]}
                              style={{border:"1px solid lightgrey", height:"25px"}}
                              onChange={e => {
                                e.target.value = this.checkStringByte(e.target.value);
                                value1["itemName"] = e.target.value
                              }}
                          />
                        </td>
                        <td style={{width:"75px", textAlign:"right"}} css={'padding:3px'} >
                          データ型
                        </td>
                        <td style={{overflowX:"auto"}} >
                          {dataFlag == false && (
                          <select 
                            id="itemType"
                            style={{border:"1px solid lightgrey"}}
                            onChange={e => {value1["itemType"] = parseInt(e.target.value)}}
                          >
                            <option value={1} selected={value1["itemType"] == 1}>テキスト（小）</option>
                            <option value={2} selected={value1["itemType"] == 2}>テキスト（大）</option>
                            <option value={3} selected={value1["itemType"] == 3}>数値</option>
                            <option value={4} selected={value1["itemType"] == 4}>写真</option>
                            <option value={5} selected={value1["itemType"] == 5}>日付</option>
                          </select>)}
                          {dataFlag == true && (
                            <Box
                            style={{border:"1px solid lightgrey"}}
                            >
                            {value1["itemType"] == 1 && (<>テキスト（小）</>)}
                            {value1["itemType"] == 2 && (<>テキスト（大）</>)}
                            {value1["itemType"] == 3 && (<>数値</>)}
                            {value1["itemType"] == 4 && (<>写真</>)}
                            {value1["itemType"] == 5 && (<>日付</>)}
                            </Box>
                          )}
                        </td>
                        <td style={{overflowX:"auto", width:"100px"}} css={'padding:3px'}>
                          <Input
                              type="number"
                              className={"dispNum"}
                              min="0"
                              defaultValue={value1["dispOrder"]}
                              style={{border:"1px solid lightgrey", height:"25px"}}
                              onChange={e => {value1["dispOrder"] = parseInt(e.target.value)}}
                            />
                        </td>
                </tr>
              )}
              </tbody>
            </table>
            <div style={{marginLeft:"30px"}}>※属性名は63byte以下までしか入力できません</div>
            </div>
          </Box>

          <Spacing bottom={2} />

          <Box right>
            <Button className={Styles.z_button1} onClick={this.register}>保存</Button>
            <Button className={Styles.z_button2} style={{marginLeft:"20px"}} onClick={this.backPage}>戻る</Button>
          </Box>
        </div>
      </div>
    );
  }
  
  backPage = () =>{
    console.log("back")
    this.props.viewState.hideLayerPanel();
    this.setLayerData();
  }

  register = () => {
    console.log("register");
    const layerData = this.state.layerData;
    const data = this.state.attributedata;
    const layerName = document.getElementById("layerName").value;
    const icon = document.getElementById("icon").value;
    console.log(data);
    // console.log(layerName);
    const duplicatedList = data.filter((attribute1, index1) => {
      return data.find((attribute2, index2) => {
        return attribute1.dispOrder == attribute2.dispOrder && index1 != index2;
      });
    })
    if(duplicatedList.length > 0){
      alert("重複している表示順があります");
      return;
    } 
    console.log(duplicatedList);
    const apiUrl = Config.config.apiUrl + "/layers/admin/register/" + layerData.layerId;
    fetch(apiUrl, {
      method: 'POST',
      body: JSON.stringify({
        iconPath: icon,
        layerGraphCooporationFormList: layerData.layerGraphCooporationFormList,
        layerId: layerData.layerId,
        layerName: layerName,
        layerSettings: layerData.layerSettings,
        layerType: layerData.layerType,
        placeHolderFlag: layerData.placeHolderFlag,
      }),
      headers: new Headers({ 'Content-type': 'application/json' }),
    })
    const url = Config.config.apiUrl + "/layers/registerAttribute/" + layerData.layerId;
    fetch(url, {
      method: 'POST',
      body: JSON.stringify(data),
      headers: new Headers({ 'Content-type': 'application/json' }),
    })
    .then((res) => res.json())
    .then(res => {
        // console.log(res);
        if(!res.status){
            this.setState({attributedata: res});
        }
    })
  }

  setLayerData = () =>{
    this.props.setLayer();
  }

}

export default withTranslation()(withTheme(LayerPanel));
