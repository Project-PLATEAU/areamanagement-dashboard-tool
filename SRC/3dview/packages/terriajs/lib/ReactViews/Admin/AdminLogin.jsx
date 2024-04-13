import { observer } from "mobx-react";
import PropTypes from "prop-types";
import React from "react";
import { withTranslation } from "react-i18next";
import { withTheme } from "styled-components";
import Icon, { StyledIcon } from "../../Styled/Icon";
import Spacing from "../../Styled/Spacing";
import Text from "../../Styled/Text";
import Input from "../../Styled/Input";
import Box from "../../Styled/Box";
import Select from "../../Styled/Select";
import Button, { RawButton } from "../../Styled/Button";
import { BaseModel } from "../../Models/Definition/Model";
import Config from "../../../customconfig.json";
import { action, runInAction } from "mobx";
import { Link } from "react-router-dom";
import Style from "./admin-login.scss";
import Style2 from "./admin-interface.scss";

/**
 * ログイン画面
 */
@observer
class AdminLoginPanel extends React.Component {

    static propTypes = {
        terria: PropTypes.object.isRequired,
        viewState: PropTypes.object.isRequired,
        theme: PropTypes.object,
        t: PropTypes.func.isRequired
    };

    constructor(props) {
        super(props);
        this.state = {passwordType:"password"}
    }

    render(){
        let passwordType = this.state.passwordType
        return(
            <div style={{height:"100%"}}>
                <Box col12 className={Style.header} textAlignCenter={true} >
                <img src={Config.config.plateauPath + "/sample/img/bg1.png"} className={Style2.z_bg}/>
                    <Text 
                    className={Style2.z_head}
                        heading={true}
                    >
                        エリア地域情報プラットフォーム
                    </Text>
                </Box>
                <div className={Style.originalGradient}>
                    <Box col3 className={Style.panel}>
                        <Text 
                            subHeading={true}
                            textAlignCenter={true}
                            className={Style.z_title}
                        >
                            管理者機能用ログイン画面
                        </Text>
                        
                        <Text className={Style.z_h1} large={true}>ユーザID</Text>
                        <Spacing bottom={2} />
                        <div className={Style.z_input} >
                        <img src={Config.config.plateauPath + "/sample/img/icon07.png"}/>
                        <Input
                            light={true}
                            type="text"
                            id="loginId"
                            placeholder="ユーザIDを入力"
                            onChange={e => this.setState({ loginId: e.target.value })}
                        />
                        </div>
                        <Spacing bottom={5} />
                        
                        <Text className={Style.z_h1} large={true}>パスワード</Text>
                        <Spacing bottom={2} />
                        <div className={Style.z_input} >
                        <img src={Config.config.plateauPath + "/sample/img/icon07.png"}/>
                        <Input
                            light={true}
                            type={passwordType}
                            id="password"
                            placeholder="パスワードを入力"
                            onChange={e => this.setState({ password: e.target.value })}
                        />
                        {passwordType === "password" && (<img src={Config.config.plateauPath + "/sample/img/icon20.png"} className={Style.yj} onClick={this.changeType}/>)}
                        {passwordType == "text" && (<img src={Config.config.plateauPath + "/sample/img/icon21.png"} className={Style.yj}  onClick={this.changeType}/>)}
                        </div>
                        
                        <Button className={Style.z_button} onClick={this.login} style={{ backgroundColor: "#2AAE7A", color: "#ffff", innerWidth: "100%"}}>
                            <Box>
                                <Text medium={true} textAlignCenter={true}>ログイン</Text>
                            </Box>
                        </Button>
                    </Box>
                </div>
            </div>
        );
    }

    changeType = () => {
        const type = this.state.passwordType
        if(type === "password"){
            this.setState({passwordType:"text"})
        }else{
            this.setState({passwordType:"password"})
        }
    }

    login = () => {
        const loginId = this.state.loginId;
        const password = this.state.password;
        if(!loginId || !password || loginId == "" || password == ""){
        alert("ユーザIDもしくはパスワードが入力されていません");
        return false;
        }
        
        const apiUrl = Config.config.apiUrl + "/user/admin/login";
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
                const pageNum = 1;
                runInAction(() => {
                    const username = res.userName;
                    const role = res.role;
                    const userId = res.userId;
                    this.props.terria.setName(username);
                    this.props.terria.setPermission(role);
                    this.props.terria.setUserId(userId);
                    this.props.viewState.setAdminPageNum(pageNum);
                });
            }else{
                alert("ログインに失敗しました");
            }
        })
    }

}

export default withTranslation()(withTheme(AdminLoginPanel));