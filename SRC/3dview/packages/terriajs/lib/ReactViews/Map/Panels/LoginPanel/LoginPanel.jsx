import { observer } from "mobx-react";
import React, { useEffect } from "react";
import { withTranslation } from "react-i18next";
import { withTheme } from "styled-components";
import Icon, { StyledIcon } from "../../../../Styled/Icon";
import Spacing from "../../../../Styled/Spacing";
import Text from "../../../../Styled/Text";
import Box from "../../../../Styled/Box";
import Input from "../../../../Styled/Input";
import Button, { RawButton } from "../../../../Styled/Button";
import Styles from "./login-panel.scss";
import { action } from "mobx";
import Config from "../../../../../customconfig.json";
import { method } from "lodash-es";
import { runInAction } from "mobx";
import {setThemeData} from '../../../../Models/GraphList/GraphList';

/**
 * ログイン画面
 */
@observer
class LoginPanel extends React.Component {
  static displayName = "LoginPanel";

  constructor(props) {
    super(props);
    this.state = {passwordType:"password"}
  }

  @action.bound
  close() {
    console.log("close")
    const viewState = this.props.viewState || {};
    viewState.showLoginMenu = false;
  }

  render() {
    let passwordType = this.state.passwordType
    return(
      <div className={Styles.z_login}>
      <div 
        className={Styles.LoginModalWrapper}
        onClick={this.close}
      >
        <div 
          className={Styles.LoginModal}
          onClick={e => e.stopPropagation()}
        >
            <Spacing bottom={0}/>
            <div className={Styles.li}>
            <div className={Styles.h1}>ユーザIDを入力</div>
           <div className={Styles.input}>
           <img src={Config.config.plateauPath + "/sample/img/icon07.png"}/>
            <Input
              light={true}
              dark={false}
              type="text"
              id="loginId"
              placeholder=""
            />
            </div>
           </div>
            <Spacing bottom={1}/>
            <div className={Styles.li}>
            <div className={Styles.h1}>パスワードを入力</div>
           <div className={Styles.input}>
           <img src={Config.config.plateauPath + "/sample/img/icon08.png"}/>
            <Input
              light={true}
              dark={false}
              type={passwordType}
              id="password"
              placeholder=""
            />
              {passwordType === "password" && (<img src={Config.config.plateauPath + "/sample/img/icon20.png"} className={Styles.yj} onClick={this.changeType}/>)}
              {passwordType == "text" && (<img src={Config.config.plateauPath + "/sample/img/icon21.png"} className={Styles.yj} onClick={this.changeType}/>)}
            </div>
            </div>
            <Spacing bottom={2}/>
            <Button onClick={this.login} style={{ backgroundColor: "#2AAE7A", color: "#ffff", width:"100%"}}>
              <Box>
              {/* <StyledIcon
                styledWidth={"20px"}
                fillColor={this.props.theme.textLight}
                glyph={Icon.GLYPHS.downloadNew}
                css={`
                  transform: rotate(-90deg);
                  margin-right: 5px;
                `}
              /> */}
              <Text extraLarge={true}>ログイン</Text>
              </Box>
            </Button>
        </div>
      </div>
      </div>
    );
  }

  //ログイン処理
  login = () => {
    //属性情報とパネルを非表示にする
    try{
      runInAction(() => {
          this.props.viewState.featureInfoPanelIsVisible = false;
          setTimeout(
          action(() => {
              this.props.terria.pickedFeatures = undefined;
              this.props.terria.selectedFeature = undefined;
          }),
          200
          );
          this.props.viewState.hideAllPanel();
      })
    }catch(e){}

    const loginId = document.getElementById("loginId").value;
    const password = document.getElementById("password").value;
    if(loginId == "" || password == ""){
      alert("ユーザIDもしくはパスワードが入力されていません");
      return false;
    }
    
    const apiUrl = Config.config.apiUrl + "/user/login";
    fetch(apiUrl, {
      method: 'POST',
      body: JSON.stringify({
        userId: -1,
        loginId: loginId,
        role: null,
        password: password,
        userName: null,
        mailAddress: null 
      }),
      headers: new Headers({ 'Content-type': 'application/json' }),
    })
    .then((res) => res.json())
    .then(res => {
      console.log(res);
      if (!res.status) {
          // console.log("成功");
          const username = res.userName;
          const role = res.role;
          const userId = res.userId;
          this.props.terria.setName(username);
          this.props.terria.setPermission(role);
          this.props.terria.setUserId(userId);
          this.close();
          runInAction(() => {
            //テーマ内切替項目を一旦リセット
            this.props.viewState.setSelectedWithThemeSwitchItemValue("");
            //現在のテーマデータを再セットしてグラフリストとレイヤを再読み込み
            setThemeData(this.props.viewState);
          })
      } else {
        alert("ログインに失敗しました")
      }
    })
  }

  //パスワード表示
  changeType = () => {
    const type = this.state.passwordType
    if(type === "password"){
        this.setState({passwordType:"text"})
    }else{
        this.setState({passwordType:"password"})
    }
  }
}

export default withTranslation()(withTheme(LoginPanel));
