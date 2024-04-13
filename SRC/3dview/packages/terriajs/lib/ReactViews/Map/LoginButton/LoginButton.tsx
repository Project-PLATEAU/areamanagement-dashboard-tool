import React from "react";
import { useTranslation } from "react-i18next";
import ViewState from "../../../ReactViewModels/ViewState";
import terria from "../../../Models/Terria";
import Icon from "../../../Styled/Icon";
import Button from "../../../Styled/Button";
import Styles from "./login-button.scss";
import { runInAction,action } from "mobx";
import {setThemeData} from '../../../Models/GraphList/GraphList';
import Config from "../../../../customconfig.json";

interface Props {
  viewState: ViewState;
  terria: terria;
}
  
export default (props: Props) => {
  const { t } = useTranslation();

  const user = props.terria.name;
  const id = props.terria.userId;
  let icon;
  let button = [];
  if(id != -1){
    icon = Icon.GLYPHS.upload;
    button.push(
      <button
        title="ログアウト"
        className={Styles.logoutBtn}
        onClick={evt => {
          evt.preventDefault();
          evt.stopPropagation();
          logout();
        }}
      >
      <Icon
        glyph={icon} 
        rotation={-90}
      />
      </button>);
  }else{
    icon = Icon.GLYPHS.downloadNew;
    button.push(
      <button
        title="ログイン"
        className={Styles.loginBtn}
        onClick={evt => {
          evt.preventDefault();
          evt.stopPropagation();
          props.viewState.showLoginPanel();
        }}
      >
      {/* <Icon
        glyph={icon} 
        rotation={-90}
      /> */}
      </button>);
  }

  //ログアウト処理
  function logout(){
    //属性情報とパネルを非表示にする
    try{
      runInAction(() => {
        props.viewState.featureInfoPanelIsVisible = false;
        setTimeout(
          action(() => {
              props.terria.pickedFeatures = undefined;
              props.terria.selectedFeature = undefined;
          }),
          200
          );
          props.viewState.hideAllPanel();
      })
    }catch(e){}

    fetch(Config.config.apiUrl + "/auth/logout")
    .then(res => {
      //ログアウト後及びログイン後は権限が変わる為テーマデータを再読み込み
      runInAction(() => {
        props.terria.setName("");
        props.terria.setPermission("");
        props.terria.setUserId(-1);
        //テーマ内切替項目を一旦リセット
        props.viewState.setSelectedWithThemeSwitchItemValue("");
        //現在のテーマデータを再セットしてグラフリストとレイヤを再読み込み
        setThemeData(props.viewState);
      })
    }).catch(error => {
      console.error('通信処理に失敗しました', error);
      alert('通信処理に失敗しました');
    });
  } 

  return (
    <div>
      {button}
    </div>
  );
};