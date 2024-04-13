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
import CustomStyle from "./post-layer-panel.scss";
import Config from "../../../../../customconfig.json";
import { action, runInAction } from "mobx";
import {refreshThemeData} from "../../../../Models/GraphList/GraphList";
import { select } from "d3-selection";
import { validateBBox } from "@turf/helpers";

/**
 * 投稿レイヤ登録・更新画面
 */
@observer
class PostLayerPanel extends React.Component {
    static displayName = "PostLayerPanel";

    static propTypes = {
        terria: PropTypes.object.isRequired,
        viewState: PropTypes.object.isRequired,
        theme: PropTypes.object,
        t: PropTypes.func.isRequired
    };

    constructor(props) {
        super(props);
        let buttonText = "登録";
        if(props.terria.featureId > 0) buttonText = "更新";
        this.state = {
            viewState: props.viewState,
            terria: props.terria,
            isAnimatingOpen: false,
            attachementList: [],
            attachementUploadList: [],
            clickLatLong: props.terria.clickLatLong,
            featureId: 0,
            parentFeatureId: 0,
            geom: "",
            buttonText: buttonText,
            items: [],
            postUserId:0,
            publishFlag:0,
            registerUpdateFlag:false

        };
        this.handleInputChange = this.handleInputChange.bind(this);
    }

    // 初期描画後
    componentDidMount() {
        setTimeout(() => this.setState({ isAnimatingOpen: false }), 0);
        //Apiで項目を取得
        //layerItemInfoに格納
        const param = "?themeId=" + this.props.viewState.selectedThemeId;
        const apiUrl = Config.config.apiUrl + "/layers/postLayerAttribute" + param;
        fetch(apiUrl)
        .then((res) => res.json())
        .then(res => {
            const attribute = res.attribute;
            const layerInfo = res.layerInfo;
            this.setState({layerName: layerInfo.layerName});
            this.setState({layerId: layerInfo.layerId});
            this.setState({layerAttribute: attribute});
            this.fetchFeatureData(this.props.terria.featureId);
        })
        document.getElementById("customloader").style.display = "none";
    }

    /**
     * 添付ファイルのアップロード
     * @param {event} event
     */
    handleInputChange(event) {
        let attachementList = this.state.attachementList;
        let attachementUploadList = this.state.attachementUploadList;
        let files = event.target.files;
        if (files[0].size > 10485760) {
            alert("10M以下のファイルをアップロードできます");
            return false;
        }
        const allowExts = new Array('jpg', 'jpeg', 'JPG', 'JPEG', 'png', 'PNG');
        let ext = this.getExt(files[0].name).toLowerCase();
        if (allowExts.indexOf(ext) === -1){
            alert("許可されていない拡張子です");
            return false;
        }

        let reader = new FileReader();

        reader.readAsDataURL(files[0]);

        reader.onload = (e) => {
            // 描画用オブジェにセット
            let key = this.state.fileNum;
            attachementList[key] = {};
            attachementList[key]["attachmentFileName"] = URL.createObjectURL(files[0]);
            // アップロード対象にセット
            attachementUploadList[key] = { "attachmentFileName": files[0].name, "uploadFile": files[0] };

            this.setState({
                attachementList: attachementList,
                attachementUploadList: attachementUploadList
            })
        }

    }

    /**
     * 拡張子を取得
     * @param {string} ファイル名
     * @return {string} 拡張子
     */
    getExt(filename){
        var pos = filename.lastIndexOf('.');
        if (pos === -1) return '';
        return filename.slice(pos + 1);
    }

    /**
     * 添付ファイルの削除
     * @param {number} 削除対象のindex
     */
    deleteAttachement(index) {
        // console.log(index);
        let attachementList = this.state.attachementList;
        let attachementDeleteList = this.state.attachementDeleteList;
        let attachementUploadList = this.state.attachementUploadList;
        // idが付与されている場合は削除対象にセット
        if (index >= 0 && attachementList[index] && attachementList[index].id) {
            attachementDeleteList.push({ id: attachementList[index].id, featureId: this.state.featureId, attachmentFileName: attachementList[index].attachmentFileName });
        }
        delete attachementList[index];
        // attachementList = Object.values(attachementList);
        // attachementList = attachementList.filter(Boolean);
        delete attachementUploadList[index];
        // attachementUploadList = Object.values(attachementUploadList);
        // attachementUploadList = attachementUploadList.filter(Boolean);
        this.setState({
            attachementList: attachementList,
            attachementUploadList: attachementUploadList,
            attachementDeleteList: attachementDeleteList
        })
    }

    /**
     *投稿レイヤ情報の取得
     * @param {number} 活動ID
     */
    async fetchFeatureData(featureId) {
        // console.log("get" + featureId);
        if (featureId > 0) {
            try {
                const param = "?themeId=" + this.props.viewState.selectedThemeId;
                const apiUrl = Config.config.apiUrl + "/layers/postLayerAttribute" + param;
                const res = await fetch(apiUrl)
                const resJson = await res.json();
                const attribute = resJson.attribute;
                this.setState({layerAttribute: attribute});
                const response = await fetch(Config.config.apiUrl + "/layers/getPostLayer?featureId=" + featureId);
                const data = await response.json();
                if (data.featureId) {
                    const items = [data.item1, data.item2, data.item3, data.item4, data.item5, data.item6, data.item7, data.item8, data.item9, data.item10]
                    let attachementList = {};
                    for(let i = 0; i < Object.keys(attribute).length; i++){
                        const itemId = attribute[i].id.itemId;
                        if(attribute[i].itemType == 4){
                            attachementList[i] = {"attachmentFileName": items[itemId - 1]};
                        }
                        if(attribute[i].itemType == 5){
                            const date = new Date(items[itemId - 1]);
                            const datestr = date.getFullYear() + "-" + date.getMonth() + "-" + date.getDate() + "T" + date.getHours() + ":" + date.getMinutes();
                            console.log(datestr);
                            items[itemId - 1] = datestr;
                        }
                    }
                    // console.log(attachementList);
                    // console.log(attachementList);
                    this.props.viewState.setCurrentFeatureId(data.featureId);
                    this.setState({
                        featureId: data.featureId,
                        items: items,
                        parentFeatureId: data.parentFeatureId,
                        geom: data.geom,
                        attachementList: attachementList,
                        postUserId:data.postUserId,
                        publishFlag:data.publishFlag,
                        layerAttribute: attribute,
                        buttonText: "更新",
                    })
                    // console.log(data);
                } else {
                    alert("投稿レイヤのデータ取得に失敗しました");
                }
            } catch (error) {
                console.error('通信処理に失敗しました', error);
                alert('通信処理に失敗しました');
            }
        }
    }

    /**
     *活動情報の登録・更新
     * @param {Object} state 状態
     */
    saveFeatureData(state) {
        // 先頭に移動
        document.getElementById("activityFrame").scrollTop = 0;
        let clickLatLongArray = this.props.terria.clickLatLong?.split(',');
        let attachementUploadList = state.attachementUploadList;
        const attachementList = state.attachementList;
        let items = [];
        let check = "";
        const attribute = state.layerAttribute;
        for(let i = 0; i < Object.keys(attribute).length; i++){
            let item = "";
            if(attribute[i].itemType == 4){
                if(attachementList[i] != null){
                    const fileUrl = attachementList[i]["attachmentFileName"].split("/");
                    item = fileUrl[fileUrl.length - 1];
                }
            }else if(attribute[i].itemType == 5){
                const date = new Date(document.getElementById("item" + i).value);
                item = date.toLocaleString();
            }else{
                item = document.getElementById("item" + i).value;
            }
            if(attribute[i].requireFlag == "1" && (item == null || item == "")){
                check += attribute[i].itemName + "の入力は必須です。\n";
            }
            items[attribute[i].id.itemId - 1] = item.toString();
        }
        if(check != ""){
            //登録・更新状態をfalseへ
            this.setState({registerUpdateFlag:false});
            alert(check);
            return;
        }
        //----①活動情報の登録更新
        //ローディング表示
        document.getElementById("customloader").style.display = "block";
        const alertMessage = this.state.buttonText;
        // console.log(state.featureId);
        fetch(Config.config.apiUrl + "/layers/register", {
            method: 'POST',
            body: JSON.stringify({
                featureId: state.featureId,
                layerId: state.layerId,
                geom: state.geom,
                longitude: clickLatLongArray[1],
                latitude: clickLatLongArray[0],
                publishFlag: "0",
                postUserId: state.terria.userId,
                parentFeatureId: state.parentFeatureId,
                item1: items[0],
                item2: items[1],
                item3: items[2],
                item4: items[3],
                item5: items[4],
                item6: items[5],
                item7: items[6],
                item8: items[7],
                item9: items[8],
                item10: items[9],
            }),
            headers: new Headers({ 'Content-type': 'application/json' }),
        })
            .then((res) => res.json())
            .then(res => {
                //----②活動情報の登録更新の結果を判定
                if (res.featureId) {
                    let resFeatureId = res.featureId;
                    let resParentFeatureId = res.parentFeatureId;
                    //----③ファイルのアップロードを行う
                    Object.keys(attachementUploadList).map(key => {
                        let itemId = attribute[key].id.itemId;
                        attachementUploadList[key]["featureId"] = resFeatureId;
                        attachementUploadList[key]["itemId"] = "item_" + itemId;
                        const formData = new FormData();
                        for (const name in attachementUploadList[key]) {
                            formData.append(name, attachementUploadList[key][name]);
                        }
                        fetch(Config.config.apiUrl + "/layers/attachments/upload", {
                            method: 'POST',
                            body: formData,
                        })
                            .then(res => res.json())
                            .then(res => {
                                attachementUploadList[key]["status"] = res.status;
                                if (res.status !== 201) {
                                    alert('アップロードに失敗しました');
                                }
                                let completeFlg = true;
                                Object.keys(attachementUploadList).map(key => {
                                    if (!attachementUploadList[key]["status"]) {
                                        completeFlg = false;
                                    }
                                })
                                if (completeFlg) {
                                    // layerの再描画
                                    try{
                                        runInAction(() => {
                                            //グラフリストデータを再取得
                                            refreshThemeData(this.props.viewState).then(res=>{
                                                //現在表示中のレイヤでlayerTypeが1または2であるレイヤを再表示
                                                this.props.terria.postAndActivityLayerForceReload();
                                            })
                                        })
                                    }catch(error){
                                        console.error('処理に失敗しました', error);
                                    }
                                    // 登録・更新状態をfalseへ
                                    this.setState({registerUpdateFlag:false});
                                    // 再取得を行う
                                    this.fetchFeatureData(resFeatureId);
                                    // 属性情報の更新
                                    const featureInfoControll = document.getElementById("featureInfoControll");
                                    if (featureInfoControll) {
                                        this.props.viewState.setCurrentFeatureId(resFeatureId);
                                        this.props.viewState.setParentActivityId(resParentFeatureId);
                                        featureInfoControll.click();
                                    }
                                    alert(alertMessage + "に成功しました");
                                }
                            }).catch(error => {
                                console.error('通信処理に失敗しました', error);
                            });
                    })
                    if (!attachementUploadList || Object.keys(attachementUploadList).length < 1) {
                        // layerの再描画
                        try{
                            runInAction(() => {
                                //グラフリストデータを再取得
                                refreshThemeData(this.props.viewState).then(res=>{
                                    //現在表示中のレイヤでlayerTypeが1または2であるレイヤを再表示
                                    this.props.terria.postAndActivityLayerForceReload();
                                })
                            })
                        }catch(error){
                            console.error('処理に失敗しました', error);
                        }
                        // 登録・更新状態をfalseへ
                        this.setState({registerUpdateFlag:false});
                        // 再取得を行う
                        this.fetchFeatureData(resFeatureId);
                        // 属性情報の更新
                        const featureInfoControll = document.getElementById("featureInfoControll");
                        if (featureInfoControll) {
                            this.props.viewState.setCurrentActivityId(0);
                            this.props.viewState.setCurrentFeatureId(resFeatureId);
                            featureInfoControll.click();
                        }
                        alert(alertMessage + "に成功しました");
                    }
                    document.getElementById("customloader").style.display = "none";
                    this.clearPoint();
                } else {
                    // 登録・更新状態をfalseへ
                    this.setState({registerUpdateFlag:false});
                    // ローディング非表示
                    document.getElementById("customloader").style.display = "none";
                    alert("登録・更新処理に失敗しました");
                }
            }).catch(error => {
                // 登録・更新状態をfalseへ
                this.setState({registerUpdateFlag:false});
                // ローディング非表示
                document.getElementById("customloader").style.display = "none";
                console.error('通信処理に失敗しました', error);
                alert('通信処理に失敗しました');
            });
    }

    /**
     *活動情報の削除
     * @param {Object} state 状態
     */
    deleteFeatureData(state) {
        if (!confirm("本当に削除しますか？")) {
            return false;
        }
        // 先頭に移動
        document.getElementById("activityFrame").scrollTop = 0;
        // ローディング表示
        document.getElementById("customloader").style.display = "block";
        fetch(Config.config.apiUrl + "/layers/delete", {
            method: 'POST',
            body: JSON.stringify({
                featureId: state.featureId,
                parentFeatureId: state.parentFeatureId,
            }),
            headers: new Headers({ 'Content-type': 'application/json' }),
        })
            .then((res) => res.json())
            .then(res => {
                // console.log(res);
                if (res.parentFeatureId && res.parentFeatureId > 0) {
                    alert('削除しました');
                    this.clearPoint();
                    // 投稿レイヤPanelをclose
                    this.props.viewState.hidePostLayerPanel();
                    // layerの再描画
                    try{
                        runInAction(() => {
                            //グラフリストデータを再取得
                            refreshThemeData(this.props.viewState).then(res=>{
                                //現在表示中のレイヤでlayerTypeが1または2であるレイヤを再表示
                                this.props.terria.postAndActivityLayerForceReload();
                            })
                        })
                    }catch(error){
                        console.error('処理に失敗しました', error);
                    }
                } else {
                    alert('削除処理に失敗しました');
                }
            }).catch(error => {
                console.error('通信処理に失敗しました', error);
                alert('通信処理に失敗しました');
            }).finally(() => {
                // ローディング非表示
                const customloader = document.getElementById("customloader");
                if (customloader) {
                    customloader.style.display = "none";
                }
            });
    }

    /**
     * 活動情報の履歴情報を取得
     * @param {number} 親活動ID
     * @param {number} 活動タイプ
     */
    fetchFeatureHistory(parentFeatureId) {
        if (parentFeatureId > 0) {
            fetch(Config.config.apiUrl + "")
                .then(res => res.json())
                .then(res => {
                    if (res) {
                        this.setState({
                            
                        });
                    } else {
                        alert("履歴情報取得に失敗しました");
                    }
                }).catch(error => {
                    console.error('通信処理に失敗しました', error);
                    alert('通信処理に失敗しました');
                });
        }
    }

    // 活動情報の追加登録
    addActivityData() {
        this.setState({
            featureId: 0,
            geom: this.state.geom,
        });
    }

    //表示している地物の削除
    clearPoint = () => {
        const items = this.state.terria.workbench.items;
        for (const aItem of items) {
          if (aItem.uniqueId === 'レイヤ投稿地点') {
            this.state.terria.workbench.remove(aItem);
            aItem.loadMapItems();
          }
        }
    }

    render() {
        const attachementList = this.state.attachementList;
        const featureId = this.state.featureId;
        const btnTextChange = this.state.buttonText;
        const selectOptions = Config.postLayerSelectOption;
        let htmlElement = [];
        let attribute = this.state.layerAttribute;
        let items = this.state.items;
        if(attribute){
            for(let i = 0; i < Object.keys(attribute).length; i++){
                const itemtype = attribute[i].itemType;
                const itemName = attribute[i].itemName;
                const itemId = attribute[i].id.itemId;
                const layerId = attribute[i].id.layerId;
                const require = attribute[i].requireFlag;
                const itemInputId = "item" + i;
                let itemValue = null;
                if(items){
                    itemValue = items[itemId - 1];
                }
                console.log(itemValue);
                let checkSelect = {}; 
                checkSelect = selectOptions.find((option) => {
                    return option.itemId == itemId && option.layerId == layerId;
                });
                if(checkSelect){
                    // console.log(checkSelect);
                    const options = checkSelect.option.split(',');
                    htmlElement.push(<div><Box column>
                        <Text textAlignLeft style={{display:"inline-block"}}>{itemName} 
                        {require == "1" && (<span style={{color:"red"}}>※</span>)}
                        </Text>
                        <Select
                            light={true}
                            dark={false}
                            className={CustomStyle.CEiLi}
                            id={itemInputId}
                            style={{ color: "#000" }}>
                            {options.map(value=> (
                                <option value={value} selected={value === itemValue}>
                                    {value}
                                </option>
                            ))}
                        </Select>
                    </Box>
                    <Spacing bottom={1} /></div>)
                    continue;
                }
                switch (itemtype){
                    case 1:
                        htmlElement.push(<div><Box column>
                            <Text textAlignLeft style={{display:"inline-block"}}>{itemName} 
                            {require == "1" && (<span style={{color:"red"}}>※</span>)}
                            </Text>
                            <Input
                                light={true}
                                dark={false}
                                type="text"
                                className={CustomStyle.iZHiCE}
                                id={itemInputId}
                                defaultValue={itemValue}
                            />
                        </Box>
                        <Spacing bottom={1} /></div>)
                        break;
                    case 2:
                        htmlElement.push(<div><Box column>
                            <Text textAlignLeft style={{display:"inline-block"}}>{itemName} 
                            {require == "1" && (<span style={{color:"red"}}>※</span>)}
                            </Text>
                            <textarea 
                                style={{
                                    background: "rgba(0,0,0,0.15)",
                                    border: "none",
                                    borderRadius: 2 + "px"
                                }} 
                                rows="3" type="text" id={itemInputId}
                                defaultValue={itemValue}
                                autoComplete="off"
                            ></textarea>
                        </Box>
                        <Spacing bottom={1} /></div>)
                        break;
                    case 3:
                        htmlElement.push(<div><Box column>
                            <Text textAlignLeft style={{display:"inline-block"}}>{itemName} 
                            {require == "1" && (<span style={{color:"red"}}>※</span>)}
                            </Text>
                            <Input
                                light={true}
                                dark={false}
                                type="number"
                                id={itemInputId}
                                className={CustomStyle.iZHiCE}
                                defaultValue={itemValue}
                            />
                        </Box>
                        <Spacing bottom={1} /></div>)
                        break;
                    case 4:
                        htmlElement.push(<div className="upload2"><Box column>
                            <Text textAlignLeft style={{display:"inline-block"}}>{itemName} 
                            {require == "1" && (<span style={{color:"red"}}>※</span>)}
                            </Text>
                            {(!attachementList[i] || !attachementList[i].attachmentFileName)&& (
                            <label className={CustomStyle.fileUploadBtn} tabIndex="0">
                                <div className={CustomStyle.upload}>
                                <input type="file" name="file" id={itemInputId} className={CustomStyle.activityImage} onClick={(e) => {
                                    e.target.value = '';
                                    this.setState({fileNum: i});
                                }} onChange={this.handleInputChange} accept=".png, .jpg, .jpeg" multiple />{/*ファイルを選択*/}
                                </div>
                            </label>)}
                            {attachementList[i] && attachementList[i].attachmentFileName != "" && (
                            <div className={CustomStyle.upload1}>
                                <Spacing bottom={3} />
                                <div column>
                                    <img scrolling="no" src={attachementList[i].attachmentFileName} style={{ width: 100 + "%", textAlign: "center", margin: "0 auto" }} width="420" height="300"></img>
                                    {attachementList[i].id && (
                                        <a href={attachementList[i].attachmentFileName} target="_blank" style={{ textAlign: "center", margin: "0 auto", textDecoration: "none", color: "#00bebe", display: "block" }}>click preview</a>
                                    )}
                                    {!attachementList[i].id && (
                                        <br />
                                    )}
                                </div>
                                <button className={CustomStyle.fileUploadBtn}
                                    onClick={evt => {
                                        this.deleteAttachement(i);
                                    }}><span>削除</span></button>
                                <Spacing bottom={3} />
                            </div>)}
                        </Box>
                        <Spacing bottom={1} /></div>)
                        break;
                    case 5:
                        htmlElement.push(<div><Box column>
                            <Text textAlignLeft style={{display:"inline-block"}}>{itemName} 
                            {require == "1" && (<span style={{color:"red"}}>※</span>)}
                            </Text>
                            <Input
                                light={true}
                                dark={false}
                                className={CustomStyle.iZHiCE}
                                type="datetime-local"
                                id={itemInputId}
                                defaultValue={itemValue}
                            />
                        </Box>
                        <Spacing bottom={1} /></div>)
                        break;
                }
            }
        }

        return (
            <div className={CustomStyle.z_shangchuan}>
            <Box
                displayInlineBlock
                backgroundColor={this.props.theme.textLight}
                styledWidth={"100%"}
                styledHeight={"100%"}
                fullHeight
                overflow={"auto"}
                id="activityFrame"
                onClick={() => this.props.viewState.setTopElement("ActivityPanel")}
                css={`
                    z-index: 99999;
                    ${this.props.viewState.useSmallScreenInterface && 
                        `
                        -webkit-overflow-scrolling:auto;
                        -webkit-overflow-scrolling:touch;
                        `}
                    `}
                >
                <div id="customloader" className={CustomStyle.customloaderParent}>
                    <div className={CustomStyle.customloader}>Loading...</div>
                </div>
                <div css={'text-align:center'}>
                    <Box position="absolute" paddedRatio={3} topRight>
                        <RawButton className={CustomStyle.bJrJHb} onClick={() => {
                            this.props.viewState.hidePostLayerPanel();
                            this.clearPoint();
                        }}>
                            <StyledIcon
                                styledWidth={"16px"}
                                fillColor={this.props.theme.textLight}
                                opacity={"0.5"}
                                glyph={Icon.GLYPHS.closeLight}
                                css={`
                            cursor:pointer;
                          `}
                            />
                        </RawButton>
                    </Box>
                    <nav className={CustomStyle.nav}>
                        {this.state.layerName}
                    </nav>
                    {(this.state.featureId<=0 && this.props.terria.clickLatLong == "") && (
                        <div style={{color:"red",fontWeight:"bold",marginBottom:"15px",background:"yellow"}}>※登録前に地図上から登録地点を選択してください</div>
                    )}
                    <Box
                        centered
                        paddedHorizontally={5}
                        paddedVertically={3}
                        displayInlineBlock
                        className={CustomStyle.content}>

                        {htmlElement}
                        
                        <Spacing bottom={3} />
                        <Box>
                            <button
                                className={CustomStyle.registerBtn}
                                onClick={evt => {
                                    //多重クリック防止
                                    evt.currentTarget.disabled = true;
                                    //evt.currentTarget.style.opacity = 0.5;
                                    //登録・更新状態をtrueへ
                                    this.setState({registerUpdateFlag:true}, () => {
                                        this.saveFeatureData(this.state);
                                    });
                                }}
                                disabled={(this.state.featureId<=0 && this.props.terria.clickLatLong == "") || this.state.registerUpdateFlag}
                                css={`
                                ${((this.state.featureId<=0 && this.props.terria.clickLatLong == "") || this.state.registerUpdateFlag ) && 
                                   `
                                    opacity:.5;
                                    `}
                                `}
                            >
                                <span>{btnTextChange}</span>
                            </button>
                        </Box>
                        {featureId > 0 && (this.props.terria.permission == "admin" || this.props.terria.permission == "erimane" || (this.props.terria.userId && this.state.postUserId && this.props.terria.permission == "user" && (this.state.postUserId == this.props.terria.userId))) && (
                            <>
                                <Spacing bottom={3} />
                                <Box>
                                    <button
                                        className={CustomStyle.deleteBtn}
                                        onClick={evt => {
                                            this.deleteFeatureData(this.state);
                                        }}
                                    >
                                        <span>削除</span>
                                    </button>
                                </Box>
                            </>
                        )}
                        <Spacing bottom={4} />
                    </Box >
                </div>
            </Box >
            </div>
        );
    }
}

export default withTranslation()(withTheme(PostLayerPanel));
