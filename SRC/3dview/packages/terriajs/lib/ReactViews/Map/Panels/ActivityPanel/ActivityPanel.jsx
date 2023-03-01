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
import CustomStyle from "./activity-panel.scss";
import Config from "../../../../../customconfig.json";
/**
 * 活動情報登録・編集画面
 */
@observer
class ActivityPanel extends React.Component {
    static displayName = "ActivityPanel";

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
            isAnimatingOpen: true,
            //編集ボタン
            btnTextChange: "登録",
            //登録対象地点の緯度経度
            clickLatLong: props.terria.clickLatLong,
            //添付ファイルの最大数
            maxAttachmentSize: 4,
            //活動ID
            activityId: props.terria.activityId,
            //親活動ID
            parentActivityId: 0,
            //geometry text
            geom: "",
            //開始日時
            startDateAndTime: "",
            //終了日時
            endDateAndTime: "",
            //活動タイプ
            activityType: 1,
            //活動タイプリスト
            activityTypeList: {},
            //グループタイプ
            groupType: 1,
            //グループタイプリスト
            groupTypeList: {},
            //活動名
            activityName: "",
            //活動場所
            activityPlace: "",
            //活動内容
            activityContent: "",
            //参加者数
            participantCount: "",
            //備考
            remarks: "",
            //添付ファイル
            attachementList: [],
            //アップロード対象の添付ファイル
            attachementUploadList: [],
            //削除対象の添付ファイル
            attachementDeleteList: [],
            //活動履歴
            activityHistoryLsit: []
        };
        this.handleInputChange = this.handleInputChange.bind(this);
    }

    // 初期描画後
    componentDidMount() {
        setTimeout(() => this.setState({ isAnimatingOpen: false }), 0);
        this.fetchTypeMasterData();
        this.fetchActivityData(this.state.activityId);
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
        let reader = new FileReader();

        reader.readAsDataURL(files[0]);

        reader.onload = (e) => {
            // 描画用オブジェにセット
            let key = Object.keys(attachementList).length;
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
     * 添付ファイルの削除
     * @param {number} 削除対象のindex
     */
    deleteAttachement(index) {
        let attachementList = this.state.attachementList;
        let attachementDeleteList = this.state.attachementDeleteList;
        let attachementUploadList = this.state.attachementUploadList;
        // idが付与されている場合は削除対象にセット
        if (index >= 0 && attachementList[index] && attachementList[index].id) {
            attachementDeleteList.push({ id: attachementList[index].id, activityId: this.state.activityId, attachmentFileName: attachementList[index].attachmentFileName });
        }
        delete attachementList[index];
        attachementList = Object.values(attachementList);
        attachementList = attachementList.filter(Boolean);
        delete attachementUploadList[index];
        attachementUploadList = Object.values(attachementUploadList);
        attachementUploadList = attachementUploadList.filter(Boolean);
        this.setState({
            attachementList: attachementList,
            attachementUploadList: attachementUploadList,
            attachementDeleteList: attachementDeleteList
        })
    }

    /**
     *活動情報の取得
     * @param {number} 活動ID
     */
    fetchActivityData(activityId) {
        // 先頭に移動
        document.getElementById("activityFrame").scrollTop = 0;
        if (activityId > 0) {
            // ローディング表示
            document.getElementById("customloader").style.display = "block";
            fetch(Config.config.apiUrl + "/activity/get?activityId=" + activityId)
                .then(res => res.json())
                .then(res => {
                    if (res.activityId) {
                        this.props.viewState.setCurrentActivityId(res.activityId);
                        this.setState({
                            activityId: res.activityId,
                            geom: res.geom,
                            startDateAndTime: res.startDateAndTime,
                            endDateAndTime: res.endDateAndTime,
                            activityType: res.activityType,
                            groupType: res.groupType,
                            activityName: res.activityName,
                            activityPlace: res.activityPlace,
                            activityContent: res.activityContent,
                            participantCount: res.participantCount,
                            remarks: res.remarks,
                            parentActivityId: res.parentActivityId,
                            attachementList: res.attachmentFormList,
                            attachementUploadList: [],
                            attachementDeleteList: [],
                            btnTextChange: "更新"
                        });
                        this.fetchActivityHistory(res.parentActivityId, res.activityType);
                    } else {
                        alert("エリマネ・イベント活動のデータ取得に失敗しました");
                    }
                    setTimeout(function () {
                        // ローディング非表示
                        document.getElementById("customloader").style.display = "none";
                    }, 2000);
                }).catch(error => {
                    // ローディング非表示
                    document.getElementById("customloader").style.display = "none";
                    console.error('通信処理に失敗しました', error);
                    alert('通信処理に失敗しました');
                });
        } else {
            // ローディング非表示
            document.getElementById("customloader").style.display = "none";
        }
    }

    /**
     *活動情報の登録更新
     * @param {Object} state 状態
     */
    saveActivityData(state) {
        // 先頭に移動
        document.getElementById("activityFrame").scrollTop = 0;
        let clickLatLongArray = state.clickLatLong.split(',');
        let attachementUploadList = state.attachementUploadList;
        attachementUploadList = attachementUploadList.filter(Boolean);
        // 日付の入力チェック
        if (state.startDateAndTime === "") {
            alert("開始日時の入力は必須です");
            return;
        } else if (!new Date(state.startDateAndTime).getDate()) {
            alert("開始日時の入力値が不正です");
            return;
        }
        if (state.endDateAndTime !== "" && !new Date(state.endDateAndTime).getDate()) {
            alert("終了日時の入力値が不正です");
            return;
        }
        //----①活動情報の登録更新
        // ローディング表示
        document.getElementById("customloader").style.display = "block";
        fetch(Config.config.apiUrl + "/activity/register", {
            method: 'POST',
            body: JSON.stringify({
                activityId: state.activityId,
                geom: state.geom,
                startDateAndTime: state.startDateAndTime,
                endDateAndTime: state.endDateAndTime,
                activityType: state.activityType,
                groupType: state.groupType,
                activityName: state.activityName,
                activityPlace: state.activityPlace,
                activityContent: state.activityContent,
                participantCount: state.participantCount,
                remarks: state.remarks,
                parentActivityId: state.parentActivityId,
                longitude: clickLatLongArray[1],
                latitude: clickLatLongArray[0],
                attachmentFormDeleteList: state.attachementDeleteList
            }),
            headers: new Headers({ 'Content-type': 'application/json' }),
        })
            .then((res) => res.json())
            .then(res => {
                //----②活動情報の登録更新の結果を判定
                if (res.activityId) {
                    let resActivityId = res.activityId;
                    let resParentActivityId = res.parentActivityId;
                    let resActivityType = res.activityType;
                    //----③ファイルのアップロードを行う
                    Object.keys(attachementUploadList).map(key => {
                        attachementUploadList[key]["activityId"] = resActivityId;
                        const formData = new FormData();
                        for (const name in attachementUploadList[key]) {
                            formData.append(name, attachementUploadList[key][name]);
                        }
                        fetch(Config.config.apiUrl + "/activity/attachments/upload", {
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
                                    // 初期登録の場合はlayerの再描画
                                    if (state.btnTextChange !== "更新" && state.parentActivityId < 1) {
                                        try{
                                            const items = this.props.terria.workbench.items;
                                            let layerId = "";
                                            if (resActivityType === '1' || resActivityType === 1) {
                                                layerId = Config.layerId.erimane;
                                            } else if (resActivityType === '2' || resActivityType === 2) {
                                                layerId = Config.layerId.event;
                                            }
                                            for (const aItem of items) {
                                                if (aItem.uniqueId === layerId) {
                                                    this.props.terria.workbench.remove(aItem);
                                                }
                                            }
                                            const item = this.props.terria.getModelById(BaseModel, layerId);
                                            this.props.terria.workbench.add(item);
                                        }catch(error){
                                            console.error('処理に失敗しました', error);
                                        }
                                    }
                                    // 再取得を行う
                                    this.fetchActivityData(resActivityId);
                                    // 属性情報の更新
                                    const featureInfoControll = document.getElementById("featureInfoControll");
                                    if (featureInfoControll) {
                                        this.props.viewState.setCurrentActivityId(resActivityId);
                                        this.props.viewState.setParentActivityId(resParentActivityId);
                                        featureInfoControll.click();
                                    }
                                }
                            }).catch(error => {
                                console.error('通信処理に失敗しました', error);
                            });
                    })
                    if (!attachementUploadList || Object.keys(attachementUploadList).length < 1) {
                        // 初期登録の場合はlayerの再描画
                        if (state.btnTextChange !== "更新" && state.parentActivityId < 1) {
                            try{
                                const items = this.props.terria.workbench.items;
                                let layerId = "";
                                if (resActivityType === '1' || resActivityType === 1) {
                                    layerId = Config.layerId.erimane;
                                } else if (resActivityType === '2' || resActivityType === 2) {
                                    layerId = Config.layerId.event;
                                }
                                for (const aItem of items) {
                                    if (aItem.uniqueId === layerId) {
                                        this.props.terria.workbench.remove(aItem);
                                    }
                                }
                                const item = this.props.terria.getModelById(BaseModel, layerId);
                                this.props.terria.workbench.add(item)
                            }catch(error){
                                console.error('処理に失敗しました', error);
                            }
                        }
                        // 再取得を行う
                        this.fetchActivityData(resActivityId);
                        // 属性情報の更新
                        const featureInfoControll = document.getElementById("featureInfoControll");
                        if (featureInfoControll) {
                            this.props.viewState.setCurrentActivityId(resActivityId);
                            this.props.viewState.setParentActivityId(resParentActivityId);
                            featureInfoControll.click();
                        }
                    }
                } else {
                    // ローディング非表示
                    document.getElementById("customloader").style.display = "none";
                    alert("登録・更新処理に失敗しました");
                }
            }).catch(error => {
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
    deleteActivityData(state) {
        if (!confirm("本当に削除しますか？")) {
            return false;
        }
        // 先頭に移動
        document.getElementById("activityFrame").scrollTop = 0;
        // ローディング表示
        document.getElementById("customloader").style.display = "block";
        fetch(Config.config.apiUrl + "/activity/delete", {
            method: 'POST',
            body: JSON.stringify({
                activityId: state.activityId,
                parentActivityId: state.parentActivityId
            }),
            headers: new Headers({ 'Content-type': 'application/json' }),
        })
            .then((res) => res.json())
            .then(res => {
                if (res.parentActivityId && res.parentActivityId > 0) {
                    // 属性情報の更新
                    const featureInfoControll = document.getElementById("featureInfoControll");
                    if (featureInfoControll) {
                        this.props.viewState.setCurrentActivityId(res.activityId);
                        this.props.viewState.setParentActivityId(res.parentActivityId);
                        featureInfoControll.click();
                    }
                    // layerの再描画
                    const items = state.terria.workbench.items;
                    if (res.parentActivityId === res.activityId) {
                        for (const aItem of items) {
                            if (aItem.uniqueId === Config.layerId.erimane || aItem.uniqueId === Config.layerId.event) {
                                state.terria.workbench.remove(aItem);
                                aItem.loadMapItems();
                                state.terria.workbench.add(aItem);

                            }
                        }
                    }
                    // Panelをclose
                    this.props.viewState.hideActivityPanel();
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
    fetchActivityHistory(parentActivityId, activityType) {
        if (parentActivityId > 0) {
            fetch(Config.config.apiUrl + "/activity/activity_history?parentActivityId=" + parentActivityId + "&activityType=" + activityType)
                .then(res => res.json())
                .then(res => {
                    if (res.activityFormList) {
                        this.setState({
                            activityHistoryLsit: res.activityFormList
                        });
                    } else {
                        alert("活動情報の履歴情報取得に失敗しました");
                    }
                }).catch(error => {
                    console.error('通信処理に失敗しました', error);
                    alert('通信処理に失敗しました');
                });
        }
    }

    // タイプ(活動タイプ・グループタイプ)のマスタ情報を取得
    fetchTypeMasterData() {
        fetch(Config.config.apiUrl + "/activity/type")
            .then(res => res.json())
            .then(res => {
                if (res.activityTypeList && res.groupTypeList) {
                    this.setState({
                        activityTypeList: res.activityTypeList,
                        groupTypeList: res.groupTypeList
                    });
                } else {
                    alert("地域活動種別・エリアマネジメント団体の取得に失敗しました");
                }
            }).catch(error => {
                console.error('通信処理に失敗しました', error);
                alert('通信処理に失敗しました');
            });
    }

    /**
     *活動タイプの変更
     * @param {event} event
     */
     handleSelectActivityType(event) {
        this.setState({ activityType: event.target.value });
    }

    /**
     *グループタイプの変更
     * @param {event} event
     */
    handleSelectGroupType(event) {
        this.setState({ groupType: event.target.value });
    }

    // 活動情報の追加登録
    addActivityData() {
        this.setState({
            activityId: 0,
            geom: this.state.geom,
            startDateAndTime: "",
            endDateAndTime: "",
            activityType: this.state.activityType,
            groupType: 1,
            activityName: "",
            activityPlace: "",
            activityContent: "",
            participantCount: "",
            remarks: "",
            parentActivityId: this.state.parentActivityId,
            attachementList: [],
            attachementUploadList: [],
            attachementDeleteList: [],
            activityHistoryLsit: [],
            btnTextChange: "登録"
        });
    }

    render() {
        const activityId = this.state.activityId;
        const parentActivityId = this.state.parentActivityId;
        const activityType = this.state.activityType;
        const activityTypeList = this.state.activityTypeList;
        const groupType = this.state.groupType;
        const groupTypeList = this.state.groupTypeList;
        const activityName = this.state.activityName;
        const activityPlace = this.state.activityPlace;
        const activityContent = this.state.activityContent;
        const participantCount = this.state.participantCount;
        const startDateAndTime = this.state.startDateAndTime;
        const endDateAndTime = this.state.endDateAndTime;
        const remarks = this.state.remarks;
        const attachementList = this.state.attachementList;
        const btnTextChange = this.state.btnTextChange;
        const maxAttachmentSize = this.state.maxAttachmentSize;
        const activityHistoryLsit = this.state.activityHistoryLsit;
        let htmlElement = [];
        for (let i = Object.keys(attachementList).length + 1; i < maxAttachmentSize + 1; i++) {
            htmlElement.push(<div><Spacing bottom={1} /> <Box column>
                <Text>添付ファイル {i}</Text>
                <label className={CustomStyle.fileUploadBtn} tabIndex="0">
                    <input type="file" name="file" className={CustomStyle.activityImage} onClick={(e) => {
                        e.target.value = '';
                    }} onChange={this.handleInputChange} accept=".png, .jpg, .jpeg, .pdf" multiple />ファイルを選択
                </label>
            </Box></div>)
        }
        return (
            <Box
                displayInlineBlock
                backgroundColor={this.props.theme.textLight}
                styledWidth={"500px"}
                styledHeight={"550px"}
                fullHeight
                overflow={"auto"}
                id="activityFrame"
                onClick={() => this.props.viewState.setTopElement("ActivityPanel")}
                css={`
          position: fixed;
          z-index: ${this.props.viewState.topElement === "ActivityPanel"
                        ? 99999
                        : 110};
          transition: right 0.25s;
          transition-timing-function: cubic-bezier(0.4, 0, 0.2, 1);
          top:50%;
          left:65%;
          transform: translate(-50%, -50%);
        `}
            >
                <div id="customloader" className={CustomStyle.customloaderParent}>
                    <div className={CustomStyle.customloader}>Loading...</div>
                </div>
                <div>
                    <Box position="absolute" paddedRatio={3} topRight>
                        <RawButton onClick={() => {
                            this.props.viewState.hideActivityPanel();
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
                    {parentActivityId > 0 && (
                        <div className={CustomStyle.addButtonContent}>
                            <button
                                onClick={evt => {
                                    this.addActivityData();
                                }}
                                className={CustomStyle.addButton}
                            >
                                <span>追加</span>
                            </button>
                        </div>
                    )}
                    <nav className={CustomStyle.nav}>
                        エリマネ・イベント活動
                    </nav>
                    <Box
                        centered
                        paddedHorizontally={5}
                        paddedVertically={3}
                        displayInlineBlock
                        className={CustomStyle.content}>
                        {activityHistoryLsit[0] && (
                            <Box column>
                                <div className={CustomStyle.historyBox}>
                                    <Select
                                        light={true}
                                        dark={false}
                                        onChange={e => this.fetchActivityData(e.target.value)}
                                        style={{ color: "#000", width: "200px" }}>
                                        {Object.keys(activityHistoryLsit).map(key => (
                                            <option key={activityHistoryLsit[key].activityId} value={activityHistoryLsit[key].activityId} selected={activityHistoryLsit[key].activityId === activityId}>
                                                {activityHistoryLsit[key].startDateAndTime}
                                            </option>
                                        ))}
                                    </Select>
                                </div>
                            </Box>
                        )}
                        <Spacing bottom={3} />
                        <Box column>
                            <Text>地域活動種別</Text>
                            <Select
                                light={true}
                                dark={false}
                                onChange={e => this.handleSelectActivityType(e)}
                                style={{ color: "#000" }}>
                                {Object.keys(activityTypeList).map(key => (
                                    (parentActivityId < 1 || (parentActivityId > 0 && activityTypeList[key].id === activityType)) && (
                                        <option key={activityTypeList[key].id} value={activityTypeList[key].id} selected={activityTypeList[key].id === activityType}>
                                            {activityTypeList[key].typeName}
                                        </option>
                                    )
                                ))}
                            </Select>
                        </Box>
                        <Spacing bottom={1} />

                        <Box column>
                            <Text>エリアマネジメント団体</Text>
                            <Select
                                light={true}
                                dark={false}
                                onChange={e => this.handleSelectGroupType(e)}
                                style={{ color: "#000" }}>
                                {Object.keys(groupTypeList).map(key => (
                                    <option key={groupTypeList[key].id} value={groupTypeList[key].id} selected={groupTypeList[key].id === groupType}>
                                        {groupTypeList[key].typeName}
                                    </option>
                                ))}
                            </Select>
                        </Box>
                        <Spacing bottom={1} />

                        <Box column>
                            <Text>活動名</Text>
                            <Input
                                light={true}
                                dark={false}
                                type="text"
                                value={activityName}
                                placeholder=""
                                onChange={e => this.setState({ activityName: e.target.value })}
                            />
                        </Box>
                        <Spacing bottom={1} />

                        <Box column>
                            <Text>活動場所</Text>
                            <textarea style={{
                                background: "rgba(0,0,0,0.15)",
                                border: "none",
                                borderRadius: 2 + "px"
                            }} rows="3" type="text" placeholder="" value={activityPlace}
                                autoComplete="off"
                                onChange={e => this.setState({ activityPlace: e.target.value })}
                            ></textarea>
                        </Box>
                        <Spacing bottom={1} />

                        <Box column>
                            <Text>活動内容</Text>
                            <textarea style={{
                                background: "rgba(0,0,0,0.15)",
                                border: "none",
                                borderRadius: 2 + "px"
                            }} rows="3" type="text" placeholder="" value={activityContent}
                                autoComplete="off"
                                onChange={e => this.setState({ activityContent: e.target.value })}
                            ></textarea>
                        </Box>
                        <Spacing bottom={1} />

                        <Box column>
                            <Text>参加者数</Text>
                            <Input
                                light={true}
                                dark={false}
                                type="number"
                                value={participantCount}
                                placeholder=""
                                id="participantCount"
                                onChange={e => this.setState({ participantCount: e.target.value })}
                            />
                        </Box>
                        <Spacing bottom={1} />

                        <Box column>
                            <Text>活動年月日</Text>
                            <Box className={CustomStyle.box}>
                                <Box className={CustomStyle.item} style={{ width: 48 + "%" }}>
                                    <Input
                                        light={true}
                                        dark={false}
                                        type="datetime-local"
                                        value={startDateAndTime}
                                        placeholder=""
                                        onChange={e => this.setState({ startDateAndTime: e.target.value })}
                                    />
                                </Box>
                                <Box className={CustomStyle.item} style={{ width: 2 + "%" }}>
                                    ~
                                </Box>
                                <Box className={CustomStyle.item} style={{ width: 48 + "%" }}>
                                    <Input
                                        light={true}
                                        dark={false}
                                        type="datetime-local"
                                        value={endDateAndTime}
                                        placeholder=""
                                        onChange={e => this.setState({ endDateAndTime: e.target.value })}
                                    />
                                </Box>
                            </Box>
                        </Box>
                        <Spacing bottom={1} />

                        <Box column>
                            <Text>備考</Text>
                            <textarea style={{
                                background: "rgba(0,0,0,0.15)",
                                border: "none",
                                borderRadius: 2 + "px"
                            }} rows="3" type="text" placeholder="" value={remarks}
                                autoComplete="off"
                                onChange={e => this.setState({ remarks: e.target.value })}
                            ></textarea>
                        </Box>
                        <Spacing bottom={1} />
                        {Object.keys(attachementList).map(key => (
                            <div>
                                <Spacing bottom={3} />
                                <div column>
                                    <iframe src={attachementList[key].attachmentFileName} style={{ width: 100 + "%", textAlign: "center", margin: "0 auto" }} width="420" height="300"></iframe>
                                    {attachementList[key].id && (
                                        <a href={attachementList[key].attachmentFileName} target="_blank" style={{ textAlign: "center", margin: "0 auto", textDecoration: "none", color: "#00bebe", display: "block" }}>click preview</a>
                                    )}
                                    {!attachementList[key].id && (
                                        <br />
                                    )}
                                </div>
                                <button className={CustomStyle.fileUploadBtn}
                                    onClick={evt => {
                                        this.deleteAttachement(key);
                                    }}><span>削除</span></button>
                                <Spacing bottom={3} />
                            </div>
                        ))}
                        {htmlElement}
                        <Spacing bottom={3} />
                        <Box>
                            <button
                                className={CustomStyle.registerBtn}
                                onClick={evt => {
                                    this.saveActivityData(this.state);
                                }}
                            >
                                <span>{btnTextChange}</span>
                            </button>
                        </Box>
                        {activityId > 0 && (
                            <>
                                <Spacing bottom={3} />
                                <Box>
                                    <button
                                        className={CustomStyle.deleteBtn}
                                        onClick={evt => {
                                            this.deleteActivityData(this.state);
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
        );
    }
}

export default withTranslation()(withTheme(ActivityPanel));
