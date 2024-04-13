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
import HTMLComponent from "./HTMLComponent.jsx"
/**
 * 地域統計・回遊性情報管理画面(既存システムからのhtml流用)
 * styleは仮で当て込み
 */
@observer
class RegionalStatisticsManagementScreen extends React.Component {
    static displayName = "RegionalStatisticsManagementScreen";

    static propTypes = {
        terria: PropTypes.object.isRequired,
        viewState: PropTypes.object.isRequired,
        theme: PropTypes.object,
        t: PropTypes.func.isRequired
    };

    constructor(props) {
        super(props);
        //htmlTemplateのapiUrlを置き換え
        htmlTemplate = htmlTemplate.replace("{apiUrl}",Config.config.apiUrl);
        //htmlTemplateのstatisticsItemsInformationを置き換え
        htmlTemplate = htmlTemplate.replace("{statisticsItemsInformation}",JSON.stringify(Config.statisticsItemsInformation));
    }

    render() {
        return (
            <div style={{height:"80vh",width:"100%"}}>
                <HTMLComponent htmlString = {htmlTemplate}>
                </HTMLComponent>
            </div>
        );
    }
}

export default withTranslation()(withTheme(RegionalStatisticsManagementScreen));

let htmlTemplate = `
<!DOCTYPE html>
<html>
  <head>
    <meta charset="UTF-8"/>
    <title> - 地域統計/回遊性情報の更新</title>
    <link
      rel="stylesheet"
      href="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/css/bootstrap.min.css"
      integrity="sha384-MCw98/SFnGE8fJT3GXwEOngsV7Zt27NXFoaoApmYm81iuXoPkFOJwJ8ERdknLPMO"
      crossorigin="anonymous"
      />
    <link
      rel="stylesheet"
      href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.1.1/css/all.min.css"
      />

    <style>
      .regional-statistics-management-container .h2 {
        background-color: #2AAE7A;
        width: 101%;
        line-height: 3em;
        text-align: center;
        color: #fff;
        font-weight: bold;
        font-size: 1.5em;
      }
      .regional-statistics-management-container a {
        color: #000;
      }
      .regional-statistics-management-container a:hover {
        text-decoration: none !important;
        color: #000;
      }
      .regional-statistics-management-container .bg {
        background-color: #2AAE7A;
      }
      .regional-statistics-management-container .card {
        margin: 0 auto;
        box-shadow: 6px 6px 10px 0px rgba(0, 0, 0, 0.4);
        cursor: pointer;
        height: 100%;
      }
      .regional-statistics-management-container .card-body {
        max-height: 180px;
      }
      .regional-statistics-management-container .card-body a {
        color: #4169e1;
        text-decoration: none;
        border-bottom: 3px solid #4169e1;
      }
      .regional-statistics-management-container .hover_shadow_none:hover {
        box-shadow: none !important;
      }
      .regional-statistics-management-container #logoutButton {
        background-color: #2AAE7A;
        color: rgb(255, 255, 255);
        position: fixed;
        right: 10px;
        top: 10px;
        border-color: #fff;
      }
      .regional-statistics-management-container #menuButton {
        background-color: #2AAE7A;
        color: rgb(255, 255, 255);
        position: fixed;
        left: 10px;
        top: 10px;
        border-color: #fff;
      }
      .regional-statistics-management-container .modal-header {
        padding: 0;
    background-color: transparent;
    color: #122757;
    border: 0;
    margin-bottom: 4.271vw;
      }
      .regional-statistics-management-container .modal-header h5{
        font-size: 1.042vw;
      }
      .regional-statistics-management-container .modal-header button{
        padding:0;
        margin:0;
      }
      .regional-statistics-management-container .modal-header button span{
        font-weight: normal;
        color: #999;
      }
      .regional-statistics-management-container .update-failed {
        background-color: rgb(212, 26, 26) !important;
        color: #fff;
      }
      .regional-statistics-management-container .csv-btn {
        background-color: #2AAE7A;
        color: rgb(255, 255, 255);
        border-color: #fff;
      }
      .regional-statistics-management-container .row-eq-height {
        display: flex;
        flex-wrap: wrap;
      }
      .regional-statistics-management-container .over_fllow_y_scroll {
        overflow-y: scroll;
      }
      .regional-statistics-management-container ::-webkit-scrollbar {
        width: 10px;
      }
      .regional-statistics-management-container ::-webkit-scrollbar-track {
        background-color: #ccc;
      }
      .regional-statistics-management-container ::-webkit-scrollbar-thumb {
        background-color: #2AAE7A;
      }
      .regional-statistics-management-container #statisticsListTable thead th {
        border-bottom-width: 0px;
        position: sticky;
        position: -webkit-sticky;
        top: 0;
        background-color: #F2F2F2;
        color: #999;
        font-weight: normal;
        font-size:0.729vw;
      }
      .regional-statistics-management-container #statisticsListTable tr{
        background:#fff;
      }
      .regional-statistics-management-container #statisticsListTable tr:nth-child(2n){
        background:#FAFAFA;
      }
      .regional-statistics-management-container #statisticsListTable tr td {
       
      }
      .regional-statistics-management-container #statisticsListTable{
    
      }
      .regional-statistics-management-container #statisticsListTable tr{
   
      }
      .regional-statistics-management-container #statisticsListTable tr td{
        flex:1;
        font-size:0.729vw;
        color:#333;
      }
      .regional-statistics-management-container #statisticsListTable tr th{
        flex:1;
        text-align: right;
      }
      .regional-statistics-management-container #statisticsListTable tr th:nth-child(1){
        text-align: left;
      }
      .regional-statistics-management-container #statisticsListTable tr th:nth-child(2){
        text-align: left;
      }
      .regional-statistics-management-container #statisticsListTable tr th:nth-child(3){
        text-align: left;
      }
      .regional-statistics-management-container #statisticsListTable thead {
        width:100%;
      }
      .regional-statistics-management-container .statisticsTableBox {
        max-width: 90%;
        max-height: 60vh;
      }
      .regional-statistics-management-container .csvDataTableBox {
        max-width: 90%;
        max-height: 480px;
      }
      .regional-statistics-management-container #csvDataTable thead th {
        border: #fff solid 2px;
        border-bottom-width: 4px;
        position: sticky;
        position: -webkit-sticky;
        top: 0;
        background-color: #2AAE7A;
        color: rgb(255, 255, 255);
      }
      .regional-statistics-management-container #csvDataTable tr td {
        border: #fff solid 2px;
      }
      .regional-statistics-management-container #csvDataTable thead {
        text-align: center;
      }
      .regional-statistics-management-container .spinner-border {
        display: inline-block;
        width: 4rem;
        height: 4rem;
        vertical-align: text-bottom;
        border: 0.5em solid currentColor;
        border-right-color: transparent;
        border-radius: 50%;
        -webkit-animation: spinner-border 0.75s linear infinite;
        animation: spinner-border 0.75s linear infinite;
        color: #2AAE7A;
      }
      @keyframes spinner-border {
        to {
          transform: rotate(360deg);
        }
      }
      .regional-statistics-management-container .overlay {
        position: absolute;
        top: 50%;
        left: 50%;
        transform: translate(-50%, -50%);
        z-index: 1051;
      }
      .regional-statistics-management-container .overlay-back {
        position: fixed;
        top: 0;
        right: 0;
        bottom: 0;
        left: 0;
        z-index: 1050;
        background-color: rgba(0, 0, 0, 0.4);
      }
      @media (min-width: 576px){
      .modal.show .modal-dialog{
        max-width:57.292vw;
      }
    }
    .modal-content{
      border-radius: 14px;
      box-sizing: border-box;
      padding: 5.010vw;
      padding-bottom: 2.760vw;
    }
    .modal-body{
      padding:1rem 0;
      display:flex;
    }
    .modal-body input{
      flex: 1;
      box-sizing: border-box;
      outline: none !important;
      border: 1px solid #ddd;
      border-radius: 4px;
      margin-right: 1.563vw;
      height: 3.021vw;
    }
    .modal-body button{
      width: 5.729vw;
    height: 3.021vw;
    background-color: #FA3232 !important;
    border-radius: 6px;
    }
    .modal-footer{
      padding:1rem 0;
    }
    .modal-footer button{
      width: auto;
      height: 3.021vw;
      border-color: #2AAE7A !important;
      border-radius: 6px;
      background-color: transparent !important;
      color: #2AAE7A !important;
    }
    </style>
  </head> 
  <body>
    <div class="regional-statistics-management-container container" style="padding-top: .1rem;display:none;max-width:100%;width:100%;box-sizing: border-box;">
      <div id="downloadFormArea"></div>
      <!-- プルダウン -->
      <div class="row justify-content-left" style="text-align: center;box-sizing: border-box;padding: 1.042vw 1.563vw;border-bottom: 1px solid rgb(235, 238, 247);">
        <div class="col-xs-auto col-sm-auto col-md-auto mx-1 mt-1" style="
        margin: 0 !important;
        padding: 0 !important;
    ">
          <span style="style="margin-right: 0.573vw;color:#536285;font-size: 0.833vw;""> 統計項目 </span>
          <span>
            <select
              id="selectStatisticsItem"
              style="
              height: 1.979vw;
              bottom: 0px;
              padding: 0px 5px 2px 5px;
              width: fit-content;
              display: inline-block;
              color:#536285;
              border:1px solid #EBEEF7;
              outline: none !important;
              margin-right:1.563vw;
              font-size: 0.833vw;
              "
              class="form-control"
              onchange="$('#hiddenItemName').val($(this).val());$('#selectedItemName').text($(this).children(':selected').text());">
              <option value=""></option>
            </select>
          </span>
        </div>
        <div class="col-xs-auto col-sm-auto col-md-auto mx-1 mt-1" style="
        margin: 0 !important;
        padding: 0;
    ">
          <span style="right: 0px">
            <button
              id="displayTableButton"
              type="button"
              class="btn csv-btn"
              style="min-width: 20px;background:#2AAE7A;margin-right:1.563vw;height:1.979vw;font-size: 0.833vw;padding:0 1.563vw;"
              onclick="displayStatisticsTable();">
              テーブル表示
            </button>
          </span>

          <!-- CSV入出力ボタン -->
          <button
            id="csvOutputButton"
            type="button"
            class="btn csv-btn"
            style="background:#2AAE7A;margin-right:1.563vw;height:1.979vw;font-size: 0.833vw;padding:0 1.563vw;"
            onclick="csvDownloadByItem()">
            CSV出力
          </button>
          <button
            id="csvInputButton"
            type="button"
            class="btn csv-btn"
            style="background:#2AAE7A;margin-right:1.563vw;height:1.979vw;font-size: 0.833vw;padding:0 1.563vw;"
            onclick="csvInputDialog()">
            CSV入力
          </button>
        </div>
      </div>

      <!-- CSVデータの登録ダイアログ -->
      <div
        class="modal fade"
        id="csvInputModal"
        tabindex="-1"
        aria-hidden="true"
        data-backdrop="static">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">CSVファイルを選択</h5>
              <button
                type="button"
                class="close"
                data-dismiss="modal"
                aria-label="Close"
                onclick="$('#csvFile').val('');$('#csvFileName').val('');">
                <span aria-hidden="true">&times;</span>
              </button>
            </div>
            <div class="modal-body">
              <input
                type="text"
                value=""
                readonly
                id="csvFileName"
                style="
                bottom: 0px;
                padding: 0 5px 5px 5px;
                width: calc(100% - 70px);
                font-size: 18px;
                "
                />
              <button
                type="button"
                class="btn csv-btn"
                id="referCsvFileButton"
                onclick="$('#csvFile').click()">
                参照
              </button>
              <form id="csvFileForm" enctype="multipart/form-data">
                <input
                  type="hidden"
                  name="itemName"
                  id="hiddenItemName"
                  value=""
                  />
                <input
                  name="updateFile"
                  type="file"
                  style="display: none"
                  value=""
                  id="csvFile"
                  accept=".csv"
                  oninput="
                  if($('#csvFile')[0].files[0].type=='text/csv'){
                  $('#csvFileName').val($('#csvFile')[0].files[0].name);
                  }else{
                  alert('CSVファイルを選択してください');
                  $(this).val('');
                  $('#csvFileName').val('');
                  }
                  "
                  />
              </form>
            </div>
            <div class="modal-footer">
              <button
                type="button"
                class="btn csv-btn"
                id="dispCsvDataButton"
                onclick="dispCsvData()">
                取込データを表示
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- プロセスサークル -->
      <div id="loading" class="overlay" style="display: none">
        <div
          id="spinner"
          class="d-flex justify-content-center align-items-center">
          <div class="spinner-border" role="status">
            <span class="sr-only">Loading...</span>
          </div>
        </div>
        <span style="color: white">データ更新中です...</span>
      </div>
      <div class="overlay-back" id="overlayBack" style="display: none"></div>
      <!-- CSV取込結果ダイアログ -->
      <div
        class="modal fade"
        id="readResultModal"
        tabindex="-1"
        aria-hidden="true"
        data-backdrop="static">
        <div class="modal-dialog modal-dialog-centered">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title" id="readResultModalTitle">
                統計データの更新成功
              </h5>
            </div>
            <div class="modal-footer">
              <button
                type="button"
                class="btn"
                id="readResultButton"
                data-dismiss="modal">
                閉じる
              </button>
            </div>
          </div>
        </div>
      </div>

      <!-- 統計情報テーブル -->
      <div
        class="row statistics-table justify-content-center"
        style="margin:0;margin-top: 20px">
        <div
          class="col-md-auto mx-auto mt-3 statisticsTableBox"
          style="overflow: auto;max-width:100%;width:100%;">
          <table
            id="statisticsListTable"
            class="table table-striped"
            style="display: none; white-space: nowrap">
            <!-- jsでテーブルの中身を作成 -->
          </table>
        </div>
      </div>

      <!-- CSVデータの登録前テーブル表示ダイアログ -->
      <div
        class="modal fade"
        id="csvDataDisplayModal"
        tabindex="-1"
        aria-hidden="true"
        data-backdrop="static">
        <div
          class="modal-dialog modal-dialog-centered"
          style="max-width: 1140px !important">
          <div class="modal-content">
            <div class="modal-header">
              <h5 class="modal-title">登録内容を確認</h5>
            </div>
            <div class="modal-body" style="flex-direction: column;">
              <p>
                選択中の統計項目：<span
                  id="selectedItemName"
                  style="font-weight: bold"></span>
              </p>
              この内容で登録しますか？
              <div
                class="row statistics-table justify-content-center">
                <div
                  class="col-md-auto mx-auto mt-3 csvDataTableBox"
                  style="overflow: auto;max-width:100%;width:100%;">
                  <table
                    id="csvDataTable"
                    class="table table-striped"
                    style="white-space: nowrap">
                    <!-- jsでテーブルの中身を作成 -->
                  </table>
                </div>
              </div>
            </div>
            <div class="modal-footer">
              <button
                type="button"
                class="btn csv-btn"
                id="registCsvDataButton"
                onclick="registCsvData()">
                登録
              </button>
              <button
                type="button"
                class="btn"
                id="backToSelectCsvButton"
                data-dismiss="modal"
                onclick="formParam='';">
                CSV選択に戻る
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
    <!-- Optional JavaScript -->

    <!-- jQuery first, then Popper.js, then Bootstrap JS -->
    <script
      src="https://code.jquery.com/jquery-3.3.1.min.js"
      integrity="sha256-FgpCb/KJQlLNfOu91ta32o/NMZxltwRo8QtmkMRdAu8="
      crossorigin="anonymous"></script>
    <script
      src="https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.14.3/umd/popper.min.js"
      integrity="sha384-ZMP7rVo3mIykV+2+9J3UJ46jBk0WLaUAdn689aCwoqbBJiSnjAK/l8WvCWPIPm49"
      crossorigin="anonymous"></script>
    <script
      src="https://stackpath.bootstrapcdn.com/bootstrap/4.1.3/js/bootstrap.min.js"
      integrity="sha384-ChfqqxuZUCnJSK3+MXmPNIyE6ZbWh2IMqE241rYiqJxyMiZ6OW/JmZQ5stwEULTy"
      crossorigin="anonymous"></script>

    <script src="https://cdnjs.cloudflare.com/ajax/libs/encoding-japanese/2.0.0/encoding.min.js"></script>

    <!-- CSVの入出力処理 -->
    <script>
      //下記のapiUrlはReact側で置き換え
      var apiUrlBasePath = "{apiUrl}";
      //下記のstatisticsItemsInformationはReact側で置き換え
      var statisticsItemsInformation = JSON.parse('{statisticsItemsInformation}');
      //CSVのデータ格納用
      var formParam;
      //CSVの文字列変換後
      var csvStringData;

      var selectStatisticsItem = document.getElementById("selectStatisticsItem");
      Object.keys(statisticsItemsInformation).forEach(function (key) {
        var option = document.createElement("option"); 
        option.text = statisticsItemsInformation[key]?.displayName;
        option.value = statisticsItemsInformation[key]?.value;
        selectStatisticsItem.appendChild(option);
      })

      /**
       * 表示ボタン押下時の処理
       */
      var displayStatisticsTable = function () {
        var selectedItem = $("#selectStatisticsItem").val();
        $.ajax({
          type: "POST",
          url: apiUrlBasePath + "/csv/dispTable",
          dataType: "json",
          xhrFields: {
              withCredentials: true
          },
          data:{
            itemName: selectedItem,
          },
        })
          .done((response) => {
            if (response.error) {
              alert(response.error);
            } else {
              //テーブル表示
              $("#statisticsListTable").empty();
              //ヘッダ作成
              var thead = "";
              thead += "<thead><tr>";
              for (let i = 0; i < response.header.length; i++) {
                thead += "<th>";
                thead += response.header[i];
                thead += "</th>";
              }
              thead += "</tr></thead>";
              $("#statisticsListTable").append(thead);
              //データ部作成
              var tbody = "<tbody>";
              for (let i = 0; i < response.data.length; i++) {
                tbody += "<tr>";
                for (let j = 0; j < response.header.length; j++) {
                  var tData = response.data[i][response.header[j]];
                  if (typeof tData == "string" || tData instanceof String) {
                    tbody += '<td style="text-align: left;">';
                    tbody += tData + "</td>";
                  } else if (tData == null) {
                    tbody += "<td></td>";
                  } else {
                    tbody += '<td style="text-align: right;">';
                    tbody += Number(tData).toLocaleString() + "</td>";
                  }
                }
                tbody += "</tr>";
              }
              tbody += "</tbody>";
              $("#statisticsListTable").append(tbody);
              $("#statisticsListTable").css("display", "block");
            }
          })
          .fail((error) => {
            console.error(error);
          });
      };

      /**
       * CSV出力ボタン押下時の処理
       */
      var csvDownloadByItem = function () {
        var selectedItem = $("#selectStatisticsItem").val();
        if (selectedItem == "") {
          alert("統計項目を選択してください");
        } else {
          var form =
            '<form method="post" action="' +
            apiUrlBasePath +
            '/csv/download" style="display: none;" id="tmpForm">';
          form +=
            '<input type="hidden" value="' +
            selectedItem +
            '" name="itemName">';
          form += "</form>";
          $("#downloadFormArea").append(form);
          $("#tmpForm").submit();
          $("#downloadFormArea").empty();
        }
      };

      /**
       * CSV入力ボタン押下時の処理
       */
      var csvInputDialog = function () {
        if ($("#selectStatisticsItem").val() == "") {
          alert("統計項目を選択してください。");
        } else {
          $("#csvInputModal").modal();
        }
      };

      /**
       * CSVデータ登録ボタン押下時の処理
       */
      var dispCsvData = async function () {
        if ($("#csvFile").val() == "") {
          alert("ファイルを選択してください");
        } else {
          formParam = await convertFileCode($("#csvFile")[0].files[0]);
          //テーブル表示
          $("#csvDataTable").empty();
          var csvDataList = csvStringData.split(/\\n/);
          //ヘッダ作成
          var thead = "";
          var headerList = csvDataList[0].slice(0, -1).split(",");
          thead += "<thead><tr>";
          for (let i = 0; i < headerList.length; i++) {
            thead += "<th>";
            thead += headerList[i];
            thead += "</th>";
          }
          thead += "</tr></thead>";
          $("#csvDataTable").append(thead);

          //データ部作成
          var tbody = "<tbody>";
          for (let i = 1; i < csvDataList.length - 1; i++) {
            tbody += "<tr>";
            for (let j = 0; j < headerList.length; j++) {
              var tData = csvDataList[i].split(",")[j];
              if (!Number.isNaN(Number(tData))&&tData!="") {
                tbody += '<td style="text-align: right;">';
                tbody += Number(tData).toLocaleString() + "</td>";
              } else if (typeof tData == "string" || tData instanceof String) {
                tbody += '<td style="text-align: left;">';
                tbody += tData + "</td>";
              } else if (tData == null) {
                tbody += "<td></td>";
              }
            }
            tbody += "</tr>";
          }
          tbody += "</tbody>";
          $("#csvDataTable").append(tbody);
          //モーダル表示
          $("#csvDataDisplayModal").modal();
        }
      };

      /**
       * テーブル仮表示内容を登録時の処理
       */
      var registCsvData = function () {
        var formData = new FormData();
        formData.append("updateFile", formParam);
        formData.append("itemName", $("#hiddenItemName").val());
        $("#csvInputModal").modal("hide");
        $("#csvDataDisplayModal").modal("hide");
        $("#loaderCircle").css("display", "");
        $("#registCsvDataButton").attr("disabled");
        $("#overlayBack").fadeIn();
        $("#loading").show();
        $.ajax({
          type: "POST",
          url: apiUrlBasePath + "/csv/upload",
          data: formData,
          dataType: "json",
          cache: false,
          contentType: false,
          processData: false,
          xhrFields: {
              withCredentials: true
          },
        })
          .done((response) => {
            $("#overlayBack").fadeOut();
            $("#loading").hide();
            $("#csvFileName").val("");
            $("#csvFile").val("");
            $("#loaderCircle").css("display", "none");
            if (response.result != "success") {
              $("#readResultModalTitle").parent().addClass("update-failed");
              $("#readResultModalTitle").text("統計データの更新に失敗");
              $("#readResultButton").text("取込キャンセル");
              var continueUpdateSrc =
                '<button type="button" class="btn" data-dismiss="modal" onclick="';
              continueUpdateSrc += "$('#csvInputModal').modal();";
              continueUpdateSrc += '">';
              continueUpdateSrc += "CSVファイルを変更する";
              continueUpdateSrc += "</button>";
              $("#readResultButton").before(continueUpdateSrc);
            }
            $("#readResultModal").modal();
            formParam = "";
          })
          .fail((error) => {
            $("#loading").hide();
            $("#overlayBack").fadeOut();
            $("#csvFileName").val("");
            $("#csvFile").val("");
            $("#loaderCircle").css("display", "none");
            console.error(error);

            $("#readResultModalTitle").parent().addClass("update-failed");
            $("#readResultModalTitle").text("統計データの更新に失敗");
            $("#readResultButton").text("取込キャンセル");
            var continueUpdateSrc =
              '<button type="button" class="btn" data-dismiss="modal" onclick="';
            continueUpdateSrc += "$('#csvInputModal').modal();";
            continueUpdateSrc += '">';
            continueUpdateSrc += "CSVファイルを変更する";
            continueUpdateSrc += "</button>";
            $("#readResultButton").before(continueUpdateSrc);
            $("#readResultModal").modal();
          });
      };

      /**
       * shiftJIS⇒バイナリに変換
       */
      var convertFileCode = function (param) {
        return new Promise((resolve, reject) => {
          let par = param;
          const reader = new FileReader();
          reader.readAsBinaryString(param);
          reader.onload = (e) => {
            const result = e.target?.result;
            var code = Encoding.detect(result);
            const sjisArray = Encoding.stringToCode(result);
            var par = Encoding.codeToString(
              Encoding.convert(sjisArray, { to: "UNICODE", from: code })
            );
            csvStringData = par;
            resolve(new File([par], param.name, { type: "text/csv" }));
          };
          reader.onerror = () => {
            // ファイル読み込みエラー
            reject(reader.error);
          };
        });
      };

      /**
       * CSV取込結果ダイアログが閉じた時初期化
       */
      $("#readResultModal").on("hidden.bs.modal", function (e) {
        $("#readResultModalTitle").parent().removeClass("update-failed");
        $("#readResultModalTitle").text("統計データの更新に成功");
        $("#readResultButton").text("閉じる");
        $("#readResultButton").prev().remove();
      });

      setTimeout(function(){
            $(".regional-statistics-management-container").css('display', 'block');
      },500);
    </script>
  </body>
</html>
`