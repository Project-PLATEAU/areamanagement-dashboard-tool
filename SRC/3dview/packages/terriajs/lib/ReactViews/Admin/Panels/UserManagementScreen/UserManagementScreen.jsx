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
import UserTable from './UserTable';
import EditUserPanel from './UserEditPanel';


@observer
class UserManagementScreen extends React.Component {
    static displayName = "DashboardManagementScreen";

    static propTypes = {
        terria: PropTypes.object.isRequired,
        viewState: PropTypes.object.isRequired,
        theme: PropTypes.object,
        t: PropTypes.func.isRequired
    };

    constructor(props) {
        super(props);
        const columnDef = [
          {field: 'userId', headerName: 'No.', width: "15%"},
          {field: 'loginId', headerName: 'ユーザID'},
          {field: 'userName', headerName: 'ユーザ名'},
          {field: 'role', headerName: 'ユーザグループ'},
          {field: 'button_edit', width: "10%", customComponent:this.editButtonCustomComponent},
          {field: 'button_delete', width: "10%", customComponent:this.deleteButtonCustomComponent},
        ]
        this.state = {
            columnDef: columnDef,
            data: [],
            editFlag: false,
        }
    }

    componentDidMount() {
        this.resetTable();
    }

    resetTable() {
        const url = Config.config.apiUrl + "/user/all";
        fetch(url)
        .then((res) => res.json())
        .then(res => {
            console.log(res);
            if(!res.status){
                this.setState({data: res});
            }
        })
    }

    closePanel() {
        this.setState({editFlag: false});
        this.resetTable();
    }

    render(){
        return(
            <>
            <div style={{height:"80vh",width:"100vw"}}>
                {this.state.editFlag == true && (
                <EditUserPanel
                    userinfo={this.state.editUser}
                    close={this.closePanel.bind(this)}
                    allusers={this.state.data}
                />)}
                <div style={{height:"100%",width:"100%"}}>
                <Box style={{boxSizing:"border-box",padding:"1.042vw 1.563vw",borderBottom:"1px solid #EBEEF7"}}>
                    <Button 
                    style={{width: "auto",height: "1.979vw",background: "#2AAE7A",borderRadius: "4px",padding:"0 1.563vw",color:"#fff"}} 
                    onClick={this.resistUser}>新規ユーザ登録</Button>
                </Box>
                <Box style={{maxHeight: "90%", padding:"1.042vw 1.563vw"}}>
                    <div style={{overflowY:"auto", width:"100%"}}>
                        <UserTable rowData={this.state.data} columnDefs={this.state.columnDef}></UserTable>
                    </div>
                </Box>
                </div>
            </div>
            </>
        )
    }

    resistUser = () => {
        this.setState({
            editFlag: true,
            editUser: {},
        })
    }

    editButtonCustomComponent = (index) =>{
        const editButtonClickHandler = () => {
            // 編集画面に遷移
            const users = this.state.data;
            const user = users[index];
            this.setState({
                editFlag: true,
                editUser: user,
            })
        };
        return (
            <>
                <button onClick={editButtonClickHandler} style={{background:"#ccddf5",color:"#1b63ee",padding:"5px",border:"1px solid #1b63ee", borderRadius:"3px"}}>
                    <span>編集</span>
                </button>
            </>
        )
    }

    deleteButtonCustomComponent = (index) =>{
        const deleteButtonClickHandler = () => {
            const users = this.state.data;
            const user = users[index];
            const userId = user.userId;
            const check = window.confirm('ユーザ' + user.loginId + 'を削除してもよろしいですか？');
            if(check){
                const url = Config.config.apiUrl + "/user/delete/" + userId;
                fetch(url, {
                    method: 'DELETE',
                })
                .then((res) => res.json())
                .then(res => {
                    console.log(res);
                    if(res.status != 204) alert("削除に失敗しました。");
                    this.resetTable();
                })
            }else{
                return;
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


export default withTranslation()(withTheme(UserManagementScreen));