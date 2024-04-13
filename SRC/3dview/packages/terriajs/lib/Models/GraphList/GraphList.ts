import Config from "../../../customconfig.json";
import { action, runInAction } from "mobx";
import LinkCellRenderrer from './LinkCellRenderrer';
import AutoLinkCellRenderrer from './AutoLinkCellRenderrer';
import CommonStrata from "../../Models/Definition/CommonStrata";
import { BaseModel } from "../Definition/Model";

/**
 * 
 * グラフリストデータ生成用class
 * 
 */
export class GraphList {
    viewState:any

    constructor();

    constructor(viewState:any);

    /**
     * コンストラクタ
     * @param viewState 
     */
    constructor(viewState?:any){
        this.viewState = viewState == null ? null : viewState;
    }

    /**
     * 複合グラフ　データを取得
     * @param graphListForm 
     * @param data 
     * @returns chart-js表示用複合グラフデータ
     */
    getComplexGraphData ( graphListForm: any, data: any) : Object {

        const viewState = this.viewState;

        //各対象のカラム名を取得
        const graphListTemplateValFormList = graphListForm.graphListTemplateValFormList;
        const xColumnName = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "xColumnName")?.itemValue;
        const rodYColumnName = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "rodYColumnName")?.itemValue;
        const lineYColumnName = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "lineYColumnName")?.itemValue;
    
        //対象のカラム名からラベル・データを取得
        let labelList: any = [];
        let dataList1: any = [];
        let dataList2: any = [];
        for (let i = 0; i < data.length; i++) {
            if (data[i][xColumnName]){
                let xColumn = data[i][xColumnName];
                xColumn = String(xColumn);
                labelList.push(xColumn?.trim());
            }
            if (data[i][rodYColumnName] != undefined && data[i][rodYColumnName] != null){
                data[i][rodYColumnName] = Number(data[i][rodYColumnName]);
                if(isNaN(data[i][rodYColumnName])){
                    data[i][rodYColumnName] = 0;
                }
                dataList1.push(data[i][rodYColumnName]);
            }else{
                dataList1.push(0);
            }
            if (data[i][lineYColumnName] != undefined && data[i][lineYColumnName] != null){
                data[i][lineYColumnName] = Number(data[i][lineYColumnName]);
                if(isNaN(data[i][lineYColumnName])){
                    data[i][lineYColumnName] = 0;
                }
                dataList2.push(data[i][lineYColumnName]);
            }else{
                dataList2.push(0);
            }
        }
    
        //オプション値をセット
        const title = graphListForm.graphName;
        let y1Label = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "rodYLabel")?.itemValue;
        let y2Label = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "lineYLabel")?.itemValue;
        let xLabel = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "xLabel")?.itemValue;
        const y1Max = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "rodYMax")?.itemValue;
        const y1Min = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "rodYMin")?.itemValue;
        const y1StepSize = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "rodYStepSize")?.itemValue;
        const y2Max = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "lineYMax")?.itemValue;
        const y2Min = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "lineYMin")?.itemValue;
        const y2StepSize = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "lineYStepSize")?.itemValue;
        let y1Unit = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "rodYUnit")?.itemValue;
        let y2Unit = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "lineYUnit")?.itemValue;
        let backgroundColor1 = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "rodBackGroundColor")?.itemValue;
        let borderColor1 = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "rodBorderColor")?.itemValue;
        let backgroundColor2 = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "lineBackGroundColor")?.itemValue;
        let borderColor2 = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "lineBorderColor")?.itemValue;
        //labelとunitが未定義の場合
        if(y1Label == null || y1Label == undefined){
            y1Label = "";
        }
        if(y2Label == null || y2Label == undefined){
            y2Label = "";
        }
        if(xLabel == null || xLabel == undefined){
            xLabel = "";
        }
        if(y1Unit == null || y1Unit == undefined){
            y1Unit = "";
        }
        if(y2Unit == null || y2Unit == undefined){
            y2Unit = "";
        }
        //グラフ色が未定義の場合
        if(backgroundColor1 == null || backgroundColor1 == undefined){
            backgroundColor1 = "#192f60";
        }
        if(backgroundColor2 == null || backgroundColor2 == undefined){
            backgroundColor2 = "#ffffff";
        }
        if(borderColor1 == null || borderColor1 == undefined){
            borderColor1 = "#192f60";
        }
        if(borderColor2 == null || borderColor2 == undefined){
            borderColor2 = "#e95464";
        }
        //グラフ色生成
        const backgroundColor1Array = new Array();
        const borderColor1Array = new Array();
        const backgroundColor2Array = new Array();
        const borderColor2Array = new Array();
        const backgroundColor1List = backgroundColor1.split(",");
        const borderColor1List = borderColor1.split(",");
        const backgroundColor2List = backgroundColor2.split(",");
        const borderColor2List = borderColor2.split(",");
        labelList.forEach((label: any, i: any) => {
            let backgroundColor1Temp = backgroundColor1List[i];
            if(!backgroundColor1Temp){
                backgroundColor1Temp = backgroundColor1List[backgroundColor1List.length-1];
            }
            backgroundColor1Array.push(backgroundColor1Temp);
            let borderColor1Temp = borderColor1List[i];
            if(!borderColor1Temp){
                borderColor1Temp = borderColor1List[borderColor1List.length-1];
            }
            borderColor1Array.push(borderColor1Temp);
            let backgroundColor2Temp = backgroundColor2List[i];
            if(!backgroundColor2Temp){
                backgroundColor2Temp = backgroundColor2List[backgroundColor2List.length-1];
            }
            backgroundColor2Array.push(backgroundColor2Temp);
            let borderColor2Temp = borderColor2List[i];
            if(!borderColor2Temp){
                borderColor2Temp = borderColor2List[borderColor2List.length-1];
            }
            borderColor2Array.push(borderColor2Temp);
        });

        //1: 3D都市モデルビューワ->ダッシュボード（データ絞込）→グラフの場合highlight
        const linkedFeatureIdList = this.viewState?.linkedFeatureIdList;
        let attributeNameList:any = [];
        let attributeColumnNameList:any = [];
        if(linkedFeatureIdList && graphListForm.layerGraphCooporationFormList){
            for(let i=0;i<graphListForm.layerGraphCooporationFormList.length;i++){
                const cooperationTypeIndex = i;
                if(graphListForm.layerGraphCooporationFormList[cooperationTypeIndex].cooperationType == 1){
                    let cooperationOption = graphListForm.layerGraphCooporationFormList[cooperationTypeIndex].cooperationOption;
                    cooperationOption = JSON.parse(cooperationOption);
                    Object.keys(linkedFeatureIdList).forEach(featureAttributeName=>{
                            if(cooperationOption.featureAttributeName == featureAttributeName){
                                attributeNameList.push(cooperationOption.featureAttributeName);
                                attributeColumnNameList.push(cooperationOption.featureAttributeColumnName);
                            }
                    })
                }
            }
        }
        //連携対象のfeatureの属性がある場合のみ
        if(attributeNameList.length>0 && attributeColumnNameList.length>0){
            const highlightIndexList = [];
            for(let i=0;i<data.length;i++){
                for(let j=0;j<attributeNameList.length;j++){
                    let attributeValue = data[i][attributeColumnNameList[j]];
                    try{
                        attributeValue = attributeValue.trim();
                    }catch(e){}
                    if (linkedFeatureIdList[attributeNameList[j]] 
                        && linkedFeatureIdList[attributeNameList[j]].findIndex((linkedFeatureId:any) => linkedFeatureId == attributeValue) > -1) {
                        if(highlightIndexList.findIndex(highlightIndex=>highlightIndex==i)<0){
                            highlightIndexList.push(i);
                        }
                    }
                }
            }
            //連携対象のfeatureの属性がありfeatureIdが該当するものがあればハイライト表示
            if(highlightIndexList.length > 0){
                for(let i =0;i<backgroundColor1Array.length;i++){
                    backgroundColor1Array[i] = backgroundColor1 + '4D';
                    borderColor1Array[i] = borderColor1 + '4D';
                    backgroundColor2Array[i] = backgroundColor2 + '4D';
                    borderColor2Array[i] = borderColor2 + '4D';
                }
                for(let i =0;i<highlightIndexList.length;i++){
                    backgroundColor1Array[highlightIndexList[i]] = backgroundColor1Array[highlightIndexList[i]].replace("4D","");
                    borderColor1Array[highlightIndexList[i]] = borderColor1Array[highlightIndexList[i]].replace("4D","");
                    backgroundColor2Array[highlightIndexList[i]] = backgroundColor2Array[highlightIndexList[i]].replace("4D","");
                    borderColor2Array[highlightIndexList[i]] = borderColor2Array[highlightIndexList[i]].replace("4D","");
                }
            }
        }
    
        //グラフオプションを定義
        const ua = window.navigator.userAgent.toLowerCase();
        const barOptions:any = {
            maintainAspectRatio: false,
            responsive: true,
            plugins: {
                title: {
                    display: true,
                    text: ""
                },
                tooltip: {
                    displayColors: false,
                    callbacks: {
                        title: function (context: any) {
                            return "";
                        },
                        beforeLabel: function (context: any) {
                            if (context.dataIndex == null
                                || context.dataIndex < 0
                                || context.dataIndex >= labelList.length)
                                return "";
                            let label = xLabel;
                            if(label != ""){
                                label += ': ';
                            }
                            label += labelList[context.dataIndex];
                            return label;
                        },
                        label: function (context: any) {
                            if (context.parsed.y == null) return "";
                            let label = context.dataset.label || '';
                            if(label != ""){
                                label += ': ';
                            }
                            if (context.dataset.label == y1Label) {
                                label += Number(context.parsed.y).toLocaleString() + " " + y1Unit;
                            } else if (context.dataset.label == y2Label) {
                                label += Number(context.parsed.y).toLocaleString() + " " + y2Unit;
                            }
                            return label;
                        },
                        afterLabel: function (context: any) {
                            if (context.dataIndex == null
                                || context.dataIndex < 0
                                || context.dataIndex >= dataList1.length
                                || context.dataIndex >= dataList2.length)
                                return "";
                            let label = "";
                            if (context.dataset.label == y1Label) {
                                label = y2Label;
                                if(label != ""){
                                    label += ': ';
                                }
                                label += Number(dataList2[context.dataIndex]).toLocaleString() + " " + y2Unit;
                            } else if (context.dataset.label == y2Label) {
                                label = y1Label;
                                if(label != ""){
                                    label += ': ';
                                }
                                label += Number(dataList1[context.dataIndex]).toLocaleString() + " " + y1Unit;
                            }
                            return label;
                        }
                    }
                },
                legend: {
                    labels: {
                        boxHeight: 10,
                        usePointStyle: true,
                        pointStyle: 'dash',
                        generateLabels: (chart:any) => {
                            const datasets = chart.data.datasets;
                            const {
                              labels: {
                                usePointStyle,
                                pointStyle,
                                textAlign,
                                color
                              }
                            } = chart.legend.options;       
                            return chart._getSortedDatasetMetas().map((meta:any) => {
                              const style = meta.controller.getStyle(datasets[meta.index].type=="line" ? 0 : undefined);
                              return {
                                text: datasets[meta.index].label,
                                fillStyle: style.backgroundColor,
                                fontColor: color,
                                hidden: !meta.visible,
                                lineCap: style.borderCapStyle,
                                lineDash: style.borderDash,
                                lineDashOffset: style.borderDashOffset,
                                lineJoin: style.borderJoinStyle,
                                lineWidth: 1,
                                strokeStyle: style.borderColor,
                                pointStyle: datasets[meta.index].type=="line"?'line':'rect',
                                rotation: style.rotation,
                                textAlign: textAlign || style.textAlign,
                                borderRadius: 0,
                                datasetIndex: meta.index
                              };
                            })
                          }
                    },
                }
            },
            scales: {
                x: {
                    title: {
                        display: true,
                        text: xLabel
                    },
                    stacked: false,
                },
                y1: {
                    stacked: false,
                    max: Number(y1Max),
                    min: Number(y1Min),
                    ticks: {
                        // 目安のstepSize(表示値は調整かかる)
                        stepSize: Number(y1StepSize),
                        beginAtZero: true,
                        callback: function (value: any) {
                            return `${Number(value).toLocaleString()}`;
                        },
                    },
                },
                y2: {
                    stacked: false,
                    position: "right",
                    max: Number(y2Max),
                    min: Number(y2Min),
                    ticks: {
                        // 目安のstepSize(表示値は調整かかる)
                        stepSize: Number(y2StepSize),
                        beginAtZero: true,
                        callback: function (value: any) {
                            return `${Number(value).toLocaleString()}`;
                        }
                    }
                }
            },
            onHover: function (evt: any, element: any, chart: any) {
                if (element && element.length > 0) {
                    const hoveredEl = chart.getElementsAtEventForMode(evt, 'point', {
                        intersect: true
                    }, true)[0];
                    if (hoveredEl && hoveredEl.index > -1) {
                        const backgroundColor1Array = new Array();
                        const borderColor1Array = new Array();
                        const backgroundColor2Array = new Array();
                        const borderColor2Array = new Array();
                        labelList.forEach((label: any, i: any) => {
                            let backgroundColor1Temp = backgroundColor1List[i];
                            if(!backgroundColor1Temp){
                                backgroundColor1Temp = backgroundColor1List[backgroundColor1List.length-1];
                            }
                            backgroundColor1Array.push(backgroundColor1Temp + '4D');
                            let borderColor1Temp = borderColor1List[i];
                            if(!borderColor1Temp){
                                borderColor1Temp = borderColor1List[borderColor1List.length-1];
                            }
                            borderColor1Array.push(borderColor1Temp + '4D');
                            let backgroundColor2Temp = backgroundColor2List[i];
                            if(!backgroundColor2Temp){
                                backgroundColor2Temp = backgroundColor2List[backgroundColor2List.length-1];
                            }
                            backgroundColor2Array.push(backgroundColor2Temp + '4D');
                            let borderColor2Temp = borderColor2List[i];
                            if(!borderColor2Temp){
                                borderColor2Temp = borderColor2List[borderColor2List.length-1];
                            }
                            borderColor2Array.push(borderColor2Temp + '4D');
                        });
                        chart.data.datasets.forEach((dataset: any, i: any) => {
                            if (dataset.type != "line") {
                                backgroundColor1Array[hoveredEl.index] = backgroundColor1Array[hoveredEl.index].replace("4D", "");
                                borderColor1Array[hoveredEl.index] = borderColor1Array[hoveredEl.index].replace("4D", "");
                                dataset.backgroundColor = backgroundColor1Array;
                                dataset.borderColor = borderColor1Array;
                            } else {
                                backgroundColor2Array[hoveredEl.index] = backgroundColor2Array[hoveredEl.index].replace("4D", "");
                                borderColor2Array[hoveredEl.index] = borderColor2Array[hoveredEl.index].replace("4D", "");
                                dataset.backgroundColor = backgroundColor2Array;
                                dataset.borderColor = borderColor2Array;
                            }
                        });
                        chart.update();
                        chart.legend.legendItems[0].fillStyle = backgroundColor2;
                        chart.legend.legendItems[0].strokeStyle = borderColor2;
                        chart.legend.legendItems[1].fillStyle = backgroundColor1;
                        chart.legend.legendItems[1].strokeStyle = borderColor1;
                    }
                }else{
                    chart.data.datasets.forEach((dataset: any, i: any) => {
                        if (dataset.type != "line") {
                            dataset.backgroundColor = backgroundColor1Array;
                            dataset.borderColor = borderColor1Array;
                        } else {
                            dataset.backgroundColor = backgroundColor2Array;
                            dataset.borderColor = borderColor2Array;
                        }
                    });
                    chart.update();
                }
                if ((ua.indexOf('macintosh') > -1 || ua.indexOf('ipad') > -1 || (ua.indexOf('Android') > -1 && ua.indexOf('Mobile') == -1)) && 'ontouchend' in document) {
                    try{
                    if (element.length > 0) {
                        const hoveredEl = chart.getElementsAtEventForMode(evt, 'point', {
                            intersect: true
                        }, true)[0];
                        if (hoveredEl && hoveredEl.index > -1) {
                            // 0: ダッシュボード->3D都市モデルビューワ（フォーカス)
                            let cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==0);
                            if(cooperationTypeIndex > -1){
                                //graphFocus(viewState,data[hoveredEl.index],graphListForm.layerGraphCooporationFormList);
                            }
                            // 4: ダッシュボード->3D建物モデル（style切替）
                            cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==4);
                            if(cooperationTypeIndex > -1){
                                let backgroundColor = "";
                                if(hoveredEl.datasetIndex == 0){
                                    backgroundColor = backgroundColor1Array[hoveredEl.index];
                                }else{
                                    backgroundColor = backgroundColor2Array[hoveredEl.index];
                                }
                                buildingStyleChange(viewState,data[hoveredEl.index],graphListForm.layerGraphCooporationFormList,backgroundColor);
                            }
                            // 2: ダッシュボード->3D都市モデルビューワ（レイヤ切替）
                            cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==2);
                            if(cooperationTypeIndex > -1){
                                layerChangeForGraphList(viewState,graphListForm.layerGraphCooporationFormList,data[hoveredEl.index],graphListForm.graphId);
                            }
                        }
                    }
                    }catch(e){}
                }
            },
            onClick: function(evt: any, element: any, chart: any) {
                if (element.length > 0) {
                    const hoveredEl = chart.getElementsAtEventForMode(evt, 'point', {
                        intersect: true
                    }, true)[0];
                    if (hoveredEl && hoveredEl.index > -1) {
                        // 0: ダッシュボード->3D都市モデルビューワ（フォーカス)
                        let cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==0);
                        if(cooperationTypeIndex > -1){
                            graphFocus(viewState,data[hoveredEl.index],graphListForm.layerGraphCooporationFormList);
                        }
                        // 4: ダッシュボード->3D建物モデル（style切替）
                        cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==4);
                        if(cooperationTypeIndex > -1){
                            let backgroundColor = "";
                            if(hoveredEl.datasetIndex == 0){
                                backgroundColor = backgroundColor1Array[hoveredEl.index];
                            }else{
                                backgroundColor = backgroundColor2Array[hoveredEl.index];
                            }
                            buildingStyleChange(viewState,data[hoveredEl.index],graphListForm.layerGraphCooporationFormList,backgroundColor);
                        }
                        // 2: ダッシュボード->3D都市モデルビューワ（レイヤ切替）
                        cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==2);
                        if(cooperationTypeIndex > -1){
                            layerChangeForGraphList(viewState,graphListForm.layerGraphCooporationFormList,data[hoveredEl.index],graphListForm.graphId);
                        }
                    }
                }
            },
            events: function (element: any) {
                const chart = element.chart;
                chart.canvas.onmouseout =  function(e: any) {
                    chart.data.datasets.forEach((dataset: any, i: any) => {
                        if (dataset.type != "line") {
                            dataset.backgroundColor = backgroundColor1Array;
                            dataset.borderColor = borderColor1Array;
                        } else {
                            dataset.backgroundColor = backgroundColor2Array;
                            dataset.borderColor = borderColor2Array;
                        }
                    });
                    chart.update();
                }
                return ['mousemove', 'mouseout', 'click', 'touchstart', 'touchmove'];
            }
        };

        // yLabel無し or 背景色がリスト指定の場合凡例を非表示
        if(y1Label == "" || y2Label == "" || backgroundColor1List.length > 1 || backgroundColor2List.length > 1){
            barOptions["plugins"]["legend"] = { display: false, };
        }
    
        //グラフデータを定義
        const barData = {
            labels: labelList,
            datasets: [
                {
                    label: y1Label,
                    data: dataList1,
                    borderColor: borderColor1Array,
                    backgroundColor: backgroundColor1Array,
                    borderWidth:1,
                    order: 2,
                    yAxisID: "y1",
                },
                {
                    type: "line",
                    label: y2Label,
                    data: dataList2,
                    borderColor: borderColor2Array,
                    backgroundColor: backgroundColor2Array,
                    order: 1,
                    yAxisID: "y2",
                }
            ],
        };

        //カスタムプラグインを定義
        const customPlugin = {
            id: 'yUnit',
            afterDraw (chart: any, args: any, options: any) {
                const { ctx, chartArea: { top, right, bottom, left, width, height } } = chart;
                ctx.save();
                ctx.font = "11px Arial";
                ctx.fillStyle = "gray";
                ctx.textAlign = 'left';
                if(y1Unit != ""){
                    ctx.fillText('('+y1Unit+')', 0, top-15);
                }
                ctx.textAlign = 'right';
                if(y2Unit != ""){
                    ctx.fillText('('+y2Unit+')', right+30, top-15);
                }
                if(y2Unit != ""){
                    ctx.fillText('('+y2Unit+')', right+30, top-15);
                }
                if(title != undefined && title != null){
                    let fontSize = width * 0.05;
                    if(width < 150 && fontSize < 12){
                        fontSize = 12;
                    }else if(width >= 150 && fontSize > 15){
                        fontSize = 15;
                    }
                    ctx.font = "bold "+fontSize+"px Arial";
                    ctx.fillStyle = "black";
                    ctx.textAlign = 'left';
                    const textWidth = ctx.measureText(title).width;
                    ctx.fillText(''+title+'', ((width-textWidth)/2)+left,fontSize);
                }
                ctx.restore();
            }
        };
    
        return {
            typeId: 1,
            options: barOptions,
            data: barData,
            custom: {
                customPlugin: customPlugin
            },
            graphName:graphListForm.graphName,
        };
    }

    /**
     * 円グラフ データを取得
     * @param graphListForm 
     * @param data 
     * @returns chart-js表示用円グラフデータ
     */
    getDoughnutGraphData( graphListForm: any, data: any) : Object {

        const viewState = this.viewState;

        //各対象のカラム名を取得
        const graphListTemplateValFormList = graphListForm.graphListTemplateValFormList;
        const labelColumnName = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "labelColumnName")?.itemValue;
        const dataColumnName = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "dataColumnName")?.itemValue;
        let dataUnit = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "dataUnit")?.itemValue;
        //unitが未定義の場合
        if(dataUnit == null || dataUnit == undefined || !isNaN(dataUnit)){
            dataUnit = "";
        }
        //対象のカラム名からラベル・データを取得
        let labelList: any = [];
        let dataList: any = [];
        for (let i = 0; i < data.length; i++) {
            if (data[i][labelColumnName]){
                let labelColumn = data[i][labelColumnName];
                labelColumn = String(labelColumn);
                labelList.push(labelColumn?.trim());
            }
            if (data[i][dataColumnName] != undefined && data[i][dataColumnName] != null){
                data[i][dataColumnName] = Number(data[i][dataColumnName]);
                if(isNaN(data[i][dataColumnName])){
                    data[i][dataColumnName] = 0;
                }
                dataList.push(data[i][dataColumnName]);
            }else{
                dataList.push(0);
            }
        }
    
        //オプション値をセット
        let backgroundColorList = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "backgroundColorList")?.itemValue;
        if (backgroundColorList) {
            backgroundColorList = backgroundColorList.split(",");
        }
        let labelColorList = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "labelColorList")?.itemValue;
        if(labelColorList){
            labelColorList = JSON.parse(labelColorList);
            backgroundColorList = [];
            for (let i = 0; i < labelList.length; i++) {
                if(labelColorList[labelList[i]]){
                    backgroundColorList.push(labelColorList[labelList[i]]);
                }else{
                    let randomColor = "#";
                    for(let i = 0; i < 6; i++) {
                        randomColor += (16*Math.random() | 0).toString(16);
                    }
                    backgroundColorList.push(randomColor);
                }
            }
        }
        //背景色が未定義 or 足りてない場合はランダムにcolorを生成
        if(!backgroundColorList || Object.keys(backgroundColorList).length < Object.keys(dataList).length){
            if(!backgroundColorList) backgroundColorList = [];
            for(let i=backgroundColorList.length;i<dataList.length;i++){
                let randomColor = "";
                do{
                    randomColor = "#";
                    for(let i = 0; i < 6; i++) {
                        randomColor += (16*Math.random() | 0).toString(16);
                    }
                }while(backgroundColorList.findIndex((backgroundColor:any)=>backgroundColor==randomColor) > -1);
                backgroundColorList[i] = randomColor;
            }
        }
        let borderColorList = new Array();
        dataList.forEach((data: any, i: any) => {
            borderColorList.push("#ffffff");
        });
    
        //中央部表示用の合計値を算出
        let sum = 0;
        for (let i = 0; i < dataList.length; i++) {
            sum += dataList[i];
        }

        const title = graphListForm.graphName;
        const unit = dataUnit;

        //1: 3D都市モデルビューワ->ダッシュボード（データ絞込）→グラフの場合highlight
        const linkedFeatureIdList = this.viewState?.linkedFeatureIdList;
        let attributeNameList:any = [];
        let attributeColumnNameList:any = [];
        if(linkedFeatureIdList && graphListForm.layerGraphCooporationFormList){
            for(let i=0;i<graphListForm.layerGraphCooporationFormList.length;i++){
                const cooperationTypeIndex = i;
                if(graphListForm.layerGraphCooporationFormList[cooperationTypeIndex].cooperationType == 1){
                    let cooperationOption = graphListForm.layerGraphCooporationFormList[cooperationTypeIndex].cooperationOption;
                    cooperationOption = JSON.parse(cooperationOption);
                    Object.keys(linkedFeatureIdList).forEach(featureAttributeName=>{
                            if(cooperationOption.featureAttributeName == featureAttributeName){
                                attributeNameList.push(cooperationOption.featureAttributeName);
                                attributeColumnNameList.push(cooperationOption.featureAttributeColumnName);
                            }
                    })
                }
            }
        }
        //連携対象のfeatureの属性がある場合のみ
        if(attributeNameList.length>0 && attributeColumnNameList.length>0){
            const highlightIndexList = [];
            for(let i=0;i<data.length;i++){
                for(let j=0;j<attributeNameList.length;j++){
                    let attributeValue = data[i][attributeColumnNameList[j]];
                    try{
                        attributeValue = attributeValue.trim();
                    }catch(e){}
                    if (linkedFeatureIdList[attributeNameList[j]] 
                        && linkedFeatureIdList[attributeNameList[j]].findIndex((linkedFeatureId:any) => linkedFeatureId == attributeValue) > -1) {
                        if(highlightIndexList.findIndex(highlightIndex=>highlightIndex==i)<0){
                            highlightIndexList.push(i);
                        }
                    }
                }
            }
            //連携対象のfeatureの属性がありfeatureIdが該当するものがあればハイライト表示
            if(highlightIndexList.length > 0){
                for(let i =0;i<backgroundColorList.length;i++){
                    backgroundColorList[i] = backgroundColorList[i] + '4D';
                }
                for(let i =0;i<highlightIndexList.length;i++){
                    backgroundColorList[highlightIndexList[i]] = backgroundColorList[highlightIndexList[i]].replace("4D","");
                }
            }
        }
    
    
        //カスタムプラグインを定義
        const centerText = {
            id: 'centerText',
            beforeDraw (chart: any, args: any, options: any) {
                let provisionalTotal = 0;
                try{
                    if(chart.data.datasets[0]?.data){
                        for (let i = 0; i < chart.data.datasets[0].data.length; i++) {
                            provisionalTotal += chart.data.datasets[0].data[i];
                        }
                    }
                }catch(e){}
                const { ctx, chartArea: { top, right, bottom, left, width, height } } = chart;
                const x = chart.boxes[1].right + (width / 2);
                const y = top + (height / 2);
                //合計値に応じてfont sizeを変更する
                let fontSize = width * 0.09;
                if(provisionalTotal>1000){
                    fontSize = width * 0.08;
                }
                if(provisionalTotal>10000){
                    fontSize = width * 0.07;
                }
                if(fontSize > 30){
                    fontSize = 30;
                }
                ctx.save();
                ctx.font = fontSize + 'px sans-serif';
                ctx.textAlign = 'center';
                ctx.fillStyle = "black";
                ctx.fillText(provisionalTotal.toLocaleString() + unit, x, y);
                ctx.font = fontSize + 'px sans-serif';
                ctx.textAlign = 'center';
                ctx.fillStyle = "gray";
                ctx.fillText('合計', x, y + (fontSize + 5));
                if(title != undefined && title != null){
                    let fontSize = width * 0.05;
                    if(width >= 200 && fontSize < 15){
                        fontSize = 15;
                    }else if(width >= 150 && fontSize < 13){
                        fontSize = 13;
                    }else if(width < 150 && fontSize < 11){
                        fontSize = 11;
                    }
                    ctx.font = "bold "+fontSize+"px Arial";
                    ctx.fillStyle = "black";
                    ctx.textAlign = 'left';
                    const textWidth = ctx.measureText(title).width;
                    ctx.fillText(''+title+'', ((width-textWidth)/2)+(left/2),fontSize);
                }
                ctx.restore();
            }
        };
    
        //グラフオプションを定義
        const ua = window.navigator.userAgent.toLowerCase();
        let doughnutOptions = {
            responsive: true,
            maintainAspectRatio: false,
            layout: {
                padding: {
                    left: 0,
                    right: 0,
                },
            },
            plugins: {
                title: {
                    display: true,
                    text: "",
                },
                legend: {
                    position: 'left',
                    labels: {
                        boxWidth:7,
                        font: {
                            size: 9
                        },
                        usePointStyle: true,
                        generateLabels: (chart:any) => {
                            if (chart.data.datasets.length > 0) {
                                let result:any = [];
                                chart.data.datasets[0].data.map((mapping:any, index:any) => {
                                    let options:any = {};
                                    options['fillStyle'] = chart.data.datasets[0].backgroundColor[index]
                                    if(chart.data.labels[index]){
                                        options['text'] = ' '+chart.data.labels[index].trim();
                                    }else{
                                        options['text'] = '';
                                    }
                                    options['index'] = index;
                                    result.push(options)       
                                });
                                try{
                                    const width = chart.width;
                                    let fontSize = width * 0.04;
                                    if(fontSize > 14){
                                        fontSize = 14;
                                    }
                                    chart.config.options.plugins.legend.labels.font.size = fontSize;
                                }catch(e){}
                                return result;
                            }
                        }
                    },
                },
                tooltip: {
                    displayColors: false,
                    callbacks: {
                        beforeLabel: function (context: any) {
                            if (context.dataIndex == null
                                || context.dataIndex < 0
                                || context.dataIndex >= labelList.length)
                                return "";
                            let label = "項目";
                            label += ': ';
                            label += labelList[context.dataIndex];
                            return label;
                        },
                        label: function (context: any) {
                            if (context.parsed == null) return "";
                            let label = '項目値';
                            label += ': ';
                            label += Number(context.parsed).toLocaleString() + " " + unit;
                            return label;
                        },
                        afterLabel: function (context: any) {
                            if (context.parsed == null) return "";
                            let label = '割合';
                            label += ': ';
                            label += ((Number(context.parsed) / sum) * 100).toFixed(2) + " %";
                            return label;
                        }
                    },
                },
            },
            onHover: function (evt: any, element: any, chart: any) {
                if (element && element.length > 0) {
                    const hoveredEl = chart.getElementsAtEventForMode(evt, 'point', {
                        intersect: true
                    }, true)[0];
                    if (hoveredEl && hoveredEl.index > -1) {
                        const backgroundColorListCopy: any = [...backgroundColorList];
                        for (let i = 0; i < backgroundColorListCopy.length; i++) {
                            if(backgroundColorListCopy[i].indexOf("4D") < 0){
                                backgroundColorListCopy[i] = backgroundColorListCopy[i] + "4D";
                            }
                        }
                        chart.data.datasets.forEach((dataset: any, i: any) => {
                            backgroundColorListCopy[hoveredEl.index] = backgroundColorListCopy[hoveredEl.index].replace("4D", "");
                            dataset.backgroundColor = backgroundColorListCopy;
                        });
                        chart.update();
                    }
                }else{
                    chart.data.datasets.forEach((dataset: any, i: any) => {
                        dataset.backgroundColor = backgroundColorList;
                    });
                    chart.update();
                }
                if ((ua.indexOf('macintosh') > -1 || ua.indexOf('ipad') > -1 || (ua.indexOf('Android') > -1 && ua.indexOf('Mobile') == -1)) && 'ontouchend' in document) {
                    try{
                    if (element.length > 0) {
                        const hoveredEl = chart.getElementsAtEventForMode(evt, 'point', {
                            intersect: true
                        }, true)[0];
                        if (hoveredEl && hoveredEl.index > -1) {
                            // 0: ダッシュボード->3D都市モデルビューワ（フォーカス)
                            let cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==0);
                            if(cooperationTypeIndex > -1){
                                //graphFocus(viewState,data[hoveredEl.index],graphListForm.layerGraphCooporationFormList);
                            }
                            // 4: ダッシュボード->3D建物モデル（style切替）
                            cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==4);
                            if(cooperationTypeIndex > -1){
                                buildingStyleChange(viewState,data[hoveredEl.index],graphListForm.layerGraphCooporationFormList,backgroundColorList[hoveredEl.index]);
                            }
                            // 2: ダッシュボード->3D都市モデルビューワ（レイヤ切替）
                            cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==2);
                            if(cooperationTypeIndex > -1){
                                layerChangeForGraphList(viewState,graphListForm.layerGraphCooporationFormList,data[hoveredEl.index],graphListForm.graphId);
                            }
                        }
                    }
                    }catch(e){}
                }
            },
            onClick: function(evt: any, element: any, chart: any) {
                if (element.length > 0) {
                    const hoveredEl = chart.getElementsAtEventForMode(evt, 'point', {
                        intersect: true
                    }, true)[0];
                    if (hoveredEl && hoveredEl.index > -1) {
                        // 0: ダッシュボード->3D都市モデルビューワ（フォーカス)
                        let cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==0);
                        if(cooperationTypeIndex > -1){
                            graphFocus(viewState,data[hoveredEl.index],graphListForm.layerGraphCooporationFormList);
                        }
                        // 4: ダッシュボード->3D建物モデル（style切替）
                        cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==4);
                        if(cooperationTypeIndex > -1){
                            buildingStyleChange(viewState,data[hoveredEl.index],graphListForm.layerGraphCooporationFormList,backgroundColorList[hoveredEl.index]);
                        }
                        // 2: ダッシュボード->3D都市モデルビューワ（レイヤ切替）
                        cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==2);
                        if(cooperationTypeIndex > -1){
                            layerChangeForGraphList(viewState,graphListForm.layerGraphCooporationFormList,data[hoveredEl.index],graphListForm.graphId);
                        }
                    }
                }
            },
            events: function (element: any) {
                const chart = element.chart;
                chart.canvas.onmouseout = function(e: any){
                    chart.data.datasets.forEach((dataset: any, i: any) => {
                        dataset.backgroundColor = backgroundColorList;
                    });
                    chart.update();
                }
                return ['mousemove', 'mouseout', 'click', 'touchstart', 'touchmove'];
            }
        };
    
        //グラフデータを定義
        const doughnutData = {
            labels: labelList.map((label: any, index: any) => `${label}: ${((dataList[index] / sum) * 100).toFixed(2)}%`),
            datasets: [
                {
                    data: dataList,
                    borderColor: borderColorList,
                    backgroundColor: backgroundColorList,
                }
            ],
        };
    
        return {
            typeId: 2,
            options: doughnutOptions,
            data: doughnutData,
            custom: {
                centerText: centerText
            },
            graphName:graphListForm.graphName,
        };
    }

    /**
     * 棒グラフ データを取得
     * @param graphListForm 
     * @param data 
     * @returns chart-js表示用棒グラフデータ
     */
    getBarGraphData( graphListForm: any, data: any) : Object {

        const viewState = this.viewState;

        //各対象のカラム名を取得
        const graphListTemplateValFormList = graphListForm.graphListTemplateValFormList;
        const xColumnName = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "xColumnName")?.itemValue;
        const subLabelColumnName = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "subLabelColumnName")?.itemValue;
        const rodYColumnName = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "rodYColumnName")?.itemValue;
        const stackFlag = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "stackFlag")?.itemValue;
        
        //対象のカラム名からラベル・データを取得
        let labelList: any = [];
        let duplicationLabelList: any = [];
        let duplicationCount: any = 0;
        let dataList: any = [];
        let duplicationDataList: any = [];
        let subLabelList: any = [];
        let duplicationSubLabelList: any = [];
        for (let i = 0; i < data.length; i++) {
            if (data[i][xColumnName]){
                let xColumn = data[i][xColumnName];
                xColumn = String(xColumn);
                labelList.push(xColumn?.trim());
                if(stackFlag && stackFlag == 1 && data[0][xColumnName] == data[i][xColumnName] ){
                    duplicationCount = duplicationCount + 1;
                }
            }
            if (data[i][rodYColumnName] != undefined && data[i][rodYColumnName] != null){
                data[i][rodYColumnName] = Number(data[i][rodYColumnName]);
                if(isNaN(Number(data[i][rodYColumnName]))){
                    data[i][rodYColumnName] = 0;
                }
                dataList.push(data[i][rodYColumnName]);
            }else{
                dataList.push(0);
            }
            if (subLabelColumnName && data[i][subLabelColumnName]){
                let subLabel = data[i][subLabelColumnName];
                subLabel = String(subLabel);
                subLabelList.push(subLabel?.trim());
            }
        }

        //X軸が重複している場合(積み立て棒グラフ)はstack形式にする為、データを分割
        if(duplicationCount > 1){
            for(let i =0;i<labelList.length;i++){
                if(!duplicationLabelList[i%duplicationCount]){
                    duplicationLabelList[i%duplicationCount] = [];
                }
                duplicationLabelList[i%duplicationCount].push(labelList[i]);
            }
            for(let i =0;i<dataList.length;i++){
                if(!duplicationDataList[i%duplicationCount]){
                    duplicationDataList[i%duplicationCount] = [];
                }
                duplicationDataList[i%duplicationCount].push(dataList[i]);
            }
            for(let i =0;i<subLabelList.length;i++){
                if(!duplicationSubLabelList[i%duplicationCount]){
                    duplicationSubLabelList[i%duplicationCount] = [];
                }
                duplicationSubLabelList[i%duplicationCount].push(subLabelList[i]);
            }
        }
    
        //オプション値をセット
        const title = graphListForm.graphName;
        let yLabel = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "rodYLabel")?.itemValue;
        let xLabel = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "xLabel")?.itemValue;
        const yMax = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "rodYMax")?.itemValue;
        const yMin = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "rodYMin")?.itemValue;
        const yStepSize = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "rodYStepSize")?.itemValue;
        let yUnit = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "rodYUnit")?.itemValue;
        let backgroundColor = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "rodBackGroundColor")?.itemValue;
        let borderColor = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "rodBorderColor")?.itemValue;
        let direction = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "direction")?.itemValue;

        //labelとunitが未定義の場合
        if(yLabel == null || yLabel == undefined){
            yLabel = "";
        }
        if(xLabel == null || xLabel == undefined){
            xLabel = "";
        }
        if(yUnit == null || yUnit == undefined){
            yUnit = "";
        }
        //グラフ色が未定義の場合
        if(backgroundColor == null || backgroundColor == undefined){
            backgroundColor = "#192f60";
        }
        if(borderColor == null || borderColor == undefined){
            borderColor = "#192f60";
        }

        //グラフ色生成
        const backgroundColorArray = new Array();
        const borderColorArray = new Array();
        const backgroundColorList = backgroundColor.split(",");
        const borderColorList = borderColor.split(",");

        //通常形式
        if(duplicationCount <= 1){
            labelList.forEach((label: any, i: any) => {
                let backgroundColorTemp = backgroundColorList[i];
                if(!backgroundColorTemp){
                    backgroundColorTemp = backgroundColorList[backgroundColorList.length-1];
                }
                backgroundColorArray.push(backgroundColorTemp);
                let borderColorTemp = borderColorList[i];
                if(!borderColorTemp){
                    borderColorTemp = borderColorList[borderColorList.length-1];
                }
                borderColorArray.push(borderColorTemp);
            });
        //X軸が重複している場合(積み立て棒グラフ)
        }else{
            for(let i=0;i<duplicationCount;i++){
                for(let j=0;j<duplicationLabelList[i].length;j++){
                    let backgroundColorTemp = backgroundColorList[i];
                    if(!backgroundColorTemp){
                        backgroundColorTemp = "#192f60";
                    }
                    if(!backgroundColorArray[i]){
                        backgroundColorArray[i] = [];
                    }
                    backgroundColorArray[i].push(backgroundColorTemp);
                    let borderColorTemp = borderColorList[i];
                    if(!borderColorTemp){
                        borderColorTemp = "#192f60";
                    }
                    if(!borderColorArray[i]){
                        borderColorArray[i] = [];
                    }
                    borderColorArray[i].push(borderColorTemp);
                }
            }
        }

        //1: 3D都市モデルビューワ->ダッシュボード（データ絞込）→グラフの場合highlight
        const linkedFeatureIdList = viewState?.linkedFeatureIdList;
        let attributeNameList:any = [];
        let attributeColumnNameList:any = [];
        if(linkedFeatureIdList && graphListForm.layerGraphCooporationFormList){
            for(let i=0;i<graphListForm.layerGraphCooporationFormList.length;i++){
                const cooperationTypeIndex = i;
                if(graphListForm.layerGraphCooporationFormList[cooperationTypeIndex].cooperationType == 1){
                    let cooperationOption = graphListForm.layerGraphCooporationFormList[cooperationTypeIndex].cooperationOption;
                    cooperationOption = JSON.parse(cooperationOption);
                    Object.keys(linkedFeatureIdList).forEach(featureAttributeName=>{
                            if(cooperationOption.featureAttributeName == featureAttributeName){
                                attributeNameList.push(cooperationOption.featureAttributeName);
                                attributeColumnNameList.push(cooperationOption.featureAttributeColumnName);
                            }
                    })
                }
            }
        }
        //連携対象のfeatureの属性がある場合のみ
        if(attributeNameList.length>0 && attributeColumnNameList.length>0){
            const highlightIndexList = [];
            for(let i=0;i<data.length;i++){
                for(let j=0;j<attributeNameList.length;j++){
                    let attributeValue = data[i][attributeColumnNameList[j]];
                    try{
                        attributeValue = attributeValue.trim();
                    }catch(e){}
                    if (linkedFeatureIdList[attributeNameList[j]] 
                        && linkedFeatureIdList[attributeNameList[j]].findIndex((linkedFeatureId:any) => linkedFeatureId == attributeValue) > -1) {
                        if(highlightIndexList.findIndex(highlightIndex=>highlightIndex==i)<0){
                            highlightIndexList.push(i);
                        }
                    }
                }
            }
            //連携対象のfeatureの属性がありfeatureIdが該当するものがあればハイライト表示
            if(highlightIndexList.length > 0){
                if(duplicationCount <= 1){
                    for(let i =0;i<backgroundColorArray.length;i++){
                        backgroundColorArray[i] = backgroundColorArray[i] + '4D';
                    }
                    for(let i =0;i<highlightIndexList.length;i++){
                        backgroundColorArray[highlightIndexList[i]] = backgroundColorArray[highlightIndexList[i]].replace("4D","");
                    }
                }else{
                    for(let i =0;i<backgroundColorArray.length;i++){
                        for(let j =0;j<backgroundColorArray[i].length;j++){
                            backgroundColorArray[i][j] = backgroundColorArray[i][j] + '4D';
                        }
                    }
                    for(let i =0;i<highlightIndexList.length;i++){
                        const index = Math.floor(highlightIndexList[i] / duplicationCount);
                        if(index > -1){
                            for(let j =0;j<backgroundColorArray.length;j++){
                                backgroundColorArray[j][index] = backgroundColorArray[j][index].replace("4D","");
                            }
                        }
                    }

                }
            }
        }
    
        //グラフオプションを定義
        const ua = window.navigator.userAgent.toLowerCase();
        const barOptions:any = {
            maintainAspectRatio: false,
            responsive: true,
            plugins: {
                title: {
                    display: true,
                    text: "",
                },
                tooltip: {
                    displayColors: false,
                    callbacks: {
                        title: function (context: any) {
                            return "";
                        },
                        beforeLabel: function (context: any) {
                            if (context.dataIndex == null
                                || context.dataIndex < 0
                                || context.dataIndex >= labelList.length)
                                return "";
                            let label = xLabel;
                            if(label!=""){
                                label += ': ';
                            }
                            if(duplicationCount <= 1 && labelList[context.dataIndex]){
                                label += labelList[context.dataIndex];
                            }else if(duplicationCount > 1 && duplicationLabelList[0] && duplicationLabelList[0][context.dataIndex]){
                                label += duplicationLabelList[0][context.dataIndex];
                            }
                            return label;
                        },
                        label: function (context: any) {
                            let y = context.parsed.y;
                            if(direction == "horizontal"){
                                y = context.parsed.x;
                            }
                            if (y == null) return "";
                            let label = context.dataset.label || '';
                            if(duplicationCount <= 1 && subLabelList[context.dataIndex]){
                                label = subLabelList[context.dataIndex];
                            }else if(duplicationCount > 1 && duplicationSubLabelList[context.datasetIndex] && duplicationSubLabelList[context.datasetIndex][context.dataIndex]){
                                label = duplicationSubLabelList[context.datasetIndex][context.dataIndex];
                            }
                            if (context.dataset.label == yLabel) {
                                if(label!=""){
                                    label += ': ';
                                }
                                label += Number(y).toLocaleString() + " " + yUnit;
                            }
                            return label;
                        }
                    }
                },
            },
            scales: {
                x: {
                    title: {
                        display: true,
                        text: xLabel
                    },
                    stacked: false,
                },
                y: {
                    title: {
                        display: false,
                        text: yLabel
                    },
                    stacked: false,
                    max: Number(yMax),
                    min: Number(yMin),
                    ticks: {
                        // 目安のstepSize(表示値は調整かかる)
                        stepSize: Number(yStepSize),
                        beginAtZero: true,
                        callback: function (value: any) {
                            return `${Number(value).toLocaleString()}`;
                        },
                    },
                },
            },
            onHover: function (evt: any, element: any, chart: any) {
                if (element && element.length > 0) {
                    const hoveredEl = chart.getElementsAtEventForMode(evt, 'point', {
                        intersect: true
                    }, true)[0];
                    if (hoveredEl && hoveredEl.index > -1) {
                        const backgroundColorArray = new Array();
                        const borderColorArray = new Array();
                        //通常の形式
                        if(duplicationCount <= 1){
                            labelList.forEach((label: any, i: any) => {
                                let backgroundColorTemp = backgroundColorList[i];
                                if(!backgroundColorTemp){
                                    backgroundColorTemp = backgroundColorList[backgroundColorList.length-1];
                                }
                                backgroundColorArray.push(backgroundColorTemp + "4D");
                                let borderColorTemp = borderColorList[i];
                                if(!borderColorTemp){
                                    borderColorTemp = borderColorList[borderColorList.length-1];
                                }
                                borderColorArray.push(borderColorTemp + "4D");
                            });
                        //X軸が重複している場合(積み立て棒グラフ)
                        }else{
                            for(let i=0;i<duplicationCount;i++){
                                for(let j=0;j<duplicationLabelList[i].length;j++){
                                    let backgroundColorTemp = backgroundColorList[i];
                                    if(!backgroundColorTemp){
                                        backgroundColorTemp = "#192f60";
                                    }
                                    if(!backgroundColorArray[i]){
                                        backgroundColorArray[i] = [];
                                    }
                                    backgroundColorArray[i].push(backgroundColorTemp + "4D");
                                    let borderColorTemp = borderColorList[i];
                                    if(!borderColorTemp){
                                        borderColorTemp = "#192f60";
                                    }
                                    if(!borderColorArray[i]){
                                        borderColorArray[i] = [];
                                    }
                                    borderColorArray[i].push(borderColorTemp + "4D");
                                }
                            }
                        }
                        chart.data.datasets.forEach((dataset: any, i: any) => {
                            //通常の形式
                            if(duplicationCount <= 1){
                                backgroundColorArray[hoveredEl.index] = backgroundColorArray[hoveredEl.index].replace("4D", "");
                                borderColorArray[hoveredEl.index] = borderColorArray[hoveredEl.index].replace("4D", "");
                                dataset.backgroundColor = backgroundColorArray;
                                dataset.borderColor = borderColorArray;
                            //X軸が重複している場合(積み立て棒グラフ)
                            }else{
                                backgroundColorArray[i][hoveredEl.index] = backgroundColorArray[i][hoveredEl.index].replace("4D", "");
                                borderColorArray[i][hoveredEl.index] = borderColorArray[i][hoveredEl.index].replace("4D", "");
                                dataset.backgroundColor = backgroundColorArray[i];
                                dataset.borderColor = borderColorArray[i];
                            }
                        });
                        chart.update();
                        if(duplicationCount <= 1){
                            chart.legend.legendItems[0].fillStyle = backgroundColor;
                            chart.legend.legendItems[0].strokeStyle = borderColor;
                        }
                    }
                }else{
                    chart.data.datasets.forEach((dataset: any, i: any) => {
                        //通常の形式
                        if(duplicationCount <= 1){
                            dataset.backgroundColor = backgroundColorArray;
                            dataset.borderColor = borderColorArray;
                        //X軸が重複している場合(積み立て棒グラフ)
                        }else{
                            dataset.backgroundColor = backgroundColorArray[i];
                            dataset.borderColor = borderColorArray[i];
                        }
                    });
                    chart.update();
                }
                if ((ua.indexOf('macintosh') > -1 || ua.indexOf('ipad') > -1 || (ua.indexOf('Android') > -1 && ua.indexOf('Mobile') == -1)) && 'ontouchend' in document) {
                    try{
                    if (element.length > 0) {
                        const hoveredEl = chart.getElementsAtEventForMode(evt, 'point', {
                            intersect: true
                        }, true)[0];
                        if (hoveredEl && hoveredEl.index > -1) {
                            let dataIndex = hoveredEl.index;
                            if(duplicationCount > 1){
                                dataIndex = (duplicationCount * hoveredEl.index) + hoveredEl.datasetIndex;
                            }
                            // 0: ダッシュボード->3D都市モデルビューワ（フォーカス)
                            let cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==0);
                            if(cooperationTypeIndex > -1){
                                //graphFocus(viewState,data[dataIndex],graphListForm.layerGraphCooporationFormList);
                            }
                            // 4: ダッシュボード->3D建物モデル（style切替）
                            cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==4);
                            if(cooperationTypeIndex > -1){
                                let backgroundColor = "";
                                if(duplicationCount <= 1){
                                    backgroundColor = backgroundColorArray[hoveredEl.index];
                                    buildingStyleChange(viewState,data[dataIndex],graphListForm.layerGraphCooporationFormList,backgroundColor);
                                }else{
                                    chart.data.datasets.forEach((dataset: any, i: any) => {
                                        backgroundColor = backgroundColorArray[i][hoveredEl.index];
                                    })
                                    buildingStyleChange(viewState,data[dataIndex],graphListForm.layerGraphCooporationFormList,backgroundColor);
                                }
                            }
                            // 2: ダッシュボード->3D都市モデルビューワ（レイヤ切替）
                            cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==2);
                            if(cooperationTypeIndex > -1){
                                layerChangeForGraphList(viewState,graphListForm.layerGraphCooporationFormList,data[dataIndex],graphListForm.graphId);
                            }
                        }
                    }
                    }catch(e){}
                }
            },
            onClick: function(evt: any, element: any, chart: any) {
                if (element.length > 0) {
                    const hoveredEl = chart.getElementsAtEventForMode(evt, 'point', {
                        intersect: true
                    }, true)[0];
                    if (hoveredEl && hoveredEl.index > -1) {
                        let dataIndex = hoveredEl.index;
                        if(duplicationCount > 1){
                            dataIndex = (duplicationCount * hoveredEl.index) + hoveredEl.datasetIndex;
                        }
                        // 0: ダッシュボード->3D都市モデルビューワ（フォーカス)
                        let cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==0);
                        if(cooperationTypeIndex > -1){
                            graphFocus(viewState,data[dataIndex],graphListForm.layerGraphCooporationFormList);
                        }
                        // 4: ダッシュボード->3D建物モデル（style切替）
                        cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==4);
                        if(cooperationTypeIndex > -1){
                            let backgroundColor = "";
                            if(duplicationCount <= 1){
                                backgroundColor = backgroundColorArray[hoveredEl.index];
                                buildingStyleChange(viewState,data[dataIndex],graphListForm.layerGraphCooporationFormList,backgroundColor);
                            }else{
                                chart.data.datasets.forEach((dataset: any, i: any) => {
                                    backgroundColor = backgroundColorArray[i][hoveredEl.index];
                                })
                                buildingStyleChange(viewState,data[dataIndex],graphListForm.layerGraphCooporationFormList,backgroundColor);
                            }
                        }
                        // 2: ダッシュボード->3D都市モデルビューワ（レイヤ切替）
                        cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==2);
                        if(cooperationTypeIndex > -1){
                            layerChangeForGraphList(viewState,graphListForm.layerGraphCooporationFormList,data[dataIndex],graphListForm.graphId);
                        }
                    }
                }
            },
            events: function (element: any) {
                const chart = element.chart;
                chart.canvas.onmouseout = function(e: any){
                    chart.data.datasets.forEach((dataset: any, i: any) => {
                        //通常の形式
                        if(duplicationCount <= 1){
                            dataset.backgroundColor = backgroundColorArray;
                            dataset.borderColor = borderColorArray;
                        //X軸が重複している場合(積み立て棒グラフ)
                        }else{
                            dataset.backgroundColor = backgroundColorArray[i];
                            dataset.borderColor = borderColorArray[i];
                        }
                    });
                    chart.update();
                }
                return ['mousemove', 'mouseout', 'click', 'touchstart', 'touchmove'];
            }
        };

        
        //横棒グラフの場合
        if(direction == "horizontal"){
            barOptions["indexAxis"] = "y";
            const temp = {...barOptions["scales"].x};
            barOptions["scales"].x = barOptions["scales"].y;
            barOptions["scales"].y = temp;
        }

        //yLabel無し or 背景色がリスト指定の場合凡例を非表示
        if( yLabel == "" || backgroundColorList.length > 1){
            barOptions["plugins"]["legend"] = { display: false, };
        }

        //X軸が重複している場合(積み立て棒グラフ)でサブラベルの指定がある場合は凡例のカスタム表示
        if(duplicationCount > 1 && duplicationSubLabelList.length == duplicationCount){
            barOptions["plugins"]["legend"] = {
                position: 'left',
                labels: {
                    boxWidth:5,
                    font: {
                        size: 9
                    },
                    usePointStyle: true,
                    generateLabels: (chart:any) => {
                        if (chart.data.datasets.length > 0) {
                            let result:any = [];
                            for(let i=duplicationCount-1;i>=0;i--){
                                let options:any = {};
                                options['fillStyle'] = backgroundColorArray[i][0];
                                if(duplicationSubLabelList[i] && duplicationSubLabelList[i][0]){
                                    options['text'] = duplicationSubLabelList[i][0].trim();
                                }
                                result.push(options)       
                            }
                            return result;
                        }
                    }
                },
            }
        }
    
        //グラフデータを定義
        let datasets:any = [];
        let labels:any = [];
        //通常の形式
        if(duplicationCount <= 1){
            datasets.push({
                label: yLabel,
                data: dataList,
                stack: 1,
                borderColor: borderColorArray,
                backgroundColor: backgroundColorArray,
                order: 2,
                borderWidth:1,
                yAxisID: "y",
            });
            labels = labelList;
        //X軸が重複している場合(積み立て棒グラフ)
        }else{
            //積み立て順(select順)の整合性が取れていない場合の表示考慮
            duplicationDataList = duplicationDataList.sort(function(a:any,b:any){
                return (a[0] < b[0]) ? -1 : 1;
            })
            if(stackFlag && stackFlag == 1){
                barOptions["scales"].y.stacked = true;
                barOptions["scales"].x.stacked = true;
            }
            for(let i=0;i<duplicationCount;i++){
                //オプションでstackFlagが1の場合積み立て表示、それ以外の場合通常表示
                if(stackFlag && stackFlag == 1){
                    datasets.push({
                        label: yLabel,
                        data: duplicationDataList[i],
                        stack: 1,
                        borderColor: borderColorArray[i],
                        backgroundColor: backgroundColorArray[i],
                        order: i+1,
                        borderWidth:1,
                        yAxisID: "y",
                    });
                }else{
                    datasets.push({
                        label: yLabel,
                        data: duplicationDataList[i],
                        stack: i+1,
                        borderColor: borderColorArray[i],
                        backgroundColor: backgroundColorArray[i],
                        order: i+1,
                        borderWidth:1,
                        yAxisID: "y",
                    });
                }
            }
            labels = duplicationLabelList[0];
        }

        const barData = {
            labels: labels,
            datasets: datasets,
        };

        //カスタムプラグインを定義
        const customPlugin = {
            id: 'yUnit',
            beforeDraw (chart: any, args: any, options: any) {
                const { ctx, chartArea: { top, right, bottom, left, width, height } } = chart;
                ctx.save();
                ctx.font = "11px Arial";
                ctx.fillStyle = "gray";
                ctx.textAlign = 'left';
                if(yUnit != ""){
                    if(duplicationCount > 1 && duplicationSubLabelList.length == duplicationCount){
                        ctx.fillText('('+yUnit+')', right/4, top-10);
                    }else if(direction == "horizontal"){
                        ctx.fillText('('+yUnit+')', width-40, bottom+20);
                    }else{
                        ctx.fillText('('+yUnit+')', 10, top-10);
                    }
                }
                if(title != undefined && title != null){
                    let fontSize = (width+left) * 0.05;
                    if(width < 150 && fontSize < 12){
                        fontSize = 12;
                    }else if(width >= 150 && fontSize > 15){
                        fontSize = 15;
                    }
                    ctx.font = "bold "+fontSize+"px Arial";
                    ctx.fillStyle = "black";
                    ctx.textAlign = 'left';
                    if(!barOptions["plugins"]["legend"]["display"] && direction != "horizontal"){
                        const textWidth = ctx.measureText(title).width;
                        ctx.fillText(''+title+'', ((width-textWidth)/2)+left,fontSize);
                    }else{
                        const textWidth = ctx.measureText(title).width;
                        ctx.fillText(''+title+'', ((width-textWidth)/2)+(left/2),fontSize);
                    }
                }
                ctx.restore();
            }
        };
    
        return {
            typeId: 3,
            options: barOptions,
            data: barData,
            custom: {customPlugin:customPlugin},
            graphName:graphListForm.graphName,
        };
    }

    /**
    * 線グラフ　データを取得
    * @param graphListForm 
    * @param data 
    * @returns chart-js表示用線グラフデータ
    */
    getLineGraphData ( graphListForm: any, data: any) : Object {

        const viewState = this.viewState;

        //各対象のカラム名を取得
        const graphListTemplateValFormList = graphListForm.graphListTemplateValFormList;
        const xColumnName = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "xColumnName")?.itemValue;
        const lineYColumnName = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "lineYColumnName")?.itemValue;

        //対象のカラム名からラベル・データを取得
        let labelList: any = [];
        let dataList: any = [];
        for (let i = 0; i < data.length; i++) {
            if (data[i][xColumnName]){
                let xColumn = data[i][xColumnName];
                xColumn = String(xColumn);
                labelList.push(xColumn?.trim());
            }
            if (data[i][lineYColumnName] != undefined && data[i][lineYColumnName] != null){
                data[i][lineYColumnName] = Number(data[i][lineYColumnName]);
                if(isNaN(data[i][lineYColumnName])){
                    data[i][lineYColumnName] = 0;
                }
                dataList.push(data[i][lineYColumnName]);
            }else{
                dataList.push(0);
            }
        }

        //オプション値をセット
        const title = graphListForm.graphName;
        let yLabel = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "lineYLabel")?.itemValue;
        let xLabel = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "xLabel")?.itemValue;
        const yMax = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "lineYMax")?.itemValue;
        const yMin = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "lineYMin")?.itemValue;
        const yStepSize = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "lineYStepSize")?.itemValue;
        let yUnit = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "lineYUnit")?.itemValue;
        let backgroundColor = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "lineBackGroundColor")?.itemValue;
        let borderColor = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm?.attributeName == "lineBorderColor")?.itemValue;
        
        //labelとunitが未定義の場合
        if(yLabel == null || yLabel == undefined){
            yLabel = "";
        }
        if(xLabel == null || xLabel == undefined){
            xLabel = "";
        }
        if(yUnit == null || yUnit == undefined){
            yUnit = "";
        }
        //グラフ色が未定義の場合
        if(backgroundColor == null || backgroundColor == undefined){
            backgroundColor = "#ffffff";
        }
        if(borderColor == null || borderColor == undefined){
            borderColor = "#e95464";
        }
        //グラフ色生成
        const backgroundColorArray = new Array();
        const borderColorArray = new Array();
        const backgroundColorList = backgroundColor.split(",");
        const borderColorList = borderColor.split(",");
        labelList.forEach((label: any, i: any) => {
            let backgroundColorTemp = backgroundColorList[i];
            if(!backgroundColorTemp){
                backgroundColorTemp = backgroundColorList[backgroundColorList.length-1];
            }
            backgroundColorArray.push(backgroundColorTemp);
            let borderColorTemp = borderColorList[i];
            if(!borderColorTemp){
                borderColorTemp = borderColorList[borderColorList.length-1];
            }
            borderColorArray.push(borderColorTemp);
        });

        //1: 3D都市モデルビューワ->ダッシュボード（データ絞込）→グラフの場合highlight
        const linkedFeatureIdList = this.viewState?.linkedFeatureIdList;
        let attributeNameList:any = [];
        let attributeColumnNameList:any = [];
        if(linkedFeatureIdList && graphListForm.layerGraphCooporationFormList){
            for(let i=0;i<graphListForm.layerGraphCooporationFormList.length;i++){
                const cooperationTypeIndex = i;
                if(graphListForm.layerGraphCooporationFormList[cooperationTypeIndex].cooperationType == 1){
                    let cooperationOption = graphListForm.layerGraphCooporationFormList[cooperationTypeIndex].cooperationOption;
                    cooperationOption = JSON.parse(cooperationOption);
                    Object.keys(linkedFeatureIdList).forEach(featureAttributeName=>{
                            if(cooperationOption.featureAttributeName == featureAttributeName){
                                attributeNameList.push(cooperationOption.featureAttributeName);
                                attributeColumnNameList.push(cooperationOption.featureAttributeColumnName);
                            }
                    })
                }
            }
        }

        //連携対象のfeatureの属性がある場合のみ
        if(attributeNameList.length>0 && attributeColumnNameList.length>0){
            const highlightIndexList = [];
            for(let i=0;i<data.length;i++){
                for(let j=0;j<attributeNameList.length;j++){
                    let attributeValue = data[i][attributeColumnNameList[j]];
                    try{
                        attributeValue = attributeValue.trim();
                    }catch(e){}
                    if (linkedFeatureIdList[attributeNameList[j]] 
                        && linkedFeatureIdList[attributeNameList[j]].findIndex((linkedFeatureId:any) => linkedFeatureId == attributeValue) > -1) {
                        if(highlightIndexList.findIndex(highlightIndex=>highlightIndex==i)<0){
                            highlightIndexList.push(i);
                        }
                    }
                }
            }
            //連携対象のfeatureの属性がありfeatureIdが該当するものがあればハイライト表示
            if(highlightIndexList.length > 0){
                for(let i =0;i<backgroundColorArray.length;i++){
                    backgroundColorArray[i] = backgroundColorArray[i] + '4D';
                }
                for(let i =0;i<highlightIndexList.length;i++){
                    backgroundColorArray[highlightIndexList[i]] = backgroundColorArray[highlightIndexList[i]].replace("4D","");
                }
            }
        }

        //グラフオプションを定義
        const ua = window.navigator.userAgent.toLowerCase();
        const lineOptions:any = {
            maintainAspectRatio: false,
            responsive: true,
            plugins: {
                title: {
                    display: true,
                    text: "",
                },
                tooltip: {
                    displayColors: false,
                    callbacks: {
                        title: function (context: any) {
                            return "";
                        },
                        beforeLabel: function (context: any) {
                            if (context.dataIndex == null
                                || context.dataIndex < 0
                                || context.dataIndex >= labelList.length)
                                return "";
                            let label = xLabel;
                            if(label != ""){
                                label += ': ';
                            }
                            label += labelList[context.dataIndex];
                            return label;
                        },
                        label: function (context: any) {
                            if (context.parsed.y == null) return "";
                            let label = context.dataset.label || '';
                            if (context.dataset.label == yLabel) {
                                if(label != ""){
                                    label += ': ';
                                }
                                label += Number(context.parsed.y).toLocaleString() + " " + yUnit;
                            }
                            return label;
                        }
                    }
                }
            },
            scales: {
                x: {
                    title: {
                        display: true,
                        text: xLabel
                    },
                    stacked: false,
                },
                y: {
                    title: {
                        display: true,
                        text: yLabel
                    },
                    stacked: false,
                    max: Number(yMax),
                    min: Number(yMin),
                    ticks: {
                        // 目安のstepSize(表示値は調整かかる)
                        stepSize: Number(yStepSize),
                        beginAtZero: true,
                        callback: function (value: any) {
                            return `${Number(value).toLocaleString()}`;
                        }
                    }
                }
            },
            onHover: function (evt: any, element: any, chart: any) {
                if (element && element.length > 0) {
                    const hoveredEl = chart.getElementsAtEventForMode(evt, 'point', {
                        intersect: true
                    }, true)[0];
                    if (hoveredEl && hoveredEl.index > -1) {
                        const backgroundColorArray = new Array();
                        const borderColorArray = new Array();
                        labelList.forEach((label: any, i: any) => {
                            let backgroundColorTemp = backgroundColorList[i];
                            if(!backgroundColorTemp){
                                backgroundColorTemp = backgroundColorList[backgroundColorList.length-1];
                            }
                            backgroundColorArray.push(backgroundColorTemp+ '4D');
                            let borderColorTemp = borderColorList[i];
                            if(!borderColorTemp){
                                borderColorTemp = borderColorList[borderColorList.length-1];
                            }
                            borderColorArray.push(borderColorTemp+ '4D');
                        });
                        chart.data.datasets.forEach((dataset: any, i: any) => {
                            backgroundColorArray[hoveredEl.index] = backgroundColorArray[hoveredEl.index].replace("4D","");
                            borderColorArray[hoveredEl.index] = borderColorArray[hoveredEl.index].replace("4D","");
                            dataset.backgroundColor = backgroundColorArray;
                            dataset.borderColor = borderColorArray;
                        });
                        chart.update();
                        chart.legend.legendItems[0].fillStyle = backgroundColor;
                        chart.legend.legendItems[0].strokeStyle = borderColor;
                    }
                }else{
                    chart.data.datasets.forEach((dataset: any, i: any) => {
                        dataset.backgroundColor = backgroundColorArray;
                        dataset.borderColor = borderColorArray;
                    });
                    chart.update();
                }
                if ((ua.indexOf('macintosh') > -1 || ua.indexOf('ipad') > -1 || (ua.indexOf('Android') > -1 && ua.indexOf('Mobile') == -1)) && 'ontouchend' in document) {
                    try{
                    if (element.length > 0) {
                        const hoveredEl = chart.getElementsAtEventForMode(evt, 'point', {
                            intersect: true
                        }, true)[0];
                        if (hoveredEl && hoveredEl.index > -1) {
                            // 0: ダッシュボード->3D都市モデルビューワ（フォーカス)
                            let cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==0);
                            if(cooperationTypeIndex > -1){
                                //graphFocus(viewState,data[hoveredEl.index],graphListForm.layerGraphCooporationFormList);
                            }
                            // 4: ダッシュボード->3D建物モデル（style切替）
                            cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==4);
                            if(cooperationTypeIndex > -1){
                                buildingStyleChange(viewState,data[hoveredEl.index],graphListForm.layerGraphCooporationFormList,borderColorArray[hoveredEl.index]);
                            }
                            // 2: ダッシュボード->3D都市モデルビューワ（レイヤ切替）
                            cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==2);
                            if(cooperationTypeIndex > -1){
                                layerChangeForGraphList(viewState,graphListForm.layerGraphCooporationFormList,data[hoveredEl.index],graphListForm.graphId);
                            }
                        }
                    }
                    }catch(e){}
                }
            },
            onClick: function(evt: any, element: any, chart: any) {
                if (element.length > 0) {
                    const hoveredEl = chart.getElementsAtEventForMode(evt, 'point', {
                        intersect: true
                    }, true)[0];
                    if (hoveredEl && hoveredEl.index > -1) {
                        // 0: ダッシュボード->3D都市モデルビューワ（フォーカス)
                        let cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==0);
                        if(cooperationTypeIndex > -1){
                            graphFocus(viewState,data[hoveredEl.index],graphListForm.layerGraphCooporationFormList);
                        }
                        // 4: ダッシュボード->3D建物モデル（style切替）
                        cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==4);
                        if(cooperationTypeIndex > -1){
                            buildingStyleChange(viewState,data[hoveredEl.index],graphListForm.layerGraphCooporationFormList,borderColorArray[hoveredEl.index]);
                        }
                        // 2: ダッシュボード->3D都市モデルビューワ（レイヤ切替）
                        cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==2);
                        if(cooperationTypeIndex > -1){
                            layerChangeForGraphList(viewState,graphListForm.layerGraphCooporationFormList,data[hoveredEl.index],graphListForm.graphId);
                        }
                    }
                }
            },
            events: function (element: any) {
                const chart = element.chart;
                chart.canvas.onmouseout = function(e: any){
                    chart.data.datasets.forEach((dataset: any, i: any) => {
                        dataset.backgroundColor = backgroundColorArray;
                        dataset.borderColor = borderColorArray;
                    });
                    chart.update();
                }
                return ['mousemove', 'mouseout', 'click', 'touchstart', 'touchmove'];
            }
        };

        //yLabel無し or 背景色がリスト指定の場合凡例を非表示
        if(yLabel == "" || backgroundColorList.length > 1){
            lineOptions["plugins"]["legend"] = { display: false, };
        }

        //グラフデータを定義
        const lineData = {
            labels: labelList,
            datasets: [
                {
                    type: "line",
                    label: yLabel,
                    data: dataList,
                    borderColor: borderColorArray,
                    backgroundColor: backgroundColorArray,
                    order: 1,
                    yAxisID: "y",
                }
            ],
        };

        //カスタムプラグインを定義
        const customPlugin = {
            id: 'yUnit',
            afterDraw (chart: any, args: any, options: any) {
                const { ctx, chartArea: { top, right, bottom, left, width, height } } = chart;
                ctx.save();
                ctx.font = "11px Arial";
                ctx.fillStyle = "gray";
                ctx.textAlign = 'left';
                if(yUnit != ""){
                    ctx.fillText('('+yUnit+')', 0, top-15);
                }
                if(title != undefined && title != null){
                    let fontSize = width * 0.05;
                    if(width < 150 && fontSize < 12){
                        fontSize = 12;
                    }else if(width >= 150 && fontSize > 15){
                        fontSize = 15;
                    }
                    ctx.font = "bold "+fontSize+"px Arial";
                    ctx.fillStyle = "black";
                    ctx.textAlign = 'left';
                    const textWidth = ctx.measureText(title).width;
                    ctx.fillText(''+title+'', ((width-textWidth)/2)+left,fontSize);
                }
                ctx.restore();
            }
        };

        return {
            typeId: 4,
            options: lineOptions,
            data: lineData,
            custom: {customPlugin:customPlugin},
            graphName:graphListForm.graphName,
        };
    }

    /**
     * リスト データを取得
     * @param graphListForm
     * @param data 
     * @returns Ag-Grid表示用リストデータ
     */
    getAgGridData( graphListForm: any, data: any) : Object {
        const viewState = this.viewState;
        const graphListTemplateValFormList = graphListForm.graphListTemplateValFormList;
        const graphListTemplateSettingForm = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm.attributeName == "selectType");
        const flex = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm.attributeName == "flexColumnWidth")?.itemValue;
        let groupColumnName = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm.attributeName == "groupColumnName")?.itemValue;
        let groupTotalColumnName = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm.attributeName == "groupTotalColumnName")?.itemValue;
        //カラム定義
        let displayColumnNameList = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm.attributeName == "displayColumnNameList")?.itemValue;
        displayColumnNameList = JSON.parse(displayColumnNameList);
        let linkColumnNameList = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm.attributeName == "linkColumnNameList")?.itemValue;
        if(linkColumnNameList){
            linkColumnNameList = JSON.parse(linkColumnNameList);
        }
        let columnDefs: any[] = [];
        let columnDefsTempObj: any = {}; 
        Object.keys(displayColumnNameList).forEach(key => {
            const index = displayColumnNameList[key];
            if (index != 0 && Config.columnDisplayExclusionList.findIndex((columnDisplayExclusion:any)=>columnDisplayExclusion == key) < 0) {
                let column:any = {
                    headerName: key, field: key,
                    filter: true, sortable: true, cellClass: 'stringType',filterParams: {
                    buttons: ['reset', 'apply'],
                    },headerTooltip: key
                };
                if(flex){
                    column["flex"] = flex;
                }
                // 独自での行グルーピングありの場合はソート機能を無効
                if(groupColumnName && groupTotalColumnName){
                    column["cellClassRules"] = {
                        'background-white': "true"
                    };
                    column["sortable"] = false;
                }
                //リンクの場合cellRender追加
                if(linkColumnNameList && linkColumnNameList[key]){
                    column["cellRenderer"] = LinkCellRenderrer;
                    column["filter"] = false;
                    column["sortable"] = false;
                //通常の場合の各自動フォーマット処理
                //リンクは行う？形式不明の為一旦保留
                }else{
                    column["cellRenderer"] = (params:any) => {
                        try{
                            const date = new Date(params.value);
                            if(params.value.match(/\d{4}\-\d{2}\-\d{2}T\d{2}:\d{2}:\d{2}\.\d{3}/) && !isNaN(date.getTime())){
                                //date.setHours(date.getHours() - 9);
                                return date.toLocaleString();
                            //活動or投稿レイヤの添付ファイルの場合自動リンク生成
                            }else if(params.value.match(/\d{4}\-\d{2}\-\d{2}-\d{2}-\d{2}-\d{2}\-\d{1,}\-\d{1,}[\.jpg|\.jpeg|\.JPG|\.JPEG|\.png|\.PNG|\.tif|\.TIF|\.TIFF|\.pdf]/) 
                                        || params.value.match(/\d{4}\-\d{2}\-\d{2}-\d{2}-\d{2}-\d{2}\-\d{1,}[\.jpg|\.jpeg|\.JPG|\.JPEG|\.png|\.PNG|\.tif|\.TIF|\.TIFF|\.pdf]/)){
                                const data = params.data;
                                const obj:any = {};
                                if(data[Config.activityIdAliasName] != undefined && data[Config.activityIdAliasName] != null){
                                    obj["value"] = Config.config.attachmentFileUrlForActivity + "/" + data[Config.activityIdAliasName] + "/" + params.value;
                                }else if(data[Config.featureIdAliasName] != undefined && data[Config.featureIdAliasName] != null){
                                    obj["value"] = Config.config.attachmentFileUrlForPostLayer + "/feature_" + data[Config.featureIdAliasName] + "/" + params.value;
                                }else{
                                    return params.value;
                                }
                                return AutoLinkCellRenderrer(obj);
                            //その他通常のURL形式の場合自動リンク生成
                            }else if(isValidUrl(params.value)){
                                const obj:any = {};
                                obj["value"] = params.value;
                                return AutoLinkCellRenderrer(obj);
                            }else{
                                return params.value;
                            }
                        }catch(e){
                            return params.value;
                        }
                    }
                };
                columnDefs.push(column);
                columnDefsTempObj[index] = {column:column,index:index};
            }
        });
        const sorted:any = Object.keys(columnDefsTempObj).map(function(key:any){
            return columnDefsTempObj[key];
        }).sort(function(a:any,b:any){
            return (Number(a.index)<Number(b.index))?-1:1;
        })
        if(sorted){
            columnDefs = [];
            for(let i=0;i<sorted.length;i++){
                columnDefs.push(sorted[i].column);
            }
        }

        //1: 3D都市モデルビューワ->ダッシュボード（データ絞込）
        const linkedFeatureIdList = this.viewState?.linkedFeatureIdList;
        let attributeNameList:any = [];
        let attributeColumnNameList:any = [];
        if(linkedFeatureIdList && graphListForm.layerGraphCooporationFormList){
            for(let i=0;i<graphListForm.layerGraphCooporationFormList.length;i++){
                const cooperationTypeIndex = i;
                if(graphListForm.layerGraphCooporationFormList[cooperationTypeIndex].cooperationType == 1){
                    let cooperationOption = graphListForm.layerGraphCooporationFormList[cooperationTypeIndex].cooperationOption;
                    cooperationOption = JSON.parse(cooperationOption);
                    Object.keys(linkedFeatureIdList).forEach(featureAttributeName=>{
                            if(cooperationOption.featureAttributeName == featureAttributeName){
                                attributeNameList.push(cooperationOption.featureAttributeName);
                                attributeColumnNameList.push(cooperationOption.featureAttributeColumnName);
                            }
                    })
                }
            }
        }

        const initFillter = (grid:any) => {
            if(linkedListSearchData && linkedListSearchData.graphId == graphListForm.graphId){
                const filterInstance = grid.api.getFilterInstance(linkedListSearchData.searchColumnName);
                if(filterInstance && linkedListSearchData.searchValue){
                    let searchValue = linkedListSearchData.searchValue;
                    try{
                        searchValue = searchValue.trim();
                    }catch(e){}
                    filterInstance.setModel({
                        filterType: 'text',
                        type: 'contains',
                        filter: searchValue,
                    });
                    grid.api.onFilterChanged();
                }
            }
        };
    
        //データ定義
        let rowData: any[] = [];
        let tempGroupName: any = {};
        const linkedListSearchData: any = viewState.linkedListSearchData;
        for (let i = 0; i < data.length; i++) {
            data[i]["layerGraphCooporationFormList"] = graphListForm.layerGraphCooporationFormList;
            if(linkColumnNameList){
                data[i]["_linkColumnNameList"] = linkColumnNameList;
            }
            if(attributeNameList.length>0 && attributeColumnNameList.length>0 && linkedFeatureIdList){
                let result = false;
                let linkedFeatureIdLength = 0;
                for(let j=0; j<attributeNameList.length;j++){
                    const attributeName = attributeNameList[j];
                    const attributeColumnName = attributeColumnNameList[j];
                    if(data[i][attributeColumnName] && linkedFeatureIdList[attributeName]){
                        try{
                            data[i][attributeColumnName] = data[i][attributeColumnName].trim();
                        }catch(e){}
                        linkedFeatureIdLength = linkedFeatureIdLength + linkedFeatureIdList[attributeName].length;
                        const index = linkedFeatureIdList[attributeName].findIndex((featureId:any) => featureId==data[i][attributeColumnName]);
                        if(index > -1){
                            result = true;
                            break;
                        }
                    }
                }
                if(result || linkedFeatureIdLength < 1){
                    rowData.push(data[i]);
                }
            }else{
                rowData.push(data[i]);
            }
            if(groupColumnName && groupTotalColumnName){
                let indexList = [];
                if(tempGroupName[data[i][groupColumnName]]?.indexList){
                    indexList = tempGroupName[data[i][groupColumnName]]?.indexList;
                }
                indexList.push(rowData.length-1)
                tempGroupName[data[i][groupColumnName]] = {indexList:indexList,total:(tempGroupName[data[i][groupColumnName]]?.total!=undefined?tempGroupName[data[i][groupColumnName]].total:0) + (data[i][groupTotalColumnName]!=undefined?data[i][groupTotalColumnName]:0)};
                if(i == data.length-1 || data[i][groupColumnName] != data[i+1][groupColumnName]){
                    const obj:any = {};
                    obj["_groupFlag"] = true;
                    obj["_indexList"] = tempGroupName[data[i][groupColumnName]].indexList;
                    obj[groupColumnName] = data[i][groupColumnName];
                    obj["_extraDataColumnName"] = data[i][groupColumnName]+"の合計";
                    obj[obj["_extraDataColumnName"]] = tempGroupName[data[i][groupColumnName]].total;
                    rowData.push(obj);
                }
            }
        }

        //選択オプション デフォルトはsingle
        let selectType = "single";
        if(graphListTemplateSettingForm){
            selectType = graphListTemplateSettingForm.itemValue;
        }
        
        const onRowClicked = (e:any) => {
            const listData = e.data;
            // 0: ダッシュボード->3D都市モデルビューワ（フォーカス)
            let cooperationTypeIndex = listData?.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==0);
            if(cooperationTypeIndex > -1){
                listFocus(viewState,listData);
            }
            // 2: ダッシュボード->3D都市モデルビューワ（レイヤ切替）
            cooperationTypeIndex = listData?.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==2);
            if(cooperationTypeIndex > -1){
                layerChangeForGraphList(viewState,listData.layerGraphCooporationFormList,listData,graphListForm.graphId);
            }
        }

        // 独自での行グルーピングありの場合は行結合
        let suppressRowTransform = false;
        if(groupColumnName && groupTotalColumnName){
            const rowSpan = (params:any) => {
                let groupColumn = params.data ? params.data[groupColumnName] : "";
                if (params.data["_groupFlag"] == undefined && tempGroupName[groupColumn] && tempGroupName[groupColumn].indexList &&
                tempGroupName[groupColumn].indexList[0] == params.node.id) {
                    return tempGroupName[groupColumn].indexList.length;
                } else {
                    return 1;
                }
            };
            const index = columnDefs.findIndex((column:any)=>column.field==groupColumnName);
            if(index>-1){
                columnDefs[index]["rowSpan"] = rowSpan;
                columnDefs[index]["cellClassRules"] = {
                    'background-white': "true"
                };
                suppressRowTransform = true;
            }
        }

        // 独自での行グルーピングありの場合はsubTotal行を表示
        let isFullWidthRow = null; 
        if(groupColumnName && groupTotalColumnName){
            const isFullWidth = (data:any) => {
                return data["_groupFlag"];
            } 
            isFullWidthRow = (params:any) => {
                return isFullWidth(params.rowNode.data);
            }    
        }

        // 独自での行グルーピングありの場合は外部filterを有効
        let doesExternalFilterPass = null;
        let isExternalFilterPresent = null;
        if(groupColumnName && groupTotalColumnName){
            isExternalFilterPresent = () => {
                return true;
            }
            doesExternalFilterPass = (node:any) => {
                if (node.data) {
                    if(node.data["_hide"]){
                        return false;
                    }         
                }
                return true;
            }
        }

        return {
            typeId: 5,
            rowData: rowData,
            columnDefs: columnDefs,
            custom: {
                selectType: selectType
            },
            graphName:graphListForm.graphName,
            onRowClicked: onRowClicked,
            isFullWidthRow:isFullWidthRow,
            doesExternalFilterPass:doesExternalFilterPass,
            isExternalFilterPresent:isExternalFilterPresent,
            suppressRowTransform:suppressRowTransform,
            initFillter:initFillter
        };
    }

    /**
     * リスト データを取得
     * @param graphListForm
     * @param data 
     * @returns 表示用シングルリストデータ
     */
    getSingleListData( graphListForm: any,data: any) : Object {
        const viewState = this.viewState;
        const graphListTemplateValFormList = graphListForm.graphListTemplateValFormList;
        const displayColumnName = graphListTemplateValFormList.find((graphListTemplateValForm: any) => graphListTemplateValForm.graphListTemplateSettingForm.attributeName == "displayColumnName")?.itemValue;
        let displayValue = "";
        if(!data){
            data = [];
        }

        Object.keys(data).forEach(index => {
            if(data[index][displayColumnName]){
                displayValue = displayValue + data[index][displayColumnName];
                if(Number(index) < data.length-1){
                    displayValue = displayValue + "\n";
                }
            }
        });

        const onRowClicked = () => {
            // 0: ダッシュボード->3D都市モデルビューワ（フォーカス)
            let cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==0);
            if(cooperationTypeIndex > -1){
                listFocus(viewState,data);
            }
            // 2: ダッシュボード->3D都市モデルビューワ（レイヤ切替）
            cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==2);
            if(cooperationTypeIndex > -1){
                layerChangeForGraphList(viewState,graphListForm.layerGraphCooporationFormList,null,graphListForm.graphId);
            }
            // 3: シングルリスト->リスト（データ絞込）
            cooperationTypeIndex = graphListForm.layerGraphCooporationFormList?.findIndex((layerGraphCooporationForm:any) => layerGraphCooporationForm.cooperationType==3);
            if(cooperationTypeIndex > -1){
                let cooperationOption:any = null;
                const layerGraphCooporationForm = graphListForm.layerGraphCooporationFormList[cooperationTypeIndex];
                cooperationOption = layerGraphCooporationForm.cooperationOption;
                cooperationOption = JSON.parse(cooperationOption);
                runInAction(() => {
                    viewState.setLinkedListSearchData({graphId:graphListForm.layerGraphCooporationFormList[cooperationTypeIndex].layerId,searchColumnName:cooperationOption.searchColumnName,searchValue:data[0][cooperationOption.columnName]});
                    highlightGraphAndFilterList(viewState);
                })
            }
        }

        return {
            typeId: 6,
            displayValue: displayValue,
            displayColumnName:displayColumnName,
            graphName:graphListForm.graphName,
            layerGraphCooporationFormList:graphListForm.layerGraphCooporationFormList,
            onRowClicked:onRowClicked
        };
    }

}

/**
 * URL判別用
 * @param 文字列 
 * @return 判定結果
 */
function isValidUrl(string:any) {
    try {
    const pattern = new RegExp( '^([a-zA-Z]+:\\/\\/)?' + 
    '((([a-z\\d]([a-z\\d-]*[a-z\\d])*)\\.)+[a-z]{2,}|' + 
    '((\\d{1,3}\\.){3}\\d{1,3}))' + 
    '(\\:\\d+)?(\\/[-a-z\\d%_.~+]*)*' + 
    '(\\?[;&a-z\\d%_.~+=-]*)?' + 
    '(\\#[-a-z\\d_]*)?', 
    'i' ); 
    return pattern.test(string); 
    } catch (err) { 
      return false; 
    }
  }

/**
 * 0: chart.js ダッシュボード->3D都市モデルビューワ（フォーカス)
 * @param viewState 
 * @param data グラフデータ
 * @param layerGraphCooporationFormList レイヤ・グラフ連携DTOリスト
 */
export const graphFocus = (viewState:any,data:any,layerGraphCooporationFormList:any) => {
    try{
        let cooperationOption:any = null;
        let attributeNameList:any = [];
        let attributeValueList:any = [];
        let height = 0;
        const linkedFeatureIdList = viewState?.linkedFeatureIdList;
        if(linkedFeatureIdList){
            for(let i=0;i<layerGraphCooporationFormList.length;i++){
                const cooperationTypeIndex = i;
                if(layerGraphCooporationFormList[cooperationTypeIndex].cooperationType == 0){
                    cooperationOption = layerGraphCooporationFormList[cooperationTypeIndex].cooperationOption;
                    cooperationOption = JSON.parse(cooperationOption);
                    Object.keys(linkedFeatureIdList).forEach(featureAttributeName=>{
                            if(cooperationOption.featureAttributeName == featureAttributeName){
                                const attributeName = cooperationOption.featureAttributeName;
                                const attributeColumnName = cooperationOption.featureAttributeColumnName;
                                if(attributeName && attributeColumnName && data){
                                    attributeNameList.push(attributeName + "");
                                    attributeValueList.push(data[attributeColumnName] + "");
                                    if(cooperationOption["heightColumnName"] && parseInt(data[cooperationOption["heightColumnName"]]) > height){
                                        height = parseInt(data[cooperationOption["heightColumnName"]]);
                                    }
                                }
                            }
                    })
                }
            }
        }
        if(viewState && cooperationOption && 
                cooperationOption["longitudeColumnName"] && cooperationOption["latitudeColumnName"]){
                if(attributeNameList.length < 1){
                    attributeNameList = null;
                }
                if(attributeValueList.length < 1){
                    attributeValueList = null;
                }
                runInAction(() => {
                    //活動情報(イベント・エリマネ)の場合は対象の活動IDをセット
                    try{
                        if(data["activity_id"] != undefined && data["activity_id"] != null){
                            viewState.setCurrentActivityId(data["activity_id"]);
                        }
                    }catch(e){}
                    if(height > 0){
                        viewState.terria.focusMapPlaceAndAttributeDisplay(data[cooperationOption["longitudeColumnName"]],data[cooperationOption["latitudeColumnName"]],attributeValueList,attributeNameList,viewState,height+50)
                    }else{
                        viewState.terria.focusMapPlaceAndAttributeDisplay(data[cooperationOption["longitudeColumnName"]],data[cooperationOption["latitudeColumnName"]],attributeValueList,attributeNameList,viewState)
                    }
                })
        }
    }catch(e){
        console.log(e);
    }
}

/**
 * 0: リスト ダッシュボード->3D都市モデルビューワ（フォーカス)
 * @param viewState 
 * @param data 行データ
 */
export const listFocus = (viewState:any,data:any) => {
    try{
        const linkedFeatureIdList = viewState?.linkedFeatureIdList;
        let cooperationOption:any = null;
        let attributeNameList:any = [];
        let attributeValueList:any = [];
        let height = 0;
        if(linkedFeatureIdList){
            for(let i=0;i<data.layerGraphCooporationFormList.length;i++){
                const cooperationTypeIndex = i;
                if(data.layerGraphCooporationFormList[cooperationTypeIndex].cooperationType == 0){
                    cooperationOption = data.layerGraphCooporationFormList[cooperationTypeIndex].cooperationOption;
                    cooperationOption = JSON.parse(cooperationOption);
                    Object.keys(linkedFeatureIdList).forEach(featureAttributeName=>{
                            if(cooperationOption.featureAttributeName == featureAttributeName){
                                const attributeName = cooperationOption.featureAttributeName;
                                const attributeColumnName = cooperationOption.featureAttributeColumnName;
                                if(attributeName && attributeColumnName && data){
                                    attributeNameList.push(attributeName + "");
                                    attributeValueList.push(data[attributeColumnName] + "");
                                    if(cooperationOption["heightColumnName"] && parseInt(data[cooperationOption["heightColumnName"]]) > height){
                                        height = parseInt(data[cooperationOption["heightColumnName"]]);
                                    }
                                }
                            }
                    })
                }
            }
            if(data && viewState && cooperationOption && cooperationOption["longitudeColumnName"] && cooperationOption["latitudeColumnName"]){
                if(attributeNameList.length < 1){
                    attributeNameList = null;
                }
                if(attributeValueList.length < 1){
                    attributeValueList = null;
                }
                runInAction(() => {
                  try{
                    //活動情報(イベント・エリマネ)の場合は対象の活動IDをセット
                    if(data["activity_id"] != undefined && data["activity_id"] != null){
                        viewState.setCurrentActivityId(data["activity_id"]);
                    }
                  }catch(e){}
                  if(height > 0){
                    viewState.terria.focusMapPlaceAndAttributeDisplay(data[cooperationOption["longitudeColumnName"]],data[cooperationOption["latitudeColumnName"]],attributeValueList,attributeNameList,viewState,height+100)
                  }else{
                    viewState.terria.focusMapPlaceAndAttributeDisplay(data[cooperationOption["longitudeColumnName"]],data[cooperationOption["latitudeColumnName"]],attributeValueList,attributeNameList,viewState)
                  }
                })
            }
        }
      }catch(e){
        console.log(e);
      }
}

/**
 * 2: ダッシュボード->3D都市モデルビューワ（レイヤ切替※グラフリスト）
 * @param viewState
 * @param graphListForm グラフリストフォーム
 * @param data グラフデータ
 * @param graphId グラフID
 */
export const layerChangeForGraphList = (viewState:any,layerGraphCooporationFormList:any,data:any,graphId:any) => {
    try{
        const layerIdGraphIdList:any = [];
        const cooperationOptionList:any = [];
        if(layerGraphCooporationFormList){
        //対象の連携IDを取得し連携対象IDリストにセット
        for(let i =0;i<layerGraphCooporationFormList.length;i++){
            if(layerGraphCooporationFormList[i] && layerGraphCooporationFormList[i].cooperationType==2){
                if(layerGraphCooporationFormList[i].cooperationId){
                    layerIdGraphIdList.push({layerId:layerGraphCooporationFormList[i].layerId,graphId:layerGraphCooporationFormList[i].graphId})
                    let cooperationOption = layerGraphCooporationFormList[i].cooperationOption;
                    try{
                        cooperationOption = JSON.parse(cooperationOption);
                    }catch(e){
                        cooperationOption = null;
                    }
                    cooperationOptionList.push(cooperationOption);
                }
            }
        }
        //連携対象のIDリストがある場合のみ連携対象のレイヤのみで再表示
        if(layerIdGraphIdList && Object.keys(layerIdGraphIdList).length > 0){
            runInAction(() => {
            const themeLayerList = [...viewState.terria.themeLayerList];
            for (let i=0;i<themeLayerList.length;i++) {
                const themeLayerIndex = i;
                const layerIdIndex = layerIdGraphIdList.findIndex((list:any)=>list.layerId == themeLayerList[themeLayerIndex]["layerId"] && graphId == list.graphId);
                let layerId = null;
                let cooperationOption = null;
                if(layerIdIndex > -1){
                    cooperationOption = cooperationOptionList[layerIdIndex];
                    layerId = layerIdGraphIdList[layerIdIndex].layerId;
                }
                let item:any = viewState.terria.getModelById(BaseModel, themeLayerList[themeLayerIndex]["uniqueId"]);
                if((layerId != undefined && layerId != null && layerId > -1) 
                    || (!themeLayerList[themeLayerIndex]["layerGraphCooporationFormList"] 
                        || themeLayerList[themeLayerIndex]["layerGraphCooporationFormList"].findIndex((layerGraphCooporationForm:any)=>layerGraphCooporationForm.cooperationType == 2 
                            && viewState.terria.themeGraphIDList.findIndex((graphId:any)=>graphId==layerGraphCooporationForm.graphId) > -1 ) < 0) ){
                    if(layerId != undefined && layerId != null && layerId > -1){
                        try{
                            item.setTrait(
                                CommonStrata.user,
                                "show",
                                true);
                            try{
                                if(data && cooperationOption && cooperationOption["queryParameterName"] && cooperationOption["queryStringColumnName"]){
                                    item.setTrait(CommonStrata.user,
                                    "parameters",
                                    {
                                        "viewparams": cooperationOption["queryParameterName"] + ":" + data[cooperationOption["queryStringColumnName"]],
                                    });
                                }else if(cooperationOption && cooperationOption["queryParameterName"] && cooperationOption["queryString"] != undefined && cooperationOption["queryString"] != null && cooperationOption["queryString"] != ""){
                                    item.setTrait(CommonStrata.user,
                                    "parameters",
                                    {
                                        "viewparams": cooperationOption["queryParameterName"] + ":" + cooperationOption["queryString"],
                                    });
                                }
                            }catch(e){}
                            viewState.terria.workbench.remove(item);
                            item.forceLoadMapItems().then((res:any) =>{
                                item.loadMapItems();
                                viewState.terria.workbench.add(item);
                            })
                        }catch(e){
                            console.log(e)
                        }
                    }
                }else{
                    try{
                        viewState.terria.workbench.remove(item);
                    }catch(e){}
                }
            }
            })
        }
        }
    }catch(e){
        console.log(e);
    }
}

/**
 * 4: chart.js ダッシュボード->3D建物モデル（style切替）
 * @param viewState
 * @param data グラフデータ
 * @param layerGraphCooporationFormList レイヤ・グラフ連携DTOリスト
 * @param backgroundColor 建物モデルの切り替え色
 */
export const buildingStyleChange = (viewState:any,data:any,layerGraphCooporationFormList:any,backgroundColor:any) => {
    try{
        if(layerGraphCooporationFormList){
            for(let i=0;i<layerGraphCooporationFormList.length;i++){
                if(layerGraphCooporationFormList[i]?.cooperationType == 4 ){
                    let cooperationOption:any = null;
                    const index = Number(i);
                    if(index > -1){
                        cooperationOption = layerGraphCooporationFormList[index].cooperationOption;
                        cooperationOption = JSON.parse(cooperationOption);
                    }
                    if(cooperationOption && (cooperationOption.featureAttributeColumnName || cooperationOption.featureAttributeName || cooperationOption.featureAttributeExpression) && cooperationOption.columnName){
                        const items = viewState.terria.workbench.items;
                        let item = null;
                        for (const aItem of items) {
                            const themeLayerList = [...viewState.terria.themeLayerList];
                            if(themeLayerList){
                                let themeLayerIndex = -1;
                                for(let i=0;i<themeLayerList.length;i++){
                                    if(themeLayerList[i]["uniqueId"] == aItem.uniqueId && themeLayerList[i]["layerId"] == layerGraphCooporationFormList[index]["layerId"]){
                                        themeLayerIndex = i;
                                        break;
                                    }
                                }
                                if(themeLayerIndex > -1){
                                    item = aItem;
                                    if(item){
                                        const customStyle:any={};
                                        customStyle["color"] = {};
                                        customStyle["color"]["conditions"] = [];
                                        const style = item.getTrait(CommonStrata.user, "style")
                                        if(data && (data[cooperationOption.featureAttributeColumnName] || cooperationOption.featureAttributeName || cooperationOption.featureAttributeExpression) && data[cooperationOption.columnName] != null){
                                            let condition:any = [];
                                            let featureAttributeName = data[cooperationOption.featureAttributeColumnName];
                                            if(!featureAttributeName){
                                                featureAttributeName = cooperationOption.featureAttributeName;
                                            }
                                            if(!featureAttributeName){
                                                featureAttributeName = cooperationOption.featureAttributeExpression;
                                            }
                                            let featureAttributeValue = data[cooperationOption.columnName];
                                            if(!/^[+,-]?([1-9]\d*|0)(\.\d+)?$/.test(featureAttributeValue)){
                                                featureAttributeValue = "'" + featureAttributeValue + "'";
                                                if(!cooperationOption.featureAttributeExpression){
                                                    condition.push("${" + featureAttributeName + "} === " + featureAttributeValue);
                                                }else{
                                                    condition.push(featureAttributeName + " === " + featureAttributeValue);
                                                }
                                            }else{
                                                if(!cooperationOption.featureAttributeExpression){
                                                    condition.push("Number(${" + featureAttributeName + "}) === Number(" + featureAttributeValue + ")");
                                                }else{
                                                    condition.push("Number(" + featureAttributeName + ") === Number(" + featureAttributeValue + ")");
                                                }
                                            }
                                            condition.push("color('"+backgroundColor+"', 1.0)");
                                            customStyle["color"]["conditions"].push(condition);
                                            //比較対象の属性値とfeatureUndefinedValueが等しい場合は、未定義も条件対象とする
                                            try{
                                                featureAttributeValue = featureAttributeValue.replace(/'/g,'');
                                            }catch(e){}
                                            if(featureAttributeValue != null && cooperationOption.featureUndefinedValue != undefined && featureAttributeValue == cooperationOption.featureUndefinedValue){
                                                if(!cooperationOption.featureAttributeExpression){
                                                    condition = [];
                                                    condition.push("${" + featureAttributeName + "} === '' || " + "${" + featureAttributeName + "} === undefined || "+"${" + featureAttributeName + "} === null");
                                                    condition.push("color('"+backgroundColor+"', 1.0)");
                                                    customStyle["color"]["conditions"].push(condition);
                                                }else{
                                                    condition = [];
                                                    condition.push(featureAttributeName + " === '' || "+featureAttributeName + " === undefined || "+featureAttributeName + " === null");
                                                    condition.push("color('"+backgroundColor+"', 1.0)");
                                                    customStyle["color"]["conditions"].push(condition);
                                                }
                                            }
                                            condition = [];
                                            condition.push("true");
                                            condition.push("color('#FFFFFF', 1.0)");
                                            customStyle["color"]["conditions"].push(condition);
                                            item.setTrait(CommonStrata.user, "style", customStyle);
                                            item.setTrait(CommonStrata.user, "colorBlendMode", "HIGHLIGHT");
                                            item.loadMapItems();
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }catch(e){
        console.log(e);
    }
}

/**
 * グラフのhighlight及びリストのfilter処理(3Dviewe→ダッシュボード連携)
 * @param viewState 
 */
export const highlightGraphAndFilterList = (viewState:any) => {
    let graphList:GraphList = new GraphList(viewState);
    const resultData:any = {};
    Object.keys(viewState.graphList).forEach(key => {
      let graphListData = null;
      switch (viewState.graphList[key]?.graphListForm?.graphTypeId) {
          case 1:
              graphListData = graphList.getComplexGraphData(viewState.graphList[key].graphListForm, viewState.graphList[key].data);
              break;
          case 2:
              graphListData = graphList.getDoughnutGraphData(viewState.graphList[key].graphListForm, viewState.graphList[key].data);
              break;
          case 3:
              graphListData = graphList.getBarGraphData(viewState.graphList[key].graphListForm, viewState.graphList[key].data);
              break;
          case 4:
              graphListData = graphList.getLineGraphData(viewState.graphList[key].graphListForm, viewState.graphList[key].data);
              break;
          case 5:
              graphListData = graphList.getAgGridData(viewState.graphList[key].graphListForm, viewState.graphList[key].data);
              break;
          default:
              break;
      }
      resultData[viewState.graphList[key].graphListForm.graphId + ""] = {...viewState.graphList[key]};
      if(graphListData && resultData[viewState.graphList[key].graphListForm.graphId + ""]){
          resultData[viewState.graphList[key].graphListForm.graphId + ""].graphListData = graphListData;
      }
    })
    runInAction(() => {
      viewState.setGraphList(resultData);
    })
}

/**
 * 対象テーマのグラフ・リスト一覧をviewStateにセット
 * @param viewState 
 * @param withThemeSwitchItem
 * @returns Promise
 */
export const setThemeData = (viewState: any,withThemeSwitchItem: any = false) => {
    if(!viewState){
        return;
    }else{
        //現テーマレイヤをリセット(レンダリングが他処理のボトルネックとならないように)
        if(!withThemeSwitchItem && !viewState.initShowFlag){
            viewState.terria.workbench.removeAll();
        }
    }
    return new Promise(function(resolve, reject) {
        const themeId = viewState.selectedThemeId;
        const resEvent = function(res:any){
            const graphs = res;
            const graphList:any = {};
            const themeLayoutList:any = [];
            const graphListClass:GraphList = new GraphList(viewState);
            if (graphs != null) {
                for (let i = 0; i < graphs.length; i++) {
                    if (graphs[i].graphId > 0) {
                        //連携情報をセット
                        const layerGraphCooporationFormList = graphs[i].graphListForm?.layerGraphCooporationFormList;
                        if(layerGraphCooporationFormList){
                            Object.keys(layerGraphCooporationFormList).forEach(key => {
                                if (layerGraphCooporationFormList[key].cooperationType == 0 || layerGraphCooporationFormList[key].cooperationType == 1) {
                                    let cooperationOption = layerGraphCooporationFormList[key].cooperationOption;
                                    cooperationOption = JSON.parse(cooperationOption);
                                    runInAction(() => {
                                        viewState.setLinkedFeatureAttribute(cooperationOption.featureAttributeName);
                                    })
                                }
                            })
                        }
                    }
                    //グラフ・リストのテーマレイアウトを追加
                    const themeLayout = { i: graphs[i].graphId + "", x: graphs[i].topLeftX, y: graphs[i].topLeftY, w: graphs[i].panelWidth, h: graphs[i].panelHeight };
                    themeLayoutList.push(themeLayout);
                    //内部で使用するグラフ・リストデータを追加
                    let graphListData = null;
                    if(graphs[i] && graphs[i].graphListForm && graphs[i].graphListForm.graphTypeId && graphs[i].graphListForm.dataList){
                        switch (graphs[i].graphListForm.graphTypeId) {
                            case 1:
                                graphListData = graphListClass.getComplexGraphData(graphs[i].graphListForm, graphs[i].graphListForm.dataList);
                                break;
                            case 2:
                                graphListData = graphListClass.getDoughnutGraphData(graphs[i].graphListForm, graphs[i].graphListForm.dataList);
                                break;
                            case 3:
                                graphListData = graphListClass.getBarGraphData(graphs[i].graphListForm, graphs[i].graphListForm.dataList);
                                break;
                            case 4:
                                graphListData = graphListClass.getLineGraphData(graphs[i].graphListForm, graphs[i].graphListForm.dataList);
                                break;
                            case 5:
                                graphListData = graphListClass.getAgGridData(graphs[i].graphListForm, graphs[i].graphListForm.dataList);
                                break;
                            case 6:
                                graphListData = graphListClass.getSingleListData(graphs[i].graphListForm, graphs[i].graphListForm.dataList);
                                break;
                            default:
                                break;
                        }
                        if(graphListData && graphs[i].graphId){
                            //graphListData:表示用グラフリストデータ,graphListForm:グラフリストDTO,data：グラフ・リストデータ
                            graphList[graphs[i].graphId + ""] = {graphListData:graphListData,graphListForm:graphs[i].graphListForm,data:graphs[i].graphListForm.dataList};
                        }
                    }
                }
                runInAction(() => {
                    //グラフ・リストのテーマレイアウトをセット
                    viewState.setDisplayLayout(themeLayoutList);
                    //内部で使用するグラフ・リストデータをセット
                    viewState.setGraphList(graphList);
                    //内部で使用するテーマグラフIDリストをセット
                    let graphIDList = [];
                    if(graphs){
                        graphIDList = graphs.map((graph:any) => (graph.graphId));
                    }
                    viewState.terria.setThemeGraphIDList(graphIDList);
                    //テーマレイヤを読み込み
                    if(!viewState.initShowFlag){
                        viewState.terria.customLoadInitSource(withThemeSwitchItem);
                    }else{
                        viewState.setInitShowFlag(false);
                    }
                    resolve({message:"success"});
                })
            }else{
                reject({message:"No Data"});
            }
        }
        if(!withThemeSwitchItem){
            fetch(Config.config.apiUrl + "/graphs/" + themeId)
                .then(res => res.json())
                .then(res => {
                resEvent(res);
            }).catch(error => {
                reject({message:"error"});
                console.error('処理に失敗しました', error);
            });
        }else{
            fetch(Config.config.apiUrl + "/graphs/" + themeId, {
                method: 'POST',
                body: JSON.stringify(withThemeSwitchItem),
                headers: new Headers({ 'Content-type': 'application/json' }),
            })
            .then(res => res.json())
            .then(res => {
            resEvent(res);
            }).catch(error => {
                reject({message:"error"});
                console.error('処理に失敗しました', error);
            });
        }
    });
};

/**
 * 対象テーマのグラフ・リスト一覧をviewStateにセット(再取得)
 * @param viewState 
 * @param withThemeSwitchItem
 * @returns Promise
 */
export const refreshThemeData = (viewState: any) => {
    if(!viewState){
        return;
    }
    return new Promise(function(resolve, reject) {
        const themeId = viewState.selectedThemeId;
        const resEvent = function(res:any){
            const graphs = res;
            const graphList:any = {};
            const graphListClass:GraphList = new GraphList(viewState);
            if (graphs != null) {
                for (let i = 0; i < graphs.length; i++) {
                    //内部で使用するグラフ・リストデータを追加
                    let graphListData = null;
                    if(graphs[i] && graphs[i].graphListForm && graphs[i].graphListForm.graphTypeId && graphs[i].graphListForm.dataList){
                        switch (graphs[i].graphListForm.graphTypeId) {
                            case 1:
                                graphListData = graphListClass.getComplexGraphData(graphs[i].graphListForm, graphs[i].graphListForm.dataList);
                                break;
                            case 2:
                                graphListData = graphListClass.getDoughnutGraphData(graphs[i].graphListForm, graphs[i].graphListForm.dataList);
                                break;
                            case 3:
                                graphListData = graphListClass.getBarGraphData(graphs[i].graphListForm, graphs[i].graphListForm.dataList);
                                break;
                            case 4:
                                graphListData = graphListClass.getLineGraphData(graphs[i].graphListForm, graphs[i].graphListForm.dataList);
                                break;
                            case 5:
                                graphListData = graphListClass.getAgGridData(graphs[i].graphListForm, graphs[i].graphListForm.dataList);
                                break;
                            case 6:
                                graphListData = graphListClass.getSingleListData(graphs[i].graphListForm, graphs[i].graphListForm.dataList);
                                break;
                            default:
                                break;
                        }
                        if(graphListData && graphs[i].graphId){
                            //graphListData:表示用グラフリストデータ,graphListForm:グラフリストDTO,data：グラフ・リストデータ
                            graphList[graphs[i].graphId + ""] = {graphListData:graphListData,graphListForm:graphs[i].graphListForm,data:graphs[i].graphListForm.dataList};
                        }
                    }
                }
                runInAction(() => {
                    //内部で使用するグラフ・リストデータをセット
                    viewState.setGraphList(graphList);
                    //内部で使用するテーマグラフIDリストをセット
                    let graphIDList = [];
                    if(graphs){
                        graphIDList = graphs.map((graph:any) => (graph.graphId));
                    }
                    viewState.terria.setThemeGraphIDList(graphIDList);
                    //切り替え項目アリの場合はデフォに
                    viewState.setSelectedWithThemeSwitchItemValue("");
                    resolve({message:"success"});
                })
            }else{
                reject({message:"No Data"});
            }
        }
        fetch(Config.config.apiUrl + "/graphs/" + themeId)
            .then(res => res.json())
            .then(res => {
            resEvent(res);
        }).catch(error => {
            reject({message:"error"});
            console.error('処理に失敗しました', error);
        });
    });
};
