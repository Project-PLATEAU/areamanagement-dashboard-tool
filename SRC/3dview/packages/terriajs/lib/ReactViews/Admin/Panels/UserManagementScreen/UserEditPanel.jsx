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
import Styles from "./user-edit-panel.scss";
import { action } from "mobx";
import Config from "../../../../../customconfig.json";
import { method } from "lodash-es";
import { runInAction } from "mobx";
import {setThemeData} from '../../../../Models/GraphList/GraphList';
import { Padding } from "terriajs-protomaps";
import Select from "../../../../Styled/Select";

/**
 * ユーザ編集・新規登録画面
 */
@observer
class UserEditPanel extends React.Component {
  static displayName = "UserEditPanel";

  constructor(props) {
    super(props);
    const data = this.props.userinfo
    let type = "update";
    if(data.userId === void 0) type = "register";
    this.state = {
      userinfo: data,
      editType: type,
    };
  }

  // 初期描画後
  componentDidMount() {
    setTimeout(() => this.setState({ isAnimatingOpen: false }), 0);
  }

  render() {
    const userInfo = this.state.userinfo;
    const editType = this.state.editType;
    let disable = false;
    if(editType == "update") disable = true;

    return(
      <div 
        className={Styles.ModalWrapper}
        onClick={this.close}
      >
        <div 
          className={Styles.Modal}
          onClick={e => e.stopPropagation()}
        >
          <Text className={Styles.title} subHeading bold textAlignCenter>ユーザ
          {editType == "register" && (<>登録</>)}
          {editType == "update" && (<>編集</>)}
          </Text>

          <Spacing bottom={4} />

          <Box textAlignCenter column>
            <Box col12 css={'display:flex;'}>
              <Box className={Styles.title1} col3 right>ユーザID <span style={{color:"red"}}>※</span></Box>
              <Spacing right={2} />
              <Box className={Styles.title2} col9 centered>
                <Input
                  type="text"
                  id="loginId"
                  width="100%"
                  defaultValue={userInfo.loginId || ""}
                  disabled={disable}
                />
              </Box>
            </Box>
            <Spacing bottom={2} />
            <Box col12 css={'display:flex;'}>
              <Box className={Styles.title1} col3 right>ユーザ名 </Box>
              <Spacing right={2} />
              <Box className={Styles.title2} col9 centered>
                <Input
                  type="text"
                  id="userName"
                  width="100%"
                  defaultValue={userInfo.userName || ""}
                />
              </Box>
            </Box>
            <Spacing bottom={2} />
            <Box col12 css={'display:flex;'}>
              <Box className={Styles.title1} col3 right>ユーザグループ <span style={{color:"red"}}>※</span></Box>
              <Spacing right={2} />
              <Box className={Styles.title2} col9 centered>
                  <select
                    id="userGroup"
                    style={{width:"100%"}}
                    css={'padding-left:1em'}
                  >
                    <option value="admin" selected={userInfo.role == "admin"}>管理ユーザ</option>
                    <option value="user" selected={userInfo.role == "user"}>地域住民ユーザ</option>
                    <option value="erimane" selected={userInfo.role == "erimane"}>エリマネ団体ユーザ</option>
                  </select>
              </Box>
            </Box>
            <Spacing bottom={2} />
            <Box col12 css={'display:flex;'}>
              <Box className={Styles.title1} col3 right>メールアドレス </Box>
              <Spacing right={2} />
              <Box className={Styles.title2} col9 centered>
                <Input
                  type="text"
                  id="mailAddress"
                  width="100%"
                  defaultValue={userInfo.mailAddress || ""}
                />
              </Box>
            </Box>
            <Spacing bottom={2} />
            <Box col12 css={'display:flex;'}>
              <Box className={Styles.title1} col3 right>パスワード <span style={{color:"red"}}>※</span></Box>
              <Spacing right={2} />
              <Box className={Styles.title2} col9 centered>
                <Input
                  type="password"
                  id="password"
                  width="100%"
                  defaultValue={""}
                  css={'padding-left:1em'}
                />
              </Box>
            </Box>
            <Spacing bottom={2} />
            <Box col12 css={'display:flex;'}>
              <Box className={Styles.title1} col3 right>パスワード(確認) <span style={{color:"red"}}>※</span></Box>
              <Spacing right={2} />
              <Box className={Styles.title2} col9 centered>
                <Input
                  type="password"
                  id="passwordForCheck"
                  width={"100%"}
                  defaultValue={""}
  
                  css={'padding-left:1em'}
                />
              </Box>
            </Box>
          </Box>

          <Spacing bottom={4} />

          <Box className={Styles.button} centered>
            <Button onClick={this.register}>保存</Button>
            <Button onClick={this.backPage}>戻る</Button>
          </Box>
        </div>
      </div>
    );
  }
  
  backPage = () =>{
    this.props.close();
  }

  checkDuplicateId = (loginId) => {
    const allUsers = this.props.allusers;
    const editType = this.state.editType;
    const duplicate = allUsers.filter((user) => {
      if(user.loginId == loginId) return true
    })
    // console.log(duplicate);
    if(duplicate.length > 0 && editType == "register"){
      return true;
    }else{
      return false;
    }
  }

  register = () => {
    let userInfo = this.state.userinfo;
    const password = document.getElementById("password").value;
    const check = document.getElementById("passwordForCheck").value;
    const loginId = document.getElementById("loginId").value;
    const userGroup = document.getElementById("userGroup").value;
    const userName = document.getElementById("userName").value;
    let inputCheck = "";
    if(loginId == null || loginId == "") inputCheck += "ユーザIDが入力されていません。\n";
    if(loginId.length > 50) inputCheck += "ユーザIDは50文字以内にしてください。\n";
    if(this.checkDuplicateId(loginId) == true) inputCheck += "このユーザIDは既に使用されています。\n"
    if(password == null || password == "") inputCheck += "パスワードが入力されていません。\n";
    if(password.length > 20) inputCheck += "パスワードは20文字以内にしてください。\n";
    if(check == null || check == "") inputCheck += "確認用パスワードが入力されていません。\n";
    if(password !== check) inputCheck += "パスワードと確認用パスワードが一致しません。\n";
    if(inputCheck != "") {alert(inputCheck); return;}
    userInfo.loginId = loginId;
    userInfo.password = password;
    userInfo.role = userGroup;
    userInfo.userId = userInfo.userId || null;
    userInfo.mailAddress = userInfo.mailAddress || null;
    userInfo.userName = userName;
    const editType = this.state.editType
    let method = "POST";
    if(editType == "update") method = "PUT";
    const url = Config.config.apiUrl + "/user/" + editType;
    fetch(url, {
      method: method,
      body: JSON.stringify(userInfo),
      headers: new Headers({ 'Content-type': 'application/json' }),
    })
    .then((res) => res.json())
    .then(res => {
      console.log(res);
      if(res.status != 204){alert("失敗しました。");}
      else{this.backPage();}
    })
  }
}

export default withTranslation()(withTheme(UserEditPanel));
